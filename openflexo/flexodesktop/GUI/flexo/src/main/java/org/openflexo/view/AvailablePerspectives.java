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
package org.openflexo.view;

import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.FlexoController;


/**
 * @author gpolet
 *
 */
public class AvailablePerspectives extends JPanel
{
	   protected static final Logger logger = Logger.getLogger(AvailablePerspectives.class.getPackage().getName());

	   protected final FlexoController controller;

    private Hashtable<FlexoPerspective<?>,FlexoPerspectiveView> views;

    /**
     *
     */
    public AvailablePerspectives(FlexoController controller)
    {
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.controller = controller;
        views = new Hashtable<FlexoPerspective<?>,FlexoPerspectiveView>();

        Enumeration<FlexoPerspective<?>> en = controller.getPerspectives().elements();
        while (en.hasMoreElements()) {
            FlexoPerspective<?> p = en.nextElement();
            FlexoPerspectiveView view = new FlexoPerspectiveView(controller, this, p);
            add(view);
            views.put(p, view);

        }

        validate();
        repaint();
    }

    public void refresh()
    {
        Vector<FlexoPerspective<?>> displayedPerspective = new Vector<FlexoPerspective<?>>();
        removeAll();
        add(new JLabel(IconLibrary.NAVIGATION_CLOSE_LEFT));
        Enumeration<FlexoPerspective<?>> en = controller.getPerspectives().elements();
        while (en.hasMoreElements()) {
            FlexoPerspective<?> p = en.nextElement();
            FlexoPerspectiveView v = views.get(p);
            if (v == null){
            	views.put(p, v = new FlexoPerspectiveView(controller,this,p));
            }
            if (p.isAlwaysVisible() || controller.hasViewForObjectAndPerspective(controller.getCurrentDisplayedObjectAsModuleView(), p)) {
                if (displayedPerspective.size()>1) {
                    JLabel s = new JLabel(IconLibrary.NAVIGATION_SPACER);
                    s.setSize(IconLibrary.NAVIGATION_SPACER.getIconWidth(), IconLibrary.NAVIGATION_SPACER.getIconHeight());
                    add(s);
                }
                displayedPerspective.add(p);
                add(v);
                v.refresh();
            }
        }
        add(new JLabel(IconLibrary.NAVIGATION_CLOSE_RIGHT));
        if (displayedPerspective.size()<2) {
            removeAll();
            setVisible(false);
        } else {
            setVisible(true);
        }
    }
}
