package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.localization.FlexoLocalization;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;

public class OntologicObjectPatternRole extends PatternRole {

	public static enum OntologicObjectType
	{
		Individual,
		Class,
		ObjectProperty,
		DataProperty,
		OntologyStatement
	}
	
	private OntologicObjectType ontologicObjectType;

	@Override
	public PatternRoleType getType()
	{
		return PatternRoleType.OntologicObject;
	}

	public OntologicObjectType getOntologicObjectType()
	{
		return ontologicObjectType;
	}

	public void setOntologicObjectType(OntologicObjectType ontologicObjectType)
	{
		this.ontologicObjectType = ontologicObjectType;
	}


	@Override
	public  String getPreciseType()
	{
			if (ontologicObjectType == null) {
				return null;
			}
			switch (ontologicObjectType) {
			case Class:
				return FlexoLocalization.localizedForKey("ontology_class");
			case Individual:
				return FlexoLocalization.localizedForKey("ontology_individual");
			case ObjectProperty:
				return FlexoLocalization.localizedForKey("object_property");
			case DataProperty:
				return FlexoLocalization.localizedForKey("data_property");
			case OntologyStatement:
				return FlexoLocalization.localizedForKey("ontology_statement");
			default:
				return null;
			}
	}
	
	public Class<?> getAccessedClass()
	{
		if (ontologicObjectType == null) {
			return null;
		}
		switch (ontologicObjectType) {
		case Class:
			return OntologyClass.class;
		case Individual:
			return OntologyIndividual.class;
		case ObjectProperty:
			return OntologyObjectProperty.class;
		case DataProperty:
			return OntologyDataProperty.class;
		case OntologyStatement:
			return OntologyStatement.class;
		default:
			return null;
		}
	}

}
