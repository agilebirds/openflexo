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
package org.openflexo.technologyadapter.owl.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.owl.model.IsAStatement;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;

@Deprecated
@FIBPanel("Fib/AddSubClassStatementPanel.fib")
@ModelEntity
@ImplementationClass(AddSubClassStatement.AddSubClassStatementImpl.class)
@XMLElement(xmlTag="AddIsAPropertyStatement")
public interface AddSubClassStatement extends AddStatement<SubClassStatement>{

@PropertyIdentifier(type=DataBinding.class)
public static final String FATHER_KEY = "father";

@Getter(value=FATHER_KEY)
@XMLAttribute
public DataBinding getFather();

@Setter(FATHER_KEY)
public void setFather(DataBinding father);


public static abstract  class AddSubClassStatementImpl extends AddStatement<SubClassStatement>Impl implements AddSubClassStatement
{

	private static final Logger logger = Logger.getLogger(AddSubClassStatement.class.getPackage().getName());

	public AddSubClassStatementImpl() {
		super();
	}

	@Override
	public IFlexoOntologyStructuralProperty getProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(IFlexoOntologyStructuralProperty aProperty) {
		// TODO Auto-generated method stub
	}

	public OWLConcept<?> getPropertyFather(EditionSchemeAction action) {
		try {
			return (OWLConcept<?>) getFather().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DataBinding<IFlexoOntologyConcept> father;

	public DataBinding<IFlexoOntologyConcept> getFather() {
		if (father == null) {
			father = new DataBinding<IFlexoOntologyConcept>(this, IFlexoOntologyConcept.class, BindingDefinitionType.GET);
			father.setBindingName("father");
		}
		return father;
	}

	public void setFather(DataBinding<IFlexoOntologyConcept> father) {
		if (father != null) {
			father.setOwner(this);
			father.setBindingName("father");
			father.setDeclaredType(IFlexoOntologyConcept.class);
			father.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.father = father;
	}

	@Override
	public Type getAssignableType() {
		return IsAStatement.class;
	}

	@Override
	public SubClassStatement performAction(EditionSchemeAction action) {
		OWLConcept<?> subject = getPropertySubject(action);
		OWLConcept<?> father = getPropertyFather(action);
		if (father instanceof OWLClass) {
			if (subject instanceof OWLClass) {
				return ((OWLClass) subject).addToSuperClasses((OWLClass) father);
			} else if (subject instanceof OWLIndividual) {
				return ((OWLIndividual) subject).addToTypes((OWLClass) father);
			}
		}
		return null;
	}

}
}
