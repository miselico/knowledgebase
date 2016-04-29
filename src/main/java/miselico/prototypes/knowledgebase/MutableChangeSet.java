package miselico.prototypes.knowledgebase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Normal changesets are immutable. When there is a need for a mutable
 * changeset, this one can be used. This does however not implement the
 * {@link ChangeSet} interface because it would violate the immutability
 * contract.
 * 
 * This class works together with {@link AddChangeSet#addTo(MutableChangeSet)},
 * {@link RemoveChangeSet#removeFrom(MutableChangeSet)},
 * {@link AddChangeSet#mutableCopy()}, and
 * {@link AddChangeSet#fromMutable(MutableChangeSet)}
 * 
 * @author michael
 */
class MutableChangeSet {

	/**
	 * The changes of the changeset
	 */
	public final Multimap<Property, ID> changes;

	/**
	 * Create an empty {@link MutableChangeSet}
	 */
	public MutableChangeSet() {
		this.changes = HashMultimap.create();
	}

	/**
	 * create a {@link MutableChangeSet} which contains a copy of the given
	 * source
	 * 
	 * @param source
	 *            the initial changes to be added to this set. The set will be
	 *            copied.
	 */
	public MutableChangeSet(Multimap<Property, ID> source) {
		this.changes = HashMultimap.create(source);
	}

	// public MutableChangeSet copy() {
	// return new MutableChangeSet(this.changes);
	// }
}
