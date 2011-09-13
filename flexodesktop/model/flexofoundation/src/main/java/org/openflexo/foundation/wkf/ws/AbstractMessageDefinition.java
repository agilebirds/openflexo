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
package org.openflexo.foundation.wkf.ws;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.dm.BindingAdded;
import org.openflexo.foundation.ie.dm.BindingRemoved;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * 
 * Used to store port's message definition
 * 
 * @author sguerin
 */
public abstract class AbstractMessageDefinition extends WKFObject implements InspectableObject {

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AbstractMessageDefinition.class.getPackage().getName());

    //private FlexoPort _port;
    private Vector<MessageEntry> _entries;
    private String _name;
    // input, output, or fault
    private String _messageType;
    
    

    /**
     * Constructor used during deserialization
     */
    public AbstractMessageDefinition(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public AbstractMessageDefinition(FlexoProcess process)
    {
        super(process);
        _entries = new Vector();
    }
    
    
    @Override
	public String getInspectorName() 
    {
    	
        // Never inspected by its own
    	    // Inspected by WSEditor.see WSESelectionManager.
        return null;
    }

    public abstract WKFObject getFatherObject();
    
    @Override
	public String getFullyQualifiedName() 
    {
        if (getFatherObject() != null)
            return getFatherObject().getFullyQualifiedName()+".MESSAGE";
        return null;
    }
    
    
    public String getDefaultName()
    {
    		if(isInputMessageDefinition()){
    			return FlexoLocalization.localizedForKey("input_message_definition_name");
    		}
    		else if(isOutputMessageDefinition()){
    			return FlexoLocalization.localizedForKey("output_message_definition_name");
    		}
    		else if(isFaultMessageDefinition()){
    			return FlexoLocalization.localizedForKey("fault_message_definition_name"); 
    		}
        return getDefaultInitialName();
    }

    public static String getDefaultInitialName()
    {
        return FlexoLocalization.localizedForKey("message_definition_name");
    }
    
    public boolean isInputMessageDefinition(){
    		return "input".equals(_messageType);
    }
    public boolean isOutputMessageDefinition(){
    		return "output".equals(_messageType);
    }
    public boolean isFaultMessageDefinition(){
    		return "fault".equals(_messageType);
    }
    public void setIsInputMessageDefinition(){
    		_messageType="input";
    }
    public void setIsOutputMessageDefinition(){
    		_messageType="output";
    }
    public void setIsFaultMessageDefinition(){
    		_messageType="fault";
    }

    @Override
	public String getName(){
    		return _name;
    }
    
    @Override
	public void setName(String n){
    		_name = n;
    }
    
    public Vector<MessageEntry> getEntries() 
    {
        return _entries;
    }

    public void setEntries(Vector<MessageEntry> entries) 
    {
        _entries = entries;
    }

    public void addToEntries(MessageEntry entry) 
    {
        entry.setMessage(this);
        _entries.add(entry);
        setChanged();
        notifyObservers(new BindingAdded(entry));
    }

    public void removeFromEntries(MessageEntry entry) 
    {
        entry.setMessage(null);
        _entries.remove(entry);
        setChanged();
        notifyObservers(new BindingRemoved(entry));
   }

    public MessageEntry entryWithName(String aName)
    {
        for (Enumeration e = getEntries().elements(); e.hasMoreElements();) {
            MessageEntry temp = (MessageEntry) e.nextElement();
            if (temp.getVariableName().equals(aName)) {
                return temp;
            }
        }
         return null;
    }

    public MessageEntry createNewMessageEntry()
    {
        String baseName = FlexoLocalization.localizedForKey("default_entry_name");
        String newEntryName = baseName;
        int inc = 0;
        while (entryWithName(newEntryName) != null) {
            inc++;
            newEntryName = baseName + inc;
        }
        MessageEntry newMessageEntry = new MessageEntry(getProcess(), this);
        newMessageEntry.setVariableName(newEntryName);
        addToEntries(newMessageEntry);
        return newMessageEntry;
    }

    public void deleteMessageEntry(MessageEntry aMessageEntry)
    {
        removeFromEntries(aMessageEntry);
    }

    public boolean isMessageEntryDeletable(MessageEntry aMessageEntry)
    {
        return true;
    }

    /**
     * Return a Vector of all embedded WKFObjects
     * 
     * @return a Vector of WKFObject instances
     */
    @Override
	public Vector getAllEmbeddedWKFObjects()
    {
        Vector returned = new Vector();
        returned.add(this);
       returned.addAll(getEntries());
        return returned;
    }

    // ==========================================================================
    // ================================= Delete ===============================
    // ==========================================================================

    @Override
	public void delete()
    {
        _entries.clear();
        super.delete();
        deleteObservers();
    }

    /**
     * Build and return a vector of all the objects that will be deleted during
     * process deletion
     * 
     * @param aVector of DeletableObject
     */
    @Override
	public Vector<WKFObject> getAllEmbeddedDeleted()
    {
        return getAllEmbeddedWKFObjects();
    }

}
