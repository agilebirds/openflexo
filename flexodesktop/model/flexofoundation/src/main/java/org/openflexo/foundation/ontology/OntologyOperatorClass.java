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
package org.openflexo.foundation.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.BooleanClassDescription;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public abstract class OntologyOperatorClass extends OntologyClass {

	private static final Logger logger = Logger.getLogger(OntologyOperatorClass.class.getPackage().getName());

	private List<OntologyClass> operands;

	private final BooleanClassDescription booleanClassDescription;

	protected OntologyOperatorClass(BooleanClassDescription anOntClass, FlexoOntology ontology) {
		super(anOntClass, ontology);
		this.booleanClassDescription = anOntClass;
		operands = new ArrayList<OntologyClass>();
	}

	@Override
	protected void init() {
		super.init();
		updateOperands();
	}

	@Override
	protected void update(OntClass anOntClass) {
		super.update(anOntClass);
		updateOperands();
	}

	private void updateOperands() {
		operands.clear();
		for (ExtendedIterator<? extends OntClass> i = booleanClassDescription.listOperands(); i.hasNext();) {
			OntClass c = i.next();
			OntologyClass ontologyClass = getOntology().retrieveOntologyClass(c);
			if (ontologyClass != null) {
				operands.add(ontologyClass);
			} else {
				logger.warning("Cannot find class for " + c);
			}
		}
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	public abstract String getClassNameKey();

	@Override
	public abstract String getFullyQualifiedName();

	@Override
	public abstract BooleanClassDescription getOntResource();

	@Override
	public abstract String getDisplayableDescription();

	protected String getOperandListDisplayableDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		boolean isFirst = true;
		for (OntologyClass c : operands) {
			sb.append((isFirst ? "" : ",") + c.getDisplayableDescription());
			isFirst = false;
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getName() {
		return getDisplayableDescription();
	}

}
