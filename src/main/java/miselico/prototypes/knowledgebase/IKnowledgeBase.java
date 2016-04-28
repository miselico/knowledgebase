package miselico.prototypes.knowledgebase;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The interface which every knowledge base implementation must implement.
 * 
 * @author michael
 *
 */
public interface IKnowledgeBase {
	/**
	 * Is a prototype with the given ID defined?
	 * 
	 * Every KB must define the empty prototype {@code P_O }.
	 * 
	 * Every knowledge base must support concurrent reads.
	 * 
	 * @param id
	 *            The ID of the prototype
	 * @return An Optional containing the prototype if it was defined in this
	 *         knowledge base.
	 */
	public Optional<? extends Prototype> isDefined(ID id);
}
