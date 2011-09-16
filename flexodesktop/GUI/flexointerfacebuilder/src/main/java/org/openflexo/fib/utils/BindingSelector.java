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
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingExpression;
import org.openflexo.antar.binding.BindingExpression.BindingValueConstant;
import org.openflexo.antar.binding.BindingExpression.BindingValueFunction;
import org.openflexo.antar.binding.BindingExpression.BindingValueVariable;
import org.openflexo.antar.binding.BindingExpressionFactory;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingValue;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.BooleanStaticBinding;
import org.openflexo.antar.binding.ComplexPathElement;
import org.openflexo.antar.binding.FloatStaticBinding;
import org.openflexo.antar.binding.IntegerStaticBinding;
import org.openflexo.antar.binding.MethodCall;
import org.openflexo.antar.binding.MethodDefinition;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.StaticBinding;
import org.openflexo.antar.binding.StaticBindingFactory;
import org.openflexo.antar.binding.StringStaticBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.parser.Word;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.swing.SwingUtils;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.toolbox.StringUtils;


/**
 * Widget allowing to edit a binding
 *
 * @author sguerin
 *
 */
public class BindingSelector
extends TextFieldCustomPopup<AbstractBinding>
implements FIBCustomComponent<AbstractBinding,BindingSelector>
{
	static final Logger logger = Logger.getLogger(BindingSelector.class.getPackage().getName());

	private AbstractBinding _revertBindingValue;

	protected boolean _allowsBindingExpressions = true;
	protected boolean _allowsCompoundBindings = true;
	protected boolean _allowsStaticValues = true;
	protected boolean _hideFilteredObjects = false;

	protected AbstractBindingSelectorPanel _selectorPanel;
	boolean isUpdatingModel = false;

	private boolean textIsEditing = false;

	protected KeyEventDispatcher tabDispatcher = new KeyEventDispatcher()
	{
		@Override
		public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (e.getID() == KeyEvent.KEY_TYPED && (e.getKeyChar() == KeyEvent.VK_RIGHT || e.getKeyChar() == KeyEvent.VK_TAB)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Calling tab pressed "+e);
				}
				getCustomPanel().processTabPressed();
				e.consume();
			}
			return false;
		}
	};

	static enum EditionMode
	{
		NORMAL_BINDING,
		COMPOUND_BINDING,
		STATIC_BINDING,
		BINDING_EXPRESSION;/*,
		NEW_ENTRY;*/

		boolean useCommonPanel()
		{
			return (this != BINDING_EXPRESSION);
		}
	}

	EditionMode editionMode = EditionMode.NORMAL_BINDING;

	public KeyAdapter shortcutsKeyAdapter;
	private DocumentListener documentListener;

	public BindingSelector(AbstractBinding editedObject)
	{
		this(editedObject, -1);
	}

	public BindingSelector(AbstractBinding editedObject, int cols)
	{
		super(null, cols);
		setFocusable(true);
		getTextField().setFocusable(true);
		getTextField().setEditable(true);
		getTextField().addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent focusEvent) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(tabDispatcher);
			}
			@Override
			public void focusLost(FocusEvent focusEvent) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(tabDispatcher);
				Component opposite = focusEvent.getOppositeComponent();
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("focus lost for "+(opposite!=null?SwingUtils.getComponentPath(opposite):"null"));
				}
				if ((opposite != null)
						&& !(opposite instanceof JRootPane)
						&& !(SwingUtils.isComponentContainedInContainer(opposite, getParent()))
						&& !(SwingUtils.isComponentContainedInContainer(opposite, _selectorPanel))) {
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
			public void keyPressed(KeyEvent e)
			{
				boolean isSignificativeKey =
						(e.getKeyCode() >= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z)
						|| (e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9);


				if (!popupIsShown()
						&& getTextField().getText()!=null
						&& !isAcceptableAsBeginningOfStaticBindingValue(getTextField().getText())
						&& isSignificativeKey) {
					boolean requestFocus = (getTextField().hasFocus());
					openPopup();
					if (requestFocus) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								getTextField().requestFocus();
							}
						});
					}
				}

				if (_selectorPanel != null) {

					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if(StringUtils.isNotEmpty(getTextField().getText()) && textFieldNotSynchWithEditedObject()) {
							if (_selectorPanel instanceof BindingSelectorPanel) {
								BindingSelectorPanel selectorPanel = (BindingSelectorPanel)_selectorPanel;
								if (selectorPanel.isKeyPathFromTextASubKeyPath(getTextField().getText()) && selectorPanel.isKeyPathFromPanelValid()){
									setEditedObject(selectorPanel.makeBindingValueFromPanel());
									apply();
								} else {
									String input = getTextField().getText();
									if(input.indexOf(".")>-1){
										String pathIgnoringLastPart = input.substring(0,input.lastIndexOf("."));
										if(isKeyPathValid(pathIgnoringLastPart)){
											String inexitingPart = input.substring(input.lastIndexOf(".")+1);
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
					}
					else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						_selectorPanel.processDownPressed();
						e.consume();
					}
					else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						_selectorPanel.processLeftPressed();
						e.consume();
					}
					else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						_selectorPanel.processRightPressed();
						e.consume();
					}
				}
			}
		};

		documentListener = new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e) { textEdited(e); }
			@Override
			public void insertUpdate(DocumentEvent e) { textEdited(e); }
			@Override
			public void removeUpdate(DocumentEvent e) { textEdited(e); }
			public void textEdited(DocumentEvent e)  {
				if (isProgrammaticalySet()) {
					return;
				}
				String textValue = getTextField().getText();
				textIsEditing = true;
				try {
					synchronizeWithTextFieldValue(textValue);
				}
				finally {
					textIsEditing = false;
				}
			}
		};

		getTextField().addKeyListener(shortcutsKeyAdapter);
		getTextField().getDocument().addDocumentListener(documentListener);

		//setEditedObjectAndUpdateBDAndOwner(editedObject);
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
	public void init(FIBCustom component, FIBController controller)
	{
	}

	@Override
	public Class<AbstractBinding> getRepresentedType()
	{
		return AbstractBinding.class;
	}

	protected void synchronizeWithTextFieldValue(String textValue)
	{
		try {
			isUpdatingModel = true;

			AbstractBinding newEditedBinding = makeBindingFromString(textValue);

			if (newEditedBinding != null) {
				logger.fine("Decoding binding as "+newEditedBinding);
				//if (newEditedBinding instanceof BindingValue) logger.info("BV="+((BindingValue)newEditedBinding).getBindingVariable());
				if (newEditedBinding.isBindingValid()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Decoded as VALID binding: "+newEditedBinding);
					}
					if (!newEditedBinding.equals(getEditedObject())) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("This is a new one, i take this");
						}
						setEditedObject(newEditedBinding);
						return;
					}
					else {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Skipping as it represents the same binding");
						}
						getTextField().setForeground(Color.BLACK);
						return;
					}
				}
				else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Decoded as INVALID binding: "+newEditedBinding+" trying to synchronize panel");
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


	/*public void setEditedObjectAndUpdateBDAndOwner(AbstractBinding object)
	{
		setEditedObject(object);
		if (object != null) {
			if (object.getBindingDefinition() != null) setBindingDefinition(object.getBindingDefinition());
			if (object.getOwner() != null) setBindable((Bindable)object.getOwner());
		}
	}*/

	@Override
	public void setEditedObject(AbstractBinding object)
	{
		setEditedObject(object, true);
		if (object != null) {
			if (object.getBindingDefinition() != null) {
				setBindingDefinition(object.getBindingDefinition());
			}
			if (object.getOwner() != null) {
				setBindable(object.getOwner());
			}
		}
	}


	public void setEditedObject(AbstractBinding object, boolean updateBindingSelectionMode)
	{
		//logger.info(">>>>>>>>>>>>>> setEditedObject() with "+object);
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

	AbstractBinding checkIfDisplayModeShouldChange(AbstractBinding object, boolean setValueAsNewEditedValue)
	{
		AbstractBinding returned = object;

		EditionMode oldEditionMode = editionMode;
		EditionMode newEditionMode = editionMode;

		if (object != null) {
			if (object instanceof BindingExpression && ((BindingExpression)object).getExpression() != null) {
				Expression exp = ((BindingExpression)object).getExpression();
				if (exp instanceof BindingValueConstant) {
					returned = ((BindingValueConstant)exp).getStaticBinding();
				}
				if (exp instanceof BindingValueVariable) {
					returned = ((BindingValueVariable)exp).getBindingValue();
				}
				if (exp instanceof BindingValueFunction) {
					returned = ((BindingValueFunction)exp).getBindingValue();
				}
			}
		}

		if (returned instanceof StaticBinding) {
			newEditionMode = EditionMode.STATIC_BINDING;
		}
		if (returned instanceof BindingExpression) {
			newEditionMode = EditionMode.BINDING_EXPRESSION;
		}
		if (returned instanceof BindingValue) {
			if (((BindingValue)returned).isCompoundBinding()
					|| (getBindingDefinition() != null && getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE)) {
				newEditionMode = EditionMode.COMPOUND_BINDING;
			} else if (oldEditionMode != EditionMode.NORMAL_BINDING && oldEditionMode != EditionMode.COMPOUND_BINDING) {
				newEditionMode = EditionMode.NORMAL_BINDING;
			}
		}
		if (returned==null) {
			newEditionMode = EditionMode.NORMAL_BINDING;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("DISPLAY_MODE was: "+oldEditionMode+" is now "+newEditionMode);
		}
		if (oldEditionMode.useCommonPanel() != newEditionMode.useCommonPanel()) {
			if (newEditionMode.useCommonPanel()) {
				if (newEditionMode == EditionMode.COMPOUND_BINDING) {
					activateCompoundBindingMode();
				} else {
					activateNormalBindingMode();
				}
			}
			else if (object instanceof BindingExpression) {
				activateBindingExpressionMode((BindingExpression)object);
			}
		}
		if (oldEditionMode != EditionMode.COMPOUND_BINDING && newEditionMode == EditionMode.COMPOUND_BINDING) {
			activateCompoundBindingMode();
		}

		editionMode = newEditionMode;

		// Should i change edited object ???
		if (returned != object && setValueAsNewEditedValue) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Switching edited object from "+object+" to "+returned);
			}
			_editedObject = returned;
			updateCustomPanel(getEditedObject());
		}

		return returned;
	}




	boolean isKeyPathValid(String pathIgnoringLastPart)
	{
		if (!(_selectorPanel instanceof BindingSelectorPanel)) {
			return false;
		}
		StringTokenizer token = new StringTokenizer(pathIgnoringLastPart,".",false);
		Object obj = null;
		int i = 0;
		while(token.hasMoreTokens()){
			obj = ((BindingSelectorPanel)_selectorPanel).findElementEquals(((BindingSelectorPanel)_selectorPanel).listAtIndex(i).getModel(), token.nextToken());
			if(obj==null) {
				return false;
			}
			i++;
		}
		return true;
	}

	@Override
	public void fireEditedObjectChanged()
	{
		updateCustomPanel(getEditedObject());
		if (!getIsUpdatingModel()) {
			_isProgrammaticalySet = true;
			if (!textIsEditing) {
				getTextField().setText(renderedString(getEditedObject()));
			}
			if (getEditedObject() != null) {
				getTextField().setForeground(getEditedObject().isBindingValid()?Color.BLACK:Color.RED);
			}
			else {
				getTextField().setForeground(Color.RED);
			}
			_isProgrammaticalySet = false;
		}
	}

	public boolean areCompoundBindingAllowed()
	{
		//if (getBindingDefinition() != null && getBindingDefinition().getIsSettable()) return false;
		return _allowsCompoundBindings;
	}

	public void allowsCompoundBindings()
	{
		_allowsCompoundBindings = true;
		rebuildPopup();
	}

	public void denyCompoundBindings()
	{
		_allowsCompoundBindings = false;
		rebuildPopup();
	}

	public boolean areBindingExpressionsAllowed()
	{
		if (getBindingDefinition() != null && (getBindingDefinition().getIsSettable()
				|| getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE)) {
			return false;
		}
		return _allowsBindingExpressions;
	}

	public void allowsBindingExpressions()
	{
		_allowsBindingExpressions = true;
		rebuildPopup();
	}

	public void denyBindingExpressions()
	{
		_allowsBindingExpressions = false;
		rebuildPopup();
	}

	public boolean areStaticValuesAllowed()
	{
		if (getBindingDefinition() != null
				&& (getBindingDefinition().getIsSettable()
						|| getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE)) {
			return false;
		}
		return _allowsStaticValues;
	}

	public void allowsStaticValues()
	{
		_allowsStaticValues = true;
		rebuildPopup();
	}

	public void denyStaticValues()
	{
		_allowsStaticValues = false;
		rebuildPopup();
	}

	private void rebuildPopup()
	{
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

	public void activateCompoundBindingMode()
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("ActivateCompoundBindingMode() getEditedObject()="+getEditedObject()+" editionMode="+editionMode+" popupIsShown()="+popupIsShown()+" _selectorPanel="+_selectorPanel);
		}
		if ((_selectorPanel != null) && (editionMode != EditionMode.COMPOUND_BINDING)) {
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

	public void activateNormalBindingMode()
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("activateNormalBindingMode()");
		}
		if ((_selectorPanel != null) && (editionMode != EditionMode.NORMAL_BINDING)) {
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

	public void activateBindingExpressionMode(BindingExpression bindingExpression)
	{
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
			}
			else {
				_editedObject = new BindingExpression(getBindingDefinition(),getBindable()); // I dont want to notify it !!!
			}
			deleteCustomPanel();
			if (showAgain) {
				openPopup();
				updateCustomPanel(getEditedObject());
			}
		}
	}

	@Override
	protected void deleteCustomPanel()
	{
		super.deleteCustomPanel();
		_selectorPanel = null;
	}

	@Override
	public void setRevertValue(AbstractBinding oldValue)
	{
		if (oldValue != null) {
			_revertBindingValue = oldValue.clone();
		} else {
			_revertBindingValue = null;
		}
	}

	@Override
	public AbstractBinding getRevertValue()
	{
		return _revertBindingValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(AbstractBinding editedObject)
	{
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

	public void refreshBindingModel()
	{
		if (_bindable != null && _selectorPanel != null) {
			//_selectorPanel.setBindingModel(_bindable.getBindingModel());
			_selectorPanel.update();
		}
	}

	@Override
	public void updateCustomPanel(AbstractBinding editedObject)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateCustomPanel() with "+editedObject);
		}
		if (editedObject != null && editedObject instanceof BindingValue && ((BindingValue)editedObject).isCompoundBinding()) {
			activateCompoundBindingMode();
		}
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
	}

	public void resetMethodCallPanel()
	{
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			((BindingSelectorPanel)_selectorPanel).resetMethodCallPanel();
		}
	}

	@Override
	public String renderedString(AbstractBinding editedObject)
	{
		if (editedObject != null) {
			return (editedObject).getStringRepresentation();
		}
		return "";
	}

	public Bindable getBindable()
	{
		return _bindable;
	}

	public void setBindable(Bindable bindable)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setBindable with " + bindable);
		}
		_bindable = bindable;
		if ((bindable != null) && (_selectorPanel != null)) {
			_selectorPanel.fireBindableChanged();
		}
		//getCustomPanel().setBindingModel(bindable.getBindingModel());
		updateTextFieldProgrammaticaly();
	}

	@Override
	public void updateTextFieldProgrammaticaly()
	{
		// Don't update textfield if original event was triggered by a textfield edition
		if (!textIsEditing) {
			super.updateTextFieldProgrammaticaly();
		}
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
	//BindingModel _bindingModel;

	public BindingDefinition getBindingDefinition()
	{
		return _bindingDefinition;
	}

	public void setBindingDefinition(BindingDefinition bindingDefinition)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(toString()+"Setting new binding definition: "+bindingDefinition+ " old: "+_bindingDefinition);
		}
		if (bindingDefinition != _bindingDefinition) {
			_bindingDefinition = bindingDefinition;
			AbstractBinding bindingValue = getEditedObject();
			if (bindingValue != null) {
				bindingValue.setBindingDefinition(bindingDefinition);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("set BD "+bindingDefinition+" for BV "+bindingValue);
				}
			}
			if (_selectorPanel != null) {
				_selectorPanel.fireBindingDefinitionChanged();
			}
		}
	}

	public BindingModel getBindingModel()
	{
		if (getBindable() != null) {
			return getBindable().getBindingModel();
		}
		return null;
	}

	@Override
	public AbstractBindingSelectorPanel getCustomPanel()
	{
		return (AbstractBindingSelectorPanel)super.getCustomPanel();
	}

	protected BindingExpression makeBindingExpression()
	{
		if (getBindable() != null) {
			BindingExpressionFactory factory = getBindable().getBindingFactory().getBindingExpressionFactory();
			BindingExpression returned = new BindingExpression(getBindingDefinition(), getBindable());
			returned.setExpression(factory.getVariableFactory().makeVariable(new Word("")));
			return returned;
		}
		return null;
	}

	protected AbstractBinding makeBinding()
	{
		AbstractBinding returned = null;
		if (editionMode == EditionMode.BINDING_EXPRESSION) {
			if ((getBindingDefinition() != null) && (getBindable() != null)) {
				returned = makeBindingExpression();
			}
		}
		else if (editionMode == EditionMode.STATIC_BINDING) {
			if ((getBindingDefinition() != null) && (getBindable() != null)) {
				if (getBindingDefinition().getType() != null) {
					if (TypeUtils.isBoolean(getBindingDefinition().getType())) {
						returned = new BooleanStaticBinding(getBindingDefinition(),getBindable(),false);
					}
					else if (TypeUtils.isInteger(getBindingDefinition().getType())
							|| TypeUtils.isLong(getBindingDefinition().getType())
							|| TypeUtils.isShort(getBindingDefinition().getType())
							|| TypeUtils.isChar(getBindingDefinition().getType())
							|| TypeUtils.isByte(getBindingDefinition().getType())) {
						returned = new IntegerStaticBinding(getBindingDefinition(),getBindable(),0);
					}
					else if (TypeUtils.isFloat(getBindingDefinition().getType())
							|| TypeUtils.isDouble(getBindingDefinition().getType())) {
						returned = new FloatStaticBinding(getBindingDefinition(),getBindable(),0.0);
					}
				}
				else if (TypeUtils.isString(getBindingDefinition().getType())) {
					returned = new StringStaticBinding(getBindingDefinition(), getBindable(),"");
				}
			}
		}
		else if (editionMode == EditionMode.NORMAL_BINDING || editionMode == EditionMode.COMPOUND_BINDING) { // Normal or compound binding
			if ((getBindingDefinition() != null) && (getBindable() != null)) {
				returned = new BindingValue(getBindingDefinition(), getBindable());
			}
		}
		return returned;
	}

	AbstractBinding recreateBindingValue()
	{
		setEditedObject(makeBinding());
		logger.info("Recreating Binding with mode "+editionMode+" as "+getEditedObject());
		return getEditedObject();
	}

	Bindable _bindable;

	protected abstract class AbstractBindingSelectorPanel extends ResizablePanel
	{
		protected abstract void synchronizePanelWithTextFieldValue(String textValue);

		protected abstract void init();

		protected abstract void update();
		protected abstract void fireBindingDefinitionChanged();
		protected abstract void fireBindableChanged();


		protected abstract void processTabPressed();
		protected abstract void processDownPressed();
		protected abstract void processUpPressed();
		protected abstract void processLeftPressed();
		protected abstract void processRightPressed();
		protected abstract void processEnterPressed();
		protected abstract void processBackspace();
		protected abstract void processDelete();

		protected abstract void willApply();

	}

	@Override
	protected void openPopup() {
		if (_selectorPanel != null) {
			if (_selectorPanel instanceof BindingSelectorPanel) {
				JList list = ((BindingSelectorPanel)_selectorPanel).listAtIndex(0);
				if (list.getModel().getSize()==1) {
					list.setSelectedIndex(0);
				}
			}
		}
		super.openPopup();
		if (_selectorPanel != null) {
			ButtonsControlPanel controlPanel = null;
			if (_selectorPanel instanceof BindingSelectorPanel) {
				controlPanel = ((BindingSelectorPanel)_selectorPanel)._controlPanel;
			}
			else if (_selectorPanel instanceof BindingExpressionSelectorPanel) {
				controlPanel = ((BindingExpressionSelectorPanel)_selectorPanel)._controlPanel;
			}
			if (controlPanel != null) {
				controlPanel.applyFocusTraversablePolicyTo(controlPanel,false);
			}
		}
	}


	@Override
	public void closePopup()
	{
		super.closePopup();
		//logger.info("closePopup()");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextField().requestFocus();
			}
		});
	}

	@Override
	public void apply()
	{
		if (_selectorPanel != null) {
			_selectorPanel.willApply();
		}
		AbstractBinding bindingValue = getEditedObject();
		if (bindingValue != null) {
			if (bindingValue.isBindingValid()) {
				if (bindingValue instanceof BindingValue) {
					((BindingValue)bindingValue).connect();
				}
				getTextField().setForeground(Color.BLACK);
			}else{
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
	public void cancel()
	{
		setEditedObject(_revertBindingValue);
		closePopup();
		super.cancel();
	}

	@Override
	protected void pointerLeavesPopup()
	{
		cancel();
	}

	public boolean getIsUpdatingModel()
	{
		return isUpdatingModel;
	}

	public void setUpdatingModel(boolean isUpdatingModelFlag)
	{
		this.isUpdatingModel = isUpdatingModelFlag;
	}

	protected AbstractListModel getRootListModel()
	{
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			return ((BindingSelectorPanel)_selectorPanel).getRootColumnListModel();
		}
		return null;
	}

	protected final AbstractListModel getListModelFor(BindingPathElement element,Type resultingType)
	{
		if (_selectorPanel != null && _selectorPanel instanceof BindingSelectorPanel) {
			return ((BindingSelectorPanel)_selectorPanel).getColumnListModel(element,resultingType);
		}
		return null;
	}

	protected void valueSelected(int index, JList list, AbstractBinding binding)
	{
		if (!(binding instanceof BindingValue)) {
			editionMode = EditionMode.NORMAL_BINDING;
			binding = makeBinding(); // Should create a BindingValue instance !!!
			if (!(binding instanceof BindingValue)) {
				logger.severe("Should never happen: valueSelected() called for a non-BindingValue instance !");
				return;
			}
		}
		BindingValue bindingValue = (BindingValue)binding;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Value selected: index="+index+" list="+list+" bindingValue="+bindingValue);
		}
		BindingSelectorPanel.BindingColumnElement selectedValue
		= (BindingSelectorPanel.BindingColumnElement)list.getSelectedValue();
		if (selectedValue == null) {
			return;
		}
		if (index == 0
				&& (selectedValue.getElement() instanceof BindingVariable)) { // ICI
			if (list.getSelectedValue() != bindingValue.getBindingVariable()) {
				bindingValue.setBindingVariable((BindingVariable)selectedValue.getElement());
				setEditedObject(bindingValue);
				fireEditedObjectChanged();
			}
		} else {
			if (selectedValue.getElement() instanceof SimplePathElement) {
				if (selectedValue != bindingValue.getBindingPathElementAtIndex(index - 1)) {
					bindingValue.setBindingPathElementAtIndex(selectedValue.getElement(), index - 1);
					setEditedObject(bindingValue);
					fireEditedObjectChanged();
				}
			}
			else if ((selectedValue.getElement() instanceof ComplexPathElement) && (editionMode == EditionMode.COMPOUND_BINDING)) {
				// TODO: we need to handle here generic ComplexPathElement and not only MethodCall
				BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(index - 1);
				if (!(currentElement instanceof MethodCall)
						|| (((MethodCall)currentElement).getMethod() != ((MethodDefinition)selectedValue.getElement()).getMethod())) {
					Method method = ((MethodDefinition)selectedValue.getElement()).getMethod();
					MethodCall newMethodCall = new MethodCall(bindingValue,method,bindingValue.getAccessedType());
					bindingValue.setBindingPathElementAtIndex(newMethodCall, index - 1);
					setEditedObject(bindingValue);
					fireEditedObjectChanged();
				}
			}
		}
	}

	boolean isAcceptableStaticBindingValue(String stringValue)
	{
		if (getBindingDefinition() == null || getBindingDefinition().getType() == null) {
			return false;
		}
		StaticBinding b = makeStaticBindingFromString(stringValue);
		if (b==null) {
			return false;
		}
		if (TypeUtils.isObject(getBindingDefinition().getType())
				&& !(stringValue.endsWith("."))) {
			return true;
		}
		if (TypeUtils.isBoolean(getBindingDefinition().getType())) {
			return (b instanceof BooleanStaticBinding);
		}
		else if (TypeUtils.isInteger(getBindingDefinition().getType())
				|| TypeUtils.isLong(getBindingDefinition().getType())
				|| TypeUtils.isShort(getBindingDefinition().getType())
				|| TypeUtils.isChar(getBindingDefinition().getType())
				|| TypeUtils.isByte(getBindingDefinition().getType())) {
			return (b instanceof IntegerStaticBinding);
		}
		else if (TypeUtils.isFloat(getBindingDefinition().getType())
				|| TypeUtils.isDouble(getBindingDefinition().getType())) {
			if (stringValue.endsWith(".")) {
				return false;
			}
			return (b instanceof IntegerStaticBinding) || (b instanceof FloatStaticBinding);
		}
		else if (TypeUtils.isString(getBindingDefinition().getType())) {
			return (b instanceof StringStaticBinding);
		}
		return false;
	}

	private boolean isAcceptableAsBeginningOfBooleanStaticBindingValue(String stringValue)
	{
		if (stringValue.length() > 0) {
			if (stringValue.length() <= 4 && "true".substring(0,stringValue.length()).equalsIgnoreCase(stringValue)) {
				return true;
			}
			if (stringValue.length() <= 5 && "false".substring(0,stringValue.length()).equalsIgnoreCase(stringValue)) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	boolean isAcceptableAsBeginningOfStringStaticBindingValue(String stringValue)
	{
		if (stringValue.length() > 0) {
			if ((stringValue.indexOf("\"") == 0) || (stringValue.indexOf("'") == 0)) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	boolean isAcceptableAsBeginningOfStaticBindingValue(String stringValue)
	{
		//logger.info("isAcceptableAsBeginningOfStaticBindingValue for ? "+stringValue+" project="+getProject()+" bd="+getBindingDefinition());
		if (getBindingDefinition() == null || getBindingDefinition().getType() == null) {
			return false;
		}

		if (stringValue.length()==0) {
			return true;
		}

		if (TypeUtils.isObject(getBindingDefinition().getType())) {
			// In this case, any of matching is enough
			return ((isAcceptableStaticBindingValue(stringValue) && !(stringValue.endsWith("."))) // Special case to handle float on-the-fly typing
					|| isAcceptableAsBeginningOfBooleanStaticBindingValue(stringValue)
					|| isAcceptableAsBeginningOfStringStaticBindingValue(stringValue));
		}

		if (TypeUtils.isBoolean(getBindingDefinition().getType())) {
			return isAcceptableAsBeginningOfBooleanStaticBindingValue(stringValue);
		}
		else if (TypeUtils.isInteger(getBindingDefinition().getType())
				|| TypeUtils.isLong(getBindingDefinition().getType())
				|| TypeUtils.isShort(getBindingDefinition().getType())
				|| TypeUtils.isChar(getBindingDefinition().getType())
				|| TypeUtils.isByte(getBindingDefinition().getType())) {
			return isAcceptableStaticBindingValue(stringValue);
		}
		else if (TypeUtils.isFloat(getBindingDefinition().getType())
				|| TypeUtils.isDouble(getBindingDefinition().getType())) {
			if (stringValue.endsWith(".") && stringValue.length() > 1) {
				return isAcceptableStaticBindingValue(stringValue.substring(0,stringValue.length()-1));
			}
			return isAcceptableStaticBindingValue(stringValue);
		}
		else if (TypeUtils.isString(getBindingDefinition().getType())) {
			return isAcceptableAsBeginningOfStringStaticBindingValue(stringValue);
		}
		return false;
	}

	StaticBinding makeStaticBindingFromString(String stringValue)
	{
		if (getBindable() != null) {
			StaticBindingFactory factory = getBindable().getBindingFactory().getStaticBindingFactory();
			StaticBinding returned = factory.convertFromString(stringValue);
			if (returned == null) {
				return null;
			}
			returned.setOwner(getBindable());
			returned.setBindingDefinition(getBindingDefinition());
			return returned;
		}
		return null;
	}

	AbstractBinding makeBindingFromString(String stringValue)
	{
		if (getBindable() != null) {
			BindingFactory factory = getBindable().getBindingFactory();
			if (factory == null) {
				logger.info("OK, je tiens le probleme, factory=null");
				logger.info("bindable="+getBindable());
			}
			factory.setWarnOnFailure(false);
			factory.setBindable(getBindable());
			AbstractBinding returned = factory.convertFromString(stringValue);
			if (returned == null) {
				return null;
			}
			returned.setOwner(getBindable());
			returned.setBindingDefinition(getBindingDefinition());
			factory.setWarnOnFailure(true);
			return returned;
		}
		return null;
	}

	boolean textFieldSynchWithEditedObject()
	{
		if(StringUtils.isEmpty(getTextField().getText())) {
			return getEditedObject()==null || StringUtils.isEmpty(renderedString(getEditedObject()));
		}
		return getTextField().getText()!=null && getTextField().getText().equals(renderedString(getEditedObject()));
	}

	boolean textFieldNotSynchWithEditedObject()
	{
		return !textFieldSynchWithEditedObject();
	}

	@Override
	public BindingSelector getJComponent()
	{
		return this;
	}

}