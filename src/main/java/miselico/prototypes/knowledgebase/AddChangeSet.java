package miselico.prototypes.knowledgebase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

public class AddChangeSet extends ChangeSet {

	private AddChangeSet(ImmutableSetMultimap<Property, ID> changes) {
		super(changes);
	}

	public static AddChangeSet fromMutable(MutableChangeSet mcs) {
		return new AddChangeSet(ImmutableSetMultimap.copyOf(mcs.changes));
	}

	public MutableChangeSet mutableCopy() {
		return new MutableChangeSet(this.changes);

	}

	public static Builder builder() {
		return new Builder();
	}

	public void addTo(MutableChangeSet mcs) {
		mcs.changes.putAll(super.changes);
	}

	public static class Builder {
		SetMultimap<Property, ID> add = HashMultimap.create();

		private Builder() {
		}

		// public Builder andAddAll(AddChangeSet other) {
		// this.add.putAll(other.changes);
		// return this;
		// }

		public Builder andAdd(Property p, ID id) {
			this.add.put(p, id);
			return this;
		}

		public AddChangeSet build() {
			return new AddChangeSet(ImmutableSetMultimap.copyOf(this.add));
		}
	}

	/**
	 * An empty change set
	 * 
	 * @return
	 */
	public static AddChangeSet empty() {
		return AddChangeSet.EMPTY;
	}

	private static AddChangeSet EMPTY = new AddChangeSet(ImmutableSetMultimap.<Property, ID> of());

}
