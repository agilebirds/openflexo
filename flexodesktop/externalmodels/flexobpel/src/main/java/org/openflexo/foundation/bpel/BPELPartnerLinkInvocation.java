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
package org.openflexo.foundation.bpel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.oasis_open.docs.wsbpel._2_0.process.executable.TInvoke;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.MessageEntryBinding;
import org.openflexo.foundation.wkf.ws.ServiceOperation;

public class BPELPartnerLinkInvocation {

	/*
	 * The BPEL variable herbelow is just a name to identify the Messages. There is no corresponding variable in Flexo.
	 * In Flexo, we just have varibales assigned to the parts of that messages.
	 */
	private String varIN;
	private String varOUT;

	// <Full Flexo Variable name(String), Part name (String)>
	private Hashtable<String, String> flexoVarNameToPartIN = new Hashtable<String, String>();
	private Hashtable<String, String> flexoVarNameToPartOUT = new Hashtable<String, String>();

	// <Full Flexo Variable name(String), Flexo variable (AbstractBinding)
	private Hashtable<String, AbstractBinding> flexoVarNameToFlexoVar = new Hashtable<String, AbstractBinding>();

	private ServiceOperation operation;

	private BPELPartnerLink partnerLink;
	private SubProcessNode process;

	public BPELPartnerLinkInvocation(BPELPartnerLink pLink, SubProcessNode p) {
		partnerLink = pLink;
		process = p;
		setVarIN(p.getName() + "IN");
		setVarOUT(p.getName() + "OUT");

		Vector pMaps = process.getPortMapRegistery().getPortMaps();
		FlexoPortMap currentPort = null;

		for (int i = 0; i < pMaps.size(); i++) {
			currentPort = (FlexoPortMap) pMaps.get(i);
			if (currentPort.getIncomingPostConditions().size() >= 1) {
				operation = currentPort.getOperation();
				break;
			}
		}

		if (operation == null) {
			// unbound invocation...
			return;
		}

		Vector<MessageEntryBinding> v = ((MessageEdge) currentPort.getIncomingPostConditions().get(0)).getInputMessage().getBindings();
		for (MessageEntryBinding part : v) {
			if (part.getBindingValue() == null || part.getBindingDefinitionName() == null) {
				// should throw something
			} else {
				flexoVarNameToPartIN.put(part.getBindingValue().getStringRepresentation(), part.getBindingDefinitionName());
				flexoVarNameToFlexoVar.put(part.getBindingValue().getStringRepresentation(), part.getBindingValue());
			}
		}

		Vector<MessageEntryBinding> v2 = ((MessageEdge) currentPort.getOutgoingPostConditions().get(0)).getOutputMessage().getBindings();
		for (MessageEntryBinding part : v2) {
			if (part.getBindingDefinitionName() == null || part.getBindingValue() == null) {
				// should throw something
			} else {
				flexoVarNameToPartOUT.put(part.getBindingValue().getStringRepresentation(), part.getBindingDefinitionName());
				flexoVarNameToFlexoVar.put(part.getBindingValue().getStringRepresentation(), part.getBindingValue());
			}
		}

	}

	public QName getMessageINType() {
		String targetNameSpace = partnerLink.getServiceInterface().getParentService().getTargetNamespace();
		String messageType = operation.getInputMessageDefinition().getEntries().get(0).getMessage().getName();
		return new QName(targetNameSpace, messageType);
	}

	public QName getMessageOUTType() {
		String targetNameSpace = partnerLink.getServiceInterface().getParentService().getTargetNamespace();
		String messageType = operation.getOutputMessageDefinition().getEntries().get(0).getMessage().getName();
		return new QName(targetNameSpace, messageType);
	}

	public void setVarIN(String vi) {
		varIN = vi;
	}

	public String getVarIN() {
		return varIN;
	}

	public void setVarOUT(String vo) {
		varOUT = vo;
	}

	public String getVarOUT() {
		return varOUT;
	}

	public BPELPartnerLink getPartnerLink() {
		return partnerLink;
	}

	public TInvoke getTInvoke() {
		TInvoke inv = new TInvoke();
		inv.setInputVariable(getVarIN());
		inv.setOutputVariable(getVarOUT());
		inv.setPartnerLink(getPartnerLink().getName());
		inv.setOperation(operation.getName());
		return inv;
	}

	public String getName() {
		if (process == null)
			return "process is null";
		if (process.getName() == null)
			return "null";
		return process.getName();
	}

	@Override
	public String toString() {
		String toReturn = new String();
		toReturn += "Invocation : \n";
		toReturn += "  name :" + getName() + "\n";
		toReturn += "  varIN :" + getVarIN() + " \n";
		toReturn += "  varOUT :" + getVarOUT() + " \n";
		return toReturn;
	}

	public SubProcessNode getSubProcessNode() {
		return process;
	}

	// Namespace prefixes should be properly treated as well...
	public String[] getBPELPathForFlexoVariable(String fv) {
		String[] toReturn = new String[2];
		System.out.println("ServiceVariables : " + flexoVarNameToPartIN + flexoVarNameToPartOUT);
		Enumeration<String> en = flexoVarNameToPartIN.keys();
		while (en.hasMoreElements()) {
			String currentVar = en.nextElement();
			if (fv.indexOf(currentVar + ".") != -1) {
				// we've found the message part associated to te flexo variable
				String varAndPart = "$" + varIN + "." + flexoVarNameToPartIN.get(currentVar);
				toReturn[0] = varAndPart;
				String replaced = fv.substring(0, fv.indexOf(currentVar) + currentVar.length());
				toReturn[1] = replaced;
				return toReturn;
			}
		}
		Enumeration<String> en2 = flexoVarNameToPartOUT.keys();
		while (en2.hasMoreElements()) {
			String currentVar = en2.nextElement();
			if (fv.indexOf(currentVar + ".") != -1) {
				String varAndPart = "$" + varOUT + "." + flexoVarNameToPartOUT.get(currentVar);
				toReturn[0] = varAndPart;
				String replaced = fv.substring(0, fv.indexOf(currentVar) + currentVar.length());
				toReturn[1] = replaced;
				return toReturn;
			}
		}

		return null;
	}

	public boolean hasOperation() {
		return operation != null;
	}

}
