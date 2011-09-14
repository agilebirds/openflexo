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
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.DocType;
import org.openflexo.oo3.OO3Attachments.OO3Attachment;
import org.openflexo.oo3.OO3NamedStyles.OO3NamedStyle;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class OO3Document implements XMLSerializable
{

    private static final Logger logger = Logger.getLogger(OO3Document.class.getPackage().getName());

    public URL xmlns;

    public OO3StyleAttributeRegistry styleAttributeRegistry;

    public OO3NamedStyles namedStyles;

    public OO3Attachments attachments;

    public OO3Settings settings;

    public OO3Editor editor;

    public OO3Columns columns;

    public OO3DocumentContents contents;

    public static DocType getDocType()
    {
        return new DocType("outline", "-//omnigroup.com//DTD OUTLINE 3.0//EN", "http://www.omnigroup.com/namespace/OmniOutliner/xmloutline-v3.dtd");
    }

    public OO3Document()
    {
        super();
        try {
            xmlns = new URL("http://www.omnigroup.com/namespace/OmniOutliner/v3");
        } catch (MalformedURLException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
            e.printStackTrace();
        }
        styleAttributeRegistry = new OO3StyleAttributeRegistry();
        namedStyles = new OO3NamedStyles();
        settings = new OO3Settings();
        editor = new OO3Editor();
        columns = new OO3Columns();
        contents = new OO3DocumentContents();
        // test();
    }

    public OO3Attachment registerAttachment(File file, String name)
    {
        if (attachments == null) {
            attachments = new OO3Attachments();
        }
        return attachments.registerAttachment(file, name);
    }

    public void test()
    {
        // Adding a new Style
        OO3Style myStyle = new OO3Style();
        myStyle.addColorProperty("text-background-color", OO3Color.rgbColor(0, (float) 0.5, (float) 0.4));
        myStyle.addStringProperty("font-weight", "12");
        namedStyles.add("MyStyle", myStyle);

        // Configuring columns
        columns.getNoteColumn().setWidth(30);
        columns.getOutlineColumn().setWidth(300);
        columns.getOutlineColumn().setTitle("MyTitle");
        columns.createAdditionalColumn("AnOtherColumn").setWidth(200);

        // Adding some sample items
        OO3Item newItem = new OO3Item(this, "Root");
        newItem.setValueForColumn("AnOtherColumn", "A value");
        newItem.setStyle("MyStyle");
        OO3Item subItem1 = new OO3Item(this, newItem, "Sub item 1");
        subItem1.setStyle(OO3NamedStyles.HIGHLIGHT);
        new OO3Item(this, subItem1, "Sub item 11");
        OO3Item subItem2 = new OO3Item(this, newItem, "Sub item 2");
        OO3Item subItem21 = new OO3Item(this, subItem2, "Sub item 21");
        subItem21.setNoteValue("This is a description\nfor the sub item 2.1");
        subItem21.setStyle(OO3NamedStyles.EMPHASIS);
        OO3Item subItem22 = new OO3Item(this, subItem2, "Sub item 22");
        subItem22.setStyle(OO3NamedStyles.CITATION);

        // Adding an attachment
        subItem22.setNoteValue("Here comes an attachment");
        File file = new File("/Users/sguerin/Documents/TestsFlexo/DNL_PM_WKF.prj/Documentation/DLDirectoryEditEntryPage.jpg");
        subItem22.getNoteValue().addAttachment(this, file, "MyFile");
    }

    public void saveToFile(File file)
    {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (logger.isLoggable(Level.INFO))
                logger.info("Saving OmniOutliner v3 document : " + file.getAbsolutePath());

            File xmlFile;
            if (attachments != null) {
                if (file.exists()) {
                    file.delete();
                }
                file.mkdirs();
                attachments.saveToDirectory(file);
                xmlFile = new File(file, "contents.xml");
            } else {
                xmlFile = file;
            }
            FileOutputStream out = new FileOutputStream(xmlFile);
            XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), out, getDocType());
            out.flush();
            out.close();
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Unexpected exception raised during OmniOutliner v3 document saving: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getXMLRepresentation()
    {
        try {
            return XMLCoder.encodeObjectWithMapping(this, getXMLMapping());
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Unexpected exception raised during OmniOutliner v3 document saving: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public XMLMapping getXMLMapping()
    {
        if (_xmlMapping == null) {
            try {
                _xmlMapping = new XMLMapping(new FileResource("Models/OO3Model.xml"));
            } catch (Exception e) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Unexpected exception raised during OmniOutliner v3 document saving: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return _xmlMapping;
    }

    private XMLMapping _xmlMapping;

    public OO3NamedStyle styleWithName(String styleName)
    {
        return namedStyles.styleWithName(styleName);
    }

}
