package miselico.prototypes.knowledgebase;

import com.google.common.base.Optional;

public class EmptyKnowledgeBase implements IKnowledgeBase {

	private EmptyKnowledgeBase() {
	}

	public static final EmptyKnowledgeBase instance = new EmptyKnowledgeBase();

	@Override
	public Optional<Prototype> isDefined(ID id) {
		return Optional.absent();
	}

	@Override
	public Prototype get(ID id) {
		throw new Error();
	}

}
