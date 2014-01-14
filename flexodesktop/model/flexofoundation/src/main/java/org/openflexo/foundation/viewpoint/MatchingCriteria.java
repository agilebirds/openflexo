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

public class MatchingCriteria extends EditionSchemeObject implements Bindable {

	private MatchEditionPatternInstance action;

	private PatternRole patternRole;
	private String patternRoleName;
	private DataBinding<Object> value;

	// Use it only for deserialization
	public MatchingCriteria() {
		super();
	}

	public MatchingCriteria(PatternRole patternRole) {
		super();
		this.patternRole = patternRole;
	}

	@Override
	public EditionPattern getEditionPattern() {
		if (getAction() != null) {
			return getAction().getEditionPattern();
		}
		return null;
	}

	@Override
	public EditionScheme getEditionScheme() {
		if (getAction() != null) {
			return getAction().getEditionScheme();
		}
		return null;
	}

	public DataBinding<Object> getValue() {
		if (value == null) {
			value = new DataBinding<Object>(this, getPatternRole() != null ? getPatternRole().getType() : Object.class,
					DataBinding.BindingDefinitionType.GET);
			value.setBindingName(getPatternRole() != null ? getPatternRole().getName() : "param");
		}
		return value;
	}

	public void setValue(DataBinding<Object> value) {
		if (value != null) {
			value.setOwner(this);
			value.setBindingName(getPatternRole() != null ? getPatternRole().getName() : "param");
			value.setDeclaredType(getPatternRole() != null ? getPatternRole().getType() : Object.class);
			value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.value = value;
	}

	public Object evaluateCriteriaValue(EditionSchemeAction action) {
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

	public PatternRole getPatternRole() {
		if (patternRole == null && patternRoleName != null && getAction() != null && getAction().getEditionPatternType() != null) {
			patternRole = getAction().getEditionPatternType().getPatternRole(patternRoleName);
		}
		return patternRole;
	}

	public String _getPatternRoleName() {
		if (patternRole != null) {
			return patternRole.getName();
		}
		return patternRoleName;
	}

	public void _setPatternRoleName(String patternRoleName) {
		this.patternRoleName = patternRoleName;
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