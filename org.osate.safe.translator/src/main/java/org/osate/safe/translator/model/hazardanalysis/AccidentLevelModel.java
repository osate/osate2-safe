package org.osate.safe.translator.model.hazardanalysis;

import org.osate.safe.translator.model.SystemModel;

public class AccidentLevelModel extends StpaPreliminaryModel {
	protected int number;
	private SystemModel system;
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}

	public SystemModel getSystem() {
		return system;
	}

	public void setSystem(SystemModel system) {
		this.system = system;
	}
}
