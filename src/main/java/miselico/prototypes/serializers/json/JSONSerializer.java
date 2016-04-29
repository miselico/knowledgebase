package miselico.prototypes.serializers.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.serializers.Serializer;
import miselico.prototypes.serializers.json.GsonHelper.JSONPrototype;
import miselico.prototypes.serializers.json.GsonHelper.JSONPrototypes;

/**
 * A {@link Serializer} which produces JSON which can be read using
 * {@link JSONDeserializer}.
 * 
 * @author michael
 *
 */
public final class JSONSerializer implements Serializer {

	/**
	 * Currently this is implemented as a singleton because there is no state.
	 */
	private static JSONSerializer instance = new JSONSerializer();

	/**
	 * Create a {@link JSONSerializer}
	 * 
	 * @return
	 */
	public static JSONSerializer create() {
		return JSONSerializer.instance;
	}

	private JSONSerializer() {
	}

	@Override
	public void serializeOne(Prototype p, Writer w) throws IOException {
		JSONPrototype pp = new JSONPrototype(p);
		GsonHelper.gson.toJson(pp, w);
	}

	@Override
	public void serialize(Iterator<Prototype> ps, Writer w) throws IOException {
		JSONPrototypes pps = new JSONPrototypes(ps);
		GsonHelper.gson.toJson(pps, w);
	}
}
