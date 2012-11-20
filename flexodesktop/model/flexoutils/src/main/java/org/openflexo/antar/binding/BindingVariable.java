package org.openflexo.antar.binding;

import java.lang.reflect.Type;

import org.openflexo.toolbox.ToolBox;

public class BindingVariable implements BindingPathElement {

	private String variableName;
	private Type type;

	public BindingVariable(String variableName, Type type) {
		super();
		this.variableName = variableName;
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String aVariableName) {
		this.variableName = aVariableName;
	}

	@Override
	public String toString() {
		return getVariableName() + "/" + (getType() instanceof Class ? ((Class) getType()).getSimpleName() : getType());
	}

	@Override
	public String getSerializationRepresentation() {
		return variableName;
	}

	@Override
	public String getLabel() {
		return getVariableName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (getType() != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(getType());
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		} else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getVariableName() + "</b></p>";
		// returned +=
		// "<p><i>"+(bv.getDescription()!=null?bv.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingVariable) {
			return getVariableName().equals(((BindingVariable) obj).getVariableName()) && getType() != null
					&& getType().equals(((BindingVariable) obj).getType());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return (toString()).hashCode();
	}

	@Override
	public Object getBindingValue(Object owner, BindingEvaluationContext context) {
		return context.getValue(this);
	}

	@Override
	public BindingPathElement getParent() {
		return null;
	}

}
