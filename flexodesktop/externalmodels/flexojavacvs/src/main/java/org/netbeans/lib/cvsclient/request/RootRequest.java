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
package org.netbeans.lib.cvsclient.request;

/**
 * Implements the Root request
 * 
 * @author Robert Greig
 */
public final class RootRequest extends Request {
	/**
	 * The CVS root to send.
	 */
	private final String cvsRoot;

	/**
	 * Construct a new Root request
	 * 
	 * @param cvsRoot
	 *            the CVS root to use as an argument in the request
	 */
	public RootRequest(String cvsRoot) {
		if (cvsRoot == null) {
			throw new IllegalArgumentException("cvsRoot must not be null!"); // NOI18N
		}

		this.cvsRoot = cvsRoot;
	}

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		return "Root " + cvsRoot + "\n"; // NOI18N
	}

	/**
	 * Is a response expected from the server?
	 * 
	 * @return true if a response is expected, false if no response if expected
	 */
	@Override
	public boolean isResponseExpected() {
		return false;
	}
}