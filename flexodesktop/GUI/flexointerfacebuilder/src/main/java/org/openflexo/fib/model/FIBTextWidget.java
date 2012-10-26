package org.openflexo.fib.model;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;

public abstract class FIBTextWidget extends FIBWidget {

	public static BindingDefinition EDITABLE = new BindingDefinition("editable", Boolean.class, BindingDefinitionType.GET, false);

	public static enum Parameters implements FIBModelAttribute {
		editable;
	}

	public boolean validateOnReturn = false;
	public String text = null;
	public Integer columns = null;
	private DataBinding editable;

	public DataBinding getEditable() {
		if (editable == null) {
			editable = new DataBinding(this, Parameters.editable, EDITABLE);
		}
		return editable;
	}

	public void setEditable(DataBinding editable) {
		editable.setOwner(this);
		editable.setBindingAttribute(Parameters.editable);
		editable.setBindingDefinition(EDITABLE);
		this.editable = editable;
	}

	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}
}
