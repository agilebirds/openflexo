package org.openflexo.technologyadapter.powerpoint.model;

import org.openflexo.foundation.DefaultFlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

public abstract class PowerpointObject extends DefaultFlexoObject implements TechnologyObject {

	private final PowerpointTechnologyAdapter technologyAdapter;

	private String uri;

	public PowerpointObject(PowerpointTechnologyAdapter adapter) {
		super();
		technologyAdapter = adapter;
	}

	/**
	 * Name of Object.
	 * 
	 * @return
	 */
	public abstract String getName();

	@Override
	public PowerpointTechnologyAdapter getTechnologyAdapter() {
		return technologyAdapter;
	}

	public String getUri() {
		return uri;
	}

}
