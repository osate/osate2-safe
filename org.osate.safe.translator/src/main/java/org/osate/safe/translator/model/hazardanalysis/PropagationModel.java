package org.osate.safe.translator.model.hazardanalysis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.osate.safe.translator.model.FeatureModel;

/**
 * This class models EMv2 propagations.
 * 
 * @author Sam
 *
 */
public class PropagationModel{
	private String name;
	private Map<String, ManifestationTypeModel> errors;
	private FeatureModel port;

	public PropagationModel(String name, Collection<ManifestationTypeModel> errors, FeatureModel portModel) {
		this.name = name;
		setError(errors);
		this.port = portModel;
	}

	public Set<ManifestationTypeModel> getErrors() {
		return new LinkedHashSet<>(errors.values());
	}

	public void setError(Collection<ManifestationTypeModel> errors) {
		this.errors = new LinkedHashMap<>();
		for(ManifestationTypeModel error : errors){
			this.errors.put(error.getName(), error);
		}
	}
	
	public ManifestationTypeModel getErrorByName(String name){
		return errors.get(name);
	}

	public FeatureModel getPort() {
		return port;
	}

	public String getName() {
		return name;
	}
}
