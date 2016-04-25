package miselico.prototypes.joiners;

import miselico.prototypes.knowledgebase.AddChangeSet;

public interface AddStrategy {
	AddChangeSet join(AddChangeSet a, AddChangeSet b);

}
