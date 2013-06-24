/**
 * 
 */
package org.openflexo.technologyadapter.xml.model;

import java.util.List;

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyFeature;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

/**
 * @author xtof
 *
 */
public class XMLAttribute implements IFlexoOntologyStructuralProperty {

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyFeature#getReferencingFeatureAssociations()
	 */
	@Override
	public List<? extends IFlexoOntologyFeatureAssociation> getReferencingFeatureAssociations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getOntology()
	 */
	@Override
	public IFlexoOntology getOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getAnnotations()
	 */
	@Override
	public List<? extends IFlexoOntologyAnnotation> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getContainer()
	 */
	@Override
	public IFlexoOntologyConceptContainer getContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getStructuralFeatureAssociations()
	 */
	@Override
	public List<? extends IFlexoOntologyFeatureAssociation> getStructuralFeatureAssociations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getBehaviouralFeatureAssociations()
	 */
	@Override
	public List<? extends IFlexoOntologyFeatureAssociation> getBehaviouralFeatureAssociations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#isSuperConceptOf(org.openflexo.foundation.ontology.IFlexoOntologyConcept)
	 */
	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#isSubConceptOf(org.openflexo.foundation.ontology.IFlexoOntologyConcept)
	 */
	@Override
	public boolean isSubConceptOf(IFlexoOntologyConcept concept) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#accept(org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor)
	 */
	@Override
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#equalsToConcept(org.openflexo.foundation.ontology.IFlexoOntologyConcept)
	 */
	@Override
	public boolean equalsToConcept(IFlexoOntologyConcept concept) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getPropertiesTakingMySelfAsRange()
	 */
	@Override
	public List<? extends IFlexoOntologyStructuralProperty> getPropertiesTakingMySelfAsRange() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getPropertiesTakingMySelfAsDomain()
	 */
	@Override
	public List<? extends IFlexoOntologyFeature> getPropertiesTakingMySelfAsDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getURI()
	 */
	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getTechnologyAdapter()
	 */
	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty#getDomain()
	 */
	@Override
	public IFlexoOntologyConcept getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty#getRange()
	 */
	@Override
	public IFlexoOntologyObject getRange() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty#isAnnotationProperty()
	 */
	@Override
	public boolean isAnnotationProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty#getSuperProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyStructuralProperty> getSuperProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty#getSubProperties(org.openflexo.foundation.ontology.IFlexoOntology)
	 */
	@Override
	public List<? extends IFlexoOntologyStructuralProperty> getSubProperties(
			IFlexoOntology context) {
		// TODO Auto-generated method stub
		return null;
	}

}
