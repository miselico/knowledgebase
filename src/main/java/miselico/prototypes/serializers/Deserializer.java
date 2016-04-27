package miselico.prototypes.serializers;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import miselico.prototypes.knowledgebase.Prototype;

public interface Deserializer {
	public Prototype deserializeOne(Reader reader) throws IOException, ParseException;

	public List<Prototype> deserialize(Reader reader) throws IOException, ParseException;
}
