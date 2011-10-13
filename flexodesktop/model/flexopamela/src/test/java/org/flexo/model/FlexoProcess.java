package org.flexo.model;

import java.util.List;

import org.flexo.model.impl.FlexoProcessImpl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Deleter;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.IntegrityConstraint;
import org.openflexo.model.annotations.IntegrityConstraints;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.PastingPoints;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FlexoProcessImpl.class)
@XMLElement(xmlTag="FlexoProcess")
@PastingPoints({
	@PastingPoint(type=AbstractNode.class,id=FlexoProcess.NODES)
})
@IntegrityConstraints({
	@IntegrityConstraint("foo > 0"),
	@IntegrityConstraint("name != null")
})
@Imports({
	@Import(ActivityNode.class),
	@Import(StartNode.class),
	@Import(EndNode.class),
	@Import(TokenEdge.class)
})
public interface FlexoProcess extends WKFObject {

	public static final String FOO = "foo";
	public static final String NODES = "nodes";

	@Getter(id=FOO,defaultValue="4")
	@XMLAttribute(xmlTag=FOO)
	public int getFoo();

	@Setter(id=FOO)
	public void setFoo(int foo);

	@Getter(id=NODES,cardinality=Cardinality.LIST,inverse=WKFObject.PROCESS)
	@XMLElement(primary=true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<AbstractNode> getNodes();
	@Setter(id=NODES)
	public void setNodes(List<AbstractNode> nodes);
	@Adder(id=NODES)
	public void addToNodes(AbstractNode node);
	@Remover(id=NODES)
	public void removeFromNodes(AbstractNode node);

	@Finder(attribute = AbstractNode.NAME, collection = NODES)
	public AbstractNode getNodeNamed(String name);
	public Edge getEdgeNamed(String name);

	@Deleter
	public void delete();
}
