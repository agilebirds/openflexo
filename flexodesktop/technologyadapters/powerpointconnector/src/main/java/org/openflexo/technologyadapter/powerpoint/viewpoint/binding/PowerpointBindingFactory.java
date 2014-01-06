package org.openflexo.technologyadapter.powerpoint.viewpoint.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;

/**
 * This class represent the {@link BindingFactory} dedicated to handle Excel technology-specific binding elements
 * 
 * @author sylvain, vincent
 * 
 */
public final class PowerpointBindingFactory extends TechnologyAdapterBindingFactory {
	static final Logger logger = Logger.getLogger(PowerpointBindingFactory.class.getPackage().getName());

	public PowerpointBindingFactory() {
		super();
	}

	@Override
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public boolean handleType(TechnologySpecificCustomType technologySpecificType) {
		if (technologySpecificType instanceof PowerpointSlideshow) {
			return true;
		}
		if (technologySpecificType instanceof PowerpointSlide) {
			return true;
		}
		return true;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
		if (parent instanceof PowerpointSlideshow) {
			for (PowerpointSlide sheet : ((PowerpointSlideshow) parent).getPowerpointSlides()) {
				returned.add(getSimplePathElement(sheet, parent));
			}
		}
		return returned;
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		// TODO
		return Collections.emptyList();
	}

}