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
package org.openflexo.foundation;

/**
 * This class intented to be root class for all objects representing data (in the context of Model/View/Controller scheme) in Flexo<br>
 * Instances if this class implements Key/Value-coding and wrapers String values to some objects of Flexo Foundation
 * 
 * @author sguerin
 */
@Deprecated
public abstract class KVCFlexoObject /*extends KVCObject*/{

	/*private static boolean isInitialized = false;

	static {
		initialize(true);
	}

	public static void initialize(boolean force) {
		if (!isInitialized || force) {
			initialize(StringEncoder.getDefaultInstance());
			isInitialized = true;
		}
	}

	public static void initialize(StringEncoder encoder) {
		// Ce qui suit (date format) est très important: utilise par le RM: ne pas toucher
		encoder._setDateFormat("HH:mm:ss dd/MM/yyyy");
		encoder._addConverter(Language.languageConverter);
		encoder._addConverter(ResourceType.resourceTypeConverter);
		encoder._addConverter(FileFormat.fileFormatConverter);
		encoder._addConverter(CodeType.codeTypeConverter);
		encoder._addConverter(NodeType.nodeTypeConverter);
		encoder._addConverter(ActionType.actionTypeConverter);
		encoder._addConverter(LoopType.loopTypeConverter);
		encoder._addConverter(FlexoFont.flexoFontConverter);
		encoder._addConverter(FlexoCSS.flexoCSSConverter);
		encoder._addConverter(DropDownType.dropdownTypeConverter);
		encoder._addConverter(HyperlinkType.hyperlinkTypeConverter);
		encoder._addConverter(ClientSideEventType.ClientSideEventTypeConverter);
		encoder._addConverter(TDCSSType.tdCSSTypeConverter);
		encoder._addConverter(TextCSSClass.textCSSClassConverter);
		encoder._addConverter(TextFieldFormatType.textFieldFormatTypeConverter);
		encoder._addConverter(TextFieldType.textFieldTypeConverter);
		encoder._addConverter(TRCSSType.trCSSTypeConverter);
		encoder._addConverter(TextFieldClass.textFieldClassConverter);
		encoder._addConverter(DMCardinality.cardinalityConverter);
		encoder._addConverter(DMPropertyImplementationType.propertyImplementationTypeConverter);
		encoder._addConverter(DMVisibilityType.visibilityTypeConverter);
		encoder._addConverter(DMEOAdaptorType.adaptorTypeConverter);
		encoder._addConverter(DeleteRuleType.deleteRuleTypeConverter);
		encoder._addConverter(JoinSemanticType.joinSemanticTypeConverter);
		encoder._addConverter(HyperlinkActionType.hyperlinkActionTypeConverter);
		encoder._addConverter(IEControlOperator.controlOperatorConverter);
		encoder._addConverter(FlexoDocFormat.flexoDocFormatConverter);
		encoder._addConverter(FolderType.folderTypeConverter);
		encoder._addConverter(DateFormatType.DateFormatTypeConverter);
		encoder._addConverter(RectangleConverter.instance);
		encoder._addConverter(FlexoProjectFile.converter);
		encoder._addConverter(FlexoVersion.converter);
		encoder._addConverter(CGVersionIdentifier.converter);
		encoder._addConverter(Duration.converter);
		encoder._addConverter(DataBinding.CONVERTER);
		encoder._addConverter(FGEUtils.DEPRECATED_POINT_CONVERTER);
		encoder._addConverter(FGEUtils.DEPRECATED_RECT_POLYLIN_CONVERTER);
	}*/

	/*@Override
	public synchronized String valueForKey(String key) {
		return super.valueForKey(key);
	}

	@Override
	public synchronized void setValueForKey(String valueAsString, String key) {
		super.setValueForKey(valueAsString, key);
	}*/

}
