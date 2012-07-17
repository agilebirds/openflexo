package org.openflexo.foundation.ontology;

import java.util.List;
import java.util.Set;

public interface OntologyClass extends OntologyObject {

	public boolean isSuperClassOf(OntologyClass aClass);

	/**
	 * Return all direct super classes of this class
	 * 
	 * @return
	 */
	public List<? extends OntologyClass> getSuperClasses();

	/**
	 * Return all direct and infered super classes of this class
	 * 
	 * @return
	 */
	public Set<? extends OntologyClass> getAllSuperClasses();

	/**
	 * Add super class to this class
	 * 
	 * @param aClass
	 * @return statement representing added super class
	 */
	public Object addSuperClass(OntologyClass aClass);

	/**
	 * Indicates if this class represents a named class
	 */
	public boolean isNamedClass();

	/**
	 * Indicates if this class represents the Thing root concept
	 */
	public boolean isThing();

}
