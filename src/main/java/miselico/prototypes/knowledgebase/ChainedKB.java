package miselico.prototypes.knowledgebase;

import java.util.Optional;

public class ChainedKB implements IKnowledgeBase {

	public static IKnowledgeBase chain(IKnowledgeBase... bases) {
		return new ChainedKB(bases);
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
