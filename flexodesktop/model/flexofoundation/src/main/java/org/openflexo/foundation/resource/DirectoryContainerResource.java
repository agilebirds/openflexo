package org.openflexo.foundation.resource;

import java.io.File;

import javax.xml.bind.annotation.XmlAttribute;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.converter.RelativePathFileConverter;

/**
 * A {@link DirectoryContainerResource} is a resource bound to a directory on file system<br>
 * This directory contains a main file (the file which is accessed through {@link FlexoFileResource} api) and some other resources (files)
 * contained in this directory.<br>
 * This resource also manage a RelativeFilePathConverter that allow to access resources inside this directory. Such resource is easyly
 * transportable (any resources contained in this directory are always retrievable whereever the parent directory is).
 * 
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
@XMLElement
public interface DirectoryContainerResource<RD extends ResourceData<RD>> extends FlexoFileResource<RD> {

	public static final String DIRECTORY = "directory";
	public static final String RELATIVE_FILE_PATH_CONVERTER = "relativePathFileConverter";

	/**
	 * Return the directory (parent file)
	 * 
	 * @return
	 */
	@Getter(DIRECTORY)
	@XmlAttribute
	public File getDirectory();

	/**
	 * Sets the directory (parent file)
	 */
	@Setter(DIRECTORY)
	public void setDirectory(File file);

	/**
	 * Return the relative file path converter associated with the directory location
	 * 
	 * @return
	 */
	@Getter(value = RELATIVE_FILE_PATH_CONVERTER, ignoreType = true)
	public RelativePathFileConverter getRelativePathFileConverter();

	/**
	 * Sets the relative file path converter
	 */
	@Setter(RELATIVE_FILE_PATH_CONVERTER)
	public void setRelativePathFileConverter(RelativePathFileConverter converter);

}