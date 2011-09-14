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
package org.openflexo.foundation.ie.util;

import org.openflexo.xmlcode.StringRepresentable;

import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEFileUploadWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWysiwygWidget;
import org.openflexo.localization.FlexoLocalization;

public enum WidgetType implements StringRepresentable{
    
    BLOCK {
        @Override
		public String getClassNameKey() {
            return IEBlocWidget.BLOC_WIDGET;
        }
    }, BROWSER {
        @Override
		public String getClassNameKey() {
            return IEBrowserWidget.BROWSER_WIDGET;
        }
    }, CHECKBOX {
        @Override
		public String getClassNameKey() {
            return IECheckBoxWidget.CHECKBOX_WIDGET;
        }
    }, CUSTOMBUTTON {
    	@Override
		public String getClassNameKey() {
    		return IEHyperlinkWidget.HYPERLINK_WIDGET;
    	}
    }, DROPDOWN {
        @Override
		public String getClassNameKey() {
            return IEDropDownWidget.DROPDOWN_WIDGET;
        }
    }, FILEUPLOAD {
        @Override
		public String getClassNameKey() {
            return IEFileUploadWidget.FILE_UPLOAD_WIDGET;
        }
    }, HEADER {
        @Override
		public String getClassNameKey() {
            return IEHeaderWidget.HEADER_WIDGET;
        }
    }, HTMLTable {
        @Override
		public String getClassNameKey() {
            return IEHTMLTableWidget.HTML_TABLE_WIDGET;
        }
    }, HYPERLINK {
        @Override
		public String getClassNameKey() {
            return IEHyperlinkWidget.HYPERLINK_WIDGET;
        }
    }, LABEL {
        @Override
		public String getClassNameKey() {
            return IELabelWidget.LABEL_WIDGET;
        }
    }, LIST {
        @Override
		public String getClassNameKey() {
            return "list_widget";
        }
    }, RADIO {
        @Override
		public String getClassNameKey() {
            return IERadioButtonWidget.RADIO_BUTTON_WIDGET;
        }
    }, STRING {
        @Override
		public String getClassNameKey() {
            return IEStringWidget.STRING_WIDGET;
        }
    }, TABS {
        @Override
		public String getClassNameKey() {
            return IESequenceTab.TAB_CONTAINER_WIDGET;
        }
    }, TEXTAREA {
        @Override
		public String getClassNameKey() {
            return IETextAreaWidget.TEXTAREA_WIDGET;
        }
    }, TEXTFIELD {
        @Override
		public String getClassNameKey() {
            return IETextFieldWidget.TEXTFIELD_WIDGET;
        }
    }, WYSIWYG {
        @Override
		public String getClassNameKey() {
            return IEWysiwygWidget.WYSIWYG_WIDGET;
        }
    };
    /**
     * Overrides toString
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return FlexoLocalization.localizedForKey(getClassNameKey());
    }
    
    public abstract String getClassNameKey();
}