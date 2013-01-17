package org.openflexo.technologyadapter.owl.viewpoint.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLProperty;
import org.openflexo.technologyadapter.owl.model.StatementWithProperty;

public final class OWLBindingFactory extends TechnologyAdapterBindingFactory {
	static final Logger logger = Logger.getLogger(OWLBindingFactory.class.getPackage().getName());

	public OWLBindingFactory() {
		super();
	}

	@Override
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		if (object instanceof OWLProperty) {
			return PropertyStatementPathElement.makePropertyStatementPathElement(parent, (OWLProperty) object);
		}
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public boolean handleType(TechnologySpecificCustomType technologySpecificType) {
		if ((technologySpecificType instanceof IndividualOfClass)
				&& ((IndividualOfClass) technologySpecificType).getOntologyClass() instanceof OWLClass) {
			return true;
		}
		if ((technologySpecificType instanceof SubClassOfClass)
				&& ((SubClassOfClass) technologySpecificType).getOntologyClass() instanceof OWLClass) {
			return true;
		}
		if ((technologySpecificType instanceof SubPropertyOfProperty)
				&& ((SubPropertyOfProperty) technologySpecificType).getOntologyProperty() instanceof OWLProperty) {
			return true;
		}
		if (technologySpecificType instanceof StatementWithProperty) {
			return true;
		}
		return false;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {

		if (parent.getType() instanceof IndividualOfClass) {
			IndividualOfClass parentType = (IndividualOfClass) parent.getType();
			List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
			returned.add(new URIPathElement(parent));
			returned.add(new URINamePathElement(parent));
			if (parentType.getOntologyClass() instanceof OWLClass) {
				for (OWLProperty p : searchProperties((OWLClass) parentType.getOntologyClass())) {
					returned.add(getSimplePathElement(p, parent));
				}
			}
			return returned;
		}

		return super.getAccessibleSimplePathElements(parent);

	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		return Collections.emptyList();
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement parent, String propertyName) {
		// We want to avoid code duplication, so iterate on all accessible simple path element and choose the right one
		for (SimplePathElement e : getAccessibleSimplePathElements(parent)) {
			if (e.getLabel().equals(propertyName)) {
				return e;
			}
		}
		return super.makeSimplePathElement(parent, propertyName);
	}

	@Override
	public FunctionPathElement makeFunctionPathElement(BindingPathElement parent, String functionName, List<DataBinding<?>> args) {
		return null;
	}

	private List<OWLProperty> searchProperties(OWLClass owlClass) {

		List<OWLProperty> returned = new ArrayList<OWLProperty>();

		OWLProperty[] array = owlClass.getPropertiesTakingMySelfAsDomain().toArray(
				new OWLProperty[owlClass.getPropertiesTakingMySelfAsDomain().size()]);

		// Big trick here
		// A property may shadow another one relatively from its name
		// We try to detect such shadowing, and we put the most specialized property first

		List<Integer> i1 = new Vector<Integer>();
		List<Integer> i2 = new Vector<Integer>();
		for (int i = 0; i < array.length; i++) {
			for (int j = i + 1; j < array.length; j++) {
				if (array[i].getName().equals(array[j].getName())) {
					// Detected name based shadowing between array[i] and array[j]
					// System.out.println("Detected name based shadowing between " + array[i] + " and " + array[j]);
					if (array[i].getFlexoOntology().getAllImportedOntologies().contains(array[j].getFlexoOntology())) {
						// array[i] appears to be the most specialized, don't do anything
					} else if (array[j].getFlexoOntology().getAllImportedOntologies().contains(array[i].getFlexoOntology())) {
						// array[j] appears to be the most specialized, we need to swap
						i1.add(i);
						i2.add(j);
					}
				}
			}
		}
		for (int i = 0; i < i1.size(); i++) {
			OWLProperty p1 = array[i1.get(i)];
			OWLProperty p2 = array[i2.get(i)];
			array[i1.get(i)] = p2;
			array[i2.get(i)] = p1;
			// Swapping p1 and p2
		}

		for (final OWLProperty property : array) {
			returned.add(property);
		}

		return returned;
	}

}