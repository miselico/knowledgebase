package miselico.prototypes.knowledgebase;

import java.util.Optional;

import com.google.common.base.Preconditions;

/**
 * Representation of a Prototype. A prototype consists of a
 * {@link PrototypeDefinition} and an {@link ID}. The prototype is immutable and
 * all subclasses must follow that.
 * 
 * @author michael
 *
 */
public class Prototype {

	/**
	 * The ID of this prototype.
	 */
	public final ID id;

	/**
	 * The definition of this prototype.
	 */
	public final PrototypeDefinition def;

	/**
	 * Create a prototype for the given id and definition
	 * 
	 * @param id
	 * @param def
	 * @throws NullPointerException
	 *             if id is null or def is null.
	 */
	public Prototype(ID id, PrototypeDefinition def) {
		Preconditions.checkNotNull(id);
		Preconditions.checkNotNull(def);
		this.id = id;
		this.def = def;
	}

	/**
	 * A constant for the empty prototype (the prototype which grounds the
	 * prototype system.)
	 */
	public static final Prototype P_0;

	static {
		ID id = ID.of("proto:P_0");
		P_0 = new Prototype(id, PrototypeDefinition.P_0);
	}

	/**
	 * Package private optional containing the empty prototype
	 */
	static final Optional<Prototype> OptP_0 = Optional.of(Prototype.P_0);

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.def == null) ? 0 : this.def.hashCode());
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Prototype other = (Prototype) obj;
		if (this.def == null) {
			if (other.def != null) {
				return false;
			}
		} else if (!this.def.equals(other.def)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
