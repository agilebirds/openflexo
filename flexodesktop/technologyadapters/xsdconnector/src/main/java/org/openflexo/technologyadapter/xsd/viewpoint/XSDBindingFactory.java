package org.openflexo.technologyadapter.xsd.viewpoint;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;

/**
 * This class represent the {@link BindingFactory} dedicated to handle XSD technology-specific binding elements
 * 
 * @author sylvain
 * 
 */
public final class XSDBindingFactory extends TechnologyAdapterBindingFactory {
	static final Logger logger = Logger.getLogger(XSDBindingFactory.class.getPackage().getName());

	public XSDBindingFactory() {
		super();
	}

	@Override
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public boolean handleType(TechnologySpecificCustomType technologySpecificType) {
		return false;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		return Collections.emptyList();
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		return Collections.emptyList();
	}

}