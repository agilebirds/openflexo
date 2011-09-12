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
package org.openflexo.inspector;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.KeyValueProperty;


public class KVUtil {

    private static final Logger logger = Logger.getLogger(KVUtil.class.getPackage().getName());

    public static boolean hasValueForKey(KeyValueCoding object,String keyPath)
    {
        try {
            if (object == null) {
                //if (logger.isLoggable(Level.WARNING)) logger.warning("model is null");
                return false;
            }
            KeyValueCoding target = getTargetObject(object,keyPath);
            if (target != null) {
            	 target.objectForKey(getLastAccessor(keyPath));
            	 return true;
            } else {
                return false;
            }
        } catch (AccessorInvocationException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("getValueForKey() failed for keyPath " + keyPath + " for object " + object.getClass().getName() + " : exception " + e.getMessage());
            e.getTargetException().printStackTrace();
            return true;
        } catch (InvalidObjectSpecificationException e) {
             return false;
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger
                        .warning("getValueForKey() failed for keyPath " + keyPath + " for object " + object.getClass().getName() + " : exception "
                                + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

   public static Object getValueForKey(KeyValueCoding object,String keyPath)
    {
        try {
            if (object == null) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("model is null");
                return null;
            }
            KeyValueCoding target = getTargetObject(object,keyPath);
            if (target != null) {
                return target.objectForKey(getLastAccessor(keyPath));
            } else {
                return null;
            }
        } catch (AccessorInvocationException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("getValueForKey() failed for keyPath " + keyPath + " for object " + object.getClass().getName() + " : exception " + e.getMessage());
            e.getTargetException().printStackTrace();
            return null;
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger
                        .warning("getValueForKey() failed for keyPath " + keyPath + " for object " + object.getClass().getName() + " : exception "
                                + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void setValueForKey(KeyValueCoding object, Object newValue, String keyPath) throws AccessorInvocationException
    {
        try {
            if (logger.isLoggable(Level.FINE))
                logger.fine("setValueForKey() for keyPath " + keyPath + " with " + newValue);
            KeyValueCoding target = getTargetObject(object,keyPath);
            if (target != null) {
                target.setObjectForKey(newValue, getLastAccessor(keyPath));
            }
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("setValueForKey() with " + newValue + " failed for keyPath " + keyPath + " for object " + object.getClass().getName()
                        + " : exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static KeyValueCoding getTargetObject(KeyValueCoding anObject,String keyPath)
    {
        KeyValueCoding object = anObject;
        String listAccessor = keyPath;
        StringTokenizer strTok = new StringTokenizer(listAccessor, ".");
        String accessor;
        Object currentObject = object;
        while (strTok.hasMoreTokens() && (currentObject != null) && (currentObject instanceof KeyValueCoding)) {
            accessor = strTok.nextToken();
            if (strTok.hasMoreTokens()) {
                if (currentObject != null) {
                    currentObject = ((KeyValueCoding) currentObject).objectForKey(accessor);
                }
            }
        }
        if (currentObject instanceof KeyValueCoding) {
            return (KeyValueCoding) currentObject;
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not find target object for object=" + object + " listAccessor=" + listAccessor
                        + ": must be a non-null KeyValueCoding object (getting " + currentObject + ")");
            return null;
        }
    }

    private static String getLastAccessor(String keyPath)
    {
        String listAccessor = keyPath;
        KeyValueProperty.PathTokenizer strTok = new KeyValueProperty.PathTokenizer(listAccessor);
        String accessor = null;
        while (strTok.hasMoreTokens()) {
            accessor = strTok.nextToken();
        }
        return accessor;
     }
}
