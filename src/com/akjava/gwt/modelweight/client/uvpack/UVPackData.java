package com.akjava.gwt.modelweight.client.uvpack;

import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.google.gwt.dom.client.ImageElement;

public class UVPackData {
private JSONModelFile modelFile;
private Geometry geometry;
public UVPackData(){
	this(null,null,0,2,0,0,null);
}
public UVPackData(JSONModelFile modelFile, String fileName, int faceType, int split, int x, int y, ImageElement texture) {
	super();
	this.modelFile = modelFile;
	this.fileName = fileName;
	this.faceType = faceType;
	this.split = split;
	this.x = x;
	this.y = y;
	this.texture = texture;
}
private String fileName;
public JSONModelFile getModelFile() {
	return modelFile;
}
public void setModelFile(JSONModelFile modelFile) {
	this.modelFile = modelFile;
}
public Geometry getGeometry() {
	return geometry;
}
public void setGeometry(Geometry geometry) {
	this.geometry = geometry;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}
public int getFaceType() {
	return faceType;
}
public void setFaceType(int faceType) {
	this.faceType = faceType;
}
public int getSplit() {
	return split;
}
public void setSplit(int split) {
	this.split = split;
}
public int getX() {
	return x;
}
public void setX(int x) {
	this.x = x;
}
public int getY() {
	return y;
}
public void setY(int y) {
	this.y = y;
}
public ImageElement getTexture() {
	return texture;
}
public void setTexture(ImageElement texture) {
	this.texture = texture;
}
private int faceType;//first byte of face datas.//use for every data same?
private int split;
private int x;
private int y;
private ImageElement texture;//use in future
}
