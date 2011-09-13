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

import java.util.logging.Logger;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class IEDynamicImage extends IEButtonWidget {

	/**
     * 
     */
    public static final String DYNAMIC_IMAGE_WIDGET = "dynamic_image_widget";

    @SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(IEDynamicImage.class.getPackage().getName());

	protected AbstractBinding _bindingImageUrl;
	
	public IEDynamicImage(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }
	
	public IEDynamicImage(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		setWidthPixel(100);
		setHeightPixel(100);
	}
	
	@Override
	public String getBeautifiedName() {
		String s = super.getBeautifiedName();
		if (s==null || s.trim().length()==0)
			return "A dynamic image";
		return s;
	}
	
	@Override
	public boolean getUsePercentage() {
		return false;
	}
	
	@Override
	public boolean getMaintainAspectRatio() {
		return false;
	}

	@Override
	public String getDefaultInspectorName() {
		return "Image.inspector";
	}

	@Override
	public String getClassNameKey() {
		return DYNAMIC_IMAGE_WIDGET;
	}

	@Override
	public String getFullyQualifiedName() {
		return "DYNAMIC_IMAGE."+getName();
	}

	public AbstractBinding getBindingImageUrl()
    {
        if (isBeingCloned())
            return null;
        return _bindingImageUrl;
    }

    public void setBindingImageUrl(AbstractBinding value)
    {
    	_bindingImageUrl = value;
        if (_bindingImageUrl != null) {
        	_bindingImageUrl.setOwner(this);
        	_bindingImageUrl.setBindingDefinition(getBindingImageUrlDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingImageUrl", null, _bindingImageUrl));
    }

    public WidgetBindingDefinition getBindingImageUrlDefinition()
    {
        return WidgetBindingDefinition.get(this, "bindingImageUrl", String.class, BindingDefinitionType.GET, false);
    }
    
}
