package org.openflexo.foundation.ontology.calc;

import org.openflexo.localization.FlexoLocalization;

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
	
}
