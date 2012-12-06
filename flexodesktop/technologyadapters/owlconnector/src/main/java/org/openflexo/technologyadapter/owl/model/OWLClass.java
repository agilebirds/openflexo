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
package org.openflexo.technologyadapter.owl.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.toolbox.StringUtils;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;

public class OWLClass extends OWLConcept<OntClass> implements IFlexoOntologyClass, Comparable<IFlexoOntologyClass> {

	private static final Logger logger = Logger.getLogger(IFlexoOntologyClass.class.getPackage().getName());

	private OntClass ontClass;

	private final Vector<OWLClass> superClasses;

	private final List<OWLRestriction> restrictions = new ArrayList<OWLRestriction>();

	protected OWLClass(OntClass anOntClass, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(anOntClass, ontology, adapter);
		superClasses = new Vector<OWLClass>();
		ontClass = anOntClass;
	}

	/**
	 * Init this IFlexoOntologyClass, given base OntClass
	 */
	protected void init() {
		updateOntologyStatements(ontClass);
		updateSuperClasses(ontClass);
	}

	@Override
	public void delete() {
		getFlexoOntology().removeClass(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
	}

	/**
	 * Update this IFlexoOntologyClass, given base OntClass
	 */
	@Override
	protected void update() {
		update(ontClass);
	}

	/**
	 * Update this IFlexoOntologyClass, given base OntClass which is assumed to extends base OntClass
	 * 
	 * @param anOntClass
	 */
	protected void update(OntClass anOntClass) {
		updateOntologyStatements(anOntClass);
		updateSuperClasses(anOntClass);
		ontClass = anOntClass;
	}

	@Override
	public void setName(String aName) {
		renameURI(aName, ontClass, OntClass.class);
	}

	@Override
	protected void _setOntResource(OntClass r) {
		ontClass = r;
	}

	@Override
	public OWLClass getOriginalDefinition() {
		return (OWLClass) super.getOriginalDefinition();
	}

	private void appendToSuperClasses(OWLClass superClass) {
		if (getURI().equals(OWL2URIDefinitions.OWL_THING_URI)) {
			return;
		}
		if (superClass == this) {
			return;
		}
		if (superClass.redefinesOriginalDefinition()) {
			if (superClasses.contains(superClass.getOriginalDefinition())) {
				superClasses.remove(superClass.getOriginalDefinition());
			}
		}
		if (!superClasses.contains(superClass)) {
			superClasses.add(superClass);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Add " + superClass.getName() + " as a super class of " + getName());
			}
		}
	}

	private void updateSuperClasses(OntClass anOntClass) {
		superClasses.clear();
		if (redefinesOriginalDefinition()) {
			for (OWLClass c : getOriginalDefinition().getSuperClasses()) {
				if (!c.isRootConcept()) {
					appendToSuperClasses(c);
				}
			}
			// superClasses.addAll(getOriginalDefinition().getSuperClasses());
		}
		// logger.info("updateSuperClasses for " + getURI());

		Iterator it = anOntClass.listSuperClasses(true);
		while (it.hasNext()) {
			OntClass father = (OntClass) it.next();
			OWLClass fatherClass = getOntology().retrieveOntologyClass(father);// getOntologyLibrary().getClass(father.getURI());
			if (fatherClass != null) {
				appendToSuperClasses(fatherClass);
			}
		}

		// If this class is equivalent to the intersection of some other classes, then all those operand classes are super classes of this
		// class
		if (getEquivalentClass() instanceof OWLIntersectionClass) {
			for (OWLClass operand : ((OWLIntersectionClass) getEquivalentClass()).getOperands()) {
				appendToSuperClasses(operand);
			}
		}

		// If computed ontology is either not RDF, nor RDFS, nor OWL
		// add OWL Thing as parent
		if (getFlexoOntology() != getOntologyLibrary().getRDFOntology() && getFlexoOntology() != getOntologyLibrary().getRDFSOntology()) {
			if (isNamedClass() && !isRootConcept()) {
				OWLClass THING_CLASS = getOntology().getRootClass();
				appendToSuperClasses(THING_CLASS);
			}
		}
	}

	@Deprecated
	@Override
	public String getClassNameKey() {
		return "ontology_class";
	}

	@Override
	public String getFullyQualifiedName() {
		return "IFlexoOntologyClass:" + getURI();
	}

	public static final Comparator<IFlexoOntologyClass> COMPARATOR = new Comparator<IFlexoOntologyClass>() {
		@Override
		public int compare(IFlexoOntologyClass o1, IFlexoOntologyClass o2) {
			return Collator.getInstance().compare(o1.getName(), o2.getName());
		}
	};

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_CLASS_READ_ONLY_INSPECTOR; // read-only
		} else {
			return Inspectors.VE.ONTOLOGY_CLASS_INSPECTOR;
		}
	}

	@Override
	public int compareTo(IFlexoOntologyClass o) {
		return COMPARATOR.compare(this, o);
	}

	@Override
	public OntClass getOntResource() {
		return ontClass;
	}

	private static boolean isSuperClassOf(OntClass parentClass, OntClass subClass) {
		if (parentClass == null) {
			return false;
		}
		if (subClass == null) {
			return false;
		}
		if (parentClass.equals(subClass)) {
			return true;
		}
		Iterator it = subClass.listSuperClasses();
		while (it.hasNext()) {
			OntClass p = (OntClass) it.next();
			if (p.equals(parentClass)) {
				return true;
			}
			if (isSuperClassOf(parentClass, p)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isSuperClassOf(OntClass parentClass, Individual individual) {
		if (parentClass == null) {
			return false;
		}
		if (individual == null) {
			return false;
		}
		Iterator it = individual.listOntClasses(false);
		while (it.hasNext()) {
			OntClass p = (OntClass) it.next();
			if (p.equals(parentClass)) {
				return true;
			}
			if (isSuperClassOf(parentClass, p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		if (OWL2URIDefinitions.OWL_THING_URI.equals(getURI())) {
			return true;
		}
		if (concept instanceof OWLIndividual) {
			OWLIndividual ontologyIndividual = (OWLIndividual) concept;
			// Doesn't work, i dont know why
			// return ontologyIndividual.getIndividual().hasOntClass(ontClass);
			return isSuperClassOf(ontClass, ontologyIndividual.getIndividual());
		}
		if (concept instanceof OWLClass) {
			OWLClass ontologyClass = (OWLClass) concept;
			// Doesn't work, i dont know why
			// return ontologyClass.getOntResource().hasSuperClass(ontClass);
			// return isSuperClassOf(ontClass, ontologyClass.getOntResource());
			return isSuperClassOf(ontologyClass);
		}
		return false;
	}

	@Override
	public boolean isSuperClassOf(IFlexoOntologyClass aClass) {
		if (aClass == this) {
			return true;
		}
		if (equalsToConcept(aClass)) {
			return true;
		}
		if (OWL2URIDefinitions.OWL_THING_URI.equals(getURI())) {
			return true;
		}
		if (aClass instanceof OWLClass) {
			for (OWLClass c : ((OWLClass) aClass).getSuperClasses()) {
				if (isSuperClassOf(c)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return all direct super classes of this class
	 * 
	 * @return
	 */
	@Override
	public Vector<OWLClass> getSuperClasses() {
		return superClasses;
	}

	/**
	 * Return all direct and infered super classes of this class
	 * 
	 * @return
	 */
	public List<OWLClass> getAllSuperClasses() {
		ArrayList<OWLClass> allSuperClasses = new ArrayList<OWLClass>();
		for (IFlexoOntologyClass cl : OntologyUtils.getAllSuperClasses(this)) {
			if (cl instanceof OWLClass) {
				allSuperClasses.add((OWLClass) cl);
			}
		}
		return allSuperClasses;
	}

	/**
	 * Add super class to this class
	 * 
	 * @param aClass
	 */
	@Override
	public void addToSuperClasses(IFlexoOntologyClass father) {
		if (father instanceof OWLClass) {
			getOntResource().addSuperClass(((OWLClass) father).getOntResource());
			updateOntologyStatements();
			return;
		}
		if (father instanceof OWLRestriction) {
			restrictions.add((OWLRestriction) father);
		}
		logger.warning("Class " + father + " is not a OWLClass");
		return;
	}

	@Override
	public void removeFromSuperClasses(IFlexoOntologyClass aClass) {
		logger.warning("Not implemented");
	}

	/**
	 * Return a vector of Ontology class, as a subset of getSubClasses(), which correspond to all classes necessary to see all classes
	 * belonging to supplied context, which is an ontology
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<OWLClass> getSubClasses(IFlexoOntology context) {
		ArrayList<OWLClass> returned = new ArrayList<OWLClass>();
		for (IFlexoOntologyClass aClass : context.getAccessibleClasses()) {
			if (aClass instanceof OWLClass && isSuperClassOf(aClass)) {
				returned.add((OWLClass) aClass);
			}
		}
		return returned;
	}

	/*private boolean isRequired(IFlexoOntologyClass aClass, IFlexoOntology context) {
		if (aClass.getFlexoOntology() == context) {
			return true;
		}
		for (IFlexoOntologyClass aSubClass : aClass.getSubClasses()) {
			if (isRequired(aSubClass, context)) {
				return true;
			}
		}
		for (IFlexoOntologyIndividual anIndividual : aClass.getIndividuals()) {
			if (anIndividual.getFlexoOntology() == context) {
				return true;
			}
		}
		return false;
	}*/

	@Override
	public String getDisplayableDescription() {
		/*String extendsLabel = " extends ";
		boolean isFirst = true;
		for (IFlexoOntologyClass s : superClasses) {
			extendsLabel += (isFirst ? "" : ",") + s.getName();
			isFirst = false;
		}
		return "Class " + getName() + extendsLabel;*/
		return getName();
	}

	@Override
	public boolean isOntologyClass() {
		return true;
	}

	@Override
	protected void recursivelySearchRangeAndDomains() {
		super.recursivelySearchRangeAndDomains();
		for (OWLClass aClass : getSuperClasses()) {
			propertiesTakingMySelfAsRange.addAll(aClass.getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(aClass.getPropertiesTakingMySelfAsDomain());
		}
		OWLClass CLASS_CONCEPT = getOntology().getClass(OWL_CLASS_URI);
		// CLASS_CONCEPT is generally non null but can be null when reading RDFS for exampel
		if (CLASS_CONCEPT != null) {
			propertiesTakingMySelfAsRange.addAll(CLASS_CONCEPT.getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(CLASS_CONCEPT.getPropertiesTakingMySelfAsDomain());
		}

		/*Vector<IFlexoOntologyClass> alreadyComputed = new Vector<IFlexoOntologyClass>();
		if (redefinesOriginalDefinition()) {
			_appendRangeAndDomains(getOriginalDefinition(), alreadyComputed);
		}
		for (IFlexoOntologyClass aClass : getSuperClasses()) {
			_appendRangeAndDomains(aClass, alreadyComputed);
		}*/
	}

	/*private void _appendRangeAndDomains(IFlexoOntologyClass superClass, Vector<IFlexoOntologyClass> alreadyComputed) {
		if (alreadyComputed.contains(superClass)) {
			return;
		}
		alreadyComputed.add(superClass);
		for (IFlexoOntologyStructuralProperty p : superClass.getDeclaredPropertiesTakingMySelfAsDomain()) {
			if (!propertiesTakingMySelfAsDomain.contains(p)) {
				propertiesTakingMySelfAsDomain.add(p);
			}
		}
		for (IFlexoOntologyStructuralProperty p : superClass.getDeclaredPropertiesTakingMySelfAsRange()) {
			if (!propertiesTakingMySelfAsRange.contains(p)) {
				propertiesTakingMySelfAsRange.add(p);
			}
		}
		for (IFlexoOntologyClass superSuperClass : superClass.getSuperClasses()) {
			_appendRangeAndDomains(superSuperClass, alreadyComputed);
		}
	}*/

	private IFlexoOntologyClass equivalentClass;
	private List<IFlexoOntologyClass> equivalentClasses = new ArrayList<IFlexoOntologyClass>();

	@Override
	public void updateOntologyStatements(OntClass anOntResource) {
		super.updateOntologyStatements(anOntResource);
		equivalentClasses.clear();
		for (OWLStatement s : getSemanticStatements()) {
			if (s instanceof EquivalentClassStatement) {
				if (((EquivalentClassStatement) s).getEquivalentObject() instanceof IFlexoOntologyClass) {
					equivalentClass = (IFlexoOntologyClass) ((EquivalentClassStatement) s).getEquivalentObject();
					equivalentClasses.add(equivalentClass);
				}
			}
		}
	}

	/**
	 * Return equivalent class, asserting there is only one equivalent class statement
	 * 
	 * @return
	 */
	public IFlexoOntologyClass getEquivalentClass() {
		return equivalentClass;
	}

	/**
	 * Return all restrictions related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public List<OWLRestriction> getRestrictions(IFlexoOntologyStructuralProperty property) {
		List<OWLRestriction> returned = new ArrayList<OWLRestriction>();
		for (IFlexoOntologyClass c : getSuperClasses()) {
			if (c instanceof OWLRestriction) {
				OWLRestriction r = (OWLRestriction) c;
				if (r.getProperty() == property) {
					returned.add(r);
				}
			}
		}
		return returned;
	}

	@Override
	public String getHTMLDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("Class <b>" + getName() + "</b><br>");
		sb.append("<i>" + getURI() + "</i><br>");
		sb.append("<b>Asserted in:</b> " + getOntology().getURI() + "<br>");
		if (redefinesOriginalDefinition()) {
			sb.append("<b>Redefines:</b> " + getOriginalDefinition() + "<br>");
		}
		sb.append("<b>Superclasses:</b>");
		for (OWLClass c : getSuperClasses()) {
			sb.append(" " + c.getDisplayableDescription());
		}
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * Indicates if this class represents a named class
	 */
	@Override
	public boolean isNamedClass() {
		return StringUtils.isNotEmpty(getURI());
	}

	/**
	 * Indicates if this class represents the Thing root concept
	 */
	@Override
	public boolean isRootConcept() {
		return isNamedClass() && getURI().equals(OWL2URIDefinitions.OWL_THING_URI);
	}

	@Override
	public List<OWLRestriction> getFeatureAssociations() {
		return getRestrictions();
	}

	public List<OWLRestriction> getRestrictions() {
		return restrictions;
	}

}
