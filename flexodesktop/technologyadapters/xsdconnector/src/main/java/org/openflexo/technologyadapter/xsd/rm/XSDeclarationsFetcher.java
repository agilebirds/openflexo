/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.technologyadapter.xsd.rm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;

import org.openflexo.toolbox.StringUtils;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.visitor.XSVisitor;

public class XSDeclarationsFetcher implements XSVisitor {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSDeclarationsFetcher.class
			.getPackage().getName());

	private final Set<XSSimpleType> simpleTypes = new HashSet<XSSimpleType>();
	private final Set<XSComplexType> complexTypes = new HashSet<XSComplexType>();
	private final Set<XSElementDecl> elementDecls = new HashSet<XSElementDecl>();
	private final Set<XSAttGroupDecl> attGroupDecls = new HashSet<XSAttGroupDecl>();
	private final Set<XSAttributeDecl> attributeDecls = new HashSet<XSAttributeDecl>();
	private final Set<XSModelGroupDecl> modelGroupDecls = new HashSet<XSModelGroupDecl>();

	private final Map<XSDeclaration, Set<XSAttributeUse>> attributeUses = new HashMap<XSDeclaration, Set<XSAttributeUse>>();

	private final Stack<XSDeclaration> path = new Stack<XSDeclaration>();

	private final Map<XSDeclaration, XSDeclaration> localOwners = new HashMap<XSDeclaration, XSDeclaration>();

	private final Map<String, XSDeclaration> declarations = new HashMap<String, XSDeclaration>();

	public void fetch(XSSchemaSet schemaSet) {
		for (XSSchema schema : schemaSet.getSchemas()) {
			if (StringUtils.isNotEmpty(schema.getTargetNamespace())) {
				schema.visit(this);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("A schema was ignored because of a lack of target namespace.");
				}
			}
		}
		// logUris();
	}

	public void fetch(XSSchema schema) {
		schema.visit(this);
		// logUris();
	}

	public XSDeclaration getDeclaration(String uri) {
		return declarations.get(uri);
	}

	public XSDeclaration getOwner(XSDeclaration declaration) {
		if (declaration.isLocal()) {
			return localOwners.get(declaration);
		}
		return null;
	}

	public String getOwnerURI(String uri) {
		XSDeclaration declaration = getDeclaration(uri);
		XSDeclaration declOwner = getOwner(declaration);
		if (declOwner instanceof XSElementDecl ){
			XSType xsType = ((XSElementDecl) declOwner).getType();
			if (xsType.getName() != null ){
				return this.getUri(xsType);
			}
			else {
				return this.getUri(declOwner);
			}
		}
		if (declOwner != null) {
			return getUri(declOwner);
		}
		return null;
	}

	public String getNamespace(XSDeclaration declaration) {
		if (declaration.isLocal()) {
			XSDeclaration owner = getOwner(declaration);
			return getNamespace(getOwner(declaration)) + "/" + owner.getName();
		}
		return declaration.getTargetNamespace();
	}

	public String getUri(XSDeclaration declaration) {
		return getNamespace(declaration) + "#" + declaration.getName();
	}

	public Set<XSAttributeUse> getAttributeUses(XSDeclaration declaration) {
		return attributeUses.get(declaration);
	}

	private boolean register(XSDeclaration decl) {
		if (decl.isLocal()) {
			localOwners.put(decl, path.lastElement());
		}
		String uri = getUri(decl);
		if (declarations.containsKey(uri)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Duplicate URI " + uri);
			}
			return false;
		} else {
			declarations.put(uri, decl);
			return true;
		}
	}

	public void logUris() {
		if (logger.isLoggable(Level.INFO)) {
			StringBuffer buffer = new StringBuffer("Registered URIs:\n");
			for (String uri : declarations.keySet()) {
				buffer.append(uri);
				buffer.append("\n");
			}
			logger.info(buffer.toString());
		}
	}

	public Set<XSSimpleType> getSimpleTypes() {
		return simpleTypes;
	}

	public Set<XSComplexType> getComplexTypes() {
		return complexTypes;
	}

	public Set<XSElementDecl> getElementDecls() {
		return elementDecls;
	}

	public Set<XSAttGroupDecl> getAttGroupDecls() {
		return attGroupDecls;
	}

	public Set<XSAttributeDecl> getAttributeDecls() {
		return attributeDecls;
	}

	public Set<XSModelGroupDecl> getModelGroupDecls() {
		return modelGroupDecls;
	}

	@Override
	public void schema(XSSchema schema) {
		for (XSSimpleType simpleType : schema.getSimpleTypes().values()) {
			simpleType(simpleType);
		}
		for (XSComplexType complexType : schema.getComplexTypes().values()) {
			complexType(complexType);
		}
		for (XSElementDecl elementDecl : schema.getElementDecls().values()) {
			elementDecl(elementDecl);
		}
		for (XSAttGroupDecl attGroupDecl : schema.getAttGroupDecls().values()) {
			attGroupDecl(attGroupDecl);
		}
		for (XSAttributeDecl attributeDecl : schema.getAttributeDecls().values()) {
			attributeDecl(attributeDecl);
		}
		for (XSModelGroupDecl modelGroupDecl : schema.getModelGroupDecls().values()) {
			modelGroupDecl(modelGroupDecl);
		}
	}

	@Override
	public void simpleType(XSSimpleType simpleType) {
		if (simpleType.isGlobal()) {
			if (register(simpleType) == false) {
				return;
			}
			simpleTypes.add(simpleType);
		}
	}

	@Override
	public void complexType(XSComplexType complexType) {

		
		if (complexType.isGlobal()) {
			if (register(complexType) == false) {
				return;
			}
			complexTypes.add(complexType);
			path.push(complexType);
		}

		attContainer(complexType);
		complexType.getContentType().visit(this);

		if (complexType.isGlobal()) {
			path.pop();
		}

	}

	private void attContainer(XSAttContainer attContainer) {
		for (XSAttributeUse attributeUse : attContainer.getDeclaredAttributeUses()) {
			attributeUse(attributeUse);
		}
	}

	private void attContainer(XSElementDecl attContainer) {
		XSComplexType xst = (XSComplexType) attContainer.getType();
		for (XSAttributeUse attributeUse : xst.getDeclaredAttributeUses()) {
			attributeUse(attributeUse);
		}
	}

	@Override
	public void attributeUse(XSAttributeUse attributeUse) {
		if (attributeUses.containsKey(path.lastElement()) == false) {
			attributeUses.put(path.lastElement(), new HashSet<XSAttributeUse>());
		}
		attributeUses.get(path.lastElement()).add(attributeUse);
		attributeDecl(attributeUse.getDecl());
	}

	@Override
	public void attributeDecl(XSAttributeDecl attributeDecl) {
		// Careful, it can be local and not have a name
		if (register(attributeDecl) == false) {
			return;
		}
		attributeDecls.add(attributeDecl);
	}

	@Override
	public void attGroupDecl(XSAttGroupDecl attGroupDecl) {
		if (attGroupDecl.isGlobal()) {
			if (register(attGroupDecl) == false) {
				return;
			}
			attGroupDecls.add(attGroupDecl);
			path.push(attGroupDecl);
		}

		attContainer(attGroupDecl);

		if (attGroupDecl.isGlobal()) {
			path.pop();
		}
	}

	@Override
	public void modelGroupDecl(XSModelGroupDecl modelGroupDecl) {
		if (modelGroupDecl.isGlobal()) {
			if (register(modelGroupDecl) == false) {
				return;
			}
			modelGroupDecls.add(modelGroupDecl);
			path.push(modelGroupDecl);
		}

		modelGroup(modelGroupDecl.getModelGroup());

		if (modelGroupDecl.isGlobal()) {
			path.pop();
		}
	}

	@Override
	public void modelGroup(XSModelGroup modelGroup) {
		for (XSParticle particle : modelGroup.getChildren()) {
			particle(particle);
		}
	}

	@Override
	public void elementDecl(XSElementDecl elementDecl) {
		// TODO Make sure there is no need to test if global first
		if (register(elementDecl) == false) {
			return;
		}
		elementDecls.add(elementDecl);
		path.push(elementDecl);

		if (elementDecl.getType().isLocal()) {
			logger.info("XML DEBUG CG: there is a local type here? " + elementDecl.getName());
			// TODO If it's global it has already been visited, make sure.
			elementDecl.getType().visit(this);			
		}
		else if (elementDecl.getType().isComplexType()){
			attContainer(elementDecl);
		}

		path.pop();
	}

	@Override
	public void particle(XSParticle particle) {
		particle.getTerm().visit(this);
	}

	@Override
	public void empty(XSContentType empty) {

	}

	@Override
	public void annotation(XSAnnotation ann) {

	}

	@Override
	public void facet(XSFacet facet) {

	}

	@Override
	public void notation(XSNotation notation) {

	}

	@Override
	public void wildcard(XSWildcard wc) {

	}

	@Override
	public void identityConstraint(XSIdentityConstraint decl) {

	}

	@Override
	public void xpath(XSXPath xp) {

	}

}