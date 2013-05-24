package org.openflexo.fib.model;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;

public abstract class FIBTextWidget extends FIBWidget {

	public static BindingDefinition EDITABLE = new BindingDefinition("editable", Boolean.class, BindingDefinitionType.GET, false);

	public static enum Parameters implements FIBModelAttribute {
		editable;
	}

	private boolean validateOnReturn = false;
	private String text = null;
	private Integer columns = null;
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

}
