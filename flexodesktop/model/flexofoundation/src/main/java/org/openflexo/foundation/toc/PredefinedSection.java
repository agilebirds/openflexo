package org.openflexo.foundation.toc;

import org.openflexo.foundation.DocType.DefaultDocType;
import org.openflexo.foundation.cg.utils.DocConstants;
import org.openflexo.foundation.xml.FlexoTOCBuilder;
import org.openflexo.localization.FlexoLocalization;

public class PredefinedSection extends TOCEntry {

	public static enum PredefinedSectionType {
		PROCESSES {
			@Override
			public int getPreferredLevel() {
				return 1;
			}

			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getTitle() {
				return "Processes";
			}
		},
		VIEWS {
			@Override
			public int getPreferredLevel() {
				return 1;
			}

			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getTitle() {
				return "Views";
			}
		},
		ROLES {

			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getTitle() {
				return "Roles";
			}
		},
		PURPOSE {
			@Override
			public String getTitle() {
				return "Purpose";
			}

			@Override
			public String getDefaultContent(String docTypeName) {
				if (DefaultDocType.Business.name().equals(docTypeName)) {
					return DocConstants.PURPOSE_BUSINESS_CONTENT;
				} else if (DefaultDocType.Technical.name().equals(docTypeName)) {
					return DocConstants.PURPOSE_TECHNICAL_CONTENT;
				} else {
					return DocConstants.PURPOSE_DEFAULT_CONTENT;
				}
			}
		},
		OBJECTIVES {
			@Override
			public String getTitle() {
				return "Objectives";
			}
		},
		SCOPE {
			@Override
			public String getTitle() {
				return "Scope";
			}
		},
		STAKEHOLDERS {
			@Override
			public String getTitle() {
				return "Stakeholders";
			}
		},
		SCREENS {
			@Override
			public int getPreferredLevel() {
				return 1;
			}

			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getTitle() {
				return "Screens";
			}
		},
		DATA_MODEL {
			@Override
			public int getPreferredLevel() {
				return 1;
			}

			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getTitle() {
				return "Datamodel";
			}
		},
		DEFINITIONS {
			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getTitle() {
				return "Definitions";
			}
		},
		READERS_GUIDE {
			@Override
			public int getPreferredLevel() {
				return 2;
			}

			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getTitle() {
				return "Reader\'s guide";
			}
		},
		NOTES_QUESTIONS {
			@Override
			public String getTitle() {
				return "Notes and questions";
			}
		},
		PROJECT_CONTEXT {
			@Override
			public String getTitle() {
				return "Project context";
			}
		},
		SIPOC_LEVEL2 {
			@Override
			public boolean getIsReadOnly() {
				return true;
			}
		},
		SIPOC_LEVEL3 {
			@Override
			public boolean getIsReadOnly() {
				return true;
			}
		},
		ER_DIAGRAM {
			@Override
			public boolean getIsReadOnly() {
				return true;
			}
		},
		RACI {
			@Override
			public boolean getIsReadOnly() {
				return true;
			}
		};

		public int getPreferredLevel() {
			return -1;
		}

		public String getLocalizedName() {
			return FlexoLocalization.localizedForKey(name().toLowerCase());
		}

		public boolean getIsReadOnly() {
			return false;
		}

		public String getTitle() {
			return "Title";
		}

		public String getDefaultContent(String docTypeName) {
			return null;
		}
	}

	private PredefinedSectionType type;

	public PredefinedSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public PredefinedSection(TOCData data) {
		super(data);
	}

	public PredefinedSectionType getType() {
		return type;
	}

	public void setType(PredefinedSectionType type) {
		this.type = type;
	}

}
