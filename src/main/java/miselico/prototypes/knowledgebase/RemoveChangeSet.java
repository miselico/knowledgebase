package miselico.prototypes.knowledgebase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

/**
 * A {@link ChangeSet} used for removing {@link Property}s from a
 * {@link Prototype}. An {@link RemoveChangeSet} is immutable and must be
 * created using a {@link Builder}
 * 
 * @author michael
 *
 */
public final class RemoveChangeSet extends ChangeSet {

	private final ImmutableSet<Property> removeAll;

	private RemoveChangeSet(ImmutableSetMultimap<Property, ID> changes, ImmutableSet<Property> removeAll) {
		super(changes);
		Preconditions.checkNotNull(removeAll);
		this.removeAll = removeAll;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((this.removeAll == null) ? 0 : this.removeAll.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		RemoveChangeSet other = (RemoveChangeSet) obj;
		if (this.removeAll == null) {
			if (other.removeAll != null) {
				return false;
			}
		} else if (!this.removeAll.equals(other.removeAll)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a {@link Builder} for creating an {@link RemoveChangeSet}.
	 * 
	 * @return a new {@link Builder}
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Get a human readable representation of this {@link RemoveChangeSet}. This
	 * representation is subject to changes.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('{');
		// http://stackoverflow.com/a/3395345
		String prefix = "";
		for (Property property : this.removeAll) {
			b.append(prefix);
			prefix = ",";
			b.append(property.toString());
			b.append("=*");
		}
		if (!this.removeAll.isEmpty() && !super.isEmpty()) {
			b.append(",");
		}
		prefix = "";
		for (Entry<Property, Collection<ID>> change : super.entrySet()) {
			b.append(prefix);
			prefix = ",";
			b.append(change.getKey().toString());
			b.append("=");
			b.append(change.getValue().toString());
		}
		b.append('}');

		return b.toString();
	}

	/**
	 * Remove the changes encoded in this {@link RemoveChangeSet} from the
	 * {@link MutableChangeSet} mcs
	 * 
	 * @param mcs
	 */
	public void removeFrom(MutableChangeSet mcs) {
		for (Property property : this.removeAll) {
			mcs.changes.removeAll(property);
		}
		for (Entry<Property, ID> property : super.changes.entries()) {
			mcs.changes.remove(property.getKey(), property.getValue());
		}
	}

	@Override
	public ImmutableSet<Property> affectsProperties() {
		return ImmutableSet.<Property> builder().addAll(super.affectsProperties()).addAll(this.removeAll).build();
	}

	/**
	 * Get the properties for which this {@link RemoveChangeSet} will remove all
	 * values.
	 * 
	 * @return
	 */
	public ImmutableSet<Property> getRemoveAll() {
		return this.removeAll;
	}

	/**
	 * A builder to create a {@link RemoveChangeSet} An instance can be obtained
	 * using {@link RemoveChangeSet#builder()}
	 * 
	 * The builder supports fluent syntax.
	 * 
	 * @author michael
	 *
	 */
	public static final class Builder {
		private final SetMultimap<Property, ID> rm = HashMultimap.create();
		private final Set<Property> rmAll = new HashSet<Property>();

		private Builder() {
		}

		/**
		 * Remove also the {@link ID} id for the {@link Property} p
		 * 
		 * @param p
		 * @param id
		 * @return the builder
		 */
		public Builder andRemove(Property p, ID id) {
			if (!this.rmAll.contains(p)) {
				this.rm.put(p, id);
			}
			return this;
		}

		/**
		 * Remove all {@link ID}s for the {@link Property} p
		 * 
		 * @param p
		 * @return the builder
		 */
		public Builder andRemoveAll(Property p) {
			this.rm.removeAll(p);
			this.rmAll.add(p);
			return this;
		}

		/**
		 * Build the {@link RemoveChangeSet}. The builder can still be used
		 * after the build step.
		 * 
		 * @return The created {@link RemoveChangeSet} which is independent form
		 *         the builder.
		 */
		public RemoveChangeSet build() {
			return new RemoveChangeSet(ImmutableSetMultimap.copyOf(this.rm), ImmutableSet.copyOf(this.rmAll));
		}
	}

	/**
	 * An empty {@link RemoveChangeSet}
	 * 
	 * @return
	 */
	public static RemoveChangeSet empty() {
		return RemoveChangeSet.EMPTY;
	}

	private static RemoveChangeSet EMPTY = new RemoveChangeSet(ImmutableSetMultimap.<Property, ID> of(), ImmutableSet.<Property> of());

	@Override
	public boolean isEmpty() {
		if (this == RemoveChangeSet.EMPTY) {
			return true;
		}
		return this.removeAll.isEmpty() && super.isEmpty();
	}

}
