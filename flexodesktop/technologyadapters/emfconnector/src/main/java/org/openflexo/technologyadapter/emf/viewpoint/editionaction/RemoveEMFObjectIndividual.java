/** Copyright (c) 2013, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.viewpoint.editionaction;

import java.util.logging.Logger;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

/**
 * Remove an EMF Object Individual from model.
 * 
 * @author gbesancon
 */
@ModelEntity
@ImplementationClass(RemoveEMFObjectIndividual.RemoveEMFObjectIndividualImpl.class)
@XMLElement
public interface RemoveEMFObjectIndividual extends DeleteAction<EMFModelSlot, EMFObjectIndividual> {

	public static abstract class RemoveEMFObjectIndividualImpl extends DeleteActionImpl<EMFModelSlot, EMFObjectIndividual> implements
			RemoveEMFObjectIndividual {

		private static final Logger logger = Logger.getLogger(RemoveEMFObjectIndividual.class.getPackage().getName());

		/**
		 * Follow the link.
		 * 
		 * @see org.openflexo.foundation.viewpoint.DeleteAction#performAction(org.openflexo.foundation.view.action.EditionSchemeAction)
		 */
		@Override
		public EMFObjectIndividual performAction(EditionSchemeAction action) {
			// ModelSlotInstance<EMFModel, EMFMetaModel> modelSlotInstance = getModelSlotInstance(action);
			// EObject object = objectIndividual.getObject();
			// EObject container = object.eContainer();
			// EStructuralFeature containmentFeature = object.eContainmentFeature();
			// if (container != null) {
			// // Model Object not root
			// if (containmentFeature.getUpperBound() != 1) {
			// List<EObject> values = (List<EObject>) container.eGet(containmentFeature);
			// values.remove(object);
			// } else {
			// objectIndividual.getObject().eUnset(containmentFeature);
			// }
			// } else {
			// // Root Model Object
			// modelSlotInstance.getModel().getEMFResource().getContents().remove(object);
			// }
			return null;
		}

		/**
		 * Follow the link.
		 * 
		 * @see org.openflexo.foundation.viewpoint.DeleteAction#finalizePerformAction(org.openflexo.foundation.view.action.EditionSchemeAction,
		 *      org.openflexo.foundation.FlexoModelObject)
		 */
		@Override
		public void finalizePerformAction(EditionSchemeAction action, EMFObjectIndividual initialContext) {
		}
	}
}
