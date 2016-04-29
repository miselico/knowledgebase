package miselico.prototypes.server;

import java.net.URI;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import miselico.prototypes.experiments.MyKnowledgeBase;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.KnowledgeBase;

/**
 * One possible way of serving prototypes. In concrete deployment the
 * administrator can setup a specific Jetty configuration as desired.
 * 
 * This setup is only for experimentation and shuts down after two minutes.
 * 
 * @author michael
 *
 */
public class StartServer {

	private static final int UPTIMEMINUTES = 2;

	public static void main(String[] args) throws Exception {
		KnowledgeBase base = MyKnowledgeBase.getSomebase();

		Multimap<ID, URI> seeAlsoMap = HashMultimap.create();
		for (ID id : base.KB.keySet()) {
			seeAlsoMap.put(id, new URIBuilder("https://www.google.com/search").addParameter("q", id.toString()).build());
			seeAlsoMap.put(id, new URIBuilder("https://search.yahoo.com/search").addParameter("p", id.toString()).build());
		}

		KBHandler kbhandler = new KBHandler(base, x -> 300L, seeAlsoMap);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { kbhandler, new DefaultHandler() });
		GzipHandler gzip = new GzipHandler();
		gzip.setHandler(handlers);

		Server server = new Server(8080);
		server.setHandler(gzip);
		server.start();
		try {
			Thread.sleep(StartServer.UPTIMEMINUTES * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Stopping server after " + StartServer.UPTIMEMINUTES + " minute(s).");
		server.stop();
	}
}
