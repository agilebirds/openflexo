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
package org.netbeans.lib.cvsclient.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.netbeans.lib.cvsclient.request.GzipStreamRequest;
import org.netbeans.lib.cvsclient.request.Request;

/**
 * Handles the reading and writing of Compressed files to and from the server.
 * 
 * @author Milos Kleint
 */
public class GzippedFileHandler extends DefaultFileHandler {
	/**
	 * Indicates whether the file is actually compressed
	 */
	private boolean isCompressed;

	/**
	 * Get any requests that must be sent before commands are sent, to init this file handler.
	 * 
	 * @return an array of Requests that must be sent
	 */
	@Override
	public Request[] getInitialisationRequests() {
		return new Request[] { new GzipStreamRequest() };
	}

	@Override
	protected Reader getProcessedReader(File f) throws IOException {
		return new InputStreamReader(new GZIPInputStream(new FileInputStream(f)));
	}

	@Override
	protected InputStream getProcessedInputStream(File f) throws IOException {
		return new GZIPInputStream(new FileInputStream(f));
	}
}