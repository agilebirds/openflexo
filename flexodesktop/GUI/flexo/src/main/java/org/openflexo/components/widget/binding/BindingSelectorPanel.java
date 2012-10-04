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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.AdvancedPrefs;
import org.openflexo.FlexoCst;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.tabular.TabularPanel;
import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.BindingValueColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.components.widget.binding.BindingSelector.EditionMode;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingExpression;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingValue.BindingPathElement;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.MethodCall;
import org.openflexo.foundation.bindings.MethodCall.MethodCallArgument;
import org.openflexo.foundation.bindings.StaticBinding;
import org.openflexo.foundation.bindings.TranstypedBinding;
import org.openflexo.foundation.bindings.TranstypedBinding.TranstypedBindingValue;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMRegExp;
import org.openflexo.foundation.dm.DMTranstyper;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMType.KindOfType;
import org.openflexo.foundation.dm.ProcessDMEntity;
import org.openflexo.foundation.dm.Typed;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.PropertiesReordered;
import org.openflexo.foundation.dm.dm.PropertyRegistered;
import org.openflexo.foundation.dm.dm.PropertyUnregistered;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.param.ChoiceListParameter;
import org.openflexo.foundation.param.DMTypeParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.StaticDropDownParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.swing.MouseOverButton;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

class BindingSelectorPanel extends BindingSelector.AbstractBindingSelectorPanel implements ListSelectionListener {

	/**
		 *
		 */
	final BindingSelector _bindingSelector;

	protected JPanel _browserPanel;

	ButtonsControlPanel _controlPanel;

	JButton _connectButton;
	JButton _cancelButton;
	JButton _resetButton;
	JButton _expressionButton;

	JButton _createsButton;

	private final Hashtable<DMType, BindingColumnListModel> _listModels;

	Vector<FilteredJList> _lists;

	protected int defaultVisibleColCount = 3;

	protected final EmptyColumnListModel EMPTY_MODEL = new EmptyColumnListModel();

	private BindingColumnListModel _rootBindingColumnListModel = null;

	JLabel currentTypeLabel;
	private JLabel searchedTypeLabel;
	private JTextArea bindingValueRepresentation;
	protected BindingColumnElement currentFocused = null;

	protected BindingSelectorPanel(BindingSelector bindingSelector) {
		bindingSelector.super();
		_bindingSelector = bindingSelector;
		_listModels = new Hashtable<DMType, BindingColumnListModel>();
		_rootBindingColumnListModel = null;
		_lists = new Vector<FilteredJList>();
	}

	@Override
	public void delete() {
		for (JList list : _lists) {
			list.removeListSelectionListener(this);
			list.setModel(null);
		}
		_lists.clear();
		_listModels.clear();
		_rootBindingColumnListModel = null;
		currentFocused = null;
	}

	public int getIndexOfList(BindingColumnListModel model) {
		for (int i = 0; i < _lists.size(); i++) {
			FilteredJList l = _lists.get(i);
			if (l.getModel() == model) {
				return i;
			}
		}
		return -1;
	}

	public DMEntity getAccessedEntity() {
		DMEntity reply = null;
		int i = 1;
		BindingColumnElement last = null;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			last = (BindingColumnElement) listAtIndex(i).getSelectedValue();
			i++;
		}
		if (last != null) {
			return last.getElement().getType().getBaseEntity();
		}
		return reply;
	}

	public BindingVariable getSelectedBindingVariable() {
		if (listAtIndex(0) != null && listAtIndex(0).getSelectedValue() != null) {
			return (BindingVariable) ((BindingColumnElement) listAtIndex(0).getSelectedValue()).getElement();
		} else if (listAtIndex(0) != null && listAtIndex(0).getModel().getSize() == 1) {
			return (BindingVariable) listAtIndex(0).getModel().getElementAt(0).getElement();
		} else {
			return null;
		}
	}

	@Deprecated
	private BindingColumnElement findElementMatching(ListModel listModel, String subPartialPath, Vector<Integer> pathElementIndex) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.getElementAt(i) instanceof BindingColumnElement
					&& ((BindingColumnElement) listModel.getElementAt(i)).getLabel().startsWith(subPartialPath)) {
				if (pathElementIndex.size() == 0) {
					pathElementIndex.add(i);
				} else {
					pathElementIndex.set(0, i);
				}
				return (BindingColumnElement) listModel.getElementAt(i);
			}
		}
		return null;
	}

	Vector<BindingColumnElement> findElementsMatching(BindingColumnListModel listModel, String subPartialPath) {
		Vector<BindingColumnElement> returned = new Vector<BindingColumnElement>();
		for (int i = 0; i < listModel.getUnfilteredSize(); i++) {
			if (listModel.getUnfilteredElementAt(i).getLabel().startsWith(subPartialPath)) {
				returned.add(listModel.getUnfilteredElementAt(i));
			}
		}
		return returned;
	}

	BindingColumnElement findElementEquals(ListModel listModel, String subPartialPath) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.getElementAt(i) instanceof BindingColumnElement) {
				if (((BindingColumnElement) listModel.getElementAt(i)).getLabel().equals(subPartialPath)) {
					return (BindingColumnElement) listModel.getElementAt(i);
				}
			}
		}
		return null;
	}

	public DMType getEndingTypeForSubPath(String pathIgnoringLastPart) {
		StringTokenizer token = new StringTokenizer(pathIgnoringLastPart, ".", false);
		Object obj = null;
		int i = 0;
		while (token.hasMoreTokens()) {
			obj = findElementEquals(listAtIndex(i).getModel(), token.nextToken());
			i++;
		}
		if (obj instanceof BindingColumnElement) {
			Typed element = ((BindingColumnElement) obj).getElement();
			if (element instanceof DMProperty) {
				return ((DMProperty) element).getType();
			}
			if (element instanceof BindingVariable) {
				return ((BindingVariable) element).getType();
			}
		}
		return null;
	}

	public void addNewEntryCreationPanel() {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (BindingSelectorPanel.this._bindingSelector.getBindingModel() == null) {
					BindingSelector.logger.warning("BindingModel is null");
					return;
				}

				final Vector<DMEntity> parentEntities = new Vector<DMEntity>();
				Hashtable<DMEntity, BindingValue> resultingBV = new Hashtable<DMEntity, BindingValue>();
				DMEntity currentEntity = null;

				if (BindingSelectorPanel.this._bindingSelector.editionMode != EditionMode.NORMAL_BINDING
						&& BindingSelectorPanel.this._bindingSelector.editionMode != EditionMode.COMPOUND_BINDING) {
					BindingSelectorPanel.this._bindingSelector.editionMode = EditionMode.NORMAL_BINDING;
				}

				if (BindingSelectorPanel.this._bindingSelector.getEditedObject() != null
						&& BindingSelectorPanel.this._bindingSelector.getEditedObject() instanceof BindingValue) {
					BindingValue bv = (BindingValue) BindingSelectorPanel.this._bindingSelector.getEditedObject();
					if (bv.getBindingVariable() != null && bv.getBindingVariable().getType() != null
							&& bv.getBindingVariable().getType().getBaseEntity() != null
							&& !bv.getBindingVariable().getType().getBaseEntity().getIsReadOnly()) {
						currentEntity = bv.getBindingVariable().getType().getBaseEntity();
						BindingValue bvInThisContext = (BindingValue) BindingSelectorPanel.this._bindingSelector.makeBinding();
						bvInThisContext.setBindingVariable(bv.getBindingVariable());
						parentEntities.add(currentEntity);
						resultingBV.put(currentEntity, bvInThisContext.clone());
						for (BindingPathElement bpe : bv.getBindingPath()) {
							if (bpe.getType() != null && bpe.getType().getBaseEntity() != null
									&& !bpe.getType().getBaseEntity().getIsReadOnly()
									&& !(bpe.getType().getBaseEntity() instanceof ProcessDMEntity)) {
								currentEntity = bpe.getType().getBaseEntity();
								bvInThisContext.addBindingPathElement(bpe);
								parentEntities.add(currentEntity);
								resultingBV.put(currentEntity, bvInThisContext.clone());
							}
						}
					}
				} else {
					for (int i = 0; i < BindingSelectorPanel.this._bindingSelector.getBindingModel().getBindingVariablesCount(); i++) {
						BindingVariable v = BindingSelectorPanel.this._bindingSelector.getBindingModel().getBindingVariableAt(0);
						if (v != null && v.getType() != null && v.getType().getBaseEntity() != null
								&& !v.getType().getBaseEntity().getIsReadOnly()) {
							currentEntity = v.getType().getBaseEntity();
							parentEntities.add(currentEntity);
							BindingValue bvInThisContext = (BindingValue) BindingSelectorPanel.this._bindingSelector.makeBinding();
							bvInThisContext.setBindingVariable(v);
							resultingBV.put(currentEntity, bvInThisContext);
						}
					}
				}

				boolean doesSelectionContainsEOEntity = false;
				for (DMEntity entity : parentEntities) {
					if (entity instanceof DMEOEntity) {
						doesSelectionContainsEOEntity = true;
					}
				}
				final boolean selectionContainsEOEntity = doesSelectionContainsEOEntity;

				/*logger.info("BindingModel="+getBindingModel());
				logger.info("currentEntity="+currentEntity);
				logger.info("parentEntities="+parentEntities);

				for (DMEntity entity : resultingBV.keySet()) {
					logger.info("entity="+entity+" for "+resultingBV.get(entity).getStringRepresentation());
				}*/

				if (currentEntity == null) {
					if (BindingSelectorPanel.this._bindingSelector.getBindingModel().allowsNewBindingVariableCreation()) {
						BindingSelector.logger.warning("BindingVariable creation not implemented");
						return;
					} else {
						FlexoController.notify(FlexoLocalization.localizedForKey("you_cannot_create_data_entries_from_this_model"));
						return;
					}
				}

				ParameterDefinition parentEntity = null;

				final String BASIC_PROPERTY = FlexoLocalization.localizedForKey("basic_property");
				final String EO_ATTRIBUTE = FlexoLocalization.localizedForKey("eo_attribute");
				Vector<String> kindOfProperty = new Vector<String>();
				kindOfProperty.add(BASIC_PROPERTY);
				kindOfProperty.add(EO_ATTRIBUTE);
				final StaticDropDownParameter<String> kindOfPropertyParam = new StaticDropDownParameter<String>("kindOfProperty",
						"kind_of_property", kindOfProperty, currentEntity instanceof DMEOEntity ? EO_ATTRIBUTE : BASIC_PROPERTY);
				kindOfPropertyParam.setShowReset(false);

				if (parentEntities.size() == 1) {
					parentEntity = new ReadOnlyTextFieldParameter("parentEntity", "creates_entry_in_entity", parentEntities.firstElement()
							.getName());
				} else {
					parentEntity = new DynamicDropDownParameter<DMEntity>("parentEntity", "creates_entry_in_entity", parentEntities,
							currentEntity) {
						@Override
						public void setValue(DMEntity anEntity) {
							super.setValue(anEntity);
							if (anEntity instanceof DMEOEntity) {
								kindOfPropertyParam.setValue(EO_ATTRIBUTE);
							} else {
								kindOfPropertyParam.setValue(BASIC_PROPERTY);
							}
						}
					};
					parentEntity.setFormatter("fullQualifiedName");
					((DynamicDropDownParameter) parentEntity).setShowReset(false);
				}

				final TextFieldParameter newPropertyNameParam = new TextFieldParameter("propertyName", "property_name", "item");

				final DMTypeParameter newPropertyTypeParam = new DMTypeParameter("propertyType", "property_type", null);
				if (selectionContainsEOEntity) {
					newPropertyTypeParam.setDepends("kindOfProperty");
					newPropertyTypeParam.setConditional("kindOfProperty=" + '"' + BASIC_PROPERTY + '"');
				}

				ChoiceListParameter<DMPropertyImplementationType> newPropertyImplementationType = new ChoiceListParameter<DMPropertyImplementationType>(
						"implementation", "implementation", currentEntity.getPropertyDefaultImplementationType());
				newPropertyImplementationType.setFormatter("localizedName");
				newPropertyImplementationType.setShowReset(false);
				if (selectionContainsEOEntity) {
					newPropertyImplementationType.setDepends("kindOfProperty");
					newPropertyImplementationType.setConditional("kindOfProperty=" + '"' + BASIC_PROPERTY + '"');
				}

				Vector<DMEOPrototype> allEOPrototypes = currentEntity.getDMModel().getAllEOPrototypes();
				final DynamicDropDownParameter<DMEOPrototype> prototypeParam = new DynamicDropDownParameter<DMEOPrototype>("prototype",
						"prototype", allEOPrototypes, null);
				prototypeParam.setFormatter("name");
				prototypeParam.setDepends("kindOfProperty");
				prototypeParam.setConditional("kindOfProperty=" + '"' + EO_ATTRIBUTE + '"');
				prototypeParam.setShowReset(false);

				Vector<ParameterDefinition> allParameters = new Vector<ParameterDefinition>();
				allParameters.add(parentEntity);
				allParameters.add(newPropertyNameParam);
				if (selectionContainsEOEntity) {
					allParameters.add(kindOfPropertyParam);
				}
				allParameters.add(newPropertyTypeParam);
				allParameters.add(newPropertyImplementationType);
				if (selectionContainsEOEntity) {
					allParameters.add(prototypeParam);
				}
				Frame owner = null;
				if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
					owner = FlexoFrame.getActiveFrame();
				} else {
					owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, _createsButton);
				}
				if (owner == null) {
					if (BindingSelector.logger.isLoggable(Level.WARNING)) {
						BindingSelector.logger.warning("Window ancestor is not a Frame, cannot pass the owner ");
					}
				}
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(currentEntity.getProject(), owner,
						FlexoLocalization.localizedForKey("creates_new_data_entry"),
						FlexoLocalization.localizedForKey("enter_parameters_for_new_data_entry_creation"),
						new AskParametersDialog.ValidationCondition() {
							@Override
							public boolean isValid(ParametersModel model) {
								DMEntity entity = null;
								if (model.parameterForKey("parentEntity") instanceof DynamicDropDownParameter) {
									entity = (DMEntity) model.objectForKey("parentEntity");
								} else if (model.parameterForKey("parentEntity") instanceof ReadOnlyTextFieldParameter) {
									entity = parentEntities.firstElement();
								}

								if (model.objectForKey("propertyName") == null
										|| ((String) model.objectForKey("propertyName")).trim().equals("")) {
									errorMessage = FlexoLocalization.localizedForKey("property_name_must_be_non_empty");
									return false;
								} else if (model.objectForKey("propertyName") != null) {
									if (!DMRegExp.ENTITY_NAME_PATTERN.matcher((CharSequence) model.objectForKey("propertyName")).matches()) {
										errorMessage = FlexoLocalization
												.localizedForKey("property_name_must_start_with_a_letter_and_be_followed_by_digits_or_letters");
										return false;
									}
									if (entity != null && entity.getDeclaredProperty((String) model.objectForKey("propertyName")) != null) {
										errorMessage = FlexoLocalization
												.localizedForKey("a_property_with_this_name_is_already_declared_in_this_entity");
										return false;
									}
								} else if (selectionContainsEOEntity && model.objectForKey("kindOfProperty").equals(BASIC_PROPERTY)
										|| !selectionContainsEOEntity) {
									if (model.objectForKey("propertyType") == null) {
										errorMessage = FlexoLocalization.localizedForKey("no_type_defined_for_this_new_property");
										return false;
									}
								} else if (selectionContainsEOEntity && model.objectForKey("kindOfProperty").equals(EO_ATTRIBUTE)) {
									if (model.objectForKey("prototype") == null) {
										errorMessage = FlexoLocalization.localizedForKey("no_prototype_defined_for_this_new_attribute");
										return false;
									}
								}
								return true;
							}
						}, allParameters.toArray(new ParameterDefinition[allParameters.size()]));

				/*parentEntity,
						newPropertyNameParam,
						kindOfPropertyParam,
						newPropertyTypeParam,
						newPropertyImplementationType,
						prototypeParam);*/
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {

					if (parentEntity instanceof DynamicDropDownParameter) {
						currentEntity = (DMEntity) ((DynamicDropDownParameter) parentEntity).getValue();
					}

					DMProperty newProperty = null;
					if (selectionContainsEOEntity && kindOfPropertyParam.getValue().equals(BASIC_PROPERTY) || !selectionContainsEOEntity) {
						newProperty = BindingSelectorPanel.this._bindingSelector.createsNewEntry(newPropertyNameParam.getValue(),
								newPropertyTypeParam.getValue(), newPropertyImplementationType.getValue(), currentEntity);
					} else {
						try {
							newProperty = DMEOAttribute.createsNewDMEOAttribute(currentEntity.getDMModel(), (DMEOEntity) currentEntity,
									newPropertyNameParam.getValue(), false, true, DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY);
							((DMEOAttribute) newProperty).setPrototype(prototypeParam.getValue());
						} catch (EOAccessException e1) {
							e1.printStackTrace();
						}
					}
					BindingValue newBindingValue = resultingBV.get(currentEntity);
					newBindingValue.addBindingPathElement(newProperty);
					if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition() != null
							&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType() != null) {
						if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType()
								.isAssignableFrom(newBindingValue.getAccessedType(), true)) {
							newBindingValue.connect();
						}
					}
					BindingSelectorPanel.this._bindingSelector.setEditedObject(newBindingValue);
					BindingSelectorPanel.this._bindingSelector.apply();

				}
				BindingSelectorPanel.this._bindingSelector.openPopup();
				/*JFrame father = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, BindingSelectorPanel.this);
				NewEntryDialog dialog = new NewEntryDialog(father);
				if (dialog.getStatus() == NewEntryDialog.CANCEL)
				    return;
				String name = dialog.getEditedName();
				DMType type = dialog.getEditedType();
				DMPropertyImplementationType implementationType = dialog.getEditedImplementationType();
				if ((name != null) && (!(name.trim().equals(""))) && (type != null) && (implementationType != null)) {
				    DMEntity editedEntity = getAccessedEntity();
				    BindingVariable selectedBindingVariable = getSelectedBindingVariable();
					createsNewEntry(name, type, implementationType,editedEntity);
				//                        if (newEntry != null) {
				//                            BindingValue bindingValue = getEditedObject();
				//                            if (bindingValue == null) {
				//                                bindingValue = makeBindingValue();
				//                                if (bindingValue == null) return;
				//                            }
				//                            //bindingValue.setBindingVariable(newEntry);
				//                            setEditedObject(bindingValue);
				//                            apply();
				//                        }
				}*/

				/*Enumeration<JList> en = _lists.elements();
				while (en.hasMoreElements()) {
				    JList list = en.nextElement();
				    ((BindingColumnListModel)list.getModel()).fireModelChanged();
				}
				update();*/
			}
		};

		_createsButton = _controlPanel.addButton("creates", actionListener);

		revalidate();
		repaint();
	}

	public void removeNewEntryCreationPanel() {
		if (_createsButton != null) {
			_controlPanel.remove(_createsButton);
		}
		_createsButton = null;
		revalidate();
		repaint();
	}

	protected class MethodCallBindingsModel extends AbstractModel<MethodCall, MethodCallArgument> {
		public MethodCallBindingsModel() {
			super(null, null);
			addToColumns(new IconColumn<MethodCallArgument>("icon", 25) {
				@Override
				public Icon getIcon(MethodCallArgument entity) {
					return DMEIconLibrary.DM_METHOD_ICON;
				}
			});
			addToColumns(new StringColumn<MethodCallArgument>("name", 100) {
				@Override
				public String getValue(MethodCallArgument arg) {
					if (arg != null) {
						return arg.getParam().getName();
					}
					/*if (paramForValue(bindingValue) != null)
						return paramForValue(bindingValue).getName();*/
					return "null";
				}
			});
			addToColumns(new StringColumn<MethodCallArgument>("type", 100) {
				@Override
				public String getValue(MethodCallArgument arg) {
					if (arg != null) {
						return arg.getParam().getType().getSimplifiedStringRepresentation();
					}
					return "null";
				}
			});
			addToColumns(new BindingValueColumn<MethodCallArgument>("value", 250, null, true, false) {

				@Override
				public AbstractBinding getValue(MethodCallArgument arg) {
					return arg.getBinding();
				}

				@Override
				public void setValue(MethodCallArgument arg, AbstractBinding aValue) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Sets value " + arg + " to be " + aValue);
					}
					if (arg != null) {
						arg.setBinding(aValue);
					}

					BindingSelectorPanel.this._bindingSelector.fireEditedObjectChanged();
				}

				@Override
				public Bindable getBindableFor(AbstractBinding value, MethodCallArgument rowObject) {
					return BindingSelectorPanel.this._bindingSelector.getBindable();
				}

				@Override
				public BindingDefinition getBindingDefinitionFor(AbstractBinding value, MethodCallArgument rowObject) {
					if (rowObject != null) {
						return rowObject.getBindingDefinition();
					}
					return null;
				}

				@Override
				public boolean allowsCompoundBinding(AbstractBinding value) {
					return true;
				}

				@Override
				public boolean allowsNewEntryCreation(AbstractBinding value) {
					return false;
				}
			});
		}

		DMMethodParameter paramForValue(AbstractBinding bindingValue) {
			if (bindingValue.getBindingDefinition() != null
					&& bindingValue.getBindingDefinition() instanceof MethodCall.MethodCallParamBindingDefinition) {
				return ((MethodCall.MethodCallParamBindingDefinition) bindingValue.getBindingDefinition()).getParam();
			}
			return null;
		}

		public MethodCall getMethodCall() {
			return getModel();
		}

		@Override
		public MethodCallArgument elementAt(int row) {
			if (row >= 0 && row < getRowCount()) {
				return getMethodCall().getArgs().elementAt(row);
			} else {
				return null;
			}
		}

		@Override
		public int getRowCount() {
			if (getMethodCall() != null) {
				return getMethodCall().getArgs().size();
			}
			return 0;
		}

	}

	protected class MethodCallBindingsPanel extends TabularPanel {
		public MethodCallBindingsPanel() {
			super(getMethodCallBindingsModel(), 3);
		}

	}

	private MethodCallBindingsModel _methodCallBindingsModel;

	private MethodCallBindingsPanel _methodCallBindingsPanel;

	public MethodCallBindingsPanel getMethodCallBindingsPanel() {
		if (_methodCallBindingsPanel == null) {
			_methodCallBindingsPanel = new MethodCallBindingsPanel();

		}

		return _methodCallBindingsPanel;
	}

	public MethodCallBindingsModel getMethodCallBindingsModel() {
		if (_methodCallBindingsModel == null) {
			_methodCallBindingsModel = new MethodCallBindingsModel();
		}
		return _methodCallBindingsModel;
	}

	@Override
	public Dimension getDefaultSize() {
		int baseHeight;

		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			baseHeight = 300;
		} else {
			baseHeight = 180;
		}

		if (_bindingSelector.getAllowsStaticValues() || _bindingSelector.getAllowsCompoundBindings()) {
			baseHeight += 30;
		}

		if (_bindingSelector.getAllowsTranstypers() && _bindingSelector.areSomeTranstyperAvailable()) {
			baseHeight += 30;
		}

		return new Dimension(500, baseHeight);

	}

	@Override
	protected void willApply() {
		if (editStaticValue && staticBindingPanel != null) {
			staticBindingPanel.willApply();
		}
	}

	private MouseOverButton showHideCompoundBindingsButton;

	private TranstyperPanel transtypersPanel;

	protected class TranstyperPanel extends JPanel {
		boolean isUpdatingPanel = false;
		protected JCheckBox useTranstyperCB;
		protected JComboBox transtyperCB;
		private final DefaultComboBoxModel availableTranstypers;

		protected TranstyperPanel() {
			setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
			useTranstyperCB = new JCheckBox(FlexoLocalization.localizedForKey("use_transtyper"));
			useTranstyperCB.setFont(FlexoCst.SMALL_FONT);
			useTranstyperCB.setSelected(false);
			useTranstyperCB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (useTranstyperCB.isSelected()) {
						BindingSelectorPanel.this._bindingSelector._editedObject = new TranstypedBinding(
								BindingSelectorPanel.this._bindingSelector.getBindingDefinition(),
								(FlexoModelObject) BindingSelectorPanel.this._bindingSelector.getBindable());
						((TranstypedBinding) BindingSelectorPanel.this._bindingSelector._editedObject)
								.setTranstyper((DMTranstyper) transtyperCB.getSelectedItem());
						setEditTranstypedBinding(true);
					} else {
						setEditTranstypedBinding(false);
					}
				}
			});
			add(useTranstyperCB);
			availableTranstypers = new DefaultComboBoxModel();
			transtyperCB = new JComboBox(availableTranstypers);
			transtyperCB.setFont(FlexoCst.SMALL_FONT);
			transtyperCB.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					JLabel returned = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					returned.setText(((DMTranstyper) value).getName());
					return returned;
				}
			});
			transtyperCB.setEnabled(false);
			transtyperCB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (isUpdatingPanel) {
						return;
					}
					TranstypedBinding newBinding = new TranstypedBinding(BindingSelectorPanel.this._bindingSelector.getBindingDefinition(),
							(FlexoModelObject) BindingSelectorPanel.this._bindingSelector.getBindable());
					newBinding.setTranstyper((DMTranstyper) transtyperCB.getSelectedItem());
					BindingSelectorPanel.this._bindingSelector.setEditedObject(newBinding);

					/*if (typeCB.getSelectedItem().equals(BOOLEAN))
						setEditedObject(new BooleanStaticBinding(getBindingDefinition(),(FlexoModelObject)getBindable(),true));
					else if (typeCB.getSelectedItem().equals(INTEGER))
						setEditedObject(new IntegerStaticBinding(getBindingDefinition(),(FlexoModelObject)getBindable(),0));
					else if (typeCB.getSelectedItem().equals(FLOAT))
						setEditedObject(new FloatStaticBinding(getBindingDefinition(),(FlexoModelObject)getBindable(),0));
					else if (typeCB.getSelectedItem().equals(STRING))
						setEditedObject(new StringStaticBinding(getBindingDefinition(),(FlexoModelObject)getBindable(),""));
					else if (typeCB.getSelectedItem().equals(DATE))
						setEditedObject(new DateStaticBinding(getBindingDefinition(),(FlexoModelObject)getBindable(),new Date()));
					else if (typeCB.getSelectedItem().equals(DURATION))
						setEditedObject(new DurationStaticBinding(getBindingDefinition(),(FlexoModelObject)getBindable(),new Duration(1,DurationUnit.SECONDS)));
					else if (typeCB.getSelectedItem().equals(DKV))
						setEditedObject(new DKVBinding(getBindingDefinition(),(FlexoModelObject)getBindable(),new Key(getProject().getDKVModel())));*/
				}
			});
			add(transtyperCB);

			if (getEditTranstypedBinding()) {
				enableTranstyperPanel();
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(false);
				}
			} else {
				disableTranstyperPanel();
			}

			updateTranstyperPanel();
		}

		void updateTranstyperPanel() {
			isUpdatingPanel = true;
			boolean isVisible = BindingSelectorPanel.this._bindingSelector.areSomeTranstyperAvailable();
			setVisible(isVisible);
			if (isVisible) {
				availableTranstypers.removeAllElements();
				for (DMTranstyper tt : BindingSelectorPanel.this._bindingSelector.getProject().getDataModel()
						.getDMTranstypers(BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType())) {
					availableTranstypers.addElement(tt);
				}
				if (BindingSelectorPanel.this._bindingSelector.getEditedObject() instanceof TranstypedBinding
						&& ((TranstypedBinding) BindingSelectorPanel.this._bindingSelector.getEditedObject()).getTranstyper() != null) {
					availableTranstypers.setSelectedItem(((TranstypedBinding) BindingSelectorPanel.this._bindingSelector.getEditedObject())
							.getTranstyper());
				}
			}

			isUpdatingPanel = false;
		}

		void enableTranstyperPanel() {
			_connectButton.setText(FlexoLocalization.localizedForKey("validate"));
			useTranstyperCB.setSelected(true);
			if (transtyperCB != null) {
				transtyperCB.setEnabled(true);
			}
		}

		void disableTranstyperPanel() {
			_connectButton.setText(FlexoLocalization.localizedForKey("connect"));
			useTranstyperCB.setSelected(false);
			if (transtyperCB != null) {
				transtyperCB.setEnabled(false);
			}
		}

	}

	private StaticBindingPanel staticBindingPanel;

	@Override
	protected void init() {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("init() with " + _bindingSelector.editionMode + " for " + _bindingSelector.getEditedObject());
		}

		setLayout(new BorderLayout());

		_browserPanel = new JPanel();
		_browserPanel.setLayout(new BoxLayout(_browserPanel, BoxLayout.X_AXIS));
		for (int i = 0; i < defaultVisibleColCount; i++) {
			makeNewJList();
		}
		_controlPanel = new ButtonsControlPanel() {
			@Override
			public String localizedForKeyAndButton(String key, JButton component) {
				return FlexoLocalization.localizedForKey(key, component);
			}
		};
		_connectButton = _controlPanel.addButton("connect", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingSelectorPanel.this._bindingSelector.apply();
			}
		});
		_cancelButton = _controlPanel.addButton("cancel", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingSelectorPanel.this._bindingSelector.cancel();
			}
		});
		_resetButton = _controlPanel.addButton("reset", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingSelectorPanel.this._bindingSelector.setEditedObject(null);
				BindingSelectorPanel.this._bindingSelector.apply();
			}
		});
		if (_bindingSelector.getAllowsBindingExpressions()) {
			_expressionButton = _controlPanel.addButton("expression", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (BindingSelectorPanel.this._bindingSelector.getEditedObject() != null) {
						BindingSelectorPanel.this._bindingSelector.activateBindingExpressionMode(new BindingExpression(
								BindingSelectorPanel.this._bindingSelector.getBindingDefinition(),
								(FlexoModelObject) BindingSelectorPanel.this._bindingSelector.getBindable(),
								BindingSelectorPanel.this._bindingSelector.getEditedObject()));
					} else {
						BindingSelectorPanel.this._bindingSelector.activateBindingExpressionMode(null);
					}
				}
			});
		}

		_controlPanel.applyFocusTraversablePolicyTo(_controlPanel, false);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout());

		if (_bindingSelector.getAllowsCompoundBindings() && _bindingSelector.editionMode != EditionMode.TRANSTYPED_BINDING) {
			showHideCompoundBindingsButton = new MouseOverButton();
			showHideCompoundBindingsButton.setBorder(BorderFactory.createEmptyBorder());
			showHideCompoundBindingsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (BindingSelectorPanel.this._bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
						BindingSelectorPanel.this._bindingSelector.activateNormalBindingMode();
					} else {
						BindingSelectorPanel.this._bindingSelector.activateCompoundBindingMode();
					}
				}
			});

			JLabel showHideCompoundBindingsButtonLabel = new JLabel("", SwingConstants.RIGHT);
			showHideCompoundBindingsButtonLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
			if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				showHideCompoundBindingsButton.setNormalIcon(IconLibrary.TOGGLE_ARROW_TOP_ICON);
				showHideCompoundBindingsButton.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
				showHideCompoundBindingsButton.setToolTipText(FlexoLocalization.localizedForKey("specify_basic_binding"));
				showHideCompoundBindingsButtonLabel.setText(FlexoLocalization.localizedForKey("specify_basic_binding") + "  ");
			} else {
				showHideCompoundBindingsButton.setNormalIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
				showHideCompoundBindingsButton.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
				showHideCompoundBindingsButton.setToolTipText(FlexoLocalization.localizedForKey("specify_compound_binding"));
				showHideCompoundBindingsButtonLabel.setText(FlexoLocalization.localizedForKey("specify_compound_binding") + "  ");
			}

			JPanel showHideCompoundBindingsButtonPanel = new JPanel();
			showHideCompoundBindingsButtonPanel.setLayout(new BorderLayout());
			showHideCompoundBindingsButtonPanel.add(showHideCompoundBindingsButtonLabel, BorderLayout.CENTER);
			showHideCompoundBindingsButtonPanel.add(showHideCompoundBindingsButton, BorderLayout.EAST);

			optionsPanel.add(showHideCompoundBindingsButtonPanel, BorderLayout.EAST);
		}

		if (_bindingSelector.getAllowsStaticValues() || _bindingSelector.getAllowsTranstypers()) {
			JPanel optionsWestPanel = new JPanel();
			optionsWestPanel.setLayout(new VerticalLayout());

			if (_bindingSelector.getAllowsStaticValues() && _bindingSelector.editionMode != EditionMode.TRANSTYPED_BINDING) {
				staticBindingPanel = new StaticBindingPanel(this);
				optionsWestPanel.add(staticBindingPanel);
			}

			if (_bindingSelector.getAllowsTranstypers()) {
				transtypersPanel = new TranstyperPanel();
				optionsWestPanel.add(transtypersPanel);
			}

			optionsPanel.add(optionsWestPanel, BorderLayout.WEST);

		}

		currentTypeLabel = new JLabel(FlexoLocalization.localizedForKey("no_type"), SwingConstants.LEFT);
		currentTypeLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
		currentTypeLabel.setForeground(Color.GRAY);

		searchedTypeLabel = new JLabel("[" + FlexoLocalization.localizedForKey("no_type") + "]", SwingConstants.LEFT);
		searchedTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		searchedTypeLabel.setForeground(Color.RED);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.add(currentTypeLabel, BorderLayout.CENTER);
		labelPanel.add(searchedTypeLabel, BorderLayout.EAST);

		JComponent topPane;

		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			topPane = new JPanel();
			topPane.setLayout(new BorderLayout());
			bindingValueRepresentation = new JTextArea(3, 80);
			bindingValueRepresentation.setFont(new Font("SansSerif", Font.PLAIN, 10));
			bindingValueRepresentation.setEditable(false);
			bindingValueRepresentation.setLineWrap(true);
			topPane.add(bindingValueRepresentation, BorderLayout.CENTER);
			topPane.add(labelPanel, BorderLayout.SOUTH);
		} else {
			topPane = labelPanel;
		}

		add(topPane, BorderLayout.NORTH);

		JComponent middlePane;

		// logger.info("Rebuild middle pane, with mode="+editionMode);

		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			middlePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(_browserPanel), getMethodCallBindingsPanel());
			((JSplitPane) middlePane).setDividerLocation(0.5);
			((JSplitPane) middlePane).setResizeWeight(0.5);
		} else if (_bindingSelector.editionMode == EditionMode.TRANSTYPED_BINDING) {
			middlePane = new JScrollPane(getTranstyperBindingsPanel());
		} else { // For NORMAL_BINDING and STATIC_BINDING
			middlePane = new JScrollPane(_browserPanel);
		}

		JPanel middlePaneWithOptions = new JPanel();
		middlePaneWithOptions.setLayout(new BorderLayout());
		middlePaneWithOptions.add(middlePane, BorderLayout.CENTER);
		if (_bindingSelector.getAllowsStaticValues() || _bindingSelector.getAllowsCompoundBindings()) {
			middlePaneWithOptions.add(optionsPanel, BorderLayout.SOUTH);
		}

		add(middlePaneWithOptions, BorderLayout.CENTER);
		add(_controlPanel, BorderLayout.SOUTH);

		if (_bindingSelector._allowsEntryCreation) {
			addNewEntryCreationPanel();
		}

		resetMethodCallPanel();

		// Init static panel
		editStaticValue = true;
		setEditStaticValue(false);

		update();
		FilteredJList firstList = listAtIndex(0);
		if (firstList != null && firstList.getModel().getSize() == 1) {
			firstList.setSelectedIndex(0);
		}
	}

	protected class TranstyperBindingsModel extends AbstractModel<TranstypedBinding, TranstypedBindingValue> {
		public TranstyperBindingsModel() {
			super(null, null);
			addToColumns(new IconColumn<TranstypedBindingValue>("icon", 25) {
				@Override
				public Icon getIcon(TranstypedBindingValue entity) {
					return DMEIconLibrary.DM_TRANSTYPER_ENTRY_ICON;
				}
			});
			addToColumns(new StringColumn<TranstypedBindingValue>("name", 100) {
				@Override
				public String getValue(TranstypedBindingValue arg) {
					if (arg != null && arg.getEntry() != null) {
						return arg.getEntry().getName();
					}
					return "null";
				}
			});
			addToColumns(new StringColumn<TranstypedBindingValue>("type", 100) {
				@Override
				public String getValue(TranstypedBindingValue arg) {
					if (arg != null && arg.getEntry() != null && arg.getEntry().getType() != null) {
						return arg.getEntry().getType().getSimplifiedStringRepresentation();
					}
					return "null";
				}
			});
			addToColumns(new BindingValueColumn<TranstypedBindingValue>("value", 250, null, true, false) {

				@Override
				public AbstractBinding getValue(TranstypedBindingValue arg) {
					return arg.getBindingValue();
				}

				@Override
				public void setValue(TranstypedBindingValue arg, AbstractBinding aValue) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Sets value " + arg + " to be " + aValue);
					}
					if (arg != null) {
						arg.setBindingValue(aValue);
					}

					BindingSelectorPanel.this._bindingSelector.fireEditedObjectChanged();
				}

				@Override
				public Bindable getBindableFor(AbstractBinding value, TranstypedBindingValue rowObject) {
					return BindingSelectorPanel.this._bindingSelector.getBindable();
				}

				@Override
				public BindingDefinition getBindingDefinitionFor(AbstractBinding value, TranstypedBindingValue rowObject) {
					if (rowObject != null) {
						return rowObject.getBindingDefinition();
					}
					return null;
				}

				@Override
				public boolean allowsCompoundBinding(AbstractBinding value) {
					return true;
				}

				@Override
				public boolean allowsNewEntryCreation(AbstractBinding value) {
					return false;
				}
			});
		}

		/*	TranstypedBindingValue paramForValue(TranstypedBinding bindingValue)
			{
				if ((bindingValue.getBindingDefinition() != null)
						&& (bindingValue.getBindingDefinition() instanceof MethodCall.MethodCallParamBindingDefinition)) {
					return ((MethodCall.MethodCallParamBindingDefinition)bindingValue.getBindingDefinition()).getParam();
				}
				return null;
			}*/

		public TranstypedBinding getTranstypedBinding() {
			return getModel();
		}

		@Override
		public TranstypedBindingValue elementAt(int row) {
			if (row >= 0 && row < getRowCount()) {
				return getTranstypedBinding().getValues().elementAt(row);
			} else {
				return null;
			}
		}

		@Override
		public int getRowCount() {
			if (getTranstypedBinding() != null) {
				return getTranstypedBinding().getValues().size();
			}
			return 0;
		}

	}

	protected class TranstyperBindingsPanel extends TabularPanel {
		public TranstyperBindingsPanel() {
			super(getTranstyperBindingsModel(), 6);
		}

	}

	private TranstyperBindingsModel _transtyperBindingsModel;

	private TranstyperBindingsPanel _transtyperBindingsPanel;

	public TranstyperBindingsPanel getTranstyperBindingsPanel() {
		if (_transtyperBindingsPanel == null) {
			_transtyperBindingsPanel = new TranstyperBindingsPanel();
		}

		return _transtyperBindingsPanel;
	}

	public TranstyperBindingsModel getTranstyperBindingsModel() {
		if (_transtyperBindingsModel == null) {
			_transtyperBindingsModel = new TranstyperBindingsModel();
		}
		return _transtyperBindingsModel;
	}

	/*private PropertyListParameter<TranstypedBinding.TranstypedBindingValue> transtypingValuesParameter;
	private PropertyListWidget transtypingValuesWidget;

	private JComponent makeTranstypingValuePanel()
	{
		if (getEditedObject() != null
				&& getEditedObject() instanceof TranstypedBinding) {
			transtypingValuesParameter
			= new PropertyListParameter<TranstypedBinding.TranstypedBindingValue>("transtyping_values",
					"transtyping_values",
					((TranstypedBinding)getEditedObject()).getValues(),
					20,5);
			transtypingValuesParameter.addIconColumn("icon", "",30,false);
			//transtypingValuesParameter.addReadOnlyTextFieldColumn("entry.type.simplifiedStringRepresentation", "type",100,true);
			transtypingValuesParameter.addReadOnlyTextFieldColumn("entry.name", "entry",100,true);
			PropertyListColumn valueAttributeColumn = transtypingValuesParameter.addCustomColumn("bindingValue", "value","org.openflexo.components.widget.BindingSelectorInspectorWidget",200,true);
			valueAttributeColumn.setValueForParameter("binding_definition", "bindingDefinition");
			valueAttributeColumn.setValueForParameter("format", "stringRepresentation");

			AskParametersPanel panel = new AskParametersPanel(
					getProject(),
					transtypingValuesParameter);
			transtypingValuesWidget = (PropertyListWidget)panel.getInspectorWidgetForParameter(transtypingValuesParameter);
			return transtypingValuesWidget.getDynamicComponent();
		}
		else {
			logger.warning("Cannot make transtyping panel for null transtyper or edited value");
			return null;
		}
	}*/

	/*protected boolean displayOptions()
	{
		return true;
	}*/

	protected void updateSearchedTypeLabel() {
		if (_bindingSelector.getEditedObject() != null && _bindingSelector.getEditedObject() instanceof TranstypedBinding
				&& ((TranstypedBinding) _bindingSelector.getEditedObject()).getTranstyper() != null) {
			searchedTypeLabel.setText(((TranstypedBinding) _bindingSelector.getEditedObject()).getTranstyper()
					.getTranstyperStringRepresentation());
		} else if (_bindingSelector.getBindingDefinition() != null) {
			if (BindingSelector.logger.isLoggable(Level.FINE)) {
				BindingSelector.logger.fine("updateSearchedTypeLabel() with "
						+ _bindingSelector.getBindingDefinition().getTypeStringRepresentation());
			}
			searchedTypeLabel.setText("[" + _bindingSelector.getBindingDefinition().getTypeStringRepresentation() + "]");
		}
	}

	protected int getVisibleColsCount() {
		return _lists.size();
	}

	public boolean ensureBindingValueExists() {
		AbstractBinding bindingValue = _bindingSelector.getEditedObject();
		if (bindingValue == null) {
			if (_bindingSelector.getBindingDefinition() != null && _bindingSelector.getBindable() != null) {
				bindingValue = _bindingSelector.makeBinding();
				_bindingSelector.setEditedObject(bindingValue);
			} else {
				return false;
			}
		}
		return _bindingSelector.getEditedObject() != null;
	}

	protected class FilteredJList extends JList {
		public FilteredJList() {
			super(new EmptyColumnListModel());
		}

		public String getFilter() {
			return getModel().getFilter();
		}

		public void setFilter(String aFilter) {
			getModel().setFilter(aFilter);
		}

		@Override
		public void setModel(ListModel model) {
			if (!(model instanceof BindingColumnListModel)) {
				new Exception("oops").printStackTrace();
			}
			setFilter(null);
			super.setModel(model);
		}

		@Override
		public BindingColumnListModel getModel() {
			if (super.getModel() instanceof BindingColumnListModel) {
				return (BindingColumnListModel) super.getModel();
			} else {
				new Exception("oops, j'ai un " + super.getModel()).printStackTrace();
				return null;
			}
		}
	}

	protected JList makeNewJList() {
		FilteredJList newList = new FilteredJList();

		newList.addMouseMotionListener(new TypeResolver(newList));

		_lists.add(newList);
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("makeNewJList() size = " + _lists.size());
		}
		newList.setPrototypeCellValue("               ");
		// newList.setSize(new Dimension(100,150));
		// newList.setPreferredSize(new Dimension(100,150));
		newList.setCellRenderer(new BindingSelectorCellRenderer());
		newList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		newList.addListSelectionListener(this);
		newList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (BindingSelectorPanel.this._bindingSelector.getEditedObject() != null
							&& BindingSelectorPanel.this._bindingSelector.getEditedObject().isBindingValid()) {
						BindingSelectorPanel.this._bindingSelector.apply();
					}
				} else if (e.getClickCount() == 1) {
					// Trying to update MethodCall Panel
					JList list = (JList) e.getSource();
					int index = _lists.indexOf(list);
					if (BindingSelector.logger.isLoggable(Level.FINE)) {
						BindingSelector.logger.fine("Click on index " + index);
					}
					if (index < 0) {
						return;
					}
					_selectedPathElementIndex = index;
					updateMethodCallPanel();
				}
			}
		});

		newList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// processAnyKeyTyped(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					BindingSelectorPanel.this._bindingSelector._selectorPanel.processEnterPressed();
					e.consume();
				} else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					BindingSelectorPanel.this._bindingSelector._selectorPanel.processBackspace();
					e.consume();
				} else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
					BindingSelectorPanel.this._bindingSelector._selectorPanel.processDelete();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					AbstractBinding bindingValue = BindingSelectorPanel.this._bindingSelector.getEditedObject();
					if (bindingValue instanceof BindingValue) {
						int i = _lists.indexOf(e.getSource());
						if (i > -1 && i < _lists.size() && listAtIndex(i + 1) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0).getElement() instanceof BindingPathElement) {
							((BindingValue) bindingValue).setBindingPathElementAtIndex((BindingPathElement) listAtIndex(i + 1).getModel()
									.getElementAt(0).getElement(), i);
							BindingSelectorPanel.this._bindingSelector.setEditedObject(bindingValue);
							BindingSelectorPanel.this._bindingSelector.fireEditedObjectChanged();
							listAtIndex(i + 1).requestFocus();
						}
						e.consume();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					AbstractBinding bindingValue = BindingSelectorPanel.this._bindingSelector.getEditedObject();
					if (bindingValue instanceof BindingValue) {
						int i = _lists.indexOf(e.getSource()) - 1;
						if (((BindingValue) bindingValue).getBindingPath().size() > i && i > -1 && i < _lists.size()) {
							((BindingValue) bindingValue).getBindingPath().removeElementAt(i);
							((BindingValue) bindingValue).disconnect();
							BindingSelectorPanel.this._bindingSelector.setEditedObject(bindingValue);
							BindingSelectorPanel.this._bindingSelector.fireEditedObjectChanged();
							listAtIndex(i).requestFocus();
						}
						e.consume();
					}
				}
			}

		});

		_browserPanel.add(new JScrollPane(newList));
		newList.setVisibleRowCount(6);
		revalidate();
		repaint();
		return newList;
	}

	int _selectedPathElementIndex = -1;

	protected void resetMethodCallPanel() {
		if (_bindingSelector.getEditedObject() == null || _bindingSelector.getEditedObject().isStaticValue()
				|| _bindingSelector.getEditedObject() instanceof BindingValue
				&& ((BindingValue) _bindingSelector.getEditedObject()).getBindingPath().size() == 0) {
			_selectedPathElementIndex = -1;
		} else if (_bindingSelector.getEditedObject() instanceof BindingValue) {
			_selectedPathElementIndex = ((BindingValue) _bindingSelector.getEditedObject()).getBindingPath().size();
		}
		updateMethodCallPanel();
	}

	void updateMethodCallPanel() {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("updateMethodCallPanel with " + _bindingSelector.editionMode + " binding="
					+ _bindingSelector.getEditedObject() + " _selectedPathElementIndex=" + _selectedPathElementIndex);
		}
		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING && _bindingSelector.getEditedObject() instanceof BindingValue) {
			if (((BindingValue) _bindingSelector.getEditedObject()).isCompoundBinding() && _selectedPathElementIndex == -1) {
				_selectedPathElementIndex = ((BindingValue) _bindingSelector.getEditedObject()).getBindingPathElementCount();
			}
			if (_selectedPathElementIndex >= _lists.size()) {
				_selectedPathElementIndex = -1;
			}
			BindingValue bindingValue = (BindingValue) _bindingSelector.getEditedObject();
			if (bindingValue == null) {
				_selectedPathElementIndex = -1;
			} else if (_selectedPathElementIndex > bindingValue.getBindingPath().size()) {
				_selectedPathElementIndex = -1;
			}
			if (_selectedPathElementIndex > -1 && bindingValue != null) {
				JList list = _lists.get(_selectedPathElementIndex);
				int newSelectedIndex = list.getSelectedIndex();
				if (newSelectedIndex > 0) {
					BindingColumnElement selectedValue = (BindingColumnElement) list.getSelectedValue();
					if (selectedValue.getElement() instanceof DMMethod) {
						BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(_selectedPathElementIndex - 1);
						if (currentElement instanceof MethodCall && ((MethodCall) currentElement).getMethod() == selectedValue.getElement()) {
							getMethodCallBindingsModel().setModel((MethodCall) currentElement);
							return;
						}
					}
				}
			}
			getMethodCallBindingsModel().setModel(null);
			return;
		}
	}

	protected void deleteJList(JList list) {
		_lists.remove(list);
		Component[] scrollPanes = _browserPanel.getComponents();
		for (int i = 0; i < scrollPanes.length; i++) {
			if (((Container) scrollPanes[i]).isAncestorOf(list)) {
				_browserPanel.remove(scrollPanes[i]);
			}
		}
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("deleteJList() size = " + _lists.size());
		}
		revalidate();
		repaint();
	}

	protected FilteredJList listAtIndex(int index) {
		if (index >= 0 && index < _lists.size()) {
			return _lists.elementAt(index);
		}
		return null;
	}

	// TODO ???
	/*public void setBindingDefinition(BindingDefinition bindingDefinition)
	{
		if (bindingDefinition != getBindingDefinition()) {
			super.setBindingDefinition(bindingDefinition);
			staticBindingPanel.updateStaticBindingPanel();
		}
	}*/

	@Override
	protected void fireBindableChanged() {
		_rootBindingColumnListModel = buildRootColumnListModel();
		update();
	}

	@Override
	protected void fireBindingDefinitionChanged() {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("fireBindingDefinitionChanged / Setting new binding definition: "
					+ _bindingSelector.getBindingDefinition());
		}

		update();

		if (staticBindingPanel != null) {
			staticBindingPanel.updateStaticBindingPanel();
		}

		if (transtypersPanel != null) {
			transtypersPanel.updateTranstyperPanel();
		}
	}

	private void clearColumns() {
		listAtIndex(0).setModel(_bindingSelector.getRootListModel());
		int lastUpdatedList = 0;
		// Remove unused lists
		int lastVisibleList = defaultVisibleColCount - 1;
		if (lastUpdatedList > lastVisibleList) {
			lastVisibleList = lastUpdatedList;
		}
		int currentSize = getVisibleColsCount();
		for (int i = lastVisibleList + 1; i < currentSize; i++) {
			JList toRemove = listAtIndex(getVisibleColsCount() - 1);
			deleteJList(toRemove);
		}
		// Sets model to null for visible but unused lists
		for (int i = lastUpdatedList + 1; i < getVisibleColsCount(); i++) {
			JList list = listAtIndex(i);
			list.setModel(EMPTY_MODEL);
		}
	}

	@Override
	protected void update() {
		AbstractBinding binding = _bindingSelector.getEditedObject();
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("update with " + binding);
		}
		if (binding == null || binding instanceof StaticBinding) {
			clearColumns();
			if (binding == null) {
				setEditStaticValue(false);
			}
		} else if (binding instanceof BindingValue) {
			BindingValue bindingValue = (BindingValue) binding;
			listAtIndex(0).setModel(_bindingSelector.getRootListModel());
			int lastUpdatedList = 0;
			if (bindingValue.getBindingVariable() != null) {
				if (bindingValue.getBindingVariable().getType() != null) {
					listAtIndex(1).setModel(_bindingSelector.getListModelFor(bindingValue.getBindingVariable().getType()));
				} else {
					listAtIndex(1).setModel(EMPTY_MODEL);
				}
				listAtIndex(0).removeListSelectionListener(this);
				BindingColumnElement elementToSelect = listAtIndex(0).getModel().getElementFor(bindingValue.getBindingVariable());
				listAtIndex(0).setSelectedValue(elementToSelect, true);
				listAtIndex(0).addListSelectionListener(this);
				lastUpdatedList = 1;
				for (int i = 0; i < bindingValue.getBindingPath().size(); i++) {
					BindingPathElement pathElement = bindingValue.getBindingPath().elementAt(i);
					if (i + 2 == getVisibleColsCount()) {
						makeNewJList();
					}
					if (!(bindingValue.isConnected() && bindingValue.isLastBindingPathElement(pathElement, i))) {
						DMType resultingType = bindingValue.getBindingPath().getResultingTypeAtIndex(i);
						listAtIndex(i + 2).setModel(_bindingSelector.getListModelFor(resultingType));
						lastUpdatedList = i + 2;
					}
					listAtIndex(i + 1).removeListSelectionListener(this);
					if (pathElement instanceof DMProperty) {
						BindingColumnElement propertyElementToSelect = listAtIndex(i + 1).getModel().getElementFor(pathElement);
						listAtIndex(i + 1).setSelectedValue(propertyElementToSelect, true);
					} else if (pathElement instanceof MethodCall) {
						BindingColumnElement methodElementToSelect = listAtIndex(i + 1).getModel().getElementFor(
								((MethodCall) pathElement).getMethod());
						listAtIndex(i + 1).setSelectedValue(methodElementToSelect, true);
					}
					listAtIndex(i + 1).addListSelectionListener(this);
					if (i < bindingValue.getBindingPath().size() - 1) {
						listAtIndex(i).setFilter(null);
					}
				}
			}

			// Remove and clean unused lists
			cleanLists(lastUpdatedList);

			if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				bindingValueRepresentation.setText(_bindingSelector.renderedString(bindingValue));
				bindingValueRepresentation.setForeground(bindingValue.isBindingValid() ? Color.BLACK : Color.RED);
				updateMethodCallPanel();
			}

			currentTypeLabel.setText(FlexoLocalization.localizedForKey("no_type"));
			currentTypeLabel.setToolTipText(null);

		} else if (binding instanceof TranstypedBinding) {
			getTranstyperBindingsModel().setModel((TranstypedBinding) binding);
			if (((TranstypedBinding) binding).getTranstyper() != null) {
				currentTypeLabel.setText(((TranstypedBinding) binding).getTranstyper().getName());
				currentTypeLabel.setToolTipText(((TranstypedBinding) binding).getTranstyper().getDescription());
			} else {
				currentTypeLabel.setText(FlexoLocalization.localizedForKey("no_transtyper_defined"));
				currentTypeLabel.setToolTipText(null);
			}
		}

		updateSearchedTypeLabel();

		if (binding != null) {
			if (BindingSelector.logger.isLoggable(Level.FINE)) {
				BindingSelector.logger.fine("Binding " + binding + " isValid()=" + binding.isBindingValid());
			} else if (BindingSelector.logger.isLoggable(Level.FINE)) {
				BindingSelector.logger.fine("Binding is null");
			}
		}

		// Set connect button state
		_connectButton.setEnabled(binding != null && binding.isBindingValid());
		if (binding != null && binding.isBindingValid()) {
			if (ToolBox.isMacOSLaf()) {
				_connectButton.setSelected(true);
			}
		}
		if (binding != null) {
			_bindingSelector.getTextField().setForeground(binding.isBindingValid() ? Color.BLACK : Color.RED);
		}

		if (_bindingSelector.getAllowsStaticValues() && staticBindingPanel != null) {
			staticBindingPanel.updateStaticBindingPanel();
		}

		if (_bindingSelector.getAllowsTranstypers() && _bindingSelector.areSomeTranstyperAvailable() && transtypersPanel != null) {
			transtypersPanel.updateTranstyperPanel();
		}

		if (binding instanceof BindingValue) {
			setEditStaticValue(false);
		} else if (binding instanceof StaticBinding) {
			setEditStaticValue(true);
		} else if (binding instanceof TranstypedBinding) {
			setEditTranstypedBinding(true);
		}

	}

	private void cleanLists(int lastUpdatedList) {
		// Remove unused lists
		int lastVisibleList = defaultVisibleColCount - 1;
		if (lastUpdatedList > lastVisibleList) {
			lastVisibleList = lastUpdatedList;
		}
		int currentSize = getVisibleColsCount();
		for (int i = lastVisibleList + 1; i < currentSize; i++) {
			JList toRemove = listAtIndex(getVisibleColsCount() - 1);
			deleteJList(toRemove);
		}
		// Sets model to null for visible but unused lists
		for (int i = lastUpdatedList + 1; i < getVisibleColsCount(); i++) {
			JList list = listAtIndex(i);
			list.setModel(EMPTY_MODEL);
		}

	}

	private boolean editStaticValue;

	boolean getEditStaticValue() {
		return editStaticValue;
	}

	void setEditStaticValue(boolean aFlag) {
		if (!_bindingSelector.getAllowsStaticValues() || staticBindingPanel == null) {
			return;
		}
		if (editStaticValue != aFlag) {
			editStaticValue = aFlag;
			if (editStaticValue) {
				staticBindingPanel.enableStaticBindingPanel();
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(false);
				}
			} else {
				staticBindingPanel.disableStaticBindingPanel();
				_bindingSelector.setEditedObject(null, false);
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(true);
				}
				// logger.info("bindable="+getBindable()+" bm="+getBindingModel());
				_rootBindingColumnListModel = buildRootColumnListModel();
				// if (listAtIndex(0).getModel() instanceof EmptyColumnListModel) {
				listAtIndex(0).setModel(_bindingSelector.getRootListModel());
				// }
			}
			staticBindingPanel.updateStaticBindingPanel();
		}
	}

	private boolean editTranstypedBinding;

	boolean getEditTranstypedBinding() {
		return editTranstypedBinding;
	}

	void setEditTranstypedBinding(boolean aFlag) {
		if (aFlag) {
			_bindingSelector.activateTranstypedBindingMode();
		} else {
			_bindingSelector.activateNormalBindingMode();
		}

		if (!_bindingSelector.getAllowsTranstypers() || transtypersPanel == null) {
			return;
		}
		if (editTranstypedBinding != aFlag) {
			editTranstypedBinding = aFlag;
			if (editTranstypedBinding) {
				transtypersPanel.enableTranstyperPanel();
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(false);
				}
			} else {
				transtypersPanel.disableTranstyperPanel();
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(true);
				}
				_rootBindingColumnListModel = buildRootColumnListModel();
				listAtIndex(0).setModel(_bindingSelector.getRootListModel());
			}
			transtypersPanel.updateTranstyperPanel();
		}
	}

	protected BindingColumnListModel getRootColumnListModel() {
		if (_rootBindingColumnListModel == null) {
			_rootBindingColumnListModel = buildRootColumnListModel();
		}
		return _rootBindingColumnListModel;
	}

	protected BindingColumnListModel buildRootColumnListModel() {
		if (_bindingSelector.getBindingModel() != null) {
			if (BindingSelector.logger.isLoggable(Level.FINE)) {
				BindingSelector.logger.fine("buildRootColumnListModel() from " + _bindingSelector.getBindingModel());
			}
			return new RootBindingColumnListModel(_bindingSelector.getBindingModel());
		}
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("buildRootColumnListModel(): EMPTY_MODEL");
		}
		return EMPTY_MODEL;
	}

	// public void refreshColumListModel(DMType type){
	// _listModels.remove(type);
	// getColumnListModel(type);
	// }

	protected BindingColumnListModel getColumnListModel(DMType type) {
		if (type == null) {
			return EMPTY_MODEL;
		}
		if (type.getKindOfType() == DMType.KindOfType.RESOLVED) {
			if (_listModels.get(type) == null) {
				_listModels.put(type, makeColumnListModel(type));
			}
			return _listModels.get(type);
		} else {
			return EMPTY_MODEL;
		}
	}

	protected BindingColumnListModel makeColumnListModel(DMType type) {
		return new NormalBindingColumnListModel(type);
	}

	protected class BindingColumnElement {
		private final Typed _element;
		private final DMType _resultingType;

		protected BindingColumnElement(Typed element, DMType resultingType) {
			_element = element;
			_resultingType = resultingType;
		}

		public Typed getElement() {
			return _element;
		}

		public DMType getResultingType() {
			return _resultingType;
		}

		public String getLabel() {
			if (getElement() != null && getElement() instanceof BindingVariable) {
				return ((BindingVariable) getElement()).getVariableName();
			} else if (getElement() != null && getElement() instanceof DMProperty) {
				return ((DMProperty) getElement()).getName();
			} else if (getElement() != null && getElement() instanceof DMMethod) {
				DMMethod method = (DMMethod) getElement();
				return method.getSimplifiedSignature();
			}
			return "???";
		}

		public String getTypeStringRepresentation() {
			if (getResultingType() == null) {
				return FlexoLocalization.localizedForKey("no_type");
			} else {
				return getResultingType().getSimplifiedStringRepresentation();
			}
		}

		public String getTooltipText() {
			if (getElement() instanceof BindingVariable) {
				return getTooltipText((BindingVariable) getElement());
			} else if (getElement() instanceof DMProperty) {
				return getTooltipText((DMProperty) getElement(), getResultingType());
			} else if (getElement() instanceof DMMethod) {
				return getTooltipText((DMMethod) getElement(), getResultingType());
			} else {
				return "???";
			}
		}

		private String getTooltipText(BindingVariable bv) {
			String returned = "<html>";
			String resultingTypeAsString;
			if (bv.getType() != null) {
				resultingTypeAsString = bv.getType().getSimplifiedStringRepresentation();
				resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
				resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
			} else {
				resultingTypeAsString = "???";
			}
			returned += "<p><b>" + resultingTypeAsString + " " + bv.getVariableName() + "</b></p>";
			returned += "<p><i>"
					+ (bv.getDescription() != null ? bv.getDescription() : FlexoLocalization.localizedForKey("no_description"))
					+ "</i></p>";
			returned += "</html>";
			return returned;
		}

		private String getTooltipText(DMProperty property, DMType resultingType) {
			String returned = "<html>";
			if (property.getName().equals("itemCoucou")) {
				System.out.println(property + " " + resultingType);
			}
			String resultingTypeAsString;
			if (resultingType != null) {
				resultingTypeAsString = resultingType.getSimplifiedStringRepresentation();
				resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
				resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
			} else {
				resultingTypeAsString = "???";
			}
			returned += "<p><b>" + resultingTypeAsString + " " + property.getName() + "</b></p>";
			returned += "<p><i>"
					+ (property.getDescription() != null ? property.getDescription() : FlexoLocalization.localizedForKey("no_description"))
					+ "</i></p>";
			returned += "</html>";
			return returned;
		}

		private String getTooltipText(DMMethod method, DMType resultingType) {
			String returned = "<html>";
			String resultingTypeAsString;
			if (resultingType != null) {
				resultingTypeAsString = resultingType.getSimplifiedStringRepresentation();
				resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
				resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
			} else {
				resultingTypeAsString = "???";
			}
			returned += "<p><b>" + resultingTypeAsString + " " + method.getSimplifiedSignature() + "</b></p>";
			returned += "<p><i>"
					+ (method.getDescription() != null ? method.getDescription() : FlexoLocalization.localizedForKey("no_description"))
					+ "</i></p>";
			returned += "</html>";
			return returned;
		}

	}

	abstract class BindingColumnListModel extends AbstractListModel {
		public void fireModelChanged() {
			fireContentsChanged(this, 0, getUnfilteredSize() - 1);
		}

		public BindingColumnElement getElementFor(Typed element) {
			for (int i = 0; i < getSize(); i++) {
				if (getElementAt(i).getElement() == element) {
					return getElementAt(i);
				}
			}
			return null;
		}

		public void updateValues() {
		}

		private String filter = null;

		public String getFilter() {
			return filter;
		}

		public void setFilter(String aFilter) {
			filter = aFilter;
			fireModelChanged();
		}

		@Override
		public int getSize() {
			if (getFilter() == null && !AdvancedPrefs.hideFilteredObjects()) {
				return getUnfilteredSize();
			}
			int returned = 0;
			if (!AdvancedPrefs.hideFilteredObjects()) {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter)) {
						returned++;
					}
				}
			} else if (getFilter() == null) {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (!isFiltered(getUnfilteredElementAt(i))) {
						returned++;
					}
				}
			} else {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter) && !isFiltered(getUnfilteredElementAt(i))) {
						returned++;
					}
				}
			}
			return returned;
		}

		@Override
		public BindingColumnElement getElementAt(int index) {
			if (getFilter() == null && !AdvancedPrefs.hideFilteredObjects()) {
				return getUnfilteredElementAt(index);
			}
			if (!AdvancedPrefs.hideFilteredObjects()) {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter)) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			} else if (getFilter() == null) {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (!isFiltered(getUnfilteredElementAt(i))) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			} else {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter) && !isFiltered(getUnfilteredElementAt(i))) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			}
			return null;
		}

		private boolean isFiltered(BindingColumnElement columnElement) {
			if (columnElement.getElement() != null && columnElement.getElement() instanceof BindingVariable) {
				BindingVariable bv = (BindingVariable) columnElement.getElement();
				if (bv.getType() == null) {
					return true;
				}
			} else if (columnElement.getElement() != null && columnElement.getElement() instanceof DMProperty) {
				DMProperty property = (DMProperty) columnElement.getElement();
				AbstractBinding binding = BindingSelectorPanel.this._bindingSelector.getEditedObject();
				if (binding != null && binding instanceof BindingValue) {
					BindingValue bindingValue = (BindingValue) binding;
					if (bindingValue.isConnected() && bindingValue.isLastBindingPathElement(property, getIndexOfList(this) - 1)) {
						// setIcon(label, CONNECTED_ICON, list);
					} else if (columnElement.getResultingType() != null) {
						if (columnElement.getResultingType().getKindOfType() == KindOfType.RESOLVED) {
							if (columnElement.getResultingType().getBaseEntity() == null) {
								BindingSelector.logger.warning("Unexpected type: " + property.getType() + " kind: "
										+ property.getType().getKindOfType());
							} else if (columnElement.getResultingType().getBaseEntity().getAccessibleProperties().size() > 0) {
								// setIcon(label, ARROW_RIGHT_ICON, list);
							} else {
								if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition() != null
										&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType() != null
										&& !BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType()
												.isAssignableFrom(columnElement.getResultingType(), true)) {
									return true;
								}
								if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition() != null
										&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getIsSettable()
										&& !property.getIsSettable()) {
									return true;
								}
							}
						}
					}
				}
			} else if (columnElement.getElement() != null && columnElement.getElement() instanceof DMMethod) {
				DMMethod method = (DMMethod) columnElement.getElement();

				String methodAsString = method.getSimplifiedSignature();
				int idx = getIndexOfList(this);
				if (idx > 0 && _lists.elementAt(idx - 1).getSelectedValue() != null) {
					DMType context = ((BindingColumnElement) _lists.elementAt(idx - 1).getSelectedValue()).getResultingType();
					methodAsString = method.getSimplifiedSignatureInContext(context);
				}

				AbstractBinding binding = BindingSelectorPanel.this._bindingSelector.getEditedObject();
				if (binding instanceof BindingValue) {
					BindingValue bindingValue = (BindingValue) binding;
					BindingPathElement bpe = bindingValue.getBindingPathElementAtIndex(getIndexOfList(this) - 1);
					if (bindingValue.isConnected() && bindingValue.isLastBindingPathElement(bpe, getIndexOfList(this) - 1)
							&& bpe instanceof MethodCall && ((MethodCall) bpe).getMethod() == method) {
					} else if (columnElement.getResultingType() != null && columnElement.getResultingType().getBaseEntity() != null) {
						if (columnElement.getResultingType().getBaseEntity().getAccessibleProperties().size()
								+ columnElement.getResultingType().getBaseEntity().getAccessibleMethods().size() > 0) {
						} else {
							if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition() != null
									&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType() != null
									&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType().getBaseEntity() != null
									&& !BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType().getBaseEntity()
											.isAncestorOf(columnElement.getResultingType().getBaseEntity())) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		public abstract int getUnfilteredSize();

		public abstract BindingColumnElement getUnfilteredElementAt(int index);

	}

	private class NormalBindingColumnListModel extends BindingColumnListModel implements FlexoObserver {
		private final DMType _type;
		private final Vector<DMProperty> _accessibleProperties;
		private final Vector<DMMethod> _accessibleMethods;
		private final Vector<BindingColumnElement> _elements;

		NormalBindingColumnListModel(DMType type) {
			super();
			_type = type;
			// logger.info("Build NormalBindingColumnListModel for "+type);
			_accessibleProperties = new Vector<DMProperty>();
			_accessibleMethods = new Vector<DMMethod>();
			_elements = new Vector<BindingColumnElement>();
			if (_type.getBaseEntity() != null) {
				_type.getBaseEntity().addObserver(this);
			}
			updateValues();
		}

		@Override
		public void updateValues() {
			_accessibleProperties.clear();
			_accessibleMethods.clear();
			for (BindingColumnElement bce : _elements) {
				if (bce.getElement() instanceof FlexoObservable) {
					((FlexoObservable) bce.getElement()).deleteObserver(this);
				}
			}
			_elements.clear();
			if (_type.getBaseEntity() == null || _type.getBaseEntity().isDeleted()) {
				return;
			}

			_accessibleProperties.addAll(_type.getBaseEntity().getAccessibleProperties());
			if (BindingSelectorPanel.this._bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				_accessibleMethods.addAll(_type.getBaseEntity().getAccessibleMethods());
			}
			for (DMProperty p : _accessibleProperties) {
				_elements.add(new BindingColumnElement(p, DMType.makeInstantiatedDMType(p.getResultingType(), _type)));
			}
			for (DMMethod m : _accessibleMethods) {
				_elements.add(new BindingColumnElement(m, DMType.makeInstantiatedDMType(m.getType(), _type)));
			}
			for (BindingColumnElement bce : _elements) {
				if (bce.getElement() instanceof FlexoObservable) {
					((FlexoObservable) bce.getElement()).addObserver(this);
				}
			}
		}

		@Override
		public int getUnfilteredSize() {
			return _elements.size();
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			if (index < _elements.size() && index >= 0) {
				return _elements.elementAt(index);
			}
			return null;
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if ((dataModification instanceof PropertyRegistered || dataModification instanceof PropertyUnregistered
					|| dataModification instanceof PropertiesReordered || dataModification instanceof DMAttributeDataModification
					&& dataModification.propertyName().equals("parentType") || dataModification instanceof DMAttributeDataModification
					&& dataModification.propertyName().equals("isClassProperty")

			)
					&& observable.equals(_type.getBaseEntity())) {
				updateValues();
				fireModelChanged();
			}
			if (dataModification instanceof DMAttributeDataModification
					&& (dataModification.propertyName().equals("returnType") || dataModification.propertyName().equals("type"))) {
				updateValues();
				fireModelChanged();
				if (BindingSelectorPanel.this._bindingSelector._selectorPanel != null) {
					BindingSelectorPanel.this._bindingSelector._selectorPanel.update();
				}
			}
			if (dataModification instanceof ObjectDeleted) {
				observable.deleteObserver(this);
			}
		}

	}

	class EmptyColumnListModel extends BindingColumnListModel {
		@Override
		public int getUnfilteredSize() {
			return 0;
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			return null;
		}

	}

	private class RootBindingColumnListModel extends BindingColumnListModel {
		private final BindingModel _myBindingModel;
		private final Vector<BindingColumnElement> _elements;

		RootBindingColumnListModel(BindingModel bindingModel) {
			super();
			_myBindingModel = bindingModel;
			_elements = new Vector<BindingColumnElement>();
			updateValues();
		}

		@Override
		public void updateValues() {
			if (BindingSelector.logger.isLoggable(Level.FINE)) {
				BindingSelector.logger.fine("BindingModel is: " + _myBindingModel + " with " + _myBindingModel.getBindingVariablesCount());
			}

			_elements.clear();
			for (int i = 0; i < _myBindingModel.getBindingVariablesCount(); i++) {
				_elements.add(new BindingColumnElement(_myBindingModel.getBindingVariableAt(i), _myBindingModel.getBindingVariableAt(i)
						.getType()));
			}
		}

		@Override
		public int getUnfilteredSize() {
			return _elements.size();
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			return _elements.elementAt(index);
		}

		@Override
		public String toString() {
			return "RootBindingColumnListModel with " + getSize() + " elements";
		}

	}

	protected class TypeResolver extends MouseMotionAdapter {

		private final JList list;

		protected TypeResolver(JList aList) {
			currentFocused = null;
			list = aList;
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			// Get item index
			int index = list.locationToIndex(e.getPoint());

			// Get item
			if (index < 0 || index >= list.getModel().getSize()) {
				return;
			}
			BindingColumnElement item = ((BindingColumnListModel) list.getModel()).getElementAt(index);

			if (item != currentFocused) {
				currentFocused = item;
				currentTypeLabel.setText(currentFocused.getTypeStringRepresentation());
			}
		}
	}

	protected class BindingSelectorCellRenderer extends DefaultListCellRenderer {

		private JPanel panel;
		private JLabel iconLabel;

		public BindingSelectorCellRenderer() {
			panel = new JPanel(new BorderLayout());
			iconLabel = new JLabel();
			panel.add(iconLabel, BorderLayout.EAST);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object bce, int index, boolean isSelected, boolean cellHasFocus) {
			JComponent returned = (JComponent) super.getListCellRendererComponent(list, bce, index, isSelected, cellHasFocus);
			if (returned instanceof JLabel) {
				JLabel label = (JLabel) returned;
				if (bce instanceof BindingColumnElement) {
					BindingColumnElement columnElement = (BindingColumnElement) bce;
					if (columnElement.getElement() != null && columnElement.getElement() instanceof BindingVariable) {
						BindingVariable bv = (BindingVariable) columnElement.getElement();
						label.setText(columnElement.getLabel());
						returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
						if (bv.getType() != null) {
							returned.setToolTipText(columnElement.getTooltipText());
						} else {
							label.setForeground(Color.GRAY);
						}
					} else if (columnElement.getElement() != null && columnElement.getElement() instanceof DMProperty) {
						DMProperty property = (DMProperty) columnElement.getElement();
						label.setText(columnElement.getLabel());
						AbstractBinding binding = BindingSelectorPanel.this._bindingSelector.getEditedObject();
						if (binding != null && binding instanceof BindingValue) {
							BindingValue bindingValue = (BindingValue) binding;
							if (bindingValue.isConnected() && bindingValue.isLastBindingPathElement(property, _lists.indexOf(list) - 1)) {
								returned = getIconLabelComponent(label, FIBIconLibrary.CONNECTED_ICON);
							} else if (columnElement.getResultingType() != null) {
								if (columnElement.getResultingType().getKindOfType() == KindOfType.RESOLVED) {
									if (columnElement.getResultingType().getBaseEntity() == null) {
										BindingSelector.logger.warning("Unexpected type: " + property.getType() + " kind: "
												+ property.getType().getKindOfType());
									} else if (columnElement.getResultingType().getBaseEntity().getAccessibleProperties().size() > 0) {
										returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
									} else {
										if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition() != null
												&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType() != null
												&& !BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType()
														.isAssignableFrom(columnElement.getResultingType(), true)) {
											label.setForeground(Color.GRAY);
										}
										if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition() != null
												&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getIsSettable()
												&& !property.getIsSettable()) {
											label.setForeground(Color.GRAY);
										}
									}
								}
							}
						}
						if (property != null) {
							returned.setToolTipText(columnElement.getTooltipText());
						}

					} else if (columnElement.getElement() != null && columnElement.getElement() instanceof DMMethod) {
						DMMethod method = (DMMethod) columnElement.getElement();

						String methodAsString = method.getSimplifiedSignature();
						int idx = _lists.indexOf(list);
						if (idx > 0) {
							DMType context = ((BindingColumnElement) _lists.elementAt(idx - 1).getSelectedValue()).getResultingType();
							methodAsString = method.getSimplifiedSignatureInContext(context);
						}

						label.setText(methodAsString);
						AbstractBinding binding = BindingSelectorPanel.this._bindingSelector.getEditedObject();
						if (binding != null && binding instanceof BindingValue) {
							BindingValue bindingValue = (BindingValue) binding;
							BindingPathElement bpe = bindingValue.getBindingPathElementAtIndex(_lists.indexOf(list) - 1);
							if (bindingValue.isConnected() && bindingValue.isLastBindingPathElement(bpe, _lists.indexOf(list) - 1)
									&& bpe instanceof MethodCall && ((MethodCall) bpe).getMethod() == method) {
								returned = getIconLabelComponent(label, FIBIconLibrary.CONNECTED_ICON);
							} else if (columnElement.getResultingType() != null && columnElement.getResultingType().getBaseEntity() != null) {
								if (columnElement.getResultingType().getBaseEntity().getAccessibleProperties().size()
										+ columnElement.getResultingType().getBaseEntity().getAccessibleMethods().size() > 0) {
									returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
								} else {
									if (BindingSelectorPanel.this._bindingSelector.getBindingDefinition() != null
											&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType() != null
											&& BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType().getBaseEntity() != null
											&& !BindingSelectorPanel.this._bindingSelector.getBindingDefinition().getType().getBaseEntity()
													.isAncestorOf(columnElement.getResultingType().getBaseEntity())) {
										label.setForeground(Color.GRAY);
									}
								}
							}
						}
						if (method.getType() != null) {
							returned.setToolTipText(columnElement.getTooltipText());
						}
					}
				} else {
					// Happen because of prototype value !
					// logger.warning("Unexpected type: "+bce+" of "+(bce!=null?bce.getClass():"null"));
				}

				/*if (isObjectFiltered) {
					label.setFont(label.getFont().deriveFont(Font.BOLD));
				}*/

			}
			return returned;
		}

		private JComponent getIconLabelComponent(JLabel label, Icon icon) {
			iconLabel.setVisible(true);
			iconLabel.setIcon(icon);
			iconLabel.setOpaque(label.isOpaque());
			iconLabel.setBackground(label.getBackground());
			panel.setToolTipText(label.getToolTipText());
			if (label.getParent() != panel) {
				panel.add(label);
			}
			return panel;
		}

		/*private void setIcon(JLabel label, Icon icon, JList list) {
			label.setIcon(icon);
			label.setHorizontalAlignment(SwingConstants.LEFT);
			label.setHorizontalTextPosition(SwingConstants.LEFT);
			FontMetrics fm = label.getFontMetrics(label.getFont());
			int labelLength = fm.stringWidth(label.getText() == null ? "" : label.getText());
			System.err.println(label.getText()+" "+labelLength + " "+list.getWidth());
			label.setIconTextGap(list.getWidth() - 20 - labelLength);
		}*/
	}

	protected JPanel getControlPanel() {
		return _controlPanel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		AbstractBinding bindingValue = _bindingSelector.getEditedObject();
		if (bindingValue == null) {
			if (_bindingSelector.getBindingDefinition() != null && _bindingSelector.getBindable() != null) {
				bindingValue = _bindingSelector.makeBinding();
				// bindingValue.setBindingVariable(getSelectedBindingVariable());
				// setEditedObject(bindingValue);
				// fireEditedObjectChanged();
			} else {
				return;
			}
		}
		JList list = (JList) e.getSource();
		int index = _lists.indexOf(list);
		_selectedPathElementIndex = index;
		if (index < 0) {
			return;
		}
		int newSelectedIndex = list.getSelectedIndex();
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("I select something from list at index " + index + " selected=" + newSelectedIndex);
		}
		if (newSelectedIndex < 0) {
			return;
			// if (index == 0 && !(list.getSelectedValue() instanceof BindingVariable)) index = index+1;
			/*if (index == 0 && (list.getSelectedValue() instanceof BindingVariable)) {
			    if (list.getSelectedValue() != bindingValue.getBindingVariable()) {
			        bindingValue.setBindingVariable((BindingVariable) list.getSelectedValue());
			        setEditedObject(bindingValue);
			        fireEditedObjectChanged();
			    }
			 } else {
			     DMObject selectedValue = (DMObject)list.getSelectedValue();
			    if (selectedValue instanceof DMProperty) {
			        if (selectedValue != bindingValue.getBindingPathElementAtIndex(index - 1)) {
			            bindingValue.setBindingPathElementAtIndex((DMProperty)selectedValue, index - 1);
			            setEditedObject(bindingValue);
			            fireEditedObjectChanged();
			        }
			     }
			    else if ((selectedValue instanceof DMMethod) && (_allowsCompoundBindings)) {
			        BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(index - 1);
			        if (!(currentElement instanceof MethodCall)
			                || (((MethodCall)currentElement).getMethod() != selectedValue)) {
			            DMMethod method = (DMMethod)selectedValue;
			            MethodCall newMethodCall = new MethodCall(bindingValue,method);
			            bindingValue.setBindingPathElementAtIndex(newMethodCall, index - 1);
			            setEditedObject(bindingValue);
			            fireEditedObjectChanged();                                    }
			     }
			}*/
		}

		// This call will perform BV edition
		_bindingSelector.valueSelected(index, list, bindingValue);

		list.removeListSelectionListener(this);
		list.setSelectedIndex(newSelectedIndex);
		list.addListSelectionListener(this);
	}

	private boolean hasBindingPathForm(String textValue) {
		if (textValue.length() == 0) {
			return false;
		}

		boolean startingPathItem = true;
		for (int i = 0; i < textValue.length(); i++) {
			char c = textValue.charAt(i);
			if (c == '.') {
				startingPathItem = true;
			} else {
				boolean isNormalChar = c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' && !startingPathItem;
				if (!isNormalChar) {
					return false;
				}
				startingPathItem = false;
			}
		}
		return true;
	}

	@Override
	protected void synchronizePanelWithTextFieldValue(String textValue) {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("Request synchronizePanelWithTextFieldValue " + textValue);
		}

		try {
			_bindingSelector.isUpdatingModel = true;

			if (!_bindingSelector.popupIsShown() && textValue != null
					&& !_bindingSelector.isAcceptableAsBeginningOfStaticBindingValue(textValue)) {
				boolean requestFocus = _bindingSelector.getTextField().hasFocus();
				_bindingSelector.openPopup();
				if (requestFocus) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							BindingSelectorPanel.this._bindingSelector.getTextField().requestFocus();
						}
					});
				}
			}

			if (_bindingSelector.getTextField().hasFocus()) {
				if (_bindingSelector.getEditedObject() != null && _bindingSelector.getEditedObject() instanceof BindingValue) {
					((BindingValue) _bindingSelector.getEditedObject()).disconnect();
				}
				if (_bindingSelector._selectorPanel != null) {
					filterWithCurrentInput(textValue);
				}
			}

			if (textValue == null || !textValue.equals(_bindingSelector.renderedString(_bindingSelector.getEditedObject()))) {
				_bindingSelector.getTextField().setForeground(Color.RED);
			} else {
				_bindingSelector.getTextField().setForeground(Color.BLACK);
			}

		} finally {
			_bindingSelector.isUpdatingModel = false;
		}

	}

	private void filterWithCurrentInput(String textValue) {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("Try to filter for current input " + textValue);
		}

		if (!hasBindingPathForm(textValue)) {
			return;
		}

		StringTokenizer tokenizer = new StringTokenizer(textValue, ".", false);
		boolean isCurrentlyValid = true;
		int listIndex = 0;
		String element = null;
		while (isCurrentlyValid && tokenizer.hasMoreTokens()) {
			element = tokenizer.nextToken();
			BindingColumnElement col_element = findElementEquals(_lists.get(listIndex).getModel(), element);
			if (col_element == null) {
				isCurrentlyValid = false;
			} else {
				_bindingSelector.setUpdatingModel(true);
				if (!ensureBindingValueExists()) {
					_bindingSelector.setUpdatingModel(false);
					return;
				}

				_lists.get(listIndex).removeListSelectionListener(this);
				_lists.get(listIndex).setFilter(null);
				_lists.get(listIndex).setSelectedValue(col_element, true);
				_lists.get(listIndex).addListSelectionListener(this);
				_bindingSelector.valueSelected(listIndex, _lists.get(listIndex), _bindingSelector.getEditedObject());
				_bindingSelector.setUpdatingModel(false);
				listIndex++;
			}
		}

		if (!isCurrentlyValid) {
			_lists.get(listIndex).setFilter(element);
			completionInfo = new CompletionInfo(_lists.get(listIndex), element, textValue);
			if (completionInfo.matchingElements.size() > 0) {
				BindingColumnElement col_element = completionInfo.matchingElements.firstElement();
				_lists.get(listIndex).removeListSelectionListener(this);
				_lists.get(listIndex).setSelectedValue(col_element, true);
				_lists.get(listIndex).addListSelectionListener(this);
			}

		}

		cleanLists(listIndex);
	}

	private CompletionInfo completionInfo;

	protected class CompletionInfo {
		String validPath = null;
		String completionInitPath = null;
		String commonBeginningPath = null;
		Vector<BindingColumnElement> matchingElements = null;

		protected CompletionInfo(FilteredJList list, String subPartialPath, String fullPath) {
			validPath = fullPath.substring(0, fullPath.lastIndexOf(".") + 1);
			completionInitPath = subPartialPath;
			matchingElements = findElementsMatching(list.getModel(), subPartialPath);
			if (matchingElements.size() == 1) {
				commonBeginningPath = matchingElements.firstElement().getLabel();
			} else if (matchingElements.size() > 1) {
				int endCommonPathIndex = 0;
				boolean foundDiff = false;
				while (!foundDiff) {
					if (endCommonPathIndex < matchingElements.firstElement().getLabel().length()) {
						char c = matchingElements.firstElement().getLabel().charAt(endCommonPathIndex);
						for (int i = 1; i < matchingElements.size(); i++) {
							if (matchingElements.elementAt(i).getLabel().charAt(endCommonPathIndex) != c) {
								foundDiff = true;
							}
						}
						if (!foundDiff) {
							endCommonPathIndex++;
						}
					} else {
						foundDiff = true;
					}
				}
				commonBeginningPath = matchingElements.firstElement().getLabel().substring(0, endCommonPathIndex);
			}
		}

		@Override
		public String toString() {
			return "CompletionInfo, completionInitPath=" + completionInitPath + " validPath=" + validPath + " commonBeginningPath="
					+ commonBeginningPath + " matchingElements=" + matchingElements;
		}

		private boolean alreadyAutocompleted = false;

		protected void autoComplete() {
			if (!alreadyAutocompleted) {
				BindingSelectorPanel.this._bindingSelector.getTextField().setText(validPath + commonBeginningPath);
			} else {
				BindingSelectorPanel.this._bindingSelector.getTextField().setText(validPath + commonBeginningPath + ".");
			}
			alreadyAutocompleted = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					BindingSelectorPanel.this._bindingSelector.getTextField().requestFocus();
				}
			});
		}

	}

	@Override
	protected void processEnterPressed() {
		if (BindingSelector.logger.isLoggable(Level.INFO)) {
			BindingSelector.logger.info("Pressed on ENTER");
		}
		if (_bindingSelector.getEditedObject() != null && _bindingSelector.getEditedObject().isBindingValid()) {
			_bindingSelector.apply();
		}
	}

	@Override
	protected void processDelete() {
		if (BindingSelector.logger.isLoggable(Level.INFO)) {
			BindingSelector.logger.info("Pressed on DELETE");
		}
		suppressSelection();
	}

	@Override
	protected void processBackspace() {
		if (BindingSelector.logger.isLoggable(Level.INFO)) {
			BindingSelector.logger.info("Pressed on BACKSPACE");
		}
		if (!suppressSelection()) {
			if (_bindingSelector.getTextField().getText().length() > 0) {
				_bindingSelector.getTextField().setText(
						_bindingSelector.getTextField().getText().substring(0, _bindingSelector.getTextField().getText().length() - 1));

			}
		}
	}

	private boolean suppressSelection() {
		if (_bindingSelector.getTextField().getText().length() > 0) {
			if (_bindingSelector.getTextField().getSelectedText() != null && _bindingSelector.getTextField().getSelectedText().length() > 0) {
				int begin = _bindingSelector.getTextField().getSelectionStart();
				int end = _bindingSelector.getTextField().getSelectionEnd();
				_bindingSelector.getTextField().setText(
						_bindingSelector.getTextField().getText().substring(0, begin)
								+ _bindingSelector.getTextField().getText()
										.substring(end, _bindingSelector.getTextField().getText().length()));
				return true;
			}
		}
		return false;
	}

	@Override
	protected void processTabPressed() {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("Pressed on TAB, completionInfo=" + completionInfo);
		}
		if (completionInfo != null) {
			completionInfo.autoComplete();
		}
	}

	@Override
	protected void processUpPressed() {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("Pressed on UP");
			// TODO: not implemented
		}
	}

	@Override
	protected void processDownPressed() {
		if (BindingSelector.logger.isLoggable(Level.FINE)) {
			BindingSelector.logger.fine("Pressed on DOWN");
		}
		if (!_bindingSelector.popupIsShown()) {
			_bindingSelector.openPopup();
		}
		if (_bindingSelector.getEditedObject() != null) {
			listAtIndex(StringUtils.countMatches(_bindingSelector.getTextField().getText(), ".")).requestFocus();
		} else {
			listAtIndex(0).requestFocus();
		}
	}

	boolean isKeyPathFromTextASubKeyPath(String inputText) {
		int dotCount = StringUtils.countMatches(inputText, ".");
		if (listAtIndex(dotCount) == null) {
			return false;
		}
		BindingColumnListModel listModel = listAtIndex(dotCount).getModel();
		String subPartialPath = inputText.substring(inputText.lastIndexOf(".") + 1);
		Vector<Integer> pathElementIndex = new Vector<Integer>();
		;
		BindingColumnElement pathElement = findElementMatching(listModel, subPartialPath, pathElementIndex);
		return pathElement != null;
	}

	boolean isKeyPathFromPanelValid() {
		if (_bindingSelector.getEditedObject() == null) {
			return false;
		}
		int i = 0;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			i++;
		}
		if (listAtIndex(i - 1).getSelectedValue() instanceof BindingColumnElement) {
			if (_bindingSelector.getBindingDefinition().getType() == null
					|| _bindingSelector.getBindingDefinition().getType()
							.isAssignableFrom(((BindingColumnElement) listAtIndex(i - 1).getSelectedValue()).getResultingType(), true)) {
				return true;
			}
		}
		return false;
	}

	BindingValue makeBindingValueFromPanel() {
		if (_bindingSelector.getEditedObject() == null || !(_bindingSelector.getEditedObject() instanceof BindingValue)) {
			return null;
		}
		int i = 1;
		BindingColumnElement last = null;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			last = (BindingColumnElement) listAtIndex(i).getSelectedValue();
			((BindingValue) _bindingSelector.getEditedObject()).setBindingPathElementAtIndex((BindingPathElement) last.getElement(), i - 1);
			i++;
		}
		if (last != null) {
			((BindingValue) _bindingSelector.getEditedObject()).removeBindingPathElementAfter((BindingPathElement) last.getElement());
		}
		return (BindingValue) _bindingSelector.getEditedObject();
	}

}