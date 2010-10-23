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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.ImageProducerListener;
import se.dolkow.imagefiltering.MutableInteger;
import se.dolkow.imagefiltering.internationalization.Messages;

public class ColorUsagePanel extends PalettePanel {
	
	private static final Sorter sorter = new Sorter();
	private static final long serialVersionUID = 1L;
	
	public ColorUsagePanel(AbstractReduceColorsFilter f) {
		super(f);
		
		f.addChangeListener(new ImageProducerListener() {
			public void changed(ImageProducer producer) {
				repaint();
			}
		});
		
		JButton show = new JButton(Messages.get("ColorUsagePanel.show_usage_button_text")); //$NON-NLS-1$
		
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComponent c;
				if (filter.isActive()) {
					c = getColorUsageComponent();
				} else {
					c = new JLabel(Messages.get("ColorUsagePanel.inactive")); //$NON-NLS-1$
				}
				JOptionPane.showMessageDialog(null, c, Messages.get("ColorUsagePanel.usage_window_title"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			}
		});
		add(show);
	}
	
	private JComponent getColorUsageComponent() {
		String[][] usage = calculateSortedColorUsage();
		JTable tbl = new JTable(new ColorUsageTableModel(usage));
		tbl.setFont(new Font("Monospaced",Font.PLAIN,tbl.getFont().getSize())); //$NON-NLS-1$
		JPanel p = new JPanel();
		p.setMaximumSize(new Dimension(600, 600));
		JScrollPane scroll = new JScrollPane(tbl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		p.add(scroll);
		return p;
	}
	
	private String[][] calculateSortedColorUsage() {
		Map<Integer, String> colors = filter.getColors();
		int total = 0;
		final String[][] result;
		synchronized(colors) {
			Map<Integer, MutableInteger> usage = filter.getColorUsage();
			
			result = new String[usage.size()+3][2];
			
			TreeSet<Map.Entry<Integer,MutableInteger>> sorted = new TreeSet<Map.Entry<Integer,MutableInteger>>(sorter);
			for (Entry<Integer, MutableInteger> e : usage.entrySet()) {
				sorted.add(e);
			}
			
			/*System.out.println("Num usage: " + usage.size());
			System.out.println("Num sorted: " + sorted.size());*/
			
			usage = null;
			int i = 0;
			for (Entry<Integer, MutableInteger> e : sorted) {
				result[i][0] = colors.get(e.getKey());
				result[i][1] = Messages.getFormatted("ColorUsagePanel.number_used", e.getValue().val); //$NON-NLS-1$
				total += e.getValue().val;
				i++;
			}
			int numColors = i;
			i++;
			result[i][0] = Messages.get("ColorUsagePanel.total"); //$NON-NLS-1$
			result[i][1] = Messages.getFormatted("ColorUsagePanel.bead_count", total); //$NON-NLS-1$
			i++;
			result[i][0] = ""; //$NON-NLS-1$
			result[i][1] = Messages.getFormatted("ColorUsagePanel.color_count", numColors); //$NON-NLS-1$
			sorted = null;
		}
		colors = null;
		return result;
	}
	
	private static class Sorter implements Comparator<Map.Entry<Integer, MutableInteger>> {
		public int compare(Entry<Integer, MutableInteger> o1, Entry<Integer, MutableInteger> o2) {
			int try1 = -new Integer(o1.getValue().val).compareTo(new Integer(o2.getValue().val));
			if (try1 == 0) {
				return o1.getKey().compareTo(o2.getKey());
			}
			return try1;
		}
	}
	
	private static class ColorUsageTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private String[][] data;
		
		public ColorUsageTableModel(String[][] data) {
			this.data = data;
		}
		
		public int getColumnCount() {
			return 2;
		}
		
		public String getColumnName(int col) {
			if (col == 0) {
				return Messages.get("ColorUsagePanel.color_column_title"); //$NON-NLS-1$
			} else {
				return ""; //$NON-NLS-1$
			}
		}

		public int getRowCount() {
			return data.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}

		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final int h = getHeight();
		final int w = getWidth();
		Color whiteTransp = new Color(255, 255, 255, 192);
		g.setColor(whiteTransp);
		g.fillRect(0, 0, w, h);
		
		if (filter.isActive()) {
			Map<Integer, String> colors = filter.getColors();
			int max = 1;
			synchronized(colors) {
				Map<Integer, MutableInteger> usage = filter.getColorUsage();
				
				for (MutableInteger mi : usage.values()) {
					if (mi.val > max) {
						max = mi.val;
					}
				}
				final float maxf = max;
				
				//Grid
				final float gridspacing = 100 * h / maxf;
				
				for (int i=0; i<=max/100; i++) {
					final int y = (int)( h - gridspacing * i );
					g.setColor(Color.LIGHT_GRAY);
					g.drawLine(0,y,w,y);
				}
				
				final float cw = getWidth() / (float)colors.size();
				int oldx = 0;
				int i = 1;
				for (Entry<Integer, String> color : colors.entrySet()) {
					final int rgb = color.getKey();
					int uses = 0;
					if (usage.containsKey(rgb)) {
						uses = usage.get(rgb).val;
					}
					final int ch = (int)( h * uses / maxf );
					final int x = (int)(cw * i);
					if (ch > 0) {
						g.setColor(new Color(rgb));
						g.fillRect(oldx, h-ch, x-oldx, h);
						g.setColor(Color.BLACK);
						g.drawRect(oldx, h-ch, x-oldx, h);
					}
					oldx = x;
					i++;
				}
			}
		}
	}
	
}
