package components;

public abstract class BaseComponent {
	private boolean m_isActive = true;
	
	// Constructor
	
	public BaseComponent() {
        System.out.println(this.getClass().getSimpleName() + " initialized");
        init();
    }
	
	// Functions
	
	private void init() {}
	
	// Base Getters & Setters

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return m_isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.m_isActive = isActive;
	}
	
}
	
	
