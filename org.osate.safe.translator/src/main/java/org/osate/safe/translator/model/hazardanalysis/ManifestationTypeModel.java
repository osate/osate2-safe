package org.osate.safe.translator.model.hazardanalysis;

import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;

import org.osate.safe.translator.model.ModelUtil.ManifestationType;

/**
 * This class models a single manifestation.
 * 
 * @author Sam
 *
 */
public class ManifestationTypeModel {
	private String name;
	private ManifestationType manifestation;
	private boolean sunk = false;
	
	public ManifestationTypeModel(String name, ErrorType parentType) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public void setManifestation(ManifestationType manifestation) {
		this.manifestation = manifestation;
	}

	public String getManifestationName() {
		if(manifestation == null){
			return null;
		} else {
			return manifestation.toString();
		}
	}
	
	public boolean isSunk() {
		return sunk;
	}
	
	public void setSunk() {
		sunk = true;
	}
}
