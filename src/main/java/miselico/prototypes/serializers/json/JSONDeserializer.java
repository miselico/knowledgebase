package miselico.prototypes.serializers.json;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.serializers.Deserializer;
import miselico.prototypes.serializers.ParseException;
import miselico.prototypes.serializers.json.GsonHelper.JSONPrototype;
import miselico.prototypes.serializers.json.GsonHelper.JSONPrototypes;

/**
 * A {@link Deserializer} which reads JSON produced by {@link JSONSerializer}
 * and converts it back to {@link Prototype}s.
 * 
 * @author michael
 *
 */
public class JSONDeserializer implements Deserializer {

	/**
	 * Currently this is implemented as a singleton because there is no state.
	 */
	private static JSONDeserializer instance = new JSONDeserializer();

	/**
	 * Create a {@link JSONSeserializer}
	 * 
	 * @return
	 */
	public static JSONDeserializer create() {
		return JSONDeserializer.instance;
	}

	private JSONDeserializer() {
	}

	@Override
	public Prototype deserializeOne(Reader reader) throws IOException, ParseException {
		JSONPrototype pp = GsonHelper.gson.fromJson(reader, JSONPrototype.class);
		return pp.asPrototype();
	}

	@Override
	public List<Prototype> deserialize(Reader reader) throws IOException, ParseException {
		JSONPrototypes pps = GsonHelper.gson.fromJson(reader, JSONPrototypes.class);
		return pps.asPrototypeList();
	}
}
