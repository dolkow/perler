package se.dolkow.imagefiltering.app.gui;

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.ImageProducerListener;
import se.dolkow.imagefiltering.gui.ColorInfoLabel;
import se.dolkow.imagefiltering.gui.ColorInfoImageDisplay;
import se.dolkow.imagefiltering.internationalization.Messages;

public class PerlerGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private boolean disposed = false;
	private final ImageProducer[] producers;
	
	private final Menu menu;
	protected final StatusBar sb;

	public PerlerGUI(ImageProducer[] producers, File loadedFrom, FileChooser fc) {
		super("Perler"); //$NON-NLS-1$
		
		if (fc == null) {
			fc = new FileChooser(new String[]{"plr", "xml"}); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		URL imgurl = AboutPanel.class.getResource("/resource/icon.png"); //$NON-NLS-1$
		if (imgurl != null) {
			Image logo =  Toolkit.getDefaultToolkit().createImage(imgurl);
			setIconImage(logo);
		} else {
			System.err.println("Couldn't find /resource/icon.png"); //$NON-NLS-1$
		}
		
		
		this.producers = producers;
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				boolean closed = close();
				if (closed) { 
					System.exit(0);
				}
			}

		});
		
		setLayout(new BorderLayout());
		
		menu = new Menu(producers, loadedFrom, fc);
		setJMenuBar(menu);
		
		ProducerTabPane pPane= new ProducerTabPane(producers);
		pPane.setMinimumSize(new Dimension(50,0));
		
		JPanel outputPanel = new JPanel(new BorderLayout());

		AbstractReduceColorsFilter reduceColorsFilter = getReduceColorsFilter(producers);
		ColorInfoImageDisplay lastOutput = new ColorInfoImageDisplay(
			producers[producers.length - 1], reduceColorsFilter, true);
		JScrollPane scroll = new JScrollPane(lastOutput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setMinimumSize(new Dimension(50,0));
		outputPanel.add(scroll, BorderLayout.CENTER);
		
		ColorInfoLabel colorInfoLabel = new ColorInfoLabel();
		lastOutput.setColorSelectionListener(colorInfoLabel);
		outputPanel.add(colorInfoLabel, BorderLayout.SOUTH);
		
		JSplitPane split = new JSplitPane();
		split.setLeftComponent(pPane);
		split.setRightComponent(outputPanel);
		split.setContinuousLayout(true);
		split.setResizeWeight(0.5);
		
		add(split, BorderLayout.CENTER);
		
		sb = new StatusBar();
		add(sb, BorderLayout.SOUTH);
		
		setPreferredSize(new Dimension(800,600));
		pack();
		producers[producers.length-1].addChangeListener(new ChangedSetter(menu));
		setVisible(true);
	}
	
	public synchronized boolean isDisposed() {
		return disposed;
	}
	
	public synchronized void waitForDispose() throws InterruptedException {
		while (!disposed) {
			wait();
		}
	}
	
	/**
	 * 
	 * @return true if the close request is accepted and the GUI is about to close.
	 */
	public boolean close() {
		if (menu.isFileChanged()) {
			String s = Messages.get("PearlsGUI.save_option"); //$NON-NLS-1$
			String d = Messages.get("PearlsGUI.discard_option"); //$NON-NLS-1$
			String c = Messages.get("PearlsGUI.cancel_option"); //$NON-NLS-1$
			int ans = JOptionPane.showOptionDialog(this, Messages.get("PearlsGUI.save_query"), Messages.get("PearlsGUI.save_query_window_title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{s,d,c}, s); //$NON-NLS-1$ //$NON-NLS-2$
			if (ans == JOptionPane.YES_OPTION) {
				boolean success = menu.save();
				if (success) { 
					dispose();
					return true;
				}
			} else if (ans == JOptionPane.NO_OPTION) {
				dispose();
				return true;
			}
		} else {
			dispose();
			return true;
		}
		return false;
	}
	
	public void dispose() {
		sb.cleanup();
		for (ImageProducer ip : producers) {
			ip.cleanup();
		}
		super.dispose();
		synchronized(this) {
			disposed = true;
			notifyAll();
		}
	}
	
	private AbstractReduceColorsFilter getReduceColorsFilter(ImageProducer[] producers) {
		for (ImageProducer producer : producers) {
			if (producer instanceof AbstractReduceColorsFilter) {
				return (AbstractReduceColorsFilter)producer;
			}
		}
		throw new RuntimeException("Reduce colors filter not found");
	}
	
	protected static class ChangedSetter implements ImageProducerListener {
		private final Menu m;
		
		public ChangedSetter(Menu m) {
			this.m = m;
		}
		
		public void changed(ImageProducer producer) {
			m.setFileChanged(true);
		}
	}
	
}
