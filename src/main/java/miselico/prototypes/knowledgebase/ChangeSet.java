package miselico.prototypes.knowledgebase;

import java.util.Map.Entry;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;

public class ChangeSet {

	protected ChangeSet(ImmutableSetMultimap<Property, ID> changes) {
		this.changes = ImmutableSetMultimap.copyOf(changes);
	}

	protected final ImmutableMultimap<Property, ID> changes;

	public ImmutableCollection<ID> apply(Property p) {
		return this.changes.get(p);
	}

	public ImmutableCollection<Entry<Property, ID>> entries() {
		return this.changes.entries();
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

}
