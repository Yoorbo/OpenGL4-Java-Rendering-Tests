package components;

public abstract class BaseComponent {
	private boolean m_isActive = true;
	private long m_start;
	
	// Constructor
	
	public BaseComponent() {
        System.out.println(this.getClass().getSimpleName() + " initialized");
        setStart(System.currentTimeMillis());
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

	/**
	 * @return the start
	 */
	public long getStart() {
		return m_start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(long start) {
		this.m_start = start;
	}
	
}
	
	
