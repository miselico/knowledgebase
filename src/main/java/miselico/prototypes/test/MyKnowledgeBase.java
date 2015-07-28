package miselico.prototypes.test;

import miselico.prototypes.knowledgebase.AddChangeSet;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.KnowledgeBase.Builder;
import miselico.prototypes.knowledgebase.PredefinedKB;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.RemoveChangeSet;

public class MyKnowledgeBase {

	private static final Property hasName = Property.of("http://example.com/#hasName");
	private static final Property livesIn = Property.of("http://example.com/#livesIn");

	public static void main(String[] args) {

		// City: no properties
		Prototype City = Prototype.create(ID.of("http://example.com/#City"), Prototype.P_0, RemoveChangeSet.empty(), AddChangeSet.empty());

		// define Galway, just one property
		AddChangeSet galwayProp = AddChangeSet.builder().andAdd(MyKnowledgeBase.hasName, PredefinedKB.get("Galway").id).build();
		Prototype Galway = Prototype.create(ID.of("http://example.ie/#Galway"), City, RemoveChangeSet.empty(), galwayProp);

		// for multiple properties
		AddChangeSet.Builder adds = AddChangeSet.builder();
		adds.andAdd(MyKnowledgeBase.hasName, PredefinedKB.get("Michael").id);
		adds.andAdd(MyKnowledgeBase.livesIn, Galway.id);
		// forward reference using ID, also an example of a property with
		// multiple values
		adds.andAdd(MyKnowledgeBase.livesIn, ID.of("http://example.fi/#Jyvaskyla"));
		AddChangeSet add = adds.build();
		Prototype Michael = Prototype.create(ID.of("http://example.org/#Michael"), Prototype.P_0, RemoveChangeSet.empty(), add);

		// define Jyvaskyla as being Galway, but redefine the name
		RemoveChangeSet removeName = RemoveChangeSet.builder().andRemove(MyKnowledgeBase.hasName, PredefinedKB.get("Galway").id).build();
		AddChangeSet jyvProp = AddChangeSet.builder().andAdd(MyKnowledgeBase.hasName, PredefinedKB.get("Jyvaskyla").id).build();
		Prototype Jyvaskyla = Prototype.create(ID.of("http://example.fi/#Jyvaskyla"), Galway, removeName, jyvProp);

		// The predefined KB is all there is as an external source
		// in more advanced set-ups, there could be real external sources as
		// well. The external source is used to check consistency and compute
		// fix-points.
		Builder b = new KnowledgeBase.Builder(PredefinedKB.kb);
		b.add(City);
		b.add(Michael);
		b.add(Galway);
		b.add(Jyvaskyla);

		KnowledgeBase kb = b.build();
		System.out.println(kb);

		// now, we add Stefan. He is like Michael, but lives in Aachen and not
		// in JKL.
		// Further, he has a different name

		b = new KnowledgeBase.Builder(kb);

		// stuff to be removed
		RemoveChangeSet.Builder removeFromMichaelBuilder = RemoveChangeSet.builder();
		// removing all properties for name
		removeFromMichaelBuilder.andRemoveAll(MyKnowledgeBase.hasName);
		// but only Jyvaskyla for the cities. (keeping Galway)
		removeFromMichaelBuilder.andRemove(MyKnowledgeBase.livesIn, Jyvaskyla.id);
		RemoveChangeSet removeFromMichael = removeFromMichaelBuilder.build();

		// and added
		AddChangeSet.Builder addsStefan = AddChangeSet.builder();
		addsStefan.andAdd(MyKnowledgeBase.hasName, PredefinedKB.get("Stefan").id);
		addsStefan.andAdd(MyKnowledgeBase.livesIn, ID.of("http://example.de/#Aachen"));
		AddChangeSet addToMichael = addsStefan.build();

		Prototype Stefan = Prototype.create(ID.of("http://example.org/#Stefan"), Michael, removeFromMichael, addToMichael);
		b.add(Stefan);

		// building the KB at this point will fail: Aachen is not defined
		// yet
		try {
			b.build();
			System.err.println("Expected error NOT thrown");
		} catch (Error e) {
			System.out.println("Expected error:");
			e.printStackTrace();
		}

		// define Aachen, just one property added to City
		AddChangeSet aachenProp = AddChangeSet.builder().andAdd(MyKnowledgeBase.hasName, PredefinedKB.get("Aachen").id).build();
		Prototype Aachen = Prototype.create(ID.of("http://example.de/#Aachen"), City, RemoveChangeSet.empty(), aachenProp);
		b.add(Aachen);
		kb = b.build();
		System.out.println("KB after adding Stefan");
		System.out.println(kb);

		System.out.println("Computing fixpoint");
		System.out.println(kb.computeFixPoint());
	}
}
