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
import java.util.Date;

import junit.framework.TestCase;

import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

public class FileUtilTest extends TestCase {

	public void testInvalidFileName() throws Exception {
		// First we check that invalid file names are indeed refused
		String[] invalidNames = new String[] { "Coucou /Blabla", "Je suis: le meilleur", "<GnaGna>", "<", ">", "|", "?", "*", "ééé" };
		for (int i = 0; i < invalidNames.length; i++) {
			String string = invalidNames[i];
			assertFalse("File name '" + string + "' should be invalid", FileUtils.isStringValidForFileName(string));
		}
		// Then we test that getting a valid file name returns indeed an acceptable name
		for (int i = 0; i < invalidNames.length; i++) {
			String string = FileUtils.getValidFileName(invalidNames[i]);
			assertTrue("File name '" + string + "' should be invalid", FileUtils.isStringValidForFileName(string));
		}
		// Then we do some basic test on file names that should be valid. Beware that '/' MUST be accepted!!!!!
		// See also org.openflexo.foundation.rm.FlexoFileResource.setResourceFile(FlexoProjectFile) and
		// org.openflexo.foundation.utils.FlexoProjectFile.nameIsValid() (maybe others rely on this)
		String[] validNames = new String[] { "Je suis-le meilleur", "Coucou/Blabla", "Some{Test}I made" };
		for (int i = 0; i < validNames.length; i++) {
			String string = validNames[i];
			assertTrue("File name '" + string + "' should be valid", FileUtils.isStringValidForFileName(string));
		}
		assertTrue(FileUtils
				.isStringValidForFileName("Documentation/ABSTRACTACTIVITY-NewProcess87-Call-the-customer-and-check-if-they-can-wait-until-engineer-is-in-office-Yes-log-comments---No--log-and-do-a-guard-recall--Drop-a-mail-to-2L-INF-team--with-ticket-details33893.png"));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append("a");
		}
		assertFalse(FileUtils.isStringValidForFileName(sb.toString()));
	}

	public void testFileNameFixing() {
		String s256 = "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
				+ "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
		String invalid = s256 + "/" + s256 + ".xml";
		String valid = s256.substring(0, 240) + "/" + s256.substring(0, 240) + ".xml";
		assertEquals(valid, FileUtils.getValidFileName(invalid));
	}

	public void testFileNameCleanUp() throws Exception {
		String test1 = "ùneImageéèàç?;,ù%$ô.jpg";
		String test2 = "éèàç?.;,ù%$ô";
		assertTrue(FileUtils.removeNonASCIIAndPonctuationAndBadFileNameChars(test1).matches("[\\-\\w.]*"));
		assertTrue(FileUtils.removeNonASCIIAndPonctuationAndBadFileNameChars(test2).matches("[\\-\\w.]*"));
	}

	public void testCopyStrategy() throws Exception {
		File tempDirectory = FileUtils.createTempDirectory("TestFileUtils", null);
		File destTempDirectory = FileUtils.createTempDirectory("TestFileUtilsDestination", null);
		File testFile = new File(tempDirectory, "TestFile");
		assertTrue(FileUtils.createNewFile(testFile));
		FileUtils.saveToFile(testFile, CONTENT);
		Date testFileLastModified = FileUtils.getDiskLastModifiedDate(testFile);
		File destFile = FileUtils.copyFileToDir(testFile, destTempDirectory);
		Date destFileModified = FileUtils.getDiskLastModifiedDate(destFile);
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.IGNORE_EXISTING);
		assertEquals("Last modified should not change when ignoring existing files", destFileModified,
				FileUtils.getDiskLastModifiedDate(destFile));
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.REPLACE_OLD_ONLY);
		assertEquals("Last modified should not change when replacing old files only", destFileModified,
				FileUtils.getDiskLastModifiedDate(destFile));
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.REPLACE);
		assertFalse("Last modified should have changed when replacing files ",
				destFileModified.equals(FileUtils.getDiskLastModifiedDate(destFile)));
		// Since we have replaced the file, we need to update its last modified
		destFileModified = FileUtils.getDiskLastModifiedDate(destFile);
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.saveToFile(testFile, CONTENT);
		assertFalse("Last modified should have changed after changing its content",
				testFileLastModified.equals(FileUtils.getDiskLastModifiedDate(testFile)));
		testFileLastModified = FileUtils.getDiskLastModifiedDate(testFile);
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.REPLACE_OLD_ONLY);
		assertFalse("Last modified should have changed when replacing old files only with a newer file",
				destFileModified.equals(FileUtils.getDiskLastModifiedDate(destFile)));
		FileUtils.deleteDir(tempDirectory);
		FileUtils.deleteDir(destTempDirectory);
	}

	public void testLowerCaseExtension() {
		assertEquals("coucou", FileUtils.lowerCaseExtension("coucou"));
		assertEquals(null, FileUtils.lowerCaseExtension(null));
		assertEquals("coucou.", FileUtils.lowerCaseExtension("coucou."));
		assertEquals("coucou.jpg", FileUtils.lowerCaseExtension("coucou.JPG"));
		assertEquals("cOucOu.jpg", FileUtils.lowerCaseExtension("cOucOu.jPg"));
		assertEquals("COUcou.j", FileUtils.lowerCaseExtension("COUcou.J"));
	}

	private static final String CONTENT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed accumsan tellus sit amet enim. In hac habitasse platea dictumst. Aliquam nec lacus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aenean pellentesque. Nam dui lorem, tempor quis, aliquet sed, porta ac, lacus. Aliquam erat volutpat. Maecenas lobortis scelerisque sapien. Nunc lorem augue, pulvinar sed, venenatis ac, venenatis at, quam. Curabitur rutrum. Sed vitae quam. Nulla nisi. Ut turpis. Vivamus rhoncus. Sed enim. Sed suscipit laoreet lacus. In hac habitasse platea dictumst.\r\n"
			+ "\r\n"
			+ "Mauris neque enim, congue dignissim, viverra non, consectetur ultrices, pede. Fusce convallis malesuada dui. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nunc et risus ut sapien blandit tincidunt. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Duis lorem. Nam turpis purus, accumsan eget, tincidunt non, aliquet quis, nibh. Nullam lectus sem, consequat non, mollis in, tempor vitae, nisl. Nulla facilisi. Sed suscipit tempor mauris. Donec volutpat commodo lectus. Pellentesque in arcu a leo posuere pretium. Aliquam eleifend dolor eget ligula. Nunc risus nulla, euismod eu, pellentesque id, vehicula sit amet, mi.\r\n"
			+ "\r\n"
			+ "Morbi condimentum velit in tellus. Aliquam tincidunt metus ut pede. Quisque ultrices quam quis ante. Praesent sit amet velit non eros suscipit mattis. Etiam dui. Nam id tortor et nunc varius cursus. Phasellus et neque non orci ornare adipiscing. Nullam viverra neque ac diam. Cras blandit enim vitae nulla. Nunc eu massa in erat rhoncus fermentum. Phasellus semper elementum nunc. Duis eu diam at pede blandit consectetur. Sed ultricies posuere urna.\r\n"
			+ "\r\n"
			+ "Nullam consectetur, tortor vitae imperdiet gravida, erat felis accumsan tortor, at feugiat nulla quam tristique neque. Praesent sodales. Nullam metus turpis, lacinia eu, posuere at, ornare non, dolor. Donec pharetra consectetur urna. Duis sagittis arcu sit amet quam. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Fusce nibh enim, faucibus id, consequat hendrerit, rhoncus at, turpis. Mauris ac quam. Phasellus viverra pulvinar pede. Cras quam. Morbi libero. Nunc ligula eros, lobortis nec, dapibus at, molestie id, ante. In dignissim ultrices neque. Maecenas lorem enim, molestie ac, euismod eu, vehicula eget, augue.\r\n"
			+ "\r\n"
			+ "Pellentesque elit nisi, convallis sit amet, mollis a, semper et, nulla. Vestibulum euismod. Vestibulum tincidunt tempus pede. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Duis sed elit at velit aliquet euismod. Integer lectus. Integer at est sed ligula suscipit posuere. Aenean libero. Aenean quis orci non ante porta dapibus. Etiam orci felis, aliquet nec, dictum quis, egestas a, ligula. Vivamus at lectus. Integer quis diam. Etiam vestibulum. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vivamus nisi. Aliquam pharetra tincidunt libero. Mauris bibendum quam eget tellus. Curabitur bibendum elementum quam. ";

}
