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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.dom4j.Namespace;

public class DocxXmlUtil
{
	private static final String NAMESPACEURL_WORDPROCESSINGML = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
	private static final String NAMESPACEURL_RELATIONSHIP = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
	private static final String NAMESPACEURL_DRAWINGMAIN = "http://schemas.openxmlformats.org/drawingml/2006/main";
	public static final Namespace NAMESPACE_WORDPROCESSINGML = new Namespace("w", NAMESPACEURL_WORDPROCESSINGML);
	public static final Namespace NAMESPACE_RELATIONSHIP = new Namespace("r", NAMESPACEURL_RELATIONSHIP);
	public static final Namespace NAMESPACE_DRAWINGMAIN = new Namespace("a", NAMESPACEURL_DRAWINGMAIN);
	
	public static final String RELATIONSHIPTYPE_COREDOCUMENT = PackageRelationshipTypes.CORE_DOCUMENT;
	public static final String RELATIONSHIPTYPE_IMAGEPART = PackageRelationshipTypes.IMAGE_PART;
	public static final String RELATIONSHIPTYPE_NUMBERINGPART = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/numbering";
	public static final String RELATIONSHIPTYPE_HYPERLINK = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink";
	
	
	public static Namespace getNamespace(String prefix)
	{
		if(NAMESPACE_WORDPROCESSINGML.getPrefix().equals(prefix))
			return NAMESPACE_WORDPROCESSINGML;
		else if(NAMESPACE_RELATIONSHIP.getPrefix().equals(prefix))
			return NAMESPACE_RELATIONSHIP;
		else if(NAMESPACE_DRAWINGMAIN.getPrefix().equals(prefix))
			return NAMESPACE_DRAWINGMAIN;
		
		throw new InvalidParameterException("Invalid prefix '" +prefix+ "' for getNamespace");
	}
	
	public static Namespace getNamespace(OpenXmlTag openXmlTag)
	{
		return getNamespace(openXmlTag.getPrefix());
	}
	
	public static byte[] getByteArrayFromInputStream(InputStream in) throws IOException
	{
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		
		while((length = in.read(buffer)) != -1)
		{
			byteArray.write(buffer, 0, length);
		}
		
		return byteArray.toByteArray();
	}
}
