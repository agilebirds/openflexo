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

import java.util.Enumeration;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

@Deprecated
public class IESequenceOperator extends IESequence<IEOperator> {

	public IESequenceOperator(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
	}
	@Deprecated
	public IESequenceOperator(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public void addToInnerWidgets(IEOperator w){
		//w.setSequenceOperator(this);
		super.addToInnerWidgets(w);
	}
	
	@Override
	public void removeFromInnerWidgets(IEOperator w){
		super.removeFromInnerWidgets(w);
		if(size()==0){
			if(getOperatedSequence().isSubsequence()){
				getOperatedSequence().unwrap();
			}
		}
	}
	
	public IESequence getOperatedSequence(){
		return (IESequence)getParent();
	}
	
	@Override
	public boolean isSubsequence(){
    	return false;
    }

	public RepetitionOperator findFirstRepetitionOperator() {
		Enumeration en = elements();
		IEOperator temp = null;
		while(en.hasMoreElements()){
			temp = (IEOperator)en.nextElement();
			if(temp instanceof RepetitionOperator)return (RepetitionOperator)temp;
		}
		return null;
	}
	
	public ConditionalOperator findFirstConditionalOperator() {
		Enumeration en = elements();
		IEOperator temp = null;
		while(en.hasMoreElements()){
			temp = (IEOperator)en.nextElement();
			if(temp instanceof ConditionalOperator)return (ConditionalOperator)temp;
		}
		return null;
	}
}
