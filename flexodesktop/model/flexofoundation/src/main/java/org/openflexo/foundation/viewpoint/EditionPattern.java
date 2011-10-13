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
package org.openflexo.foundation.viewpoint;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.viewpoint.dm.EditionSchemeInserted;
import org.openflexo.foundation.viewpoint.dm.EditionSchemeRemoved;
import org.openflexo.foundation.viewpoint.dm.PatternRoleInserted;
import org.openflexo.foundation.viewpoint.dm.PatternRoleRemoved;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;

import com.ibm.icu.util.StringTokenizer;


public class EditionPattern extends ViewPointObject implements StringConvertable<EditionPattern> {

	protected static final Logger logger = FlexoLogger.getLogger(EditionPattern.class.getPackage().getName());

	private String name;
	
	private Vector<PatternRole> patternRoles;
	private Vector<EditionScheme> editionSchemes;
	private EditionPatternInspector inspector;
	
	private ViewPoint _calc;

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return super.getDescription();
	}
	
	public EditionPattern() 
	{
		patternRoles = new Vector<PatternRole>();
		editionSchemes = new Vector<EditionScheme>();
	}
	
	@Override
	public void delete()
	{
		if (getCalc() != null) {
			getCalc().removeFromEditionPatterns(this);
		}
		super.delete();
		deleteObservers();
	}
	
	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	public Vector<EditionScheme> getEditionSchemes() 
	{
		return editionSchemes;
	}

	public void setEditionSchemes(Vector<EditionScheme> someEditionScheme)
	{
		editionSchemes = someEditionScheme;
	}

	public void addToEditionSchemes(EditionScheme anEditionScheme)
	{
		anEditionScheme.setEditionPattern(this);
		editionSchemes.add(anEditionScheme);
		if (getCalc() != null) {
			getCalc().notifyEditionSchemeModified();
		}
		setChanged();
		notifyObservers(new EditionSchemeInserted(anEditionScheme, this));
	}

	public void removeFromEditionSchemes(EditionScheme anEditionScheme)
	{
		anEditionScheme.setEditionPattern(null);
		editionSchemes.remove(anEditionScheme);
		if (getCalc() != null) {
			getCalc().notifyEditionSchemeModified();
		}
		setChanged();
		notifyObservers(new EditionSchemeRemoved(anEditionScheme, this));
	}
	
	public Vector<PatternRole> getPatternRoles() 
	{
		return patternRoles;
	}

	public void setPatternRoles(Vector<PatternRole> somePatternRole)
	{
		patternRoles = somePatternRole;
		availablePatternRoleNames = null;
	}

	public void addToPatternRoles(PatternRole aPatternRole)
	{
		availablePatternRoleNames = null;
		aPatternRole.setEditionPattern(this);
		patternRoles.add(aPatternRole);
		setChanged();
		notifyObservers(new PatternRoleInserted(aPatternRole, this));
	}

	public void removeFromPatternRoles(PatternRole aPatternRole)
	{
		availablePatternRoleNames = null;
		aPatternRole.setEditionPattern(null);
		patternRoles.remove(aPatternRole);
		setChanged();
		notifyObservers(new PatternRoleRemoved(aPatternRole, this));
	}
	
	private Vector<String> availablePatternRoleNames = null;
	
	public Vector<String> getAvailablePatternRoleNames() 
	{
		if (availablePatternRoleNames == null) {
			availablePatternRoleNames = new Vector<String>();
			for (PatternRole r : getPatternRoles()) {
				availablePatternRoleNames.add(r.getName());
			}
		}
		return availablePatternRoleNames;
	}


	public PatternRole createShapePatternRole()
	{
		ShapePatternRole newPatternRole = new ShapePatternRole();
		newPatternRole.setPatternRoleName("shape");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public ConnectorPatternRole createConnectorPatternRole()
	{
		ConnectorPatternRole newPatternRole = new ConnectorPatternRole();
		newPatternRole.setPatternRoleName("connector");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public ShemaPatternRole createShemaPatternRole()
	{
		ShemaPatternRole newPatternRole = new ShemaPatternRole();
		newPatternRole.setPatternRoleName("shema");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public FlexoModelObjectPatternRole createFlexoModelObjectPatternRole()
	{
		FlexoModelObjectPatternRole newPatternRole = new FlexoModelObjectPatternRole();
		newPatternRole.setPatternRoleName("flexoObject");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public ClassPatternRole createClassPatternRole()
	{
		ClassPatternRole newPatternRole = new ClassPatternRole();
		newPatternRole.setPatternRoleName("class");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public IndividualPatternRole createIndividualPatternRole()
	{
		IndividualPatternRole newPatternRole = new IndividualPatternRole();
		newPatternRole.setPatternRoleName("individual");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public ObjectPropertyPatternRole createObjectPropertyPatternRole()
	{
		ObjectPropertyPatternRole newPatternRole = new ObjectPropertyPatternRole();
		newPatternRole.setPatternRoleName("property");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public DataPropertyPatternRole createDataPropertyPatternRole()
	{
		DataPropertyPatternRole newPatternRole = new DataPropertyPatternRole();
		newPatternRole.setPatternRoleName("property");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public IsAStatementPatternRole createIsAStatementPatternRole()
	{
		IsAStatementPatternRole newPatternRole = new IsAStatementPatternRole();
		newPatternRole.setPatternRoleName("fact");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public ObjectPropertyStatementPatternRole createObjectPropertyStatementPatternRole()
	{
		ObjectPropertyStatementPatternRole newPatternRole = new ObjectPropertyStatementPatternRole();
		newPatternRole.setPatternRoleName("fact");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public DataPropertyStatementPatternRole createDataPropertyStatementPatternRole()
	{
		DataPropertyStatementPatternRole newPatternRole = new DataPropertyStatementPatternRole();
		newPatternRole.setPatternRoleName("fact");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public RestrictionStatementPatternRole createRestrictionStatementPatternRole()
	{
		RestrictionStatementPatternRole newPatternRole = new RestrictionStatementPatternRole();
		newPatternRole.setPatternRoleName("fact");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public PrimitivePatternRole createPrimitivePatternRole()
	{
		PrimitivePatternRole newPatternRole = new PrimitivePatternRole();
		newPatternRole.setPatternRoleName("primitive");
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}
	
	public PatternRole deletePatternRole(PatternRole aPatternRole)
	{
		removeFromPatternRoles(aPatternRole);
		aPatternRole.delete();
		return aPatternRole;
	}
	
	public PatternRole getPatternRole(String patternRole) 
	{
		for (PatternRole pr : patternRoles) {
			if ((pr.getPatternRoleName() != null) && pr.getPatternRoleName().equals(patternRole)) {
				return pr;
			}
		}
		return null;
	}


	
	public Vector<DropScheme> getDropSchemes() 
	{
		Vector<DropScheme> returned = new Vector<DropScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof DropScheme) {
				returned.add((DropScheme)es);
			}
		}
		return returned;
	}

	public Vector<LinkScheme> getLinkSchemes() 
	{
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof LinkScheme) {
				returned.add((LinkScheme)es);
			}
		}
		return returned;
	}

	public Vector<ActionScheme> getActionSchemes() 
	{
		Vector<ActionScheme> returned = new Vector<ActionScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof ActionScheme) {
				returned.add((ActionScheme)es);
			}
		}
		return returned;
	}

	public boolean hasDropScheme()
	{
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof DropScheme) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasLinkScheme()
	{
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof LinkScheme) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasActionScheme()
	{
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof ActionScheme) {
				return true;
			}
		}
		return false;
	}
	
	public DropScheme createDropScheme()
	{
		DropScheme newDropScheme = new DropScheme();
		newDropScheme.setName("drop");
		addToEditionSchemes(newDropScheme);
		return newDropScheme;
	}
	
	public LinkScheme createLinkScheme()
	{
		LinkScheme newLinkScheme = new LinkScheme();
		newLinkScheme.setName("link");
		addToEditionSchemes(newLinkScheme);
		return newLinkScheme;
	}
	
	public ActionScheme createActionScheme()
	{
		ActionScheme newActionScheme = new ActionScheme();
		newActionScheme.setName("action");
		addToEditionSchemes(newActionScheme);
		return newActionScheme;
	}
	
	public EditionScheme deleteEditionScheme(EditionScheme editionScheme)
	{
		removeFromEditionSchemes(editionScheme);
		editionScheme.delete();
		return editionScheme;
	}

	public EditionPatternInspector getInspector() 
	{
		if (inspector == null) {
			inspector = EditionPatternInspector.makeEditionPatternInspector(this);
		}
		return inspector;
	}

	public void setInspector(EditionPatternInspector inspector)
	{
		inspector.setEditionPattern(this);
		this.inspector = inspector;
	}


	@Override
	public ViewPoint getCalc() 
	{
		return _calc;
	}

	public void setCalc(ViewPoint calc) 
	{
		_calc = calc;
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.EDITION_PATTERN_INSPECTOR;
	}

	public static class EditionPatternConverter extends StringEncoder.Converter<EditionPattern>
	{
		private final FlexoResourceCenter _resourceCenter;
		 
		public EditionPatternConverter(FlexoResourceCenter resourceCenter)
		{
			super(EditionPattern.class);
			_resourceCenter = resourceCenter;
		}

		@Override
		public EditionPattern convertFromString(String value)
		{
			String calcURI;
			String editionPattern;
			StringTokenizer st = new StringTokenizer(value,"#");
			if (st.hasMoreElements()) {
				calcURI = st.nextToken();
				ViewPoint calc = _resourceCenter.retrieveViewPointLibrary().getOntologyCalc(calcURI);
				if (calc == null) {
					logger.warning("Could not find calc "+calcURI);
				}
				else {
					if (st.hasMoreElements()) {
						editionPattern = st.nextToken();
						EditionPattern returned = calc.getEditionPattern(editionPattern);
						if (calc == null) {
							logger.warning("Could not find edition pattern "+editionPattern);
						} else {
							return returned;
						}
					}
				}
			}
			return null;
		}

		@Override
		public String convertToString(EditionPattern value) 
		{
			return value.getCalc().getViewPointURI()+"#"+value.getName();
		}
	}
	
	
	   @Override
	public EditionPatternConverter getConverter()
	    {
		   return getViewPointLibrary().editionPatternConverter;
		   
		   /*if (getProject()!=null)
			   return getProject().getEditionPatternConverter();
		   return null;*/
	    }

	  /* public EditionAction getAction(String patternRole)
	   {
		   return getEditionScheme().getAction(patternRole);
	   }

	   public AddShape getAddShapeAction(String patternRole)
	   {
		   return getEditionScheme().getAddShapeAction(patternRole);
	   }

	   public AddShemaElementAction getAddShemaElementAction(String patternRole)
	   {
		   return getEditionScheme().getAddShemaElementAction(patternRole);
	   }

	   public AddClass getAddClassAction(String patternRole)
	   {
		   return getEditionScheme().getAddClassAction(patternRole);
	   }

	   public AddIndividual getAddIndividualAction(String patternRole)
	   {
		   return getEditionScheme().getAddIndividualAction(patternRole);
	   }

	   public AddConnector getAddConnectorAction(String patternRole)
	   {
		   return getEditionScheme().getAddConnectorAction(patternRole);
	   }
	   */

	   @Override
	   public String toString()
	   {
		   return "EditionPattern:"+getName();
	   }

	public void finalizeEditionPatternDeserialization()
	{
		for (EditionScheme es : getEditionSchemes()) {
			es.finalizeEditionSchemeDeserialization();
		}
		for (PatternRole pr : getPatternRoles()) {
			pr.finalizePatternRoleDeserialization();
		}
	}

	public void debug()
	{
		System.out.println(getXMLRepresentation());
	}
	
	public void save()
	{
		getCalc().save();
	}
	
}
