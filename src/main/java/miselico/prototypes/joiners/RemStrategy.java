package miselico.prototypes.joiners;

import miselico.prototypes.knowledgebase.RemoveChangeSet;

public interface RemStrategy {
	RemoveChangeSet join(RemoveChangeSet a, RemoveChangeSet b);
}
