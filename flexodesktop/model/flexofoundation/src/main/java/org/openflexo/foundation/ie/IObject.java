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
package org.openflexo.foundation.ie;

import java.util.Observable;
import java.util.Vector;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.xml.FlexoXMLSerializable;
import org.openflexo.xmlcode.XMLMapping;

/**
 * 
 * @author gpolet
 * 
 */
public interface IObject extends FlexoXMLSerializable, Validable {
	@Override
	public XMLMapping getXMLMapping();

	public void update(Observable observable, Object obj);

	public void update(FlexoObservable observable, DataModification obj);

	public abstract FlexoProject getProject();

	@Override
	public boolean isDeleted();

	// ==========================================================================
	// ============================== KeyValueCoding
	// ============================
	// ==========================================================================

	/**
	 * Overrides the default KV-coding implementation by using dynamic properties if the searched properties could not be resolved for this
	 * instance.<b>
	 * 
	 * Overrides
	 * 
	 * @see org.openflexo.kvc.KeyValueCoding#objectForKey(java.lang.String)
	 * @see org.openflexo.kvc.KeyValueCoding#objectForKey(java.lang.String)
	 */
	public Object objectForKey(String key);

	/**
	 * Overrides the default KV-coding implementation by using dynamic properties if the searched properties could not be resolved for this
	 * instance.<b> Additionaly send notifications about modified attribute
	 * 
	 * Overrides
	 * 
	 * @see org.openflexo.kvc.KeyValueCoding#setObjectForKey(java.lang.Object, java.lang.String)
	 * @see org.openflexo.kvc.KeyValueCoding#setObjectForKey(java.lang.Object, java.lang.String)
	 */
	public void setObjectForKey(Object object, String key);

	public Class getTypeForKey(String key);

	@Override
	public ValidationModel getDefaultValidationModel();

	/**
	 * Return a Vector of all embedded IEObjects: recursive method (Note must include itself in this vector)
	 * 
	 * @return a Vector of IEObject instances
	 */
	public Vector getAllEmbeddedIEObjects();

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	public abstract Vector<IObject> getEmbeddedIEObjects();

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects();

	public Vector<FlexoModelObjectReference<?>> getReferencers();

}
