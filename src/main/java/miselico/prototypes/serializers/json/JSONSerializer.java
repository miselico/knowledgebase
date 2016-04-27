package miselico.prototypes.serializers.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.serializers.Serializer;
import miselico.prototypes.serializers.json.GsonHelper.JSONPrototype;
import miselico.prototypes.serializers.json.GsonHelper.JSONPrototypes;

public class JSONSerializer implements Serializer {

	private static JSONSerializer instance = new JSONSerializer();

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
