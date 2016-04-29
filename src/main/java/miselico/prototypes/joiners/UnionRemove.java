package miselico.prototypes.joiners;

import java.util.Map.Entry;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.RemoveChangeSet;
import miselico.prototypes.knowledgebase.RemoveChangeSet.Builder;

/**
 * Joins two {@link RemoveChangeSet}s by taking their union them.
 * 
 * @author michael
 *
 */
public class UnionRemove implements RemStrategy {

	@Override
	public RemoveChangeSet join(RemoveChangeSet a, RemoveChangeSet b) {
		Builder builder = RemoveChangeSet.builder();
		for (Property prop : a.getRemoveAll()) {
			builder.andRemoveAll(prop);
		}
		for (Property prop : b.getRemoveAll()) {
			builder.andRemoveAll(prop);
		}
		for (Entry<Property, ID> rem : a.entries()) {
			builder.andRemove(rem.getKey(), rem.getValue());
		}
		for (Entry<Property, ID> rem : b.entries()) {
			builder.andRemove(rem.getKey(), rem.getValue());
		}
		return builder.build();
	}

}
