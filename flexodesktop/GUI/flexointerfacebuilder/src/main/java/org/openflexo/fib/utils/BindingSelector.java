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
package org.openflexo.fib.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingModelChanged;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.Function;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.JavaBindingFactory;
import org.openflexo.antar.binding.JavaMethodPathElement;
import org.openflexo.antar.binding.MethodDefinition;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.BindingValue;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.swing.SwingUtils;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

/**
 * Widget allowing to edit a binding
 * 
 * @author sguerin
 * 
 */
public class BindingSelector extends TextFieldCustomPopup<DataBinding> implements FIBCustomComponent<DataBinding, BindingSelector>,
		Observer, PropertyChangeListener {
	static final Logger logger = Logger.getLogger(BindingSelector.class.getPackage().getName());

	private DataBinding _revertBindingValue;

	protected boolean _allowsBindingExpressions = true;
	protected boolean _allowsCompoundBindings = true;
	protected boolean _allowsStaticValues = true;
	protected boolean _hideFilteredObjects = false;

	protected AbstractBindingSelectorPanel _selectorPanel;
	boolean isUpdatingModel = false;

	private boolean textIsEditing = false;

	private boolean isConnected = false;

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
		NORMAL_BINDING, COMPOUND_BINDING, STATIC_BINDING, BINDING_EXPRESSION;/*,
																				NEW_ENTRY;*/

		boolean useCommonPanel() {
			return this != BINDING_EXPRESSION;
		}
	}

	EditionMode editionMode = EditionMode.NORMAL_BINDING;

	public KeyAdapter shortcutsKeyAdapter;
	private DocumentListener documentListener;

	private Color defaultForeground;

	private Color defaultSelectedColor;

	public BindingSelector(DataBinding<?> editedObject) {
		this(editedObject, -1);
	}

	public BindingSelector(DataBinding<?> editedObject, int cols) {
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
				if (opposite == null || !SwingUtilities.isDescendingFrom(opposite, BindingSelector.this)
						&& !SwingUtilities.isDescendingFrom(opposite, _selectorPanel)) {
					// Little hook used to automatically apply a valid value which has generally been edited
					// By typing text in text field
					if (getEditedObject() != null && getEditedObject().isValid()) {
						apply();
					}
				}
			}
		});
		shortcutsKeyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				// if command-key is pressed, do not open popup
				if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isMetaDown()) {
					return;
				}

				boolean isSignificativeKey = e.getKeyCode() >= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z
						|| e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9;

				if (!popupIsShown() && getTextField().getText() != null
						&& !isAcceptableAsBeginningOfStaticBindingValue(getTextField().getText()) && isSignificativeKey) {
					boolean requestFocus = getTextField().hasFocus();
					openPopup();
					if (requestFocus) {
						logger.info("Request focus in " + getTextField());
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								getTextField().requestFocusInWindow();
							}
						});
					}
				}

				// This code was added to allow direct typing without opening selector panel (sic !)
				// TODO:provide better implementation !!!
				if (_selectorPanel == null) {
					createCustomPanel(getEditedObject());
				}

				if (_selectorPanel != null) {

					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (StringUtils.isNotEmpty(getTextField().getText()) && textFieldNotSynchWithEditedObject()) {
							if (_selectorPanel instanceof BindingSelectorPanel) {
								BindingSelectorPanel selectorPanel = (BindingSelectorPanel) _selectorPanel;
								if (selectorPanel.isKeyPathFromTextASubKeyPath(getTextField().getText())
										&& selectorPanel.isKeyPathFromPanelValid()) {
									getEditedObject().setExpression(selectorPanel.makeBindingValueFromPanel());
									fireEditedObjectChanged();
									apply();
								} else {
									String input = getTextField().getText();
									if (input.indexOf(".") > -1) {
										String pathIgnoringLastPart = input.substring(0, input.lastIndexOf("."));
										if (isKeyPathValid(pathIgnoringLastPart)) {
											String inexitingPart = input.substring(input.lastIndexOf(".") + 1);
											Type hostType = selectorPanel.getEndingTypeForSubPath(pathIgnoringLastPart);
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
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						_selectorPanel.processLeftPressed();
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						_selectorPanel.processRightPressed();
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
				textIsEditing = true;
				try {
					synchronizeWithTextFieldValue(textValue);
				} finally {
					textIsEditing = false;
				}
			}
		};

		getTextField().addKeyListener(shortcutsKeyAdapter);
		getTextField().getDocument().addDocumentListener(documentListener);
		updateUI(); // Just to initiate the default color values
		// setEditedObjectAndUpdateBDAndOwner(editedObject);
		setEditedObject(editedObject);

		/*(new Thread() {
			@Override
			public void run() {
				while(true) {
					Component c = (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
					if (c != null)
						System.out.println("focus owner = "+c.hashCode()+" "+c.getClass().getSimpleName()+" is "+c);
					try {
						sleep(3000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}).start();*/

	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (getTextField() != null) {
			defaultForeground = getTextField().getForeground();
			defaultSelectedColor = getTextField().getSelectedTextColor();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public Class<DataBinding> getRepresentedType() {
		return DataBinding.class;
	}

	protected void synchronizeWithTextFieldValue(String textValue) {
		try {
			isUpdatingModel = true;

			DataBinding newEditedBinding = makeBindingFromString(textValue);

			if (newEditedBinding != null) {
				logger.info("Decoding binding as " + newEditedBinding + " valid=" + newEditedBinding.isValid());
				// if (newEditedBinding instanceof BindingValue) logger.info("BV="+((BindingValue)newEditedBinding).getBindingVariable());
				if (newEditedBinding.isValid()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Decoded as VALID binding: " + newEditedBinding);
					}
					getTextField().setForeground(defaultForeground);
					getTextField().setSelectedTextColor(defaultSelectedColor);
					if (!newEditedBinding.equals(getEditedObject())) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("This is a new one, i take this");
						}
						setEditedObject(newEditedBinding);
						fireEditedObjectChanged();
						return;
					} else {
						// Anyway, in case of it is the same object, but with changes, we always fire fireEditedObjectChanged()
						checkIfDisplayModeShouldChange(newEditedBinding, true);
						fireEditedObjectChanged();
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Skipping as it represents the same binding");
						}
						return;
					}
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Decoded as INVALID binding: " + newEditedBinding + " trying to synchronize panel");
					}
					getTextField().setForeground(Color.RED);
					getTextField().setSelectedTextColor(Color.RED);
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
				logger.info("Couldn't decode as binding, trying to synchronize panel anyway");
				getTextField().setForeground(Color.RED);
				getTextField().setSelectedTextColor(Color.RED);
				if (_selectorPanel != null) {
					_selectorPanel.synchronizePanelWithTextFieldValue(textValue);
				}
				return;
			}

		} finally {
			isUpdatingModel = false;
		}

	}

	/*public void setEditedObjectAndUpdateBDAndOwner(AbstractBinding object)
	{
		setEditedObject(object);
		if (object != null) {
			if (object.getBindingDefinition() != null) setBindingDefinition(object.getBindingDefinition());
			if (object.getOwner() != null) setBindable((Bindable)object.getOwner());
		}
	}*/

	@Override
	public void setEditedObject(DataBinding dataBinding) {

		// logger.info("setEditedObject in BindingSelector with " + dataBinding);

		if (dataBinding == null) {
			// Mais des fois, c'est autorise quand meme, il faut donc tracer tous les appels pour voir ceux qui sont legaux (sylvain)
			// logger.warning("forbidden setEditedObject(null) !!!");
			// Thread.dumpStack();
			return;
		}

		setEditedObject(dataBinding, true);
		if (dataBinding != null && dataBinding.isValid()) {
			isConnected = true;
		}

		if (dataBinding != null) {
			/*if (dataBinding.getBindingDefinition() != null) {
				setBindingDefinition(dataBinding.getBindingDefinition());
			}*/
			if (dataBinding.getOwner() != null) {
				setBindable(dataBinding.getOwner());
			}
		}
		// SGU: I suppress this code that was the cause for huge problems
		// in BindingSelector, making it quite unusable
		// I don't think this code was usefull, was it ?
		/*else {
			setBindingDefinition(null);
			setBindable(null);
			}*/
	}

	public void setEditedObject(DataBinding dataBinding, boolean updateBindingSelectionMode) {
		// logger.info(">>>>>>>>>>>>>> setEditedObject() with "+object);
		if (updateBindingSelectionMode) {
			if (dataBinding != null) {
				dataBinding = checkIfDisplayModeShouldChange(dataBinding, false);
			} else {
				activateNormalBindingMode();
			}
		}
		super.setEditedObject(dataBinding);

		if (getEditedObject() != null && getEditedObject().isValid()) {
			getTextField().setForeground(defaultForeground);
			getTextField().setSelectedTextColor(defaultSelectedColor);
		} else {
			getTextField().setForeground(Color.RED);
			getTextField().setSelectedTextColor(Color.RED);
		}
	}

	DataBinding<?> checkIfDisplayModeShouldChange(DataBinding<?> dataBinding, boolean setValueAsNewEditedValue) {
		DataBinding<?> returned = dataBinding;

		EditionMode oldEditionMode = editionMode;
		EditionMode newEditionMode = editionMode;

		if (dataBinding != null && dataBinding.isSet()) {
			if (dataBinding.isConstant()) {
				newEditionMode = EditionMode.STATIC_BINDING;
			} else if (dataBinding.isBindingValue()) {
				if (((BindingValue) dataBinding.getExpression()).isCompoundBinding()
						|| dataBinding.getBindingDefinitionType() == DataBinding.BindingDefinitionType.EXECUTE) {
					newEditionMode = EditionMode.COMPOUND_BINDING;
				} else if (oldEditionMode != EditionMode.NORMAL_BINDING && oldEditionMode != EditionMode.COMPOUND_BINDING) {
					newEditionMode = EditionMode.NORMAL_BINDING;
				}
			} else {
				newEditionMode = EditionMode.BINDING_EXPRESSION;
			}
			if (returned == null) {
				newEditionMode = EditionMode.NORMAL_BINDING;
			}
		} else {
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
			} else if (dataBinding.isExpression()) {
				activateBindingExpressionMode();
			}
		}
		if (oldEditionMode != EditionMode.COMPOUND_BINDING && newEditionMode == EditionMode.COMPOUND_BINDING) {
			activateCompoundBindingMode();
		}

		editionMode = newEditionMode;

		// Should i change edited object ???
		if (returned != dataBinding && setValueAsNewEditedValue) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Switching edited object from " + dataBinding + " to " + returned);
			}
			_editedObject = returned;
			updateCustomPanel(getEditedObject());
		}

		return returned;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void connect() {
		if (getEditedObject().isValid()) {
			// logger.info("Is connected = TRUE");
			isConnected = true;
		}
	}

	public void disconnect() {
		// logger.info("Is connected = FALSE");
		isConnected = false;
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
		if (getEditedObject() == null || !getEditedObject().isValid()) {
			disconnect();
		}
		updateCustomPanel(getEditedObject());
		if (!getIsUpdatingModel()) {
			_isProgrammaticalySet = true;
			if (!textIsEditing) {
				getTextField().setText(renderedString(getEditedObject()));
			}
			if (getEditedObject() != null) {
				getTextField().setForeground(getEditedObject().isValid() ? defaultForeground : Color.RED);
				getTextField().setSelectedTextColor(getEditedObject().isValid() ? defaultSelectedColor : Color.RED);
			} else {
				getTextField().setForeground(Color.RED);
				getTextField().setSelectedTextColor(Color.RED);
			}
			_isProgrammaticalySet = false;
		}
	}

	public boolean areCompoundBindingAllowed() {
		// if (getBindingDefinition() != null && getBindingDefinition().getIsSettable()) return false;
		return _allowsCompoundBindings;
	}

	public void allowsCompoundBindings() {
		_allowsCompoundBindings = true;
		rebuildPopup();
	}

	public void denyCompoundBindings() {
		_allowsCompoundBindings = false;
		rebuildPopup();
	}

	public boolean areBindingExpressionsAllowed() {
		if (getEditedObject() != null
				&& (getEditedObject().isSettable() || getEditedObject().getBindingDefinitionType() == DataBinding.BindingDefinitionType.EXECUTE)) {
			return false;
		}
		return _allowsBindingExpressions;
	}

	public void allowsBindingExpressions() {
		_allowsBindingExpressions = true;
		rebuildPopup();
	}

	public void denyBindingExpressions() {
		_allowsBindingExpressions = false;
		rebuildPopup();
	}

	public boolean areStaticValuesAllowed() {
		if (getEditedObject() != null
				&& (getEditedObject().isSettable() || getEditedObject().getBindingDefinitionType() == DataBinding.BindingDefinitionType.EXECUTE)) {
			return false;
		}
		return _allowsStaticValues;
	}

	public void allowsStaticValues() {
		_allowsStaticValues = true;
		rebuildPopup();
	}

	public void denyStaticValues() {
		_allowsStaticValues = false;
		rebuildPopup();
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
			if (getEditedObject() != null && !(getEditedObject().isBindingValue())) {
				_editedObject.setExpression(makeBinding()); // I dont want to notify it !!!
				fireEditedObjectChanged();
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
		editionMode = EditionMode.COMPOUND_BINDING;
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
			// sylvain: i don't understand this code, i suppressed it
			/*if (getEditedObject() != null && !(getEditedObject().isBindingValue())) {
				getEditedObject().setExpression(makeBinding()); // I dont want to notify it !!!
				fireEditedObjectChanged();
			}*/
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
		editionMode = EditionMode.NORMAL_BINDING;
	}

	public void activateBindingExpressionMode(/*Expression bindingExpression*/) {
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
			/*if (bindingExpression != null) {
				_editedObject = bindingExpression;
			} else {
				_editedObject = new BindingExpression(getBindingDefinition(), getBindable()); // I dont want to notify it !!!
			}*/
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
	}

	@Override
	public void delete() {
		super.delete();
		unregisterListenerForBindable();
		// unregisterListenerForBindingDefinition();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		_bindable = null;
	}

	@Override
	protected void deleteCustomPanel() {
		super.deleteCustomPanel();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
	}

	@Override
	public void setRevertValue(DataBinding oldValue) {
		if (oldValue != null) {
			_revertBindingValue = oldValue.clone();
		} else {
			_revertBindingValue = null;
		}
	}

	@Override
	public DataBinding getRevertValue() {
		return _revertBindingValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(DataBinding editedObject) {
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
			if (editedObject == null || editedObject.isConstant()) {
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
			_selectorPanel.update();
		}
	}

	/*public void refreshBindingDefinitionType() {
		if (_selectorPanel != null) {
			_selectorPanel.fireBindingDefinitionChanged();
		}
	}*/

	@Override
	public void updateCustomPanel(DataBinding editedObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateCustomPanel() with " + editedObject);
		}
		if (editedObject != null && editedObject.isBindingValue() && ((BindingValue) editedObject.getExpression()).isCompoundBinding()) {
			activateCompoundBindingMode();
		}
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		if (editedObject != null) {
			if (editedObject.isSet()) {
				if (editedObject.isValid()) {
					getLabel().setVisible(true);
					getLabel().setIcon(UtilsIconLibrary.OK_ICON);
				} else {
					logger.info("Binding not valid: " + editedObject + " reason=" + editedObject.invalidBindingReason());
					/*if (editedObject.isBindingValue()) {
						BindingValue bv = (BindingValue) (editedObject.getExpression());
						System.out.println("BV=" + bv);
						System.out.println("valid=" + bv.isValid());
						System.out.println("reason=" + bv.invalidBindingReason());
					}*/
					getLabel().setVisible(true);
					getLabel().setIcon(UtilsIconLibrary.ERROR_ICON);
				}
			} else {
				if (editedObject.isMandatory()) {
					getLabel().setVisible(true);
					getLabel().setIcon(UtilsIconLibrary.WARNING_ICON);
				} else {
					getLabel().setVisible(false);
				}
			}
		} else {
			getLabel().setVisible(true);
			getLabel().setIcon(UtilsIconLibrary.ERROR_ICON);
		}
	}

	public void resetMethodCallPanel() {
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			((BindingSelectorPanel) _selectorPanel).resetMethodCallPanel();
		}
	}

	@Override
	public String renderedString(DataBinding editedObject) {
		if (editedObject != null) {
			// System.out.println("Try to render " + editedObject);
			return editedObject.toString();
		}
		return "";
	}

	public Bindable getBindable() {
		return _bindable;
	}

	@CustomComponentParameter(name = "bindable", type = CustomComponentParameter.Type.MANDATORY)
	public void setBindable(Bindable bindable) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setBindable with " + bindable);
		}
		unregisterListenerForBindable();
		_bindable = bindable;
		if (bindable != null && _selectorPanel != null) {
			_selectorPanel.fireBindableChanged();
		}
		registerListenerForBindable();
		// getCustomPanel().setBindingModel(bindable.getBindingModel());
		updateTextFieldProgrammaticaly();
	}

	public void registerListenerForBindable() {
		if (_bindable instanceof Observable) {
			((Observable) _bindable).addObserver(this);
		}
		if (_bindable instanceof HasPropertyChangeSupport) {
			// System.out.println("registering " + bindable + " for " + this);
			if (((HasPropertyChangeSupport) _bindable).getPropertyChangeSupport() != null) {
				((HasPropertyChangeSupport) _bindable).getPropertyChangeSupport().addPropertyChangeListener(
						BindingModelChanged.BINDING_MODEL_CHANGED, this);
			}
		}
	}

	public void unregisterListenerForBindable() {
		if (_bindable instanceof Observable) {
			((Observable) _bindable).deleteObserver(this);
		}
		if (_bindable instanceof HasPropertyChangeSupport) {
			if (((HasPropertyChangeSupport) _bindable).getPropertyChangeSupport() != null) {
				((HasPropertyChangeSupport) _bindable).getPropertyChangeSupport().removePropertyChangeListener(this);
			}
		}
	}

	@Override
	public void updateTextFieldProgrammaticaly() {
		// Don't update textfield if original event was triggered by a textfield edition
		if (!textIsEditing) {
			super.updateTextFieldProgrammaticaly();
		}
	}

	@Override
	public void update(Observable observable, Object notification) {
		if (observable == _bindable) {
			if (notification instanceof BindingModelChanged) {
				logger.fine("Refreshing Binding Model");
				refreshBindingModel();
			}
		}
		/*if (observable == _bindingDefinition) {
			if (notification instanceof BindingDefinitionTypeChanged) {
				logger.fine("Updating BindingDefinition type");
				refreshBindingDefinitionType();
			}
		}*/
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BindingModelChanged.BINDING_MODEL_CHANGED)) {
			logger.fine("Refreshing Binding Model");
			refreshBindingModel();
		} /*else if (evt.getPropertyName().equals(BindingDefinitionTypeChanged.BINDING_DEFINITION_TYPE_CHANGED)) {
			logger.fine("Updating BindingDefinition type");
			refreshBindingDefinitionType();
			}*/

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

	// BindingDefinition _bindingDefinition;

	// BindingModel _bindingModel;

	/*public BindingDefinition getBindingDefinition() {
		return _bindingDefinition;
	}

	@CustomComponentParameter(name = "bindingDefinition", type = CustomComponentParameter.Type.MANDATORY)
	public void setBindingDefinition(BindingDefinition bindingDefinition) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(toString() + "Setting new binding definition: " + bindingDefinition + " old: " + _bindingDefinition);
		}
		if (bindingDefinition != _bindingDefinition) {
			unregisterListenerForBindingDefinition();
			_bindingDefinition = bindingDefinition;
			DataBinding bindingValue = getEditedObject();
			if (bindingValue != null) {
				bindingValue.setBindingDefinition(bindingDefinition);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("set BD " + bindingDefinition + " for BV " + bindingValue);
				}
			}
			if (_selectorPanel != null) {
				_selectorPanel.fireBindingDefinitionChanged();
			}
			registerListenerForBindingDefinition();
			updateCustomPanel(getEditedObject());
		}
	}*/

	/*public void registerListenerForBindingDefinition() {
		if (_bindingDefinition instanceof Observable) {
			((Observable) _bindingDefinition).addObserver(this);
		}
		if (_bindingDefinition instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) _bindingDefinition).getPropertyChangeSupport().addPropertyChangeListener(
					BindingDefinitionTypeChanged.BINDING_DEFINITION_TYPE_CHANGED, this);
			((HasPropertyChangeSupport) _bindingDefinition).getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}*/

	/*public void unregisterListenerForBindingDefinition() {
		if (_bindingDefinition instanceof Observable) {
			((Observable) _bindingDefinition).deleteObserver(this);
		}
		if (_bindingDefinition instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) _bindingDefinition).getPropertyChangeSupport().removePropertyChangeListener(
					BindingDefinitionTypeChanged.BINDING_DEFINITION_TYPE_CHANGED, this);
			((HasPropertyChangeSupport) _bindingDefinition).getPropertyChangeSupport().removePropertyChangeListener(this);
		}
	}*/

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

	/*protected Expression makeBindingExpression() {
		(new Exception("Qui m'appelle la ?")).printStackTrace();
		return new Variable("");
	}*/

	protected Expression makeBinding() {

		BindingValue newBindingValue = new BindingValue();
		newBindingValue.setDataBinding(getEditedObject());
		return newBindingValue;

		/*Expression returned = null;
		if (editionMode == EditionMode.BINDING_EXPRESSION) {
			if (getBindingDefinition() != null && getBindable() != null) {
				returned = makeBindingExpression();
			}
		} else if (editionMode == EditionMode.STATIC_BINDING) {
			if (getBindingDefinition() != null && getBindable() != null) {
				if (getBindingDefinition().getType() != null) {
					if (TypeUtils.isBoolean(getBindingDefinition().getType())) {
						return BooleanConstant.FALSE;
					} else if (TypeUtils.isInteger(getBindingDefinition().getType()) || TypeUtils.isLong(getBindingDefinition().getType())
							|| TypeUtils.isShort(getBindingDefinition().getType()) || TypeUtils.isChar(getBindingDefinition().getType())
							|| TypeUtils.isByte(getBindingDefinition().getType())) {
						returned = new Constant.IntegerConstant(0);
					} else if (TypeUtils.isFloat(getBindingDefinition().getType()) || TypeUtils.isDouble(getBindingDefinition().getType())) {
						returned = new Constant.FloatConstant(0);
					}
				} else if (TypeUtils.isString(getBindingDefinition().getType())) {
					returned = new Constant.StringConstant("");
				}
			}
		} else if (editionMode == EditionMode.NORMAL_BINDING || editionMode == EditionMode.COMPOUND_BINDING) { // Normal or compound binding
			if (getBindingDefinition() != null && getBindable() != null) {
				returned = new BindingValue();
			}
		}
		return returned;*/
	}

	void recreateBindingValue() {
		getEditedObject().setExpression(makeBinding());
		fireEditedObjectChanged();
		logger.info("Recreating Binding with mode " + editionMode + " as " + getEditedObject());
	}

	Bindable _bindable;

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
		DataBinding dataBinding = getEditedObject();
		if (dataBinding != null) {
			if (dataBinding.isValid()) {
				/*if (bindingValue instanceof BindingValue) {
					((BindingValue) bindingValue).connect();
				}*/
				connect();
				getTextField().setForeground(defaultForeground);
				getTextField().setSelectedTextColor(defaultSelectedColor);
			} else {
				getTextField().setForeground(Color.RED);
				getTextField().setSelectedTextColor(Color.RED);
			}
			_revertBindingValue = dataBinding.clone();
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

	protected final AbstractListModel getListModelFor(BindingPathElement element, Type resultingType) {
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			return ((BindingSelectorPanel) _selectorPanel).getColumnListModel(element, resultingType);
		}
		return null;
	}

	protected void valueSelected(int index, JList list, DataBinding binding) {

		boolean bindingRecreated = false;

		if (!(binding.isBindingValue())) {

			editionMode = EditionMode.NORMAL_BINDING;
			binding.setExpression(makeBinding()); // Should create a BindingValue instance !!!
			bindingRecreated = true;
			if (!(binding.isBindingValue())) {
				logger.severe("Should never happen: valueSelected() called for a non-BindingValue instance !");
				return;
			}
		}
		BindingValue bindingValue = (BindingValue) binding.getExpression();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Value selected: index=" + index + " list=" + list + " bindingValue=" + bindingValue);
		}
		BindingSelectorPanel.BindingColumnElement selectedValue = (BindingSelectorPanel.BindingColumnElement) list.getSelectedValue();
		if (selectedValue == null) {
			return;
		}
		if (index == 0 && selectedValue.getElement() instanceof BindingVariable) { // ICI
			if (list.getSelectedValue() != bindingValue.getBindingVariable()) {
				disconnect();
				bindingValue.setBindingVariable((BindingVariable) selectedValue.getElement());
				getEditedObject().setExpression(bindingValue);
				fireEditedObjectChanged();
			}
		} else {
			if (selectedValue.getElement() instanceof SimplePathElement) {
				// FIXED invalid type object comparison
				if (selectedValue.getElement() != bindingValue.getBindingPathElementAtIndex(index - 1)) {
					// System.out.println("bindingValue was " + bindingValue);
					// System.out.println("select " + selectedValue.getElement());
					disconnect();
					bindingValue.setBindingPathElementAtIndex(selectedValue.getElement(), index - 1);
					// System.out.println("bindingValue is now " + bindingValue);
					getEditedObject().setExpression(bindingValue);
					fireEditedObjectChanged();
				}
			} else if (selectedValue.getElement() instanceof FunctionPathElement && editionMode == EditionMode.COMPOUND_BINDING) {
				BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(index - 1);
				/*System.out.println("selectedValue.getElement()=" + selectedValue.getElement() + " of "
						+ selectedValue.getElement().getClass());
				System.out.println("currentElement=" + currentElement + " of " + currentElement != null ? currentElement.getClass() : null);*/

				if (!(currentElement instanceof FunctionPathElement)
						|| (!((FunctionPathElement) currentElement).getFunction().equals(
								((FunctionPathElement) selectedValue.getElement()).getFunction()))) {
					disconnect();
					Function function = ((FunctionPathElement) selectedValue.getElement()).getFunction();
					// TODO: we need to handle here generic FunctionPathElement and not only JavaMethodPathElement
					JavaMethodPathElement newMethodCall = new JavaMethodPathElement(bindingValue.getLastBindingPathElement(),
							(MethodDefinition) ((FunctionPathElement) selectedValue.getElement()).getFunction(),
							new ArrayList<DataBinding<?>>());
					bindingValue.setBindingPathElementAtIndex(newMethodCall, index - 1);
					getEditedObject().setExpression(bindingValue);
					fireEditedObjectChanged();
				}
			}
		}
	}

	boolean isAcceptableStaticBindingValue(String stringValue) {
		if (getEditedObject() == null) {
			return false;
		}
		if (getEditedObject().getDeclaredType() == null) {
			return false;
		}
		Constant b = makeStaticBindingFromString(stringValue);
		if (b == null) {
			return false;
		}
		if (TypeUtils.isObject(getEditedObject().getDeclaredType()) && !stringValue.endsWith(".")) {
			return true;
		}
		if (TypeUtils.isBoolean(getEditedObject().getDeclaredType())) {
			return b instanceof BooleanConstant;
		} else if (TypeUtils.isInteger(getEditedObject().getDeclaredType()) || TypeUtils.isLong(getEditedObject().getDeclaredType())
				|| TypeUtils.isShort(getEditedObject().getDeclaredType()) || TypeUtils.isChar(getEditedObject().getDeclaredType())
				|| TypeUtils.isByte(getEditedObject().getDeclaredType())) {
			return b instanceof IntegerConstant;
		} else if (TypeUtils.isFloat(getEditedObject().getDeclaredType()) || TypeUtils.isDouble(getEditedObject().getDeclaredType())) {
			if (stringValue.endsWith(".")) {
				return false;
			}
			return b instanceof IntegerConstant || b instanceof FloatConstant;
		} else if (TypeUtils.isString(getEditedObject().getDeclaredType())) {
			return b instanceof StringConstant;
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
		if (getEditedObject() == null) {
			return false;
		}

		if (stringValue.length() == 0) {
			return true;
		}

		if (TypeUtils.isObject(getEditedObject().getDeclaredType())) {
			// In this case, any of matching is enough
			return isAcceptableStaticBindingValue(stringValue)
					&& !stringValue.endsWith(".") // Special case to handle float on-the-fly
													// typing
					|| isAcceptableAsBeginningOfBooleanStaticBindingValue(stringValue)
					|| isAcceptableAsBeginningOfStringStaticBindingValue(stringValue);
		}

		if (TypeUtils.isBoolean(getEditedObject().getDeclaredType())) {
			return isAcceptableAsBeginningOfBooleanStaticBindingValue(stringValue);
		} else if (TypeUtils.isInteger(getEditedObject().getDeclaredType()) || TypeUtils.isLong(getEditedObject().getDeclaredType())
				|| TypeUtils.isShort(getEditedObject().getDeclaredType()) || TypeUtils.isChar(getEditedObject().getDeclaredType())
				|| TypeUtils.isByte(getEditedObject().getDeclaredType())) {
			return isAcceptableStaticBindingValue(stringValue);
		} else if (TypeUtils.isFloat(getEditedObject().getDeclaredType()) || TypeUtils.isDouble(getEditedObject().getDeclaredType())) {
			if (stringValue.endsWith(".") && stringValue.length() > 1) {
				return isAcceptableStaticBindingValue(stringValue.substring(0, stringValue.length() - 1));
			}
			return isAcceptableStaticBindingValue(stringValue);
		} else if (TypeUtils.isString(getEditedObject().getDeclaredType())) {
			return isAcceptableAsBeginningOfStringStaticBindingValue(stringValue);
		}
		return false;
	}

	Constant makeStaticBindingFromString(String stringValue) {
		Expression e;
		try {
			e = ExpressionParser.parse(stringValue);
			if (e instanceof Constant) {
				return (Constant) e;
			}
		} catch (ParseException e1) {
			// e1.printStackTrace();
		}
		return null;
	}

	DataBinding makeBindingFromString(String stringValue) {
		if (getEditedObject() != null) {
			getEditedObject().setUnparsedBinding(stringValue);
			getEditedObject().decode();
			return getEditedObject();
		}
		/*
				if (getBindable() != null) {
					DataBinding<?> returned = new DataBinding<Object>(stringValue, getBindable(), getBindingDefinition().getType(),
							getBindingDefinition().getBindingDefinitionType());
					returned.decode();
					return returned;
				}*/
		logger.warning("Cannot build binding: null DataBinding !");
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

	public static class TestBindable implements Bindable {
		private BindingFactory bindingFactory = new JavaBindingFactory();
		private BindingModel bindingModel = new BindingModel();

		public TestBindable() {
			bindingModel.addToBindingVariables(new BindingVariable("aString", String.class));
			bindingModel.addToBindingVariables(new BindingVariable("anInteger", Integer.class));
			bindingModel.addToBindingVariables(new BindingVariable("aFloat", Float.TYPE));
		}

		@Override
		public BindingModel getBindingModel() {
			return bindingModel;
		}

		@Override
		public BindingFactory getBindingFactory() {
			return bindingFactory;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		}
	}

	public static void main(String[] args) {
		final JDialog dialog = new JDialog((Frame) null, false);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), dialog);
			}
		});

		Bindable testBindable = new TestBindable();

		BindingFactory factory = new JavaBindingFactory();
		DataBinding binding = new DataBinding<String>("aString.toString", testBindable, String.class, DataBinding.BindingDefinitionType.GET);

		BindingSelector _selector = new BindingSelector(null) {
			@Override
			public void apply() {
				super.apply();
				// System.out.println("Apply, getEditedObject()=" + getEditedObject());
			}

			@Override
			public void cancel() {
				super.cancel();
				// System.out.println("Cancel, getEditedObject()=" + getEditedObject());
			}
		};
		_selector.setBindable(testBindable);
		_selector.setEditedObject(binding);
		_selector.setRevertValue(binding.clone());

		JPanel panel = new JPanel(new VerticalLayout());
		panel.add(_selector);

		panel.add(closeButton);
		panel.add(logButton);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.pack();

		dialog.setVisible(true);
	}
}
