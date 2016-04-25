package miselico.prototypes.joiners;

import com.google.common.base.Preconditions;

import miselico.prototypes.knowledgebase.AddChangeSet;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.PrototypeDefinition;
import miselico.prototypes.knowledgebase.RemoveChangeSet;

public class JoinStrategy {
	private final AddStrategy add;
	private final RemStrategy rem;

	public JoinStrategy(AddStrategy add, RemStrategy rem) {
		this.add = add;
		this.rem = rem;
	}

	public Prototype join(Prototype a, Prototype b) {
		Preconditions.checkArgument(a.id.equals(b.id));
		Preconditions.checkArgument(a.def.parent.equals(b.def.parent));
		AddChangeSet acs = this.add.join(a.def.add, b.def.add);
		RemoveChangeSet rcs = this.rem.join(a.def.remove, b.def.remove);
		PrototypeDefinition pd = PrototypeDefinition.create(a.def.parent, rcs, acs);
		return new Prototype(a.id, pd);
	}
}
