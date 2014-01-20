/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;

/**
 * This class defines and implements the Openflexo built-in virtual model technology adapter
 * 
 * @author sylvain
 * 
 */
public class VirtualModelTechnologyAdapter extends TechnologyAdapter {

	private static final Logger logger = Logger.getLogger(VirtualModelTechnologyAdapter.class.getPackage().getName());

	public VirtualModelTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "Openflexo virtual model";
	}

	/**
	 * Creates and return a new {@link VirtualModelModelSlot} adressing supplied VirtualModel.<br>
	 * 
	 * @param modelSlotClass
	 * @param containerVirtualModel
	 *            the virtual model in which model slot should be created
	 * @param addressedVirtualModel
	 *            the virtual model referenced by the model slot
	 * @return
	 */
	public VirtualModelModelSlot makeVirtualModelModelSlot(VirtualModel containerVirtualModel, VirtualModel addressedVirtualModel) {
		VirtualModelModelSlot returned = makeModelSlot(VirtualModelModelSlot.class, containerVirtualModel);
		returned.setAddressedVirtualModel(addressedVirtualModel);
		return returned;
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// no specific binding factory for this technology
		return null;
	}

	@Override
	public TechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <I> void initializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {
		// TODO Auto-generated method stub

	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <I> void contentsAdded(FlexoResourceCenter<I> resourceCenter, I contents) {
		// TODO Auto-generated method stub

	}

	@Override
	public <I> void contentsDeleted(FlexoResourceCenter<I> resourceCenter, I contents) {
		// TODO Auto-generated method stub

	}

}
