package org.openflexo.foundation.ontology;

import java.util.List;
import java.util.Set;

public interface IFlexoOntologyClass extends IFlexoOntologyConcept {

	public boolean isSuperClassOf(IFlexoOntologyClass aClass);

	/**
	 * Return all direct super classes of this class
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyClass> getSuperClasses();

	/**
	 * Return all direct and infered super classes of this class
	 * 
	 * @return
	 */
	public Set<? extends IFlexoOntologyClass> getAllSuperClasses();

	/**
	 * Add super class to this class
	 * 
	 * @param aClass
	 * @return statement representing added super class
	 */
	public Object addSuperClass(IFlexoOntologyClass aClass);

	/**
	 * Indicates if this class represents a named class
	 */
	public boolean isNamedClass();

	/**
	 * Indicates if this class represents the Thing root concept
	 */
	public boolean isThing();

}
