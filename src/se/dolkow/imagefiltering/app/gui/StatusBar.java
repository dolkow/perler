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
package se.dolkow.imagefiltering.app.gui;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import se.dolkow.imagefiltering.internationalization.Messages;

/**
 * @author snild
 *
 */
public class StatusBar extends JPanel {

	private static final long serialVersionUID = 1L;
	protected final JLabel mem;
	protected final UpdateThread ut;
	
	public StatusBar() {
		LayoutManager layout = new BorderLayout();
		setLayout(layout);
		
		JPanel filler = new JPanel();
		filler.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		add(filler, BorderLayout.CENTER);
		
		Box container = new Box(BoxLayout.X_AXIS);
		
		mem = new JLabel("placeholder"); //$NON-NLS-1$
		container.add(mem);
		mem.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		add(container, BorderLayout.EAST);
		
		ut = new UpdateThread();
		ut.start();
	}
	
	public void cleanup() {
		ut.interrupt();
	}
	
	private class UpdateThread extends Thread {
		public UpdateThread() {
			super("UpdateThread:StatusBar"); //$NON-NLS-1$
			setDaemon(true);
		}
		
		public void run() {
			try {
				while(true) {
					long currentb = Runtime.getRuntime().totalMemory(); 
					long used = (currentb - Runtime.getRuntime().freeMemory()) / 1048576;
					long current = currentb / 1048576;
					long max = Runtime.getRuntime().maxMemory() / 1058576;
					mem.setText(Messages.get("StatusBar.memory_usage") + used + "/" + current + "/" + max + " MB");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
			}
		}
	}
	
}
