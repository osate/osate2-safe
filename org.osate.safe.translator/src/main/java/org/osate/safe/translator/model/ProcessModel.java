package org.osate.safe.translator.model;

import java.util.HashMap;
import java.util.Set;

import org.osate.safe.translator.exception.DuplicateElementException;
import org.osate.safe.translator.model.ModelUtil.ComponentType;
import org.osate.safe.translator.model.ModelUtil.ProcessType;

public class ProcessModel extends DevOrProcModel{
	
	// variable name -> type
	private HashMap<String, String> globals;
	
	// method name -> method model
	private HashMap<String, MethodModel> methods;

	public ProcessModel() {
		super();
		methods = new HashMap<>();
		globals = new HashMap<>();
	}

	public HashMap<String, String> getGlobals() {
		return globals;
	}
	
	/**
	 * This will return the type of a global variable
	 * @param name The name of the global variable
	 * @return The type of the global variable
	 */
	public String getGlobalType(String name){
		return globals.get(name);
	}

	public void setDisplay(boolean display) {
		if(display){
			this.processType = ProcessType.DISPLAY;
		} else {
			this.processType = ProcessType.LOGIC;
		}
	}

	public TaskModel getLastThread(){
		return (TaskModel) children.get(children.size() - 1);
	}
	
	public void addGlobal(String name, String type) {
		globals.put(name, type);
	}

	public HashMap<String, MethodModel> getMethods() {
		return methods;
	}

	public void addMethod(String methodName, MethodModel method) {
		methods.put(methodName, method);
	}
	
	public void addParameterToMethod(String methodName, String parameterName, String parameterType) throws DuplicateElementException{
		if(!methods.containsKey(methodName))
			methods.put(methodName, new MethodModel(methodName));
		methods.get(methodName).addParameter(parameterName, parameterType);
	}

	public void addReturnToMethod(String methodName, String returnType) {
		if(!methods.containsKey(methodName))
			methods.put(methodName, new MethodModel(methodName));
		methods.get(methodName).setRetType(returnType);
	}
}
