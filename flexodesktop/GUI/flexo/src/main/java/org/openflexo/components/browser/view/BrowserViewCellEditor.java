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
package org.openflexo.components.browser.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;


/**
 * Cell editor for a browser view
 *
 * @author sguerin
 */
public class BrowserViewCellEditor extends DefaultTreeCellEditor
{

    Icon _customIcon;

    public BrowserViewCellEditor(JTree tree, BrowserViewCellRenderer renderer)
    {
        super(tree, renderer);
        //setFont(AdvancedPrefs.getBrowserFont().getTheFont());
    }

    @Override
	public Component getTreeCellEditorComponent(JTree arg0, Object element, boolean _selected, boolean expanded, boolean leaf, int row)
    {
        String editingName = ((BrowserElement) element).getName();
        _customIcon = expanded?((BrowserElement) element).getExpandedIcon():((BrowserElement) element).getIcon();
        Component returned = super.getTreeCellEditorComponent(arg0, editingName, _selected, expanded, leaf, row);
        return returned;
    }

    @Override
	public boolean isCellEditable(EventObject event)
    {
        if (super.isCellEditable(event)) {
            if (event != null) {
                if (event.getSource() instanceof JTree) {
                    setTree((JTree) event.getSource());
                    if (event instanceof MouseEvent) {
                        TreePath path = tree.getPathForLocation(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
                        if (path != null) {
                            BrowserElement element = (BrowserElement) path.getLastPathComponent();
                            return element.isNameEditable();
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates the container to manage placement of
     * <code>editingComponent</code>.
     */
    @Override
	protected Container createContainer()
    {
        return new CustomEditorContainer();
    }

    protected Component getEditingComponent()
    {
        return editingComponent;
    }

    protected int getOffset()
    {
        return offset;
    }

    protected DefaultTreeCellRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * Container responsible for placing the <code>editingComponent</code>.
     */
    public class CustomEditorContainer extends Container
    {
        /**
         * Constructs an <code>EditorContainer</code> object.
         */
        public CustomEditorContainer()
        {
            setLayout(null);
        }

        /**
         * Overrides <code>Container.paint</code> to paint the node's icon and
         * use the selection color for the background.
         */
        @Override
		public void paint(Graphics g)
        {
            Dimension size = getSize();

            // Then the icon.
            if (_customIcon != null) {
                int yLoc = Math.max(0, (getSize().height - _customIcon.getIconHeight()) / 2);

                _customIcon.paintIcon(this, g, 0, yLoc);
            }

            // Border selection color
            Color background = getBorderSelectionColor();
            if (background != null) {
                g.setColor(background);
                g.drawRect(0, 0, size.width - 1, size.height - 1);
            }
            super.paint(g);
        }

        /**
         * Lays out this <code>Container</code>. If editing, the editor will
         * be placed at <code>offset</code> in the x direction and 0 for y.
         */
        @Override
		public void doLayout()
        {
            if (getEditingComponent() != null) {
                Dimension cSize = getSize();

                getEditingComponent().getPreferredSize();
                getEditingComponent().setLocation(getOffset(), 0);
                getEditingComponent().setBounds(getOffset(), 0, cSize.width - getOffset(), cSize.height);
            }
        }

        /**
         * Returns the preferred size for the <code>Container</code>. This
         * will be at least preferred size of the editor plus
         * <code>offset</code>.
         *
         * @return a <code>Dimension</code> containing the preferred size for
         *         the <code>Container</code>; if
         *         <code>editingComponent</code> is <code>null</code> the
         *         <code>Dimension</code> returned is 0, 0
         */
        @Override
		public Dimension getPreferredSize()
        {
            if (getEditingComponent() != null) {
                Dimension pSize = getEditingComponent().getPreferredSize();

                pSize.width += getOffset() + 5;

                Dimension rSize = (getRenderer() != null) ? getRenderer().getPreferredSize() : null;

                if (rSize != null)
                    pSize.height = Math.max(pSize.height, rSize.height);
                if (_customIcon != null)
                    pSize.height = Math.max(pSize.height, _customIcon.getIconHeight());

                // Make sure width is at least 100.
                pSize.width = Math.max(pSize.width, 100);
                return pSize;
            }
            return new Dimension(0, 0);
        }
    }
}
