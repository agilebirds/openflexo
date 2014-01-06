package org.openflexo.foundation.converter;

import java.io.File;

import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

/**
 * String converter from/to {@link FlexoProjectFile} objects
 * 
 * @author sylvain
 * 
 */
public class FlexoProjectFileConverter extends Converter<FlexoProjectFile> {

	private static final char fileSeparator = File.separator.charAt(0);

	private static final char alternateFileSeparator = fileSeparator == '\\' ? '/' : '\\';

	public static String transformIntoValidPath(String aRelativePath) {
		return aRelativePath.replace(alternateFileSeparator, fileSeparator);
	}

	public FlexoProjectFileConverter() {
		super(FlexoProjectFile.class);
	}

	@Override
	public FlexoProjectFile convertFromString(String value, ModelFactory factory) {
		return new FlexoProjectFile(transformIntoValidPath(value));
	}

	@Override
	public String convertToString(FlexoProjectFile value) {
		return value.toString();
	}

}