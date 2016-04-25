package miselico.prototypes.knowledgebase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Longs;

/**
 * All predefined values, which are implicitly in all knowledge bases. This
 * includes all literals like integers and strings. And also the empty
 * prototype.
 * 
 * @author michael
 *
 */
public class PredefinedKB implements IKnowledgeBase {

	public static final PredefinedKB kb = new PredefinedKB();

	private PredefinedKB() {
	}

	@Override
	public Optional<Prototype> isDefined(ID id) {
		if (Prototype.P_0.id.equals(id)) {
			return Optional.of(Prototype.P_0);
		}
		Optional<Prototype> asInt = integers.kb.isDefined(id);
		if (asInt.isPresent()) {
			return asInt;
		}
		Optional<Prototype> asString = strings.kb.isDefined(id);
		if (asString.isPresent()) {
			return asString;
		}
		return Optional.empty();
	}

	public static Prototype get(String value) {
		return strings.kb.define(value);
	}

	public static Prototype get(long i) {
		return integers.kb.define(i);
	}

	public static final integers INTEGERS = new integers();

	public static class integers implements IKnowledgeBase {
		private integers() {
		}

		private static final PredefinedKBPart<Long> kb = new PredefinedKBPart<Long>() {

			@Override
			protected String getBaseString() {
				return "value:integer#";
			}

			@Override
			protected String toURLFragment(Long val) {
				return Long.toString(val);
			}

			@Override
			protected Optional<Long> fromURLFragment(String fragment) {
				Long str = Longs.tryParse(fragment);
				return Optional.ofNullable(str);
			}

		};

		@Override
		public Optional<Prototype> isDefined(ID id) {
			return integers.kb.isDefined(id);
		}

		public Optional<Long> convertBack(ID id) {
			return integers.kb.convertBack(id);
		}

	};

	public static final strings STRINGS = new strings();

	public static class strings implements IKnowledgeBase {
		private strings() {
			// TODO Auto-generated constructor stub
		}

		private static final PredefinedKBPart<String> kb = new PredefinedKBPart<String>() {

			@Override
			protected String getBaseString() {
				return "value:string#";
			}

			@Override
			protected String toURLFragment(String val) {
				try {
					return URLEncoder.encode(val, StandardCharsets.UTF_8.name());
				} catch (UnsupportedEncodingException e) {
					throw new Error("Built-in charset utf-8 must be supported.");
				}
			}

			@Override
			protected Optional<String> fromURLFragment(String fragment) {
				try {
					String val = URLDecoder.decode(fragment, StandardCharsets.UTF_8.name());
					return Optional.of(val);
				} catch (UnsupportedEncodingException e) {
					throw new Error("Built-in charset utf-8 must be supported.");
				}
			}
		};

		@Override
		public Optional<Prototype> isDefined(ID id) {
			return strings.kb.isDefined(id);
		}

		public Optional<String> convertBack(ID id) {
			return strings.kb.convertBack(id);
		}

	};

	private static final PrototypeDefinition def = PrototypeDefinition.create(Prototype.P_0, RemoveChangeSet.empty(), AddChangeSet.empty());

	private abstract static class PredefinedKBPart<E> {

		protected abstract String getBaseString();

		private final String baseString = this.getBaseString();

		// private final URI base = URI.create(this.baseString);

		protected abstract String toURLFragment(E val);

		protected abstract Optional<E> fromURLFragment(String fragment);

		private final LoadingCache<E, Prototype> cache = CacheBuilder.newBuilder().build(new CacheLoader<E, Prototype>() {

			@Override
			public Prototype load(E value) throws Exception {
				String fragment = PredefinedKBPart.this.toURLFragment(value);
				ID id = ID.of(PredefinedKBPart.this.baseString + fragment);
				return new Prototype(id, PredefinedKB.def);
			}
		});

		public Prototype define(E value) {
			return this.cache.getUnchecked(value);
		}

		private final Splitter s = Splitter.on(this.baseString).omitEmptyStrings();

		public Optional<Prototype> isDefined(ID id) {
			Optional<E> parsedVal = this.convertBack(id);
			if (!parsedVal.isPresent()) {
				return Optional.empty();
			}
			return Optional.of(this.define(parsedVal.get()));
		}

		public Optional<E> convertBack(ID id) {
			String val = id.toString();
			if (!val.startsWith(this.baseString)) {
				return Optional.empty();
			}
			List<String> parts = this.s.splitToList(val);
			if (!(parts.size() == 1)) {
				System.err.println("prefix " + this.baseString + " used, but incorect URI to be of this type, likely an error" + id);
				return Optional.empty();
			}
			Optional<E> parsedVal = this.fromURLFragment(parts.get(0));
			if (!parsedVal.isPresent()) {
				System.err.println("prefix " + this.baseString + " used, but incorect URI to be of this type, likely an error : " + id);
			}
			return parsedVal;
		}

	}

}
