package miselico.prototypes.knowledgebase.experiments;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import miselico.prototypes.knowledgebase.EmptyKnowledgeBase;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.PredefinedKB;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.PrototypeDefinition;
import miselico.prototypes.knowledgebase.Prototypes;
import miselico.prototypes.knowledgebase.Prototypes.Builder;

public class ExperimentsFromArticle {

	public static void main(String[] args) {
		for (int n : new int[] { 19, 20, 21 }) {
			ExperimentsFromArticle.computeStats(ExperimentsFromArticle.idealCase(n));
			System.gc();
		}
		for (int blocks : new int[] { 10, 20, 30 }) {
			ExperimentsFromArticle.computeStats(ExperimentsFromArticle.blocks(blocks));
			System.gc();
		}
		for (int amount : new int[] { 1_000_000, 2_000_000, 3_000_000 }) {
			ExperimentsFromArticle.computeStats(ExperimentsFromArticle.incremental(amount));
			System.gc();
		}
	}

	public static void computeStats(KnowledgeBase.Builder kbb) {
		Stopwatch w = Stopwatch.createStarted();
		KnowledgeBase kb = kbb.build();
		w.stop();
		long building = w.elapsed(TimeUnit.MILLISECONDS);
		System.out.println("cons check done");
		System.gc();
		w.start();
		KnowledgeBase fixP = kb.computeFixPoint();
		w.stop();
		long fixpoint = w.elapsed(TimeUnit.MILLISECONDS);

		DescriptiveStatistics kbPropertyStats = new DescriptiveStatistics();
		for (PrototypeDefinition def : kb.KB.values()) {
			kbPropertyStats.addValue(def.add.entries().size());
		}

		DescriptiveStatistics fpPropertyStats = new DescriptiveStatistics();
		for (PrototypeDefinition def : fixP.KB.values()) {
			fpPropertyStats.addValue(def.add.entries().size());
		}

		System.out.println("Total number of prototypes " + fixP.size());
		System.out.println("time for consistency check " + building + "ms");
		System.out.println("Time for fixpoint computation " + fixpoint + "ms");
		System.out.println("KB property stats : " + kbPropertyStats);
		System.out.println("FP property stats : " + fpPropertyStats);
	}

	public static KnowledgeBase.Builder idealCase(int n) {
		int layers = n + 1;
		System.out.println("####Fanout with n = " + n + " layers =" + layers + " ####");
		KnowledgeBase.Builder kbb = new KnowledgeBase.Builder(EmptyKnowledgeBase.instance);
		Builder pb = Prototypes.builder(Prototype.P_0);
		kbb.add(pb.build(ExperimentsFromArticle.generateID(0, 0)));
		for (int i = 1; i < layers; i++) {
			for (int j = 0; j < Math.pow(2, i); j++) {
				ID base = ExperimentsFromArticle.generateID(i - 1, j >> 1);
				pb = Prototypes.builder(base);
				kbb.add(pb.build(ExperimentsFromArticle.generateID(i, j)));
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

		ID firstValue = ExperimentsFromArticle.generateID(0, 0);
		for (int j = 0; j < numberPerlayer; j++) {
			Builder pb = Prototypes.builder(Prototype.P_0);
			for (Property property : properties) {
				pb.add(property, firstValue);
			}
			kbb.add(pb.build(ExperimentsFromArticle.generateID(0, j)));
		}

		for (int i = 1; i < blocks; i++) {
			for (int j = 0; j < numberPerlayer; j++) {
				// a random one from the layer above
				ID base = ExperimentsFromArticle.generateID(i - 1, r.nextInt(numberPerlayer));
				Builder pb = Prototypes.builder(base);
				for (Property property : properties) {
					// a random one from the layer above
					ID val = ExperimentsFromArticle.generateID(i - 1, r.nextInt(numberPerlayer));
					pb.add(property, val);
				}
				kbb.add(pb.build(ExperimentsFromArticle.generateID(i, j)));
			}
		}
		return kbb;
	}

	private static KnowledgeBase.Builder incremental(final int amount) {
		final int PROP_MAX = 5;
		final int DISTINCT_PROP = 10;
		final ArrayList<Property> properties = new ArrayList<>(DISTINCT_PROP);
		for (int i = 0; i < DISTINCT_PROP; i++) {
			properties.add(Property.of("http://www.example.com#knows" + i));
		}
		System.out.println("#### StressTest ####");

		KnowledgeBase.Builder kbb = new KnowledgeBase.Builder(PredefinedKB.kb);

		Random r = new Random(54657879123L);

		Prototype zero = Prototypes.builder(Prototype.P_0).build(ExperimentsFromArticle.generateID(0));
		kbb.add(zero);

		for (int i = 1; i < amount; i++) {
			ID parent = ExperimentsFromArticle.generateID(r.nextInt(i));

			Builder pb = Prototypes.builder(parent);
			int nrOfProperties = r.nextInt(PROP_MAX);
			for (int j = 0; j < nrOfProperties; j++) {
				ID propval = ExperimentsFromArticle.generateID(r.nextInt(amount));
				Property prop = properties.get(r.nextInt(DISTINCT_PROP));
				pb.add(prop, propval);
			}

			kbb.add(pb.build(ExperimentsFromArticle.generateID(i)));
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
