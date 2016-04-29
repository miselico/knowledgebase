package miselico.prototypes.knowledgebase;

/**
 * A helper class for creating prototypes.
 * 
 * @author michael
 *
 */
public final class Prototypes {

	private Prototypes() {
		// utility class
	}

	/**
	 * A builder for prototypes.
	 * 
	 * note that the baseID if given when the builder is created. The ID of the
	 * new prototype is given in the end. The reason is that this way the
	 * builder can be reused to construct multiple new prototypes.
	 * 
	 * The builder supports fluent syntax.
	 * 
	 * @author michael
	 *
	 */
	public static class Builder {

		private final RemoveChangeSet.Builder remove = RemoveChangeSet.builder();
		private final AddChangeSet.Builder add = AddChangeSet.builder();
		private final ID basePTID;

		/**
		 * Create a builder for a prototype with the given base ID
		 * 
		 * @param basePTID
		 */
		public Builder(ID basePTID) {
			this.basePTID = basePTID;
		}

		/**
		 * create a builder for a prototype with the ID of the basePT as its
		 * base ID
		 * 
		 * @param basePT
		 */
		public Builder(Prototype basePT) {
			this(basePT.id);
		}

		/**
		 * Add the given {@link Property} and {@link ID} to the new prototype's
		 * add set.
		 * 
		 * @param p
		 * @param id
		 * @return
		 */
		public Builder add(Property p, ID id) {
			this.add.andAdd(p, id);
			return this;
		}

		/**
		 * Add the given {@link Property} and the {@link ID} of the
		 * {@link Prototype} prot to the new prototype's add set.
		 * 
		 * @param p
		 * @param id
		 * @return
		 */
		public Builder add(Property p, Prototype prot) {
			return this.add(p, prot.id);
		}

		/**
		 * Add the given {@link Property} and {@link ID} to the new prototype's
		 * remove set.
		 * 
		 * @param p
		 * @param id
		 * @return
		 */
		public Builder remove(Property p, ID id) {
			this.remove.andRemove(p, id);
			return this;
		}

		/**
		 * Add the given {@link Property} and the {@link ID} of the
		 * {@link Prototype} prot to the new prototype's remove set.
		 * 
		 * @param p
		 * @param id
		 * @return
		 */
		public Builder remove(Property p, Prototype prot) {
			return this.remove(p, prot.id);
		}

		/**
		 * Add the given {@link Property} to the new {@link Prototype}s
		 * removeAll set.
		 * 
		 * @param p
		 * @return
		 */
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

		/**
		 * Builds the prototype with ID as its own ID.
		 * 
		 * This builder can still be used after building the first time.
		 * 
		 * @param ownID
		 * @return
		 */
		public Prototype build(ID ownID) {
			PrototypeDefinition def = PrototypeDefinition.create(this.basePTID, this.remove.build(), this.add.build());

			return new Prototype(ownID, def);
		}

	}

	/**
	 * Create a new builder with basePT as a base for the newly created
	 * {@link Prototype}s
	 * 
	 * @param basePT
	 * @return
	 */
	public static Builder builder(ID basePT) {
		return new Builder(basePT);
	}

	/**
	 * Create a new builder with the ID of basePT as a base for the newly
	 * created {@link Prototype}s
	 * 
	 * @param basePT
	 * @return
	 */
	public static Builder builder(Prototype basePT) {
		return new Builder(basePT);
	}

	/**
	 * Create a new prototype for the given components. A
	 * {@link PrototypeDefinition} is created first.
	 * 
	 * @param id
	 * @param parent
	 * @param remove
	 * @param add
	 * @return
	 */
	public static Prototype create(ID id, Prototype parent, RemoveChangeSet remove, AddChangeSet add) {
		return new Prototype(id, PrototypeDefinition.create(parent, remove, add));
	}

}
