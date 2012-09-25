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
package org.openflexo.swing;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.openflexo.toolbox.ToolBox;

public class FlexoFileChooser {

	private enum ImplementationType {
		JFileChooserImplementation, FileDialogImplementation
	}

	static ImplementationType getImplementationType() {
		return ToolBox.getPLATFORM() == ToolBox.MACOS ? ImplementationType.FileDialogImplementation
				: ImplementationType.JFileChooserImplementation;
	}

	/**
	 *
	 */
	public static JFileChooser getFileChooser(String location) {
		JFileChooser chooser;
		if (ToolBox.fileChooserRequiresFix()) {
			// ToolBox.fixFileChooser();
			chooser = new JFileChooser(location) {
				@Override
				public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
					ToolBox.fixFileChooser();
					try {
						int r = super.showDialog(parent, approveButtonText);
						return r;
					} finally {
						ToolBox.undoFixFileChooser();
					}
				}
			};
		} else {
			chooser = new JFileChooser(location);
		}
		return chooser;
	}

	private FileDialog _fileDialog;
	private JFileChooser _fileChooser;
	private Window _owner;

	public FlexoFileChooser(Window owner) {
		super();
		_owner = owner;
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			buildAsJFileChooser();
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			buildAsFileDialog();
		}
	}

	public void setCurrentDirectory(File dir) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setCurrentDirectory(dir);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			try {
				_fileDialog.setDirectory(dir != null ? dir.getCanonicalPath() : System.getProperty("user.home"));
			} catch (IOException e) {
				_fileDialog.setDirectory(System.getProperty("user.home"));
			}
		}
	}

	public void setDialogTitle(String title) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogTitle(title);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setTitle(title);
		}
	}

	public void setFileSelectionMode(int mode) {
		_mode = mode;
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			if (mode == JFileChooser.FILES_ONLY) {
				mode = JFileChooser.FILES_AND_DIRECTORIES;
			}
			_fileChooser.setFileSelectionMode(mode);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (mode == JFileChooser.DIRECTORIES_ONLY) {
				System.setProperty("apple.awt.fileDialogForDirectories", "true");
			} else if (mode == JFileChooser.FILES_ONLY) {
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
			}
		}
		/*else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if(mode==JFileChooser.DIRECTORIES_ONLY){
				System.setProperty("apple.awt.fileDialogForDirectories", "true");
				_fileDialog.setFilenameFilter(new FilenameFilter(){
					public boolean accept(File dir, String name) {
						return dir.isDirectory() && name==null;
					}
				});
			}else if(mode==JFileChooser.FILES_ONLY){
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
				_fileDialog.setFilenameFilter(new FilenameFilter(){
					public boolean accept(File dir, String name) {
						return dir.isDirectory() && name!=null && new File(dir,name).exists();
					}
				});
			}
		}*/
	}

	int _mode;

	/**
	 * <p>All extensions should be prefaced with '*.'
	 * <p>For more than one entry, use the ',' character
	 * <p>Example: '*.xsd, *.owl'
	 * <p>Note: trims whitespaces before and after extensions, and ignores case
	 * 
	 * @param filter
	 */
	public void setFileFilterAsString(final String filter) {
		if (filter == null) {
			return;
		}
		final String [] extensions = filter.split(",");
		for (int i = 0; i < extensions.length ; i++) {
			extensions[i] = extensions[i].trim();
			extensions[i] = extensions[i].substring(1); // removes '*' from the string
		}
		final String endsWith = filter.substring(filter.indexOf("*") + 1);
		setFileFilter(new FileFilter() {
			private boolean accept(String name) {
				for (String extension : extensions) {
					if (name.toLowerCase().endsWith(extension.toLowerCase())) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			public boolean accept(File f) {
				if (getImplementationType() == ImplementationType.FileDialogImplementation) {
					if (_mode == JFileChooser.DIRECTORIES_ONLY && !f.isDirectory()) {
						return false;
					}
					if (_mode == JFileChooser.FILES_ONLY && !f.isFile()) {
						return false;
					}
				}
				if (_mode == JFileChooser.DIRECTORIES_ONLY) {
					return f.isDirectory() && accept(f.getName());
				} else {
					return f.isDirectory() || accept(f.getName());
				}
			}

			@Override
			public String getDescription() {
				return filter;
			}
		});
	}

	public void setDialogType(int type) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogType(type);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (type == JFileChooser.SAVE_DIALOG) {
				_fileDialog.setMode(FileDialog.SAVE);
			}
			if (type == JFileChooser.OPEN_DIALOG) {
				_fileDialog.setMode(FileDialog.LOAD);
			}
		}
	}

	public void setFileView(FileView view) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setFileView(view);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {

		}
	}

	public void setFileFilter(FileFilter filter) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setFileFilter(filter);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setFilenameFilter(new FilenameFilterAdapter(filter));
		}
	}

	private static class FilenameFilterAdapter implements FilenameFilter {
		private FileFilter _fileFilter;

		public FilenameFilterAdapter(FileFilter fileFilter) {
			super();
			_fileFilter = fileFilter;
		}

		@Override
		public boolean accept(File dir, String name) {
			if (name == null) {
				return _fileFilter.accept(dir);
			}
			return _fileFilter.accept(new File(dir, name));
		}

	}

	private Component buildAsJFileChooser() {
		_fileChooser = getFileChooser(System.getProperty("user.home"));
		FileFilter[] ff = _fileChooser.getChoosableFileFilters();
		for (int i = 0; i < ff.length; i++) {
			FileFilter filter = ff[i];
			_fileChooser.removeChoosableFileFilter(filter);
		}
		return _fileChooser;
	}

	private Component buildAsFileDialog() {
		if (_owner instanceof Frame) {
			_fileDialog = new FileDialog((Frame) _owner);
		} else if (_owner instanceof Dialog) {
			_fileDialog = new FileDialog((Dialog) _owner);
		} else {
			_fileDialog = new FileDialog(Frame.getFrames()[0]);
		}
		return _fileDialog;
	}

	public Component getComponent() {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			return _fileChooser;
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			return _fileDialog;
		}
		return null;
	}

	public int showDialog(String title) {
		setDialogTitle(title);
		return showOpenDialog();
	}

	public int showOpenDialog(Component parent) throws HeadlessException {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			return _fileChooser.showDialog(parent, null);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setMode(FileDialog.LOAD);
			_fileDialog.setAlwaysOnTop(true);
			_fileDialog.setModal(true);
			_fileDialog.setVisible(true);
			_fileDialog.toFront();
			if (_fileDialog.getFile() == null) {
				return JFileChooser.CANCEL_OPTION;
			} else {
				return JFileChooser.APPROVE_OPTION;
			}
		}
		return JFileChooser.ERROR_OPTION;
	}

	public int showOpenDialog() throws HeadlessException {
		return showOpenDialog(_owner);
	}

	public int showSaveDialog(Component parent) throws HeadlessException {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			return _fileChooser.showDialog(parent, null);
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setMode(FileDialog.SAVE);
			_fileDialog.setModal(true);
			_fileDialog.setVisible(true);
			_fileDialog.toFront();
			if (_fileDialog.getFile() == null) {
				return JFileChooser.CANCEL_OPTION;
			} else {
				return JFileChooser.APPROVE_OPTION;
			}
		}
		return JFileChooser.ERROR_OPTION;
	}

	public File getSelectedFile() {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			return _fileChooser.getSelectedFile();
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (_fileDialog.getFile() != null) {
				return new File(_fileDialog.getDirectory(), _fileDialog.getFile());
			}
		}
		return null;
	}

	public void setSelectedFile(File _file) {
		if (_file != null && !_file.isDirectory()) {
			if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
				_fileChooser.setSelectedFile(_file);
			} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
				_fileDialog.setFile(_file.getName());
			}
		}
	}
}
