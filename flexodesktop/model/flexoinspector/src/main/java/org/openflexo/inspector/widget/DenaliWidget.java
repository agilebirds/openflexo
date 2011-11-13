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
package org.openflexo.inspector.widget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableModification;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectingWidget;
import org.openflexo.inspector.InspectorModelView;
import org.openflexo.inspector.InspectorObserver;
import org.openflexo.inspector.InspectorTabbedPanel;
import org.openflexo.inspector.TabModelView;
import org.openflexo.inspector.model.GroupModel;
import org.openflexo.inspector.model.InnerTabWidget;
import org.openflexo.inspector.model.PropertyListModel;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.propertylist.PropertyListWidget;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;
import org.openflexo.xmlcode.StringRepresentable;

/**
 * Abstract class representing a widget in the inspector
 * 
 * @author bmangez, sguerin
 */
public abstract class DenaliWidget<T> extends JPanel implements InspectorObserver, InnerTabWidgetView {

	private static final Logger logger = Logger.getLogger(DenaliWidget.class.getPackage().getName());

	private static final String READONLY = "readOnly";

	// ==========================================================================
	// ============================= Constants
	// ==================================
	// ==========================================================================

	public static final String DROPDOWN = "DROPDOWN";

	public static final String TEXT_FIELD = "TEXT_FIELD";

	public static final String TEXT_FIELD_AND_LABEL = "TEXT_FIELD_AND_LABEL";

	public static final String LOCALIZED_TEXT_FIELD = "LOCALIZED_TEXT_FIELD";

	public static final String TEXT_AREA = "TEXT_AREA";

	public static final String CHECKBOX = "CHECKBOX";

	public static final String CHECKBOX_LIST = "CHECKBOX_LIST";

	public static final String RADIOBUTTON_LIST = "RADIOBUTTON_LIST";

	public static final String DROPDOWN_BUTTON = "DROPDOWN_BUTTON";

	public static final String RADIO_DROPDOWN = "RADIO_DROPDOWN";

	public static final String INTEGER = "INTEGER";

	public static final String FLOAT = "FLOAT";

	public static final String DOUBLE = "DOUBLE";

	public static final String FILE = "FILE";

	public static final String DIRECTORY = "DIRECTORY";

	public static final String COLOR = "COLOR";

	public static final String FONT = "FONT";

	public static final String EXTERNAL = "EXTERNAL";

	public static final String READ_ONLY_TEXT_FIELD = "READ_ONLY_TEXT_FIELD";

	public static final String READ_ONLY_CHECKBOX = "READ_ONLY_CHECKBOX";

	public static final String READ_ONLY_TEXT_AREA = "READ_ONLY_TEXT_AREA";

	public static final String READ_ONLY_INTEGER = "READ_ONLY_INTEGER";

	public static final String JAVA_CODE = "JAVA_CODE";

	public static final String WYSIWYG_LIGHT = "WYSIWYG_LIGHT";

	public static final String WYSIWYG_ULTRA_LIGHT = "WYSIWYG_ULTRA_LIGHT";

	public static final String INFOLABEL = "INFOLABEL";

	public static final String LABEL = "LABEL";

	public static final String CUSTOM = "CUSTOM";

	public static final String REGEXP = "REGEXP";

	public static final String BUTTON = "BUTTON";

	public static final Dimension MINIMUM_SIZE = new Dimension(30, 25);
	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	// protected String _observedPropertyName;

	protected String _observedTabName;

	private InspectableObject _model;

	protected JLabel _label;

	private WidgetConditional conditional;

	protected PropertyModel _propertyModel;

	private int indexInTab;

	private TabModelView _tab;

	public static Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static Font DEFAULT_MEDIUM_FONT = new Font("SansSerif", Font.PLAIN, 10);

	protected boolean modelUpdating = false;

	protected boolean widgetUpdating = false;

	boolean widgetRefreshPlanned = false;

	protected DenaliWidget(PropertyModel model, AbstractController controller) {
		super();
		_propertyModel = model;
		_observedTabName = model._tabModelName;
		conditional = new WidgetConditional(model);
		_label = null;
		_controller = controller;
	}

	public void refreshMe() {
		if (getDynamicComponent().getParent() != null && getDynamicComponent().getParent() instanceof JComponent) {
			((JComponent) getDynamicComponent().getParent()).revalidate();
			((JComponent) getDynamicComponent().getParent()).repaint();
		}
	}

	/**
	 * Overrides getMinimumSize
	 * 
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
	}

	AbstractController _controller;

	public AbstractController getController() {
		/*if (InspectorController.hasInstance())
			return InspectorController.instance();
		else return null;*/
		return _controller;
	}

	public static InnerTabWidgetView instance(InnerTabWidget propModel, AbstractController controller) {
		InnerTabWidgetView answer = null;
		if (answer == null) {
			answer = createWidget(propModel, controller);
		}
		answer.setController(controller);
		return answer;
	}

	private static InnerTabWidgetView createWidget(InnerTabWidget widget, AbstractController controller) {
		if (widget instanceof PropertyModel) {
			PropertyModel model = (PropertyModel) widget;
			String propType = model.getWidget();
			if (model instanceof PropertyListModel) {
				return new PropertyListWidget((PropertyListModel) model, controller);
			}
			if (propType.equals(DROPDOWN)) {
				return new DropDownWidget(model, controller);
			} else if (propType.equals(TEXT_FIELD)) {
				return new TextFieldWidget(model, controller);
			} else if (propType.equals(TEXT_FIELD_AND_LABEL)) {
				return new TextFieldAndLabelWidget(model, controller);
			} else if (propType.equals(LOCALIZED_TEXT_FIELD)) {
				return new LocalizedTextFieldWidget(model, controller);
			} else if (propType.equals(TEXT_AREA)) {
				return new TextAreaWidget(model, controller);
			} else if (propType.equals(CHECKBOX)) {
				return new CheckBoxWidget(model, controller);
			} else if (propType.equals(CHECKBOX_LIST)) {
				return new CheckBoxListWidget(model, controller);
			} else if (propType.equals(RADIOBUTTON_LIST)) {
				return new RadioButtonListWidget(model, controller);
			} else if (propType.equals(DROPDOWN_BUTTON)) {
				return new DropDownButtonWidget(model, controller);
			} else if (propType.equals(RADIO_DROPDOWN)) {
				return new RadioDropDownWidget(model, controller);
			} else if (propType.equals(INTEGER)) {
				return new IntegerWidget(model, controller);
			} else if (propType.equals(FLOAT)) {
				return new FloatWidget(model, controller);
			} else if (propType.equals(DOUBLE)) {
				return new DoubleWidget(model, controller);
			} else if (propType.equals(FILE)) {
				return new FileEditWidget(model, controller);
			} else if (propType.equals(DIRECTORY)) {
				return new DirectoryEditWidget(model, controller);
			} else if (propType.equals(COLOR)) {
				return new ColorWidget(model, controller);
			} else if (propType.equals(FONT)) {
				return new FontWidget(model, controller);
			} else if (propType.equals(EXTERNAL)) {
				return new ExternalWidget(model, controller);
			} else if (propType.equals(READ_ONLY_TEXT_FIELD)) {
				return new ReadOnlyWidget(model, controller);
			} else if (propType.equals(READ_ONLY_CHECKBOX)) {
				return new ReadOnlyCheckBoxWidget(model, controller);
			} else if (propType.equals(READ_ONLY_TEXT_AREA)) {
				return new ReadOnlyTextAreaWidget(model, controller);
			} else if (propType.equals(READ_ONLY_INTEGER)) {
				return new ReadOnlyIntegerWidget(model, controller);
			} else if (propType.equals(JAVA_CODE)) {
				return new JavaCodeWidget(model, controller);
			} else if (propType.equals(WYSIWYG_LIGHT)) {
				return new WysiwygLightWidget(model, controller, false);
			} else if (propType.equals(WYSIWYG_ULTRA_LIGHT)) {
				return new WysiwygLightWidget(model, controller, true);
			} else if (propType.equals(INFOLABEL)) {
				return new InfoLabelWidget(model, controller);
			} else if (propType.equals(LABEL)) {
				return new LabelWidget(model, controller);
			} else if (propType.equals(REGEXP)) {
				return new RegexpTextfieldWidget(model, controller);
			} else if (propType.equals(BUTTON)) {
				return new ButtonWidget(model, controller);
			} else if (propType.equals(CUSTOM)) {
				String className = model.getValueForParameter("className");
				try {
					Class widgetClass = Class.forName(className);
					Class[] constructorClassParams = new Class[2];
					constructorClassParams[0] = PropertyModel.class;
					constructorClassParams[1] = AbstractController.class;
					Constructor c = widgetClass.getConstructor(constructorClassParams);
					Object[] constructorParams = new Object[2];
					constructorParams[0] = model;
					constructorParams[1] = controller;
					CustomWidget returned = (CustomWidget) c.newInstance(constructorParams);
					if (!returned.isMinimumSizeSet()) {
						returned.setMinimumSize(MINIMUM_SIZE);
					}
					return returned;
				} catch (ClassNotFoundException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Class not found: " + className + ". See console for details.");
					}
					e.printStackTrace();
				} catch (SecurityException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (InstantiationException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				}
				return null;
			} else if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unknow widget in inspector file: " + propType);
			}
			return new UnknownWidget(model, controller);
		} else if (widget instanceof GroupModel) {
			return new LineWidget((GroupModel) widget, controller);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unknown model of type " + widget.getClass().getSimpleName());
			}
			return null;
		}
	}

	@Override
	public void setController(AbstractController controller) {
		_controller = controller;
	}

	// ==========================================================================
	// ========================== Updating from/to model
	// ========================
	// ==========================================================================

	/**
	 * Update the widget retrieving data from the model. This method is called when the observed property change.
	 */
	@Override
	public abstract void updateWidgetFromModel();

	/**
	 * Update the model given the actual state of the widget
	 */
	public abstract void updateModelFromWidget();

	// ==========================================================================
	// ========================== Widget Store Management
	// =======================
	// ==========================================================================

	@Override
	public synchronized void switchObserved(InspectableObject inspectable) {
		// SGU: I think we can try here to this:
		/*if (inspectable != getModel()) {
			if (widgetHasFocus()) {
				looseFocus();
			}
		    if (getModel() != null) {
		        getModel().deleteInspectorObserver(this);
		    }
		    setModel(inspectable);
		    getModel().addInspectorObserver(this);
		}*/

		if (widgetHasFocus()) {
			looseFocus();
		}
		if (getModel() != null) {
			getModel().deleteInspectorObserver(this);
		}
		setModel(inspectable);
		getModel().addInspectorObserver(this);
		try {
			if (isWidgetVisible()) {
				if (!modelUpdating && !widgetUpdating) {
					updateWidgetFromModel();
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							updateWidgetFromModel();
						}
					});
				}
			}
		} catch (IllegalStateException e) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("IllegalStateException raised: loop in AWT loop");
				// Cannot modify, since was initialized from the widget itself
				// Just catching this exception allow to update fields while
				// avoiding loops
			}
		}

	}

	public void performUpdate() {
		if (!widgetUpdating) {
			updateWidgetFromModel();
		}
	}

	/*
	 * private void refreshAllConditional() { if (getParent() != null) { for
	 * (int i = 0; i < getParent().getComponents().length; i++) { if
	 * (getParent().getComponent(i) instanceof DenaliWidget) { ((DenaliWidget)
	 * getParent()).refreshConditional(); } } } }
	 *
	 * private void refreshConditional() { boolean conditionalValue =
	 * checkConditionalValue(); if (conditionalValue &&
	 * getDynamicComponent().getParent().getParent() == null) { int index =
	 * Math.min(indexInTab, _tab.getComponentCount());
	 * _tab.add(TabModelView.widgetPanel(this), index);
	 *  } else if (!conditionalValue &&
	 * getDynamicComponent().getParent().getParent() != null) {
	 * _tab.remove(getDynamicComponent().getParent()); } _tab.updateUI(); }
	 */
	/*
	 * public boolean checkConditionalValue() { return
	 * conditional.checkConditionalValue(getModel()); }
	 */

	/*
	 * public boolean checkConditionalValue() { if (_conditional != null &&
	 * !_conditional.equals("")) { try { StringTokenizer or = new
	 * StringTokenizer(_conditional, "|"); while (or.hasMoreTokens()) { // OR
	 * StringTokenizer or_and = new StringTokenizer(or.nextToken().trim(), "&");
	 * boolean and = true; while (or_and.hasMoreTokens() && and) { // AND String
	 * cond = or_and.nextToken().trim(); boolean negate = false; if
	 * (cond.indexOf("!=") != -1) { negate = true; } StringTokenizer stk = new
	 * StringTokenizer(cond, "!="); String attribute = stk.nextToken().trim();
	 * String value = stk.nextToken().trim(); Object attributeValue =
	 * getModel().objectForKey(attribute); if (attributeValue instanceof
	 * KeyValueCoding) { if (_propertyModel.hasFormatter()) { attributeValue =
	 * getStringRepresentation(attributeValue); } } if (attributeValue == null &&
	 * value.equals("null")) and = true; else if (attributeValue != null &&
	 * attributeValue.equals(value)) and = true; else and = false;
	 *
	 * if (negate) and = !and; } if (and == true) { return true; } } // if none
	 * OR exists => return true. return false; } catch (Exception e) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Exception in
	 * DenaliWidget.checkConditionalValue (" + _conditional + ") for:" +
	 * _propertyModel.label + ""); e.printStackTrace(); } } return true; }
	 */

	@Override
	public abstract JComponent getDynamicComponent();

	// ==========================================================================
	// ============================= ObserverInterface
	// ==========================
	// ==========================================================================

	@Override
	public void update(final InspectableObject inspectable, final InspectableModification modification) {
		if (modification == null) {
			return;
		}
		if (widgetRefreshPlanned) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Loop detected...trying to avoid... please pray...");
			}
			return;
		}
		/*
		 * The purpose of the following lines is to avoid an update
		 * of the widget while it is already updating the model (this is forbidden
		 * by AWT). To do so, we will post a thread that will perform
		 * the update operation after the execution of the current
		 * AWT-loop
		 */
		if (modelUpdating && !widgetRefreshPlanned && modification.isReentrant() /* Do it only for reentrant modifications !!! */) {
			if (willUpdateModel(inspectable, modification)) {
				widgetRefreshPlanned = true;
				SwingUtilities.invokeLater(new Runnable() {
					/**
					 * Overrides run
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("pfew...that was close. Good Swing consultant: updating now");
						}
						widgetRefreshPlanned = false;
						updateImmediately(inspectable, modification);
					}
				});
			}
			return;
		}
		updateImmediately(inspectable, modification);
	}

	/**
	 * @param inspectable
	 * @param modification
	 */
	protected void updateImmediately(final InspectableObject inspectable, final InspectableModification modification) {
		if ((!widgetUpdating) && (!modelUpdating) && willUpdateModel(inspectable, modification)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("update in " + this.getClass().getName() + " with " + modification);
			}
			try {
				if (isWidgetVisible()) {
					updateWidgetFromModel();
				}
			} catch (IllegalStateException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("!!!!IllegalStateException raised: loop in AWT loop. You should investigate this.");
				}
			}
		}
	}

	private boolean willUpdateModel(InspectableObject inspectable, InspectableModification modification) {
		String propertyName = modification.propertyName();
		return (inspectable == _model && propertyName != null && (propertyName.equals(getObservedPropertyName()) || propertyName
				.regionMatches(0, getObservedPropertyName(), getObservedPropertyName().lastIndexOf('.') + 1, propertyName.length())));
	}

	// ==========================================================================
	// ============================== Formatting
	// ================================
	// ==========================================================================

	public synchronized void setStringValue(String newValueAsString) {
		if (typeIsString() || typeIsObject()) {
			setObjectValue((T) newValueAsString);
		} else if (typeIsStringConvertable()) {
			setObjectValue((T) getTypeConverter().convertFromString(newValueAsString));
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an error in some configuration file :\n the property named '" + _propertyModel.name
						+ "' has no way to handle string representation building !");
			}
			System.out.println("TypeIsString=" + typeIsString());
			System.out.println("getType()=" + getType());
			System.out.println("hasObjectValue()=" + hasObjectValue());
			System.out.println("getObjectValue()=" + getObjectValue());
		}
	}

	public String getStringValue() {
		Object object = getObjectValue();
		if (object != null) {
			return getStringRepresentation(object);
		} else {
			return null;
		}
	}

	public String getDisplayStringRepresentation(Object object) {
		if (object instanceof Enum && (_propertyModel.hasFormatter())) {

			Method formatter;
			try {
				formatter = ((Enum) object).getDeclaringClass().getMethod(_propertyModel.getValueForParameter("format"));
				return (String) formatter.invoke(object);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return getStringRepresentation(object);
	}

	public String getStringRepresentation(Object object) {
		if (object instanceof String) {
			return (String) object;
		} else if ((object instanceof KeyValueCoding) && (_propertyModel.hasFormatter())) {
			return _propertyModel.getFormattedObject((KeyValueCoding) object);
		} else if (object instanceof Enum) {
			return FlexoLocalization.localizedForKey(((Enum) object).name().toLowerCase());
		} else if (object instanceof StringConvertable) {
			String str = ((StringConvertable) object).getConverter().convertToString(object);
			return str != null ? FlexoLocalization.localizedForKey(str.toLowerCase()) : "";
		} else if (object instanceof Number) {
			return object.toString();
		} else if (object instanceof Boolean) {
			return object.toString();
		} else if (object instanceof File) {
			return object.toString();
		} else if (object instanceof StringRepresentable) {
			return object.toString();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an error in some configuration file :\n the property named '" + _propertyModel.name
						+ "' has no string representation formatter ! Object is a "
						+ (object != null ? object.getClass().getName() : "null"));
			}
			return object.toString();
		}
	}

	// ==========================================================================
	// ============================= Type resolution
	// ============================
	// ==========================================================================

	protected Class _type;

	protected StringEncoder.Converter _typeConverter;

	protected Class getType() {
		if (_type == null) {
			retrieveType();
		}
		return _type;
	}

	protected Class retrieveType() {
		if (hasObjectValue()) {
			Object value = getObjectValue();
			if (value != null) {
				_type = value.getClass();
				if (StringConvertable.class.isAssignableFrom(_type)) {
					_typeConverter = ((StringConvertable) value).getConverter();
				}
			} else if (getPropertyModel().hasValueForParameter("type")) {
				try {
					_type = Class.forName(getPropertyModel().getValueForParameter("type"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				try {
					_type = getModel().getTypeForKey(observedPropertyName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (_type == null) {
					_type = getDefaultType();
				}
				if (StringConvertable.class.isAssignableFrom(_type)) {
					_typeConverter = lookupStaticStringConverter(_type);
				}
			}
			if (_type != null) {
				while (_type.isAnonymousClass()) {
					_type = _type.getSuperclass();
				}
			}
			return _type;
		} else {
			if (getPropertyModel().hasValueForParameter("type")) {
				try {
					_type = Class.forName(getPropertyModel().getValueForParameter("type"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			return _type;
		}
	}

	private Converter<T> lookupStaticStringConverter(Class<T> type) {
		for (int i = 0; i < type.getFields().length; i++) {
			Field f = type.getFields()[i];
			if (Modifier.isStatic(f.getModifiers()) && Converter.class.isAssignableFrom(f.getType())) {
				try {
					return (Converter<T>) f.get(null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		// if ("textColor".equals(_observedPropertyName) || "backColor".equals(_observedPropertyName))
		// System.err.println("coucou");
		return null;
	}

	/**
	 * Returns a string representation
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + observedPropertyName() + "]";
	}

	protected boolean typeIsString() {
		if (getType() != null) {
			return (String.class.isAssignableFrom(getType()));
		} else {
			return false;
		}
	}

	protected boolean typeIsObject() {
		return Object.class == getType();
	}

	protected boolean typeIsStringConvertable() {
		return getTypeConverter() != null;
	}

	protected StringEncoder.Converter getTypeConverter() {
		return _typeConverter;
	}

	public boolean isChoiceList() {
		if (getPropertyModel().getBooleanValueForParameter("ignorechoicelist")) {
			return false;
		}
		if (getType() != null) {
			return (ChoiceList.class.isAssignableFrom(getType()) || getType().getEnumConstants() != null);
		} else {
			return false;
		}
	}

	// =================================================================
	// ==================== Dynamic access to values ===================
	// =================================================================

	protected boolean valueInError = false;

	protected boolean focusListenerEnabled = true;

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (getDynamicComponent() != null) {
			getDynamicComponent().setBackground(bg);
		}
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (getDynamicComponent() != null) {
			getDynamicComponent().setForeground(fg);
		}
	}

	/*public KeyValueCoding getTargetObject()
	{
	    return _propertyModel.getTargetObject(getModel());
	}

	public String getLastAccessor()
	{
	    return _propertyModel.getLastAccessor();
	}*/

	/**
	 * This method can be called to store the newValue in the model.
	 * 
	 * @param newValue
	 */
	protected synchronized void setObjectValue(T newValue) {
		if (getController() != null && getController().getDelegate() != null && _propertyModel.allowDelegateHandling()) {

			// Special case for delegate handling

			KeyValueCoding target = _propertyModel.getTargetObject(getModel());
			String lastAccessor = _propertyModel.getLastAccessor();
			Object oldValue = getObjectValue();
			if (oldValue == null && newValue == null) {
				return;
			}
			if ((newValue != null) && (oldValue != null) && (oldValue.equals(newValue))) {
				return;
			}
			// logger.info("_propertyModel="+_propertyModel);
			// logger.info("target="+target);
			// logger.info("lastAccessor="+lastAccessor);
			// logger.info("delegate="+getController().getDelegate());
			if (target != null && getController().getDelegate().handlesObjectOfClass(target.getClass())) {
				getController().getDelegate().setLocalizedPropertyName(getLocalizedLabel());
				getController().getDelegate().setTarget(target);
				getController().getDelegate().setKey(lastAccessor);
				focusListenerEnabled = false;
				boolean ok = getController().getDelegate().setObjectValue(newValue);
				if (!ok) {
					valueInError = true;
					SwingUtilities.invokeLater(new UpdateFromModel());
					if (_tab != null) {
						_tab.valueChange(newValue, this);
					}
					modelUpdating = false;
				}
				focusListenerEnabled = true;
				if (ok) {
					notifyInspectedPropertyChanged(newValue);
				}
			}
		} else {
			try {

				// Normal case here, just invoke this

				_propertyModel.setObjectValue(getModel(), newValue);
				notifyInspectedPropertyChanged(newValue);
			}

			catch (AccessorInvocationException e) {
				valueInError = true;
				if (_controller != null) {
					if (!_controller.handleException(getModel(), observedPropertyName(), newValue, e.getTargetException())) {
						// Revert to value defined in the model !
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Exception was NOT handled");
						}
						SwingUtilities.invokeLater(new UpdateFromModel());
					} else {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Exception was handled");
						}
						SwingUtilities.invokeLater(new UpdateFromModel());
						if (_tab != null) {
							_tab.valueChange(newValue, this);
						}
					}
				} else {
					e.getTargetException().printStackTrace();
				}
				valueInError = false;
				modelUpdating = false;
			}
		}

		/*Object oldValue = getObjectValue();
		// logger.info("Old value="+oldValue+" New value="+newValue);
		if (oldValue == null) {
		    if (newValue == null) {
		        if (logger.isLoggable(Level.FINE))
		            logger.fine("Same null value. Ignored.");
		        return;
		    }
		} else if ((newValue != null) && (oldValue.equals(newValue))) {
		    if (logger.isLoggable(Level.FINE))
		        logger.fine("Same value. Ignored.");
		    return;
		}
		try {
		    boolean ok = true;
		    KeyValueCoding target = getTargetObject();
		    if (getController() == null || getController().getDelegate() == null || !getController().getDelegate().handlesObjectOfClass(target.getClass())) {
		        if (newValue != null)
		            if (logger.isLoggable(Level.FINE))
		                logger.fine("setObjectValue() for property " + observedPropertyName() + " with " + newValue + " (type is "
		                        + newValue.getClass() + ")");
		            else if (logger.isLoggable(Level.FINE))
		                logger.fine("setObjectValue() for property " + observedPropertyName() + " with " + newValue + ")");
		        if (logger.isLoggable(Level.FINE))
		            logger.fine("setObjectValue() for property " + observedPropertyName() + " with " + newValue);
		        if (target != null) {
		            target.setObjectForKey(newValue, getLastAccessor());
		        } else if (logger.isLoggable(Level.WARNING))
		            logger.warning("Target object is null for key " + observedPropertyName() + ". We should definitely investigate this.");
		    } else {
		        getController().getDelegate().setLocalizedPropertyName(getLocalizedLabel());
		        getController().getDelegate().setTarget(target);
		        getController().getDelegate().setKey(getLastAccessor());
		        focusListenerEnabled = false;
		        ok =getController().getDelegate().setObjectValue(newValue);
		        if (!ok) {
		            valueInError = true;
		            SwingUtilities.invokeLater(new UpdateFromModel());
		            if (_tab != null) {
		                _tab.valueChange(newValue, this);
		            }
		            modelUpdating = false;
		        }
		        focusListenerEnabled = true;
		    }
		    if (ok) {
		        notifyInspectedPropertyChanged(newValue);
		        //BMA : comment those line because already executed in notify
		//                if (_tab != null) {
		//                    _tab.valueChange(newValue, this);
		//                }
		    }
		} catch (AccessorInvocationException e) {
		    valueInError = true;
		    if (InspectorController.instance() != null) {
		    if (!InspectorController.instance().handleException(getModel(), observedPropertyName(),
		            newValue, e.getTargetException())) {
		        // Revert to value defined in the model !
		        if (logger.isLoggable(Level.INFO))
		            logger.info("Exception was NOT handled");
		        SwingUtilities.invokeLater(new UpdateFromModel());
		    } else {
		        if (logger.isLoggable(Level.FINE))
		            logger.fine("Exception was handled");
		        SwingUtilities.invokeLater(new UpdateFromModel());
		        if (_tab != null) {
		            _tab.valueChange(newValue, this);
		        }
		    }
		    }
		    else {
		    	e.getTargetException().printStackTrace();
		    }
		    valueInError = false;
		    modelUpdating = false;
		} catch (Exception e) {
		    e.printStackTrace();
		    if (logger.isLoggable(Level.WARNING))
		        logger.warning("setObjectValue() with " + newValue + " failed for property "
		                + observedPropertyName() + " for object " + getModel().getClass().getName()
		                + " : exception " + e.getMessage());
		}*/
	}

	protected boolean hasObjectValue() {
		try {
			return _propertyModel.hasObjectValue(getModel());
		} catch (AccessorInvocationException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getObjectValue() failed for property " + getObservedPropertyName() + " for object " + getModel()
						+ " : exception " + e.getMessage());
			}
			e.getTargetException().printStackTrace();
			return false;
		}
		/* try {
		    if (getModel() == null) {
		        return false;
		    }
		    KeyValueCoding target = getTargetObject();
		    if (target != null) {
		    	target.objectForKey(getLastAccessor());
		    	return true;
		    } else {
		        return false;
		    }
		} catch (InvalidObjectSpecificationException e) {
		    return false;
		} catch (AccessorInvocationException e) {
		    if (logger.isLoggable(Level.WARNING))
		        logger.warning("getObjectValue() failed for property " + observedPropertyName()
		                + " for object " + getModel().getClass().getName() + " : exception "
		                + e.getMessage());
		    e.getTargetException().printStackTrace();
		    return false;
		} catch (Exception e) {
			e.printStackTrace();
		    if (logger.isLoggable(Level.WARNING))
		        logger.warning("getObjectValue() failed for property " + observedPropertyName()
		                + " for object " + getModel().getClass().getName() + " : exception "
		                + e.getMessage());
		    return false;
		}*/
	}

	protected T getObjectValue() {
		if (getModel() == null) {
			return null;
		}
		return (T) _propertyModel.getObjectValue(getModel());
		/* try {
		    if (getModel() == null) {
		        return null;
		    }
		    KeyValueCoding target = getTargetObject();
		    if (target != null) {
		        return (T)target.objectForKey(getLastAccessor());
		    } else {
		        return null;
		    }
		} catch (AccessorInvocationException e) {
		    if (logger.isLoggable(Level.WARNING))
		        logger.warning("getObjectValue() failed for property " + observedPropertyName()
		                + " for object " + getModel().getClass().getName() + " : exception "
		                + e.getMessage());
		    e.getTargetException().printStackTrace();
		    return null;
		} catch (Exception e) {
		    if (logger.isLoggable(Level.WARNING))
		        logger.warning("getObjectValue() failed for property " + observedPropertyName()
		                + " for object " + getModel().getClass().getName() + " : exception "
		                + e.getMessage());
		    return null;
		}*/
	}

	/*protected synchronized void setBooleanValue(boolean value)
	{
	    boolean oldValue = getBooleanValue();
	    if (oldValue == value) {
	        logger.info("Same value. Ignored.");
	        return;
	    }

	    try {
	        KeyValueCoding target = getTargetObject();
	        if (target != null) {
	            target.setBooleanValueForKey(value, getLastAccessor());
	        }
	        notifyInspectedPropertyChanged(new Boolean(value));
	    } catch (Exception e) {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("setBooleanValue() with " + value + " failed for property "
	                    + observedPropertyName() + " for object " + getModel().getClass().getName()
	                    + " : exception " + e.getMessage());
	    }
	}*/

	protected void notifyInspectedPropertyChanged(Object newValue) {
		if (_inspectorModelView != null) {
			_inspectorModelView.valueChange(newValue, this);
		} else if (_tab != null) {
			_tab.valueChange(newValue, this);
		}

	}

	public void notifyInspectedPropertyChanged() {
		notifyInspectedPropertyChanged(getObjectValue());
	}

	/* protected synchronized boolean getBooleanValue()
	{
	    try {
	        KeyValueCoding target = getTargetObject();
	        if (target != null) {
	            return target.booleanValueForKey(getLastAccessor());
	        } else {
	            return false;
	        }
	    } catch (Exception e) {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("getBooleanValue() failed for property " + observedPropertyName()
	                    + " for object " + getModel().getClass().getName() + " : exception "
	                    + e.getMessage());
	        return false;
	    }
	}*/

	/* protected synchronized void setIntegerValue(int value)
	{
	    int oldValue = getIntegerValue();
	    if (oldValue == value) {
	        logger.info("Same value. Ignored.");
	        return;
	    }

	    try {
	        KeyValueCoding target = getTargetObject();
	        if (target != null) {
	            target.setIntegerValueForKey(value, getLastAccessor());
	        }
	        notifyInspectedPropertyChanged(new Integer(value));
	    } catch (Exception e) {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("setIntegerValue() with " + value + " failed for property "
	                    + observedPropertyName() + " for object " + getModel().getClass().getName()
	                    + " : exception " + e.getMessage());
	    }
	}*/

	/*protected synchronized void setDoubleValue(double value)
	{
	    double oldValue = getDoubleValue();
	    if (oldValue == value) {
	        logger.info("Same value. Ignored.");
	        return;
	    }

	    try {
	        KeyValueCoding target = getTargetObject();
	        if (target != null) {
	            target.setDoubleValueForKey(value, getLastAccessor());
	        }
	        notifyInspectedPropertyChanged(new Double(value));
	    } catch (Exception e) {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("setIntegerValue() with " + value + " failed for property "
	                    + observedPropertyName() + " for object " + getModel().getClass().getName()
	                    + " : exception " + e.getMessage());
	    }
	}*/

	/*protected synchronized int getIntegerValue()
	{
	    try {
	        KeyValueCoding target = getTargetObject();
	        if (target != null) {
	            return target.integerValueForKey(getLastAccessor());
	        } else {
	            return 0;
	        }
	    } catch (Exception e) {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("getIntegerValue() failed for property " + observedPropertyName()
	                    + " for object " + getModel().getClass().getName() + " : exception "
	                    + e.getMessage());
	        return 0;
	    }
	}*/

	/*protected synchronized double getDoubleValue()
	{
	    try {
	        KeyValueCoding target = getTargetObject();
	        if (target != null) {
	            return target.doubleValueForKey(getLastAccessor());
	        } else {
	            return 0;
	        }
	    } catch (Exception e) {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("getIntegerValue() failed for property " + observedPropertyName()
	                    + " for object " + getModel().getClass().getName() + " : exception "
	                    + e.getMessage());
	        return 0;
	    }
	}

	protected float getFloatValue()
	{
	    try {
	        KeyValueCoding target = getTargetObject();
	        if (target != null) {
	            return target.floatValueForKey(getLastAccessor());
	        } else {
	            return 0;
	        }
	    } catch (Exception e) {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("getIntegerValue() failed for property " + observedPropertyName()
	                    + " for object " + getModel().getClass().getName() + " : exception "
	                    + e.getMessage());
	        return 0;
	    }
	}*/

	// ====================================================
	// ===================== Accessors ====================
	// ====================================================

	protected String observedPropertyName() {
		return getObservedPropertyName();
	}

	/**
	 * Overrides getXMLModel
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#getXMLModel()
	 */
	@Override
	public InnerTabWidget getXMLModel() {
		return _propertyModel;
	}

	public InspectableObject getModel() {
		return _model;
	}

	protected synchronized void setModel(InspectableObject value) {
		_model = value;
		if (isWidgetVisible()) {
			retrieveType();
			// refreshConditional();
		}
	}

	public String getLocalizedLabel() {
		if (_propertyModel.getLocalizedLabel() != null) {
			return _propertyModel.getLocalizedLabel();
		} else {
			return FlexoLocalization.localizedForKey(_propertyModel.label);
		}
	}

	@Override
	public JLabel getLabel() {
		if (_label == null && _propertyModel.label != null) {
			_label = new JLabel(_propertyModel.label + " : ", SwingConstants.RIGHT);
			if (_propertyModel.getLocalizedLabel() != null) {
				_label.setText(_propertyModel.getLocalizedLabel() + " : ");
			} else {
				_label.setText(FlexoLocalization.localizedForKey(_propertyModel.label, " : ", _label));
			}
			_label.setForeground(Color.BLACK);
			// _label.setBackground(InspectorCst.BACK_COLOR);
			_label.setFont(DEFAULT_LABEL_FONT);
			if (_propertyModel.help != null && !_propertyModel.help.equals("")) {
				_label.setToolTipText(_propertyModel.help);
				logger.fine("setToolTipText() with " + _propertyModel.help);
			}
			if (_controller.getHelpDelegate() != null && _controller.getHelpDelegate().isHelpAvailableFor(_propertyModel)) {
				_label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						if (_propertyModel.getLocalizedLabel() != null) {
							_label.setText("<html><u>" + _propertyModel.getLocalizedLabel() + "</u>" + " :&nbsp;" + "</html>");
						} else {
							_label.setText("<html><u>" + FlexoLocalization.localizedForKey(_propertyModel.label) + "</u>" + " :&nbsp;"
									+ "</html>");
						}
						_label.setForeground(Color.BLUE);
						_label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						if (_propertyModel.getLocalizedLabel() != null) {
							_label.setText(_propertyModel.getLocalizedLabel() + " : ");
						} else {
							_label.setText(FlexoLocalization.localizedForKey(_propertyModel.label) + " : ");
						}
						_label.setForeground(Color.BLACK);
						_label.setCursor(Cursor.getDefaultCursor());
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						_controller.getHelpDelegate().displayHelpFor(getModel());
					}
				});
			}
		}
		return _label;
	}

	public abstract Class getDefaultType();

	/**
	 * @param model
	 * @return
	 */
	protected boolean isReadOnly() {
		if (getPropertyModel().hasValueForParameter(READONLY)) {
			String ro = getPropertyModel().getValueForParameter(READONLY);
			if (ro.equalsIgnoreCase("true") || ro.equalsIgnoreCase("yes")) {
				return true;
			} else if (ro.equalsIgnoreCase("false") || ro.equalsIgnoreCase("no")) {
				return false;
			} else {
				// dynamic test
				boolean negate = ro.startsWith("!");
				if (negate) {
					ro = ro.substring(1);
				}
				boolean isReadOnly = getModel() != null && getModel().booleanValueForKey(ro);
				if (negate) {
					isReadOnly = !isReadOnly;
				}
				return isReadOnly;
			}
		}
		return false;
	}

	public boolean isWidgetVisible() {
		if (getController() != null && !getController().isTabPanelVisible(getPropertyModel().getTabModel(), getModel())) {
			return false;
		}
		if (conditional != null) {
			return (conditional.isVisible(getModel()));
		}
		return true;
	}

	/**
	 * @param newValue
	 * @param propertyName
	 * @return
	 */
	@Override
	public boolean isStillVisible(Object newValue, String propertyName) {
		return isWidgetVisible();
		// return conditional.isVisible(getModel());
	}

	public boolean isStillInvisible(Object newValue, String propertyName) {
		// return conditional.isStillInvisible(newValue,propertyName);
		return !conditional.isVisible(getModel());
	}

	public boolean dependsOfProperty(String propertyName) {
		return conditional.dependsOfProperty(propertyName);
	}

	@Override
	public boolean dependsOfProperty(DenaliWidget widget) {
		return dependsOfProperty(widget.observedPropertyName());
	}

	public boolean shouldBeDisplayed() {
		return conditional.isVisible(getModel());
	}

	public boolean isVisible(InspectableObject inspectable) {
		return conditional.isVisible(inspectable);
	}

	protected boolean _hasFocus;

	public void gainFocus() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("DenaliWidget " + getClass().getSimpleName() + ":" + _propertyModel.name + " GAIN focus");
		}
		_hasFocus = true;
		if (getInspectingWidget() != null && getInspectingWidget() instanceof InspectorTabbedPanel) {
			((InspectorTabbedPanel) getInspectingWidget()).widgetGetFocus(this);
		}
	}

	public void looseFocus() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("DenaliWidget " + getClass().getSimpleName() + ":" + _propertyModel.name + " LOOSE focus");
		}
		if (!valueInError && focusListenerEnabled && !modelUpdating) {
			updateModelFromWidget();
		}
		_hasFocus = false;
	}

	public boolean widgetHasFocus() {
		return _hasFocus;
	}

	public String getObservedPropertyName() {
		return _propertyModel.name;
	}

	public String getObservedTabName() {
		return _observedTabName;
	}

	public PropertyModel getPropertyModel() {
		return _propertyModel;
	}

	private InspectorModelView _inspectorModelView;

	@Override
	public void setTabModelView(TabModelView tab, int index) {
		_tab = tab;
		indexInTab = index;
		if (tab != null) {
			_inspectorModelView = tab.getInspectorModelView();
			if (getInspectingWidget() instanceof InspectorTabbedPanel) {
				if (_propertyModel.name.equals(((InspectorTabbedPanel) getInspectingWidget()).getLastInspectedPropertyName())) {
					((InspectorTabbedPanel) getInspectingWidget()).setNextFocusedWidget(this);
				}
			}
		}
	}

	public InspectingWidget getInspectingWidget() {
		if (_tab != null && _tab.getInspectorModelView() != null) {
			return _tab.getInspectorModelView().getInspectingWidget();
		}
		return null;
	}

	public void setTabModelView(TabModelView tab) {
		_tab = tab;
		if (tab != null) {
			_inspectorModelView = tab.getInspectorModelView();
		}
	}

	public int getIndexInTab() {
		return indexInTab;
	}

	public class UpdateFromModel implements Runnable {

		/**
		 * Overrides run
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			while (modelUpdating) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			if (!getModel().isDeleted()) {
				updateWidgetFromModel();
			}
			valueInError = false;
		}
	}

	// ====================================================
	// =============== Layout informations ================
	// ====================================================

	public static final String EXPAND_VERTICALLY = "expandVertically";
	public static final String EXPAND_HORIZONTALLY = "expandHorizontally";
	public static final String WIDGET_LAYOUT = "widgetLayout";
	public static final String DISPLAY_LABEL = "displayLabel";

	private WidgetLayout _widgetLayout = null;
	private Boolean _expandHorizontally = null;
	private Boolean _expandVertically = null;
	private Boolean _displayLabel = null;

	@Override
	public final boolean shouldExpandHorizontally() {
		if (_expandHorizontally == null) {
			if (getPropertyModel().hasValueForParameter(EXPAND_HORIZONTALLY)) {
				_expandHorizontally = getPropertyModel().getBooleanValueForParameter(EXPAND_HORIZONTALLY);
			} else {
				_expandHorizontally = defaultShouldExpandHorizontally();
			}
		}
		return _expandHorizontally;
	}

	@Override
	public final boolean shouldExpandVertically() {
		if (_expandVertically == null) {
			if (getPropertyModel().hasValueForParameter(EXPAND_VERTICALLY)) {
				_expandVertically = getPropertyModel().getBooleanValueForParameter(EXPAND_VERTICALLY);
			} else {
				_expandVertically = defaultShouldExpandVertically();
			}
		}
		return _expandVertically;
	}

	@Override
	public final WidgetLayout getWidgetLayout() {
		if (_widgetLayout == null) {
			if (getPropertyModel().hasValueForParameter(WIDGET_LAYOUT)) {
				if (getPropertyModel().getValueForParameter(WIDGET_LAYOUT).equals("1COL")) {
					_widgetLayout = WidgetLayout.LABEL_ABOVE_WIDGET_LAYOUT;
				} else if (getPropertyModel().getValueForParameter(WIDGET_LAYOUT).equals("2COL")) {
					_widgetLayout = WidgetLayout.LABEL_NEXTTO_WIDGET_LAYOUT;
				} else {
					_widgetLayout = getDefaultWidgetLayout();
				}
			} else {
				_widgetLayout = getDefaultWidgetLayout();
			}
		}
		return _widgetLayout;
	}

	public WidgetLayout getDefaultWidgetLayout() {
		return WidgetLayout.LABEL_NEXTTO_WIDGET_LAYOUT;
	}

	public boolean defaultShouldExpandHorizontally() {
		return true;
	}

	public boolean defaultShouldExpandVertically() {
		return false;
	}

	// Override this if you want
	public boolean defaultDisplayLabel() {
		return (getPropertyModel().label != null);
	}

	@Override
	public final boolean displayLabel() {
		if (_displayLabel == null) {
			if (getPropertyModel().hasValueForParameter(DISPLAY_LABEL)) {
				_displayLabel = getPropertyModel().getBooleanValueForParameter(DISPLAY_LABEL);
			} else {
				_displayLabel = defaultDisplayLabel();
			}
		}
		return _displayLabel;
	}

	public enum WidgetLayout {
		LABEL_NEXTTO_WIDGET_LAYOUT, LABEL_ABOVE_WIDGET_LAYOUT
	}

}
