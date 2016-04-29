package miselico.prototypes.joiners;

import miselico.prototypes.knowledgebase.AddChangeSet;

/**
 * A strategy for joining two {@link AddChangeSet}s together.
 * 
 * @author michael
 *
 */
public interface AddStrategy {
	/**
	 * Joins a and b together and produces a new {@link AddChangeSet}
	 * 
	 * @param a
	 * @param b
	 * @return The new changeset.
	 */
	AddChangeSet join(AddChangeSet a, AddChangeSet b);

}
