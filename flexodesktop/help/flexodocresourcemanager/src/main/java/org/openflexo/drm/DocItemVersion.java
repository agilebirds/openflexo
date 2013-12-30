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
package org.openflexo.drm;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class DocItemVersion extends DRMObject {

	private static final Logger logger = Logger.getLogger(DocItemVersion.class.getPackage().getName());

	private DocItem item;

	private String languageId;

	private Version version;

	private String _shortHTMLDescription;

	private String _fullHTMLDescription;

	private boolean _isLoaded;

	private boolean _needsSaving;

	public DocItemVersion(DRMBuilder builder) {
		this(builder.docResourceCenter);
		initializeDeserialization(builder);
	}

	public DocItemVersion(DocResourceCenter docResourceCenter) {
		super(docResourceCenter);
		_isLoaded = false;
		_needsSaving = false;
	}

	public static DocItemVersion createVersion(DocItem item, Version version, Language language, String fullDescription,
			String shortDescription, DocResourceCenter docResourceCenter) {
		DocItemVersion newVersion = new DocItemVersion(docResourceCenter);
		newVersion.item = item;
		newVersion.languageId = language.getIdentifier();
		newVersion.version = version;
		if (fullDescription != null) {
			newVersion.setFullHTMLDescription(fullDescription);
		}
		if (shortDescription != null) {
			newVersion.setShortHTMLDescription(shortDescription);
		}
		return newVersion;
	}

	public void setFullHTMLDescription(String fullHTMLDescription) {
		if (fullHTMLDescription != getFullHTMLDescription()) {
			_fullHTMLDescription = fullHTMLDescription;
			_needsSaving = true;
		}
	}

	public String getFullHTMLDescription() {
		if (!_isLoaded) {
			load();
		}
		return _fullHTMLDescription;
	}

	public void setShortHTMLDescription(String shortHTMLDescription) {
		if (shortHTMLDescription != getShortHTMLDescription()) {
			_shortHTMLDescription = shortHTMLDescription;
			_needsSaving = true;
		}
	}

	public String getShortHTMLDescription() {
		if (!_isLoaded) {
			load();
		}
		return _shortHTMLDescription;
	}

	private void load() {
		if (!_isLoaded) {
			if (getShortHTMLDescriptionFile() != null) {
				if (getShortHTMLDescriptionFile().exists()) {
					try {
						_shortHTMLDescription = FileUtils.fileContents(getShortHTMLDescriptionFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Could not find: " + getShortHTMLDescriptionFile().getAbsolutePath());
					}
				}
			}
			if (getFullHTMLDescriptionFile() != null) {
				if (getFullHTMLDescriptionFile().exists()) {
					try {
						_fullHTMLDescription = FileUtils.fileContents(getFullHTMLDescriptionFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			_isLoaded = true;
		}
	}

	public void save() {
		if (!getItem().getFolder().getDirectory().exists()) {
			getItem().getFolder().getDirectory().mkdirs();
		}
		if (_shortHTMLDescription != null) {
			try {
				FileUtils.saveToFile(getShortHTMLDescriptionFile(), _shortHTMLDescription);
				logger.info("Save " + getShortHTMLDescriptionFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (_fullHTMLDescription != null) {
			try {
				FileUtils.saveToFile(getFullHTMLDescriptionFile(), _fullHTMLDescription);
				logger.info("Save " + getFullHTMLDescriptionFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		_needsSaving = false;
	}

	public boolean needsSaving() {
		return _needsSaving;
	}

	public void setNeedsSaving() {
		_needsSaving = true;
	}

	public File getFullHTMLDescriptionFile() {
		if (getItem() != null && getItem().getFolder() != null) {
			File directory = getItem().getFolder().getDirectory();
			String fileName = getItem().getIdentifier() + "-0-" + languageId + "-" + version.toString() + "-FULL.html";
			return new File(directory, fileName);
		}
		return null;
	}

	public File getShortHTMLDescriptionFile() {
		if (getItem() != null && getItem().getFolder() != null) {
			File directory = getItem().getFolder().getDirectory();
			String fileName = getItem().getIdentifier() + "-0-" + languageId + "-" + version.toString() + "-SHORT.html";
			return new File(directory, fileName);
		}
		return null;
	}

	public DocItem getItem() {
		return item;
	}

	public void setItem(DocItem item) {
		this.item = item;
	}

	public Language getLanguage() {
		return getDocResourceCenter().getLanguageNamed(getLanguageId());
	}

	public void setLanguage(Language aLanguage) {
		setLanguageId(aLanguage.getIdentifier());
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
		setChanged();
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		if (!isDeserializing()) {
			load();
			_needsSaving = true;
		}
		this.version = version;
		setChanged();
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKeyWithParams("($version)/($languageId)", this);
	}

	public static class Version extends KVCFlexoObject implements StringConvertable<Version> {
		public static StringEncoder.Converter<Version> converter = StringEncoder.getDefaultInstance()._addConverter(
				new Converter<Version>(Version.class) {

					@Override
					public Version convertFromString(String value) {
						return new Version(value);
					}

					@Override
					public String convertToString(Version value) {
						return value.toString();
					}

				});

		public int major = 0;

		public int minor = 0;

		public int patch = 0;

		public Version() {
			super();
			major = 1;
		}

		public Version(String versionAsString) {
			super();
			StringTokenizer st = new StringTokenizer(versionAsString, ".");
			if (st.hasMoreTokens()) {
				major = Integer.valueOf(st.nextToken()).intValue();
			}
			if (st.hasMoreTokens()) {
				minor = Integer.valueOf(st.nextToken()).intValue();
			}
			if (st.hasMoreTokens()) {
				patch = Integer.valueOf(st.nextToken()).intValue();
			}
		}

		public static Version versionByIncrementing(Version aVersion, int majorInc, int minorInc, int patchInc) {
			Version returned = new Version();
			returned.major = aVersion.major + majorInc;
			returned.minor = aVersion.minor + minorInc;
			returned.patch = aVersion.patch + patchInc;
			return returned;
		}

		@Override
		public String toString() {
			return "" + major + "." + minor + "." + patch;
		}

		@Override
		public boolean equals(Object anObject) {
			if (anObject instanceof Version) {
				return toString().equals(anObject.toString());
			} else {
				return super.equals(anObject);
			}
		}

		public boolean isLesserThan(Version version) {
			return new VersionComparator().compare(Version.this, version) < 0;
		}

		public boolean isGreaterThan(Version version) {
			return new VersionComparator().compare(Version.this, version) > 0;
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		@Override
		public StringEncoder.Converter<Version> getConverter() {
			return converter;
		}

		public static VersionComparator comparator = new VersionComparator();

		public static class VersionComparator implements Comparator<Version> {

			VersionComparator() {
				super();
			}

			@Override
			public int compare(Version o1, Version o2) {
				if (o1 instanceof Version && o2 instanceof Version) {
					Version v1 = o1;
					Version v2 = o2;
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
				// don't know what to do with this !!!
				else {
					return 0;
				}
			}

		}

	}

	/**
	 * Overrides getIdentifier
	 * 
	 * @see org.openflexo.drm.DRMObject#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getItem().getIdentifier() + "_" + version.toString() + getLanguageId();
	}

}
