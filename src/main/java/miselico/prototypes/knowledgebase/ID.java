package miselico.prototypes.knowledgebase;

import java.net.URI;
import java.net.URISyntaxException;

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
		// creating a URI type is about three times faster as a IRI type, so we
		// try that first
		// This might be implementation dependent.

		String uriString;
		try {
			URI uri = new URI(value);
			uriString = uri.toString();
		} catch (URISyntaxException e) {
			uriString = new IRI(value).toString();
		}
		return new ID(uriString);
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
		Preconditions.checkArgument(iri.isAbsolute(), "Only absolute IRIs are valid IDs");
		this.value = iri.toString();
	}

	/**
	 * Constructs an ID from the URI
	 * 
	 * @param uri
	 * @throws NullPointerException
	 *             if uri is null
	 * @throws IllegalArgumentException
	 *             if the uri is not absolute
	 */
	public ID(URI uri) {
		Preconditions.checkNotNull(uri);
		Preconditions.checkArgument(uri.isAbsolute(), "Only absolute IRIs are valid IDs");
		this.value = uri.toString();
	}

	/**
	 * Constructs an ID from a String. This is private because the String value
	 * is not checked.
	 * 
	 * @param uri
	 * @throws NullPointerException
	 *             if uri is null
	 * @throws IllegalArgumentException
	 *             if the uri is not absolute
	 */
	private ID(String uriString) {
		Preconditions.checkNotNull(uriString);
		this.value = uriString;
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
