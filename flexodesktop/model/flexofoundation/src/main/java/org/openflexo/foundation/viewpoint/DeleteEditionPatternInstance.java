/*
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.DeletionSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * This action is used to explicitely instanciate a new {@link EditionPatternInstance} in a given {@link VirtualModelInstance} with some
 * parameters
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <MM>
 */

@FIBPanel("Fib/DeleteEditionPatternInstancePanel.fib")
@ModelEntity
@ImplementationClass(DeleteEditionPatternInstance.DeleteEditionPatternInstanceImpl.class)
@XMLElement
public interface DeleteEditionPatternInstance extends DeleteAction<VirtualModelModelSlot, EditionPatternInstance> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String VIRTUAL_MODEL_INSTANCE_KEY = "virtualModelInstance";
	@PropertyIdentifier(type = String.class)
	public static final String DELETION_SCHEME_URI_KEY = "deletionSchemeURI";
	@PropertyIdentifier(type = List.class)
	public static final String PARAMETERS_KEY = "parameters";

	@Getter(value = VIRTUAL_MODEL_INSTANCE_KEY)
	@XMLAttribute
	public DataBinding<VirtualModelInstance> getVirtualModelInstance();

	@Setter(VIRTUAL_MODEL_INSTANCE_KEY)
	public void setVirtualModelInstance(DataBinding<VirtualModelInstance> virtualModelInstance);

	@Getter(value = DELETION_SCHEME_URI_KEY)
	@XMLAttribute
	public String _getDeletionSchemeURI();

	@Setter(DELETION_SCHEME_URI_KEY)
	public void _setDeletionSchemeURI(String creationSchemeURI);

	public DeletionScheme getDeletionScheme();

	public void setDeletionScheme(DeletionScheme deletionScheme);

	@Getter(value = PARAMETERS_KEY, cardinality = Cardinality.LIST, inverse = DeleteEditionPatternInstanceParameter.ACTION_KEY)
	@XMLElement
	public List<DeleteEditionPatternInstanceParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<DeleteEditionPatternInstanceParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(DeleteEditionPatternInstanceParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(DeleteEditionPatternInstanceParameter aParameter);

	public EditionPattern getEditionPatternType();

	public void setEditionPatternType(EditionPattern editionPatternType);

	public abstract static class DeleteEditionPatternInstanceImpl extends DeleteActionImpl<VirtualModelModelSlot, EditionPatternInstance>
			implements DeleteEditionPatternInstance {

		private static final Logger logger = Logger.getLogger(DeleteEditionPatternInstance.class.getPackage().getName());

		private EditionPattern editionPatternType;
		private DeletionScheme deletionScheme;
		private String _deletionSchemeURI;

		public VirtualModelInstance getVirtualModelInstance(EditionSchemeAction<?, ?, ?> action) {
			try {
				return getVirtualModelInstance().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		private DataBinding<VirtualModelInstance> virtualModelInstance;

		@Override
		public DataBinding<VirtualModelInstance> getVirtualModelInstance() {
			if (virtualModelInstance == null) {
				virtualModelInstance = new DataBinding<VirtualModelInstance>(this, VirtualModelInstance.class,
						DataBinding.BindingDefinitionType.GET);
			}
			return virtualModelInstance;
		}

		@Override
		public void setVirtualModelInstance(DataBinding<VirtualModelInstance> aVirtualModelInstance) {
			if (aVirtualModelInstance != null) {
				aVirtualModelInstance.setOwner(this);
				aVirtualModelInstance.setBindingName("virtualModelInstance");
				aVirtualModelInstance.setDeclaredType(VirtualModelInstance.class);
				aVirtualModelInstance.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.virtualModelInstance = aVirtualModelInstance;
		}

		@Override
		public EditionPattern getEditionPatternType() {
			if (getDeletionScheme() != null) {
				return getDeletionScheme().getEditionPattern();
			}
			return editionPatternType;
		}

		@Override
		public void setEditionPatternType(EditionPattern editionPatternType) {
			this.editionPatternType = editionPatternType;
			if (getDeletionScheme() != null && getDeletionScheme().getEditionPattern() != editionPatternType) {
				setDeletionScheme(null);
			}
		}

		@Override
		public String _getDeletionSchemeURI() {
			if (getDeletionScheme() != null) {
				return getDeletionScheme().getURI();
			}
			return _deletionSchemeURI;
		}

		@Override
		public void _setDeletionSchemeURI(String uri) {
			if (getViewPointLibrary() != null) {
				deletionScheme = (DeletionScheme) getViewPointLibrary().getEditionScheme(uri);
			}
			_deletionSchemeURI = uri;
		}

		@Override
		public DeletionScheme getDeletionScheme() {
			if (deletionScheme == null && _deletionSchemeURI != null && getViewPointLibrary() != null) {
				deletionScheme = (DeletionScheme) getViewPointLibrary().getEditionScheme(_deletionSchemeURI);
			}
			if (deletionScheme == null && getPatternRole() instanceof EditionPatternInstancePatternRole) {
				deletionScheme = ((EditionPatternInstancePatternRole) getPatternRole()).getEditionPattern().getDefaultDeletionScheme();
			}
			return deletionScheme;
		}

		@Override
		public void setDeletionScheme(DeletionScheme deletionScheme) {
			this.deletionScheme = deletionScheme;
			if (deletionScheme != null) {
				_deletionSchemeURI = deletionScheme.getURI();
			}
		}

		// private Vector<AddEditionPatternInstanceParameter> parameters = new Vector<AddEditionPatternInstanceParameter>();

		@Override
		public List<DeleteEditionPatternInstanceParameter> getParameters() {
			updateParameters();
			return (List<DeleteEditionPatternInstanceParameter>) performSuperGetter(PARAMETERS_KEY);
		}

		public DeleteEditionPatternInstanceParameter getParameter(EditionSchemeParameter p) {
			for (DeleteEditionPatternInstanceParameter deleteEPParam : getParameters()) {
				if (deleteEPParam.getParam() == p) {
					return deleteEPParam;
				}
			}
			return null;
		}

		private void updateParameters() {
			List<DeleteEditionPatternInstanceParameter> parametersToRemove = new ArrayList<DeleteEditionPatternInstanceParameter>(
					getParameters());
			if (getDeletionScheme() != null) {
				for (EditionSchemeParameter p : getDeletionScheme().getParameters()) {
					DeleteEditionPatternInstanceParameter existingParam = getParameter(p);
					if (existingParam != null) {
						parametersToRemove.remove(existingParam);
					} else {
						addToParameters(getVirtualModelFactory().newDeleteEditionPatternInstanceParameter(p));
					}
				}
			}
			for (DeleteEditionPatternInstanceParameter removeThis : parametersToRemove) {
				removeFromParameters(removeThis);
			}
		}

		@Override
		public EditionPatternInstance performAction(EditionSchemeAction action) {
			logger.info("Perform performDeleteEditionPatternInstance " + action);
			VirtualModelInstance vmInstance = getVirtualModelInstance(action);

			DeletionSchemeAction deletionSchemeAction = DeletionSchemeAction.actionType.makeNewEmbeddedAction(null, null, action);

			try {
				EditionPatternInstance objectToDelete = (EditionPatternInstance) getObject().getBindingValue(action);
				// if VmInstance is null, use the one of the EPI
				vmInstance = objectToDelete.getVirtualModelInstance();

				logger.info("EditionPatternInstance To Delete: " + objectToDelete);
				logger.info("VirtualModelInstance: " + vmInstance);
				deletionSchemeAction.setEditionPatternInstanceToDelete(objectToDelete);
				deletionSchemeAction.setVirtualModelInstance(vmInstance);
				deletionSchemeAction.setDeletionScheme(getDeletionScheme());

				for (DeleteEditionPatternInstanceParameter p : getParameters()) {
					EditionSchemeParameter param = p.getParam();
					Object value = p.evaluateParameterValue(action);
					logger.info("For parameter " + param + " value is " + value);
					if (value != null) {
						deletionSchemeAction.setParameterValue(p.getParam(), p.evaluateParameterValue(action));
					}
				}
				logger.info("Performing action");
				deletionSchemeAction.doAction();
				// Finally delete the FlexoConcept
				objectToDelete.delete();

			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
			if (deletionSchemeAction.hasActionExecutionSucceeded()) {
				logger.info("Successfully performed performDeleteEditionPattern " + action);
				return deletionSchemeAction.getEditionPatternInstance();
			}
			return null;
		}

	}

	public static class DeleteEditionPatternInstanceMustAddressADeletionScheme extends
			ValidationRule<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance> {
		public DeleteEditionPatternInstanceMustAddressADeletionScheme() {
			super(DeleteEditionPatternInstance.class, "delete_edition_pattern_action_must_address_a_valid_creation_scheme");
		}

		@Override
		public ValidationIssue<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance> applyValidation(
				DeleteEditionPatternInstance action) {
			if (action.getDeletionScheme() == null) {
				if (action.getEditionPatternType() == null) {
					return new ValidationError<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance>(this,
							action, "delete_edition_pattern_action_doesn't_define_any_edition_pattern");
				} else {
					return new ValidationError<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance>(this,
							action, "delete_edition_pattern_action_doesn't_define_any_creation_scheme");
				}
			}
			return null;
		}
	}

	public static class DeleteEditionPatternInstanceParametersMustBeValid extends
			ValidationRule<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance> {

		public DeleteEditionPatternInstanceParametersMustBeValid() {
			super(DeleteEditionPatternInstance.class, "delete_edition_pattern_parameters_must_be_valid");
		}

		@Override
		public ValidationIssue<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance> applyValidation(
				DeleteEditionPatternInstance action) {
			if (action.getDeletionScheme() != null) {
				Vector<ValidationIssue<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>> issues = new Vector<ValidationIssue<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>>();
				for (DeleteEditionPatternInstanceParameter p : action.getParameters()) {

					EditionSchemeParameter param = p.getParam();
					if (param.getIsRequired()) {
						if (p.getValue() == null || !p.getValue().isSet()) {
							DataBinding<String> uri = ((URIParameter) param).getBaseURI();
							if (param instanceof URIParameter && uri.isSet() && uri.isValid()) {
								// Special case, we will find a way to manage this
							} else {
								issues.add(new ValidationError<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>(
										this, action, "parameter_s_value_is_not_defined: " + param.getName()));
							}
						} else if (!p.getValue().isValid()) {
							DeleteEditionPatternInstanceImpl.logger.info("Binding NOT valid: " + p.getValue() + " for "
									+ p.getParam().getName() + " object=" + p.getAction() + ". Reason: "
									+ p.getValue().invalidBindingReason());
							issues.add(new ValidationError<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>(
									this, action, "parameter_s_value_is_not_valid: " + param.getName()));
						}
					}
				}
				if (issues.size() == 0) {
					return null;
				} else if (issues.size() == 1) {
					return issues.firstElement();
				} else {
					return new CompoundIssue<DeleteEditionPatternInstance.DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>(
							action, issues);
				}
			}
			return null;
		}
	}

	public static class VirtualModelInstanceBindingIsRequiredAndMustBeValid extends
			BindingIsRequiredAndMustBeValid<DeleteEditionPatternInstance> {
		public VirtualModelInstanceBindingIsRequiredAndMustBeValid() {
			super("'virtual_model_instance'_binding_is_not_valid", DeleteEditionPatternInstance.class);
		}

		@Override
		public DataBinding<VirtualModelInstance> getBinding(DeleteEditionPatternInstance object) {
			return object.getVirtualModelInstance();
		}

	}

}
