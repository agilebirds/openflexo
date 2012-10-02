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

public class FIBFile extends FIBWidget {

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

	private FileMode mode;
	private String filter;
	private String title;
	private boolean directory = false;
	private File defaultDirectory;
	private Integer columns;

	public FIBFile() {
	}

	@Override
	protected String getBaseName() {
		return "FileSelector";
	}

	@Override
	public Type getDefaultDataClass() {
		return File.class;
	}

	/**
	 * @return the columns
	 */
	public Integer getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	/**
	 * @return the defaultDirectory
	 */
	public File getDefaultDirectory() {
		return defaultDirectory;
	}

	/**
	 * @param defaultDirectory the defaultDirectory to set
	 */
	public void setDefaultDirectory(File defaultDirectory) {
		this.defaultDirectory = defaultDirectory;
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * @return the directory
	 */
	public boolean isDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	/**
	 * @return the mode
	 */
	public FileMode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(FileMode mode) {
		this.mode = mode;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
