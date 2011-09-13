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
package org.openflexo.foundation.dm.eo;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.dm.eo.model.EORelationship;

/**
 * Represents the join semantic type for a relationship
 * 
 * @author sguerin
 * 
 */
public abstract class JoinSemanticType extends FlexoObject implements StringConvertable, ChoiceList
{

    private static final Logger logger = Logger.getLogger(JoinSemanticType.class.getPackage().getName());

    public static final JoinSemanticType INNER = new InnerJoin();

    public static final JoinSemanticType FULL_OUTER = new FullOuterJoin();

    public static final JoinSemanticType LEFT_OUTER = new LeftOuterJoin();

    public static final JoinSemanticType RIGHT_OUTER = new RightOuterJoin();

    public static final StringEncoder.Converter<JoinSemanticType> joinSemanticTypeConverter = new Converter<JoinSemanticType>(JoinSemanticType.class) {

        @Override
		public JoinSemanticType convertFromString(String value)
        {
            return get(value);
        }

        @Override
		public String convertToString(JoinSemanticType value)
        {
            return value.getName();
        }

    };

    protected static class InnerJoin extends JoinSemanticType
    {
        @Override
		public String getName()
        {
            return "inner_join";
        }

        @Override
		public String getEOCode()
        {
            return EORelationship.InnerJoin;
        };
    }

    protected static class FullOuterJoin extends JoinSemanticType
    {
        @Override
		public String getName()
        {
            return "full_outer";
        }

        @Override
		public String getEOCode()
        {
            return EORelationship.FullOuterJoin;
        }
    }

    protected static class LeftOuterJoin extends JoinSemanticType
    {
        @Override
		public String getName()
        {
            return "left_outer";
        }

        @Override
		public String getEOCode()
        {
            return EORelationship.LeftOuterJoin;
        }
    }

    protected static class RightOuterJoin extends JoinSemanticType
    {
        @Override
		public String getName()
        {
            return "right_outer";
        }

        @Override
		public String getEOCode()
        {
            return EORelationship.RightOuterJoin;
        }
    }

    public abstract String getName();

    public abstract String getEOCode();

    public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey(getName());
    }

    public static JoinSemanticType get(String joinSemantic)
    {
        if (joinSemantic==null)
            return null;
        for (Enumeration e = INNER.getAvailableValues().elements(); e.hasMoreElements();) {
            JoinSemanticType temp = (JoinSemanticType) e.nextElement();
            if (temp.getName().equals(joinSemantic)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find JoinSemantic named " + joinSemantic);
        return null;
    }

    public static JoinSemanticType getJoinSemanticType(String joinSemanticEOEcode)
    {
        if (joinSemanticEOEcode==null)
            return null;
        for (Enumeration e = INNER.getAvailableValues().elements(); e.hasMoreElements();) {
            JoinSemanticType temp = (JoinSemanticType) e.nextElement();
            if (temp.getEOCode().equals(joinSemanticEOEcode)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find JoinSemantic coded as " + joinSemanticEOEcode);
        return null;
    }

    private Vector<JoinSemanticType> _availableValues = null;

    @Override
	public Vector<JoinSemanticType> getAvailableValues()
    {
        if (_availableValues == null) {
            _availableValues = new Vector<JoinSemanticType>();
            _availableValues.add(INNER);
            _availableValues.add(FULL_OUTER);
            _availableValues.add(LEFT_OUTER);
            _availableValues.add(RIGHT_OUTER);
        }
        return _availableValues;
    }

    @Override
	public StringEncoder.Converter getConverter()
    {
        return joinSemanticTypeConverter;
    }

    public static Vector availableValues()
    {
        return INNER.getAvailableValues();
    }

}
