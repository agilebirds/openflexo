package org.openflexo.swing.layout;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.swing.layout.MultiSplitLayout.Node;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

public class MultiSplitLayoutTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<? super T> rawType = type.getRawType();
		if (Node.class.isAssignableFrom(rawType)) {
			TypeAdapter<List<Node>> collectionAdapter = gson.getAdapter(new TypeToken<List<Node>>() {
			});
			return (TypeAdapter<T>) new NodeAdapter(collectionAdapter);
		} else if (List.class.isAssignableFrom(rawType)) {
			Type type2 = type.getType();
			if (type2 instanceof ParameterizedType && ((ParameterizedType) type2).getActualTypeArguments().length == 1) {
				Type type3 = ((ParameterizedType) type2).getActualTypeArguments()[0];
				if (Node.class.isAssignableFrom($Gson$Types.getRawType(type3))) {
					TypeAdapter<Node> nodeAdapter = gson.getAdapter(Node.class);
					TypeAdapter<List<?>> collectionAdapter = gson.getAdapter(new TypeToken<List<?>>() {
					});
					return (TypeAdapter<T>) new NodeListAdapter(collectionAdapter, nodeAdapter);
				}
			}
		}
		return null;
	}

}