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

public class BPELConstants {
	static final String NAMESPACE_WSDL = "http://schemas.xmlsoap.org/wsdl/";
	static final String NAMESPACE_BPEL = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
	static final String NAMESPACE_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String NAMESPACE_PLINKTYPE = "http://docs.oasis-open.org/wsbpel/2.0/plnktype";
	static final String APPEND_PROCESS = "Process";
	static final String APPEND_SERVICE = "Service";
	static final String APPEND_ROLE = "Role";
	static final String APPEND_PARTNERLINKTYPE = "PartnerLinkType";
	static final String APPEND_PORTTYPE = "PortType";
	static final String THIS_NAMESPACE = "http://default";

	public static String normalise(String n) {
		String toReturn = n.replaceAll(" ", "_");
		return toReturn;
	}

}
