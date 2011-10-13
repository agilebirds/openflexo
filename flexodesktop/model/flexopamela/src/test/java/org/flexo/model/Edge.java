package org.flexo.model;

import org.flexo.model.impl.EdgeImpl;
import org.openflexo.model.annotations.Constructor;
import org.openflexo.model.annotations.Constructors;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.ReturnedValue;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract=true)
@ImplementationClass(EdgeImpl.class)
@Constructors({
	@Constructor({
		@Parameter(name=FlexoModelObject.NAME,type=String.class),
		@Parameter(name=Edge.START_NODE,type=AbstractNode.class),
		@Parameter(name=Edge.END_NODE,type=AbstractNode.class)}),
	@Constructor({
		@Parameter(name=Edge.START_NODE,type=AbstractNode.class),
		@Parameter(name=Edge.END_NODE,type=AbstractNode.class)})
})
public interface Edge extends WKFObject {

	public static final String START_NODE = "startNode";
	public static final String END_NODE = "endNode";

	@Getter(id=PROCESS)
	@ReturnedValue("startNode.process")
	public FlexoProcess getProcess();

	@Getter(id=START_NODE,inverse=AbstractNode.OUTGOING_EDGES)
	@XMLElement(context="Start")
	public AbstractNode getStartNode();
	
	@Setter(id=START_NODE)
	public void setStartNode(AbstractNode node);

	@Getter(id=END_NODE,inverse=AbstractNode.INCOMING_EDGES)
	@XMLElement(context="End")
	public AbstractNode getEndNode();
	
	@Setter(id=END_NODE)
	public void setEndNode(AbstractNode node);
}
