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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;
import org.openflexo.xml.diff2.AttributesDiff;
import org.openflexo.xml.diff3.mergerule.AverageLocation;
import org.openflexo.xml.diff3.mergerule.AveragePosition;
import org.openflexo.xml.diff3.mergerule.MaxValue;
import org.openflexo.xml.diff3.mergerule.MergeAttributeRule;
import org.openflexo.xml.diff3.mergerule.NewestDate;
import org.openflexo.xml.diff3.mergerule.OldestDate;


public class AttributesChange {

	private AttributesDiff _diff1;
	private AttributesDiff _diff2;
	private Vector<MergeAttributeAction> _mergeAttributesActions;
	private Vector<UnresolvedAttributesConflict> _unresolvedAttributesConflict;
	private Hashtable<UnresolvedAttributesConflict, MergeAttributeAction> _autoResolvedAttributesConflict;
	private Element _sourceElement;
	private Element _mergedElement;
	private XMLDiff3 _merge;
	
	public AttributesChange(XMLDiff3 merge,AttributesDiff diff1, AttributesDiff diff2, Element sourceElement, Element mergedElement){
		super();
		_merge = merge;
		_diff1 = diff1;
		_diff2 = diff2;
		_mergedElement = mergedElement;
		_mergeAttributesActions = new Vector<MergeAttributeAction>();
		_autoResolvedAttributesConflict = new Hashtable<UnresolvedAttributesConflict, MergeAttributeAction>();
		_unresolvedAttributesConflict = new Vector<UnresolvedAttributesConflict>();
		_sourceElement = sourceElement;
		processToAdditionOfAttributes();
		processToUpdateOfAttributes();
		processToDeletionOfAttributes();
		
	}

	public boolean hasUnresolvedConflict(){
		return _unresolvedAttributesConflict.size()>0;
	}
	public Vector<UnresolvedAttributesConflict> getUnresolvedConflicts(){
		return _unresolvedAttributesConflict;
	}
	public Hashtable<UnresolvedAttributesConflict, MergeAttributeAction>  getAutoResolvedConflicts(){
		return _autoResolvedAttributesConflict;
	}
	public Element getMergedElement(){
		if (_sourceElement.getAttributes() != null) {
			Iterator<Attribute> it = _sourceElement.getAttributes().iterator();
			Attribute item = null;
			while (it.hasNext()) {
				item = it.next();
				_mergedElement.setAttribute(item.getName(), item.getValue());
			}
		}
		Enumeration<MergeAttributeAction> en = _mergeAttributesActions.elements();
		while(en.hasMoreElements()){
			en.nextElement().execute();
		}
		en = _autoResolvedAttributesConflict.elements();
		while(en.hasMoreElements()){
			en.nextElement().execute();
		}
		return _mergedElement;
	}
	
	private void processToAdditionOfAttributes() {
		Enumeration<String> en = _diff1.getAddedAttributes().keys();
		String attributeName = null;
		while(en.hasMoreElements()){
			attributeName = en.nextElement();
			if(_diff2.getAddedAttributes().get(attributeName)==null){
				
				//here we have test if the added attribute isn't in a deleted part of the tree
				if(elementIsInOneOfThoseTree(_diff1.getSourceElement(),_diff2.getDocumentMapping().getRemovedElements())){
					//here we have a conflict : the modified attribute is in a part of the tree that has been deleted
					UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement,_diff1.getAddedAttributes().get(attributeName),null,_mergedElement);
					MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheAdditionDeleteConflict(conflict);
					if(autoResolvedConflictAction!=null){
						//Whooah : this tool is smart enough to solve the conflict
						_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
						conflict.setSolveAction(autoResolvedConflictAction, false);
					}else{
						//I'm afraid that the user will have to manually solve the conflict :-(
						_unresolvedAttributesConflict.add(conflict);
					}
				}else{
					//non conflicting addition
					_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.INSERT,attributeName,_diff1.getAddedAttributes().get(attributeName).getValue(),_mergedElement));
				}
			
			
			}else{
				//the same attribute was added on both sides
				if(_diff2.getAddedAttributes().get(attributeName).getValue().equals(_diff1.getAddedAttributes().get(attributeName).getValue())){
					//ouf : the same value has been set on both side
					_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.INSERT,attributeName,_diff1.getAddedAttributes().get(attributeName).getValue(),_mergedElement));
				}else{
					// !!! WE HAVE A CONFLICT !!!
					// same attribute added on both side with different values !!!
					UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement,_diff1.getAddedAttributes().get(attributeName),_diff2.getAddedAttributes().get(attributeName),_mergedElement);
					MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheAdditionConflict(conflict);
					if(autoResolvedConflictAction!=null){
						//Whooah : this tool is smart enough to solve the conflict
						_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
						conflict.setSolveAction(autoResolvedConflictAction, false);
					}else{
						//I'm afraid that the user will have to manually solve the conflict :-(
						_unresolvedAttributesConflict.add(conflict);
					}
					
				}
			}
		}
		
		en = _diff2.getAddedAttributes().keys();
		while(en.hasMoreElements()){
			attributeName = en.nextElement();
			if (_diff1.getAddedAttributes().get(attributeName) == null) {
				// here we have test if the added attribute isn't in a deleted
				// part of the tree
				if (elementIsInOneOfThoseTree(_diff2.getSourceElement(), _diff1.getDocumentMapping().getRemovedElements())) {
					// here we have a conflict : the modified attribute is in a
					// part of the tree that has been deleted
					UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement, null, _diff2.getAddedAttributes().get(attributeName),_mergedElement);
					MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheAdditionDeleteConflict(conflict);
					if (autoResolvedConflictAction != null) {
						// Whooah : this tool is smart enough to solve the
						// conflict
						_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
						conflict.setSolveAction(autoResolvedConflictAction, false);
					} else {
						// I'm afraid that the user will have to manually solve
						// the conflict :-(
						_unresolvedAttributesConflict.add(conflict);
					}
				} else {
					// non conflicting addition
					_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.INSERT, attributeName, _diff2.getAddedAttributes().get(attributeName).getValue(),_mergedElement));
				}
			} else {
				// we have already seen this conflicting case in the previous
				// loop
			}
		}
	}
	
	public static boolean elementIsInOneOfThoseTree(Element sourceElement, Vector<Content> removedElements) {
		Element e = sourceElement;
		if(removedElements.contains(e))return true;
		if(e.getParentElement()==null)return false;
		return elementIsInOneOfThoseTree(e.getParentElement(), removedElements);
	}
	public static Element getCauseOfConflict(Element sourceElement, Vector<Content> removedElements) {
		Element e = sourceElement;
		if(removedElements.contains(e))return e;
		if(e.getParentElement()==null)return null;
		return getCauseOfConflict(e.getParentElement(), removedElements);
	}

	private void processToUpdateOfAttributes(){
		Enumeration<Attribute> en = _diff1.getUpdatedAttributes().keys();
		String attributeName = null;
		Attribute srcAttribute = null;
		while(en.hasMoreElements()){
			srcAttribute = en.nextElement();
			attributeName = srcAttribute.getName();
			if(_diff2.getUpdatedAttributes().get(srcAttribute)==null && _diff2.getDeletedAttributes().get(attributeName)==null){
				// here we have test if the added attribute isn't in a deleted
				// part of the tree
				if (elementIsInOneOfThoseTree(_diff1.getSourceElement(), _diff2.getDocumentMapping().getRemovedElements())) {
					// here we have a conflict : the modified attribute is in a
					// part of the tree that has been deleted
					UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement, _diff1.getUpdatedAttributes().get(srcAttribute), null,_mergedElement);
					MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheUpdateDeleteTreeConflict(conflict);
					if (autoResolvedConflictAction != null) {
						// Whooah : this tool is smart enough to solve the
						// conflict
						_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
						conflict.setSolveAction(autoResolvedConflictAction, false);
					} else {
						// I'm afraid that the user will have to manually solve
						// the conflict :-(
						_unresolvedAttributesConflict.add(conflict);
					}
				} else {// non conflicting update
					_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.UPDATE, attributeName, _diff1.getUpdatedAttributes().get(srcAttribute).getValue(),_mergedElement));
				}
			} else {
				if (_diff2.getUpdatedAttributes().get(srcAttribute) != null) {
					// the same attribute was updated on both sides
					if (_diff2.getUpdatedAttributes().get(srcAttribute).getValue().equals(_diff1.getUpdatedAttributes().get(srcAttribute).getValue())) {
						// ouf : the same value has been set on both side
						_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.UPDATE, attributeName, _diff1.getUpdatedAttributes().get(srcAttribute).getValue(),_mergedElement));
					} else {
						// !!! WE HAVE A CONFLICT !!!
						// same attribute updated on both side with different
						// values !!!
						UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement, _diff1.getUpdatedAttributes().get(srcAttribute), _diff2.getUpdatedAttributes().get(srcAttribute),_mergedElement);
						MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheUpdateConflict(conflict);
						if (autoResolvedConflictAction != null) {
							// Whooah : this tool is smart enough to solve the conflict
							_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
							conflict.setSolveAction(autoResolvedConflictAction, false);
						} else {
							// I'm afraid that the user will have to manually
							// solve the conflict :-(
							_unresolvedAttributesConflict.add(conflict);
						}

					}
				}else{
					//the attribute has been updated in diff1 and deleted in diff2
					UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement, _diff1.getUpdatedAttributes().get(srcAttribute), null,_mergedElement);
					MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheUpdateDeleteConflict(conflict);
					if (autoResolvedConflictAction != null) {
						// Whooah : this tool is smart enough to solve the conflict
						_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
						conflict.setSolveAction(autoResolvedConflictAction, false);
					} else {
						// I'm afraid that the user will have to manually
						// solve the conflict :-(
						_unresolvedAttributesConflict.add(conflict);
					}
				}
			}
		}
		
		
		en = _diff2.getUpdatedAttributes().keys();
		while(en.hasMoreElements()){
			srcAttribute = en.nextElement();
			attributeName = srcAttribute.getName();
			if (_diff1.getUpdatedAttributes().get(srcAttribute) == null && _diff1.getDeletedAttributes().get(attributeName) == null) {
				// here we have test if the added attribute isn't in a deleted
				// part of the tree
				if (elementIsInOneOfThoseTree(_diff2.getSourceElement(), _diff1.getDocumentMapping().getRemovedElements())) {
					// here we have a conflict : the modified attribute is in a
					// part of the tree that has been deleted
					UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement, null, _diff2.getUpdatedAttributes().get(srcAttribute),_mergedElement);
					MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheUpdateDeleteTreeConflict(conflict);
					if (autoResolvedConflictAction != null) {
						// Whooah : this tool is smart enough to solve the
						// conflict
						_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
						conflict.setSolveAction(autoResolvedConflictAction, false);
					} else {
						// I'm afraid that the user will have to manually solve
						// the conflict :-(
						_unresolvedAttributesConflict.add(conflict);
					}
				} else {// non conflicting update
					_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.UPDATE, attributeName, _diff2.getUpdatedAttributes().get(srcAttribute).getValue(),_mergedElement));
				}
			} else {
				if (_diff1.getUpdatedAttributes().get(srcAttribute) != null) {
					// this case was solved in the previous loop
				}else{
					//the attribute has been updated in diff2 and deleted in diff1
					UnresolvedAttributesConflict conflict = new UnresolvedAttributesConflict(_merge,_sourceElement, null, _diff2.getUpdatedAttributes().get(srcAttribute),_mergedElement);
					MergeAttributeAction autoResolvedConflictAction = tryAutoResolvingTheUpdateDeleteConflict(conflict);
					if (autoResolvedConflictAction != null) {
						// Whooah : this tool is smart enough to solve the conflict
						_autoResolvedAttributesConflict.put(conflict,autoResolvedConflictAction);
						conflict.setSolveAction(autoResolvedConflictAction, false);
					} else {
						// I'm afraid that the user will have to manually
						// solve the conflict :-(
						_unresolvedAttributesConflict.add(conflict);
					}
				}
			}
		}
	}

	private void processToDeletionOfAttributes(){
		Enumeration<String> en = _diff1.getDeletedAttributes().keys();
		String attributeName = null;
		Attribute sourceAttribute = null;
		while(en.hasMoreElements()){
			attributeName = en.nextElement();
			sourceAttribute = _diff1.getDeletedAttributes().get(attributeName);
			if(_diff2.getUpdatedAttributes().get(sourceAttribute)==null){
				_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.DELETE,attributeName,null,_mergedElement));
			}else{
				//this case has been solved in method : processToUpdateOfAttributes
			}
		}
		en = _diff2.getDeletedAttributes().keys();
		while(en.hasMoreElements()){
			attributeName = en.nextElement();
			sourceAttribute = _diff2.getDeletedAttributes().get(attributeName);
			if(_diff1.getUpdatedAttributes().get(sourceAttribute)==null){
				_mergeAttributesActions.add(new MergeAttributeAction(0,MergeActionType.DELETE,attributeName,null,_mergedElement));
			}else{
				//this case has been solved in method : processToUpdateOfAttributes
			}
		}
	}
	
	private MergeAttributeAction tryAutoResolvingTheUpdateDeleteConflict(UnresolvedAttributesConflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	private MergeAttributeAction tryAutoResolvingTheAdditionConflict(UnresolvedAttributesConflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private MergeAttributeAction tryAutoResolvingTheUpdateConflict(UnresolvedAttributesConflict conflict) {
		MergeAttributeRule rule = null;
		rule = new NewestDate(conflict);
		if(rule.canBeApplyed())return rule.getAction();
		rule = new OldestDate(conflict);
		if(rule.canBeApplyed())return rule.getAction();
		rule = new AveragePosition(conflict);
		if(rule.canBeApplyed())return rule.getAction();
		rule = new AverageLocation(conflict);
		if(rule.canBeApplyed())return rule.getAction();
		rule = new MaxValue(conflict);
		if(rule.canBeApplyed())return rule.getAction();
		return null;
	}
	private MergeAttributeAction tryAutoResolvingTheAdditionDeleteConflict(UnresolvedAttributesConflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}
	private MergeAttributeAction tryAutoResolvingTheUpdateDeleteTreeConflict(UnresolvedAttributesConflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
