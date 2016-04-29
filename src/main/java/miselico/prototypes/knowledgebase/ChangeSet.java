package miselico.prototypes.knowledgebase;

import java.util.Collection;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

/**
 * A collections of changes to be made to a prototype. This collection is
 * usually used trough its subclasses.
 * 
 * The changeset is immutable
 * 
 * @author michael
 *
 */
public class ChangeSet {

	protected final ImmutableSetMultimap<Property, ID> changes;

	protected ChangeSet(ImmutableSetMultimap<Property, ID> changes) {
		Preconditions.checkNotNull(changes);
		this.changes = ImmutableSetMultimap.copyOf(changes);
	}

	/**
	 * Get the changes for the given property.
	 * 
	 * @param p
	 *            The property
	 * @return The changes
	 */
	public ImmutableSet<ID> apply(Property p) {
		return this.changes.get(p);
	}

	/**
	 * Get a set of all the changes this {@link ChangeSet} makes. One entry for
	 * each (property,ID) pair this changeset touches.
	 * 
	 * @return The changes.
	 */
	public ImmutableSet<Entry<Property, ID>> entries() {
		return this.changes.entries();
	}

	/**
	 * Get a set of all the changes this {@link ChangeSet} makes. One entry for
	 * each property this changeset touches.
	 * 
	 * @return
	 */
	public ImmutableSet<Entry<Property, Collection<ID>>> entrySet() {
		return this.changes.asMap().entrySet();
	}

	/**
	 * The set of properties affected by this changeset.
	 * 
	 * @return
	 */
	public ImmutableSet<Property> affectsProperties() {
		return this.changes.keySet();
	}

	/**
	 * Get a human readable representation of this {@link RemoveChangeSet}. This
	 * representation is subject to changes.
	 */
	@Override
	public String toString() {
		return this.changes.toString();
	}

	/**
	 * 
	 * @return True if this changeset does not affect any properties.
	 */
	public boolean isEmpty() {
		return this.changes.isEmpty();
	}

	@Override
	public int hashCode() {
		return this.changes.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		ChangeSet other = (ChangeSet) obj;
		return this.changes.equals(other.changes);
	}

}
