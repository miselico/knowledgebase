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
 * This class can be extended to contain other literals as well. To do this, one
 * will also want to extend the {@link PredefinedKBPart} class.
 * 
 * @author michael
 *
 */
public class PredefinedKB implements IKnowledgeBase {

	/**
	 * The only instance of the bare {@link PredefinedKB}. There is no strict
	 * need for a singleton beyond the fact that all {@link PredefinedKB}s would
	 * be exactly the same and immutable. Classes extending from this class can
	 * make their own singletons.
	 */
	public static final PredefinedKB kb = new PredefinedKB();

	/**
	 * Protected constructor for derived types. Users of {@link PredefinedKB}
	 * should use the singleton directly.
	 */
	protected PredefinedKB() {
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

	/**
	 * Get the {@link Prototype} corresponding to the given string value.
	 * 
	 * @param value
	 * @return
	 */
	public static Prototype get(String value) {
		return strings.kb.define(value);
	}

	/**
	 * Get the {@link Prototype} corresponding to the given long value.
	 * 
	 * @param i
	 * @return
	 */
	public static Prototype get(long i) {
		return integers.kb.define(i);
	}

	/**
	 * Sinleton of a knowledge base only containing the integers.
	 */
	public static final integers INTEGERS = new integers();

	/**
	 * A knowledge base only containing the integers.
	 */
	public static final class integers implements IKnowledgeBase {
		private integers() {
		}

		private static final PredefinedKBPart<Long> kb = new PredefinedKBPart<Long>() {

			@Override
			protected String getBaseString() {
				return "value:integer#";
			}

			@Override
			protected String toIRIFragment(Long val) {
				return Long.toString(val);
			}

			@Override
			protected Optional<Long> fromIRIFragment(String fragment) {
				Long str = Longs.tryParse(fragment);
				return Optional.ofNullable(str);
			}

		};

		@Override
		public Optional<Prototype> isDefined(ID id) {
			return integers.kb.isDefined(id);
		}

		/**
		 * A method to convert the ID of a prototype produced by this class back
		 * to a long.
		 * 
		 * @param id
		 * @return
		 */
		public Optional<Long> convertBack(ID id) {
			return integers.kb.convertBack(id);
		}

	};

	/**
	 * Sinleton of a knowledge base only containing the strings.
	 */
	public static final strings STRINGS = new strings();

	/**
	 * A knowledge base only containing the strings.
	 */
	public static final class strings implements IKnowledgeBase {
		private strings() {
			// TODO Auto-generated constructor stub
		}

		private static final PredefinedKBPart<String> kb = new PredefinedKBPart<String>() {

			@Override
			protected String getBaseString() {
				return "value:string#";
			}

			@Override
			protected String toIRIFragment(String val) {
				try {
					return URLEncoder.encode(val, StandardCharsets.UTF_8.name());
				} catch (UnsupportedEncodingException e) {
					throw new Error("Built-in charset utf-8 must be supported.");
				}
			}

			@Override
			protected Optional<String> fromIRIFragment(String fragment) {
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

		/**
		 * Convert the {@link ID} of a prototype created using this
		 * {@link KnowledgeBase} back to a string.
		 * 
		 * @param id
		 * @return
		 */
		public Optional<String> convertBack(ID id) {
			return strings.kb.convertBack(id);
		}

	};

	/**
	 * The prototype definition for all value types. It derives from P_0 and
	 * does not add or remove anything.
	 */
	private static final PrototypeDefinition def = PrototypeDefinition.create(Prototype.P_0, RemoveChangeSet.empty(), AddChangeSet.empty());

	/**
	 * A class representing part of a knowledge base. The part is responsible
	 * for one specific datatype like String, long, date, ...
	 * 
	 * @author michael
	 *
	 * @param <E>
	 *            The type this part is responsible for
	 */
	protected abstract static class PredefinedKBPart<E> {

		/**
		 * The base IRI of the datatypes in string format. This has to be unique
		 * for the given datatype and combined with the IRI fragment (
		 * {@link PredefinedKBPart#toIRIFragment(Object)}) it must be a valid
		 * IRI
		 * 
		 * @return the base IRI
		 */
		protected abstract String getBaseString();

		private final String baseString = this.getBaseString();

		/**
		 * convert the val to a fragment which together with the baseString form
		 * a valid IRI. The mapping from val to String here and back (provided
		 * by {@link PredefinedKBPart#fromIRIFragment(String)}) must be a
		 * bijection, ie. a one-to-one mapping.
		 * 
		 * @param val
		 * @return the value as a IRI fragment.
		 */
		protected abstract String toIRIFragment(E val);

		/**
		 * Convert the given fragment back to a value. This is the reverse
		 * operation of {@link PredefinedKBPart#toIRIFragment(Object)}.
		 * 
		 * @param fragment
		 * @return
		 */
		protected abstract Optional<E> fromIRIFragment(String fragment);

		private final LoadingCache<E, Prototype> cache = CacheBuilder.newBuilder().build(new CacheLoader<E, Prototype>() {

			@Override
			public Prototype load(E value) throws Exception {
				String fragment = PredefinedKBPart.this.toIRIFragment(value);
				ID id = ID.of(PredefinedKBPart.this.baseString + fragment);
				return new Prototype(id, PredefinedKB.def);
			}
		});

		/**
		 * Get a prototype for the given value.
		 * 
		 * @param value
		 * @return
		 */
		public Prototype define(E value) {
			return this.cache.getUnchecked(value);
		}

		private final Splitter s = Splitter.on(this.baseString).omitEmptyStrings();

		/**
		 * Get the Optional prototype for the given ID.
		 * 
		 * @param id
		 * @return
		 * @see {@link IKnowledgeBase#isDefined(ID)}
		 */
		public Optional<Prototype> isDefined(ID id) {
			Optional<E> parsedVal = this.convertBack(id);
			if (!parsedVal.isPresent()) {
				return Optional.empty();
			}
			return Optional.of(this.define(parsedVal.get()));
		}

		/**
		 * Convert the id back to a value if it was an id created with (or
		 * equivalent to created with) this {@link PredefinedKBPart}.
		 * 
		 * @param id
		 * @return
		 */
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
			Optional<E> parsedVal = this.fromIRIFragment(parts.get(0));
			if (!parsedVal.isPresent()) {
				System.err.println("prefix " + this.baseString + " used, but incorect URI to be of this type, likely an error : " + id);
			}
			return parsedVal;
		}

	}

}
