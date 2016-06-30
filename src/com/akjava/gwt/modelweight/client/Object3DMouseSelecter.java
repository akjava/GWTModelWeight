package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.Camera;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.core.Raycaster;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.google.gwt.core.client.JsArray;

public class Object3DMouseSelecter {
	private WebGLRenderer renderer;
	public Object3DMouseSelecter(WebGLRenderer renderer, Camera camera) {
		super();
		this.renderer = renderer;
		this.camera = camera;
	}

	private Camera camera;
	
	public JsArray<Intersect> pickIntersects(int mx,int my,JsArray<? extends Object3D> objects){
		return pickIntersects(mx,my,renderer.getSize().getInt("width"),renderer.getSize().getInt("height"),camera,objects);
	}
	
	public JsArray<Intersect> pickIntersects(int mx,int my,Object3D object){
		return pickIntersects(mx,my,renderer.getSize().getInt("width"),renderer.getSize().getInt("height"),camera,object);
	}
	
	private JsArray<Intersect> pickIntersects(double mx,double my,double sw,double sh,Camera camera,JsArray<? extends Object3D> objects){
		Vector3 screenPosition=THREE.Vector3(( mx / sw ) * 2 - 1, - ( my / sh ) * 2 + 1,1 );//no idea why 0.5
		screenPosition.unproject(camera);
		Raycaster ray=THREE.Raycaster(camera.getPosition(), screenPosition.sub( camera.getPosition() ).normalize());
	
		JsArray<Intersect> intersects=ray.intersectObjects(objects);
		return intersects;
	}
	private JsArray<Intersect> pickIntersects(double mx,double my,double sw,double sh,Camera camera,Object3D object){
		Vector3 screenPosition=THREE.Vector3(( mx / sw ) * 2 - 1, - ( my / sh ) * 2 + 1,1 );//no idea why 0.5
		screenPosition.unproject(camera);
		Raycaster ray=THREE.Raycaster(camera.getPosition(), screenPosition.sub( camera.getPosition() ).normalize());
	
		JsArray<Intersect> intersects=ray.intersectObject(object);
		return intersects;
	}
}
