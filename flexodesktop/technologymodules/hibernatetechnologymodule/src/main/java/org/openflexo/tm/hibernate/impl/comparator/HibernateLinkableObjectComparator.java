/**
 * 
 */
package org.openflexo.tm.hibernate.impl.comparator;

import java.util.Comparator;

import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;

/**
 * @author Nicolas Daniels
 * 
 */
public class HibernateLinkableObjectComparator implements Comparator<LinkableTechnologyModelObject<?>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(LinkableTechnologyModelObject<?> o1, LinkableTechnologyModelObject<?> o2) {
		if (o1.getLinkedFlexoModelObject() != null && o2.getLinkedFlexoModelObject() == null)
			return -1;
		if (o1.getLinkedFlexoModelObject() == null && o2.getLinkedFlexoModelObject() != null)
			return 1;

		if (o1.getName() == null)
			return 1;

		if (o2.getName() == null)
			return -1;

		return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
	}
}
