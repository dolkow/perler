package se.dolkow.imagefiltering.app;

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

import java.io.File;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import se.dolkow.imagefiltering.BrightnessContrastFilter;
import se.dolkow.imagefiltering.ColorBalance;
import se.dolkow.imagefiltering.CroppingFilter;
import se.dolkow.imagefiltering.DitherReduceColorsFilter;
import se.dolkow.imagefiltering.Flipper;
import se.dolkow.imagefiltering.Grayscale;
import se.dolkow.imagefiltering.ImageException;
import se.dolkow.imagefiltering.ImageInput;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.RotateFilter;
import se.dolkow.imagefiltering.ShrinkFilter;
import se.dolkow.imagefiltering.EdgeFilter;
import se.dolkow.imagefiltering.ThreadedCacher;
import se.dolkow.imagefiltering.TreeParseException;
import se.dolkow.imagefiltering.Upscaler;
import se.dolkow.imagefiltering.app.gui.FileChooser;
import se.dolkow.imagefiltering.app.gui.PerlerGUI;
import se.dolkow.imagefiltering.app.gui.VersionCheck;
import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Node;
import se.dolkow.imagefiltering.xml.XMLParser;
import se.dolkow.imagefiltering.xml.XMLParserException;

public class PerlerApp {
	
	private static PerlerGUI pgui = null;
	private static FileChooser fc = null;
	public static boolean jnlp = false;
	
	public static void main(String[] args) {
    	try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	} 
    	catch (Exception e) {
    		System.out.println("Failed to set system Look-and-feel, using default."); //$NON-NLS-1$
    		System.err.println("Failed to set system Look-and-feel, using default."); //$NON-NLS-1$
    	}
    	
    	if (args.length > 0 && args[0].equals("jnlp")) { //$NON-NLS-1$
    		jnlp = true;
    	}
    	
    	boolean loaded = false;
    	if (!jnlp) {
	    	Thread updateThread = new Thread() {
	    		public void run() {
	    			VersionCheck.autoCheck();
	    		}
	    	};
	    	updateThread.start();
	    	
	    	if (args.length > 0) {
	    		File f = new File(args[0]);
    	    	loaded = load(f);
	    	}
    	}
    	
    	if (!loaded) {
    		startWithNew(null);
    	}
	}
	
	private synchronized static void init() {
		if (fc == null) {
			fc = new FileChooser(new String[]{"plr", "xml"}); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	protected synchronized static boolean closeGUI() {
		if (pgui != null) {
			boolean closing = pgui.close();
			if (closing) {
				try {
					pgui.waitForDispose();
					pgui = null;
				} catch (InterruptedException e) {
					System.out.println("Interrupted while waiting for GUI to be disposed."); //$NON-NLS-1$
				}
			}
			return closing;
		}
		return true;
	}
	
	public synchronized static void startWithNew(String imgpath) {
		startWithNew(false, imgpath);
	}
	
	public synchronized static void startWithNew(boolean customize, String imgpath) {
		if (!closeGUI()) {
			return;
		}
		
		init();
		
		LinkedList<ImageProducer> p = new LinkedList<ImageProducer>();
		try {
			Grayscale gs;
			EdgeFilter ef;
			ImageInput ii = new ImageInput(imgpath);
			p.add(ii);
			p.add(new ThreadedCacher(p.removeLast()));
			p.add(new ShrinkFilter(p.getLast(), 400, 500));
			p.add(new ThreadedCacher(p.removeLast()));
			p.add(new Flipper(p.getLast()));
			p.add(new RotateFilter(p.getLast()));
			p.add(new CroppingFilter(p.getLast()));
			p.add(ef = new EdgeFilter(p.getLast()));
			p.add(new ThreadedCacher(p.removeLast()));
			p.add(new ShrinkFilter(p.getLast(), 58, 58));		
			p.add(gs = new Grayscale(p.getLast()));
			p.add(new ColorBalance(p.getLast()));
			p.add(new BrightnessContrastFilter(p.getLast()));
			p.add(new DitherReduceColorsFilter(0.8f,p.getLast()));
			p.add(new Upscaler(p.getLast()));
			p.add(new ThreadedCacher(p.removeLast()));
			
			gs.setActive(false);
			ef.setActive(false);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, Messages.get("PerlerApp.setup_error") + ": \n" + e.getLocalizedMessage(), Messages.get("General.error"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			e.printStackTrace();
		}
		
		System.out.println("Creating GUI..."); //$NON-NLS-1$
		pgui = new PerlerGUI(p.toArray(new ImageProducer[p.size()]), null, fc);
	}
	
	
	public static void startByLoading() {
		startByLoading(false);
	}
	
	public static void startByLoading(boolean customize) {
		if (!closeGUI()) {
			return;
		}
		
		init();
		
		LinkedList<ImageProducer> producers = new LinkedList<ImageProducer>();
		
		producers.add(new ImageInput());
		
		int ans = fc.showOpenDialog(null);
		while (ans == JFileChooser.APPROVE_OPTION) {
			
			File f = fc.getSelectedFile();
			if (load(f)) {
				return;
			}
			ans = fc.showOpenDialog(null);
		}
		startWithNew(customize, null);
	}
	
	private static boolean load(File f) {
		if (f == null) {
			return false;
		}
		if (!f.exists() || !f.canRead()) {
			String msg;
			if (!f.exists()) {
				System.err.println(f.getPath());
				msg = Messages.getFormatted("PerlerApp.filenotfound", f.getPath()); //$NON-NLS-1$
			} else {
				msg = Messages.getFormatted("PerlerApp.cantreadfile", f.getAbsolutePath()); //$NON-NLS-1$
			}
			String title = Messages.get("General.error"); //$NON-NLS-1$
			JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		XMLParser parser;
		try {
			parser = XMLParser.getInstance();
		} catch (XMLParserException e1) {
			JOptionPane.showMessageDialog(null, "Can't initialize XMLParser" + ": \n" + e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			return false;
		}
		try {
			LinkedList<ImageProducer> p = null;
			Node chain = parser.parse(f, "chain"); //$NON-NLS-1$
			System.out.println("Creating producer/filter list..."); //$NON-NLS-1$
			p = createProducerList(chain);
			if (p.size() > 1) {
				System.out.println("Creating GUI..."); //$NON-NLS-1$
				pgui = new PerlerGUI(p.toArray(new ImageProducer[p.size()]), f, fc);
				return true;
			}
			JOptionPane.showMessageDialog(null, Messages.getFormatted("PerlerApp.no_filters_error", f.getName()), Messages.get("General.error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			if (p!=null) {
				for (ImageProducer ip : p) {
					ip.cleanup();
				}
				p.clear();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, Messages.get("PerlerApp.load_error") + ": \n" + e.getLocalizedMessage(), Messages.get("General.error"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}
		return false;
	}
	
	private static LinkedList<ImageProducer> createProducerList(se.dolkow.imagefiltering.tree.Node chain) throws InterruptedException, ImageException, TreeParseException, LoadException {
		LinkedList<ImageProducer> res = new LinkedList<ImageProducer>();
		
		for (se.dolkow.imagefiltering.tree.Node producer : chain) {
			String name = producer.getName();
			boolean found = true;
			
			if (name.equals("loader")) res.add(new ImageInput()); //$NON-NLS-1$
			else if (name.equals("grayscale")) res.add(new Grayscale(res.getLast())); //$NON-NLS-1$
			else if (name.equals("balance")) res.add(new ColorBalance(res.getLast())); //$NON-NLS-1$
			else if (name.equals("flipper")) res.add(new Flipper(res.getLast())); //$NON-NLS-1$
			else if (name.equals("rotate")) res.add(new RotateFilter(res.getLast())); //$NON-NLS-1$
			else if (name.equals("upscaler")) res.add(new Upscaler(res.getLast())); //$NON-NLS-1$
			else if (name.equals("shrinker")) res.add(new ShrinkFilter(res.getLast())); //$NON-NLS-1$
			else if (name.equals("cropper")) res.add(new CroppingFilter(res.getLast())); //$NON-NLS-1$
			else if (name.equals("brightnesscontrast")) res.add(new BrightnessContrastFilter(res.getLast())); //$NON-NLS-1$
			else if (name.equals("reduce")) res.add(new DitherReduceColorsFilter(res.getLast())); //$NON-NLS-1$
			else if (name.equals("cacher")) res.add(new ThreadedCacher(res.removeLast())); //$NON-NLS-1$
			else if (name.equals("edge")) res.add(new EdgeFilter(res.getLast())); //$NON-NLS-1$
			else found = false;
			
			if (found) {
				res.getLast().loadFromTree(producer);
			} else {
				throw new LoadException("Unhandled filter type " + name); //$NON-NLS-1$
			}
			
			
		}
		
		return res;
	}
	
}
