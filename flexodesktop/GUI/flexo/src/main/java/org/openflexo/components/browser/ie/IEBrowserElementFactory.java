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
package org.openflexo.components.browser.ie;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DKVModel.DomainList;
import org.openflexo.foundation.dkv.DKVModel.LanguageList;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Domain.KeyList;
import org.openflexo.foundation.dkv.Domain.ValueList;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.dkv.Value;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.MonitoringComponentDefinition;
import org.openflexo.foundation.ie.cl.MonitoringScreenDefinition;
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
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWysiwygWidget;

public class IEBrowserElementFactory implements BrowserElementFactory {
	@Override
	public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
		if (object instanceof OperationComponentDefinition) {
			return new OperationComponentElement((OperationComponentDefinition) object, browser, parent);
		} else if (object instanceof TabComponentDefinition) {
			return new TabComponentElement((TabComponentDefinition) object, browser, parent);
		} else if (object instanceof PopupComponentDefinition) {
			return new PopupComponentElement((PopupComponentDefinition) object, browser, parent);
		}

		else if (object instanceof FlexoComponentFolder) {
			return new ComponentFolderElement((FlexoComponentFolder) object, browser, parent);
		} else if (object instanceof FlexoComponentLibrary) {
			return new ComponentLibraryElement((FlexoComponentLibrary) object, browser, parent);
		}

		else if (object instanceof IEBlocWidget) {
			return new IEBlocElement((IEBlocWidget) object, browser, parent);
		} else if (object instanceof IEHTMLTableWidget) {
			return new IEHtmlTableElement((IEHTMLTableWidget) object, browser, parent);
		} else if (object instanceof IETRWidget) {
			return new IETRElement((IETRWidget) object, browser, parent);
		} else if (object instanceof IETDWidget) {
			return new IETDElement((IETDWidget) object, browser, parent);
		} else if (object instanceof IESequenceTab && ((IESequenceTab) object).isRoot()) {
			return new IETabContainerElement((IESequenceTab) object, browser, parent);
		} else if (object instanceof IEStringWidget) {
			return new IEStringElement((IEStringWidget) object, browser, parent);
		} else if (object instanceof IELabelWidget) {
			return new IELabelElement((IELabelWidget) object, browser, parent);
		} else if (object instanceof IEWysiwygWidget) {
			return new IEWysiwygElement((IEWysiwygWidget) object, browser, parent);
		} else if (object instanceof IEDropDownWidget) {
			return new IEDropDownElement((IEDropDownWidget) object, browser, parent);
		} else if (object instanceof IEDynamicImage) {
			return new IEDynamicImageElement((IEDynamicImage) object, browser, parent);
		} else if (object instanceof IEMultimediaWidget) {
			return new IEMultimediaElement((IEMultimediaWidget) object, browser, parent);
		} else if (object instanceof IEButtonWidget) {
			return new IEButtonElement((IEButtonWidget) object, browser, parent);
		} else if (object instanceof IEBIRTWidget) {
			return new IEBIRTElement((IEBIRTWidget) object, browser, parent);
		} else if (object instanceof IETextFieldWidget) {
			return new IETextFieldElement((IETextFieldWidget) object, browser, parent);
		} else if (object instanceof IETextAreaWidget) {
			return new IETextAreaElement((IETextAreaWidget) object, browser, parent);
		} else if (object instanceof IEHeaderWidget) {
			return new IEHeaderElement((IEHeaderWidget) object, browser, parent);
		} else if (object instanceof IEHyperlinkWidget) {
			return new IEHyperlinkElement((IEHyperlinkWidget) object, browser, parent);
		} else if (object instanceof IECheckBoxWidget) {
			return new IECheckBoxElement((IECheckBoxWidget) object, browser, parent);
		} else if (object instanceof IEFileUploadWidget) {
			return new IEFileUploadElement((IEFileUploadWidget) object, browser, parent);
		} else if (object instanceof IETabWidget) {
			return new IETabWidgetElement((IETabWidget) object, browser, parent);
		} else if (object instanceof TabComponentDefinition) {
			return new IETabComponentElement((TabComponentDefinition) object, browser, parent);
		} else if (object instanceof IERadioButtonWidget) {
			return new IERadioButtonElement(object, null, browser, parent);
		} else if (object instanceof IEBrowserWidget) {
			return new IEBrowserWidgetElement(object, null, browser, parent);
		} else if (object instanceof IESequence) {
			return new IESequenceElement((IESequence) object, BrowserElementType.SEQUENCE, browser, parent);
		} else if (object instanceof RepetitionOperator) {
			return new IERepetitionElement((RepetitionOperator) object, browser, parent);
		} else if (object instanceof ConditionalOperator) {
			return new IEConditionalElement((ConditionalOperator) object, browser, parent);
		} else if (object instanceof FlexoComponentFolder) {
			return new ComponentFolderElement((FlexoComponentFolder) object, browser, parent);
		} else if (object instanceof ReusableComponentDefinition) {
			return new ReusableComponentDefinitionElement((ReusableComponentDefinition) object, browser, parent);
		} else if (object instanceof MonitoringComponentDefinition) {
			return new MonitoringScreenDefinitionElement((MonitoringScreenDefinition) object, browser, parent);
		} else if (object instanceof IEReusableWidget) {
			return new IEReusableWidgetElement((IEReusableWidget) object, browser, parent);
		}

		else if (object instanceof FlexoItemMenu) {
			return new FlexoItemMenuElement((FlexoItemMenu) object, browser, parent);
		}

		else if (object instanceof DKVModel) {
			return new DKVModelElement((DKVModel) object, browser, parent);
		} else if (object instanceof Domain) {
			return new DKVDomainElement((Domain) object, browser, parent);
		} else if (object instanceof DomainList) {
			return new DKVDomainListElement((DomainList) object, browser, parent);
		} else if (object instanceof LanguageList) {
			return new DKVLanguageListElement((LanguageList) object, browser, parent);
		} else if (object instanceof Language) {
			return new DKVLanguageElement((Language) object, browser, parent);
		} else if (object instanceof Key) {
			return new DKVKeyElement((Key) object, browser, parent);
		} else if (object instanceof Value) {
			return new DKVValueElement((Value) object, browser, parent);
		} else if (object instanceof KeyList) {
			return new DKVKeyListElement((KeyList) object, browser, parent);
		} else if (object instanceof ValueList) {
			return new DKVValueListElement((ValueList) object, browser, parent);
		}

		return null;
	}

}
