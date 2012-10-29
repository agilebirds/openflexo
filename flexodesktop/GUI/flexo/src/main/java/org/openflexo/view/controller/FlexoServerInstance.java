package org.openflexo.view.controller;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement
public interface FlexoServerInstance {

	public static final String URL = "url";
	public static final String WS_URL = "ws_url";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String USER_TYPES = "userTypes";
	public static final String OTHER_ID = "other";

	@Getter(value = ID)
	@XMLAttribute
	public String getID();

	@Setter(value = ID)
	public void setID(String id);

	@Getter(value = URL)
	@XMLAttribute
	public String getURL();

	@Setter(value = URL)
	public void setURL(String url);

	@Getter(value = WS_URL)
	@XMLAttribute
	public String getWSURL();

	@Setter(value = WS_URL)
	public void setWSURL(String url);

	@Getter(value = NAME)
	@XMLAttribute
	public String getName();

	@Setter(value = NAME)
	public void setName(String name);

	@Getter(value = USER_TYPES, cardinality = Cardinality.LIST)
	@XMLElement(xmlTag = "usertype")
	public List<String> getUserTypes();

	@Setter(value = USER_TYPES)
	public void setUserTypes(List<String> userTypes);

	@Adder(value = USER_TYPES)
	public void addToUserTypes(String userType);

	@Remover(value = USER_TYPES)
	public void removeFromUserTypes(String userType);
}
