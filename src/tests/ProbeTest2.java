package tests;

import autoleveller.DoubleManipulator;
import autoleveller.SimplePoint3DCNC;
import autoleveller.probe.Probe;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProbeTest2 
{
    private Probe _probe;
    private DoubleManipulator _dm;
    
    public ProbeTest2() {}
    
    @Before
    public void setUp() 
    {
        _probe = Probe.createProbe("linuxCNC", "millimeters", 0, 0, 100, 80, 200, -1, 5, 20, 2);
        _dm = new DoubleManipulator();
    }
       
    @Test
    public void bottomLeft()
    {
        SimplePoint3DCNC blPoint = _probe.getBLPoint(new SimplePoint3DCNC(17.389, 29.876, "null"));
        
        assertEquals(15, blPoint.getX(), 0);
        assertEquals(25, blPoint.getY(), 0);
    }

    @Test
    public void topRightNegative()
    {
    	_probe = Probe.createProbe("linuxCNC", "millimeters", -80, 10, 75, 60, 200, -1, 5, 20, 2);
        SimplePoint3DCNC trPoint = _probe.getTRPoint(new SimplePoint3DCNC(-59.789, 29.876, "null"));
        
        assertEquals(-55, trPoint.getX(), 0);
        assertEquals(30, trPoint.getY(), 0);
    }
    
    @Test
    public void testExtremities()
    {
    	_probe = Probe.createProbe("linuxCNC", "millimeters", -80, 10, 75, 60, 200, -1, 5, 20, 2);
        SimplePoint3DCNC trPoint = _probe.getTRPoint(new SimplePoint3DCNC(-5, 70, "null"));
        SimplePoint3DCNC trPointOutside = _probe.getTRPoint(new SimplePoint3DCNC(-6, 71, "null"));
        
        assertEquals(-5, trPoint.getX(), 0);
        assertEquals(70, trPoint.getY(), 0);
        assertEquals(-5, trPointOutside.getX(), 0);
        assertEquals(70, trPointOutside.getY(), 0);
    }
    
    @Test
    public void testExtremities2()
    {
    	_probe = Probe.createProbe("linuxCNC", "millimeters", -80, 10, 75, 60, 200, -1, 5, 20, 2);
        SimplePoint3DCNC blExreme = new SimplePoint3DCNC(-80, 10, "null");
        SimplePoint3DCNC trEextreme = new SimplePoint3DCNC(-5, 70, "null");
        
        assertEquals(new SimplePoint3DCNC(-80, 10, "#500"), _probe.getBLPoint(blExreme));
        assertEquals(new SimplePoint3DCNC(-80, 10, "#500"), _probe.getTLPoint(blExreme));
        assertEquals(new SimplePoint3DCNC(-80, 10, "#500"), _probe.getBRPoint(blExreme));
        assertEquals(new SimplePoint3DCNC(-80, 15, "#516"), _probe.getTRPoint(blExreme));
        assertEquals(new SimplePoint3DCNC(-5, 70, "#707"), _probe.getTRPoint(trEextreme));
        assertEquals(new SimplePoint3DCNC(-5, 70, "#707"), _probe.getBRPoint(trEextreme));
        assertEquals(new SimplePoint3DCNC(-5, 70, "#707"), _probe.getTLPoint(trEextreme));
        assertEquals(new SimplePoint3DCNC(-10, 70, "#706"), _probe.getBLPoint(trEextreme));
    }
    
    @Test
    public void bottomLeftNegative()
    {
    	_probe = Probe.createProbe("linuxCNC", "millimeters", -80, 10, 80, 60, 200, -1, 5, 20, 2);
        SimplePoint3DCNC blPoint = _probe.getBLPoint(new SimplePoint3DCNC(-59.789, 29.876, "null"));
        
        assertEquals(-60, blPoint.getX(), 0);
        assertEquals(25, blPoint.getY(), 0);
    }
    
    @Test
    public void topLeft()
    {
        _probe = Probe.createProbe("linuxCNC", "millimeters", 0, 0, 50, 30, 200, -1, 5, 20, 2);
        SimplePoint3DCNC tlPoint = _probe.getTLPoint(new SimplePoint3DCNC(17.389, 29.876, "null"));
        
        assertEquals(15, tlPoint.getX(), 0);
        assertEquals(30, tlPoint.getY(), 0);
    }
    
    @Test
    public void topLeftNegative()
    {
        _probe = Probe.createProbe("linuxCNC", "millimeters", -25, 0, 70, 30, 200, -1, 5, 20, 2);
        SimplePoint3DCNC tlPoint = _probe.getTLPoint(new SimplePoint3DCNC(-19.3, 29.876, "null"));
        
        assertEquals(-20, tlPoint.getX(), 0);
        assertEquals(30, tlPoint.getY(), 0);
    }
    
    @Test
    public void topRight()
    {
        _probe = Probe.createProbe("linuxCNC", "millimeters", 0, 0, 50, 30, 200, -1, 5, 20, 2);
        SimplePoint3DCNC tlPoint = _probe.getTRPoint(new SimplePoint3DCNC(17.389, 26.876, "null"));
        
        assertEquals(20, tlPoint.getX(), 0);
        assertEquals(30, tlPoint.getY(), 0);
    }
    
    @Test
    public void bottomRightNegative()
    {
        _probe = Probe.createProbe("linuxCNC", "millimeters", -10, 0, 70, 30, 200, -1, 5, 20, 2);
        SimplePoint3DCNC brPoint = _probe.getBRPoint(new SimplePoint3DCNC(-9.389, 29.876, "null"));
        
        assertEquals(-5, brPoint.getX(), 0);
        assertEquals(25, brPoint.getY(), 0);
    }
    
    @Test
    public void getAllPoints()
    {
    	_probe = Probe.createProbe("linuxCNC", "millimeters", 0, 0, 100, 80, 200, -1, 5, 20, 2);
        SimplePoint3DCNC point = new SimplePoint3DCNC(47.675, 77.566, "null");
        
        SimplePoint3DCNC tlPoint = _probe.getTLPoint(point);
        SimplePoint3DCNC blPoint = _probe.getBLPoint(point);
        SimplePoint3DCNC trPoint = _probe.getTRPoint(point);
        SimplePoint3DCNC brPoint = _probe.getBRPoint(point);
        
        assertEquals(45, tlPoint.getX(), 0);
        assertEquals(80, tlPoint.getY(), 0);
        assertEquals(45, blPoint.getX(), 0);
        assertEquals(75, blPoint.getY(), 0);
        assertEquals(50, trPoint.getX(), 0);
        assertEquals(80, trPoint.getY(), 0);
        assertEquals(50, brPoint.getX(), 0);
        assertEquals(75, brPoint.getY(), 0);
    }
    
    @Test
    public void getAllNegative()
    {
    	_probe = Probe.createProbe("linuxCNC", "millimeters", -90, 0, 105, 80, 200, -1, 5, 20, 2);
    	SimplePoint3DCNC point = new SimplePoint3DCNC(-78.675, 12.566, "null");
        
        SimplePoint3DCNC tlPoint = _probe.getTLPoint(point);
        SimplePoint3DCNC blPoint = _probe.getBLPoint(point);
        SimplePoint3DCNC trPoint = _probe.getTRPoint(point);
        SimplePoint3DCNC brPoint = _probe.getBRPoint(point);
        
        assertEquals(-80, tlPoint.getX(), 0);
        assertEquals(15, tlPoint.getY(), 0);
        assertEquals(-80, blPoint.getX(), 0);
        assertEquals(10, blPoint.getY(), 0);
        assertEquals(-75, trPoint.getX(), 0);
        assertEquals(15, trPoint.getY(), 0);
        assertEquals(-75, brPoint.getX(), 0);
        assertEquals(10, brPoint.getY(), 0);
    }
    
    @Test
    public void exponentCheck()
    {
    	_probe = Probe.createProbe("linuxCNC", "inches", -0.0379, -0.0447, 1.5332000000000001, 1.2861, 5, -0.0625, 0.250, 1, 0.125);
    	SimplePoint3DCNC point = new SimplePoint3DCNC(1.2193, 0.7271, "null");
        
        SimplePoint3DCNC tlPoint = _probe.getTLPoint(point);
        SimplePoint3DCNC blPoint = _probe.getBLPoint(point);
        SimplePoint3DCNC trPoint = _probe.getTRPoint(point);
        SimplePoint3DCNC brPoint = _probe.getBRPoint(point);
        SimplePoint3DCNC intPointLeft = _probe.interpolateY(blPoint, tlPoint, point);
    	SimplePoint3DCNC intPointRight = _probe.interpolateY(brPoint, trPoint, point);
        String intPointLeftSave = intPointLeft.getZ();
    	String intPointRightSave = intPointRight.getZ();
    	intPointLeft.setZ("intPointLeftSave");
    	intPointRight.setZ("intPointRightSave");
    	
    	SimplePoint3DCNC intPointHoriz = _probe.interpolateX(intPointLeft, intPointRight, point);
    	point.setZ(intPointHoriz.getZ());
    	
    	assertEquals("[#525+0.0005*#532-0.0005*#525]", _dm.formatOutput(intPointLeftSave));
    	assertEquals("[#526+0.0005*#533-0.0005*#526]", _dm.formatOutput(intPointRightSave));
    	assertEquals("[intPointLeftSave+0.9199*intPointRightSave-0.9199*intPointLeftSave]", _dm.formatOutput(intPointHoriz.getZ()));
    }
}
