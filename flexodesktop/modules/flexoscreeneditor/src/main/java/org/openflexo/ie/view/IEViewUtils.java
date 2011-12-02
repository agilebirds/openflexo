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
package org.openflexo.ie.view;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.util.FlexoConceptualColor;
import org.openflexo.foundation.utils.FlexoCSS;

/**
 * @author sguerin
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class IEViewUtils {

	private static final Logger logger = Logger.getLogger(IEViewUtils.class.getPackage().getName());

	public static Color colorFromConceptualColor(FlexoConceptualColor conceptualColor, FlexoCSS css) {
		if (conceptualColor instanceof FlexoConceptualColor.CustomColor) {
			return ((FlexoConceptualColor.CustomColor) conceptualColor).getColor();
		} else if (conceptualColor == FlexoConceptualColor.MAIN_COLOR) {
			return css.getMainColor();
		} else if (conceptualColor == FlexoConceptualColor.TEXT_COLOR) {
			return css.getTextColor();
		} else if (conceptualColor == FlexoConceptualColor.ODD_LINE_COLOR) {
			return css.getOddLineColor();
		} else if (conceptualColor == FlexoConceptualColor.OTHER_LINE_COLOR) {
			return css.getOtherLineColor();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No color defined for FlexoConceptualColor " + conceptualColor);
			}
			return Color.WHITE;
		}
	}

	public static Border getBorderForOperator(IEOperator operator) {
		if (operator instanceof ConditionalOperator) {
			return new LineBorder(Color.BLUE);
		}
		return new LineBorder(Color.GREEN);
	}

	/**
	 * @param insertedWidget
	 * @return
	 */

}
