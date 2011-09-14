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
package org.openflexo.tm.java.formatter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openflexo.sg.formatter.Formatter;
import org.openflexo.sg.formatter.exception.FormattingException;
import org.openflexo.toolbox.FileFormat;


public class JavaFormatter implements Formatter {

	private static final Logger logger = Logger.getLogger(JavaFormatter.class.getPackage().getName());

	private static final String DEFAULT_JAVA_FORMATTER_SETTINGS_RESOURCEPATH = "/Java/Formatter/defaultjavaformatter.xml";

	private final CodeFormatter formatter;

	public JavaFormatter() {
		formatter = org.eclipse.jdt.core.ToolFactory.createCodeFormatter(buildFormatSettingsMap());
	}

	@Override
	public synchronized String format(String input) throws FormattingException {

		TextEdit te = formatter.format(CodeFormatter.K_UNKNOWN, input, 0, input.length(), 0, "\n");
		IDocument dc = new Document(input);
		try {
			te.apply(dc);
			return dc.get();
		} catch (Exception e) {
			throw new FormattingException(e.getMessage(), getFileFormat());
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> buildFormatSettingsMap() {
		Map<String, String> options = new HashMap<String, String>();

		InputStream inputStream = null;
		try
		{
			inputStream = getClass().getResourceAsStream(DEFAULT_JAVA_FORMATTER_SETTINGS_RESOURCEPATH);
			SAXBuilder sxb = new SAXBuilder();
			org.jdom.Document document = sxb.build(getClass().getResourceAsStream(DEFAULT_JAVA_FORMATTER_SETTINGS_RESOURCEPATH));
			Element root = document.getRootElement();
			Element profile = root.getChild("profile");
			for (Element settingElement : ((List<Element>) profile.getChildren("setting"))) {
				options.put(settingElement.getAttributeValue("id"), settingElement.getAttributeValue("value"));
			}

			return options;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Cannot load java formatter options ! The java formatter will be initialized with its default values. Cause: " + e.getMessage(), e);
			e.printStackTrace();
			return options;
		}
		finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileFormat getFileFormat() {
		return FileFormat.JAVA;
	}
}
