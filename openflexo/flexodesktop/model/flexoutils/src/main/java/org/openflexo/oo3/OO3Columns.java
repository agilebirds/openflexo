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

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3Columns implements XMLSerializable
{

    public static final String NOTES_COLUMN = "NOTES";

    public static final String OUTLINE_COLUMN = "OUTLINE";

    public static final String DefaultOutlineColumnTitle = "Topic";

    private int additionalColumnsCount = 0;

    private OO3Column noteColumn;

    private OO3Column outlineColumn;

    public Vector<OO3Column> columns;

    public OO3Columns()
    {
        super();
        columns = new Vector<OO3Column>();
        createNoteColumn();
        createOutlineColumn();
        additionalColumnsCount = 0;
    }

    public int getAdditionalColumnsCount()
    {
        return additionalColumnsCount;
    }

    public OO3Column getColumnNamed(String columnTitle)
    {
        for (Enumeration e = columns.elements(); e.hasMoreElements();) {
            OO3Column next = (OO3Column) e.nextElement();
            if (next.name.equals(columnTitle)) {
                return next;
            }
        }
        return null;
    }

    public OO3Column getNoteColumn()
    {
        return noteColumn;
    }

    public OO3Column getOutlineColumn()
    {
        return outlineColumn;
    }

    public int getIndexOfColumnNamed(String columnTitle)
    {
        OO3Column column = getColumnNamed(columnTitle);
        if (column != null) {
            return getIndexOfColumn(column);
        }
        return -1;
    }

    public int getIndexOfColumn(OO3Column column)
    {
        return column.columnIndex;
    }

    public OO3Column createAdditionalColumn(String columnTitle)
    {
        OO3Column column = OO3Column.createAdditionalColumn(columnTitle);
        columns.add(column);
        column.columnIndex = additionalColumnsCount + 2;
        additionalColumnsCount++;
        return column;
    }

    private void createNoteColumn()
    {
        noteColumn = OO3Column.createNoteColumn();
        columns.add(noteColumn);
    }

    private void createOutlineColumn()
    {
        outlineColumn = OO3Column.createOutlineColumn(DefaultOutlineColumnTitle);
        columns.add(outlineColumn);
    }

    public static class OO3Column implements XMLSerializable
    {

        public String id;

        public String type;

        public String name;

        public int width;

        public int minimumWidth;

        public int maximumWidth;

        public int textExportWidth;

        public boolean isNoteColumn;

        public boolean isOutlineColumn;

        public ColumnTitle columnTitle;

        public OO3Style columnStyle;

        int columnIndex;

        public OO3Column()
        {
            super();
            generateColumnId();
            columnIndex = -1;
            type = "text";
            width = 100;
            minimumWidth = 13;
            maximumWidth = 1000000;
            textExportWidth = 80;
            isNoteColumn = false;
            isOutlineColumn = false;
            columnStyle = null;
        }

        public OO3Column(String ct)
        {
            this();
            this.name = ct;
            setTitle(ct);
        }

        public void setTitle(String ct)
        {
            this.columnTitle = new ColumnTitle(ct);
        }

        public void setWidth(int aWidth)
        {
            this.width = aWidth;
        }

        static OO3Column createNoteColumn()
        {
            OO3Column returned = new OO3Column(null);
            returned.name = NOTES_COLUMN;
            returned.width = 18;
            returned.minimumWidth = 18;
            returned.maximumWidth = 18;
            returned.textExportWidth = 1;
            returned.isNoteColumn = true;
            returned.isOutlineColumn = false;
            returned.columnStyle = OO3Style.getNoteColumnStyle();
            returned.columnIndex = 0;
            return returned;
        }

        static OO3Column createOutlineColumn(String columnTitle)
        {
            OO3Column returned = new OO3Column(columnTitle);
            returned.name = OUTLINE_COLUMN;
            returned.width = 400;
            returned.isNoteColumn = false;
            returned.isOutlineColumn = true;
            returned.columnIndex = 1;
            return returned;
        }

        static OO3Column createAdditionalColumn(String columnTitle)
        {
            OO3Column returned = new OO3Column(columnTitle);
            returned.width = 100;
            returned.isNoteColumn = false;
            returned.isOutlineColumn = false;
            return returned;
        }

        private void generateColumnId()
        {
            id = "OO3Column" + hashCode();
        }

        public static class ColumnTitle implements XMLSerializable
        {

            public OO3Text title;

            public ColumnTitle()
            {
                super();
            }

            public ColumnTitle(String aTitle)
            {
                super();
                title = new OO3Text(aTitle);
            }
        }
    }
}
