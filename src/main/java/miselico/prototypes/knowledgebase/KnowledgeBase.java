package miselico.prototypes.knowledgebase;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

/**
 * A KB is a set of prototypes which (when combined with the Predefined KB) is
 * closed. ie. All IDs are reachable.
 * 
 * A {@link KnowledgeBase} is immutable. If you want to make changes to it, you
 * have to create a new one using the {@link Builder}
 * 
 * @author michael
 *
 */
public class KnowledgeBase implements IFPKnowledgeBase {

	public final ImmutableMap<ID, PrototypeDefinition> KB;
	private final IKnowledgeBase external;

	/**
	 * 
	 * @param kB
	 *            the data in this knowledge base
	 * @param external
	 *            data defined externally
	 */
	public KnowledgeBase(ImmutableMap<ID, PrototypeDefinition> kB, IKnowledgeBase external) {
		this(kB, external, true);
	}

	/**
	 * 
	 * @param kB
	 *            the data in this knowledge base
	 * @param external
	 *            data defined externally
	 * @throws Error
	 *             in case the constructed knowledge base is not consistent.
	 */
	private KnowledgeBase(ImmutableMap<ID, PrototypeDefinition> kB, IKnowledgeBase external, boolean checkConsistency) {
		this.KB = kB;
		this.external = external;
		if (checkConsistency) {
			this.checkConsistency();
		}
	}

	// Checks whether ALL IDs mentioned are reachable within the KB or in the
	// Predefined KB AND wheter all prototypes are recursively deriving from P_0
	private void checkConsistency() {
		if (this.KB.containsKey(Prototype.P_0.id)) {
			throw new Error("A KB definition cannot contain P_0");
		}
		for (PrototypeDefinition proto : this.KB.values()) {
			// Check the parent and added things. A recursive check is not
			// necessary: They will be checked themselves in later/before in the
			// same loop
			ID parent = proto.parent;
			if (!this.isDefined(parent).isPresent()) {
				throw new Error("Parent of " + proto + " is not defined in the knowledge base.");
			}
			for (Entry<Property, ID> addition : proto.add.entries()) {
				ID v = addition.getValue();
				if (v.equals(Prototype.P_0.id)) {
					throw new Error("Value for added property " + addition.getKey() + " refers to P0");
				}

				if (!this.isDefined(v).isPresent()) {
					throw new Error("Value for added property " + addition.getKey() + " of prototype " + proto + " refers to undefined prototype " + addition.getValue());
				}
			}
		}
		// IDs should not be defined twice, check in external KB
		for (ID id : this.KB.keySet()) {
			if (this.external.isDefined(id).isPresent()) {
				throw new Error("Prototype with ID " + id + " defined twice.");
			}
		}

		// Check derivation is DAG

		// It could be tried to reduce the size of 'grounded' by only including
		// IDs with a given probability. This might speed up things, but should
		// be benchmarked.

		HashSet<ID> grounded = new HashSet<>();
		grounded.add(Prototype.P_0.id);
		for (Entry<ID, PrototypeDefinition> proto : this.KB.entrySet()) {
			ID currentID = proto.getKey();
			Set<ID> currentBranch = new HashSet<ID>();
			PrototypeDefinition currentDef = proto.getValue();
			while (!grounded.contains(currentID)) {
				if (!currentBranch.add(currentID)) {
					throw new Error("Cycle detected in inheritance tree for " + currentDef);
				}
				currentID = currentDef.parent;
				currentDef = this.KB.get(currentID);
			}
			grounded.addAll(currentBranch);
		}
	}

	private static final Optional<Prototype> OptP_0 = Optional.of(Prototype.P_0);

	@Override
	public Optional<? extends Prototype> isDefined(ID id) {
		PrototypeDefinition prot = this.KB.get(id);
		if (prot != null) {
			return Optional.of(new Prototype(id, prot));
		}
		if (id.equals(Prototype.P_0.id)) {
			return KnowledgeBase.OptP_0;
		}
		return this.external.isDefined(id);
	}

	/**
	 * get the map containing the actual prototype IDsand their definitions
	 * 
	 * @return
	 */
	public ImmutableMap<ID, PrototypeDefinition> prototypes() {
		return this.KB;
	}

	/**
	 * A collection contianing all prototype definitions
	 * 
	 * @return
	 */
	public ImmutableCollection<PrototypeDefinition> prototypeDefs() {
		return this.KB.values();
	}

	@Override
	public String toString() {
		return "KnowledgeBase [KB=" + this.KB + "]";
	}

	@Override
	public Prototype computeFixPoint(ID id) {
		Optional<? extends Prototype> ooriginal = this.isDefined(id);
		Preconditions.checkArgument(ooriginal.isPresent());
		Prototype original = ooriginal.get();
		Deque<Prototype> branch = new LinkedList<Prototype>();
		Prototype current = original;
		while (!(Prototype.P_0.id.equals(current.id))) {
			branch.addFirst(current);
			current = this.isDefined(current.def.parent).get();
		}
		MutableChangeSet fixpointAdd = new MutableChangeSet();
		for (Prototype prototype : branch) {
			prototype.def.remove.removeFrom(fixpointAdd);
			prototype.def.add.addTo(fixpointAdd);
		}
		AddChangeSet addCS = AddChangeSet.fromMutable(fixpointAdd);
		PrototypeDefinition def = PrototypeDefinition.create(Prototype.P_0, RemoveChangeSet.empty(), addCS);
		return new Prototype(original.id, def);
	}

	/**
	 * Compute the fixpoint of every prototype in this {@link KnowledgeBase} and
	 * returns the result as a new (independent) {@link KnowledgeBase}
	 * 
	 * @return A new knowledge base containing all prototypes of this knowledge
	 *         base in fixpoint form.
	 */
	public KnowledgeBase computeFixPoint() {
		Builder b = new Builder(this.external);
		// this is optimized by re-using paths in the derivation.
		HashMap<ID, AddChangeSet> done = new HashMap<ID, AddChangeSet>();
		// ground with P_0
		done.put(Prototype.P_0.id, AddChangeSet.empty());
		for (Entry<ID, PrototypeDefinition> proto : this.KB.entrySet()) {
			if (!done.containsKey(proto.getKey())) {
				// find the fixpoint for the add set
				Deque<Prototype> branch = new LinkedList<Prototype>();
				Prototype current = new Prototype(proto.getKey(), proto.getValue());
				// TODO we might want to stop at the 'boundaries' of this
				// knowledge
				// base. ie. externally defined things.
				while (!(done.containsKey(current.id))) {
					branch.addFirst(current);
					current = this.isDefined(current.def.parent).get();
				}
				// invariant: we now pop the things form the stack.
				// We know that at each stage the fixpoint of the parent has
				// been
				// calculated already.
				while (!branch.isEmpty()) {
					current = branch.removeFirst();
					// the fix point is whatever the parent fix point has, minus
					// what gets removed, plus what gets added
					// take what the parent had and copy.
					MutableChangeSet fpParent = done.get(current.def.parent).mutableCopy();
					current.def.remove.removeFrom(fpParent);
					current.def.add.addTo(fpParent);
					AddChangeSet addCS = AddChangeSet.fromMutable(fpParent);
					done.put(current.id, addCS);
					PrototypeDefinition def = PrototypeDefinition.create(Prototype.P_0, RemoveChangeSet.empty(), addCS);
					// FIXME we just take the same ID. This seems fine, but is
					// it?
					b.add(new Prototype(current.id, def));
				}
			}
		}
		return b.build(false);
	}

	/**
	 * Create a new empty knowledge base with the given {@link IKnowledgeBase}.
	 * This is, for instance, useful for if you want to compute fixpoints in the
	 * underlying {@link IKnowledgeBase}
	 * 
	 * @param external
	 * @return
	 */
	public static KnowledgeBase empty(IKnowledgeBase external) {
		return new KnowledgeBase(ImmutableMap.<ID, PrototypeDefinition> of(), external);
	}

	// private static final KnowledgeBase EMPTY = new
	// KnowledgeBase(ImmutableMap.<ID, Prototype> of());

	/**
	 * A builder to create a knowledge base. This is needed because during
	 * construction things might be momentarily inconsistent.
	 * 
	 * The builder supports fluent syntax.
	 * 
	 * @author michael
	 *
	 */
	public static class Builder {

		// private KnowledgeBase base;
		// private List<Prototype> adds = new ArrayList<Prototype>();
		// private List<ID> removals = new ArrayList<ID>();

		private final Map<ID, PrototypeDefinition> prototypez;
		private final IKnowledgeBase external;

		/**
		 * Create a builder for a {@link KnowledgeBase} based on the given
		 * external {@link IKnowledgeBase}. If you have a {@link KnowledgeBase},
		 * use the {@link Builder#Builder(KnowledgeBase)} constructor.
		 * 
		 * @param external
		 */
		public Builder(IKnowledgeBase external) {
			this(KnowledgeBase.empty(external));
		}

		/**
		 * Create a builder based on the given {@link KnowledgeBase}. This
		 * specialization results in a more efficient {@link KnowledgeBase} as
		 * the generic {@link Builder#Builder(IKnowledgeBase)} constructor.
		 * 
		 * @param base
		 */
		public Builder(KnowledgeBase base) {
			this.prototypez = new HashMap<>(base.KB);
			this.external = base.external;
		}

		/**
		 * Add the given prototype to the {@link KnowledgeBase}
		 * 
		 * @param p
		 * @return the builder.
		 * @throws Error
		 *             in case the given prototype is already defined.
		 */
		public Builder add(Prototype p) {
			if (this.prototypez.containsKey(p.id) || this.external.isDefined(p.id).isPresent()) {
				throw new Error("A prototype with ID " + p.id + " already exists.");
			}
			this.prototypez.put(p.id, p.def);
			return this;
		}

		/**
		 * Add the given prototypes to the {@link KnowledgeBase}
		 * 
		 * @param p
		 * @return the builder.
		 * @throws Error
		 *             in case the given prototype is already defined.
		 */
		public Builder addAll(List<Prototype> protos) {
			for (Prototype prototype : protos) {
				this.add(prototype);
			}
			return this;
		}

		/**
		 * Remove the prototype with the given ID from this builder.
		 * 
		 * @param p
		 * @return the builder itself
		 * @throws Error
		 *             in case the prototype with that ID is defined in the
		 *             external {@link IKnowledgeBase}
		 */
		public Builder remove(ID p) {
			if (this.external.isDefined(p).isPresent()) {
				throw new Error("Cannot remove prototype with ID " + p + " because it is defined externally.");
			}
			this.prototypez.remove(p);
			return this;
		}

		/**
		 * Does the KB which would be built contain a prototype with the given
		 * ID?
		 * 
		 * @param p
		 * @return
		 */
		public boolean buildKnowledgeBaseContains(ID p) {
			return this.prototypez.containsKey(p);
		}

		/**
		 * Construct the {@link KnowledgeBase}, this also checks the
		 * consistency. After a call to this constructor the builder can still
		 * be used. The constructed {@link KnowledgeBase} is independent of the
		 * builder.
		 * 
		 * @return The built {@link KnowledgeBase}
		 * @throws Error
		 *             in case the constructed knowledge base is not consistent.
		 */
		public KnowledgeBase build() {
			return this.build(true);
		}

		/**
		 * Construct the {@link KnowledgeBase}, this and checks the consistency
		 * if checkConsistency is true. After a call to this constructor the
		 * builder can still be used. The constructed {@link KnowledgeBase} is
		 * independent of the builder.
		 * 
		 * This method is only used form within {@link KnowledgeBase} itself in
		 * cases were is can be guaranteed that the {@link KnowledgeBase}
		 * constructed is consistent.
		 * 
		 * @return The constructed {@link KnowledgeBase}
		 * @throws Error
		 *             in case checkConsistency was true and the constructed
		 *             knowledge base is not consistent.
		 * @param checkConsistency
		 *            should consistency be checked
		 */
		private KnowledgeBase build(boolean checkConsistency) {
			return new KnowledgeBase(ImmutableMap.copyOf(this.prototypez), this.external, checkConsistency);
		}
	}

	/**
	 * Amount of prototypes internal to this KB
	 * 
	 * @return
	 */
	public int size() {
		return this.KB.size();
	}

}
