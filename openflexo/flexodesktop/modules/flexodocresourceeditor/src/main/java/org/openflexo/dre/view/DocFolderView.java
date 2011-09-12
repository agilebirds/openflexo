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
package org.openflexo.dre.view;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.openflexo.dre.controller.DREController;
import org.openflexo.dre.controller.DRESelectionManager;
import org.openflexo.dre.view.model.DocItemFolderTableModel;
import org.openflexo.dre.view.model.DocItemTableModel;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DocFolderView extends DREView<DocItemFolder>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DocFolderView.class.getPackage().getName());

    private DocItemFolderTableModel folderTableModel;
    protected DRETabularView folderTable;

    private DocItemTableModel itemTableModel;
    protected DRETabularView itemTable;

    public DocFolderView(DocItemFolder folder, DREController controller)
    {
        super(folder, controller, "folder_($identifier)");
        /*addAction(new TabularViewAction(CreateDMProperty.actionType) {
            protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            protected FlexoModelObject getFocusedObject() 
            {
                return getDMEntity();
            }           
        });
        addAction(new TabularViewAction(CreateDMMethod.actionType) {
            protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            protected FlexoModelObject getFocusedObject() 
            {
                return getDMEntity();
            }           
        });
        addAction(new TabularViewAction(DMDelete.actionType) {
            protected Vector getGlobalSelection()
            {
                 return getViewSelection();
            }

            protected FlexoModelObject getFocusedObject() 
            {
                return null;
            }           
        });*/
         finalizeBuilding();
    }

    @Override
	protected JComponent buildContentPane()
    {
        DocItemFolder folder = getDRMObject();

        folderTableModel = new DocItemFolderTableModel(folder, getDREController().getProject());
        addToMasterTabularView(folderTable = new DRETabularView(getDREController(), folderTableModel, 15));

        itemTableModel = new DocItemTableModel(folder, getDREController().getProject());
        addToMasterTabularView(itemTable = new DRETabularView(getDREController(), itemTableModel, 15));

        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, folderTable, itemTable);
    }

    public DocItemFolder getDocItemFolder()
    {
        return getDRMObject();
    }

    public DocItemFolder getSelectedDocItemFolder()
    {
        DRESelectionManager sm = getDREController().getDRESelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DocItemFolder)) {
            return (DocItemFolder) selection.firstElement();
        }
        return null;
    }

    public DocItem getSelectedDocItem()
    {
        DRESelectionManager sm = getDREController().getDRESelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DocItem)) {
            return (DocItem) selection.firstElement();
        }
        return null;
    }

    public DRETabularView getFolderTable() 
    {
        return folderTable;
    }

    public DRETabularView getItemTable() 
    {
        return itemTable;
    }

     /**
     * Overrides willShow
     * @see org.openflexo.view.ModuleView#willShow()
     */
    @Override
	public void willShow()
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * Overrides willHide
     * @see org.openflexo.view.ModuleView#willHide()
     */
    @Override
	public void willHide()
    {
        // TODO Auto-generated method stub
        
    }

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management
	 * When not, Flexo will manage it's own scrollbar for you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() 
	{
		return false;
	}


}
