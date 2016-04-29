package miselico.prototypes.knowledgebase;

import java.util.List;
import java.util.Optional;

/**
 * An {@link IKnowledgeBase} which connects multiple {@link IKnowledgeBase}s
 * together. If a prototype is not available in the first KB, then it searches
 * the next one until it find one where the Prototype is defined. If none is
 * found, an empty {@link Optional} is returned.
 * 
 * @author michael
 *
 */
public final class ChainedKB implements IKnowledgeBase {

	/**
	 * Constructs a {@link ChainedKB} from the given bases.
	 * 
	 * @param bases
	 * @return
	 */
	public static IKnowledgeBase chain(IKnowledgeBase... bases) {
		return new ChainedKB(bases);
	}

	/**
	 * Constructs a {@link ChainedKB} from the given bases.
	 * 
	 * @param bases
	 * @return
	 */
	public static IKnowledgeBase chain(List<IKnowledgeBase> bases) {
		return ChainedKB.chain(bases.toArray(new KnowledgeBase[bases.size()]));
	}

	private final IKnowledgeBase[] bases;

	private ChainedKB(IKnowledgeBase[] bases) {
		this.bases = bases;
	}

	@Override
	public Optional<? extends Prototype> isDefined(ID id) {
		for (IKnowledgeBase base : this.bases) {
			Optional<? extends Prototype> proto = base.isDefined(id);
			if (proto.isPresent()) {
				return proto;
			}
		}
		return Optional.empty();
	}

}
