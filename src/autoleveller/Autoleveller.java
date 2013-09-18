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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Autoleveller
{
	public static final int MAJOR = 0;
	public static final double MINOR = 7.7;
	public static final String VERSION = MAJOR + "." + MINOR;
	
    public static void main(String[] args)
    {
        AutolevellerGUI autoleveller = new AutolevellerGUI();
        autoleveller.init();
    }
    
    public static int checkLatestMajorVersion(Element appElement) throws MalformedURLException 
	{
		String majorFromSite = appElement.getElementsByTagName("MAJOR").item(0).getTextContent();
		return Integer.parseInt(majorFromSite);			
	}
	
	public static double checkMinorVersion(Element appElement) throws MalformedURLException
	{
		String minorFromSite = appElement.getElementsByTagName("MINOR").item(0).getTextContent();
		return Double.parseDouble(minorFromSite);
	}
	
	public static Element getAppElement(String url, String appID) throws Exception
	{
		//URLConnection allows a timeout to be set
		URL webaddress = new URL(url);
		URLConnection con = webaddress.openConnection();
		con.setConnectTimeout(5000); //5 second timeout
		con.setReadTimeout(10000); //10 second timeout
		InputStream stream = con.getInputStream();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document xmlDoc = dBuilder.parse(stream);
		NodeList nodes = xmlDoc.getElementsByTagName("APP");
		for (int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				if (element.getAttribute("id").equals(appID))
					return element;
			}
		}
		throw new Exception();
	}
	
	public static boolean isLatest()
	{
		try
		{
			Element autoLElement = getAppElement("http://autoleveller.co.uk/Version.xml", "00");
			int currentMajorVersion = checkLatestMajorVersion(autoLElement);
			double currentMinorVersion = checkMinorVersion(autoLElement);
			if (currentMajorVersion > MAJOR)
				return false;
			if ((currentMajorVersion == MAJOR) && (currentMinorVersion > MINOR))
				return false;
			
			return true;
		}
		catch(Exception ex)
		{
			// if there is any problem return true
			return true;
		}
	}
}
