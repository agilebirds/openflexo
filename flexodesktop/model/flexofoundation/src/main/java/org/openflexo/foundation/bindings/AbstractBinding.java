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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;


public abstract class AbstractBinding extends FlexoModelObject implements Bindable, DataFlexoObserver, Cloneable, StringConvertable<AbstractBinding>, Serializable {

	static final Logger logger = Logger.getLogger(BindingValue.class.getPackage().getName());

	protected FlexoModelObject _owner;
	private BindingDefinition _bindingDefinition;
	protected String unparsableValue = null;

    public AbstractBinding()
    {
        super(null);
      }
    
    public AbstractBinding(BindingDefinition bindingDefinition, FlexoModelObject owner)
    {
        super(owner != null ? owner.getProject() : null);
        _owner = owner;
        setBindingDefinition(bindingDefinition);
    }


	public abstract String getStringRepresentation();

	public abstract String getCodeStringRepresentation();
	public abstract String getWodStringRepresentation();

	@Override
	public abstract String getClassNameKey();

    public abstract String getJavaCodeStringRepresentation();
    
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

	@Override
	public abstract String getFullyQualifiedName();

    public BindingDefinition getBindingDefinition()
    {
        return _bindingDefinition;
    }

    public final void setBindingDefinition(BindingDefinition bindingDefinition)
    {
        BindingDefinition oldBindingDefinition = getBindingDefinition();
        if (oldBindingDefinition != bindingDefinition) {
            _bindingDefinition = bindingDefinition;
            _applyNewBindingDefinition();
            if (logger.isLoggable(Level.FINE))
				logger.fine("Binding "+this+" received "+bindingDefinition+" as BindingDefinition");
        }
    }

    protected abstract void _applyNewBindingDefinition();
    
    // ==========================================================
    // ================= Serialization stuff ====================
    // ==========================================================

    public AbstractBinding getBindingFromString(String aValue)
    {
    	return getConverter().convertFromString(aValue);
    }
    

    public static class AbstractBindingStringConverter<T extends AbstractBinding> extends StringEncoder.Converter<T>
    {
    	private FlexoProject _project;
    	boolean warnOnFailure = true;
    	
    	public AbstractBindingStringConverter(Class<T> aClass) {
			super(aClass);
		}
    	
        public AbstractBindingStringConverter(Class<T> aClass, FlexoProject project)
        {
            super(aClass);
            _project = project;
        }
        
        public void setBindable(Bindable bindable)
        {
        	_project.getBindingValueConverter().setBindable(bindable);
        	_project.getBindingExpressionConverter().setBindable(bindable);
           	_project.getStaticBindingConverter().setBindable(bindable);
          	_project.getTranstypedBindingStringConverter().setBindable(bindable);
        }

        public void setWarnOnFailure(boolean aFlag)
        {
        	if (_project != null && this == _project.getBindingValueConverter()) {
        		logger.info("Sets warnOnFailure to be "+aFlag);
        	}
        	warnOnFailure = aFlag;
        	if (_project != null && this == _project.getAbstractBindingConverter()) {
        		if (_project.getBindingValueConverter() != this) _project.getBindingValueConverter().setWarnOnFailure(aFlag);
        		if (_project.getBindingExpressionConverter() != this) _project.getBindingExpressionConverter().setWarnOnFailure(aFlag);
           		if (_project.getStaticBindingConverter() != this) _project.getStaticBindingConverter().setWarnOnFailure(aFlag);
           		if (_project.getTranstypedBindingStringConverter() != this) _project.getTranstypedBindingStringConverter().setWarnOnFailure(aFlag);
        	}
        }
        
        @Override
        public T convertFromString(String value) 
        {
        	if (logger.isLoggable(Level.FINE))
        		logger.fine("Decoding abstract binding: "+value);
        	if ("null".equals(value))
        		return null;
        	StaticBinding decodedStringAsStaticBinding = _project.getStaticBindingConverter().convertFromString(value);
        	if (decodedStringAsStaticBinding != null) {
        		if (logger.isLoggable(Level.FINE))
        			logger.fine("Succeeded to decode as a StaticBinding");
        		return (T)decodedStringAsStaticBinding;
        	}
        	else {
        		// Lets try as a binding value
        		_project.getBindingValueConverter().setWarnOnFailure(false);
        		BindingValue decodedStringAsBindingValue = _project.getBindingValueConverter().convertFromString(value);
        		_project.getBindingValueConverter().setWarnOnFailure(true);
        		if (decodedStringAsBindingValue != null) {
        			if (logger.isLoggable(Level.FINE))
        				logger.fine("Succeeded to decode as a BindingValue");
        			return (T)decodedStringAsBindingValue;
        		}				
        		else {
               		// Lets try as a transtyped binding
            		_project.getTranstypedBindingStringConverter().setWarnOnFailure(false);
            		TranstypedBinding decodedStringAsTranstypedBinding = _project.getTranstypedBindingStringConverter().convertFromString(value);
            		_project.getTranstypedBindingStringConverter().setWarnOnFailure(true);
            		if (decodedStringAsTranstypedBinding != null) {
            			if (logger.isLoggable(Level.FINE))
            				logger.fine("Succeeded to decode as a TranstypedBinding");
            			return (T)decodedStringAsTranstypedBinding;
            		}				
            		else {
            			// Lets try as an expression
            			BindingExpression decodedStringAsBindingExpression = _project.getBindingExpressionConverter().convertFromString(value);
            			if (logger.isLoggable(Level.FINE))
            				logger.fine("Cound not decode as a BindingValue, trying as an expression");
            			return (T)decodedStringAsBindingExpression;
            		}
       		}
        	}
        }

		@Override
		public String convertToString(T value) 
		{
			if (value instanceof BindingValue) return _project.getBindingValueConverter().convertToString((BindingValue)value);
			if (value instanceof BindingExpression) return _project.getBindingExpressionConverter().convertToString((BindingExpression)value);
			if (value instanceof StaticBinding) return _project.getStaticBindingConverter().convertToString((StaticBinding)value);
			if (value instanceof TranstypedBinding) return _project.getTranstypedBindingStringConverter().convertToString((TranstypedBinding)value);
			return "???";
		}

 
    }

    @Override
	public AbstractBindingStringConverter<? extends AbstractBinding> getConverter()
    {
        if (getProject()!=null)
            return getProject().getAbstractBindingConverter();
        return null;
    }

    public void setsWith(AbstractBinding aValue)
    {
        if (aValue != null) {
            _owner = aValue._owner;
            _bindingDefinition = aValue.getBindingDefinition();
        }

    }

    public abstract DMType getAccessedType();

    public abstract boolean isBindingValid();

    public abstract boolean isStaticValue();

    @Override
	public abstract AbstractBinding clone();
    
    @Override
	public boolean equals(Object object)
    {
        if (object == null)
            return false;
        if (object instanceof AbstractBinding) {
        	AbstractBinding bv = (AbstractBinding) object;
            if (getBindingDefinition() == null) {
                if (bv.getBindingDefinition() != null)
                    return false;
            } else {
                if (!getBindingDefinition().equals(bv.getBindingDefinition()))
                    return false;
            }
            return ((_owner == bv._owner) && (getStringRepresentation().equals(bv
                    .getStringRepresentation())));
        } else {
            return super.equals(object);
        }
    }

	public String getUnparsableValue() 
	{
		return unparsableValue;
	}

	public void setUnparsableValue(String unparsableString) 
	{
		this.unparsableValue = unparsableString;
	}


}

