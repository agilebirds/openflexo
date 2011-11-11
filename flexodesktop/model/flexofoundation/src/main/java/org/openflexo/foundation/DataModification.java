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
package org.openflexo.foundation;

import java.awt.Dimension;

import org.openflexo.inspector.InspectableModification;

/*
 * DataModification.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 5, 2004
 */

/**
 * A DataModification encapsulates a modification that has been done on the datastructure. This object is sent to the datastructure
 * observers.
 * 
 * @author benoit
 */
public class DataModification implements InspectableModification {

	private int _modificationType;

	private String _propertyName;

	private Object _newValue;

	private Object _oldValue;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * @param modifcationType
	 *            : one of the static int declared in this class.
	 * @param oldValue
	 * @param newValue
	 */
	public DataModification(int modificationType, Object oldValue, Object newValue) {
		super();
		_modificationType = modificationType;
		_oldValue = oldValue;
		_newValue = newValue;
	}

	public DataModification(int modificationType, String propertyName, Object oldValue, Object newValue) {
		super();
		_modificationType = modificationType;
		_oldValue = oldValue;
		_newValue = newValue;
		_propertyName = propertyName;
	}

	public static DataModification changeSizeModif(String containerType, Dimension oldVal, Dimension newVal) {
		int mod = -1;
		if (containerType.equals("TR")) {
			mod = CHANGE_TR_SIZE;
		}
		if (containerType.equals("TD")) {
			mod = CHANGE_TD_SIZE;
		}
		return new DataModification(mod, oldVal, newVal);
	}

	public static DataModification changeLocModif(String containerType, int oldVal, int newVal) {
		return changeLocModif(containerType, new Integer(oldVal), new Integer(newVal));
	}

	public static DataModification changeLocModif(String containerType, Integer oldVal, Integer newVal) {
		int mod = -1;
		if (containerType.equals("TR")) {
			mod = CHANGE_TR_LOC_Y;
		}
		if (containerType.equals("TD")) {
			mod = CHANGE_TD_LOC_X;
		}
		return new DataModification(mod, oldVal, newVal);
	}

	public int modificationType() {
		return _modificationType;
	}

	public Object oldValue() {
		return _oldValue;
	}

	@Override
	public Object newValue() {
		return _newValue;
	}

	@Override
	public String propertyName() {
		return _propertyName;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "/" + getModificationTypeAsString() + "\nold Value: "
				+ ((_oldValue != null) ? _oldValue : "null") + "\nnew Value: " + ((_newValue != null) ? _newValue : "null");
	}

	private String getModificationTypeAsString() {
		switch (_modificationType) {
		case ATTRIBUTE:
			return "ATTRIBUTE:" + propertyName();
			// case NODE_INSERTED: return "NODE_INSERTED";
			// case NODE_DELETED: return "NODE_DELETED";
			// case PRE_ADDED: return "PRE_ADDED";
			// case POST_ADDED: return "POST_ADDED";
			// case PRE_REMOVED: return "PRE_REMOVED";
			// case POST_REMOVED: return "POST_REMOVED";
		case OPEN_NODE_DELETED:
			return "OPEN_NODE_DELETED";
			// case POST_DELETED: return "POST_DELETED";
			// case PRE_DELETED: return "PRE_DELETED";
			// case NAME_CHANGED: return "NAME_CHANGED";
		case PROPERTY_ADDED:
			return "PROPERTY_ADDED";
		case NODE_COLOR_CHANGED:
			return "NODE_COLOR_CHANGED";
			// case NODE_LOCATION_CHANGED: return "NODE_LOCATION_CHANGED";
			// case PROCESS_DELETED: return "NODE_LOCATION_CHANGED";
		case PROPERTY_DELETED:
			return "PROPERTY_DELETED";
			// case NODE_NAME_CHANGED: return "NODE_NAME_CHANGED";
			// case OPEN_NODE_VISIBILITY_CHANGED: return
			// "OPEN_NODE_VISIBILITY_CHANGED";
		case PROCESS_FILE_CHANGED:
			return "PROCESS_FILE_CHANGED";
		case WORKFLOW_FILE_CHANGED:
			return "WORKFLOW_FILE_CHANGED";
			// case BROUGHT_TO_FRONT: return "BROUGHT_TO_FRONT";
		default:
			return getClass().getName();
		}
	}

	public static final int UNDEFINED = -1;

	public static final int ATTRIBUTE = 0;

	// public static final int NODE_INSERTED = 1;
	// public static final int NODE_DELETED = 2;
	// public static final int PRE_ADDED = 3;
	// public static final int POST_ADDED = 4;
	// public static final int PRE_REMOVED = 5;
	// public static final int POST_REMOVED = 6;
	public static final int OPEN_NODE_DELETED = 7;

	// public static final int POST_DELETED = 8;
	// public static final int PRE_DELETED = 9;
	// public static final int NAME_CHANGED = 10;
	public static final int PROPERTY_ADDED = 11;

	public static final int NODE_COLOR_CHANGED = 12;

	// public static final int NODE_LOCATION_CHANGED = 13;
	// public static final int PROCESS_DELETED = 14;
	public static final int PROPERTY_DELETED = 15;

	// public static final int NODE_NAME_CHANGED = 16;
	// public static final int OPEN_NODE_VISIBILITY_CHANGED = 17;
	public static final int PROCESS_FILE_CHANGED = 18;

	public static final int WORKFLOW_FILE_CHANGED = 19;

	// public static final int BROUGHT_TO_FRONT = 20; // open node has been
	// brought to the front
	public static final int ELEMENT_ADDED = 21;

	public static final int PROPERTY_REMOVED = 22;

	public static final int ELEMENT_REMOVED = 23;

	public static final int BUTTON_REMOVED = 24;

	public static final int BUTTON_INSERTED = 25;

	// public static final int BLOC_REMOVED = 26; /* DEPRECATED */
	// public static final int BLOC_INSERTED = 27; /* DEPRECATED */
	public static final int WOCOMPONENT_REMOVED = 28;

	public static final int WOCOMPONENT_INSERTED = 29;

	public static final int BLOC_BG_CLOR_CHANGE = 30;

	public static final int BLOC_FG_CLOR_CHANGE = 31;

	// public static final int WIDGET_REMOVED_FROM_TABLE = 32; /* DEPRECATED */
	// public static final int WIDGET_ADDED_TO_TABLE = 33; /* DEPRECATED */
	// public static final int TABLE_INSERTED = 34; /* DEPRECATED */
	// public static final int REMOVING_EMPTY_ROW = 35;
	// public static final int REMOVING_EMPTY_COL = 36; /* DEPRECATED */
	// public static final int ADDING_EMPTY_COL = 37;
	// public static final int ADDING_EMPTY_ROW = 38;
	// public static final int TABLE_REMOVED = 39; /* DEPRECATED */
	public static final int LABEL_DELETED = 40;

	public static final int CHECKBOX_DELETED = 41;

	public static final int DROPDOWN_DELETED = 42;

	// public static final int FLEXOBUTTON_INSERTED = 43;
	// public static final int BUTTON_DELETED = 44;
	// public static final int PRE_MOVED = 45;
	// public static final int ROLE_CHANGED = 46;
	public static final int SELF_ACTIVATED_STATUS_CHANGE = 47;

	public static final int JAVA_METHOD_STATUS_CHANGED = 48;

	// public static final int THUMB_INSERTED = 49; /* DEPRECATED */
	// public static final int THUMB_REMOVED = 50; /* DEPRECATED */
	public static final int SMALLBUTTONSCONTAINER_DELETED = 51;

	// public static final int SMALL_BUTTON_REMOVED = 52;
	// public static final int SMALL_BUTTON_ADDED = 53;
	public static final int ROWSPAN_INCREASE = 54;

	public static final int ROWSPAN_DECREASE = 55;

	public static final int COLSPAN_INCREASE = 56;

	public static final int COLSPAN_DECREASE = 57;

	public static final int TD_HIDDEN = 58;

	public static final int TD_UNHIDDEN = 59;

	public static final int CHANGE_TR_LOC_Y = 60;

	public static final int CHANGE_TD_LOC_X = 61;

	public static final int CHANGE_TR_SIZE = 62;

	public static final int CHANGE_TD_SIZE = 63;

	public static final int CHANGE_BG_COLOR = 64;

	public static final int COMPONENT_FOLDER_ADDED_TO_LIBRARY = 65;

	public static final int OBJECT_DELETION = 998;

	public static final int RM_DM = 999;

	public static final int IE_DM = 1000;

	public static final int WKF_DM = 1001;

	public static final int DM_DM = 1002;

	public static final int WS_DM = 1003;

	public static final int DM_DRM = 1004;

	public static final int CG_DM = 1005;

	public static final int FPS_DM = 1006;

	public static final int OE_DM = 1007;

	public static final int SG_DM = 1008;

	public static final int LOG = 1100;

	public static final int CG_WAR = 1107;

	public static final int TOC_DM = 1108;

	private boolean _isReentrant = false;

	@Override
	public boolean isReentrant() {
		return _isReentrant;
	}

	public void setReentrant(boolean isReentrant) {
		_isReentrant = isReentrant;
	}
}
