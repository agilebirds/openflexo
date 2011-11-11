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

package org.netbeans.lib.cvsclient.connection;

import org.netbeans.lib.cvsclient.CVSRoot;

/**
 * Simple class for managing the mapping from CVSROOT specifications to Connection classes.
 * 
 * @author <a href="mailto:gerrit.riessen@wiwi.hu-berlin.de">Gerrit Riessen</a>, OAR Development AG
 * @author <a href="mailto:rami.ojares@elisa.fi">Rami Ojares</a>, Elisa Internet Oy
 */
public class ConnectionFactory {

	/**
	 * <b>Protected Constructor</b>
	 */
	protected ConnectionFactory() {
	}

	/**
	 * Returns a Connection object to handle the specific CVSRoot specification. This returns null if not suitable connection was found.
	 * 
	 * If the return value is an instance of the PServerConnection class, then the encoded password needs to be set if not defined in the
	 * CVSRoot. This is left up to the client to set.
	 */
	public static Connection getConnection(String cvsRoot) throws IllegalArgumentException {

		CVSRoot root = CVSRoot.parse(cvsRoot);
		return getConnection(root);

	}

	/**
	 * Returns a Connection object to handle the specific CVSRoot specification. This returns null if not suitable connection was found.
	 * 
	 * If the return value is an instance of the PServerConnection class, then the encoded password needs to be set if not defined in the
	 * CVSRoot. This is left up to the client to set.
	 */
	public static Connection getConnection(CVSRoot root) throws IllegalArgumentException {

		// LOCAL CONNECTIONS (no-method, local & fork)
		if (root.isLocal()) {
			LocalConnection con = new LocalConnection();
			con.setRepository(root.getRepository());
			return con;
		}

		String method = root.getMethod();
		// SSH2Connection (server, ext)
		/* SSH2Connection is TBD
		if (
		    method == null || CVSRoot.METHOD_SERVER == method || CVSRoot.METHOD_EXT == method
		) {
		    // NOTE: If you want to implement your own authenticator you have to construct SSH2Connection yourself
		    SSH2Connection con = new SSH2Connection(
		        root,
		        new ConsoleAuthenticator()
		    );
		    return con;
		}
		 */

		// PServerConnection (pserver)
		if (CVSRoot.METHOD_PSERVER == method) {
			PServerConnection con = new PServerConnection(root);
			return con;
		}

		throw new IllegalArgumentException("Unrecognized CVS Root: " + root);

	}

}
