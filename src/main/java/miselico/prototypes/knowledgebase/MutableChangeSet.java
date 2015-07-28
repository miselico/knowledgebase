package miselico.prototypes.knowledgebase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class MutableChangeSet {

	public final Multimap<Property, ID> changes;

	public MutableChangeSet() {
		this.changes = HashMultimap.create();
	}

	public MutableChangeSet(Multimap<Property, ID> source) {
		this.changes = HashMultimap.create(source);
	}

	// public MutableChangeSet copy() {
	// return new MutableChangeSet(this.changes);
	// }
}
