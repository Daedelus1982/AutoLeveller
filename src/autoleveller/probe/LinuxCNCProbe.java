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

class LinuxCNCProbe extends Probe
{
    public LinuxCNCProbe(String units, double xStart, double yStart, double millWidth, double millHeight, double probeFeed, double probeDepth, double probeSpacing, double finishHeight, double probeClearance) 
    {
        super(units, xStart, yStart, millWidth, millHeight, probeFeed, probeDepth, probeSpacing, finishHeight, probeClearance);
    }

    @Override
    protected String probeCommand(String depth, String feed) 
    {
        return "G38.2 Z" + depth + " F" + feed;
    }

    @Override
    protected String currentZ() 
    {
        return "#5422";
    }

    @Override
    protected String zeroZ() 
    {
        return "G10 L20 P0 Z0";
    }

    @Override
    public void writeSubs(PrintWriter writer) 
    {
    /*    writer.println("(This sub is a linear interpolation procedure)");
        writer.println("(parameters: #1 = p1, #2 = v1, #3 = p2, #4 = v2, #5 = p)");
        writer.println("(v returned to #100)");
        writer.println("o100 sub");
        writer.println("  #100 = [#2 + [[[#5 - #1] / [#3 - #1]] * [#4 - #2]]]");
        writer.println("o100 endsub");
        writer.println();
        writer.println("(This sub is for bilinear interpolation. It calls the linear method 3 times)");
        writer.println("(parameters: #1 = p1, #2 = v1, #3 = p2, #4 = v2, #5 = m1)");
        writer.println("(#6 = p3, #7 = v3, #8 = p4, #9 = v4, #10 = m2)");
        writer.println("(#11 = x1, #12 = x2, #13 = x)");
        writer.println("o101 sub");
        writer.println("  o100 call [#1] [#2] [#3] [#4] [#5]");
        writer.println("  #102 = #100");
        writer.println("  o100 call [#6] [#7] [#8] [#9] [#10]");
        writer.println("  #101 = #100");
        writer.println("  o100 call [#11] [#102] [#12] [#101] [#13]");
        writer.println("o101 endsub");*/
    }

	@Override
	protected void openLog(PrintWriter writer) 
	{
		//writer.println("(LOGOPEN, probeLog.log)");		
	}

	@Override
	protected void logProbePoint(PrintWriter writer, SimplePoint3DCNC point) 
	{
		//writer.println("(LOG, X is " + point.getX() + " Y is " + point.getY() + " Z is HASH" + 
		//		point.getZ().substring(1, point.getZ().length()) + " = " + point.getZ() + ")");	
		//writer.println("(LOG, HASH 5063 is #5063 HASH 5422 is #5422)");
	}

	@Override
	protected void closeLog(PrintWriter writer) 
	{
		//writer.println("(LOGCLOSE)");
	}
}