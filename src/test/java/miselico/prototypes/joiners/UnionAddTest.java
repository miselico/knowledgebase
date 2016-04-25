package miselico.prototypes.joiners;

import org.junit.Assert;
import org.junit.Test;

import miselico.prototypes.knowledgebase.AddChangeSet;
import miselico.prototypes.knowledgebase.AddChangeSet.Builder;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;

public class UnionAddTest {

	@Test
	public void testUniontComplex() {
		Builder aBuilder = AddChangeSet.builder();
		aBuilder.andAdd(Property.of("P:3"), ID.of("V:3"));
		aBuilder.andAdd(Property.of("P:4"), ID.of("V:4"));
		aBuilder.andAdd(Property.of("P:4"), ID.of("V:5"));
		AddChangeSet a = aBuilder.build();

		Builder bBuilder = AddChangeSet.builder();
		bBuilder.andAdd(Property.of("P:1"), ID.of("V:1"));
		bBuilder.andAdd(Property.of("P:4"), ID.of("V:6"));
		bBuilder.andAdd(Property.of("P:4"), ID.of("V:5"));
		AddChangeSet b = bBuilder.build();

		Builder expectedCBuilder = AddChangeSet.builder();
		expectedCBuilder.andAdd(Property.of("P:3"), ID.of("V:3"));
		expectedCBuilder.andAdd(Property.of("P:4"), ID.of("V:4"));
		expectedCBuilder.andAdd(Property.of("P:4"), ID.of("V:5"));
		expectedCBuilder.andAdd(Property.of("P:1"), ID.of("V:1"));
		expectedCBuilder.andAdd(Property.of("P:4"), ID.of("V:6"));
		AddChangeSet expectedC = expectedCBuilder.build();

		AddChangeSet c = new UnionAdd().join(a, b);

		Assert.assertEquals("Union did not lead to the expected result", expectedC, c);
	}

}
