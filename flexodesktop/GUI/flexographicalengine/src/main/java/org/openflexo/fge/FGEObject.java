package org.openflexo.fge;

import org.openflexo.inspector.InspectableObject;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.xmlcode.XMLSerializable;

public interface FGEObject extends /*AccessibleProxyObject,*/XMLSerializable, Cloneable, IObservable, KeyValueCoding, FGEConstants,
		InspectableObject {

	public FGEModelFactory getFactory();

	public void setFactory(FGEModelFactory factory);
}
