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
package org.openflexo.ch;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.drm.DocItem;
import org.openflexo.help.FlexoHelp;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.FlexoFrame;


public class TrackComponentCHForHelpView extends TrackComponentCH {

    private static final Logger logger = Logger.getLogger(TrackComponentCHForHelpView.class.getPackage().getName());

    private static Cursor HELP_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(IconLibrary.HELP_CURSOR.getImage(),new Point(8,8),"Help cursor");
    
    public TrackComponentCHForHelpView (FlexoFrame frame)
    {
        super(frame);
        frame.getContentPane().setCursor(HELP_CURSOR);
     }
    
     @Override
	public void applyTracking(JComponent component)
    {
        DocItem item = FCH.getDocForComponent(focusedComponent);
        if (item != null) {
            FlexoHelp.getHelpBroker().setCurrentID(item.getIdentifier());
            FlexoHelp.getHelpBroker().setDisplayed(true);
            logger.info("Trying to display help for "+item.getIdentifier());
        }
    }
    
}
