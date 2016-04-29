package miselico.prototypes.serializers.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.Prototypes;
import miselico.prototypes.knowledgebase.Prototypes.Builder;

/**
 * Package private class containing classes to help serializing prototypes to
 * JSON using {@link Gson}.
 * 
 * The classes here are basically simple data containers used by reflection.
 * 
 * @author michael
 *
 */
class GsonHelper {

	private GsonHelper() {
		// Utility class
	}

	/**
	 * The {@link Gson} insance used for serialization and deserialization
	 */
	static final Gson gson = new Gson();

	static class JSONPrototypes extends ArrayList<JSONPrototype> {

		@SuppressWarnings("unused")
		private JSONPrototypes() {
		}

		JSONPrototypes(Iterator<Prototype> protos) {
			while (protos.hasNext()) {
				Prototype prototype = protos.next();
				this.add(new JSONPrototype(prototype));
			}
		}

		public List<Prototype> asPrototypeList() {
			return this.stream().map(jp -> jp.asPrototype()).collect(Collectors.toList());
		}
	}

	static class JSONPrototype {

		public String id;
		public String base;
		public Map<String, Collection<String>> add;
		public Map<String, Collection<String>> rem;
		public Set<String> remAll;

		@SuppressWarnings("unused")
		private JSONPrototype() {
		}

		JSONPrototype(Prototype p) {
			this.id = p.id.toString();
			this.base = p.def.parent.toString();
			HashMultimap<String, String> addtmp = HashMultimap.create();
			for (Entry<Property, ID> addEl : p.def.add.entries()) {
				addtmp.put(addEl.getKey().toString(), addEl.getValue().toString());
			}
			this.add = addtmp.asMap();

			HashMultimap<String, String> remtmp = HashMultimap.create();
			for (Entry<Property, ID> remEl : p.def.remove.entries()) {
				remtmp.put(remEl.getKey().toString(), remEl.getValue().toString());
			}
			this.rem = remtmp.asMap();
			this.remAll = p.def.remove.getRemoveAll().stream().map(m -> m.toString()).collect(Collectors.toSet());
		}

		Prototype asPrototype() {
			Builder builder = Prototypes.builder(ID.of(this.base));
			for (String remAllEl : this.remAll) {
				builder.removeAll(Property.of(remAllEl));
			}
			for (Entry<String, Collection<String>> removal : this.rem.entrySet()) {
				Property remProp = Property.of(removal.getKey());
				for (String remVal : removal.getValue()) {
					builder.remove(remProp, ID.of(remVal));
				}
			}
			for (Entry<String, Collection<String>> addition : this.add.entrySet()) {
				Property addProp = Property.of(addition.getKey());
				for (String addVal : addition.getValue()) {
					builder.add(addProp, ID.of(addVal));
				}
			}
			return builder.build(ID.of(this.id));
		}

	}

}
