package org.openflexo.model.converter;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileFormat;

public class FileFormatConverter extends Converter<FileFormat> {

	public FileFormatConverter() {
		super(FileFormat.class);
	}

	@Override
	public FileFormat convertFromString(String value, ModelFactory factory) {
		return FileFormat.getFileFormat(value);
	}

	@Override
	public String convertToString(FileFormat value) {
		return value.getIdentifier();
	}
}