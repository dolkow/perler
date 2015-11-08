package se.dolkow.imagefiltering.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.dolkow.imagefiltering.app.gui.configuration.palette.Color;
import se.dolkow.imagefiltering.app.gui.configuration.palette.Swatch;
import se.dolkow.imagefiltering.gui.ColorInfoImageDisplay.ColorSelectionListener;


public class ColorInfoLabel extends JPanel implements ColorSelectionListener {
	private JLabel colorName;
	private Swatch swatch;

	public ColorInfoLabel() {
		Color c = new Color(java.awt.Color.GRAY.getRGB(), "None");
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(4, 6, 2, 4));
		JLabel info = new JLabel("Selected color: ");
		colorName = new JLabel(c.getName());
		swatch = new Swatch(c);
		
		add(info);
		add(Box.createHorizontalStrut(5));
		add(swatch);
		add(Box.createHorizontalStrut(5));
		add(colorName);
		add(Box.createHorizontalGlue());
	}
	
	public void setColor(Color c) {
		colorName.setText(c.getName());
		swatch.setColor(c.getRGB());
	}

	public void selectedColorChanged(Color color) {
		setColor(color);
	}
}
