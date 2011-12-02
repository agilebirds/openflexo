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
package org.openflexo.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ZipUtils;

public class ZipTest extends TestCase {

	private static final String INNER_FILE_NAME = "InnerFileName";
	private static final String SUB_INNER_FILE_NAME = "SubInnerFileName";
	private static final String INNER_DIRECTORY_NAME = "InnerDirectoryName";

	private static final String FILE_CONTENT = "²&é\"'(§è!çà)-azertyuiop^$qsdfghjklmùµ<wxcvbn,;:=³1234567890°_AZERTYUIOP¨*QSDFGHJKLM%%%£>WXCVBN?./+|@#^{}€[]µ´`\\~\n";

	private File directory;
	private File innerFile;
	private File innerDirectory;
	private File subInnerFile;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		File temp = File.createTempFile("ZipTests", "");
		temp.delete();
		assertTrue(temp.mkdir());
		directory = temp;
		System.err.println("Zip directory test: " + directory.getAbsolutePath());
		innerFile = new File(directory, INNER_FILE_NAME);
		assertTrue(innerFile.createNewFile());
		FileUtils.saveToFile(innerFile, FILE_CONTENT);
		innerDirectory = new File(directory, INNER_DIRECTORY_NAME);
		assertTrue(innerDirectory.mkdir());
		subInnerFile = new File(innerDirectory, SUB_INNER_FILE_NAME);
		assertTrue(subInnerFile.createNewFile());
		FileUtils.saveToFile(subInnerFile, FILE_CONTENT);
	}

	@Override
	protected void tearDown() throws Exception {
		FileUtils.deleteDir(directory);
		super.tearDown();
	}

	public void testZipUnzip() throws Exception {
		File archive = File.createTempFile("ZipTestFile", ".zip");
		System.err.println("Zip archive: " + archive.getAbsolutePath());
		ZipUtils.makeZip(archive, directory);
		assertTrue(archive.exists());
		ZipFile zipFile = new ZipFile(archive);
		assertZipEntryCount(zipFile, 2); // Directories don't count
		File output = new File(System.getProperty("java.io.tmpdir"), "OutputOf" + archive.getName());
		ZipUtils.unzip(archive, output);
		File extractedDirectory = new File(output, directory.getName());
		File extractedInnerFile = new File(extractedDirectory, INNER_FILE_NAME);
		File extractedInnerDirectory = new File(extractedDirectory, INNER_DIRECTORY_NAME);
		File extractedSubInnerFile = new File(extractedInnerDirectory, SUB_INNER_FILE_NAME);
		assertTrue(extractedDirectory.exists());
		assertTrue(extractedDirectory.isDirectory());
		assertTrue(extractedInnerDirectory.exists());
		assertTrue(extractedInnerDirectory.isDirectory());
		assertTrue(extractedInnerFile.exists());
		assertTrue(extractedInnerFile.isFile());
		assertTrue(extractedSubInnerFile.exists());
		assertTrue(extractedSubInnerFile.isFile());
		assertEquals(FILE_CONTENT, FileUtils.fileContents(extractedInnerFile));
		assertEquals(FILE_CONTENT, FileUtils.fileContents(extractedSubInnerFile));
		FileUtils.deleteDir(output);
		archive.delete();
	}

	public void testZipUnzipFiltered() throws Exception {
		File archive = File.createTempFile("ZipTestFile", ".zip");
		System.err.println("Zip archive: " + archive.getAbsolutePath());
		ZipUtils.makeZip(archive, directory, null, new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.equals(innerFile);
			}
		});
		assertTrue(archive.exists());
		ZipFile zipFile = new ZipFile(archive);
		assertZipEntryCount(zipFile, 1);
		File output = new File(System.getProperty("java.io.tmpdir"), "OutputOf" + archive.getName());
		ZipUtils.unzip(archive, output);
		File extractedDirectory = new File(output, directory.getName());
		File extractedInnerFile = new File(extractedDirectory, INNER_FILE_NAME);
		File extractedInnerDirectory = new File(extractedDirectory, INNER_DIRECTORY_NAME);
		File extractedSubInnerFile = new File(extractedInnerDirectory, SUB_INNER_FILE_NAME);
		assertTrue(extractedDirectory.exists());
		assertTrue(extractedDirectory.isDirectory());
		assertTrue(extractedInnerDirectory.exists());
		assertTrue(extractedInnerDirectory.isDirectory());
		assertFalse(extractedInnerFile.exists());
		assertTrue(extractedSubInnerFile.exists());
		assertTrue(extractedSubInnerFile.isFile());
		FileUtils.deleteDir(output);
		archive.delete();
	}

	public void testZipUnzipFiltered2() throws Exception {
		File archive = File.createTempFile("ZipTestFile", ".zip");
		System.err.println("Zip archive: " + archive.getAbsolutePath());
		ZipUtils.makeZip(archive, directory, null, new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.equals(subInnerFile);
			}
		});
		assertTrue(archive.exists());
		ZipFile zipFile = new ZipFile(archive);
		assertZipEntryCount(zipFile, 1);
		File output = new File(System.getProperty("java.io.tmpdir"), "OutputOf" + archive.getName());
		ZipUtils.unzip(archive, output);
		File extractedDirectory = new File(output, directory.getName());
		File extractedInnerFile = new File(extractedDirectory, INNER_FILE_NAME);
		File extractedInnerDirectory = new File(extractedDirectory, INNER_DIRECTORY_NAME);
		File extractedSubInnerFile = new File(extractedInnerDirectory, SUB_INNER_FILE_NAME);
		assertTrue(extractedDirectory.exists());
		assertTrue(extractedDirectory.isDirectory());
		assertFalse(extractedInnerDirectory.exists());
		assertTrue(extractedInnerFile.exists());
		assertFalse(extractedSubInnerFile.exists());
		FileUtils.deleteDir(output);
		archive.delete();
	}

	/**
	 * @param zipFile
	 */
	private void assertZipEntryCount(ZipFile zipFile, int expected) {
		int count = 0;
		Enumeration<? extends ZipEntry> en = zipFile.entries();
		while (en.hasMoreElements()) {
			ZipEntry e = en.nextElement();
			count++;
		}
		assertEquals(expected, count);
	}

}
