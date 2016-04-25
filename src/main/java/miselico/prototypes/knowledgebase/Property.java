package miselico.prototypes.knowledgebase;

import org.apache.abdera.i18n.iri.IRI;

import com.google.common.base.Preconditions;

public class Property {
	// kept as a string, since this is way faster to compare as a URI object.
	private final String value;

	public static Property of(String value) {
		return new Property(new IRI(value));
	}

	public Property(IRI iri) {
		Preconditions.checkNotNull(iri);
		Preconditions.checkArgument(iri.isAbsolute(), "Only absolute URIs are valid properties");
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
		Property other = (Property) obj;
		return this.value.equals(other.value);
	}

	/**
	 * The string representation of the property i.e. the IRI
	 */
	@Override
	public String toString() {
		return this.value.toString();
	}

}
