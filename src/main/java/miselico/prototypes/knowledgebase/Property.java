package miselico.prototypes.knowledgebase;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;

import com.google.common.base.Preconditions;

/**
 * Representation of a property. Immutable.
 * 
 * @author michael
 *
 */
public class Property {
	// kept as a string, since this is way faster to compare as a URI object.
	private final String value;

	/**
	 * Create a property for the given string.
	 * 
	 * @param value
	 * @return
	 * @throws IRISyntaxException
	 *             in case the value is not a proper IRI
	 * @throws IllegalArgumentException
	 *             if the IRI is not absolute
	 */
	public static Property of(String value) {

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
		return new Property(uriString);
	}

	/**
	 * Create a property for the given IRI
	 * 
	 * @param iri
	 * @throws NullPointerException
	 *             if iri is null
	 * @throws IllegalArgumentException
	 *             if the IRI is not absolute
	 */
	public Property(IRI iri) {
		Preconditions.checkNotNull(iri);
		Preconditions.checkArgument(iri.isAbsolute(), "Only absolute URIs are valid properties");
		this.value = iri.toString();
	}

	/**
	 * Create a property for the given URI
	 * 
	 * @param uri
	 * @throws NullPointerException
	 *             if uri is null
	 * @throws IllegalArgumentException
	 *             if the IRI is not absolute
	 */
	public Property(URI uri) {
		Preconditions.checkNotNull(uri);
		Preconditions.checkArgument(uri.isAbsolute(), "Only absolute IRIs are valid properties");
		this.value = uri.toString();
	}

	/**
	 * Constructs an Property from a String. This is private because the String
	 * value is not checked.
	 * 
	 * @param uri
	 * @throws NullPointerException
	 *             if uri is null
	 * @throws IllegalArgumentException
	 *             if the uri is not absolute
	 */
	private Property(String uriString) {
		Preconditions.checkNotNull(uriString);
		this.value = uriString;
	}

	/**
	 * Get the value of this property as an IRI
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
