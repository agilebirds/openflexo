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

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;


public class HTMLListDescriptorCollection extends Vector<HTMLListDescriptor> {

	public HTMLListDescriptorCollection(){
		super();
	}
	
	public HTMLListDescriptor getHTMLListForBloc(IEBlocWidget b){
		Enumeration en = elements();
		HTMLListDescriptor temp = null;
		while(en.hasMoreElements()){
			temp = (HTMLListDescriptor)en.nextElement();
			if(temp.getBloc().equals(b))return temp;
		}
		return null;
	}
	
	public HTMLListDescriptor getHTMLListForHeader(IEHeaderWidget h){
		Enumeration en = elements();
		HTMLListDescriptor temp = null;
		while(en.hasMoreElements()){
			temp = (HTMLListDescriptor)en.nextElement();
			if(temp.containsHeader(h))return temp;
		}
		return null;
	}
}
