HEADER_FOOTER: CIPopupHeaderFooter {
	title = "Edit meta data";
}

FORM: WOForm {
	action = formAction;
	name = formName;
}

HIDDEN_FORM: WOHiddenField {
	name = hiddenFieldName;
	value = hiddenFieldValue;
}

CONDITIONAL_VALIDATION: WOConditional {
	condition = hasValidationMessages;
}

VALIDATION_MESSAGE: WOString {
	value = validationMessages;
	escapeHTML = false;
}

Textarea1888488: WOText {
	value = metaData.description;
	class = "DLExtensible";
	rows = 4;
}

Textarea1888502: WOText {
	value = metaData.instruction;
	class = "DLExtensible";
	rows = 4;
}

String562901: WOString {
	value = metaData.metadataID;
	valueWhenEmpty = "&nbsp;";
}

authoringDeadline562969: WDLDateTextField {
	value = writingDeadline;
	class = "DLNormal";
	validationDic = contributionTaskParentTaskMasterMetaDataWritingDeadline_validationDic;
	validationLabel = "authoringDeadline";
	name = "authoringDeadline562969";
	dateFormat = "DATE_APPL";
	formatter = dateFormatter;
}

BUTTON_562970: WDLButton {
	prefix = page.css;
	imageName = "Icon_Calendar";
	showLoadingLayer = false;
}

calendar_562970: WDLDateAssistantLink {
	fieldName = "authoringDeadline562969";
	}
LINK_FOR_BUTTON_calendar_562970: WDLMultiSubmitLink {
	formName = formName;
	value = "calendar_562970";
	fieldName = hiddenFieldName;
}

IsTaskAvailable: WOConditional{
	condition = hasContributionTask;
}

DropDown_562940: WOPopUpButton {
	selection = contributionTask.worker;
	list = listForDropDown562940;
	item = item_CIEmployee_562940;
	displayString = item_CIEmployee_562940.fullName;
	noSelectionString = "NOBODY";
}

reviewingDeadline563000: WDLDateTextField {
	value = metaData.primaryMasterMetaData.reviewingDeadline;
	class = "DLNormal";
	validationDic = contributionTaskParentTaskMasterMetaDataReviewingDeadline_validationDic;
	validationLabel = "reviewingDeadline";
	name = "reviewingDeadline563000";
	dateFormat = "DATE_APPL";
	formatter = dateFormatter;
}

BUTTON_563001: WDLButton {
	prefix = page.css;
	imageName = "Icon_Calendar";
	showLoadingLayer = false;
}

calendar_563001: WDLDateAssistantLink {
	fieldName = "reviewingDeadline563000";
	}



String1888517: WOString {
	value = "not available in alpha release";
	valueWhenEmpty = "&nbsp;";
}

BUTTON_1888519: WDLButton {
	prefix = page.css;
	imageName = "Icon_PreviewFile";
	showLoadingLayer = false;
}

previewFile_1888519: KeywordChooserLink {
	confirmValue = "GEO-";
	hiddenFieldName = hiddenFieldName;
	showGeographic = true;
}

String1888508: WOString {
	value = "not available in alpha release";
	valueWhenEmpty = "&nbsp;";
}

BUTTON_1888510: WDLButton {
	prefix = page.css;
	imageName = "Icon_PreviewFile";
	showLoadingLayer = false;
}

previewFile_1888510: KeywordChooserLink {
	confirmValue = "KW-";
	hiddenFieldName = hiddenFieldName;
	showServiceThesaurus = true;
	showGeographicThesaurus = false;
	showOnlyFrontOfficeThesaurus=false;
	showOthers=true;
}

CheckBox_563022: WOCheckBox {

	name = checkbox_563022Name;
	checked = metaData.isSearchableBoolean;
	
}

CheckBox_2540126: WOCheckBox {
	checked = checkbox_2540126;
	name = checkbox_2540126Name;
}

String2540127: WOString {
	value = delivrableType2540127;
	valueWhenEmpty = "&nbsp;";
}

CheckBox_2540142: WOCheckBox {
	checked = checkbox_2540142;
	name = checkbox_2540142Name;
}

String2540143: WOString {
	value = delivrableType2540143;
	valueWhenEmpty = "&nbsp;";
}

CheckBox_2540158: WOCheckBox {
	checked = checkbox_2540158;
	name = checkbox_2540158Name;
}

String2540159: WOString {
	value = delivrableType2540159;
	valueWhenEmpty = "&nbsp;";
}

CheckBox_2540174: WOCheckBox {
	checked = checkbox_2540174;
	name = checkbox_2540174Name;
}

String2540175: WOString {
	value = delivrableType2540175;
	valueWhenEmpty = "&nbsp;";
}

DelivrableTypeName: WOString {
	value = metaData.primaryMasterMetaData.delivrable.name;
	valueWhenEmpty = "&nbsp;";
}


BUTTON_563032: WDLButton {
	prefix = page.css;
	imageName = "Button_Save";
	showLoadingLayer = false;
}

LINK_FOR_BUTTON_save_563032: WDLMultiSubmitLink {
	formName = formName;
	value = "save_563032";
	fieldName = hiddenFieldName;
}

BUTTON_563033: WDLButton {
	prefix = page.css;
	imageName = "Button_Cancel";
	showLoadingLayer = false;
}

