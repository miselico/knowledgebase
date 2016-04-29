package miselico.prototypes.knowledgebase;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;

import com.google.common.base.Preconditions;

/**
 * An ID, which can be any absolute IRI
 * 
 * An ID is immutable
 * 
 * @author michael
 *
 */
public class ID {
	private final String value;

	/**
	 * Creates an ID from the given value
	 * 
	 * @param value
	 *            the IRI
	 * @return an ID
	 * @throws IRISyntaxException
	 *             if the string is not a valid IRI
	 * @throws IllegalArgumentException
	 *             if the iri is not absolute
	 */
	public static ID of(String value) throws IRISyntaxException {
		return new ID(new IRI(value));
	}

	/**
	 * Constructs an ID from the IRI
	 * 
	 * @param iri
	 * @throws NullPointerException
	 *             if iri is null
	 * @throws IllegalArgumentException
	 *             if the iri is not absolute
	 */
	public ID(IRI iri) {
		Preconditions.checkNotNull(iri);
		Preconditions.checkArgument(iri.isAbsolute(), "Only absolute URIs are valid IDs");
		this.value = iri.toString();
	}

	/**
	 * get the IRI from the ID
	 * 
	 * @return
	 */
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
