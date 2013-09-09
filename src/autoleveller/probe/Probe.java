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
package autoleveller.probe;

import autoleveller.SimplePoint3DCNC;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;

public abstract class Probe
{
    private SimplePoint3DCNC[][] _probePoints;

    //probing values
    private String _units;
    private Rectangle2D.Double _probeArea;
    private double _probeFeed;
    private double _probeDepth;
    private double _finishHeight;
    private double _probeClearance;
    
    protected Probe(String units, double xStart, double yStart, double millWidth, double millHeight,
            double probeFeed, double probeDepth, double probeSpacing, double finishHeight, double probeClearance)
    {
        setProbeVars(units, xStart, yStart, millWidth, millHeight, probeFeed, probeDepth, finishHeight, probeClearance);
        setProbePoints(createProbePoints(xStart, yStart, millWidth, millHeight, probeSpacing));
        double width = Math.abs(_probePoints[_probePoints.length-1][_probePoints[0].length-1].getX() - _probePoints[0][0].getX());
        double height = Math.abs(_probePoints[_probePoints.length-1][_probePoints[0].length-1].getY() - _probePoints[0][0].getY());
        _probeArea = new Rectangle2D.Double(_probePoints[0][0].getX(), _probePoints[0][0].getY(), width, height); 
    }
    
    public void setProbeVars(String units, double xStart, double yStart, double millWidth, double millHeight,
            double probeFeed, double probeDepth, double finishHeight, double probeClearance)
    {
        _units = units;
        _probeFeed = probeFeed;
        _probeDepth = probeDepth; 
        _finishHeight = finishHeight;
        _probeClearance = probeClearance;
    }
    
    public static Probe createProbe(String software, String units, double xStart, double yStart, double millWidth, double millHeight,
            double probeFeed, double probeDepth, double probeSpacing, double finishHeight, double probeClearance)
    {
        if (software.equalsIgnoreCase("LinuxCNC"))
            return new LinuxCNCProbe(units, xStart, yStart, millWidth, millHeight, probeFeed, probeDepth, probeSpacing, finishHeight, probeClearance);
        else if (software.equalsIgnoreCase("Mach3"))
            return new Mach3Probe(units, xStart, yStart, millWidth, millHeight, probeFeed, probeDepth, probeSpacing, finishHeight, probeClearance);
        else 
            return null;
            
    }
    
    public String getUnits()
    {
    	return _units;
    }
    
    public void setProbePoints(SimplePoint3DCNC[][] points)
    {
        _probePoints = points;
    }
    
    public SimplePoint3DCNC[][] getProbePoints()
    {
        return _probePoints;
    }
    
    public Rectangle2D getArea()
    {
        return _probeArea;
    }
    
    public static SimplePoint3DCNC[][] createProbePoints(double xStart, double yStart, 
            double width, double height, double desiredSpacing)
    {  	
        double xSpaces = Math.abs(width / desiredSpacing);
        double ySpaces = Math.abs(height / desiredSpacing);
        //add 1 for start and end points
        int yPoints = ((int)ySpaces) + 1;
        int xPoints = ((int)xSpaces) + 1;
        
        double xEvenSpacing = width / (int)xSpaces;
        double yEvenSpacing = height / (int)ySpaces;
        
        SimplePoint3DCNC[][] points = new SimplePoint3DCNC[yPoints][xPoints];
        
        int zVariable = 500;
        
        for (int j = 0; j < yPoints; j++)
        {
            for (int i = 0; i < xPoints; i++)
            {
            	
            	double xPoint = (xStart + (i * xEvenSpacing));
            	double yPoint = (yStart + (j * yEvenSpacing));
                points[j][i] = new SimplePoint3DCNC(xPoint, yPoint, "#" + zVariable++);
            }
        	
        }
        
        return points;
    }
    
    public boolean isPointInside(Point2D point)
    {
    	return isPointInside(getArea(), point);
    }
    
    public static boolean isPointInside(Rectangle2D area, Point2D point)
    {
    	boolean xInside = isBetween(area.getX(), (area.getWidth() + area.getX()), point.getX());
    	boolean yInside = isBetween(area.getY(), (area.getHeight() + area.getY()), point.getY());
    	
    	return xInside && yInside;
    }
    
    private static boolean isBetween(double firstNum, double secondNum, double numToTest)
    {
    	double smallest = Math.min(firstNum, secondNum);
    	double biggest = Math.max(firstNum, secondNum);
    	
    	return ((numToTest >= smallest) && (numToTest <= biggest)); 
    }
    
    public void writeProbe(PrintWriter file)
    {
        file.println("(prerequisites)");
        file.println("(1. need a working probe)");
        if (_units.equals("millimeters"))
        {
            file.println("(2. tool needs to be within 10mm of copper board for the 1st probe, )");
            file.println("(i.e. Z0.000 should be no more than 10mm above the board initially)");
        }
        else
        {
            file.println("(2. tool needs to be within 3/8\" of copper board for the 1st probe, )");
            file.println("(i.e. Z0.000 should be no more than 3/8\" above the board initially)");
        }
        file.println("(Note: The first probe will touch off Z to 0.000 when it first touches to copper, )");
        file.println("(all other probe values are relative to this first point)");
        file.println();
        if (_units.equals("millimeters"))
            file.println("G21 (millimeters)");
        else
            file.println("G20 (Inches)");
        file.println("G90 (absolute distance mode, not incremental)");
        file.println();
        openLog(file);
        file.println("(begin initial probe and set Z to 0)");
        file.println("G0 X" + _probeArea.getX() + " Y" + _probeArea.getY() + " Z0");
        if (_units.equals("millimeters"))
            probeInit(file, "-10");
        else
            probeInit(file, "-0.375");
        for (int i=0; i < _probePoints.length; i++)
        {
        	if ((i % 2) == 0)
        	{
	            for (int j = 0; j < _probePoints[0].length; j++)
	                moveNProbe(file, i, j);
        	}
        	else
        	{
        		for (int j = _probePoints[0].length -1; j >= 0; j--)
                    moveNProbe(file, i, j);
        	}
        }
        file.println("G0 Z" + _probeClearance);
        file.println("G0 X" + _probeArea.getX() + " Y" + _probeArea.getY() +  " Z" + _finishHeight);
        file.println("(Set S value to ensure Speed has a value otherwise the spindle will not start on an M3 command)");
        file.println("S20000");
        closeLog(file);
        file.println();
        file.println("(The program will pause to allow the probe to be detached)");
        file.println("(press cycle start to resume from current line)");
        file.println("M0");
    }
    
    private void probeInit(PrintWriter writer, String depth)
    {
    	writer.println(probeCommand(depth, String.valueOf(_probeFeed)));
    	writer.println(zeroZ());
    	writer.println("G0 Z" + _probeClearance);
    	writer.println(probeCommand(String.valueOf(_probeDepth), String.valueOf((_probeFeed/2))));
    	writer.println(zeroZ());    	
    }
    
    private void moveNProbe(PrintWriter writer, int row, int col)
    {
    	writer.println("G0 Z" + _probeClearance);
        writer.println("G0 X" + _probePoints[row][col].getX() +  " Y" + _probePoints[row][col].getY());
        writer.println(probeCommand(String.valueOf(_probeDepth), String.valueOf(_probeFeed)));
        writer.println(_probePoints[row][col].getZ() + "=" + currentZ());
        logProbePoint(writer, _probePoints[row][col]);
    }
    
    protected abstract String probeCommand(String depth, String feed);
    
    protected abstract String currentZ();
    
    protected abstract String zeroZ();
    
    protected abstract void openLog(PrintWriter writer);
    
    protected abstract void logProbePoint(PrintWriter writer, SimplePoint3DCNC point);
    
    protected abstract void closeLog(PrintWriter writer);
    
    public abstract void writeSubs(PrintWriter writer);
    
    public SimplePoint3DCNC getTRPoint(SimplePoint3DCNC pointToFind)
    {
    	return getPoint(pointToFind, false, true);
    }
    
    public SimplePoint3DCNC getTLPoint(SimplePoint3DCNC pointToFind)
    {
    	return getPoint(pointToFind, true, true);
    }
    
    public SimplePoint3DCNC getBRPoint(SimplePoint3DCNC pointToFind)
    { 
    	return getPoint(pointToFind, false, false);
    }
    
    public SimplePoint3DCNC getBLPoint(SimplePoint3DCNC pointToFind)
    {
    	return getPoint(pointToFind, true, false);
    }
    
    private SimplePoint3DCNC getPoint(SimplePoint3DCNC pointToFind, boolean isLeft, boolean isTop)
    {
    	SimplePoint3DCNC nearestPoint = getNearestPoint(getProbePoints(), pointToFind.toPoint2D());
    	
    	try
    	{	
	    	if (!isPointInside(pointToFind.toPoint2D()))
	    		return nearestPoint;
	    	
	    	int col = getNearestColumn(getProbePoints(), pointToFind.toPoint2D());
	    	int row = getNearestRow(getProbePoints(), pointToFind.toPoint2D());
	    	
	    	boolean leftOf = isLeft(nearestPoint, pointToFind.toPoint2D());
	    	boolean topOf = isTop(nearestPoint, pointToFind.toPoint2D());
	    	
	    	if (leftOf && !isLeft)
	    		col++;
	    	else if (!leftOf && isLeft)
	    		col--;
	    	
	    	if (topOf && !isTop)
	    		row--;
	    	else if (!topOf && isTop)
	    		row++;
	    	
    		SimplePoint3DCNC returnPoint = getProbePoints()[row][col];
    		return returnPoint;
    	}
    	catch (ArrayIndexOutOfBoundsException ae)
    	{
    		return nearestPoint;
    	}

    }
    

	public static int getNearestColumn(SimplePoint3DCNC[][] points,	Point2D point) 
	{
		double distance = (Math.abs(point.getX() - points[0][0].getX()));
		int col = 0;
		
		for (int i = 1; i < points[0].length; i++)
		{
			if ((Math.abs(point.getX() - points[0][i].getX())) <= distance)
			{
				distance = (Math.abs(point.getX() - points[0][i].getX()));
				col = i;
			}
		}
		return col;
	}
	
	public static int getNearestRow(SimplePoint3DCNC[][] points, Point2D point)
	{
		double distance = (Math.abs(point.getY() - points[0][0].getY()));
		int row = 0;
		
		for (int i = 1; i < points.length; i++)
		{
			if ((Math.abs(point.getY() - points[i][0].getY())) <= distance)
			{
				distance = (Math.abs(point.getY() - points[i][0].getY()));
				row = i;
			}
		}
		
		return row;
	}
	
	public static SimplePoint3DCNC getNearestPoint(SimplePoint3DCNC[][] points, Point2D point)
	{
		return points[getNearestRow(points, point)][getNearestColumn(points, point)];
	}
	
	public static boolean isLeft(SimplePoint3DCNC nearestProbePoint, Point2D originalPoint)
	{
		double distance = originalPoint.getX() - nearestProbePoint.getX();
		
		//if distance is positive then nearestProbePoint is left of the original point
		return distance > 0;
	}
	
	public static boolean isTop(SimplePoint3DCNC nearestProbePoint, Point2D originalPoint)
	{
		double distance = originalPoint.getY() - nearestProbePoint.getY();
		
		//if distance is positive then nearestProbePoint is bottom of the original point
		return distance < 0;
	}
	
	private double divZeroTest(double secondCoord, double firstCoord)
	{
		double addition = 0;
		
		if (secondCoord - firstCoord == 0)
			addition += 0.001;
		
		return addition;
	}
    
    public SimplePoint3DCNC interpolateY(SimplePoint3DCNC firstPoint, SimplePoint3DCNC secondPoint, SimplePoint3DCNC intPoint)
    {
    	SimplePoint3DCNC returnPoint = new SimplePoint3DCNC(firstPoint.getX(), intPoint.getY(), "null");
    	
    	if (firstPoint.equals(secondPoint))
    		returnPoint.setZ(firstPoint.getZ());
    	else
    	{
    		double addition = divZeroTest(secondPoint.getY(), firstPoint.getY()); //ensures no division by zero errors
    		double inner = (intPoint.getY() - firstPoint.getY()) / 
    				((secondPoint.getY() + addition) - firstPoint.getY());
    		returnPoint.setZ("[" + firstPoint.getZ() + "+" + inner + "*" + secondPoint.getZ() +
    				"-" + inner + "*" + firstPoint.getZ() + "]");
    	}
    	
    	return returnPoint;
    }
    
    public SimplePoint3DCNC interpolateX(SimplePoint3DCNC firstPoint, SimplePoint3DCNC secondPoint, SimplePoint3DCNC intPoint)
    {
    	SimplePoint3DCNC returnPoint = new SimplePoint3DCNC(intPoint.getX(), firstPoint.getY(), "null");
    	
    	if (firstPoint.equals(secondPoint))
    		returnPoint.setZ(firstPoint.getZ());
    	else
    	{
    		double addition = divZeroTest(secondPoint.getX(), firstPoint.getX());
    		double inner = (intPoint.getX() - firstPoint.getX()) / 
    				((secondPoint.getX() + addition) - firstPoint.getX());
    		returnPoint.setZ("[" + firstPoint.getZ() + "+" + inner + "*" + secondPoint.getZ() +
    				"-" + inner + "*" + firstPoint.getZ() + "]");
    	}
    	
    	return returnPoint;
    }
}