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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.openflexo.logging.FlexoLogger;

/**
 * Some File utilities
 * 
 * @author sguerin
 */
public class FileUtils {

	private static final Logger logger = FlexoLogger.getLogger(FileUtils.class.getPackage().getName());

	public static enum CopyStrategy {
		REPLACE, REPLACE_OLD_ONLY, IGNORE_EXISTING
	}

	private static final String WIN_REGISTRY_DOCUMENTS_KEY_PATH = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders";

	private static final String WIN_REGISTRY_DOCUMENTS_ATTRIBUTE = "Personal";

	public static final String BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP = "[\"|\\?\\*/<>:\\\\]|[^\\p{ASCII}]";

	public static final String GOOD_CHARACTERS_REG_EXP = "[^\"|\\?\\*/<>:\\\\]|[\\p{ASCII}]";

	public static final Pattern BAD_CHARACTERS_FOR_FILE_NAME_PATTERN = Pattern.compile(BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP);

	private static final String NO_BLANK_NO_SLASH = "[^ /\\\\:\\*\\?\"<>|]";

	private static final String SLASH = "(/|\\\\)";

	private static final String NO_SLASH = "[^/\\\\:\\*\\?\"<>|]";

	private static final String UNACCEPTABLE_CHARS = "[:\\*\\?\"<>|]|[^\\p{ASCII}]";

	private static final Pattern UNACCEPTABLE_CHARS_PATTERN = Pattern.compile(UNACCEPTABLE_CHARS);

	private static final String UNACCEPTABLE_SLASH = "\\s+" + SLASH + "\\s*|\\s*" + SLASH + "\\s+|\\\\";

	private static final Pattern UNACCEPTABLE_SLASH_PATTERN = Pattern.compile(UNACCEPTABLE_SLASH);

	public static final String VALID_FILE_NAME_REGEXP = SLASH + "?(" + NO_BLANK_NO_SLASH + "(" + NO_SLASH + "+?" + NO_BLANK_NO_SLASH + "|"
			+ NO_BLANK_NO_SLASH + "*?)(" + SLASH + "(" + NO_BLANK_NO_SLASH + "*?|" + NO_BLANK_NO_SLASH + NO_SLASH + "+?)"
			+ NO_BLANK_NO_SLASH + ")*)+" + SLASH + "?";

	public static byte[] getBytes(File f) {
		byte[] b = new byte[(int) f.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		try {
			fis.read(b);
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void copyDirFromDirToDir(String srcName, File srcParentDir, File destDir) throws IOException {
		copyDirFromDirToDir(srcName, srcParentDir, destDir, CopyStrategy.REPLACE);
	}

	public static void copyDirFromDirToDir(String srcName, File srcParentDir, File destDir, CopyStrategy stragtegy) throws IOException {
		copyDirToDir(new File(srcParentDir, srcName), destDir);
	}

	public static File copyDirToDir(File src, File dest) throws IOException {
		return copyDirToDir(src, dest, CopyStrategy.REPLACE);
	}

	public static File copyDirToDir(File src, File dest, CopyStrategy strategy) throws IOException {
		File newDir = new File(dest, src.getName());
		newDir.mkdirs();
		copyContentDirToDir(src, newDir, strategy);
		return newDir;
	}

	public static void copyDirFromDirToDirIncludingCVSFiles(String srcName, File srcParentDir, File destDir) throws IOException {
		copyDirToDirIncludingCVSFiles(new File(srcParentDir, srcName), destDir);
	}

	public static File copyDirToDirIncludingCVSFiles(File src, File dest) throws IOException {
		File newDir = new File(dest, src.getName());
		newDir.mkdirs();
		copyContentDirToDirIncludingCVSFiles(src, newDir);
		return newDir;
	}

	public static void copyContentDirToDir(File src, File dest) throws IOException {
		copyContentDirToDir(src, dest, CopyStrategy.REPLACE);
	}

	public static void copyContentDirToDir(File src, File dest, CopyStrategy strategy) throws IOException {
		copyContentDirToDir(src, dest, strategy, FileFilterUtils.trueFileFilter());
	}

	public static void copyContentDirToDir(File src, File dest, CopyStrategy strategy, FileFilter fileFilter) throws IOException {
		if (!src.exists()) {
			return;
		}
		if (!dest.exists()) {
			dest.mkdirs();
		}
		File[] fileArray = src.listFiles();
		for (int i = 0; i < fileArray.length; i++) {
			File curFile = fileArray[i];
			if (curFile.isDirectory() && !curFile.getName().equals("CVS") && fileFilter.accept(curFile)) {
				copyContentDirToDir(curFile, new File(dest, curFile.getName()), strategy, fileFilter);
			} else if (curFile.isFile() && fileFilter.accept(curFile)) {
				File destFile = new File(dest, curFile.getName());
				if (destFile.exists()) {
					switch (strategy) {
					case IGNORE_EXISTING:
						continue;
					case REPLACE_OLD_ONLY:
						if (!getDiskLastModifiedDate(curFile).after(getDiskLastModifiedDate(destFile))) {
							continue;
						}
					default:
						break;
					}
				}
				copyFileToFile(curFile, destFile);
			}
		}
	}

	public static void copyContentDirToDirIncludingCVSFiles(File src, File dest) throws IOException {
		if (!src.exists()) {
			return;
		}
		if (!dest.exists()) {
			dest.mkdirs();
		}
		File[] fileArray = src.listFiles();
		for (int i = 0; i < fileArray.length; i++) {
			File curFile = fileArray[i];
			if (curFile.isDirectory()) {
				copyDirFromDirToDirIncludingCVSFiles(curFile.getName(), src, dest);
			} else if (curFile.isFile()) {
				FileInputStream is = new FileInputStream(curFile);
				try {
					copyFileToDir(is, curFile.getName(), dest);
				} finally {
					is.close();
				}
			}
		}
	}

	public static boolean createNewFile(File newFile) throws IOException {
		boolean ret = false;
		if (!newFile.exists()) {
			if (!newFile.getParentFile().exists()) {
				ret = newFile.getParentFile().mkdirs();
				if (!ret) {
					newFile = newFile.getCanonicalFile();
					ret = newFile.getParentFile().mkdirs();
				}
				if (!ret) {
					System.err.println("WARNING: cannot create directory: " + newFile.getParent() + " createNewFile(File)["
							+ FileUtils.class.getName() + "]");
				}
			}
			try {
				ret = newFile.createNewFile();
			} catch (IOException e) {
				newFile = newFile.getCanonicalFile();
				ret = newFile.createNewFile();
				if (!ret) {
					System.err.println("WARNING: cannot create file: " + newFile.getAbsolutePath() + " createNewFile(File)["
							+ FileUtils.class.getName() + "]");
				}
			}
		}
		return ret;
	}

	public static void copyFileToFile(File curFile, File newFile) throws IOException {
		FileInputStream is = new FileInputStream(curFile);
		try {
			createNewFile(newFile);
			FileOutputStream os = new FileOutputStream(newFile);
			try {
				while (is.available() > 0) {
					byte[] byteArray = new byte[is.available()];
					is.read(byteArray);
					os.write(byteArray);
				}
				os.flush();
			} finally {
				os.close();
			}
		} finally {
			is.close();
		}
	}

	public static File copyFileToDir(FileInputStream is, String newFileName, File dest) throws IOException {
		File newFile = new File(dest, newFileName);
		createNewFile(newFile);
		FileOutputStream os = new FileOutputStream(newFile);
		try {
			while (is.available() > 0) {
				byte[] byteArray = new byte[is.available()];
				is.read(byteArray);
				os.write(byteArray);
			}
			os.flush();
		} finally {
			os.close();
		}
		return newFile;
	}

	public static File copyFileToDir(File src, File dest) throws IOException {
		return copyFileToDir(new FileInputStream(src), src.getName(), dest);
	}

	public static void saveToFile(File dest, byte[] b) throws IOException {
		createNewFile(dest);
		FileOutputStream fos = new FileOutputStream(dest);
		try {
			fos.write(b);
			fos.flush();
		} finally {
			fos.close();
		}
	}

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final FilenameFilter CVSFileNameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return !"CVS".equals(name);
		}
	};

	public static final FilenameFilter JARFileNameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".jar");
		}
	};

	public static void saveToFile(File dest, String fileContent) throws IOException {
		saveToFile(dest, fileContent, null);
	}

	public static void saveToFile(File dest, String fileContent, String encoding) throws IOException {
		createNewFile(dest);
		FileOutputStream fos = new FileOutputStream(dest);
		BufferedReader bufferedReader = new BufferedReader(new StringReader(fileContent));
		OutputStreamWriter fw = new OutputStreamWriter(fos, Charset.forName(encoding != null ? encoding : "UTF-8"));
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				fw.write(line);
				fw.write(LINE_SEPARATOR);
			}
			fw.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (fw != null) {
				fw.close();
			}
		}
	}

	public static void saveToFile(String fileName, String fileContent, File dir, String fileExtention) throws IOException {
		File dest = new File(dir.getAbsolutePath() + "/" + fileName + "." + fileExtention);
		saveToFile(dest, fileContent);
	}

	public static void saveToFile(String fileName, String fileContent, File dir) throws IOException {
		File dest = new File(dir.getAbsolutePath() + "/" + fileName);
		saveToFile(dest, fileContent);
	}

	public static void saveToFile(File file, InputStream is) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			byte[] b = new byte[8192];
			int read = 0;
			while ((read = is.read(b)) > 0) {
				fos.write(b, 0, read);
			}
		} finally {
			fos.close();
		}
	}

	public static String fileContents(File aFile) throws IOException {
		return fileContents(aFile, null);
	}

	public static String fileContents(File aFile, String encoding) throws IOException {
		FileInputStream fis = new FileInputStream(aFile);
		try {
			return fileContents(fis, encoding);
		} finally {
			fis.close();
		}
	}

	public static String fileContents(InputStream inputStream, String encoding) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, encoding != null ? encoding : "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(StringUtils.LINE_SEPARATOR);
		}
		return sb.toString();
	}

	/**
	 * @param file
	 * @return
	 */
	public static boolean recursiveDeleteFile(File file) {
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles();
			boolean returned = true;
			for (int i = 0; i < fileArray.length; i++) {
				File curFile = fileArray[i];
				if (recursiveDeleteFile(curFile) == false) {
					returned = false;
				}
			}
			return returned && file.delete();
		} else {
			return file.delete();
		}
	}

	public static String convertBackslashesToSlash(String fileName) {
		return fileName.replaceAll("\\\\", "/");
	}

	public static int countFilesInDirectory(File directory, boolean recursive) {
		return countFilesInDirectory(directory, recursive, null);
	}

	public static int countFilesInDirectory(File directory, boolean recursive, FileFilter fileFilter) {
		if (!directory.isDirectory() || !directory.exists()) {
			return -1;
		}
		File[] files = directory.listFiles();
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (fileFilter != null && !fileFilter.accept(file)) {
				continue;
			}
			if (file.isDirectory()) {
				if (recursive) {
					count += countFilesInDirectory(file, recursive);
				}
			} else {
				count++;
			}
		}
		return count;
	}

	/**
	 * Recursive computing of last modified date (deep check for contained files)
	 * 
	 * @param file
	 * @return
	 */
	public static Date getDiskLastModifiedDate(File file) {
		if (file == null || !file.exists()) {
			return new Date(0);
		}
		if (file.isFile()) {
			return new Date(file.lastModified());
		} else {
			File[] fileArray = file.listFiles();
			Date returned = new Date(file.lastModified());
			if (fileArray == null) {
				return returned;
			}
			if (fileArray.length > 0) {
				returned = new Date(0); // the lastModified() takes into account contained files and directories (but we want to ignore
				// everything related to CVS)
			}
			for (int i = 0; i < fileArray.length; i++) {
				File curFile = fileArray[i];
				if (curFile.isDirectory() && curFile.getName().equals("CVS")) {
					continue;
				}
				if (curFile.isFile() && curFile.getName().equals(".cvsignore")) {
					continue;
				}
				Date d = getDiskLastModifiedDate(curFile);
				if (d.after(returned)) {
					returned = d;
				}
			}
			return returned;
		}
	}

	public static void main(String[] args) {
		File file = new File("C:\\Documents and Settings\\gpolet.DENALI\\Desktop\\Workers Remittances-19-02-09.prj");
		Vector<File> files = listFilesRecursively(file, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("~");
			}
		});
		for (File file2 : files) {
			System.err.println(file2.getAbsolutePath());
		}
	}

	public static boolean isStringValidForFileName(String s) {
		return s != null && !UNACCEPTABLE_CHARS_PATTERN.matcher(s).find() && s.matches(VALID_FILE_NAME_REGEXP) && s.length() < 256;
	}

	public static String removeNonASCIIAndPonctuationAndBadFileNameChars(String s) {
		if (s.lastIndexOf(".") > 0) {
			String s1 = s.substring(s.lastIndexOf(".") + 1);
			String s0 = s.substring(0, s.lastIndexOf("."));
			s0 = performCleanup(s0);
			s1 = performCleanup(s1);
			return s0 + "." + s1;
		}

		return performCleanup(s);
	}

	private static String performCleanup(String s) {
		s = StringUtils.convertAccents(s);
		s = s.replaceAll(BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP, "-");
		s = s.replaceAll("\\P{ASCII}+", "-");
		s = s.replaceAll("[^\\w]+", "-");
		return s;
	}

	/**
	 * @param componentName
	 * @return
	 */
	public static String getValidFileName(String fileName) {
		fileName = fileName.replace('\\', '/');
		StringBuffer sb = new StringBuffer();
		Matcher m = UNACCEPTABLE_SLASH_PATTERN.matcher(fileName);
		while (m.find()) {
			m.appendReplacement(sb, "/");
		}
		m.appendTail(sb);
		m = UNACCEPTABLE_CHARS_PATTERN.matcher(sb.toString());
		sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "_");
		}
		m.appendTail(sb);
		fileName = sb.toString();
		String extension = null;
		if (fileName.length() > 4) {
			if (fileName.charAt(fileName.length() - 4) == '.') {
				extension = fileName.substring(fileName.length() - 4);
				fileName = fileName.substring(0, fileName.length() - 4);
			} else if (fileName.charAt(fileName.length() - 5) == '.') {
				extension = fileName.substring(fileName.length() - 5);
				fileName = fileName.substring(0, fileName.length() - 5);
			}
		}
		sb.setLength(0);
		int previous = 0;
		int index;
		while ((index = fileName.indexOf('/', previous)) > -1) {
			if (index - previous > 240) {
				sb.append(fileName.substring(previous, previous + 240)).append('/');
			} else {
				sb.append(fileName.substring(previous, index + 1));
			}
			previous = index + 1;
		}
		index = fileName.length();
		if (index - previous > 240) {
			sb.append(fileName.substring(previous, previous + 240));
		} else {
			sb.append(fileName.substring(previous, index));
		}
		if (extension != null) {
			sb.append(extension);
		}
		return sb.toString();
	}

	/**
	 * @param dir
	 */
	public static void deleteDir(File dir) {
		if (!dir.isDirectory()) {
			System.err.println("Tried to delete a directory but file is not a directory: " + dir.getAbsolutePath());
			return;
		}
		File[] f = dir.listFiles();
		if (f == null) {
			return;
		}
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (file.isDirectory()) {
				deleteDir(file);
			} else {
				file.delete();
			}
		}
		dir.delete();
	}

	/**
	 * Recursively deletes all the files of the specified directory. Directories themselves are not removed.
	 * 
	 * @param dir
	 */
	public static void deleteFilesInDir(File dir) {
		deleteFilesInDir(dir, false);
	}

	public static void deleteFilesInDir(File dir, boolean keepCVSTags) {
		if (!dir.isDirectory()) {
			System.err.println("Tried to delete a directory but file is not a directory: " + dir.getAbsolutePath());
			return;
		}
		if (keepCVSTags && dir.getName().equals("CVS")) {
			System.err.println("Tried to delete CVS directory but keepCVSTags flag is true!");
			return;
		}

		File[] f = dir.listFiles();
		if (f == null) {
			return;
		}
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (file.isDirectory()) {
				if (!file.getName().equals("CVS") || !keepCVSTags) {
					deleteFilesInDir(file, keepCVSTags);
				}
			} else {
				file.delete();
			}
		}
	}

	public static boolean directoryContainsFile(File directory, File file) {
		if (file.equals(directory)) {
			return true;
		}
		if (file.getParentFile() != null) {
			return directoryContainsFile(directory, file.getParentFile());
		}
		return false;
	}

	/**
	 *
	 */

	public static void makeFileHidden(File f) {
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			try {
				Runtime.getRuntime().exec("attrib +H \"" + f.getAbsolutePath() + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 */

	public static void unmakeFileHidden(File f) {
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			try {
				Runtime.getRuntime().exec("attrib -H \"" + f.getAbsolutePath() + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param desktopIni
	 */
	public static void makeFileSystem(File f) {
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			try {
				Runtime.getRuntime().exec("attrib +S \"" + f.getAbsolutePath() + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param desktopIni
	 */
	public static void unmakeFileSystem(File f) {
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			try {
				Runtime.getRuntime().exec("attrib -S \"" + f.getAbsolutePath() + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isFileContainedIn(File aFile, File ancestorFile) {
		File current = aFile;
		while (current != null) {
			if (current.equals(ancestorFile)) {
				return true;
			}
			current = current.getParentFile();
		}
		return false;
	}

	/**
	 * Finds a relative path to a given file, relative to a specified directory.
	 * 
	 * @param file
	 *            file that the relative path should resolve to
	 * @param relativeToDir
	 *            directory that the path should be relative to
	 * @return a relative path. This always uses / as the separator character.
	 */
	public static String makeFilePathRelativeToDir(File file, File relativeToDir) throws IOException {
		String canonicalFile = file.getCanonicalPath();
		String canonicalRelTo = relativeToDir.getCanonicalPath();
		String[] filePathComponents = getPathComponents(canonicalFile);
		String[] relToPathComponents = getPathComponents(canonicalRelTo);
		int i = 0;
		while (i < filePathComponents.length && i < relToPathComponents.length && filePathComponents[i].equals(relToPathComponents[i])) {
			i++;
		}
		StringBuffer buf = new StringBuffer();
		for (int j = i; j < relToPathComponents.length; j++) {
			buf.append("../");
		}
		for (int j = i; j < filePathComponents.length - 1; j++) {
			buf.append(filePathComponents[j]).append('/');
		}
		buf.append(filePathComponents[filePathComponents.length - 1]);
		return buf.toString();
	}

	/**
	 * Splits a path into components using the OS file separator character. This can be used on the results of File.getCanonicalPath().
	 * 
	 * @param canonicalPath
	 *            a file path that uses the OS file separator character
	 * @return an array of strings, one for each component of the path
	 */
	public static String[] getPathComponents(String canonicalPath) {
		String regex = File.separator;
		if (regex.equals("\\")) {
			regex = "\\\\";
		}
		return canonicalPath.split(regex);
	}

	/**
	 * @param file
	 * @param wd
	 * @return
	 * @throws IOException
	 */
	/*public static String makeFilePathRelativeToDir(File file, File dir) throws IOException {
		System.out.println("file=" + file.getAbsolutePath());
		System.out.println("dir=" + dir.getAbsolutePath());
		file = file.getCanonicalFile();
		dir = dir.getCanonicalFile();
		String d = dir.getCanonicalPath().replace('\\', '/');
		String f = file.getCanonicalPath().replace('\\', '/');
		int i = 0;
		while (i < d.length() && i < f.length() && d.charAt(i) == f.charAt(i)) {
			i++;
		}
		String common = d.substring(0, i);
		if (!new File(common).exists()) {
			if (common.indexOf('/') > -1) {
				common = common.substring(0, common.lastIndexOf('/') + 1);
			}
			if (!new File(common).exists()) {
				System.err.println("WARNING\tNothing in common between\n" + file.getAbsolutePath() + " and\n" + dir.getAbsolutePath());
				return file.getAbsolutePath();
			}
		}
		File commonFather = new File(common);
		System.out.println("commonFather=" + commonFather.getAbsolutePath());
		File parentDir = dir;
		StringBuilder sb = new StringBuilder();
		while (parentDir != null && !commonFather.equals(parentDir)) {
			sb.append("../");
			parentDir = parentDir.getParentFile();
		}
		sb.append(f.substring(common.length()));
		if (sb.charAt(0) == '/') {
			return sb.substring(1);
		}
		System.out.println("returned=" + sb.toString());
		return sb.toString();
	}*/

	public static File createTempFile(InputStream in) {
		File tempFile;
		try {
			tempFile = File.createTempFile("FlexoTempFile", null);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		tempFile.deleteOnExit();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempFile);
			byte[] b = new byte[8192];
			int r;
			while ((r = in.read(b)) > 0) {
				fos.write(b, 0, r);
			}
			return tempFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Creates an empty directory in the default temporary-file directory, using the given prefix and suffix to generate its name.
	 * 
	 * @param prefix
	 *            The prefix string to be used in generating the directory's name; must be at least three characters long
	 * @param suffix
	 *            The suffix string to be used in generating the directory's name; may be null, in which case the suffix ".tmp" will be used
	 * @return An abstract pathname denoting a newly-created empty directory
	 * @throws IOException
	 */
	public static File createTempDirectory(String prefix, String suffix) throws IOException {
		File tmp = File.createTempFile(prefix, suffix);
		File tmpDir = new File(tmp.getAbsolutePath());
		if (tmp.delete() && tmpDir.mkdirs()) {
			return tmpDir;
		} else {
			tmpDir = new File(System.getProperty("java.io.tmpdir"), prefix + suffix);
			tmpDir.mkdirs();
			return tmpDir;
		}
	}

	public static Vector<File> listFilesRecursively(File dir, final FilenameFilter filter) {
		if (!dir.isDirectory()) {
			return null;
		}
		Vector<File> files = new Vector<File>();
		File[] f = dir.listFiles();
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (file.isDirectory()) {
				files.addAll(listFilesRecursively(file, filter));
			} else if (filter.accept(dir, file.getName())) {
				files.add(file);
			}
		}
		return files;
	}

	public static String lowerCaseExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		if (fileName.indexOf('.') > -1) {
			return fileName.substring(0, fileName.lastIndexOf('.')) + fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
		}
		return fileName;
	}

	/**
	 * An extension to Java's API rename method. Will attempt Java's method of doing the rename, if this fails, this method will then
	 * attempt to forcibly copy the old file to the new file name, and then delete the old file. (This in appearance makes it look like a
	 * file rename has occurred.) The method will also attempt to preserve the new file's modification times and permissions to equal that
	 * of the original file's.
	 * 
	 * @param source
	 *            File
	 * @param destination
	 *            File
	 * @return boolean
	 * @throws IOException
	 */
	public static boolean rename(File source, File destination) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			// First (very important on Windows) delete the destination if it exists (rename will fail on Windows if destination
			// exists)
			if (destination.exists()) {
				destination.delete();
			}
			// Do a normal API rename attempt
			if (source.renameTo(destination)) {
				return true;
			}
			FileUtils.createNewFile(destination);
			// API rename attempt failed, forcibly copy
			bis = new BufferedInputStream(new FileInputStream(source));
			bos = new BufferedOutputStream(new FileOutputStream(destination));

			// Do the copy
			pipeStreams(bos, bis);

			// Close the files
			bos.flush();

			// Close the files
			bis.close();
			bos.close();

			// Attempt to preserve file modification times
			destination.setLastModified(source.lastModified());
			if (!source.canWrite()) {
				destination.setReadOnly();
			}

			// Delete the original
			source.delete();

			bis = null;
			bos = null;
			return true;
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void pipeStreams(OutputStream to, InputStream from) throws IOException {
		BufferedInputStream in = new BufferedInputStream(from);
		BufferedOutputStream out = new BufferedOutputStream(to);
		byte[] buffer = new byte[8192];
		int read;
		while ((read = in.read(buffer, 0, 8192)) != -1) {
			out.write(buffer, 0, read);
		}
		out.flush();
	}

	public static String createOrUpdateFileFromURL(URL url, File file) {
		return createOrUpdateFileFromURL(url, file, null);
	}

	public static String createOrUpdateFileFromURL(URL url, File file, Map<String, String> headers) {
		long lastModified = 0;
		String fileContent = null;
		if (file.exists()) {
			lastModified = file.lastModified();
			try {
				fileContent = FileUtils.fileContents(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (url != null) {
			try {
				URLConnection c = url.openConnection();
				if (headers != null) {
					for (Map.Entry<String, String> h : headers.entrySet()) {
						c.addRequestProperty(h.getKey(), h.getValue());
					}
				}
				if (c instanceof HttpURLConnection) {
					HttpURLConnection connection = (HttpURLConnection) c;
					connection.setIfModifiedSince(lastModified);
					connection.connect();
					if (connection.getResponseCode() == 200) {
						fileContent = FileUtils.fileContents(connection.getInputStream(), "UTF-8");
						FileUtils.saveToFile(file, fileContent);
					}
				} else {
					if (c.getDate() == 0 || c.getDate() > lastModified) {
						fileContent = FileUtils.fileContents(c.getInputStream(), "UTF-8");
						FileUtils.saveToFile(file, fileContent);
					}
				}
			} catch (IOException e) {
				logger.warning("Could not read url " + url);
				e.printStackTrace();

			}
		}
		return fileContent;
	}

	public static File getApplicationDataDirectory() {
		File dir = new File(System.getProperty("user.home"), ".openflexo");
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			String appData = System.getenv("APPDATA");
			if (appData != null) {
				File f = new File(appData);
				if (f.isDirectory() && f.canWrite()) {
					dir = new File(f, "OpenFlexo");
				}
			}
		} else if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			dir = new File(new File(System.getProperty("user.home")), "Library/OpenFlexo");
		}
		return dir;
	}

	private static final String MACOS_DOC_DIRECTORY_KEY = "docs";

	public static File getDocumentDirectory() {
		if (ToolBox.isMacOS()) {
			try {
				Class<?> fileManagerClass = Class.forName("com.apple.eio.FileManager");
				short userDomain = fileManagerClass.getField("kUserDomain").getShort(null);
				Method typeToInt = fileManagerClass.getDeclaredMethod("OSTypeToInt", String.class);
				Method findFolder = fileManagerClass.getDeclaredMethod("findFolder", short.class, int.class);
				int docDirectoryInt = (Integer) typeToInt.invoke(null, MACOS_DOC_DIRECTORY_KEY);
				String documentDirectory = (String) findFolder.invoke(null, userDomain, docDirectoryInt);
				return new File(documentDirectory);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else if (ToolBox.isWindows()) {
			String value = WinRegistryAccess.getRegistryValue(WIN_REGISTRY_DOCUMENTS_KEY_PATH, WIN_REGISTRY_DOCUMENTS_ATTRIBUTE,
					WinRegistryAccess.REG_EXPAND_SZ_TOKEN);
			value = WinRegistryAccess.substituteEnvironmentVariable(value);
			if (value != null) {
				return new File(value);
			}
		}
		return new File(System.getProperty("user.home"), "Documents");
	}
}
