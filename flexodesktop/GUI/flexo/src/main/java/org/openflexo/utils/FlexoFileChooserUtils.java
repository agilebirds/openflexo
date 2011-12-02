package org.openflexo.utils;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.localization.FlexoLocalization;

public class FlexoFileChooserUtils {

	public static class FlexoPaletteFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return FlexoLocalization.localizedForKey("flexo_palettes");
		}

	}

	public static class FlexoPaletteFileView extends FileView {

		protected FlexoPaletteFileView() {

		}

		/**
		 * Overrides isTraversable
		 * 
		 * @see javax.swing.filechooser.FileView#isTraversable(java.io.File)
		 */
		@Override
		public Boolean isTraversable(File f) {
			if (f == null || !f.isDirectory()) {
				return Boolean.FALSE;
			}
			if (f.getName().toLowerCase().endsWith(".iepalette")) {
				return Boolean.FALSE;
			}
			File[] files = f.listFiles(new java.io.FileFilter() {

				@Override
				public boolean accept(File file) {
					return !file.isDirectory() && file.getName().toLowerCase().endsWith(".woxml");
				}
			});
			if (files != null && files.length > 0) {
				return Boolean.FALSE;
			}
			return super.isTraversable(f);
		}

		/**
		 * Overrides getIcon
		 * 
		 * @see javax.swing.filechooser.FileView#getIcon(java.io.File)
		 */
		@Override
		public Icon getIcon(File f) {
			if (f.getName().toLowerCase().endsWith(".iepalette")) {
				return FilesIconLibrary.SMALL_FOLDER_ICON;
			} else if (f.isDirectory()) {
				File[] files = f.listFiles(new java.io.FileFilter() {

					@Override
					public boolean accept(File file) {
						return !file.isDirectory() && file.getName().toLowerCase().endsWith(".woxml");
					}
				});
				if (files != null && files.length > 0) {
					return FilesIconLibrary.SMALL_FOLDER_ICON;
				} else {
					return super.getIcon(f);
				}
			} else {
				return super.getIcon(f);
			}
		}
	}

	public static class FlexoProjectFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return FlexoLocalization.localizedForKey("flexo_projects");
		}

	}

	public static class FlexoProjectFilenameFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			if (new File(dir, name).isDirectory() && name.toLowerCase().endsWith(".prj")) {
				return true;
			}
			return false;
		}
	}

	/**
	 * @author gpolet
	 * 
	 */
	public static class FlexoProjectFileView extends FileView {

		protected FlexoProjectFileView() {

		}

		/**
		 * Overrides isTraversable
		 * 
		 * @see javax.swing.filechooser.FileView#isTraversable(java.io.File)
		 */
		@Override
		public Boolean isTraversable(File f) {
			if (f == null || !f.isDirectory()) {
				return Boolean.FALSE;
			}
			if (f.getName().toLowerCase().endsWith(".prj")) {
				return Boolean.FALSE;
			} else {
				return super.isTraversable(f);
			}
		}

		/**
		 * Overrides getIcon
		 * 
		 * @see javax.swing.filechooser.FileView#getIcon(java.io.File)
		 */
		@Override
		public Icon getIcon(File f) {
			if (f.getName().toLowerCase().endsWith(".prj")) {
				return FilesIconLibrary.SMALL_UNKNOWN_FILE_ICON;
			} else {
				return super.getIcon(f);
			}
		}
	}

	public static final FileView PROJECT_FILE_VIEW = new FlexoProjectFileView();

	public static final FileFilter PROJECT_FILE_FILTER = new FlexoProjectFileFilter();

	public static final FilenameFilter PROJECT_FILE_NAME_FILTER = new FlexoProjectFilenameFilter();

	public static final FileView PALETTE_FILE_VIEW = new FlexoPaletteFileView();

	public static final FileFilter PALETTE_FILE_FILTER = new FlexoPaletteFileFilter();

}
