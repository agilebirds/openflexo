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
package org.openflexo.fme;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.Cloner;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;

public class FIBInspector extends FIBPanel {

	private boolean superInspectorWereAppended = false;

	protected void appendSuperInspectors(FIBInspectorController controller) {
		if (getDataType() == null) {
			return;
		}
		Class dataClass = TypeUtils.getBaseClass(getDataType());
		FIBInspector superInspector = controller.inspectorForClass(dataClass.getSuperclass());
		if (superInspector != null) {
			superInspector.appendSuperInspectors(controller);
			appendSuperInspector(superInspector);
		}
		for (Class<?> superInterface : dataClass.getInterfaces()) {
			FIBInspector superInterfaceInspector = controller.inspectorForClass(superInterface);
			if (superInterfaceInspector != null) {
				superInterfaceInspector.appendSuperInspectors(controller);
				appendSuperInspector(superInterfaceInspector);
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
			/*try {
				System.out.println("Clone container:\n"+XMLCoder.encodeObjectWithMapping(superInspector, FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
				System.out.println("Found this:\n"+XMLCoder.encodeObjectWithMapping((XMLSerializable)Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()), FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateSerializationIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			append((FIBPanel) Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()));
			superInspectorWereAppended = true;
		}
	}

	public String getXMLRepresentation() {
		try {
			return XMLCoder.encodeObjectWithMapping(this, FIBLibrary.getFIBMapping(), StringEncoder.getDefaultInstance());
		} catch (InvalidObjectSpecificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessorInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DuplicateSerializationIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error ???";
	}

}
