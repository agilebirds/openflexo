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

import org.oasis_open.docs.wsbpel._2_0.plnktype.TPartnerLinkType;
import org.oasis_open.docs.wsbpel._2_0.plnktype.TRole;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TBoolean;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TImport;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TPartnerLink;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TReceive;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TReply;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TVariable;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.MessageEntryBinding;
import org.openflexo.foundation.wkf.ws.OutputPort;

public class BPELExportedPartnerLink implements BPELPartnerLinkInterface {

	FlexoProcess process;
	AbstractInPort portIN;
	OutputPort portOUT;

	// contains <FlexoVariable;PartName>
	private Hashtable<String, String> flexoVarNameToPartIN = new Hashtable<String, String>();
	private Hashtable<String, String> flexoVarNameToPartOUT = new Hashtable<String, String>();

	public BPELExportedPartnerLink(FlexoProcess pro, AbstractInPort pIN, OutputPort pOUT) {
		System.out.println("FGHDFGHDFGHDFGHDFGHDFGHDFGHDFGHDFGHDFGHDFGHDFGHDFGHDFGHDH");
		System.out.println("CREATING ONE EXP PLINK : " + pIN + pOUT);
		process = pro;
		portIN = pIN;
		portOUT = pOUT;
		BPELNamespacePrefixMapperFactory.addNamespaceAndPrefix(BPELConstants.THIS_NAMESPACE, "tns");
		BPELNamespacePrefixMapperFactory.addNamespaceAndPrefix(BPELConstants.THIS_NAMESPACE + "/wsdl", "exp");

		Vector<FlexoPostCondition<AbstractNode, AbstractNode>> postConditions = portIN.getOutgoingPostConditions();
		Vector<MessageEntryBinding> v = ((MessageEdge) postConditions.get(0)).getInputMessage().getBindings();

		// postConditions.g
		for (MessageEntryBinding part : v) {
			if (part.getBindingValue() == null || part.getBindingDefinitionName() == null) {
				// should throw something
			} else {
				System.out.println("EXP PLINKS Adding IN : " + part.getBindingValue().getStringRepresentation() + " - "
						+ part.getBindingDefinitionName());
				flexoVarNameToPartIN.put(part.getBindingValue().getStringRepresentation(), part.getBindingDefinitionName());
			}
		}
		Vector<FlexoPostCondition<AbstractNode, AbstractNode>> preConditions = ((FlexoPort) portOUT).getIncomingPostConditions();
		Vector<MessageEntryBinding> v2 = ((MessageEdge) preConditions.get(0)).getOutputMessage().getBindings();

		for (MessageEntryBinding part : v2) {
			if (part.getBindingValue() == null || part.getBindingDefinitionName() == null) {
				// should throw something
			} else {
				System.out.println("EXP PLINKS ADDING OUT :" + part.getBindingValue().getStringRepresentation() + " - "
						+ part.getBindingDefinitionName());
				flexoVarNameToPartOUT.put(part.getBindingValue().getStringRepresentation(), part.getBindingDefinitionName());
			}
		}
	}

	public String getVarIN() {
		return BPELConstants.normalise(process.getName()) + "_IN";
	}

	public String getVarOUT() {
		return BPELConstants.normalise(process.getName()) + "_OUT";
	}

	public String getPartnerLinkName() {
		return BPELConstants.normalise(process.getName()) + BPELConstants.APPEND_SERVICE;
	}

	public String getOperationName() {
		return BPELConstants.normalise(process.getName());
	}

	public String getTargetNamespace() {
		return BPELConstants.THIS_NAMESPACE + "/wsdl";
	}

	public AbstractInPort getPortIN() {
		return portIN;
	}

	public OutputPort getPortOUT() {
		return portOUT;
	}

	public QName getPortType() {
		return new QName(BPELConstants.THIS_NAMESPACE + "/wsdl", BPELConstants.normalise(process.getName()) + BPELConstants.APPEND_PORTTYPE);
	}

	public TImport getImport() {
		TImport toReturn = new TImport();
		toReturn.setImportType(BPELConstants.NAMESPACE_WSDL);
		toReturn.setLocation(BPELConstants.normalise(process.getName()) + ".wsdl");
		toReturn.setNamespace(BPELConstants.THIS_NAMESPACE + "/wsdl");
		return toReturn;

	}

	public TPartnerLink getTPartnerLink() {
		TPartnerLink toReturn = new TPartnerLink();
		toReturn.setName(getPartnerLinkName());
		toReturn.setPartnerLinkType(new QName(BPELConstants.THIS_NAMESPACE + "/wsdl", BPELConstants.normalise(process.getName())
				+ BPELConstants.APPEND_PARTNERLINKTYPE));
		toReturn.setMyRole(process.getName() + BPELConstants.APPEND_ROLE);
		return toReturn;
	}

	public Vector<TVariable> getVariables() {
		Vector<TVariable> toReturn = new Vector<TVariable>();
		TVariable varIN = new TVariable();
		varIN.setMessageType(getMessageINType());
		varIN.setName(getVarIN());

		TVariable varOUT = new TVariable();
		varOUT.setMessageType(getMessageOUTType());
		varOUT.setName(getVarOUT());

		toReturn.add(varIN);
		toReturn.add(varOUT);
		return toReturn;
	}

	public QName getMessageINType() {
		String type = portIN.getInputMessageDefinition().getName();
		return new QName(BPELConstants.THIS_NAMESPACE + "/wsdl", type);
	}

	public QName getMessageOUTType() {
		String type = portOUT.getOutputMessageDefinition().getName();
		return new QName(BPELConstants.THIS_NAMESPACE + "/wsdl", type);
	}

	public TReceive getReceive() {
		TReceive toReturn = new TReceive();
		toReturn.setCreateInstance(TBoolean.YES);
		toReturn.setPartnerLink(getPartnerLinkName());
		toReturn.setOperation(getOperationName());
		// TO-DO : fix the xsd for allowing QNames in here
		toReturn.setPortType(getPortType());
		toReturn.setVariable(getVarIN());
		return toReturn;
	}

	public TReply getReply() {
		TReply toReturn = new TReply();
		toReturn.setPartnerLink(getPartnerLinkName());
		toReturn.setPortType(getPortType());
		toReturn.setVariable(getVarOUT());
		toReturn.setOperation(getOperationName());
		return toReturn;
	}

	@Override
	public TPartnerLinkType getTPartnerLinkType() {
		TPartnerLinkType toReturn = new TPartnerLinkType();
		toReturn.setName(BPELConstants.normalise(process.getName()) + BPELConstants.APPEND_PARTNERLINKTYPE);

		// for now on, only one port type is supported per service

		TRole role = new TRole();
		role.setName(BPELConstants.normalise(process.getName()) + BPELConstants.APPEND_ROLE);
		role.setPortType(getPortType());
		toReturn.getRole().add(role);

		return toReturn;
	}

	public FlexoProcess getProcess() {
		return process;
	}

	// Namespace prefixes should be properly treated as well...
	public String[] getPartNameForFlexoVariable(String fv) {
		System.out.println("Exported variables " + this + ": " + flexoVarNameToPartIN + flexoVarNameToPartOUT);
		String[] toReturn = new String[2];
		Enumeration<String> en = flexoVarNameToPartIN.keys();
		// <processInstance.businessData.serviceIN,part1)
		while (en.hasMoreElements()) {
			String currentVar = en.nextElement();
			if (fv.indexOf(currentVar + ".") != -1) {
				String varAndPart = "$" + getVarIN() + "." + flexoVarNameToPartIN.get(currentVar);
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
				String varAndPart = "$" + getVarOUT() + "." + flexoVarNameToPartOUT.get(currentVar);
				toReturn[0] = varAndPart;
				String replaced = fv.substring(0, fv.indexOf(currentVar) + currentVar.length());
				toReturn[1] = replaced;
				return toReturn;
			}
		}
		return null;
	}

}
