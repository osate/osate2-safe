package org.osate.safe.translator.model.hazardanalysis;

import org.osate.safe.translator.exception.CoreException;

/**
 * An externally caused danger represents the path of a set of errors through a
 * component; it's essentially an incoming danger (manifestation), outgoing
 * danger (succDanger), and some metadata.
 * 
 * @author sam
 *
 */
public class ExternallyCausedDangerModel extends CausedDangerModel {

	private PropagationModel danger;
	private ProcessVariableModel processVariableModel;
	private String processVariableModelConstraint;

	public ExternallyCausedDangerModel(PropagationModel inProp, PropagationModel outProp, String interp,
			ProcessVariableModel processVariableModel, String constraint) throws CoreException {
		super(inProp, interp);
		if (!inProp.getName().equals(outProp.getName())) {
			throw new CoreException(
					"Tried to create an external danger out of mismatched successor danger and manifestation!");
		}
		super.setName(inProp.getName());
		this.danger = outProp;
		this.processVariableModel = processVariableModel;
		this.processVariableModelConstraint = constraint;
	}

	public PropagationModel getDanger() {
		return danger;
	}

	public ProcessVariableModel getProcessVariable() {
		return processVariableModel;
	}

	public String getProcessVariableConstraint() {
		return processVariableModelConstraint;
	}
}
