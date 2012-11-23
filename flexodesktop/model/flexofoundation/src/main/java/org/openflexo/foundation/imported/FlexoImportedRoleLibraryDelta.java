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
package org.openflexo.foundation.imported;

import java.util.Vector;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.ws.client.PPMWebService.PPMRole;

@Deprecated
public class FlexoImportedRoleLibraryDelta {

	public interface DeltaVisitor {
		public void visit(RoleDelta role);
	}

	public class RoleDelta {
		/**
		 * Various states: 1. Classical one, fiProcess and ppmProcess are both not null: it means that fiProcess was imported and ppmProcess
		 * is its refreshed version (either updated or not) 2. fiProcess is null but not ppmProcess: it means that there is a new process 3.
		 * fiProcess is not null but ppmProcess is null: it means that the process has been deleted on the server
		 */
		private Role fiRole;
		private PPMRole ppmRole;

		private DeltaStatus status;

		public RoleDelta(Role fiRole, PPMRole ppmRole) {
			this.fiRole = fiRole;
			this.ppmRole = ppmRole;
		}

		public PPMRole getPPMRole() {
			return ppmRole;
		}

		public Role getFiRole() {
			return fiRole;
		}

		public DeltaStatus getStatus() {
			return status;
		}

		public void setStatus(DeltaStatus status) {
			this.status = status;
		}

	}

	private Vector<RoleDelta> deltas;

	public FlexoImportedRoleLibraryDelta(RoleList library, PPMRole[] updatedRoles) {
		deltas = computeDiff(library.getRoles(), updatedRoles);
	}

	public void visit(DeltaVisitor visitor) {
		for (RoleDelta delta : deltas) {
			visitor.visit(delta);
		}
	}

	private Vector<RoleDelta> computeDiff(Vector<Role> originalRoles, PPMRole[] updatedRoles) {
		Vector<RoleDelta> returned = new Vector<RoleDelta>();
		Vector<Role> copyOfOriginal = new Vector<Role>(originalRoles);
		if (updatedRoles != null) {
			for (PPMRole role : updatedRoles) {
				Role fir = FlexoModelObject.getObjectWithURI(originalRoles, role.getUri());
				RoleDelta delta;
				if (fir != null) {
					copyOfOriginal.remove(fir);
					delta = new RoleDelta(fir, role);
					if (fir.isEquivalentTo(role)) {
						delta.setStatus(DeltaStatus.UNCHANGED);
					} else {
						delta.setStatus(DeltaStatus.UPDATED);
					}
				} else {
					delta = new RoleDelta(null, role);
					delta.setStatus(DeltaStatus.NEW);
				}
				returned.add(delta);
			}
		}
		for (Role role : copyOfOriginal) {
			RoleDelta delta = new RoleDelta(role, null);
			delta.setStatus(DeltaStatus.DELETED);
			returned.add(delta);
		}
		return returned;
	}

}
