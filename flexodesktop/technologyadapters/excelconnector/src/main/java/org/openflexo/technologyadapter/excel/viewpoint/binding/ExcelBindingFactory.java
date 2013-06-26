package org.openflexo.technologyadapter.excel.viewpoint.binding;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;

/**
 * This class represent the {@link BindingFactory} dedicated to handle Excel technology-specific binding elements
 * 
 * @author sylvain
 * 
 */
public final class ExcelBindingFactory extends TechnologyAdapterBindingFactory {
	static final Logger logger = Logger.getLogger(ExcelBindingFactory.class.getPackage().getName());

	public ExcelBindingFactory() {
		super();
	}

	@Override
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public boolean handleType(TechnologySpecificCustomType technologySpecificType) {
		if ((technologySpecificType instanceof ExcelSheetType)) {
			return true;
		}
		return false;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		/*if (object instanceof EMFAttributeAssociation) {
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
		logger.warning("Unexpected " + object);*/
		return null;
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		// TODO
		return Collections.emptyList();
	}

}