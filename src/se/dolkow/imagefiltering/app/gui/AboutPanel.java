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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import se.dolkow.imagefiltering.internationalization.Messages;

/**
 * @author snild
 *
 */
public class AboutPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public AboutPanel() {
		super();
		setLayout(new BorderLayout());
		
		URL imgurl = AboutPanel.class.getResource("/resource/perler.png"); //$NON-NLS-1$
		if (imgurl == null) {
			JOptionPane.showMessageDialog(null, Messages.get("AboutPanel.load_error") + " perler.png", Messages.get("General.error"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		Image logo =  Toolkit.getDefaultToolkit().createImage(imgurl);
		Box northbox = new Box(BoxLayout.Y_AXIS);
		northbox.add(new JLabel(new ImageIcon(logo)));
		northbox.add(new JLabel("http://dolkow.se/perler")); //$NON-NLS-1$
		northbox.add(new JLabel(Messages.get("AboutPanel.author"))); //$NON-NLS-1$
		northbox.add(new JLabel(Messages.get("AboutPanel.translator"))); //$NON-NLS-1$
		northbox.add(Box.createVerticalStrut(10));
		add(northbox, BorderLayout.NORTH);
		
		JTextArea text = new JTextArea();
		
		String gpl = "Copyright 2009/2010 Snild Dolkow\n\nPerler is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.\n\nPerler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.\n\nYou should have received a copy of the GNU General Public License along with Perler.  If not, see <http://www.gnu.org/licenses/>." //$NON-NLS-1$
			+ "\n\n\n" + Messages.get("AboutPanel.gpl_statement"); //$NON-NLS-1$ //$NON-NLS-2$
		text.setText(gpl);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setOpaque(false);
		JScrollPane textscroll = new JScrollPane(text);
		textscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(textscroll, BorderLayout.CENTER);
		text.setCaretPosition(0);

		try {
			JLabel version = new JLabel("v. " + Version.getCurrent().toString()); //$NON-NLS-1$
			add(version, BorderLayout.SOUTH);
		} catch (IOException e) {
			// whatever
			e.printStackTrace();
		}
		
		setPreferredSize(new Dimension(400, 500));
	}
}
