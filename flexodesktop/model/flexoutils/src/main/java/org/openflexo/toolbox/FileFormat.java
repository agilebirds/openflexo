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
package org.openflexo.toolbox;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

public abstract class FileFormat {

	protected static final Logger logger = FlexoLogger.getLogger(FileFormat.class.getPackage().getName());

	public static final FileFormat UNKNOWN = new FileFormat(null, null) {
		@Override
		public boolean isBinary() {
			return false;
		}

		@Override
		public boolean isImage() {
			return false;
		}
	};

	public static final DirectoryFormat UNKNOWN_DIRECTORY = new DirectoryFormat(null, null);
	public static final BinaryFileFormat UNKNOWN_BINARY_FILE = new BinaryFileFormat(null, null);
	public static final TextFileFormat UNKNOWN_ASCII_FILE = new TextFileFormat(null, null, TextSyntax.Plain);
	public static final ImageFileFormat UNKNOWN_IMAGE_FILE = new ImageFileFormat(null, null);

	public static final TextFileFormat TEXT, SYSTEM, XML, OWL, API, WSDL, BPEL, XSD, WOD, DOCXML, ANT, HTML, JS, JAVA, LATEX, PLIST, SQL,
			CSS;
	public static final BinaryFileFormat JAR, ZIP;
	public static final ImageFileFormat GIF, JPG, PNG;
	public static final DirectoryFormat EOMODEL, WO;

	static {
		_fileFormats = new Hashtable<String, FileFormat>();
		_fileFormatsByExtensions = new Hashtable<String, List<FileFormat>>();

		TEXT = registerTextFileFormat("TXT", "text/plain", TextSyntax.Plain, "txt", "text");
		SYSTEM = registerTextFileFormat("SYSTEM", "text/plain", TextSyntax.Plain);

		XML = registerTextFileFormat("XML", "text/xml", TextSyntax.XML, "xml");
		OWL = registerTextFileFormat("OWL", "text/owl", TextSyntax.XML, "owl");
		API = registerTextFileFormat("API", "text/api", TextSyntax.XML, "api");
		WSDL = registerTextFileFormat("WSDL", "text/wsdl", TextSyntax.XML, "wsdl");
		BPEL = registerTextFileFormat("BPEL", "text/bpel", TextSyntax.XML, "bpel");
		XSD = registerTextFileFormat("XSD", "text/xsd", TextSyntax.XML, "xsd");
		WOD = registerTextFileFormat("WOD", "text/wod", TextSyntax.XML, "wod");
		DOCXML = registerTextFileFormat("DOCXML", "text/docxml", TextSyntax.XML, "docx");
		ANT = registerTextFileFormat("ANT", "text/ant", TextSyntax.XML, "ant");

		HTML = registerTextFileFormat("HTML", "text/html", TextSyntax.HTML, "html", "htm");
		JS = registerTextFileFormat("JS", "text/javascript", TextSyntax.JavaScript, "js");
		JAVA = registerTextFileFormat("JAVA", "text/java", TextSyntax.Java, "java", "jav");
		LATEX = registerTextFileFormat("LATEX", "text/latex", TextSyntax.Latex, "latex", "tex", "sty", "def");
		PLIST = registerTextFileFormat("PLIST", "text/plist", TextSyntax.PList, "plist");
		SQL = registerTextFileFormat("SQL", "text/sql", TextSyntax.SQL, "sql");
		CSS = registerTextFileFormat("CSS", "text/css", TextSyntax.CSS, "css");

		JAR = registerBinaryFileFormat("JAR", "application/jar", "jar");
		ZIP = registerBinaryFileFormat("ZIP", "application/zip", "zip", "gz");

		EOMODEL = registerDirectoryFormat("EOMODEL", "directory/eomodel", "eomodel");
		WO = registerDirectoryFormat("WO", "directory/wo", "wo");

		GIF = registerImageFileFormat("GIF", "image/gif", "gif");
		JPG = registerImageFileFormat("JPG", "image/jpeg", "jpeg", "jpg");
		PNG = registerImageFileFormat("PNG", "image/png", "png");
	}

	public static enum TextSyntax {
		Plain, Java, JavaScript, XML, HTML, CSS, Latex, PList, SQL
	}

	public static TextFileFormat registerTextFileFormat(String formatId, String mimeType, String... extensions) {
		return registerTextFileFormat(formatId, mimeType, null, extensions);
	}

	public static TextFileFormat registerTextFileFormat(String formatId, String mimeType, TextSyntax syntax, String... extensions) {
		if (_fileFormats.get(formatId) != null) {
			if (_fileFormats.get(formatId) instanceof TextFileFormat) {
				logger.warning("Already declared FileFormat " + formatId);
				return (TextFileFormat) _fileFormats.get(formatId);
			} else {
				logger.severe("Already declared FileFormat of different type for " + formatId);
				return null;
			}
		}
		TextFileFormat returned = new TextFileFormat(formatId, mimeType, syntax);
		_fileFormats.put(formatId, returned);
		for (String ext : extensions) {
			registerExtension(ext, returned);
		}
		return returned;
	}

	public static DirectoryFormat registerDirectoryFormat(String formatId, String mimeType, String... extensions) {
		if (_fileFormats.get(formatId) != null) {
			if (_fileFormats.get(formatId) instanceof DirectoryFormat) {
				logger.warning("Already declared FileFormat " + formatId);
				return (DirectoryFormat) _fileFormats.get(formatId);
			} else {
				logger.severe("Already declared FileFormat of different type for " + formatId);
				return null;
			}
		}
		DirectoryFormat returned = new DirectoryFormat(formatId, mimeType);
		_fileFormats.put(formatId, returned);
		for (String ext : extensions) {
			registerExtension(ext, returned);
		}
		return returned;
	}

	public static BinaryFileFormat registerBinaryFileFormat(String formatId, String mimeType, String... extensions) {
		if (_fileFormats.get(formatId) != null) {
			if (_fileFormats.get(formatId) instanceof BinaryFileFormat) {
				logger.warning("Already declared FileFormat " + formatId);
				return (BinaryFileFormat) _fileFormats.get(formatId);
			} else {
				logger.severe("Already declared FileFormat of different type for " + formatId);
				return null;
			}
		}
		BinaryFileFormat returned = new BinaryFileFormat(formatId, mimeType);
		_fileFormats.put(formatId, returned);
		for (String ext : extensions) {
			registerExtension(ext, returned);
		}
		return returned;
	}

	public static ImageFileFormat registerImageFileFormat(String formatId, String mimeType, String... extensions) {
		if (_fileFormats.get(formatId) != null) {
			if (_fileFormats.get(formatId) instanceof ImageFileFormat) {
				logger.warning("Already declared FileFormat " + formatId);
				return (ImageFileFormat) _fileFormats.get(formatId);
			} else {
				logger.severe("Already declared FileFormat of different type for " + formatId);
				return null;
			}
		}
		ImageFileFormat returned = new ImageFileFormat(formatId, mimeType);
		_fileFormats.put(formatId, returned);
		for (String ext : extensions) {
			registerExtension(ext, returned);
		}
		return returned;
	}

	public static FileFormat getFileFormat(String formatIdentifier) {
		FileFormat returned = _fileFormats.get(formatIdentifier);
		if (returned != null) {
			return returned;
		}
		for (String id : _fileFormats.keySet()) {
			if (id.equalsIgnoreCase(formatIdentifier)) {
				return _fileFormats.get(id);
			}
		}
		if (formatIdentifier.equals("File/ASCII")) {
			return TEXT;
		}
		if (formatIdentifier.equals("File/XML")) {
			return XML;
		}
		return UNKNOWN;
	}

	public static List<FileFormat> getFileFormatByExtension(String extension) {
		List<FileFormat> returned = _fileFormatsByExtensions.get(extension);
		if (returned != null) {
			return returned;
		}
		for (String ext : _fileFormatsByExtensions.keySet()) {
			if (ext.equalsIgnoreCase(extension)) {
				return _fileFormatsByExtensions.get(ext);
			}
		}
		Vector<FileFormat> newVector = new Vector<FileFormat>();
		newVector.add(UNKNOWN);
		_fileFormatsByExtensions.put(extension, newVector);
		return newVector;
	}

	public static FileFormat getDefaultFileFormatByExtension(String extension) {
		List<FileFormat> list = getFileFormatByExtension(extension);
		if (list.size() > 0) {
			return list.get(0);
		}
		return UNKNOWN;
	}

	public static void registerExtension(String extension, FileFormat fileFormat) {
		List<FileFormat> returned = _fileFormatsByExtensions.get(extension);
		if (returned == null) {
			returned = new Vector<FileFormat>();
			_fileFormatsByExtensions.put(extension, returned);
		}
		if (returned.contains(UNKNOWN)) {
			returned.remove(UNKNOWN);
		}
		if (!returned.contains(fileFormat)) {
			returned.add(fileFormat);
		}
		if (!fileFormat.extensions.contains(extension)) {
			fileFormat.extensions.add(extension);
		}
	}

	private static Hashtable<String, FileFormat> _fileFormats;
	private static Hashtable<String, List<FileFormat>> _fileFormatsByExtensions;

	private final String identifier;
	private final String mimeType;
	private final List<String> extensions;

	private FileFormat(String identifier, String mimeType) {
		super();
		this.identifier = identifier;
		this.mimeType = mimeType;
		extensions = new Vector<String>();
	}

	public static class TextFileFormat extends FileFormat {
		private final TextSyntax syntax;

		private TextFileFormat(String identifier, String mimeType, TextSyntax syntax) {
			super(identifier, mimeType);
			this.syntax = syntax;
		}

		public TextSyntax getSyntax() {
			return syntax;
		}

		@Override
		public boolean isBinary() {
			return false;
		}

		@Override
		public boolean isImage() {
			return false;
		}
	}

	public static class BinaryFileFormat extends FileFormat {
		private BinaryFileFormat(String identifier, String mimeType) {
			super(identifier, mimeType);
		}

		@Override
		public boolean isBinary() {
			return true;
		}

		@Override
		public boolean isImage() {
			return false;
		}
	}

	public static class ImageFileFormat extends FileFormat {
		private ImageFileFormat(String identifier, String mimeType) {
			super(identifier, mimeType);
		}

		@Override
		public boolean isBinary() {
			return true;
		}

		@Override
		public boolean isImage() {
			return true;
		}
	}

	public static class DirectoryFormat extends FileFormat {
		private DirectoryFormat(String identifier, String mimeType) {
			super(identifier, mimeType);
		}

		@Override
		public boolean isBinary() {
			return false;
		}

		@Override
		public boolean isImage() {
			return false;
		}
	}

	public abstract boolean isBinary();

	public abstract boolean isImage();

	@Override
	public String toString() {
		return identifier + "-" + mimeType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getMimeType() {
		return mimeType;
	}

	public List<String> getExtensions() {
		return extensions;
	}

}