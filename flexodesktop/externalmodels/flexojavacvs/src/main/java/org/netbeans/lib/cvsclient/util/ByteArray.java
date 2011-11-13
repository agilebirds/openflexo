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

/**
 * The growable array of bytes. This class in not thread safe.
 * 
 * @author Martin Entlicher
 */
public class ByteArray extends Object {

	private byte[] bytesBuffer;
	private int length;

	/** Creates a new instance of ByteArray */
	public ByteArray() {
		bytesBuffer = new byte[50];
		length = 0;
	}

	/**
	 * Add a byte to the byte array.
	 */
	public void add(byte b) {
		if (bytesBuffer.length <= length) {
			byte[] newBytesBuffer = new byte[length + length / 2];
			System.arraycopy(bytesBuffer, 0, newBytesBuffer, 0, bytesBuffer.length);
			bytesBuffer = newBytesBuffer;
		}
		bytesBuffer[length++] = b;
	}

	/**
	 * Get the array of bytes.
	 */
	public byte[] getBytes() {
		byte[] bytes = new byte[length];
		System.arraycopy(bytesBuffer, 0, bytes, 0, length);
		return bytes;
	}

	/**
	 * Get the String representation of bytes in this array. The bytes are decoded using the platform's default charset.
	 */
	public String getStringFromBytes() {
		return new String(bytesBuffer, 0, length);
	}

	/**
	 * Reset the byte array to zero length.
	 */
	public void reset() {
		length = 0;
	}
}
