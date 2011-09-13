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
package org.openflexo.foundation.validation;

import java.util.Vector;
import java.util.logging.Logger;


/**
 * Represents a validation issue containing many other validation issues.
 * This is an artefact to express that some validation rules may throw more than
 * one error or warning 
 * 
 * @author sguerin
 * 
 */
public class CompoundIssue<R extends ValidationRule<R,V>, V extends Validable> extends ValidationIssue<R,V>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CompoundIssue.class.getPackage().getName());

    private Vector<ValidationIssue<R,V>> _containedIssues;
    
    public CompoundIssue(V anObject)
    {
        super(anObject, null);
        _containedIssues = new Vector<ValidationIssue<R,V>>();
    }

     public Vector<ValidationIssue<R,V>> getContainedIssues() 
    {
        return _containedIssues;
    }

    public void setContainedIssues(Vector<ValidationIssue<R,V>> containedIssues) 
    {
        _containedIssues = containedIssues;
    }

    public void addToContainedIssues(ValidationIssue<R,V> issue)
    {
        _containedIssues.add(issue);
    }
    
    public void removeFromContainedIssues(ValidationIssue<R,V> issue)
    {
        _containedIssues.remove(issue);
    }
    
    @Override
    public void setCause(R rule) {
    	super.setCause(rule);
    	for (ValidationIssue<R,V> issue : _containedIssues) {
    		if (issue!=this) {
				issue.setCause(rule);
			}
		}
    }
    
    @Override
	public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	for (ValidationIssue issue : getContainedIssues()) {
    		sb.append(issue.toString()+"\n");
    	}
    	return sb.toString();
    }


}
