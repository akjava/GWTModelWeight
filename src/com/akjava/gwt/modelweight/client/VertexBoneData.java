package com.akjava.gwt.modelweight.client;

import static com.google.common.base.Preconditions.checkArgument;

import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector4;

/**
 * for editing vertex indices & weights
 * @author aki
 *
 */
public class VertexBoneData {
private int vertexIndex;
public VertexBoneData(int vertexIndex, Vector4 indices, Vector4 weights) {
	super();
	this.vertexIndex = vertexIndex;
	this.indices = indices;
	this.weights = weights;
}
public int getVertexIndex() {
	return vertexIndex;
}
public void setVertexIndex(int vertexIndex) {
	this.vertexIndex = vertexIndex;
}
public Vector4 getIndices() {
	return indices;
}
public void setIndices(Vector4 indices) {
	this.indices = indices;
}
public Vector4 getWeights() {
	return weights;
}
public void setWeights(Vector4 weights) {
	this.weights = weights;
}
private Vector4 indices;
private Vector4 weights;


public static VertexBoneData createFromdMesh(Geometry geometry,int vertexIndex){
	checkArgument(vertexIndex>=0 && vertexIndex<geometry.getVertices().length(),"out of vertex index:geometry-vertex="+geometry.getVertices().length()+",vertexInex="+vertexIndex);
	checkArgument(vertexIndex<geometry.getSkinIndices().length(),"out of skinIndices:skinIndices="+geometry.getSkinIndices().length()+",vertexInex="+vertexIndex);
	checkArgument(vertexIndex<geometry.getSkinWeights().length(),"out of skinWeights:skinWeights="+geometry.getSkinWeights().length()+",vertexInex="+vertexIndex);
	
	return new VertexBoneData(vertexIndex, geometry.getSkinIndices().get(vertexIndex), geometry.getSkinWeights().get(vertexIndex));
}

}
