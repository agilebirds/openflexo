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

/*
 * FlexoProcess.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 3, 2004
 */

import java.io.StringWriter;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.oasis_open.docs.wsbpel._2_0.plnktype.TPartnerLinkType;
import org.oasis_open.docs.wsbpel._2_0.process.executable.ObjectFactory;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TActivityContainer;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TAssign;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TBooleanExpr;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TCopy;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TDocumentation;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TEmpty;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TFlow;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TFrom;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TIf;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TInvoke;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TLiteral;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TProcess;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TReceive;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TReply;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TSequence;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TTo;
import org.openflexo.antar.Assignment;
import org.openflexo.antar.Conditional;
import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Flow;
import org.openflexo.antar.Nop;
import org.openflexo.antar.Sequence;
import org.openflexo.foundation.exec.InvalidModelException;
import org.openflexo.foundation.exec.NotSupportedException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.OutputPort;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
/*
=======
>>>>>>> 1.2

 */
public class BPELWriter
{
	private FlexoProcess process;
	private BPELExportedPartnerLink exp=null;
	private BPELPartnerLinkSet pLinks=null;

	public BPELWriter() {

	}

	public BPELWriter(FlexoProcess p) {
		System.out.println("Instanciating BPELWriter for process : "+p.getName());
		process=p;
		BPELPrettyPrinter.getInstance(this);
	}

	public BPELPartnerLinkSet getPartnerLinkSet() {
		return pLinks;
	}

	public BPELExportedPartnerLink getExportedPartnerLink() {
		return exp;
	}

	public String getStringPartnerLinkTypeDefinition(String pLinkName) throws BPELModelException{
		BPELPartnerLink pLink=pLinks.getPartnerLink(pLinkName);
		return getStringPartnerLinkTypeDefinition(pLink);
	}


	public String getStringPartnerLinkTypeDefinition(BPELPartnerLinkInterface pLink) throws BPELModelException {
		String toReturn;

		TPartnerLinkType pLinkType=pLink.getTPartnerLinkType();
		/* Serialisation */
		try {
			JAXBElement<TPartnerLinkType> el=new JAXBElement<TPartnerLinkType>(new QName(BPELConstants.NAMESPACE_PLINKTYPE,"partnerLinkType"),TPartnerLinkType.class,pLinkType);

			BPELNamespacePrefixMapper mapper=new BPELNamespacePrefixMapper();
			mapper.addNamespaceAndPrefix(BPELConstants.NAMESPACE_PLINKTYPE,"plink");
			toReturn=getXml(el,mapper, true);
		}
		catch (Exception e) {
			e.printStackTrace();
			toReturn=null;
		}
		return toReturn;

	}

	public Node getPartnerLinkTypeDefinition(String pLinkName) throws BPELModelException{
		BPELPartnerLink pLink=pLinks.getPartnerLink(pLinkName);
		return getPartnerLinkTypeDefinition(pLink);
	}

	public Node getPartnerLinkTypeDefinition(BPELPartnerLinkInterface pLink) throws BPELModelException {
		Node toReturn;

		TPartnerLinkType pLinkType=pLink.getTPartnerLinkType();
		/* Serialisation */
		try {
			JAXBElement<TPartnerLinkType> el=new JAXBElement<TPartnerLinkType>(new QName(BPELConstants.NAMESPACE_PLINKTYPE,"partnerLinkType"),TPartnerLinkType.class,pLinkType);

			BPELNamespacePrefixMapper mapper=new BPELNamespacePrefixMapper();
			mapper.addNamespaceAndPrefix(BPELConstants.NAMESPACE_PLINKTYPE,"plink");
			toReturn=getNode(el,mapper);
		}
		catch (Exception e) {
			e.printStackTrace();
			toReturn=null;
		}
		return toReturn;
	}


	public String write() throws BPELModelException {

		AbstractInPort portIN=null;
		OutputPort portOUT=null;
		if (process.getPortRegistery().getInOutPorts().size()==1) {
			portIN=(AbstractInPort) process.getPortRegistery().getInOutPorts().get(0);
			portOUT=((OutputPort)process.getPortRegistery().getInOutPorts().get(0));
		}
		else if(process.getPortRegistery().getNewPorts().size()==1 && process.getPortRegistery().getOutPorts().size()==1) {
			portIN=(AbstractInPort) process.getPortRegistery().getNewPorts().get(0);
			portOUT=(OutputPort) process.getPortRegistery().getOutPorts().get(0);
		}
		else if(process.getPortRegistery().getInPorts().size()==1 && process.getPortRegistery().getOutPorts().size()==1) {
			portIN=(AbstractInPort) process.getPortRegistery().getInPorts().get(0);
			portOUT=(OutputPort) process.getPortRegistery().getOutPorts().get(0);
		}
		else {
			throw new BPELModelException("There must be one and only one IN/OUT port defined for a FlexoProcess");
		}


		String toReturn=new String();
		TProcess tp=new TProcess();

		BPELControlGraphBuilder builder=new BPELControlGraphBuilder(portIN,portOUT);
		tp.setName(process.getName());
		tp.setTargetNamespace(BPELConstants.THIS_NAMESPACE);

		// build the structure of the partnerLinks;
		exp=new BPELExportedPartnerLink(process,portIN,portOUT);

		pLinks=new BPELPartnerLinkSet();

		System.out.println("Assinging plinks in "+this.toString());

		Vector<FlexoProcess> importedProcesses=process.getProject().getAllLocalFlexoProcesses();
		try {
			for (int i=0;i<importedProcesses.size();i++) {
				FlexoProcess currentPro=importedProcesses.elementAt(i);
				if (!currentPro.getIsWebService()) {
					continue;
				}
				pLinks.addPartnerLink(currentPro);
			}


			Vector<SubProcessNode> invokedWebServices=process.getAllSubProcessNodes();
			for (int i=0;i<invokedWebServices.size();i++) {
				SubProcessNode currentSubProcess=invokedWebServices.get(i);

				// we only have to import the WebServices SubProcess
				if (currentSubProcess==null || ! currentSubProcess.getIsWebService()) {
					continue;
				}

				//BPELPartnerLink plink=new BPELPartnerLink((ServiceInterface)currentSubProcess.getServiceInterfaces().get(0));
				System.out.println("* * * : Adding : "+currentSubProcess.getName());
				pLinks.addPartnerLink(currentSubProcess);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		tp.getImport().addAll(pLinks.getTImports());
		tp.getImport().add(exp.getImport());

		tp.setPartnerLinks(pLinks.getPartnerLinks());
		tp.getPartnerLinks().getPartnerLink().add(exp.getTPartnerLink());

		tp.setVariables(pLinks.getTVariables());
		tp.getVariables().getVariable().addAll(exp.getVariables());

		try {
			ControlGraph g=builder.makeControlGraph(false);
			tp.setSequence((TSequence)getBPELObject(g));
		}
		catch (InvalidModelException e) {
			throw new BPELModelException(e.getMessage());
		}
		catch (NotSupportedException e) {
			throw new BPELModelException(e.getMessage());
		}


		/* Serialisation */
		try {
			JAXBElement<TProcess> el=new JAXBElement<TProcess>(new QName(BPELConstants.NAMESPACE_BPEL,"process"),TProcess.class,tp);
			BPELNamespacePrefixMapper mapper=BPELNamespacePrefixMapperFactory.getInstance();
			mapper.addNamespaceAndPrefix(BPELConstants.NAMESPACE_BPEL,"");
			mapper.addNamespaceAndPrefix(BPELConstants.NAMESPACE_WSDL,"wsdl");
			mapper.addNamespaceAndPrefix(BPELConstants.NAMESPACE_PLINKTYPE,"plink");
			toReturn=getXml(el,mapper);
		}
		catch (Exception e) {
			e.printStackTrace();
			toReturn="An error occured while serializing BPEL source; See java stack for more trace.";
		}
		return toReturn;
	}

	private Object getBPELObject(ControlGraph g) throws BPELInvalidModelException {
		if (g==null) {
			return new TEmpty();
		}
		if (g instanceof BPELWSAPI) {
			TSequence tSeq=new TSequence();

			TReceive rec=exp.getReceive();
			tSeq.getActivity().add(rec);

			// the elements starting from a BPELWSAPI are in a sequence...
			// but we've already created a sequence with the receive and reply
			// we therefore do not recreate one, and just copy the elments.
			TSequence receivedSeq=(TSequence)getBPELObject(((BPELWSAPI)g).getControlGraph());
			for (Object o:receivedSeq.getActivity()) {
				if (o!=null) {
					tSeq.getActivity().add(o);
				}
			}
			TReply rep=exp.getReply();
			tSeq.getActivity().add(rep);
			return tSeq;
		}

		if (g instanceof Sequence) {
			TSequence tSeq=new TSequence();
			Sequence seq=((Sequence)g);
			for (ControlGraph currentGraph:seq.getStatements()) {
				if (currentGraph!=null) {
					tSeq.getActivity().add(getBPELObject(currentGraph));
				}
			}
			return tSeq;
		}

		if (g instanceof Flow) {
			TFlow tFlow=new TFlow();
			Flow flow=((Flow)g);
			for (ControlGraph currentGraph:flow.getStatements()) {
				if (currentGraph!=null) {
					tFlow.getActivity().add(getBPELObject(currentGraph));
				}
			}
			return tFlow;
		}

		if (g instanceof Conditional) {
			Conditional cond=((Conditional)g);
			TIf tIf=new TIf();
			TBooleanExpr tEx=new TBooleanExpr();
			tIf.setCondition(tEx);
			if (cond.getCondition() != null) {
				String sEx=BPELPrettyPrinter.getInstance().getStringRepresentation(cond.getCondition());
				tEx.getContent().add(sEx);
			}
			else {
				TDocumentation doc=new TDocumentation();
				doc.getContent().add("the condition has to be manually specified here.");
				tIf.getDocumentation().add(doc);
				// should warn that no condition has been specified.
			}
			tIf.setSequence((TSequence)getBPELObject(cond.getThenStatement()));
			tIf.setElse(new TActivityContainer());
			tIf.getElse().setSequence((TSequence)getBPELObject(cond.getElseStatement()));
			return tIf;
		}

		if (g instanceof Nop) {
			return null;
		}

		if (g instanceof BPELWSInvocation) {
			BPELWSInvocation inv=((BPELWSInvocation)g);
			BPELPartnerLinkInvocation invoc=pLinks.findInvocation(inv.getSubProcessNode());
			if (invoc==null) {
				System.out.println("INVOC IS NULL : "+inv.getSubProcessNode().getName());
				TInvoke toReturn=new TInvoke();
				TDocumentation tDoc=new TDocumentation();
				tDoc.getContent().add("This invoke Activity should be manually created.");
				toReturn.getDocumentation().add(tDoc);
				return toReturn;
			} else {
				return invoc.getTInvoke();
			}
		}

		if (g instanceof Assignment) {
			Assignment ass=((Assignment)g);

			String variableName=ass.getReceiver().getName();
			/*
			if (ass.getAssignmentValue() instanceof BinaryOperatorExpression) {
				assignedValue=((BinaryOperatorExpression)ass.getAssignmentValue()).getRightArgument().toString();
			}
			else {
				System.out.println("Right value is not a variable : "+ass.getAssignmentValue().getClass().getName());
			}
			 */
			TAssign tAss=new TAssign();
			TCopy tCopy=new TCopy();
			tAss.getCopyOrExtensionAssignOperation().add(tCopy);
			TFrom tFrom=new TFrom();
			TTo tTo=new TTo();
			tCopy.setFrom(tFrom);
			tCopy.setTo(tTo);


			// tTo.getContent().add(getBPELMessagePartFromFlexoVariable(variableName)[0]);
			tTo.getContent().add(variableName);


			System.out.println("- - - - - - Getting expression for variable ");
			String value=BPELPrettyPrinter.getInstance().getStringRepresentation(ass.getAssignmentValue());

			if (value.indexOf("$")==-1) {
				TLiteral tLit=new TLiteral();
				tLit.getContent().add(value);
				tFrom.getContent().add((new ObjectFactory()).createLiteral(tLit));
			}
			else {
				//String value=BPELPrettyPrinter.getInstance().getStringRepresentation(ass.getAssignmentValue());
				tFrom.getContent().add(value);
				//String value=((Variable)((BinaryOperatorExpression)ass.getAssignmentValue;tFrom.getContent().add(value);
			}
			/*
			if (getBPELMessagePartFromFlexoVariable(assignedValue) != null) {
				tFrom.getContent().add(getBPELMessagePartFromFlexoVariable(assignedValue)[0]);
			}
			else {
				TLiteral tLit=new TLiteral();
				tLit.getContent().add(assignedValue);
				tFrom.getContent().add((new ObjectFactory()).createLiteral(tLit));
			}
			 */

			return tAss;
		}

		throw new BPELInvalidModelException("The Antar model could not be translated into BPEL : class "+g.getClass().getName()+ " is unknown");
	}


	public String[] getBPELMessagePartFromFlexoVariable(String v) {
		String[] toReturn=null;

		// first check in the exported PLink.
		if (exp != null) {
			toReturn=exp.getPartNameForFlexoVariable(v);
			if (toReturn != null) {
				return toReturn;
			}
		}


		System.out.println("plinks is null... writer id : "+this.toString());
		// then check in every other paartner link def.
		if (pLinks==null) {
			return null;
		}
		System.out.println(" * * * Checking in plinks");
		toReturn=pLinks.getBPELMessagePartFromFlexoVariable(v);

		return toReturn;
	}


	private String getXml(JAXBElement el, NamespacePrefixMapper mapper) throws Exception {
		return getXml(el,mapper,false);
	}


	private  String getXml(JAXBElement el, NamespacePrefixMapper mapper, boolean fragment) throws Exception {
		JAXBContext jContext=JAXBContext.newInstance("org.oasis_open.docs.wsbpel._2_0.process.executable:org.oasis_open.docs.wsbpel._2_0.plnktype");
		Marshaller myM=jContext.createMarshaller();

		if (mapper!=null) {
			myM.setProperty("com.sun.xml.bind.namespacePrefixMapper",mapper);
		}

		myM.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,new Boolean(true));

		if (fragment) {
			myM.setProperty(Marshaller.JAXB_FRAGMENT,new Boolean(true));
		}


		String output=new String();
		StringWriter sw=new StringWriter();


		myM.marshal(el, sw);

		output=sw.getBuffer().toString();
		return output;
	}

	private  Node getNode(JAXBElement el, NamespacePrefixMapper mapper) throws Exception {
		JAXBContext jContext=JAXBContext.newInstance("org.oasis_open.docs.wsbpel._2_0.process.executable:org.oasis_open.docs.wsbpel._2_0.plnktype");
		Marshaller myM=jContext.createMarshaller();

		if (mapper!=null) {
			myM.setProperty("com.sun.xml.bind.namespacePrefixMapper",mapper);
		}

		myM.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,new Boolean(true));

		DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc=builder.newDocument();

		if (mapper==null) {
			myM.setProperty(Marshaller.JAXB_FRAGMENT,new Boolean(true));
		}

		myM.marshal(el, doc);
		return doc.getFirstChild();
	}





}
