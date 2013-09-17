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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;

public class AutolevellerGUI extends JFrame

{
	private static final long serialVersionUID = 1L;
	
	ProbeSettingsPanel _probeSettingsPanel = new ProbeSettingsPanel();

	private JFileChooser _fc = new JFileChooser();
    
    private JTextField _originalFileTxt = new JTextField(10);
    private File _originalFile = null;
    private File _outputFile = null;
    private JTextArea _fileInfo = new JTextArea(5, 10);
    private JTextArea _probeInfo = new JTextArea(5, 10);
    private JButton _inputBtn = new JButton("Browse");
    private JComboBox<String> _software = new JComboBox<String>(new String[]{"LinuxCNC", "Mach3"});
    private Surface _surface;
    
    public void init()
    {
        this.setTitle("Autoleveller");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        _probeSettingsPanel.init(_probeInfo);
        JPanel logPanel = new JPanel();
        logPanel.setBorder(BorderFactory.createTitledBorder("Autolevel"));
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.PAGE_AXIS));
        layoutLevelleddFilePanel(logPanel);
        this.add(logPanel, BorderLayout.CENTER);
        JPanel probePanel = new JPanel();
        probePanel.setBorder(BorderFactory.createTitledBorder("Probe Settings"));
        probePanel.setLayout(new BoxLayout(probePanel, BoxLayout.PAGE_AXIS));
        probePanel.add(_probeSettingsPanel);
        JButton clearBtn = new JButton("Clear");
        probePanel.add(clearBtn);
        clearBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_probeSettingsPanel.clear();	
			}
		});
        probePanel.add(Box.createVerticalStrut(10));
        this.add(probePanel, BorderLayout.WEST);
        this.pack();
        this.setVisible(true);
    }
    
    private JEditorPane getAboutContent()
    {
    	JEditorPane text = new JEditorPane("text/html", "<html><center><strong>AutoLeveller</strong> - <a href=\"mailto:daedelus1982@gmail.com\">Email Me</a><br>" +
			"Version: " + Autoleveller.VERSION + ", license - <a href=\"http://www.gnu.org/licenses/\">GPLv2</a><br>" +
			"Copyright (c) 2013 James Hawthorne PhD<br>" +
			"<a href=\"http://www.autoleveller.co.uk/\">www.autoleveller.co.uk</a></center></html>");
    	
    	text.setEditable(false);
    	//text.setBackground((new JLabel()).getBackground());
    	
    	text.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) 
			{
				if (hle.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
				{
					try 
					{
						Desktop.getDesktop().browse(hle.getURL().toURI());
					} 
					catch (Exception e){}
				}
			}
		});
    	
    	return text;
    }
    
    private void setFileInfoPanel(Map<String, Long> states, Rectangle2D area)
    {
    	_fileInfo.setText("Units: " + GCodeReader.getUnits(states) + System.getProperty("line.separator"));
    	_fileInfo.append("X: " + area.getX() + System.getProperty("line.separator"));
    	_fileInfo.append("Y: " + area.getY() + System.getProperty("line.separator"));
    	_fileInfo.append("X length: " + area.getWidth() + System.getProperty("line.separator"));
    	_fileInfo.append("Y length: " + area.getHeight() + System.getProperty("line.separator"));
    }
	
    private void layoutLevelleddFilePanel(JPanel levelPanel)
    {
        JPanel softChoice = new JPanel();
        softChoice.add(_software);
        JButton aboutBtn = new JButton("About");
        softChoice.add(aboutBtn);
        aboutBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JOptionPane.showMessageDialog(null, getAboutContent(), "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
        levelPanel.add(softChoice);
        JPanel filesPnl = new JPanel();
        filesPnl.setLayout(new BoxLayout(filesPnl, BoxLayout.PAGE_AXIS));
        JPanel inputPnl = new JPanel();
        inputPnl.add(new JLabel("Original GCode File"));
        inputPnl.add(_originalFileTxt);
        _originalFileTxt.setEditable(false);
        inputPnl.add(_inputBtn);
        _inputBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (_fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    _originalFileTxt.setText(_fc.getSelectedFile().getAbsolutePath());
                    _originalFile = _fc.getSelectedFile();
                    try {
						GCodeReader originalFile = new GCodeReader(_originalFile);
						
						while(originalFile.readNextLine() != null); // read every line
					
						_probeSettingsPanel.resetVariables(originalFile.getState(), originalFile.getArea());
						setFileInfoPanel(originalFile.getState(), originalFile.getArea());
						_probeSettingsPanel.setProbeInfoPanel(Probe.createProbePoints(_probeSettingsPanel.getXstart(), 
								_probeSettingsPanel.getYstart(), _probeSettingsPanel.getMillWidth(), 
								_probeSettingsPanel.getMillHeight(), _probeSettingsPanel.getSpacing()));
						originalFile.close();
					} 
                    catch (IOException e) 
                    {							
						_originalFileTxt.setText("");
						_fileInfo.setText("");

					}
                }
            }
        });
        filesPnl.add(inputPnl);
        levelPanel.add(filesPnl);
        levelPanel.add(Box.createVerticalGlue());
        JPanel fileInfoPanel = new JPanel();
        fileInfoPanel.setBorder(BorderFactory.createTitledBorder("GCode file Information"));
        fileInfoPanel.setLayout(new BoxLayout(fileInfoPanel, BoxLayout.PAGE_AXIS));
        fileInfoPanel.add(_fileInfo);
        _fileInfo.setEditable(false);
        _fileInfo.setBackground(Color.white);
        levelPanel.add(fileInfoPanel);
        fileInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelPanel.add(Box.createVerticalGlue());
        JPanel probeInfoPanel = new JPanel();
        probeInfoPanel.setBorder(BorderFactory.createTitledBorder("Probe Information"));
        probeInfoPanel.setLayout(new BoxLayout(probeInfoPanel, BoxLayout.PAGE_AXIS));
        probeInfoPanel.add(_probeInfo);
        _probeInfo.setEditable(false);
        _probeInfo.setBackground(Color.white);
        levelPanel.add(probeInfoPanel);
        probeInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelPanel.add(Box.createVerticalGlue());
        JButton createGCodeBtn = new JButton("Create Levelled GCode");
        levelPanel.add(createGCodeBtn);
        createGCodeBtn.setAlignmentX(Component.CENTER_ALIGNMENT); 
        createGCodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (verifyInput())
                {
                	if ((_outputFile = getValidOutputFile()) != null)
                	{
	                	
	                    Probe probe = Probe.createProbe(String.valueOf(_software.getSelectedItem()), _probeSettingsPanel.getUnits(), 
	                            _probeSettingsPanel.getXstart(), _probeSettingsPanel.getYstart(), _probeSettingsPanel.getMillWidth(), 
	                            _probeSettingsPanel.getMillHeight(), _probeSettingsPanel.getProbeFeed(), _probeSettingsPanel.getProbeDepth(),
	                            _probeSettingsPanel.getSpacing(), _probeSettingsPanel.getFinishHeight(), _probeSettingsPanel.getProbeClearance());
	                    try {
							_surface = new Surface(probe, _outputFile, _originalFile);
							if (!_surface.probeAreaGEJobArea())
							{
								if (JOptionPane.showConfirmDialog(null, "<html>The milling area is not contained by the probing area<br>" +
														"This is OK but, only the probed area can be levelled<br>" +
														"Are you sure you want to continue?</html>", 
										"Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
									return;
							}
		                    _surface.writeLeveledFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
                	}	
                }
            }
        });
    }
    
    private File getValidOutputFile()
    {
    	String extension = "";
    	if (String.valueOf(_software.getSelectedItem()).equalsIgnoreCase("LinuxCNC"))
    		extension = ".ngc";
    	else if (String.valueOf(_software.getSelectedItem()).equalsIgnoreCase("Mach3"))
    		extension = ".tap";
    	String outputName = "AL" + _originalFile.getName();
    	
    	if (outputName.lastIndexOf('.') > 0) //test if the file has an extension, ignore hidden files i.e. .hidden
    	{
    		outputName = outputName.substring(0, outputName.lastIndexOf('.')); //remove extension
    		outputName = outputName + extension; //add appropriate extension
    	}
    	String fullPath = _originalFile.getAbsolutePath();
    	_fc.setSelectedFile(new File(fullPath.substring(0, (fullPath.lastIndexOf(File.separator) + 1)) + outputName));
    	if (_fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
            return _fc.getSelectedFile();
    	
    	return null;
    }
    
    private boolean verifyInput()
    {
    	if (_originalFileTxt.getText().equals(""))
    	{
    		JOptionPane.showMessageDialog(null, "No GCode file selected. \nPlease make a choice and try again");
    		return false;
    	}
    	
    	try
    	{
    		_probeSettingsPanel.verify();
    	}
    	catch (Exception e)
    	{
    		JOptionPane.showMessageDialog(null, "One or more of the probe settings is incorrect. \nPlease change and try again");
    		return false;
    	}
    	
    	return true;
    }

	public static int checkLatestMajorVersion(String url, String appID) throws MalformedURLException 
	{
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			dBuilder.parse(url);
		}
		catch (Exception e)
		{
			throw new MalformedURLException();
		}
		
		return 0;
	}
}
