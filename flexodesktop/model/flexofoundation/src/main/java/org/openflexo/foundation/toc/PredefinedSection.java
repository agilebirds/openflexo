package org.openflexo.foundation.toc;

import org.openflexo.foundation.DocType.DefaultDocType;
import org.openflexo.foundation.cg.utils.DocConstants;
import org.openflexo.foundation.xml.FlexoTOCBuilder;
import org.openflexo.localization.FlexoLocalization;

public class PredefinedSection extends TOCEntry {

	private static final String PROCESSES_DOC_TEMPLATE = "docx_tocentry_processes.xml.vm";
	private static final String SCREENS_DOC_TEMPLATE = "docx_tocentry_screens.xml.vm";
	private static final String DATA_MODEL_DOC_TEMPLATE = "docx_tocentry_datamodel.xml.vm";
	private static final String DEFINITIONS_DOC_TEMPLATE = "docx_tocentry_definitions.xml.vm";
	private static final String ROLES_DOC_TEMPLATE = "docx_tocentry_roles.xml.vm";
	private static final String RACI_DOC_TEMPLATE = "docx_tocentry_raci_full.xml.vm";
	private static final String VIEWS_DOC_TEMPLATE = "docx_tocentry_views.xml.vm";
	private static final String DIAGRAMS_DOC_TEMPLATE = "docx_tocentry_diagram.xml.vm";

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

			@Override
			public String getDefaultTemplateName() {
				return PROCESSES_DOC_TEMPLATE;
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

			@Override
			public String getDefaultTemplateName() {
				return VIEWS_DOC_TEMPLATE;
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

			@Override
			public String getDefaultTemplateName() {
				return ROLES_DOC_TEMPLATE;
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

			@Override
			public String getDefaultTemplateName() {
				return SCREENS_DOC_TEMPLATE;
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

			@Override
			public String getDefaultTemplateName() {
				return DATA_MODEL_DOC_TEMPLATE;
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

			@Override
			public String getDefaultTemplateName() {
				return DEFINITIONS_DOC_TEMPLATE;
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

			@Override
			public String getDefaultTemplateName() {
				return DIAGRAMS_DOC_TEMPLATE;
			}
		},
		RACI {
			@Override
			public boolean getIsReadOnly() {
				return true;
			}

			@Override
			public String getDefaultTemplateName() {
				return RACI_DOC_TEMPLATE;
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

		public String getDefaultTemplateName() {
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

	@Override
	public PredefinedSectionType getIdentifier() {
		return getType();
	}

	public PredefinedSectionType getType() {
		return type;
	}

	public void setType(PredefinedSectionType type) {
		this.type = type;
	}

	@Override
	public String getDefaultTemplateName() {
		if (getType() != null) {
			return getType().getDefaultTemplateName();
		}
		return null;
	}

}
