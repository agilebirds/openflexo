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
package org.openflexo.oo3;

import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3Settings implements XMLSerializable
{
    public PageAdornment pageAdornment = new PageAdornment();

    public PrintInfo printInfo = new PrintInfo();

    public static class PageAdornment implements XMLSerializable
    {
        public FirstPageHeaders firstPageHeaders = new FirstPageHeaders();

        public static class FirstPageHeaders implements XMLSerializable
        {
            public String isActive = "yes";

            public Header header = new Header();

            public static class Header implements XMLSerializable
            {
                public String location;

                public OO3Text text;

                public Header()
                {
                    super();
                    location = "center";
                    text = new OO3Text();
                    text.addParagraph(new OO3Text.Paragraph(new OO3Text.Paragraph.Run.Lit.Cell("OOSectionTitleVariableIdentifier")));
                }
            }

        }

    }

    public static class PrintInfo implements XMLSerializable
    {
        public Vector<PrintInfoKey> printInfoKeys;

        public PrintInfo()
        {
            super();
            printInfoKeys = new Vector<PrintInfoKey>();
            printInfoKeys.add(new PrintInfoKey("OOScaleDocumentToFitPageWidth", "boolean", "true"));
        }

        public static class PrintInfoKey implements XMLSerializable
        {
            public String name;

            public String type;

            public String value;

            public PrintInfoKey()
            {
                super();
            }

            public PrintInfoKey(String n, String t, String v)
            {
                this();
                this.name = n;
                this.type = t;
                this.value = v;
            }
        }

    }

}
