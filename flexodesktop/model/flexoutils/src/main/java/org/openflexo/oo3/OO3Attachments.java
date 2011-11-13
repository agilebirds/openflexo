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
package org.openflexo.oo3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3Attachments implements XMLSerializable {
	public Vector<OO3Attachment> attachments;

	public OO3Attachments() {
		super();
		attachments = new Vector<OO3Attachment>();
	}

	public OO3Attachment registerAttachment(File file, String name) {
		OO3Attachment newOO3Attachment = new OO3Attachment(file, name);
		attachments.add(newOO3Attachment);
		return newOO3Attachment;
	}

	public static class OO3Attachment implements XMLSerializable {
		public String id;

		public String href;

		public File file;

		public String name;

		public OO3Attachment() {
			super();
		}

		public OO3Attachment(File f, String n) {
			this();
			generateItemId();
			this.name = n;
			this.href = f.getName();
			this.file = f;
		}

		private void generateItemId() {
			id = "Attachment" + hashCode();
		}

	}

	protected void saveToDirectory(File directory) {
		for (int i = 0; i < attachments.size(); i++) {
			File curFile = attachments.get(i).file;
			if (curFile.isFile() && (curFile.exists())) {
				try {
					FileInputStream is = new FileInputStream(curFile);
					copyFileToDir(is, curFile.getName(), directory);
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static void copyFileToDir(FileInputStream is, String newFileName, File dest) {
		File newFile = new File(dest, newFileName);
		try {
			newFile.createNewFile();
			FileOutputStream os = new FileOutputStream(newFile);
			while (is.available() > 0) {
				byte[] byteArray = new byte[is.available()];
				is.read(byteArray);
				os.write(byteArray);
			}
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
