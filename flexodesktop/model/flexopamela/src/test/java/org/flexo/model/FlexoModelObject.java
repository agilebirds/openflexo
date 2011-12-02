package org.flexo.model;

import org.flexo.model.impl.FlexoModelObjectImpl;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Deleter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.ObservableObject;

@ModelEntity(isAbstract = true)
@ImplementationClass(FlexoModelObjectImpl.class)
public interface FlexoModelObject extends AccessibleProxyObject, ObservableObject, CloneableProxyObject {

	public static final String FLEXO_ID = "flexoId";
	public static final String NAME = "name";
	public static final String DELETED = "deleted";

	// @XMLProperty(id="flexoId",kind=Kind.GETTER,xml="flexoID",defaultValue="00000")

	@Getter(value = FLEXO_ID, defaultValue = "0000")
	@XMLAttribute(xmlTag = FLEXO_ID)
	public String getFlexoID();

	// @XMLProperty(id="flexoId",kind=Kind.SETTER)
	@Setter(value = FLEXO_ID)
	public void setFlexoID(String flexoID);

	@Getter(value = NAME, defaultValue = "???")
	@XMLAttribute(xmlTag = NAME)
	@CloningStrategy(value = StrategyType.FACTORY, factory = "deriveName()")
	public String getName();

	@Setter(value = NAME)
	public void setName(String name);

	public String deriveName();

	@Deleter(deletedProperty = DELETED)
	public void delete();

	@Getter(value = DELETED, defaultValue = "false")
	public boolean isDeleted();

	/*@XMLProperty(xml="lastUpdateDate")
	public Date getLastUpdateDate();
	public void setLastUpdateDate(Date lastUpdateDate);

	@XMLRelationship(xml="father",target=FlexoModelObject.class, cardinality=Cardinality.MANY_TO_ONE, reverseMethodName="children")
	public FlexoModelObject getFather();
	public void setFather(FlexoModelObject father);

	@XMLRelationship(xml="children",target=FlexoModelObject.class, cardinality=Cardinality.ONE_TO_MANY, reverseMethodName="father")
	public List<FlexoModelObject> getChildren();
	public void setChildren();*/

}
