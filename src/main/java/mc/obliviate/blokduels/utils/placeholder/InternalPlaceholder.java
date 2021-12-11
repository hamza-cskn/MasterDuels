package mc.obliviate.blokduels.utils.placeholder;

public class InternalPlaceholder {

	private final String placeholder;
	private final String value;

	protected InternalPlaceholder(final String placeholder, final String value) {
		this.placeholder = placeholder;
		this.value = value;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public String getValue() {
		return value;
	}
}