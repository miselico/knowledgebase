package miselico.prototypes.knowledgebase;

import com.google.common.base.Preconditions;

public class PrototypeDefinition {

	// If this gets ever changed to Prototype, there should be a thorough check
	// for equals wherever parent is used.
	public final ID parent;
	public final RemoveChangeSet remove;
	public final AddChangeSet add;

	private PrototypeDefinition(ID parent, RemoveChangeSet remove, AddChangeSet add) {
		this.parent = parent;
		this.remove = remove;
		this.add = add;
	}

	public static PrototypeDefinition create(Prototype parent, RemoveChangeSet remove, AddChangeSet add) {
		return PrototypeDefinition.create(parent.id, remove, add);
	}

	public static PrototypeDefinition create(ID parent, RemoveChangeSet remove, AddChangeSet add) {
		Preconditions.checkNotNull(parent);
		Preconditions.checkNotNull(remove);
		Preconditions.checkNotNull(add);
		PrototypeDefinition p = new PrototypeDefinition(parent, remove, add);
		return p;
	}

	public static final PrototypeDefinition P_0;

	static {
		ID parent = null;
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

}
