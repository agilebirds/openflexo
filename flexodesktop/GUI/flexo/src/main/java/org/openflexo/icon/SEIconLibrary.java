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

import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DKVModel.DomainList;
import org.openflexo.foundation.dkv.DKVModel.LanguageList;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Domain.KeyList;
import org.openflexo.foundation.dkv.Domain.ValueList;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.dkv.Value;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.IEBIRTWidget;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEDynamicImage;
import org.openflexo.foundation.ie.widget.IEFileUploadWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IEMultimediaWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWysiwygWidget;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of IEModule
 * 
 * @author sylvain
 * 
 */
public class SEIconLibrary extends IconLibrary {

	private static final Logger logger = Logger.getLogger(SEIconLibrary.class.getPackage().getName());

	// Module icons
	public static final ImageIconResource SE_SMALL_ICON = new ImageIconResource("Icons/SE/module-se-16.png");
	public static final ImageIconResource SE_MEDIUM_ICON = new ImageIconResource("Icons/SE/module-se-32.png");
	public static final ImageIconResource SE_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/SE/module-se-hover-32.png");
	public static final ImageIconResource SE_BIG_ICON = new ImageIconResource("Icons/SE/module-se-hover-64.png");

	// Perspective icons
	public static final ImageIcon COMPONENT_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/SE/ComponentPerspective_A.png");
	public static final ImageIcon EXAMPLE_VALUE_ACTIVE_ICON = new ImageIconResource("Icons/SE/ExampleValuePerspective_A.png");
	public static final ImageIcon MENU_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/SE/MenuPerspective_A.png");
	public static final ImageIcon DKV_PERSPECTIVE_ACTIVE_ICON = new ImageIconResource("Icons/SE/DKVPerspective_A.png");

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

	public static ImageIcon iconForObject(DKVObject object) {
		if (object instanceof DKVModel) {
			return DOMAIN_ICON;
		} else if (object instanceof Domain) {
			return DOMAIN_ICON;
		} else if (object instanceof DomainList) {
			return LIST_ICON;
		} else if (object instanceof LanguageList) {
			return LIST_ICON;
		} else if (object instanceof Language) {
			return LANGUAGE_ICON;
		} else if (object instanceof Key) {
			return DKV_KEY_ICON;
		} else if (object instanceof Value) {
			return VALUE_ICON;
		} else if (object instanceof KeyList) {
			return LIST_ICON;
		} else if (object instanceof ValueList) {
			return LIST_ICON;
		}

		return null;
	}

	public static ImageIcon iconForObject(IEObject object) {
		if (object instanceof ComponentInstance) {
			object = ((ComponentInstance) object).getComponentDefinition();
		}
		if (object instanceof IEWOComponent) {
			object = ((IEWOComponent) object).getComponentDefinition();
		}

		if (object instanceof OperationComponentDefinition) {
			return OPERATION_COMPONENT_ICON;
		} else if (object instanceof PopupComponentDefinition) {
			return POPUP_COMPONENT_ICON;
		} else if (object instanceof TabComponentDefinition) {
			return TAB_COMPONENT_ICON;
		} else if (object instanceof ReusableComponentDefinition) {
			return REUSABLE_COMPONENT_ICON;
		} else if (object instanceof FlexoComponentFolder) {
			return IE_FOLDER_ICON;
		} else if (object instanceof FlexoComponentLibrary) {
			return COMPONENT_LIBRARY_ICON;
		} else if (object instanceof FlexoItemMenu) {
			return MENUITEM_ICON;
		} else if (object instanceof IEBlocWidget) {
			return BLOC_ICON;
		} else if (object instanceof IEHTMLTableWidget) {
			return HTMLTABLE_ICON;
		} else if (object instanceof IETRWidget) {
			return TR_ICON;
		} else if (object instanceof IETDWidget) {
			return TD_ICON;
		} else if (object instanceof IESequenceTab && ((IESequenceTab) object).isRoot()) {
			return THUMBNAILCONTAINER_ICON;
		} else if (object instanceof IEStringWidget) {
			return STRING_ICON;
		} else if (object instanceof IELabelWidget) {
			return LABEL_ICON;
		} else if (object instanceof IEWysiwygWidget) {
			return WYSIWYG_ICON;
		} else if (object instanceof IEDropDownWidget) {
			return DROPDOWN_ICON;
		} else if (object instanceof IEDynamicImage) {
			return IMAGE_FILE;
		} else if (object instanceof IEMultimediaWidget) {
			return MULTIMEDIA_ICON;
		} else if (object instanceof IEButtonWidget) {
			return BUTTON_ICON;
		} else if (object instanceof IEBIRTWidget) {
			return BIRT_ICON;
		} else if (object instanceof IETextFieldWidget) {
			return TEXTFIELD_ICON;
		} else if (object instanceof IETextAreaWidget) {
			return TEXTAREA_ICON;
		} else if (object instanceof IEHeaderWidget) {
			return HEADER_ICON;
		} else if (object instanceof IEHyperlinkWidget) {
			return HYPERLINK_ICON;
		} else if (object instanceof IECheckBoxWidget) {
			return CHECKBOX_ICON;
		} else if (object instanceof IEFileUploadWidget) {
			return FILEUPLOAD_ICON;
		} else if (object instanceof IETabWidget) {
			return TAB_COMPONENT_ICON;
		} else if (object instanceof TabComponentDefinition) {
			return TAB_COMPONENT_ICON;
		} else if (object instanceof IERadioButtonWidget) {
			return RADIOBUTTON_ICON;
		} else if (object instanceof IEBrowserWidget) {
			return BROWSER_ICON;
		} else if (object instanceof IESequence) {
			return CONDITIONAL_ICON;
		} else if (object instanceof RepetitionOperator) {
			return REPETITION_ICON;
		} else if (object instanceof ConditionalOperator) {
			return CONDITIONAL_ICON;
		}

		return null;
	}
}
