package miselico.prototypes.knowledgebase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

import miselico.prototypes.joiners.AddStrategy;

/**
 * A {@link ChangeSet} used for adding {@link Property}s to a {@link Prototype}.
 * An AddChangeSet is immutable and must be created using a {@link Builder}
 * 
 * @author michael
 *
 */
public final class AddChangeSet extends ChangeSet {

	private AddChangeSet(ImmutableSetMultimap<Property, ID> changes) {
		super(changes);
	}

	/**
	 * Convert a {@link MutableChangeSet} into an {@link AddChangeSet}.
	 * 
	 * @param mcs
	 * @return
	 */
	public static AddChangeSet fromMutable(MutableChangeSet mcs) {
		return new AddChangeSet(ImmutableSetMultimap.copyOf(mcs.changes));
	}

	/**
	 * Get a mutable,copy of this ChangeSet
	 * 
	 * @return a {@link MutableChangeSet} with the same content as this
	 *         {@link ChangeSet}
	 */
	public MutableChangeSet mutableCopy() {
		return new MutableChangeSet(this.changes);

	}

	/**
	 * Returns a {@link Builder} for creating an {@link AddChangeSet}.
	 * 
	 * @return a new {@link Builder}
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Add all changes of this changeset to the changes of the
	 * {@link MutableChangeSet} mcs. In effect this means taking the union of
	 * all changes.
	 * 
	 * @param mcs
	 *            The {@link MutableChangeSet} which will be modified.
	 * @see AddStrategy
	 */
	public void addTo(MutableChangeSet mcs) {
		mcs.changes.putAll(super.changes);
	}

	/**
	 * A class for building an {@link AddChangeSet}. Created using
	 * {@link AddChangeSet#builder()}
	 * 
	 * @author michael
	 *
	 */
	public static final class Builder {
		SetMultimap<Property, ID> add = HashMultimap.create();

		private Builder() {
		}

		// public Builder andAddAll(AddChangeSet other) {
		// this.add.putAll(other.changes);
		// return this;
		// }

		/**
		 * Add a new property and id pair to this builder.
		 * 
		 * @param p
		 * @param id
		 * @return the builder
		 */
		public Builder andAdd(Property p, ID id) {
			this.add.put(p, id);
			return this;
		}

		/**
		 * Create the {@link AddChangeSet}. The builder keeps its contents and
		 * can be used multiple times.
		 * 
		 * @return
		 */
		public AddChangeSet build() {
			if (this.add.isEmpty()) {
				return AddChangeSet.empty();
			} else {
				return new AddChangeSet(ImmutableSetMultimap.copyOf(this.add));
			}
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
