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
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select an Ontology while browsing the ontology library
 * 
 * @author sguerin
 * 
 */
public class OntologySelector extends AbstractBrowserSelector<ImportedOntology> {

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	private OntologyLibrary ontologyLibrary;

	public OntologySelector(ImportedOntology object) {
		super(null, object, ImportedOntology.class);
	}

	public OntologySelector(OntologyLibrary ontologyLibrary, ImportedOntology object) {
		super(null, object, ImportedOntology.class);
		setOntologyLibrary(ontologyLibrary);
	}

	public OntologySelector(OntologyLibrary ontologyLibrary, ImportedOntology object, int cols) {
		super(null, object, ImportedOntology.class, cols);
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
	protected OntologySelectorPanel makeCustomPanel(ImportedOntology editedObject) {
		return new OntologySelectorPanel();
	}

	@Override
	public String renderedString(ImportedOntology editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return STRING_REPRESENTATION_WHEN_NULL;
	}

	protected class OntologySelectorPanel extends AbstractSelectorPanel<ImportedOntology> {
		protected OntologySelectorPanel() {
			super(OntologySelector.this);
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
			setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.ONTOLOGY_FOLDER, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);
			setOEViewMode(OEViewMode.NoHierarchy);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			if (getOntologyLibrary() != null) {
				return getOntologyLibrary();
			}
			return null;
		}
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

	@Override
	public FlexoModelObject getRootObject() {
		return getOntologyLibrary();
	}

}
