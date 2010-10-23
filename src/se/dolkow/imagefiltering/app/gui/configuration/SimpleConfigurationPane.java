package se.dolkow.imagefiltering.app.gui.configuration;

/*
	Copyright 2009 Snild Dolkow
	
	This file is part of Perler.
	
	Perler is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Perler is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Perler.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.gui.ImageDisplay;

public class SimpleConfigurationPane extends ConfigurationPane {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final JTabbedPane tabs;

	public SimpleConfigurationPane(ImageProducer toBeDisplayed) {
		this(toBeDisplayed, new ImageDisplay(toBeDisplayed, false));
	}

	public SimpleConfigurationPane(ImageProducer toBeDisplayed, JComponent display) {
		setContinuousLayout(true);
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.setPreferredSize(new Dimension(10,150));
		
		JScrollPane scroll = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scroll.setMinimumSize(new Dimension(10,50));
		tabs.setMinimumSize(new Dimension(10,50));
		
		setResizeWeight(1);
		
		setBottomComponent(tabs);
		setTopComponent(scroll);
		
	}

	public void addSettingsTab(String title, JComponent settings) {
		tabs.addTab(title, settings);
	}
	
}
