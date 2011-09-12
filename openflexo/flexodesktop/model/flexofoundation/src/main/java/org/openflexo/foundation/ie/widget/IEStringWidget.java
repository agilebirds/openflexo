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

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.DomainDeleted;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.ContentTypeChanged;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.ListOfValuesHasChanged;
import org.openflexo.foundation.ie.util.TextCSSClass;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Represents a 'String' widget
 *
 * @author bmangez
 */
public class IEStringWidget extends IENonEditableTextWidget implements IEWidgetWithValueList {

    /**
     *
     */
    public static final String STRING_WIDGET = "string_widget";

    private String _prototypeValues;

    private String _keyPath;

    private boolean isHTML = true;

    private String _bindingValueWhenEmpty;

    private TextFieldType _fieldType;

    private boolean _isHyperlink = false;

    private Domain domain;

    private String domainName;

	public IEStringWidget(FlexoComponentBuilder builder) {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

	public IEStringWidget(IEWOComponent woComponent, IEObject parent,
			FlexoProject prj) {
        super(woComponent, parent, prj);
        this.setTextCSSClass(TextCSSClass.BLOC_BODY_CONTENT);
    }

	@Override
	public String getDefaultInspectorName() {
        return "String.inspector";
    }

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		registerDomainObserving();
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

	public String getKeyPath() {
        return _keyPath;
    }

	public void setKeyPath(String path) {
        _keyPath = path;
        setChanged();
        notifyObservers(new IEDataModification("keyPath", null, _keyPath));
    }

	public String getPrototypeValues() {
        return _prototypeValues;
    }

	public void setPrototypeValues(String values) {
        _prototypeValues = values;
        setChanged();
		notifyObservers(new IEDataModification("prototypeValues", null,
				_prototypeValues));
    }

	public boolean getIsHyperlink() {
        return _isHyperlink;
    }

	public void setIsHyperlink(boolean hyperlink) {
        _isHyperlink = hyperlink;
        setChanged();
		notifyObservers(new IEDataModification("isHyperlink", null,
				new Boolean(_isHyperlink)));
    }

	@Override
	public String getDefaultValue() {
		return "dynamic text";
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
        _fieldType = type;
        setChanged();
        notifyObservers(new IEDataModification("fieldType", null, type));
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
		notifyObservers(new IEDataModification("bindingValueWhenEmpty", null,
				_bindingValueWhenEmpty));
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
        notifyObservers(new ContentTypeChanged("isHTML",isHTML));
    }

    /**
     * Return a Vector of embedded IEObjects at this level. NOTE that this is
     * NOT a recursive method
     *
     * @return a Vector of IEObject instances
     */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
        return EMPTY_IOBJECT_VECTOR;
    }

	@Override
	public String getFullyQualifiedName() {
        return "String";
    }

	@Override
	public WidgetBindingDefinition getBindingValueDefinition() {
        if (getFieldType() != null && getFieldType() == TextFieldType.DATE) {
			return WidgetBindingDefinition.get(this, "bindingValue",
					Date.class, BindingDefinitionType.GET, false);
        }
        if (getFieldType() != null && getFieldType() == TextFieldType.INTEGER) {
			return WidgetBindingDefinition.get(this, "bindingValue",
					Number.class, BindingDefinitionType.GET, false);
        }
        if (getFieldType() != null && getFieldType() == TextFieldType.FLOAT) {
			return WidgetBindingDefinition.get(this, "bindingValue",
					Float.class, BindingDefinitionType.GET, false);
        }
        return super.getBindingValueDefinition();
    }

    /**
     * Overrides getClassNameKey
     *
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
	@Override
	public String getClassNameKey() {
        return STRING_WIDGET;
    }

	public Domain getDomain() {
        if (domain == null && domainName != null) {
            domain = getProject().getDKVModel().getDomainNamed(domainName);
            if (domain == null)
                setDomainName(null);
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
        if (getDomain() != null)
            return getDomain().getName();
        else
            return null;
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

	@Override
	public void update(FlexoObservable observable, DataModification obj) {
		if (obj instanceof DomainDeleted
				&& getDomain() == ((DomainDeleted) obj).oldValue()) {
            setDomain(null);
        } else if (obj instanceof DKVDataModification) {
            setChanged();
			notifyObservers(new ListOfValuesHasChanged(new Object(), new Object()));
        } else
            super.update(observable, obj);
    }

	@Override
	public String getProcessInstanceDictionaryKey()
	{
		if(isStatusField())
			return FlexoProcess.PROCESSINSTANCE_STATUS_KEY;
		return super.getProcessInstanceDictionaryKey();
	}
	
	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList()
	 */
	@Override
	public List<Object> getValueList()
	{
		return getValueList(null);
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList(org.openflexo.foundation.wkf.FlexoProcess)
	 */
	@Override
	public List<Object> getValueList(FlexoProcess process) 
    {
    	return parseValueListToAppropriateType(getValue(), getPrototypeValues(), getFieldType(), getDomain(), process);
    }
}
