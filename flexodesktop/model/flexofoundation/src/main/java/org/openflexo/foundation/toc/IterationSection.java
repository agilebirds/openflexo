package org.openflexo.foundation.toc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class IterationSection extends ControlSection {

	private String iteratorName;
	private ConditionalOwner conditionalOwner;

	public class ConditionalOwner implements Bindable {
		@Override
		public BindingFactory getBindingFactory() {
			return IterationSection.this.getBindingFactory();
		}

		@Override
		public BindingModel getBindingModel() {
			return IterationSection.this.getInferedBindingModel();
		}
	}

	public IterationSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public IterationSection(TOCData data) {
		super(data);
		conditionalOwner = new ConditionalOwner();
	}

	private TOCDataBinding iteration;

	private Type LIST_BINDING_TYPE = new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(Object.class));;

	private BindingDefinition ITERATION = new BindingDefinition("iteration", LIST_BINDING_TYPE, BindingDefinitionType.GET, false);

	public BindingDefinition getIterationBindingDefinition() {
		return ITERATION;
	}

	@Override
	public boolean isIteration() {
		return true;
	}

	public TOCDataBinding getIteration() {
		if (iteration == null) {
			iteration = new TOCDataBinding(this, ControlSectionBindingAttribute.iteration, getIterationBindingDefinition());
		}
		return iteration;
	}

	public void setIteration(TOCDataBinding iteration) {
		if (iteration != null) {
			iteration.setOwner(this);
			iteration.setBindingAttribute(ControlSectionBindingAttribute.iteration);
			iteration.setBindingDefinition(getIterationBindingDefinition());
		}
		this.iteration = iteration;
		rebuildInferedBindingModel();
	}

	private TOCDataBinding condition;

	private BindingDefinition CONDITION = new BindingDefinition("condition", Boolean.class, BindingDefinitionType.GET, false);

	public BindingDefinition getConditionBindingDefinition() {
		return CONDITION;
	}

	public TOCDataBinding getCondition() {
		if (condition == null) {
			condition = new TOCDataBinding(conditionalOwner, ControlSectionBindingAttribute.condition, getConditionBindingDefinition());
		}
		return condition;
	}

	public void setCondition(TOCDataBinding condition) {
		if (condition != null) {
			condition.setOwner(conditionalOwner);
			condition.setBindingAttribute(ControlSectionBindingAttribute.condition);
			condition.setBindingDefinition(getConditionBindingDefinition());
		}
		this.condition = condition;
	}

	public String getIteratorName() {
		return iteratorName;
	}

	public void setIteratorName(String iteratorName) {
		this.iteratorName = iteratorName;
	}

	public Type getItemType() {
		if (getIteration() != null && getIteration().hasBinding()) {
			Type accessedType = getIteration().getBinding().getAccessedType();
			if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
				return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
			}
		}
		return Object.class;
	}

	public ConditionalOwner getConditionalOwner() {
		return conditionalOwner;
	}

	@Override
	protected BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		returned.addToBindingVariables(new BindingVariableImpl(this, getIteratorName(), getItemType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + getIteratorName() + " ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}

			@Override
			public Type getType() {
				return getItemType();
			}
		});
		getIteration().finalizeDeserialization();
		getCondition().finalizeDeserialization();
		return returned;
	}

	/*@Override
	protected BindingModel buildBindingModel() {
		BindingModel returned = super.buildBindingModel();
		returned.addToBindingVariables(new BindingVariableImpl(this, getIteratorName(), getItemType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + getIteratorName() + " ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}

			@Override
			public Type getType() {
				return getItemType();
			}
		});
		return returned;
	}*/

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		getIteration().finalizeDeserialization();
		getCondition().finalizeDeserialization();
	}

}
