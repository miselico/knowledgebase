package miselico.prototypes.serializers.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.Prototypes;
import miselico.prototypes.knowledgebase.Prototypes.Builder;
import miselico.prototypes.serializers.Deserializer;
import miselico.prototypes.serializers.ParseException;

public class SimpleDeserializer implements Deserializer {
	private static final Splitter SPACESPLITTER = Splitter.on(' ').omitEmptyStrings().trimResults();

	@Override
	public Prototype deserializeOne(Reader reader) throws IOException, ParseException {

		try (BufferedReader r = new BufferedReader(reader)) {

			ArrayList<String> prototypeLines = new ArrayList<>();
			String line = r.readLine();
			while ((line != null) && !CharMatcher.WHITESPACE.matchesAllOf(line)) {
				prototypeLines.add(line);
				line = r.readLine();
			}
			// lines for one prototype are collected.
			Prototype p = this.deserializeOnePrototype(prototypeLines);
			// very strict that there is no extra stuff in the reader
			while ((line != null)) {
				if (!CharMatcher.WHITESPACE.matchesAllOf(line)) {
					throw new ParseException(line, "came after the prototype definition");
				}
			}
			return p;
		}
	}

	@Override
	public List<Prototype> deserialize(Reader reader) throws IOException, ParseException {
		List<Prototype> ps = new ArrayList<>();

		try (BufferedReader r = new BufferedReader(reader)) {
			while (true) {

				ArrayList<String> prototypeLines = new ArrayList<>();
				String line = r.readLine();
				if (line == null) {
					return ps;
				}
				while (CharMatcher.WHITESPACE.matchesAllOf(line)) {
					line = r.readLine();
					if (line == null) {
						return ps;
					}
				}
				while ((line != null) && !CharMatcher.WHITESPACE.matchesAllOf(line)) {
					prototypeLines.add(line);
					line = r.readLine();
				}
				// lines for one prototype are collected.
				Prototype p = this.deserializeOnePrototype(prototypeLines);
				ps.add(p);
			}
		}
	}

	private static ID deserializeIDLine(String line) {
		return ID.of(line);
	}

	private static ID deserializeBaseLine(String baseLine) throws ParseException {
		List<String> parts = SimpleDeserializer.SPACESPLITTER.splitToList(baseLine);
		if (!parts.get(0).equals("base")) {
			throw new ParseException(baseLine, " is not a propper base line");
		}
		return ID.of(parts.get(1));
	}

	private Prototype deserializeOnePrototype(ArrayList<String> lines) throws ParseException {

		Builder builder = Prototypes.builder(SimpleDeserializer.deserializeBaseLine(lines.get(1)));
		int index = 2;
		// read removes
		while ((index < lines.size()) && lines.get(index).startsWith("rem ")) {
			String line = lines.get(index);
			List<String> remparts = SimpleDeserializer.SPACESPLITTER.splitToList(line);
			if (remparts.size() < 3) {
				throw new ParseException(line, "is not a proper remove line");
			}
			Property prop = Property.of(remparts.get(1));
			if (remparts.get(2).equals("*")) {
				// removeAll
				if (remparts.size() != 3) {
					throw new ParseException(line, "is not a proper removeAll line");
				}
				builder.removeAll(prop);
			} else {
				for (int i = 2; i < remparts.size(); i++) {
					builder.remove(prop, ID.of(remparts.get(i)));
				}
			}
			index++;
		}
		// read adds
		while ((index < lines.size()) && lines.get(index).startsWith("add ")) {
			String line = lines.get(index);
			List<String> addparts = SimpleDeserializer.SPACESPLITTER.splitToList(line);
			if (addparts.size() < 3) {
				throw new ParseException(line, "is not a proper add line");
			}
			Property prop = Property.of(addparts.get(1));
			for (int i = 2; i < addparts.size(); i++) {
				builder.add(prop, ID.of(addparts.get(i)));
			}
			index++;
		}

		if (index != lines.size()) {
			throw new ParseException(lines.get(index), "is not consumed");
		}

		return builder.build(SimpleDeserializer.deserializeIDLine(lines.get(0)));
	}
}
