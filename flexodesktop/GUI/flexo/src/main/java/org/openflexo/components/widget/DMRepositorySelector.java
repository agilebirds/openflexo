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
package org.openflexo.components.widget;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.rm.FlexoProject;


/**
 * Widget allowing to select a DMRepository while browsing the data model
 *
 * @author sguerin
 *
 */
public class DMRepositorySelector extends AbstractBrowserSelector<DMRepository>
{

    protected static final String EMPTY_STRING = "";
    protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

    public DMRepositorySelector(FlexoProject project, DMRepository repository)
    {
        super(project, repository, DMRepository.class);
    }

    public DMRepositorySelector(FlexoProject project, DMRepository repository, int cols)
    {
        super(project, repository, DMRepository.class, cols);
    }

    DMModel getDataModel()
    {
        if (getProject() != null) {
            return getProject().getDataModel();
        }
        return null;
    }

    @Override
	protected DMRepositorySelectorPanel makeCustomPanel(DMRepository editedObject)
    {
        return new DMRepositorySelectorPanel();
    }

    @Override
	public String renderedString(DMRepository editedObject)
    {
        if (editedObject != null) {
            return editedObject.getLocalizedName();
        }
        return STRING_REPRESENTATION_WHEN_NULL;
    }

    protected class DMRepositorySelectorPanel extends AbstractSelectorPanel<DMRepository>
    {
        protected DMRepositorySelectorPanel()
        {
            super(DMRepositorySelector.this);
        }

        @Override
		protected ProjectBrowser createBrowser(FlexoProject project)
        {
            return new DataModelBrowser(/* project.getDataModel() */);
        }

    }

    protected class DataModelBrowser extends ProjectBrowser
    {

        // private DMModel _dmModel;

        protected DataModelBrowser(/* DMModel dataModel */)
        {
            super((getDataModel()!=null?getDataModel().getProject():null), false);
            init();
        }

        @Override
		public void configure()
        {
            setFilterStatus(BrowserElementType.DM_PACKAGE, BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.DM_EOMODEL, BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.DM_ENTITY, BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.DM_PROPERTY, BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.DM_EOATTRIBUTE, BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.DM_EORELATIONSHIP, BrowserFilterStatus.HIDE);
        }

        @Override
		public FlexoModelObject getDefaultRootObject()
        {
            return getDataModel();
        }
    }

    public void setNullStringRepresentation(String aString)
    {
        STRING_REPRESENTATION_WHEN_NULL = aString;
    }

}
