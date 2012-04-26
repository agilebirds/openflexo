package org.openflexo.foundation.toc;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ConditionalSection extends ControlSection {

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

	private TOCDataBinding condition;

	private BindingDefinition CONDITION = new BindingDefinition("condition", Boolean.class, BindingDefinitionType.GET, false);

	public BindingDefinition getConditionBindingDefinition() {
		return CONDITION;
	}

	public TOCDataBinding getCondition() {
		if (condition == null) {
			condition = new TOCDataBinding(this, ControlSectionBindingAttribute.condition, getConditionBindingDefinition());
		}
		return condition;
	}

	public void setCondition(TOCDataBinding condition) {
		if (condition != null) {
			condition.setOwner(this);
			condition.setBindingAttribute(ControlSectionBindingAttribute.condition);
			condition.setBindingDefinition(getConditionBindingDefinition());
			this.condition = condition;
		}
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		getCondition().finalizeDeserialization();
	}

}
