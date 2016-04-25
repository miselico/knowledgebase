package miselico.prototypes.knowledgebase;

import java.util.Collection;
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

	public static Builder builder() {
		return new Builder();
	}

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

	public ImmutableSet<Property> getRemoveAll() {
		return this.removeAll;
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

	@Override
	public boolean isEmpty() {
		if (this == RemoveChangeSet.EMPTY) {
			return true;
		}
		return this.removeAll.isEmpty() && super.isEmpty();
	}

}
