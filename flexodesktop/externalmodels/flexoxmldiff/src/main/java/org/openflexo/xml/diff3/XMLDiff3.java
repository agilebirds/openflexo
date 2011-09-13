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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.IMerge;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.diff.merge.MergedDocumentType;
import org.openflexo.diff.merge.MergeChange.ChangeCategory;
import org.openflexo.xml.diff2.DocumentsMapping;
import org.openflexo.xml.diff2.DocumentsMapping.ParentReferences;
import org.openflexo.xml.diff3.mergerule.MergeTextRule;
import org.openflexo.xml.diff3.mergerule.ResourceCount;
import org.openflexo.xmlcode.ModelEntity;
import org.openflexo.xmlcode.ModelProperty;
import org.openflexo.xmlcode.XMLMapping;


public class XMLDiff3 extends Observable implements IMerge{

	private Document _src;
	private Document _target1;
	private Document _target2;
	private DocumentsMapping _mapping1;
	private DocumentsMapping _mapping2;
	private Vector<UnresolvedConflict> _unresolvedConflict;
	private Document mergedDocument;
	private Vector<MergeElementAction> _mergeElementsAction;
	private Hashtable<UnresolvedConflict,MergeElementAction> _autoMergeElementsAction;
	private Hashtable<UnresolvedConflict,MergeTextAction> _autoMergeTextsAction;
	private Hashtable<UnresolvedConflict, MergeAttributeAction> _autoMergeAttributesAction;
	private Vector<MergeTextAction> _mergeTextsActions;
	private Vector<MergeMoveAction> _mergeMovesActions;
	private Hashtable<Content, Content> _srcMergedMapping;
	private Hashtable<IndexedContent, Content> _pendingInsertions;
	private Hashtable<UnresolvedConflict,MergeAction> _manualChoices;
	private XMLMapping _mapping;
	private int _conflictIndex;
	//compliance with other merge algorithm
	private DiffSource _leftSource;
	private DiffSource _rightSource;
	private DiffSource _originalSource;
	private DiffSource _mergedSource;
	//private MergedDocumentType _docType;
	public static final boolean DEBUG = false;
	
	private class IndexedContent{
		private Content _content;
		private Integer _index;

		public IndexedContent(Content content, Integer index){
			super();
			_content = content;
			_index = index;
		}

		public Content getContent() {
			return _content;
		}

		public Integer getIndex() {
			return _index;
		}
	}
	public XMLDiff3(Document src, Document target1, Document target2, XMLMapping mapping, MergedDocumentType mergedDocumentType){
		super();
		_unresolvedConflict = new Vector<UnresolvedConflict>();
		_src = src;
		_target1 = target1;
		_target2 = target2;
		//_docType = mergedDocumentType;
		_conflictIndex = 0;
		_mapping1 = new DocumentsMapping(_src,_target1);
		_mapping2 = new DocumentsMapping(_src,_target2);
		_leftSource = new DiffSource(getXMLText(target2));
		_rightSource = new DiffSource(getXMLText(target1));
		_originalSource = new DiffSource(getXMLText(src));
		
		_mapping = mapping;
		_mergeElementsAction = new Vector<MergeElementAction>();
		_mergeTextsActions = new Vector<MergeTextAction>();
		_srcMergedMapping = new Hashtable<Content, Content>();
		_pendingInsertions = new Hashtable<IndexedContent, Content>();
		_autoMergeElementsAction = new Hashtable<UnresolvedConflict, MergeElementAction>();
		_autoMergeTextsAction = new Hashtable<UnresolvedConflict, MergeTextAction>();
		_autoMergeAttributesAction = new Hashtable<UnresolvedConflict, MergeAttributeAction>();
		_manualChoices = new Hashtable<UnresolvedConflict, MergeAction>();
		mergedDocument = new Document();
		//now let's parse the source Document
		parse(_src.getRootElement(),mergedDocument);
		
		//everything is parsed and all required actions are in actions container.
		//let's apply all those actions now
		//Note that all merage attribute action (normal or automatic merge) are already executed !!!

		//let's sort actions
		MergeAction[] _actions = new MergeAction[_mergeElementsAction.size()+_mergeTextsActions.size()+_autoMergeElementsAction.size()+_autoMergeTextsAction.size()];
		int i = 0;
		Enumeration en = _mergeElementsAction.elements();
		while(en.hasMoreElements()){_actions[i]=(MergeAction)en.nextElement();i++;}
		en = _mergeTextsActions.elements();
		while(en.hasMoreElements()){_actions[i]=(MergeAction)en.nextElement();i++;}
		en = _autoMergeElementsAction.elements();
		while(en.hasMoreElements()){_actions[i]=(MergeAction)en.nextElement();i++;}
		en = _autoMergeTextsAction.elements();
		while(en.hasMoreElements()){_actions[i]=(MergeAction)en.nextElement();i++;}
		Arrays.sort(_actions, new MergeActionComparator());
		
		//now we execute everything
		for(int j=0;j<_actions.length;j++)_actions[j].execute();
		
		if(DEBUG){
		System.out.println("pending insertion count = "+_pendingInsertions.size());
		if(_unresolvedConflict.size()>0){
			System.out.println("Unresoved conflicts :\n");
			Enumeration<UnresolvedConflict> en2 = _unresolvedConflict.elements();
			while(en2.hasMoreElements()){
				System.out.println(en2.nextElement());
			}
		}else{
			System.out.println("All conflict are resolved");
		}
		System.out.println(getAutoMergeResolutionCount()+" conflicts were resolved automatically.");
		}
	}
	
	public int getAutoMergeResolutionCount(){
		return _autoMergeElementsAction.size()+_autoMergeTextsAction.size()+_autoMergeAttributesAction.size();
	}
	
	private class MergeActionComparator implements Comparator<MergeAction>{

		@Override
		public int compare(MergeAction arg0, MergeAction arg1) {
			return arg0.getActionIndex()-arg1.getActionIndex();
		}
		
	}

	public void registerManualChoice(UnresolvedConflict conflict,MergeAction action){
		if(!_unresolvedConflict.contains(conflict))
			throw new IllegalStateException("Cannot resolve a conflict manually if this conflict doesn't exists");
		_manualChoices.put(conflict, action);
	}
	
	public void unregisterManualChoice(UnresolvedConflict conflict){
		if(_manualChoices.get(conflict)==null)
			throw new IllegalStateException("Cannot remove a manual choice if this conflict doesn't exists");
		_manualChoices.remove(conflict);
	}
	
	public boolean allConflictsAreResolved(){
		return _unresolvedConflict.size()==0;
	}
	public boolean allConflictsAreManuallyResolved(){
		Enumeration<UnresolvedConflict> en = _unresolvedConflict.elements();
		while(en.hasMoreElements()){
			if(!en.nextElement().isSolved())return false;
		}
		return true;
	}
	
	public int getConflictManuallyResolvedCount(){
		int reply = 0;
		Enumeration<UnresolvedConflict> en = _unresolvedConflict.elements();
		while(en.hasMoreElements()){
			if(en.nextElement().isSolved())reply++;
		}
		return reply;
	}
	
//	public Document applyAllManualChoices(){
//		Enumeration<UnresolvedConflict> en = _manualChoices.keys();
//		while(en.hasMoreElements()){
//			MergeAction action = _manualChoices.get(en.nextElement());
//			action.execute();
//		}
//		return getMergedDocument();
//	}
	
	private MergeElementAction tryAutoResolvingTheAdditionDeleteConflict(Element parentSource, Content insertedElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getMergedDocument(){
		return mergedDocument;
	}
	public Content getMergedContentMatchingContentInTargetOfMapping(Content contentInTargetOfMapping, DocumentsMapping docMapping){
		return DocumentsMapping.getContentMatchingContent(contentInTargetOfMapping, docMapping.getTargetDocument(), getMergedDocument());
	}
	private void parse(Content srcContent, Parent parent){
		boolean hasMoved = false;
		boolean isMoveConflict = false;
		Element parentInMergedDocument = null;
		Element newParentInTargetOfMapping = null;
		int moveInsertionIndex = -1;
		if(srcContent instanceof Element){
			//before everything... srcContent has moved ?
			DocumentsMapping.ParentReferences move1 = _mapping1.getMovedElements().get(srcContent);
			DocumentsMapping.ParentReferences move2 = _mapping2.getMovedElements().get(srcContent);
			if(move1!=null || move2!=null){
				hasMoved = true; // position this flag and use it at insertion time.
				if(move2==null){ //move in branch1
					newParentInTargetOfMapping = move1.getParentInTarget();
					parentInMergedDocument = (Element)getMergedContentMatchingContentInTargetOfMapping(newParentInTargetOfMapping,_mapping1);
					moveInsertionIndex = DocumentsMapping.indexOfElement(move1.getContentInTarget());
				}else if(move1==null){ //move in branch2
					newParentInTargetOfMapping = move2.getParentInTarget();
					parentInMergedDocument = (Element)getMergedContentMatchingContentInTargetOfMapping(newParentInTargetOfMapping,_mapping2);
					moveInsertionIndex = DocumentsMapping.indexOfElement(move2.getContentInTarget());
				}else if(isSameMove(move1,move2)){ //same move in both branch
					newParentInTargetOfMapping = move1.getParentInTarget();
					parentInMergedDocument = (Element)getMergedContentMatchingContentInTargetOfMapping(newParentInTargetOfMapping,_mapping1);
					moveInsertionIndex = DocumentsMapping.indexOfElement(move1.getContentInTarget());
				}else{ //complex case : different move in both branch
					isMoveConflict = true;
					Element newParentInTargetOfMapping1 = move1.getParentInTarget();
					Element parent1InMergedDocument = (Element)getMergedContentMatchingContentInTargetOfMapping(newParentInTargetOfMapping1,_mapping1);
				
					Element newParentInTargetOfMapping2 = move2.getParentInTarget();
					Element parent2InMergedDocument = (Element)getMergedContentMatchingContentInTargetOfMapping(newParentInTargetOfMapping2,_mapping2);
					int insertionIndex1 = DocumentsMapping.indexOfElement(move1.getContentInTarget());
					int insertionIndex2 = DocumentsMapping.indexOfElement(move2.getContentInTarget());
					UnresolvedMoveConflict conflict = new UnresolvedMoveConflict(this,_conflictIndex++,(Element)srcContent,parent1InMergedDocument,parent2InMergedDocument,insertionIndex1,insertionIndex2);

					MergeMoveAction autoResolvedConflictAction = tryAutoResolvingMoveConflict(conflict);
					if (autoResolvedConflictAction != null) {
						// Whooah : this tool is smart enough to solve the conflict
						_mergeMovesActions.add(autoResolvedConflictAction);
					} else {
						// I'm afraid that the user will have to manually
						// solve the conflict :-(
						_unresolvedConflict.add(conflict);
					}
				}
			}
			if(parentInMergedDocument!=null){
				if(parentInMergedDocument.getDocument().equals(getMergedDocument())){
					System.err.println("OK parent in merged doc is really in merge doc");
				}else{
					System.err.println("????????????????????? KO KO KO");
				}
			}
			//at this point we assume that the job required for move action is done
			//retreive comparaison data (note that if they exists : mapping are of type : DocumentsMapping.MatchingElements)
			DocumentsMapping.MatchingElements matching1 = (DocumentsMapping.MatchingElements)_mapping1.getMatchingXMLForSourceContent(srcContent);
			DocumentsMapping.MatchingElements matching2 = (DocumentsMapping.MatchingElements)_mapping2.getMatchingXMLForSourceContent(srcContent);
			//case 1 : Element is still in both branch
			if(matching1 != null & matching2!=null){
				
				//we are in the most common case : srcContent is still alive in both branch
				Element buildedElement = (Element)_srcMergedMapping.get(srcContent);
				
				if(buildedElement==null)
					buildedElement = new Element(((Element)srcContent).getName());
				_srcMergedMapping.put(srcContent, buildedElement);
				
				AttributesChange attributesChange = new AttributesChange(this,(matching1).getAttributesDiff(),(matching2).getAttributesDiff(),(Element)srcContent,buildedElement);
				//this call is very important since it will fill buildedElement with all it's content
				attributesChange.getMergedElement();
				//if we have unresolved conflicts on Attributes, then let's put them into the Diff3 unresolved list
				_unresolvedConflict.addAll(attributesChange.getUnresolvedConflicts());
				_autoMergeAttributesAction.putAll(attributesChange.getAutoResolvedConflicts());
				
				//now : let's insert the builded element into it's parent
				if(parent instanceof Document){ 
					//we are sure that the root Element didn't move : so we don't care about the hasMoved flag
					((Document)parent).setRootElement(buildedElement);
				}else{
					if(hasMoved){ 
						if(parentInMergedDocument!=null){ 
							//the new parent exist, so we are able to do the insertion immediately
							System.err.println("insert a moved element : "+buildedElement+"(id="+buildedElement.getAttributeValue("id")+")"+" into "+parentInMergedDocument+"(id="+parentInMergedDocument.getAttributeValue("id")+")");
							insert(parentInMergedDocument,buildedElement,moveInsertionIndex);
						}else{
							// the new parent doesn't exist yet, so we put the new Element in a queue
							System.err.println("queue a moved element : "+buildedElement);
							_pendingInsertions.put(new IndexedContent(buildedElement,moveInsertionIndex),newParentInTargetOfMapping);
						}
					}else{
						insert((Element)parent,buildedElement,-1);
					}
				}
				
				//At this point the buildedElement is either in the merged document or in the pending insertions table
				
				// now let's look at the pending insertions to see if one of them need to be inserted here.
				Hashtable<Content,Integer> contentsToInsert = new Hashtable<Content,Integer>();
				//first we need to retreive all content to insert
				Enumeration<IndexedContent> keyEnum = _pendingInsertions.keys();
				IndexedContent contentToInsert = null;
				while(keyEnum.hasMoreElements()){
					contentToInsert = keyEnum.nextElement();
					Content parentInTarget = _pendingInsertions.get(contentToInsert);
					if(parentInTarget.equals(matching1.getTargetElement()) || parentInTarget.equals(matching2.getTargetElement()))
						contentsToInsert.put(contentToInsert.getContent(),contentToInsert.getIndex());
				}
				
				
				
				Enumeration<Content> en0 = contentsToInsert.keys();
				Content contentToInsertAsAChildOfBuildedElement = null;
				while(en0.hasMoreElements()){
					contentToInsertAsAChildOfBuildedElement = en0.nextElement();
					insert(buildedElement,contentToInsertAsAChildOfBuildedElement,contentsToInsert.get(contentToInsertAsAChildOfBuildedElement));
					//remove the pending insertion
					_pendingInsertions.remove(contentToInsertAsAChildOfBuildedElement);
				}
				
				
				
				//finally : recursive call to the embedded items.
				Iterator<Content> it = ((Element)srcContent).getContent().iterator();
				Content child = null;
				while(it.hasNext()){
					child=it.next();
					if(child instanceof Text && ((Text)child).getTextTrim().length()==0)
						continue;
					if(_srcMergedMapping.get(child)==null) //just to test if child hasn't been inserted yet
						parse(child,buildedElement);
				}
				
				//now let's take a look at Element added in _mapping1 to see if one of them must be inserted here
				Hashtable<Content, Integer> additions1 = _mapping1.getAddedElementsInSourceRef((Element)srcContent);
				Enumeration<Content> en = additions1.keys();
				Content itemToInsert = null;
				while(en.hasMoreElements()){
					itemToInsert = en.nextElement();
					insert(buildedElement,itemToInsert,additions1.get(itemToInsert));
				}
				//now let's take a look at Element added in _mapping2 to see if one of them must be inserted here
				Hashtable<Content, Integer> additions2 = _mapping2.getAddedElementsInSourceRef((Element)srcContent);
				en = additions2.keys();
				while(en.hasMoreElements()){
					itemToInsert = en.nextElement();
					insert(buildedElement,itemToInsert,additions2.get(itemToInsert));
				}
				
				
				//before recursive iteration, let's see if there is some new child
				//that were added in branch1 or branch2
				
				
			}else if(matching1==null && matching2!=null){
				// removed from branch1, but still in branch2
				// let's see if it has been modified in branch2
				if(_mapping2.containsUpdateOrModificationsUnder(srcContent)){
					//we have a conflict... :-(
					UnresolvedDeleteConflict conflict = new UnresolvedDeleteConflict(this,_conflictIndex++,(Element)srcContent,(Element)parent,null,matching2.getTargetElement(),-1,DocumentsMapping.indexOfElement(matching2.getTargetElement()));
					_unresolvedConflict.add(conflict);
				}else{
					//no conflict : the removal of srcContent done in branch1 is "commited"
				}
			}else if(matching1!=null && matching2==null){
				// removed from branch1, but still in branch1
				// let's see if it has been modified in branch1
				if(_mapping1.containsUpdateOrModificationsUnder(srcContent)){
					//we have a conflict... :-(
					UnresolvedDeleteConflict conflict = new UnresolvedDeleteConflict(this,_conflictIndex++,(Element)srcContent,(Element)parent,matching1.getTargetElement(),null,DocumentsMapping.indexOfElement(matching1.getTargetElement()),-1);
					_unresolvedConflict.add(conflict);
				}else{
					//no conflict : the removal of srcContent done in branch2 is "commited"
				}
			}else if(matching1==null && matching2==null){
				//nothing match the current srcContent in none of the 2 branches
				//so : it seems that that it has been removed on both side
				//so : no need to go any further
			}
		} else { //srcContent is a Text
			//just like with Element : we have 4 different cases
			DocumentsMapping.MatchingTexts matching1 = (DocumentsMapping.MatchingTexts)_mapping1.getMatchingXMLForSourceContent(srcContent);
			DocumentsMapping.MatchingTexts matching2 = (DocumentsMapping.MatchingTexts)_mapping2.getMatchingXMLForSourceContent(srcContent);
			if(matching1!=null && matching2!=null){
				if(matching1.isUnchanged()){
					// so we can get matching2
					Text buildedText = new Text(matching2.getTargetText().getText());
					_srcMergedMapping.put(srcContent, buildedText);
					//((Element)parent).addContent(buildedText); // note that we assume that "parent" is not the document
					_mergeTextsActions.add(new MergeTextAction(_conflictIndex++,MergeActionType.CHOOSE1,((Element)parent),buildedText,null));
				}else if(matching2.isUnchanged()){
					// so we can get matching1
					Text buildedText = new Text(matching1.getTargetText().getText());
					_srcMergedMapping.put(srcContent, buildedText);
					//((Element)parent).addContent(buildedText); // note that we assume that "parent" is not the document
					_mergeTextsActions.add(new MergeTextAction(_conflictIndex++,MergeActionType.CHOOSE1,((Element)parent),buildedText,null));
				}else{
					//change on both side ==> conflict
					UnresolvedTextConflict conflict = new UnresolvedTextConflict(this,_conflictIndex++,(Element)parent,
							(matching1).getTargetText(),
							(matching2).getTargetText());
					MergeTextAction autoResolvedConflictAction = tryAutoResolvingTextUpdateConflict(conflict);
					if (autoResolvedConflictAction != null) {
						// Whooah : this tool is smart enough to solve the conflict
						_mergeTextsActions.add(autoResolvedConflictAction);
					} else {
						// I'm afraid that the user will have to manually
						// solve the conflict :-(
						_unresolvedConflict.add(conflict);
					}
					
				}
			}else if(matching1==null && matching2!=null){
				if(matching2.isUnchanged()){
					//no change in 2, removed in 1 ==> removed
				}else{
					//changed in 2, removed in 1 ==> conflict
					_unresolvedConflict.add(new UnresolvedTextConflict(this,_conflictIndex++,(Element)parent,
							null,
							(matching2).getTargetText()));
				}
			}else if(matching1!=null && matching2==null){
				if(matching1.isUnchanged()){
					//no change in 1, removed in 2 ==> removed
				}else{
					//changed in 1, removed in 2 ==> conflict
					_unresolvedConflict.add(new UnresolvedTextConflict(this,_conflictIndex++,(Element)parent,
							(matching1).getTargetText(),
							null));
					
				}
			}else if(matching1==null && matching2==null){
				//nothing match the current srcContent in none of the 2 branches
				//so : it seems that that it has been removed on both side
				//so : no need to go any further
			}
		}
		
		
		
	}
	private MergeMoveAction tryAutoResolvingMoveConflict(UnresolvedMoveConflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	private MergeTextAction tryAutoResolvingTextUpdateConflict(UnresolvedTextConflict conflict) {
		MergeTextRule rule = null;
		rule = new ResourceCount(conflict);
		if(rule.canBeApplyed())return rule.getAction();
		return null;
	}
	private boolean isSameMove(ParentReferences move1, ParentReferences move2) {
		if(move1.getParentInSource().equals(move2.getParentInSource())){
			Content destinationOfMove1MappedIntoDoc2 = DocumentsMapping.getContentMatchingContent(move1.getParentInTarget(),_mapping1.getTargetDocument(),_mapping2.getTargetDocument());
			if(destinationOfMove1MappedIntoDoc2!=null){
				return destinationOfMove1MappedIntoDoc2.equals(move2.getParentInTarget());
			}
		}
		return false;
	}
	private void insert(Element parent, Content child, Integer index) {
		Content conflictingItem = canAcceptChild(parent,child);
		if(conflictingItem==null){
			//parent.addContent(child);
			MergeElementAction mergeAction = new MergeElementAction(_conflictIndex++,MergeActionType.INSERT,child,parent,index);
			_mergeElementsAction.add(mergeAction);
			if(child instanceof Element)registerPendingAddition(parent,(Element) child);
		}else{
			if(conflictingItem instanceof Element){
				UnresolvedInsertionConflict conflict = new UnresolvedInsertionConflict(this,_conflictIndex++,parent,(Element)conflictingItem,(Element)child);
				MergeElementAction autoResolvedConflictAction = tryAutoResolvingAdditionConflict(conflict);
				if (autoResolvedConflictAction != null) {
					// Whooah : this tool is smart enough to solve the conflict
					//_mergeElementsAction.add(autoResolvedConflictAction);
					_autoMergeElementsAction.put(conflict, autoResolvedConflictAction);
					conflict.setSolveAction(autoResolvedConflictAction, false);
				} else {
					// I'm afraid that the user will have to manually
					// solve the conflict :-(
					_unresolvedConflict.add(conflict);
				}
			}else{
				UnresolvedTextConflict conflict = new UnresolvedTextConflict(this,_conflictIndex++,parent,(Text)conflictingItem,(Text)child);
				MergeTextAction autoResolvedConflictAction = tryAutoResolvingTextUpdateConflict(conflict);
				if (autoResolvedConflictAction != null) {
					// Whooah : this tool is smart enough to solve the conflict
					//_mergeTextsActions.add(autoResolvedConflictAction);
					_autoMergeTextsAction.put(conflict, autoResolvedConflictAction);
					conflict.setSolveAction(autoResolvedConflictAction, false);
				} else {
					// I'm afraid that the user will have to manually
					// solve the conflict :-(
					_unresolvedConflict.add(conflict);
				}
			
			}
		}
	}
	private MergeElementAction tryAutoResolvingAdditionConflict(UnresolvedInsertionConflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	private Content canAcceptChild(Element parent,Content child) {
		if(child instanceof Element){
			if(_mapping!=null){
				ModelEntity parentEntity = _mapping.entityWithXMLTag(parent.getName());
				if(parentEntity==null){
					//System.out.println("Cannot fing entity for "+parent.getName()+" in mapping "+_mapping.toString());
					return null;
				}
				Hashtable<ModelProperty, Vector<String>> potentialMatches = parentEntity.getAllNonAttributeProperties();
				Enumeration<ModelProperty> en = potentialMatches.keys();
				ModelProperty prop = null;
				while(en.hasMoreElements()){
					prop = en.nextElement();
					Vector<String> tags = potentialMatches.get(prop);
					if(tags.contains(((Element)child).getName())){
						if(!prop.isSingle())return null;
						Iterator<String> it = tags.iterator();
						String tag = null;
						while(it.hasNext()){
							tag = it.next();
							Content concurrent = findAPendingAdditionWithTag(parent,tag);
							if(concurrent!=null)return concurrent;
						}
						//return null;
					}
				}
				ModelProperty childProperty = parentEntity.getModelPropertyWithXMLTag(((Element)child).getName());
				if(childProperty.isSingle()){
					return parent.getChild(((Element)child).getName());
				}
			}
		}else{
			Iterator<Content> it = parent.getChildren().iterator();
			Content c = null;
			while(it.hasNext()){
				c = it.next();
				if(c instanceof Text && ((Text)c).getTextTrim().length()>0) return c;
			}
		}
		return null;
	}

	private Hashtable<Element, Vector<Element>> _pendingAdditions;
	private void registerPendingAddition(Element parent,Element child){
		if(_pendingAdditions==null)_pendingAdditions=new Hashtable<Element, Vector<Element>>();
		Vector<Element> inserted = _pendingAdditions.get(parent);
		if(inserted==null){
			inserted = new Vector<Element>();
			_pendingAdditions.put(parent, inserted);
		}
		inserted.add(child);
	}
	private Content findAPendingAdditionWithTag(Element parent, String tag){
		if(_pendingAdditions==null)_pendingAdditions=new Hashtable<Element, Vector<Element>>();
		Vector<Element> additions = _pendingAdditions.get(parent);
		if(additions!=null){
			Enumeration<Element> en = additions.elements();
			while(en.hasMoreElements()){
				Element item =en.nextElement();
				if(tag.equals(item.getName()))return item;
			}
		}
		return null;
	}
	public Vector<UnresolvedConflict> getAllUnresolvedConflicts() {
		return _unresolvedConflict;
	}

	@Override
	public int getConflictsChangeCount() {
		return getAllUnresolvedConflicts().size()+getAutoMergeResolutionCount();
	}

	@Override
	public int getLeftChangeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMergedText() {
		//applyAllManualChoices();
		return getXMLText(getMergedDocument());
	}
	
	private String getXMLText(Document doc){
		StringWriter writer = new StringWriter();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputter.output(doc, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return writer.toString();
	}
	
	public static String getXMLText(Element el){
		StringWriter writer = new StringWriter();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputter.output(el, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return writer.toString();
	}

	@Override
	public int getResolvedConflictsChangeCount() {
		return getAutoMergeResolutionCount()+getConflictManuallyResolvedCount();
	}

	@Override
	public int getRightChangeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isResolved() {
		return allConflictsAreManuallyResolved();
	}

	@Override
	public MergeChange changeBefore(MergeChange change) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<MergeChange> filteredChangeList(List<ChangeCategory> selectedCategories) {
		// TODO Auto-generated method stub
		return new Vector<MergeChange>();
	}

	@Override
	public Vector<MergeChange> getChanges() {
		// TODO Auto-generated method stub
		return new Vector<MergeChange>();
	}


	
	@Override
	public DiffSource getLeftSource() {
		return _leftSource;
	}

	@Override
	public DiffSource getMergedSource() {
		if(_mergedSource==null){
			_mergedSource = new DiffSource(getMergedText());
		}
		return new DiffSource(getMergedText());
	}

	@Override
	public DiffSource getOriginalSource() {
		return _originalSource;
	}

	@Override
	public DiffSource getRightSource() {
		return _rightSource;
	}

	public void propagateChange() {
		setChanged();
		notifyObservers();
		
	}
	
	
	
}
