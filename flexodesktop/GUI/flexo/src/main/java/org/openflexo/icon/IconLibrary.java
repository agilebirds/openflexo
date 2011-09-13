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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.validation.InformationIssue;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in whole application
 * 
 * @author sylvain
 *
 */
public class IconLibrary {

	// Panels
	public static final ImageIcon PROGRESS_BACKGROUND = new ImageIconResource("Icons/Panels/ProgressBackground.jpg");
	public static final ImageIcon WELCOME_BACKGROUND = new ImageIconResource("Icons/Panels/WelcomeBackground.jpg");
	public static final ImageIcon SPLASH_IMAGE = new ImageIconResource("Icons/Panels/SplashPanel.jpg");

	// Flexo icons
	public static final ImageIcon SERVER_32_ICON = new ImageIconResource("Icons/Flexo/Server32.gif");
	public static final ImageIcon BUSINESS_32_ICON = new ImageIconResource("Icons/Flexo/Business32.png");
	public static final ImageIcon ENTERPRISE_32_ICON = new ImageIconResource("Icons/Flexo/Entreprise32.png");
	public static final ImageIcon FLEXO_ICON = new ImageIconResource("Icons/Flexo/FlexoIcon.png");
	public static final ImageIcon FLEXO_ICON_SMALL = new ImageIconResource("Icons/Flexo/FlexoIcon_16.png");
	public static final ImageIcon DEV_TEAM_ICON = new ImageIconResource("Icons/Flexo/Flexo_DT.gif");
	
	// Common icons
	public static final ImageIcon FOLDER_ICON = new ImageIconResource("Icons/Common/Folder.gif");
	public static final ImageIcon SMALL_EXCEL_ICON = new ImageIconResource("Icons/Common/SmallExcel.gif");
	public static final ImageIcon BIG_EXCEL_ICON = new ImageIconResource("Icons/Common/BigExcel.png");
	
	public static final ImageIconResource FIX_PROPOSAL_ICON = new ImageIconResource("Icons/Common/Validation/FixProposal.gif");
	public static final ImageIconResource INFO_ISSUE_ICON = new ImageIconResource("Icons/Common/Validation/Info.gif");
	public static final ImageIconResource FIXABLE_ERROR_ICON = new ImageIconResource("Icons/Common/Validation/FixableError.gif");
	public static final ImageIconResource UNFIXABLE_ERROR_ICON = new ImageIconResource("Icons/Common/Validation/UnfixableError.gif");
	public static final ImageIconResource FIXABLE_WARNING_ICON = new ImageIconResource("Icons/Common/Validation/FixableWarning.gif");
	public static final ImageIconResource UNFIXABLE_WARNING_ICON = new ImageIconResource("Icons/Common/Validation/UnfixableWarning.gif");


	
	// Actions icons
	public static final ImageIcon UNDO_ICON = new ImageIconResource("Icons/Actions/Undo.gif");
	public static final ImageIcon REDO_ICON = new ImageIconResource("Icons/Actions/Redo.gif");
	public static final ImageIcon COPY_ICON = new ImageIconResource("Icons/Actions/Copy.gif");
	public static final ImageIcon PASTE_ICON = new ImageIconResource("Icons/Actions/Paste.gif");
	public static final ImageIcon CUT_ICON = new ImageIconResource("Icons/Actions/Cut.gif");
	public static final ImageIcon DELETE_ICON = new ImageIconResource("Icons/Actions/Delete.gif");
	public static final ImageIcon HELP_ICON = new ImageIconResource("Icons/Actions/Help.gif");
	public static final ImageIcon IMPORT_ICON = new ImageIconResource("Icons/Actions/Import.gif");
	public static final ImageIcon EXPORT_ICON = new ImageIconResource("Icons/Actions/Export.gif");
	public static final ImageIcon PRINT_ICON = new ImageIconResource("Icons/Actions/Print.gif");
	public static final ImageIcon SAVE_ICON = new ImageIconResource("Icons/Actions/Save.gif");
	public static final ImageIcon SAVE_DISABLED_ICON = new ImageIconResource("Icons/Actions/Save-disabled.gif");
	public static final ImageIcon SAVE_ALL_ICON = new ImageIconResource("Icons/Actions/SaveAll.gif");
	public static final ImageIcon SAVE_AS_ICON = new ImageIconResource("Icons/Actions/SaveAs.gif");
	public static final ImageIcon INFO_ICON = new ImageIconResource("Icons/Actions/Info.gif");
	public static final ImageIcon INSPECT_ICON = new ImageIconResource("Icons/Actions/Inspect.gif");	
	public static final ImageIcon REFRESH_ICON = new ImageIconResource("Icons/Actions/Refresh.gif");
	public static final ImageIcon REFRESH_DISABLED_ICON = new ImageIconResource("Icons/Actions/Refresh-disabled.gif");
	public static final ImageIcon TIME_TRAVEL_ICON = new ImageIconResource("Icons/Actions/AutoSaveRestore.gif");
	

	// GUI icons
	
	public static final Icon NAVIGATION_SPACER = new ImageIconResource("Icons/GUI/PerspectiveSpacer.gif");
	public static final Icon NAVIGATION_CLOSE_LEFT = new ImageIconResource("Icons/GUI/PerspectiveLeft.gif");
	public static final Icon NAVIGATION_CLOSE_RIGHT = new ImageIconResource("Icons/GUI/PerspectiveRight.gif");

	public static final ImageIcon DEFAULT_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/GUI/DefaultPerspective_A.gif");
	public static final ImageIcon DEFAULT_PERSPECTIVE_SELECTED_ICON = new ImageIconResource("Icons/GUI/DefaultPerspective_S.gif");
	public static final ImageIcon LIST_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/GUI/ListPerspective_A.gif");
	public static final ImageIcon LIST_PERSPECTIVE_SELECTED_ICON = new ImageIconResource("Icons/GUI/ListPerspective_S.gif");

	public static final ImageIcon BROWSER_PLUS_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserPlus.gif");
	public static final ImageIcon BROWSER_PLUS_DISABLED_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserPlusDisabled.gif");
	public static final ImageIcon BROWSER_PLUS_SELECTED_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserPlusSelected.gif");
	public static final ImageIcon BROWSER_MINUS_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserMinus.gif");
	public static final ImageIcon BROWSER_MINUS_DISABLED_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserMinusDisabled.gif");
	public static final ImageIcon BROWSER_MINUS_SELECTED_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserMinusSelected.gif");
	public static final ImageIcon BROWSER_OPTIONS_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserOptions.gif");
	public static final ImageIcon BROWSER_OPTIONS_DISABLED_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserOptionsDisabled.gif");
	public static final ImageIcon BROWSER_OPTIONS_SELECTED_ICON = new ImageIconResource("Icons/GUI/Browser/BrowserOptionsSelected.gif");

	public static final Icon TOGGLE_ARROW_BOTTOM_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowBottom.gif");
	public static final Icon TOGGLE_ARROW_BOTTOM_SELECTED_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowBottomSelected.gif");
	public static final Icon TOGGLE_ARROW_TOP_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowTop.gif");
	public static final Icon TOGGLE_ARROW_TOP_SELECTED_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowTopSelected.gif");
	public static final Icon TOGGLE_ARROW_LEFT_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowLeft.gif");
	public static final Icon TOGGLE_ARROW_LEFT_SELECTED_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowLeftSelected.gif");
	public static final Icon TOGGLE_ARROW_RIGHT_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowRight.gif");
	public static final Icon TOGGLE_ARROW_RIGHT_SELECTED_ICON = new ImageIconResource("Icons/GUI/Controls/toggleArrowRightSelected.gif");
	
	public static final Icon NAVIGATION_BACKWARD_ICON = new ImageIconResource("Icons/GUI/Navigation/MenuNAV_Fleche_01.gif");
	public static final Icon NAVIGATION_FORWARD_ICON = new ImageIconResource("Icons/GUI/Navigation/MenuNAV_Fleche_03.gif");
	public static final Icon NAVIGATION_UP_ICON = new ImageIconResource("Icons/GUI/Navigation/MenuNAV_Fleche_02.gif");
	public static final Icon NAVIGATION_DISABLED_BACKWARD_ICON = new ImageIconResource("Icons/GUI/Navigation/MenuNAV_Fleche_01_NA.gif");
	public static final Icon NAVIGATION_DISABLED_FORWARD_ICON = new ImageIconResource("Icons/GUI/Navigation/MenuNAV_Fleche_03_NA.gif");
	public static final Icon NAVIGATION_DISABLED_UP_ICON = new ImageIconResource("Icons/GUI/Navigation/MenuNAV_Fleche_02_NA.gif");

	public static final Icon COLLAPSE_ALL_ICON = new ImageIconResource("Icons/GUI/Actions/CollapseAll.png");
	public static final Icon AUTO_LAYOUT_ICON = new ImageIconResource("Icons/GUI/Actions/AutoLayout.png");
	public static final Icon CLOSE_ICON = new ImageIconResource("Icons/GUI/Actions/Close.png");

	// Utils icons
	
	public static final ImageIcon OK_ICON = new ImageIconResource("Icons/Utils/OK.gif");
	public static final ImageIcon WARNING_ICON = new ImageIconResource("Icons/Utils/Warning.gif");
	public static final ImageIcon ERROR_ICON = new ImageIconResource("Icons/Utils/Error.gif");

	public static final ImageIcon MOVE_UP_ICON = new ImageIconResource("Icons/Utils/Up.gif");
	public static final ImageIcon MOVE_DOWN_ICON = new ImageIconResource("Icons/Utils/Down.gif");
	public static final ImageIcon MOVE_LEFT_ICON = new ImageIconResource("Icons/Utils/Left.gif");
	public static final ImageIcon MOVE_RIGHT_ICON = new ImageIconResource("Icons/Utils/Right.gif");
	public static final ImageIcon CLOCK_ICON = new ImageIconResource("Icons/Utils/Clock.gif");
	public static final ImageIcon SEPARATOR_ICON = new ImageIconResource("Icons/Utils/Separator.gif");

	// Markers
	public static final IconMarker POSITIVE_MARKER = new IconMarker(new ImageIconResource("Icons/Utils/Markers/Plus.png"), 0, 0);
	public static final IconMarker NEGATIVE_MARKER = new IconMarker(new ImageIconResource("Icons/Utils/Markers/Minus.png"), 0, 0);
	public static final IconMarker WARNING = new IconMarker(new ImageIconResource("Icons/Utils/Markers/Warning.gif"), 0, 9);
	public static final IconMarker IMPORT = new IconMarker(new ImageIconResource("Icons/Utils/Markers/Import.gif"), 10, 7);
	public static final IconMarker ERROR = new IconMarker(new ImageIconResource("Icons/Utils/Markers/Error.gif"), 0, 9);
	public static final IconMarker ERROR2 = new IconMarker(new ImageIconResource("Icons/Utils/Markers/Error2.gif"), 0, 9);
	public static final IconMarker QUESTION = new IconMarker(new ImageIconResource("Icons/Utils/Markers/Question.gif"), 12, 9);
	public static final IconMarker MERGE_OK = new IconMarker(new ImageIconResource("Icons/Utils/Markers/OK.gif"), 12, 1);
	public static final ImageIcon QUESTION_ICON = new ImageIconResource("Icons/Utils/Question.gif");

	// Cursors
	public static final ImageIcon DROP_OK_CURSOR = new ImageIconResource("Icons/Utils/Cursors/DropOKCursor.gif");
	public static final ImageIcon DROP_KO_CURSOR = new ImageIconResource("Icons/Utils/Cursors/DropKOCursor.gif");
	public static final ImageIcon HELP_CURSOR = new ImageIconResource("Icons/Utils/Cursors/HelpCursor.gif");

	// Palette
	public static final ImageIcon ADD_PALETTE_ICON = new ImageIconResource("Icons/Actions/Palette/AddPalettePanel.gif");
	public static final ImageIcon ADD_PALETTE_DISABLED_ICON = new ImageIconResource("Icons/Actions/Palette/AddPalettePanelDisabled.gif");
	public static final ImageIcon EDIT_PALETTE_ICON = new ImageIconResource("Icons/Actions/Palette/EditPalette.gif");
	public static final ImageIcon EDIT_PALETTE_DISABLED_ICON = new ImageIconResource("Icons/Actions/Palette/EditPaletteDisabled.gif");
	public static final ImageIcon SAVE_PALETTE_ICON = new ImageIconResource("Icons/Actions/Palette/SavePalette.gif");
	public static final ImageIcon SAVE_PALETTE_DISABLED_ICON = new ImageIconResource("Icons/Actions/Palette/SavePaletteDisabled.gif");
	public static final ImageIcon CLOSE_EDITION_ICON = new ImageIconResource("Icons/Actions/Palette/CloseEdition.gif");
	public static final ImageIcon CLOSE_EDITION_DISABLED_ICON = new ImageIconResource("Icons/Actions/Palette/CloseEditionDisabled.gif");

	// Flags
	public static final Icon UK_FLAG = new ImageIconResource("Icons/Utils/Lang/uk-flag.gif");
	public static final Icon FR_FLAG = new ImageIconResource("Icons/Utils/Lang/fr-flag.gif");
	
	// Model icons
	public static final ImageIcon PROJECT_ICON = new ImageIconResource("Icons/Model/Project.gif");

	public static ImageIcon getIconForValidationIssue(ValidationIssue issue)
	{
		if (issue instanceof ValidationWarning) {
			return (((ValidationWarning)issue).isFixable() ? IconLibrary.FIXABLE_WARNING_ICON : IconLibrary.UNFIXABLE_WARNING_ICON);
		}
		else if (issue instanceof ValidationError) {
			return (((ValidationError)issue).isFixable() ? IconLibrary.FIXABLE_ERROR_ICON : IconLibrary.UNFIXABLE_ERROR_ICON);
		}
		else if (issue instanceof InformationIssue) {
	        return IconLibrary.INFO_ISSUE_ICON;
		}
		return null;
	}

	public static ImageIcon getIconForResourceType(ResourceType resourceType)
	{
		if (resourceType == ResourceType.COMPONENT_LIBRARY) {
			return SEIconLibrary.COMPONENT_LIBRARY_ICON;
		}
		else if (resourceType == ResourceType.DKV_MODEL) {
			return SEIconLibrary.DOMAIN_ICON;
		}
		else if (resourceType == ResourceType.DATA_MODEL) {
			return DMEIconLibrary.DM_MODEL_ICON;
		}
		else if (resourceType == ResourceType.GENERATED_CODE) {
			return CGIconLibrary.GENERATED_CODE_ICON;
		}
		else if (resourceType == ResourceType.GENERATED_DOC) {
			return DGIconLibrary.GENERATED_DOC_ICON;
		}
		else if (resourceType == ResourceType.GENERATED_SOURCES) {
			return CGIconLibrary.GENERATED_CODE_ICON;
		}
		else if (resourceType == ResourceType.IMPLEMENTATION_MODEL) {
			return CGIconLibrary.GENERATED_CODE_ICON;
		}
		else if (resourceType == ResourceType.MONITORING_COMPONENT) {
			return SEIconLibrary.SCREEN_COMPONENT_ICON;
		}
		else if (resourceType == ResourceType.MONITORING_SCREEN) {
			return SEIconLibrary.SCREEN_COMPONENT_ICON;
		}
		else if (resourceType == ResourceType.OPERATION_COMPONENT) {
			return SEIconLibrary.SCREEN_COMPONENT_ICON;
		}
		else if (resourceType == ResourceType.TAB_COMPONENT) {
			return SEIconLibrary.SCREEN_COMPONENT_ICON;
		}
		else if (resourceType == ResourceType.POPUP_COMPONENT) {
			return SEIconLibrary.SCREEN_COMPONENT_ICON;
		}
		else if (resourceType == ResourceType.PROCESS) {
			return WKFIconLibrary.PROCESS_ICON;
		}
		else if (resourceType == ResourceType.RM) {
			return IconLibrary.PROJECT_ICON;
		}
		else if (resourceType == ResourceType.SCREENSHOT) {
			return FilesIconLibrary.SMALL_IMAGE_ICON;
		}
		else if (resourceType == ResourceType.WORKFLOW) {
			return WKFIconLibrary.WORKFLOW_ICON;
		}
		
		return FilesIconLibrary.smallIconForFileFormat(resourceType.getFormat());
	}
}
