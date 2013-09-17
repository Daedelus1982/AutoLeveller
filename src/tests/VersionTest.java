package tests;

import java.net.MalformedURLException;

import static org.junit.Assert.*;
import org.junit.Test;

import autoleveller.AutolevellerGUI;

public class VersionTest 
{
	
	@Test
	public void xmlNotExist()
	{
		try
		{
			int major = AutolevellerGUI.checkLatestMajorVersion("www.autoleveller.co.uk/nonexistenturl.xml", "00");
		}
		catch (MalformedURLException ex)
		{
			//should catch thins as the url does not exist
			assertTrue(true);
			return;
		}
		
		assertTrue(false);
	}

}
