package miselico.prototypes.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * This parser is tailored to our specific needs, namely finding the alternate
 * links from the HTTP Link header value.
 * 
 * @author michael
 * @see <a href=https://tools.ietf.org/html/rfc5988>https://tools.ietf.org/html/
 *      rfc5988</a>
 */
public class LimitedLinkHeaderParser {

	/**
	 * Returns an immutable list of URIs which have the rel=alternate relation.
	 * 
	 * @param headerValue
	 *            the Value of the header.
	 * @return The list of URIs
	 * @throws URISyntaxException
	 */
	public List<URI> parse(String headerValue) throws URISyntaxException {
		Preconditions.checkNotNull(headerValue);
		// A quick check to save on parsing
		if (!headerValue.contains("alternate")) {
			return ImmutableList.of();
		}
		List<URI> relAlt = new ArrayList<>();
		// the variable names in this code reflect the naming used in the BNF in
		// rfc5988
		Iterable<String> link_values = LimitedLinkHeaderParser.commaSplitter.split(headerValue);
		link_value: for (String link_value : link_values) {
			if (!link_value.contains("alternate")) {
				continue;
			}
			List<String> parts = LimitedLinkHeaderParser.closeAngularSplitter.splitToList(link_value);
			if (parts.size() != 2) {
				throw new Error();
			}
			if (!parts.get(0).startsWith("<")) {
				throw new Error();
			}
			URI url = new URI(parts.get(0).substring(1));
			Iterable<String> link_params = LimitedLinkHeaderParser.linkParamListSplitter.split(parts.get(1));
			for (String link_param : link_params) {
				parts = LimitedLinkHeaderParser.linkParamSplitter.splitToList(link_param);
				if (parts.size() != 2) {
					throw new Error();
				}
				if (parts.get(0).equals("rel")) {
					String relation_types = parts.get(1);
					if (relation_types.equals("alternate") || relation_types.equals("\"alternate\"")) {
						relAlt.add(url);
					} else {
						// it might be a list of multiple relation types split
						// by space, but then it must be quoted
						if (relation_types.startsWith("\"") && relation_types.endsWith("\"")) {
							parts = LimitedLinkHeaderParser.relationTypesSplitter.splitToList(relation_types.substring(1, relation_types.length() - 1));
							if (parts.contains("alternate")) {
								relAlt.add(url);
							}
						}
					}
					// https://tools.ietf.org/html/rfc5988#section-5.3
					// The relation type of a link is conveyed in the "rel"
					// parameter's value. The "rel" parameter MUST NOT appear
					// more than once in a given link-value; occurrences after
					// the first MUST be ignored by parsers.
					continue link_value;
				}
			}
		}
		return relAlt;
	}

	private static final Splitter closeAngularSplitter = Splitter.on('>').limit(2);
	private static final Splitter commaSplitter = Splitter.on(',').trimResults().omitEmptyStrings();
	private static final Splitter linkParamListSplitter = Splitter.on(';').trimResults().omitEmptyStrings();
	private static final Splitter linkParamSplitter = Splitter.on('=').trimResults();
	private static final Splitter relationTypesSplitter = Splitter.on(' ');

}
