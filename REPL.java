package mide;

import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;


class MIDEObject {
	MIDEObject(char type, Object name, Object value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
	char type;
	Object name;
	Object value;
}

public class REPL extends HashMap<Object, MIDEObject> {
	private REPL parent = null;
	public static void main(String [] args) {
		Scanner in = new Scanner(System.in); 
		REPL r = new REPL(in, null, "");
	}

	public Object parseValue(Object object) {
		try {
			object = Long.parseLong(object.toString());
		} catch (Exception e) {
			System.err.println("Not a long");
		}
		if (object instanceof String) {
			try {
				object = Double.parseDouble(object.toString());
			} catch (Exception e) {
				System.err.println("Not a double");
			}
		}
		return object;
	}

	public REPL(Scanner in, REPL parent, String map) {
		this.parent = parent;
		char type = '\0';
		while (type != 'q') {
			down(this, 0);
			System.out.println("\nSelect a type to operate on for map "+map+":");
			System.out.println("Entry (e), Map (m), Quit(q)");
			String line = in.nextLine();
			type = line.charAt(0);
			if (type == 'q') {
				break;
			}
			MIDEObject mo = null;
			Object name = null;
			Object value = null;
			switch (type) {
			case 'e':
				System.out.println("Enter variable name or index of type "+type+":");
				name = in.nextLine().intern();
				name = parseValue(name);
				System.out.println("Enter a value to assign to the variable "+name);
				value = in.nextLine();
				value = parseValue(value);
				mo = new MIDEObject(type, name, value);
				this.put(name, mo);
				break;
			case 'm':
				System.out.println("Enter variable name or index of type "+type+":");
				name = in.nextLine().intern();
				name = parseValue(name);
				value = new REPL(in, this, name.toString());
				mo = new MIDEObject(type, name, value);
				this.put(name, mo);
				break;
			}
		}
		export(this);
	}
	public void export(REPL c) {
		while (c.parent != null) {
			c = c.parent;
		}
		down(c, 0);
	}
	public void down(REPL c, int indent) {
		boolean first = true;
		for (Map.Entry<Object, MIDEObject> entry : c.entrySet()) {
			if (first) {
				first = false;
			} else {
				System.out.println(",");
			}
			MIDEObject m = entry.getValue();
			Object name = m.name;
			if (name instanceof String) {
				name = "\""+name+"\"";
			}
			System.out.print(" ".repeat(indent*2)+m.type+" "+name+" : ");
			if (m.value instanceof REPL) {
				System.out.println(" ".repeat(indent*2)+"{");
				down((REPL)m.value, indent+1);
				System.out.println(" ".repeat(indent*2)+"}\n");
			} else {
				Object value = m.value;
				if (value instanceof String) {
					value = "\""+value+"\"";
				}
				System.out.print(value);
			}
		}
	}
}
