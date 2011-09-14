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
package org.openflexo.dm.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMRepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMRepositoryFolderTableModel extends AbstractModel<DMModel,DMRepositoryFolder>
{

    protected static final Logger logger = Logger.getLogger(DMRepositoryFolderTableModel.class.getPackage().getName());

    public DMRepositoryFolderTableModel(DMModel dataModel, FlexoProject project)
    {
        super(dataModel, project);
        addToColumns(new IconColumn<DMRepositoryFolder>("repository_folder_icon", 30) {
            @Override
			public Icon getIcon(DMRepositoryFolder folder)
            {
                return DMEIconLibrary.DM_REPOSITORY_FOLDER_ICON;
           }
        });
         addToColumns(new StringColumn<DMRepositoryFolder>("name", 300) {
            @Override
			public String getValue(DMRepositoryFolder folder)
            {
                return folder.getLocalizedName();
            }
        });
         addToColumns(new StringColumn<DMRepositoryFolder>("description", 570) {
             @Override
			public String getValue(DMRepositoryFolder folder)
             {
                 return folder.getLocalizedDescription();
             }
         });
        setRowHeight(20);
    }

    public DMModel getDMModel()
    {
        return getModel();
    }

    @Override
	public DMRepositoryFolder elementAt(int row)
    {
        if (row == 0)
            return getDMModel().getInternalRepositoryFolder();
        int nonEmptyFolders = 1;
        if (getDMModel().getNonPersistantDataRepositoryFolder().getRepositoriesCount() > 0) {
            if (row == nonEmptyFolders) return getDMModel().getNonPersistantDataRepositoryFolder();
            else nonEmptyFolders++;
        }
        if (getDMModel().getPersistantDataRepositoryFolder().getRepositoriesCount() > 0) {
            if (row == nonEmptyFolders) return getDMModel().getPersistantDataRepositoryFolder();
            else nonEmptyFolders++;
        }
        if (getDMModel().getLibraryRepositoryFolder().getRepositoriesCount() > 0) {
            if (row == nonEmptyFolders) return getDMModel().getLibraryRepositoryFolder();
            else nonEmptyFolders++;
        }
        return null;
    }

    public DMRepositoryFolder repositoryFolderAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getDMModel() != null) {
            int returned = 1;
            if (getDMModel().getNonPersistantDataRepositoryFolder().getRepositoriesCount() > 0) 
                returned++;
            if (getDMModel().getPersistantDataRepositoryFolder().getRepositoriesCount() > 0) 
                returned++;
           if (getDMModel().getLibraryRepositoryFolder().getRepositoriesCount() > 0) 
               returned++;
            return returned;
        }
        return 0;
    }

}
