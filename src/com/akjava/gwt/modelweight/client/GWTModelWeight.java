package com.akjava.gwt.modelweight.client;

import com.akjava.bvh.client.AnimationBoneConverter;
import com.akjava.bvh.client.AnimationDataConverter;
import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Quaternion;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.gwt.SimpleDemoEntryPoint;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.akjava.gwt.three.client.gwt.animation.AnimationData;
import com.akjava.gwt.three.client.gwt.animation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.gwt.animation.AnimationKey;
import com.akjava.gwt.three.client.lights.Light;
import com.akjava.gwt.three.client.objects.Mesh;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTModelWeight extends SimpleDemoEntryPoint{

	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initializeOthers(WebGLRenderer renderer) {

		
		scene.add(THREE.AmbientLight(0x888888));
		Light pointLight = THREE.PointLight(0xffffff);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		
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
	private void parseBVH(String bvhText){
		final BVHParser parser=new BVHParser();
		
		parser.parseAsync(bvhText, new ParserListener() {
			
			@Override
			public void onSuccess(BVH bv) {
				bvh=bv;
				bvh.setSkips(100);
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
				
				int  keyIndex=2;
				AnimationDataConverter dataConverter=new AnimationDataConverter();
				AnimationData data=dataConverter.convertJsonAnimation(bvh);
				log(data);
				JsArray<AnimationHierarchyItem> hitem=data.getHierarchy();
				for(int i=0;i<hitem.length();i++){
					AnimationHierarchyItem item=hitem.get(i);
					AnimationKey key=item.getKeys().get(keyIndex);
					Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5), THREE.MeshLambertMaterial().color(0x00ff00).build());
					scene.add(mesh);
					JsArrayNumber pos=key.getPos();
					mesh.setPosition(pos.get(0),pos.get(1),pos.get(2));
					
					Quaternion q=key.getRot();
					Matrix4 rot=THREE.Matrix4();
					rot.setRotationFromQuaternion(q);
					Vector3 rotV=THREE.Vector3();
					rotV.setRotationFromMatrix(rot);
					mesh.setRotation(rotV);
					
				}
			}
			
			@Override
			public void onFaild(String message) {
				log(message);
			}
		});
	}
}
