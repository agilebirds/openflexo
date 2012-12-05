package org.flexo.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement
public interface MyNode extends AbstractNode {

	public static final String MY_PROPERTY = "myProperty";

	@Getter(MY_PROPERTY)
	@XMLAttribute
	public String getMyProperty();

	@Setter(MY_PROPERTY)
	public void setMyProperty(String property);
}
