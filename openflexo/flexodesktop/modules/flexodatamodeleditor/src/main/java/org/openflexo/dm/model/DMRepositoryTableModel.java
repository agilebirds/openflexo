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

import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;


import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.ComponentRepository;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMRepositoryFolder;
import org.openflexo.foundation.dm.DenaliFoundationRepository;
import org.openflexo.foundation.dm.ExternalDatabaseRepository;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.ProcessBusinessDataRepository;
import org.openflexo.foundation.dm.ProcessInstanceRepository;
import org.openflexo.foundation.dm.ProjectDatabaseRepository;
import org.openflexo.foundation.dm.ProjectRepository;
import org.openflexo.foundation.dm.RationalRoseRepository;
import org.openflexo.foundation.dm.ThesaurusDatabaseRepository;
import org.openflexo.foundation.dm.ThesaurusRepository;
import org.openflexo.foundation.dm.WORepository;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.dm.XMLSchemaRepository;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMRepositoryTableModel extends AbstractModel<DMRepositoryFolder,DMRepository>
{

    protected static final Logger logger = Logger.getLogger(DMRepositoryTableModel.class.getPackage().getName());

    public DMRepositoryTableModel(DMRepositoryFolder repositoryFolder, FlexoProject project)
    {
        super(repositoryFolder, project);
        addToColumns(new IconColumn<DMRepository>("repository_icon", 30) {
            @Override
			public Icon getIcon(DMRepository repository)
            {
                return iconForRepository(repository);
            }
        });
        addToColumns(new IconColumn<DMRepository>("read_only", 25) {
            @Override
			public Icon getIcon(DMRepository repository)
            {
                return (repository.isReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON);
            }
            
            @Override
            public String getLocalizedTooltip(DMRepository repository) {
            	return (repository.isReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization.localizedForKey("is_not_read_only"));
            }
        });
        addToColumns(new StringColumn<DMRepository>("name", 190) {
            @Override
			public String getValue(DMRepository repository)
            {
                return repository.getLocalizedName();
            }
        });
        addToColumns(new StringColumn<DMRepository>("type", 190) {
            @Override
			public String getValue(DMRepository repository)
            {
                return typeOfRepository(repository);
            }
        });
        addToColumns(new EditableStringColumn<DMRepository>("description", 365) {
            @Override
			public String getValue(DMRepository repository)
            {
                return repository.getDescription();
            }

            @Override
			public void setValue(DMRepository repository, String aValue)
            {
                repository.setDescription(aValue);
            }
        });
        setRowHeight(20);
    }

    public DMRepositoryFolder getDMRepositoryFolder()
    {
        return getModel();
    }

    @Override
	public DMRepository elementAt(int row)
    {
        if ((row >= 0) && (row < getRowCount())) {
            return getDMRepositoryFolder().getRepositoryAtIndex(row);
         } else {
            return null;
        }
    }

    public DMRepository repositoryAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getDMRepositoryFolder() != null) {
            return getDMRepositoryFolder().getRepositoriesCount();
        }
        return 0;
    }

    protected static Icon iconForRepository(DMRepository repository)
    {
        if (repository instanceof JDKRepository) {
            return DMEIconLibrary.JDK_REPOSITORY_ICON;
        } else if (repository instanceof WORepository) {
            return DMEIconLibrary.WO_REPOSITORY_ICON;
        } else if (repository instanceof ComponentRepository) {
            return DMEIconLibrary.COMPONENT_REPOSITORY_ICON;
        } else if (repository instanceof ProcessInstanceRepository) {
            return DMEIconLibrary.PROCESS_INSTANCE_REPOSITORY_ICON;
        } else if (repository instanceof ProcessBusinessDataRepository) {
        	return DMEIconLibrary.PROCESS_BUSINESS_DATA_REPOSITORY_ICON;
        } else if (repository instanceof ExternalRepository) {
            return DMEIconLibrary.EXTERNAL_REPOSITORY_ICON;
        } else if (repository instanceof DMEORepository) {
            return DMEIconLibrary.DM_EOREPOSITORY_ICON;
        }
        return DMEIconLibrary.DM_REPOSITORY_ICON;
    }

    protected static String typeOfRepository(DMRepository repository)
    {
        if ((repository instanceof JDKRepository) || (repository instanceof WORepository) || (repository instanceof ComponentRepository)
                || (repository instanceof ProcessInstanceRepository) || (repository instanceof ProcessBusinessDataRepository) || (repository instanceof EOPrototypeRepository)
                || (repository instanceof FlexoExecutionModelRepository)) {
            return repository.getLocalizedName();
        } else if (repository instanceof ProjectRepository) {
            return FlexoLocalization.localizedForKey("project_repository");
        } else if (repository instanceof ProjectDatabaseRepository) {
            return FlexoLocalization.localizedForKey("project_database_repository");
        } else if (repository instanceof ExternalRepository) {
            return FlexoLocalization.localizedForKey("external_repository");
        } else if (repository instanceof DenaliFoundationRepository) {
            return FlexoLocalization.localizedForKey("denali_foundation_repository");
        } else if (repository instanceof ExternalDatabaseRepository) {
            return FlexoLocalization.localizedForKey("external_database_repository");
        } else if (repository instanceof ThesaurusRepository) {
            return FlexoLocalization.localizedForKey("thesaurus_repository");
        } else if (repository instanceof ThesaurusDatabaseRepository) {
            return FlexoLocalization.localizedForKey("thesaurus_database_repository");
        } else if (repository instanceof RationalRoseRepository) {
            return FlexoLocalization.localizedForKey("rational_rose_repository");
        }
        else if (repository instanceof WSDLRepository) {
            return FlexoLocalization.localizedForKey("web_service_repository");
        } 
        else if (repository instanceof XMLSchemaRepository) {
            return FlexoLocalization.localizedForKey("xml_schema_repository");
        }
        return "???";
    }

}
