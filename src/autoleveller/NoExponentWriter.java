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

import java.io.PrintWriter;
import java.io.Writer;

public class NoExponentWriter extends PrintWriter 
{	
	DoubleManipulator _dm = new DoubleManipulator();
	
	public NoExponentWriter(Writer out) {
		super(out);
	}

	@Override
	public void println(String original)
	{
		String exponentsRemoved = _dm.formatOutput(original);
		super.println(exponentsRemoved);
	}
	
}
