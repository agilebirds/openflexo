package org.openflexo.foundation.imported;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.factory.AccessibleProxyObject;

public interface ImportedObject extends AccessibleProxyObject {

	public static final String NAME = "name";

	@Getter(NAME)
	public String getName();

	@Setter(NAME)
	public void setName(String name);
}
