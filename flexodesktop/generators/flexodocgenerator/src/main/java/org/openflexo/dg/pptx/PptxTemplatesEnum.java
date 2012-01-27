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
package org.openflexo.dg.pptx;

/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS Modify to fit the pptx structure
 * 
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PptxTemplatesEnum
{
//	RELS("docx_.rels.vm", "_rels/.rels", false),
//	APP_XML("docx_app.xml.vm", "docProps/app.xml", false),
//	CORE_XML("docx_core.xml.vm", "docProps/core.xml", false),
//	DOCUMENT_XML("docx_document.xml.vm", "word/document.xml", true),
//	DOCUMENT_XML_RELS("docx_document.xml.rels.vm", "word/_rels/document.xml.rels", true),
//	COMMENTS_XML("docx_comments.xml.vm", "word/comments.xml", false),
//	COMMENTS_XML_RELS("docx_comments.xml.rels.vm", "word/_rels/comments.xml.rels", false),
//	ENDNOTES_XML("docx_endnotes.xml.vm", "word/endnotes.xml", false),
//	ENDNOTES_XML_RELS("docx_endnotes.xml.rels.vm", "word/_rels/endnotes.xml.rels", false),
//	FOOTER1_XML("docx_footer1.xml.vm", "word/footer1.xml", false),
//	FOOTER1_XML_RELS("docx_footer1.xml.rels.vm", "word/_rels/footer1.xml.rels", false),
//	FOOTER2_XML("docx_footer2.xml.vm", "word/footer2.xml", false),
//	FOOTER2_XML_RELS("docx_footer2.xml.rels.vm", "word/_rels/footer2.xml.rels", false),
//	FOOTER3_XML("docx_footer3.xml.vm", "word/footer3.xml", false),
//	FOOTER3_XML_RELS("docx_footer3.xml.rels.vm", "word/_rels/footer3.xml.rels", false),
//	FONTTABLE_XML("docx_fontTable.xml.vm", "word/fontTable.xml", false),
//	FONTTABLE_XML_RELS("docx_fontTable.xml.rels.vm", "word/_rels/fontTable.xml.rels", false),
//	FOOTNOTES_XML("docx_footnotes.xml.vm", "word/footnotes.xml", false),
//	FOOTNOTES_XML_RELS("docx_footnotes.xml.rels.vm", "word/_rels/footnotes.xml.rels", false),
//	HEADER1_XML("docx_header1.xml.vm", "word/header1.xml", false),
//	HEADER1_XML_RELS("docx_header1.xml.rels.vm", "word/_rels/header1.xml.rels", false),
//	HEADER2_XML("docx_header2.xml.vm", "word/header2.xml", false),
//	HEADER2_XML_RELS("docx_header2.xml.rels.vm", "word/_rels/header2.xml.rels", false),
//	HEADER3_XML("docx_header3.xml.vm", "word/header3.xml", false),
//	HEADER3_XML_RELS("docx_header3.xml.rels.vm", "word/_rels/header3.xml.rels", false),
//	NUMBERING_XML("docx_numbering.xml.vm", "word/numbering.xml", false),
//	SETTINGS_XML("docx_settings.xml.vm", "word/settings.xml", false),
//	WEBSETTINGS_XML("docx_websettings.xml.vm", "word/webSettings.xml", false),
//	STYLES_XML("docx_styles.xml.vm", "word/styles.xml", false),
//	THEME1_XML("docx_theme1.xml.vm", "word/theme/theme1.xml", false),
//	THEME1_XML_RELS("docx_theme1.xml.rels.vm", "word/theme/_rels/theme1.xml.rels", false),
//	FLEXONAMES_XML("docx_flexonames.xml.vm", "customXml/flexoNames.xml", false),
//	CONTENT_TYPES_XML("docx_content_types.xml.vm", "[Content_Types].xml", false);
	
	
	RELS("pptx_.rels.vm", "_rels/.rels", false),
	APP_XML("pptx_app.xml.vm", "docProps/app.xml", false),
	CORE_XML("pptx_core.xml.vm", "docProps/core.xml", false),
	PRESENTATION_XML("pptx_presentation.xml.vm", "ppt/presentation.xml", false),
	PRESENTATION_XML_RELS("pptx_presentation.xml.rels.vm", "ppt/_rels/presentation.xml.rels", false),
	PRESPROPS_XML("pptx_presProps.xml.vm", "ppt/presProps.xml", false),
	TABLESTYLES_XML("pptx_tableStyles.xml.vm", "ppt/tableStyles.xml", false),
	
	
	VIEWPROPS_XML("pptx_viewProps.xml.vm", "ppt/viewProps.xml.vm", false),
	SLIDELAYOUT_XML("pptx_slideLayout1.xml.vm", "ppt/slideLayouts/slideLayout1.xml.vm", false),
	SLIDELAYOUT_RELS("pptx_slideLayout1.xml.rels.vm", "ppt/slideLayouts/_rels/slideLayout1.xml.rels.vm", false),
	SLIDEMASTER_RELS("pptx_slideMaster1.xml.rels.vm", "ppt/slideMasters/_rels/slideMaster1.xml.rels.vm", false),
	SLIDEMASTER_XML("pptx_slideMaster1.xml.vm", "ppt/slideMasters/slideMaster1.xml.vm", false),
	SLIDE_RELS("pptx_slide1.xml.rels.vm", "ppt/slides/_rels/slide1.xml.rels.vm", false),
	SLIDE_XML("pptx_slide1.xml.vm", "ppt/slides/slide1.xml.vm", true),
	THEME_XML("pptx_theme1.xml.vm", "ppt/theme/theme1.xml.vm", false),
	CONTENT_TYPES_XML("pptx_[Content_Types].xml.vm", "[Content_Types].xml", false);

	private static Map<String, List<PptxTemplatesEnum>> orderedTemplateListGroupedPerGenerator;

	private String templatePath;
	private String filePath;
	private boolean isFullProjectDependent;

	private PptxTemplatesEnum(String templatePath, String filePath, boolean isFullProjectDependent)
	{
		this.templatePath = templatePath;
		this.filePath = filePath;
		this.isFullProjectDependent = isFullProjectDependent;
	}

	public String getTemplatePath()
	{
		return templatePath;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public boolean getIsFullProjectDependent()
	{
		return isFullProjectDependent;
	}

	public void setFullProjectDependent(boolean isFullProjectDependent)
	{
		this.isFullProjectDependent = isFullProjectDependent;
	}
	
	
	public static Map<String, List<PptxTemplatesEnum>> getOrderedTemplateListGroupedPerGenerator()
	{
		if(orderedTemplateListGroupedPerGenerator == null)
		{
			orderedTemplateListGroupedPerGenerator = new HashMap<String, List<PptxTemplatesEnum>>();

			List<PptxTemplatesEnum> orderedTemplateList = new ArrayList<PptxTemplatesEnum>();

			//RELS can be generated alone
			orderedTemplateList.add(RELS);
			orderedTemplateListGroupedPerGenerator.put(RELS.toString(), orderedTemplateList);

			//APP_XML can be generated alone
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(APP_XML);
			orderedTemplateListGroupedPerGenerator.put(APP_XML.toString(), orderedTemplateList);

			//CORE_XML can be generated alone
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(CORE_XML);
			orderedTemplateListGroupedPerGenerator.put(CORE_XML.toString(), orderedTemplateList);

			//CONTENT_TYPES_XML can be generated alone
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(CONTENT_TYPES_XML);
			orderedTemplateListGroupedPerGenerator.put(CONTENT_TYPES_XML.toString(), orderedTemplateList);

			//All files here are interdependent. !!!! Order is important
			//Main cause of this interdependency is that comments, numberings, endnotes and footnotes contents are dependents of other file contents
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(PRESENTATION_XML);
			orderedTemplateList.add(PRESENTATION_XML_RELS);
			orderedTemplateList.add(PRESPROPS_XML);
			orderedTemplateList.add(SLIDE_XML);
			orderedTemplateList.add(SLIDE_RELS);
			orderedTemplateList.add(SLIDEMASTER_XML);
			orderedTemplateList.add(SLIDEMASTER_RELS);
			orderedTemplateList.add(SLIDELAYOUT_XML);
			orderedTemplateList.add(SLIDELAYOUT_RELS);
			orderedTemplateList.add(VIEWPROPS_XML);
			orderedTemplateListGroupedPerGenerator.put(PRESENTATION_XML.toString(), orderedTemplateList);

			//STYLES_XML can be generated alone
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(TABLESTYLES_XML);
			orderedTemplateListGroupedPerGenerator.put(TABLESTYLES_XML.toString(), orderedTemplateList);

			//THEME1_XML is only dependent with its RELS can be generated alone
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(THEME_XML);
			orderedTemplateListGroupedPerGenerator.put(THEME_XML.toString(), orderedTemplateList);
		}

		return orderedTemplateListGroupedPerGenerator;
	}
	

//	public static Map<String, List<PptxTemplatesEnum>> getOrderedTemplateListGroupedPerGenerator()
//	{
//		if(orderedTemplateListGroupedPerGenerator == null)
//		{
//			orderedTemplateListGroupedPerGenerator = new HashMap<String, List<PptxTemplatesEnum>>();
//
//			List<PptxTemplatesEnum> orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//
//			//RELS can be generated alone
//			orderedTemplateList.add(RELS);
//			orderedTemplateListGroupedPerGenerator.put(RELS.toString(), orderedTemplateList);
//
//			//APP_XML can be generated alone
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(APP_XML);
//			orderedTemplateListGroupedPerGenerator.put(APP_XML.toString(), orderedTemplateList);
//
//			//CORE_XML can be generated alone
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(CORE_XML);
//			orderedTemplateListGroupedPerGenerator.put(CORE_XML.toString(), orderedTemplateList);
//
//			//CONTENT_TYPES_XML can be generated alone
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(CONTENT_TYPES_XML);
//			orderedTemplateListGroupedPerGenerator.put(CONTENT_TYPES_XML.toString(), orderedTemplateList);
//
//			//All files here are interdependent. !!!! Order is important
//			//Main cause of this interdependency is that comments, numberings, endnotes and footnotes contents are dependents of other file contents
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(DOCUMENT_XML);
//			orderedTemplateList.add(DOCUMENT_XML_RELS);
//			orderedTemplateList.add(HEADER1_XML);
//			orderedTemplateList.add(HEADER1_XML_RELS);
//			orderedTemplateList.add(HEADER2_XML);
//			orderedTemplateList.add(HEADER2_XML_RELS);
//			orderedTemplateList.add(HEADER3_XML);
//			orderedTemplateList.add(HEADER3_XML_RELS);
//			orderedTemplateList.add(FOOTER1_XML);
//			orderedTemplateList.add(FOOTER1_XML_RELS);
//			orderedTemplateList.add(FOOTER2_XML);
//			orderedTemplateList.add(FOOTER2_XML_RELS);
//			orderedTemplateList.add(FOOTER3_XML);
//			orderedTemplateList.add(FOOTER3_XML_RELS);
//			orderedTemplateList.add(COMMENTS_XML);
//			orderedTemplateList.add(COMMENTS_XML_RELS);
//			orderedTemplateList.add(FOOTNOTES_XML);
//			orderedTemplateList.add(FOOTNOTES_XML_RELS);
//			orderedTemplateList.add(ENDNOTES_XML);
//			orderedTemplateList.add(ENDNOTES_XML_RELS);
//			orderedTemplateList.add(NUMBERING_XML);
//			orderedTemplateList.add(FONTTABLE_XML);
//			orderedTemplateList.add(FONTTABLE_XML_RELS);
//			orderedTemplateList.add(FLEXONAMES_XML);
//			orderedTemplateListGroupedPerGenerator.put(DOCUMENT_XML.toString(), orderedTemplateList);
//
//			//SETTINGS_XML can be generated alone
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(SETTINGS_XML);
//			orderedTemplateListGroupedPerGenerator.put(SETTINGS_XML.toString(), orderedTemplateList);
//
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(WEBSETTINGS_XML);
//			orderedTemplateListGroupedPerGenerator.put(WEBSETTINGS_XML.toString(), orderedTemplateList);
//
//			//STYLES_XML can be generated alone
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(STYLES_XML);
//			orderedTemplateListGroupedPerGenerator.put(STYLES_XML.toString(), orderedTemplateList);
//
//			//THEME1_XML is only dependent with its RELS can be generated alone
//			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
//			orderedTemplateList.add(THEME1_XML);
//			orderedTemplateList.add(THEME1_XML_RELS);
//			orderedTemplateListGroupedPerGenerator.put(THEME1_XML.toString(), orderedTemplateList);
//		}
//
//		return orderedTemplateListGroupedPerGenerator;
//	}
}
