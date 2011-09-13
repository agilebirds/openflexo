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
package org.openflexo.icon;

import javax.swing.ImageIcon;

import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.FileFormat.BinaryFileFormat;
import org.openflexo.toolbox.FileFormat.DirectoryFormat;
import org.openflexo.toolbox.FileFormat.ImageFileFormat;
import org.openflexo.toolbox.FileFormat.TextFileFormat;

/**
 * Utility class containing all icons used in whole application
 * Note that this file will be relocated inside Flexo project for common icons, and in each module
 * for all module-specific icons
 * 
 * @author sylvain
 *
 */
public class FilesIconLibrary {

	// Big files
	public static final ImageIcon BIG_JAVA_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-x-java-2.png");
	public static final ImageIcon BIG_WOD_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/wo-wod.png");
	public static final ImageIcon BIG_API_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/wo-api.png");
	public static final ImageIcon BIG_TEXT_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-x-generic.png");
	public static final ImageIcon BIG_XML_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-xml.png");
	public static final ImageIcon BIG_HTML_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-html.png");
	public static final ImageIcon BIG_ANT_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-xml.png");
	public static final ImageIcon BIG_CSS_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-css.png");
	public static final ImageIcon BIG_JAVASCRIPT_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-javascript.png");
	public static final ImageIcon BIG_SQL_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-vnd.ms-access.png");
	public static final ImageIcon BIG_DOCX_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-msword.png");
	public static final ImageIcon BIG_SYSTEM_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-x-desktop.png");
	public static final ImageIcon BIG_LATEX_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-x-tex-2.png");
	public static final ImageIcon BIG_P_LIST_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-x-pascal-2.png");
	public static final ImageIcon BIG_XSD_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-xml.png");
	public static final ImageIcon BIG_OWL_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-xml.png");
	public static final ImageIcon BIG_WSDL_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/text-xml.png");

	public static final ImageIcon BIG_IMAGE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-vnd.stardivision.draw.png");

	public static final ImageIcon BIG_BINARY_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-octet-stream.png");
	public static final ImageIcon BIG_ZIP_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-x-compressed-tar.png");
	public static final ImageIcon BIG_JAR_ICON = new ImageIconResource("Icons/MimeTypes/64x64/application-x-java-archive.png");

	public static final ImageIcon BIG_FOLDER_ICON = new ImageIconResource("Icons/MimeTypes/64x64/folder.png");
	public static final ImageIcon BIG_WO_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/wo-wo.png");
	public static final ImageIcon BIG_EO_MODEL_ICON = new ImageIconResource("Icons/MimeTypes/64x64/folder.png");
	
	public static final ImageIcon BIG_UNKNOWN_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/unknown.png");
	public static final ImageIcon BIG_MISC_FILE_ICON = new ImageIconResource("Icons/MimeTypes/64x64/misc.png");
	
	public static ImageIcon bigIconForFileFormat(FileFormat format)
	{
		if (format instanceof TextFileFormat) {
			if (format == FileFormat.JAVA) {
				return BIG_JAVA_FILE_ICON;
			} else if (format == FileFormat.WOD) {
				return BIG_WOD_FILE_ICON;
			} else if (format == FileFormat.API) {
				return BIG_API_FILE_ICON;
			} else if (format == FileFormat.TEXT) {
				return BIG_TEXT_FILE_ICON;
			} else if (format == FileFormat.XML) {
				return BIG_XML_FILE_ICON;
			} else if (format == FileFormat.HTML) {
				return BIG_HTML_FILE_ICON;
			} else if (format == FileFormat.ANT) {
				return BIG_ANT_FILE_ICON;
			} else if (format == FileFormat.CSS) {
				return BIG_CSS_FILE_ICON;
			} else if (format == FileFormat.JS) {
				return BIG_JAVASCRIPT_ICON;
			} else if (format == FileFormat.SQL) {
				return BIG_SQL_ICON;
			} else if (format == FileFormat.DOCXML) {
				return BIG_DOCX_ICON;
			} else if (format == FileFormat.SYSTEM) {
				return BIG_SYSTEM_ICON;
			} else if (format == FileFormat.LATEX) {
				return BIG_LATEX_ICON;
			} else if (format == FileFormat.PLIST) {
				return BIG_P_LIST_ICON;
			} else if (format == FileFormat.WSDL) {
				return BIG_WSDL_FILE_ICON;
			} else if (format == FileFormat.XSD) {
				return BIG_XSD_FILE_ICON;
			} else if (format == FileFormat.OWL) {
				return BIG_OWL_FILE_ICON;
			} else {
				return BIG_MISC_FILE_ICON;
			}
		}
		
		else if (format instanceof DirectoryFormat) {
			if (format == FileFormat.EOMODEL) {
				return BIG_EO_MODEL_ICON;
			} else if (format == FileFormat.WO) {
				return BIG_WO_FILE_ICON;
			} else {
				return BIG_FOLDER_ICON;
			}
		}
		
		else if (format instanceof BinaryFileFormat) {
			if (format == FileFormat.ZIP) {
				return BIG_ZIP_ICON;
			} else if (format == FileFormat.JAR) {
				return BIG_JAR_ICON;
			} else {
				return BIG_BINARY_ICON;
			}
		}
		
		else if (format instanceof ImageFileFormat) {
			return BIG_IMAGE_ICON;
		}
		
		return BIG_UNKNOWN_FILE_ICON;
	}
	
	// MEDIUM files
	public static final ImageIcon MEDIUM_JAVA_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-x-java-2.png");
	public static final ImageIcon MEDIUM_WOD_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/wo-wod.png");
	public static final ImageIcon MEDIUM_API_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/wo-api.png");
	public static final ImageIcon MEDIUM_TEXT_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-x-generic.png");
	public static final ImageIcon MEDIUM_XML_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-xml.png");
	public static final ImageIcon MEDIUM_HTML_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-html.png");
	public static final ImageIcon MEDIUM_ANT_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-xml.png");
	public static final ImageIcon MEDIUM_CSS_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-css.png");
	public static final ImageIcon MEDIUM_JAVASCRIPT_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-javascript.png");
	public static final ImageIcon MEDIUM_SQL_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-vnd.ms-access.png");
	public static final ImageIcon MEDIUM_DOCX_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-msword.png");
	public static final ImageIcon MEDIUM_SYSTEM_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-x-desktop.png");
	public static final ImageIcon MEDIUM_LATEX_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-x-tex-2.png");
	public static final ImageIcon MEDIUM_P_LIST_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-x-pascal-2.png");
	public static final ImageIcon MEDIUM_XSD_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-xml.png");
	public static final ImageIcon MEDIUM_OWL_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-xml.png");
	public static final ImageIcon MEDIUM_WSDL_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/text-xml.png");

	public static final ImageIcon MEDIUM_IMAGE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-vnd.stardivision.draw.png");

	public static final ImageIcon MEDIUM_BINARY_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-octet-stream.png");
	public static final ImageIcon MEDIUM_ZIP_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-x-compressed-tar.png");
	public static final ImageIcon MEDIUM_JAR_ICON = new ImageIconResource("Icons/MimeTypes/32x32/application-x-java-archive.png");

	public static final ImageIcon MEDIUM_FOLDER_ICON = new ImageIconResource("Icons/MimeTypes/32x32/folder.png");
	public static final ImageIcon MEDIUM_WO_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/wo-wo.png");
	public static final ImageIcon MEDIUM_EO_MODEL_ICON = new ImageIconResource("Icons/MimeTypes/32x32/folder.png");
	
	public static final ImageIcon MEDIUM_UNKNOWN_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/unknown.png");
	public static final ImageIcon MEDIUM_MISC_FILE_ICON = new ImageIconResource("Icons/MimeTypes/32x32/misc.png");

	public static ImageIcon mediumIconForFileFormat(FileFormat format)
	{
		if (format instanceof TextFileFormat) {
			if (format == FileFormat.JAVA) {
				return MEDIUM_JAVA_FILE_ICON;
			} else if (format == FileFormat.WOD) {
				return MEDIUM_WOD_FILE_ICON;
			} else if (format == FileFormat.API) {
				return MEDIUM_API_FILE_ICON;
			} else if (format == FileFormat.TEXT) {
				return MEDIUM_TEXT_FILE_ICON;
			} else if (format == FileFormat.XML) {
				return MEDIUM_XML_FILE_ICON;
			} else if (format == FileFormat.HTML) {
				return MEDIUM_HTML_FILE_ICON;
			} else if (format == FileFormat.ANT) {
				return MEDIUM_ANT_FILE_ICON;
			} else if (format == FileFormat.CSS) {
				return MEDIUM_CSS_FILE_ICON;
			} else if (format == FileFormat.JS) {
				return MEDIUM_JAVASCRIPT_ICON;
			} else if (format == FileFormat.SQL) {
				return MEDIUM_SQL_ICON;
			} else if (format == FileFormat.DOCXML) {
				return MEDIUM_DOCX_ICON;
			} else if (format == FileFormat.SYSTEM) {
				return MEDIUM_SYSTEM_ICON;
			} else if (format == FileFormat.LATEX) {
				return MEDIUM_LATEX_ICON;
			} else if (format == FileFormat.PLIST) {
				return MEDIUM_P_LIST_ICON;
			} else if (format == FileFormat.WSDL) {
				return MEDIUM_WSDL_FILE_ICON;
			} else if (format == FileFormat.XSD) {
				return MEDIUM_XSD_FILE_ICON;
			} else if (format == FileFormat.OWL) {
				return MEDIUM_OWL_FILE_ICON;
			} else {
				return MEDIUM_MISC_FILE_ICON;
			}
		}
		
		else if (format instanceof DirectoryFormat) {
			if (format == FileFormat.EOMODEL) {
				return MEDIUM_EO_MODEL_ICON;
			} else if (format == FileFormat.WO) {
				return MEDIUM_WO_FILE_ICON;
			} else {
				return MEDIUM_FOLDER_ICON;
			}
		}
		
		else if (format instanceof BinaryFileFormat) {
			if (format == FileFormat.ZIP) {
				return MEDIUM_ZIP_ICON;
			} else if (format == FileFormat.JAR) {
				return MEDIUM_JAR_ICON;
			} else {
				return MEDIUM_BINARY_ICON;
			}
		}
		
		else if (format instanceof ImageFileFormat) {
			return MEDIUM_IMAGE_ICON;
		}
		
		return MEDIUM_UNKNOWN_FILE_ICON;
	}
	
	// SMALL files
	public static final ImageIcon SMALL_JAVA_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-x-java-2.png");
	public static final ImageIcon SMALL_WOD_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/wo-wod.png");
	public static final ImageIcon SMALL_API_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/wo-api.png");
	public static final ImageIcon SMALL_TEXT_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-x-generic.png");
	public static final ImageIcon SMALL_XML_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-xml.png");
	public static final ImageIcon SMALL_HTML_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-html.png");
	public static final ImageIcon SMALL_ANT_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-xml.png");
	public static final ImageIcon SMALL_CSS_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-css.png");
	public static final ImageIcon SMALL_JAVASCRIPT_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-javascript.png");
	public static final ImageIcon SMALL_SQL_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-vnd.ms-access.png");
	public static final ImageIcon SMALL_DOCX_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-msword.png");
	public static final ImageIcon SMALL_SYSTEM_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-x-desktop.png");
	public static final ImageIcon SMALL_LATEX_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-x-tex-2.png");
	public static final ImageIcon SMALL_P_LIST_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-x-pascal-2.png");
	public static final ImageIcon SMALL_XSD_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-xml.png");
	public static final ImageIcon SMALL_OWL_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-xml.png");
	public static final ImageIcon SMALL_WSDL_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/text-xml.png");

	public static final ImageIcon SMALL_IMAGE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-vnd.stardivision.draw.png");

	public static final ImageIcon SMALL_BINARY_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-octet-stream.png");
	public static final ImageIcon SMALL_ZIP_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-x-compressed-tar.png");
	public static final ImageIcon SMALL_JAR_ICON = new ImageIconResource("Icons/MimeTypes/16x16/application-x-java-archive.png");

	public static final ImageIcon SMALL_FOLDER_ICON = new ImageIconResource("Icons/MimeTypes/16x16/folder.png");
	public static final ImageIcon SMALL_WO_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/wo-wo.png");
	public static final ImageIcon SMALL_EO_MODEL_ICON = new ImageIconResource("Icons/MimeTypes/16x16/folder.png");
	
	public static final ImageIcon SMALL_UNKNOWN_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/unknown.png");
	public static final ImageIcon SMALL_MISC_FILE_ICON = new ImageIconResource("Icons/MimeTypes/16x16/misc.png");
	
	public static ImageIcon smallIconForFileFormat(FileFormat format)
	{
		if (format instanceof TextFileFormat) {
			if (format == FileFormat.JAVA) {
				return SMALL_JAVA_FILE_ICON;
			} else if (format == FileFormat.WOD) {
				return SMALL_WOD_FILE_ICON;
			} else if (format == FileFormat.API) {
				return SMALL_API_FILE_ICON;
			} else if (format == FileFormat.TEXT) {
				return SMALL_TEXT_FILE_ICON;
			} else if (format == FileFormat.XML) {
				return SMALL_XML_FILE_ICON;
			} else if (format == FileFormat.HTML) {
				return SMALL_HTML_FILE_ICON;
			} else if (format == FileFormat.ANT) {
				return SMALL_ANT_FILE_ICON;
			} else if (format == FileFormat.CSS) {
				return SMALL_CSS_FILE_ICON;
			} else if (format == FileFormat.JS) {
				return SMALL_JAVASCRIPT_ICON;
			} else if (format == FileFormat.SQL) {
				return SMALL_SQL_ICON;
			} else if (format == FileFormat.DOCXML) {
				return SMALL_DOCX_ICON;
			} else if (format == FileFormat.SYSTEM) {
				return SMALL_SYSTEM_ICON;
			} else if (format == FileFormat.LATEX) {
				return SMALL_LATEX_ICON;
			} else if (format == FileFormat.PLIST) {
				return SMALL_P_LIST_ICON;
			} else if (format == FileFormat.WSDL) {
				return SMALL_WSDL_FILE_ICON;
			} else if (format == FileFormat.XSD) {
				return SMALL_XSD_FILE_ICON;
			} else if (format == FileFormat.OWL) {
				return SMALL_OWL_FILE_ICON;
			} else {
				return SMALL_MISC_FILE_ICON;
			}
		}
		
		else if (format instanceof DirectoryFormat) {
			if (format == FileFormat.EOMODEL) {
				return SMALL_EO_MODEL_ICON;
			} else if (format == FileFormat.WO) {
				return SMALL_WO_FILE_ICON;
			} else {
				return SMALL_FOLDER_ICON;
			}
		}
		
		else if (format instanceof BinaryFileFormat) {
			if (format == FileFormat.ZIP) {
				return SMALL_ZIP_ICON;
			} else if (format == FileFormat.JAR) {
				return SMALL_JAR_ICON;
			} else {
				return SMALL_BINARY_ICON;
			}
		}
		
		else if (format instanceof ImageFileFormat) {
			return SMALL_IMAGE_ICON;
		}
		
		return SMALL_UNKNOWN_FILE_ICON;
	}
	

}
