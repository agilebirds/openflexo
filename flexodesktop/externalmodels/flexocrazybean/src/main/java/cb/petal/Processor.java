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
package cb.petal;

import java.util.Collection;

/**
 * Represents Processor object
 * 
 * @version $Id: Processor.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Processor extends QuidObject {
	public Processor(PetalNode parent, Collection params) {
		super(parent, "Processor", params);
	}

	public Processor() {
		super("Processor");
	}

	public void setNameParameter(String o) {
		params.set(0, o);
	}

	public String getNameParameter() {
		return params.get(0);
	}

	public String getDocumentation() {
		return getPropertyAsString("documentation");
	}

	public void setDocumentation(String o) {
		defineProperty("documentation", o);
	}

	public List getConnections() {
		return (List) getProperty("connections");
	}

	public void setConnections(List o) {
		defineProperty("connections", o);
	}

	public String getCharacteristics() {
		return getPropertyAsString("characteristics");
	}

	public void setCharacteristics(String o) {
		defineProperty("characteristics", o);
	}

	public String getScheduling() {
		return getPropertyAsString("scheduling");
	}

	public void setScheduling(String o) {
		defineProperty("scheduling", o);
	}

	public List getProcesses() {
		return (List) getProperty("processes");
	}

	public void setProcesses(List o) {
		defineProperty("processes", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
