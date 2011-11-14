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
package org.openflexo.toolbox;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WRLocator {

	private static final FileResource imageDir = new FileResource(FileCst.IMAGE_REL_PATH);

	public static final File AGILE_BIRDS_LOGO = new FileResource("Config/Images/agilebirdssmalllogo_1002652.jpg");

	private static final Logger logger = Logger.getLogger(WRLocator.class.getPackage().getName());

	public static File locate(File projectDirectory, String imageName, String css) {
		File answer = null;
		if (imageName == null) {
			logger.severe("Search for an image with no name !");
			return null;
		}

		// GPO: Gros hack bien pourri pour retrouver les images contento
		// TODO: create and copy in Flexo's resources icons for NewContento and BlueWave
		if (css.equals("BlueWave") || css.equals("NewContento")) {
			css = "Contento";
		}
		if (imageName.indexOf("/") > -1) {
			answer = new File(projectDirectory, FileCst.IMPORTED_IMAGE_DIR_NAME + "/" + imageName);
		} else if (imageName.startsWith("_")) {
			answer = new File(imageDir, css + "/" + css + imageName);
		} else {
			answer = new File(imageDir, imageName);
			if (!answer.exists()) {
				answer = new File(projectDirectory, FileCst.IMPORTED_IMAGE_DIR_NAME + "/" + imageName);
			}
		}
		if (!answer.exists()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Couldn't find web resource with image name:" + imageName + " at path " + answer.getAbsolutePath());
			}
			return null;
		}
		return answer;
	}

	private static File styledImageDir(String css) {
		File reply = new File(imageDir, css);
		if (!reply.exists()) {
			reply = new File(imageDir, "Contento");
		}
		return reply;
	}

	/**
	 * Returns the specificButtons directory. If it does not exist, it is then created.
	 * 
	 * @return
	 */
	// public static File specificImageDir()
	// {
	// File f = new File(prjDir, FileCst.SPECIFIC_BUTTONS_DIR_NAME);
	// if (!f.exists())
	// f.mkdirs();
	// return f;
	// }

	public static File[] listBigButtons(String css) {
		if (css == null) {
			css = FileCst.CONTENTO_CSS_DIR_NAME;
		}
		FileFilter bigButtonFilter = new FileFilter() {
			@Override
			public boolean accept(File aFile) {
				return aFile.getName().indexOf("_Button_") > 0 || aFile.getName().indexOf("_BigIcon_") > 0
						|| aFile.getName().indexOf("_ProgressIcon_") > 0;
			}
		};
		return styledImageDir(css).listFiles(bigButtonFilter);
	}

	public static File[] listSmallButtons(String css) {
		if (css == null) {
			css = FileCst.CONTENTO_CSS_DIR_NAME;
		}
		FileFilter styledIconFilter = new FileFilter() {
			@Override
			public boolean accept(File aFile) {
				return aFile.getName().indexOf("_Icon_") > 0 || aFile.getName().indexOf("_BigIcon_") > 0
						|| aFile.getName().indexOf("_ProgressIcon_") > 0;
			}
		};
		File[] styledImages = styledImageDir(css).listFiles(styledIconFilter);
		Arrays.sort(styledImages, WRLocator.FILE_COMPARATOR);
		FileFilter smallButtonFilter = new FileFilter() {
			@Override
			public boolean accept(File aFile) {
				return !aFile.isDirectory();
			}
		};

		File[] defaultImages = imageDir.listFiles(smallButtonFilter);
		Arrays.sort(defaultImages, WRLocator.FILE_COMPARATOR);

		int total = styledImages.length + defaultImages.length;
		File[] answer = new File[total];
		int k = 0;
		for (int i = 0; i < styledImages.length; i++) {
			answer[k] = styledImages[i];
			k++;
		}
		for (int i = 0; i < defaultImages.length; i++) {
			answer[k] = defaultImages[i];
			k++;
		}
		return answer;
	}

	// private static Vector<File> getAllSpecificImages()
	// {
	// File[] directories = specificImageDir().listFiles();
	// FileFilter smallButtonFilter = new FileFilter() {
	// public boolean accept(File aFile)
	// {
	// return !aFile.isDirectory();
	// }
	// };
	// Vector<File> answer = new Vector<File>();
	// File[] f = specificImageDir().listFiles(smallButtonFilter);
	// Arrays.sort(f, WRLocator.FILE_COMPARATOR);
	// for (int j = 0; j < f.length; j++) {
	// answer.add(f[j]);
	// }
	// if (directories != null) {
	// for (int i = 0; i < directories.length; i++) {
	// if (!directories[i].getName().equals(FileCst.CVS_DIR_NAME) && directories[i].isDirectory()) {
	// File[] specificImages = directories[i].listFiles(smallButtonFilter);
	// Arrays.sort(specificImages, WRLocator.FILE_COMPARATOR);
	// for (int j = 0; j < specificImages.length; j++) {
	// answer.add(specificImages[j]);
	// }
	// }
	// }
	// }
	// return answer;
	// }

	public WRLocator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static final FileComparator FILE_COMPARATOR = new FileComparator();

	static class FileComparator implements Comparator<File> {
		/**
		 * Overrides compare
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}
