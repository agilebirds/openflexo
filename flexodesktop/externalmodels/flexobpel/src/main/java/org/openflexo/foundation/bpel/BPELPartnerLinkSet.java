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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.oasis_open.docs.wsbpel._2_0.process.executable.TImport;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TPartnerLinks;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TVariable;
import org.oasis_open.docs.wsbpel._2_0.process.executable.TVariables;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.ServiceInterface;

public class BPELPartnerLinkSet {
	private Hashtable<String, BPELPartnerLink> partnerLinks;

	public BPELPartnerLinkSet() {
		partnerLinks = new Hashtable<String, BPELPartnerLink>();
	}

	/*
	 * Creates and adds a partnerLink for the specified serviceInvocation, in case it does not exist.
	 * It also registers the invocation into that partner Link.
	 */
	public void addPartnerLink(SubProcessNode pro) {
		// From the process, we need to look at the serviceInterface
		if (pro.getSubProcess().getServiceInterfaces() == null)
			return;
		if (pro.getSubProcess().getServiceInterfaces().size() == 0)
			return;
		ServiceInterface si = pro.getSubProcess().getServiceInterfaces().get(0);
		if (si == null)
			return;
		if (pro.getSubProcess() == null)
			return;
		BPELPartnerLink pLink = addPartnerLink(pro.getSubProcess());
		BPELPartnerLinkInvocation inv = new BPELPartnerLinkInvocation(pLink, pro);
		System.out.println("new partner link invocation :" + pro.getName());
		if (inv.hasOperation()) {
			pLink.addInvocation(inv);
		}
		return;
	}

	/*
	 * Adds a partner link in the collection and returns it.
	 * If it is already present, it just returns it.
	 */
	public BPELPartnerLink addPartnerLink(FlexoProcess pro) {
		if (!partnerLinks.containsKey(pro.getName())) {
			BPELPartnerLink pLink = new BPELPartnerLink(pro);
			partnerLinks.put(pro.getName(), pLink);

			return pLink;
		}

		return partnerLinks.get(pro.getName());
	}

	public BPELPartnerLink getPartnerLink(String name) {
		return partnerLinks.get(name);
	}

	public Collection<BPELPartnerLink> getAllPartnerLinks() {
		return partnerLinks.values();
	}

	@Override
	public String toString() {
		String toReturn = new String();
		toReturn = partnerLinks.toString();
		return toReturn;
	}

	public TPartnerLinks getPartnerLinks() {
		TPartnerLinks toReturn = new TPartnerLinks();
		Enumeration en = partnerLinks.elements();
		while (en.hasMoreElements()) {
			toReturn.getPartnerLink().add(((BPELPartnerLink) en.nextElement()).getTPartnerLink());
		}
		return toReturn;
	}

	public Vector<TImport> getTImports() {
		Vector<TImport> toReturn = new Vector<TImport>();
		Enumeration en = partnerLinks.elements();
		while (en.hasMoreElements()) {
			toReturn.add(((BPELPartnerLink) en.nextElement()).getImport());
		}
		return toReturn;
	}

	public TVariables getTVariables() {
		TVariables toReturn = new TVariables();
		Vector<BPELPartnerLinkInvocation> inv = getAllInvocations();
		for (int i = 0; i < inv.size(); i++) {
			TVariable varIN = new TVariable();
			varIN.setName(inv.get(i).getVarIN());
			varIN.setMessageType(inv.get(i).getMessageINType());

			TVariable varOUT = new TVariable();
			varOUT.setName(inv.get(i).getVarOUT());
			varOUT.setMessageType(inv.get(i).getMessageOUTType());

			toReturn.getVariable().add(varIN);
			toReturn.getVariable().add(varOUT);
		}

		return toReturn;
	}

	public Vector<BPELPartnerLinkInvocation> getAllInvocations() {
		Vector<BPELPartnerLinkInvocation> toReturn = new Vector<BPELPartnerLinkInvocation>();
		Enumeration en = partnerLinks.elements();
		while (en.hasMoreElements()) {
			toReturn.addAll(((BPELPartnerLink) en.nextElement()).getAllInvocations());
		}
		return toReturn;
	}

	public BPELPartnerLinkInvocation findInvocation(SubProcessNode n) {
		Vector<BPELPartnerLinkInvocation> allInvocations = getAllInvocations();
		for (int i = 0; i < allInvocations.size(); i++) {
			if (allInvocations.get(i).getSubProcessNode() == n) {
				return allInvocations.get(i);
			}
		}
		return null;
	}

	public String[] getBPELMessagePartFromFlexoVariable(String fv) {
		String[] toReturn = null;
		Vector<BPELPartnerLinkInvocation> allInvocations = getAllInvocations();
		for (int i = 0; i < allInvocations.size(); i++) {
			toReturn = allInvocations.get(i).getBPELPathForFlexoVariable(fv);
			if (toReturn != null)
				break;
		}
		return toReturn;

	}

}
