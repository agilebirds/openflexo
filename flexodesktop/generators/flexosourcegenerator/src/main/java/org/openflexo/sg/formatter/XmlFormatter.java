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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format.TextMode;
import org.openflexo.sg.formatter.exception.FormattingException;
import org.openflexo.toolbox.FileFormat;

public class XmlFormatter implements Formatter {

	private final Format format;

	protected XmlFormatter() {

		format = Format.getRawFormat();
		format.setIndent("\t");
		format.setTextMode(TextMode.TRIM);
	}

	@Override
	public String format(String input) throws FormattingException {

		InputStream in = null;
		try {
			in = new ByteArrayInputStream(input.getBytes("utf-8"));
			XMLOutputter outputter = new XMLOutputter(format);
			SAXBuilder sxb = new SAXBuilder(false);
			// This remove the DTD parsing (faster) and additionally it avoid the defaut attributes to be included
			sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Document result = sxb.build(in);
			return outputter.outputString(result);
		} catch (Exception e) {
			throw new FormattingException(e.getMessage(), getFileFormat());
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@Override
	public FileFormat getFileFormat() {
		return FileFormat.XML;
	}
}
