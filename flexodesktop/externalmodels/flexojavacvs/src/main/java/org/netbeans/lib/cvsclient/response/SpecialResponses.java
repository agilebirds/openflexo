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

package org.netbeans.lib.cvsclient.response;

/**
 * Defines few hacks needed to support special and error cases.
 * 
 * @author Petr Kuzel
 */
final class SpecialResponses {

	/**
	 * Sometimes server aborts without sending <tt>error</tt> terminating response. It sends just message reponse ending with following
	 * text.
	 * <p>
	 * It could terminate prematurely, ignoring <tt>error</tt> that should be probably the next response. AFAIK it's harmless.
	 * <p>
	 * <a ref="http://tasklist.netbeans.org/issues/show_bug.cgi?id=56552">#56552</a>
	 * 
	 */
	public static final String SERVER_ABORTED = " [server aborted]: received termination signal"; // NOI18N

	/**
	 * Sometimes server aborts without sending <tt>error</tt> terminating response. It sends just message reponse ending with following
	 * text.
	 * <p>
	 * It could terminate prematurely, ignoring <tt>error</tt> that should be probably the next response. AFAIK it's harmless.
	 * <p>
	 * <a ref="http://tasklist.netbeans.org/issues/show_bug.cgi?id=56552">#56552</a>
	 * 
	 */
	public static final String SERVER_ABORTED_2 = " [server aborted]: received broken pipe signal"; // NOI18N

	/**
	 * Sometimes server aborts without sending <tt>error</tt> terminating response. It sends just message reponse ending with following
	 * text.
	 * <p>
	 * It could terminate prematurely, ignoring <tt>error</tt> that should be probably the next response. AFAIK it's harmless.
	 * <p>
	 * <a ref="http://tasklist.netbeans.org/issues/show_bug.cgi?id=63376">#63376</a>
	 * 
	 */
	public static final String SERVER_ABORTED_3 = " [checkout aborted]: end of file from server (consult above messages if any)"; // NOI18N
}
