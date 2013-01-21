package org.openflexo.foundation.viewpoint;

import org.openflexo.antar.binding.CustomType;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

/**
 * Represents a {@link CustomType} in a given technology, returned by {@link #getTechnologyAdapter()} method
 * 
 * @author sylvain
 * 
 */
public interface TechnologySpecificCustomType extends CustomType {

	public TechnologyAdapter<?, ?> getTechnologyAdapter();
}
