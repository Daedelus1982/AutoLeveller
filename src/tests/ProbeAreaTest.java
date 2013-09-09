package tests;

import autoleveller.SimplePoint3DCNC;
import autoleveller.Surface;
import autoleveller.probe.Probe;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProbeAreaTest 
{
    private Probe _probe;
    
    
    public ProbeAreaTest() {
    }
    
    @Before
    public void setUp() 
    {
        _probe = Probe.createProbe("linuxCNC", "millimeteters", 10, 10, 20, 40, 200, -1, 10, 20, 2);
    }
    
    @Test
    public void pointInside()
    {
        assertTrue(_probe.isPointInside(new Point2D.Double(15,35)));
    }
    
    @Test
    public void pointOutside()
    {
        assertFalse(_probe.isPointInside(new Point2D.Double(15,51)));
    }
    
    @Test
    public void negativePoint()
    {
    	assertTrue(Probe.isPointInside(new Rectangle2D.Double(-4, 0, 4, 5), new Point2D.Double(-3, 3)));
    }
    
    @Test
    public void correctSize()
    {        
        SimplePoint3DCNC[][] points = Probe.createProbePoints(0, 0, 40, 20, 5);
        
        assertEquals(5, points.length);
        assertEquals(9, points[0].length);
    }
    
    @Test
    public void negativeSize()
    {        
        SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, -70, 50, 5);
        
        assertEquals(11, points.length);
        assertEquals(15, points[0].length);
    }
    
    @Test
    public void testXValues()
    {        
        SimplePoint3DCNC[][] points = Probe.createProbePoints(0, 0, 40, 20, 5);
        
        assertEquals(0, points[0][0].getX(), 0);
        assertEquals(40, points[0][8].getX(), 0);
    }
    
    @Test
    public void testXNegative()
    {
        SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, -70, 50, 5);
        
        assertEquals(-10, points[0][0].getX(), 0);
        assertEquals(-80, points[8][14].getX(), 0);
        assertEquals(60, points[10][12].getY(), 0);
    }
    
    @Test
    public void nearestColumn()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	
    	int nearestColum = (Probe.getNearestColumn(points, new Point2D.Double(13.543, 11.34)));
    	
    	assertEquals(15, points[0][nearestColum].getX(), 0);
    }
    
    @Test
    public void nearestColumnOutside()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	
    	int nearestColum = (Probe.getNearestColumn(points, new Point2D.Double(-13.543, 11.34)));
    	
    	assertEquals(-10, points[0][nearestColum].getX(), 0);
    }
    
    @Test
    public void nearestRow()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	
    	int nearestRow = (Probe.getNearestRow(points, new Point2D.Double(13.543, 17.34)));
    	
    	assertEquals(15, points[nearestRow][0].getY(), 0);
    }
    
    @Test
    public void nearestRowOutside()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	
    	int nearestRow = (Probe.getNearestRow(points, new Point2D.Double(-13.543, 9.34)));
    	
    	assertEquals(10, points[nearestRow][0].getY(), 0);
    }
    
    @Test
    public void isLeft()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	Point2D pointToTest = new Point2D.Double(-3.543, 42.34);
    	SimplePoint3DCNC nearestPoint = Probe.getNearestPoint(points, pointToTest);
    	
    	assertTrue(Probe.isLeft(nearestPoint, pointToTest));
    }
    
    @Test
    public void isRight()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	Point2D pointToTest = new Point2D.Double(-2.443, 42.34);
    	SimplePoint3DCNC nearestPoint = Probe.getNearestPoint(points, pointToTest);
    	
    	assertFalse(Probe.isLeft(nearestPoint, pointToTest));
    }

    
    @Test
    public void isBottom()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	Point2D pointToTest = new Point2D.Double(-3.543, 42.34);
    	SimplePoint3DCNC nearestPoint = Probe.getNearestPoint(points, pointToTest);
    	
    	assertFalse(Probe.isTop(nearestPoint, pointToTest));
    }
    
    @Test
    public void isTop()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	Point2D pointToTest = new Point2D.Double(-3.543, 43.34);
    	SimplePoint3DCNC nearestPoint = Probe.getNearestPoint(points, pointToTest);
    	
    	assertTrue(Probe.isTop(nearestPoint, pointToTest));
    }
    
    @Test
    public void isMiddle()
    {
    	SimplePoint3DCNC[][] points = Probe.createProbePoints(-10, 10, 70, 50, 5);
    	Point2D yPointToTest = new Point2D.Double(-3.543, 42.5);
    	Point2D xPointToTest = new Point2D.Double(-2.5, 43.4);
    	SimplePoint3DCNC nearestPoint = Probe.getNearestPoint(points, yPointToTest);
    	SimplePoint3DCNC nearestPoint2 = Probe.getNearestPoint(points, xPointToTest);
    	
    	assertTrue(Probe.isTop(nearestPoint, yPointToTest));
    	assertFalse(Probe.isLeft(nearestPoint2, xPointToTest));
    	
    	// so top and right points take priority over bottom and left when the point lay between
    }
    
    @Test
    public void jobFits()
    {
    	Rectangle2D millArea = new Rectangle2D.Double(0, 0, 34, 56);
    	Rectangle2D probeArea = new Rectangle2D.Double(0, 0, 34, 56);
    	
    	assertTrue(Surface.probeAreaGEJobArea(probeArea, millArea));
    	
    	probeArea = new Rectangle2D.Double(0, 0, 35, 56);
    	assertTrue(Surface.probeAreaGEJobArea(probeArea, millArea));
    }
    
    @Test
    public void jobFitsNegative()
    {
    	Rectangle2D millArea = new Rectangle2D.Double(-80, 0, 80, 50);
    	Rectangle2D probeArea = new Rectangle2D.Double(-90, 0, 90, 56);
    	
    	boolean result = Surface.probeAreaGEJobArea(probeArea, millArea);
    	
    	assertTrue(result);
    }
    
    @Test
    public void jobDoesntFitNegative()
    {
    	Rectangle2D millArea = new Rectangle2D.Double(-55, 5, 55, 50);
    	Rectangle2D probeArea = new Rectangle2D.Double(-55, 5, 54, 50);
    	
    	boolean result = Surface.probeAreaGEJobArea(probeArea, millArea);
    	
    	assertFalse(result);
    }
}
