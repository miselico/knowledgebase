package miselico.prototypes.knowledgebase;

import java.util.Optional;

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
	 * @return
	 */
	public Optional<Prototype> isDefined(ID id);
}
