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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openflexo.docxparser.dto.ParsedDocx;
import org.openflexo.docxparser.dto.ParsedHtml;
import org.openflexo.docxparser.dto.api.IParsedFlexoContent;
import org.openflexo.docxparser.dto.api.IParsedFlexoDescription;
import org.openflexo.docxparser.dto.api.IParsedFlexoName;
import org.openflexo.docxparser.dto.api.IParsedFlexoTitle;
import org.openflexo.docxparser.flexotag.FlexoContentTag;
import org.openflexo.docxparser.flexotag.FlexoDescriptionTag;
import org.openflexo.docxparser.flexotag.FlexoNameTag;
import org.openflexo.docxparser.flexotag.FlexoTitleTag;

public class DocxFileParser {
	protected static final Logger logger = Logger.getLogger(DocxFileParser.class.getPackage().toString());

	private Set<String> availableCssClasses;
	private String resourcesDirectory;

	private OPCPackage filePackage;
	private PackagePart documentPart = null;
	private Document documentXml = null;

	public DocxFileParser(byte[] documentBytes, Set<String> availableCssClasses, String resourcesDirectory) throws InvalidFormatException {
		ByteArrayInputStream in = new ByteArrayInputStream(documentBytes);

		try {
			initialize(in, availableCssClasses, resourcesDirectory);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public DocxFileParser(InputStream in, Set<String> availableCssClasses, String resourcesDirectory) throws InvalidFormatException {
		initialize(in, availableCssClasses, resourcesDirectory);
	}

	private void initialize(InputStream in, Set<String> availableCssClasses, String resourcesDirectory) throws InvalidFormatException {
		if (availableCssClasses == null) {
			this.availableCssClasses = new HashSet<String>();
		} else {
			this.availableCssClasses = availableCssClasses;
		}

		this.resourcesDirectory = resourcesDirectory;

		try {
			filePackage = OPCPackage.open(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public ParsedDocx getParsedDocx() {
		ParsedDocx parsedDocx = new ParsedDocx();

		List<?> resultList = getDocumentXml().selectNodes(
				"//w:sdt/w:sdtPr[not(w:showingPlcHdr)]/w:tag[" + "starts-with(@w:val, '" + FlexoDescriptionTag.FLEXODESCRIPTIONTAG
						+ "') or " + "starts-with(@w:val, '" + FlexoNameTag.FLEXONAMETAG + "') or " + "starts-with(@w:val, '"
						+ FlexoTitleTag.FLEXOTITLETAG + "') or " + "starts-with(@w:val, '" + FlexoContentTag.FLEXOCONTENTTAG + "')" + "]");
		for (Iterator<?> iterator = resultList.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			String tagValue = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));

			try {
				if (tagValue.startsWith(FlexoDescriptionTag.FLEXODESCRIPTIONTAG)) {
					FlexoDescriptionTag descTag = new FlexoDescriptionTag(tagValue);

					element = element.getParent().getParent(); // On w:sdt
					Element sdtContentElement = element.element(DocxQName.getQName(OpenXmlTag.w_sdtContent));

					ParsedHtml parsedHtml = OpenXml2Html.getHtml(sdtContentElement, getDocumentPart(), availableCssClasses,
							resourcesDirectory);

					IParsedFlexoDescription parsedFlexoDescription = parsedDocx.getOrCreateParsedDescription(descTag.getFlexoId(),
							descTag.getUserId());
					parsedFlexoDescription.addHtmlDescription(descTag.getTarget(), parsedHtml);
				} else if (tagValue.startsWith(FlexoNameTag.FLEXONAMETAG)) {
					FlexoNameTag nameTag = new FlexoNameTag(tagValue);

					element = element.getParent().getParent(); // On w:sdt
					Element sdtContentElement = element.element(DocxQName.getQName(OpenXmlTag.w_sdtContent));

					StringBuilder sb = new StringBuilder();
					Iterator<?> iteratorWt = sdtContentElement.selectNodes("descendant::w:t").iterator();
					while (iteratorWt.hasNext()) {
						Element textElement = (Element) iteratorWt.next();
						sb.append(textElement.getText());
					}

					if (sb.toString().trim().length() > 0) {
						IParsedFlexoName parsedFlexoName = parsedDocx.getOrCreateParsedName(nameTag.getFlexoId(), nameTag.getUserId());
						parsedFlexoName.setFlexoName(sb.toString().trim());
					}
				} else if (tagValue.startsWith(FlexoTitleTag.FLEXOTITLETAG)) {
					FlexoTitleTag TitleTag = new FlexoTitleTag(tagValue);

					element = element.getParent().getParent(); // On w:sdt
					Element sdtContentElement = element.element(DocxQName.getQName(OpenXmlTag.w_sdtContent));

					StringBuilder sb = new StringBuilder();
					Iterator<?> iteratorWt = sdtContentElement.selectNodes("descendant::w:t").iterator();
					while (iteratorWt.hasNext()) {
						Element textElement = (Element) iteratorWt.next();
						sb.append(textElement.getText());
					}

					if (sb.toString().trim().length() > 0) {
						IParsedFlexoTitle parsedFlexoTitle = parsedDocx.getOrCreateParsedTitle(TitleTag.getFlexoId(), TitleTag.getUserId());
						parsedFlexoTitle.setFlexoTitle(sb.toString().trim());
					}
				} else if (tagValue.startsWith(FlexoContentTag.FLEXOCONTENTTAG)) {
					FlexoContentTag contentTag = new FlexoContentTag(tagValue);

					element = element.getParent().getParent(); // On w:sdt
					Element sdtContentElement = element.element(DocxQName.getQName(OpenXmlTag.w_sdtContent));

					ParsedHtml parsedHtml = OpenXml2Html.getHtml(sdtContentElement, getDocumentPart(), availableCssClasses,
							resourcesDirectory);

					IParsedFlexoContent parsedFlexoContent = parsedDocx.getOrCreateParsedContent(contentTag.getFlexoId(),
							contentTag.getUserId());
					parsedFlexoContent.setFlexoContent(parsedHtml);
				}
			} catch (FlexoDescriptionTag.FlexoTagFormatException e) {
				logger.log(Level.WARNING, "Cannot parse tag from a building block which seems to be a Flexo Tag", e);
			}
		}

		return parsedDocx;
	}

	public void close() {
		try {
			filePackage.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public OPCPackage getFilePackage() {
		return filePackage;
	}

	public PackagePart getDocumentPart() {
		if (documentPart == null) {
			PackageRelationship documentRelationship = getFilePackage().getRelationshipsByType(DocxXmlUtil.RELATIONSHIPTYPE_COREDOCUMENT)
					.getRelationship(0);
			documentPart = getFilePackage().getPart(documentRelationship);
		}

		return documentPart;
	}

	public Document getDocumentXml() {
		if (documentXml == null) {
			try {
				documentXml = new SAXReader().read(getDocumentPart().getInputStream());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return documentXml;
	}
}
