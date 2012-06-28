package org.openflexo.toolbox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.openflexo.xmlcode.StringEncoder.Converter;

public class RelativePathFileConverter extends Converter<File> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(RelativePathFileConverter.class
			.getPackage().getName());

	private final File relativePath;

	public RelativePathFileConverter(File aRelativePath) {
		super(File.class);
		relativePath = aRelativePath;
	}

	@Override
	public File convertFromString(String value) {
		File file = new File(relativePath, value);
		if (!file.exists()) {
			System.err.println("Shit: this does not exist: " + file.getAbsolutePath());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("********* convertFromString " + value + " return " + file.getAbsolutePath());
		}
		return file;
	}

	@Override
	public String convertToString(File value) {
		try {
			return FileUtils.makeFilePathRelativeToDir(value, relativePath);
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IOException while computing relative path for " + value + " relative to " + relativePath);
			}
			return value.getAbsolutePath();
		}
	}

}