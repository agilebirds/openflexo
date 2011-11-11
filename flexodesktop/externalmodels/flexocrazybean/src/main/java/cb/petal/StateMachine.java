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
 * Represents State_Machine object
 * 
 * @version $Id: StateMachine.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class StateMachine extends QuidObject {
	public StateMachine(PetalNode parent, Collection params) {
		super(parent, "State_Machine", params);
	}

	public StateMachine() {
		super("State_Machine");
	}

	public List getStates() {
		return (List) getProperty("states");
	}

	public void setStates(List o) {
		defineProperty("states", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
