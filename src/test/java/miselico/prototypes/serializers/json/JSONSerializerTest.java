package miselico.prototypes.serializers.json;

import miselico.prototypes.serializers.Deserializer;
import miselico.prototypes.serializers.Serializer;
import miselico.prototypes.serializers.SerializerTest;

public class JSONSerializerTest extends SerializerTest {

	@Override
	protected Serializer getSerializer() {
		return JSONSerializer.create();
	}

	@Override
	protected Deserializer getDeserializer() {
		return JSONDeserializer.create();
	}

}
