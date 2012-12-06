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
package org.openflexo.technologyadapter.owl.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.owl.model.OWL2URIDefinitions;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OntologyRestrictionClass;
import org.openflexo.technologyadapter.owl.model.RDFSURIDefinitions;
import org.openflexo.technologyadapter.owl.model.RDFURIDefinitions;
import org.openflexo.toolbox.StringUtils;

/**
 * Model supporting browsing through models or metamodels conform to {@link FlexoOntology} API<br>
 * 
 * Developers note: this model is shared by many widgets. Please modify it with caution.
 * 
 * @see FIBOWLClassSelector
 * @see FIBOWLIndividualSelector
 * @see FIBOWLPropertySelector
 * 
 * @author sguerin
 */
public class OWLOntologyBrowserModel extends OntologyBrowserModel {

	static final Logger logger = Logger.getLogger(OWLOntologyBrowserModel.class.getPackage().getName());

	private boolean showOWLAndRDFConcepts = false;

	public OWLOntologyBrowserModel(OWLOntology context) {
		super(context);
	}

	@Override
	public boolean isDisplayable(IFlexoOntologyObject object) {

		if (object instanceof OWLOntology) {
			if ((object == ((OWLOntology) object).getOntologyLibrary().getRDFOntology()
					|| object == ((OWLOntology) object).getOntologyLibrary().getRDFSOntology() || object == ((OWLOntology) object)
					.getOntologyLibrary().getOWLOntology()) && object != getContext()) {
				return getShowOWLAndRDFConcepts();
			}
			return true;
		}
		if (!getShowOWLAndRDFConcepts() && StringUtils.isNotEmpty(object.getURI()) && object instanceof IFlexoOntologyConcept
				&& ((IFlexoOntologyConcept) object).getOntology() != getContext()) {
			if (object.getURI().startsWith(RDFURIDefinitions.RDF_ONTOLOGY_URI)
					|| object.getURI().startsWith(RDFSURIDefinitions.RDFS_ONTOLOGY_URI)
					|| object.getURI().startsWith(OWL2URIDefinitions.OWL_ONTOLOGY_URI)) {
				return false;
			}
		}

		return super.isDisplayable(object);
	}

	/**
	 * Compute a list of preferred location for an ontology property to be displayed.<br>
	 * If searchedOntology is not null, restrict returned list to classes declared in supplied ontology
	 * 
	 * @param p
	 * @param searchedOntology
	 * @return
	 */
	@Override
	protected List<IFlexoOntologyClass> getPreferredStorageLocations(IFlexoOntologyStructuralProperty p, IFlexoOntology searchedOntology) {
		List<IFlexoOntologyClass> potentialStorageClasses = super.getPreferredStorageLocations(p, searchedOntology);

		for (IFlexoOntologyClass c : getContext().getAccessibleClasses()) {
			if (c.isNamedClass()) {
				for (IFlexoOntologyClass superClass : c.getSuperClasses()) {
					if (superClass instanceof OntologyRestrictionClass
							&& ((OntologyRestrictionClass) superClass).getProperty().equalsToConcept(p)) {
						if (searchedOntology == null || c.getOntology() == searchedOntology) {
							potentialStorageClasses.add(c);
						}
					}
				}
			}
		}

		return potentialStorageClasses;

		/*if (potentialStorageClasses.size() > 0) {
			return potentialStorageClasses.get(0);
		}*/

		/*if (p.getStorageLocations().size() > 0) {
			return p.getStorageLocations().get(0);
		}*/
		// return null;
	}

	/**
	 * Remove originals from redefined classes<br>
	 * Special case: original Thing definition is kept and redefinitions are excluded
	 * 
	 * @param list
	 */
	@Override
	protected void removeOriginalFromRedefinedObjects(List<? extends IFlexoOntologyConcept> list) {
		for (IFlexoOntologyConcept c : new ArrayList<IFlexoOntologyConcept>(list)) {
			if (c instanceof OWLObject<?> && ((OWLObject<?>) c).redefinesOriginalDefinition()) {
				list.remove(((OWLObject<?>) c).getOriginalDefinition());
			}
			if (c instanceof OWLClass && ((OWLClass) c).isThing() && c.getOntology() != getContext()
					&& list.contains(getContext().getRootConcept())) {
				list.remove(c);
			}
		}
	}

	public boolean getShowOWLAndRDFConcepts() {
		return showOWLAndRDFConcepts;
	}

	public void setShowOWLAndRDFConcepts(boolean showOWLAndRDFConcepts) {
		this.showOWLAndRDFConcepts = showOWLAndRDFConcepts;
	}

}
