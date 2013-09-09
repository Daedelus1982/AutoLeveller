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
import java.io.File;
import java.io.IOException;
import javax.vecmath.Point3d;

public class GCodeBreaker extends GCodeReader
{
    private double _segmentLength;
    
    public GCodeBreaker(File file, double segmentLength) throws IOException
    {
        super(file);
        _segmentLength = segmentLength;
    }
    
    public Point2D reduceSegment(Point2D point1, Point2D point2, double segmentLength)
    {
        double distance = point1.distance(point2);        
        
        if (distance > segmentLength)
        {
            double k = 1 / (distance / segmentLength);
            double newX = point1.getX() + k * (point2.getX() - point1.getX());
            double newY = point1.getY() + k * (point2.getY() - point1.getY());
            
            return new Point2D.Double(newX, newY);
        }
        else
            return point2;
    }
    
    @Override
    public String readNextLine() throws IOException
    {
        //we only need to break the line down if we are cutting
        //where < 0 is assumed to be cutting
        if (gotAllPoints() && (_currentCoords.getZ() < 0) && (isCurrentMoveLinear()))
        {
            String nextString = peek();
            if (nextString == null)
                return null;
            nextString = nextString.toUpperCase();

            Point2D startPoint = new Point2D.Double(_currentCoords.getX(), _currentCoords.getY());
            Point3d endPoint3D = createPointFromString(_currentCoords, nextString);
            Point2D endPoint = new Point2D.Double(endPoint3D.getX(), endPoint3D.getY());
            if (startPoint.distance(endPoint) > _segmentLength)
            {
                Point2D segment = reduceSegment(startPoint, endPoint, _segmentLength);
                String segmentString = createStringFromPoint(nextString, segment);
                updateStateFromString(segmentString);
                return segmentString;
            }
        }
        
        return super.readNextLine();
    }
    
    public Point3d createPointFromString(Point3d prevPoint, String line)
    {
        Point3d retPoint = new Point3d(prevPoint);
        
        if (!Double.isNaN(getDoubleFromChar(line, 'X')))
            retPoint.setX(getDoubleFromChar(line, 'X'));
        if (!Double.isNaN(getDoubleFromChar(line, 'Y')))
            retPoint.setY(getDoubleFromChar(line, 'Y'));
        if (!Double.isNaN(getDoubleFromChar(line, 'Z')))
            retPoint.setZ(getDoubleFromChar(line, 'Z'));
        
        return retPoint;
    }
    
    public String createStringFromPoint(String original, Point2D point)
    {
        String modifiedString = original.toUpperCase();
        
        modifiedString = modifiedString.replaceAll(("X" + getStringDoubleFromChar(modifiedString, 'X')), "X" + _localFormat.format(point.getX()));
        modifiedString = modifiedString.replaceAll(("Y" + getStringDoubleFromChar(modifiedString, 'Y')), "Y" + _localFormat.format(point.getY()));
        
        return modifiedString;
    }
}