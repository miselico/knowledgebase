package miselico.prototypes.experiments;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.base.Stopwatch;

import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.PrototypeDefinition;

/**
 * Benchmarks for the standalone {@link KnowledgeBase} implementation.
 * 
 * @author michael
 *
 */
public final class StandaloneExperiments {

	private StandaloneExperiments() {
		// utility class
	}

	public static void main(String[] args) {
		for (int n : new int[] { 19, 20, 21 }) {
			StandaloneExperiments.computeStats(Datasets.idealCase(n));
			System.gc();
		}
		for (int blocks : new int[] { 10, 20, 30 }) {
			StandaloneExperiments.computeStats(Datasets.blocks(blocks));
			System.gc();
		}
		for (int amount : new int[] { 1_000_000, 2_000_000, 3_000_000 }) {
			StandaloneExperiments.computeStats(Datasets.incremental(amount));
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

}
