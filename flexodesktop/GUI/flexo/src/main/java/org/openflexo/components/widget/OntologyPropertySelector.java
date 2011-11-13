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
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select an Ontology Object while browsing the ontology library
 * 
 * @author sguerin
 * 
 */
public class OntologyPropertySelector extends AbstractBrowserSelector<OntologyProperty> {

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	private OntologyLibrary ontologyLibrary;

	public OntologyPropertySelector(OntologyProperty object) {
		super(null, object, OntologyProperty.class);
	}

	public OntologyPropertySelector(OntologyLibrary ontologyLibrary, OntologyProperty object, int cols) {
		super(null, object, OntologyProperty.class, cols);
		setOntologyLibrary(ontologyLibrary);
	}

	public OntologyLibrary getOntologyLibrary() {
		return ontologyLibrary;
	}

	public void setOntologyLibrary(OntologyLibrary ontologyLibrary) {
		this.ontologyLibrary = ontologyLibrary;
	}

	@Override
	protected OntologyPropertySelectorPanel makeCustomPanel(OntologyProperty editedObject) {
		return new OntologyPropertySelectorPanel();
	}

	@Override
	public String renderedString(OntologyProperty editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return STRING_REPRESENTATION_WHEN_NULL;
	}

	protected class OntologyPropertySelectorPanel extends AbstractSelectorPanel<OntologyProperty> {
		protected OntologyPropertySelectorPanel() {
			super(OntologyPropertySelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new OntologyBrowser();
		}

	}

	protected class OntologyBrowser extends ProjectBrowser {

		protected OntologyBrowser() {
			super((getOntologyLibrary() != null ? getOntologyLibrary().getProject() : null), false);
			init();
		}

		@Override
		public void configure() {
			if (hierarchicalMode) {
				setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.HIDE, true);
				setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.HIDE, true);
				setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);
				setOEViewMode(OEViewMode.FullHierarchy);
			} else {
				setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);
				setOEViewMode(OEViewMode.PartialHierarchy);
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

	@Override
	public FlexoModelObject getRootObject() {
		return getOntologyLibrary();
	}

	private boolean hierarchicalMode = true;

	public boolean getHierarchicalMode() {
		return hierarchicalMode;
	}

	public void setHierarchicalMode(boolean hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
	}

}
