package com.akjava.gwt.modelweight.client;

import com.akjava.bvh.client.AnimationBoneConverter;
import com.akjava.bvh.client.AnimationDataConverter;
import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Quaternion;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.core.Vector4;
import com.akjava.gwt.three.client.extras.animation.Animation;
import com.akjava.gwt.three.client.extras.animation.AnimationHandler;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader.LoadHandler;
import com.akjava.gwt.three.client.gwt.Clock;
import com.akjava.gwt.three.client.gwt.SimpleDemoEntryPoint;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.akjava.gwt.three.client.gwt.animation.AnimationData;
import com.akjava.gwt.three.client.gwt.animation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.gwt.animation.AnimationKey;
import com.akjava.gwt.three.client.gwt.animation.AnimationUtils;
import com.akjava.gwt.three.client.lights.Light;
import com.akjava.gwt.three.client.objects.Mesh;
import com.akjava.gwt.three.client.objects.SkinnedMesh;
import com.akjava.gwt.three.client.renderers.WebGLRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTModelWeight extends SimpleDemoEntryPoint{

	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		long delta=clock.delta();
		//log(""+animation.getCurrentTime());
		double v=(double)delta/1000;
		AnimationHandler.update(v);
	}

	private Clock clock=new Clock();
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		Light pointLight = THREE.PointLight(0xffffff);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		/*
		//write test
		Geometry g=THREE.CubeGeometry(1, 1, 1);
		log(g);
		
		Geometry g2=THREE.CubeGeometry(1, 1, 1);
		Matrix4 mx=THREE.Matrix4();
		mx.setPosition(THREE.Vector3(0,10,0));
		//g2.applyMatrix(mx);
		Mesh tmpM=THREE.Mesh(g2, THREE.MeshBasicMaterial().build());
		tmpM.setPosition(0, 10, 0);
		
		
		GeometryUtils.merge(g, tmpM);
		
		JSONModelFile model=JSONModelFile.create();
		
		model.setVertices(g.vertices());
		model.setFaces(g.faces());
		
		
		
		//JSONArray vertices=new JSONArray(nums);
		JSONObject js=new JSONObject(model);
		log(js.toString());
		
		scene.add(THREE.AmbientLight(0x888888));
		Light pointLight = THREE.PointLight(0xffffff);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		*/
		
		//loadBVH("14_08.bvh");
		
		JSONLoader loader=THREE.JSONLoader();
		loader.load("buffalo.js", new LoadHandler() {
			
			@Override
			public void loaded(Geometry geometry) {
				AnimationHandler.add(geometry.getAnimation());
				log(geometry.getBones());
				log(geometry.getAnimation());
				//JSONObject test=new JSONObject(geometry.getAnimation());
				//log(test.toString());
				
				Geometry cube=THREE.CubeGeometry(1, 1, 1);
				JsArray<Vector4> indices=(JsArray<Vector4>) JsArray.createArray();
				JsArray<Vector4> weight=(JsArray<Vector4>) JsArray.createArray();
				for(int i=0;i<cube.vertices().length();i++){
					Vector4 v4=THREE.Vector4();
					v4.set(0, 0, 0, 0);
					indices.push(v4);
					
					Vector4 v4w=THREE.Vector4();
					v4w.set(1, 0, 0, 0);
					weight.push(v4w);
				}
				
				cube.setSkinIndices(indices);
				cube.setSkinWeight(weight);
				
				cube.setBones(geometry.getBones());
				
				SkinnedMesh mesh=THREE.SkinnedMesh(cube, THREE.MeshLambertMaterial().skinning(true).color(0xff0000).build());
				scene.add(mesh);
				GWT.log("l2.5");
				//Animation animation = THREE.Animation( mesh, "take_001" );
				//log(animation);
				//animation.play(); //buffalo
				GWT.log("l3");
				
			}
		});
		
		loadBVH("14_08.bvh");
	}
	
	

	@Override
	public void onMouseClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createControl(Panel parent) {
		// TODO Auto-generated method stub
		
	}
	
	private void loadBVH(String path){
	
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(path));

			try {
				builder.sendRequest(null, new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						
						String bvhText=response.getText();
						//log("loaded:"+Benchmark.end("load"));
						//useless spend allmost time with request and spliting.
						parseBVH(bvhText);

					}
					
					
					

@Override
public void onError(Request request, Throwable exception) {
				Window.alert("load faild:");
}
				});
			} catch (RequestException e) {
				log(e.getMessage());
				e.printStackTrace();
			}
	}
	private BVH bvh;
	private Animation animation;
	private void parseBVH(String bvhText){
		final BVHParser parser=new BVHParser();
		
		parser.parseAsync(bvhText, new ParserListener() {
			
			

			@Override
			public void onSuccess(BVH bv) {
				
				
				bvh=bv;
				//bvh.setSkips(10);
				//bvh.setSkips(skipFrames);
				GWT.log("parsed");
				AnimationBoneConverter converter=new AnimationBoneConverter();
				JsArray<AnimationBone> bones=converter.convertJsonBone(bvh);
				for(int i=0;i<bones.length();i++){
					Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5), THREE.MeshLambertMaterial().color(0xff0000).build());
					scene.add(mesh);
					JsArrayNumber pos=bones.get(i).getPos();
					mesh.setPosition(pos.get(0),pos.get(1),pos.get(2));
				}
				
				int  keyIndex=5;
				AnimationDataConverter dataConverter=new AnimationDataConverter();
				AnimationData data=dataConverter.convertJsonAnimation(bvh);
				
				JsArray<AnimationHierarchyItem> hitem=data.getHierarchy();
				/*
				for(int i=0;i<hitem.length();i++){
					AnimationHierarchyItem item=hitem.get(i);
					
					AnimationHierarchyItem parent=null;
					if(item.getParent()!=-1){
						parent=hitem.get(item.getParent());
					}
					AnimationKey key=item.getKeys().get(keyIndex);
					Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5), THREE.MeshLambertMaterial().color(0x00ff00).build());
					scene.add(mesh);
					
					Vector3 meshPos=AnimationUtils.getPosition(key);
					mesh.setPosition(meshPos);
					if(parent!=null){
						Vector3 parentPos=AnimationUtils.getPosition(parent.getKeys().get(keyIndex));
						Geometry lineG = THREE.Geometry();
						lineG.vertices().push(THREE.Vertex(meshPos));
						lineG.vertices().push(THREE.Vertex(parentPos));
						Mesh line=THREE.Line(lineG, THREE.LineBasicMaterial().color(0xffffff).build());
						scene.add(line);
					}
					
					
					
					Quaternion q=key.getRot();
					Matrix4 rot=THREE.Matrix4();
					rot.setRotationFromQuaternion(q);
					Vector3 rotV=THREE.Vector3();
					rotV.setRotationFromMatrix(rot);
					mesh.setRotation(rotV);
					
				
				
				}
				*/
				
				
				Geometry cube=THREE.CubeGeometry(1, 1, 1);
				JsArray<Vector4> indices=(JsArray<Vector4>) JsArray.createArray();
				JsArray<Vector4> weight=(JsArray<Vector4>) JsArray.createArray();
				for(int i=0;i<cube.vertices().length();i++){
					Vector4 v4=THREE.Vector4();
					v4.set(0, 0, 0, 0);
					indices.push(v4);
					
					Vector4 v4w=THREE.Vector4();
					v4w.set(1, 0, 0, 0);
					weight.push(v4w);
				}
				
				cube.setSkinIndices(indices);
				cube.setSkinWeight(weight);
				
				cube.setBones(bones);
				AnimationHandler.add(data);
				log(data);
				log(bones);
				
				JSONArray array=new JSONArray(bones);
				//log(array.toString());
				
				JSONObject test=new JSONObject(data);
				//log(test.toString());
				
				
				log("before create");
				SkinnedMesh mesh=THREE.SkinnedMesh(cube, THREE.MeshLambertMaterial().skinning(true).color(0xff0000).build());
				log("create-mesh");
				scene.add(mesh);
				
				animation = THREE.Animation( mesh, data.getName() );
				log(animation);
				animation.play();
				
				
			}
			
			@Override
			public void onFaild(String message) {
				log(message);
			}
		});
	}
}
