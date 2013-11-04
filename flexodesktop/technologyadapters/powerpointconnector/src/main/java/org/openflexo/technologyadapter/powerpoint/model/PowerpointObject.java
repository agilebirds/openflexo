package org.openflexo.technologyadapter.powerpoint.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

public abstract class PowerpointObject extends FlexoObject implements TechnologyObject{

	private PowerpointTechnologyAdapter technologyAdapter;
	
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
		// TODO Auto-generated method stub
		return technologyAdapter;
	}

	@Override
	public String getFullyQualifiedName() {
		return getName();
	}

	public String getUri() {
		return uri;
	}

}
