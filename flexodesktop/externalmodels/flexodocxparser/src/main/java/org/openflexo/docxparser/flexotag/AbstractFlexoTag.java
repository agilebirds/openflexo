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
package org.openflexo.docxparser.flexotag;

public abstract class AbstractFlexoTag {
	// Flexo Tag must be of the form "__TAG_[FLEXOID]_[USERID]_[OPTION]", only [OPTION] is optional

	private String flexoId;
	private String userId;
	private String optionalInfo;

	@SuppressWarnings("serial")
	public static class FlexoTagFormatException extends Exception {
		public FlexoTagFormatException(String msg) {
			super(msg);
		}
	}

	protected AbstractFlexoTag(String tagValue) throws FlexoTagFormatException {
		this.parse(tagValue);
	}

	protected static String buildFlexoTag(String tag, String flexoId, String userId, String optionalInfo) {
		return tag + flexoId + "_" + userId + (optionalInfo != null && optionalInfo.length() > 0 ? "_" + optionalInfo : "");
	}

	abstract protected String getTag();

	private void parse(String tagValue) throws FlexoTagFormatException {
		if (tagValue == null || !tagValue.startsWith(getTag())) {
			throw new FlexoTagFormatException("Tag value '" + tagValue + "' is either null or doesn't start with '" + getTag() + "'");
		}

		int indexOfStartFlexoId = getTag().length();
		if (tagValue.length() <= indexOfStartFlexoId) {
			throw new FlexoTagFormatException("Tag value '" + tagValue + "' doesn't contain anything after '" + getTag() + "'");
		}

		int indexOfEndFlexoId = tagValue.indexOf('_', indexOfStartFlexoId);
		if (indexOfEndFlexoId == -1 || indexOfEndFlexoId == indexOfStartFlexoId) {
			throw new FlexoTagFormatException("Tag value '" + tagValue
					+ "' doesn't contain a Flexo Id (or the flexo id is not followed by '_')");
		}

		this.flexoId = tagValue.substring(indexOfStartFlexoId, indexOfEndFlexoId);

		int indexOfStartUserId = indexOfEndFlexoId + 1;
		if (tagValue.length() <= indexOfStartUserId) {
			throw new FlexoTagFormatException("Tag value '" + tagValue + "' doesn't contain an User id");
		}

		int indexOfEndUserId = tagValue.indexOf('_', indexOfStartUserId);
		if (indexOfEndUserId == -1) {
			this.userId = tagValue.substring(indexOfStartUserId);
		} else if (indexOfEndUserId == indexOfStartUserId) {
			throw new FlexoTagFormatException("Tag value '" + tagValue + "' doesn't contain an User id");
		} else {
			this.userId = tagValue.substring(indexOfStartUserId, indexOfEndUserId);

			if (tagValue.length() > indexOfEndUserId + 1) {
				this.optionalInfo = tagValue.substring(indexOfEndUserId + 1);
			}
		}
	}

	public String getFlexoId() {
		return flexoId;
	}

	public String getUserId() {
		return userId;
	}

	protected String getOptionalInfo() {
		return optionalInfo;
	}
}
