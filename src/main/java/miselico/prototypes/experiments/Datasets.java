package miselico.prototypes.experiments;

import java.util.ArrayList;
import java.util.Random;

import com.google.common.collect.Lists;

import miselico.prototypes.knowledgebase.EmptyKnowledgeBase;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.PredefinedKB;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.Prototypes;
import miselico.prototypes.knowledgebase.Prototypes.Builder;

/**
 * Methods for the creation of synthetic datasets for experiments.
 * 
 * @author michael
 */
public final class Datasets {

	private Datasets() {
		// utility class
	}

	/**
	 * Generates a synthetic prototype KB. To generate the data we start with
	 * one prototype which derives from {@link Prototype#P_0}, next we create
	 * two prototypes which derive form the one, then we create four prototypes
	 * which derive from these two, and so on until we create 2^n prototypes
	 * which derive from 2^(n-1)
	 * 
	 * @param n
	 * @return A builder ready to create the Knowledge base containing the
	 *         prototypes.
	 */
	public static KnowledgeBase.Builder baseline(int n) {
		int layers = n + 1;
		System.out.println("####baseline with n = " + n + " layers =" + layers + " ####");
		KnowledgeBase.Builder kbb = new KnowledgeBase.Builder(EmptyKnowledgeBase.instance);
		Builder pb = Prototypes.builder(Prototype.P_0);
		kbb.add(pb.build(Datasets.generateID(0, 0)));
		for (int i = 1; i < layers; i++) {
			for (int j = 0; j < Math.pow(2, i); j++) {
				ID base = Datasets.generateID(i - 1, j >> 1);
				pb = Prototypes.builder(base);
				kbb.add(pb.build(Datasets.generateID(i, j)));
			}
		}
		return kbb;
	}

	/**
	 * Generates a synthetic prototype KB. Creates n blocks of 100,000
	 * prototypes. All prototypes in each block derive from a randomly chosen
	 * prototype in a lower block. Then, each of the prototypes has a property
	 * with a value randomly chosen from the block below. In the lowest block,
	 * the base is always P_0 and the value for the property is always the same
	 * fixed prototype.
	 * 
	 * @param n
	 * @return A builder ready to create the Knowledge base containing the
	 *         prototypes.
	 */
	public static KnowledgeBase.Builder blocks(final int n) {
		final int numberPerlayer = 100000;
		ArrayList<Property> properties = Lists.newArrayList(Property.of("http://www.example.com#knows"));
		Random r = new Random(546876542346L);
		System.out.println("####blocks with " + n + " layers. ####");
		KnowledgeBase.Builder kbb = new KnowledgeBase.Builder(EmptyKnowledgeBase.instance);

		ID firstValue = Datasets.generateID(0, 0);
		for (int j = 0; j < numberPerlayer; j++) {
			Builder pb = Prototypes.builder(Prototype.P_0);
			for (Property property : properties) {
				pb.add(property, firstValue);
			}
			kbb.add(pb.build(Datasets.generateID(0, j)));
		}

		for (int i = 1; i < n; i++) {
			for (int j = 0; j < numberPerlayer; j++) {
				// a random one from the layer above
				ID base = Datasets.generateID(i - 1, r.nextInt(numberPerlayer));
				Builder pb = Prototypes.builder(base);
				for (Property property : properties) {
					// a random one from the layer above
					ID val = Datasets.generateID(i - 1, r.nextInt(numberPerlayer));
					pb.add(property, val);
				}
				kbb.add(pb.build(Datasets.generateID(i, j)));
			}
		}
		return kbb;
	}

	/**
	 * Generates a synthetic prototype KB. The KB is constructed by adding
	 * amount prototypes to the KB, one at a time. Each prototype gets a
	 * randomly selected earlier created one as its base. Furthermore, each
	 * prototype gets between 0 and 4 properties chosen from 10 distinct ones
	 * (with replacement). The value of each property is chosen randomly among
	 * the prototypes.
	 * 
	 * @param n
	 * @return A builder ready to create the Knowledge base containing the
	 *         prototypes.
	 */
	public static KnowledgeBase.Builder incremental(final int amount) {
		final int PROP_MAX = 5;
		final int DISTINCT_PROP = 10;
		final ArrayList<Property> properties = new ArrayList<>(DISTINCT_PROP);
		for (int i = 0; i < DISTINCT_PROP; i++) {
			properties.add(Property.of("http://www.example.com#knows" + i));
		}
		System.out.println("#### StressTest ####");

		KnowledgeBase.Builder kbb = new KnowledgeBase.Builder(PredefinedKB.kb);

		Random r = new Random(54657879123L);

		Prototype zero = Prototypes.builder(Prototype.P_0).build(Datasets.generateID(0));
		kbb.add(zero);

		for (int i = 1; i < amount; i++) {
			ID parent = Datasets.generateID(r.nextInt(i));

			Builder pb = Prototypes.builder(parent);
			int nrOfProperties = r.nextInt(PROP_MAX);
			for (int j = 0; j < nrOfProperties; j++) {
				ID propval = Datasets.generateID(r.nextInt(amount));
				Property prop = properties.get(r.nextInt(DISTINCT_PROP));
				pb.add(prop, propval);
			}

			kbb.add(pb.build(Datasets.generateID(i)));
		}
		return kbb;
	}

	/**
	 * For layered synthetic datasets this results in the ID of prototype j on
	 * level i.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	static ID generateID(int i, int j) {
		return ID.of("http://www.example.com#object" + i + "_" + j);
	}

	private static ID generateID(int i) {
		return ID.of("http://www.example.com#object" + i);
	}

}
