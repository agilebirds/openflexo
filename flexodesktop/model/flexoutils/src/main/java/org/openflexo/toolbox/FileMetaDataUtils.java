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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * Utilities to encode meta-data on file system
 * 
 * @author sylvain
 */
public class FileMetaDataUtils {

	private static final Logger logger = FlexoLogger.getLogger(FileMetaDataUtils.class.getPackage().getName());

	public static String getProperty(String propertyName, File aFile) {
		return getProperties(aFile).getProperty(propertyName);
	}

	public static void setProperty(String propertyName, String value, File aFile) {
		Properties properties = getProperties(aFile);
		properties.setProperty(propertyName, value);
		try {
			properties.store(new FileOutputStream(getMetaDataFile(aFile)), "MetaData for file " + aFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Properties getProperties(File file) {
		File metaDataFile = getMetaDataFile(file);
		Properties properties = new Properties();
		if (metaDataFile.exists()) {
			try {
				properties.load(new FileReader(metaDataFile));
			} catch (FileNotFoundException e) {
				properties = null;
			} catch (IOException e) {
				properties = null;
			}
		}
		return properties;
	}

	private static File getMetaDataFile(File file) {
		if (file.isDirectory()) {
			return new File(file, ".metadata");
		} else {
			return new File(file.getParentFile(), "." + file.getName() + ".metadata");
		}
	}

	public static void main(String[] args) {
		File f = new File("/Users/sylvain/Temp/TestMetaData");
		System.out.println("coucou=" + getProperty("coucou", f));
		setProperty("coucou", "c'est moi", f);
		System.out.println("coucou=" + getProperty("coucou", f));
		setProperty("coucou2", "c'est encore moi", f);
		System.out.println("coucou2=" + getProperty("coucou2", f));
		File f2 = new File(f, "salut");
		setProperty("hop", "zobi", f2);
		System.out.println("hop=" + getProperty("hop", f2));
		System.out.println("f2.lastModified()=" + f2.lastModified());
		System.out.println("getMetaDataFile(f2).lastModified()=" + getMetaDataFile(f2).lastModified());
	}
}
