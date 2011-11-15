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

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Class <code>SellReport</code> is intented to represent a sellers' month report.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class SellReport implements XMLSerializable {

	// Serialized fields
	protected Vendor vendor;

	protected CommandsList commands;

	// Computed fields
	protected float totalAmount;

	protected float totalPaidAmount;

	protected float totalUnpaidAmount;

	@Override
	public String toString() {
		String returnedString = "SellReport (totalAmount=" + totalAmount + ", totalPaidAmount=" + totalPaidAmount + ", totalUnpaidAmount="
				+ totalUnpaidAmount;
		if (vendor != null) {
			returnedString += ", vendor=" + vendor.toString();
		}
		returnedString += "\n";
		if (commands != null) {
			returnedString += commands.toString();
		}
		return returnedString;
	}

	public CommandsList getCommands() {
		return commands;
	}

	public void setCommands(CommandsList v) {
		commands = v;
		v.setRelatedSellReport(this);
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor v) {
		vendor = v;
	}

	public void setCommandForKey(Command aCommand, CommandIdentifier aKey) {
		System.out.println("setCommandForKey(Command,CommandIdentifier)");
		commands.put(aKey, aCommand);
	}

	public void removeCommandWithKey(Object aKey) {
		System.out.println("removeCommandWithKey(Object)");
		commands.remove(aKey);
	}

	public void removeCommandWithKey(CommandIdentifier aKey) {
		System.out.println("removeCommandWithKey(CommandIdentifier)");
		commands.remove(aKey);
	}

	public float getTotalAmount() {
		return totalAmount;
	}

	public void addToTotalAmount(float value) {
		totalAmount += value;
	}

	public void removeFromTotalAmount(float value) {
		totalAmount -= value;
	}

	public float getPaidAmount() {
		return totalPaidAmount;
	}

	public void addToPaidAmount(float value) {
		totalPaidAmount += value;
	}

	public void removeFromPaidAmount(float value) {
		totalPaidAmount -= value;
	}

	public float getUnpaidAmount() {
		return totalUnpaidAmount;
	}

	public void addToUnpaidAmount(float value) {
		totalUnpaidAmount += value;
	}

	public void removeFromUnpaidAmount(float value) {
		totalUnpaidAmount -= value;
	}

}
