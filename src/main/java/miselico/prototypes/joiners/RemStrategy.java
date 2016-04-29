package miselico.prototypes.joiners;

import miselico.prototypes.knowledgebase.RemoveChangeSet;

/**
 * A strategy for joining two {@link RemoveChangeSet}s together.
 * 
 * @author michael
 *
 */
public interface RemStrategy {
	/**
	 * Joins a and b together and produces a new {@link RemoveChangeSet}
	 * 
	 * @param a
	 * @param b
	 * @return The new changeset.
	 */
	RemoveChangeSet join(RemoveChangeSet a, RemoveChangeSet b);
}
