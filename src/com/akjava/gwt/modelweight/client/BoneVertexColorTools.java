package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.akjava.lib.common.utils.ColorUtils;

public class BoneVertexColorTools {
private Geometry geometry;

/*
 * need .vertexColors(THREE.VertexColors) on material
 */
public BoneVertexColorTools(Geometry geometry) {
	super();
	this.geometry = geometry;
	initColor();
}

private void initColor(){
			for(int i=0;i<geometry.getFaces().length();i++){
				Face3 face=geometry.getFaces().get(i);
				for(int j=0;j<3;j++){
					face.getVertexColors().set(j, THREE.Color(0));
				}
			}
			geometry.setColorsNeedUpdate(true);
}

public int getVertexColor(int vertexIndex,int boneIndex){
	
	double v=0;
	
	Vector4 indices=geometry.getSkinIndices().get(vertexIndex);
	for(int i=0;i<4;i++){
		if(indices.gwtGet(i)==boneIndex){
			v+=geometry.getSkinWeights().get(vertexIndex).gwtGet(i);
		}
	}
	
	if(v==1){
		return 0xffffff;
	}else if(v==0){
		return 0;
	}
	
	int[] cv=toColorByDouble(v);
	
	int color=ColorUtils.toColor(cv);
	return color;
	
}

private int [] toColorByDouble(double v){
	return toColor((int) (255*v));
}
//i get this somewhere internet
//0-255
private int[] toColor(int v){
	int[] rgb=new int[3];
	double phase = (double)v/255;
	double shift =Math.PI+Math.PI/4;
	 rgb[2]=(int) (255*(Math.sin(1.5*Math.PI*phase + shift + Math.PI ) + 1)/2.0) ;
	 rgb[1]=(int) (255*(Math.sin(1.5*Math.PI*phase + shift + Math.PI/2 ) + 1)/2.0) ;
	 rgb[0]=(int) (255*(Math.sin(1.5*Math.PI*phase + shift  ) + 1)/2.0) ;
	return rgb;
}

/*
 * black it
 */
public void clearVertexsColor() {
	for(int i=0;i<geometry.getFaces().length();i++){
		Face3 face=geometry.getFaces().get(i);
		for(int j=0;j<3;j++){
			face.getVertexColors().get(j).setHex(0);
		}
	}
	geometry.setColorsNeedUpdate(true);
}
public void updateVertexsColorByBone(int selectedBoneIndex) {
	
	for(int i=0;i<geometry.getFaces().length();i++){
		Face3 face=geometry.getFaces().get(i);
		//LogUtils.log("color-size:"+face.getVertexColors().length());
		for(int j=0;j<3;j++){
			int vertexIndex=face.gwtGet(j);
			//Vector4 index=geometry.getSkinIndices().get(vertexIndex);
			int color=getVertexColor(vertexIndex,selectedBoneIndex);
			
			face.getVertexColors().get(j).setHex(color);
		}
	}
	geometry.setColorsNeedUpdate(true);
	
}

}
