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
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select an Ontology Class while browsing the ontology library
 * 
 * @author sguerin
 * 
 */
public class OntologyClassSelector extends AbstractBrowserSelector<OntologyClass> {

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	private OntologyLibrary ontologyLibrary;

	public OntologyClassSelector(OntologyClass object) {
		super(null, object, OntologyClass.class);
	}

	public OntologyClassSelector(OntologyLibrary ontologyLibrary, OntologyClass object) {
		super(null, object, OntologyClass.class);
		setOntologyLibrary(ontologyLibrary);
	}

	public OntologyClassSelector(OntologyLibrary ontologyLibrary, OntologyClass object, int cols) {
		super(null, object, OntologyClass.class, cols);
		setOntologyLibrary(ontologyLibrary);
	}

	public OntologyLibrary getOntologyLibrary() {
		return ontologyLibrary;
	}

	public void setOntologyLibrary(OntologyLibrary ontologyLibrary) {
		this.ontologyLibrary = ontologyLibrary;
	}

	@Override
	protected OntologyClassSelectorPanel makeCustomPanel(OntologyClass editedObject) {
		return new OntologyClassSelectorPanel();
	}

	@Override
	public String renderedString(OntologyClass editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return STRING_REPRESENTATION_WHEN_NULL;
	}

	protected class OntologyClassSelectorPanel extends AbstractSelectorPanel<OntologyClass> {
		protected OntologyClassSelectorPanel() {
			super(OntologyClassSelector.this);
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
			setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);
			setOEViewMode(OEViewMode.FullHierarchy);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			if (getOntologyLibrary() != null) {
				return getOntologyLibrary().getRootClass();
			}
			return null;
		}
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

	@Override
	public FlexoModelObject getRootObject() {
		if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getRootClass();
		}
		return null;
	}

}
