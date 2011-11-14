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

import org.netbeans.lib.cvsclient.admin.Entry;

/**
 * Sends an entry to the server, to tell the server which version of a file is on the local machine. The filename is relative to the most
 * recent Directory request. Note that if an
 * 
 * <pre>
 * Entry
 * </pre>
 * 
 * request is sent without
 * 
 * <pre>
 * Modified
 * </pre>
 * 
 * ,
 * 
 * <pre>
 * Is - Modified
 * </pre>
 * 
 * or
 * 
 * <pre>
 * Unchanged
 * </pre>
 * 
 * it means that the file is lost. Also note that if
 * 
 * <pre>
 * Modified
 * </pre>
 * 
 * ,
 * 
 * <pre>
 * Is - Modified
 * </pre>
 * 
 * or </pre>Unchanged</pre> is sent with
 * 
 * <pre>
 * Entry
 * </pre>
 * 
 * then </pre>Entry</pre> must be sent first.
 * 
 * @author Robert Greig
 * @see org.netbeans.lib.cvsclient.request.DirectoryRequest
 * @see org.netbeans.lib.cvsclient.request.ModifiedRequest
 * @see org.netbeans.lib.cvsclient.request.ModifiedRequest
 * @see org.netbeans.lib.cvsclient.request.UnchangedRequest
 */
public class EntryRequest extends Request {
	/**
	 * The Entry sent by this request
	 */
	private Entry entry;

	/**
	 * Create an EntryRequest
	 * 
	 * @param theEntry
	 *            the Entry to send
	 */
	public EntryRequest(Entry theEntry) {
		if (theEntry == null) {
			throw new IllegalArgumentException("EntryRequest: Entry must not " + "be null");
		}
		entry = theEntry;
	}

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 * @throws UnconfiguredRequestException
	 *             if the request has not been properly configured
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		return "Entry " + entry.toString() + "\n"; // NOI18N
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

	public Entry getEntry() {
		return entry;
	}

}