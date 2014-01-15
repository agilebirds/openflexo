package org.openflexo.foundation.viewpoint;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(MatchingCriteria.MatchingCriteriaImpl.class)
@XMLElement
public interface MatchingCriteria extends EditionSchemeObject, Bindable {

	@PropertyIdentifier(type = MatchEditionPatternInstance.class)
	public static final String ACTION_KEY = "action";

	@PropertyIdentifier(type = String.class)
	public static final String PATTERN_ROLE_NAME_KEY = "patternRoleName";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";

	@Getter(value = ACTION_KEY, inverse = MatchEditionPatternInstance.MATCHING_CRITERIAS_KEY)
	public MatchEditionPatternInstance getAction();

	@Setter(ACTION_KEY)
	public void setAction(MatchEditionPatternInstance action);

	@Getter(value = PATTERN_ROLE_NAME_KEY)
	@XMLAttribute
	public String _getPatternRoleName();

	@Setter(PATTERN_ROLE_NAME_KEY)
	public void _setPatternRoleName(String patternRoleName);

	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<?> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<?> value);

	public PatternRole<?> getPatternRole();

	public void setPatternRole(PatternRole<?> patternRole);

	public Object evaluateCriteriaValue(EditionSchemeAction action);

	public static abstract class MatchingCriteriaImpl extends EditionSchemeObjectImpl implements MatchingCriteria {

		private static final Logger logger = Logger.getLogger(MatchingCriteria.class.getPackage().getName());

		private MatchEditionPatternInstance action;

		private PatternRole patternRole;
		private String patternRoleName;
		private DataBinding<?> value;

		// Use it only for deserialization
		public MatchingCriteriaImpl() {
			super();
		}

		public MatchingCriteriaImpl(PatternRole patternRole) {
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

		@Override
		public DataBinding<?> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, getPatternRole() != null ? getPatternRole().getType() : Object.class,
						DataBinding.BindingDefinitionType.GET);
				value.setBindingName(getPatternRole() != null ? getPatternRole().getName() : "param");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<?> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName(getPatternRole() != null ? getPatternRole().getName() : "param");
				value.setDeclaredType(getPatternRole() != null ? getPatternRole().getType() : Object.class);
				value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.value = value;
		}

		@Override
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
				logger.warning("Invalid binding: " + getValue() + " Reason: " + getValue().invalidBindingReason());
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

		@Override
		public MatchEditionPatternInstance getAction() {
			return action;
		}

		@Override
		public void setAction(MatchEditionPatternInstance action) {
			this.action = action;
		}

		@Override
		public PatternRole getPatternRole() {
			if (patternRole == null && patternRoleName != null && getAction() != null && getAction().getEditionPatternType() != null) {
				patternRole = getAction().getEditionPatternType().getPatternRole(patternRoleName);
			}
			return patternRole;
		}

		@Override
		public void setPatternRole(PatternRole patternRole) {
			this.patternRole = patternRole;
		}

		@Override
		public String _getPatternRoleName() {
			if (patternRole != null) {
				return patternRole.getName();
			}
			return patternRoleName;
		}

		@Override
		public void _setPatternRoleName(String patternRoleName) {
			this.patternRoleName = patternRoleName;
		}

		@Override
		public String getURI() {
			return null;
		}

	}
}
