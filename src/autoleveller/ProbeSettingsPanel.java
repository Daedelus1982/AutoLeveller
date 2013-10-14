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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import autoleveller.probe.Probe;

public class ProbeSettingsPanel extends JPanel implements FocusListener 
{
	private static final long serialVersionUID = 1L;
	
	private JTextField _units = new JTextField(8);
    private JTextField _xstart = new JTextField(5);
    private JTextField _ystart = new JTextField(5);
    private JTextField _millWidth = new JTextField(5);
    private JTextField _millHeight = new JTextField(5);
    private JTextField _probeFeed = new JTextField(5);
    private JTextField _probeDepth = new JTextField(5);
    private JTextField _probeClearance = new JTextField(5);
    private JTextField _dPSpacing = new JTextField(5);
    private JTextField _finishHeight = new JTextField(5);
    private JTextArea _probeInfo = new JTextArea();
    
    public String getUnits()
    {
    	return _units.getText();
    }
    
    public Double getXstart() {
		return Double.parseDouble(_xstart.getText());
	}
    
    public Double getYstart() {
		return Double.parseDouble(_ystart.getText());
	}
    
    public Double getMillWidth() {
		return Double.parseDouble(_millWidth.getText());
	}
    
    public Double getMillHeight() {
		return Double.parseDouble(_millHeight.getText());
	}
    
    public Double getProbeFeed() {
		return Double.parseDouble(_probeFeed.getText());
	}
    
    public Double getProbeDepth() {
		return Double.parseDouble(_probeDepth.getText());
	}
    
    public Double getProbeClearance() {
		return Double.parseDouble(_probeClearance.getText());
	}
    
    public Double getSpacing() {
		return Double.parseDouble(_dPSpacing.getText());
	}
    
    public Double getFinishHeight() {
		return Double.parseDouble(_finishHeight.getText());
	}

	public void init(JTextArea probeInfo)
    {
		_probeInfo = probeInfo;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        layoutPanel();
    }
    
    private void layoutPanel()
    {
    	JLabel message = new JLabel("<html>Empty probe settings will automatically be filled when<br>" +
    								"the orginal GCode file is selcted.</html>");
    	this.add(Box.createVerticalStrut(5));
    	this.add(message);
    	message.setAlignmentX(Component.CENTER_ALIGNMENT);
    	this.add(Box.createVerticalStrut(15));
    	JPanel unitsPanel = new JPanel();
    	unitsPanel.add(new JLabel("Units:"));
    	unitsPanel.add(_units);
    	_units.setEditable(false);
    	_units.setBackground(Color.white);
    	this.add(unitsPanel);
    	this.add(Box.createVerticalStrut(5));
        JPanel variablePnl = new JPanel();
        variablePnl.setLayout(new GridLayout(10, 2));
        JPanel xStartPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("X", SwingConstants.RIGHT));
        _xstart.setToolTipText("<html>The X origin of the job.<br>Leave blank to automatically set this value <br>when an input file is loaded.</html>");
        xStartPnl.add(_xstart);
        _xstart.addFocusListener(this);
        variablePnl.add(xStartPnl);
        JPanel yStartPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("Y", SwingConstants.RIGHT));
        yStartPnl.add(_ystart);
        _ystart.addFocusListener(this);
        _ystart.setToolTipText("<html>The Y origin of the job.<br>Leave blank to automatically set this value <br>when an input file is loaded.</html>");
        variablePnl.add(yStartPnl);
        JPanel widthPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("X Length", SwingConstants.RIGHT));
        _millWidth.setToolTipText("<html>The X extremities of the job.<br>Leave blank to automatically set this value <br>when an input file is loaded.</html>");
        widthPnl.add(_millWidth);
        _millWidth.addFocusListener(this);
        variablePnl.add(widthPnl);
        JPanel heightPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("Y Length", SwingConstants.RIGHT));
        _millHeight.setToolTipText("<html>The Y extremities of the job.<br>Leave blank to automatically set this value <br>when an input file is loaded.</html>");
        heightPnl.add(_millHeight);
        _millHeight.addFocusListener(this);
        variablePnl.add(heightPnl);
        JPanel feedPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("Z Feed", SwingConstants.RIGHT));
        _probeFeed.setToolTipText("<html>The Z feed rate to use whilst probing.<br>Units per minute.</html>");
        feedPnl.add(_probeFeed);
        variablePnl.add(feedPnl);
        JPanel probeDepth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("Probe Depth", SwingConstants.RIGHT));
        _probeDepth.setToolTipText("<html>The maximum Z value to probe to.<br>Example: Current Z height is 2 and Probe Depth is -1.<br>The tool will move down 3 or stop if probe-in is triggered.</html>");
        probeDepth.add(_probeDepth);
        variablePnl.add(probeDepth);
        JPanel probeHeight = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("Probe Clearance", SwingConstants.RIGHT));
        _probeClearance.setToolTipText("<html>The Z height to use whilst moving between probe points.</html>");
        probeHeight.add(_probeClearance);
        variablePnl.add(probeHeight);
        JPanel spacing = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("Probe Spacing", SwingConstants.RIGHT));
        _dPSpacing.setToolTipText("<html>The desired space between probe points.<br>The actual space will attempt to be as close to this value<br>as possible whilst maintaining an equal spacing across the row.</html>");
        spacing.add(_dPSpacing);
        _dPSpacing.addFocusListener(this);
        variablePnl.add(spacing);
        JPanel finishHeight = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variablePnl.add(new JLabel("Z Safe Height", SwingConstants.RIGHT));
        finishHeight.add(_finishHeight);
        variablePnl.add(finishHeight);
        this.add(variablePnl);
    }
   
    public void setProbeInfoPanel(SimplePoint3DCNC[][] points)
    {
    	_probeInfo.setText("Points per row: " + points[0].length + System.getProperty("line.separator"));
    	_probeInfo.append("Rows: " + points.length + System.getProperty("line.separator"));
    	_probeInfo.append("Total points: " + (points[0].length * points.length) + System.getProperty("line.separator"));
    }
    
    public void resetVariables(Map<String, Long> settings, Rectangle2D millArea) 
    {
    	if (GCodeReader.getUnits(settings).equals(""))
    	{
    		JPanel unitSelector = new JPanel(new GridLayout(3, 1));
    		ButtonGroup unitRadios = new ButtonGroup();
    		JRadioButton inchButton = new JRadioButton("inches");
    		JRadioButton mmButton = new JRadioButton("millimeters");
    		
    		unitRadios.add(inchButton);
    		unitRadios.add(mmButton);
    		unitSelector.add(new JLabel("<html>The selected file does not state the units to use (G20/G21).<br>Please select either inches or millimeters and press OK</html>"));
    		unitSelector.add(inchButton);
    		unitSelector.add(mmButton);
    		inchButton.setSelected(true);
    		
    		JOptionPane.showMessageDialog(null,  unitSelector);
    		
    		if (inchButton.isSelected())
    			settings.put("G20", 1L);
    		else
    			settings.put("G21", 1L);
    	}
    	
    	_units.setText(GCodeReader.getUnits(settings));
    	_xstart.setText(String.valueOf(millArea.getX()));
    	_ystart.setText(String.valueOf(millArea.getY()));
    	_millWidth.setText(String.valueOf(millArea.getWidth()));
    	_millHeight.setText(String.valueOf(millArea.getHeight()));
    	
        if (_units.getText().equalsIgnoreCase("millimeters"))
        {
        	_units.setText("millimeters");
            setFieldIfBlank(_probeFeed, "100");
            setFieldIfBlank(_probeDepth, "-1");
            setFieldIfBlank(_probeClearance, "2");
            setFieldIfBlank(_dPSpacing, "10");
            setFieldIfBlank(_finishHeight, "20");
        }
        else if (_units.getText().equalsIgnoreCase("inches"))
        {
        	_units.setText("inches");
        	setFieldIfBlank(_probeFeed, "5");
        	setFieldIfBlank(_probeDepth, "-0.0625");
        	setFieldIfBlank(_probeClearance, "0.125");
        	setFieldIfBlank(_dPSpacing, "0.375");
        	setFieldIfBlank(_finishHeight, "1");
        }
    }
    
    private void setFieldIfBlank(JTextField box, String value)
    {
    	if (box.getText().equals(""))
    		box.setText(value);
    }
    
    public void verify()
    {
    	try
    	{
    		getXstart();
    		getYstart();
    		getMillWidth();
    		getMillHeight();
    		getProbeFeed();
    		getProbeDepth();
    		getProbeClearance();
    		getSpacing();
    		getFinishHeight();
    	}
    	catch (Exception e)
    	{
    		throw e;
    	}
    }

	@Override
	public void focusGained(FocusEvent arg0) {
		// do nothing
		
	}

	@Override
	public void focusLost(FocusEvent arg0) 
	{
		setProbeInfoPanel(Probe.createProbePoints(getXstart(), getYstart(), getMillWidth(), getMillHeight(), getSpacing()));
	}
	
	public void clear()
	{
		_xstart.setText("");
		_ystart.setText("");
		_millWidth.setText("");
		_millHeight.setText("");
		_probeFeed.setText("");
		_probeDepth.setText("");
		_probeClearance.setText("");
		_dPSpacing.setText("");
		_finishHeight.setText("");
	}
}
