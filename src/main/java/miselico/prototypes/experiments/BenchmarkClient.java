package miselico.prototypes.experiments;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import miselico.prototypes.client.RemoteKB;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Prototype;

public class BenchmarkClient {

	private static final int layers = BenchmarkServer.blocks;
	private static final int pPerLayer = 100000;

	private static final int batches = 10;
	private static final int batchFactor = 1000;

	public static void main(String[] args) throws URISyntaxException, IOException {

		URI address = new URI("http://localhost:8080/");
		int concurrentRequests = 1;
		if (args.length > 0) {
			concurrentRequests = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			address = new URI(args[1]);
		}

		Random r = new Random(475646L);
		// variable to make sure the JIT does not throw out the computations
		int _void = 0;
		ExecutorService pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(concurrentRequests));

		for (int i = 1; i <= BenchmarkClient.batches; i++) {
			Stopwatch w = Stopwatch.createStarted();
			int batchSize = i * BenchmarkClient.batchFactor;
			List<Future<Prototype>> fprots = new ArrayList<>(batchSize);
			try (RemoteKB rkb = new RemoteKB(address);) {
				for (int j = 0; j < batchSize; j++) {
					ID id = Datasets.generateID(r.nextInt(BenchmarkClient.layers), r.nextInt(BenchmarkClient.pPerLayer));
					fprots.add(pool.submit(() -> rkb.computeFixPoint(id)));
				}
				for (Future<Prototype> future : fprots) {
					_void ^= Futures.getUnchecked(future).hashCode();
				}
			}
			System.out.println("Experiment for " + batchSize + " prototypes finished in " + w.elapsed(TimeUnit.MILLISECONDS) + "ms");
		}
		if (_void == 0) {
			// close to impossible, but some side effect is needed to ensure
			// computation.
			System.out.println();
		}
	}
}
