/*  	AutoLeveller (http://www.autoleveller.co.uk) is a stand-alone PC application written in Java which is designed
 *  	to measure precisely the height of the material to be milled / etched in several places,
 *  	then use the information gathered to make adjustments to the Z height
 *  	during the milling / etching process so that a more consistent and accurate result can be achieved. 
 *   
 *   	Copyright (C) 2013 James Hawthorne PhD, daedelus1982@gmail.com
 *
 *   	This program is free software; you can redistribute it and/or modify
 *   	it under the terms of the GNU General Public License as published by
 *   	the Free Software Foundation; either version 2 of the License, or
 *   	(at your option) any later version.
 *
 *   	This program is distributed in the hope that it will be useful,
 *   	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   	GNU General Public License for more details.
 *
 *   	You should have received a copy of the GNU General Public License along
 *   	with this program; if not, see http://www.gnu.org/licenses/
*/
package autoleveller;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.vecmath.Point3d;

public class GCodeReader
{
	private DecimalFormatSymbols _symbols = new DecimalFormatSymbols();
	protected DecimalFormat _localFormat = new DecimalFormat("#.####");
    private Pattern _floatPt = Pattern.compile("-?[0-9]*[\\.,]?[0-9]+");
    private Pattern _variable = Pattern.compile("#[1-9][0-9]*");
    private Pattern _varAssign = Pattern.compile(_variable.pattern() + "=" + _floatPt.pattern());
    protected BufferedReader _gcodeFile;
    private Rectangle2D _area = new Rectangle2D.Double();
    private File _rawFile;
    private Map<String, Long> _storedStates = new HashMap<String, Long>();
    private Map<String, Double> _storedVars = new HashMap<String, Double>();
    private String _currentGCode = null;
    private long _lineNumber = 0;
    
    //The current state as per the line read up to
    protected Point3d _currentCoords = new Point3d(Double.NaN, Double.NaN, Double.NaN);
    
    public GCodeReader(File gCodeFile) throws IOException
    {
       if (!(gCodeFile.exists())) 
           throw new IOException("file does not exist");
       
       _rawFile = gCodeFile;
       _gcodeFile = new BufferedReader(new FileReader(gCodeFile));
		_symbols.setDecimalSeparator('.');
		_symbols.setGroupingSeparator(',');
		_localFormat.setDecimalFormatSymbols(_symbols);
       createArea();
    }
    
    public File getOriginalFile()
    {
    	return _rawFile;
    }
   
    private void createArea() throws IOException
    {
        while (readThisLine() != null)
        {    
            if (gotAllPoints() && _currentCoords.getZ() < 0)
                _area.add(new Point2D.Double(_currentCoords.getX(), _currentCoords.getY()));
        }
        _gcodeFile.close();
        _gcodeFile = new BufferedReader(new FileReader(_rawFile));
        _currentCoords = new Point3d(Double.NaN, Double.NaN, Double.NaN);
    }
    
    protected boolean gotAllPoints()
    {
        if (Double.isNaN(_currentCoords.getX()))
            return false;
        if (Double.isNaN(_currentCoords.getY()))
            return false;
        if (Double.isNaN(_currentCoords.getZ()))
            return false;
        
        return true;
    }
    
    public Rectangle2D getArea()
    {
        return _area;
    }
    
    public double getDoubleFromChar(String line, char charToFind) 
    {
        String result;
        result = getStringDoubleFromChar(line, charToFind);
        result = result.contains(",") ? result.replaceAll(",", ".") : result;
        if (result != "")
        {
        	try {
				return _localFormat.parse(result).doubleValue();
			} catch (ParseException e) {JOptionPane.showMessageDialog(null, "parse error");}
        }
        return Double.NaN;
    }
    
    public boolean doesContain(String line, char charToFind)
    {
        String upperLine = line.toUpperCase();
        if (Double.isNaN(getDoubleFromChar(upperLine, charToFind)))
            return false;
        
        return true;
    }
    
    protected String getStringDoubleFromChar(String line, char charToFind)
    {
    	if (getAllStringDoubleFromChar(line, charToFind).size() > 0)
    		return getAllStringDoubleFromChar(line, charToFind).get(0);
    	
    	return "";
    }
    
    private ArrayList<String> getAllStringDoubleFromChar(String line, char charToFind)
    {
    	 String upperString = line.toUpperCase();
    	 upperString = upperString.replaceAll("\\s", "");
         Matcher floatMatcher = _floatPt.matcher(upperString);
         ArrayList<String> foundFloats = new ArrayList<String>();
         
         Pattern varNotAssign = Pattern.compile(_variable.pattern() + "(?!=)"); 
         Matcher varMatcher = varNotAssign.matcher(upperString);
         
         while (varMatcher.find())
         {
        	 if ((upperString.charAt(varMatcher.start()-1) == charToFind) && (_storedVars.containsKey(upperString.substring(varMatcher.start(), varMatcher.end()))))
                 foundFloats.add(_storedVars.get(upperString.substring(varMatcher.start(), varMatcher.end())).toString());
         }
         
         while (floatMatcher.find())
         {
             //if the character before the match is 'charToFind'
             if (upperString.charAt(floatMatcher.start()-1) == charToFind)
                 foundFloats.add(upperString.substring(floatMatcher.start(), floatMatcher.end()));
         }
         
         //searched whole string and didnt find what we wanted
         return foundFloats;
    }

    public String readNextLine() throws IOException 
    {
        return readThisLine();
    }
    
    private String readThisLine() throws IOException
    {
        String line = _gcodeFile.readLine();
        if (line == null)
            return null;
        
        String upperLine = line.toUpperCase();
        updateStateFromString(upperLine);
        
        return line;
    }
    
    public Point3d getCurrentCoords()
    {
        return _currentCoords;
    }
    
    private void recordState(String line)
    {
    	ArrayList<String> mCodes = getAllStringDoubleFromChar(line, 'M');
    	ArrayList<String> gCodes = getAllStringDoubleFromChar(line, 'G');
    	
    	for (String mCode: mCodes)
		{
			if (!mCode.equals(""))
				_storedStates.put("M" + mCode, _lineNumber);
		}
    		
    	for (String gCode: gCodes)
		{
			if (!gCode.equals(""))
			{
				_storedStates.put("G" + gCode, _lineNumber);
				_currentGCode = ("G" + gCode);
			}
		}
    }
    
    protected boolean isCurrentMoveLinear()
    {
    	String upperGCode = _currentGCode.toUpperCase();
    	if ((upperGCode.equals("G0")) || (upperGCode.equals("G00")) ||
    			(upperGCode.equals("G01")) || (upperGCode.equals("G1")))
    		return true;
    	
    	return false;    					
    }
    
    public Map<String, Long> getState()
    {
    	return _storedStates;
    }

    public void updateStateFromString(String upperLine) 
    {
    	_lineNumber++;
    	//add all variables
    	addVarsFromLine(upperLine);
    	//strip comments to avoid confusion when a comment exists at the end of a non-comment line
    	stripComments(upperLine);
    	//get machine states
    	recordState(upperLine);
    	
        double xValue = getDoubleFromChar(upperLine, 'X');
        double yValue = getDoubleFromChar(upperLine, 'Y');
        double zValue = getDoubleFromChar(upperLine, 'Z');
        if (!Double.isNaN(xValue))
            _currentCoords.setX(xValue);
        if (!Double.isNaN(yValue))
            _currentCoords.setY(yValue);
        if (!Double.isNaN(zValue))
            _currentCoords.setZ(zValue);
    }
    
    public static String stripComments(String gCodeLine)
    {
    	int outerMostCommentStart = gCodeLine.indexOf('(');
    	int outerMostCommentEnd = gCodeLine.lastIndexOf(')');
    	if (outerMostCommentEnd == -1 || outerMostCommentStart == -1)
    		return gCodeLine.trim();
    	String outerMostComment = gCodeLine.substring(outerMostCommentStart, outerMostCommentEnd + 1);
    	return gCodeLine.replace(outerMostComment, "").trim();	
    }
    
    public String peek() throws IOException
    {
        _gcodeFile.mark(1000);
        String retString = _gcodeFile.readLine();
        _gcodeFile.reset();
        return retString;
    }
    
    public static String getUnits(Map<String, Long> state)
    {
    	if (state.containsKey("G21"))
    		return "millimeters";
    	else if (state.containsKey("G20"))
    		return "inches";
    	
    	return "";
    }
    
    public void close() throws IOException
    {
        _gcodeFile.close();
    }

    public Map<String, Double> getVars()
    {
    	return _storedVars;
    }
    
	public void addVarsFromLine(String line) 
	{
		String upperNoSpace = stripComments(line);
		upperNoSpace = upperNoSpace.toUpperCase();
		upperNoSpace = upperNoSpace.replaceAll("\\s", "");
		
	     Matcher varAssignMatcher = _varAssign.matcher(upperNoSpace);
	     
	     while (varAssignMatcher.find())
	     {
	    	 String foundString = upperNoSpace.substring(varAssignMatcher.start(), varAssignMatcher.end());
	         String key = "#" + getStringDoubleFromChar(foundString, '#');
	         double value = getDoubleFromChar(foundString, '=');
	         
	         _storedVars.put(key, value);
	     }
	}
}