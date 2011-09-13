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
package org.openflexo.foundation.dm.eo.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.wocompat.PropertyListSerialization;

/**
 * @author gpolet
 *
 */
public class FlexoPropertyListSerialization extends PropertyListSerialization
{
    
    public static String getPListRepresentation(Object o)
    {
        try {
            StringWriter sw = new StringWriter();
            BufferedWriter out = new BufferedWriter(sw);
            try {
                writeObject("", out, o);
                out.flush();
                return sw.toString();
            } finally {
                out.close();
            }
        } catch (IOException ioex) {
            throw new CayenneRuntimeException("Error saving plist.", ioex);
        }
    }
    
    /**
     * Saves property list to file.
     */
    public static void propertyListToFile(File f, Object plist) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            try {
                writeObject("", out, plist);
            }
            finally {
                out.close();
            }
        }
        catch (IOException ioex) {
            throw new CayenneRuntimeException("Error saving plist.", ioex);
        }
    }

    
    protected static void writeObject(String offset, Writer out, Object plist) throws IOException {
		if (plist == null) {
			return;
		}

		if (plist instanceof Collection) {
			Collection list = (Collection) plist;

			out.write('\n');
			out.write(offset);

			if (list.size() == 0) {
				out.write("()");
				return;
			}

			out.write("(\n");

			String childOffset = offset + "   ";
			
			Iterator it = null;
			if(list.iterator().next() instanceof Comparable){
				Vector sortedEntries = new Vector();
				Object[] entries = list.toArray();
				Arrays.sort(entries);
				for(int ind=0;ind<entries.length;ind++){
					sortedEntries.add(entries[ind]);
				}
				it = sortedEntries.iterator();
			}else{
				it = list.iterator();
			}
			
			
			boolean appended = false;
			while (it.hasNext()) {
				// Java collections can contain nulls, skip them
				Object obj = it.next();
				if (obj != null) {
					if (appended) {
						out.write(", \n");
					}

					out.write(childOffset);
					writeObject(childOffset, out, obj);
					appended = true;
				}
			}

			out.write('\n');
			out.write(offset);
			out.write(')');
		} else if (plist instanceof Map) {
			Map map = (Map) plist;
			out.write('\n');
			out.write(offset);

			if (map.size() == 0) {
				out.write("{}");
				return;
			}

			out.write("{");

			String childOffset = offset + "    ";

			Iterator it = null;
			
			if(map.keySet().iterator().next() instanceof Comparable){
				Vector sortedEntries = new Vector();
				Object[] entries = map.keySet().toArray();
				Arrays.sort(entries);
				for(int ind=0;ind<entries.length;ind++){
					sortedEntries.add(entries[ind]);
				}
				it = sortedEntries.iterator();
			}else{
				it = map.keySet().iterator();
			}
			while (it.hasNext()) {
				// Java collections can contain nulls, skip them
				
				Object key = it.next();
				if (key == null) {
					continue;
				}
				Object obj = map.get(key);
				if (obj == null) {
					continue;
				}
				out.write('\n');
				out.write(childOffset);
				out.write(quoteString(key.toString()));
				out.write(" = ");
				writeObject(childOffset, out, obj);
				out.write(';');
			}

			out.write('\n');
			out.write(offset);
			out.write('}');
		} else if (plist instanceof String) {
			out.write(quoteString(plist.toString()));
		} else if (plist instanceof Number) {
			out.write(plist.toString());
		} else {
			throw new CayenneRuntimeException("Unsupported class for property list serialization: " + plist.getClass().getName());
		}
	}

}
