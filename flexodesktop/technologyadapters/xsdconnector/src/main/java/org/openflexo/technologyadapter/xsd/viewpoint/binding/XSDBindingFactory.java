package org.openflexo.technologyadapter.xsd.viewpoint.binding;

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
import org.openflexo.technologyadapter.xsd.model.XSOntAttributeRestriction;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XSOntDataProperty;
/**
 * This class represent the {@link BindingFactory} dedicated to handle XSD technology-specific binding elements
 * 
 * @author sylvain
 * 
 */
public final class XSDBindingFactory extends TechnologyAdapterBindingFactory {
	static final Logger logger = Logger.getLogger(XSDBindingFactory.class.getPackage().getName());

	public XSDBindingFactory() {
		super();
	}

	@Override
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		if (object instanceof XSOntAttributeRestriction ){
			XSOntAttributeRestriction attr = (XSOntAttributeRestriction) object;

			return new AttributeDataPropertyFeatureAssociationPathElement(parent, attr,
					(XSOntDataProperty) attr.getFeature());
		}
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public boolean handleType(TechnologySpecificCustomType technologySpecificType) {
		if ((technologySpecificType instanceof IndividualOfClass)
				&& ((IndividualOfClass) technologySpecificType).getOntologyClass() instanceof XSOntClass) {
			return true;
		}
		if ((technologySpecificType instanceof SubClassOfClass)
				&& ((SubClassOfClass) technologySpecificType).getOntologyClass() instanceof XSOntClass) {
			return true;
		}
		if ((technologySpecificType instanceof SubPropertyOfProperty)
		/*&& ((SubPropertyOfProperty) technologySpecificType).getOntologyProperty() instanceof*/) {
			return true;
		}
		return false;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {if (parent.getType() instanceof IndividualOfClass) {
		IndividualOfClass parentType = (IndividualOfClass) parent.getType();
		List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
		if (parentType.getOntologyClass() instanceof XSOntClass) {
			for (IFlexoOntologyFeatureAssociation fa : ((XSOntClass) parentType.getOntologyClass()).getFeatureAssociations()) {
				returned.add(getSimplePathElement(fa, parent));
			}
		}
		return returned;
	}

	return super.getAccessibleSimplePathElements(parent);
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		return Collections.emptyList();
	}

}