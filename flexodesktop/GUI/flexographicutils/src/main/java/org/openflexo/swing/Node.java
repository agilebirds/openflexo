package org.openflexo.swing;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "Node")
public interface Node {

	public static final String NAME = "name";
	public static final String VISIBLE = "visible";
	public static final String ROW_LAYOUT = "rowLayout";
	public static final String PERCENTAGE = "percentage";
	public static final String CHILDREN = "children";
	public static final String PARENT = "parent";
	public static final String SIZE = "size";

	@Getter(NAME)
	@XMLAttribute(xmlTag = NAME)
	public String getName();

	@Setter(NAME)
	public void setName(String name);

	@Getter(value = VISIBLE, defaultValue = "true")
	@XMLAttribute(xmlTag = VISIBLE)
	public boolean isVisible();

	@Setter(VISIBLE)
	public void setVisible(boolean visible);

	@Getter(value = ROW_LAYOUT, defaultValue = "true")
	@XMLAttribute(xmlTag = ROW_LAYOUT)
	public boolean isRowLayout();

	@Setter(ROW_LAYOUT)
	public void setRowLayout(boolean rowLayout);

	@Getter(value = PERCENTAGE, defaultValue = "-1.0")
	@XMLAttribute(xmlTag = PERCENTAGE)
	public double getPercentage();

	@Setter(PERCENTAGE)
	public void setPercentage(double percentage);

	@Getter(value = SIZE, defaultValue = "-1")
	@XMLAttribute(xmlTag = SIZE)
	public int getSize();

	@Setter(SIZE)
	public void setSize(int size);

	@Getter(value = CHILDREN, cardinality = Cardinality.LIST, inverse = PARENT)
	@XMLElement(xmlTag = "child")
	public List<Node> getChildren();

	@Setter(CHILDREN)
	public void setChildren(List<Node> children);

	@Adder(CHILDREN)
	public void addChild(Node child);

	@Remover(CHILDREN)
	public void removeChild(Node child);

	@Getter(value = PARENT, inverse = CHILDREN)
	public Node getParent();

	@Setter(PARENT)
	public void setParent(Node parent);

}
