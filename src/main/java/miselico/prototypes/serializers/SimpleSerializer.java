package miselico.prototypes.serializers;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import miselico.prototypes.knowledgebase.ID;
import miselico.prototypes.knowledgebase.Property;
import miselico.prototypes.knowledgebase.Prototype;
import miselico.prototypes.knowledgebase.PrototypeDefinition;

public class SimpleSerializer {

	public void serialize(Iterator<Prototype> ps, Writer w) throws IOException {
		String separator = "";
		while (ps.hasNext()) {
			w.write(separator);
			this.serialize(ps.next(), w);
			separator = "\n";
		}

	}

	public void serialize(Prototype p, Writer w) throws IOException {
		// ID
		w.write(p.id.toString());
		w.write('\n');
		PrototypeDefinition def = p.def;
		// base
		w.write("base ");
		w.write(def.parent.toString());
		w.write('\n');
		// removeAll
		for (Property remA : def.remove.getRemoveAll()) {
			w.write("rem ");
			w.write(remA.toString());
			w.write(" * \n");
		}
		// remove
		for (Entry<Property, Collection<ID>> rem : def.remove.entrySet()) {
			w.write("rem ");
			w.write(rem.getKey().toString());
			w.write(" ");
			SimpleSerializer.serializeIDCollection(rem.getValue(), w);
			w.write('\n');
		}
		// add
		for (Entry<Property, Collection<ID>> add : def.add.entrySet()) {
			w.write("add ");
			w.write(add.getKey().toString());
			w.write(" ");
			SimpleSerializer.serializeIDCollection(add.getValue(), w);
			w.write('\n');
		}
	}

	private static void serializeIDCollection(Collection<ID> c, Writer w) throws IOException {
		String separator = "";
		for (ID id : c) {
			w.write(separator);
			w.write(id.toString());
			separator = " ";
		}
	}

}
