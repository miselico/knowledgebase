package miselico.prototypes.knowledgebase;

public class Prototypes {

	// public static Prototype create(ID id, Prototype parent, RemoveChangeSet
	// remove, AddChangeSet add) {
	// return Prototype.create(id, parent.id, remove, add);
	// }
	//
	// private static Prototype create(ID id, ID parent, RemoveChangeSet remove,
	// AddChangeSet add) {
	// Preconditions.checkNotNull(id);
	// Preconditions.checkNotNull(parent);
	// Preconditions.checkNotNull(remove);
	// Preconditions.checkNotNull(add);
	// Prototype p = new Prototype(id, parent, remove, add);
	// Prototype previous = Prototype.allProtoTypes.put(id, p);
	// if (previous != null) {
	// System.err.println("A prototype was redefined. Was :" + previous + " now
	// :" + p);
	// }
	// return p;
	// }
	//
	// private static final Map<ID, Prototype> allProtoTypes;
	//
	// static {
	// System.err.println("Currently all protoypes created are checked for
	// uniqueness, this is a performance burden but helps ensure correctness.
	// PredefinedKB Prototypes are cached, so could show up unregularly.");
	// allProtoTypes = new HashMap<ID, Prototype>();
	// }

	public static class Builder {

		private final RemoveChangeSet.Builder remove = RemoveChangeSet.builder();
		private final AddChangeSet.Builder add = AddChangeSet.builder();
		private final ID basePTID;

		public Builder(ID basePTID) {
			this.basePTID = basePTID;
		}

		public Builder(Prototype basePT) {
			this(basePT.id);
		}

		public Builder add(Property p, ID id) {
			this.add.andAdd(p, id);
			return this;
		}

		public Builder add(Property p, Prototype prot) {
			return this.add(p, prot.id);
		}

		public Builder remove(Property p, ID id) {
			this.remove.andRemove(p, id);
			return this;
		}

		public Builder remove(Property p, Prototype prot) {
			return this.remove(p, prot.id);
		}

		public Builder removeAll(Property p) {
			this.remove.andRemoveAll(p);
			return this;
		}

		/**
		 * Removes any value for property and adds the new one
		 * 
		 * @param p
		 * @param id
		 * @return
		 */
		public Builder replace(Property p, ID id) {
			return this.removeAll(p).add(p, id);
		}

		/**
		 * Removes any value for property and adds the new one
		 * 
		 * @param p
		 * @param id
		 * @return
		 */
		public Builder replace(Property p, Prototype prot) {
			return this.replace(p, prot.id);
		}

		public Prototype build(ID ownID) {
			PrototypeDefinition def = PrototypeDefinition.create(this.basePTID, this.remove.build(), this.add.build());

			return new Prototype(ownID, def);
		}

	}

	public static Builder builder(ID basePT) {
		return new Builder(basePT);
	}

	public static Builder builder(Prototype basePT) {
		return new Builder(basePT);
	}

	public static Prototype create(ID id, Prototype parent, RemoveChangeSet remove, AddChangeSet add) {
		return new Prototype(id, PrototypeDefinition.create(parent, remove, add));
	}

}
