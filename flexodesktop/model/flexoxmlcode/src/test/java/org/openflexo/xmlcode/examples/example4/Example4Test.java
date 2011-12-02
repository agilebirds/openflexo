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
package org.openflexo.xmlcode.examples.example4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.openflexo.xmlcode.Debugging;
import org.openflexo.xmlcode.TestFileFinder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * <p>
 * Class <code>Example4</code> is intented to show an example of xml coding/decoding scheme
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 * @see XMLMapping
 */
public class Example4Test extends TestCase {

	private static final File exampleModelFile = TestFileFinder.findTestFile("example4/ExampleModel.xml");
	private static final File dataFile = TestFileFinder.findTestFile("example4/ExampleGraph.xml");
	private static final File resultFile = new File(new File(System.getProperty("java.io.tmpdir")), "ResultGraph.xml");

	private String xmlData = "";
	private XMLMapping aMapping;

	public Example4Test(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
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
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test1() {
		try {
			Graph graph;
			String result;
			FileOutputStream out;

			aMapping = new XMLMapping(exampleModelFile);
			System.out.println("Reading, parsing and getting following model:\n" + aMapping.toString());

			GraphBuilder gb = new GraphBuilder();
			graph = (Graph) XMLDecoder.decodeObjectWithMapping(new FileInputStream(dataFile), aMapping, gb);
			System.out.println("Obtaining by parsing stream: " + graph.toString());

			System.out.print(gb.toString());

			result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
			System.out.println("Coding to XML and getting " + result);

			out = new FileOutputStream(resultFile);
			XMLCoder.encodeObjectWithMapping(graph, aMapping, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void test2() {
		try {
			Graph graph;
			String result;
			FileOutputStream out;

			graph = buildNewGraph();
			// Debugging.enableDebug();

			aMapping = new XMLMapping(exampleModelFile);
			System.out.println("Reading, parsing and getting following model:\n" + aMapping.toString());

			result = XMLCoder.encodeObjectWithMapping(graph, aMapping);
			System.out.println("Coding to XML and getting " + result);

			out = new FileOutputStream(resultFile);
			XMLCoder.encodeObjectWithMapping(graph, aMapping, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public static Graph buildNewGraph() {
		GraphBuilder gb = new GraphBuilder();
		Graph newGraph = new Graph(gb);
		Node node1 = new Node(gb);
		Node node2 = new Node(gb);
		Node node3 = new Node(gb);
		Node node4 = new Node(gb);
		Edge edge1 = new Edge(gb, node1, node2);
		Edge edge2 = new Edge(gb, node1, node3);
		Edge edge3 = new Edge(gb, node2, node3);
		Edge edge4 = new Edge(gb, node2, node4);
		Edge edge5 = new Edge(gb, node3, node4);
		newGraph.startNode = node1;
		return newGraph;
	}

}
