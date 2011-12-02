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
 * Sends the expand-modules request. This request expands the modules which have been specified in previous argument requests. The server
 * can assume this is a checkout or export.<br>
 * Expand is not the best word for what this request does. It does not expand a module in any meaningful way. What it does is ask the server
 * to tell you which working directories the server needs to know about in order to handle a checkout of a specific module. This is
 * important where you have aliased modules. If you alias module foo as bar, then you need to know when you do a checkout of foo that bar on
 * disk is an existing checkout of the module.
 * 
 * @author Robert Greig
 * @see org.netbeans.lib.cvsclient.response.ModuleExpansionResponse
 */
public class ExpandModulesRequest extends Request {
	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 * @throws UnconfiguredRequestException
	 *             if the request has not been properly configured
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		return "expand-modules \n"; // NOI18N
	}

	/**
	 * Is a response expected from the server?
	 * 
	 * @return true if a response is expected, false if no response if expected
	 */
	@Override
	public boolean isResponseExpected() {
		return true;
	}
}
