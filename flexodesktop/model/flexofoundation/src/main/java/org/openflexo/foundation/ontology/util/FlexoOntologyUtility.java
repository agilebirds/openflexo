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
package org.openflexo.foundation.ontology.util;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyFeature;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.util.visitor.ToStringVisitor;

/**
 * Utilitaires pour FlexoOntology.
 * 
 * @author gbesancon
 */
public class FlexoOntologyUtility {

	/**
	 * To String.
	 * 
	 * @param flexoOntology
	 * @return
	 */
	public static String toString(IFlexoOntology flexoOntology) {
		StringBuilder builder = new StringBuilder();
		try {
			builder.append("Ontology : ");
			builder.append(flexoOntology.getName());
			builder.append(" (");
			builder.append(flexoOntology.getUri());
			builder.append(")\n");
			for (IFlexoOntologyConcept concept : flexoOntology.getConcepts()) {
				builder.append(concept.accept(new ToStringVisitor()));
				builder.append("\n");
			}
			for (IFlexoOntologyDataType dataType : flexoOntology.getDataTypes()) {
				builder.append(dataType.getName());
				builder.append(" (");
				builder.append(dataType.getUri());
				builder.append(")\n");
			}
			for (IFlexoOntologyContainer subContainer : flexoOntology.getSubContainers()) {
				builder.append(toString(subContainer));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * To String.
	 * 
	 * @param container
	 * @return
	 */
	protected static String toString(IFlexoOntologyContainer container) {
		StringBuilder builder = new StringBuilder();
		builder.append("Container : ");
		builder.append(container.getName());
		builder.append("\n");
		for (IFlexoOntologyConcept concept : container.getConcepts()) {
			builder.append(concept.accept(new ToStringVisitor()));
			builder.append("\n");
		}
		for (IFlexoOntologyDataType dataType : container.getDataTypes()) {
			builder.append(dataType.getName());
			builder.append(" (");
			builder.append(dataType.getUri());
			builder.append(")\n");
		}
		for (IFlexoOntologyContainer subContainer : container.getSubContainers()) {
			builder.append(toString(subContainer));
		}
		return builder.toString();
	}

	/**
	 * Get All Concepts.
	 * 
	 * @param container
	 * @return
	 */
	public static List<IFlexoOntologyConcept> getAllConcepts(IFlexoOntologyConceptContainer container) {
		List<IFlexoOntologyConcept> result = new ArrayList<IFlexoOntologyConcept>();
		result.addAll(container.getConcepts());
		for (IFlexoOntologyConceptContainer subContainer : container.getSubContainers()) {
			result.addAll(getAllConcepts(subContainer));
		}
		return result;
	}

	/**
	 * Get Class from uri.
	 * 
	 * @param flexoOntology
	 * @param uri
	 * @return
	 */
	public static IFlexoOntologyClass getClass(IFlexoOntology flexoOntology, String uri) {
		IFlexoOntologyClass result = null;
		for (IFlexoOntologyConcept concept : getAllConcepts(flexoOntology)) {
			if (concept instanceof IFlexoOntologyClass) {
				if (concept.getUri().equalsIgnoreCase(uri)) {
					result = (IFlexoOntologyClass) concept;
				}
			}
		}
		return result;
	}

	/**
	 * Get Individual from uri.
	 * 
	 * @param flexoOntology
	 * @param uri
	 * @return
	 */
	public static IFlexoOntologyIndividual getIndividual(IFlexoOntology flexoOntology, String uri) {
		IFlexoOntologyIndividual result = null;
		for (IFlexoOntologyConcept concept : getAllConcepts(flexoOntology)) {
			if (concept instanceof IFlexoOntologyIndividual) {
				if (concept.getUri().equalsIgnoreCase(uri)) {
					result = (IFlexoOntologyIndividual) concept;
				}
			}
		}
		return result;
	}

	/**
	 * Get Individual of type.
	 * 
	 * @param flexoOntology
	 * @param uri
	 * @return
	 */
	public static List<IFlexoOntologyIndividual> getIndividualOfType(IFlexoOntology flexoOntology, IFlexoOntologyClass emfClass) {
		List<IFlexoOntologyIndividual> result = new ArrayList<IFlexoOntologyIndividual>();
		for (IFlexoOntologyConcept concept : getAllConcepts(flexoOntology)) {
			if (concept instanceof IFlexoOntologyIndividual) {
				if (((IFlexoOntologyIndividual) concept).isIndividualOf(emfClass)) {
					result.add((IFlexoOntologyIndividual) concept);
				}
			}
		}
		return result;
	}

	/**
	 * Get Feature from uri.
	 * 
	 * @param flexoOntology
	 * @param uri
	 * @return
	 */
	public static IFlexoOntologyFeature getFeature(IFlexoOntology flexoOntology, String uri) {
		IFlexoOntologyFeature result = null;
		for (IFlexoOntologyConcept concept : getAllConcepts(flexoOntology)) {
			if (concept instanceof IFlexoOntologyFeature) {
				if (concept.getUri().equalsIgnoreCase(uri)) {
					result = (IFlexoOntologyFeature) concept;
				}
			}
		}
		return result;
	}
}
