package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLProperty;

@ModelEntity
@ImplementationClass(OWLPropertyPatternRole.OWLPropertyPatternRoleImpl.class)
@XMLElement
public interface OWLPropertyPatternRole extends PropertyPatternRole<OWLProperty> {

	@PropertyIdentifier(type = String.class)
	public static final String PARENT_PROPERTY_URI_KEY = "parentPropertyURI";
	@PropertyIdentifier(type = String.class)
	public static final String DOMAIN_URI_KEY = "domainURI";

	@Override
	@Getter(value = PARENT_PROPERTY_URI_KEY)
	@XMLAttribute(xmlTag = "parentProperty")
	public String _getParentPropertyURI();

	@Override
	@Setter(PARENT_PROPERTY_URI_KEY)
	public void _setParentPropertyURI(String parentPropertyURI);

	@Override
	@Getter(value = DOMAIN_URI_KEY)
	@XMLAttribute(xmlTag = "domain")
	public String _getDomainURI();

	@Override
	@Setter(DOMAIN_URI_KEY)
	public void _setDomainURI(String domainURI);

	public static abstract class OWLPropertyPatternRoleImpl extends PropertyPatternRoleImpl<OWLProperty> implements OWLPropertyPatternRole {

		public OWLPropertyPatternRoleImpl() {
			super();
		}

	}
}
