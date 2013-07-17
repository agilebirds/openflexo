package org.openflexo.fge;

import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.xmlcode.XMLSerializable;

public interface FGEObject extends /*AccessibleProxyObject,*/XMLSerializable, Cloneable, IObservable, KeyValueCoding, FGEConstants {

	public FGEModelFactory getFactory();

	public void setFactory(FGEModelFactory factory);
}
