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

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;

/**
 * This class modifies a connection by gzipping all client/server communication
 * 
 * @author Robert Greig
 */
public class GzipModifier extends Object implements ConnectionModifier {
	/**
	 * Creates new GzipModifier
	 */
	public GzipModifier() {
	}

	@Override
	public void modifyInputStream(LoggedDataInputStream ldis) throws IOException {
		// System.err.println("Setting the underlying stream for the IS");
		GZIPInputStream gzis = new GZIPInputStream(ldis.getUnderlyingStream());
		// System.err.println("Finished constructing the gzipinputstream");
		ldis.setUnderlyingStream(gzis);
	}

	@Override
	public void modifyOutputStream(LoggedDataOutputStream ldos) throws IOException {
		// System.err.println("Setting the underlying stream for the OS");
		ldos.setUnderlyingStream(new GZIPOutputStream(ldos.getUnderlyingStream()));
	}
}
