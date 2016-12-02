package org.osate.safe.translator.model;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.osate.safe.translator.exception.DuplicateElementException;
import org.osate.safe.translator.model.hazardanalysis.ManifestationTypeModel;

public class SystemModel extends ComponentModel<DevOrProcModel, SystemConnectionModel>{

	// Type name -> Child name Model
	private HashMap<String, DevOrProcModel> typeToComponent;

	private String timestamp;
	
	private List<String> haExplanations = new LinkedList<>();
	
	// Fault name -> Fault model
//	private HashMap<String, ErrorTypeModel> faultClasses;

	/**
	 * Error type name -> model
	 */
	private Map<String, ManifestationTypeModel> errorTypeModels;

	public SystemModel() {
		super();
		typeToComponent = new HashMap<>();
	}
	
	public ProcessModel getProcessByType(String processTypeName) {
		if (typeToComponent.get(processTypeName) instanceof ProcessModel)
			return (ProcessModel) typeToComponent.get(processTypeName);
		else
			return null;
	}

	public DeviceModel getDeviceByType(String deviceTypeName) {
		if (typeToComponent.get(deviceTypeName) instanceof DeviceModel)
			return (DeviceModel) typeToComponent.get(deviceTypeName);
		else
			return null;
	}
	
	@Override
	public void addChild(String childName, DevOrProcModel childModel) throws DuplicateElementException {
		super.addChild(childName, childModel);
		if(typeToComponent.containsKey(childName))
			throw new DuplicateElementException(childName + " already exists");
		typeToComponent.put(childModel.getName(), childModel);
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public HashMap<String, ProcessModel> getLogicComponents() {
		Map<String, DevOrProcModel> preCast = children.entrySet()
				.stream()
				.filter(p -> p.getValue() instanceof ProcessModel)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		
		HashMap<String, ProcessModel> ret = new HashMap<>();
		for(String elemName : preCast.keySet()){
			ret.put(elemName, (ProcessModel) preCast.get(elemName));
		}
		return ret;
	}
	
	public HashMap<String, DevOrProcModel> getLogicAndDevices() {
		return children;
	}
	
	public boolean hasProcessType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof ProcessModel));
	}

	public boolean hasDeviceType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof DeviceModel));
	}
	
	public HashMap<String, ConnectionModel> getUniqueDevicePublishedChannels(){
		Set<SystemConnectionModel> chanSet = channels.values()
				.stream()
				.filter(cs -> cs.publisher instanceof DeviceModel)
				.collect(Collectors.toSet());
		
		// Get a set that's distinct based on publishing identity 
		// (publisher component name + publisher port name)
		HashMap<String, ConnectionModel> chanMap = new HashMap<String, ConnectionModel>();
		for(ConnectionModel cm : chanSet) {
			chanMap.put(cm.getPubName().concat(cm.getPubPortName()), cm);
		}
		return chanMap;
	}
	
	public HashMap<String, ConnectionModel> getUniqueDeviceSubscribedChannels(){
		Set<SystemConnectionModel> chanSet = channels.values()
				.stream()
				.filter(cs -> cs.subscriber instanceof DeviceModel)
				.collect(Collectors.toSet());
		
		// Get a set that's distinct based on subscriber identity 
		// (subscriber component name + subscriber port name)
		HashMap<String, ConnectionModel> chanMap = new HashMap<String, ConnectionModel>();
		for(ConnectionModel cm : chanSet) {
			chanMap.put(cm.getSubName().concat(cm.getSubPortName()), cm);
		}
		return chanMap;
	}

	public void setErrorTypes(Map<String, ManifestationTypeModel> errorTypeModels) {
		this.errorTypeModels = errorTypeModels;
	}
	
	public Set<String> getAllErrorTypes(){
		return errorTypeModels.values()
				.stream()
				.map(v -> v.getManifestationName())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
	
	public ManifestationTypeModel getErrorTypeModelByName(String name){
		if(errorTypeModels == null){
			// This will happen if there is no error type information at all
			// We don't want to require that, so we just return null
			return null;
		}
		return errorTypeModels.get(name);
	}
	
	public void addExplanation(String exp){
		haExplanations.add(exp);
	}
	
	public List<String> getExplanations(){
		return haExplanations;
	}
}
