package miselico.prototypes.joiners;

import org.junit.Assert;
import org.junit.Test;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.RemoveChangeSet;
import miselico.prototypes.knowledgebase.RemoveChangeSet.Builder;

public class UnionRemTest {

	@Test
	public void testUniontComplex() {
		Builder aBuilder = RemoveChangeSet.builder();
		aBuilder.andRemoveAll(Property.of("P:1"));
		aBuilder.andRemoveAll(Property.of("P:2"));
		aBuilder.andRemove(Property.of("P:3"), ID.of("V:3"));
		aBuilder.andRemove(Property.of("P:4"), ID.of("V:4"));
		aBuilder.andRemove(Property.of("P:4"), ID.of("V:5"));
		RemoveChangeSet a = aBuilder.build();

		Builder bBuilder = RemoveChangeSet.builder();
		bBuilder.andRemoveAll(Property.of("P:3"));
		bBuilder.andRemoveAll(Property.of("P:2"));
		bBuilder.andRemove(Property.of("P:1"), ID.of("V:1"));
		bBuilder.andRemove(Property.of("P:4"), ID.of("V:6"));
		bBuilder.andRemove(Property.of("P:4"), ID.of("V:5"));
		RemoveChangeSet b = bBuilder.build();

		Builder expectedCBuilder = RemoveChangeSet.builder();
		expectedCBuilder.andRemoveAll(Property.of("P:1"));
		expectedCBuilder.andRemoveAll(Property.of("P:2"));
		expectedCBuilder.andRemoveAll(Property.of("P:3"));
		expectedCBuilder.andRemove(Property.of("P:4"), ID.of("V:4"));
		expectedCBuilder.andRemove(Property.of("P:4"), ID.of("V:5"));
		expectedCBuilder.andRemove(Property.of("P:4"), ID.of("V:6"));
		RemoveChangeSet expectedC = expectedCBuilder.build();

		RemoveChangeSet c = new UnionRemove().join(a, b);

		Assert.assertEquals("Union did not lead to the expected result", expectedC, c);
	}

}
