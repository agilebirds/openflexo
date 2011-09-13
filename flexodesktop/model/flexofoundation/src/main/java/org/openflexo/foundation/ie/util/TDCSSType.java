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
package org.openflexo.foundation.ie.util;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of a TD CSS
 * (DL_LIST_CELL,DL_LIST_ROW_TITLE,DL_LIST_LAST_CELL)
 * 
 * @author sguerin
 * 
 */
public enum TDCSSType implements StringConvertable
{
	DL_LIST_CELL("DLListCell"),
	DL_LIST_ROW_TITLE("DLListColumnTitle"),
	DL_LIST_ROW_LAST_TITLE("DLListLastColumnTitle"),
	DL_LIST_LAST_CELL("DLListLastCell"),
	DL_BLOCKTOOLS_CONTENT_TITLE("DLBlockToolsContentTableTitle"),
	DL_BLOCKTOOLS_CONTENT_CONTENT("DLBlockToolsContentTableContent"),
	DL_BLOCKTOOLS("DLBlockTools"),
	DL_BLOCKTOOLS_CONTENT("DLBlockToolsContent"),
	DL_BLOCK_HEADER("DLBlockHeader"),
	DL_BLOCK_BODY("DLBlockBody"),
	DL_BLOCK_FOOTER("DLBlockFooter"),
	DL_BLOCK_BODY_TITLE("DLBlockBodyTitle"),
	DL_BLOCK_BODY_CONTENT("DLBlockBodyContent"),
	DL_BLOCK_BODY_COMMENT("DLBlockBodyComment");
	
	private final String name;

	public String getName(){
		return name;
	}
	
	TDCSSType(String _name){
		this.name = _name;
	}
	
	@Override
	public Converter<TDCSSType> getConverter() {
		return tdCSSTypeConverter;
	}
	
	public static final StringEncoder.EnumerationConverter<TDCSSType> tdCSSTypeConverter = new StringEncoder.EnumerationConverter<TDCSSType>(TDCSSType.class,"getName");

	/*
	
	
	
    private static final Logger logger = Logger.getLogger(TDCSSType.class.getPackage().getName());

    public static final TDCSSType DL_LIST_CELL = new DLListCellType();

    public static final TDCSSType DL_LIST_ROW_TITLE = new DLListRowTitleType();
    
    public static final TDCSSType DL_LIST_ROW_LAST_TITLE = new DLListLastColumnTitle();
    
    public static final TDCSSType DL_LIST_LAST_CELL = new DLListLastCellType();
    
    public static final TDCSSType DL_BLOCKTOOLS_CONTENT_TITLE = new DLBlockToolsContentTableTitle();
    
    public static final TDCSSType DL_BLOCKTOOLS_CONTENT_CONTENT = new DLBlockToolsContentTableContent();

    public static final TDCSSType DL_BLOCKTOOLS = new DLBlockTools();
    
    public static final TDCSSType DL_BLOCKTOOLS_CONTENT = new DLBlockToolsContent();
    
    public static final TDCSSType DL_BLOCK_HEADER = new DLBlockHeader();

    public static final TDCSSType DL_BLOCK_BODY = new DLBlockBody();
    
    public static final TDCSSType DL_BLOCK_FOOTER = new DLBlockFooter();

    public static final TDCSSType DL_BLOCK_BODY_TITLE = new DLBlockBodyTitle();
    
    public static final TDCSSType DL_BLOCK_BODY_CONTENT = new DLBlockBodyContent();
    public static final TDCSSType DL_BLOCK_BODY_COMMENT = new DLBlockBodyComment();
    
    
    public static StringEncoder.Converter tdCSSTypeConverter = new Converter(TDCSSType.class) {

        public Object convertFromString(String value)
        {
            return get(value);
        }

        public String convertToString(Object value)
        {
            return ((TDCSSType) value).getName();
        }

    };

    public static class DLListCellType extends TDCSSType
    {
        DLListCellType()
        {
        }

        public String getName()
        {
            return "DLListCell";
        }
    }

    public static class DLListRowTitleType extends TDCSSType
    {
        DLListRowTitleType()
        {
        }

        public String getName()
        {
            return "DLListColumnTitle";
        }
    }
    
    public static class DLListLastColumnTitle extends TDCSSType
    {
        DLListLastColumnTitle()
        {
        }

        public String getName()
        {
            return "DLListLastColumnTitle";
        }
    }

    public static class DLListLastCellType extends TDCSSType
    {
        DLListLastCellType()
        {
        }

        public String getName()
        {
            return "DLListLastCell";
        }
    }

    
    public static class DLBlockToolsContentTableTitle extends TDCSSType
    {
        DLBlockToolsContentTableTitle()
        {
        }

        public String getName()
        {
            return "DLBlockToolsContentTableTitle";
        }
    }
    
    public static class DLBlockToolsContentTableContent extends TDCSSType
    {
        DLBlockToolsContentTableContent()
        {
        }

        public String getName()
        {
            return "DLBlockToolsContentTableContent";
        }
    }
    
    public static class DLBlockTools extends TDCSSType
    {
        DLBlockTools()
        {
        }

        public String getName()
        {
            return "DLBlockTools";
        }
    }
    
    public static class DLBlockHeader extends TDCSSType
    {
        DLBlockHeader()
        {
        }

        public String getName()
        {
            return "DLBlockHeader";
        }
    }
    
    public static class DLBlockBody extends TDCSSType
    {
        DLBlockBody()
        {
        }

        public String getName()
        {
            return "DLBlockBody";
        }
    }
    public static class DLBlockBodyTitle extends TDCSSType
    {
    	DLBlockBodyTitle()
        {
        }

        public String getName()
        {
            return "DLBlockBodyTitle";
        }
    }
    public static class DLBlockBodyContent extends TDCSSType
    {
    	DLBlockBodyContent()
        {
        }

        public String getName()
        {
            return "DLBlockBodyContent";
        }
    }
    public static class DLBlockBodyComment extends TDCSSType
    {
    	DLBlockBodyComment()
        {
        }

        public String getName()
        {
            return "DLBlockBodyComment";
        }
    }
    public static class DLBlockFooter extends TDCSSType
    {
        DLBlockFooter()
        {
        }

        public String getName()
        {
            return "DLBlockFooter";
        }
    }
    
    public static class DLBlockToolsContent extends TDCSSType
    {
        DLBlockToolsContent()
        {
        }

        public String getName()
        {
            return "DLBlockToolsContent";
        }
    }
    
    public abstract String getName();

    public static TDCSSType get(String typeName)
    {
        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
            TDCSSType temp = (TDCSSType) e.nextElement();
            if (temp.getName().equals(typeName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find TDCSSType named " + typeName);
        return null;
    }

    private Vector<TDCSSType> _availableValues = null;

    public Vector getAvailableValues()
    {
        if (_availableValues == null) {
            _availableValues = new Vector<TDCSSType>();
            _availableValues.add(DL_LIST_CELL);
            _availableValues.add(DL_LIST_ROW_TITLE);
            _availableValues.add(DL_LIST_ROW_LAST_TITLE);
            _availableValues.add(DL_LIST_LAST_CELL);
            _availableValues.add(DL_BLOCKTOOLS_CONTENT);
            _availableValues.add(DL_BLOCKTOOLS_CONTENT_CONTENT);
            _availableValues.add(DL_BLOCKTOOLS_CONTENT_TITLE);
            _availableValues.add(DL_BLOCKTOOLS);
            _availableValues.add(DL_BLOCK_BODY);
            _availableValues.add(DL_BLOCK_HEADER);
            _availableValues.add(DL_BLOCK_FOOTER);
            _availableValues.add(DL_BLOCK_BODY_TITLE);
            _availableValues.add(DL_BLOCK_BODY_CONTENT);
        }
        return _availableValues;
    }

    public StringEncoder.Converter getConverter()
    {
        return tdCSSTypeConverter;
    }

    public static Vector availableValues()
    {
        return DL_LIST_CELL.getAvailableValues();
    }
	*/
}
