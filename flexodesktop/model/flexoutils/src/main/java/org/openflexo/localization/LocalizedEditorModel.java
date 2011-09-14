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
package org.openflexo.localization;

import java.text.Collator;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class LocalizedEditorModel implements TableModel
{

    private static final Logger logger = Logger.getLogger(LocalizedEditorModel.class.getPackage().getName());

    private DefaultTableModel model;

    private Vector<String> _keys;

    public LocalizedEditorModel(char aChar)
    {
        super();
        model = new DefaultTableModel();
        _keys = FlexoLocalization.buildAllKeys(aChar);
        Collections.sort(_keys, Collator.getInstance());
    }

    public LocalizedEditorModel(String s)
    {
        this(s.charAt(0));
    }

    public LocalizedEditorModel()
    {
        super();
        model = new DefaultTableModel();
        _keys = FlexoLocalization.buildAllWarningKeys();
        Collections.sort(_keys, Collator.getInstance());
    }

    /**
     * Overrides
     *
     * @see javax.swing.table.TableModel#getRowCount()
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
	public int getRowCount()
    {
        return _keys.size();
    }

    /**
     * Overrides
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
	public int getColumnCount()
    {
        return FlexoLocalization.getAvailableLanguages().size() + 1;
    }

    /**
     * Overrides
     *
     * @see javax.swing.table.TableModel#getColumnName(int)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
	public String getColumnName(int columnIndex)
    {
        if (columnIndex == 0) {
            return FlexoLocalization.localizedForKey("key");
        } else {
            return FlexoLocalization.localizedForKey(( Language.getAvailableLanguages().elementAt(columnIndex - 1)).getName());
        }
    }

    /**
     * Overrides
     *
     * @see javax.swing.table.TableModel#getColumnClass(int)
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
	public Class<String> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    /**
     * Overrides
     *
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return (columnIndex > 0 ? true : false);
    }

    /**
     * Overrides
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
	public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            return _keys.elementAt(rowIndex);
        } else {
            Language language = (Language) FlexoLocalization.getAvailableLanguages().elementAt(columnIndex - 1);
            return FlexoLocalization.localizedForKeyAndLanguage(_keys.elementAt(rowIndex), language);
        }
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("setValueAt " + value + " " + rowIndex + "/" + columnIndex);
        Language language = (Language) FlexoLocalization.getAvailableLanguages().elementAt(columnIndex - 1);
        FlexoLocalization.setLocalizedForKeyAndLanguage(_keys.elementAt(rowIndex), (String) value, language);
        model.fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override
	public void addTableModelListener(TableModelListener arg0)
    {
        model.addTableModelListener(arg0);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override
	public void removeTableModelListener(TableModelListener arg0)
    {
        model.removeTableModelListener(arg0);
    }

    public static boolean isKeyValid(String aKey)
    {
        if ((aKey == null) || (aKey.length() == 0)) {
            return false;
        } // null or empty key is not valid
        /*if (!aKey.toLowerCase().equals(aKey)) {
            return false;
        } // should not contains maj chars*/
        /*if (aKey.lastIndexOf(" ") > -1) {
            return false;
        } // should not contains SPACE char*/
        return true;
    }

    public static boolean isValueValid(String aKey, String aValue)
    {
        if ((aValue == null) || (aValue.length() == 0)) {
            return false;
        } // null or empty value is not valid
        if (aValue.equals(aKey)) {
            return false;
        } // not the same value > means not translated
        if (aValue.lastIndexOf("_") > -1) {
            return false;
        } // should not contains UNDERSCORE char
        return true;
    }

}
