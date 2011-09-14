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
package org.openflexo.sg.formatter;

import java.util.Hashtable;
import java.util.Map;

import org.openflexo.toolbox.FileFormat;


/**
 * @author Nicolas Daniels
 *
 */
public class FormatterFactory {

	private static Map<FileFormat, Formatter> recordedFormatter = new Hashtable<FileFormat, Formatter>();

	static {
		// Initialize default formatters
		recordFormatter(new XmlFormatter());
	}

	public static Formatter getFormater(FileFormat format) {
		return recordedFormatter.get(format);
	}

	public static void recordFormatter(Formatter formatter) {
		recordedFormatter.put(formatter.getFileFormat(), formatter);
	}
}
