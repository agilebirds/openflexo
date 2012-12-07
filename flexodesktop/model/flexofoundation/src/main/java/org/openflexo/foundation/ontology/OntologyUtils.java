package org.openflexo.foundation.ontology;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class OntologyUtils {

	private static final Logger logger = Logger.getLogger(OntologyUtils.class.getPackage().getName());

	public static <C extends IFlexoOntologyClass> C getMostSpecializedClass(Collection<C> someClasses) {

		if (someClasses.size() == 0) {
			return null;
		}
		if (someClasses.size() == 1) {
			return someClasses.iterator().next();
		}
		IFlexoOntologyClass[] array = someClasses.toArray(new IFlexoOntologyClass[someClasses.size()]);

		for (int i = 0; i < someClasses.size(); i++) {
			for (int j = i + 1; j < someClasses.size(); j++) {
				IFlexoOntologyClass c1 = array[i];
				IFlexoOntologyClass c2 = array[j];
				if (c1.isSuperClassOf(c2)) {
					someClasses.remove(c1);
					return getMostSpecializedClass(someClasses);
				}
				if (c2.isSuperClassOf(c1)) {
					someClasses.remove(c2);
					return getMostSpecializedClass(someClasses);
				}
			}
		}

		// No parent were found, take first item
		logger.warning("Undefined specializing criteria between " + someClasses);
		return someClasses.iterator().next();

	}

	public static <C extends IFlexoOntologyClass> IFlexoOntologyClass getFirstCommonAncestor(C c1, C c2) {
		Set<C> commonAncestors = new HashSet<C>();
		Set<C> ancestors1 = (Set<C>) getAllSuperClasses(c1);
		ancestors1.add(c1);
		Set<C> ancestors2 = (Set<C>) getAllSuperClasses(c2);
		ancestors2.add(c2);
		for (C cl1 : ancestors1) {
			for (C cl2 : ancestors2) {
				if (cl1.equalsToConcept(cl2)) {
					commonAncestors.add(cl1);
				}
			}
		}
		return getMostSpecializedClass(commonAncestors);
	}

	/**
	 * Return all direct and inferred super classes of supplied class
	 * 
	 * @return
	 */
	public static Set<IFlexoOntologyClass> getAllSuperClasses(IFlexoOntologyClass aClass) {
		Set<IFlexoOntologyClass> returned = new HashSet<IFlexoOntologyClass>();
		for (IFlexoOntologyClass c : aClass.getSuperClasses()) {
			returned.add(c);
			returned.addAll(getAllSuperClasses(c));
		}
		return returned;
	}

	/**
	 * Return all direct and inferred super classes of supplied individual
	 * 
	 * @return
	 */
	public static Set<IFlexoOntologyClass> getAllSuperClasses(IFlexoOntologyIndividual anIndividual) {
		Set<IFlexoOntologyClass> returned = new HashSet<IFlexoOntologyClass>();
		for (IFlexoOntologyClass c : anIndividual.getTypes()) {
			returned.add(c);
			returned.addAll(getAllSuperClasses(c));
		}
		return returned;
	}

	/**
	 * Return all direct and inferred imported ontology, including supplied ontology
	 * 
	 * @return
	 */
	public static <O extends IFlexoOntology> Set<O> getAllImportedOntologies(O ontology) {
		Set<O> returned = new HashSet<O>();
		returned.add(ontology);
		for (IFlexoOntology i : ontology.getImportedOntologies()) {
			returned.add((O) i);
			returned.addAll((Set<O>) getAllImportedOntologies(i));
		}
		return returned;
	}

}
