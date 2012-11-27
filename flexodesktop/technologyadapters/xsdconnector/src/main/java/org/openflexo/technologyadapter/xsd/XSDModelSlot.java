package org.openflexo.technologyadapter.xsd;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;

/**
 * Implementation of the ModelSlot class for the XSD/XML technology adapter
 * 
 * @author Luka Le Roux
 * 
 */
public class XSDModelSlot extends ModelSlotImpl<XMLModel, XSDMetaModel> {

	public XSDModelSlot(ViewPoint viewPoint) {
		super(viewPoint);
	}

	public XSDModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Deprecated
	@Override
	public String getFullyQualifiedName() {
		return "XSDModelSlot";
	}

	@Deprecated
	@Override
	public String getClassNameKey() {
		return "xsd_model_slot";
	}

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter() {
		return TechnologyAdapter.getTechnologyAdapter(XSDTechnologyAdapter.class);
	}

}
