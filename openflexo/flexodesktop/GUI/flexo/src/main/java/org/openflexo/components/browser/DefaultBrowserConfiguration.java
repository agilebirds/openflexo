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
package org.openflexo.components.browser;

import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;




public class DefaultBrowserConfiguration implements BrowserConfiguration {

	public static interface ObjectVisibilityDelegate
	{
		public BrowserFilterStatus getVisibility(BrowserElementType elementType);
	}
	
	private FlexoModelObject _rootObject;
	private ObjectVisibilityDelegate _visibilityDelegate;
	
    public DefaultBrowserConfiguration(FlexoModelObject rootObject, ObjectVisibilityDelegate visibilityDelegate)
    {
    	_rootObject = rootObject;
    	_visibilityDelegate = visibilityDelegate;
    }

	@Override
	public void configure(ProjectBrowser browser)
	{
		for (BrowserElementType elementType : BrowserElementType.values()) {
			browser.setFilterStatus(elementType,_visibilityDelegate.getVisibility(elementType));
		}
	}

	@Override
	public BrowserElementFactory getBrowserElementFactory()
	{
		 return DefaultBrowserElementFactory.DEFAULT_FACTORY; 
	}

	@Override
	public FlexoModelObject getDefaultRootObject()
	{
		return _rootObject;
	}

	public void setDefaultRootObject(FlexoModelObject rootObject) 
	{
		_rootObject = rootObject;
	}
  
	@Override
	public FlexoProject getProject() 
	{
		if (_rootObject != null)
			return _rootObject.getProject();
		return null;
	}

}
