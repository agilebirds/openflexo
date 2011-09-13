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
package org.openflexo.xmlcode.examples.example5;

import java.io.File;
import java.io.FileInputStream;

import org.openflexo.xmlcode.Debugging;
import org.openflexo.xmlcode.TestFileFinder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;

import junit.framework.TestCase;

/**
 * <p>
 * Class <code>Example4</code> is intented to show an example of xml
 * coding/decoding scheme
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 * @see XMLMapping
 */
public class Example5Test extends TestCase {

    private static final boolean LOG = true;
    
    private static final String path = "example5/";
    private static final File exampleModel1File = TestFileFinder.findTestFile(path+"ExampleModel1.xml");
    private static final File exampleModel2File = TestFileFinder.findTestFile(path+"ExampleModel2.xml");
    private static final File exampleModel3File = TestFileFinder.findTestFile(path+"ExampleModel3.xml");
    private static final File exampleModel4File = TestFileFinder.findTestFile(path+"ExampleModel4.xml");
    private static final File dataFile1 = TestFileFinder.findTestFile(path+"ExampleGraph1.xml");
    private static final File dataFile2 = TestFileFinder.findTestFile(path+"ExampleGraph2.xml");
    private static final File dataFile3 = TestFileFinder.findTestFile(path+"ExampleGraph3.xml");
    
    
    
    public Example5Test(String name) 
    {
        super(name);
    }

    @Override
	protected void setUp() throws Exception 
    {
        super.setUp();
        
        Debugging.disableDebug();

        XMLCoder.setTransformerFactoryClass("org.apache.xalan.processor.TransformerFactoryImpl");
    }

    @Override
	protected void tearDown() throws Exception 
    {
        super.tearDown();
     }

    private String readDataFromFile(File dataFile)
    {
        String xmlData = "";

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
        
        return xmlData;
    }
    
    public void test1()
    {
        Graph graph = buildNewGraph();
        if (LOG) System.out.println("Build new graph:\n" + graph);
        
        try {
            
            System.out.println("\nTEST1: programmatically build a graph and serialize it (DEEP_FIRST mode, no contexts) ");
                        
            XMLMapping aMapping = new XMLMapping(exampleModel1File);
            if (LOG) System.out.println("TEST1: Reading, parsing and getting following model:\n" + aMapping.toString());
            
            synchronized(this) {
                wait(1000);
            }
            
            long start = System.currentTimeMillis();
            String result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
            long stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST1: Coding to XML and getting " + result);
            System.out.println ("TEST1: serialization took "+(stop-start)+" ms");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public void test2()
    {
        Graph graph;
        
        try {
            
            System.out.println("\nTEST2: deserialize a graph from file and re-serialize it (DEEP_FIRST mode, no contexts) ");

            XMLMapping aMapping = new XMLMapping(exampleModel1File);
            if (LOG) System.out.println("TEST2: Reading, parsing and getting following model:\n" + aMapping.toString());
            
            long start = System.currentTimeMillis();
            graph = (Graph) XMLDecoder.decodeObjectWithMapping(new FileInputStream(dataFile1), aMapping);
            long stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST2: Obtaining by parsing stream: \n" + graph.toString());
            System.out.println ("TEST2: deserialization took "+(stop-start)+" ms");

            start = System.currentTimeMillis();
            String result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
            stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST2: Coding to XML and getting " + result);
            System.out.println ("TEST2: serialization took "+(stop-start)+" ms");
           
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void test3()
    {
        Graph graph;
        
        try {
            
            System.out.println("\nTEST3: deserialize a graph from file and re-serialize it (DEEP_FIRST mode, with contexts) ");

            XMLMapping aMapping = new XMLMapping(exampleModel2File);
            if (LOG) System.out.println("TEST3: Reading, parsing and getting following model:\n" + aMapping.toString());
            
            long start = System.currentTimeMillis();
            graph = (Graph) XMLDecoder.decodeObjectWithMapping(new FileInputStream(dataFile1), aMapping);
            long stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST3: Obtaining by parsing stream: \n" + graph.toString());
            System.out.println ("TEST3: deserialization took "+(stop-start)+" ms");

            start = System.currentTimeMillis();
            String result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
            stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST3: Coding to XML and getting " + result);
            System.out.println ("TEST3: serialization took "+(stop-start)+" ms");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void test4()
    {
        Graph graph = buildNewGraph();
        if (LOG) System.out.println("Build new graph:\n" + graph);
        
        try {
            
            System.out.println("\nTEST4: programmatically build a graph and serialize it (PSEUDO_TREE mode) ");

            XMLMapping aMapping = new XMLMapping(exampleModel3File);
            if (LOG) System.out.println("TEST4: Reading, parsing and getting following model:\n" + aMapping.toString());
            
            long start = System.currentTimeMillis();
            String result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
            long stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST4: Coding to XML and getting " + result);
            System.out.println ("TEST4: serialization took "+(stop-start)+" ms");

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public void test5()
    {
        Graph graph = buildNewGraph();
        if (LOG) System.out.println("Build new graph:\n" + graph);
        
        try {
            
            System.out.println("\nTEST5: programmatically build a graph and serialize it (ORDERED_PSEUDO_TREE mode) ");

            XMLMapping aMapping = new XMLMapping(exampleModel4File);
            if (LOG) System.out.println("TEST5: Reading, parsing and getting following model:\n" + aMapping.toString());
            
            long start = System.currentTimeMillis();
            String result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
            long stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST5: Coding to XML and getting " + result);
            System.out.println ("TEST5: serialization took "+(stop-start)+" ms");

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public void test6()
    {
        Graph graph;
        
        try {
            
            System.out.println("\nTEST6: deserialize a graph from file and re-serialize it (PSEUDO_TREE mode) ");

            XMLMapping aMapping = new XMLMapping(exampleModel3File);
            if (LOG) System.out.println("TEST6: Reading, parsing and getting following model:\n" + aMapping.toString());
            
            long start = System.currentTimeMillis();
            graph = (Graph) XMLDecoder.decodeObjectWithMapping(new FileInputStream(dataFile2), aMapping);
            long stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST6: Obtaining by parsing stream: \n" + graph.toString());
            System.out.println ("TEST6: deserialization took "+(stop-start)+" ms");

            start = System.currentTimeMillis();
            String result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
            stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST6: Coding to XML and getting " + result);
            System.out.println ("TEST6: serialization took "+(stop-start)+" ms");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void test7()
    {
        Graph graph;
        
        try {
            
            System.out.println("\nTEST7: deserialize a graph from file and re-serialize it (ORDERED_PSEUDO_TREE mode) ");

            XMLMapping aMapping = new XMLMapping(exampleModel4File);
            if (LOG) System.out.println("TEST7: Reading, parsing and getting following model:\n" + aMapping.toString());
            
            long start = System.currentTimeMillis();
            graph = (Graph) XMLDecoder.decodeObjectWithMapping(new FileInputStream(dataFile3), aMapping);
            long stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST7: Obtaining by parsing stream: \n" + graph.toString());
            System.out.println ("TEST7: deserialization took "+(stop-start)+" ms");

            start = System.currentTimeMillis();
            String result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
            stop = System.currentTimeMillis();
            if (LOG) System.out.println("TEST7: Coding to XML and getting " + result);
            System.out.println ("TEST7: serialization took "+(stop-start)+" ms");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    public static Graph buildNewGraph()
    {
        Graph newGraph = new Graph();
        Node node1 = new Node(1,newGraph);
        Node node2 = new Node(2,newGraph);
        Node node3 = new Node(3,newGraph);
        Node node4 = new Node(4,newGraph);
        Node node5 = new Node(5,newGraph);
        PreCondition pre6 = new PreCondition(6,node2);
        PreCondition pre7 = new PreCondition(7,node3);
        PreCondition pre8 = new PreCondition(8,node4);
        PreCondition pre9 = new PreCondition(9,node5);
        PreCondition pre10 = new PreCondition(10,node5);
        Edge1 edge11 = new Edge1(11,node1,pre6);
        Edge2 edge12 = new Edge2(12,node1,pre7);
        Edge2 edge13 = new Edge2(13,node1,pre8);
        Edge1 edge14 = new Edge1(14,node1,pre8);
        Edge1 edge15 = new Edge1(15,node2,pre9);
        Edge1 edge16 = new Edge1(16,node3,pre10);
        Edge2 edge17 = new Edge2(17,node4,pre10);
        return newGraph;
    }

    
}
