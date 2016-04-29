package miselico.prototypes.serializers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import miselico.prototypes.knowledgebase.Prototype;

/**
 * A {@link Serializer} converts a prototype to a stream of characters.
 * 
 * @author michael
 *
 */
public interface Serializer {

	/**
	 * Serialize all prototypes of the given {@link Iterator} to the writer.
	 * 
	 * The opposite of {@link Deserializer#deserialize(java.io.Reader)}
	 * 
	 * @param ps
	 * @param to
	 * @throws IOException
	 *             if the Writer throws an exception in the process.
	 */
	public void serialize(Iterator<Prototype> ps, Writer to) throws IOException;

	/**
	 * Serialize all prototypes of the given {@link Iterable} to the writer.
	 * 
	 * The opposite of {@link Deserializer#deserialize(java.io.Reader)}
	 * 
	 * @param ps
	 * @param to
	 * @throws IOException
	 *             if the Writer throws an exception in the process.
	 */
	public default void serialize(Iterable<Prototype> ps, Writer to) throws IOException {
		this.serialize(ps.iterator(), to);
	}

	/**
	 * Serialize one prototype to the writer. the opposite of
	 * {@link Deserializer#deserializeOne(java.io.Reader)}.
	 * 
	 * There is NO guarantee that a single serialized prototype can be read as a
	 * collection of one prototype by
	 * {@link Deserializer#deserialize(java.io.Reader)}
	 * 
	 * @param p
	 * @param to
	 * @throws IOException
	 */
	public void serializeOne(Prototype p, Writer to) throws IOException;
}
