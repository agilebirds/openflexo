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
 * Implements the Sticky request
 * 
 * @author Milos Kleint
 */
public final class StickyRequest extends Request {
	/**
	 * The sticky tag/date to send
	 */
	private String sticky;

	/**
	 * Construct a new Sticky request
	 * 
	 * @param theStickyTag
	 *            the sticky tag to use as an argument in the request
	 */
	public StickyRequest(String theStickyTag) {
		sticky = theStickyTag;
	}

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		if (sticky == null) {
			throw new UnconfiguredRequestException("Sticky tag has not been set");
		}

		return "Sticky " + sticky + "\n"; // NOI18N
	}

	public void setStickyTag(String tag) {
		sticky = tag;
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