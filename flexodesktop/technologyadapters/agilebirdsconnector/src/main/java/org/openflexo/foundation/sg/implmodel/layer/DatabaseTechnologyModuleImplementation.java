/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.layer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * This class is intended to be extended by any Database Layer Technology mode implementation instead of extending directly
 * TechnologyModuleImplementation. It allows exposing some convenient methods to be used by other technology module.
 * 
 * @author Nicolas Daniels
 */
public abstract class DatabaseTechnologyModuleImplementation extends TechnologyModuleImplementation {

	public static final String DB_ACCEPTABLE_CHARS = "[_A-Za-z0-9]+";
	public static final Pattern DB_ACCEPTABLE_PATTERN = Pattern.compile(DB_ACCEPTABLE_CHARS);

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public DatabaseTechnologyModuleImplementation(ImplementationModelBuilder builder) throws TechnologyModuleCompatibilityCheckException {
		this(builder.implementationModel);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public DatabaseTechnologyModuleImplementation(ImplementationModel implementationModel)
			throws TechnologyModuleCompatibilityCheckException {
		super(implementationModel);
	}

	/**
	 * Return true if the specified name is a reserved keyword for the represented database.
	 * 
	 * @return true if the specified name is a reserved keyword for the represented database, false otherwise.
	 */
	public abstract boolean getIsReservedKeywordsForDbObject(String name);

	/**
	 * Transform the specified name into a suitable name to be used for any database object. <br>
	 * Invalid characters are escaped and reserved keywords not allowed (in such case, '_' is prepended).
	 * 
	 * @param name
	 * @return the transformed name.
	 */
	public String getDbObjectName(String name) {
		name = escapeDbObjectName(name);

		if (name != null && getIsReservedKeywordsForDbObject(name)) {
			name = '_' + name;
		}

		return name;
	}

	/**
	 * Escape all invalid characters in a name intended to be used in any database context name (DB name, Table name, Column name, ...) from
	 * the specified name. table name. <br>
	 * This doesn't handle reserved keywords, for this functionality use #getDbObjectName from the database module instance.
	 * 
	 * @param name
	 * @return the escaped name.
	 */
	public static String escapeDbObjectName(String name) {
		if (name == null) {
			return null;
		}
		if (name.equals("")) {
			return name;
		}
		name = StringUtils.convertAccents(name);
		StringBuffer sb = new StringBuffer();
		Matcher m = DB_ACCEPTABLE_PATTERN.matcher(name);
		while (m.find()) {
			String group = m.group();
			sb.append(group);
		}
		name = sb.toString();
		if (name.length() == 0) {
			return "_";
		}
		return name;
	}
}
