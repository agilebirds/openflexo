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
package org.openflexo.foundation.dm.eo;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cayenne.wocompat.PropertyListSerialization;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents the implementation type of a property
 * 
 * @author sguerin
 * 
 */
public abstract class DMEOAdaptorType implements StringConvertable, ChoiceList {

	private static final Logger logger = Logger.getLogger(DMEOAdaptorType.class.getPackage().getName());

	public static final DMEOAdaptorType JDBC = new JDBCAdaptorType();

	public static final DMEOAdaptorType LDAP = new LDAPAdaptorType();

	public static final DMEOAdaptorType JNDI = new JNDIAdaptorType();

	public static final StringEncoder.Converter<DMEOAdaptorType> adaptorTypeConverter = new Converter<DMEOAdaptorType>(
			DMEOAdaptorType.class) {

		@Override
		public DMEOAdaptorType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(DMEOAdaptorType value) {
			return value.getName();
		}

	};

	private static class JDBCAdaptorType extends DMEOAdaptorType {

		public JDBCAdaptorType() {
			super();
		}

		@Override
		public String getName() {
			return "JDBC";
		}

		@Override
		public FileResource getAdaptorInfoFile() {
			return new FileResource("Config/AdaptorInfo/JavaJDBCAdaptorInfo.plist");
		}

		@Override
		@SuppressWarnings("unchecked")
		public Map<String, Object> getCustomAdaptorInfo() {
			return (Map<String, Object>) getAdaptorInfo().get("OpenBaseJDBC");
		}

		@Override
		public String getCustomAdaptorInfoKey() {
			return "jdbc2Info";
		}
	}

	private static class LDAPAdaptorType extends DMEOAdaptorType {
		public LDAPAdaptorType() {
			super();
		}

		@Override
		public String getName() {
			return "LDAP";
		}

		@Override
		public FileResource getAdaptorInfoFile() {
			return null;
		}

		@Override
		public Map<String, Object> getCustomAdaptorInfo() {
			return null;
		}

		@Override
		public String getCustomAdaptorInfoKey() {
			return null;
		}
	}

	private static class JNDIAdaptorType extends DMEOAdaptorType {

		public JNDIAdaptorType() {
			super();
		}

		@Override
		public String getName() {
			return "JNDI";
		}

		@Override
		public FileResource getAdaptorInfoFile() {
			return new FileResource("Config/AdaptorInfo/JavaJNDIAdaptorInfo.plist");
		}

		@Override
		public Map<String, Object> getCustomAdaptorInfo() {
			return null;
		}

		@Override
		public String getCustomAdaptorInfoKey() {
			return null;
		}
	}

	public abstract String getName();

	public abstract FileResource getAdaptorInfoFile();

	@SuppressWarnings("unchecked")
	public Map<String, Object> getAdaptorInfo() {
		if (getAdaptorInfoFile() != null) {
			try {
				return (Map<String, Object>) PropertyListSerialization.propertyListFromFile(getAdaptorInfoFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public abstract Map<String, Object> getCustomAdaptorInfo();

	public abstract String getCustomAdaptorInfoKey();

	public static final String USERNAME = "username";

	public static final String PASSWORD = "password";

	public static final String DATABASE_SERVER = "URL";

	public static final String PLUGIN = "plugin";

	public static final String DRIVER = "driver";

	public Map<String, Object> getDefaultConnectionDictionary(String userName, String passwd, String databaseServer, String plugin,
			String driver) {
		Map<String, Object> returned = new HashMap<String, Object>();
		if (userName != null) {
			returned.put(USERNAME, userName);
		}
		if (passwd != null) {
			returned.put(PASSWORD, passwd);
		}
		if (databaseServer != null) {
			returned.put(DATABASE_SERVER, databaseServer);
		}
		if (plugin != null) {
			returned.put(PLUGIN, plugin);
		}
		if (driver != null) {
			returned.put(DRIVER, driver);
		}
		if (getCustomAdaptorInfo() != null) {
			returned.put(getCustomAdaptorInfoKey(), getCustomAdaptorInfo());
		}
		return returned;
	}

	public static DMEOAdaptorType get(String adaptorName) {
		if (adaptorName == null || adaptorName.equals("None")) {
			return null;
		}
		for (Enumeration e = JDBC.getAvailableValues().elements(); e.hasMoreElements();) {
			DMEOAdaptorType temp = (DMEOAdaptorType) e.nextElement();
			if (temp.getName().equals(adaptorName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find AdaptorType named " + adaptorName);
		}
		return null;
	}

	private Vector<DMEOAdaptorType> _availableValues = null;

	@Override
	public Vector getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<DMEOAdaptorType>();
			_availableValues.add(JDBC);
			_availableValues.add(LDAP);
			_availableValues.add(JNDI);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return adaptorTypeConverter;
	}

	public static Vector availableValues() {
		return JDBC.getAvailableValues();
	}

}
