package miselico.prototypes.knowledgebase;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

/**
 * A KB is a set of prototypes which (when combined with the Predefined KB) is
 * closed. ie. All IDs are reachable.
 * 
 * @author michael
 *
 */
public class KnowledgeBase implements IKnowledgeBase {

	public final ImmutableMap<ID, PrototypeDefinition> KB;
	private IKnowledgeBase external;

	/**
	 * 
	 * @param kB
	 *            the data in this knowledge base
	 * @param external
	 *            data defined externally
	 */
	public KnowledgeBase(ImmutableMap<ID, PrototypeDefinition> kB, IKnowledgeBase external) {
		this.KB = kB;
		this.external = external;
		this.checkConsistency();
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

		// It could be tried to reduce the size of 'grounded' by only including IDs with a given probability. This might speed up things, but should be benchmarked.

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
	public Optional<Prototype> isDefined(ID id) {
		PrototypeDefinition prot = this.KB.get(id);
		if (prot != null) {
			return Optional.of(new Prototype(id, prot));
		}
		if (id.equals(Prototype.P_0.id)) {
			return KnowledgeBase.OptP_0;
		}
		return this.external.isDefined(id);
	}

	@Override
	public Prototype get(ID id) {
		return this.isDefined(id).get();
	}

	public ImmutableMap<ID, PrototypeDefinition> prototypes() {
		return this.KB;
	}

	public ImmutableCollection<PrototypeDefinition> prototypeDefs() {
		return this.KB.values();
	}

	@Override
	public String toString() {
		return "KnowledgeBase [KB=" + this.KB + "]";
	}

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
					current = this.get(current.def.parent);
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
		return b.build();
	}

	public static KnowledgeBase empty(IKnowledgeBase external) {
		return new KnowledgeBase(ImmutableMap.<ID, PrototypeDefinition> of(), external);
	}

	// private static final KnowledgeBase EMPTY = new
	// KnowledgeBase(ImmutableMap.<ID, Prototype> of());

	/**
	 * A builder to create a knowledge base. This is needed because during
	 * construction things might be momentarily inconsistent.
	 * 
	 * @author michael
	 *
	 */
	public static class Builder {

		private KnowledgeBase base;
		private List<Prototype> adds = new ArrayList<Prototype>();
		private List<ID> removals = new ArrayList<ID>();

		public Builder(IKnowledgeBase external) {
			this(KnowledgeBase.empty(external));
		}

		public Builder(KnowledgeBase base) {
			this.base = base;
		}

		public Builder add(Prototype p) {
			this.adds.add(p);
			return this;
		}

		public Builder remove(ID p) {
			this.removals.add(p);
			return this;
		}

		public KnowledgeBase build() {

			Map<ID, PrototypeDefinition> prototypes = new HashMap<>();
			prototypes.putAll(this.base.KB);
			for (Prototype prototype : this.adds) {
				prototypes.put(prototype.id, prototype.def);
			}
			for (ID id : this.removals) {
				prototypes.remove(id);
			}
			return new KnowledgeBase(ImmutableMap.copyOf(prototypes), this.base.external);
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
