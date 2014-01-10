package org.openflexo.model4;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.DeserializationInitializer;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
@ImplementationClass(Node.NodeImpl.class)
@XMLElement
public interface Node extends AccessibleProxyObject {

	public static final String NAME = "name";
	public static final String PARENT_NODE = "parent";
	public static final String NODES = "nodes";

	@Getter(value = NAME, defaultValue = "???")
	@XMLAttribute(xmlTag = NAME)
	public String getName();

	@Setter(NAME)
	public void setName(String name);

	@Getter(value = PARENT_NODE, inverse = NODES)
	public Node getParentNode();

	@Setter(PARENT_NODE)
	public void setParentNode(Node aNode);

	@Getter(value = NODES, cardinality = Cardinality.LIST, inverse = PARENT_NODE)
	@XMLElement(primary = true)
	@Embedded
	public List<Node> getNodes();

	@Setter(NODES)
	public void setNodes(List<Node> nodes);

	@Adder(NODES)
	public void addToNodes(Node node);

	@Remover(NODES)
	public void removeFromNodes(Node node);

	@DeserializationInitializer
	public void initializeDeserialization();

	@DeserializationFinalizer
	public void finalizeDeserialization();

	public static abstract class NodeImpl implements Node {

		public static String DESERIALIZATION_TRACE = "";

		private boolean isDeserializing = false;

		@Override
		public void initializeDeserialization() {
			System.out.println("Init deserialization for Node " + getName());
			isDeserializing = true;
		}

		@Override
		public void setName(String name) {
			if (isDeserializing) {
				DESERIALIZATION_TRACE += " BEGIN:" + name;
			}
			performSuperSetter(NAME, name);
		}

		@Override
		public void finalizeDeserialization() {
			isDeserializing = false;
			DESERIALIZATION_TRACE += " END:" + getName();
			System.out.println("Finalize deserialization for Node " + getName());
		}
	}

}
