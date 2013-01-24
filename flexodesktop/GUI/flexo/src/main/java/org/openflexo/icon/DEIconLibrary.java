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

import javax.swing.ImageIcon;

import org.openflexo.foundation.toc.ConditionalSection;
import org.openflexo.foundation.toc.ERDiagramSection;
import org.openflexo.foundation.toc.EntitySection;
import org.openflexo.foundation.toc.IterationSection;
import org.openflexo.foundation.toc.NormalSection;
import org.openflexo.foundation.toc.OperationScreenSection;
import org.openflexo.foundation.toc.PredefinedSection;
import org.openflexo.foundation.toc.ProcessSection;
import org.openflexo.foundation.toc.RoleSection;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.toc.ViewFolderSection;
import org.openflexo.foundation.toc.ViewSection;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of DEModule
 * 
 * @author sylvain
 * 
 */
public class DEIconLibrary extends IconLibrary {

	private static final Logger logger = Logger.getLogger(DEIconLibrary.class.getPackage().getName());

	// Module icons
	public static final ImageIcon DE_SMALL_ICON = new ImageIconResource("Icons/DG/module-dg-16.png");
	public static final ImageIcon DE_MEDIUM_ICON = new ImageIconResource("Icons/DG/module-dg-32.png");
	public static final ImageIcon DE_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/DG/module-dg-hover-32.png");
	public static final ImageIcon DE_BIG_ICON = new ImageIconResource("Icons/DG/module-dg-hover-64.png");

	// Perspective icons
	public static final ImageIcon DE_TOC_ACTIVE_ICON = new ImageIconResource("Icons/GUI/ListPerspective_A.png");

	// Model icons

	public static final ImageIcon TOC_ENTRY_ICON = new ImageIconResource("Icons/DE/TocEntry.gif");
	public static final ImageIcon TOC_REPOSITORY_ICON = new ImageIconResource("Icons/DE/TOCRepository.png");
	public static final ImageIcon TOC_ENTRY_BIG = new ImageIconResource("Icons/DE/TocEntryBig.png");

	public static final IconMarker PROCESS_SECTION_ICON_MARKER = new IconMarker(WKFIconLibrary.PROCESS_ICON, 5, 5);
	public static final IconMarker VIEW_SECTION_ICON_MARKER = new IconMarker(VEIconLibrary.VIEW_ICON, 5, 5);
	public static final IconMarker VIEW_FOLDER_SECTION_ICON_MARKER = new IconMarker(VEIconLibrary.FOLDER_ICON, 5, 5);
	public static final IconMarker ROLE_SECTION_ICON_MARKER = new IconMarker(WKFIconLibrary.ROLE_ICON, 5, 5);
	public static final IconMarker ENTITY_SECTION_ICON_MARKER = new IconMarker(DMEIconLibrary.DM_ENTITY_ICON, 5, 5);
	public static final IconMarker ER_DIAGRAM_SECTION_ICON_MARKER = new IconMarker(DMEIconLibrary.DIAGRAM_ICON, 5, 5);
	public static final IconMarker OPERATION_COMPONENT_SECTION_ICON_MARKER = new IconMarker(SEIconLibrary.OPERATION_COMPONENT_ICON, 5, 5);

	public static final IconMarker PREDEFINED_SECTION_ICON_MARKER = new IconMarker(VEIconLibrary.VIEW_LIBRARY_ICON, 5, 5);

	public static final ImageIcon PROCESS_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, PROCESS_SECTION_ICON_MARKER);
	public static final ImageIcon VIEW_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, VIEW_SECTION_ICON_MARKER);
	public static final ImageIcon VIEW_FOLDER_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, VIEW_FOLDER_SECTION_ICON_MARKER);
	public static final ImageIcon ROLE_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, ROLE_SECTION_ICON_MARKER);
	public static final ImageIcon ENTITY_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, ENTITY_SECTION_ICON_MARKER);
	public static final ImageIcon ER_DIAGRAM_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, ER_DIAGRAM_SECTION_ICON_MARKER);
	public static final ImageIcon OPERATION_COMPONENT_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON,
			OPERATION_COMPONENT_SECTION_ICON_MARKER);

	public static final ImageIcon PREDEFINED_SECTION_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, PREDEFINED_SECTION_ICON_MARKER);

	public static final ImageIcon DEPRECATED_TOC_ENTRY_ICON = IconFactory.getImageIcon(TOC_ENTRY_ICON, IconLibrary.WARNING);

	public static ImageIcon iconForObject(TOCObject object) {
		if (object instanceof TOCData) {
			return DGIconLibrary.GENERATED_DOC_ICON;
		} else if (object instanceof TOCRepository) {
			return TOC_REPOSITORY_ICON;
		} else if (object instanceof NormalSection) {
			return TOC_ENTRY_ICON;
		} else if (object instanceof PredefinedSection) {
			return PREDEFINED_SECTION_ICON;
		} else if (object instanceof ProcessSection) {
			return PROCESS_SECTION_ICON;
		} else if (object instanceof ViewSection) {
			return VIEW_SECTION_ICON;
		} else if (object instanceof ViewFolderSection) {
			return VIEW_FOLDER_SECTION_ICON;
		} else if (object instanceof RoleSection) {
			return ROLE_SECTION_ICON;
		} else if (object instanceof EntitySection) {
			return ENTITY_SECTION_ICON;
		} else if (object instanceof ERDiagramSection) {
			return ER_DIAGRAM_SECTION_ICON;
		} else if (object instanceof OperationScreenSection) {
			return OPERATION_COMPONENT_SECTION_ICON;
		} else if (object instanceof ConditionalSection) {
			return SEIconLibrary.CONDITIONAL_ICON;
		} else if (object instanceof IterationSection) {
			return SEIconLibrary.REPETITION_ICON;
		} else if (object instanceof TOCEntry) {
			return DEPRECATED_TOC_ENTRY_ICON;
		}

		logger.warning("iconForObject(TOCObject) not implemented yet");
		return null;
	}
}
