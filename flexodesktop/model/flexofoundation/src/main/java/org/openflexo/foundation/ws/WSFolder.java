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
package org.openflexo.foundation.ws;

import java.util.Vector;

import org.openflexo.foundation.wkf.ws.ServiceInterface;

public abstract class WSFolder extends WSObject {

	/**
	 * @param dl
	 */
	public WSFolder(FlexoWSLibrary dl) {
		super(dl);
	}

	public abstract Vector<? extends WSService> getWSServices();

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "WS_FOLDER";
	}

	public abstract String getLocalizedDescription();

	@Override
	public void delete() {
		super.delete();
	}

	// ==========================================================================
	// ======================== TreeNode implementation
	// =========================
	// ==========================================================================

	@Override
	public FlexoWSLibrary getParent() {
		return getWSLibrary();
	}

	@Override
	public Vector<? extends WSObject> getOrderedChildren() {
		return getWSServices();
	}

	// ==========================================================================
	// ======================== Search WSPortType
	// =========================
	// ==========================================================================

	/**
	 * utility method to retrieve a service from a serviceInterface.
	 * 
	 * @param si
	 * @return
	 */
	public WSService getParentOfServiceInterface(ServiceInterface si) {
		WSPortType pt = getWSPortTypeNamed(si.getName());
		if (pt != null) {
			return pt.getWSService();
		}
		return null;
	}

	public WSPortType getWSPortTypeNamed(String name) {
		if (name == null) {
			return null;
		}
		WSPortType found = null;
		for (WSService group : getWSServices()) {
			found = group.getWSPortTypeNamed(name);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	public WSRepository getWSRepositoryNamed(String name) {
		if (name == null) {
			return null;
		}
		WSRepository found = null;
		for (WSService group : getWSServices()) {
			found = group.getWSRepositoryNamed(name);
			if (found != null) {
				return found;
			}
		}
		return null;
	}
}