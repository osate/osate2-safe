package org.osate.safe.translator.model.hazardanalysis;

public class ErrorFlowModel {
	private PropagationModel in;
	private PropagationModel out;
	
	public PropagationModel getIn() {
		return in;
	}
	
	public void setIn(PropagationModel in) {
		this.in = in;
	}
	
	public PropagationModel getOut() {
		return out;
	}
	
	public void setOut(PropagationModel out) {
		this.out = out;
	}
}
