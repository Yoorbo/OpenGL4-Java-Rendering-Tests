package components;

public enum EShaderAttribute {
	POSITION("inPosition"), COLOR("inColor");

	private final String attributeName;

	private EShaderAttribute(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return shader attribute name as it is appearing in the shader source
	 *         code.
	 */
	public String getAttributeName() {
		return attributeName;
	}
}
