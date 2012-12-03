/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.foundation.ontology.visitor;

import org.openflexo.foundation.ontology.IFlexoOntologyBehaviouralProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyClabject;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConstraint;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;

/**
 * Visitor for Concept of Ontology.
 * 
 * @author gbesancon
 * 
 * @param <T>
 * 
 * @pattern visitor
 */
public interface IFlexoOntologyConceptVisitor<T> {

	/**
	 * Visit a Constraint.
	 * 
	 * @param aConstraint
	 * @return
	 */
	T visit(IFlexoOntologyConstraint aConstraint);

	/**
	 * Visit a DataType.
	 * 
	 * @param aDataType
	 * @return
	 */
	T visit(IFlexoOntologyDataType aDataType);

	/**
	 * Visit an Individual.
	 * 
	 * @param aIndividual
	 * @return
	 */
	T visit(IFlexoOntologyIndividual aIndividual);

	/**
	 * Visit a Class.
	 * 
	 * @param aClass
	 * @return
	 */
	T visit(IFlexoOntologyClass aClass);

	/**
	 * Visit a Clabject.
	 * 
	 * @param aClabject
	 * @return
	 */
	T visit(IFlexoOntologyClabject aClabject);

	/**
	 * Visit a Data Property.
	 * 
	 * @param aDataProperty
	 * @return
	 */
	T visit(IFlexoOntologyDataProperty aDataProperty);

	/**
	 * Visit an Object Property.
	 * 
	 * @param aObjectProperty
	 * @return
	 */
	T visit(IFlexoOntologyObjectProperty aObjectProperty);

	/**
	 * Visit a Behavioural Property.
	 * 
	 * @param aBehaviouralProperty
	 * @return
	 */
	T visit(IFlexoOntologyBehaviouralProperty aBehaviouralProperty);
}
