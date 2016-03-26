package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Set;

public abstract class CausedDangerModel {

	PropagationModel successorDanger;
	String interp;
	Set<ErrorTypeModel> cooccurringDangers;
	//RuntimeDetectionModel
	//RuntimeHandlingModel
	
	public CausedDangerModel(PropagationModel succDanger, String interp,
			Set<ErrorTypeModel> cooccurringDangers) {
		this.successorDanger = succDanger;
		this.interp = interp;
		this.cooccurringDangers = cooccurringDangers;
	}
	
	public PropagationModel getSuccessorDanger() {
		return successorDanger;
	}

	public String getInterp() {
		return interp;
	}

	public Set<ErrorTypeModel> getCooccurringDangers() {
		return cooccurringDangers;
	}
}