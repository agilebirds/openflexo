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
package org.openflexo.foundation.cg.templates;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.toolbox.FileUtils;

public abstract class CGDirectoryTemplateSet extends CGTemplateSet {

	protected static final Logger logger = Logger.getLogger(CGDirectoryTemplateSet.class.getPackage().getName());

	private File _directory;
	private boolean _recursive = false;

	public CGDirectoryTemplateSet(File directory, CGTemplateRepository repository, boolean recursive) {
		super(repository);
		_directory = directory;
		_recursive = recursive;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CGTemplate[] findAllTemplates() {
		File[] files = null;
		if (!_recursive) {
			files = _directory.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isFile() && !file.getName().endsWith("CVS");
				}
			});
		} else {
			files = findAllTemplateFiles();
		}

		List<CGTemplate> result = new ArrayList<CGTemplate>();
		if (files != null) {
			for (File file : files) {
				String relativePath = null;
				try {
					relativePath = FileUtils.makeFilePathRelativeToDir(file, _directory);
				} catch (IOException e) {
					e.printStackTrace();
				}
				result.add(new CGTemplateFile(file, this, relativePath));
			}
		}

		return result.toArray(new CGTemplate[0]);
	}

	private File[] findAllTemplateFiles() {
		Vector<File> v = new Vector<File>();
		appendAllTemplateFiles(_directory, v);
		File[] returned = new File[v.size()];
		for (int i = 0; i < v.size(); i++) {
			returned[i] = v.get(i);
		}
		return returned;
	}

	private void appendAllTemplateFiles(File directory, Vector<File> v) {
		for (File f : directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile() && !file.getName().endsWith("CVS");
			}
		})) {
			v.add(f);
		}
		for (File f : directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() && !file.getName().endsWith("CVS");
			}
		})) {
			appendAllTemplateFiles(f, v);
		}
	}

	public File getDirectory() {
		return _directory;
	}

	// public CGTemplateFile getTemplateFile(File aFile) {
	// return _files.get(aFile);
	// }
	//
	// public CGTemplateFile getTemplateFile(String templateName) {
	// return getTemplateFile(new File(_directory, templateName));
	// }
}
