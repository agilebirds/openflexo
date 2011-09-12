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
package org.openflexo.foundation.ie.widget;

import java.awt.Font;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.StaticBinding;
import org.openflexo.foundation.bindings.StringStaticBinding;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.ComponentInstanceOwner;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.PopupComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.ContentTypeChanged;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.WidgetAttributeChanged;
import org.openflexo.foundation.ie.util.ClientSideEventType;
import org.openflexo.foundation.ie.util.HyperlinkActionType;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents an hyperlink widget
 * 
 * @author bmangez
 */
public class IEHyperlinkWidget extends IEControlWidget implements ComponentInstanceOwner, Comparable<IEHyperlinkWidget>,
		IEWidgetWithValueList, IEWidgetWithMainBinding {
	private static final Logger logger = FlexoLogger.getLogger(IEHyperlinkWidget.class.getPackage().getName());

	private static final java.awt.Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 10);

	public static final String HYPERLINK_WIDGET = "hyperlink_widget";

	public static final String CUSTOM_METHOD = "customMethod";

	private Domain domain;

	private String domainName;

	private TextFieldType _fieldType;

	private boolean isHTML = false;

	private HyperlinkType _hyperlinkType = null;

	private boolean _isSubmittingOpener = true;

	private String _link;

	private boolean _openInNewWindow = false;

	private PopupComponentInstance _popupComponentInstance;

	private String helpMessage;

	private String _value;

	private String _prototypeValues;

	private AbstractBinding _bindingLink;

	private AbstractBinding _bindingValue;

	private String _bindingValueWhenEmpty;

	private AbstractBinding _bindingMailSubject;

	private AbstractBinding _bindingMailToAdress;

	private AbstractBinding _bindingUrl;

	private AbstractBinding _bindingMailBody;

	private AbstractBinding _bindingClientSideScriptCode;

	private ClientSideEventType _clientSideEventType;

	private HyperlinkActionType _actionType;

	private AbstractBinding _customMethod;

	private boolean includePopupInPage = false;

	private boolean isMandatoryFlexoAction = false;

	private String _input;

	private String _behavior;

	private String _funcName;

	private String _confirmMessage;

	private String nextPageTabName;

	private TabComponentDefinition nextPageTab;

	private String _methodName;

	private IETextFieldWidget dateTextfield;

	private boolean submitForm = false;

	private boolean isClosingPopup = false;

	private boolean _isValidatingForm = true;

	private boolean _isSavingChanges = false;

	private boolean isCustomButton = false;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEHyperlinkWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IEHyperlinkWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public String getDefaultInspectorName() {
		return "Hyperlink.inspector";
	}

	@Override
	public void performOnDeleteOperations() {
		if (_popupComponentInstance != null) {
			_popupComponentInstance.delete();
		}
		super.performOnDeleteOperations();
	}

	// ==========================================================================
	// =================== AbstractButtonWidgetInterface accessors
	// =====================
	// ==========================================================================

	public HyperlinkType getHyperlinkType() {
		return _hyperlinkType;
	}

	public void setHyperlinkType(HyperlinkType type) {
		if (getWOComponent() instanceof IEPopupComponent
				&& (HyperlinkType.FLEXOACTION.equals(type) || HyperlinkType.DISPLAYACTION.equals(type))) {
			return;
		}
		_hyperlinkType = type;
		if (!isDeserializing() && !isBeingCloned() && !isCreatedByCloning()) {
			if (type == HyperlinkType.FLEXOACTION || type == HyperlinkType.DISPLAYACTION) {
				setIsMandatoryFlexoAction(true);
				setSubmitForm(true);
			}
		}
		setChanged();
		notifyObservers(new IEDataModification("hyperlinkType", null, type));
		if (type == HyperlinkType.MAILTO) {
			setActionType(null);
		}
	}

	public String getLink() {
		return _link;
	}

	public void setLink(String link) {
		this._link = link;
		setChanged();
		notifyObservers(new IEDataModification("link", null, link));
	}

	public boolean getOpenInNewWindow() {
		return _openInNewWindow;
	}

	public void setOpenInNewWindow(boolean openInNewWindow) {
		_openInNewWindow = openInNewWindow;
		setChanged();
		notifyObservers(new IEDataModification("openInNewWindow", null, new Boolean(openInNewWindow)));
	}

	public String getPrototypeValues() {
		return _prototypeValues;
	}

	public void setPrototypeValues(String values) {
		_prototypeValues = values;
		setChanged();
		notifyObservers(new IEDataModification("prototypeValues", null, values));
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList()
	 */
	@Override
	public List<Object> getValueList() {
		return getValueList(null);
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList(org.openflexo.foundation.wkf.FlexoProcess)
	 */
	@Override
	public List<Object> getValueList(FlexoProcess process) {
		return parseValueListToAppropriateType(getValue(), getPrototypeValues(), getFieldType(), getDomain(), process);
	}

	public String getDefaultValue() {
		if (isCustomButton()) {
			return ToolBox.capitalize(FlexoLocalization.localizedForKey("your_text"));
		} else {
			return ToolBox.capitalize(FlexoLocalization.localizedForKey("link"));
		}
	}

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		if (value == null || value.equals("")) {
			value = "link";
		}
		this._value = value;
		setChanged();
		notifyObservers(new IEDataModification("value", null, value));
	}

	public Vector<ActionNode> getAllActionNodesLinkedToThisButton() {
		Vector<ActionNode> actions = new Vector<ActionNode>();
		for (OperationNode operatioNode : getComponentDefinition().getAllOperationNodesLinkedToThisComponent()) {
			ActionNode a = operatioNode.getActionNodeForButton(this);
			if (a != null) {
				actions.add(a);
			}
		}
		return actions;
	}

	public WidgetBindingDefinition getBindingLinkDefinition() {
		return WidgetBindingDefinition.get(this, "bindingLink", String.class, BindingDefinitionType.GET, false);
	}

	public AbstractBinding getBindingLink() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingLink;
	}

	public void setBindingLink(AbstractBinding value) {
		_bindingLink = value;
		if (_bindingLink != null) {
			_bindingLink.setOwner(this);
			_bindingLink.setBindingDefinition(getBindingLinkDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingLink", null, _bindingLink));
	}

	public WidgetBindingDefinition getBindingValueDefinition() {
		if (getFieldType() != null && getFieldType() == TextFieldType.DATE) {
			return WidgetBindingDefinition.get(this, "binding_value", Date.class, BindingDefinitionType.GET, false);
		}
		if (getFieldType() != null && getFieldType() == TextFieldType.INTEGER) {
			return WidgetBindingDefinition.get(this, "binding_value", Number.class, BindingDefinitionType.GET, false);
		}
		if (getFieldType() != null && getFieldType() == TextFieldType.FLOAT) {
			return WidgetBindingDefinition.get(this, "binding_value", Float.class, BindingDefinitionType.GET, false);
		}
		if (getFieldType() != null && getFieldType() == TextFieldType.DOUBLE) {
			return WidgetBindingDefinition.get(this, "binding_value", Double.class, BindingDefinitionType.GET, false);
		}
		return WidgetBindingDefinition.get(this, "binding_value", String.class, BindingDefinitionType.GET, false);
	}

	public AbstractBinding getBindingValue() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingValue;
	}

	public void setBindingValue(AbstractBinding value) {
		_bindingValue = value;
		if (_bindingValue != null) {
			_bindingValue.setOwner(this);
			_bindingValue.setBindingDefinition(getBindingValueDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingValue", null, _bindingValue));
	}

	/**
	 * @return Returns the _valueWhenEmpty.
	 */
	public String getBindingValueWhenEmpty() {
		return _bindingValueWhenEmpty;
	}

	/**
	 * @param whenEmpty
	 *            The _valueWhenEmpty to set.
	 */
	public void setBindingValueWhenEmpty(String whenEmpty) {
		_bindingValueWhenEmpty = whenEmpty;
		setChanged();
		notifyObservers(new IEDataModification("bindingValueWhenEmpty", null, _bindingValueWhenEmpty));
	}

	public AbstractBinding getBindingMailSubject() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingMailSubject;
	}

	public void setBindingMailSubject(AbstractBinding value) {
		_bindingMailSubject = value;
		if (_bindingMailSubject != null) {
			_bindingMailSubject.setOwner(this);
			_bindingMailSubject.setBindingDefinition(getBindingMailSubjectDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingMailSubject", null, _bindingMailSubject));
	}

	public WidgetBindingDefinition getBindingMailSubjectDefinition() {
		return WidgetBindingDefinition.get(this, "bindingMailSubject", String.class, BindingDefinitionType.GET, false);
	}

	public AbstractBinding getBindingMailBody() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingMailBody;
	}

	public void setBindingMailBody(AbstractBinding value) {
		_bindingMailBody = value;
		if (_bindingMailBody != null) {
			_bindingMailBody.setOwner(this);
			_bindingMailBody.setBindingDefinition(getBindingMailBodyDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingMailBody", null, _bindingMailBody));
	}

	public WidgetBindingDefinition getBindingMailBodyDefinition() {
		return WidgetBindingDefinition.get(this, "bindingMailBody", String.class, BindingDefinitionType.GET, false);
	}

	public AbstractBinding getBindingMailToAdress() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingMailToAdress;
	}

	public void setBindingMailToAdress(AbstractBinding value) {
		_bindingMailToAdress = value;
		if (_bindingMailToAdress != null) {
			_bindingMailToAdress.setOwner(this);
			_bindingMailToAdress.setBindingDefinition(getBindingMailToAdressDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingMailToAdress", null, _bindingMailToAdress));
	}

	public WidgetBindingDefinition getBindingMailToAdressDefinition() {
		return WidgetBindingDefinition.get(this, "bindingMailToAdress", String.class, BindingDefinitionType.GET, false);
	}

	public String getUrl() {
		if (getBindingUrl() != null && getBindingUrl() instanceof StringStaticBinding) {
			return ((StringStaticBinding) getBindingUrl()).getValue();
		}
		return null;
	}

	public void setUrl(String url) {
		if (url != null) {
			if (!(getBindingUrl() instanceof StringStaticBinding)) {
				setBindingUrl(new StringStaticBinding(getBindingUrlDefinition(), this, url));
			}
			((StringStaticBinding) getBindingUrl()).setValue(url);
			setChanged();
			notifyModification("url", null, url);
		} else {
			setBindingUrl(null);
		}

	}

	public AbstractBinding getBindingUrl() {
		if (isBeingCloned() && !(_bindingUrl instanceof StaticBinding)) {
			return null;
		}
		return _bindingUrl;
	}

	public void setBindingUrl(AbstractBinding value) {
		_bindingUrl = value;
		if (_bindingUrl != null) {
			_bindingUrl.setOwner(this);
			_bindingUrl.setBindingDefinition(getBindingUrlDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingUrl", null, _bindingUrl));
	}

	public WidgetBindingDefinition getBindingUrlDefinition() {
		return WidgetBindingDefinition.get(this, "bindingUrl", String.class, BindingDefinitionType.GET, false);
	}

	public PopupComponentInstance getPopupComponentInstance() {
		return _popupComponentInstance;
	}

	public void setPopupComponentInstance(PopupComponentInstance popup) {
		if (_popupComponentInstance != null) {
			_popupComponentInstance.delete();
		}
		_popupComponentInstance = popup;
		_popupComponentInstance.setOwner(this);
		setChanged();
		notifyObservers(new IEDataModification("popupComponentInstance", null, popup));
	}

	/**
	 * call by inspector when setting the property "popupComponentInstance". It must be done because the choosen value in the inspector's
	 * dropdown is of type PopupComponentDefinition.
	 * 
	 * @param popupDef
	 */
	public void setPopupComponentDefinition(PopupComponentDefinition popupDef) {
		if (popupDef != null && isInPopup()) {
			logger.warning("Cannot bind a popup from a popup... sorry");
			popupDef = null;
		}
		if (_popupComponentInstance == null && popupDef == null) {
			return;
		}
		if (popupDef != null && _popupComponentInstance != null && _popupComponentInstance.getComponentDefinition().equals(popupDef)) {
			return;
		}
		setChanged();
		if (_popupComponentInstance != null) {
			_popupComponentInstance.delete();
			_popupComponentInstance = null;
		}
		if (popupDef != null) {
			if (popupDef.isHelper()) {
				setHyperlinkType(null);
			}
			// setting the new operationcomponent instance.
			_popupComponentInstance = new PopupComponentInstance(popupDef, getWOComponent());
			_popupComponentInstance.setOwner(this);
			// popupDef.addToReferences(_popupComponentInstance);
		}
	}

	public PopupComponentDefinition getPopupComponentDefinition() {
		if (_popupComponentInstance == null) {
			return null;
		}
		return _popupComponentInstance.getPopupComponentDefinition();
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		if (getPopupComponentInstance() != null) {
			answer.add(getPopupComponentInstance());
		}
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Hyperlink : " + (getValue() != null ? getValue() : getCalculatedLabel());
	}

	public static class HyperlinkUrlMustHaveAnUrl extends ValidationRule<HyperlinkUrlMustHaveAnUrl, IEHyperlinkWidget> {
		public HyperlinkUrlMustHaveAnUrl() {
			super(IEHyperlinkWidget.class, "hyperlink_url_must_have_an_url");
		}

		@Override
		public ValidationIssue<HyperlinkUrlMustHaveAnUrl, IEHyperlinkWidget> applyValidation(IEHyperlinkWidget hl) {
			if (hl.getHyperlinkType() == HyperlinkType.URL && StringUtils.isEmpty(hl.getUrl()) && hl.getBindingUrl() == null) {
				ValidationError<HyperlinkUrlMustHaveAnUrl, IEHyperlinkWidget> error = new ValidationError<HyperlinkUrlMustHaveAnUrl, IEHyperlinkWidget>(
						this, hl, "hyperlink_($object.value)_must_have_an_url");

				return error;
			}
			return null;
		}
	}

	public String getBehavior() {
		return _behavior;
	}

	public void setBehavior(String behavior) {
		_behavior = behavior;
		setChanged();
		notifyObservers(new IEDataModification("behavior", null, behavior));
	}

	public String getInput() {
		return _input;
	}

	public void setInput(String input) {
		_input = input;
		setChanged();
		notifyObservers(new IEDataModification("input", null, input));
	}

	public String getFuncName() {
		return _funcName;
	}

	public void setFuncName(String s) {
		_funcName = s;
		setChanged();
		notifyObservers(new IEDataModification("_funcName", null, _funcName));
	}

	/**
	 * 
	 * @return
	 * @deprecated use getHyperlinkType()
	 */
	@Deprecated
	public HyperlinkActionType getActionType() {
		return _actionType;
	}

	/**
	 * 
	 * @param actionType
	 * @deprecated use setHyperlinkType(HyperlinkType)
	 */
	@Deprecated
	public void setActionType(HyperlinkActionType actionType) {
		if (actionType != null) {
			if (actionType == HyperlinkActionType.FLEXO_ACTION) {
				setHyperlinkType(HyperlinkType.FLEXOACTION);
			} else {
				setHyperlinkType(HyperlinkType.DISPLAYACTION);
			}
		}
		HyperlinkActionType old = _actionType;
		_actionType = null;
		setChanged();
		notifyObservers(new WidgetAttributeChanged(WidgetAttributeChanged.ACTION_TYPE, old, actionType));
	}

	public boolean hasActionType() {
		return getIsDisplayAction() || getIsFlexoAction();
	}

	public boolean getIsMandatoryFlexoAction() {
		return isMandatoryFlexoAction;
	}

	public void setIsMandatoryFlexoAction(boolean isMandatory) {
		boolean old = this.isMandatoryFlexoAction;
		this.isMandatoryFlexoAction = isMandatory;
		setChanged();
		notifyObservers(new WidgetAttributeChanged(WidgetAttributeChanged.IS_MANDATORY_FLEXO_ACTION, new Boolean(old), new Boolean(
				isMandatoryFlexoAction)));
	}

	public String getBeautifiedName() {
		String s;
		s = getLabel();
		if (s == null || s.trim().length() == 0) {
			s = getName();
		}
		if (s == null || s.trim().length() == 0) {
			s = getValue();
		}
		if (s != null) {
			return s.replace('"', ' ');
		}
		return s;
	}

	public TabComponentDefinition getNextPageTab() {
		if (nextPageTab == null && nextPageTabName != null) {
			nextPageTab = (TabComponentDefinition) getProject().getFlexoComponentLibrary().getComponentNamed(nextPageTabName);
		}
		return nextPageTab;
	}

	public void setNextPageTab(TabComponentDefinition nextPageTab) {
		this.nextPageTab = nextPageTab;
		if (nextPageTab != null) {
			nextPageTabName = nextPageTab.getComponentName();
		} else {
			nextPageTabName = null;
		}
		setChanged();
		notifyObservers(new IEDataModification("nextPageTab", null, nextPageTab));
	}

	public String getNextPageTabName() {
		return nextPageTabName;
	}

	public void setNextPageTabName(String nextPageTabName) {
		this.nextPageTabName = nextPageTabName;
		nextPageTab = null;
		setChanged();
		notifyObservers(new IEDataModification("nextPageTabName", null, nextPageTabName));
	}

	public String getMethodName() {
		return _methodName;
	}

	public void setMethodName(String methodName) {
		_methodName = methodName;
		setChanged();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return HYPERLINK_WIDGET;
	}

	public IETextFieldWidget getDateTextfield() {
		return dateTextfield;
	}

	public void setDateTextfield(IETextFieldWidget field) {
		/*
		 * if (isCreatedByCloning()) return;
		 */
		IETextFieldWidget old = dateTextfield;
		dateTextfield = field;
		setChanged();
		notifyObservers(new IEDataModification("dateTextfield", old, dateTextfield));
	}

	/**
	 * Overrides setWOComponent
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#setWOComponent(org.openflexo.foundation.ie.IEWOComponent)
	 */
	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		if (_popupComponentInstance != null) {
			_popupComponentInstance.updateDependancies(getWOComponent(), woComponent);
		}
		super.setWOComponent(woComponent);// This call is very important because
											// it will update the WOComponent
											// components cache
		if (woComponent instanceof IEPopupComponent) {
			if (getHyperlinkType() == HyperlinkType.FLEXOACTION || getHyperlinkType() == HyperlinkType.DISPLAYACTION) {
				setHyperlinkType(null);
			}
		}
	}

	public String getBindingValueCodeStringRepresentation() {
		// TODO... but is it needed ?
		return null;
	}

	public String getConfirmMessage() {
		return _confirmMessage;
	}

	public WidgetBindingDefinition getBindingClientSideScriptDefinition() {
		return WidgetBindingDefinition.get(this, "bindingScriptMethod", String.class, BindingDefinitionType.GET, false);
	}

	public String getJavascriptConfirmMessage() {
		if (hasConfirmMessage()) {
			return ToolBox.getJavascriptComment(_confirmMessage);
		}
		return null;
	}

	public boolean hasConfirmMessage() {
		return _confirmMessage != null && _confirmMessage.trim().length() > 0;
	}

	public void setConfirmMessage(String m) {
		String old = _confirmMessage;
		_confirmMessage = m;
		notifyModification("confirmMessage", old, _confirmMessage);
	}

	public boolean isInstantiatedInOperation(OperationNode op) {
		if (!hasActionType()) {
			return true;
		}
		Enumeration<ActionNode> en = op.getAllActionNodes().elements();
		while (en.hasMoreElements()) {
			ActionNode a = en.nextElement();
			if (a.getAssociatedButtonWidget() != null && a.getAssociatedButtonWidget().equals(this)) {
				if (!a.isActivatedByAToken()) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean areComponentInstancesValid() {
		return getPopupComponentInstance() == null || getPopupComponentInstance().isValidInstance();
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (getPopupComponentInstance() != null && !getPopupComponentInstance().isValidInstance()) {
			_popupComponentInstance = null;// Cannot remove the dependance,
											// because we don't have the
											// component def anymore
		}
	}

	/**
	 * Overrides getRawRowKeyPath
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getRawRowKeyPath()
	 */
	@Override
	public String getRawRowKeyPath() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		if (desc == null) {
			return null;
		}
		String item = desc.getItemName();
		if (item == null) {
			return null;
		}
		if (getBindingValue() == null) {
			return null;
		}
		if (getBindingValue().getCodeStringRepresentation().indexOf(item) > -1
				&& getBindingValue().getCodeStringRepresentation().indexOf(item) + item.length() + 1 <= getBindingValue()
						.getCodeStringRepresentation().length()) {
			return getBindingValue().getCodeStringRepresentation().substring(
					getBindingValue().getCodeStringRepresentation().indexOf(item) + item.length() + 1);
		} else {
			return null;
		}
	}

	public boolean needAWorkflowConditional() {
		return (getIsDisplayAction() || getIsFlexoAction()) && !isAlwaysVisible();
	}

	private boolean isAlwaysVisible() {
		ComponentDefinition cd = getWOComponent().getComponentDefinition();
		Enumeration<ComponentInstance> en = null;
		if (cd instanceof OperationComponentDefinition) {
			en = cd.getComponentInstances().elements();
		} else if (cd instanceof TabComponentDefinition) {
			en = ((TabComponentDefinition) cd).getComponentInstances().elements();
		} else {
			return true;
		}
		while (en.hasMoreElements()) {
			ComponentInstance inst = en.nextElement();
			if (!isInstantiatedInOperation(((OperationComponentInstance) inst).getOperationNode())) {
				return false;
			}
		}
		return true;
	}

	public String workflowConditionalBindingName() {
		return getLabel() != null ? "showHyperlink_" + ToolBox.getJavaName(getLabel()) : "showHyperlink_" + getFlexoID();
	}

	public boolean isSearch() {
		return getHyperlinkType() == HyperlinkType.SEARCH;
	}

	public boolean getSubmitForm() {
		if (getHyperlinkType() != null) {
			if (getHyperlinkType() == HyperlinkType.CLIENTSIDESCRIPT) {
				return false;
			}
		}
		return getIsFlexoAction() || submitForm;
	}

	public void setSubmitForm(boolean submitForm) {
		boolean old = this.submitForm;
		this.submitForm = submitForm;
		setChanged();
		notifyObservers(new IEDataModification("submitForm", old, submitForm));
	}

	@Override
	public int compareTo(IEHyperlinkWidget o) {
		if (getFlexoID() < o.getFlexoID()) {
			return -1;
		} else if (getFlexoID() == o.getFlexoID()) {
			return 0;
		}
		return 1;
	}

	public boolean getIsDisplayAction() {
		return getHyperlinkType() == HyperlinkType.DISPLAYACTION;
	}

	public boolean getIsFlexoAction() {
		return getHyperlinkType() == HyperlinkType.FLEXOACTION;
	}

	public AbstractBinding getBindingScriptMethod() {
		return _bindingClientSideScriptCode;
	}

	public ClientSideEventType getClientSideEventType() {
		return _clientSideEventType;
	}

	public void setBindingScriptMethod(AbstractBinding value) {
		AbstractBinding old = _bindingClientSideScriptCode;
		_bindingClientSideScriptCode = value;
		notifyModification("bindingScriptMethod", old, _bindingClientSideScriptCode);
	}

	public void setClientSideEventType(ClientSideEventType eventType) {
		ClientSideEventType old = _clientSideEventType;
		_clientSideEventType = eventType;
		notifyModification("clientSideEventType", old, _clientSideEventType);
	}

	public boolean seemsToBeASelectAllLink() {
		return _value != null && _value.toLowerCase().equals("all") && getRelatedCheckBox() != null;
	}

	public IECheckBoxWidget getRelatedCheckBox() {
		if (findTDInParent() != null) {
			Enumeration<IObject> en = findTDInParent().tr().htmlTable().getAllEmbeddedIEObjects().elements();
			IObject temp = null;
			while (en.hasMoreElements()) {
				temp = en.nextElement();
				if (temp instanceof IECheckBoxWidget) {
					return (IECheckBoxWidget) temp;
				}
			}
		}
		return null;
	}

	public boolean getIsSubmittingOpener() {
		return _isSubmittingOpener;
	}

	public void setIsSubmittingOpener(boolean value) {
		boolean old = _isSubmittingOpener;
		_isSubmittingOpener = value;
		notifyModification("isSubmittingOpener", old, _isSubmittingOpener);
	}

	public boolean getIsValidatingForm() {
		return _isValidatingForm;
	}

	public void setIsValidatingForm(boolean validate) {
		boolean old = _isValidatingForm;
		_isValidatingForm = validate;
		notifyModification("isValidatingForm", old, _isValidatingForm);
	}

	public boolean getIsSavingChanges() {
		return _isSavingChanges;
	}

	public void setIsSavingChanges(boolean validate) {
		boolean old = _isSavingChanges;
		_isSavingChanges = validate;
		notifyModification("isSavingChanges", old, _isSavingChanges);
	}

	public WidgetBindingDefinition getCustomMethodBindingDefinition() {
		return WidgetBindingDefinition.get(this, CUSTOM_METHOD, null, BindingDefinitionType.EXECUTE, false);
	}

	public AbstractBinding getCustomMethod() {
		if (isBeingCloned()) {
			return null;
		}
		return _customMethod;
	}

	public void setCustomMethod(AbstractBinding executionPrimitive) {
		AbstractBinding oldBindingValue = _customMethod;
		_customMethod = executionPrimitive;
		if (_customMethod != null) {
			_customMethod.setOwner(this);
			_customMethod.setBindingDefinition(getCustomMethodBindingDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(CUSTOM_METHOD, oldBindingValue, executionPrimitive));
	}

	public boolean getIncludePopupInPage() {
		return includePopupInPage;
	}

	public void setIncludePopupInPage(boolean includePopupInPage) {
		boolean old = this.includePopupInPage;
		this.includePopupInPage = includePopupInPage;
		setChanged();
		notifyObservers(new AttributeDataModification("includePopupInPage", old, includePopupInPage));
	}

	@Override
	protected Hashtable<String, String> getLocalizableProperties(Hashtable<String, String> props) {
		if (StringUtils.isNotEmpty(getValue()) && getBindingValue() == null) {
			props.put("value", getValue());
		}
		if (StringUtils.isNotEmpty(getConfirmMessage())) {
			props.put("confirmMessage", getConfirmMessage());
		}
		return super.getLocalizableProperties(props);
	}

	public Domain getDomain() {
		if (domain == null && domainName != null) {
			domain = getProject().getDKVModel().getDomainNamed(domainName);
			if (domain == null) {
				setDomainName(null);
			}
		}
		return domain;
	}

	public void setDomain(Domain domain) {
		unregisterDomainObserving();
		Domain old = this.domain;
		this.domain = domain;
		registerDomainObserving();
		setChanged();
		notifyObservers(new IEDataModification("domain", old, domain));
	}

	public String getDomainName() {
		if (getDomain() != null) {
			return getDomain().getName();
		} else {
			return null;
		}
	}

	public void setDomainName(String domainName) {
		String old = this.domainName;
		this.domainName = domainName;
		domain = null;
		setChanged();
		notifyObservers(new IEDataModification("domainName", old, domainName));

	}

	private void registerDomainObserving() {
		if (getDomain() != null) {
			getDomain().addObserver(this);
			getDomain().getKeyList().addObserver(this);
			getDomain().getValueList().addObserver(this);
		}
	}

	/**
     *
     */
	private void unregisterDomainObserving() {
		if (getDomain() != null) {
			getDomain().deleteObserver(this);
			getDomain().getKeyList().deleteObserver(this);
			getDomain().getValueList().deleteObserver(this);
		}
	}

	public boolean isDKVField() {
		return TextFieldType.KEYVALUE.equals(getFieldType());
	}

	public boolean isDateField() {
		return TextFieldType.DATE.equals(getFieldType());
	}

	public boolean isStatusField() {
		return TextFieldType.STATUS_LIST.equals(getFieldType());
	}

	/**
	 * @return Returns the _formatType.
	 */
	public TextFieldType getFieldType() {
		return _fieldType;
	}

	/**
	 * @param type
	 *            The _formatType to set.
	 */
	public void setFieldType(TextFieldType type) {
		TextFieldType oldType = getFieldType();
		if (oldType != type) {
			_fieldType = type;
			setChanged();
			notifyObservers(new WKFAttributeDataModification("fieldType", oldType, type));
		}
	}

	public boolean isCustomButton() {
		return isCustomButton;
	}

	public void setIsCustomButton(boolean isCustomButton) {
		this.isCustomButton = isCustomButton;
		setChanged();
		notifyObservers(new IEDataModification("isCustomButton", !isCustomButton, isCustomButton));
	}

	public boolean getIsClosingPopup() {
		return isClosingPopup;
	}

	public void setIsClosingPopup(boolean value) {
		isClosingPopup = value;
		setChanged();
		notifyObservers(new IEDataModification("isClosingPopup", !isClosingPopup, isClosingPopup));
	}

	/**
	 * Getter method for the attribute isHTML
	 * 
	 * @return Returns the isHTML.
	 */
	public boolean getIsHTML() {
		return isHTML;
	}

	/**
	 * Setter method for the isHTML attribute
	 * 
	 * @param isHTML
	 *            The isHTML to set.
	 */
	public void setIsHTML(boolean isHTML) {
		this.isHTML = isHTML;
		setChanged();
		notifyObservers(new ContentTypeChanged("isHTML", isHTML));
	}

	@Override
	public String getCalculatedLabel() {
		if (getLabel() != null && getLabel().trim().length() > 0) {
			return ensureCalculatedLabelIsAcceptable(getLabel());
		}
		return ensureCalculatedLabelIsAcceptable(getValue());
	}

	public boolean isHyperlink() {
		return true;
	}

	public boolean isImageButton() {
		return false;
	}

	@Override
	public final Vector<IEHyperlinkWidget> getAllButtonInterface() {
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		v.add(this);
		return v;
	}

	public boolean isDateAssistantPopup() {
		return getPopupComponentDefinition() != null && getPopupComponentDefinition().getName().equals("WDLDateAssistant");
	}

	@Override
	public String getNiceName() {
		String niceName = getBeautifiedName();
		if (niceName == null || niceName.trim().length() == 0) {
			niceName = getCalculatedLabel();
		}
		if (niceName == null || niceName.trim().length() == 0) {
			niceName = getFuncName();
		}
		if (niceName == null || niceName.trim().length() == 0) {
			return getWidgetType();
		}
		if (Character.isDigit(niceName.trim().charAt(0))) {
			return "_" + ToolBox.getJavaName(niceName);
		} else {
			return ToolBox.getJavaName(niceName);
		}
	}

	public static Font getButtonFont() {
		return BUTTON_FONT;
	}

	/**
	 * Validation
	 */

	public static class DateAssistantPopupMustBeLinkedWithADateTextField extends
			ValidationRule<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget> {
		public DateAssistantPopupMustBeLinkedWithADateTextField() {
			super(IEHyperlinkWidget.class, "date_assistant_popup_must_be_linked_with_a_date_text_field");
		}

		@Override
		public ValidationIssue<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget> applyValidation(IEHyperlinkWidget button) {
			if (button.isDateAssistantPopup() && (button.getDateTextfield() == null || button.getDateTextfield().getParent() == null)) {
				ValidationError<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget> error = new ValidationError<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget>(
						this, button, "date_assistant_popup_must_be_linked_with_a_date_text_field");

				IETextFieldWidget potential = findPotentialDateTextField(button);
				if (potential != null) {
					error.addToFixProposals(new LinkToDateTextField(potential, button));
				} else {
					potential = findHypotheticDateTextField(button);
					if (potential != null) {
						error.addToFixProposals(new ChangeFieldTypeAndLinkToDateTextField(potential, button));
					}
				}

				return error;
			}
			return null;
		}

		IETextFieldWidget findHypotheticDateTextField(IEHyperlinkWidget bt) {
			if (bt.getParent() instanceof IESequenceWidget) {
				IESequenceWidget seq = (IESequenceWidget) bt.getParent();
				Object prev = seq.getPrevious(bt);
				if (prev != null) {
					if (prev instanceof IETextFieldWidget) {
						return (IETextFieldWidget) prev;
					}
				} else {
					IETDWidget td = seq.td();
					if (td != null) {
						IETDWidget previousTD = (IETDWidget) td.getParent().getPrevious(td);
						if (previousTD != null) {
							if (previousTD.getSequenceWidget().size() == 1) {
								Object pot = previousTD.getSequenceWidget().get(0);
								if (pot instanceof IETextFieldWidget) {
									return (IETextFieldWidget) pot;
								}
							}
						}
					}
				}
			}
			return null;
		}

		IETextFieldWidget findPotentialDateTextField(IEHyperlinkWidget bt) {
			if (bt.getParent() instanceof IESequenceWidget) {
				IESequenceWidget seq = (IESequenceWidget) bt.getParent();
				Object prev = seq.getPrevious(bt);
				if (prev != null) {
					if (prev instanceof IETextFieldWidget && ((IETextFieldWidget) prev).getFieldType() == TextFieldType.DATE) {
						return (IETextFieldWidget) prev;
					}
				} else {
					IETDWidget td = seq.td();
					if (td != null) {
						IETDWidget previousTD = (IETDWidget) td.getParent().getPrevious(td);
						if (previousTD != null) {
							if (previousTD.getSequenceWidget().size() == 1) {
								Object pot = previousTD.getSequenceWidget().get(0);
								if (pot instanceof IETextFieldWidget && ((IETextFieldWidget) pot).getFieldType() == TextFieldType.DATE) {
									return (IETextFieldWidget) pot;
								}
							}
						}
					}
				}
			}
			return null;
		}
	}

	public static class ChangeFieldTypeAndLinkToDateTextField extends
			FixProposal<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget> {
		public IETextFieldWidget textField;

		public IEHyperlinkWidget button;

		public ChangeFieldTypeAndLinkToDateTextField(IETextFieldWidget tf, IEHyperlinkWidget bt) {
			super("change_field_type_of_previous_text_field_and_link_date_assistant_with_it");
			textField = tf;
			button = bt;
		}

		@Override
		protected void fixAction() {
			textField.setFieldType(TextFieldType.DATE);
			button.setDateTextfield(textField);
		}
	}

	public static class LinkToDateTextField extends FixProposal<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget> {
		public IETextFieldWidget textField;

		public IEHyperlinkWidget button;

		public LinkToDateTextField(IETextFieldWidget tf, IEHyperlinkWidget bt) {
			super("link_date_assistant_popup_to_previous_date_textfield");
			textField = tf;
			button = bt;
		}

		@Override
		protected void fixAction() {
			button.setDateTextfield(textField);
		}
	}

	public static class SetLinkTypeSearch extends FixProposal<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget> {
		public IEHyperlinkWidget button;

		public SetLinkTypeSearch(IEHyperlinkWidget bt) {
			super("set_link_type_search");
			button = bt;
		}

		@Override
		protected void fixAction() {
			button.setHyperlinkType(HyperlinkType.SEARCH);
		}
	}

	public static class SetLinkTypeMailto extends FixProposal<DateAssistantPopupMustBeLinkedWithADateTextField, IEHyperlinkWidget> {
		public IEHyperlinkWidget button;

		public SetLinkTypeMailto(IEHyperlinkWidget bt) {
			super("set_link_type_mailto");
			button = bt;
		}

		@Override
		protected void fixAction() {
			button.setHyperlinkType(HyperlinkType.MAILTO);
		}
	}

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		String old = this.helpMessage;
		this.helpMessage = helpMessage;
		setChanged();
		notifyModification("helpMessage", old, helpMessage);
	}

	@Override
	public String getProcessInstanceDictionaryKey() {
		if (isStatusField()) {
			return FlexoProcess.PROCESSINSTANCE_STATUS_KEY;
		}
		return super.getProcessInstanceDictionaryKey();
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithMainBinding#getMainBinding()
	 */
	@Override
	public AbstractBinding getMainBinding() {
		return getBindingValue();
	}
}
