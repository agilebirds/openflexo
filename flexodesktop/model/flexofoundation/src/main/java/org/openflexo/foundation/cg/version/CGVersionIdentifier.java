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
package org.openflexo.foundation.cg.version;

import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.localization.FlexoLocalization;

public class CGVersionIdentifier extends FlexoObject implements StringConvertable, Cloneable
{
	protected static final Logger logger = Logger.getLogger(CGVersionIdentifier.class.getPackage().getName());

	public enum VersionType
	{
		GenerationIteration,
		DiskUpdate,
		Release
	}
	
	public static final Converter<CGVersionIdentifier> converter = new Converter<CGVersionIdentifier>(CGVersionIdentifier.class) {

		@Override
		public CGVersionIdentifier convertFromString(String value)
		{
			try {
				return new CGVersionIdentifier(value);
			} catch (InvalidVersionFormatException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public String convertToString(CGVersionIdentifier value)
		{
			return value.toString();
		}

	};

	public int major = 0;

	public int minor = 0;

	public int patch = 0;

	public VersionType type = null;
	
	public class InvalidVersionFormatException extends Exception
	{
		
	}

	public CGVersionIdentifier(String versionAsString) throws InvalidVersionFormatException
	{
		super();
		StringTokenizer st = new StringTokenizer(versionAsString, ".");
		if (st.hasMoreTokens()) {
			String unparsed = st.nextToken();
			try {
				major = (new Integer(unparsed)).intValue();
			}
			catch (NumberFormatException e) {
				logger.warning("Cannot parse "+unparsed);
				throw new InvalidVersionFormatException();
			}
		}
		if (st.hasMoreTokens()) {
			String unparsed = st.nextToken();
			try {
				minor = (new Integer(unparsed)).intValue();
			}
			catch (NumberFormatException e) {
				logger.warning("Cannot parse "+unparsed);
				throw new InvalidVersionFormatException();
			}
		}
		if (st.hasMoreTokens()) {
			String unparsed = st.nextToken();
			try {
				patch = (new Integer(unparsed)).intValue();
			}
			catch (NumberFormatException e) {
				logger.warning("Cannot parse "+unparsed);
				throw new InvalidVersionFormatException();
			}
		}
		if (st.hasMoreTokens()) {
			String unparsed = st.nextToken();
			if (unparsed.equalsIgnoreCase(DISK_UPDATE_STRING)) type = VersionType.DiskUpdate;
			if (unparsed.equalsIgnoreCase(GENERATION_ITERATION_STRING)) type = VersionType.GenerationIteration;
		}
	}

	public static CGVersionIdentifier DEFAULT_VERSION_ID() 
	{
		try {
			return new CGVersionIdentifier("0.0");
		} catch (InvalidVersionFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	@Override
	public String toString()
	{
		return "" + major + "." + minor + "." + patch + (type!=null?"."+typeId():"");
	}

	public String versionAsString()
	{
		return "" + major + "." + minor + "." + patch;
	}

	private String typeId()
	{
		if (type == VersionType.DiskUpdate) return DISK_UPDATE_STRING;
		if (type == VersionType.GenerationIteration) return GENERATION_ITERATION_STRING;
		if (type == VersionType.Release) return RELEASE_STRING;
		return null;
	}
	
	public String typeAsString()
	{
		if (type == VersionType.DiskUpdate) return FlexoLocalization.localizedForKey("disk_edition");
		if (type == VersionType.GenerationIteration) return FlexoLocalization.localizedForKey("generation_iteration");
		if (type == VersionType.Release) return FlexoLocalization.localizedForKey("release");
		return null;
	}
	
	private static final String DISK_UPDATE_STRING = "DU";
	private static final String GENERATION_ITERATION_STRING = "GI";
	private static final String RELEASE_STRING = "RL";
	
	// Take care here that kind (type) of version is ignored when testing equality
	// (used in Hashtable to retrieve versions)
	@Override
	public int hashCode()
	{
		return versionAsString().hashCode();
	}

	// Take care here that kind (type) of version is ignored when testing equality
	// (used in Hashtable to retrieve versions)
	@Override
	public boolean equals(Object anObject)
	{
		if (anObject instanceof CGVersionIdentifier) {
			return versionAsString().equals(((CGVersionIdentifier)anObject).versionAsString());
		} else {
			return super.equals(anObject);
		}
	}

	@Override
	public CGVersionIdentifier clone()
	{
		try {
			return new CGVersionIdentifier(toString());
		} catch (InvalidVersionFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isLesserThan(CGVersionIdentifier version)
	{
		return (COMPARATOR.compare(this, version) < 0);
	}

	public boolean isGreaterThan(CGVersionIdentifier version)
	{
		return (COMPARATOR.compare(this, version) > 0);
	}

	@Override
	public StringEncoder.Converter getConverter()
	{
		return converter;
	}

	public static final VersionComparator COMPARATOR = new VersionComparator();

	public static class VersionComparator implements Comparator<CGVersionIdentifier>
	{

		VersionComparator()
		{
			super();
		}

		@Override
		public int compare(CGVersionIdentifier v1, CGVersionIdentifier v2)
		{
			if (v1.major < v2.major) {
				return -1;
			} else if (v1.major > v2.major) {
				return 1;
			} else {
				if (v1.minor < v2.minor) {
					return -1;
				} else if (v1.minor > v2.minor) {
					return 1;
				} else {
					if (v1.patch < v2.patch) {
						return -1;
					} else if (v1.patch > v2.patch) {
						return 1;
					} else {
						// equals object !!!
						return 0;
					}
				}
			}
		}

	}

	public CGVersionIdentifier newVersionByIncrementingMajor()
	{
		CGVersionIdentifier returned = clone();
		returned.major++;
		returned.minor=0;
		returned.patch=0;
		return returned;
	}

	public CGVersionIdentifier newVersionByIncrementingMinor()
	{
		CGVersionIdentifier returned = clone();
		returned.minor++;
		returned.patch=0;
		return returned;
	}
}

