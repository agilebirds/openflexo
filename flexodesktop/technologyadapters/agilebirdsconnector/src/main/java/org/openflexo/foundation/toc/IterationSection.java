package org.openflexo.foundation.toc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class IterationSection extends ControlSection {

	private static Type LIST_BINDING_TYPE = new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(Object.class));;

	private String iteratorName;
	private ConditionalOwner conditionalOwner;
	private DataBinding<List<?>> iteration;
	private DataBinding<Boolean> condition;

	public class ConditionalOwner implements Bindable {
		@Override
		public BindingFactory getBindingFactory() {
			return IterationSection.this.getBindingFactory();
		}

		@Override
		public BindingModel getBindingModel() {
			return IterationSection.this.getInferedBindingModel();
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
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

	@Override
	public boolean isIteration() {
		return true;
	}

	public DataBinding<List<?>> getIteration() {
		if (iteration == null) {
			iteration = new DataBinding<List<?>>(this, LIST_BINDING_TYPE, DataBinding.BindingDefinitionType.GET);
		}
		return iteration;
	}

	public void setIteration(DataBinding<List<?>> iteration) {
		if (iteration != null) {
			iteration.setOwner(this);
			iteration.setDeclaredType(LIST_BINDING_TYPE);
			iteration.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.iteration = iteration;
		rebuildInferedBindingModel();
	}

	public DataBinding<Boolean> getCondition() {
		if (condition == null) {
			condition = new DataBinding<Boolean>(conditionalOwner, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return condition;
	}

	public void setCondition(DataBinding<Boolean> condition) {
		if (condition != null) {
			condition.setOwner(conditionalOwner);
			condition.setDeclaredType(Boolean.class);
			condition.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
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
		if (getIteration().isSet()) {
			Type accessedType = getIteration().getAnalyzedType();
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
		returned.addToBindingVariables(new BindingVariable(getIteratorName(), getItemType()) {
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
		getIteration().decode();
		getCondition().decode();
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
		getIteration().decode();
		getCondition().decode();
	}

}
