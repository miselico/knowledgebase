package miselico.prototypes.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;

import com.google.common.base.Stopwatch;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.IKnowledgeBase;
import miselico.prototypes.knowledgebase.KnowledgeBase;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.experiments.MyKnowledgeBase;
import miselico.prototypes.serializers.ParseException;
import miselico.prototypes.serializers.SimpleDeserializer;

public class RemoteKB implements IKnowledgeBase {

	private static final boolean CONTENTCOMPRESSION = false;
	private final CloseableHttpClient cachingClient;
	private final URI datasource;
	private final SimpleDeserializer des;

	public RemoteKB(URI datasource) {
		this.datasource = datasource;
		CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(1000).setMaxObjectSize(8192).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).setCookieSpec(CookieSpecs.IGNORE_COOKIES).setContentCompressionEnabled(RemoteKB.CONTENTCOMPRESSION).build();
		this.cachingClient = CachingHttpClients.custom().setCacheConfig(cacheConfig).setDefaultRequestConfig(requestConfig).build();
		this.des = new SimpleDeserializer();
	}

	@Override
	public Optional<Prototype> isDefined(ID id) {
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

	private Prototype fetch(URI uri) {
		HttpCacheContext context = HttpCacheContext.create();
		HttpGet httpget = new HttpGet(uri);
		try (CloseableHttpResponse response = this.cachingClient.execute(httpget, context)) {
			CacheResponseStatus responseStatus = context.getCacheResponseStatus();
			switch (responseStatus) {
			case CACHE_HIT:
				System.out.println("A response was generated from the cache with " + "no requests sent upstream");
				break;
			case CACHE_MODULE_RESPONSE:
				System.out.println("The response was generated directly by the " + "caching module");
				break;
			case CACHE_MISS:
				System.out.println("The response came from an upstream server");
				break;
			case VALIDATED:
				System.out.println("The response was generated from the cache " + "after validating the entry with the origin server");
				break;
			}
			return this.des.deserializeOne(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
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
		Optional<Prototype> prot = kb.isDefined(res);
		long time = w.elapsed(TimeUnit.MILLISECONDS);
		System.out.println(prot.isPresent());
		return time;
	}

}