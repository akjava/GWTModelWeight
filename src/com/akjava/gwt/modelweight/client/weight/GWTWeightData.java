package com.akjava.gwt.modelweight.client.weight;

import com.akjava.gwt.three.client.gwt.collada.WeightData;

public class GWTWeightData implements Comparable<GWTWeightData> {
private int boneIndex;
private double weight;

public GWTWeightData(){}
public GWTWeightData(WeightData data){
	this.boneIndex=data.getJoint();
	this.weight=data.getWeight();
}
public int getBoneIndex() {
	return boneIndex;
}
public void setBoneIndex(int boneIndex) {
	this.boneIndex = boneIndex;
}
public double getWeight() {
	return weight;
}
public void setWeight(double weight) {
	this.weight = weight;
}
@Override
public int compareTo(GWTWeightData o) {
	if(this.weight>o.getWeight()){
		return -1;
	}else{
		return 1;
	}
}

}
