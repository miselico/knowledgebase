package miselico.prototypes.knowledgebase;

import com.google.common.base.Preconditions;

/**
 * This class represents the prototype definition. A definition consists of the
 * base {@link ID}, an {@link AddChangeSet} and a {@link RemoveChangeSet}.
 * 
 * @author michael
 *
 */
public class PrototypeDefinition {

	public final ID parent;
	public final RemoveChangeSet remove;
	public final AddChangeSet add;

	private PrototypeDefinition(ID parent, RemoveChangeSet remove, AddChangeSet add) {
		this.parent = parent;
		this.remove = remove;
		this.add = add;
	}

	/**
	 * Equivalent to
	 * {@link PrototypeDefinition#create(ID, RemoveChangeSet, AddChangeSet)},
	 * but the ID is taken from the provided prototype.
	 * 
	 * @param parent
	 * @param remove
	 * @param add
	 * @return
	 */
	public static PrototypeDefinition create(Prototype parent, RemoveChangeSet remove, AddChangeSet add) {
		return PrototypeDefinition.create(parent.id, remove, add);
	}

	/**
	 * Create a {@link PrototypeDefinition} with the given components.
	 * 
	 * @param parent
	 * @param remove
	 * @param add
	 * @return
	 */
	public static PrototypeDefinition create(ID parent, RemoveChangeSet remove, AddChangeSet add) {
		Preconditions.checkNotNull(parent);
		Preconditions.checkNotNull(remove);
		Preconditions.checkNotNull(add);
		PrototypeDefinition p = new PrototypeDefinition(parent, remove, add);
		return p;
	}

	/**
	 * The prototype definition of the empty prototype.
	 */
	public static final PrototypeDefinition P_0;

	static {
		ID parent = ID.of("proto:P_0");
		RemoveChangeSet remove = RemoveChangeSet.empty();
		AddChangeSet add = AddChangeSet.empty();
		P_0 = new PrototypeDefinition(parent, remove, add);
	}

	/**
	 * A prototype def is fixpoint if it derives from P_O and has an empty
	 * remove set
	 * 
	 * @return
	 */
	public boolean isFixPoint() {
		return (this.parent.equals(Prototype.P_0.id) && this.remove.isEmpty());
	}

	@Override
	public String toString() {
		return "base=" + this.parent + ", remove=" + this.remove + ", add=" + this.add;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.add == null) ? 0 : this.add.hashCode());
		result = (prime * result) + ((this.parent == null) ? 0 : this.parent.hashCode());
		result = (prime * result) + ((this.remove == null) ? 0 : this.remove.hashCode());
		return result;
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
		PrototypeDefinition other = (PrototypeDefinition) obj;
		if (this.add == null) {
			if (other.add != null) {
				return false;
			}
		} else if (!this.add.equals(other.add)) {
			return false;
		}
		if (this.parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!this.parent.equals(other.parent)) {
			return false;
		}
		if (this.remove == null) {
			if (other.remove != null) {
				return false;
			}
		} else if (!this.remove.equals(other.remove)) {
			return false;
		}
		return true;
	}

}
