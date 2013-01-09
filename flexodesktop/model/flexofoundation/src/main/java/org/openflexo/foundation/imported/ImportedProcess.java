package org.openflexo.foundation.imported;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@XMLElement
public interface ImportedProcess extends ImportedObject {

	public static final String WORKFLOW = "workflow";

	public static final String CHILDREN = "children";

	public static final String PARENT = "parent";

	@Getter(value = PARENT, inverse = CHILDREN)
	public ImportedProcess getParent();

	@Setter(PARENT)
	public void setParent(ImportedProcess parent);

	@Getter(value = CHILDREN, cardinality = Cardinality.LIST, inverse = PARENT)
	public List<ImportedProcess> getChildren();

	@Setter(CHILDREN)
	public void setChildren(List<ImportedProcess> children);

	@Adder(CHILDREN)
	public void addToChildren(ImportedProcess child);

	@Remover(CHILDREN)
	public void removeFromChildren(ImportedProcess child);
}
