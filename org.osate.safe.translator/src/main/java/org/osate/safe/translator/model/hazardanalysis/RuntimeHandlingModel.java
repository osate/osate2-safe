package org.osate.safe.translator.model.hazardanalysis;

import org.osate.safe.translator.model.ModelUtil.RuntimeErrorHandlingApproach;
import org.osate.safe.translator.model.ModelUtil.RuntimeFaultHandlingApproach;

public class RuntimeHandlingModel {
	private RuntimeErrorHandlingApproach errorHandlingApproach;
	private RuntimeFaultHandlingApproach faultHandlingApproach;
	private String explanation;
	private String name;
	
	public RuntimeHandlingModel(String name, String approachStr, String explanation) {
		this.name = name;
		this.explanation = explanation;
		this.errorHandlingApproach = RuntimeErrorHandlingApproach.valueOf(approachStr.toUpperCase());
		this.faultHandlingApproach = RuntimeFaultHandlingApproach.NOTSPECIFIED;
	}

	public RuntimeErrorHandlingApproach getErrorHandlingApproach() {
		return errorHandlingApproach;
	}

	public void setFaultHandlingApproach(String faultHandlingApproachStr) {
		this.faultHandlingApproach = RuntimeFaultHandlingApproach.valueOf(faultHandlingApproachStr.toUpperCase());
	}
	
	public RuntimeFaultHandlingApproach getFaultHandlingApproach() {
		return faultHandlingApproach;
	}
	
	public String getExplanation() {
		return explanation;
	}
	
	public String getName() {
		return name;
	}
}
