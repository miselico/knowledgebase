package miselico.prototypes.knowledgebase.experiments;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.base.Stopwatch;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.PredefinedKB;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.Prototypes;
import miselico.prototypes.knowledgebase.Prototypes.Builder;

public class ScalabilityTest {
	private static final int PROP_MAX = 10;
	private static final int n = 100000;
	private static final int PROC_COUNT = 10;

	public static void main(String[] args) {

		KnowledgeBase.Builder kbb = new KnowledgeBase.Builder(PredefinedKB.kb);

		Random r = new Random(54657879123L);

		Prototype zero = Prototypes.builder(Prototype.P_0).build(ScalabilityTest.generateID(0));
		kbb.add(zero);

		for (int i = 1; i < ScalabilityTest.n; i++) {
			// TODO use skewed distribution
			ID parent = ScalabilityTest.generateID(r.nextInt(i));

			Builder pb = Prototypes.builder(parent);
			// TODO add/remove properties
			int nrOfProperties = r.nextInt(ScalabilityTest.PROP_MAX);
			for (int j = 0; j < nrOfProperties; j++) {
				ID propval = ScalabilityTest.generateID(r.nextInt(ScalabilityTest.n));
				Property prop = Property.of("http://www.example.com#knows" + r.nextInt(ScalabilityTest.PROC_COUNT));
				pb.add(prop, propval);
			}

			kbb.add(pb.build(ScalabilityTest.generateID(i)));
		}
		Stopwatch w = Stopwatch.createStarted();
		KnowledgeBase kb = kbb.build();
		w.stop();
		System.out.println(w.elapsed(TimeUnit.MILLISECONDS));
		w.start();
		KnowledgeBase fixP = kb.computeFixPoint();
		w.stop();
		System.out.println(w.elapsed(TimeUnit.MILLISECONDS));

		DescriptiveStatistics s = new DescriptiveStatistics(ScalabilityTest.n);

		for (int i = 0; i < ScalabilityTest.n; i++) {
			s.addValue(fixP.get(ScalabilityTest.generateID(i)).def.add.entries().size());
		}
		System.out.println("Properties amount : " + s);
		System.out.println(fixP.hashCode());
	}

	public static ID generateID(int i) {
		return ID.of("http://www.example.com#object" + i);
	}

}
