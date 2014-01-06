package org.openflexo.technologyadapter.emf.viewpoint.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeAssociation;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeObjectProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceAssociation;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;

/**
 * This class represent the {@link BindingFactory} dedicated to handle EMF technology-specific binding elements
 * 
 * @author sylvain
 * 
 */
public final class EMFBindingFactory extends TechnologyAdapterBindingFactory {
	static final Logger logger = Logger.getLogger(EMFBindingFactory.class.getPackage().getName());

	public EMFBindingFactory() {
		super();
	}

	@Override
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		if (object instanceof EMFAttributeAssociation) {
			if (((EMFAttributeAssociation) object).getFeature() instanceof EMFAttributeDataProperty) {
				return new AttributeDataPropertyFeatureAssociationPathElement(parent, (EMFAttributeAssociation) object,
						(EMFAttributeDataProperty) ((EMFAttributeAssociation) object).getFeature());
			} else if (((EMFAttributeAssociation) object).getFeature() instanceof EMFAttributeObjectProperty) {
				return new AttributeObjectPropertyFeatureAssociationPathElement(parent, (EMFAttributeAssociation) object,
						(EMFAttributeObjectProperty) ((EMFAttributeAssociation) object).getFeature());
			}
		} else if (object instanceof EMFReferenceAssociation
				&& ((EMFReferenceAssociation) object).getFeature() instanceof EMFReferenceObjectProperty) {
			return new ObjectReferenceFeatureAssociationPathElement(parent, (EMFReferenceAssociation) object,
					(EMFReferenceObjectProperty) ((EMFReferenceAssociation) object).getFeature());
		}
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public boolean handleType(TechnologySpecificCustomType technologySpecificType) {
		if ((technologySpecificType instanceof IndividualOfClass)
				&& ((IndividualOfClass) technologySpecificType).getOntologyClass() instanceof EMFClassClass) {
			return true;
		}
		if ((technologySpecificType instanceof SubClassOfClass)
				&& ((SubClassOfClass) technologySpecificType).getOntologyClass() instanceof EMFClassClass) {
			return true;
		}
		if ((technologySpecificType instanceof SubPropertyOfProperty)
		/*&& ((SubPropertyOfProperty) technologySpecificType).getOntologyProperty() instanceof*/) {
			return true;
		}
		return false;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		if (parent.getType() instanceof IndividualOfClass) {
			IndividualOfClass parentType = (IndividualOfClass) parent.getType();
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			if (parentType.getOntologyClass() instanceof EMFClassClass) {
				for (IFlexoOntologyFeatureAssociation fa : ((EMFClassClass) parentType.getOntologyClass())
						.getStructuralFeatureAssociations()) {
					returned.add(getSimplePathElement(fa, parent));
				}
			}
			return returned;
		}

		return super.getAccessibleSimplePathElements(parent);
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		// TODO: implements same as above, with behavioural features
		return Collections.emptyList();
	}

}