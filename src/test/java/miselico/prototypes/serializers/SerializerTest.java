package miselico.prototypes.serializers;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import miselico.prototypes.experiments.MyKnowledgeBase;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.PrototypeDefinition;
import miselico.prototypes.knowledgebase.Prototypes;
import miselico.prototypes.knowledgebase.Prototypes.Builder;

public abstract class SerializerTest {

	private Serializer ser;
	private Deserializer deser;

	protected abstract Serializer getSerializer();

	protected abstract Deserializer getDeserializer();

	@Before
	public void init() {
		this.ser = this.getSerializer();
		this.deser = this.getDeserializer();
	}

	@Test
	public void testSerializePrototype() throws IOException, ParseException {
		KnowledgeBase b = MyKnowledgeBase.getSomebase();

		for (ID id : b.prototypes().keySet()) {
			Prototype prot = b.isDefined(id).get();
			StringWriter w = new StringWriter();
			this.ser.serializeOne(prot, w);
			// System.out.println(w);
			StringReader r = new StringReader(w.toString());
			Prototype protD = this.deser.deserializeOne(r);
			Assert.assertEquals(prot.id, protD.id);
			Assert.assertEquals(prot.def.parent, protD.def.parent);
			Assert.assertEquals(prot.def.add, protD.def.add);
			Assert.assertEquals(prot.def.remove, protD.def.remove);
			r = new StringReader(w.toString());
			protD = this.deser.deserializeOne(r);
			Assert.assertEquals(prot.id, protD.id);
			Assert.assertEquals(prot.def.parent, protD.def.parent);
			Assert.assertEquals(prot.def.add, protD.def.add);
			Assert.assertEquals(prot.def.remove, protD.def.remove);
		}
	}

	@Test
	public void testSerializePrototypes() throws IOException, ParseException {
		KnowledgeBase b = MyKnowledgeBase.getSomebase();
		List<Prototype> protoOriginal = b.prototypes().entrySet().stream().map(e -> new Prototype(e.getKey(), e.getValue())).collect(Collectors.toList());
		StringWriter w = new StringWriter();
		this.ser.serialize(protoOriginal.iterator(), w);
		// System.out.println(w);
		StringReader r = new StringReader(w.toString());
		List<Prototype> protoResult = this.deser.deserialize(r);

		HashMap<ID, PrototypeDefinition> allOriginal = new HashMap<>(b.prototypes());

		Assert.assertEquals("The resulting size does not equal the original", protoOriginal.size(), protoResult.size());

		for (Prototype resultingProto : protoResult) {
			PrototypeDefinition originalProto = allOriginal.remove(resultingProto.id);
			Assert.assertEquals(resultingProto.def.parent, originalProto.parent);
			Assert.assertEquals(resultingProto.def.add, originalProto.add);
			Assert.assertEquals(resultingProto.def.remove, originalProto.remove);
		}
		Assert.assertTrue("Not all prototypes where in the result", allOriginal.size() == 0);

	}

	@Test
	public void testSerializeMany() throws IOException, ParseException {

		for (int i = 1; i < 10000; i++) {
			Builder builder = Prototypes.builder(ID.of("http://example.com#soMany" + i));
			for (int j = 0; j < 10; j++) {
				builder.add(Property.of("http://example.com#prop" + (j % 3)), ID.of("http://example.com#val" + j));
			}
			for (int j = 0; j < 10; j++) {
				builder.add(Property.of("http://example.com#proprem" + (j % 3)), ID.of("http://example.com#val" + j));
			}
			Prototype prot = builder.build(ID.of("http://example.com#soMany" + (i - 1)));
			StringWriter w = new StringWriter();
			this.ser.serialize(ImmutableList.of(prot).iterator(), w);
			StringReader r = new StringReader(w.toString());
			Collection<Prototype> protCol = this.deser.deserialize(r);
			Assert.assertEquals(protCol.size(), 1);
			Prototype protD = protCol.iterator().next();
			Assert.assertEquals(prot.id, protD.id);
			Assert.assertEquals(prot.def.parent, protD.def.parent);
			Assert.assertEquals(prot.def.add, protD.def.add);
			Assert.assertEquals(prot.def.remove, protD.def.remove);
			r = new StringReader(w.toString());
		}
	}

}
