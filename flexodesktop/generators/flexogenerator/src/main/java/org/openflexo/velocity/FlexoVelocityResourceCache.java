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
package org.openflexo.velocity;

import org.apache.velocity.runtime.resource.ResourceCacheImpl;

/**
 * This class is intended to have a hand on the resource cache. Whenever a macro file is reloaded, we must clear the cache, so that the
 * templates are reloaded and the node trees are recreated. We need them to be recreated because the nodes that render the macros, create a
 * cache of those macros, and there is no way to tell them that their corresponding macro has been reloaded.
 * 
 * @author gpolet
 * 
 */
public class FlexoVelocityResourceCache extends ResourceCacheImpl {

	public FlexoVelocityResourceCache() {
		FlexoVelocity.setResourceCache(this);
	}
	
	public void clearCache() {
		cache.clear();
	}
	
}
