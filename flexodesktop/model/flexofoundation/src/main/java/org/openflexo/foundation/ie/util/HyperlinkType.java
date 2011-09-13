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
package org.openflexo.foundation.ie.util;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of an hyperlink
 * 
 * @author sguerin
 * 
 */
public enum HyperlinkType implements StringConvertable
{

	FLEXOACTION("FlexoAction"),
	DISPLAYACTION("DisplayAction"),
	CANCEL("Cancel"),
	SEARCH("Search"),
	URL("URL"),
	MAILTO("Mail-to"),
	CONFIRM("Confirm"),
	IMAGE("Image"),
	CLIENTSIDESCRIPT("ClientSideScript"),
	HELP("HELP"),
	PRINT("PRINT");
	
	
	private final String serializationRepresentation;

	public String getSerializationRepresentation() {
		return serializationRepresentation;
	}
	
	HyperlinkType(String serializationRepresentation){
		this.serializationRepresentation = serializationRepresentation;
	}
	
	@Override
	public Converter getConverter() {
		return hyperlinkTypeConverter;
	}
	
	public boolean isFlexoAction() {
		return this==FLEXOACTION;
	}
	
	public boolean isDisplayAction() {
		return this==DISPLAYACTION;
	}
	
	public boolean isCancel() {
		return this==CANCEL;
	}
	
	public boolean isSearch() {
		return this==SEARCH;
	}
	
	public boolean isURL() {
		return this==URL;
	}
	
	public boolean isMailto() {
		return this==MAILTO;
	}
	
	public boolean isConfirm() {
		return this==CONFIRM;
	}
	
	public boolean isClientSideScript() {
		return this==CLIENTSIDESCRIPT;
	}
	
	public boolean isImage() {
		return this==IMAGE;
	}
	
	public boolean isHelp() {
		return this==HELP;
	}
	
	public boolean isPrint() {
		return this==PRINT;
	}
	
	public static final StringEncoder.EnumerationConverter<HyperlinkType> hyperlinkTypeConverter = new StringEncoder.EnumerationConverter<HyperlinkType>(HyperlinkType.class,"getSerializationRepresentation");

}
