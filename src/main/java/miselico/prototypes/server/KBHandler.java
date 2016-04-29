package miselico.prototypes.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.i18n.iri.IRISyntaxException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.IFPKnowledgeBase;
import miselico.prototypes.knowledgebase.IKnowledgeBase;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.serializers.Serializer;
import miselico.prototypes.serializers.json.JSONSerializer;

/**
 * A handler for a Jetty {@link Server} serving a knowledge base. This handler
 * sets the Cache-Control HTTP header on the response if the supplied function
 * provides a value greater than 0 for the given ID. Further, this handler
 * manages a cache of ETags to prototypes and will not send the representation
 * if it the ETag matches and the respective Prototype has not changed.
 * 
 * @author michael
 *
 */
public class KBHandler extends AbstractHandler {

	private final IKnowledgeBase kb;
	private final Function<ID, Long> timeoutF;
	private final Multimap<ID, URI> seeAlsoMap;
	private final Function<ID, Prototype> fpcomp;
	private final Serializer ser;

	/**
	 * Same as {@link KBHandler#KBHandler(IKnowledgeBase, Function, Multimap)}
	 * but with an empty seeAlsoMap
	 * 
	 * @param kb
	 * @param timeoutF
	 */
	public KBHandler(IKnowledgeBase kb, Function<ID, Long> timeoutF) {
		this(kb, timeoutF, ImmutableMultimap.of());
	}

	/**
	 * Same as
	 * {@link KBHandler#KBHandler(IKnowledgeBase, Function, Multimap, Serializer)}
	 * with serializer set to {@link JSONSerializer}
	 * 
	 * @param kb
	 * @param timeoutF
	 * @param seeAlsoMap
	 */
	public KBHandler(IKnowledgeBase kb, Function<ID, Long> timeoutF, Multimap<ID, URI> seeAlsoMap) {
		this(kb, timeoutF, seeAlsoMap, JSONSerializer.create());
	}

	/**
	 * Create a handler for the given {@link IKnowledgeBase}. The timout
	 * Function specifies for each prototype how long a client can cache it. The
	 * value can change over time and is computed at the moment when the
	 * prototype is served. The seeAlsoMap contains URLs which will be use din
	 * the Link header using the "alternate" relation type. Also this map is
	 * allowed to change after construction. The {@link Serializer} is used for
	 * serializing the {@link Prototype}s.
	 * 
	 * @param kb
	 * @param timeoutF
	 * @param seeAlsoMap
	 */
	public KBHandler(IKnowledgeBase kb, Function<ID, Long> timeoutF, Multimap<ID, URI> seeAlsoMap, Serializer ser) {
		this.kb = kb;
		this.timeoutF = timeoutF;
		this.seeAlsoMap = seeAlsoMap;
		if (kb instanceof IFPKnowledgeBase) {
			this.fpcomp = id -> ((IFPKnowledgeBase) kb).computeFixPoint(id);
		} else {
			this.fpcomp = null;
		}
		this.ser = ser;
	}

	/**
	 * Cache used to map the ETags provided to clients to Prototypes as they
	 * were when the ETag was given out.
	 */
	private final Cache<String, Prototype> ETagCache = CacheBuilder.newBuilder().maximumSize(10000).build();

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// only GET requests are handled.
		if (!request.getMethod().equals("GET")) {
			return;
		}
		String[] prototypeIDs = request.getParameterValues("p");
		if (prototypeIDs == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing p parameter indicating the prototype requested");
			return;
		}
		// there is at least one prototypeID
		List<ID> IDs = new ArrayList<>();
		for (String protoypeID : prototypeIDs) {
			ID id;
			try {
				id = ID.of(protoypeID);
			} catch (IRISyntaxException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "parameter p with value '" + protoypeID + "' does not have an IRI value");
				return;
			}
			IDs.add(id);
		}

		List<Prototype> prototypes = new ArrayList<>();
		if ((this.fpcomp == null) || (request.getParameter("fp") == null) || !request.getParameter("fp").equals("true")) {
			// no fixpoints
			long minTimeout = Long.MAX_VALUE;
			for (ID id : IDs) {
				Optional<? extends Prototype> optPrototype = this.kb.isDefined(id);
				if (!optPrototype.isPresent()) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				prototypes.add(optPrototype.get());
				long timeout = this.timeoutF.apply(id);
				if (minTimeout > timeout) {
					minTimeout = timeout;
				}
			}
			// set cache time out
			if (minTimeout > 0) {
				response.setHeader("Cache-Control", "public, max-age=" + minTimeout);
			}
		} else {
			// get fixpoints.
			for (ID id : IDs) {
				Optional<? extends Prototype> optPrototype = this.kb.isDefined(id);
				if (!optPrototype.isPresent()) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				prototypes.add(this.fpcomp.apply(id));
			}
		}
		// serialize the prototypes to the stream.
		try (OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
			if (prototypes.size() == 1) {
				// If there is only one prototype requested, the ETag and Link
				// header are used.
				Prototype prototype = prototypes.get(0);
				// set see also https://tools.ietf.org/html/rfc5988
				Collection<URI> seeAlso = this.seeAlsoMap.get(prototype.id);
				for (URI uri : seeAlso) {
					response.addHeader("Link", "<" + uri.toString() + ">;rel=alternate");
				}
				// try etag
				String etag = request.getHeader("If-None-Match");
				if (etag != null) {
					Prototype cachedPrototype = this.ETagCache.getIfPresent(etag);
					if (prototype.equals(cachedPrototype)) {
						// return 304 : not modified
						response.setHeader("ETag", etag);
						response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
						baseRequest.setHandled(true);
						return;
					}
				}
				// ETag did not work out
				// set new ETag
				String newEtag = UUID.randomUUID().toString();
				this.ETagCache.put(newEtag, prototype);
				response.setHeader("ETag", newEtag.toString());

				this.ser.serializeOne(prototype, out);
			} else {
				this.ser.serialize(prototypes, out);
			}
			out.flush();
		}
	}
}
