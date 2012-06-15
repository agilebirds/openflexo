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

	private FlexoOntology ontology;
	private OntologyClass domainClass;
	private OntologyClass rangeClass;

	public OntologyPropertySelector(OntologyProperty object) {
		super(null, object, OntologyProperty.class);
	}

	public OntologyPropertySelector(FlexoOntology ontology, OntologyProperty object, int cols) {
		super(null, object, OntologyProperty.class, cols);
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
			super(getOntology() != null ? getOntology().getProject() : null, false);
			init();
		}

		@Override
		public void configure() {
			if (hierarchicalMode) {
				setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.HIDE, true);
				setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.HIDE, true);
				setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);
				setOEViewMode(OEViewMode.FullHierarchy);
			} else {
				setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.SHOW);
				setFilterStatus(BrowserElementType.ONTOLOGY_STATEMENT, BrowserFilterStatus.HIDE);
				setOEViewMode(OEViewMode.PartialHierarchy);
			}
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getOntology();
		}

	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

	@Override
	public void setProject(FlexoProject project) {
		super.setProject(project);
		if (project != null && getOntology() == null) {
			setOntology(project.getProjectOntology());
		}
	}

	private boolean hierarchicalMode = true;

	public boolean getHierarchicalMode() {
		return hierarchicalMode;
	}

	public void setHierarchicalMode(boolean hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
	}

	@Override
	public FlexoModelObject getRootObject() {
		if (super.getRootObject() != null) {
			return super.getRootObject();
		} else if (getOntology() != null) {
			if (hierarchicalMode) {
				return getOntology().getRootClass();
			} else {
				return getOntology();
			}
		}
		return null;
	}

	public OntologyClass getDomainClass() {
		/*if (getRootObject() instanceof OntologyClass) {
			return (OntologyClass) getRootObject();
		}*/
		return domainClass;
	}

	public void setDomainClass(OntologyClass aClass) {
		this.domainClass = aClass;
		// super.setRootObject(aClass);
	}

	public OntologyClass getRangeClass() {
		/*if (getRootObject() instanceof OntologyClass) {
			return (OntologyClass) getRootObject();
		}*/
		return rangeClass;
	}

	public void setRangeClass(OntologyClass aClass) {
		this.rangeClass = aClass;
		// super.setRootObject(aClass);
	}

	public String getDomainClassURI() {
		if (getDomainClass() != null) {
			return getDomainClass().getURI();
		}
		return null;
	}

	public void setDomainClassURI(String aDomainClassURI) {
		if (getOntology() != null) {
			OntologyClass ontologyClass = getOntology().getClass(aDomainClassURI);
			if (ontologyClass != null) {
				setDomainClass(ontologyClass);
			}
		}
	}

	@Override
	public boolean isSelectable(FlexoModelObject object) {
		if (super.isSelectable(object)) {
			OntologyProperty property = (OntologyProperty) object;
			if (getDomainClass() != null && property.getDomain() != null) {
				return property.getDomain().isSuperConceptOf(getDomainClass());
			}
			return true;
		}
		return false;
	}

	@Override
	public void openPopup() {
		super.openPopup();
		if (domainClass != null) {
			getSelectorPanel().getBrowser().expand(domainClass, true);
		}
	}
}
