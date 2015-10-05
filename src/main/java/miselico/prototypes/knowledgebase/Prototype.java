package miselico.prototypes.knowledgebase;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class Prototype {

	public final ID id;
	// If this gets ever changed to Prototype, there should be a thorough check
	// for equals wherever parent is used.
	public final ID parent;
	public final RemoveChangeSet remove;
	public final AddChangeSet add;

	public static Prototype create(ID id, Prototype parent, RemoveChangeSet remove, AddChangeSet add) {
		return Prototype.create(id, parent.id, remove, add);
	}

	private static Prototype create(ID id, ID parent, RemoveChangeSet remove, AddChangeSet add) {
		Preconditions.checkNotNull(id);
		Preconditions.checkNotNull(parent);
		Preconditions.checkNotNull(remove);
		Preconditions.checkNotNull(add);
		Prototype p = new Prototype(id, parent, remove, add);
		Prototype previous = Prototype.allProtoTypes.put(id, p);
		if (previous != null) {
			System.err.println("A prototype was redefined. Was :" + previous + " now :" + p);
		}
		return p;
	}

	private static final Map<ID, Prototype> allProtoTypes;

	static {
		System.err.println("Currently all protoypes created are checked for uniqueness, this is a performance burden but helps ensure correctness. PredefinedKB Prototypes are cached, so could show up unregularly.");
		allProtoTypes = new HashMap<ID, Prototype>();
	}

	private Prototype(ID id, ID parent, RemoveChangeSet remove, AddChangeSet add) {
		this.id = id;
		this.parent = parent;
		this.remove = remove;
		this.add = add;
	}

	public static final Prototype P_0;

	static {
		ID id = new ID(URI.create("proto:P_0"));
		ID parent = null;
		RemoveChangeSet remove = RemoveChangeSet.empty();
		AddChangeSet add = AddChangeSet.empty();
		P_0 = new Prototype(id, parent, remove, add);
	}

	@Override
	public String toString() {
		return "Prototype [id=" + this.id + ", parent=" + this.parent + ", remove=" + this.remove + ", add=" + this.add + "]";
	}

	/**
	 * A prototype is fixpoint if it derives from P_O and has an empty remove
	 * set
	 * 
	 * @return
	 */
	public boolean isFixPoint() {
		return (this.parent.equals(Prototype.P_0.id) && this.remove.isEmpty());
	}

}
