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
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select an Ontology Object while browsing the ontology library
 * 
 * @author sguerin
 * 
 */
public class OntologyObjectSelector extends AbstractBrowserSelector<OntologyObject> {

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	private OntologyLibrary ontologyLibrary;

	public OntologyObjectSelector(OntologyObject object) {
		super(null, object, OntologyObject.class);
	}

	public OntologyObjectSelector(OntologyLibrary ontologyLibrary, OntologyObject object, int cols) {
		super(null, object, OntologyObject.class, cols);
		setOntologyLibrary(ontologyLibrary);
	}

	@Override
	public void delete() {
		super.delete();
		setOntologyLibrary(null);
	}

	public OntologyLibrary getOntologyLibrary() {
		return ontologyLibrary;
	}

	public void setOntologyLibrary(OntologyLibrary ontologyLibrary) {
		this.ontologyLibrary = ontologyLibrary;
	}

	@Override
	protected OntologyObjectSelectorPanel makeCustomPanel(OntologyObject editedObject) {
		return new OntologyObjectSelectorPanel();
	}

	@Override
	public String renderedString(OntologyObject editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return STRING_REPRESENTATION_WHEN_NULL;
	}

	protected class OntologyObjectSelectorPanel extends AbstractSelectorPanel<OntologyObject> {
		protected OntologyObjectSelectorPanel() {
			super(OntologyObjectSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new OntologyBrowser();
		}

	}

	protected class OntologyBrowser extends ProjectBrowser {

		protected OntologyBrowser() {
			super(getOntologyLibrary() != null ? getOntologyLibrary().getProject() : null, false);
			init();
		}

		@Override
		public void configure() {

			setFilterStatus(BrowserElementType.WORKFLOW, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT_FOLDER, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DKV_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.MENU_ITEM, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.IMPORTED_PROCESS_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OE_SHEMA_LIBRARY, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			setFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);

			if (hierarchicalMode) {
				setOEViewMode(OEViewMode.PartialHierarchy);
			} else {
				setOEViewMode(OEViewMode.NoHierarchy);
			}
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getOntologyLibrary();
		}
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

	private boolean hierarchicalMode = false;

	public boolean getHierarchicalMode() {
		return hierarchicalMode;
	}

	public void setHierarchicalMode(boolean hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
	}

}
