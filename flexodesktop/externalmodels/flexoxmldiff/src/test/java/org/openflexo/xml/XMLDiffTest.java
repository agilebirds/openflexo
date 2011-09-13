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
package org.openflexo.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

public class XMLDiffTest {
	
	public static Document docFromFile(File file) throws IOException, SAXException, ParserConfigurationException 
    {

        FileInputStream in = new FileInputStream(file);
        try {
        	SAXBuilder parser = new SAXBuilder();
            return parser.build(in);
        } catch (IOException e) {
            throw e;
        } catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
	
	public static boolean areSame(Document d1, Document d2){
		return areSame(d1.getRootElement(), d2.getRootElement());
	}
	public static boolean areSame(Element e1,Element e2){
		if(!e1.getName().equals(e2.getName())){
			System.err.println("---------------------> "+e1.getName()+" VS "+e2.getName());
			return false;
		}
		if(e1.getAttributes().size()!=e2.getAttributes().size()){
			System.err.println("---------------------> "+e1.getName()+" VS "+e2.getName()+" attributes size");
			return false;
		}
		if(!e1.getText().trim().equals(e2.getText().trim())){
			System.err.println("---------------------> "+e1.getName()+" VS "+e2.getName()+" inner text");
			return false;
		}
		Iterator<Attribute> it = e1.getAttributes().iterator();
		while(it.hasNext()){
			Attribute att = it.next();
			if(!att.getValue().equals(e2.getAttributeValue(att.getName()))){
				System.err.println("---------------------> "+e1.getName()+" VS "+e2.getName()+" attribute value for "+att.getName());
				return false;
			}
		}
		if(e1.getChildren().size()!=e2.getChildren().size()){
			System.err.println("---------------------> "+e1.getName()+" VS "+e2.getName()+" children size");
			return false;
		}
		Iterator<Element> it1 = e1.getChildren().iterator();
		Iterator<Element> it2 = e1.getChildren().iterator();
		while(it1.hasNext()){
			if(!areSame(it1.next(), it2.next()))return false;
		}
		
		return true;
	}
}
