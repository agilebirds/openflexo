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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * This input stream worked exactly like the normal DataInputStream except that it logs anything read to a file
 * 
 * @author Robert Greig
 */
public class LoggedDataInputStream extends FilterInputStream {

	private long counter;

	/**
	 * Construct a logged stream using the specified underlying stream
	 * 
	 * @param in
	 *            the stream
	 */
	public LoggedDataInputStream(InputStream in) {
		super(in);
	}

	/**
	 * Read a line (up to the newline character) from the stream, logging it too.
	 * 
	 * @deprecated It converts input data to string using {@link ByteArray#getStringFromBytes} that works only for ASCII without <tt>0</tt>.
	 *             Use <tt>byte</tt> access methods instead.
	 */
	@Deprecated
	public String readLine() throws IOException {
		return readLineBytes().getStringFromBytes();
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws EOFException
	 *             at stream end
	 */
	public ByteArray readLineBytes() throws IOException {
		int ch;
		boolean throwEOF = true;
		ByteArray byteArray = new ByteArray();
		loop: while (true) {
			if (Thread.interrupted()) {
				Thread.currentThread().interrupt();
				break;
			}
			if (in.available() == 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException iex) {
					Thread.currentThread().interrupt();
					break loop;
				}
				continue;
			}
			ch = in.read();
			counter++;
			switch (ch) {
			case -1:
				if (throwEOF) {
					throw new EOFException();
				}
			case '\n':
				break loop;
			default:
				byteArray.add((byte) ch);
			}
			throwEOF = false;
		}
		byte[] bytes = byteArray.getBytes();
		Logger.logInput(bytes);
		Logger.logInput('\n'); // NOI18N
		return byteArray;
	}

	/**
	 * Synchronously reads fixed chunk from the stream, logging it too.
	 * 
	 * @param len
	 *            blocks until specifid number of bytes is read.
	 */
	public byte[] readBytes(int len) throws IOException {
		int ch;
		ByteArray byteArray = new ByteArray();
		loop: while (len != 0) {
			if (Thread.interrupted()) {
				Thread.currentThread().interrupt();
				break;
			}
			if (in.available() == 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException iex) {
					Thread.currentThread().interrupt();
					break loop;
				}
				continue;
			}
			ch = in.read();
			counter++;
			switch (ch) {
			case -1:
				break loop;
			default:
				byteArray.add((byte) ch);
				len--;
			}
		}
		byte[] bytes = byteArray.getBytes();
		Logger.logInput(bytes);
		return bytes;
	}

	/**
	 * Closes this input stream and releases any system resources associated with the stream.
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

	/**
	 * Reads up to byte.length bytes of data from this input stream into an array of bytes.
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int read = in.read(b);
		if (read != -1) {
			Logger.logInput(b, 0, read);
			counter += read;
		}
		return read;
	}

	/**
	 * Reads up to len bytes of data from this input stream into an array of bytes
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = in.read(b, off, len);
		if (read != -1) {
			Logger.logInput(b, off, read);
			counter += read;
		}
		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		long skip = super.skip(n);
		if (skip > 0) {
			Logger.logInput(new String("<skipped " + skip + " bytes>").getBytes("utf8")); // NOI18N
			counter += skip;
		}
		return skip;
	}

	/**
	 * Interruptible read.
	 * 
	 * @throws InterruptedIOException
	 *             on thread interrupt
	 */
	@Override
	public int read() throws IOException {
		while (in.available() == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException iex) {
				Thread.currentThread().interrupt();
				throw new InterruptedIOException();
			}
		}

		int i = super.read();
		if (i != -1) {
			Logger.logInput((char) i);
			counter++;
		}
		return i;
	}

	public InputStream getUnderlyingStream() {
		return in;
	}

	public void setUnderlyingStream(InputStream is) {
		in = is;
	}

	public long getCounter() {
		return counter;
	}
}