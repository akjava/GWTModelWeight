package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.Camera;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.math.Matrix3;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Line;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;

public class MeshVertexSelector extends Object3DMouseSelecter{

	public MeshVertexSelector(Mesh mesh,WebGLRenderer renderer, Camera camera,Object3D lineContainer) {
		super(renderer, camera);
		this.mesh=mesh;
		this.lineContainer=lineContainer;
	}
	private Mesh mesh;
	private Object3D lineContainer;
	
	
	public int pickVertex(ClickEvent event){
		return pickVertex(event.getX(),event.getY());
	}
	public void dispose(){
		if(selectedLine!=null){
			lineContainer.remove(selectedLine);
		}
	}
	public int pickVertex(int mx,int my){
		JsArray<Intersect> intersects=pickIntersects(mx, my, mesh);
		if(intersects==null || intersects.length()==0){
			selected=false;
			
			if(selectedLine!=null){
				selectedLine.setVisible(false);
			}
			
			return -1;
		}
		selected=true;
		
		Face3 face=intersects.get(0).getFace();
		Vector3 point=intersects.get(0).getPoint();
		int faceIndex=intersects.get(0).getFaceIndex();
		
		
		//ThreeLog.log("point:",point);
		
		int vertexOfFaceIndex=0;
		Vector3 selection=mesh.getGeometry().getVertices().get(face.getA());
	
		//ThreeLog.log("vertex1:",mesh.getGeometry().getVertices().get(face.getA()));
		double distance=point.distanceTo(matrixedPoint(mesh.getGeometry().getVertices().get(face.getA())));
		
		//ThreeLog.log("vertex2:",mesh.getGeometry().getVertices().get(face.getB()));
		double distance2=point.distanceTo(matrixedPoint(mesh.getGeometry().getVertices().get(face.getB())));
		if(distance2<distance){
			vertexOfFaceIndex=1;
			distance=distance2;
			selection=mesh.getGeometry().getVertices().get(face.getB());
		}
		//ThreeLog.log("vertex3:",mesh.getGeometry().getVertices().get(face.getC()));
		
		double distance3=point.distanceTo(matrixedPoint(mesh.getGeometry().getVertices().get(face.getC())));
		if(distance3<distance){
			vertexOfFaceIndex=2;
			distance=distance3;
			selection=mesh.getGeometry().getVertices().get(face.getC());
		}
		
		
		Face3 face2=mesh.getGeometry().getFaces().get(faceIndex);
		int vertex = face2.gwtGet(vertexOfFaceIndex);
		
		setSelectionVertex(vertex);
		
		
		
		
		
		
		return selectecVertexIndex;
	}
	protected Vector3 matrixedPoint(Vector3 vec){
		return vec.clone().applyMatrix4(mesh.getMatrixWorld());
	}
	
	private boolean visible=true;
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
		if(selected){
			if(selectedLine!=null){
				selectedLine.setVisible(visible);
			}
		}
	}
	private boolean selected;
	
	public boolean isSelected() {
		return selected;
	}
	private double lineLength=0.1;
	private Line selectedLine;
	private int selectecVertexIndex=-1;//-1 means no selection
	
	
	public void setSelectionVertex(int vertexIndex){
		
		selectecVertexIndex=vertexIndex;
		
		
		if(selectecVertexIndex==-1){
			
			selected=false;
			if(selectedLine!=null){
				selectedLine.setVisible(false);
			}
			return;
		}
		
		
		Vector3 normal=THREE.Vector3();
		for(int i=0;i<mesh.getGeometry().getFaces().length();i++){
			Face3 face3=mesh.getGeometry().getFaces().get(i);
			for(int j=0;j<3;j++){
				int vindex=face3.gwtGet(j);
				if(vindex==vertexIndex){
					normal.add(face3.getVertexNormals().get(j));
				}
			}
		}
		normal.normalize();
		
		
		Vector3 selection=mesh.getGeometry().getVertices().get(vertexIndex);
		
		//TODO log switch
		//LogUtils.log(vertexIndex+","+ThreeLog.get(selection));
		
		//make lines
		
		Matrix3 normalMatrix=THREE.Matrix3();
		Vector3 v1 = THREE.Vector3();
		Vector3 v2 = THREE.Vector3();
		
		normalMatrix.getNormalMatrix( mesh.getMatrixWorld());
		
		//Vector3 normal = face.getVertexNormals().get(vertexOfFaceIndex);

		v1.copy( selection ).applyMatrix4( mesh.getMatrixWorld() );

		v2.copy( normal ).applyMatrix3( normalMatrix ).normalize().multiplyScalar( lineLength ).add( v1 );
		
		updateLine(v1,v2);
}
	
	
	public int getSelectecVertex() {
		return selectecVertexIndex;
	}
	public void updateLine(Vector3 v1,Vector3 v2){
		if(selectedLine==null){
		Geometry geo = THREE.Geometry();//var geo = new THREE.Geometry();
		geo.getVertices().push( THREE.Vector3(  ));//geo.vertices.push( new THREE.Vector3( pos1[0], pos1[1], pos1[2] ) );
		geo.getVertices().push( THREE.Vector3(  ));//geo.vertices.push( new THREE.Vector3( pos2[0], pos2[1], pos2[2] ) );
		//TODO support color
		selectedLine = THREE.Line(geo, THREE.LineBasicMaterial(GWTParamUtils.LineBasicMaterial().color(0xff00ff).linewidth(1)));
		lineContainer.add(selectedLine);
		}
		selectedLine.setVisible(visible);
		
		selectedLine.getGeometry().getVertices().get(0).copy(v1);
		selectedLine.getGeometry().getVertices().get(1).copy(v2);
		selectedLine.getGeometry().setVerticesNeedUpdate(true);
		selectedLine.getGeometry().computeBoundingSphere();
	}
	public void update() {
		setSelectionVertex(selectecVertexIndex);
	}
}
