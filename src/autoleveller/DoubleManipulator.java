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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleManipulator 
{
	private Pattern _floatPt = Pattern.compile("[0-9]*[\\.,]?[0-9]+([eE][-+]?[0-9]+)?");
	private DecimalFormat _df = new DecimalFormat("#.#####"); //remove .0 where possible
	private DecimalFormatSymbols _symbols = new DecimalFormatSymbols();
	
	public DoubleManipulator()
	{
		_symbols.setDecimalSeparator('.');
		_symbols.setGroupingSeparator(',');
		_df.setDecimalFormatSymbols(_symbols);
	}
	
	public boolean isExponent(String number)
	{
		String upperNumber = number.toUpperCase();
		if (isNumber(number))
			return (upperNumber.contains("E"));
		
		return false;
	}
	
	private boolean isNumber(String input)
	{
		Map<Integer, String> floats = getNumbersInString(input);
		String upperNumber = input.toUpperCase();
		
		//contains 1 number starting at index 0 and is not bigger than the input string
		if ((floats.get(0) != null) && (floats.get(0).equals(upperNumber)))
			return true;
		
		return false;	
	}
	
	public String formatNumber(String exponentDouble)
	{
		if (isNumber(exponentDouble))
		{
			double inputDouble = Double.parseDouble(exponentDouble);
			inputDouble = (double)Math.round(inputDouble * 10000) / 10000;
			return _df.format(inputDouble);
		}
		
		return exponentDouble;
	}
	
	public String formatOutput(String original)
	{
		String modified = original;
		Map<Integer, String> floats = getNumbersInString(original);
		
		for (Integer key : floats.keySet())
		{
			char prevChar = original.charAt(key.intValue() - 1);
			modified = modified.replaceAll(Pattern.quote(prevChar + floats.get(key.intValue())), prevChar + formatNumber(floats.get(key.intValue())));
		}
		
		modified = modified.trim();
		return modified;
	}
	
	public Map<Integer, String> getNumbersInString(String line)
	{
		Map <Integer, String> allDoubles = new HashMap<Integer, String>();

		String upperLine = GCodeReader.stripComments(line).toUpperCase();
        Matcher floatMatcher = _floatPt.matcher(upperLine);
        
        while (floatMatcher.find())
        	allDoubles.put(floatMatcher.start(), upperLine.substring(floatMatcher.start(), floatMatcher.end()));
        
		return allDoubles;
	}
}
