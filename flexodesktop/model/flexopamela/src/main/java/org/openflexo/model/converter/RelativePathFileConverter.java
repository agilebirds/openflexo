package org.openflexo.model.converter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

public class RelativePathFileConverter extends Converter<File> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(RelativePathFileConverter.class
			.getPackage().getName());

	private final File relativePath;

	public RelativePathFileConverter(File aRelativePath) {
		super(File.class);
		relativePath = aRelativePath;
	}

	@Override
	public File convertFromString(String value, ModelFactory factory) {
		File file = new File(relativePath, value);
		if (!file.exists()) {
			logger.warning("Cannot fin relative file: " + value + " in " + relativePath + " searched:" + file.getAbsolutePath());
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