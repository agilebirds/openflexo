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
package org.openflexo.fib.model;

import java.io.File;
import java.lang.reflect.Type;

import javax.swing.JFileChooser;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBFile.FIBFileImpl.class)
@XMLElement(xmlTag = "File")
public interface FIBFile extends FIBWidget {

	public static enum FileMode {
		OpenMode {
			@Override
			public int getMode() {
				return JFileChooser.OPEN_DIALOG;
			}
		},
		SaveMode {
			@Override
			public int getMode() {
				return JFileChooser.SAVE_DIALOG;
			}
		};
		public abstract int getMode();
	}

	@PropertyIdentifier(type = FileMode.class)
	public static final String MODE_KEY = "mode";
	@PropertyIdentifier(type = String.class)
	public static final String FILTER_KEY = "filter";
	@PropertyIdentifier(type = String.class)
	public static final String TITLE_KEY = "title";
	@PropertyIdentifier(type = boolean.class)
	public static final String DIRECTORY_KEY = "directory";
	@PropertyIdentifier(type = File.class)
	public static final String DEFAULT_DIRECTORY_KEY = "defaultDirectory";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";

	@Getter(value = MODE_KEY)
	@XMLAttribute
	public FileMode getMode();

	@Setter(MODE_KEY)
	public void setMode(FileMode mode);

	@Getter(value = FILTER_KEY)
	@XMLAttribute
	public String getFilter();

	@Setter(FILTER_KEY)
	public void setFilter(String filter);

	@Getter(value = TITLE_KEY)
	@XMLAttribute
	public String getTitle();

	@Setter(TITLE_KEY)
	public void setTitle(String title);

	@Getter(value = DIRECTORY_KEY, defaultValue = "false")
	@XMLAttribute(xmlTag = "isDirectory")
	public boolean isDirectory();

	@Setter(DIRECTORY_KEY)
	public void setDirectory(boolean directory);

	@Getter(value = DEFAULT_DIRECTORY_KEY)
	@XMLAttribute
	public File getDefaultDirectory();

	@Setter(DEFAULT_DIRECTORY_KEY)
	public void setDefaultDirectory(File defaultDirectory);

	@Getter(value = COLUMNS_KEY)
	@XMLAttribute
	public Integer getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(Integer columns);

	public static abstract class FIBFileImpl extends FIBWidgetImpl implements FIBFile {

		private FileMode mode;
		private String filter;
		private String title;
		private boolean directory = false;
		private File defaultDirectory;
		private Integer columns;

		public FIBFileImpl() {
		}

		@Override
		public String getBaseName() {
			return "FileSelector";
		}

		@Override
		public Type getDefaultDataClass() {
			return File.class;
		}

		/**
		 * @return the columns
		 */
		@Override
		public Integer getColumns() {
			return columns;
		}

		/**
		 * @param columns
		 *            the columns to set
		 */
		@Override
		public void setColumns(Integer columns) {
			this.columns = columns;
		}

		/**
		 * @return the defaultDirectory
		 */
		@Override
		public File getDefaultDirectory() {
			return defaultDirectory;
		}

		/**
		 * @param defaultDirectory
		 *            the defaultDirectory to set
		 */
		@Override
		public void setDefaultDirectory(File defaultDirectory) {
			this.defaultDirectory = defaultDirectory;
		}

		/**
		 * @return the filter
		 */
		@Override
		public String getFilter() {
			return filter;
		}

		/**
		 * @param filter
		 *            the filter to set
		 */
		@Override
		public void setFilter(String filter) {
			this.filter = filter;
		}

		/**
		 * @return the directory
		 */
		@Override
		public boolean isDirectory() {
			return directory;
		}

		/**
		 * @param directory
		 *            the directory to set
		 */
		@Override
		public void setDirectory(boolean directory) {
			this.directory = directory;
		}

		/**
		 * @return the mode
		 */
		@Override
		public FileMode getMode() {
			return mode;
		}

		/**
		 * @param mode
		 *            the mode to set
		 */
		@Override
		public void setMode(FileMode mode) {
			this.mode = mode;
		}

		/**
		 * @return the title
		 */
		@Override
		public String getTitle() {
			return title;
		}

		/**
		 * @param title
		 *            the title to set
		 */
		@Override
		public void setTitle(String title) {
			this.title = title;
		}
	}
}
