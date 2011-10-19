/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.wkf.edge;

/*
 * FlexoPostCondition.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.foundation.wkf.MetricsValue.MetricsValueOwner;
import org.openflexo.foundation.wkf.MetricsValueAdded;
import org.openflexo.foundation.wkf.MetricsValueRemoved;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddEdgeMetricsValue;
import org.openflexo.foundation.wkf.action.DeleteMetricsValue;
import org.openflexo.foundation.wkf.dm.PostRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.ProgrammingLanguage;

/**
 * A FlexoPostCondition is a link (edge) between two AbstractNode, eventually
 * through a FlexoPreCondition, in the context of Petri graphs
 *
 * @author benoit, sylvain
 */
public abstract class FlexoPostCondition<S extends AbstractNode, E extends AbstractNode> extends WKFEdge<S,E> implements DeletableObject, LevelledObject, ExecutableWorkflowElement, MetricsValueOwner {

	private static final Logger logger = Logger.getLogger(FlexoPostCondition.class.getPackage().getName());

	public static final String EDGE_REPRESENTATION = "edgeRepresentation";
	public static final String CONDITION_PRIMITIVE = "conditionPrimitive";

	// ==========================================================================
	// ============================= Variables ==================================
	// ==========================================================================

	private Duration _delay;
	private Vector<MetricsValue> metricsValues;
	private boolean isGenericOutput = false;
	private AbstractBinding _conditionPrimitive;
	private boolean isConditional = false;
	private boolean isDefaultFlow = false;
	private String conditionDescription;

	public static FlexoActionizer<AddEdgeMetricsValue, FlexoPostCondition<?, ?>, WKFObject> addMetricsActionizer;
	public static FlexoActionizer<DeleteMetricsValue,MetricsValue,MetricsValue> deleteMetricsActionizer;

	// ==========================================================================
	// ============================= Constructor ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public FlexoPostCondition(FlexoProcess process)
	{
		super(process);
		metricsValues = new Vector<MetricsValue>();
	}

	public void setDefaultName()
	{
		setName(getDefaultName());
	}

	public String getDefaultName()
	{
		return FlexoLocalization.localizedForKey("no_name");
	}

	public boolean isStartingFromAnExclusiveGateway(){
		return getStartNode() instanceof OperatorNode && ((OperatorNode)getStartNode()).isExclusiveGateway();
	}

	@Override
	public Vector<MetricsValue> getMetricsValues() {
		return metricsValues;
	}

	public void setMetricsValues(Vector<MetricsValue> metricsValues) {
		this.metricsValues = metricsValues;
		setChanged();
	}

	@Override
	public void addToMetricsValues(MetricsValue value) {
		if (value.getMetricsDefinition()!=null) {
			metricsValues.add(value);
			value.setOwner(this);
			setChanged();
			notifyObservers(new MetricsValueAdded(value,"metricsValues"));
		}
	}

	@Override
	public void removeFromMetricsValues(MetricsValue value) {
		metricsValues.remove(value);
		value.setOwner(null);
		setChanged();
		notifyObservers(new MetricsValueRemoved(value,"metricsValues"));
	}

	@Override
	public void updateMetricsValues() {
		getWorkflow().updateMetricsForEdge(this);
	}

	public void addMetrics() {
		if (addMetricsActionizer!=null) {
			addMetricsActionizer.run(this, null);
		}
	}

	public void deleteMetrics(MetricsValue value) {
		if (deleteMetricsActionizer!=null) {
			deleteMetricsActionizer.run(value, null);
		}
	}

	/**
	 * Default token increment is 1 Parametered value is overriden in TokenEdge
	 *
	 * @return token increment as int
	 */
	public int getTokenIncrem()
	{
		return 1;
	}

	/**
	 * Remove itself from the datastructure. Mark itself has deleted. Set status
	 * to changed.
	 */
	@Override
	public final void delete() {
		logger.info("delete() in FlexoPostCondition for "+this);
		FlexoPreCondition pre = null;
		if(getEndNode() instanceof FlexoPreCondition){
			pre = (FlexoPreCondition)getEndNode();
		}
		super.delete();
		if (pre!=null && pre.getIncomingPostConditions().size()==0) {
			pre.delete();
		}
		setChanged();
		notifyObservers(new PostRemoved(this));
		deleteObservers();
	}

	@Override
	public void addOutgoingEdgeToStartNode(S startNode) {
		// ContextualEdgeStarting must take care of maintaining the start node of their outgoing edges
		if (!(startNode instanceof ContextualEdgeStarting)) {
			startNode.addToOutgoingPostConditions(this);
		}
	}

	@Override
	public void addIncomingEdgeToEndNode(E endNode) {
		endNode.addToIncomingPostConditions(this);
	}

	@Override
	public void removeOutgoingEdgeFromStartNode(S startNode) {
		startNode.removeFromOutgoingPostConditions(this);
	}
	@Override
	public void removeIncomingEdgeFromEndNode(E endNode) {
		endNode.removeFromIncomingPostConditions(this);
	}

	public AbstractNode getNextNode() {
		if (getEndNode()!=null) {
			return getEndNode().getNode();
		}
		return null;
	}

	// ==========================================================================
	// ============================= InspectableObject ==========================
	// ==========================================================================

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.POST_CONDITION_INSPECTOR;
	}

	public Duration getDelay()
	{
		return _delay;
	}

	public void setDelay(Duration delay)
	{
		if (_delay == null || !_delay.equals(delay)) {
			Duration oldDelay = _delay;
			_delay = delay;
			setChanged();
			notifyAttributeModification("delay", oldDelay, delay);
		}
	}

	public boolean hasDelay() {
		return getDelay()!=null && getDelay().isValid();
	}

	public boolean hideWhenInduced() {
		return _booleanGraphicalPropertyForKey("hideWhenInduced", false);
	}

	public void setHideWhenInduced(boolean hide) {
		_setGraphicalPropertyForKey(hide, "hideWhenInduced");
		setChanged();
		notifyAttributeModification("hideWhenInduced", !hide, hide);
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
		//v.add(TightenEdge.actionType);
		return v;
	}

	// =========================================================
	// ============= Control graph management ==================
	// =========================================================

	private static ControlGraphFactory<FlexoPostCondition<?,?>> _executionComputingFactory;

	public static void setExecutionComputingFactory(ControlGraphFactory<FlexoPostCondition<?,?>> factory)
	{
		_executionComputingFactory = factory;
	}

	public WorkflowControlGraph<FlexoPostCondition<?,?>> getExecution()
	{
		if (_executionComputingFactory != null) {
			return _executionComputingFactory.getControlGraph(this);
		}
		return null;
	}

	@Override
	public void setProgrammingLanguageForControlGraphComputation(ProgrammingLanguage language)
	{
		if (getExecution() != null) {
			getExecution().setProgrammingLanguage(language);
		}
	}

	@Override
	public void setInterproceduralForControlGraphComputation(boolean interprocedural)
	{
		if (getExecution() != null) {
			getExecution().setInterprocedural(interprocedural);
		}
	}

	@Override
	public String getExecutableElementName()
	{
		return FlexoLocalization.localizedForKeyWithParams("edge_($0)",getName());
	}

	public Object getEdgeRepresentation()
	{
		return _graphicalPropertyForKey(EDGE_REPRESENTATION+"_"+DEFAULT);
	}

	public void setEdgeRepresentation(Object edgeRepresentation)
	{
		if (requireChange(getEdgeRepresentation(), edgeRepresentation)) {
			Object oldEdgeRepresentation = getEdgeRepresentation();
			_setGraphicalPropertyForKey(edgeRepresentation, EDGE_REPRESENTATION+"_"+DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(EDGE_REPRESENTATION,oldEdgeRepresentation,edgeRepresentation));
		}
	}

	public boolean startOperatorIsIfNode() {
		return getStartNode() instanceof IFOperator;
	}

	public boolean isPositiveEvaluation() {
		if (getStartNode() instanceof OperatorNode) {
			if (getStartNode() instanceof IFOperator) {
				return ((IFOperator)getStartNode()).isPositiveEvaluationPostcondition(this);
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("isPositiveEvaluation called on an edge that does not come out of an IF-operator");
			}
		}
		return false;
	}

	public void setIsPositiveEvaluation(boolean positive) {
		if (getStartNode() instanceof OperatorNode) {
			if (getStartNode() instanceof IFOperator) {
				IFOperator operator = (IFOperator)getStartNode();
				if (isPositiveEvaluation() && !positive) {
					operator.removeFromPositiveEvaluationPostConditions(this);
					operator.addToNegativeEvaluationPostConditions(this);
				} else if(!isPositiveEvaluation() && positive) {
					operator.removeFromNegativeEvaluationPostConditions(this);
					operator.addToPositiveEvaluationPostConditions(this);
				}
			} else
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("isPositiveEvaluation called on an edge that does not come out of an IF-operator");
				}
		}
	}

	public String getDerivedNameFromStartingObject()
	{
		if (getName() != null) {
			return getName();
		} else if (getStartNode() != null) {
			return getStartNode().getName();
		}
		return null;
	}

	public String getDerivedNameFromEndingObject()
	{
		if (getName() != null) {
			return getName();
		} else if (getNextNode() != null) {
			return getNextNode().getName();
		}
		return null;
	}

	public boolean startNodeIsFlexoNode() {
		return getStartNode() instanceof FlexoNode;
	}

	public boolean endNodeIsFlexoNode() {
		return getEndNode() instanceof FlexoNode;
	}

	public boolean endNodeIsPreCondition() {
		return getEndNode() instanceof FlexoPreCondition;
	}

	public boolean startNodeIsOperatorNode() {
		return getStartNode() instanceof OperatorNode;
	}

	public boolean endNodeIsOperatorNode() {
		return getEndNode() instanceof OperatorNode;
	}

	public boolean startNodeIsEventNode() {
		return getStartNode() instanceof EventNode;
	}

	public boolean endNodeIsEventNode() {
		return getEndNode() instanceof EventNode;
	}

	// =========================================================
	// ===================== Validation ========================
	// =========================================================

	public abstract boolean isEdgeValid();

	public static class PostConditionMustHaveAStartingObject extends
	ValidationRule<PostConditionMustHaveAStartingObject,FlexoPostCondition<AbstractNode,AbstractNode>> {
		public PostConditionMustHaveAStartingObject() {
			super(FlexoPostCondition.class,
					"post_condition_must_have_a_starting_oject");
		}

		@Override
		public ValidationIssue<PostConditionMustHaveAStartingObject,FlexoPostCondition<AbstractNode,AbstractNode>> applyValidation(FlexoPostCondition<AbstractNode,AbstractNode> post) {
			if (post.getStartNode() == null || !post.getStartNode().isNodeValid()) {
				return new ValidationError<PostConditionMustHaveAStartingObject,FlexoPostCondition<AbstractNode,AbstractNode>>(
						this, post,
						"post_condition_has_no_starting_object",
						new DeletionFixProposal<PostConditionMustHaveAStartingObject,FlexoPostCondition<AbstractNode,AbstractNode>>("delete_this_post_condition"));
			}
			return null;
		}
	}

	public static class PostConditionMustHaveAnEndingObject extends
	ValidationRule<PostConditionMustHaveAnEndingObject,FlexoPostCondition<?,?>> {
		public PostConditionMustHaveAnEndingObject() {
			super(FlexoPostCondition.class,
					"post_condition_must_have_an_ending_oject");
		}

		@Override
		public ValidationIssue<PostConditionMustHaveAnEndingObject,FlexoPostCondition<?,?>> applyValidation(FlexoPostCondition<?,?> post) {
			if (post.getEndNode() == null || post.getEndNode().getNode()==null || !post.getEndNode().getNode().isNodeValid()) {
				return new ValidationError<PostConditionMustHaveAnEndingObject,FlexoPostCondition<?,?>>(
						this, post,
						"post_condition_has_no_starting_object",
						new DeletionFixProposal<PostConditionMustHaveAnEndingObject,FlexoPostCondition<?,?>>("delete_this_post_condition"));
			}
			return null;
		}
	}

	public static class PostConditionStartingPointShouldBeExplicitelyDefined extends
	ValidationRule<PostConditionStartingPointShouldBeExplicitelyDefined,FlexoPostCondition<?,?>> {
		public PostConditionStartingPointShouldBeExplicitelyDefined() {
			super(FlexoPostCondition.class,
					"post_condition_starting_point_should_be_explicitely_defined");
		}

		@Override
		public ValidationIssue<PostConditionStartingPointShouldBeExplicitelyDefined,FlexoPostCondition<?,?>> applyValidation(FlexoPostCondition<?,?> post) {
			if (post.getStartNode() instanceof AbstractActivityNode
					&& ((AbstractActivityNode)post.getStartNode()).isNormalNode()
					&& post.getNextNode() instanceof AbstractActivityNode
					&& !post.getIsGenericOutput() && ((AbstractActivityNode)post.getStartNode()).mightHaveOperationPetriGraph()) {
				ValidationWarning<PostConditionStartingPointShouldBeExplicitelyDefined,FlexoPostCondition<?,?>> warning
				= new ValidationWarning<PostConditionStartingPointShouldBeExplicitelyDefined,FlexoPostCondition<?,?>>(
						this, post,
						"post_condition_starting_point_should_be_explicitely_defined");
				warning.addToFixProposals(new MarkAsGenericOutput(post));
				warning.addToFixProposals(new DeletionFixProposal<PostConditionStartingPointShouldBeExplicitelyDefined,FlexoPostCondition<?,?>>("delete_this_post_condition"));
				return warning;
			}
			return null;
		}
	}

	public static class EdgesStartingFromEventBasedGatewayCannotBeConditionnal extends
	ValidationRule<EdgesStartingFromEventBasedGatewayCannotBeConditionnal,FlexoPostCondition<?,?>> {
		public EdgesStartingFromEventBasedGatewayCannotBeConditionnal() {
			super(FlexoPostCondition.class,
					"EdgesStartingFromEventBasedGatewayCannotBeConditionnal");
		}

		@Override
		public ValidationIssue<EdgesStartingFromEventBasedGatewayCannotBeConditionnal,FlexoPostCondition<?,?>> applyValidation(FlexoPostCondition<?,?> post) {

			if (post.getIsConditional() && post.isStartingFromAnExclusiveGateway()) {
				ValidationWarning<EdgesStartingFromEventBasedGatewayCannotBeConditionnal, FlexoPostCondition<?, ?>> warning = new ValidationWarning<EdgesStartingFromEventBasedGatewayCannotBeConditionnal, FlexoPostCondition<?, ?>>(
						this, post, "EdgesStartingFromEventBasedGatewayCannotBeConditionnal",new UnsetConditional());
				return warning;
			}
			return null;
		}

		public static class UnsetConditional extends FixProposal<EdgesStartingFromEventBasedGatewayCannotBeConditionnal, FlexoPostCondition<?, ?>> {
			public UnsetConditional() {
				super("unset_conditional");
			}

			@Override
			protected void fixAction() {
				getObject().setIsConditional(false);
			}
		}
	}

	public static class EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow extends
	ValidationRule<EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow,FlexoPostCondition<?,?>> {
		public EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow() {
			super(FlexoPostCondition.class,
					"EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow");
		}

		@Override
		public ValidationIssue<EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow,FlexoPostCondition<?,?>> applyValidation(FlexoPostCondition<?,?> post) {

			if (post.getIsDefaultFlow() && post.isStartingFromAnExclusiveGateway()) {
				ValidationWarning<EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow, FlexoPostCondition<?, ?>> warning = new ValidationWarning<EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow, FlexoPostCondition<?, ?>>(
						this, post, "EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow",new UnsetDefaultFlow());
				return warning;
			}
			return null;
		}

		public static class UnsetDefaultFlow extends FixProposal<EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow, FlexoPostCondition<?, ?>> {
			public UnsetDefaultFlow() {
				super("unset_default_flow");
			}

			@Override
			protected void fixAction() {
				getObject().setIsDefaultFlow(false);
			}
		}
	}


	public static class SinglePostConditionCannotBeConditionnal extends
	ValidationRule<SinglePostConditionCannotBeConditionnal,FlexoPostCondition<?,?>> {
		public SinglePostConditionCannotBeConditionnal() {
			super(FlexoPostCondition.class,
					"SinglePostConditionCannotBeConditionnal");
		}

		@Override
		public ValidationIssue<SinglePostConditionCannotBeConditionnal,FlexoPostCondition<?,?>> applyValidation(FlexoPostCondition<?,?> post) {
			if (post.getIsConditional() && post.getStartNode() instanceof FlexoNode && ((FlexoNode)post.getStartNode()).isEndNode() && post.getEndNodesAndOutgoingPortCountForParentNodeOfStartNode()==1) {
				FlexoNode startNode = (FlexoNode) post.getStartNode();
				if (startNode.getOutgoingPostConditions().size()==1) {
					ValidationWarning<SinglePostConditionCannotBeConditionnal,FlexoPostCondition<?,?>> warning
					= new ValidationWarning<SinglePostConditionCannotBeConditionnal,FlexoPostCondition<?,?>>(
							this, post,
							"SinglePostConditionCannotBeConditionnal");
					return warning;
				}
				return null;
			}
			if (post.getIsConditional()) {
				if (post.getStartNode().getOutgoingPostConditions().size()==1
						&& post.getEndNodesAndOutgoingPortCountForParentNodeOfStartNode()==1) {
					ValidationWarning<SinglePostConditionCannotBeConditionnal,FlexoPostCondition<?,?>> warning
					= new ValidationWarning<SinglePostConditionCannotBeConditionnal,FlexoPostCondition<?,?>>(
							this, post,
							"SinglePostConditionCannotBeConditionnal");
					return warning;
				}
			}
			return null;
		}
	}

	public static class MarkAsGenericOutput extends FixProposal<PostConditionStartingPointShouldBeExplicitelyDefined,FlexoPostCondition<?,?>>
	{
		public MarkAsGenericOutput(FlexoPostCondition<?,?> post)
		{
			super("define_as_generic_output");
		}

		@Override
		protected void fixAction()
		{
			getObject().setIsGenericOutput(true);
		}
	}

	/**
	 * Return a flag indicating if this edge was explicitely created as generic output:
	 * no semantic except that validation forget to mention that this edge should be defined
	 * as outgoing an END node
	 * @return
	 */
	public boolean getIsGenericOutput()
	{
		return isGenericOutput;
	}

	public void setIsGenericOutput(boolean isGenericOutput)
	{
		if (requireChange(getIsGenericOutput(), isGenericOutput)) {
			this.isGenericOutput = isGenericOutput;
			setChanged();
			notifyObservers(new WKFAttributeDataModification("isGenericOutput",!isGenericOutput,isGenericOutput));
		}
	}

	public WKFBindingDefinition getConditionPrimitiveBindingDefinition() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, CONDITION_PRIMITIVE, Boolean.class,BindingDefinitionType.GET,false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : "+returned);
			}
			return returned;
		}
		return null;
	}

	public AbstractBinding getConditionPrimitive() {
		if (isBeingCloned()) {
			return null;
		}
		return _conditionPrimitive;
	}

	public void setConditionPrimitive(AbstractBinding conditionPrimitive) {
		AbstractBinding oldBindingValue = _conditionPrimitive;
		_conditionPrimitive = conditionPrimitive;
		if (_conditionPrimitive != null) {
			_conditionPrimitive.setOwner(this);
			_conditionPrimitive.setBindingDefinition(getConditionPrimitiveBindingDefinition());
			isConditional = true;
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(CONDITION_PRIMITIVE, oldBindingValue, conditionPrimitive));
	}

	public BindingModel getBindingModel() {
		if (getProcess() != null) {
			return getProcess().getBindingModel();
		}
		return null;
	}

	public boolean getIsConditional() {
		/*if (getStartNode() instanceof OperatorNode && ((OperatorNode) getStartNode()).isExclusiveGateway()) {
			return !getIsDefaultFlow();
		}*/
		if (isStartingFromOr()) {
			return !getIsDefaultFlow();
		}
		if (canBeConditional()) {
			if(mustBeConditional()) {
				isConditional = true;
			}
		}
		return isConditional;
	}

	public boolean mustBeConditional() {
		if (canBeConditional()) {
			if(getStartNode() instanceof ActivityNode || getStartNode() instanceof OperationNode) {
				FlexoNode start = (FlexoNode) getStartNode();
				if (start.isEndNode() && getEndNodesAndOutgoingPortCountForParentNodeOfStartNode() > 1) {
					return true;
				}
			} else if (getStartNode() instanceof FlexoPortMap && ((FlexoPortMap)getStartNode()).isOutputPort()) {
				if (getEndNodesAndOutgoingPortCountForParentNodeOfStartNode()>1) {
					return true;
				}
			}
		}
		return false;
	}

	public int getEndNodesAndOutgoingPortCountForParentNodeOfStartNode() {
		int count = 0;
		if (getStartNode() instanceof FlexoNode) {
			FlexoNode start = (FlexoNode)getStartNode();
			if (start.getParentPetriGraph()!=null) {
				count+=start.getParentPetriGraph().getAllEndNodes().size();
				if (start.getParentPetriGraph().getContainer() instanceof SubProcessNode
						&& ((SubProcessNode) start.getParentPetriGraph().getContainer()).getPortMapRegistery() != null) {
					count+=((SubProcessNode)start.getParentPetriGraph().getContainer()).getPortMapRegistery().getAllOutPortmaps().size();
				}
			}
		} else if (getStartNode() instanceof FlexoPortMap && ((FlexoPortMap)getStartNode()).isOutputPort()) {
			SubProcessNode node = ((FlexoPortMap)getStartNode()).getSubProcessNode();
			if (node!=null && node.getPortMapRegistery()!=null) {
				count = node.getPortMapRegistery().getAllOutPortmaps().size()+(node.getOperationPetriGraph()!=null?node.getOperationPetriGraph().getAllEndNodes().size():0);
			}
		}
		return count;
	}

	public boolean canBeConditional() {
		//Incident 1007031
		if(getStartNode() instanceof EventNode) {
			return false;
		}
		boolean b = !getIsDefaultFlow() /* && getStartNode().getOutgoingPostConditions().size()>1*/;
		if (b && getStartNode() instanceof OperatorNode) {
			OperatorNode start = (OperatorNode) getStartNode();
			b &= start.isInclusiveGateway();
		}
		return b;
	}

	public boolean canBeDefaultFlow() {
		if(getStartNode() instanceof EventNode) {
			return false;
		}
		if (getStartNode() instanceof OperatorNode) {
			OperatorNode start = (OperatorNode) getStartNode();
			return start.isInclusiveGateway() && !getIsConditional() || start instanceof OROperator;
		}
		return !getIsConditional();
	}

	public boolean isStartingFromOr(){
		return getStartNode() instanceof OROperator;
	}
	/**
	 * used by inspector to display or not the hasConditionnal checkBox
	 * @return
	 */
	public boolean isFreelyConditionnal(){
		return !(getStartNode() instanceof OperatorNode && ((OperatorNode)getStartNode()).isExclusiveGateway());
	}

	public void setIsConditional(boolean isConditional) {
		if (isDefaultFlow && isConditional) {
			setIsDefaultFlow(false);
		}
		this.isConditional = isConditional;
		setChanged();
		notifyAttributeModification("isConditional", !isConditional, isConditional);
	}

	public boolean getIsDefaultFlow() {
		return isDefaultFlow;
	}

	public void setIsDefaultFlow(boolean isDefaultFlow) {
		//a token edge cannot be conditional and the default flow at the same time.
		if(isDefaultFlow && isConditional) {
			setIsConditional(false);
		}
		this.isDefaultFlow = isDefaultFlow;
		setChanged();
		notifyAttributeModification("isDefaultFlow", !isDefaultFlow, isDefaultFlow);
	}

	public String getConditionDescription() {
		return conditionDescription;
	}

	public void setConditionDescription(String conditionDescription) {
		this.conditionDescription = conditionDescription;
		setChanged();
		notifyAttributeModification("conditionDescription", null, conditionDescription);
	}

	/**fake property to have a ReadOnly checkbox in inspector*/
	public boolean isImplicitConditional(){
		return getIsConditional();
	}
	public void setIsImplicitConditional(boolean b){}
}
