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

import java.io.PrintWriter;

import autoleveller.SimplePoint3DCNC;

class Mach3Probe extends Probe
{
    public Mach3Probe(String units, double xStart, double yStart, double millWidth, double millHeight, double probeFeed, double probeDepth, double probeSpacing, double finishHeight, double probeClearance) 
    {
        super(units, xStart, yStart, millWidth, millHeight, probeFeed, probeDepth, probeSpacing, finishHeight, probeClearance);
    }

    @Override
    protected String probeCommand(String depth, String feed) 
    {
        return "G31 Z" + depth + " F" + feed;
    }

    @Override
    protected String currentZ() 
    {
        return "#2002";
    }

    @Override
    protected String zeroZ() 
    {
        return "G92 Z0";
    }

    @Override
    public void writeSubs(PrintWriter writer) 
    {
        // Mach3 version does not usee sub-procedures so do nothing here
    }

	@Override
	protected void openLog(PrintWriter writer) 
	{
		// do nothing in mach3		
	}

	@Override
	protected void logProbePoint(PrintWriter writer, SimplePoint3DCNC point) 
	{
		// do nothing in mach3		
	}

	@Override
	protected void closeLog(PrintWriter writer) 
	{
		// do nothing in mach3	
	}
    
}