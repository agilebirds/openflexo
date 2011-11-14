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
package org.openflexo.docxparser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DocxNumberingParser {
	private static final Logger logger = Logger.getLogger(DocxNumberingParser.class.getPackage().toString());

	private PackagePart documentPart;
	private PackagePart numberingPart;
	private Document numberingXml;

	public DocxNumberingParser(PackagePart documentPart) {
		this.documentPart = documentPart;
	}

	public boolean isOrderedNumbering(String numberingId, String levelNumber) {
		if (levelNumber == null) {
			levelNumber = "0";
		}

		Document xml = getNumberingXml();

		if (xml == null) {
			return false;
		}

		Element element = (Element) xml.selectSingleNode("/w:numbering/w:num[@w:numId = '" + numberingId + "']");

		if (element == null) {
			return false;
		}

		Element numberingFormatElement = (Element) element
				.selectSingleNode("w:lvlOverride[@w:ilvl = '" + levelNumber + "']/w:lvl/w:numFmt");

		if (numberingFormatElement == null) { // Get the abstract one
			Element abstractNumIdElement = element.element(DocxQName.getQName(OpenXmlTag.w_abstractNumId));
			if (abstractNumIdElement == null) {
				return false;
			}

			String abstractNumId = abstractNumIdElement.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));

			numberingFormatElement = (Element) xml.selectSingleNode("/w:numbering/w:abstractNum[@w:abstractNumId = '" + abstractNumId
					+ "']/w:lvl[@w:ilvl = '" + levelNumber + "']/w:numFmt");

			if (numberingFormatElement == null) {
				return false;
			}
		}

		String numberingFormat = numberingFormatElement.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));

		return numberingFormat != null && !"bullet".equals(numberingFormat);
	}

	public PackagePart getNumberingPart() {
		if (numberingPart == null) {
			PackageRelationshipCollection packageRelationships;
			try {
				packageRelationships = documentPart.getRelationshipsByType(DocxXmlUtil.RELATIONSHIPTYPE_NUMBERINGPART);
				if (packageRelationships.size() > 0) {
					PackagePartName parName = PackagingURIHelper.createPartName(packageRelationships.getRelationship(0).getTargetURI());
					numberingPart = documentPart.getPackage().getPart(parName);
				}
			} catch (InvalidFormatException e) {
				logger.log(Level.SEVERE, "Cannot get numbering relationship", e);
			}
		}

		return numberingPart;
	}

	public Document getNumberingXml() {
		if (numberingXml == null && getNumberingPart() != null) {
			try {
				numberingXml = new SAXReader().read(getNumberingPart().getInputStream());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return numberingXml;
	}
}
