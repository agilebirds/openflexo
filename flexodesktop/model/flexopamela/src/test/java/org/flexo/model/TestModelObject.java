package org.flexo.model;

import org.flexo.model.impl.FlexoModelObjectImpl;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.DeletableProxyObject;

@ModelEntity(isAbstract = true)
@ImplementationClass(FlexoModelObjectImpl.class)
public interface TestModelObject extends AccessibleProxyObject, DeletableProxyObject, CloneableProxyObject {

	public static final String FLEXO_ID = "flexoId";
	public static final String NAME = "name";
	public static final String DELETED = "deleted";

	@Initializer
	public TestModelObject init();

	@Initializer
	public TestModelObject init(@Parameter(FLEXO_ID) String flexoId);

	// @XMLProperty(id="flexoId",kind=Kind.GETTER,xml="flexoID",defaultValue="00000")

	@Getter(value = FLEXO_ID, defaultValue = "0000")
	@XMLAttribute(xmlTag = FLEXO_ID)
	public String getFlexoID();

	// @XMLProperty(id="flexoId",kind=Kind.SETTER)
	@Setter(FLEXO_ID)
	public void setFlexoID(String flexoID);

	@Getter(value = NAME, defaultValue = "???")
	@XMLAttribute(xmlTag = NAME)
	// @CloningStrategy(StrategyType.FACTORY, factory = "deriveName()")
	@CloningStrategy(StrategyType.CLONE)
	public String getName();

	@Setter(NAME)
	public void setName(String name);

	public String deriveName();

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
