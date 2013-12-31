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

import java.util.Vector;

import javax.swing.ImageIcon;

import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of all generation module (common resources for code, source and doc generators)
 * 
 * @author sylvain
 * 
 */
public class GeneratorIconLibrary extends IconLibrary {

	// Editor icons
	public static final ImageIcon BACKUP_ICON = new ImageIconResource("Icons/Generator/backup.gif");
	public static final ImageIcon TOMCAT_ICON = new ImageIconResource("Icons/Generator/tomcat.gif");
	public static final ImageIcon WOLIPS_ICON = new ImageIconResource("Icons/Generator/wolips.gif");

	// Utils
	protected static final ImageIcon TEMPLATE_TAG_ICON = new ImageIconResource("Icons/Generator/Utils/tag.gif");
	protected static final ImageIcon VELOCITY_TEMPLATE_ICON = new ImageIconResource("Icons/Generator/Utils/VelocityMarker.gif");
	public static final IconMarker VELOCITY_MARKER = new IconMarker(VELOCITY_TEMPLATE_ICON, 0, 0);
	public static final IconMarker TEMPLATE_MARKER = new IconMarker(TEMPLATE_TAG_ICON, 12, 0);
	public static final ImageIcon DO_NOT_GENERATE_ICON = new ImageIconResource("Icons/Generator/Utils/dont_generate.gif");
	public static final IconMarker DO_NOT_GENERATE = new IconMarker(DO_NOT_GENERATE_ICON, 12, 7);
	public static final ImageIcon NEEDS_REGENERATE_ICON = new ImageIconResource("Icons/Generator/Utils/NeedsRegenerate.gif");
	public static final ImageIcon NEEDS_MODEL_REINJECTION_ICON = new ImageIconResource("Icons/Generator/Utils/NeedsModelReinjection.gif");
	public static final IconMarker NEEDS_REGENERATE = new IconMarker(NEEDS_REGENERATE_ICON, 0, 0);
	public static final IconMarker NEEDS_MODEL_REINJECTION = new IconMarker(NEEDS_MODEL_REINJECTION_ICON, 10, 0);

	public static final ImageIcon INTERESTING_FILES_VIEW_MODE_ICON = new ImageIconResource(
			"Icons/Generator/ViewMode/InterestingFilesViewMode.gif");
	public static final ImageIcon GENERATION_MODIFIED_VIEW_MODE_ICON = new ImageIconResource(
			"Icons/Generator/ViewMode/GenerationModifiedViewMode.gif");
	public static final ImageIcon DISK_MODIFIED_VIEW_MODE_ICON = new ImageIconResource("Icons/Generator/ViewMode/DiskModifiedViewMode.gif");
	public static final ImageIcon CONFLICTING_FILES_VIEW_MODE_ICON = new ImageIconResource(
			"Icons/Generator/ViewMode/ConflictingFilesViewMode.gif");
	public static final ImageIcon NEED_REINJECTING_VIEW_MODE_ICON = new ImageIconResource(
			"Icons/Generator/ViewMode/NeedReinjectingViewMode.gif");
	public static final ImageIcon GENERATION_ERROR_VIEW_MODE_ICON = new ImageIconResource(
			"Icons/Generator/ViewMode/GenerationErrorViewMode.gif");

	public static final ImageIcon RELEASE_VERSION_ICON = new ImageIconResource("Icons/Generator/Utils/Version1.gif");
	public static final ImageIcon INTERMEDIATE_VERSION_ICON = new ImageIconResource("Icons/Generator/Utils/Version2.gif");
	public static final IconMarker RELEASE_VERSION = new IconMarker(RELEASE_VERSION_ICON, 0, 7);
	public static final IconMarker INTERMEDIATE_VERSION = new IconMarker(INTERMEDIATE_VERSION_ICON, 0, 7);

	public static final ImageIcon DIFF_EDITOR_ICON = new ImageIconResource("Icons/Generator/DiffEditor.png");

	// Actions
	public static final ImageIcon WRITE_TO_DISK_ICON = new ImageIconResource("Icons/Generator/Actions/WriteToDiskIcon.gif");
	public static final ImageIcon WRITE_TO_DISK_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/WriteToDiskIcon-disabled.gif");
	public static final ImageIcon DISMISS_UNCHANGED_ICON = new ImageIconResource("Icons/Generator/Actions/DismissUnchangedIcon.gif");
	public static final ImageIcon DISMISS_UNCHANGED_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/DismissUnchangedIcon-disabled.gif");
	public static final ImageIcon ACCEPT_FROM_DISK_ICON = new ImageIconResource("Icons/Generator/Actions/AcceptFromDiskIcon.gif");
	public static final ImageIcon ACCEPT_FROM_DISK_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/AcceptFromDiskIcon-disabled.gif");
	public static final ImageIcon ACCEPT_AND_REINJECT_ICON = new ImageIconResource("Icons/Generator/Actions/AcceptAndReinjectIcon.gif");
	public static final ImageIcon ACCEPT_AND_REINJECT_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/AcceptAndReinjectIcon-disabled.gif");
	public static final ImageIcon REINJECT_IN_MODEL_ICON = new ImageIconResource("Icons/Generator/Actions/ReinjectInModelIcon.gif");
	public static final ImageIcon REINJECT_IN_MODEL_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/ReinjectInModelIcon-disabled.gif");
	public static final ImageIcon UPDATE_MODEL_ICON = new ImageIconResource("Icons/Generator/Actions/UpdateModelIcon.gif");
	public static final ImageIcon UPDATE_MODEL_DISABLED_ICON = new ImageIconResource("Icons/Generator/Actions/UpdateModelIcon-disabled.gif");
	public static final ImageIcon IMPORT_IN_MODEL_ICON = new ImageIconResource("Icons/Generator/Actions/ImportInModelIcon.gif");
	public static final ImageIcon IMPORT_IN_MODEL_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/ImportInModelIcon-disabled.gif");
	public static final ImageIcon SYNCHRONIZE_CODE_GENERATION_ICON = new ImageIconResource(
			"Icons/Generator/Actions/SynchronizeCodeGenerationIcon.gif");
	public static final ImageIcon GENERATE_CODE_ICON = new ImageIconResource("Icons/Generator/Actions/GenerateCodeIcon.gif");
	public static final ImageIcon GENERATE_WRITE_CODE_ICON = new ImageIconResource("Icons/Generator/Actions/GenerateAndWrite.gif");
	public static final ImageIcon GENERATE_CODE_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/GenerateCodeIcon-disabled.gif");
	public static final ImageIcon FORCE_REGENERATE_CODE_ICON = new ImageIconResource("Icons/Generator/Actions/ForceRegenerateCodeIcon.gif");
	public static final ImageIcon FORCE_REGENERATE_CODE_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/ForceRegenerateCodeIcon-disabled.gif");
	public static final ImageIcon MARK_AS_MERGED_ICON = new ImageIconResource("Icons/Generator/Actions/MarkAsMergedIcon.gif");
	public static final ImageIcon MARK_AS_MERGED_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/MarkAsMergedIcon-disabled.gif");
	public static final ImageIcon EDIT_ICON = new ImageIconResource("Icons/Generator/Actions/Edit.gif");
	public static final ImageIcon EDIT_DISABLED_ICON = new ImageIconResource("Icons/Generator/Actions/Edit-disabled.gif");
	public static final ImageIcon CANCEL_ICON = new ImageIconResource("Icons/Generator/Actions/Cancel.gif");
	public static final ImageIcon CANCEL_DISABLED_ICON = new ImageIconResource("Icons/Generator/Actions/Cancel-disabled.gif");
	public static final ImageIcon REGISTER_NEW_CG_RELEASE_ICON = new ImageIconResource("Icons/Generator/Actions/RegisterNewReleaseIcon.gif");
	public static final ImageIcon REGISTER_NEW_CG_RELEASE_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/RegisterNewReleaseIcon-disabled.gif");
	public static final ImageIcon REVERT_REPOSITORY_TO_VERSION_ICON = new ImageIconResource(
			"Icons/Generator/Actions/RevertRepositoryToVersionIcon.gif");
	public static final ImageIcon REVERT_REPOSITORY_TO_VERSION_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/RevertRepositoryToVersionIcon-disabled.gif");
	public static final ImageIcon REVERT_TO_HISTORY_VERSION_ICON = new ImageIconResource(
			"Icons/Generator/Actions/RevertToHistoryVersionIcon.gif");
	public static final ImageIcon REVERT_TO_HISTORY_VERSION_DISABLED_ICON = new ImageIconResource(
			"Icons/Generator/Actions/RevertToHistoryVersionIcon-disabled.gif");
	public static final ImageIcon COMPARE_ICON = new ImageIconResource("Icons/Generator/Actions/Compare.gif");
	public static final ImageIcon COMPARE_DISABLED_ICON = new ImageIconResource("Icons/Generator/Actions/Compare-disabled.gif");

	/**
	 * Calculate the icon to use for a template (based on its format and additional marker necessary)
	 * 
	 * @return the calculated icon.
	 */
	public static ImageIcon getIconForTemplate(CGTemplate template) {
		ImageIcon returned = FilesIconLibrary.smallIconForFileFormat(template.getFileFormat());

		Vector<IconMarker> markers = new Vector<IconMarker>();
		markers.add(GeneratorIconLibrary.TEMPLATE_MARKER);

		if (template.getTemplateName().endsWith(".vm")) {
			markers.add(GeneratorIconLibrary.VELOCITY_MARKER);
		}

		// Get icon with all markers
		IconMarker[] markersArray = markers.toArray(new IconMarker[markers.size()]);
		returned = IconFactory.getImageIcon(returned, markersArray);

		return returned;
	}

}
