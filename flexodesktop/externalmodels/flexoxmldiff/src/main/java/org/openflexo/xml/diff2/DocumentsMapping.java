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
package org.openflexo.xml.diff2;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.Filter;
import org.openflexo.xml.diff3.XMLDiff3;


public class DocumentsMapping {

	private Document _src;
	private Document _target;
	private Hashtable<Content, Content> _srcTarget;
	private Hashtable<Content, Content> _targetSource;
	private Hashtable<Content, ParentReferences> _movedElements;
	private Hashtable<Content, MatchingXML> _matchingXMLs;
	private Vector<Content> _removedFromTarget;
	private Vector<Content> _addedInTarget;
	private Hashtable<Content, ModifierFlags> _modifiersFlags;
	
	public DocumentsMapping(Document src, Document target){
		super();
		_src = src;
		_target = target;
		_srcTarget = new Hashtable<Content, Content>();
		_targetSource = new Hashtable<Content, Content>();
		_removedFromTarget = new Vector<Content>();
		_addedInTarget = new Vector<Content>();
		_movedElements = new Hashtable<Content, ParentReferences>();
		_matchingXMLs = new Hashtable<Content, MatchingXML>();
		_modifiersFlags = new Hashtable<Content, ModifierFlags>();
		//first pass over src
		_srcTarget.put(_src.getRootElement(), _target.getRootElement());
		_matchingXMLs.put(_src.getRootElement(), new MatchingElements(_src.getRootElement(),_target.getRootElement()));
		processToMappingSrcTarget(_src.getRootElement());
		//first pass over target
		_targetSource.put(_target.getRootElement(),_src.getRootElement());
		processToMappingTargetSrc(_target.getRootElement());
		checkMapping();
		
	}
	public Document getTargetDocument(){
		return _target;
	}
	public Document getSourceDocument(){
		return _src;
	}
	public Vector<Content> getRemovedElements(){
		return _removedFromTarget;
	}
	public Vector<Content> getAddedElements(){
		return _addedInTarget;
	}
	public Hashtable<Content,Integer> getAddedElementsInSourceRef(Element srcRef){
		Hashtable<Content,Integer> reply = new Hashtable<Content,Integer>();
		Enumeration<Content> en = _addedInTarget.elements();
		Content item = null;
		Element parentOfItemInTarget = null;
		while(en.hasMoreElements()){
			item = en.nextElement();
			parentOfItemInTarget = item.getParentElement();
			Element parentOfItemInSrc = (Element)_targetSource.get(parentOfItemInTarget);
			if(srcRef.equals(parentOfItemInSrc))
				reply.put((Content)item.clone(),new Integer(indexOfElement(item)));
		}
		return reply;
	}
	public static int indexOfElement(Content element){
		int reply = 0;
		Iterator<Content> it = element.getParentElement().getContent().iterator();
		while(it.hasNext()){
			if(it.next().equals(element))return reply;
			reply++;
		}
		return reply;
	}
	public Hashtable<Content, ParentReferences> getMovedElements(){
		return _movedElements;
	}
	public MatchingXML getMatchingXMLForSourceContent(Content content){
		return _matchingXMLs.get(content);
	}
	private void checkMapping(){
		if(_srcTarget.size()!=_targetSource.size()){
			System.err.println("Mapping aren't of the same size : srcTarget.size="+_srcTarget.size() + " targetSource.size="+_targetSource.size());
		}else{
			//System.out.println("Found mapping for : "+_targetSource.size()+" xml objects");
		}
		Enumeration<Content> en = _srcTarget.keys();
		Content itemKey = null;
		while(en.hasMoreElements()){
			itemKey = en.nextElement();
			Content matchingTargetInSrcTarget = _srcTarget.get(itemKey);
			Content matchingSourceInTargetSource = _targetSource.get(matchingTargetInSrcTarget);
			if(matchingSourceInTargetSource==null){
				System.err.println("Cannot find the pending of "+itemKey+","+matchingTargetInSrcTarget+" into targetSource");
			}else{
				if(!matchingSourceInTargetSource.equals(itemKey)){
					System.err.println("mapping does'nt match :"+itemKey+","+matchingTargetInSrcTarget+","+matchingSourceInTargetSource);
				}
			}
		}
		if(XMLDiff3.DEBUG){
			System.out.println("xmlObjects removed from target : "+_removedFromTarget.size());
			print(_removedFromTarget);
			System.out.println("xmlObjects added in target : "+_addedInTarget.size());
			print(_addedInTarget);
			System.out.println("xmlObjects moved in target : "+_movedElements.size());
			print(_movedElements);
		}
	}
	
	private void print(Vector<Content> v){
		Enumeration<Content> en = v.elements();
		Content item = null;
		while(en.hasMoreElements()){
			item = en.nextElement();
			//System.out.println("\t"+print(item));
		}
	}
	
	private void print(Hashtable<Content,ParentReferences> h){
		Enumeration<Content> en = h.keys();
		Content item = null;
		while(en.hasMoreElements()){
			item = en.nextElement();
			System.out.println("\t"+item+"   "+print(item));
			System.out.println(h.get(item));
			System.out.println("------");
		}
	}
	
	private String print(Content c){
		if(c instanceof Element)return c+"   id="+((Element)c).getAttributeValue("id");
		if(c instanceof Text)return c+"   text="+((Text)c).getText();
		return c+"   unknown !!!";
	}
	private void processToMappingSrcTarget(Element e){
		for(int i=0;i<e.getContentSize();i++){
			Content content = e.getContent(i);
			if(content instanceof Text){
				if(((Text)content).getText().trim().length()==0){
					continue;
				}
			}
			Content matchingContent = getTargetContentForSource(content);
			if(matchingContent==null){
				_removedFromTarget.add(content);
				registerModifier(content,false,true,false,false,false,false);
				if(content instanceof Element){
					processToMappingSrcTarget((Element)content);
				}
			}else{
				_srcTarget.put(content, matchingContent);
				if(content instanceof Element){
					MatchingElements match = new MatchingElements((Element)content,(Element)matchingContent);
					if(!match.isUnchanged())registerModifier(content,false,false,true,false,false,false);
					_matchingXMLs.put(content, match);
				}else if(content instanceof Text){
					MatchingTexts match = new MatchingTexts((Text)content,(Text)matchingContent);
					if(!match.isUnchanged())registerModifier(content,false,false,false,false,false,true);
					_matchingXMLs.put(content, match);
				}
				if(!content.getParentElement().equals(getSourceContentForTarget(matchingContent.getParentElement()))){
					//element has moved
					ParentReferences parentRefs = new ParentReferences(content.getParentElement(),matchingContent.getParentElement(),matchingContent);
					_movedElements.put(content, parentRefs);
					registerModifier(content,false,false,false,false,true,false);
				}
				if(content instanceof Element){
					processToMappingSrcTarget((Element)content);
				}
			}
		}
	}
	
	private void processToMappingTargetSrc(Element e){
		for(int i=0;i<e.getContentSize();i++){
			Content content = e.getContent(i);
			if(content instanceof Text){
				if(((Text)content).getText().trim().length()==0){
					continue;
				}
			}
			Content matchingContent = getSourceContentForTarget(content);
			if(matchingContent==null){
				_addedInTarget.add(content);
				if(_targetSource.get(e)!=null)registerModifier(_targetSource.get(e),true,false,false,false,false,false);
				if(content instanceof Element){
					processToMappingTargetSrc((Element)content);
				}
			}else{
				_targetSource.put(content, matchingContent);
				
				if(!content.getParentElement().equals(getTargetContentForSource(matchingContent.getParentElement()))){
					//element has moved
					ParentReferences parentRefs = new ParentReferences(matchingContent.getParentElement(),content.getParentElement(),content);
					if(_movedElements.get(matchingContent)==null){
						System.err.println("moved elements doesn't match in 2nd pass");
					}else if(!_movedElements.get(matchingContent).equals(parentRefs)){
						System.err.println("parents of moved elements doesn't match");
					}
					
					
					//_movedElements.put(content, parentRefs);
				}
				
				
				if(content instanceof Element){
					processToMappingTargetSrc((Element)content);
				}
			}
		}
	}
	
	public Content getTargetContentForSource(Content srcContent){
		if(_src.getRootElement().equals(srcContent))return _target.getRootElement();
		if (srcContent instanceof Element) {
			String idVal = ((Element)srcContent).getAttributeValue("id");
			if (idVal != null) {
				return findElementWithId(_target, idVal);
			} else {
				//shit we don't have an id !!!!
				//let's try an idref
				String idrefVal = ((Element)srcContent).getAttributeValue("idref");
				if(idrefVal!=null){
					Content parentTargetElement = getTargetContentForSource(srcContent.getParentElement());
					if (parentTargetElement != null) {
						return findElementWithIdRef((Element)parentTargetElement,idrefVal);
					}
				}else{
					//shit nor an ID, nor an id ref
					Content parentTargetElement = getTargetContentForSource(srcContent.getParentElement());
					if (parentTargetElement != null) {
						return ((Element)parentTargetElement).getChild(((Element)srcContent).getName());
					}
				}
			}
			return null;
		}else{
			Element parentElement = (Element)getTargetContentForSource(srcContent.getParentElement());
			if(parentElement!=null){
				return getFirstNonEmptyChildText(parentElement);
			}
		}
		return null;
	}
	
	private Content findElementWithIdRef(Element parentTargetElement, String idrefVal) {
		Iterator<Element> it = parentTargetElement.getChildren().iterator();
		Element item = null;
		while(it.hasNext()){
			item = it.next();
			if(idrefVal.equals(item.getAttributeValue("idref")))return item;
		}
		return null;
	}
	public static Content getContentMatchingContent(Content srcContent, Document sourceDocument, Document targetDocument){
		if(sourceDocument.getRootElement().equals(srcContent))return targetDocument.getRootElement();
		if (srcContent instanceof Element) {
			String idVal = ((Element)srcContent).getAttributeValue("id");
			if (idVal != null) {
				return findElementWithId(targetDocument, idVal);
			} else {
				Content parentTargetElement = getContentMatchingContent(srcContent.getParentElement(),sourceDocument,targetDocument);
				if (parentTargetElement != null) {
					return ((Element)parentTargetElement).getChild(((Element)srcContent).getName());
				}
			}
			return null;
		}else{
			Element parentElement = (Element)getContentMatchingContent(srcContent.getParentElement(),sourceDocument,targetDocument);
			if(parentElement!=null){
				return getFirstNonEmptyChildText(parentElement);
			}
		}
		return null;
	}
	
	private static Content getFirstNonEmptyChildText(Element parentElement) {
		for(int i=0;i<parentElement.getContentSize();i++){
			Content content = parentElement.getContent(i);
			if(content instanceof Text){
				if(((Text)content).getText().trim().length()>0){
					return content;
				}
			}
		}
		return null;
	}

	public Content getSourceContentForTarget(Content targetContent){
		if(_target.getRootElement().equals(targetContent))return _src.getRootElement();
		if (targetContent instanceof Element) {
			String idVal = ((Element)targetContent).getAttributeValue("id");
			if (idVal != null) {
				return findElementWithId(_src, idVal);
			} else {
				Content parentTargetElement = getSourceContentForTarget(targetContent.getParentElement());
				if (parentTargetElement != null) {
					return ((Element)parentTargetElement).getChild(((Element)targetContent).getName());
				}
			}
			return null;
		}else{
			Element parentElement = (Element)getSourceContentForTarget(targetContent.getParentElement());
			if(parentElement!=null){
				return getFirstNonEmptyChildText(parentElement);
			}
		}
		return null;
	}
	
	private static Element findElementWithId(Document document, String idRef)
    {
    	if(idRef==null)return null;
    	Iterator it = document.getDescendants(new IDFilter(idRef));
    	if (it.hasNext()) return (Element)it.next();
    	return null;
    }
	
	private static class IDFilter implements Filter{

    	private String _searchedID;
    	public IDFilter(String searchedID){
    		super();
    		_searchedID = searchedID;
    	}
		@Override
		public boolean matches(Object arg0) {
			if(arg0 instanceof Element){
				return _searchedID.equals(((Element)arg0).getAttributeValue("id"));
			}
			return false;
		}
    }
	
	public class MatchingTexts extends MatchingXML{
		private Text _srcText;
		private Text _targetText;
		public MatchingTexts(Text src, Text target){
			super();
			_srcText = src;
			_targetText = target;
		}
		public Text getSourceText(){
			return _srcText;
		}
		public Text getTargetText(){
			return _targetText;
		}
		@Override
		public boolean equals(Object obj){
			if(obj instanceof MatchingTexts){
				return (getSourceText().equals(((MatchingTexts)obj).getSourceText()))
						&& (getTargetText().equals(((MatchingTexts)obj).getTargetText()));
			}
			return false;
		}
		@Override
		public boolean isUnchanged(){
			if(_srcText.getText()==null)return _targetText==null;
			return _srcText.getText().equals(_targetText.getText());
		}
	}
	public class MatchingElements extends MatchingXML{
		private Element _srcElement;
		private Element _targetElement;
		private AttributesDiff _attributesDiff;
		public MatchingElements(Element src, Element target){
			super();
			_srcElement = src;
			_targetElement = target;
			_attributesDiff = new AttributesDiff(_srcElement,_targetElement,DocumentsMapping.this);
		}
		public AttributesDiff getAttributesDiff(){
			return _attributesDiff;
		}
		public Element getSourceElement(){
			return _srcElement;
		}
		public Element getTargetElement(){
			return _targetElement;
		}
		@Override
		public boolean isUnchanged(){
			return getAttributesDiff().isUnchanged();
		}
		@Override
		public boolean equals(Object obj){
			if(obj instanceof MatchingElements){
				return (getSourceElement().equals(((MatchingElements)obj).getSourceElement()))
						&& (getTargetElement().equals(((MatchingElements)obj).getTargetElement()));
			}
			return false;
		}
	}
	
	public abstract class MatchingXML{
		public abstract boolean  isUnchanged();
	}
	public class ParentReferences{
		
		private Element _parentInSource;
		private Element _parentInTarget;
		private Content _contentInTarget;
		public ParentReferences(Element parentInSource, Element parentInTarget, Content contentInTarget){
			super();
			_parentInSource = parentInSource;
			_parentInTarget = parentInTarget;
			_contentInTarget = contentInTarget;
		}
		public Content getContentInTarget(){
			return _contentInTarget;
		}
		public Element getParentInSource() {
			return _parentInSource;
		}
		public Element getParentInTarget() {
			return _parentInTarget;
		}
		
		@Override
		public String toString(){
			return "\t\tParent in source : "+print(getParentInSource())+"\n\t\tParent in target : "+print(getParentInTarget());
			
		}
		@Override
		public boolean equals(Object obj){
			if(obj instanceof ParentReferences){
				return (getParentInSource().equals(((ParentReferences)obj).getParentInSource()))
						&& (getParentInTarget().equals(((ParentReferences)obj).getParentInTarget()));
			}
			return false;
		}
	}
	public boolean containsUpdateOrModificationsUnder(Content srcContent) {
		if(_modifiersFlags.get(srcContent)!=null && _modifiersFlags.get(srcContent).isModified())return true;
		if(srcContent instanceof Text && _modifiersFlags.get(srcContent)==null) return false;
		Iterator<Content> it = ((Element)srcContent).getContent().iterator();
		while(it.hasNext()){
			if(containsUpdateOrModificationsUnder(it.next()))return true;
		}
		return false;
	}
	
	private void registerModifier(Content e,boolean hasNewChild, boolean hasRemovedChild, boolean hasAttributeModified,boolean hasReceivedChild, boolean hasSendChild, boolean hasTextChanged){
		ModifierFlags flags = _modifiersFlags.get(e);
		if(flags==null){
			flags = new ModifierFlags(hasNewChild,hasRemovedChild,hasAttributeModified,hasAttributeModified,hasSendChild,hasTextChanged);
			_modifiersFlags.put(e, flags);
		}else{
			if(hasNewChild)flags.setHasNewChild(true);
			if(hasRemovedChild)flags.setHasRemovedChild(true);
			if(hasAttributeModified)flags.setHasAttributeModified(true);
		}
	}
	private class ModifierFlags {
		private boolean _hasNewChild;
		private boolean _hasRemovedChild;
		private boolean _hasAttributeModified;
		private boolean _hasTextChanged;
		private boolean _hasReceveidChild;
		private boolean _hasSendChild;
		
		public ModifierFlags(boolean hasNewChild, boolean hasRemovedChild, boolean hasAttributeModified,boolean hasReceivedChild, boolean hasSendChild, boolean hasTextChanged){
			super();
			_hasAttributeModified = hasAttributeModified;
			_hasNewChild = hasNewChild;
			_hasRemovedChild = hasRemovedChild;
			_hasReceveidChild = hasReceivedChild;
			_hasSendChild = hasSendChild;
			_hasTextChanged = hasTextChanged;
		}
		public void setHasTextChanged(boolean b) {
			_hasTextChanged = b;
		}

		public void setHasSendChild(boolean b) {
			_hasSendChild = b;
		}

		public void setHasReceivedChild(boolean b) {
			_hasReceveidChild = b;
		}
		public void setHasAttributeModified(boolean b) {
			_hasAttributeModified = b;
		}

		public void setHasRemovedChild(boolean b) {
			_hasRemovedChild = b;
		}

		public void setHasNewChild(boolean b) {
			_hasNewChild = b;
		}
		public boolean isModified(){
			return _hasAttributeModified || _hasNewChild || _hasRemovedChild || _hasSendChild || _hasReceveidChild || _hasTextChanged;
		}
	}
}
