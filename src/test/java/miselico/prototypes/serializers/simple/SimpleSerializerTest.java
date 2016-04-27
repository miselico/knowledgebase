package miselico.prototypes.serializers.simple;

import miselico.prototypes.serializers.Deserializer;
import miselico.prototypes.serializers.Serializer;
import miselico.prototypes.serializers.SerializerTest;

public class SimpleSerializerTest extends SerializerTest {

	@Override
	protected Serializer getSerializer() {
		return new SimpleSerializer();
	}

	@Override
	protected Deserializer getDeserializer() {
		return new SimpleDeserializer();
	}

}
