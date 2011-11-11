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
package org.netbeans.lib.cvsclient.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A data output stream that also logs everything sent to a Writer (via the logger).
 * 
 * @author Robert Greig
 */
public class LoggedDataOutputStream extends FilterOutputStream {

	private long counter;

	/**
	 * Construct a logged stream using the specified underlying stream
	 * 
	 * @param out
	 *            the stream
	 */
	public LoggedDataOutputStream(OutputStream out) {
		super(out);
	}

	/**
	 * Write a line to the stream, logging it too. For compatibility reasons only. Does exactly the same what writeBytes() does.
	 * 
	 * @deprecated Line to to bytes conversion is host specifics. Use raw byte access methods insted.
	 * 
	 */
	@Deprecated
	public void writeChars(String line) throws IOException {
		writeBytes(line);
	}

	/**
	 * Write a line to the stream, logging it too.
	 * 
	 * Line to to bytes conversion is host specifics. Use {@link #writeBytes(String, String)} if possible.
	 */
	public void writeBytes(String line) throws IOException {
		byte[] bytes = line.getBytes();
		out.write(bytes);
		Logger.logOutput(bytes);
		counter += bytes.length;
	}

	/**
	 * Write a line to the stream, logging it too.
	 */
	public void writeBytes(String line, String encoding) throws IOException {
		byte[] bytes = line.getBytes(encoding);
		out.write(bytes);
		Logger.logOutput(bytes);
		counter += bytes.length;
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		counter++;
	}

	@Override
	public void write(byte b[]) throws IOException {
		super.write(b);
		counter += b.length;
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		super.write(b, off, len);
		counter += len;
	}

	/**
	 * Closes this input stream and releases any system resources associated with the stream.
	 */
	@Override
	public void close() throws IOException {
		out.close();
	}

	public OutputStream getUnderlyingStream() {
		return out;
	}

	public void setUnderlyingStream(OutputStream os) {
		out = os;
	}

	public long getCounter() {
		return counter;
	}
}