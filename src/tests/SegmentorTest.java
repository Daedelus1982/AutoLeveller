package tests;

import autoleveller.GCodeBreaker;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import javax.vecmath.Point3d;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SegmentorTest 
{
    private GCodeBreaker _breaker;
    
    public SegmentorTest() {
    }
    
    @Before
    public void setUp() throws IOException 
    {
        _breaker = new GCodeBreaker(new File("testcutout.ngc"), 5);
    }
    
    @Test
    public void reduceSegment()
    {
        Point2D point1 = new Point2D.Double(3, 8);
        Point2D point2 = new Point2D.Double(34, 23);
        
        Point2D newPoint = _breaker.reduceSegment(point1, point2, 5);
        Point2D newpoint2 = _breaker.reduceSegment(newPoint, point2, 5);
        
        assertEquals(5, point1.distance(newPoint), 0.0001);
        assertEquals(5, newPoint.distance(newpoint2), 0.0001);
    }
    
    @Test
    public void reduceSegmentNegative()
    {
        Point2D point1 = new Point2D.Double(-3, 22);
        Point2D point2 = new Point2D.Double(-14, 2);
        
        Point2D newPoint = _breaker.reduceSegment(point1, point2, 7);
        Point2D newpoint2 = _breaker.reduceSegment(newPoint, point2, 7);
        
        assertEquals(7, point1.distance(newPoint), 0.0001);
        assertEquals(7, newPoint.distance(newpoint2), 0.0001);
    }
    
    @Test
    public void writePointString()
    {
        Point2D point = new Point2D.Double(5.345456, 6.564545678);
        String original = "G01 X36.8473 Y74.5005 Z0.6000";
        
        String modifiedString = _breaker.createStringFromPoint(original, point);
        
        assertEquals("G01 X5.34546 Y6.56455 Z0.6000", modifiedString);
    }
    
    @Test
    public void equalPoints()
    {
        Point2D point1 = new Point2D.Double(3, 22);
        Point2D point2 = new Point2D.Double(6, 23);
        
        Point2D newPoint = _breaker.reduceSegment(point1, point2, 10);
        
        assertEquals(newPoint, point2);
    }
    
   @Test
    public void breakDown() throws IOException
    {
        String lastLine = "";
        
        for (int i = 0; i < 13; i++)
            lastLine = _breaker.readNextLine();
        
        assertEquals("G1Y0.000Z-1.000", lastLine);
        
        //the next line is 80mm away so at a breakdown of 5mm there will be 16 
        //lines between
        for (int i = 0; i < 16; i++)
            lastLine = _breaker.readNextLine();

        assertEquals("G1Y80.000F250.0", lastLine);
    }
    
    @Test
    public void peekAt() throws IOException
    {      
        for (int i = 0; i < 9; i++)
            _breaker.readNextLine();
        
        assertEquals("G1Y0.000Z-1.000", _breaker.peek());
        assertEquals("G1Y0.000Z-1.000", _breaker.peek());
    }
    
    @Test
    public void pointFromString()
    {
        Point3d point = _breaker.createPointFromString(new Point3d(-12.12, 32.23, 8.98), "G1Y80.12000F250.0");
        
        assertEquals(-12.12, point.getX(), 0);
        assertEquals(80.12, point.getY(), 0);
        assertEquals(8.98, point.getZ(), 0);
    }
}
