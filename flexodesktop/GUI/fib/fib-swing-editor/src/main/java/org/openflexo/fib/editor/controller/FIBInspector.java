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
package org.openflexo.fib.editor.controller;

import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBInspector.FIBInspectorImpl.class)
@XMLElement(xmlTag = "Inspector")
public interface FIBInspector extends FIBPanel {

	public void appendSuperInspectors(FIBInspectorController controller);

	public FIBTabPanel getTabPanel();

	public static abstract class FIBInspectorImpl extends FIBPanelImpl implements FIBInspector {

		private boolean superInspectorWereAppended = false;

		@Override
		public void appendSuperInspectors(FIBInspectorController controller) {
			if (getDataType() == null) {
				return;
			}
			if (getDataType() instanceof Class) {
				FIBInspector superInspector = controller.inspectorForClass(((Class) getDataType()).getSuperclass());
				if (superInspector != null) {
					superInspector.appendSuperInspectors(controller);
					appendSuperInspector(superInspector);
				}
			}
		}

		@Override
		public String toString() {
			return "Inspector[" + getDataType() + "]";
		}

		protected void appendSuperInspector(FIBInspector superInspector) {
			if (!superInspectorWereAppended) {
				// System.out.println("Append "+superInspector+" to "+this);
				// TODO: i dont't know if this clone is still required (check this)
				FIBInspector clonedSuperInspector = (FIBInspector) superInspector.cloneObject();
				append(clonedSuperInspector);
				superInspectorWereAppended = true;
			}
		}

		@Override
		public FIBTabPanel getTabPanel() {
			return (FIBTabPanel) getSubComponents().get(0);
		}

		public String getXMLRepresentation() {
			// TODO: we use here the default factory !!!
			return getFactory().stringRepresentation(this);
		}
	}
}
