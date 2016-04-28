package miselico.prototypes.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import miselico.prototypes.client.LimitedLinkHeaderParser;

public class LimitedLinkHeaderParserTest {

	private LimitedLinkHeaderParser parser;

	@Before
	public void setup() {
		this.parser = new LimitedLinkHeaderParser();
	}

	@Test
	public void testIgnoredSecondRel() throws URISyntaxException {
		List<URI> urls = this.parser.parse("<http://example.com/TheBook/chapter2>; rel=\"previous\"; title=\"previous chapter\"; rel=\"alternate\"");
		List<URI> expected = Lists.newArrayList();
		Assert.assertEquals("MUST NOT report beyond the first rel of the given link-value", expected, urls);
	}

	@Test
	public void testQuoted() throws URISyntaxException {
		List<URI> urls = this.parser.parse("<http://example.com/TheBook/chapter2>; title=\"previous chapter\"; rel=\"alternate\"");
		List<URI> expected = Lists.newArrayList(new URI("http://example.com/TheBook/chapter2"));
		Assert.assertEquals("Could not find alternate between quotes", expected, urls);
	}

	@Test
	public void testQuotedMultiple() throws URISyntaxException {
		List<URI> urls = this.parser.parse("<http://example.com/TheBook/chapter2>; title=\"previous chapter\"; rel=\"alternate previous\"");
		List<URI> expected = Lists.newArrayList(new URI("http://example.com/TheBook/chapter2"));
		Assert.assertEquals("Could not find multiple alternate between quotes", expected, urls);
	}

	@Test
	public void testQuotedMultipleExt() throws URISyntaxException {
		List<URI> urls = this.parser.parse("<http://example.com/TheBook/chapter2>; title=\"previous chapter\"; rel=\"alternate http://example.net/foo\"");
		List<URI> expected = Lists.newArrayList(new URI("http://example.com/TheBook/chapter2"));
		Assert.assertEquals("Could not find multiple alternate in separate link-values", expected, urls);
	}

	@Test
	public void testQuotedMultipleExtWithEmpty() throws URISyntaxException {
		List<URI> urls = this.parser.parse("<http://example.com/TheBook/chapter1>; title=\"previous chapter\"; rel=\"alternate http://example.net/foo\" , , " + "</TheBook/chapter2>;rel=\"alternate\"");
		List<URI> expected = Lists.newArrayList(new URI("http://example.com/TheBook/chapter1"), new URI("/TheBook/chapter2"));
		Assert.assertEquals("Could not find multiple alternate in separate link-values inclusing empty ones", expected, urls);
	}

	@Test
	public void testMultipleURI() throws URISyntaxException {
		List<URI> urls = this.parser.parse("</TheBook/chapter2>;rel=\"previous\"; title*=UTF-8'de'letztes%20Kapitel,</TheBook/chapter4>;rel=\"next alternate\"; title*=UTF-8'de'n%c3%a4chstes%20Kapitel");
		List<URI> expected = Lists.newArrayList(new URI("/TheBook/chapter4"));
		Assert.assertEquals("Could not find alternate between quotes", expected, urls);
	}

	@After
	public void tearDown() {
		this.parser = null;
	}

}
