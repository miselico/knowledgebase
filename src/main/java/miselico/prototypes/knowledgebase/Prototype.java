package miselico.prototypes.knowledgebase;

import java.net.URI;

public class Prototype {

	public final ID id;

	public final PrototypeDefinition def;

	public Prototype(ID id, PrototypeDefinition def) {
		this.id = id;
		this.def = def;
	}

	public static final Prototype P_0;

	static {
		ID id = new ID(URI.create("proto:P_0"));
		P_0 = new Prototype(id, PrototypeDefinition.P_0);
	}

	@Override
	public String toString() {
		return this.id + "(" + this.def + ")";
	}

	/**
	 * A prototype is fixpoint if it derives from P_O and has an empty remove
	 * set
	 * 
	 * @return
	 */
	public boolean isFixPoint() {
		return this.def.isFixPoint();
	}

}
