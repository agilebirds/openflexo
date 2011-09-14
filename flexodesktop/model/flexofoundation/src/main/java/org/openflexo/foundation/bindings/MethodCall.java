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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.Typed;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.xmlcode.XMLMapping;


public class MethodCall extends FlexoModelObject implements Typed, BindingValue.BindingPathElement {

	static final Logger logger = Logger.getLogger(MethodCall.class.getPackage().getName());

    protected AbstractBinding _owner;
    private DMMethod _method;
    private Vector<MethodCallArgument> _args;
    
    public MethodCall(AbstractBinding owner)
    {
        super(owner.getProject());
        _owner = owner;
        _method = null;
        _args = new Vector<MethodCallArgument>();
    }
    
    public MethodCall(AbstractBinding owner, DMMethod method)
    {
        this(owner);
        setMethod(method);
    }
    
    @Override
	public DMType getResultingType()
    {
    	return getType();
    }
    
     @Override
	public DMType getType()
    {
        if (_method != null) {
            return _method.getReturnType();
        }
        return null;
    }

    @Override
	public void setType(DMType type) 
    {
        // Not allowed
    }

    @Override
	public DMEntity getEntity() 
    {
        if (_method != null) {
            return _method.getEntity();
        }
        return null;
    }

    @Override
	public String getSerializationRepresentation() 
    {
        if (_method == null) return "null";
        String returned = _method.getName();
        returned += "(";
        boolean isFirst = true;
        if (_method.getParameters() != null)
			for (MethodCallArgument arg : _args) {
				returned += (isFirst ? "" : ",")
				+ (arg.getBinding()!=null?arg.getBinding().getStringRepresentation():"");
				isFirst = false;
			}
        returned += ")";
        return returned;
     }
    
    public String getJavaCodeStringRepresentation() 
    {
        if (_method == null) return "null";
        String returned = _method.getName();
        returned += "(";
        boolean isFirst = true;
        if (_method.getParameters() != null)
			for (MethodCallArgument arg : _args) {
				returned += (isFirst ? "" : ",")
				+ (arg.getBinding()!=null?arg.getBinding().getJavaCodeStringRepresentation():"");
				isFirst = false;
			}
        returned += ")";
        return returned;
     }

    public DMMethod getMethod() 
    {
        return _method;
    }

    public void setMethod(DMMethod method) 
    {
        if (method != _method) {
            _method = method;
            _args.clear();
            for (DMMethodParameter param : method.getParameters()) {
            	_args.add(new MethodCallArgument(param));
             }
        }
    }

    public class MethodCallParamBindingDefinition extends BindingDefinition
    {
        private DMMethodParameter _param;
        
        public MethodCallParamBindingDefinition(DMMethodParameter param)
        {
            super(param.getName(),param.getType(),_owner,BindingDefinitionType.GET,true);
            _param = param;
        }

        public DMMethodParameter getParam() 
        {
            return _param;
        }
    }
    
    @Override
	public String getClassNameKey()
    {
        return "method_call";
    }

    @Override
	public String getFullyQualifiedName() 
    {
        return "METHOD_CALL." + getSerializationRepresentation();
    }

    @Override
	public FlexoProject getProject() 
    {
        if (_owner != null) return _owner.getProject();
        return null;
    }

    @Override
	public XMLMapping getXMLMapping()
    {
        if (_owner != null)
            return _owner.getXMLMapping();
        return null;
    }

    @Override
	public XMLStorageResourceData getXMLResourceData()
    {
        if (_owner != null)
            return _owner.getXMLResourceData();
        return null;
    }

    public Vector<MethodCallArgument> getArgs() 
    {
        return _args;
    }

    /*public AbstractBinding getBindingValueForParam (DMMethod.DMMethodParameter param)
    {
        return _args.get(param);
    }*/

    public MethodCallArgument argumentForParam(DMMethodParameter param)
    {
    	for (MethodCallArgument arg : _args) {
    		if (arg.getParam() == param) return arg;
    	}
    	return null;
    }
    
    public void setBindingValueForParam (AbstractBinding binding, DMMethodParameter param)
    {
    	MethodCallArgument arg = argumentForParam(param);
    	
    	if (arg == null) {
    		logger.warning("Could not find argument matching param "+param);
    		return;
    	}
    	else {
    		binding.setOwner(_owner);
    		binding.setBindingDefinition(arg.getBindingDefinition());
    		arg.setBinding(binding);
    	}   	
     }

    @Override
	public boolean isBindingValid()
    {
    	for (MethodCallArgument arg : _args) {
             if (arg.getBinding() == null || !arg.getBinding().isBindingValid()) return false;
        }
        return true;
    }

    /**
     * Overrides getCardinality
     * @see org.openflexo.foundation.bindings.BindingValue.BindingPathElement#getCardinality()
     */
    /*public DMCardinality getCardinality()
    {
        if (getType()!=null && (getType().getName().equals("Vector") || getType().getName().equals("NSArray")))
            return DMCardinality.VECTOR;
        return DMCardinality.SINGLE;
    }*/

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
    	if (dataModification instanceof DMEntityClassNameChanged && observable == getType().getBaseEntity()) {
    		// do nothing : no cached code
      	}
    	else if (dataModification instanceof EntityDeleted && observable == getType().getBaseEntity()) {
    		// do nothing : no cached code
    	}
     }
   
    public class MethodCallArgument extends TemporaryFlexoModelObject implements Typed
    {
    	private DMMethodParameter param;
    	private MethodCallParamBindingDefinition bindingDefinition;
    	private AbstractBinding binding;
    	
    	protected MethodCallArgument(DMMethodParameter aParam)
    	{
    		param = aParam;
    		bindingDefinition = new MethodCallParamBindingDefinition(param);
    		binding = null;
    	}

		@Override
		public DMType getType() 
		{
			return bindingDefinition.getType();
		}

		@Override
		public void setType(DMType type) 
		{
			// Not applicable
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) 
		{
			// TODO: implements type updating !!!!
		}

		public AbstractBinding getBinding()
		{
			return binding;
		}

		public void setBinding(AbstractBinding aBinding) 
		{
			this.binding = aBinding;
		}

		public MethodCallParamBindingDefinition getBindingDefinition() 
		{
			return bindingDefinition;
		}

		public DMMethodParameter getParam() 
		{
			return param;
		}
		@Override
		public String toString() {
			return "MethodCallArg:"+param.getName()+"/"+Integer.toHexString(hashCode());
		}
    }

}
