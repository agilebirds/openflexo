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
 * Represents Message object
 * 
 * @version $Id: Message.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Message extends QuidObject implements Named {
	public Message(PetalNode parent, Collection params) {
		super(parent, "Message", params);
	}

	public Message() {
		super("Message");
	}

	@Override
	public void setNameParameter(String o) {
		params.set(0, o);
	}

	@Override
	public String getNameParameter() {
		return (String) params.get(0);
	}

	public String getFrequency() {
		return getPropertyAsString("frequency");
	}

	public void setFrequency(String o) {
		defineProperty("frequency", o);
	}

	public String getSynchronization() {
		return getPropertyAsString("synchronization");
	}

	public void setSynchronization(String o) {
		defineProperty("synchronization", o);
	}

	public String getDir() {
		return getPropertyAsString("dir");
	}

	public void setDir(String o) {
		defineProperty("dir", o);
	}

	public String getSequence() {
		return getPropertyAsString("sequence");
	}

	public void setSequence(String o) {
		defineProperty("sequence", o);
	}

	public int getOrdinal() {
		return getPropertyAsInteger("ordinal");
	}

	public void setOrdinal(int o) {
		defineProperty("ordinal", o);
	}

	public String getOperation() {
		return getPropertyAsString("Operation");
	}

	public void setOperation(String o) {
		defineProperty("Operation", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
