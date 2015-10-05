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

	public final ImmutableMap<ID, Prototype> KB;
	private IKnowledgeBase external;

	/**
	 * 
	 * @param kB
	 *            the data in this knowledge base
	 * @param external
	 *            data defined externally
	 */
	public KnowledgeBase(ImmutableMap<ID, Prototype> kB, IKnowledgeBase external) {
		this.KB = kB;
		this.external = external;
		this.checkConsistency();
	}

	// Checks whether ALL IDs mentioned are reachable within the KB or in the
	// Predefined KB AND wheter all prototypes are recursively deriving from P_0
	private void checkConsistency() {
		for (Prototype proto : this.KB.values()) {
			// Check the parent and added things. A recursive check is not
			// necessary: They will be checked themselves in later/before in the
			// same loop
			ID parent = proto.parent;
			if (!this.isDefined(parent).isPresent()) {
				throw new Error("Parent of " + proto + " is not defined in the knowledge base.");
			}
			for (Entry<Property, ID> addition : proto.add.entries()) {
				if (!this.isDefined(addition.getValue()).isPresent()) {
					throw new Error("Value for added property " + addition.getKey() + " of prototype " + proto + " refers to undefined prototype " + addition.getValue());
				}
			}
		}
		// Check derivation is DAG

		// This implementation could possibly be improved by not re-traversing
		// paths.
		// This might also be slower due to look-up times, mainly for short
		// chains.
		for (Prototype proto : this.KB.values()) {
			Set<ID> currentBranch = new HashSet<ID>();
			Prototype current = proto;
			while (!current.parent.equals(Prototype.P_0.id)) {
				if (!currentBranch.add(current.id)) {
					throw new Error("Cycle detected in inheritance tree for " + proto);
				}
				ID parentID = current.parent;
				current = this.KB.get(parentID);
			}
		}
	}

	public Optional<Prototype> isDefined(ID id) {
		Prototype prot = this.KB.get(id);
		if (prot != null) {
			return Optional.of(prot);
		}
		return this.external.isDefined(id);
	}

	public Prototype get(ID id) {
		return this.isDefined(id).get();
	}

	public ImmutableCollection<Prototype> prototypes() {
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
		for (Prototype proto : this.KB.values()) {
			// find the fixpoint for the add set
			Deque<Prototype> branch = new LinkedList<Prototype>();
			Prototype current = proto;
			// TODO we might want to stop at the 'boundaries' of this knowledge
			// base. ie. externally defined things.
			while (!(done.containsKey(current.id))) {
				branch.addFirst(current);
				current = this.get(current.parent);
			}
			// invariant: we now pop the things form the stack.
			// We know that at each stage the fixpoint of the parent has been
			// calculated already.
			while (!branch.isEmpty()) {
				current = branch.removeFirst();
				// the fix point is whatever the parent fix point has, minus
				// what gets removed, plus what gets added
				// take what the parent had and copy.
				MutableChangeSet fpParent = done.get(current.parent).mutableCopy();
				current.remove.removeFrom(fpParent);
				current.add.addTo(fpParent);
				AddChangeSet addCS = AddChangeSet.fromMutable(fpParent);
				done.put(current.id, addCS);
				// FIXME we just take the same ID. This seems fine, but is it?
				b.add(Prototype.create(current.id, Prototype.P_0, RemoveChangeSet.empty(), addCS));
			}
		}
		return b.build();
	}

	public static KnowledgeBase empty(IKnowledgeBase external) {
		return new KnowledgeBase(ImmutableMap.<ID, Prototype> of(), external);
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
		private List<Prototype> removals = new ArrayList<Prototype>();

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

		public Builder remove(Prototype p) {
			this.removals.add(p);
			return this;
		}

		public KnowledgeBase build() {

			Map<ID, Prototype> prototypes = new HashMap<ID, Prototype>();
			prototypes.putAll(this.base.KB);
			for (Prototype prototype : this.adds) {
				prototypes.put(prototype.id, prototype);
			}
			for (Prototype prototype : this.removals) {
				prototypes.remove(prototype.id);
			}
			return new KnowledgeBase(ImmutableMap.copyOf(prototypes), this.base.external);
		}
	}

}
