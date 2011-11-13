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
package org.netbeans.lib.cvsclient.command;

/**
 * Extended Builder interface. If implemented it's means that farmework must call this interface {@link #parseBytes} instead of
 * {@link Builder#parseLine}.
 * 
 * @author Petr Kuzel
 */
public interface BinaryBuilder {

	/**
	 * Raw binary data from stream. One <tt>Mbinary</tt> is typicaly splitted into several chunks.
	 * 
	 * @param chunk
	 *            one data chunk. It must be cloned if builer wants to retain data after finishing this callback.
	 * @param len
	 *            defines valid data length
	 */
	void parseBytes(byte[] chunk, int len);
}
