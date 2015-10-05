package miselico.prototypes.knowledgebase;

import java.util.Collection;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public class ChangeSet {

	protected final ImmutableMultimap<Property, ID> changes;

	protected ChangeSet(ImmutableSetMultimap<Property, ID> changes) {
		this.changes = ImmutableSetMultimap.copyOf(changes);
	}

	public ImmutableCollection<ID> apply(Property p) {
		return this.changes.get(p);
	}

	public ImmutableCollection<Entry<Property, ID>> entries() {
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
		return "ChangeSet [changes=" + this.changes + "]";
	}

	public boolean isEmpty() {
		if (this == ChangeSet.EMPTY) {
			return true;
		}
		return this.changes.isEmpty();
	}

}
