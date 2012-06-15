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
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
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

	private FlexoOntology ontology;

	public OntologyClassSelector(OntologyClass object) {
		super(null, object, OntologyClass.class);
	}

	public OntologyClassSelector(FlexoOntology ontology, OntologyClass object) {
		super(null, object, OntologyClass.class);
		setOntology(ontology);
	}

	public OntologyClassSelector(FlexoOntology ontology, OntologyClass object, int cols) {
		super(null, object, OntologyClass.class, cols);
		setOntology(ontology);
	}

	@Override
	public void delete() {
		super.delete();
		setOntology(null);
	}

	public FlexoOntology getOntology() {
		return ontology;
	}

	public void setOntology(FlexoOntology ontology) {
		this.ontology = ontology;
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
			super(getOntology() != null ? getOntology().getProject() : null, false);
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
			if (getOntology() != null) {
				return getOntology().getRootClass();
			}
			return null;
		}
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

	@Override
	public FlexoModelObject getRootObject() {
		if (super.getRootObject() != null) {
			return super.getRootObject();
		} else if (getOntology() != null) {
			return getOntology().getRootClass();
		}
		return null;
	}

	public OntologyClass getParentClass() {
		if (getRootObject() instanceof OntologyClass) {
			return (OntologyClass) getRootObject();
		}
		return null;
	}

	public void setParentClass(OntologyClass aClass) {
		super.setRootObject(aClass);
	}

	public String getParentClassURI() {
		if (getParentClass() != null) {
			return getParentClass().getURI();
		}
		return null;
	}

	public void setParentClassURI(String aParentClassURI) {
		if (getOntology() != null) {
			OntologyClass ontologyClass = getOntology().getClass(aParentClassURI);
			if (ontologyClass != null) {
				setParentClass(ontologyClass);
			}
		}
	}

	@Override
	public void setProject(FlexoProject project) {
		super.setProject(project);
		if (project != null && getOntology() == null) {
			setOntology(project.getProjectOntology());
		}
	}

}
