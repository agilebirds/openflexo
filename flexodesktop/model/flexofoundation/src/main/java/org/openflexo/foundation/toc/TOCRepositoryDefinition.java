package org.openflexo.foundation.toc;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class TOCRepositoryDefinition implements StringConvertable<TOCRepositoryDefinition> {

	public static final Converter<TOCRepositoryDefinition> converter = new Converter<TOCRepositoryDefinition>(TOCRepositoryDefinition.class) {

		@Override
		public TOCRepositoryDefinition convertFromString(String value) {
			int index0 = value.indexOf('#');
			int index1 = -1;
			if (index0 > -1) {
				index1 = value.indexOf('#', index0 + 1);
			}
			if (index0 > -1 && index1 > -1) {
				return new TOCRepositoryDefinition(value.substring(0, index0), value.substring(index0 + 1, index1),
						value.substring(index1 + 1));
			}
			return null;
		}

		@Override
		public String convertToString(TOCRepositoryDefinition value) {
			if (value == null) {
				return null;
			} else {
				return value.getSerializationRepresentation();
			}
		}

	};

	private String flexoID;
	private String userID;
	private String title;

	public TOCRepositoryDefinition(String flexoID, String userID, String title) {
		super();
		this.flexoID = flexoID;
		this.userID = userID;
		this.title = title;
	}

	private String getSerializationRepresentation() {
		return flexoID + "#" + userID + "#" + title;
	}

	public String getFlexoID() {
		return flexoID;
	}

	public void setFlexoID(String flexoID) {
		this.flexoID = flexoID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (flexoID == null ? 0 : flexoID.hashCode());
		result = prime * result + (title == null ? 0 : title.hashCode());
		result = prime * result + (userID == null ? 0 : userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TOCRepositoryDefinition other = (TOCRepositoryDefinition) obj;
		if (flexoID == null) {
			if (other.flexoID != null) {
				return false;
			}
		} else if (!flexoID.equals(other.flexoID)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (userID == null) {
			if (other.userID != null) {
				return false;
			}
		} else if (!userID.equals(other.userID)) {
			return false;
		}
		return true;
	}

	@Override
	public Converter<? extends TOCRepositoryDefinition> getConverter() {
		return converter;
	}

}
