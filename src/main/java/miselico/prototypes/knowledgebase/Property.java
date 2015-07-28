package miselico.prototypes.knowledgebase;

import java.net.URI;

import com.google.common.base.Preconditions;

public class Property {
	private final URI value;

	public static Property of(String value) {
		return new Property(URI.create(value));
	}

	public Property(URI value) {
		Preconditions.checkNotNull(value);
		this.value = value;
	}

	public URI getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.value == null) ? 0 : this.value.hashCode());
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
		Property other = (Property) obj;
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Property [value=" + this.value + "]";
	}

}
