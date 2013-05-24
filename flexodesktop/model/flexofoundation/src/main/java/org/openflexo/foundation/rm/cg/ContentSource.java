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
package org.openflexo.foundation.rm.cg;

import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class ContentSource {

	private ContentSourceType type;
	private CGVersionIdentifier version;

	public static final ContentSource PURE_GENERATION = new ContentSource(ContentSourceType.PureGeneration);
	public static final ContentSource GENERATED_MERGE = new ContentSource(ContentSourceType.GeneratedMerge);
	public static final ContentSource RESULT_FILE_MERGE = new ContentSource(ContentSourceType.ResultFileMerge);
	public static final ContentSource CONTENT_ON_DISK = new ContentSource(ContentSourceType.ContentOnDisk);
	public static final ContentSource LAST_GENERATED = new ContentSource(ContentSourceType.LastGenerated);
	public static final ContentSource LAST_ACCEPTED = new ContentSource(ContentSourceType.LastAccepted);

	public static ContentSource getContentSource(ContentSourceType type) {
		if (type == ContentSourceType.PureGeneration) {
			return PURE_GENERATION;
		}
		if (type == ContentSourceType.GeneratedMerge) {
			return GENERATED_MERGE;
		}
		if (type == ContentSourceType.ResultFileMerge) {
			return RESULT_FILE_MERGE;
		}
		if (type == ContentSourceType.ContentOnDisk) {
			return CONTENT_ON_DISK;
		}
		if (type == ContentSourceType.LastGenerated) {
			return LAST_GENERATED;
		}
		if (type == ContentSourceType.LastAccepted) {
			return LAST_ACCEPTED;
		}
		return new ContentSource(type);
	}

	public static ContentSource getContentSource(ContentSourceType type, CGVersionIdentifier version) {
		if (type == ContentSourceType.PureGeneration) {
			return PURE_GENERATION;
		}
		if (type == ContentSourceType.GeneratedMerge) {
			return GENERATED_MERGE;
		}
		if (type == ContentSourceType.ResultFileMerge) {
			return RESULT_FILE_MERGE;
		}
		if (type == ContentSourceType.ContentOnDisk) {
			return CONTENT_ON_DISK;
		}
		if (type == ContentSourceType.LastGenerated) {
			return LAST_GENERATED;
		}
		if (type == ContentSourceType.LastAccepted) {
			return LAST_ACCEPTED;
		}
		return new ContentSource(type, version);
	}

	private ContentSource(ContentSourceType type) {
		super();
		this.type = type;
	}

	private ContentSource(ContentSourceType type, CGVersionIdentifier version) {
		super();
		this.type = type;
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ContentSource) {
			ContentSource cs = (ContentSource) obj;
			return type == cs.type && (version == cs.version || version.equals(cs.version));
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public String getStringRepresentation() {
		if (type == ContentSourceType.HistoryVersion) {
			return FlexoLocalization.localizedForKey(type.getUnlocalizedStringRepresentation()) + " "
					+ (version != null ? version.versionAsString() : "???");
		}
		return FlexoLocalization.localizedForKey(type.getUnlocalizedStringRepresentation());
	}

	public enum ContentSourceType implements StringConvertable {
		PureGeneration, GeneratedMerge, ResultFileMerge, ContentOnDisk, LastGenerated, LastAccepted, HistoryVersion;

		public String getUnlocalizedStringRepresentation() {
			if (this == PureGeneration) {
				return "pure_generation";
			} else if (this == GeneratedMerge) {
				return "generated_merge";
			} else if (this == ResultFileMerge) {
				return "result_file_merge";
			} else if (this == ContentOnDisk) {
				return "content_on_disk";
			} else if (this == LastGenerated) {
				return "last_generated_version";
			} else if (this == LastAccepted) {
				return "last_accepted_version";
			} else if (this == HistoryVersion) {
				return "history_version";
			}
			return "???";
		}

		public String getStringRepresentation() {
			return FlexoLocalization.localizedForKey(getUnlocalizedStringRepresentation());
		}

		@Override
		public StringEncoder.Converter getConverter() {
			return contentSourceConverter;
		}

		public static final StringEncoder.Converter contentSourceConverter = new Converter<ContentSourceType>(ContentSourceType.class) {

			@Override
			public ContentSourceType convertFromString(String value) {
				for (ContentSourceType cs : values()) {
					if (cs.getStringRepresentation().equals(value)) {
						return cs;
					}
				}
				return null;
			}

			@Override
			public String convertToString(ContentSourceType value) {
				return value.getStringRepresentation();
			}

		};

	}

	public ContentSourceType getType() {
		return type;
	}

	public CGVersionIdentifier getVersion() {
		return version;
	}

	public void setVersion(CGVersionIdentifier version) {
		this.version = version;
	}

}
