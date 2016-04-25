package miselico.prototypes.knowledgebase;

import java.util.Optional;

public class EmptyKnowledgeBase implements IKnowledgeBase {

	private EmptyKnowledgeBase() {
	}

	public static final EmptyKnowledgeBase instance = new EmptyKnowledgeBase();
	private static final Optional<Prototype> OptP_0 = Optional.of(Prototype.P_0);

	@Override
	public Optional<Prototype> isDefined(ID id) {
		if (id.equals(Prototype.P_0.id)) {
			return EmptyKnowledgeBase.OptP_0;
		}
		return Optional.empty();
	}

}
