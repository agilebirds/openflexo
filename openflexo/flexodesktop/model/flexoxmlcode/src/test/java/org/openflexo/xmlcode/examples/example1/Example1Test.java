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
package org.openflexo.xmlcode.examples.example1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.openflexo.xmlcode.Debugging;
import org.openflexo.xmlcode.TestFileFinder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;

import junit.framework.TestCase;

/**
 * <p>
 * Class <code>Example1</code> is intented to show an example of xml
 * coding/decoding scheme
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 * @see XMLMapping
 */
public class Example1Test extends TestCase {

    private static final File exampleModelFile = TestFileFinder.findTestFile("example1/ExampleModel.xml");
    private static final File dataFile = TestFileFinder.findTestFile("example1/ExampleCommand.xml");
    private static final File resultFile = new File(new File(System.getProperty("java.io.tmpdir")),"ResultCommand.xml");
    
    private String xmlData = "";
    private XMLMapping aMapping;
    private Command myCommand;
    private Role onlyForCompilingProcess;

    public Example1Test(String name) 
    {
        super(name);
    }

    @Override
	protected void setUp() throws Exception 
    {
        super.setUp();
        
        Debugging.disableDebug();

        if (!exampleModelFile.exists()) {
            fail("File " + exampleModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
        }

        if (!dataFile.exists()) {
            fail("File " + dataFile.getName() + " doesn't exist. Maybe you have to check your paths ?");
        }
        
        FileInputStream in;
        byte[] buffer;

        try {
            in = new FileInputStream(dataFile);
            buffer = new byte[in.available()];
            in.read(buffer);
            xmlData = new String(buffer);
            in.close();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        XMLCoder.setTransformerFactoryClass("org.apache.xalan.processor.TransformerFactoryImpl");
    }

    @Override
	protected void tearDown() throws Exception 
    {
        super.tearDown();
     }

    public void test1()
    {
        try {
            Object anObject;
            String result;
            FileOutputStream out;

            aMapping = new XMLMapping(exampleModelFile);
            System.out.println("Reading, parsing and getting following model:\n" + aMapping.toString());
            System.out.println("Reading data file:\n" + xmlData);

            anObject = XMLDecoder.decodeObjectWithMapping(xmlData, aMapping);
            System.out.println("Obtaining by parsing string: " + anObject.toString());

            anObject = XMLDecoder.decodeObjectWithMapping(new FileInputStream(dataFile), aMapping);
            System.out.println("Obtaining by parsing stream: " + anObject.toString());

            result = XMLCoder.encodeObjectWithMapping((XMLSerializable) anObject, aMapping);
            System.out.println("Coding to XML and getting " + result);

            out = new FileOutputStream(resultFile);
            XMLCoder.encodeObjectWithMapping((XMLSerializable) anObject, aMapping, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            fail (e.getMessage());
        } 
    }

}
