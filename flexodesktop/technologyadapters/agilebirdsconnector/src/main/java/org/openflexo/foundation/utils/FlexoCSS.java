/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.utils;

import java.awt.Color;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents a style sheet to use for a process
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoCSS extends KVCFlexoObject implements StringConvertable<FlexoCSS>, ChoiceList {

	private static final Logger logger = Logger.getLogger(FlexoCSS.class.getPackage().getName());

	public static final FlexoCSS FLEXO = new FlexoMasterCSS();

	public static final FlexoCSS CONTENTO = new ContentoCSS();

	public static final FlexoCSS OMNISCIO = new OmniscioCSS();

	/*    public static final FlexoCSS BLUEWAVE = new BlueWaveCSS();*/

	public static final StringEncoder.Converter<FlexoCSS> flexoCSSConverter = new Converter<FlexoCSS>(FlexoCSS.class) {

		@Override
		public FlexoCSS convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(FlexoCSS value) {
			return value.getName();
		}

	};

	public static class FlexoMasterCSS extends FlexoCSS {
		protected FlexoMasterCSS() {
		}

		private FlexoProjectFile file = new FlexoProjectFile("FlexoMasterStyle.css");

		@Override
		public String getName() {
			return "Flexo";
		}

		@Override
		public FlexoProjectFile getFile() {
			return file;
		}

		@Override
		public Color getMainColor() {
			return flexoMainColor;
		}

		@Override
		public Color getButtonColor() {
			return flexoButtonColor;
		}

		@Override
		public Color getTextColor() {
			return flexoTextColor;
		}

		@Override
		public Color getOtherLineColor() {
			return flexoOtherLineColor;
		}

		@Override
		public Color getOddLineColor() {
			return flexoOddLineColor;
		}
	}

	public static class ContentoCSS extends FlexoCSS {
		protected ContentoCSS() {
		}

		private FlexoProjectFile file = new FlexoProjectFile("ContentoMasterStyle.css");

		@Override
		public String getName() {
			return "Contento";
		}

		@Override
		public FlexoProjectFile getFile() {
			return file;
		}

		@Override
		public Color getMainColor() {
			return contentoMainColor;
		}

		@Override
		public Color getButtonColor() {
			return contentoButtonColor;
		}

		@Override
		public Color getTextColor() {
			return contentoTextColor;
		}

		@Override
		public Color getOtherLineColor() {
			return contentoOtherLineColor;
		}

		@Override
		public Color getOddLineColor() {
			return contentoOddLineColor;
		}
	}

	public static class BlueWaveCSS extends FlexoCSS {
		protected BlueWaveCSS() {
		}

		private FlexoProjectFile file = new FlexoProjectFile("BlueWaveMasterStyle.css");

		@Override
		public String getName() {
			return "BlueWave";
		}

		@Override
		public FlexoProjectFile getFile() {
			return file;
		}

		@Override
		public Color getMainColor() {
			return contentoMainColor;
		}

		@Override
		public Color getButtonColor() {
			return contentoButtonColor;
		}

		@Override
		public Color getTextColor() {
			return contentoTextColor;
		}

		@Override
		public Color getOtherLineColor() {
			return contentoOtherLineColor;
		}

		@Override
		public Color getOddLineColor() {
			return contentoOddLineColor;
		}
	}

	public static class NewContentoCSS extends FlexoCSS {
		protected NewContentoCSS() {
		}

		private FlexoProjectFile file = new FlexoProjectFile("NewContentoMasterStyle.css");

		@Override
		public String getName() {
			return "NewContento";
		}

		@Override
		public FlexoProjectFile getFile() {
			return file;
		}

		@Override
		public Color getMainColor() {
			return contentoMainColor;
		}

		@Override
		public Color getButtonColor() {
			return contentoButtonColor;
		}

		@Override
		public Color getTextColor() {
			return contentoTextColor;
		}

		@Override
		public Color getOtherLineColor() {
			return contentoOtherLineColor;
		}

		@Override
		public Color getOddLineColor() {
			return contentoOddLineColor;
		}
	}

	public static class OmniscioCSS extends FlexoCSS {
		protected OmniscioCSS() {
		}

		private FlexoProjectFile file = new FlexoProjectFile("OmniscioMasterStyle.css");

		@Override
		public String getName() {
			return "Omniscio";
		}

		@Override
		public FlexoProjectFile getFile() {
			return file;
		}

		@Override
		public Color getMainColor() {
			return omniscioMainColor;
		}

		@Override
		public Color getButtonColor() {
			return omniscioButtonColor;
		}

		@Override
		public Color getTextColor() {
			return omniscioTextColor;
		}

		@Override
		public Color getOtherLineColor() {
			return omniscioOtherLineColor;
		}

		@Override
		public Color getOddLineColor() {
			return omniscioOddLineColor;
		}
	}

	public abstract String getName();

	public abstract Color getMainColor();

	public abstract Color getButtonColor();

	public abstract Color getTextColor();

	public abstract Color getOtherLineColor();

	public abstract Color getOddLineColor();

	public abstract FlexoProjectFile getFile();

	public static FlexoCSS get(String cssName) {
		if (FLEXO.getName().equalsIgnoreCase(cssName)) {
			return FLEXO;
		} else if (CONTENTO.getName().equalsIgnoreCase(cssName)) {
			return CONTENTO;
		} else if (OMNISCIO.getName().equalsIgnoreCase(cssName)) {
			return OMNISCIO;
		} else if ("ELECTRABEL".equalsIgnoreCase(cssName)) {
			return CONTENTO;
		} else {
			for (FlexoCSS css : availableValues()) {
				if (css.getName().equals(cssName)) {
					return css;
				}
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find FlexoCSS named " + cssName);
			}
			return null;
		}
	}

	@Override
	public StringEncoder.Converter<FlexoCSS> getConverter() {
		return flexoCSSConverter;
	}

	/**
	 * Return a Vector of possible values (which must be of the same type as the one declared as class implemented this interface)
	 * 
	 * @return a Vector of ChoiceList
	 */
	@Override
	public Vector<FlexoCSS> getAvailableValues() {
		if (_availableCSS == null) {
			_availableCSS = new Vector<FlexoCSS>();
			_availableCSS.add(FLEXO);
			_availableCSS.add(CONTENTO);
			_availableCSS.add(OMNISCIO);
			// _availableCSS.add(BLUEWAVE);
		}
		return _availableCSS;
	}

	private Vector<FlexoCSS> _availableCSS;

	public static Vector<FlexoCSS> availableValues() {
		return FLEXO.getAvailableValues();
	}

	public static final Color flexoTextColor = new Color(53, 85, 36);

	public static final Color flexoMainColor = new Color(162, 185, 94);

	public static final Color flexoOddLineColor = new Color(244, 246, 235);

	public static final Color flexoOtherLineColor = new Color(232, 237, 215);

	public static final Color contentoTextColor = new Color(27, 65, 128);

	public static final Color contentoMainColor = new Color(145, 170, 208);

	public static final Color contentoOddLineColor = new Color(249, 246, 249);

	public static final Color contentoOtherLineColor = new Color(231, 232, 234);

	public static final Color omniscioTextColor = new Color(248, 141, 0);

	public static final Color omniscioMainColor = new Color(249, 186, 109);

	public static final Color omniscioOddLineColor = new Color(253, 229, 200);

	public static final Color omniscioOtherLineColor = new Color(239, 220, 190);

	public static final Color omniscioButtonColor = new Color(245, 121, 15);

	public static final Color contentoButtonColor = new Color(44, 60, 122);

	public static final Color flexoButtonColor = new Color(74, 119, 49);
}
