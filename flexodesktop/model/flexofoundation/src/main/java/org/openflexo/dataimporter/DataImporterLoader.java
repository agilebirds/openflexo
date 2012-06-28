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
package org.openflexo.dataimporter;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DataImporterLoader {

	private static final Logger logger = Logger.getLogger(DataImporterLoader.class.getPackage().getName());

	/*public static final String RATIONAL_ROSE_IMPORTER = "org.openflexo.rationalrose.Importer";
	public static final String WSDL_IMPORTER = "org.openflexo.wsdl.Importer";*/

	public enum KnownDataImporter {
		WSDL_IMPORTER {
			@Override
			public String getClassName() {
				return "org.openflexo.wsdl.Importer";
			}
		},
		RATIONAL_ROSE_IMPORTER {
			@Override
			public String getClassName() {
				return "org.openflexo.rationalrose.Importer";
			}
		};
		public abstract String getClassName();

		public boolean isAvailable() {
			return DataImporterLoader.getDataImporter(getClassName()) != null;
		}

		public DataImporter getImporter() {
			return DataImporterLoader.getDataImporter(getClassName());
		}
	}

	private static final Hashtable<String, DataImporter> _dataImporter = new Hashtable<String, DataImporter>();

	/**
	 * Return supplied data importer, or null if such a data importer was not found
	 * 
	 * @param aDataImporter
	 * @return
	 */
	public static DataImporter getDataImporter(String aDataImporter) {
		return getAllDataImporter().get(aDataImporter);
	}

	/**
	 * Return all DataImporter instances
	 * 
	 * @return a Hashtable of DataImporter instances
	 */
	public static Hashtable<String, DataImporter> getAllDataImporter() {
		if (_dataImporter.size() == 0) {
			loadAllDataImporter();
		}
		return _dataImporter;
	}

	/**
	 * Internnaly used to load data importers
	 */
	private static void loadAllDataImporter() {

		for (int i = 0; i < KnownDataImporter.values().length; i++) {
			KnownDataImporter candidate = KnownDataImporter.values()[i];
			try {
				Class<?> importer = Class.forName(candidate.getClassName());
				Object dataImporter = importer.newInstance();
				if (dataImporter instanceof DataImporter) {
					_dataImporter.put(candidate.getClassName(), (DataImporter) dataImporter);
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Data importer :" + candidate
								+ " cannot be loaded because it doesn't implements org.openflexo.dataimporter.DataImporter.");
					}
				}
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING) && !candidate.equals(KnownDataImporter.RATIONAL_ROSE_IMPORTER)) {
					logger.warning("Data importer :" + candidate + " cannot be loaded.\n\t\t" + e.getMessage());
					if (logger.isLoggable(Level.FINE)) {
						e.printStackTrace();
					}
				}
			} catch (Error e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("An error has occured while trying to load " + candidate.getClassName());
				}
				e.printStackTrace();
			}
		}
	}

}
