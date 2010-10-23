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

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import se.dolkow.imagefiltering.AbstractImageFilter;
import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.BrightnessContrastFilter;
import se.dolkow.imagefiltering.Cacher;
import se.dolkow.imagefiltering.ColorBalance;
import se.dolkow.imagefiltering.CroppingFilter;
import se.dolkow.imagefiltering.DitherReduceColorsFilter;
import se.dolkow.imagefiltering.Flipper;
import se.dolkow.imagefiltering.ImageInput;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.RotateFilter;
import se.dolkow.imagefiltering.ShrinkFilter;
import se.dolkow.imagefiltering.EdgeFilter;
import se.dolkow.imagefiltering.Upscaler;
import se.dolkow.imagefiltering.internationalization.Messages;

public class ConfigurationComponentFactory {

	/**
	 * @param producer the ImageProducer to create a configuration component for.
	 * @return a JComponent linked to the supplied producer, 
	 * or null if none could be created.
	 */
	public static ConfigurationPane createConfigurationComponent(ImageProducer producer) {
		return createConfigurationComponent(producer, null);
	}

	protected static ConfigurationPane createConfigurationComponent(ImageProducer producer, ImageProducer topLevel) {
		
		if (producer instanceof Cacher) {
			Cacher c = (Cacher)producer; 
			if (topLevel == null) {
				topLevel = c;
			}
			return createConfigurationComponent(c.getSource(), topLevel);
		}
		
		if (topLevel == null) {
			topLevel = producer;
		}
		
		ConfigurationPane cp;
		
		if (producer instanceof CroppingFilter) {
			cp = CropConfigurationPane.create((CroppingFilter)producer);
		} else {
			cp = new SimpleConfigurationPane(topLevel);
		}
		
		
		
		if (producer instanceof ImageInput) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.image_loader_file_tab"), new ImageInputSettings((ImageInput)producer)); //$NON-NLS-1$
		} else if (producer instanceof ShrinkFilter) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.shrinker_shrinkage_tab"),  new ShrinkerSettings((ShrinkFilter)producer)); //$NON-NLS-1$
		} else if (producer instanceof AbstractReduceColorsFilter) {
			try {
				cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.reduce_colors_palette_tab"),  new PaletteSettings((AbstractReduceColorsFilter)producer)); //$NON-NLS-1$
			} catch (Exception e) {
				String info = Messages.get("ConfigurationComponentFactory.error_creating_palette_settings") + " \n" + e.getLocalizedMessage(); //$NON-NLS-1$ //$NON-NLS-2$
				cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.reduce_colors_palette_tab"), new JLabel(info)); //$NON-NLS-1$
				JOptionPane.showMessageDialog(null, info, Messages.get("General.error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
			if (producer instanceof DitherReduceColorsFilter) {
				cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.reduce_colors_dithering_tab"),  new DitherSettings((DitherReduceColorsFilter)producer)); //$NON-NLS-1$
			}
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.reduce_colors_usage_tab"), new ColorUsagePanel((AbstractReduceColorsFilter)producer)); //$NON-NLS-1$
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.reduce_colors_bead_sheet_tab"), new BeadSheetSettings((AbstractReduceColorsFilter)producer)); //$NON-NLS-1$
		} else if (producer instanceof BrightnessContrastFilter) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.brightness_contrast_tab"), new BrightnessContrastSettings((BrightnessContrastFilter)producer)); //$NON-NLS-1$
		} else if (producer instanceof ColorBalance) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.color_balance_color_tab"), new ColorBalanceSettings((ColorBalance)producer)); //$NON-NLS-1$
		} else if (producer instanceof Upscaler) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.upscaler_magnification_tab"), new UpscalerSettings((Upscaler)producer)); //$NON-NLS-1$
		} else if (producer instanceof Flipper) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.flipper_flip_tab"), new FlipSettings((Flipper)producer)); //$NON-NLS-1$
		} else if (producer instanceof RotateFilter) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.rotate_rotation_tab"), new RotateSettings((RotateFilter)producer)); //$NON-NLS-1$
		} else if (producer instanceof EdgeFilter) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.edge_filter_fields_tab"), new EdgeFieldSettings((EdgeFilter)producer)); //$NON-NLS-1$
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.edge_filter_fill_tab"), new EdgeFillSettings((EdgeFilter)producer)); //$NON-NLS-1$
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.edge_filter_edges_tab"), new EdgeSettings((EdgeFilter)producer)); //$NON-NLS-1$
		}
		
		
		if (producer instanceof AbstractImageFilter) {
			cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.general_tab"), new GeneralSettings((AbstractImageFilter)producer)); //$NON-NLS-1$
		}
		
		cp.addSettingsTab(Messages.get("ConfigurationComponentFactory.about_tab"), new AboutPanel(producer)); //$NON-NLS-1$
		
		return cp;
	}
	
}
