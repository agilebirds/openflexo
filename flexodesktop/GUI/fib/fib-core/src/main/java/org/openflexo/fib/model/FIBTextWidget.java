package org.openflexo.fib.model;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;

public abstract class FIBTextWidget extends FIBWidget {

	@Deprecated
	public static BindingDefinition EDITABLE = new BindingDefinition("editable", Boolean.class, DataBinding.BindingDefinitionType.GET,
			false);

	public static enum Parameters implements FIBModelAttribute {
		editable;
	}

	private boolean validateOnReturn = false;
	private String text = null;
	private Integer columns = null;
	private DataBinding<Boolean> editable;

	public DataBinding<Boolean> getEditable() {
		if (editable == null) {
			editable = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return editable;
	}

	public void setEditable(DataBinding<Boolean> editable) {
		if (editable != null) {
			editable.setOwner(this);
			editable.setDeclaredType(Boolean.class);
			editable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.editable = editable;
	}

	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}

	/**
	 * @return the columns
	 */
	public Integer getColumns() {
		return columns;
	}

	/**
	 * @param columns
	 *            the columns to set
	 */
	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the validateOnReturn
	 */
	public boolean isValidateOnReturn() {
		return validateOnReturn;
	}

	/**
	 * @param validateOnReturn
	 *            the validateOnReturn to set
	 */
	public void setValidateOnReturn(boolean validateOnReturn) {
		this.validateOnReturn = validateOnReturn;
	}

	/**
	 * Return a list of all bindings declared in the context of this component
	 * 
	 * @return
	 */
	public List<DataBinding<?>> getDeclaredBindings() {
		List<DataBinding<?>> returned = super.getDeclaredBindings();
		returned.add(getEditable());
		return returned;
	}

}
