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
package org.openflexo.foundation.wkf.node;

import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.dm.PostInserted;
import org.openflexo.foundation.wkf.dm.PostRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.edge.ContextualEdgeStarting;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

public class IFOperator extends OperatorNode implements ContextualEdgeStarting {

	/**
	 * Stores the outgoing post conditions for Negative result, as a Vector of OperatorExit
	 */
	private Vector<FlexoPostCondition<AbstractNode,AbstractNode>> _positiveEvaluationOutgoingPostConditions;

	/**
	 * Stores the outgoing post conditions for negative result, as a Vector of OperatorExit
	 */
	private Vector<FlexoPostCondition<AbstractNode,AbstractNode>> _negativeEvaluationOutgoingPostConditions;

	public static final String CONDITION_PRIMITIVE = "conditionPrimitive";

	private AbstractBinding _conditionPrimitive;

	private Status newStatusForPositiveEvaluation;
	private String newStatusForPositiveEvaluationAsString;

	private Status newStatusForNegativeEvaluation;
	private String newStatusForNegativeEvaluationAsString;

	/**
     * Constructor used during deserialization
     */
    public IFOperator(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public IFOperator(FlexoProcess process)
    {
        super(process);
        _positiveEvaluationOutgoingPostConditions = new Vector<FlexoPostCondition<AbstractNode,AbstractNode>>();
        _negativeEvaluationOutgoingPostConditions = new Vector<FlexoPostCondition<AbstractNode,AbstractNode>>();
    }

	@Override
	public String getInspectorName()
	{
		return Inspectors.WKF.OPERATOR_IF_INSPECTOR;
	}

	/**
	 * Returns the outgoing post conditions for positive evaluation, as a Vector of OperatorExit
	 */
	public Vector<FlexoPostCondition<AbstractNode,AbstractNode>> getPositiveEvaluationPostConditions()
	{
		return _positiveEvaluationOutgoingPostConditions;
	}

	public void setPositiveEvaluationPostConditions(Vector<FlexoPostCondition<AbstractNode,AbstractNode>> postConditions)
	{
		_positiveEvaluationOutgoingPostConditions = postConditions;
	}

	public void removeFromPositiveEvaluationPostConditions(FlexoPostCondition post)
	{
		if (_positiveEvaluationOutgoingPostConditions.contains(post)) {
			_positiveEvaluationOutgoingPostConditions.remove(post);
			post.setStartNode(null);
			setChanged();
			notifyObservers(new PostRemoved(post));
		}
	}

	public void addToPositiveEvaluationPostConditions(FlexoPostCondition post)
	{
		if (!_positiveEvaluationOutgoingPostConditions.contains(post) && post.getStartNodeClass().isAssignableFrom(getClass())) {
			_positiveEvaluationOutgoingPostConditions.add(post);
			post.setStartNode(this);
			setChanged();
			notifyObservers(new PostInserted(post));
			notifyPostInsertedToProcess(post);
		}
	}

	/**
	 * Returns the outgoing post conditions for Negative evaluation, as a Vector of OperatorExit
	 */
	public Vector<FlexoPostCondition<AbstractNode,AbstractNode>> getNegativeEvaluationPostConditions()
	{
		return _negativeEvaluationOutgoingPostConditions;
	}

	public void setNegativeEvaluationPostConditions(Vector<FlexoPostCondition<AbstractNode,AbstractNode>> postConditions)
	{
		_negativeEvaluationOutgoingPostConditions = postConditions;
	}

	public void removeFromNegativeEvaluationPostConditions(FlexoPostCondition post)
	{
		if (_negativeEvaluationOutgoingPostConditions.contains(post)) {
			_negativeEvaluationOutgoingPostConditions.remove(post);
			post.setStartNode(null);
			setChanged();
			notifyObservers(new PostRemoved(post));
		}
	}

	public void addToNegativeEvaluationPostConditions(FlexoPostCondition post)
	{
		if (!_negativeEvaluationOutgoingPostConditions.contains(post) && post.getStartNodeClass().isAssignableFrom(getClass())) {
			_negativeEvaluationOutgoingPostConditions.add(post);
			post.setStartNode(this);
			setChanged();
			notifyObservers(new PostInserted(post));
			notifyPostInsertedToProcess(post);
		}
	}

	@Override
	public Vector<FlexoPostCondition<AbstractNode, AbstractNode>> getOutgoingPostConditions()
	{
		Vector<FlexoPostCondition<AbstractNode,AbstractNode>> returned = new Vector<FlexoPostCondition<AbstractNode,AbstractNode>>();
		returned.addAll(_positiveEvaluationOutgoingPostConditions);
		returned.addAll(_negativeEvaluationOutgoingPostConditions);
		return returned;
	}

	@Override
	public void setOutgoingPostConditions(Vector<FlexoPostCondition<AbstractNode,AbstractNode>> postConditions)
	{
		// Not applicable
	}

	@Override
	public void addToOutgoingPostConditions(FlexoPostCondition post)
	{
		// Not applicable
		/*logger.warning("addToOutgoingPostConditions() called in IFOperator with no context, use POSITIVE evaluation");
		addToPositiveEvaluationPostConditions(post);*/
	}

	@Override
	public void addToOutgoingPostConditions(FlexoPostCondition post, Object outputContext)
	{
		logger.info("addToOutgoingPostConditions() called in IFOperator with context="+outputContext);
		if (outputContext.equals(Boolean.TRUE)) {
			addToPositiveEvaluationPostConditions(post);
		}
		else if (outputContext.equals(Boolean.FALSE)) {
			addToNegativeEvaluationPostConditions(post);
		}
		else {
			logger.warning("INCONSISTENT DATA: addToOutgoingPostConditions() called in IFOperator with context="+outputContext);
		}
	}

	@Override
	public void removeFromOutgoingPostConditions(FlexoPostCondition post)
	{
		removeFromPositiveEvaluationPostConditions(post);
		removeFromNegativeEvaluationPostConditions(post);
	}

	@Override
	public String getDefaultName() {
		return FlexoLocalization.localizedForKey("IF");
	}

     public WKFBindingDefinition getConditionPrimitiveBindingDefinition()
    {
    	if (getProject() != null) {
    		WKFBindingDefinition returned = WKFBindingDefinition.get(this, CONDITION_PRIMITIVE, Boolean.class,BindingDefinitionType.GET,false);
    		if (logger.isLoggable(Level.FINE))
    			logger.fine("Returned WKFBindingDefinition : "+returned);
    		return returned;
    	}
    	return null;
    }

    public AbstractBinding getConditionPrimitive()
    {
        if (isBeingCloned())
            return null;
        return _conditionPrimitive;
    }

    public void setConditionPrimitive(AbstractBinding conditionPrimitive)
    {
    	AbstractBinding oldBindingValue = _conditionPrimitive;
        _conditionPrimitive = conditionPrimitive;
        if (_conditionPrimitive != null) {
            _conditionPrimitive.setOwner(this);
            _conditionPrimitive.setBindingDefinition(getConditionPrimitiveBindingDefinition());
        }
        setChanged();
        notifyObservers(new WKFAttributeDataModification(CONDITION_PRIMITIVE, oldBindingValue, conditionPrimitive));
    }

    public boolean isPositiveEvaluationPostcondition(FlexoPostCondition post)
    {
    	return (getPositiveEvaluationPostConditions().contains(post));
    }

    public boolean isNegativeEvaluationPostcondition(FlexoPostCondition post)
    {
    	return (getNegativeEvaluationPostConditions().contains(post));
    }

    public Status getNewStatusForPositiveEvaluation() {
    	if (newStatusForPositiveEvaluation==null && newStatusForPositiveEvaluationAsString!=null) {
    		if (getProject()!=null) {
    			newStatusForPositiveEvaluation = getProject().getGlobalStatus().get(newStatusForPositiveEvaluationAsString);
    			if (newStatusForPositiveEvaluation==null && !isDeserializing()) {
    				if (logger.isLoggable(Level.WARNING))
						logger.warning("Status with name "+newStatusForPositiveEvaluationAsString+" could not be found.");
    				newStatusForPositiveEvaluationAsString = null;
    			}
    		} else if (!isDeserializing()) {
    			if (logger.isLoggable(Level.WARNING))
					logger.warning("No project for Operation node "+getName());
    		}
    	}
    	return newStatusForPositiveEvaluation;
    }

    public void setNewStatusForPositiveEvaluation(Status newStatusForPositiveEvaluation) {
    	Status old = this.newStatusForPositiveEvaluation;
		this.newStatusForPositiveEvaluation = newStatusForPositiveEvaluation;
		notifyAttributeModification("newStatusForPositiveEvaluation", old, newStatusForPositiveEvaluation);
	}

    public String getNewStatusForPositiveEvaluationAsString() {
    	if (getNewStatusForPositiveEvaluation()!=null)
    		return getNewStatusForPositiveEvaluation().getFullyQualifiedName();
    	else
    		return null;
	}

    public void setNewStatusForPositiveEvaluationAsString(String newStatusForPositiveEvaluationAsString) {
		this.newStatusForPositiveEvaluationAsString = newStatusForPositiveEvaluationAsString;
	}

    public Status getNewStatusForNegativeEvaluation() {
    	if (newStatusForNegativeEvaluation==null && newStatusForNegativeEvaluationAsString!=null) {
    		if (getProject()!=null) {
    			newStatusForNegativeEvaluation = getProject().getGlobalStatus().get(newStatusForNegativeEvaluationAsString);
    			if (newStatusForNegativeEvaluation==null && !isDeserializing()) {
    				if (logger.isLoggable(Level.WARNING))
    					logger.warning("Status with name "+newStatusForNegativeEvaluationAsString+" could not be found.");
    				newStatusForNegativeEvaluationAsString = null;
    			}
    		} else if (!isDeserializing()) {
    			if (logger.isLoggable(Level.WARNING))
    				logger.warning("No project for Operation node "+getName());
    		}
    	}
    	return newStatusForNegativeEvaluation;
    }

    public void setNewStatusForNegativeEvaluation(Status newStatusForNegativeEvaluation) {
    	Status old = this.newStatusForNegativeEvaluation;
    	this.newStatusForNegativeEvaluation = newStatusForNegativeEvaluation;
    	notifyAttributeModification("newStatusForNegativeEvaluation", old, newStatusForNegativeEvaluation);
    }

    public String getNewStatusForNegativeEvaluationAsString() {
    	if (getNewStatusForNegativeEvaluation()!=null)
    		return getNewStatusForNegativeEvaluation().getFullyQualifiedName();
    	else
    		return null;
    }

    public void setNewStatusForNegativeEvaluationAsString(String newStatusForNegativeEvaluationAsString) {
    	this.newStatusForNegativeEvaluationAsString = newStatusForNegativeEvaluationAsString;
    }

	public static class CannotHaveMoreThanTwoOutgoingEdge extends
	ValidationRule<CannotHaveMoreThanTwoOutgoingEdge,IFOperator> {
		public CannotHaveMoreThanTwoOutgoingEdge() {
			super(IFOperator.class,
					"if_node_cannot_have_more_than_2_outgoing_flow");
		}

		@Override
		public ValidationIssue<CannotHaveMoreThanTwoOutgoingEdge,IFOperator> applyValidation(IFOperator post) {
			if (post.getOutgoingPostConditions().size()>2) {
				return new ValidationWarning<CannotHaveMoreThanTwoOutgoingEdge,IFOperator>(
						this, post,
						"if_node_cannot_have_more_than_2_outgoing_flow");
			}
			if (post.getOutgoingPostConditions().size()<2) {
				return new ValidationWarning<CannotHaveMoreThanTwoOutgoingEdge,IFOperator>(
						this, post,
						"if_node_cannot_have_less_than_2_outgoing_flow");
			}
			return null;
		}
	}
	
	
	public static class MustHaveOnePositiveAndOneNegativeOutgoingFlow extends
	ValidationRule<MustHaveOnePositiveAndOneNegativeOutgoingFlow,IFOperator> {
		public MustHaveOnePositiveAndOneNegativeOutgoingFlow() {
			super(IFOperator.class,
					"if_node_cannot_have_more_than_2_outgoing_flow");
		}

		@Override
		public ValidationIssue<MustHaveOnePositiveAndOneNegativeOutgoingFlow,IFOperator> applyValidation(IFOperator post) {
			if (post.getOutgoingPostConditions().size()==2) {
				FlexoPostCondition post1 = post.getOutgoingPostConditions().get(0);
				FlexoPostCondition post2 = post.getOutgoingPostConditions().get(1);
				if(post1.isPositiveEvaluation()!=!post2.isPositiveEvaluation())
					return new ValidationWarning<MustHaveOnePositiveAndOneNegativeOutgoingFlow,IFOperator>(
						this, post,
						"if_node_must_have_exactly_one_positive_and_one_negative_outgoing_flow");
			}
			return null;
		}
	}
}
