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

package org.openflexo.xmlcode.examples.example2;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Class <code>CommandsList</code> is intended to represent a list of commands stored in a hashtable where key is a CommandIdentifier
 * object.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class CommandsList extends Hashtable<CommandIdentifier, Command> {

	private SellReport relatedSellReport;

	protected int commandsNumber;

	public CommandsList() {
		super();
		commandsNumber = 0;
	}

	@Override
	public Command get(Object key) {
		return super.get(key);
	}

	@Override
	public Command put(CommandIdentifier key, Command value) {
		if (relatedSellReport != null) {
			value.setRelatedSellReport(relatedSellReport);
		}
		commandsNumber++;
		return super.put(key, value);
	}

	public Object remove(CommandIdentifier key) {
		Command value = get(key);
		if (relatedSellReport != null) {
			value.setRelatedSellReport(null);
		}
		commandsNumber--;
		return super.remove(key);
	}

	public void setRelatedSellReport(SellReport aSellReport) {
		relatedSellReport = aSellReport;
		if (size() > 0) {
			for (Enumeration<Command> e = elements(); e.hasMoreElements();) {
				e.nextElement().setRelatedSellReport(aSellReport);
			}
		}

	}

	@Override
	public String toString() {
		String returnedString = "CommandsList (" + size() + " commands)\n";

		if (size() > 0) {
			for (Enumeration<CommandIdentifier> e = keys(); e.hasMoreElements();) {
				CommandIdentifier key = e.nextElement();
				returnedString += "[" + key + "] " + get(key);
			}
		}
		return returnedString;
	}

}
