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

import org.openflexo.foundation.cg.templates.ApplicationDGTemplateRepository;
import org.openflexo.foundation.cg.templates.CGDocTemplates;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFolder;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.CommonCGTemplateSet;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.templates.TargetSpecificCGTemplateSet;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of DGModule
 * 
 * @author sylvain
 * 
 */
public class DGIconLibrary extends GeneratorIconLibrary {

	static final Logger logger = Logger.getLogger(DGIconLibrary.class.getPackage().getName());

	// Module icons
	public static final ImageIcon DG_SMALL_ICON = new ImageIconResource("Icons/DG/module-dg-16.png");
	public static final ImageIcon DG_MEDIUM_ICON = new ImageIconResource("Icons/DG/module-dg-32.png");
	public static final ImageIcon DG_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/DG/module-dg-hover-32.png");
	public static final ImageIcon DG_BIG_ICON = new ImageIconResource("Icons/DG/module-dg-hover-64.png");

	// Perspective icons
	public static final ImageIcon DG_TEMPLATES_ACTIVE_ICON = new ImageIconResource("Icons/DG/TemplatesPerspective_A.png");
	public static final ImageIcon DG_VP_ACTIVE_ICON = new ImageIconResource("Icons/DG/VersionningPerspective_A.png");
	public static final ImageIcon DG_DGP_ACTIVE_ICON = new ImageIconResource("Icons/DG/DGPerspective_A.png");

	public static final ImageIcon GENERATED_DOC_ICON = new ImageIconResource("Icons/DG/GeneratedDoc.gif");
	public static final ImageIcon GENERATE_PDF = new ImageIconResource("Icons/DG/GeneratePDF.gif");
	public static final ImageIcon GENERATE_DOCX = new ImageIconResource("Icons/DG/GenerateDOCX.gif");
	public static final ImageIcon REINJECT_DOCX = new ImageIconResource("Icons/DG/ReinjectDOCX.gif");
	public static final ImageIcon GENERATE_ZIP = new ImageIconResource("Icons/DG/GenerateZIP.gif");

	public static final ImageIcon GENERATE_DOC_BUTTON = new ImageIconResource("Icons/DG/GenerateDocButton.gif");
	public static final ImageIcon GENERATE_DOC_AND_WRITE_BUTTON = new ImageIconResource("Icons/DG/GenerateDocAndWriteButton.gif");

	public static final ImageIcon SYMBOLIC_FOLDER_ICON = new ImageIconResource("Icons/DG/prj_obj.gif");
	public static final ImageIcon TARGET_FOLDER_ICON = new ImageIconResource("Icons/DG/TargetFolder.gif");

	public static ImageIcon iconForObject(CGTemplateObject object) {
		if (object instanceof CGDocTemplates) {
			return GENERATED_DOC_ICON;
		} else if (object instanceof ApplicationDGTemplateRepository) {
			return FOLDER_ICON;
		} else if (object instanceof CustomCGTemplateRepository) {
			return FOLDER_ICON;
		} else if (object instanceof CommonCGTemplateSet) {
			return FOLDER_ICON;
		} else if (object instanceof TargetSpecificCGTemplateSet) {
			return TARGET_FOLDER_ICON;
		} else if (object instanceof CGTemplateFolder) {
			return FOLDER_ICON;
		} else if (object instanceof CGTemplate) {
			return getIconForTemplate((CGTemplate) object);
		}
		logger.warning("iconForObject(CGTemplateObject) not implemented yet");
		return null;
	}

}
