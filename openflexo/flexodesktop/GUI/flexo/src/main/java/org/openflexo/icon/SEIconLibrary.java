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

import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of IEModule
 * 
 * @author sylvain
 *
 */
public class SEIconLibrary extends IconLibrary {

	// Module icons
	public static final ImageIcon IE_ACTIVE_ICON = new ImageIconResource("Icons/SE/IE_A_Small.gif");
	public static final ImageIcon IE_UNACTIVE_ICON = new ImageIconResource("Icons/SE/IE_NA_Small.gif");
	public static final ImageIcon IE_SELECTED_ICON = new ImageIconResource("Icons/SE/IE_S_Small.gif");
	public static final ImageIcon IE_BIG_ACTIVE_ICON = new ImageIconResource("Icons/SE/IE_A.gif");
	public static final ImageIcon IE_BIG_UNACTIVE_ICON = new ImageIconResource("Icons/SE/IE_NA.gif");
	public static final ImageIcon IE_BIG_SELECTED_ICON = new ImageIconResource("Icons/SE/IE_S.gif");

	// Perspective icons
	public static final ImageIcon COMPONENT_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/SE/ComponentPerspective_A.gif");
	public static final ImageIcon COMPONENT_PERSPECTIVE_SELECTED_ICON = new ImageIconResource("Icons/SE/ComponentPerspective_S.gif");
	public static final ImageIcon EXAMPLE_VALUE_ACTIVE_ICON = new ImageIconResource("Icons/SE/ExampleValuePerspective_A.gif");
	public static final ImageIcon EXAMPLE_VALUE_SELECTED_ICON = new ImageIconResource("Icons/SE/ExampleValuePerspective_S.gif");
	public static final ImageIcon MENU_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/SE/MenuPerspective_A.gif");
	public static final ImageIcon MENU_PERSPECTIVE_SELECTED_ICON = new ImageIconResource("Icons/SE/MenuPerspective_S.gif");
	public static final ImageIcon DKV_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/SE/DKVPerspective_A.gif");
	public static final ImageIcon DKV_PERSPECTIVE_SELECTED_ICON = new ImageIconResource("Icons/SE/DKVPerspective_S.gif");

	// Editor icons
	public static final ImageIcon ICON_DOWN = new ImageIconResource("Icons/SE/Utils/Arrow_Down_White.gif");
	public static final ImageIcon ICON_RIGHT = new ImageIconResource("Icons/SE/Utils/Arrow_Right_White.gif");
	public static final ImageIcon ICON_UP = new ImageIconResource("Icons/SE/Utils/Arrow_Up_White.gif");
	public static final ImageIcon DEFAULT_IMAGE_ICON = new ImageIconResource("Icons/SE/Utils/Default_Image.gif");
	public static final ImageIcon MULTIMEDIA_ICON = new ImageIconResource("Icons/SE/Utils/Multimedia.png");
	public static final ImageIcon NO_IMAGE = new ImageIconResource("Icons/SE/Utils/Unavailable.jpeg");

	// Action icons
	public static final Icon DELETECOL_ICON = new ImageIconResource("Icons/SE/Actions/DeleteCol.gif");
	public static final Icon DELETEROW_ICON = new ImageIconResource("Icons/SE/Actions/DeleteRow.gif");
	public static final Icon INSERTCOLAFTER_ICON = new ImageIconResource("Icons/SE/Actions/InsertColAfter.gif");
	public static final Icon INSERTCOLBEFORE_ICON = new ImageIconResource("Icons/SE/Actions/InsertColBefore.gif");
	public static final Icon INSERTROWBOTTOM_ICON = new ImageIconResource("Icons/SE/Actions/InsertRowBottom.gif");
	public static final Icon INSERTROWUP_ICON = new ImageIconResource("Icons/SE/Actions/InsertRowUp.gif");
	
	// Model icons
	
	// IE CL Icons
	public static final ImageIcon COMPONENT_LIBRARY_ICON = new ImageIconResource("Icons/Model/SE/Library_IE.gif");
	public static final ImageIcon IE_FOLDER_ICON = new ImageIconResource("Icons/Model/SE/Folder_IE.gif");

	public static final ImageIcon OPERATION_COMPONENT_ICON = new ImageIconResource("Icons/Model/SE/SmallOperationComponent.gif");
	public static final ImageIcon POPUP_COMPONENT_ICON = new ImageIconResource("Icons/Model/SE/SmallPopupComponent.gif");
	public static final ImageIcon SCREEN_COMPONENT_ICON = new ImageIconResource("Icons/Model/SE/SmallPopupComponent.gif");
	public static final ImageIcon TAB_COMPONENT_ICON = new ImageIconResource("Icons/Model/SE/SmallTabComponent.gif");
	public static final ImageIcon REUSABLE_COMPONENT_ICON = new ImageIconResource("Icons/Model/SE/ReusableComponent.gif");
	public static final ImageIcon COMPONENT_INSTANCE_ICON = new ImageIconResource("Icons/Model/SE/ReusableComponentInstance.gif");

	// Widget icons
	public static final ImageIcon BLOC_ICON = new ImageIconResource("Icons/Model/SE/Small_BLOCK.gif");
	public static final ImageIcon BUTTON_ICON = new ImageIconResource("Icons/Model/SE/Small_BUTTON.gif");
	public static final ImageIcon BIRT_ICON = new ImageIconResource("Icons/Model/SE/Small_BIRT.gif");
	public static final ImageIcon SMALL_LIST_ICON = new ImageIconResource("Icons/Model/SE/Small_LIST.gif");
	public static final ImageIcon TABLE_ICON = new ImageIconResource("Icons/Model/SE/Small_TABLE.gif");
	public static final ImageIcon DROPDOWN_ICON = new ImageIconResource("Icons/Model/SE/Small_DROPDOWN.gif");
	public static final ImageIcon TEXTFIELD_ICON = new ImageIconResource("Icons/Model/SE/Small_TEXTFIELD.gif");
	public static final ImageIcon TEXTAREA_ICON = new ImageIconResource("Icons/Model/SE/Small_TEXTAREA.gif");
	public static final ImageIcon LABEL_ICON = new ImageIconResource("Icons/Model/SE/Small_LABEL.gif");
	public static final ImageIcon WYSIWYG_ICON = new ImageIconResource("Icons/Model/SE/Small_WYSIWYG.gif");
	public static final ImageIcon STRING_ICON = new ImageIconResource("Icons/Model/SE/Small_STRING.gif");
	public static final ImageIcon HEADER_ICON = new ImageIconResource("Icons/Model/SE/Small_HEADER.gif");
	public static final ImageIcon THUMBNAILCONTAINER_ICON = new ImageIconResource("Icons/Model/SE/Small_TABS.gif");
	public static final ImageIcon HTMLTABLE_ICON = new ImageIconResource("Icons/Model/SE/Small_TABLE.gif");
	public static final ImageIcon TD_ICON = new ImageIconResource("Icons/Model/SE/SmallTD.gif");
	public static final ImageIcon TR_ICON = new ImageIconResource("Icons/Model/SE/SmallTR.gif");
	public static final ImageIcon HYPERLINK_ICON = new ImageIconResource("Icons/Model/SE/Small_HYPERLINK.gif");
	public static final ImageIcon CHECKBOX_ICON = new ImageIconResource("Icons/Model/SE/Small_CHECKBOX.gif");
	public static final ImageIcon FILEUPLOAD_ICON = new ImageIconResource("Icons/Model/SE/Small_FILEUPLOAD.gif");
	public static final ImageIcon BROWSER_ICON = new ImageIconResource("Icons/Model/SE/Small_BROWSER.gif");
	public static final ImageIcon CONDITIONAL_ICON = new ImageIconResource("Icons/Model/SE/Conditional.gif");
	public static final ImageIcon REPETITION_ICON = new ImageIconResource("Icons/Model/SE/Repetition.gif");
	public static final ImageIcon RADIOBUTTON_ICON = new ImageIconResource("Icons/Model/SE/Small_RADIOBUTTON.gif");
	public static final ImageIcon IMAGE_FILE = new ImageIconResource("Icons/Model/SE/Small_IMAGE.png");
	
	// DKV Icons
	public static final ImageIcon DKV_KEY_ICON = new ImageIconResource("Icons/Model/SE/DKV/Key.jpg");
	public static final ImageIcon LANGUAGE_ICON = new ImageIconResource("Icons/Model/SE/DKV/Language.jpg");
	public static final ImageIcon DOMAIN_ICON = new ImageIconResource("Icons/Model/SE/DKV/Domain.gif");
	public static final ImageIcon LIST_ICON = new ImageIconResource("Icons/Model/SE/DKV/List.gif");
	public static final ImageIcon VALUE_ICON = new ImageIconResource("Icons/Model/SE/DKV/Value.jpg");
	
	// Menu Icons
	public static final ImageIcon MENUITEM_ICON = new ImageIconResource("Icons/Model/SE/Small_HEADER.gif");
	public static final ImageIcon SMALL_MULTIMEDIA = new ImageIconResource("Icons/Model/SE/Small_MULTIMEDIA.gif");
	public static final ImageIcon REUSABLEWIDGET_ICON = new ImageIconResource("Icons/Model/SE/ReusableComponent.gif");


}
