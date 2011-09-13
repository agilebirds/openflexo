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
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openflexo.AdvancedPrefs;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;


/**
 * Cell renderer for a browser view
 *
 * @author sguerin
 */
public class BrowserViewCellRenderer extends DefaultTreeCellRenderer
{

    /**
     *
     */
    public BrowserViewCellRenderer()
    {
        super();
        setFont(AdvancedPrefs.getBrowserFont().getFont());
        if (getBorder()!=null) {
            setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(0, 1, 0, 0)));
        } else
            setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
    }

    @Override
	public Component getTreeCellRendererComponent(JTree arg0, Object object, boolean _selected, boolean expanded, boolean leaf, int arg5,
            boolean arg6)
    {
        ProjectBrowser browser = (ProjectBrowser) arg0.getModel();
        try {
            browser.setHoldStructure();
            super.getTreeCellRendererComponent(arg0, object, _selected, expanded, leaf, arg5, arg6);
            BrowserElement element = (BrowserElement) object;
            if (!(element.isDeleted())) {
                String fullName = element.getName();
                if (element.getSuffixName() != null)
                    fullName += element.getSuffixName();
                setText(fullName);
                setIcon(expanded ? (element).getExpandedIcon() : (element).getIcon());
                if (!element.isSelectable()) {
                    setForeground(Color.LIGHT_GRAY);
                }
                Font f = element.mustBeHighlighted() ? getFont().deriveFont(Font.BOLD) : getFont();
                if (element.mustBeItalic())
                    f = f.deriveFont(Font.ITALIC);
                setFont(f);
               	setToolTipText(element.getToolTip());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            browser.resetHoldStructure();
        }
        return this;
    }

    @Override
    public String getToolTipText() {
    	return super.getToolTipText();
    }
/*
    public Color getBackgroundNonSelectionColor()
    {
        return Color.WHITE;
    }

    public Color getBackgroundSelectionColor()
    {
        return FlexoCst.LIGHT_BLUE;
    }

    */
    /*
     * public Icon getIcon() { if (_element != null) { return
     * _element.getIcon(); } return null; }
     *
     * public Icon getClosedIcon() { if (getIcon() != null) { return getIcon(); }
     * return super.getClosedIcon(); }
     *
     * public Icon getOpenIcon() { if (getIcon() != null) { return getIcon(); }
     * return super.getOpenIcon(); }
     *
     * public Icon getLeafIcon() { if (getIcon() != null) { return getIcon(); }
     * return super.getLeafIcon(); }
     */

    /*
     * public Icon getDefaultClosedIcon() { return null; }
     *
     * public Icon getDefaultLeafIcon() { return null; }
     *
     * public Icon getDefaultOpenIcon() { return null; }
     *
     */

    /**
     * Overrides updateUI
     * @see javax.swing.JLabel#updateUI()
     */
    @Override
    public void updateUI()
    {
        super.updateUI();
        // Fix for TreeCellRenderer
        setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
        setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
        setOpenIcon(UIManager.getIcon("Tree.openIcon"));

        setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
        setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
        setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
        setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
    }
}
