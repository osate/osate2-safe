package org.osate.safe.translator.model.hazardanalysis;

public class HazardModel extends StpaPreliminaryModel {
	
	private String systemElement;
	private String environmentElement;

	public String getSystemElement() {
		return systemElement;
	}

	public void setSystemElement(String systemElement) {
		this.systemElement = systemElement;
	}

	public String getEnvironmentElement() {
		return environmentElement;
	}

	public void setEnvironmentElement(String environmentElement) {
		this.environmentElement = environmentElement;
	}

}
