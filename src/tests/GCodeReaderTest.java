package tests;

import autoleveller.GCodeReader;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GCodeReaderTest 
{
    private GCodeReader _reader;
    
    public GCodeReaderTest() {
    }
    
    @Before
    public void setUp() throws IOException 
    {
        _reader = new GCodeReader(new File("testcutout.ngc"));
    }
    
    @Test(expected=IOException.class)
    public void getCoordsFromStringNoFile() throws IOException
    {
        _reader = new GCodeReader(new File("notafile"));
    }
    
    @Test
    public void getCoordsFromString() throws IOException
    {        
        String testLine = "G1 X-5.324 Y54.543 ";
        
        assertEquals(-5.324, _reader.getDoubleFromChar(testLine, 'X'), 0);
        assertEquals(54.543, _reader.getDoubleFromChar(testLine, 'Y'), 0);
        assertEquals(Double.NaN, _reader.getDoubleFromChar(testLine, 'Z'), 0);
    }
    
    @Test
    public void getCoordsNoSpaces() throws IOException
    {
        String testLine = "G0X-0.500Y0.000Z2.000";
        
        assertEquals(-0.5, _reader.getDoubleFromChar(testLine, 'X'), 0);
        assertEquals(0, _reader.getDoubleFromChar(testLine, 'Y'), 0);
        assertEquals(2, _reader.getDoubleFromChar(testLine, 'Z'), 0);
    }
    
    
    @Test
    public void getDecimalDifferentLocales()
    {
    	String englishTestLine = "G0X-0.500Y3.543Z2.000";
    	String nanTestLine = "G0X-0.500Y-tZ2.000";
    	String euroTestLine = "G0X-0,500Y3,543Z2,000"; //commas in place of points
        
        assertEquals(3.543, _reader.getDoubleFromChar(englishTestLine, 'Y'), 0);
        assertEquals(Double.NaN, _reader.getDoubleFromChar(nanTestLine, 'Y'), 0);
        assertEquals(3.543, _reader.getDoubleFromChar(euroTestLine, 'Y'), 0);
    }
    
    @Test
    public void checkcoordState() throws IOException
    {
        _reader.readNextLine();
        String line = _reader.readNextLine(); //2nd line
        
        assertEquals("G21 G40 G49 G64 P0.03 M6 T1", line);
        assertEquals(Double.NaN, _reader.getCurrentCoords().getX(), 0);
        assertEquals(Double.NaN, _reader.getCurrentCoords().getY(), 0);
        assertEquals(Double.NaN, _reader.getCurrentCoords().getZ(), 0);
    }
    
    @Test
    public void checkcoordStateLine6() throws IOException
    {
        String line = "";
        for (int i=0; i < 6; i++)
            line = _reader.readNextLine(); //6th line
        
        assertEquals("G0X0.000Y0.000S8000M3", line);
        assertEquals(0, _reader.getCurrentCoords().getX(), 0);
        assertEquals(0, _reader.getCurrentCoords().getY(), 0);
        assertEquals(15, _reader.getCurrentCoords().getZ(), 0);
    }
    
    @Test
    public void peekAtLastLine() throws IOException
    {              
        for (int i = 0; i < 33; i++)
            _reader.readNextLine();
        
        assertNull(_reader.peek());  
    }
    
    @Test
    public void readLastLine() throws IOException
    {              
        String lastLine = "";
        
        for (int i = 0; i < 35; i++)
            lastLine = _reader.readNextLine();
        
        assertNull(lastLine);
    }
    
    @Test
    public void getMillArea() throws IOException
    {
        //1mm milling tool
        Rectangle2D rect = _reader.getArea();
        
        assertEquals(-0.5, rect.getX(), 0);
        assertEquals(101, rect.getWidth(), 0);
        assertEquals(-0.5, rect.getY(), 0);
        assertEquals(81, rect.getHeight(), 0);
    }
    
    @Test
    public void commentRemoverNoComments()
    {
    	String line = GCodeReader.stripComments("there are no comments on this line");
    	assertEquals("there are no comments on this line", line);
    }
    
    @Test
    public void commentsRemoverAllComments()
    {
    	String line = GCodeReader.stripComments("(this line is all comment)");
    	assertEquals("", line);
    }
    
    @Test
    public void commentsRemoverCommentsAtEnd()
    {
    	String line = GCodeReader.stripComments("this is not a comment(this is a comment)");
    	assertEquals("this is not a comment", line);
    }
    
    @Test
    public void commentRemoverNestedComments()
    {
    	String line = GCodeReader.stripComments("this is not a comment(this is a (inner Comment)comment)");
    	assertEquals("this is not a comment", line);
    }
    
    @Test
    public void logMCodeState()
    {
    	_reader.updateStateFromString("M01 F200");
    	_reader.updateStateFromString("M7 S40000");
    	assertTrue(_reader.getState().containsKey("M01"));
    	assertTrue(_reader.getState().containsKey("M7"));	
    }
 
    @Test
    public void logDuplicateMCodes()
    {
    	// already 4 mcodes in testcutout.ngc
    	
    	_reader.updateStateFromString("M01 F200");
    	assertEquals(34, (long)_reader.getState().get("M01"));
    	_reader.updateStateFromString("M01 F250");
    	assertEquals(35, (long)_reader.getState().get("M01"));
    }
    
    @Test
    public void logMultipleGCodes()
    {
    	_reader.updateStateFromString("G21 G40 G49 G64 P0.03 M6 T1");
    	assertTrue(_reader.getState().containsKey("G21"));
    	assertTrue(_reader.getState().containsKey("G40"));
    	assertTrue(_reader.getState().containsKey("G49"));
    	assertTrue(_reader.getState().containsKey("G64"));
    }
    
    @Test
    public void noVars()
    {
    	_reader.addVarsFromLine("M01 F250 (no vars #7=5.4)");
    	assertEquals(0, _reader.getVars().size());
    }
    
    @Test
    public void oneVar()
    {
    	_reader.addVarsFromLine("#3=4.23 (#4=2)");
    	
    	assertEquals(1, _reader.getVars().size());
    	assertEquals(4.23, _reader.getVars().get("#3"), 0);
    }
    
    @Test
    public void twoVar()
    {
    	_reader.addVarsFromLine("#1 = 0.2500  ( Safe Z )");
    	_reader.addVarsFromLine("#2 = -0.0050  ( Engraving Depth Z )");
    	
    	assertEquals(2, _reader.getVars().size());
    	assertEquals(0.25, _reader.getVars().get("#1"), 0);
    	assertEquals(-0.005, _reader.getVars().get("#2"), 0);
    }
    
    @Test
    public void singleVarNotAssigned()
    {
    	_reader.updateStateFromString("X#1");
    	assertEquals(0, _reader.getVars().size());
    }
    
    @Test
    public void singleVarAssigned()
    {
    	_reader.updateStateFromString("#1 = 0.2500  ( Safe Z )");
    	_reader.updateStateFromString("X#1");
    	
    	assertEquals(0.25, _reader.getCurrentCoords().getX(), 0);
    	assertEquals(1, _reader.getVars().size());
    }
    
    @Test
    public void singleVarAssignedNegative()
    {
    	_reader.updateStateFromString("#1 = -0.2500  ( Safe Z )");
    	_reader.updateStateFromString("X#1");
    	_reader.updateStateFromString("X#1, Y0");
    	
    	assertEquals(-0.25, _reader.getCurrentCoords().getX(), 0);
    	assertEquals(1, _reader.getVars().size());
    	assertEquals(0, _reader.getCurrentCoords().getY(), 0);
    }
}
