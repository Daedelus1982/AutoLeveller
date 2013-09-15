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

import autoleveller.probe.Probe;

import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.vecmath.Point3d;

public class Surface
{
    private Probe _probe;
    private GCodeBreaker _segFile;
    private File _outputFile;
    
    public Surface(Probe probe, File outputFile, File inputFile) throws IOException
    {
    	_probe = probe;
    	_outputFile = outputFile;
    	double units = (_probe.getUnits().equalsIgnoreCase("millimeters")) ? 5 : 0.187;
    	_segFile = new GCodeBreaker(inputFile, units);
    }
    
    
    public boolean probeAreaGEJobArea()
    {
    	return probeAreaGEJobArea(_probe.getArea(), _segFile.getArea());
    }
    
    public static boolean probeAreaGEJobArea(Rectangle2D probeArea, Rectangle2D millArea)
    {
    	return probeArea.contains(millArea);
    }
    
    public void writeLeveledFile()
    {    
        try (PrintWriter lvldFile = new NoExponentWriter(new BufferedWriter(new FileWriter(_outputFile, false))))
        {
        	writePreAmble(lvldFile, _segFile.getOriginalFile());
        	lvldFile.println();
            _probe.writeProbe(lvldFile);
            lvldFile.println();
            _probe.writeSubs(lvldFile);
            lvldFile.println();
            writeMillFile(lvldFile, _segFile);
            lvldFile.println();
            _segFile.close();
            lvldFile.close();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "File error occured: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void writeBilinear(PrintWriter writer, Point3d point)
    {
    	SimplePoint3DCNC currentPoint = SimplePoint3DCNC.point3dToSimplePoint3DCNC(point);
    	SimplePoint3DCNC intPointLeft = _probe.interpolateY(_probe.getBLPoint(currentPoint), _probe.getTLPoint(currentPoint), currentPoint);
    	SimplePoint3DCNC intPointRight = _probe.interpolateY(_probe.getBRPoint(currentPoint), _probe.getTRPoint(currentPoint), currentPoint);
    	writer.println("#102=" + intPointLeft.getZ());
    	writer.println("#101=" + intPointRight.getZ());
    	intPointLeft.setZ("#102");
    	intPointRight.setZ("#101");
    	SimplePoint3DCNC intPointHoriz = _probe.interpolateX(intPointLeft, intPointRight, currentPoint);
    	currentPoint.setZ(intPointHoriz.getZ());
    	writer.println("#100=" + currentPoint.getZ());    
    }
    
    private void writeMillFile(PrintWriter file, GCodeBreaker original) throws IOException
    {
        String current;
        file.println("(The original mill file is now rewritten with z depth replaced with a)");
        file.println("(bilinear interpolated value based on the initial probing)");
        file.println();
        
        while ((current = original.readNextLine()) != null)
        {
            if (original.getCurrentCoords().getZ() < 0)
            {
                writeBilinear(file, original.getCurrentCoords());
                String modifiedLine = current;
                
                if (original.doesContain(current, 'Z'))              
                    modifiedLine = current.replaceAll("Z" + original.getStringDoubleFromChar(current, 'Z'), 
                            "Z[#100 + " + original.getCurrentCoords().getZ() + "] ");                
                else if (original.doesContain(current, 'Y'))
                    modifiedLine = current.replaceAll("Y" + original.getStringDoubleFromChar(current, 'Y'), 
                            "Y" + original.getCurrentCoords().getY() + " Z[#100 + " + original.getCurrentCoords().getZ() + "] ");
                else if (original.doesContain(current, 'X'))
                    modifiedLine = current.replaceAll("X" + original.getStringDoubleFromChar(current, 'X'), 
                            "X" + original.getCurrentCoords().getX() + " Z[#100 + " + original.getCurrentCoords().getZ() + "] ");
                
                modifiedLine.trim();
                file.println(modifiedLine);
            }
            else
            {
                file.println(current);
            }
        }
    }
    
    private void writePreAmble(PrintWriter file, File rawFile)
    {
    	DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
    	DateFormat time = new SimpleDateFormat("HH:mm");
    	Date rawDate = new Date();
    	
    	file.println("(AutoLeveller, Version: " + Autoleveller.VERSION + ", http://autoleveller.co.uk)");
    	file.println("(Copyright 2013 James Hawthorne PhD)");
    	file.println("(Original file: " + rawFile.getName() + ")");
    	file.println("(Creation date: " + date.format(rawDate) + " time: " + time.format(rawDate) + ")");
    	file.println();
    	file.println("(This program and any of its output is licensed under GPLv2 and as such...)");
    	file.println("(AutoLeveller comes with ABSOLUTELY NO WARRANTY; for details, see sections 11 and 12 of the GPLv2)");
    }

}
