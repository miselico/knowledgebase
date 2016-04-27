package miselico.prototypes.serializers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import miselico.prototypes.knowledgebase.Prototype;

public interface Serializer {
	public void serialize(Iterator<Prototype> ps, Writer w) throws IOException;

	public void serializeOne(Prototype p, Writer w) throws IOException;
}
