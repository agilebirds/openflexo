package org.openflexo.foundation.viewpoint;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.action.EditionSchemeAction;

public class CreateEditionPatternInstanceParameter extends EditionSchemeObject implements Bindable {

	MatchEditionPatternInstance action;

	private EditionSchemeParameter param;
	String paramName;
	private DataBinding<Object> value;

	// Use it only for deserialization
	public CreateEditionPatternInstanceParameter() {
		super();
	}

	public CreateEditionPatternInstanceParameter(EditionSchemeParameter param) {
		super();
		this.param = param;
	}

	public EditionSchemeParameter getParameter() {
		return param;
	}

	@Override
	public EditionPattern getEditionPattern() {
		if (param != null) {
			return param.getEditionPattern();
		}
		return null;
	}

	@Override
	public EditionScheme getEditionScheme() {
		if (param != null) {
			return param.getEditionScheme();
		}
		return null;
	}

	public DataBinding<Object> getValue() {
		if (value == null) {
			value = new DataBinding<Object>(this, param.getType(), DataBinding.BindingDefinitionType.GET);
			value.setBindingName(param.getName());
		}
		return value;
	}

	public void setValue(DataBinding<Object> value) {
		if (value != null) {
			value.setOwner(this);
			value.setBindingName(param != null ? param.getName() : "param");
			value.setDeclaredType(param != null ? param.getType() : Object.class);
			value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.value = value;
	}

	public Object evaluateParameterValue(EditionSchemeAction action) {
		if (getValue() == null || getValue().isUnset()) {
			/*logger.info("Binding for " + param.getName() + " is not set");
			if (param instanceof URIParameter) {
				logger.info("C'est une URI, de base " + ((URIParameter) param).getBaseURI());
				logger.info("Je retourne " + ((URIParameter) param).getBaseURI().getBinding().getBindingValue(action));
				return ((URIParameter) param).getBaseURI().getBinding().getBindingValue(action);
			} else if (param.getDefaultValue() != null && param.getDefaultValue().isSet() && param.getDefaultValue().isValid()) {
				return param.getDefaultValue().getBinding().getBindingValue(action);
			}
			if (param.getIsRequired()) {
				logger.warning("Required parameter missing: " + param + ", some strange behaviour may happen from now...");
			}*/
			return null;
		} else if (getValue().isValid()) {
			try {
				return getValue().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			MatchEditionPatternInstance.logger.warning("Invalid binding: " + getValue() + " Reason: " + getValue().invalidBindingReason());
		}
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		if (getAction() != null) {
			return getAction().getBindingModel();
		}
		return null;
	}

	@Override
	public VirtualModel getVirtualModel() {
		if (getAction() != null) {
			return getAction().getVirtualModel();
		}
		return null;
	}

	public MatchEditionPatternInstance getAction() {
		return action;
	}

	public void setAction(MatchEditionPatternInstance action) {
		this.action = action;
	}

	public EditionSchemeParameter getParam() {
		if (param == null && paramName != null && getAction() != null && getAction().getCreationScheme() != null) {
			param = getAction().getCreationScheme().getParameter(paramName);
		}
		return param;
	}

	public String _getParamName() {
		if (param != null) {
			return param.getName();
		}
		return paramName;
	}

	public void _setParamName(String param) {
		this.paramName = param;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	@Override
	public String getURI() {
		return null;
	}

}