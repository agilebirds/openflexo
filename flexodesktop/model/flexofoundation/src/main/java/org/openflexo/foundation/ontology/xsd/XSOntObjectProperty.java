package org.openflexo.foundation.ontology.xsd;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;

public class XSOntObjectProperty extends XSOntProperty implements OntologyObjectProperty {

	private AbstractXSOntObject range;
	private boolean noRangeFoundYet = true;

	private List<XSOntObjectProperty> superProperties;

	protected XSOntObjectProperty(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
		range = ontology.getThingConcept();
		superProperties = new ArrayList<XSOntObjectProperty>();
	}

	protected XSOntObjectProperty(XSOntology ontology, String name) {
		this(ontology, name, XS_ONTOLOGY_URI + "#" + name);
	}

	public void addSuperProperty(XSOntObjectProperty parent) {
		superProperties.add(parent);
	}

	public void clearSuperProperties() {
		superProperties.clear();
	}

	@Override
	public List<XSOntObjectProperty> getSuperProperties() {
		return superProperties;
	}

	@Override
	public List<XSOntObjectProperty> getSubProperties(FlexoOntology context) {
		// TODO
		return new ArrayList<XSOntObjectProperty>();
	}

	@Override
	public OntologyObject getRange() {
		return range;
	}

	public void newRangeFound(AbstractXSOntObject range) {
		if (noRangeFoundYet) {
			this.range = range;
			noRangeFoundYet = false;
		} else {
			this.range = getOntology().getThingConcept();
		}
	}

	public void resetRange() {
		this.range = getOntology().getThingConcept();
		noRangeFoundYet = true;
	}

	@Override
	public boolean isLiteralRange() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDisplayableDescription() {
		return getName();
	}

	@Override
	public boolean isOntologyObjectProperty() {
		return true;
	}

	@Override
	public String getClassNameKey() {
		return "XSD_ontology_object_property";
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_OBJECT_PROPERTY_READ_ONLY_INSPECTOR;
		} else {
			return Inspectors.VE.ONTOLOGY_OBJECT_PROPERTY_INSPECTOR;
		}
	}

}
