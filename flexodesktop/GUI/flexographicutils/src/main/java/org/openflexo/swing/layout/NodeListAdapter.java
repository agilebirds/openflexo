package org.openflexo.swing.layout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.MultiSplitLayout.Node;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class NodeListAdapter extends TypeAdapter<List<Node>> {

	private final TypeAdapter<List<?>> collectionAdapter;
	private final TypeAdapter<Node> nodeAdapter;

	public NodeListAdapter(TypeAdapter<List<?>> collectionAdapter, TypeAdapter<Node> nodeAdapter) {
		this.collectionAdapter = collectionAdapter;
		this.nodeAdapter = nodeAdapter;
	}

	@Override
	public void write(JsonWriter out, List<Node> value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		collectionAdapter.write(out, value);
	}

	@Override
	public List<Node> read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}

		List<Node> collection = new ArrayList<Node>();
		in.beginArray();
		while (in.hasNext()) {
			Node instance = nodeAdapter.read(in);
			collection.add(instance);
		}
		in.endArray();
		return collection;
	}

}