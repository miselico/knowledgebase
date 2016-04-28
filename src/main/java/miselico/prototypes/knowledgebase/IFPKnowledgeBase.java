package miselico.prototypes.knowledgebase;

/**
 * The interface which every knowledge base implementation must implement.
 * 
 * @author michael
 *
 */
public interface IFPKnowledgeBase extends IKnowledgeBase {

	/**
	 * Compute the fixpoint of the given prototype.
	 * 
	 * @param id
	 *            The ID of the prototype.
	 * @return The Prototype in fixpoint form.
	 * @throws IllegalArgumentException
	 *             If the id is not in the {@link IKnowledgeBase}
	 */
	public Prototype computeFixPoint(ID id);
}
