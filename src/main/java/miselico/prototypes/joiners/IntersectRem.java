package miselico.prototypes.joiners;

import java.util.Collection;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableSet;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.RemoveChangeSet;
import miselico.prototypes.knowledgebase.RemoveChangeSet.Builder;

/**
 * Joins two {@link RemoveChangeSet}s by intersecting them.
 * 
 * @author michael
 *
 */
public class IntersectRem implements RemStrategy {

	@Override
	public RemoveChangeSet join(RemoveChangeSet a, RemoveChangeSet b) {
		Builder builder = RemoveChangeSet.builder();
		ImmutableSet<Property> bAll = b.getRemoveAll();
		for (Property prop : a.getRemoveAll()) {
			if (bAll.contains(prop)) {
				builder.andRemoveAll(prop);
			}
		}
		// ImmutableSet<Entry<Property, ID>> bEntries = b.entries();
		for (Entry<Property, Collection<ID>> rem : a.entrySet()) {
			ImmutableSet<ID> bIDs = b.apply(rem.getKey());
			ImmutableSet<ID> aIDs = ImmutableSet.copyOf(rem.getValue());

			for (ID id : aIDs) {
				if (bIDs.contains(id) || bAll.contains(rem.getKey())) {
					builder.andRemove(rem.getKey(), id);
				}
			}
		}
		ImmutableSet<Property> aAll = a.getRemoveAll();
		for (Entry<Property, ID> rem : b.entries()) {
			if (aAll.contains(rem.getKey())) {
				builder.andRemove(rem.getKey(), rem.getValue());
			}
		}
		return builder.build();
	}

}
