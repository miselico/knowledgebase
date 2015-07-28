package miselico.prototypes.knowledgebase;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Ints;

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

	public Optional<Prototype> isDefined(ID id) {
		if (Prototype.P_0.id.equals(id)) {
			return Optional.of(Prototype.P_0);
		}
		Optional<Prototype> asInt = INTEGERS.kb.isDefined(id);
		if (asInt.isPresent()) {
			return asInt;
		}
		Optional<Prototype> asString = STRINGS.kb.isDefined(id);
		if (asString.isPresent()) {
			return asString;
		}
		return Optional.absent();
	}

	public Prototype get(ID id) {
		return this.isDefined(id).get();
	}

	public static Prototype get(String value) {
		return PredefinedKB.STRINGS.kb.define(value);
	}

	public static Prototype get(int i) {
		return PredefinedKB.INTEGERS.kb.define(i);
	}

	public static class INTEGERS implements IKnowledgeBase {
		private static final PredefinedKBPart<Integer> kb = new PredefinedKBPart<Integer>() {

			@Override
			protected String getBaseString() {
				return "http://example.com/integer/";
			}

			@Override
			protected String toURLFragment(Integer val) {
				return Integer.toString(val);
			}

			@Override
			protected Optional<Integer> fromURLFragment(String fragment) {
				Integer str = Ints.tryParse(fragment);
				return Optional.fromNullable(str);
			}

		};

		public Optional<Prototype> isDefined(ID id) {
			return INTEGERS.kb.isDefined(id);
		}

		public Prototype get(ID id) {
			return INTEGERS.kb.get(id);
		}

	};

	public static class STRINGS implements IKnowledgeBase {
		private static final PredefinedKBPart<String> kb = new PredefinedKBPart<String>() {

			@Override
			protected String getBaseString() {
				return "http://example.com/string/";
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

		public Optional<Prototype> isDefined(ID id) {
			return STRINGS.kb.isDefined(id);
		}

		public Prototype get(ID id) {
			return STRINGS.kb.get(id);
		}

	};

	private abstract static class PredefinedKBPart<E> {

		protected abstract String getBaseString();

		private final String baseString = this.getBaseString();
		private final URI base = URI.create(this.baseString);

		protected abstract String toURLFragment(E val);

		protected abstract Optional<E> fromURLFragment(String fragment);

		private LoadingCache<E, Prototype> cache = CacheBuilder.newBuilder().build(new CacheLoader<E, Prototype>() {

			@Override
			public Prototype load(E value) throws Exception {
				String fragment = PredefinedKBPart.this.toURLFragment(value);
				return Prototype.create(new ID(PredefinedKBPart.this.base.resolve(fragment)), Prototype.P_0, RemoveChangeSet.empty(), AddChangeSet.empty());
			}
		});

		public Prototype define(E value) {
			return this.cache.getUnchecked(value);
		}

		private final Splitter s = Splitter.on(this.baseString).omitEmptyStrings();

		public Prototype get(ID id) {
			Optional<Prototype> v = this.isDefined(id);
			return v.get();
		}

		public Optional<Prototype> isDefined(ID id) {
			String val = id.value.toString();
			if (!val.startsWith(this.base.toString())) {
				return Optional.absent();
			}
			List<String> parts = this.s.splitToList(val);
			if (!(parts.size() == 1)) {
				System.err.println("prefix " + this.baseString + " used, but incorect URI to be of this type, likely an error" + id);
				return Optional.absent();
			}
			Optional<E> parsedVal = this.fromURLFragment(parts.get(0));
			if (!parsedVal.isPresent()) {
				System.err.println("prefix " + this.baseString + " used, but incorect URI to be of this type, likely an error : " + id);
				return Optional.absent();
			}
			return Optional.of(this.define(parsedVal.get()));
		}
	}

	// public static final ImmutableList<Prototype> integers;
	//
	// public static final int MAX_INT = 512;
	//
	// static {
	// ImmutableList.Builder<Prototype> b = ImmutableList.builder();
	// for (int i = 0; i < PredefinedKB.MAX_INT; i++) {
	// b.add(Prototype.create(new ID(URI.create("http://example.com/integer/" +
	// i)), Prototype.P_0.id, ChangeSet.empty(), ChangeSet.empty()));
	// }
	// integers = b.build();
	// }

	// private static final ImmutableMap<ID, Prototype> KB;

	// public static boolean isDefined(ID id) {
	// return PredefinedKB.KB.containsKey(id);
	// }

	// static {
	// ImmutableMap.Builder<ID, Prototype> b = ImmutableMap.builder();
	// // empty
	// b.put(Prototype.P_0.id, Prototype.P_0);
	// // integers
	// for (Prototype prototype : PredefinedKB.integers) {
	// b.put(prototype.id, prototype);
	// }
	// KB = b.build();
	// }
}
