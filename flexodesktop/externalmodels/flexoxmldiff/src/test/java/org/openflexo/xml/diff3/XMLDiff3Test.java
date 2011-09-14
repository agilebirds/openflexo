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
package org.openflexo.xml.diff3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xml.XMLDiffTest;
import org.openflexo.xml.diff3.XMLDiff3;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;


public class XMLDiff3Test extends TestCase{

	private static final Logger logger = Logger.getLogger(XMLDiff3Test.class.getPackage()
            .getName());
	
	public void testTableMoved(){
		logger.info("Start Diff3 Table moved test");
		XMLDiff3 diff3 = buildDiff3("TableMoved/Screen1A.woxml","TableMoved/Screen1A.woxml","TableMoved/Screen1B.woxml",new FlexoXMLMappings().getIEMapping());
		//print(diff3.getMergedDocument(), System.out);
	}
	public void testBlocMove() throws SAXException, ParserConfigurationException, Exception{
		logger.info("Start Diff3 Bloc move test");
		XMLDiff3 diff3 = buildDiff3("BlocMove/Screen1Base.woxml","BlocMove/Screen1A.woxml","BlocMove/Screen1B.woxml",new FlexoXMLMappings().getIEMapping());
		//print(diff3.getMergedDocument(), System.out);
		assertDocumentMatchContentOfFile(diff3.getMergedDocument(),"BlocMove/ExpectedResults.woxml");
	}
	
	public void testSwitch() throws IOException, SAXException, ParserConfigurationException{
		logger.info("Start Diff3 Switch test");
		XMLDiff3 diff3 = buildDiff3("SwitchIEWidget/Base.woxml","SwitchIEWidget/UserA.woxml","SwitchIEWidget/UserB.woxml",new FlexoXMLMappings().getIEMapping());
		//print(diff3.getMergedDocument(), System.out);
		assertDocumentEquals(diff3.getMergedDocument(),"SwitchIEWidget/Results.woxml");
	}
	
	public void testBlocTitleConflict(){
		logger.info("Start Diff3 Bloc title conflict test");
		XMLDiff3 diff3 = buildDiff3("BlocTitleConflict/Screen1Base.woxml","BlocTitleConflict/Screen1A.woxml","BlocTitleConflict/Screen1B.woxml",new FlexoXMLMappings().getIEMapping());
		//print(diff3.getMergedDocument(), System.out);
	}
	
	public void testTableInBlocConflict(){
		logger.info("Start TableInBloc conflict test");
		XMLDiff3 diff3 = buildDiff3("TableInBlocConflict/Screen1Base.woxml","TableInBlocConflict/Screen1A.woxml","TableInBlocConflict/Screen1B.woxml",new FlexoXMLMappings().getIEMapping());
		//print(diff3.getMergedDocument(), System.out);
	}
	private void assertDocumentMatchContentOfFile(Document doc,String filePath) throws Exception, SAXException, ParserConfigurationException{
		assertDocumentEquals(doc,filePath);
		String expectedResult = readTextFile(new FileResource(filePath));
		String effectiveResult = getStringRepresentation(doc);
		StringTokenizer t1 = new StringTokenizer(expectedResult.trim(),System.getProperty("line.separator"),false);
		StringTokenizer t2 = new StringTokenizer(effectiveResult.trim(),"\n",false);
		while(t1.hasMoreTokens()){
			assertEquals(t1.nextToken().trim(), t2.nextToken().trim());
		}
	}
	
	private void assertDocumentEquals(Document doc,String filePath) throws IOException, SAXException, ParserConfigurationException{
		Document expectedResult = XMLDiffTest.docFromFile(new FileResource(filePath));
		assertTrue(XMLDiffTest.areSame(expectedResult, doc));
	}
	
	public static String readTextFile(File file) {
        StringBuffer result = new StringBuffer();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            char[] buffer = new char[16384];
            int count;
            while ((count = isr.read(buffer)) >= 0) {
                result.append(buffer, 0, count);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (isr != null) try {
                isr.close();
            }
            catch (IOException e1) {
                logger.severe(e1.getMessage());
                e1.printStackTrace();
            }
        }
        return result.toString();
    }
	
	protected String getStringRepresentation(Document doc){
		StringWriter writer = new StringWriter();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputter.output(doc, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return writer.toString();
	}
	private static XMLDiff3 buildDiff3(String fileNameBase,String fileNameSource, String fileNameTarget, XMLMapping mapping){
		try {
			Document base = XMLDiffTest.docFromFile(new FileResource(fileNameBase));
			Document src = XMLDiffTest.docFromFile(new FileResource(fileNameSource)); 
			Document target = XMLDiffTest.docFromFile(new FileResource(fileNameTarget));
			return new XMLDiff3(base,src,target,mapping,null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void print(Document doc, OutputStream aWriter) 
    throws InvalidObjectSpecificationException, InvalidModelException
    {

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputter.output(doc, aWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
