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
package org.openflexo.xml.diff3;

import java.util.Observable;
import java.util.Vector;

import org.jdom.Element;

public abstract class UnresolvedConflict extends Observable{

	private MergeAction _solveAction;
	private boolean _isManualChoice = false;
	private Vector<MergeAction> _potentialActions;
	private int _conflictIndex;
	private MergeAction _keepYourChangeAction;
	private MergeAction _discardYourChange;
	private XMLDiff3 _merge;
	
	protected UnresolvedConflict(XMLDiff3 merge,int conflictIndex){
		super();
		_merge = merge;
		_conflictIndex = conflictIndex;
		_potentialActions = new Vector<MergeAction>();
	}
	
	public int getConflictIndex(){
		return _conflictIndex;
	}
	public boolean isSolved(){
		return _solveAction!=null;
	}
	
	public void setSolveAction(MergeAction action, boolean isManualChoice){
		if(isManualChoice){
			if(_solveAction!=null)_solveAction.undo();
		}
		_solveAction = action;
		if(isManualChoice)_solveAction.execute();
		_isManualChoice = isManualChoice;
		_merge.propagateChange();
	}
	
	public MergeAction getSolveAction(){
		return _solveAction;
	}

	public boolean getIsManualChoice(){
		return _isManualChoice;
	}
	
	public MergeAction getKeepYourChangeAction(){
		if(_keepYourChangeAction==null)_keepYourChangeAction = buildKeepYourChangeAction();
		return _keepYourChangeAction;
	}
	public MergeAction getDiscardYourChangeAction(){
		if(_discardYourChange==null)_discardYourChange = buildDiscardYourChangeAction();
		return _discardYourChange;
	}
	
	protected abstract MergeAction buildKeepYourChangeAction();
	protected abstract MergeAction buildDiscardYourChangeAction();
	
	public void addToPotentialMergeAction(MergeAction action){
		_potentialActions.add(action);
	}

	public String getXMLStringRepresentation(Element e){
		return XMLDiff3.getXMLText(e);
	}
}
