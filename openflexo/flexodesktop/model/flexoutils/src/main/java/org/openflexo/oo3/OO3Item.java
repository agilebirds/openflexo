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
public class OO3Item implements XMLSerializable
{

    public String id;

    public boolean expanded;

    public OO3Note note;

    public OO3Values values;

    public OO3Children children;

    public OO3Style style;

    protected OO3Item parentItem;

    private OO3Document document;

    public OO3Item()
    {
        super();
        generateItemId();
        expanded = true;
        note = null;
        parentItem = null;
        children = null;
    }

    public OO3Item(OO3Document d, OO3Item _parentItem, String outlineValue)
    {
        this();
        this.document = d;
        if (_parentItem != null) {
            this.parentItem = _parentItem;
            if (_parentItem.children == null) {
                _parentItem.children = new OO3Children(d);
            }
            _parentItem.children.children.add(this);
        } else { // this is a root item
            d.contents.items.add(this);
        }
        values = new OO3Values(d, outlineValue);
        note = new OO3Note();
    }

    

    public OO3Item(OO3Document d, String outlineValue)
    {
        this(d, null, outlineValue);
    }

    public void setNoteValue(String text)
    {
        getNoteValue().setText(text);
    }

    public OO3Text getNoteValue()
    {
        return note.text;
    }

    public void setStyle(OO3NamedStyles.OO3NamedStyle st)
    {
        this.style = new OO3Style(st);
    }

    public void setStyle(String styleName)
    {
        setStyle(document.styleWithName(styleName));
    }

    public void setOutlineValue(String text)
    {
        values.setOutlineValue(text);
    }

    public OO3Text getOutlineValue()
    {
        return values.getOutlineValue();
    }

    public OO3Text getValueForColumn(String columnTitle)
    {
        return values.getValueForColumn(columnTitle);
    }

    public void setValueForColumn(String columnTitle, String text)
    {
        values.setValueForColumn(columnTitle, text);
    }

    private void generateItemId()
    {
        id = "Item" + hashCode();
    }

    public static class OO3Values implements XMLSerializable
    {
        public Vector<OO3Text> values;

        public OO3Document document;

        public OO3Values()
        {
            super();
            values = new Vector<OO3Text>();
        }

        public OO3Values(OO3Document d, String outlineValue)
        {
            this();
            this.document = d;
            values.add(new OO3Text(outlineValue));
            for (int i = 0; i < d.columns.getAdditionalColumnsCount(); i++) {
                values.add(new OO3Text());
            }
        }

        public void setOutlineValue(String text)
        {
            getOutlineValue().setText(text);
        }

        public OO3Text getOutlineValue()
        {
            return values.elementAt(0);
        }

        public OO3Text getValueForColumn(String columnTitle)
        {
            OO3Columns.OO3Column column = document.columns.getColumnNamed(columnTitle);
            return values.elementAt(document.columns.getIndexOfColumn(column) - 1);
        }

        public void setValueForColumn(String columnTitle, String text)
        {
            getValueForColumn(columnTitle).setText(text);
        }

    }

    public static class OO3Note implements XMLSerializable
    {
        public boolean expanded;

        public OO3Text text;

        public OO3Note()
        {
            super();
            expanded = true;
            text = new OO3Text();
        }

        public OO3Note(String noteText)
        {
            this();
            setText(noteText);
        }

        public void setText(String t)
        {
            this.text.setText(t);
        }
    }

    public static class OO3Children implements XMLSerializable
    {
        public Vector children;

        public OO3Document document;

        public OO3Children()
        {
            super();
            children = new Vector();
        }

        public OO3Children(OO3Document d)
        {
            this();
            this.document = d;
        }

    }

}
