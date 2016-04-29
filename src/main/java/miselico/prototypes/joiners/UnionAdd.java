package miselico.prototypes.joiners;

import java.util.Map.Entry;

import miselico.prototypes.knowledgebase.AddChangeSet;
import miselico.prototypes.knowledgebase.AddChangeSet.Builder;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;

/**
 * Joins two {@link AddChangeSet}s by taking their union them.
 * 
 * @author michael
 *
 */
public class UnionAdd implements AddStrategy {

	@Override
	public AddChangeSet join(AddChangeSet a, AddChangeSet b) {
		Builder builder = AddChangeSet.builder();
		for (Entry<Property, ID> add : a.entries()) {
			builder.andAdd(add.getKey(), add.getValue());
		}
		for (Entry<Property, ID> add : b.entries()) {
			builder.andAdd(add.getKey(), add.getValue());
		}
		return builder.build();
	}

}
