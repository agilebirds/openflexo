package org.openflexo.fib.model;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.DataBinding;

public abstract class FIBTextWidget extends FIBWidget {

	@Deprecated
	public static BindingDefinition EDITABLE = new BindingDefinition("editable", Boolean.class, BindingDefinitionType.GET, false);

	public static enum Parameters implements FIBModelAttribute {
		editable;
	}

	public boolean validateOnReturn = false;
	public String text = null;
	public Integer columns = null;
	private DataBinding<Boolean> editable;

	public DataBinding<Boolean> getEditable() {
		if (editable == null) {
			editable = new DataBinding<Boolean>(this, Boolean.class, BindingDefinitionType.GET);
		}
		return editable;
	}

	public void setEditable(DataBinding<Boolean> editable) {
		if (editable != null) {
			editable.setOwner(this);
			editable.setDeclaredType(Boolean.class);
			editable.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.editable = editable;
	}

	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}
}
