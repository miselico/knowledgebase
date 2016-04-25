package miselico.prototypes.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.experiments.MyKnowledgeBase;

public class StartServer {

	public static void main(String[] args) throws Exception {
		KnowledgeBase base = MyKnowledgeBase.getSomebase();
		SimpleKBHandler kbhandler = new SimpleKBHandler(base, x -> 300L);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { kbhandler, new DefaultHandler() });
		GzipHandler gzip = new GzipHandler();
		gzip.setHandler(handlers);

		Server server = new Server(8080);
		server.setHandler(gzip);
		server.start();
		try {
			Thread.sleep(1 * 60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Stopping server after five minutes.");
		server.stop();
	}
}
