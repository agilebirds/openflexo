package org.openflexo.icon;

import javax.swing.ImageIcon;

import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of utils
 * 
 * @author sylvain
 * 
 */
public class UtilsIconLibrary {

	// Common icons used in the context of utils

	public static final ImageIcon CUSTOM_POPUP_DOWN = new ImageIconResource("Icons/CustomPopupDown.png");
	public static final ImageIcon CUSTOM_POPUP_DOWN_DISABLED = new ImageIconResource("Icons/CustomPopupDownDisabled.png");

	public static final ImageIcon CUSTOM_POPUP_BUTTON = new ImageIconResource("Icons/CustomPopupButton.png");
	public static final ImageIcon CUSTOM_POPUP_OPEN_BUTTON = new ImageIconResource("Icons/CustomPopupOpenButton.png");

	public static final ImageIcon CLOSE_ICON = new ImageIconResource("Icons/Actions/Close.png");
	public static final ImageIcon CLOSE_HOVER_ICON = new ImageIconResource("Icons/Actions/CloseHover.png");
	public static final ImageIcon CLOSE_PRESSED_ICON = new ImageIconResource("Icons/Actions/ClosePressed.png");

	public static final ImageIcon ARROW_DOWN = new ImageIconResource("Icons/ArrowDown.gif");
	public static final ImageIcon ARROW_UP = new ImageIconResource("Icons/ArrowUp.gif");
	public static final ImageIcon ARROW_LEFT = new ImageIconResource("Icons/ArrowLeft.gif");
	public static final ImageIcon ARROW_RIGHT = new ImageIconResource("Icons/ArrowRight.gif");

	public static final ImageIcon ARROW_DOWN_2 = new ImageIconResource("Icons/Arrows/Down.png");
	public static final ImageIcon ARROW_UP_2 = new ImageIconResource("Icons/Arrows/Up.png");
	public static final ImageIcon ARROW_BOTTOM_2 = new ImageIconResource("Icons/Arrows/Bottom.png");
	public static final ImageIcon ARROW_TOP_2 = new ImageIconResource("Icons/Arrows/Top.png");

	// Diff icons

	public static final ImageIcon ADDITION_ICON = new ImageIconResource("Icons/IconsDiff/Addition.gif");
	public static final ImageIcon REMOVAL_ICON = new ImageIconResource("Icons/IconsDiff/Removal.gif");
	public static final ImageIcon ADDITION_LEFT_ICON = new ImageIconResource("Icons/IconsDiff/Addition-left.gif");
	public static final ImageIcon REMOVAL_LEFT_ICON = new ImageIconResource("Icons/IconsDiff/Removal-left.gif");
	public static final ImageIcon MODIFICATION_LEFT_ICON = new ImageIconResource("Icons/IconsDiff/Modification-left.gif");
	public static final ImageIcon ADDITION_RIGHT_ICON = new ImageIconResource("Icons/IconsDiff/Addition-right.gif");
	public static final ImageIcon REMOVAL_RIGHT_ICON = new ImageIconResource("Icons/IconsDiff/Removal-right.gif");
	public static final ImageIcon MODIFICATION_RIGHT_ICON = new ImageIconResource("Icons/IconsDiff/Modification-right.gif");

	public static final ImageIcon RIGHT_UPDATE_ICON = new ImageIconResource("Icons/IconsDiff/RightUpdate.gif");
	public static final ImageIcon LEFT_UPDATE_ICON = new ImageIconResource("Icons/IconsDiff/LeftUpdate.gif");

	// Merge icons

	public static final ImageIcon LEFT_ADDITION_ICON = new ImageIconResource("Icons/IconsMerge/r_outadd_ov2.gif");
	public static final ImageIcon LEFT_MODIFICATION_ICON = new ImageIconResource("Icons/IconsMerge/r_outchg_ov2.gif");
	public static final ImageIcon LEFT_REMOVAL_ICON = new ImageIconResource("Icons/IconsMerge/r_outdel_ov2.gif");
	public static final ImageIcon RIGHT_ADDITION_ICON = new ImageIconResource("Icons/IconsMerge/r_inadd_ov2.gif");
	public static final ImageIcon RIGHT_MODIFICATION_ICON = new ImageIconResource("Icons/IconsMerge/r_inchg_ov2.gif");
	public static final ImageIcon RIGHT_REMOVAL_ICON = new ImageIconResource("Icons/IconsMerge/r_indel_ov2.gif");
	public static final ImageIcon CONFLICT_ADDITION_ICON = new ImageIconResource("Icons/IconsMerge/confadd_ov2.gif");
	public static final ImageIcon CONFLICT_MODIFICATION_ICON = new ImageIconResource("Icons/IconsMerge/confchg_ov2.gif");
	public static final ImageIcon CONFLICT_REMOVAL_ICON = new ImageIconResource("Icons/IconsMerge/confdel_ov2.gif");
	public static final ImageIcon CONFLICT_ICON = new ImageIconResource("Icons/IconsMerge/ConflictUnresolved.gif");
	public static final ImageIcon RIGHT_ICON = new ImageIconResource("Icons/IconsMerge/r_inchg_ov.gif");
	public static final ImageIcon LEFT_ICON = new ImageIconResource("Icons/IconsMerge/r_outchg_ov.gif");
	public static final ImageIcon ACCEPT_ICON = new ImageIconResource("Icons/IconsMerge/Accept.gif");
	public static final ImageIcon REFUSE_ICON = new ImageIconResource("Icons/IconsMerge/Refuse.gif");
	public static final ImageIcon CHOOSE_LEFT_ICON = new ImageIconResource("Icons/IconsMerge/Left.gif");
	public static final ImageIcon CHOOSE_RIGHT_ICON = new ImageIconResource("Icons/IconsMerge/Right.gif");
	public static final ImageIcon CHOOSE_NONE = new ImageIconResource("Icons/IconsMerge/Refuse.gif");
	public static final ImageIcon CHOOSE_BOTH_LEFT_FIRST = new ImageIconResource("Icons/IconsMerge/LeftRight.gif");
	public static final ImageIcon CHOOSE_BOTH_RIGHT_FIRST = new ImageIconResource("Icons/IconsMerge/RightLeft.gif");
	public static final ImageIcon CUSTOM_EDITING_ICON = new ImageIconResource("Icons/IconsMerge/CustomEditing.gif");
	public static final ImageIcon AUTOMATIC_MERGE_RESOLVING_ICON = new ImageIconResource("Icons/IconsMerge/SmartConflictResolving.gif");
	public static final ImageIcon SMART_CONFLICT_RESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/SmartConflictResolved.gif");
	public static final ImageIcon SMART_CONFLICT_UNRESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/SmartConflictUnresolved.gif");
	public static final ImageIcon CUSTOM_EDITING_RESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/CustomEditingResolved.gif");
	public static final ImageIcon CUSTOM_EDITING_UNRESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/CustomEditingUnresolved.gif");
	public static final ImageIcon CONFLICT_RESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/ConflictResolved.gif");
	public static final ImageIcon CONFLICT_UNRESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/ConflictUnresolved.gif");
	public static final ImageIcon ADD_CONFLICT_RESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/AddConflictResolved.gif");
	public static final ImageIcon ADD_CONFLICT_UNRESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/AddConflictUnresolved.gif");
	public static final ImageIcon DEL_CONFLICT_RESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/DeleteConflictResolved.gif");
	public static final ImageIcon DEL_CONFLICT_UNRESOLVED_ICON = new ImageIconResource("Icons/IconsMerge/DeleteConflictUnresolved.gif");

	/* Icon markers */
	public static final IconMarker LEFT_ADDITION = new IconMarker(LEFT_ADDITION_ICON, 12, 7);
	public static final IconMarker LEFT_MODIFICATION = new IconMarker(LEFT_MODIFICATION_ICON, 12, 7);
	public static final IconMarker LEFT_REMOVAL = new IconMarker(LEFT_REMOVAL_ICON, 12, 7);
	public static final IconMarker RIGHT_ADDITION = new IconMarker(RIGHT_ADDITION_ICON, 12, 7);
	public static final IconMarker RIGHT_MODIFICATION = new IconMarker(RIGHT_MODIFICATION_ICON, 12, 7);
	public static final IconMarker RIGHT_REMOVAL = new IconMarker(RIGHT_REMOVAL_ICON, 12, 7);
	public static final IconMarker CONFLICT = new IconMarker(CONFLICT_ICON, 12, 7);

	// Utils icons

	public static final ImageIcon OK_ICON = new ImageIconResource("Icons/Utils/OK.gif");
	public static final ImageIcon WARNING_ICON = new ImageIconResource("Icons/Utils/Warning.gif");
	public static final ImageIcon ERROR_ICON = new ImageIconResource("Icons/Utils/Error.gif");

	public static final ImageIcon MOVE_UP_ICON = new ImageIconResource("Icons/Utils/Up.gif");
	public static final ImageIcon MOVE_DOWN_ICON = new ImageIconResource("Icons/Utils/Down.gif");
	public static final ImageIcon MOVE_LEFT_ICON = new ImageIconResource("Icons/Utils/Left.gif");
	public static final ImageIcon MOVE_RIGHT_ICON = new ImageIconResource("Icons/Utils/Right.gif");
	public static final ImageIcon CLOCK_ICON = new ImageIconResource("Icons/Clock.gif");
	public static final ImageIcon SEPARATOR_ICON = new ImageIconResource("Icons/Utils/Separator.gif");
	public static final ImageIcon SEARCH_ICON = new ImageIconResource("Icons/Utils/Search.png");
	public static final ImageIcon CANCEL_ICON = new ImageIconResource("Icons/Utils/Cancel.png");

	// Flags
	public static final ImageIcon UK_FLAG = new ImageIconResource("Icons/Lang/uk-flag.gif");
	public static final ImageIcon FR_FLAG = new ImageIconResource("Icons/Lang/fr-flag.gif");
	public static final ImageIcon NE_FLAG = new ImageIconResource("Icons/Lang/ne-flag.gif");

}
