package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.technologyadapter.owl.model.OWLProperty;

@ModelEntity
@ImplementationClass(OWLPropertyPatternRole.OWLPropertyPatternRoleImpl.class)
@XMLElement
public interface OWLPropertyPatternRole extends PropertyPatternRole<OWLProperty>{

@PropertyIdentifier(type=String.class)
public static final String PARENT_PROPERTY_URI_KEY = "parentPropertyURI";
@PropertyIdentifier(type=String.class)
public static final String DOMAIN_URI_KEY = "domainURI";

@Getter(value=PARENT_PROPERTY_URI_KEY)
@XMLAttribute(xmlTag="parentProperty")
public String _getParentPropertyURI();

@Setter(PARENT_PROPERTY_URI_KEY)
public void _setParentPropertyURI(String parentPropertyURI);


@Getter(value=DOMAIN_URI_KEY)
@XMLAttribute(xmlTag="domain")
public String _getDomainURI();

@Setter(DOMAIN_URI_KEY)
public void _setDomainURI(String domainURI);


public static abstract  class OWLPropertyPatternRoleImpl extends PropertyPatternRole<OWLProperty>Impl implements OWLPropertyPatternRole
{

	public OWLPropertyPatternRoleImpl() {
		super();
	}

}
}
