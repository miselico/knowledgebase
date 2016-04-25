package miselico.prototypes.knowledgebase;

import java.util.Collection;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public class ChangeSet {

	protected final ImmutableSetMultimap<Property, ID> changes;

	protected ChangeSet(ImmutableSetMultimap<Property, ID> changes) {
		this.changes = ImmutableSetMultimap.copyOf(changes);
	}

	public ImmutableSet<ID> apply(Property p) {
		return this.changes.get(p);
	}

	public ImmutableSet<Entry<Property, ID>> entries() {
		return this.changes.entries();
	}

	public ImmutableSet<Entry<Property, Collection<ID>>> entrySet() {
		return this.changes.asMap().entrySet();
	}

	public ImmutableSet<Property> affectsProperties() {
		return this.changes.keySet();
	}

	/**
	 * An empty change set
	 * 
	 * @return
	 */
	public static ChangeSet empty() {
		return ChangeSet.EMPTY;
	}

	private static ChangeSet EMPTY = new ChangeSet(ImmutableSetMultimap.<Property, ID> of());

	@Override
	public String toString() {
		return this.changes.toString();
	}

	public boolean isEmpty() {
		if (this == ChangeSet.EMPTY) {
			return true;
		}
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
