package miselico.prototypes.joiners;

import java.util.Map.Entry;

import com.google.common.collect.ImmutableCollection;

import miselico.prototypes.knowledgebase.AddChangeSet;
import miselico.prototypes.knowledgebase.AddChangeSet.Builder;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;

public class IntersectAdd implements AddStrategy {

	@Override
	public AddChangeSet join(AddChangeSet a, AddChangeSet b) {
		Builder builder = AddChangeSet.builder();
		ImmutableCollection<Entry<Property, ID>> bEntries = b.entries();
		for (Entry<Property, ID> add : a.entries()) {
			if (bEntries.contains(add)) {
				builder.andAdd(add.getKey(), add.getValue());
			}
		}
		return builder.build();
	}

}
