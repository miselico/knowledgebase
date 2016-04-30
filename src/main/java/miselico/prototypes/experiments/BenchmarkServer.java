package miselico.prototypes.experiments;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.KnowledgeBase.Builder;
import miselico.prototypes.server.KBHandler;

/**
 * Start the server which was can be used for Benchmarking.
 * 
 * This server serves the {@link Datasets#blocks(n)} dataset with n set to 30,
 * ie., a KB containing 3M prototypes.
 * 
 * @author michael
 *
 */
public class BenchmarkServer {
	private BenchmarkServer() {
		// utility class
	}

	private static final double seeAlsoFraction = 0.10;

	static final int blocks = 30;

	public static void main(String[] args) throws Exception {
		Stopwatch w = Stopwatch.createStarted();
		Builder b = Datasets.blocks(BenchmarkServer.blocks);
		System.out.println(w.elapsed(TimeUnit.MILLISECONDS));
		KnowledgeBase base = b.build();
		Multimap<ID, URI> seeAlsoMap = HashMultimap.create();
		if (args.length > 0) {
			String seeAlsoURL = args[0];
			URIBuilder seeAlsoBuilder = new URIBuilder(seeAlsoURL);
			int prototypeCount = base.KB.size();
			int seeAlsoNeeded = (int) (prototypeCount * BenchmarkServer.seeAlsoFraction);
			int seeAlsoCounter = 0;
			for (ID id : base.KB.keySet()) {
				seeAlsoMap.put(id, seeAlsoBuilder.setParameter("q", id.toString()).build());
				seeAlsoCounter++;
				if (seeAlsoCounter == seeAlsoNeeded) {
					break;
				}
			}
		}
		KBHandler kbhandler = new KBHandler(base, x -> 300L, seeAlsoMap);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { kbhandler, new DefaultHandler() });
		GzipHandler gzip = new GzipHandler();
		gzip.setHandler(handlers);

		Server server = new Server(8080);
		server.setHandler(gzip);
		server.start();
		System.out.println("Server started");
		server.join();
	}
}
