package miselico.prototypes.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;

import miselico.prototypes.experiments.MyKnowledgeBase;
import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.IFPKnowledgeBase;
import miselico.prototypes.knowledgebase.IKnowledgeBase;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.serializers.Deserializer;
import miselico.prototypes.serializers.ParseException;
import miselico.prototypes.serializers.json.JSONDeserializer;

/**
 * An {@link IKnowledgeBase} which interacts with a remote prototype knowledge
 * base.
 * 
 * @author michael
 *
 */
public class RemoteKB implements IKnowledgeBase, IFPKnowledgeBase, AutoCloseable {

	/**
	 * When a prototype is requested from a remote source, the remote source
	 * might indicate several alternative locations from which a representation
	 * of the prototype can be found. This class includes both the prototype and
	 * the alternative locations
	 * 
	 * @author michael
	 *
	 */
	public static class PrototypeWithAlternates extends Prototype {
		private final ImmutableSet<URI> alt;

		public PrototypeWithAlternates(Prototype p, Collection<URI> alternatives) {
			super(p.id, p.def);
			this.alt = ImmutableSet.copyOf(alternatives);
		}

		public ImmutableSet<URI> getAlternatives() {
			return this.alt;
		}
	}

	/**
	 * Only needed for debugging purposes. To see the content of the http
	 * request on the wire.
	 */
	private static final boolean CONTENTCOMPRESSION = true;
	private final CloseableHttpClient cachingClient;
	private final URI datasource;
	private final Deserializer des;

	/**
	 * Construct a {@link RemoteKB} with default httpclient and JSON
	 * deserialization. The default httpclient support content compression,
	 * multiple connections, reuse of connections, and implements caching.
	 * 
	 * @param location
	 *            The location of the knowledge base.
	 */
	public RemoteKB(URI location) {
		this.datasource = location;
		CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(10000).setMaxObjectSize(4096).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).setCookieSpec(CookieSpecs.IGNORE_COOKIES).setContentCompressionEnabled(RemoteKB.CONTENTCOMPRESSION).build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setDefaultMaxPerRoute(1000);
		this.cachingClient = CachingHttpClients.custom().setCacheConfig(cacheConfig).setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).build();
		this.des = JSONDeserializer.create();
	}

	/**
	 * Construct a {@link RemoteKB} with the provided location, http client and
	 * deserialization mechanism.
	 * 
	 * @param datasource
	 * @param httpClient
	 * @param des
	 */
	public RemoteKB(URI datasource, CloseableHttpClient httpClient, Deserializer des) {
		this.datasource = datasource;
		this.cachingClient = httpClient;
		this.des = des;
	}

	@Override
	public Optional<PrototypeWithAlternates> isDefined(ID id) {
		URIBuilder b = new URIBuilder(this.datasource);
		b.addParameter("p", id.toString());
		URI uri;
		try {
			uri = b.build();
		} catch (URISyntaxException e) {
			throw new Error("This URI cannot be wrong");
		}
		return Optional.ofNullable(this.fetch(uri));
	}

	@Override
	public PrototypeWithAlternates computeFixPoint(ID id) {
		URIBuilder b = new URIBuilder(this.datasource);
		b.addParameter("p", id.toString());
		b.addParameter("fp", "true");
		URI uri;
		try {
			uri = b.build();
		} catch (URISyntaxException e) {
			throw new Error("This URI cannot be wrong");
		}
		PrototypeWithAlternates result = this.fetch(uri);
		if (result == null) {
			throw new Error("Prototype with ID " + id + "could not be found.");
		}
		return result;
	}

	private PrototypeWithAlternates fetch(URI uri) {
		HttpGet httpget = new HttpGet(uri);
		HttpCacheContext context = new HttpCacheContext();
		try (CloseableHttpResponse response = this.cachingClient.execute(httpget, context)) {

			if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
				Logger.getLogger(RemoteKB.class.getName()).fine("request " + uri + "failed, returning empty prototype");
				return null;
			}
			// parse link headers to search for alternates
			Set<URI> alternates = new HashSet<>();
			for (Header linkHeader : response.getHeaders("Link")) {
				try {
					alternates.addAll(RemoteKB.linkHeaderParser.parse(linkHeader.getValue()));
				} catch (URISyntaxException e) {
					Logger.getLogger(RemoteKB.class.getName()).info("An excepiton was thrown while parsing the Link header. This exception is silenced. Original message :" + e.getMessage());
				}
			}

			Prototype prot = this.des.deserializeOne(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
			return new PrototypeWithAlternates(prot, alternates);
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		} finally {
			httpget.reset();
		}
	}

	private static final LimitedLinkHeaderParser linkHeaderParser = new LimitedLinkHeaderParser();

	/**
	 * Close this remote KB, releasing all resources related to the http Client.
	 */
	@Override
	public void close() throws IOException {
		this.cachingClient.close();
	}

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		KnowledgeBase base = MyKnowledgeBase.getSomebase();

		RemoteKB kb = new RemoteKB(new URI("http://localhost:8080/"));
		for (ID id : base.prototypes().keySet()) {
			System.out.println(RemoteKB.timeFetch(kb, id));
		}
		Thread.sleep(5000);
		for (ID id : base.prototypes().keySet()) {
			System.out.println(RemoteKB.timeFetch(kb, id));
		}
	}

	private static long timeFetch(RemoteKB kb, ID res) {
		Stopwatch w = Stopwatch.createStarted();
		Optional<PrototypeWithAlternates> prot = kb.isDefined(res);
		long time = w.elapsed(TimeUnit.MILLISECONDS);
		System.out.println(prot.isPresent());
		System.out.println(prot.get().alt);
		return time;
	}

}
