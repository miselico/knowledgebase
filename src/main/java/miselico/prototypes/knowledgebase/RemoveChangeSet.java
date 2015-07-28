package miselico.prototypes.knowledgebase;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

public class RemoveChangeSet extends ChangeSet {

	private final ImmutableSet<Property> removeAll;

	private RemoveChangeSet(ImmutableSetMultimap<Property, ID> changes, ImmutableSet<Property> removeAll) {
		super(changes);
		this.removeAll = removeAll;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "RemoveChangeSet [removeAll=" + this.removeAll + ", remove=" + super.toString() + "]";
	}

	public void removeFrom(MutableChangeSet mcs) {
		for (Property property : this.removeAll) {
			mcs.changes.removeAll(property);
		}
		for (Entry<Property, ID> property : super.changes.entries()) {
			mcs.changes.remove(property.getKey(), property.getValue());
		}
	}

	public static class Builder {
		SetMultimap<Property, ID> rm = HashMultimap.create();
		Set<Property> rmAll = new HashSet<Property>();

		private Builder() {
		}

		public Builder andRemove(Property p, ID id) {
			if (!this.rmAll.contains(p)) {
				this.rm.put(p, id);
			}
			return this;
		}

		public Builder andRemoveAll(Property p) {
			this.rm.removeAll(p);
			this.rmAll.add(p);
			return this;
		}

		public RemoveChangeSet build() {
			return new RemoveChangeSet(ImmutableSetMultimap.copyOf(this.rm), ImmutableSet.copyOf(this.rmAll));
		}
	}

	/**
	 * An empty change set
	 * 
	 * @return
	 */
	public static RemoveChangeSet empty() {
		return RemoveChangeSet.EMPTY;
	}

	private static RemoveChangeSet EMPTY = new RemoveChangeSet(ImmutableSetMultimap.<Property, ID> of(), ImmutableSet.<Property> of());

}
