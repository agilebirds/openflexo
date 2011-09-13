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
import org.oasis_open.docs.wsbpel._2_0.process.executable.TImport;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TPartnerLink;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.WSPortType;


public class BPELPartnerLink implements BPELPartnerLinkInterface{
	private FlexoProcess process;
	private ServiceInterface serviceInterface;
	private Hashtable<String,BPELPartnerLinkInvocation> invocations;
	
	private String name;
	private QName partnerLinkType;

	
	
	public BPELPartnerLink(FlexoProcess pro) {
		process=pro;
		invocations=new Hashtable<String, BPELPartnerLinkInvocation>();
		serviceInterface=pro.getServiceInterfaces().get(0);
		name=BPELConstants.normalise(process.getName());
		partnerLinkType=new QName(serviceInterface.getParentService().getTargetNamespace(),getName()+BPELConstants.APPEND_PARTNERLINKTYPE);
		System.out.println("Service namespace : " + serviceInterface.getParentService().getTargetNamespace());
		BPELNamespacePrefixMapperFactory.addNamespaceAndPrefix(serviceInterface.getParentService().getTargetNamespace(), null);
		
	}
	
	public BPELPartnerLinkInvocation getInvocation(String name) {
		return invocations.get(name);
	}
	
	public String getName() {
		return name;
	}
	
	public QName getPartnerLinkType() {
		return partnerLinkType;
	}
	
	public void addInvocation(BPELPartnerLinkInvocation pli) {
		invocations.put(pli.getName(), pli);
	}
	
	public TImport getImport() {
		TImport toReturn=new TImport();
		toReturn.setImportType(BPELConstants.NAMESPACE_WSDL);
		toReturn.setLocation(BPELConstants.normalise(process.getName())+".wsdl");
		toReturn.setNamespace(serviceInterface.getParentService().getTargetNamespace());
		
		return toReturn;
	}
	
	public TPartnerLink getTPartnerLink() {
		TPartnerLink toReturn=new TPartnerLink();
		toReturn.setName(this.getName());
		toReturn.setPartnerLinkType(this.getPartnerLinkType());
		toReturn.setPartnerRole(BPELConstants.normalise(process.getName())+BPELConstants.APPEND_ROLE);
		return toReturn;
	}
	
	@Override
	public TPartnerLinkType getTPartnerLinkType() {
		TPartnerLinkType toReturn=new TPartnerLinkType();
		toReturn.setName(partnerLinkType.getLocalPart());
		
		// for now on, only one port type is supported per service
		WSPortType portType=serviceInterface.getParentService().getWSPortTypes().get(0);
	
		TRole role=new TRole();
		role.setName(BPELConstants.normalise(process.getName())+BPELConstants.APPEND_ROLE);
		role.setPortType(new QName(serviceInterface.getParentService().getTargetNamespace(),portType.getName()));
		toReturn.getRole().add(role);
		
		return toReturn;
	}
	
	
	/*
	 * TO-DO : this suppose that there is only one operation per PartnerLink
	 * THIS SUPPOSITION IS WRONG
	 */

	
	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	public Vector<BPELPartnerLinkInvocation> getAllInvocations() {
		Vector<BPELPartnerLinkInvocation> toReturn=new Vector<BPELPartnerLinkInvocation>();
		
		Enumeration en=invocations.elements();
		while (en.hasMoreElements()) {
			toReturn.add((BPELPartnerLinkInvocation)en.nextElement());
		}
		return toReturn;
	}
	
	
	@Override
	public String toString() {
		String toReturn=new String();
		toReturn+="BPELPartnerLink : \n";
		toReturn+="  servceInterface : "+serviceInterface.getName()+"\n";
		toReturn+=invocations.toString()+"\n";
		return toReturn;
	}
	
	
}
