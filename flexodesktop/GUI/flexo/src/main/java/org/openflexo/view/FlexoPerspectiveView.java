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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.openflexo.ch.FCH;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.FlexoController;


/**
 * @author gpolet
 *
 */
public class FlexoPerspectiveView extends JLabel
{
    private FlexoPerspective<?> perspective;

    protected FlexoController controller;

    protected AvailablePerspectives parent;

    public FlexoPerspectiveView(final FlexoController controller, final AvailablePerspectives parent,FlexoPerspective<?> p)
    {
        this.controller = controller;
        this.perspective = p;
        this.parent = parent;
        addMouseListener(new MouseAdapter() {
            /**
             * Overrides mouseClicked
             *
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
			public void mouseClicked(MouseEvent e)
            {
                if (e.getSource() instanceof FlexoPerspectiveView) {
                    controller.switchToPerspective(((FlexoPerspectiveView) e.getSource()).getPerspective());
                    parent.refresh();
                }
            }
        });
        refresh();
        FCH.setHelpItem(this, p.getName());
    }

    public FlexoPerspective<?> getPerspective()
    {
        return perspective;
    }

    @Override
	public Icon getIcon()
    {
        if (controller.getCurrentPerspective() == perspective) {
            //setEnabled(false);
            if (perspective.getSelectedIcon() != null)
                return perspective.getSelectedIcon();
            else
                return IconLibrary.DEFAULT_PERSPECTIVE_SELECTED_ICON;
        } else {
            //setEnabled(true);
            if (perspective.getSelectedIcon() != null)
                return perspective.getActiveIcon();
            else
                return IconLibrary.DEFAULT_PERSPECTIVE_ACTIVE_ICON;
        }
    }

    public void refresh()
    {
        setIcon(getIcon());
        setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
    }
}
