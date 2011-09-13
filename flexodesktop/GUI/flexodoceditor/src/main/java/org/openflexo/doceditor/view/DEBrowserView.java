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
package org.openflexo.doceditor.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;


import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.doceditor.DECst;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.doceditor.controller.browser.DEBrowser;
import org.openflexo.doceditor.controller.browser.TOCTreeDropTarget;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 *
 */
public class DEBrowserView extends BrowserView
{

    private DEController controller;
    
    /**
     * @param browser
     * @param kl
     */
    public DEBrowserView(DEController controller, ProjectBrowser browser)
    {
        super(browser, controller.getKeyEventListener(), controller.getEditor());
        this.controller = controller;
        
        FlowLayout flowLayout = new FlowLayout();
        JPanel viewModePanels = new JPanel(flowLayout);
        viewModePanels.setBorder(BorderFactory.createEmptyBorder());
        flowLayout.setHgap(2);
        //logger.info("hGap="+flowLayout.getHgap()+" vGap="+flowLayout.getVgap());       
        
        setMinimumSize(new Dimension(DECst.MINIMUM_BROWSER_VIEW_WIDTH, DECst.MINIMUM_BROWSER_VIEW_HEIGHT));
        setPreferredSize(new Dimension(DECst.PREFERRED_BROWSER_VIEW_WIDTH, DECst.PREFERRED_BROWSER_VIEW_HEIGHT));
 
    }
    
    @Override
    protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2,
    		ProjectBrowser _browser2) {
    	return new TOCTreeDropTarget(treeView2, _browser2);
    }

    protected abstract class ViewModeButton extends JButton implements MouseListener, ActionListener
    {
    	protected ViewModeButton(ImageIcon icon, String unlocalizedDescription)
    	{
    		super(icon);
    		setToolTipText(FlexoLocalization.localizedForKey(unlocalizedDescription));
    		setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    		addMouseListener(this);
    		addActionListener(this);
    	}

		@Override
		public void mouseClicked(MouseEvent e) 
		{
		}

		@Override
		public void mouseEntered(MouseEvent e) 
		{
			setBorder(BorderFactory.createEtchedBorder());
		}

		@Override
		public void mouseExited(MouseEvent e) 
		{
			setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		}

		@Override
		public void mousePressed(MouseEvent e) 
		{
		}

		@Override
		public void mouseReleased(MouseEvent e) 
		{
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			setFilters();
			getBrowser().update();
		}
		
		public abstract void setFilters();
    }
    
    @Override
	public DEBrowser getBrowser()
    {
    	return (DEBrowser)super.getBrowser();
    }
    
    /**
     * Overrides treeSingleClick
     * @see org.openflexo.components.browser.view.BrowserView#treeSingleClick(org.openflexo.foundation.FlexoModelObject)
     */
    @Override
	public void treeSingleClick(FlexoModelObject object)
    {
    	if (object instanceof GenerationRepository) {
        	controller.setCurrentEditedObjectAsModuleView(object);
    	}
    	else if (object instanceof TOCEntry) {
    		controller.setCurrentEditedObjectAsModuleView(object);
    	}
    }

    /**
     * Overrides treeDoubleClick
     * @see org.openflexo.components.browser.view.BrowserView#treeDoubleClick(org.openflexo.foundation.FlexoModelObject)
     */
    @Override
	public void treeDoubleClick(FlexoModelObject object)
    {
    	
    }

}
