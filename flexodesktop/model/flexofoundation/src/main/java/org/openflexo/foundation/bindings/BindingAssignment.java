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
package org.openflexo.foundation.bindings;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.Expression;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;



public class BindingAssignment extends FlexoModelObject implements Bindable, InspectableObject, StringConvertable
{
	static final Logger logger = Logger.getLogger(BindingAssignment.class.getPackage().getName());

	protected FlexoModelObject _owner;
	private DMType _defaultType = DMType.makeWildcardDMType(null, null);
	private ReceiverBindingDefinition _receiverBD;
	private ValueBindingDefinition _valueBD;
	private BindingValue _receiver;
	private AbstractBinding _value;
	
    public BindingAssignment(FlexoModelObject owner)
    {
    	super();
    	_owner = owner;
    	_receiverBD = new ReceiverBindingDefinition(owner);
    	_valueBD = new ValueBindingDefinition(owner);
    }

    public BindingAssignment(BindingValue receiver, AbstractBinding value, FlexoModelObject owner)
    {
    	this(owner);
    	setReceiver(receiver);
    	setValue(value);
    }

	@Override
	public String getClassNameKey() 
	{
		return "binding_assignment";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "ASSIGNMENT:" 
		+ (getReceiver()!=null?getReceiver().getStringRepresentation():null)
		+ "="
		+ (getValue()!=null?getValue().getStringRepresentation():null);
	}

	@Override
	public final FlexoProject getProject()
	{
		if (_owner != null)
			return _owner.getProject();
		return null;
	}

	@Override
	public final XMLMapping getXMLMapping()
	{
		if (_owner != null)
			return _owner.getXMLMapping();
		return null;
	}

	@Override
	public final XMLStorageResourceData getXMLResourceData()
	{
		if (_owner != null)
			return _owner.getXMLResourceData();
		return null;
	}

	public final FlexoModelObject getOwner()
	{
		return _owner;
	}

	public void setOwner(FlexoModelObject owner)
	{
		_owner = owner;
       if (_owner == null) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Null owner declared for BindingValue");
        } else if (!(_owner instanceof Bindable)) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Declared owner is not a Bindable !");
        }
	}

	@Override
	public final BindingModel getBindingModel()
	{
		if (_owner != null) {
			return ((Bindable) _owner).getBindingModel();
		}
		return null;
	}

	@Override
	public final String toString()
	{
		return getFullyQualifiedName();
	}


    public BindingValue getReceiver()
    {
    	return _receiver;
    }

    public void setReceiver(BindingValue aReceiver)
    {
    	BindingValue oldValue = _receiver;
    	if (aReceiver != null) {
    		aReceiver.setBindingDefinition(getReceiverBindingDefinition());
    		aReceiver.setOwner(this);
    	}
    	_receiver = aReceiver;
    	setChanged();
    	notifyObservers(new WKFAttributeDataModification("receiver",oldValue,aReceiver));
    }

    public AbstractBinding getValue()
    {
    	return _value;
    }

    public void setValue(AbstractBinding aValue)
    {
    	AbstractBinding oldValue = _value;
    	if (aValue != null) {
    		aValue.setBindingDefinition(getValueBindingDefinition());
    		aValue.setOwner(this);
    	}
    	_value = aValue;
    	setChanged();
    	notifyObservers(new WKFAttributeDataModification("value",oldValue,aValue));
    }

    public DMType getType() 
    {
    	if (getReceiver() != null && getReceiver().getAccessedType() != null) 
    		return getReceiver().getAccessedType();
    	return _defaultType;
    }


    public ReceiverBindingDefinition getReceiverBindingDefinition() 
    {
		return _receiverBD;
	}

	public ValueBindingDefinition getValueBindingDefinition() 
	{
		return _valueBD;
	}
	
	private static final String ASSIGN_LABEL = "=";
	
	public String getAssignLabel()
	{
		return ASSIGN_LABEL;
	}


    protected class ReceiverBindingDefinition extends BindingDefinition
    {
    	public ReceiverBindingDefinition() 
    	{
    		this(null);
    	}

    	public ReceiverBindingDefinition(FlexoModelObject owner) 
    	{
    		super("receiver",null,owner,BindingDefinitionType.SET,true);
    	}

    	@Override
    	public DMType getType() {
     		return BindingAssignment.this.getType();
    	}
    	
    	@Override
    	public FlexoModelObject getOwner() {
       		return BindingAssignment.this.getOwner();
    	}
    	
    	@Override
    	public FlexoProject getProject() {
     		return BindingAssignment.this.getProject();
    	}
    }

    protected class ValueBindingDefinition extends BindingDefinition
    {
       	public ValueBindingDefinition() 
    	{
    		this(null);
    	}

    	public ValueBindingDefinition(FlexoModelObject owner) 
    	{
    		super("value",null,owner,BindingDefinitionType.GET,true);
    	}

   	
    	@Override
    	public DMType getType() {
     		return BindingAssignment.this.getType();
    	}
    	
    	@Override
    	public FlexoModelObject getOwner() {
       		return BindingAssignment.this.getOwner();
    	}

    	@Override
    	public FlexoProject getProject() {
     		return BindingAssignment.this.getProject();
    	}
    }

	@Override
	public String getInspectorName() 
	{
		// never inspected by its own
		return null;
	}
	
    public static class BindingAssignmentStringConverter extends StringEncoder.Converter<BindingAssignment>
    {
    	private FlexoProject _project;
    	private Bindable _bindable;
     	
        public BindingAssignmentStringConverter(FlexoProject project)
        {
            super(BindingAssignment.class);
            _project = project;
        }
        
        public Bindable getBindable()
        {
            return _bindable;
        }

        public void setBindable(Bindable bindable)
        {
        	_bindable = bindable;
        	_project.getStaticBindingConverter().setBindable(bindable);
           	_project.getTranstypedBindingStringConverter().setBindable(bindable);
        	_project.getBindingValueConverter().setBindable(bindable);
        	_project.getBindingExpressionConverter().setBindable(bindable);
        }

        @Override
        public BindingAssignment convertFromString(String aString) 
        {
        	BindingExpression decodedExpression = _project.getBindingExpressionConverter().convertFromString(aString);

        	if (decodedExpression != null) {
        		if ((decodedExpression.getExpression()!= null)
        				&& (decodedExpression.getExpression() instanceof BinaryOperatorExpression)
        				&& (((BinaryOperatorExpression)decodedExpression.getExpression()).getOperator() == BooleanBinaryOperator.EQUALS)) {
        			Expression left = ((BinaryOperatorExpression)decodedExpression.getExpression()).getLeftArgument();
        			Expression right = ((BinaryOperatorExpression)decodedExpression.getExpression()).getRightArgument();
        			BindingValue receiver = _project.getBindingValueConverter().convertFromString(left.toString());
        			AbstractBinding value = _project.getAbstractBindingConverter().convertFromString(right.toString());
        			return new BindingAssignment(receiver,value,(FlexoModelObject)_bindable);
        		}
        	}
        	logger.warning("Could not decode "+aString+" as BindingAssignment");
        	return null;
        }

        @Override
        public String convertToString(BindingAssignment bindingAssignment) 
        {
        	BindingValue receiver = bindingAssignment.getReceiver();
        	AbstractBinding value = bindingAssignment.getValue();
        	return (receiver!=null?_project.getAbstractBindingConverter().convertToString(receiver):"null")
        	+"="
        	+(value!=null?_project.getAbstractBindingConverter().convertToString(value):"null");
        }

 
    }

    public String getStringRepresentation()
    {
       	return (getReceiver()!=null?getProject().getAbstractBindingConverter().convertToString(getReceiver()):"null")
    	+"="+(getValue()!=null?getProject().getAbstractBindingConverter().convertToString(getValue()):"null");
    }
    
	public String getJavaStringRepresentation() 
	{
		return getReceiver().getSetterJavaCodeStringRepresentation(getValue())+";";
	}

    @Override
	public BindingAssignmentStringConverter getConverter()
    {
        if (getProject()!=null)
            return getProject().getBindingAssignementConverter();
        return null;
    }

}
