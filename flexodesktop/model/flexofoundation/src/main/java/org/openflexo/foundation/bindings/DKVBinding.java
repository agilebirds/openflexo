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

import java.util.logging.Logger;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dm.DMType;


public class DKVBinding extends StaticBinding<Key> {

    @SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(DKVBinding.class.getPackage().getName());

	public DKVBinding()
	{
		super();
	}
	
    public DKVBinding(BindingDefinition bindingDefinition, FlexoModelObject owner)
    {
    	super(bindingDefinition,owner);
    }

    public DKVBinding(BindingDefinition bindingDefinition, FlexoModelObject owner, Key aValue)
    {
    	this(bindingDefinition,owner);
    	setValue(aValue);
     }

 	@Override
	public String getCodeStringRepresentation()
	{
		return getStringRepresentation();
	}
	
 	@Override
	public String getWodStringRepresentation() {
		logger.severe("dkv in wod files isn't supported yet");
		return "\"dkv in wod files isn't supported yet\"";
	}
 	
	@Override
	public String getClassNameKey() {
		return "dkv_binding";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "DKV_BINDING=" + getSerializationRepresentation();
	}

	@Override
	public String getSerializationRepresentation() 
	{
		return "$"+getStringRepresentation();
	}
	
	@Override
	protected void _applyNewBindingDefinition() {
		// TODO Auto-generated method stub	
	}
	
    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
    	// TODO to be implemented
     }


	@Override
	public EvaluationType getEvaluationType()
	{
		return EvaluationType.ENUM;
	}

	private Key value;
	
	@Override
	public Key getValue() 
	{
		return value;
	}

	@Override
	public void setValue(Key aValue) 
	{
		value = aValue;
		accessedType = null;
	}

    @Override
	public DMType getAccessedType()
    {
     	if (getValue() != null && accessedType == null) {
     		accessedType = DMType.makeDKVDMType(getValue().getDomain());
    	}
    	return accessedType;
    }

	@Override
	public DKVBinding clone()
	{
		DKVBinding returned = new DKVBinding();
		returned.setsWith(this);
		return returned;
	}
 
    @Override
    public String getJavaCodeStringRepresentation()
    {
    	return getStringRepresentation() + "/* TODO: implement DKV */";
    }

	@Override
	public Class<Key> getStaticBindingClass() {
		return Key.class;
	}

	@Override
	public String getStringRepresentation() 
	{
		return DMType.DKV_PREFIX+(getValue()==null?"null":(getValue().getDomain()==null?"null":getValue().getDomain().getName())+"."+getValue().getName());
	}
}
