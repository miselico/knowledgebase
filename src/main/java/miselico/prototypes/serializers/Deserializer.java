package miselico.prototypes.serializers;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import miselico.prototypes.knowledgebase.Prototype;

public interface Deserializer {

	/**
	 * Deserialize one prototypes from the given {@link Reader}.
	 * 
	 * The opposite of
	 * {@link Serializer#serializeOne(Prototype, java.io.Writer)}
	 * 
	 * This CANNOT be used to read a single prototype from a collection
	 * serialized using {@link Serializer#serialize(Iterable, java.io.Writer)}
	 * 
	 * @param reader
	 * 
	 * @throws IOException
	 *             if the Reader throws an exception in the process.
	 * @throws ParseException
	 *             if the serialized stream could not be interpreted as a stream
	 *             of prototypes.
	 */
	public Prototype deserializeOne(Reader reader) throws IOException, ParseException;

	/**
	 * Deserialize all prototypes from the given {@link Reader}.
	 * 
	 * The opposite of {@link Serializer#serialize(Iterator, java.io.Writer)}
	 * 
	 * @param reader
	 * 
	 * @throws IOException
	 *             if the Reader throws an exception in the process.
	 * @throws ParseException
	 *             if the serialized stream could not be interpreted as a
	 *             collection of prototypes.
	 */
	public List<Prototype> deserialize(Reader reader) throws IOException, ParseException;
}
