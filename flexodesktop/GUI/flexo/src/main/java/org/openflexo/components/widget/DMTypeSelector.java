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
package org.openflexo.components.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import org.openflexo.components.AskParametersPanel;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.widget.AbstractSelectorPanel.AbstractSelectorPanelOwner;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMEntity.DMTypeVariable;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMType.DMTypeStringConverter;
import org.openflexo.foundation.dm.DMType.WildcardBound;
import org.openflexo.foundation.dm.DMTypeOwner;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.DMEntityParameter;
import org.openflexo.foundation.param.DomainParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.EnumDropDownParameter;
import org.openflexo.foundation.param.IntegerParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.PropertyListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.IconLibrary;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.widget.DenaliWidget.WidgetLayout;
import org.openflexo.kvc.KVCObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.MouseOverButton;
import org.openflexo.swing.TextFieldCustomPopup;

import com.ibm.icu.util.StringTokenizer;

/**
 * Widget allowing to view and edit a DMType
 * 
 * @author sguerin
 * 
 */
public class DMTypeSelector extends TextFieldCustomPopup<DMType> implements FIBCustomComponent<DMType, DMTypeSelector> {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(DMTypeSelector.class.getPackage().getName());

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	protected FlexoProject _project;
	protected DMTypeOwner _owner;
	private DMType _revertValue;

	protected AbstractDMTypeSelectorPanel _selectorPanel;

	private FlexoEditor _editor;

	private boolean _displayTypeAsSimplified;

	private boolean _isReadOnly;

	private KeyAdapter completionListKeyAdapter;

	static enum EditionMode {
		BASIC_TYPE, COMPLEX_TYPE;
	}

	EditionMode editionMode = EditionMode.BASIC_TYPE;

	public DMTypeSelector(DMType editedObject) {
		this(null, editedObject, true);
	}

	public DMTypeSelector(FlexoProject project, DMType editedObject, boolean displayTypeAsSimplified) {
		super(editedObject, -1);
		_project = project;
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		_displayTypeAsSimplified = displayTypeAsSimplified;
		setFocusable(true);
		getTextField().setFocusable(true);
		getTextField().setEditable(true);
		if (editedObject != null && !editedObject.isBasicType()) {
			editionMode = EditionMode.COMPLEX_TYPE;
		}
		completionListKeyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (_selectorPanel != null && _selectorPanel.isUpdating()) {
					return;
				}

				if (_selectorPanel != null) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						logger.info("Process enter");
						_selectorPanel.processEnterPressed();
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
						logger.info("Process tab");
						_selectorPanel.processTabPressed();
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_UP) {
						logger.info("Process up");
						_selectorPanel.processUpPressed();
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						logger.info("Process down");
						_selectorPanel.processDownPressed();
						e.consume();
					}
				}

				if (!e.isActionKey() && e.getKeyCode() != KeyEvent.VK_SHIFT && e.getKeyCode() != KeyEvent.VK_ENTER
						&& e.getKeyCode() != KeyEvent.VK_TAB) {
					getCustomPanel();
					if (!popupIsShown()) {
						openPopup();
					}
					if (editionMode == EditionMode.BASIC_TYPE) {
						((BasicDMTypeSelectorPanel) _selectorPanel).textChanged();
					}

					/*SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							DMEntitySelector baseEntitySelector = _selectorPanel.getBaseEntitySelector();
							if (baseEntitySelector != null) {
								String lookupString = getTextField().getText();
								if (logger.isLoggable(Level.FINE)) logger.fine("lookup "+lookupString);
								baseEntitySelector.setText(lookupString);
								baseEntitySelector.addApplyCancelListener(new CustomPopup.ApplyCancelListener() {
									public void fireApplyPerformed() {
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												apply();
											}
										});
									}
									public void fireCancelPerformed() {
									}
								});
							}
						}
					});*/
				}

			}
		};
		getTextField().addKeyListener(completionListKeyAdapter);
	}

	@Override
	public void delete() {
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
			_selectorPanel = null;
		}
		if (unambigousEntities != null) {
			unambigousEntities.clear();
			unambigousEntities = null;
		}
		_owner = null;
		_editor = null;
		_project = null;
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(DMType oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = oldValue.clone();
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public DMType getRevertValue() {
		return _revertValue;
	}

	public FlexoProject getProject() {
		return _project;
	}

	public void setProject(FlexoProject project) {
		_project = project;
	}

	public DMTypeOwner getOwner() {
		return _owner;
	}

	@CustomComponentParameter(name = "owner", type = CustomComponentParameter.Type.MANDATORY)
	public void setOwner(DMTypeOwner owner) {
		_owner = owner;
		if (getOwner() != null && _selectorPanel != null) {
			_selectorPanel.setOwner(owner);
		}
	}

	@Override
	protected AbstractDMTypeSelectorPanel createCustomPanel(DMType editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		_selectorPanel.init();
		return _selectorPanel;
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

	protected AbstractDMTypeSelectorPanel makeCustomPanel(DMType editedObject) {
		// logger.info("editionMode="+editionMode);
		if (editionMode == EditionMode.COMPLEX_TYPE) {
			return new ComplexDMTypeSelectorPanel();
		} else if (editionMode == EditionMode.BASIC_TYPE) {
			return new BasicDMTypeSelectorPanel();
		}
		return null;
	}

	@Override
	public void updateCustomPanel(DMType editedObject) {
		if (_selectorPanel != null) {
			/*if (editedObject == null) {
			 if (editionMode != EditionMode.BASIC_TYPE) activateBasicTypeMode();
			}
			else {
			 if (editionMode == EditionMode.BASIC_TYPE && !editedObject.isBasicType()) activateComplexTypeMode();
			 else if (editionMode == EditionMode.COMPLEX_TYPE && editedObject.isBasicType()) activateBasicTypeMode();
			}*/
			_selectorPanel.update();
		}
	}

	@Override
	public void setEditedObject(DMType editedObject) {
		super.setEditedObject(editedObject);
		if (editedObject == null) {
			if (editionMode != EditionMode.BASIC_TYPE) {
				activateBasicTypeMode();
			}
		} else {
			if (editionMode == EditionMode.BASIC_TYPE && !editedObject.isBasicType()) {
				activateComplexTypeMode();
			} else if (editionMode == EditionMode.COMPLEX_TYPE && editedObject.isBasicType()) {
				activateBasicTypeMode();
			}
		}
	}

	@Override
	public String renderedString(DMType editedObject) {
		if (editedObject != null) {
			return _displayTypeAsSimplified ? editedObject.getSimplifiedStringRepresentation() : editedObject.getStringRepresentation();
		} else {
			return STRING_REPRESENTATION_WHEN_NULL;
		}
	}

	public abstract class AbstractDMTypeSelectorPanel extends ResizablePanel {
		abstract DMEntitySelector<DMEntity> getBaseEntitySelector();

		abstract boolean isUpdating();

		abstract void setOwner(DMTypeOwner owner);

		abstract void init();

		abstract void update();

		abstract void delete();

		abstract void processEnterPressed();

		abstract void processTabPressed();

		abstract void processUpPressed();

		abstract void processDownPressed();
	}

	public class BasicDMTypeSelectorPanel extends AbstractDMTypeSelectorPanel implements AbstractSelectorPanelOwner<DMEntity> {
		private JButton _applyButton;
		private JButton _cancelButton;
		private JButton _resetButton;
		private JPanel _controlPanel;
		private MouseOverButton complexTypeButton;

		private DMEntitySelectorPanel _entitySelectorPanel;

		private DMEntity editedEntity;

		public BasicDMTypeSelectorPanel() {
			_entitySelectorPanel = new DMEntitySelectorPanel();
			_entitySelectorPanel.setRootObject(getDataModel());
			_entitySelectorPanel.init();
		}

		@Override
		void delete() {
		}

		@Override
		DMEntitySelector<DMEntity> getBaseEntitySelector() {
			return null;
		}

		@Override
		void init() {

			setLayout(new BorderLayout());

			/*_controlPanel = new JPanel();
			_controlPanel.setLayout(new FlowLayout());
			_controlPanel.add(_applyButton = new JButton(FlexoLocalization.localizedForKey("ok")));
			_controlPanel.add(_cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel")));
			_controlPanel.add(_resetButton = new JButton(FlexoLocalization.localizedForKey("reset")));
			_applyButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e)
			    {
			         apply();
			    }
			});
			_cancelButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e)
			    {
			        cancel();
			    }
			});
			_resetButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e)
			    {
			        setEditedObject(null);
			        apply();
			    }
			});*/

			complexTypeButton = new MouseOverButton();
			complexTypeButton.setBorder(BorderFactory.createEmptyBorder());
			complexTypeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					activateComplexTypeMode();
				}
			});

			JLabel complexTypeButtonLabel = new JLabel("", SwingConstants.RIGHT);
			complexTypeButtonLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
			complexTypeButton.setNormalIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
			complexTypeButton.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
			complexTypeButton.setToolTipText(FlexoLocalization.localizedForKey("specify_complex_type"));
			complexTypeButtonLabel.setText(FlexoLocalization.localizedForKey("specify_complex_type") + "  ");

			JPanel complexTypePanel = new JPanel();
			complexTypePanel.setLayout(new BorderLayout());
			complexTypePanel.add(complexTypeButtonLabel, BorderLayout.CENTER);
			complexTypePanel.add(complexTypeButton, BorderLayout.EAST);

			add(complexTypePanel, BorderLayout.NORTH);

			// add(new JLabel("coucou"), BorderLayout.CENTER);
			add(_entitySelectorPanel, BorderLayout.CENTER);
			// add(_controlPanel, BorderLayout.SOUTH);
			update();
		}

		@Override
		boolean isUpdating() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		void setOwner(DMTypeOwner owner) {
			// TODO Auto-generated method stub

		}

		@Override
		void update() {
			DMType currentEditedType = DMTypeSelector.this.getEditedObject();
			if (currentEditedType != null && currentEditedType.isBasicType()) {
				editedEntity = currentEditedType.getBaseEntity();
				_entitySelectorPanel.update();
			}
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(getDefaultWidth(), getDefaultHeight());
		}

		@Override
		public Integer getDefaultWidth() {
			return 300;
		}

		@Override
		public Integer getDefaultHeight() {
			return 300;
		}

		@Override
		public void apply() {
			DMTypeSelector.this.apply();
		}

		@Override
		public void cancel() {
			DMTypeSelector.this.cancel();
		}

		@Override
		public KeyAdapter getCompletionListKeyAdapter() {
			return completionListKeyAdapter;
		}

		@Override
		public DMEntity getEditedObject() {
			return editedEntity;
		}

		@Override
		public void setEditedObject(DMEntity entity) {
			editedEntity = entity;
			if (entity == null) {
				DMTypeSelector.this.setEditedObject(null);
			} else {
				DMTypeSelector.this.setEditedObject(DMType.makeResolvedDMType(entity));
			}
		}

		@Override
		public FlexoEditor getEditor() {
			return DMTypeSelector.this.getEditor();
		}

		@Override
		public FlexoProject getProject() {
			return DMTypeSelector.this.getProject();
		}

		@Override
		public FlexoModelObject getRootObject() {
			return getDataModel();
		}

		@Override
		public JTextField getTextField() {
			return DMTypeSelector.this.getTextField();
		}

		@Override
		public boolean isProgrammaticalySet() {
			return DMTypeSelector.this.isProgrammaticalySet();
		}

		@Override
		public boolean isSelectable(FlexoObject object) {
			return object instanceof DMEntity;
		}

		@Override
		public void openPopup() {
			DMTypeSelector.this.openPopup();
		}

		@Override
		public void closePopup() {
			DMTypeSelector.this.closePopup();
		}

		@Override
		public boolean popupIsShown() {
			return DMTypeSelector.this.popupIsShown();
		}

		@Override
		public String renderedString(DMEntity editedObject) {
			if (editedObject != null) {
				return editedObject.getLocalizedName();
			}
			return "";
		}

		@Override
		public void setProgrammaticalySet(boolean aFlag) {
			DMTypeSelector.this.setProgrammaticalySet(aFlag);
		}

		@Override
		void processEnterPressed() {
			if (!_entitySelectorPanel.processEnterPressed()) {
				DMType newType = parseAndSelect(getTextField().getText());
				if (newType.isResolved()) {
					apply();
				}
			}
		}

		@Override
		void processTabPressed() {
			_entitySelectorPanel.processTabPressed();
		}

		@Override
		void processUpPressed() {
			_entitySelectorPanel.processUpPressed();
		}

		@Override
		void processDownPressed() {
			_entitySelectorPanel.processDownPressed();
		}

		protected class DMEntitySelectorPanel extends AbstractSelectorPanel<DMEntity> {
			protected DMEntitySelectorPanel() {
				super(BasicDMTypeSelectorPanel.this);
			}

			@Override
			protected ProjectBrowser createBrowser(FlexoProject project) {
				return new DataModelBrowser(/* project.getDataModel() */);
			}

		}

		protected class DataModelBrowser extends ProjectBrowser {

			// private DMModel _dmModel;

			protected DataModelBrowser(/* DMModel dataModel */) {
				super(getDataModel() != null ? getDataModel().getProject() : null, false);
				init();
				setDMViewMode(DMViewMode.Packages);
				setRowHeight(16);
			}

			@Override
			public void configure() {
				setFilterStatus(BrowserElementType.DM_REPOSITORY_FOLDER, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN, true);
				setFilterStatus(BrowserElementType.DM_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN, true);
				setFilterStatus(BrowserElementType.DM_PROPERTY, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.DM_EOATTRIBUTE, BrowserFilterStatus.HIDE);
				setFilterStatus(BrowserElementType.DM_EORELATIONSHIP, BrowserFilterStatus.HIDE);
			}

			@Override
			public FlexoModelObject getDefaultRootObject() {
				return getDataModel();
			}
		}

		public void textChanged() {
			_entitySelectorPanel.textWasChanged();
		}

	}

	public class ComplexDMTypeSelectorPanel extends AbstractDMTypeSelectorPanel {
		private JButton _applyButton;
		private JButton _cancelButton;
		private JButton _resetButton;
		private JPanel _controlPanel;
		private MouseOverButton basicTypeButton;

		AskParametersPanel _dataPanel;

		EnumDropDownParameter<DMType.KindOfType> kindOfTypeParameter;
		DMEntityParameter baseEntityParameter;
		TextFieldParameter unresolvedTypeParameter;
		IntegerParameter dimensionParameter;
		CheckboxParameter hasParametersParameter;
		PropertyListParameter<DMType.ParameterizedTypeVariable> parametersParam;
		DomainParameter domainParam;
		DynamicDropDownParameter<DMTypeVariable> typeVariableParam;
		PropertyListParameter<DMType.WildcardBound> upperBoundsParam;
		PropertyListParameter<DMType.WildcardBound> lowerBoundsParam;

		protected ListSelectionListener listSelectionListener;
		protected MouseAdapter listMouseListener;

		protected WildcardActions wildcardActions;

		protected ComplexDMTypeSelectorPanel() {
			super();
			wildcardActions = new WildcardActions();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = new Dimension();
			returned.width = 300;
			returned.height = 300;
			return returned;
		}

		<T> void dataChanged(ParameterDefinition<T> param, T oldValue, T newValue) {
			// if (isUpdating) return;

			if (oldValue == null && newValue == null) {
				return;
			}
			if (oldValue == null && newValue != null || oldValue != null && !oldValue.equals(newValue)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("dataChanged() for DMType old=" + oldValue + " new=" + newValue);
				}
				getTextField().setText(renderedString(getEditedObject()));
			}
		}

		DMType getOrCreateEditedType() {
			if (getEditedObject() != null) {
				return getEditedObject();
			} else {
				DMType returned = DMType.makeUnresolvedDMType("");
				returned.setOwner(getOwner());
				setEditedObject(returned);
				typeVariableParam
						.setAvailableValues(getEditedObject() != null && getEditedObject().getTypeVariableContext() != null ? getEditedObject()
								.getTypeVariableContext().getTypeVariables() : null);
				return returned;
			}
		}

		@Override
		protected void init() {
			setLayout(new BorderLayout());

			kindOfTypeParameter = new EnumDropDownParameter<DMType.KindOfType>("KindOfType", "", null, DMType.KindOfType.values());
			kindOfTypeParameter.setShowReset(false);
			kindOfTypeParameter.setExpandHorizontally(false);
			kindOfTypeParameter.setExpandVertically(false);
			kindOfTypeParameter.setWidgetLayout(WidgetLayout.LABEL_ABOVE_WIDGET_LAYOUT);
			kindOfTypeParameter.setDisplayLabel(false);
			kindOfTypeParameter.addValueListener(new ParameterDefinition.ValueListener<DMType.KindOfType>() {
				@Override
				public void newValueWasSet(ParameterDefinition<DMType.KindOfType> param, DMType.KindOfType oldValue,
						DMType.KindOfType newValue) {
					if (isUpdating) {
						return;
					}
					isUpdating = true;
					if (newValue == DMType.KindOfType.RESOLVED) {
						getOrCreateEditedType().setDimensions(0);
						dimensionParameter.setValue(0);
						dataChanged(param, oldValue, newValue);
					} else if (newValue == DMType.KindOfType.RESOLVED_ARRAY) {
						getOrCreateEditedType().setDimensions(1);
						dimensionParameter.setValue(1);
						dataChanged(param, oldValue, newValue);
					} else if (newValue == DMType.KindOfType.DKV) {
						getOrCreateEditedType().setBaseEntity(null);
						getOrCreateEditedType().setTypeVariable(null);
						getOrCreateEditedType().setDimensions(0);
						dimensionParameter.setValue(0);
					} else if (newValue == DMType.KindOfType.TYPE_VARIABLE) {
						getOrCreateEditedType().setDomain(null);
						getOrCreateEditedType().setBaseEntity(null);
						getOrCreateEditedType().setDimensions(0);
						// typeVariableParam.setAvailableValues(getEditedObject()!=null && getEditedObject().getTypeVariableContext() !=
						// null?getEditedObject().getTypeVariableContext().getTypeVariables():null);
						if (getOrCreateEditedType() != null && getOrCreateEditedType().getTypeVariableContext() != null) {
							typeVariableParam.setAvailableValues(getOrCreateEditedType().getTypeVariableContext().getTypeVariables());
							if (getOrCreateEditedType().getTypeVariableContext().getTypeVariables() != null
									&& getOrCreateEditedType().getTypeVariableContext().getTypeVariables().size() > 0) {
								getOrCreateEditedType().setTypeVariable(
										getOrCreateEditedType().getTypeVariableContext().getTypeVariables().firstElement());
							} else {
								getOrCreateEditedType().setTypeVariable(null);
							}
							dimensionParameter.setValue(0);
							typeVariableParam.setValue(getOrCreateEditedType().getTypeVariable());
						} else {
							typeVariableParam.setAvailableValues(null);
							typeVariableParam.setValue(null);
						}
						dataChanged(param, oldValue, newValue);
					} else if (newValue == DMType.KindOfType.WILDCARD) {
						getOrCreateEditedType().setProject(getProject());
						getOrCreateEditedType().setBaseEntity(null);
						getOrCreateEditedType().setTypeVariable(null);
						getOrCreateEditedType().setDomain(null);
						dimensionParameter.setValue(0);
						dataChanged(param, oldValue, newValue);
					}
					isUpdating = false;
				}
			});

			unresolvedTypeParameter = new TextFieldParameter("type", "type", "");
			unresolvedTypeParameter.setDepends("KindOfType");
			unresolvedTypeParameter.setConditional("KindOfType=" + '"' + DMType.KindOfType.UNRESOLVED.getStringRepresentation() + '"');
			unresolvedTypeParameter.addValueListener(new ParameterDefinition.ValueListener<String>() {
				@Override
				public void newValueWasSet(ParameterDefinition<String> param, String oldValue, String newValue) {
					if (isUpdating) {
						return;
					}
					if (newValue != null && !newValue.equals(oldValue)) {
						setEditedObject(getProject().getDataModel().getDmTypeConverter()
								.convertFromString(newValue, getOwner(), getProject()));
						logger.info("Create type " + getEditedObject());
						update();
					}
					dataChanged(param, oldValue, newValue);
				}
			});
			unresolvedTypeParameter.setValidateOnReturn(true);

			baseEntityParameter = new DMEntityParameter("baseType", "base_type", null);
			baseEntityParameter.addValueListener(new ParameterDefinition.ValueListener<DMEntity>() {
				@Override
				public void newValueWasSet(ParameterDefinition<DMEntity> param, DMEntity oldValue, DMEntity newValue) {
					if (isUpdating) {
						return;
					}
					if (oldValue != newValue) {
						getOrCreateEditedType().setBaseEntity(newValue);
						hasParametersParameter
								.setValue(getEditedObject() != null && getEditedObject().getBaseEntity() != null ? getEditedObject()
										.getBaseEntity().getTypeVariables().size() > 0 : false);
						parametersParam.setValue(getEditedObject() != null ? getEditedObject().getParameterizedTypeVariables() : null);
						dataChanged(param, oldValue, newValue);
						update();
					}
				}
			});
			baseEntityParameter.setDepends("KindOfType");
			baseEntityParameter.setConditional("KindOfType=" + '"' + DMType.KindOfType.RESOLVED.getStringRepresentation() + '"' + " OR "
					+ "KindOfType=" + '"' + DMType.KindOfType.RESOLVED_ARRAY.getStringRepresentation() + '"');

			dimensionParameter = new IntegerParameter("dimensions", "dim", 1);
			dimensionParameter.setDepends("KindOfType");
			dimensionParameter.setConditional("KindOfType=" + '"' + DMType.KindOfType.RESOLVED_ARRAY.getStringRepresentation() + '"');
			dimensionParameter.addValueListener(new ParameterDefinition.ValueListener<Integer>() {
				@Override
				public void newValueWasSet(ParameterDefinition<Integer> param, Integer oldValue, Integer newValue) {
					if (isUpdating) {
						return;
					}
					if (newValue != null && !newValue.equals(oldValue)) {
						getOrCreateEditedType().setDimensions(newValue);
						dataChanged(param, oldValue, newValue);
					}
				}
			});

			hasParametersParameter = new CheckboxParameter("hasParams", "", false);
			hasParametersParameter.setDepends("KindOfType,baseType");
			hasParametersParameter.setConditional("KindOfType=THIS_WIDGET_IS_NEVER_DISPLAYED");

			parametersParam = new PropertyListParameter<DMType.ParameterizedTypeVariable>("parameters", "parameters", null, 20, 3);
			parametersParam.setDisplayLabel(false);
			parametersParam.setExpandVertically(true);
			parametersParam.setDepends("KindOfType,baseType,hasParams");
			parametersParam.setConditional("hasParams=true AND " + "(KindOfType=" + '"'
					+ DMType.KindOfType.RESOLVED.getStringRepresentation() + '"' + " OR " + "KindOfType=" + '"'
					+ DMType.KindOfType.RESOLVED_ARRAY.getStringRepresentation() + '"' + ")");

			parametersParam.addReadOnlyTextFieldColumn("typeVariable.name", "variable", 50, true);

			PropertyListColumn valueAttributeColumn = parametersParam.addCustomColumn("value", "value",
					"org.openflexo.components.widget.DMTypeInspectorWidget", 200, true);
			valueAttributeColumn.setValueForParameter("displayTypeAsSimplified", "false");
			valueAttributeColumn.setValueForParameter("format", "simplifiedStringRepresentation");
			valueAttributeColumn.setValueForParameter("project", "project");
			valueAttributeColumn.setValueForParameter("owner", "owner");

			domainParam = new DomainParameter("domain", "domain", null);
			domainParam.addValueListener(new ParameterDefinition.ValueListener<Domain>() {
				@Override
				public void newValueWasSet(ParameterDefinition<Domain> param, Domain oldValue, Domain newValue) {
					if (isUpdating) {
						return;
					}
					if (oldValue != newValue) {
						getOrCreateEditedType().setDomain(newValue);
						dataChanged(param, oldValue, newValue);
						update();
					}
				}
			});
			domainParam.setDepends("KindOfType");
			domainParam.setConditional("KindOfType=" + '"' + DMType.KindOfType.DKV.getStringRepresentation() + '"');

			typeVariableParam = new DynamicDropDownParameter<DMTypeVariable>("typeVariable", "variable", null, null);
			typeVariableParam.setFormatter("name");
			typeVariableParam.setShowReset(false);
			typeVariableParam.setDepends("KindOfType");
			typeVariableParam.setConditional("KindOfType=" + '"' + DMType.KindOfType.TYPE_VARIABLE.getStringRepresentation() + '"');
			typeVariableParam.addValueListener(new ParameterDefinition.ValueListener<DMTypeVariable>() {
				@Override
				public void newValueWasSet(ParameterDefinition<DMTypeVariable> param, DMTypeVariable oldValue, DMTypeVariable newValue) {
					if (isUpdating) {
						return;
					}
					getOrCreateEditedType().setTypeVariable(newValue);
					dataChanged(param, oldValue, newValue);
				}
			});

			upperBoundsParam = new PropertyListParameter<DMType.WildcardBound>("upperBounds", "upper_bounds", null, 20, 2);
			upperBoundsParam.setDisplayLabel(false);
			upperBoundsParam.setExpandVertically(true);
			upperBoundsParam.setDepends("KindOfType");
			upperBoundsParam.setConditional("KindOfType=" + '"' + DMType.KindOfType.WILDCARD.getStringRepresentation() + '"');

			upperBoundsParam.addReadOnlyTextFieldColumn("extendsLabel", "", 60, true);

			PropertyListColumn upperBoundsValueAttributeColumn = upperBoundsParam.addCustomColumn("bound", "upper_bounds",
					"org.openflexo.components.widget.DMTypeInspectorWidget", 200, true);
			upperBoundsValueAttributeColumn.setValueForParameter("displayTypeAsSimplified", "false");
			upperBoundsValueAttributeColumn.setValueForParameter("format", "simplifiedStringRepresentation");
			upperBoundsValueAttributeColumn.setValueForParameter("project", "project");
			upperBoundsValueAttributeColumn.setValueForParameter("owner", "owner");

			upperBoundsParam.addAddAction("add", "wildcardActions.addUpperBound", "wildcardActions.addUpperBoundEnabled", null);
			upperBoundsParam.addDeleteAction("remove", "wildcardActions.removeUpperBound", "wildcardActions.removeUpperBoundEnabled", null);

			lowerBoundsParam = new PropertyListParameter<DMType.WildcardBound>("lowerBounds", "lower_bounds", null, 20, 2);
			lowerBoundsParam.setDisplayLabel(false);
			lowerBoundsParam.setExpandVertically(true);
			lowerBoundsParam.setDepends("KindOfType");
			lowerBoundsParam.setConditional("KindOfType=" + '"' + DMType.KindOfType.WILDCARD.getStringRepresentation() + '"');

			lowerBoundsParam.addReadOnlyTextFieldColumn("superLabel", "", 60, true);

			PropertyListColumn lowerBoundsValueAttributeColumn = lowerBoundsParam.addCustomColumn("bound", "lower_bounds",
					"org.openflexo.components.widget.DMTypeInspectorWidget", 200, true);
			lowerBoundsValueAttributeColumn.setValueForParameter("displayTypeAsSimplified", "false");
			lowerBoundsValueAttributeColumn.setValueForParameter("format", "simplifiedStringRepresentation");
			lowerBoundsValueAttributeColumn.setValueForParameter("project", "project");
			lowerBoundsValueAttributeColumn.setValueForParameter("owner", "owner");

			lowerBoundsParam.addAddAction("add", "wildcardActions.addLowerBound", "wildcardActions.addLowerBoundEnabled", null);
			lowerBoundsParam.addDeleteAction("remove", "wildcardActions.removeLowerBound", "wildcardActions.removeLowerBoundEnabled", null);

			_dataPanel = new AskParametersPanel(getProject(), kindOfTypeParameter, unresolvedTypeParameter, baseEntityParameter,
					dimensionParameter, hasParametersParameter, parametersParam, domainParam, typeVariableParam, upperBoundsParam,
					lowerBoundsParam);
			_dataPanel.getParametersModel().addObjectForKey(wildcardActions, "wildcardActions");
			if (getOwner() != null) {
				_dataPanel.getParametersModel().addObjectForKey(getOwner(), "owner");
			}

			// _dataPanel.setBackground(Color.RED);

			// _dataPanel.validate();

			_controlPanel = new JPanel();
			_controlPanel.setLayout(new FlowLayout());
			_controlPanel.add(_applyButton = new JButton(FlexoLocalization.localizedForKey("ok")));
			_controlPanel.add(_cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel")));
			_controlPanel.add(_resetButton = new JButton(FlexoLocalization.localizedForKey("reset")));
			_applyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					apply();
				}
			});
			_cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
			_resetButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setEditedObject(null);
					apply();
				}
			});

			basicTypeButton = new MouseOverButton();
			basicTypeButton.setBorder(BorderFactory.createEmptyBorder());
			basicTypeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					activateBasicTypeMode();
				}
			});

			JLabel basicTypeButtonLabel = new JLabel("", SwingConstants.RIGHT);
			basicTypeButtonLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
			basicTypeButton.setNormalIcon(IconLibrary.TOGGLE_ARROW_TOP_ICON);
			basicTypeButton.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
			basicTypeButton.setToolTipText(FlexoLocalization.localizedForKey("specify_basic_type"));
			basicTypeButtonLabel.setText(FlexoLocalization.localizedForKey("specify_basic_type") + "  ");

			JPanel basicTypePanel = new JPanel();
			basicTypePanel.setLayout(new BorderLayout());
			basicTypePanel.add(basicTypeButtonLabel, BorderLayout.CENTER);
			basicTypePanel.add(basicTypeButton, BorderLayout.EAST);

			add(basicTypePanel, BorderLayout.NORTH);

			// add(new JLabel("coucou"), BorderLayout.NORTH);
			add(_dataPanel, BorderLayout.CENTER);
			add(_controlPanel, BorderLayout.SOUTH);
			update();
		}

		@Override
		DMEntitySelector<DMEntity> getBaseEntitySelector() {
			DMEntityInspectorWidget widget = (DMEntityInspectorWidget) _dataPanel.getInspectorWidgetForParameter(baseEntityParameter);
			if (widget != null) {
				return widget.getSelector();
			}
			return null;
		}

		public class WildcardActions extends KVCObject {
			protected WildcardActions() {
				super();
			}

			public void addUpperBound() {
				DMType type = getOrCreateEditedType();
				if (type.getUpperBounds() == null) {
					type.setUpperBounds(new Vector<WildcardBound>());
				}
				DMType newObjectType = DMType.makeResolvedDMType(getProject().getDataModel().getDMEntity(Object.class));
				newObjectType.setOwner(type);
				newObjectType.setProject(getProject());
				type.addToUpperBounds(newObjectType);
				update();
			}

			public boolean addUpperBoundEnabled() {
				return true;
			}

			public boolean addUpperBoundEnabled(WildcardBound type) {
				return true;
			}

			public void addLowerBound() {
				DMType type = getOrCreateEditedType();
				if (type.getLowerBounds() == null) {
					type.setLowerBounds(new Vector<WildcardBound>());
				}
				DMType newObjectType = DMType.makeResolvedDMType(getProject().getDataModel().getDMEntity(Object.class));
				newObjectType.setOwner(type);
				newObjectType.setProject(getProject());
				type.addToLowerBounds(newObjectType);
				update();
			}

			public boolean addLowerBoundEnabled() {
				return true;
			}

			public boolean addLowerBoundEnabled(WildcardBound type) {
				return true;
			}

			public void removeUpperBound(WildcardBound type) {
				getEditedObject().getUpperBounds().remove(type);
				update();
			}

			public boolean removeUpperBoundEnabled(WildcardBound type) {
				return true;
			}

			public void removeLowerBound(WildcardBound type) {
				getEditedObject().getLowerBounds().remove(type);
				update();
			}

			public boolean removeLowerBoundEnabled(WildcardBound type) {
				return true;
			}

		}

		boolean isUpdating = false;

		@Override
		public boolean isUpdating() {
			return isUpdating;
		}

		@Override
		public void update() {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update with " + getEditedObject());
			}

			if (isUpdating) {
				return;
			}

			isUpdating = true;

			if (getEditedObject() == null || getEditedObject().getStringRepresentation() == null
					|| getEditedObject().getStringRepresentation().equals("")) {
				kindOfTypeParameter.setValue(DMType.KindOfType.RESOLVED);
			} else {
				kindOfTypeParameter.setValue(getEditedObject() != null ? getEditedObject().getKindOfType() : null);
			}
			unresolvedTypeParameter.setValue(getEditedObject() != null ? getEditedObject().getStringRepresentation() : null);
			baseEntityParameter.setValue(getEditedObject() != null ? getEditedObject().getBaseEntity() : null);
			dimensionParameter.setValue(getEditedObject() != null ? getEditedObject().getDimensions() : null);
			hasParametersParameter.setValue(getEditedObject() != null && getEditedObject().getBaseEntity() != null ? getEditedObject()
					.getBaseEntity().getTypeVariables().size() > 0 : false);
			parametersParam.setValue(getEditedObject() != null ? getEditedObject().getParameterizedTypeVariables() : null);
			domainParam.setValue(getEditedObject() != null ? getEditedObject().getDomain() : null);
			typeVariableParam
					.setAvailableValues(getEditedObject() != null && getEditedObject().getTypeVariableContext() != null ? getEditedObject()
							.getTypeVariableContext().getTypeVariables() : null);
			typeVariableParam.setValue(getEditedObject() != null ? getEditedObject().getTypeVariable() : null);
			upperBoundsParam.setValue(getEditedObject() != null ? getEditedObject().getUpperBounds() : null);
			lowerBoundsParam.setValue(getEditedObject() != null ? getEditedObject().getLowerBounds() : null);

			_dataPanel.update();

			isUpdating = false;

		}

		protected JPanel getControlPanel() {
			return _controlPanel;
		}

		@Override
		public void delete() {
		}

		@Override
		public void setOwner(DMTypeOwner owner) {
			_owner = owner;
			if (_dataPanel != null) {
				_dataPanel.getParametersModel().addObjectForKey(getOwner(), "owner");
				update();
			}
		}

		@Override
		void processEnterPressed() {
			DMType newType = parseAndSelect(getTextField().getText());
			if (newType.isResolved()) {
				apply();
			}
		}

		@Override
		void processTabPressed() {
		}

		@Override
		void processUpPressed() {
		}

		@Override
		void processDownPressed() {
		}

	}

	public DMType parseAndSelect(String aTypeAsString) {
		String typeToParse = "";
		StringTokenizer st = new StringTokenizer(aTypeAsString, " <>,?", true);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (" <>,?".indexOf(token) > -1) {
				typeToParse += token; // this is a delimiter
			} else {
				DMEntity e = getAllUnambigousEntities().get(token);
				if (e == null) {
					e = getAllUnambigousEntities().get(token.toUpperCase());
				}
				if (e != null) {
					typeToParse += e.getFullQualifiedName();
				} else {
					typeToParse += token;
				}
			}
		}
		DMTypeStringConverter converter = getDataModel().getDmTypeConverter();
		DMType type = converter.convertFromString(typeToParse);
		setEditedObject(type);
		return type;
	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? getEditedObject().clone() : null);
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void openPopup() {
		super.openPopup();
		getTextField().requestFocus();
		// This little piece of code is intented to avoid
		// textfield beeing whole selected after first typing
		// (at this point, popup open, text field gets focus back, select all
		// and the second push on a key erase the first char)
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextField().select(0, 0);
				getTextField().setCaretPosition(getTextField().getText().length());
			}
		});
	}

	@Override
	public void closePopup() {
		super.closePopup();
		deletePopup();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	@Override
	protected void pointerLeavesPopup() {
		cancel();
	}

	public AbstractDMTypeSelectorPanel getSelectorPanel() {
		return _selectorPanel;
	}

	public FlexoEditor getEditor() {
		return _editor;
	}

	/**
	 * Sets an editor if you want FlexoAction available on browser
	 * 
	 * @param editor
	 */
	public void setEditor(FlexoEditor editor) {
		_editor = editor;
	}

	public boolean getDisplayTypeAsSimplified() {
		return _displayTypeAsSimplified;
	}

	@CustomComponentParameter(name = "displayTypeAsSimplified", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDisplayTypeAsSimplified(boolean displayTypeAsSimplified) {
		_displayTypeAsSimplified = displayTypeAsSimplified;
	}

	public boolean getReadOnly() {
		return _isReadOnly;
	}

	// TODO: not implemented yet
	@CustomComponentParameter(name = "readOnly", type = CustomComponentParameter.Type.OPTIONAL)
	public void setReadOnly(boolean isReadOnly) {
		_isReadOnly = isReadOnly;
	}

	public void activateBasicTypeMode() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("activateBasicTypeMode() getEditedObject()=" + getEditedObject() + " editionMode=" + editionMode
					+ " popupIsShown()=" + popupIsShown() + " _selectorPanel=" + _selectorPanel);
		}
		if (_selectorPanel != null && editionMode != EditionMode.BASIC_TYPE) {
			editionMode = EditionMode.BASIC_TYPE;
			boolean showAgain = false;
			if (popupIsShown()) {
				showAgain = true;
				closePopup(false);
			}
			if (getEditedObject() != null) {
				if (getEditedObject().getBaseEntity() != null) {
					_editedObject = DMType.makeResolvedDMType(getEditedObject().getBaseEntity());
				} else {
					_editedObject = null;
				}
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
		editionMode = EditionMode.BASIC_TYPE;
	}

	public void activateComplexTypeMode() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("activateComplexTypeMode() getEditedObject()=" + getEditedObject() + " editionMode=" + editionMode
					+ " popupIsShown()=" + popupIsShown() + " _selectorPanel=" + _selectorPanel);
		}
		if (_selectorPanel != null && editionMode != EditionMode.COMPLEX_TYPE) {
			editionMode = EditionMode.COMPLEX_TYPE;
			boolean showAgain = false;
			if (_editedObject == null && _selectorPanel instanceof BasicDMTypeSelectorPanel) {
				FlexoObject currentSelectedObject = ((BasicDMTypeSelectorPanel) _selectorPanel)._entitySelectorPanel.getSelectedObject();
				// System.out.println("selected: "+currentSelectedObject);
				if (currentSelectedObject instanceof DMEntity) {
					_editedObject = DMType.makeResolvedDMType((DMEntity) currentSelectedObject);
				}
			}
			if (popupIsShown()) {
				showAgain = true;
				closePopup(false);
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
		editionMode = EditionMode.COMPLEX_TYPE;
	}

	DMModel getDataModel() {
		if (getProject() != null) {
			return getProject().getDataModel();
		}
		return null;
	}

	private Hashtable<String, DMEntity> unambigousEntities = null;

	private Hashtable<String, DMEntity> getAllUnambigousEntities() {
		if (unambigousEntities == null) {
			unambigousEntities = new Hashtable<String, DMEntity>();
			Enumeration<DMEntity> en = getDataModel().getEntities().elements();
			while (en.hasMoreElements()) {
				DMEntity e = en.nextElement();
				String fullQualifiedName = e.getFullQualifiedName();
				if (unambigousEntities.get(fullQualifiedName) == null) {
					unambigousEntities.put(fullQualifiedName, e);
				}
				String fullQualifiedNameUC = e.getFullQualifiedName().toUpperCase();
				if (unambigousEntities.get(fullQualifiedNameUC) == null) {
					unambigousEntities.put(fullQualifiedNameUC, e);
				}
				String name = e.getClassName();
				if (unambigousEntities.get(name) == null) {
					unambigousEntities.put(name, e);
				}
				String nameUC = e.getClassName().toUpperCase();
				if (unambigousEntities.get(nameUC) == null) {
					unambigousEntities.put(nameUC, e);
				}
			}
		}
		return unambigousEntities;
	}

	@Override
	public DMTypeSelector getJComponent() {
		return this;
	}

	@Override
	public Class<DMType> getRepresentedType() {
		return DMType.class;
	}

}
