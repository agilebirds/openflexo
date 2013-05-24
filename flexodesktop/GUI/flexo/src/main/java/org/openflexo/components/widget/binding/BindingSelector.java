/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.components.widget.binding;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.antar.expr.Expression;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.AbstractBinding.AbstractBindingStringConverter;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingExpression;
import org.openflexo.foundation.bindings.BindingExpression.BindingValueConstant;
import org.openflexo.foundation.bindings.BindingExpression.BindingValueFunction;
import org.openflexo.foundation.bindings.BindingExpression.BindingValueVariable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingValue.BindingPathElement;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.BooleanStaticBinding;
import org.openflexo.foundation.bindings.DateStaticBinding;
import org.openflexo.foundation.bindings.DurationStaticBinding;
import org.openflexo.foundation.bindings.FloatStaticBinding;
import org.openflexo.foundation.bindings.IntegerStaticBinding;
import org.openflexo.foundation.bindings.MethodCall;
import org.openflexo.foundation.bindings.StaticBinding;
import org.openflexo.foundation.bindings.StringStaticBinding;
import org.openflexo.foundation.bindings.TranstypedBinding;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.param.ChoiceListParameter;
import org.openflexo.foundation.param.DMTypeParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.UserType;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.swing.SwingUtils;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.Duration.DurationUnit;
import org.openflexo.toolbox.StringUtils;

/**
 * Widget allowing to edit a binding
 * 
 * @author sguerin
 * 
 */
public class BindingSelector extends TextFieldCustomPopup<AbstractBinding> implements FIBCustomComponent<AbstractBinding, BindingSelector> {
	static final Logger logger = Logger.getLogger(BindingSelector.class.getPackage().getName());

	private AbstractBinding _revertBindingValue;

	protected boolean _allowsEntryCreation = false;
	protected boolean _allowsBindingExpressions = true;
	protected boolean _allowsCompoundBindings = UserType.isDevelopperRelease() || UserType.isMaintainerRelease();
	protected boolean _allowsStaticValues = true;
	protected boolean _allowsTranstypers = true;

	protected AbstractBindingSelectorPanel _selectorPanel;
	boolean isUpdatingModel = false;

	protected KeyEventDispatcher tabDispatcher = new KeyEventDispatcher() {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_TYPED && (e.getKeyChar() == KeyEvent.VK_RIGHT || e.getKeyChar() == KeyEvent.VK_TAB)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Calling tab pressed " + e);
				}
				getCustomPanel().processTabPressed();
				e.consume();
			}
			return false;
		}
	};

	static enum EditionMode {
		NORMAL_BINDING, COMPOUND_BINDING, STATIC_BINDING, TRANSTYPED_BINDING, BINDING_EXPRESSION;/*,
																									NEW_ENTRY;*/

		boolean useCommonPanel() {
			return this != BINDING_EXPRESSION && this != TRANSTYPED_BINDING;
		}
	}

	EditionMode editionMode = EditionMode.NORMAL_BINDING;

	public BindingSelector(AbstractBinding editedObject) {
		this(editedObject, -1);
	}

	public KeyAdapter shortcutsKeyAdapter;
	private final DocumentListener documentListener;

	public BindingSelector(AbstractBinding editedObject, int cols) {
		super(null, cols);
		setFocusable(true);
		getTextField().setFocusable(true);
		getTextField().setEditable(true);
		getTextField().addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(tabDispatcher);
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(tabDispatcher);
				Component opposite = focusEvent.getOppositeComponent();
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("focus lost for " + (opposite != null ? SwingUtils.getComponentPath(opposite) : "null"));
				}
				if (opposite != null && !(opposite instanceof JRootPane)
						&& !SwingUtils.isComponentContainedInContainer(opposite, getParent())
						&& !SwingUtils.isComponentContainedInContainer(opposite, _selectorPanel)) {
					// Little hook used to automatically apply a valid value which has generally been edited
					// By typing text in text field
					if (getEditedObject() != null && getEditedObject().isBindingValid()) {
						apply();
					}
				}
			}
		});
		shortcutsKeyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				boolean isSignificativeKey = e.getKeyCode() >= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z
						|| e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9;

				if (!popupIsShown() && getTextField().getText() != null
						&& !isAcceptableAsBeginningOfStaticBindingValue(getTextField().getText()) && isSignificativeKey) {
					boolean requestFocus = getTextField().hasFocus();
					openPopup();
					if (requestFocus) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								getTextField().requestFocusInWindow();
							}
						});
					}
				}

				if (_selectorPanel != null) {

					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (StringUtils.isNotEmpty(getTextField().getText()) && textFieldNotSynchWithEditedObject()) {
							if (_selectorPanel instanceof BindingSelectorPanel) {
								BindingSelectorPanel selectorPanel = (BindingSelectorPanel) _selectorPanel;
								if (selectorPanel.isKeyPathFromTextASubKeyPath(getTextField().getText())
										&& selectorPanel.isKeyPathFromPanelValid()) {
									setEditedObject(selectorPanel.makeBindingValueFromPanel());
								} else {
									String input = getTextField().getText();
									if (input.indexOf(".") > -1) {
										String pathIgnoringLastPart = input.substring(0, input.lastIndexOf("."));
										if (isKeyPathValid(pathIgnoringLastPart)) {
											String inexitingPart = input.substring(input.lastIndexOf(".") + 1);
											DMType hostType = selectorPanel.getEndingTypeForSubPath(pathIgnoringLastPart);
											if (hostType != null && hostType.getBaseEntity() != null
													&& !hostType.getBaseEntity().getIsReadOnly()) {
												if (hostType.getBaseEntity() instanceof DMEOEntity) {
													try {
														askForCreationOfNewEntryInEOEntity(pathIgnoringLastPart, inexitingPart, hostType,
																getEditedObject().getBindingDefinition().getType());
													} catch (EOAccessException ex) {
														// TODO: handle exception
														ex.printStackTrace();
													}
												} else {
													askForCreationOfNewEntry(pathIgnoringLastPart, inexitingPart, hostType,
															getEditedObject().getBindingDefinition().getType());
												}
											}
										}
									}
								}
							}
						}
						_selectorPanel.processEnterPressed();
						e.consume();
					}

					else if (e.getKeyCode() == KeyEvent.VK_UP) {
						_selectorPanel.processUpPressed();
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						_selectorPanel.processDownPressed();
						e.consume();
					}
				}
			}
		};

		documentListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				textEdited(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				textEdited(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textEdited(e);
			}

			public void textEdited(DocumentEvent e) {
				if (isProgrammaticalySet()) {
					return;
				}
				String textValue = getTextField().getText();
				synchronizeWithTextFieldValue(textValue);
			}
		};

		getTextField().addKeyListener(shortcutsKeyAdapter);
		getTextField().getDocument().addDocumentListener(documentListener);

		setEditedObjectAndUpdateBDAndOwner(editedObject);

	}

	@Override
	public void delete() {
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	protected void synchronizeWithTextFieldValue(String textValue) {
		if (getProject() == null) {
			return;
		}

		try {
			isUpdatingModel = true;

			AbstractBinding newEditedBinding = makeBindingFromString(textValue);

			if (newEditedBinding != null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Decoding binding as " + newEditedBinding);
				}
				if (newEditedBinding.isBindingValid()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Decoded as VALID binding: " + newEditedBinding);
					}
					if (!newEditedBinding.equals(getEditedObject())) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("This is a new one, i take this");
						}
						setEditedObject(newEditedBinding);
						return;
					} else {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Skipping as it represents the same binding");
						}
						getTextField().setForeground(Color.BLACK);
						return;
					}
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Decoded as INVALID binding: " + newEditedBinding + " trying to synchronize panel");
					}
					getTextField().setForeground(Color.RED);
					if (_selectorPanel != null) {
						_selectorPanel.synchronizePanelWithTextFieldValue(textValue);
					}
					return;
				}
			}

			else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Couldn't decode as binding, trying to synchronize panel anyway");
				}
				getTextField().setForeground(Color.RED);
				if (_selectorPanel != null) {
					_selectorPanel.synchronizePanelWithTextFieldValue(textValue);
				}
				return;
			}

		} finally {
			isUpdatingModel = false;
		}

	}

	public void setEditedObjectAndUpdateBDAndOwner(AbstractBinding object) {
		setEditedObject(object);
		if (object != null) {
			if (object.getBindingDefinition() != null) {
				setBindingDefinition(object.getBindingDefinition());
			}
			if (object.getOwner() != null) {
				setBindable((Bindable) object.getOwner());
			}
		}
	}

	@Override
	public void setEditedObject(AbstractBinding object) {
		setEditedObject(object, true);
	}

	public void setEditedObject(AbstractBinding object, boolean updateBindingSelectionMode) {
		// logger.info("setEditedObject() with "+object);
		if (updateBindingSelectionMode) {
			if (object != null) {
				object = checkIfDisplayModeShouldChange(object, false);
			} else {
				activateNormalBindingMode();
			}
		}
		super.setEditedObject(object);

		if (getEditedObject() != null && getEditedObject().isBindingValid()) {
			getTextField().setForeground(Color.BLACK);
		} else {
			getTextField().setForeground(Color.RED);
		}

	}

	AbstractBinding checkIfDisplayModeShouldChange(AbstractBinding object, boolean setValueAsNewEditedValue) {
		AbstractBinding returned = object;

		EditionMode oldEditionMode = editionMode;
		EditionMode newEditionMode = editionMode;

		if (object != null) {
			if (object instanceof BindingExpression && ((BindingExpression) object).getExpression() != null) {
				Expression exp = ((BindingExpression) object).getExpression();
				if (exp instanceof BindingValueConstant) {
					returned = ((BindingValueConstant) exp).getStaticBinding();
				}
				if (exp instanceof BindingValueVariable) {
					returned = ((BindingValueVariable) exp).getBindingValue();
				}
				if (exp instanceof BindingValueFunction) {
					returned = ((BindingValueFunction) exp).getBindingValue();
				}
			}
		}

		if (returned instanceof StaticBinding) {
			newEditionMode = EditionMode.STATIC_BINDING;
		}
		if (returned instanceof TranstypedBinding) {
			newEditionMode = EditionMode.TRANSTYPED_BINDING;
		}
		if (returned instanceof BindingExpression) {
			newEditionMode = EditionMode.BINDING_EXPRESSION;
		}
		if (returned instanceof BindingValue) {
			if (((BindingValue) returned).isCompoundBinding()) {
				newEditionMode = EditionMode.COMPOUND_BINDING;
			} else if (oldEditionMode != EditionMode.NORMAL_BINDING && oldEditionMode != EditionMode.COMPOUND_BINDING) {
				newEditionMode = EditionMode.NORMAL_BINDING;
			}
		}
		if (returned == null) {
			newEditionMode = EditionMode.NORMAL_BINDING;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("DISPLAY_MODE was: " + oldEditionMode + " is now " + newEditionMode);
		}
		if (oldEditionMode.useCommonPanel() != newEditionMode.useCommonPanel()) {
			if (newEditionMode.useCommonPanel()) {
				if (newEditionMode == EditionMode.COMPOUND_BINDING) {
					activateCompoundBindingMode();
				} else {
					activateNormalBindingMode();
				}
			} else if (object instanceof BindingExpression) {
				activateBindingExpressionMode((BindingExpression) object);
			} else if (object instanceof TranstypedBinding) {
				activateTranstypedBindingMode();
			}
		}
		if (oldEditionMode != EditionMode.COMPOUND_BINDING && newEditionMode == EditionMode.COMPOUND_BINDING) {
			activateCompoundBindingMode();
		}

		editionMode = newEditionMode;

		// Should i change edited object ???
		if (returned != object && setValueAsNewEditedValue) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Switching edited object from " + object + " to " + returned);
			}
			_editedObject = returned;
			updateCustomPanel(getEditedObject());
		}

		return returned;
	}

	public void askForCreationOfNewEntry(String pathToEntity, String newPropertyName, DMType hostType, DMType type) {
		final DMEntity hostEntity = hostType.getBaseEntity();

		// logger.info("askForCreationOfNewEntry with pathToEntity="+pathToEntity+" inexitingPart="+newPropertyName+" hostType="+hostType+" type="+type);

		ReadOnlyTextFieldParameter parentEntity = new ReadOnlyTextFieldParameter("parentEntity", "creates_entry_in_entity",
				hostEntity.getFullQualifiedName());

		final TextFieldParameter newPropertyNameParam = new TextFieldParameter("propertyName", "property_name", newPropertyName);

		final DMTypeParameter newPropertyTypeParam = new DMTypeParameter("propertyType", "property_type", type);

		ChoiceListParameter<DMPropertyImplementationType> newPropertyImplementationType = new ChoiceListParameter<DMPropertyImplementationType>(
				"implementation", "implementation", hostEntity.getPropertyDefaultImplementationType());
		newPropertyImplementationType.setFormatter("localizedName");
		newPropertyImplementationType.setShowReset(false);

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(hostType.getProject(), null,
				FlexoLocalization.localizedForKey("creates_new_data_entry"),
				FlexoLocalization.localizedForKey("confirm_parameters_for_new_data_entry_creation"),
				new AskParametersDialog.ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						if (model.objectForKey("propertyName") == null || ((String) model.objectForKey("propertyName")).trim().equals("")) {
							errorMessage = FlexoLocalization.localizedForKey("property_name_must_be_non_empty");
							return false;
						} else if (model.objectForKey("propertyName") != null
								&& hostEntity.getDeclaredProperty((String) model.objectForKey("propertyName")) != null) {
							errorMessage = FlexoLocalization
									.localizedForKey("a_property_with_this_name_is_already_declared_in_this_entity");
							return false;
						} else if (model.objectForKey("propertyType") == null) {
							errorMessage = FlexoLocalization.localizedForKey("no_type_defined_for_this_new_property");
							return false;
						}
						return true;
					}
				}, parentEntity, newPropertyNameParam, newPropertyTypeParam, newPropertyImplementationType);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {

			DMProperty newProperty = hostEntity.createDMProperty(newPropertyNameParam.getValue(), newPropertyTypeParam.getValue(),
					newPropertyImplementationType.getValue());
			if (getEditedObject() == null) {
				setEditedObject(makeBinding());
			}
			if (getBindable() != null) {
				((FlexoModelObject) getBindable()).getProject().getBindingValueConverter().setBindable(getBindable());
			}
			// BindingValue.setBindableForConverter(getBindable());
			BindingValue newBindingValue = (BindingValue) getEditedObject().getConverter().convertFromString(pathToEntity);
			newBindingValue.addBindingPathElement(newProperty);

			/*logger.info("getBindingDefinition()="+getBindingDefinition());
			logger.info("getBindingDefinition().getType()="+getBindingDefinition().getType());
			logger.info("newBindingValue.getAccessedType()="+newBindingValue.getAccessedType());
			logger.info("type="+type);
			logger.info("getBindingDefinition().getType().isAssignableFrom(newBindingValue.getAccessedType())="+getBindingDefinition().getType().isAssignableFrom(newBindingValue.getAccessedType()));*/

			if (getEditedObject().getBindingDefinition() instanceof WidgetBindingDefinition
					&& newProperty.getEntity() instanceof ComponentDMEntity) {
				((ComponentDMEntity) newProperty.getEntity()).setBindable(newProperty, false);
			}

			if (getBindingDefinition() != null && getBindingDefinition().getType() != null) {
				if (getBindingDefinition().getType().isAssignableFrom(newBindingValue.getAccessedType(), true)) {
					newBindingValue.connect();
				}
			}
			setEditedObject(newBindingValue);
			_selectorPanel.update();
			apply();
		}

		/*
		JFrame father = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, BindingSelector.this);
		NewEntryApprovalDialog dialog = new NewEntryApprovalDialog(father,inexitingPart,hostType.getBaseEntity());
		if (dialog.getStatus() == NewEntryApprovalDialog.CANCEL)
		    return;
		DMPropertyImplementationType implementationType = dialog.getEditedImplementationType();
		DMProperty newProperty = hostType.getBaseEntity().createDMProperty(inexitingPart, type, implementationType);
		//_selectorPanel.refreshColumListModel(hostType);
		if(getEditedObject()==null)setEditedObject(makeBindingValue());
		BindingValue.setBindableForConverter(getBindable());
		setEditedObject((BindingValue)getEditedObject().getConverter().convertFromString(pathToEntity));
		if(getEditedObject().getBindingDefinition() instanceof WidgetBindingDefinition &&
				newProperty.getEntity() instanceof ComponentDMEntity){
			((ComponentDMEntity)newProperty.getEntity()).setBindable(newProperty, false);
		}
		if(newProperty!=null) getEditedObject().addBindingPathElementAfterFirstPathElementOfType(hostType.getBaseEntity(),newProperty);
		_textField.setText(renderedString(getEditedObject()));
		_selectorPanel.update();
		apply();*/
	}

	public void askForCreationOfNewEntryInEOEntity(String pathToEntity, String newPropertyName, DMType hostType, DMType type)
			throws EOAccessException {
		final DMEOEntity hostEntity = (DMEOEntity) hostType.getBaseEntity();

		// logger.info("askForCreationOfNewEntryInEOEntity with pathToEntity="+pathToEntity+" inexitingPart="+newPropertyName+" hostType="+hostType+" type="+type);

		ReadOnlyTextFieldParameter parentEntity = new ReadOnlyTextFieldParameter("parentEntity", "creates_entry_in_entity",
				hostEntity.getFullQualifiedName());

		final TextFieldParameter newPropertyNameParam = new TextFieldParameter("propertyName", "property_name", newPropertyName);

		Vector<DMEOPrototype> allEOPrototypes = hostType.getProject().getDataModel().getAllEOPrototypes();
		final DynamicDropDownParameter<DMEOPrototype> prototypeParam = new DynamicDropDownParameter<DMEOPrototype>("prototype",
				"prototype", allEOPrototypes, null);
		prototypeParam.setFormatter("name");
		prototypeParam.setShowReset(false);

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(hostType.getProject(), null,
				FlexoLocalization.localizedForKey("creates_new_data_entry"),
				FlexoLocalization.localizedForKey("confirm_parameters_for_new_data_entry_creation"),
				new AskParametersDialog.ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						if (model.objectForKey("propertyName") == null || ((String) model.objectForKey("propertyName")).trim().equals("")) {
							errorMessage = FlexoLocalization.localizedForKey("property_name_must_be_non_empty");
							return false;
						} else if (model.objectForKey("propertyName") != null
								&& hostEntity.getDeclaredProperty((String) model.objectForKey("propertyName")) != null) {
							errorMessage = FlexoLocalization
									.localizedForKey("a_property_with_this_name_is_already_declared_in_this_entity");
							return false;
						} else if (model.objectForKey("prototype") == null) {
							errorMessage = FlexoLocalization.localizedForKey("no_prototype_defined_for_this_new_attribute");
							return false;
						}
						return true;
					}
				}, parentEntity, newPropertyNameParam, prototypeParam);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {

			DMEOAttribute newProperty = DMEOAttribute.createsNewDMEOAttribute(hostEntity.getDMModel(), hostEntity,
					newPropertyNameParam.getValue(), false, true, DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY);
			newProperty.setPrototype(prototypeParam.getValue());

			if (getEditedObject() == null) {
				setEditedObject(makeBinding());
			}
			if (getBindable() != null) {
				((FlexoModelObject) getBindable()).getProject().getBindingValueConverter().setBindable(getBindable());
			}
			BindingValue newBindingValue = (BindingValue) getEditedObject().getConverter().convertFromString(pathToEntity);
			newBindingValue.addBindingPathElement(newProperty);
			/*logger.info("getBindingDefinition()="+getBindingDefinition());
			logger.info("getBindingDefinition().getType()="+getBindingDefinition().getType());
			logger.info("newBindingValue.getAccessedType()="+newBindingValue.getAccessedType());
			logger.info("type="+type);
			logger.info("getBindingDefinition().getType().isAssignableFrom(newBindingValue.getAccessedType())="+getBindingDefinition().getType().isAssignableFrom(newBindingValue.getAccessedType()));*/

			if (getEditedObject().getBindingDefinition() instanceof WidgetBindingDefinition
					&& newProperty.getEntity() instanceof ComponentDMEntity) {
				((ComponentDMEntity) newProperty.getEntity()).setBindable(newProperty, false);
			}

			if (getBindingDefinition() != null && getBindingDefinition().getType() != null) {
				if (getBindingDefinition().getType().isAssignableFrom(newBindingValue.getAccessedType(), true)) {
					newBindingValue.connect();
				}
			}
			setEditedObject(newBindingValue);
			_selectorPanel.update();
			apply();
		}

	}

	boolean isKeyPathValid(String pathIgnoringLastPart) {
		if (!(_selectorPanel instanceof BindingSelectorPanel)) {
			return false;
		}
		StringTokenizer token = new StringTokenizer(pathIgnoringLastPart, ".", false);
		Object obj = null;
		int i = 0;
		while (token.hasMoreTokens()) {
			obj = ((BindingSelectorPanel) _selectorPanel).findElementEquals(((BindingSelectorPanel) _selectorPanel).listAtIndex(i)
					.getModel(), token.nextToken());
			if (obj == null) {
				return false;
			}
			i++;
		}
		return true;
	}

	@Override
	public void fireEditedObjectChanged() {
		updateCustomPanel(getEditedObject());
		if (!getIsUpdatingModel()) {
			_isProgrammaticalySet = true;
			getTextField().setText(renderedString(getEditedObject()));
			if (getEditedObject() != null) {
				getTextField().setForeground(getEditedObject().isBindingValid() ? Color.BLACK : Color.RED);
			} else {
				getTextField().setForeground(Color.RED);
			}
			_isProgrammaticalySet = false;
		}
	}

	public boolean getAllowsEntryCreation() {
		return _allowsEntryCreation;
	}

	@CustomComponentParameter(name = "allowsEntryCreation", type = CustomComponentParameter.Type.OPTIONAL)
	public void setAllowsEntryCreation(boolean aFlag) {
		if (aFlag) {
			if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel && _allowsEntryCreation == false) {
				((BindingSelectorPanel) _selectorPanel).addNewEntryCreationPanel();
			}
			_allowsEntryCreation = true;
		} else {
			if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel && _allowsEntryCreation == true) {
				((BindingSelectorPanel) _selectorPanel).removeNewEntryCreationPanel();
			}
			_allowsEntryCreation = false;
		}
	}

	/*public void allowsNewEntryCreation()
	{
		if ((_selectorPanel != null) && (_selectorPanel instanceof BindingSelectorPanel) && (_allowsEntryCreation == false)) {
			((BindingSelectorPanel)_selectorPanel).addNewEntryCreationPanel();
		}
		_allowsEntryCreation = true;
	}

	public void denyNewEntryCreation()
	{
		if ((_selectorPanel != null) && (_selectorPanel instanceof BindingSelectorPanel) && (_allowsEntryCreation == true)) {
			((BindingSelectorPanel)_selectorPanel).removeNewEntryCreationPanel();
		}
		_allowsEntryCreation = false;
	}*/

	public boolean getAllowsCompoundBindings() {
		// if (getBindingDefinition() != null && getBindingDefinition().getIsSettable()) return false;
		return _allowsCompoundBindings;
	}

	@CustomComponentParameter(name = "allowsCompoundBindings", type = CustomComponentParameter.Type.OPTIONAL)
	public void setAllowsCompoundBindings(boolean allowsCompoundBindings) {
		_allowsCompoundBindings = allowsCompoundBindings;
		rebuildPopup();
	}

	public boolean getAllowsBindingExpressions() {
		if (getBindingDefinition() != null
				&& (getBindingDefinition().getIsSettable() || getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE)) {
			return false;
		}
		return _allowsBindingExpressions;
	}

	@CustomComponentParameter(name = "allowsBindingExpressions", type = CustomComponentParameter.Type.OPTIONAL)
	public void setAllowsBindingExpressions(boolean allowsBindingExpressions) {
		_allowsBindingExpressions = allowsBindingExpressions;
		rebuildPopup();
	}

	public boolean getAllowsStaticValues() {
		if (getBindingDefinition() != null
				&& (getBindingDefinition().getIsSettable() || getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE)) {
			return false;
		}
		return _allowsStaticValues;
	}

	@CustomComponentParameter(name = "allowsStaticValues", type = CustomComponentParameter.Type.OPTIONAL)
	public void setAllowsStaticValues(boolean allowsStaticValues) {
		_allowsStaticValues = allowsStaticValues;
		rebuildPopup();
	}

	public boolean getAllowsTranstypers() {
		if (getBindingDefinition() != null
				&& (getBindingDefinition().getIsSettable() || getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE)) {
			return false;
		}
		return _allowsTranstypers;
	}

	@CustomComponentParameter(name = "allowsTranstypers", type = CustomComponentParameter.Type.OPTIONAL)
	public void setAllowsTranstypers(boolean allowsTranstypers) {
		_allowsTranstypers = allowsTranstypers;
		rebuildPopup();
	}

	public boolean areSomeTranstyperAvailable() {
		return getBindingDefinition() != null && getBindingDefinition().getType() != null && getProject() != null
				&& getProject().getDataModel().getDMTranstypers(getBindingDefinition().getType()).size() > 0;
	}

	private void rebuildPopup() {
		boolean showAgain = false;
		if (popupIsShown()) {
			showAgain = true;
			closePopup(false);
		}
		deletePopup();
		if (showAgain) {
			openPopup();
			updateCustomPanel(getEditedObject());
		}
	}

	public void activateCompoundBindingMode() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("ActivateCompoundBindingMode() getEditedObject()=" + getEditedObject() + " editionMode=" + editionMode
					+ " popupIsShown()=" + popupIsShown() + " _selectorPanel=" + _selectorPanel);
		}
		if (_selectorPanel != null && editionMode != EditionMode.COMPOUND_BINDING) {
			editionMode = EditionMode.COMPOUND_BINDING;
			boolean showAgain = false;
			if (popupIsShown()) {
				showAgain = true;
				closePopup(false);
			}
			if (getEditedObject() != null && !(getEditedObject() instanceof BindingValue)) {
				_editedObject = makeBinding(); // I dont want to notify it !!!
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
		editionMode = EditionMode.COMPOUND_BINDING;
	}

	public void activateTranstypedBindingMode() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("activateTranstypedBindingMode() getEditedObject()=" + getEditedObject() + " editionMode=" + editionMode
					+ " popupIsShown()=" + popupIsShown() + " _selectorPanel=" + _selectorPanel);
		}
		if (_selectorPanel != null && editionMode != EditionMode.TRANSTYPED_BINDING) {
			editionMode = EditionMode.TRANSTYPED_BINDING;
			boolean showAgain = false;
			if (popupIsShown()) {
				showAgain = true;
				closePopup(false);
			}
			if (getEditedObject() != null && !(getEditedObject() instanceof TranstypedBinding)) {
				_editedObject = makeBinding(); // I dont want to notify it !!!
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
		editionMode = EditionMode.TRANSTYPED_BINDING;
	}

	public void activateNormalBindingMode() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("activateNormalBindingMode()");
		}
		if (_selectorPanel != null && editionMode != EditionMode.NORMAL_BINDING) {
			editionMode = EditionMode.NORMAL_BINDING;
			boolean showAgain = false;
			if (popupIsShown()) {
				showAgain = true;
				closePopup(false);
			}
			if (getEditedObject() != null && !(getEditedObject() instanceof BindingValue)) {
				_editedObject = makeBinding(); // I dont want to notify it !!!
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
		editionMode = EditionMode.NORMAL_BINDING;
	}

	public void activateBindingExpressionMode(BindingExpression bindingExpression) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("activateBindingExpressionMode()");
		}
		if (_selectorPanel != null) {
			editionMode = EditionMode.BINDING_EXPRESSION;
			boolean showAgain = false;
			if (popupIsShown()) {
				showAgain = true;
				closePopup(false);
			}
			if (bindingExpression != null) {
				_editedObject = bindingExpression;
			} else {
				_editedObject = new BindingExpression(getBindingDefinition(), (FlexoModelObject) getBindable()); // I dont want to notify it
																													// !!!
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
	}

	@Override
	protected void deleteCustomPanel() {
		super.deleteCustomPanel();
		_selectorPanel.delete();
		_selectorPanel = null;
	}

	@Override
	public void setRevertValue(AbstractBinding oldValue) {
		if (oldValue != null) {
			_revertBindingValue = oldValue.clone();
		} else {
			_revertBindingValue = null;
		}
	}

	@Override
	public AbstractBinding getRevertValue() {
		return _revertBindingValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(AbstractBinding editedObject) {
		if (editionMode == EditionMode.BINDING_EXPRESSION) {
			_selectorPanel = new BindingExpressionSelectorPanel(this);
			_selectorPanel.init();
		}
		/*else if (editionMode == EditionMode.NEW_ENTRY) {
			_selectorPanel = new BindingExpressionSelectorPanel(this);
			_selectorPanel.init();sqddqs
		}*/
		else {
			// When creating use normal mode
			if (editedObject instanceof StaticBinding) {
				editionMode = EditionMode.NORMAL_BINDING;
			}
			_selectorPanel = new BindingSelectorPanel(this);
			_selectorPanel.init();
		}
		refreshBindingModel();
		return _selectorPanel;
	}

	public void refreshBindingModel() {
		if (_bindable != null && _selectorPanel != null) {
			// _selectorPanel.setBindingModel(_bindable.getBindingModel());
			_selectorPanel.update();
		}
	}

	@Override
	public void updateCustomPanel(AbstractBinding editedObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateCustomPanel() with " + editedObject);
		}
		if (editedObject != null && editedObject instanceof BindingValue && ((BindingValue) editedObject).isCompoundBinding()) {
			activateCompoundBindingMode();
		}
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
	}

	public void resetMethodCallPanel() {
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			((BindingSelectorPanel) _selectorPanel).resetMethodCallPanel();
		}
	}

	@Override
	public String renderedString(AbstractBinding editedObject) {
		if (editedObject != null) {
			return editedObject.getStringRepresentation();
		}
		return "";
	}

	public FlexoProject getProject() {
		if (getBindable() instanceof FlexoModelObject) {
			return ((FlexoModelObject) getBindable()).getProject();
		}
		if (getBindingDefinition() != null) {
			return getBindingDefinition().getProject();
		}
		return null;
	}

	public Bindable getBindable() {
		return _bindable;
	}

	@CustomComponentParameter(name = "bindable", type = CustomComponentParameter.Type.MANDATORY)
	public void setBindable(Bindable bindable) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setBindable with " + bindable);
		}
		_bindable = bindable;
		if (bindable != null && _selectorPanel != null) {
			_selectorPanel.fireBindableChanged();
		}
		// getCustomPanel().setBindingModel(bindable.getBindingModel());
		updateTextFieldProgrammaticaly();
	}

	/*public void setCustomBindingModel(BindingModel aBindingModel)
	{
		setBindingModel(aBindingModel);
	}*/

	/*private BindingDefinition _bindingDefinitionForSelector = null;

	public BindingDefinition getBindingDefinition()
	{
		if (getCustomPanel() != null)
			return getCustomPanel().getBindingDefinition();
		return _bindingDefinitionForSelector;dqsdsq
	}

	public void setBindingDefinition(BindingDefinition bindingDefinition)
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("setBindingDefinition with " + bindingDefinition);
		getCustomPanel().setBindingDefinition(bindingDefinition);
	}*/

	BindingDefinition _bindingDefinition;

	// BindingModel _bindingModel;

	public BindingDefinition getBindingDefinition() {
		return _bindingDefinition;
	}

	public void setBindingDefinition(BindingDefinition bindingDefinition) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(toString() + "Setting new binding definition: " + bindingDefinition + " old: " + _bindingDefinition);
		}
		if (bindingDefinition != _bindingDefinition) {
			_bindingDefinition = bindingDefinition;
			AbstractBinding bindingValue = getEditedObject();
			if (bindingValue != null) {
				bindingValue.setBindingDefinition(bindingDefinition);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("set BD " + bindingDefinition + " for BV " + bindingValue);
				}
			}
			if (_selectorPanel != null) {
				_selectorPanel.fireBindingDefinitionChanged();
			}
		}
		if (getProject() != null && bindingDefinition != null && logger.isLoggable(Level.FINE)) {
			logger.fine("Accessible transtypers for this type: "
					+ getProject().getDataModel().getDMTranstypers(bindingDefinition.getType()));
		}
	}

	public BindingModel getBindingModel() {
		if (getBindable() != null) {
			return getBindable().getBindingModel();
		}
		return null;
	}

	@Override
	public AbstractBindingSelectorPanel getCustomPanel() {
		return (AbstractBindingSelectorPanel) super.getCustomPanel();
	}

	protected BindingExpression makeBindingExpression() {
		BindingExpression returned = new BindingExpression(getBindingDefinition(), (FlexoModelObject) getBindable());
		if (getProject() != null) {
			returned.setExpression(new BindingValueVariable("", getBindable()));
		}
		return returned;
	}

	protected AbstractBinding makeBinding() {
		AbstractBinding returned = null;
		if (editionMode == EditionMode.BINDING_EXPRESSION) {
			if (getBindingDefinition() != null && getBindable() != null) {
				returned = makeBindingExpression();
			}
		} else if (editionMode == EditionMode.STATIC_BINDING) {
			if (getBindingDefinition() != null && getBindable() != null) {
				if (getBindingDefinition().getType() != null) {
					if (getBindingDefinition().getType().isBoolean()) {
						returned = new BooleanStaticBinding(getBindingDefinition(), (FlexoModelObject) getBindable(), false);
					} else if (getBindingDefinition().getType().isInteger() || getBindingDefinition().getType().isLong()
							|| getBindingDefinition().getType().isShort() || getBindingDefinition().getType().isChar()
							|| getBindingDefinition().getType().isByte()) {
						returned = new IntegerStaticBinding(getBindingDefinition(), (FlexoModelObject) getBindable(), 0);
					} else if (getBindingDefinition().getType().isFloat() || getBindingDefinition().getType().isDouble()) {
						returned = new FloatStaticBinding(getBindingDefinition(), (FlexoModelObject) getBindable(), 0.0);
					} else if (getBindingDefinition().getType().isString()) {
						returned = new StringStaticBinding(getBindingDefinition(), (FlexoModelObject) getBindable(), "");
					} else if (getBindingDefinition().getType().isDate()) {
						returned = new DateStaticBinding(getBindingDefinition(), (FlexoModelObject) getBindable(), new Date());
					} else if (getBindingDefinition().getType().isDuration()) {
						returned = new DurationStaticBinding(getBindingDefinition(), (FlexoModelObject) getBindable(), new Duration(0,
								DurationUnit.SECONDS));
					}
				}
			}
		} else if (editionMode == EditionMode.TRANSTYPED_BINDING) { // Transtyped binding
			if (getBindingDefinition() != null && getBindable() != null) {
				returned = new TranstypedBinding(getBindingDefinition(), (FlexoModelObject) getBindable());
			}
		} else if (editionMode == EditionMode.NORMAL_BINDING || editionMode == EditionMode.COMPOUND_BINDING) { // Normal or compound binding
			if (getBindingDefinition() != null && getBindable() != null) {
				returned = new BindingValue(getBindingDefinition(), (FlexoModelObject) getBindable());
			}
		}
		return returned;
	}

	AbstractBinding recreateBindingValue() {
		setEditedObject(makeBinding());
		logger.info("Recreating Binding with mode " + editionMode + " as " + getEditedObject());
		return getEditedObject();
	}

	Bindable _bindable;

	protected abstract class AbstractBindingSelectorPanel extends ResizablePanel {
		protected abstract void synchronizePanelWithTextFieldValue(String textValue);

		protected abstract void init();

		protected abstract void delete();

		protected abstract void update();

		protected abstract void fireBindingDefinitionChanged();

		protected abstract void fireBindableChanged();

		protected abstract void processTabPressed();

		protected abstract void processDownPressed();

		protected abstract void processUpPressed();

		protected abstract void processEnterPressed();

		protected abstract void processBackspace();

		protected abstract void processDelete();

		protected abstract void willApply();

	}

	@Override
	protected void openPopup() {
		if (_selectorPanel != null) {
			if (_selectorPanel instanceof BindingSelectorPanel) {
				JList list = ((BindingSelectorPanel) _selectorPanel).listAtIndex(0);
				if (list.getModel().getSize() == 1) {
					list.setSelectedIndex(0);
				}
			}
		}
		super.openPopup();
		if (_selectorPanel != null) {
			ButtonsControlPanel controlPanel = null;
			if (_selectorPanel instanceof BindingSelectorPanel) {
				controlPanel = ((BindingSelectorPanel) _selectorPanel)._controlPanel;
			} else if (_selectorPanel instanceof BindingExpressionSelectorPanel) {
				controlPanel = ((BindingExpressionSelectorPanel) _selectorPanel)._controlPanel;
			}
			if (controlPanel != null) {
				controlPanel.applyFocusTraversablePolicyTo(controlPanel, false);
			}
		}
	}

	@Override
	public void closePopup() {
		super.closePopup();
		// logger.info("closePopup()");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextField().requestFocusInWindow();
			}
		});
	}

	@Override
	public void apply() {
		if (_selectorPanel != null) {
			_selectorPanel.willApply();
		}
		AbstractBinding bindingValue = getEditedObject();
		if (bindingValue != null) {
			if (bindingValue.isBindingValid()) {
				if (bindingValue instanceof BindingValue) {
					((BindingValue) bindingValue).connect();
				}
				getTextField().setForeground(Color.BLACK);
			} else {
				getTextField().setForeground(Color.RED);
			}
			_revertBindingValue = bindingValue.clone();
		} else {
			_revertBindingValue = null;
		}
		updateTextFieldProgrammaticaly();
		if (popupIsShown()) {
			closePopup();
		}
		super.apply();
	}

	@Override
	public void cancel() {
		setEditedObject(_revertBindingValue);
		closePopup();
		super.cancel();
	}

	public DMProperty createsNewEntry(String newPropertyName, DMType newPropertyType, DMPropertyImplementationType implementationType,
			DMEntity parentEntity) {
		// Must be overriden if used
		return null;
	}

	@Override
	protected void pointerLeavesPopup() {
		cancel();
	}

	public boolean getIsUpdatingModel() {
		return isUpdatingModel;
	}

	public void setUpdatingModel(boolean isUpdatingModelFlag) {
		this.isUpdatingModel = isUpdatingModelFlag;
	}

	protected AbstractListModel getRootListModel() {
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			return ((BindingSelectorPanel) _selectorPanel).getRootColumnListModel();
		}
		return null;
	}

	protected final AbstractListModel getListModelFor(DMType type) {
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			return ((BindingSelectorPanel) _selectorPanel).getColumnListModel(type);
		}
		return null;
	}

	protected void valueSelected(int index, JList list, AbstractBinding binding) {
		if (!(binding instanceof BindingValue)) {
			editionMode = EditionMode.NORMAL_BINDING;
			binding = makeBinding(); // Should create a BindingValue instance !!!
			if (!(binding instanceof BindingValue)) {
				logger.severe("Should never happen: valueSelected() called for a non-BindingValue instance !");
				return;
			}
		}
		BindingValue bindingValue = (BindingValue) binding;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Value selected: index=" + index + " list=" + list + " bindingValue=" + bindingValue);
		}
		org.openflexo.components.widget.binding.BindingSelectorPanel.BindingColumnElement selectedValue = (org.openflexo.components.widget.binding.BindingSelectorPanel.BindingColumnElement) list
				.getSelectedValue();
		if (index == 0 && selectedValue.getElement() instanceof BindingVariable) {
			if (list.getSelectedValue() != bindingValue.getBindingVariable()) {
				bindingValue.setBindingVariable((BindingVariable) selectedValue.getElement());
				setEditedObject(bindingValue);
				fireEditedObjectChanged();
			}
		} else {
			if (selectedValue.getElement() instanceof DMProperty) {
				if (selectedValue != bindingValue.getBindingPathElementAtIndex(index - 1)) {
					bindingValue.setBindingPathElementAtIndex((DMProperty) selectedValue.getElement(), index - 1);
					setEditedObject(bindingValue);
					fireEditedObjectChanged();
				}
			} else if (selectedValue.getElement() instanceof DMMethod && editionMode == EditionMode.COMPOUND_BINDING) {
				BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(index - 1);
				if (!(currentElement instanceof MethodCall) || ((MethodCall) currentElement).getMethod() != selectedValue.getElement()) {
					DMMethod method = (DMMethod) selectedValue.getElement();
					MethodCall newMethodCall = new MethodCall(bindingValue, method);
					bindingValue.setBindingPathElementAtIndex(newMethodCall, index - 1);
					setEditedObject(bindingValue);
					fireEditedObjectChanged();
				}
			}
		}
	}

	boolean isAcceptableStaticBindingValue(String stringValue) {
		if (getProject() == null || getBindingDefinition() == null || getBindingDefinition().getType() == null) {
			return false;
		}
		StaticBinding b = makeStaticBindingFromString(stringValue);
		if (b == null) {
			return false;
		}
		if (getBindingDefinition().getType().isObject() && !stringValue.endsWith(".")) {
			return true;
		}
		if (getBindingDefinition().getType().isBoolean()) {
			return b instanceof BooleanStaticBinding;
		} else if (getBindingDefinition().getType().isInteger() || getBindingDefinition().getType().isLong()
				|| getBindingDefinition().getType().isShort() || getBindingDefinition().getType().isChar()
				|| getBindingDefinition().getType().isByte()) {
			return b instanceof IntegerStaticBinding;
		} else if (getBindingDefinition().getType().isFloat() || getBindingDefinition().getType().isDouble()) {
			if (stringValue.endsWith(".")) {
				return false;
			}
			return b instanceof IntegerStaticBinding || b instanceof FloatStaticBinding;
		} else if (getBindingDefinition().getType().isString()) {
			return b instanceof StringStaticBinding;
		} else if (getBindingDefinition().getType().isDate()) {
			return b instanceof DateStaticBinding;
		} else if (getBindingDefinition().getType().isDuration()) {
			return b instanceof DurationStaticBinding;
		}
		return false;
	}

	private boolean isAcceptableAsBeginningOfBooleanStaticBindingValue(String stringValue) {
		if (stringValue.length() > 0) {
			if (stringValue.length() <= 4 && "true".substring(0, stringValue.length()).equalsIgnoreCase(stringValue)) {
				return true;
			}
			if (stringValue.length() <= 5 && "false".substring(0, stringValue.length()).equalsIgnoreCase(stringValue)) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	boolean isAcceptableAsBeginningOfStringStaticBindingValue(String stringValue) {
		if (stringValue.length() > 0) {
			if (stringValue.indexOf("\"") == 0 || stringValue.indexOf("'") == 0) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	boolean isAcceptableAsBeginningOfStaticBindingValue(String stringValue) {
		// logger.info("isAcceptableAsBeginningOfStaticBindingValue for ? "+stringValue+" project="+getProject()+" bd="+getBindingDefinition());
		if (getProject() == null || getBindingDefinition() == null || getBindingDefinition().getType() == null) {
			return false;
		}

		if (stringValue.length() == 0) {
			return true;
		}

		if (getBindingDefinition().getType().isObject()) {
			// In this case, any of matching is enough
			return isAcceptableStaticBindingValue(stringValue)
					&& !stringValue.endsWith(".") // Special case to handle float on-the-fly
													// typing
					|| isAcceptableAsBeginningOfBooleanStaticBindingValue(stringValue)
					|| isAcceptableAsBeginningOfStringStaticBindingValue(stringValue);
		}

		if (getBindingDefinition().getType().isBoolean()) {
			return isAcceptableAsBeginningOfBooleanStaticBindingValue(stringValue);
		} else if (getBindingDefinition().getType().isInteger() || getBindingDefinition().getType().isLong()
				|| getBindingDefinition().getType().isShort() || getBindingDefinition().getType().isChar()
				|| getBindingDefinition().getType().isByte()) {
			return isAcceptableStaticBindingValue(stringValue);
		} else if (getBindingDefinition().getType().isFloat() || getBindingDefinition().getType().isDouble()) {
			if (stringValue.endsWith(".") && stringValue.length() > 1) {
				return isAcceptableStaticBindingValue(stringValue.substring(0, stringValue.length() - 1));
			}
			return isAcceptableStaticBindingValue(stringValue);
		} else if (getBindingDefinition().getType().isString()) {
			return isAcceptableAsBeginningOfStringStaticBindingValue(stringValue);
		} else if (getBindingDefinition().getType().isDate()) {
			return isAcceptableStaticBindingValue(stringValue);
		} else if (getBindingDefinition().getType().isDuration()) {
			return isAcceptableStaticBindingValue(stringValue);
		}
		return false;
	}

	StaticBinding makeStaticBindingFromString(String stringValue) {
		if (getProject() != null) {
			StaticBinding returned = getProject().getStaticBindingConverter().convertFromString(stringValue);
			if (returned == null) {
				return null;
			}
			returned.setOwner((FlexoModelObject) getBindable());
			returned.setBindingDefinition(getBindingDefinition());
			return returned;
		}
		return null;
	}

	AbstractBinding makeBindingFromString(String stringValue) {
		if (getProject() != null) {
			AbstractBindingStringConverter converter = getProject().getAbstractBindingConverter();
			converter.setWarnOnFailure(false);
			converter.setBindable(getBindable());
			AbstractBinding returned = converter.convertFromString(stringValue);
			if (returned == null) {
				return null;
			}
			returned.setOwner((FlexoModelObject) getBindable());
			returned.setBindingDefinition(getBindingDefinition());
			converter.setWarnOnFailure(true);
			return returned;
		}
		return null;
	}

	boolean textFieldSynchWithEditedObject() {
		if (StringUtils.isEmpty(getTextField().getText())) {
			return getEditedObject() == null || StringUtils.isEmpty(renderedString(getEditedObject()));
		}
		return getTextField().getText() != null && getTextField().getText().equals(renderedString(getEditedObject()));
	}

	boolean textFieldNotSynchWithEditedObject() {
		return !textFieldSynchWithEditedObject();
	}

	@Override
	public BindingSelector getJComponent() {
		return this;
	}

	@Override
	public Class<AbstractBinding> getRepresentedType() {
		return AbstractBinding.class;
	}

}