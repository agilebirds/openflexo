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

import org.openflexo.foundation.ptoc.PSlideLayout;

enum PptxTemplateType{
	SLIDE_RELS,
	SLIDE,
	SLIDELAYOUT,
	PRESENTATION,
	OTHERS,
	
}

public enum PptxTemplatesEnum
{

	
	RELS("pptx_.rels.vm", "_rels/.rels", false,PptxTemplateType.PRESENTATION),
	APP_XML("pptx_app.xml.vm", "docProps/app.xml", false,PptxTemplateType.OTHERS),
	CORE_XML("pptx_core.xml.vm", "docProps/core.xml", false,PptxTemplateType.OTHERS),
	PRESENTATION_XML("pptx_presentation.xml.vm", "ppt/presentation.xml", false,PptxTemplateType.PRESENTATION),
	PRESENTATION_XML_RELS("pptx_presentation.xml.rels.vm", "ppt/_rels/presentation.xml.rels", false,PptxTemplateType.OTHERS),
	PRESPROPS_XML("pptx_presProps.xml.vm", "ppt/presProps.xml", false,PptxTemplateType.OTHERS),
	TABLESTYLES_XML("pptx_tableStyles.xml.vm", "ppt/tableStyles.xml", false,PptxTemplateType.OTHERS),
	
	
	VIEWPROPS_XML("pptx_viewProps.xml.vm", "ppt/viewProps.xml", false,PptxTemplateType.OTHERS),
	SLIDELAYOUT_XML_TITLE_ONLY("pptx_title_only_slideLayout.xml.vm", "ppt/slideLayouts/slideLayout1.xml", false,PptxTemplateType.OTHERS),
	SLIDELAYOUT_RELS_TITLE_ONLY("pptx_slideLayout1.xml.rels.vm", "ppt/slideLayouts/_rels/slideLayout1.xml.rels", false,PptxTemplateType.OTHERS),
	SLIDELAYOUT_XML_TITLE_CONTENT("pptx_title_content_slideLayout.xml.vm", "ppt/slideLayouts/slideLayout2.xml", false,PptxTemplateType.OTHERS),
	SLIDELAYOUT_RELS_TITLE_CONTENT("pptx_slideLayout1.xml.rels.vm", "ppt/slideLayouts/_rels/slideLayout2.xml.rels", false,PptxTemplateType.OTHERS),
	SLIDELAYOUT_XML_TITLE_IMAGE("pptx_title_content_image_slideLayout.xml.vm", "ppt/slideLayouts/slideLayout3.xml", false,PptxTemplateType.OTHERS),
	SLIDELAYOUT_RELS_TITLE_IMAGE("pptx_slideLayout1.xml.rels.vm", "ppt/slideLayouts/_rels/slideLayout3.xml.rels", false,PptxTemplateType.OTHERS),
	
	SLIDEMASTER_RELS("pptx_slideMaster1.xml.rels.vm", "ppt/slideMasters/_rels/slideMaster1.xml.rels", false,PptxTemplateType.OTHERS),
	SLIDEMASTER_XML("pptx_slideMaster1.xml.vm", "ppt/slideMasters/slideMaster1.xml", false,PptxTemplateType.OTHERS),
	SLIDE_RELS("pptx_slide1.xml.rels.vm", "ppt/slides/_rels/slide", false,PptxTemplateType.SLIDE_RELS),
	SLIDE_XML("pptx_slide1.xml.vm", "ppt/slides/slide", true,PptxTemplateType.SLIDE),
	THEME_XML("pptx_theme1.xml.vm", "ppt/theme/theme1.xml", false,PptxTemplateType.OTHERS),
	CONTENT_TYPES_XML("pptx_[Content_Types].xml.vm", "[Content_Types].xml", false,PptxTemplateType.OTHERS);

	private static Map<String, List<PptxTemplatesEnum>> orderedTemplateListGroupedPerGenerator;

	private String templatePath;
	private String filePath;
	private boolean isFullProjectDependent;
	private PptxTemplateType templateType;

	private PptxTemplatesEnum(String templatePath, String filePath, boolean isFullProjectDependent, PptxTemplateType type )
	{
		this.templatePath = templatePath;
		this.filePath = filePath;
		this.isFullProjectDependent = isFullProjectDependent;
		this.templateType = type;
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
	
	
	
	public PptxTemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(PptxTemplateType templateType) {
		this.templateType = templateType;
	}
	//TODO_MOS
	/**
	 * this method is temporarily used to get  the layout template corresponding to an element from
	 * the PSlideLayout Enumeration.
	 * @param slideLayout
	 * @return
	 */
	public static PptxTemplatesEnum getTemplateLayoutForLayout(PSlideLayout slideLayout){
		if(slideLayout == PSlideLayout.TitleAndContent)
			return PptxTemplatesEnum.SLIDELAYOUT_XML_TITLE_CONTENT;
		else if(slideLayout == PSlideLayout.TitleOnly)
			return PptxTemplatesEnum.SLIDELAYOUT_XML_TITLE_ONLY;
		else if(slideLayout == PSlideLayout.TitleImageAndDescription)
			return PptxTemplatesEnum.SLIDELAYOUT_XML_TITLE_IMAGE;
		else return null;
	}

	public boolean isSlide() {
		return getTemplateType()==PptxTemplateType.SLIDE;
	}
	
	public boolean isSlideRels(){
		return getTemplateType()==PptxTemplateType.SLIDE_RELS;
	}
	
	public boolean isPresentation(){
		return getTemplateType()==PptxTemplateType.PRESENTATION;
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

			
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(PRESENTATION_XML);
			orderedTemplateList.add(PRESENTATION_XML_RELS);
			orderedTemplateList.add(PRESPROPS_XML);
			orderedTemplateList.add(SLIDEMASTER_XML);
			orderedTemplateList.add(SLIDEMASTER_RELS);
			orderedTemplateList.add(SLIDELAYOUT_XML_TITLE_ONLY);
			orderedTemplateList.add(SLIDELAYOUT_RELS_TITLE_ONLY);
			orderedTemplateList.add(SLIDELAYOUT_XML_TITLE_IMAGE);
			orderedTemplateList.add(SLIDELAYOUT_RELS_TITLE_IMAGE);
			orderedTemplateList.add(SLIDELAYOUT_XML_TITLE_CONTENT);
			orderedTemplateList.add(SLIDELAYOUT_RELS_TITLE_CONTENT);
			orderedTemplateList.add(VIEWPROPS_XML);
			orderedTemplateListGroupedPerGenerator.put(PRESENTATION_XML.toString(), orderedTemplateList);
			
			//PSlide dependent templates must be generated using a DGPptxGenerator for every group of this template type
			orderedTemplateList = new ArrayList<PptxTemplatesEnum>();
			orderedTemplateList.add(SLIDE_XML);
			orderedTemplateList.add(SLIDE_RELS);
			orderedTemplateListGroupedPerGenerator.put(SLIDE_XML.toString(), orderedTemplateList);
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
	
}
