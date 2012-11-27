package org.openflexo.technologyadapter.xsd.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.toolbox.StringUtils;

public class XSOntClass extends AbstractXSOntObject implements OntologyClass, XSOntologyURIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntClass.class.getPackage()
			.getName());

	private final List<XSOntClass> superClasses = new ArrayList<XSOntClass>();

	protected XSOntClass(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
	}

	@Override
	public boolean isSuperClassOf(OntologyClass aClass) {
		if (aClass instanceof XSOntClass == false) {
			return false;
		}
		if (aClass == this || equalsToConcept(aClass)) {
			return true;
		}
		for (XSOntClass c : ((XSOntClass) aClass).getSuperClasses()) {
			if (isSuperClassOf(c)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<XSOntClass> getSuperClasses() {
		return superClasses;
	}

	@Override
	public Set<XSOntClass> getAllSuperClasses() {
		Set<XSOntClass> result = new HashSet<XSOntClass>();
		result.addAll(getSuperClasses());
		for (XSOntClass c : getSuperClasses()) {
			result.addAll(c.getAllSuperClasses());
		}
		return result;
	}

	@Override
	public Object addSuperClass(OntologyClass aClass) {
		if (aClass instanceof XSOntClass == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Class " + aClass + " is not a XSOntClass");
			}
			return null;
		}
		superClasses.add((XSOntClass) aClass);
		return null;
	}

	@Override
	public boolean isNamedClass() {
		return StringUtils.isNotEmpty(getURI());
	}

	@Override
	public boolean isThing() {
		return isNamedClass() && getURI().equals(XS_THING_URI);
	}

	@Override
	public String getDisplayableDescription() {
		// TODO tell where it's from (element/complex type)
		return getName();
	}

	@Override
	public boolean isOntologyClass() {
		return true;
	}

	@Override
	public String getClassNameKey() {
		return "XSD_ontology_class";
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_CLASS_READ_ONLY_INSPECTOR;
		} else {
			return Inspectors.VE.ONTOLOGY_CLASS_INSPECTOR;
		}
	}

}
