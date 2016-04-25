package miselico.prototypes.knowledgebase;

import org.apache.abdera.i18n.iri.IRI;

import com.google.common.base.Preconditions;

public class ID {
	private final String value;

	public static ID of(String value) {
		return new ID(new IRI(value));
	}

	public ID(IRI iri) {
		Preconditions.checkNotNull(iri);
		Preconditions.checkArgument(iri.isAbsolute(), "Only absolute URIs are valid IDs");
		this.value = iri.toString();
	}

	public IRI getValue() {
		return new IRI(this.value);
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
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
		ID other = (ID) obj;
		return this.value.equals(other.value);
	}

	/**
	 * The string representation of the ID, this will be the string
	 * representation of the IRI.
	 */
	@Override
	public String toString() {
		return this.value;
	}

}
