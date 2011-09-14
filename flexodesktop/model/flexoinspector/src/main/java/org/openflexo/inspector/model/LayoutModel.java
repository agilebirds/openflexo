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
package org.openflexo.inspector.model;


public class LayoutModel extends ParametersContainerModelObject
{

    public String name;

    public LayoutModel layoutModel;

    public LayoutModel()
    {
        super();
    }

    public void finalizeLayoutModelDecoding(/*AbstractController c*/)
    {
        /*
         * if(hasValueForParameter("propertylist")){
         * c.addToPropertyListAttributes(name,getValueForParameter("propertylist")); }
         */
    }

}
