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
public class Datasets {

	public static KnowledgeBase.Builder idealCase(int n) {
		int layers = n + 1;
		System.out.println("####Fanout with n = " + n + " layers =" + layers + " ####");
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

	public static KnowledgeBase.Builder blocks(final int blocks) {
		final int numberPerlayer = 100000;
		ArrayList<Property> properties = Lists.newArrayList(Property.of("http://www.example.com#knows"));
		Random r = new Random(546876542346L);
		System.out.println("####blocks with " + blocks + " layers. ####");
		KnowledgeBase.Builder kbb = new KnowledgeBase.Builder(EmptyKnowledgeBase.instance);

		ID firstValue = Datasets.generateID(0, 0);
		for (int j = 0; j < numberPerlayer; j++) {
			Builder pb = Prototypes.builder(Prototype.P_0);
			for (Property property : properties) {
				pb.add(property, firstValue);
			}
			kbb.add(pb.build(Datasets.generateID(0, j)));
		}

		for (int i = 1; i < blocks; i++) {
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

	private static ID generateID(int i, int j) {
		return ID.of("http://www.example.com#object" + i + "_" + j);
	}

	private static ID generateID(int i) {
		return ID.of("http://www.example.com#object" + i);
	}

}
