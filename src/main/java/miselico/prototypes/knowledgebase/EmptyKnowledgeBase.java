package miselico.prototypes.knowledgebase;

import java.util.Optional;

/**
 * This is a knowledge base without any content. However, as per the definition,
 * it still contains {@link Prototype#P_0} .
 * 
 * @author michael
 *
 */
public enum EmptyKnowledgeBase implements IKnowledgeBase {
	/**
	 * The only instance
	 */
	instance;

	@Override
	public Optional<Prototype> isDefined(ID id) {
		if (id.equals(Prototype.P_0.id)) {
			return Prototype.OptP_0;
		}
		return Optional.empty();
	}

}
