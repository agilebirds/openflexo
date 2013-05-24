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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.openflexo.localization.FlexoLocalization;

public class ZipUtils {

	public static final String VALID_ENTRY_NAME_REGEXP = "\\p{ASCII}+";
	public static final Pattern VALID_ENTRY_NAME_PATTERN = Pattern.compile(VALID_ENTRY_NAME_REGEXP);

	private static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		try {
			byte[] buffer = new byte[4096];
			int len;
			while ((len = in.read(buffer)) >= 0) {
				out.write(buffer, 0, len);
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	public static final void unzip(File zip, File outputDir) throws ZipException, IOException {
		unzip(zip, outputDir, null);
	}

	public static final void unzip(File zip, File outputDir, IProgress progress) throws ZipException, IOException {
		Enumeration<? extends ZipEntry> entries;
		outputDir = outputDir.getCanonicalFile();
		if (!outputDir.exists()) {
			boolean b = outputDir.mkdirs();
			if (!b) {
				throw new IllegalArgumentException("Could not create dir " + outputDir.getAbsolutePath());
			}
		}
		if (!outputDir.isDirectory()) {
			throw new IllegalArgumentException(outputDir.getAbsolutePath() + "is not a directory or is not writeable!");
		}
		ZipFile zipFile;
		zipFile = new ZipFile(zip);
		entries = zipFile.entries();
		if (progress != null) {
			progress.resetSecondaryProgress(zipFile.size());
		}
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("unzipping") + " " + entry.getName());
			}
			if (entry.getName().startsWith("__MACOSX")) {
				continue;
			}
			if (entry.isDirectory()) {
				// Assume directories are stored parents first then
				// children.
				// This is not robust, just for demonstration purposes.
				new File(outputDir, entry.getName().replace('\\', '/')).mkdirs();
				continue;
			}
			File outputFile = new File(outputDir, entry.getName().replace('\\', '/'));
			if (outputFile.getName().startsWith("._")) {
				// This block is made to drop MacOS crap added to zip files
				if (zipFile.getEntry(entry.getName().substring(0, entry.getName().length() - outputFile.getName().length())
						+ outputFile.getName().substring(2)) != null) {
					continue;
				}
				if (new File(outputFile.getParentFile(), outputFile.getName().substring(2)).exists()) {
					continue;
				}
			}
			FileUtils.createNewFile(outputFile);
			InputStream zipStream = null;
			FileOutputStream fos = null;
			try {
				zipStream = zipFile.getInputStream(entry);
				if (zipStream == null) {
					System.err.println("Could not find input stream for entry: " + entry.getName());
					continue;
				}
				fos = new FileOutputStream(outputFile);
				copyInputStream(zipStream, new BufferedOutputStream(fos));
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Could not extract: " + outputFile.getAbsolutePath() + " maybe some files contains invalid characters.");
			} finally {
				IOUtils.closeQuietly(zipStream);
				IOUtils.closeQuietly(fos);
			}
		}
		Collection<File> listFiles = org.apache.commons.io.FileUtils.listFiles(outputDir, null, true);
		for (File file : listFiles) {
			if (file.isFile() && file.getName().startsWith("._")) {
				File f = new File(file.getParentFile(), file.getName().substring(2));
				if (f.exists()) {
					file.delete();
				}
			}
		}
		zipFile.close();
	}

	public static void unzip(File zip, String outputDirPath, IProgress progress) throws ZipException, IOException {
		File outputDir = new File(outputDirPath);
		unzip(zip, outputDir, progress);
	}

	public static void unzip(File zip, String outputDirPath) throws ZipException, IOException {
		unzip(zip, outputDirPath, null);
	}

	/**
	 * This method makes a zip file on file <code>zipOutput</code>out of the given <code>fileToZip</code>
	 * 
	 * @param zipOutput
	 *            - the output where to write the zip
	 * @param fileToZip
	 *            the file to zip (wheter it is a file or a directory)
	 * @throws IOException
	 */
	public static void makeZip(File zipOutput, File fileToZip) throws IOException {
		makeZip(zipOutput, fileToZip, null);
	}

	/**
	 * This method makes a zip file on file <code>zipOutput</code>out of the given <code>fileToZip</code>
	 * 
	 * @param zipOutput
	 *            - the output where to write the zip
	 * @param fileToZip
	 *            the file to zip (wheter it is a file or a directory)
	 * @throws IOException
	 */
	public static void makeZip(File zipOutput, File fileToZip, IProgress progress) throws IOException {
		makeZip(zipOutput, fileToZip, progress, null);
	}

	/**
	 * This method makes a zip file on file <code>zipOutput</code>out of the given <code>fileToZip</code>
	 * 
	 * @param zipOutput
	 *            - the output where to write the zip
	 * @param fileToZip
	 *            the file to zip (wheter it is a file or a directory)
	 * @throws IOException
	 */
	public static void makeZip(File zipOutput, File fileToZip, IProgress progress, FileFilter filter) throws IOException {
		makeZip(zipOutput, fileToZip, progress, filter, Deflater.DEFAULT_COMPRESSION);
	}

	public static void makeZip(File zipOutput, File fileToZip, IProgress progress, FileFilter filter, int level) throws IOException {
		FileUtils.createNewFile(zipOutput);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipOutput));
		zos.setLevel(level);
		try {
			if (fileToZip.isDirectory()) {
				if (progress != null) {
					progress.resetSecondaryProgress(FileUtils.countFilesInDirectory(fileToZip, true) + 1);
				}
				zipDir(fileToZip.getParentFile().getAbsolutePath().length() + 1, fileToZip, zos, progress, filter);
			} else {
				zipFile(fileToZip, zos, progress);
			}
		} finally {
			zos.close();
		}
	}

	/**
	 * This method makes a zip on the outputsream <code>zos</code>out of the given <code>dirToZip</code>
	 * 
	 * @param dirToZip
	 *            the directory to zip
	 * @param zos
	 *            the output stream where to write the zip data
	 * @throws IOException
	 */
	public static void zipDir(File dirToZip, ZipOutputStream zos) throws IOException {
		zipDir(dirToZip, zos, null);
	}

	/**
	 * This method makes a zip on the outputsream <code>zos</code>out of the given <code>dirToZip</code>
	 * 
	 * @param dirToZip
	 *            the directory to zip
	 * @param zos
	 *            the output stream where to write the zip data
	 * @throws IOException
	 */
	public static void zipDir(File dirToZip, ZipOutputStream zos, IProgress progress) throws IOException {
		if (progress != null) {
			progress.resetSecondaryProgress(FileUtils.countFilesInDirectory(dirToZip, true));
		}
		zipDir(dirToZip.getParentFile().getAbsolutePath().length() + 1, dirToZip, zos, progress, null);
	}

	/**
	 * This method makes a zip on the outputsream <code>zos</code>out of the given <code>fileToZip</code>
	 * 
	 * @param fileToZip
	 *            the file to zip
	 * @param zos
	 *            the output stream where to write the zip data
	 * @throws IOException
	 */

	public static void zipFile(File fileToZip, ZipOutputStream zos) throws IOException {
		zipFile(fileToZip, zos, null);
	}

	/**
	 * This method makes a zip on the outputsream <code>zos</code>out of the given <code>fileToZip</code>
	 * 
	 * @param fileToZip
	 *            the file to zip
	 * @param zos
	 *            the output stream where to write the zip data
	 * @throws IOException
	 */

	public static void zipFile(File fileToZip, ZipOutputStream zos, IProgress progress) throws IOException {
		if (progress != null) {
			progress.resetSecondaryProgress(1);
		}
		zipFile(fileToZip.getParentFile().getAbsolutePath().length() + 1, fileToZip, zos, progress);
	}

	public static void zipDir(int pathPrefixSize, File dirToZip, ZipOutputStream zos, IProgress progress, FileFilter filter)
			throws IOException {
		String[] dirList = dirToZip.list();
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(dirToZip, dirList[i]);
			if (filter == null || f != null && filter.accept(f)) {
				if (f.isDirectory()) {
					zipDir(pathPrefixSize, f, zos, progress, filter);
				} else {
					zipFile(pathPrefixSize, f, zos, progress);
				}
			}
		}
	}

	private static void zipFile(int pathPrefixSize, File fileToZip, ZipOutputStream zos, IProgress progress) throws IOException {
		if (!fileToZip.exists()) {
			return;
		}
		byte[] readBuffer = new byte[4096];
		int bytesIn = 0;
		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("zipping_file") + " "
					+ fileToZip.getAbsolutePath().substring(pathPrefixSize));
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		try {
			ZipEntry anEntry = new ZipEntry(fileToZip.getAbsolutePath().substring(pathPrefixSize).replace('\\', '/'));
			// place the zip entry in the ZipOutputStream object
			zos.putNextEntry(anEntry);
			// now write the content of the file to the ZipOutputStream
			while ((bytesIn = fis.read(readBuffer)) != -1) {
				zos.write(readBuffer, 0, bytesIn);
			}
		} finally {
			fis.close();
		}
	}

	public static void createEmptyZip(File zip) throws IOException {
		ZipOutputStream zos = null;
		try {
			FileUtils.createNewFile(zip);
			zos = new ZipOutputStream(new FileOutputStream(zip));
			ZipEntry entry = new ZipEntry("");
			zos.putNextEntry(entry);
		} finally {
			if (zos != null) {
				zos.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			File zip = new File("d:/Work/SNA_1LS_REPAIR_VOICE_1_0_RO.zip");
			File tmp = new File("D:/tmp/TestUnzip");
			tmp.mkdirs();
			unzip(zip, tmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}