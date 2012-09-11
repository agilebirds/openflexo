package org.flexo.model;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.ClosureCondition;
import org.openflexo.model.annotations.Deleter;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract = true)
public interface AbstractNode extends WKFObject {

	public static final String OUTGOING_EDGES = "outgoingEdges";
	public static final String INCOMING_EDGES = "incomingEdges";

	public static final String MASTER_ANNOTATION = "masterAnnotation";
	public static final String OTHER_ANNOTATIONS = "otherAnnotations";

	@Initializer
	public AbstractNode init(@Parameter(FlexoModelObject.NAME) String name);

	@Override
	@Deleter(embedded = { OUTGOING_EDGES, INCOMING_EDGES, MASTER_ANNOTATION, OTHER_ANNOTATIONS })
	public void delete();

	@Getter(value = OUTGOING_EDGES, cardinality = Cardinality.LIST, inverse = Edge.START_NODE)
	@XMLElement(context = "Outgoing", primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded({ @ClosureCondition(id = Edge.END_NODE) })
	public List<Edge> getOutgoingEdges();

	@Setter(OUTGOING_EDGES)
	public void setOutgoingEdges(List<Edge> edges);

	@Adder(OUTGOING_EDGES)
	public void addToOutgoingEdges(Edge edge);

	@Remover(OUTGOING_EDGES)
	public void removeFromOutgoingEdges(Edge edge);

	@Getter(value = INCOMING_EDGES, cardinality = Cardinality.LIST, inverse = Edge.END_NODE)
	@XMLElement(context = "Incoming")
	@Embedded({ @ClosureCondition(id = Edge.START_NODE) })
	@CloningStrategy(StrategyType.CLONE)
	public List<Edge> getIncomingEdges();

	@Setter(INCOMING_EDGES)
	public void setIncomingEdges(List<Edge> edges);

	@Adder(INCOMING_EDGES)
	public void addToIncomingEdges(Edge edge);

	@Remover(INCOMING_EDGES)
	public void removeFromIncomingEdges(Edge edge);

	@Getter(value = PROCESS, inverse = FlexoProcess.NODES)
	@Override
	public FlexoProcess getProcess();

	@Getter(MASTER_ANNOTATION)
	@XMLAttribute
	public WKFAnnotation getMasterAnnotation();

	@Setter(MASTER_ANNOTATION)
	public void setMasterAnnotation(WKFAnnotation a);

	@Getter(value = OTHER_ANNOTATIONS, cardinality = Cardinality.LIST)
	@XMLElement()
	public List<WKFAnnotation> getOtherAnnotations();

	@Setter(OTHER_ANNOTATIONS)
	public void setOtherAnnotations(List<WKFAnnotation> as);

	@Adder(OTHER_ANNOTATIONS)
	public void addToOtherAnnotations(WKFAnnotation a);

	@Remover(OTHER_ANNOTATIONS)
	public void removeFromOtherAnnotations(WKFAnnotation a);
}
