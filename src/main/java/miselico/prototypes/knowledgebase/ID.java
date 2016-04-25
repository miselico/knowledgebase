package miselico.prototypes.knowledgebase;

import java.net.URI;

import com.google.common.base.Preconditions;

public class ID {
	private final String value;

	public static ID of(String value) {
		return new ID(URI.create(value));
	}

	public ID(URI value) {
		Preconditions.checkNotNull(value);
		Preconditions.checkArgument(value.isAbsolute(), "Only absolute URIs are valid IDs");
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
		ID other = (ID) obj;
		return this.value.equals(other.value);
	}

	/**
	 * The string representation of the ID
	 */
	@Override
	public String toString() {
		return this.value;
	}

}
