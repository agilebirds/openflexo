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
 * Contributors :
 *
 */
package org.openflexo.foundation.ontology;


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
