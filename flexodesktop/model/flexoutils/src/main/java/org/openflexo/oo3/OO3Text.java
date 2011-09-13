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

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openflexo.oo3.OO3Attachments.OO3Attachment;
import org.openflexo.xmlcode.XMLSerializable;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3Text implements XMLSerializable
{

    public Vector<Paragraph> paragraphs;

    public OO3Text()
    {
        super();
        paragraphs = new Vector<Paragraph>();
    }

    public OO3Text(String firstParagraphText)
    {
        this();
        addParagraph(firstParagraphText);
    }

    public void addParagraph(Paragraph paragraph)
    {
        paragraphs.add(paragraph);
    }

    public void addParagraph(String paragraph)
    {
        paragraphs.add(new Paragraph(paragraph));
    }

    public void setText(String text)
    {
        if (text != null) {
            StringTokenizer st = new StringTokenizer(text, "\n");
            while (st.hasMoreTokens()) {
                addParagraph(st.nextToken());
            }
        }
    }

    public OO3Attachment addAttachment(OO3Document document, File file, String name)
    {
        OO3Attachment attachment = document.registerAttachment(file, name);
        paragraphs.add(new Paragraph(attachment));
        return attachment;
    }

    public static class Paragraph implements XMLSerializable
    {
        public Run run;

        public Paragraph()
        {
            super();
        }

        public Paragraph(String paragraphText)
        {
            this();
            if (paragraphText != null) {
                run = new Run(paragraphText);
            }
        }

        public Paragraph(Run.Lit.Cell paragraphCell)
        {
            this();
            run = new Run(paragraphCell);
        }

        public Paragraph(OO3Attachment attachment)
        {
            this(new Run.Lit.Cell(attachment));
        }

        public static class Run implements XMLSerializable
        {
            public Lit lit;

            public Run()
            {
                super();
                lit = null;
            }

            public Run(String text)
            {
                this();
                lit = new Lit(text);
            }

            public Run(Lit.Cell cell)
            {
                this();
                lit = new Lit(cell);
            }

            public static class Lit implements XMLSerializable
            {
                public String text;

                public Cell cell;

                public Lit()
                {
                    super();
                }

                public Lit(String t)
                {
                    this();
                    this.text = t;
                }

                public Lit(Cell c)
                {
                    this();
                    this.cell = c;
                }

                public static class Cell implements XMLSerializable
                {
                    public String variable;

                    public String refid;

                    public String name;

                    public String expanded;

                    public Cell()
                    {
                        super();
                    }

                    public Cell(String v)
                    {
                        this();
                        this.variable = v;
                    }

                    public Cell(OO3Attachments.OO3Attachment attachment)
                    {
                        this();
                        refid = attachment.id;
                        name = attachment.name;
                        expanded = "yes";
                    }
                }

            }

        }

    }

}
