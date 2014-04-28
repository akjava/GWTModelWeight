package com.akjava.gwt.modelweight.client.uvpack;

import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.google.gwt.dom.client.ImageElement;

public class UVPackData {
private JSONModelFile modelFile;
private Geometry geometry;
public UVPackData(){
	this(null,null,0,2,0,0,null,null);
}
public UVPackData(JSONModelFile modelFile, String fileName, int faceType, int split, int x, int y, ImageElement texture,String textureFileName) {
	super();
	this.modelFile = modelFile;
	this.modelFileName = fileName;
	this.faceType = faceType;
	this.split = split;
	this.x = x;
	this.y = y;
	this.texture = texture;
	this.textureFileName=textureFileName;
}

public String toString(){
	//for pack csv
	return split+","+x+","+y+","+(comment!=null?comment.replace(",", " "):"");
}

private String comment="";//not allow null

public String getComment() {
	return comment;
}
public void setComment(String comment) {
	this.comment = comment;
}

private String textureFileName;
public String getTextureFileName() {
	return textureFileName;
}
public void setTextureFileName(String textureFileName) {
	this.textureFileName = textureFileName;
}
private String modelFileName;
public String getModelFileName() {
	return modelFileName;
}
public void setModelFileName(String modelFileName) {
	this.modelFileName = modelFileName;
}
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
private int split=1;//0 is invalid
private int x;
private int y;
private ImageElement texture;//use in future
}
