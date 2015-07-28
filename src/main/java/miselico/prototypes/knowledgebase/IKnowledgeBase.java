package miselico.prototypes.knowledgebase;

import com.google.common.base.Optional;

public interface IKnowledgeBase {
	/**
	 * Is a prototype with the given ID defined?
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Prototype> isDefined(ID id);

	/**
	 * gets the Prototype with the given ID, will throw if not defined. Use
	 * isDefined if unsure.
	 * 
	 * @param id
	 * @return
	 */
	public Prototype get(ID id);
}
