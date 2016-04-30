package miselico.prototypes.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.IKnowledgeBase;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.KnowledgeBase.Builder;
import miselico.prototypes.knowledgebase.PredefinedKB;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.serializers.simple.SimpleDeserializer;
import miselico.prototypes.serializers.simple.SimpleSerializer;

/**
 * One possible way of serving prototypes. In concrete deployment the
 * administrator can setup a specific Jetty configuration as desired.
 * 
 * When run, specify a file with serialized prototypes ( Using
 * {@link SimpleSerializer}) . If no file is specified, a small example
 * knowledge base is loaded.
 * 
 * 
 * @author michael
 *
 */
public final class StartServer {

	private StartServer() {
		// utility class
	}

	/**
	 * One possible way of serving prototypes. In concrete deployment the
	 * administrator can setup a specific Jetty configuration as desired.
	 * 
	 * When run, specify a file with serialized prototypes. If no file is
	 * specified, a small example knowledge base is loaded.
	 * 
	 * @param args
	 *            The first argument can be a filename of serialized prototypes.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		InputStream input;
		if (args.length < 1) {
			input = StartServer.class.getResourceAsStream("exampleKB.proto");
		} else {
			String filename = args[0];
			input = new FileInputStream(new File(filename));
		}
		IKnowledgeBase base;
		try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
			List<Prototype> protos = new SimpleDeserializer().deserialize(reader);
			Builder builder = new KnowledgeBase.Builder(PredefinedKB.kb);
			base = builder.addAll(protos).build();
		}
		Multimap<ID, URI> seeAlsoMap = HashMultimap.create();

		KBHandler kbhandler = new KBHandler(base, x -> 300L, seeAlsoMap);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { kbhandler, new DefaultHandler() });
		GzipHandler gzip = new GzipHandler();
		gzip.setHandler(handlers);

		Server server = new Server(8080);
		server.setHandler(gzip);
		server.start();
		server.join();
	}
}
