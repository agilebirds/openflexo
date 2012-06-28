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
package org.openflexo.icon;

import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMTranstyper;
import org.openflexo.foundation.dm.DMTranstyper.DMTranstyperEntry;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMType.KindOfType;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.eo.DMEOJoin;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of DMModule
 * 
 * @author sylvain
 * 
 */
public class DMEIconLibrary extends IconLibrary {

	static final Logger logger = Logger.getLogger(DMEIconLibrary.class.getPackage().getName());

	// Module icons
	public static final ImageIcon DME_SMALL_ICON = new ImageIconResource("Icons/DME/module-dme-16.png");
	public static final ImageIcon DME_MEDIUM_ICON = new ImageIconResource("Icons/DME/module-dme-32.png");
	public static final ImageIcon DME_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/DME/module-dme-hover-32.png");
	public static final ImageIcon DME_BIG_ICON = new ImageIconResource("Icons/DME/module-dme-hover-64.png");

	// Perspective icons
	public static final ImageIcon DME_RP_ACTIVE_ICON = new ImageIconResource("Icons/DME/RepositoryPerspective_A.gif");
	public static final ImageIcon DME_RP_SELECTED_ICON = new ImageIconResource("Icons/DME/RepositoryPerspective_S.gif");
	public static final ImageIcon DME_PP_ACTIVE_ICON = new ImageIconResource("Icons/DME/PackagePerspective_A.gif");
	public static final ImageIcon DME_PP_SELECTED_ICON = new ImageIconResource("Icons/DME/PackagePerspective_S.gif");
	public static final ImageIcon DME_HP_ACTIVE_ICON = new ImageIconResource("Icons/DME/HierarchyPerspective_A.gif");
	public static final ImageIcon DME_HP_SELECTED_ICON = new ImageIconResource("Icons/DME/HierarchyPerspective_S.gif");
	public static final ImageIcon DME_DP_ACTIVE_ICON = new ImageIconResource("Icons/DME/DiagramPerspective_A.gif");
	public static final ImageIcon DME_DP_SELECTED_ICON = new ImageIconResource("Icons/DME/DiagramPerspective_S.gif");

	// Editor icons
	public static final ImageIcon CONNECTED_ICON = new ImageIconResource("Icons/DME/Utils/Connected.gif");
	public static final ImageIcon DISCONNECTED_ICON = new ImageIconResource("Icons/DME/Utils/Disconnected.gif");
	public static final ImageIcon READONLY_ICON = new ImageIconResource("Icons/DME/Utils/ReadOnly.gif");
	public static final ImageIcon MODIFIABLE_ICON = new ImageIconResource("Icons/DME/Utils/Modifiable.gif");
	public static final ImageIcon LOCK_ICON = new ImageIconResource("Icons/DME/Utils/Lock.gif");
	public static final ImageIcon GET_ICON = new ImageIconResource("Icons/DME/Utils/Get.gif");
	public static final ImageIcon GET_SET_ICON = new ImageIconResource("Icons/DME/Utils/GetSet.gif");
	public static final ImageIcon CLASS_PROPERTY_ICON = new ImageIconResource("Icons/DME/Utils/ClassProperty.gif");
	public static final ImageIcon NULL_PROPERTY_ICON = new ImageIconResource("Icons/DME/Utils/null.gif");
	public static final ImageIcon KEY_ICON = new ImageIconResource("Icons/DME/Utils/Key.gif");
	public static final ImageIcon TO_ONE_ICON = new ImageIconResource("Icons/DME/Utils/toOne.gif");
	public static final ImageIcon TO_MANY_ICON = new ImageIconResource("Icons/DME/Utils/toMany.gif");
	public static final ImageIcon BINDABLE_ICON = new ImageIconResource("Icons/DME/Utils/Link.gif");
	public static final ImageIcon NOT_BINDABLE_ICON = new ImageIconResource("Icons/DME/Utils/notLink.gif");

	// Model icons
	public static final ImageIcon DM_MODEL_ICON = new ImageIconResource("Icons/Model/DM/Library_DM.gif");
	public static final ImageIcon DM_FOLDER_ICON = new ImageIconResource("Icons/Model/DM/SmallRepositoryFolder.gif");
	public static final ImageIcon DM_REPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallRepository.gif");
	public static final ImageIcon DM_REPOSITORY_FOLDER_ICON = new ImageIconResource("Icons/Model/DM/Folder_DM.gif");
	public static final ImageIcon DM_EOREPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallEORepository.gif");
	public static final ImageIcon DM_JAR_REPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallExternalRepository.gif");
	public static final ImageIcon DM_HIBERNATEREPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallEORepository.gif");
	public static final ImageIcon DM_PACKAGE_ICON = new ImageIconResource("Icons/Model/DM/Package.gif");
	public static final ImageIcon DM_EOMODEL_ICON = new ImageIconResource("Icons/Model/DM/DMEOModel.gif");
	public static final ImageIcon DM_ENTITY_ICON = new ImageIconResource("Icons/Model/DM/DMEntity.gif");
	public static final ImageIcon DM_ENTITY_CLASS_ICON = new ImageIconResource("Icons/Model/DM/DMEntity-class.gif");
	public static final ImageIcon DM_ENTITY_INTERFACE_ICON = new ImageIconResource("Icons/Model/DM/DMEntity-interface.gif");
	public static final ImageIcon DM_ENTITY_ENUMERATION_ICON = new ImageIconResource("Icons/Model/DM/DMEntity-enumeration.gif");
	public static final ImageIcon DM_EOENTITY_ICON = new ImageIconResource("Icons/Model/DM/SmallDMEOEntity.gif");
	public static final ImageIcon DM_PROPERTY_ICON = new ImageIconResource("Icons/Model/DM/DMProperty.gif");
	public static final ImageIcon DM_METHOD_ICON = new ImageIconResource("Icons/Model/DM/DMMethod.gif");
	public static final ImageIcon DM_EOATTRIBUTE_ICON = new ImageIconResource("Icons/Model/DM/SmallDMEOAttribute.gif");
	public static final ImageIcon DM_EORELATIONSHIP_ICON = new ImageIconResource("Icons/Model/DM/SmallDMEORelationship.gif");
	public static final ImageIcon JDK_REPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallJDKRepository.gif");
	public static final ImageIcon WO_REPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallWORepository.gif");
	public static final ImageIcon COMPONENT_REPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallComponentRepository.gif");
	public static final ImageIcon PROCESS_INSTANCE_REPOSITORY_ICON = new ImageIconResource(
			"Icons/Model/DM/SmallProcessInstanceRepository.gif");
	public static final ImageIcon PROCESS_BUSINESS_DATA_REPOSITORY_ICON = new ImageIconResource(
			"Icons/Model/DM/SmallProcessBusinessDataRepository.gif");
	public static final ImageIcon EXTERNAL_REPOSITORY_ICON = new ImageIconResource("Icons/Model/DM/SmallExternalRepository.gif");
	public static final ImageIcon DM_TRANSTYPER_ICON = new ImageIconResource("Icons/Model/DM/Transtyper.gif");
	public static final ImageIcon DM_TRANSTYPER_ENTRY_ICON = new ImageIconResource("Icons/Model/DM/Transtyper.gif");
	public static final ImageIcon DIAGRAM_ICON = new ImageIconResource("Icons/Model/DM/Diagram.gif");
	public static final ImageIcon EOENTITY_ICON = new ImageIconResource("Icons/Model/DM/EOEntity.png");
	public static final ImageIcon EOMODEL_ICON = new ImageIconResource("Icons/Model/DM/EOModel.png");

	public static ImageIcon iconForObject(DMObject object) {
		if (object instanceof DMModel) {
			return DMEIconLibrary.DM_MODEL_ICON;
		} else if (object instanceof ERDiagram) {
			return DMEIconLibrary.DIAGRAM_ICON;
		} else if (object instanceof DMEntity) {
			DMEntity entity = (DMEntity) object;
			if (entity.getIsNormalClass()) {
				return DMEIconLibrary.DM_ENTITY_CLASS_ICON;
			} else if (entity.getIsInterface()) {
				return DMEIconLibrary.DM_ENTITY_INTERFACE_ICON;
			} else if (entity.getIsEnumeration()) {
				return DMEIconLibrary.DM_ENTITY_ENUMERATION_ICON;
			}
			return null;
		} else if (object instanceof DMEOJoin) {
			return (((DMEOJoin) object).isJoinValid() ? DMEIconLibrary.CONNECTED_ICON : DMEIconLibrary.DISCONNECTED_ICON);
		} else if (object instanceof DMTranstyper) {
			return DMEIconLibrary.DM_TRANSTYPER_ICON;
		} else if (object instanceof DMTranstyperEntry) {
			return DMEIconLibrary.DM_TRANSTYPER_ENTRY_ICON;
		}
		return null;
	}

	public static Icon iconForType(DMType type) {
		if (type.getKindOfType() == KindOfType.UNRESOLVED) {
			return IconLibrary.UNFIXABLE_ERROR_ICON;
		} else if ((type.getKindOfType() == KindOfType.RESOLVED) || (type.getKindOfType() == KindOfType.RESOLVED_ARRAY)) {
			if (type.getBaseEntity().getIsNormalClass()) {
				return DMEIconLibrary.DM_ENTITY_CLASS_ICON;
			} else if (type.getBaseEntity().getIsInterface()) {
				return DMEIconLibrary.DM_ENTITY_INTERFACE_ICON;
			} else if (type.getBaseEntity().getIsEnumeration()) {
				return DMEIconLibrary.DM_ENTITY_ENUMERATION_ICON;
			}
			return null;
		}

		else if (type.getKindOfType() == KindOfType.DKV) {
			return SEIconLibrary.DOMAIN_ICON;
		}

		else if (type.getKindOfType() == KindOfType.TYPE_VARIABLE) {
			return IconLibrary.QUESTION_ICON;
		}

		else if (type.getKindOfType() == KindOfType.WILDCARD) {
			return IconLibrary.QUESTION_ICON;
		}

		else {
			logger.warning("Unexpected KindOfType: " + type.getKindOfType());
			return null;
		}
	}

}
