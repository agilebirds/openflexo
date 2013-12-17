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
package org.openflexo.foundation.rm;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ZipUtils;

public class TestConvertion extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestConvertion.class.getPackage().getName());

	public TestConvertion() {
		super("TestConvertion");
	}

	public void testConvertFBV() {
		logger.info("testConvertFBV : not maintained");

		// openAndRunValidation("FBVWarTest","FBV.prj.1.1RC5.zip","FBV.prj",CodeType.PROTOTYPE);
	}

	public void testConvertClimact() {
		logger.info("testConvertClimact : not maintained");
		// openAndRunValidation("ClimactWarTest","Climact.prj.1.1RC5.zip","Climact.prj",CodeType.PROTOTYPE);
	}

	public void testConvertHyperlinkTest() {
		logger.info("testConvertHyperlinkTest : not maintained");
		// openAndRunValidation("HyperlinkTest","HyperlinkTest.prj.1.1RC5.zip","HyperlinkTest.prj",CodeType.PROTOTYPE);
	}

	public void testConvertRentDVD() {
		logger.info("testConvertRentDVD : not maintained");
		// openAndRunValidation("RentDVD","RentDVD.1.2.zip","Rent_a_DVD.prj",CodeType.PROTOTYPE);
	}

	private void openAndRunValidation(String name, String zipName, String prjName, CodeType codeType) {
		log("Convert : " + name);
		FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
		File outputDir = null;
		try {
			File f = File.createTempFile("TestConvertion_", null);
			outputDir = new File(f.getParentFile(), f.getName() + "DIR");
			f.delete();
		} catch (IOException e) {
			e.printStackTrace();
			outputDir = new File(System.getProperty("java.io.tmpdir"), "TestConvertion");
		}
		outputDir.mkdirs();

		File unzipDir = null;
		try {
			File f = File.createTempFile("TestConvertionUnzip_", null);
			unzipDir = new File(f.getParentFile(), f.getName() + "Unzip" + name);
			f.delete();
		} catch (IOException e) {
			e.printStackTrace();
			unzipDir = new File(System.getProperty("java.io.tmpdir"), "TestConvertionUnzip" + name);
		}
		unzipDir.mkdirs();

		try {
			ZipUtils.unzip(new FileResource(zipName), unzipDir);
		} catch (ZipException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			fail();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			fail();
		}

		File projectDirectory = new File(unzipDir, prjName);

		try {
			projectDirectory = FileUtils.copyDirToDir(projectDirectory, outputDir);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail("Copy of Test project failed!");
			return;
		}
		FlexoEditor editor = reloadProject(projectDirectory);
		editor.getProject().checkModelConsistency(CodeType.PROTOTYPE);
		saveProject(editor.getProject());
	}

}
