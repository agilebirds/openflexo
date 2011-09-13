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
package org.openflexo.dm.view;

/*
 * MainScrollPane.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 10, 2004
 */

import org.openflexo.dm.DMFrame;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;


/**
 * Represents the main pane where process can be edited
 * 
 * @author benoit,sylvain
 */
public class DMMainPane extends FlexoMainPane
{
    public DMMainPane(ModuleView moduleView, DMFrame mainFrame, DMController controller)
    {
        super(moduleView,mainFrame,controller);
        //setLeftView(new DMBrowserView(controller.getDMBrowser(), controller));
     }

   /* public void switchToPerspective(FlexoPerspective perspective)
    {
    	JComponent newLeftView = null;
    	Dimension oldSize = getLeftView().getSize();
    	if (perspective == getController().REPOSITORY_PERSPECTIVE) {
    		getController().getDMBrowser().setDMViewMode(DMBrowser.DMViewMode.Repositories);
    		newLeftView = new DMBrowserView(getController().getDMBrowser(), getController());
    	}
    	else if (perspective == getController().PACKAGE_PERSPECTIVE) {
    		getController().getDMBrowser().setDMViewMode(DMBrowser.DMViewMode.Packages);
    		DMBrowserView mainBrowserView = new DMBrowserView(getController().getDMBrowser(), getController()) {
    		    public void objectAddedToSelection(ObjectAddedToSelectionEvent event)
    		    {
    		    	if (event.getAddedObject() instanceof DMEntity) {
       		    		propertiesBrowser.deleteBrowserListener(this); 		            
    		    		propertiesBrowser.setRepresentedEntity((DMEntity)event.getAddedObject());
    		    		propertiesBrowser.update();
       		    		propertiesBrowser.addBrowserListener(this); 		            
    		    	}
    		    	super.objectAddedToSelection(event);
    		    }			
    		};
    		DMBrowserView propertiesBrowserView = new DMBrowserView(propertiesBrowser, getController());
    		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,mainBrowserView,propertiesBrowserView);
    		splitPane.setDividerLocation(0.7);
    		splitPane.setResizeWeight(0.7);
    		newLeftView = splitPane;
    	}
       	else if (perspective == getController().HIERARCHY_PERSPECTIVE) {
    		getController().getDMBrowser().setDMViewMode(DMBrowser.DMViewMode.Hierarchy);
       		DMBrowserView mainBrowserView = new DMBrowserView(getController().getDMBrowser(), getController()) {
       		    public void objectAddedToSelection(ObjectAddedToSelectionEvent event)
    		    {
    		    	if (event.getAddedObject() instanceof DMEntity) {
    		    		propertiesBrowser.deleteBrowserListener(this); 		            
    		    		propertiesBrowser.setRepresentedEntity((DMEntity)event.getAddedObject());
    		    		propertiesBrowser.update();
    		    		propertiesBrowser.addBrowserListener(this); 		            
    		    	}
    		    	super.objectAddedToSelection(event);
    		    }			
    		};
    		DMBrowserView propertiesBrowserView = new DMBrowserView(propertiesBrowser, getController());
    		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,mainBrowserView,propertiesBrowserView);
    		splitPane.setDividerLocation(0.7);
    		splitPane.setResizeWeight(0.7);
    		newLeftView = splitPane;
    	}
       	else if (perspective == getController().DIAGRAM_PERSPECTIVE) {
    		getController().getDMBrowser().setDMViewMode(DMBrowser.DMViewMode.Hierarchy);
       		DMBrowserView mainBrowserView = new DMBrowserView(getController().getDMBrowser(), getController()) {
       		    public void objectAddedToSelection(ObjectAddedToSelectionEvent event)
    		    {
    		    	if (event.getAddedObject() instanceof DMEntity) {
    		    		propertiesBrowser.deleteBrowserListener(this); 		            
    		    		propertiesBrowser.setRepresentedEntity((DMEntity)event.getAddedObject());
    		    		propertiesBrowser.update();
    		    		propertiesBrowser.addBrowserListener(this); 		            
    		    	}
    		    	super.objectAddedToSelection(event);
    		    }			
    		};
    		DMBrowserView propertiesBrowserView = new DMBrowserView(propertiesBrowser, getController());
    		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,mainBrowserView,propertiesBrowserView);
    		splitPane.setDividerLocation(0.7);
    		splitPane.setResizeWeight(0.7);
    		newLeftView = splitPane;
    	}
    	JPanel leftPanel = new JPanel(new BorderLayout());
    	leftPanel.add(newLeftView,BorderLayout.CENTER);
    	JPanel searchPanel = new JPanel(new BorderLayout());
    	searchPanel.add(new JLabel(FlexoLocalization.localizedForKey("search")),BorderLayout.WEST);
    	searchPanel.add(new DMEntitySelector<DMEntity>(getController().getProject(),searchedEntity) {
    		public void setEditedObject(DMEntity entity) {
    			super.setEditedObject(entity);
    			searchedEntity = entity;
    			getController().getSelectionManager().setSelectedObject(entity);
    		}
    	},BorderLayout.CENTER);
    	leftPanel.add(searchPanel,BorderLayout.NORTH);
     	leftPanel.setPreferredSize(oldSize);
   	
       	setLeftView(leftPanel);
     }*/
     
    DMEntity searchedEntity = null;
    
    public void showDataModelBrowser()
    {
        showLeftView();
    }

    public void hideDataModelBrowser()
    {
        hideLeftView();
    }

	@Override
	public DMController getController() 
	{
		return (DMController)super.getController();
	}
  
    @Override
	protected  FlexoModelObject getParentObject(FlexoModelObject object)
    {
       if (object instanceof DMObject) {
           return (DMObject)(((DMObject)object).getParent());
       }
       return null;
    }

}
