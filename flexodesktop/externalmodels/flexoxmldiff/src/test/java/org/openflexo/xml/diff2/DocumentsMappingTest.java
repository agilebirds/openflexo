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
package org.openflexo.xml.diff2;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xml.XMLDiffTest;
import org.openflexo.xml.diff2.DocumentsMapping;
import org.xml.sax.SAXException;


public class DocumentsMappingTest extends XMLDiffTest{

	private static final Logger logger = Logger.getLogger(DocumentsMappingTest.class.getPackage()
            .getName());
	
	
	public void testTableMoved(){
		logger.info("Start Table moved test");
		buildMapping("TableMoved/Screen1A.woxml","TableMoved/Screen1B.woxml");
	}
	public void testOneAddOneRemoved(){
		logger.info("Start Operation add and remove test");
		buildMapping("OneOpAddOneOpRemoved/MergeA.xml","OneOpAddOneOpRemoved/MergeB.xml");
	}
	
	private static DocumentsMapping buildMapping(String fileNameSource, String fileNameTarget){
		try {
			Document src = docFromFile(new FileResource(fileNameSource)); 
			Document target = docFromFile(new FileResource(fileNameTarget));
			return new DocumentsMapping(src,target);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
