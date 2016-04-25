package miselico.prototypes.knowledgebase;

import java.net.URI;

import com.google.common.base.Preconditions;

public class Property {
	// kept as a string, since this is way faster to compare as a URI object.
	private final String value;

	public static Property of(String value) {
		return new Property(URI.create(value));
	}

	public Property(URI value) {
		Preconditions.checkNotNull(value);
		Preconditions.checkArgument(value.isAbsolute(), "Only absolute URIs are valid properties");
		this.value = value.toString();
	}

	public URI getValue() {
		return URI.create(this.value);
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
	 * The textual representation of the property
	 */
	@Override
	public String toString() {
		return this.value.toString();
	}

}
