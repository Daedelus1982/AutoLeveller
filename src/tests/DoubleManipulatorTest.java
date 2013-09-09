package tests;

import static org.junit.Assert.*;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import autoleveller.DoubleManipulator;

public class DoubleManipulatorTest 
{
	DoubleManipulator _dm = new DoubleManipulator();
	
	@Before
	public void setUp() throws Exception 
	{
		
	}

	@Test
	public void getDoublesFromString() 
	{
		String doublesString = "G82 X0.3775 Y-8.0E-4 Z[#100 + -0.01]";
		
		Map<Integer, String> numbers = _dm.getNumbersInString(doublesString);
		
		assertEquals("82", numbers.get(1));
		assertEquals("0.3775", numbers.get(5));
		assertEquals("8.0E-4", numbers.get(14));
		assertEquals("100", numbers.get(24));
		assertEquals("0.01", numbers.get(31));
	}
	
	@Test
	public void removeExponent()
	{
		assertEquals("82", _dm.formatNumber("82"));
		assertEquals("0.0008", _dm.formatNumber("8.0E-4"));
		assertEquals("no number", _dm.formatNumber("no number"));
		assertEquals("2.342", _dm.formatNumber("2.342"));
		assertEquals("54000000", _dm.formatNumber("5.4e7"));
		assertEquals("123456.0024", _dm.formatNumber("123456.002355"));
		assertEquals("0", _dm.formatNumber("3.04567e-8"));
		assertEquals("0", _dm.formatNumber("3.01967E-20"));
		assertEquals("1.0147", _dm.formatNumber("1.0147"));
	}
	
	@Test
	public void formatDoubles()
	{
		assertEquals("G1 X0.8245 Y1.0147", _dm.formatOutput("G01 X0.8245 Y1.0147 "));
		assertEquals("[#500+1.2*#503-1.4*#500]", _dm.formatOutput("[#500+1.2*#503-1.4*#500]"));
		assertEquals("[#500+0*#503-0*#500]", _dm.formatOutput("[#500+0.0*#503-0.0*#500]"));
		assertEquals("G82 X0.3775 Y-0.0008 Z[#100 + -0.01]", _dm.formatOutput("G82 X0.3775 Y-8.0E-4 Z[#100 + -0.01]"));
		assertEquals("G82 X0.7028 Y-0.0006 Z[#100 + -0.01]", _dm.formatOutput("G82 X0.7028467 Y-6.0E-4 Z[#100 + -0.010000]"));
		assertEquals("G1 X1.0996 Y-0.0267 Z[#100 + -0.003]", _dm.formatOutput("G01 X1.0995543 Y-0.0267432 Z[#100 + -0.003023]"));
		assertEquals("G0 X0.2434 Y0.0806", _dm.formatOutput("G0 X0.2434 Y0.0806"));
		assertEquals("G82 X0.3278 Y0.0003 Z[#100 + -0.01]", _dm.formatOutput("G82 X0.3278 Y3.0E-4 Z[#100 + -0.01]  "));
	}
	
	@Test
	public void hasExponent()
	{
		assertTrue(_dm.isExponent("8.0E-4"));
		assertFalse(_dm.isExponent("tt"));
		assertFalse(_dm.isExponent("5.543"));
		assertFalse(_dm.isExponent("26"));
		assertTrue(_dm.isExponent("5.4e7"));
	}
	
	/* thinks 0+0 is 0.0 because + is a regex character, need to quote
	@Test
	public void replace()
	{
		System.out.println("x-1.0".replaceAll("1.0", "1"));
		System.out.println("x+1.0".replaceAll("1.0", "1"));
		System.out.println("x-0.0".replaceAll("0.0", "0"));
		System.out.println("x+0.0".replaceAll("0.0", "0"));
		System.out.println("[#500+0.0*#503-0.0*#500]".replaceAll("0.0", "0"));
		System.out.println("[#500+0*#503-0.0*#500]".replaceAll("0.0", "0"));
		System.out.println("[50+0.0*#503-0.0*#500]".replaceAll(Pattern.quote("0.0"), "0"));
		System.out.println("[#500+01.0*#503-0.0*#500]".replaceAll(Pattern.quote("0.0"), "0"));
	}*/

}
