package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import autoleveller.DoubleManipulator;
import autoleveller.SimplePoint3DCNC;
import autoleveller.probe.Probe;

public class ProbeTest 
{
	SimplePoint3DCNC[][] _probePoints;
	Probe _probe;
	DoubleManipulator _dm;

	@Before
	public void setUp() throws Exception 
	{
		_probePoints = new SimplePoint3DCNC[][]{
	            {new SimplePoint3DCNC(10, 10, "#500"), new SimplePoint3DCNC(20, 10, "#501"), new SimplePoint3DCNC(30, 10, "#502")},
	            {new SimplePoint3DCNC(10, 20, "#503"), new SimplePoint3DCNC(20, 20, "#504"), new SimplePoint3DCNC(30, 20, "#505")}, 
	            {new SimplePoint3DCNC(10, 30, "#506"), new SimplePoint3DCNC(20, 30, "#507"), new SimplePoint3DCNC(30, 30, "#508")}};
		
		//create the same array
		_probe = Probe.createProbe("Mach3", "millimeters", 10, 10, 20, 20, 200, -0.1, 10, 20, 2);
		_dm = new DoubleManipulator();
	}

    @Test
    public void getTRPoint()
    {
        assertEquals(_probePoints[2][2], _probe.getTRPoint(new SimplePoint3DCNC(22.342, 29.543, "null")));
    }

    @Test
    public void getNewTRPoint()
    {
    	assertEquals(_probePoints[2][1], _probe.getTRPoint(new SimplePoint3DCNC(14.546, 28.543, "null")));
    }
    
    @Test
    public void pointOutsideProbes()
    {
    	assertEquals(_probePoints[2][2], _probe.getTRPoint(new SimplePoint3DCNC(38.546, 45.543, "null")));
    }
    
    @Test
    public void pointOutsideProbesBottomMiddle()
    {
    	assertEquals(_probePoints[0][1], _probe.getTRPoint(new SimplePoint3DCNC(19.56, 6.565, "null")));
    }
    
    @Test
    public void getAllPoints()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(21.675, 28.4, "null");
    	
    	assertEquals(_probePoints[2][2], _probe.getTRPoint(point));
    	assertEquals(_probePoints[2][1], _probe.getTLPoint(point));
    	assertEquals(_probePoints[1][2], _probe.getBRPoint(point));
    	assertEquals(_probePoints[1][1], _probe.getBLPoint(point));   	
    }
    
    @Test
    public void getAllPointsOutsideProbes()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(39.675, 52.4, "null");
    	
    	assertEquals(_probePoints[2][2], _probe.getTRPoint(point));
    	assertEquals(_probePoints[2][2], _probe.getTLPoint(point));
    	assertEquals(_probePoints[2][2], _probe.getBRPoint(point));
    	assertEquals(_probePoints[2][2], _probe.getBLPoint(point));   	
    }
    
    @Test
    public void getAllPointsOutsideProbesLeftMiddle()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(6.567, 17.4, "null");
    	
    	assertEquals(_probePoints[1][0], _probe.getTRPoint(point));
    	assertEquals(_probePoints[1][0], _probe.getTLPoint(point));
    	assertEquals(_probePoints[1][0], _probe.getBRPoint(point));
    	assertEquals(_probePoints[1][0], _probe.getBLPoint(point));   	
    }
    
    @Test
    public void interpolate()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(24.567, 21.432, "null");
    	SimplePoint3DCNC probePointTL = _probe.getTLPoint(point);
    	SimplePoint3DCNC probePointBL = _probe.getBLPoint(point);
    	
    	SimplePoint3DCNC intPoint = _probe.interpolateY(probePointBL, probePointTL, point);
    	intPoint.setZ(_dm.formatOutput(intPoint.getZ()));

    	assertEquals(new SimplePoint3DCNC(20, 21.432, "[#504+0.1432*#507-0.1432*#504]"), intPoint);
    }
    
    @Test
    public void fullBilinear()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(12.765, 19.3, "null");
    	SimplePoint3DCNC intPointLeft = _probe.interpolateY(_probe.getBLPoint(point), _probe.getTLPoint(point), point);
    	SimplePoint3DCNC intPointRight = _probe.interpolateY(_probe.getBRPoint(point), _probe.getTRPoint(point), point);
    	String intPointLeftSave = _dm.formatNumber(intPointLeft.getZ());
    	String intPointRightSave = _dm.formatNumber(intPointRight.getZ());
    	intPointLeft.setZ("intPointLeftSave");
    	intPointRight.setZ("intPointRightSave");
    	
    	SimplePoint3DCNC intPointHoriz = _probe.interpolateX(intPointLeft, intPointRight, point);
    	point.setZ(_dm.formatNumber(intPointHoriz.getZ()));
    	
    	assertEquals("[#500+0.93*#503-0.93*#500]", intPointLeftSave);
    	assertEquals("[#501+0.93*#504-0.93*#501]", intPointRightSave);
    	assertEquals("[intPointLeftSave+0.2765*intPointRightSave-0.2765*intPointLeftSave]", _dm.formatOutput(intPointHoriz.getZ()));
    }
    
    @Test
    public void interpolateDivByZero()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(30, 30, "null");
    	SimplePoint3DCNC intPointLeft = _probe.interpolateY(_probe.getBLPoint(point), _probe.getTLPoint(point), point);
    	SimplePoint3DCNC intPointRight = _probe.interpolateY(_probe.getBRPoint(point), _probe.getTRPoint(point), point);
    	String intPointLeftSave = intPointLeft.getZ();
    	String intPointRightSave = intPointRight.getZ();
    	intPointLeft.setZ("intPointLeftSave");
    	intPointRight.setZ("intPointRightSave");
    	
    	SimplePoint3DCNC intPointHoriz = _probe.interpolateX(intPointLeft, intPointRight, point);
    	point.setZ(intPointHoriz.getZ());
    	
    	assertEquals("[#507+0*#508-0*#507]", _dm.formatOutput(intPointLeftSave));
    	assertEquals("#508", _dm.formatOutput(intPointRightSave));
    	assertEquals("[intPointLeftSave+1*intPointRightSave-1*intPointLeftSave]", _dm.formatOutput(intPointHoriz.getZ()));
    }
    
    @Test
    public void interpolateDivByZeroBottomLeft()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(10, 10, "null");
    	SimplePoint3DCNC intPointLeft = _probe.interpolateY(_probe.getBLPoint(point), _probe.getTLPoint(point), point);
    	SimplePoint3DCNC intPointRight = _probe.interpolateY(_probe.getBRPoint(point), _probe.getTRPoint(point), point);
    	String intPointLeftSave = intPointLeft.getZ();
    	String intPointRightSave = intPointRight.getZ();
    	intPointLeft.setZ("intPointLeftSave");
    	intPointRight.setZ("intPointRightSave");
    	
    	SimplePoint3DCNC intPointHoriz = _probe.interpolateX(intPointLeft, intPointRight, point);
    	point.setZ(intPointHoriz.getZ());
    	
    	assertEquals("#500", _dm.formatOutput(intPointLeftSave));
    	assertEquals("[#500+0*#503-0*#500]", _dm.formatOutput(intPointRightSave));
    	assertEquals("[intPointLeftSave+0*intPointRightSave-0*intPointLeftSave]", _dm.formatOutput(intPointHoriz.getZ()));
    }
    
    @Test
    public void interpolatePointOutside()
    {
    	SimplePoint3DCNC point = new SimplePoint3DCNC(6.78, 9.3, "null");
    	SimplePoint3DCNC intPointLeft = _probe.interpolateY(_probe.getBLPoint(point), _probe.getTLPoint(point), point);
    	SimplePoint3DCNC intPointRight = _probe.interpolateY(_probe.getBRPoint(point), _probe.getTRPoint(point), point);
    	String intPointLeftSave = intPointLeft.getZ();
    	String intPointRightSave = intPointRight.getZ();
    	intPointLeft.setZ("#500");
    	intPointRight.setZ("#500");
    	
    	SimplePoint3DCNC intPointHoriz = _probe.interpolateX(intPointLeft, intPointRight, point);
    	point.setZ(intPointHoriz.getZ());
    	
    	assertEquals("#500", intPointLeftSave);
    	assertEquals("#500", intPointRightSave);
    	assertEquals(point, new SimplePoint3DCNC(6.78, 9.3, "#500"));
    }
}
