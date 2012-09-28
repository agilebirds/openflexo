package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.ontology.xsd.XSOntology;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.view.View;
import org.openflexo.xmlcode.XMLMapping;

/**
 * <p>
 * Implementation of the ModelSlot class for the XSD technoligical space.
 * 
 * @author Luka Le Roux
 * 
 */
public class XSModelSlot extends AbstractModelSlot<XSOntology> {

	@Override
	public TechnologicalSpace getTechnologicalSpace() {
		return TechnologicalSpace.XSD;
	}

	@Override
	public XSOntology createEmptyModel(View view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassNameKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLMapping getXMLMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		// TODO Auto-generated method stub
		return null;
	}

}
