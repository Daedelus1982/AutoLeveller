package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.Element;

import autoleveller.Autoleveller;

public class VersionTest 
{
	
	@Test
	public void xmlNotExist()
	{
		try
		{
			Element element = Autoleveller.getAppElement("www.autoleveller.co.uk/nonexistenturl.xml", "00");
			Autoleveller.checkLatestMajorVersion(element);
		}
		catch (Exception ex)
		{
			//should catch this as the url does not exist
			assertTrue(true);
			return;
		}
		
		assertTrue(false);
	}

	@Test
	public void xmlExist()
	{
		int major;
		double minor;
		
		try
		{
			Element element = Autoleveller.getAppElement("http://autoleveller.co.uk/xmlTest/Version.xml", "00");
			major = Autoleveller.checkLatestMajorVersion(element);
			minor = Autoleveller.checkMinorVersion(element);
		}
		catch (Exception ex)
		{
			assertTrue(false);
			return;
		}
		
		assertEquals(3, major);
		assertEquals(7.3, minor, 0);
	}
}
