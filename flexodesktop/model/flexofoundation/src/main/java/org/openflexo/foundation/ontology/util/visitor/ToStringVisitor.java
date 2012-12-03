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
package org.openflexo.foundation.ontology.util.visitor;

import org.openflexo.foundation.ontology.IFlexoOntologyBehaviouralProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyClabject;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConstraint;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyDataPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor;

/**
 * To Sting Visitor for Concepts.
 * 
 * @author gbesancon
 */
public class ToStringVisitor implements IFlexoOntologyConceptVisitor<String> {
	/**
	 * Constructor.
	 */
	public ToStringVisitor() {
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyConstraint)
	 */
	@Override
	public String visit(IFlexoOntologyConstraint aConstraint) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyDataType)
	 */
	@Override
	public String visit(IFlexoOntologyDataType aDataType) {
		StringBuilder builder = new StringBuilder();
		builder.append("DataType - ");
		builder.append(aDataType.getName());
		return builder.toString();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyIndividual)
	 */
	@Override
	public String visit(IFlexoOntologyIndividual aIndividual) {
		StringBuilder builder = new StringBuilder();
		builder.append("Individual - ");
		builder.append(aIndividual.getName());
		builder.append(" (");
		builder.append(aIndividual.getUri());
		builder.append(") : ");
		if (aIndividual.getTypes().size() != 0) {
			for (IFlexoOntologyClass aClass : aIndividual.getTypes()) {
				builder.append(aClass.getName());
				builder.append(", ");
			}
			builder.setLength(builder.length() - 2);
		}
		builder.append("\n");
		for (IFlexoOntologyPropertyValue propertyValue : aIndividual.getPropertyValues()) {
			if (propertyValue instanceof IFlexoOntologyDataPropertyValue) {
				if (((IFlexoOntologyDataPropertyValue) propertyValue).getValue() != null
						&& ((IFlexoOntologyDataPropertyValue) propertyValue).getValue().size() != 0) {
					builder.append("\t PropertyValue - (");
					builder.append(((IFlexoOntologyDataPropertyValue) propertyValue).getDataProperty().getName());
					builder.append(" = ");
					for (Object object : ((IFlexoOntologyDataPropertyValue) propertyValue).getValue()) {
						builder.append(object);
						builder.append(", ");
					}
					builder.setLength(builder.length() - 2);
					builder.append(")\n");
				}
			} else if (propertyValue instanceof IFlexoOntologyObjectPropertyValue) {
				if (((IFlexoOntologyObjectPropertyValue) propertyValue).getValue() != null
						&& ((IFlexoOntologyObjectPropertyValue) propertyValue).getValue().size() != 0) {
					builder.append("\t PropertyValue - (");
					builder.append(((IFlexoOntologyObjectPropertyValue) propertyValue).getObjectProperty().getName());
					builder.append(" = ");
					for (IFlexoOntologyConcept concept : ((IFlexoOntologyObjectPropertyValue) propertyValue).getValue()) {
						builder.append(concept.getName());
						builder.append(", ");
					}
					builder.setLength(builder.length() - 2);
					builder.append(")\n");
				}
			}
		}
		return builder.toString();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyClass)
	 */
	@Override
	public String visit(IFlexoOntologyClass aClass) {
		StringBuilder builder = new StringBuilder();
		builder.append("Class - ");
		builder.append(aClass.getName());
		builder.append(" (");
		builder.append(aClass.getUri());
		builder.append(")");
		if (aClass.getSuperClasses().size() != 0) {
			builder.append(" > ");
			for (IFlexoOntologyClass aSuperClass : aClass.getSuperClasses()) {
				builder.append(aSuperClass.getName());
				builder.append(", ");
			}
			builder.setLength(builder.length() - 2);
		}
		if (aClass.getFeatureAssociations().size() != 0) {
			builder.append("\n");
			for (IFlexoOntologyFeatureAssociation featureAssociation : aClass.getFeatureAssociations()) {
				builder.append("\t Feature - (");
				builder.append(featureAssociation.getLowerBound() == -1 ? "*" : featureAssociation.getLowerBound());
				builder.append("..");
				builder.append(featureAssociation.getUpperBound() == -1 ? "*" : featureAssociation.getUpperBound());
				builder.append(") : ");
				builder.append(featureAssociation.getFeature().getName());
				builder.append("\n");
			}
			builder.setLength(builder.length() - 1);
		}
		return builder.toString();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyClabject)
	 */
	@Override
	public String visit(IFlexoOntologyClabject aClabject) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyDataProperty)
	 */
	@Override
	public String visit(IFlexoOntologyDataProperty aDataProperty) {
		StringBuilder builder = new StringBuilder();
		builder.append("Data Property - ");
		builder.append(aDataProperty.getName());
		builder.append(" (");
		builder.append(aDataProperty.getUri());
		builder.append(")");
		builder.append(" : ");
		builder.append(aDataProperty.getRange().getName());
		return builder.toString();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty)
	 */
	@Override
	public String visit(IFlexoOntologyObjectProperty aObjectProperty) {
		StringBuilder builder = new StringBuilder();
		builder.append("Object Property - ");
		builder.append(aObjectProperty.getName());
		builder.append(" (");
		builder.append(aObjectProperty.getUri());
		builder.append(")");
		builder.append(" : ");
		builder.append(aObjectProperty.getRange().getName());
		return builder.toString();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.visitor.IFlexoOntologyConceptVisitor#visit(org.openflexo.foundation.ontology.IFlexoOntologyBehaviouralProperty)
	 */
	@Override
	public String visit(IFlexoOntologyBehaviouralProperty aBehaviouralProperty) {
		// TODO Auto-generated method stub
		return null;
	}
}
