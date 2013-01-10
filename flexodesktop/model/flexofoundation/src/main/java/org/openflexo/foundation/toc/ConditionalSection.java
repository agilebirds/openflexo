package org.openflexo.foundation.toc;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ConditionalSection extends ControlSection {

	private DataBinding<Boolean> condition;

	public ConditionalSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ConditionalSection(TOCData data) {
		super(data);
	}

	@Override
	public boolean isConditional() {
		return true;
	}

	public DataBinding<Boolean> getCondition() {
		if (condition == null) {
			condition = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return condition;
	}

	public void setCondition(DataBinding<Boolean> condition) {
		if (condition != null) {
			condition.setOwner(this);
			condition.setDeclaredType(Boolean.class);
			condition.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.condition = condition;
	}

	@Override
	protected BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		getCondition().decode();
		return returned;
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		getCondition().decode();
	}

}
