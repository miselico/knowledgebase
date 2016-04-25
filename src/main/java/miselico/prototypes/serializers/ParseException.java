package miselico.prototypes.serializers;

public class ParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParseException(String line, String problem) {
		super("The line : '" + line + "' " + problem);
	}

}
