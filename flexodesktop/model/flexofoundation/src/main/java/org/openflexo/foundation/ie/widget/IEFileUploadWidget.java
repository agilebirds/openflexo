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

import java.util.Vector;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Represents a label widget
 * 
 * @author bmangez
 */
public class IEFileUploadWidget extends IENonEditableTextWidget
{

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * 
     */
    public static final String FILE_UPLOAD_WIDGET = "file_upload_widget";

    public IEFileUploadWidget(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public IEFileUploadWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
    }

    @Override
	public String getDefaultInspectorName()
    {
        return "FileUpload.inspector";
    }

    /**
     * Return a Vector of embedded IEObjects at this level. NOTE that this is
     * NOT a recursive method
     * 
     * @return a Vector of IEObject instances
     */
    @Override
	public Vector<IObject> getEmbeddedIEObjects()
    {
        return EMPTY_IOBJECT_VECTOR;
    }

    @Override
	public String getFullyQualifiedName()
    {
        return "FileUpload";
    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return FILE_UPLOAD_WIDGET;
    }
    
    @Override
	public boolean generateJavascriptID(){
    	return true;
    }
    
    
}
