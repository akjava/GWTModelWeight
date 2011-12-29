package com.akjava.gwt.modelweight.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.bvh.client.AnimationBoneConverter;
import com.akjava.bvh.client.AnimationDataConverter;
import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.extra.HTML5Builder;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Intersect;
import com.akjava.gwt.three.client.core.Object3D;
import com.akjava.gwt.three.client.core.Projector;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.core.Vector4;
import com.akjava.gwt.three.client.extras.GeometryUtils;
import com.akjava.gwt.three.client.extras.ImageUtils;
import com.akjava.gwt.three.client.extras.animation.Animation;
import com.akjava.gwt.three.client.extras.animation.AnimationHandler;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader.LoadHandler;
import com.akjava.gwt.three.client.gwt.Clock;
import com.akjava.gwt.three.client.gwt.GWTGeometryUtils;
import com.akjava.gwt.three.client.gwt.SimpleDemoEntryPoint;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.akjava.gwt.three.client.gwt.animation.AnimationData;
import com.akjava.gwt.three.client.gwt.animation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.lights.Light;
import com.akjava.gwt.three.client.materials.Material;
import com.akjava.gwt.three.client.objects.Mesh;
import com.akjava.gwt.three.client.objects.SkinnedMesh;
import com.akjava.gwt.three.client.renderers.WebGLRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTModelWeight extends SimpleDemoEntryPoint{

	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		
		
		if(root!=null){
			
			boneAndVertex.getRotation().set(Math.toRadians(rotX),Math.toRadians(rotY),0);
			boneAndVertex.getPosition().set(posX,posY,0);
			root.setPosition(positionXRange.getValue(), positionYRange.getValue(), positionZRange.getValue());
			
			root.getRotation().set(Math.toRadians(rotationRange.getValue()),Math.toRadians(rotationYRange.getValue()),Math.toRadians(rotationZRange.getValue()));
			}
		
		long delta=clock.delta();
		//log(""+animation.getCurrentTime());
		double v=(double)delta/1000;
		if(!paused){
			AnimationHandler.update(v);
		}
		//
	}

	private Clock clock=new Clock();
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		scene.add(THREE.AmbientLight(0xffffff));
		
		Light pointLight = THREE.DirectionalLight(0xffffff,1);
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
				
				//root=boneToCube(geometry.getBones());
				//scene.add(root);
				//SkinnedMesh mesh=THREE.SkinnedMesh(cube, THREE.MeshLambertMaterial().skinning(true).color(0xff0000).build());
				//scene.add(mesh);
				GWT.log("l2.5");
				//Animation animation = THREE.Animation( mesh, "take_001" );
				//log(animation);
				//animation.play(); //buffalo
				GWT.log("l3");
				
			}
		});
		
		loadBVH("14_01.bvh");
		//loadBVH("14_08.bvh");
	}
	Object3D root;
	
	List<Mesh> tmp=new ArrayList<Mesh>();
	private Object3D boneToCube(JsArray<AnimationBone> bones){
		tmp.clear();
		Object3D group=THREE.Object3D();
		for(int i=0;i<bones.length();i++){
			AnimationBone bone=bones.get(i);
			Geometry cube=THREE.CubeGeometry(.5, .5, .5);
			int color=0xff0000;
			if(i==0){
				//color=0x00ff00;
			}
			Mesh mesh=THREE.Mesh(cube, THREE.MeshLambertMaterial().color(color).build());
			group.add(mesh);
			Vector3 pos=AnimationBone.jsArrayToVector3(bone.getPos());
			
			if(bone.getParent()!=-1){
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				pos.addSelf(ppos);
			}
			mesh.setPosition(pos);
			mesh.setName(bone.getName());
			
			if(bone.getParent()!=-1){
				//AnimationBone parent=bones.get(bone.getParent());
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				Mesh line=THREE.Line(GWTGeometryUtils.createLine(pos, ppos), THREE.LineBasicMaterial().color(0x888888).build());
				group.add(line);
			}
			tmp.add(mesh);
		}
		return group;
	}
	
	
	final Projector projector=THREE.Projector();
	Label debugLabel;
	@Override
	public void onMouseClick(ClickEvent event) {
		int x=event.getX();
		int y=event.getY();
		/*
		if(inEdge(x,y)){
			screenMove(x,y);
			return;
		}*/
		
		JsArray<Intersect> intersects=projector.pickIntersects(event.getX(), event.getY(), screenWidth, screenHeight, camera,scene);
		
		for(int i=0;i<intersects.length();i++){
			Intersect sect=intersects.get(i);
			
			Object3D target=sect.getObject();
			if(!target.getName().isEmpty()){
				if(target.getName().startsWith("point:")){
					String[] pv=target.getName().split(":");
					int at=Integer.parseInt(pv[1]);
					Vector4 in=bodyIndices.get(at);
					Vector4 we=bodyWeight.get(at);
					
					indexWeightEditor.setValue(at, in, we);
					//createSkinnedMesh();
					log("created:");
					//debugLabel.setText(in.getX()+":"+in.getY()+","+we.getX()+":"+we.getY());
				}else{
				select(target);
				break;
				}
				
			}
			
		}
	}
	

	private Mesh selection;
	private int selectionBoneIndex;
	private Object3D selectVertex;
	private void select(Object3D target) {
		if(selection==null){
			selection=THREE.Mesh(THREE.CubeGeometry(1, 1, 1), THREE.MeshLambertMaterial().color(0x00ff00).build());
			boneAndVertex.add(selection);
		}
		selection.setPosition(target.getPosition());
		selectionBoneIndex=findBoneIndex(target.getName());
		selectVertex(selectionBoneIndex);
	}
	private void selectVertex(int selected) {
		for(int i=0;i<bodyGeometry.vertices().length();i++){
			Vector4 index=bodyIndices.get(i);
			Mesh mesh=vertexs.get(i);
			if(index.getX()==selected || index.getY()==selected){
				mesh.setVisible(true);
				Vector4 weight=bodyWeight.get(i);
				log(weight.getX()+","+weight.getY());
			}else{
				mesh.setVisible(false);
			}
		}
	}
	private int findBoneIndex(String name){
		int ret=0;
		for(int i=0;i<bones.length();i++){
			if(bones.get(i).getName().equals(name)){
				ret=i;
				break;
			}
		}
		return ret;
	}

	int rotX;
	int rotY;
	int posX;
	int posY;
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(mouseDown){
			
			int diffX=event.getX()-mouseDownX;
			int diffY=event.getY()-mouseDownY;
			mouseDownX=event.getX();
			mouseDownY=event.getY();
			
			if(event.isShiftKeyDown()){
			posX+=diffX;
			posY-=diffY;
			}else{
				rotX=(rotX+diffY);
				rotY=(rotY+diffX);
			}
		}
	}
	
	private HTML5InputRange positionXRange;
	private HTML5InputRange positionYRange;
	private HTML5InputRange positionZRange;
	
	private HTML5InputRange rotationRange;
	private HTML5InputRange rotationYRange;
	private HTML5InputRange rotationZRange;
	@Override
	public void createControl(Panel parent) {
		debugLabel=new Label();
		parent.add(debugLabel);
HorizontalPanel h1=new HorizontalPanel();
		
		rotationRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("X-Rotate:", rotationRange));
		parent.add(h1);
		h1.add(rotationRange);
		Button reset=new Button("Reset");
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationRange.setValue(0);
			}
		});
		h1.add(reset);
		
		HorizontalPanel h2=new HorizontalPanel();
		
		rotationYRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("Y-Rotate:", rotationYRange));
		parent.add(h2);
		h2.add(rotationYRange);
		Button reset2=new Button("Reset");
		reset2.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationYRange.setValue(0);
			}
		});
		h2.add(reset2);
		
		
		HorizontalPanel h3=new HorizontalPanel();
		rotationZRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("Z-Rotate:", rotationZRange));
		parent.add(h3);
		h3.add(rotationZRange);
		Button reset3=new Button("Reset");
		reset3.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationZRange.setValue(0);
			}
		});
		h3.add(reset3);
		
		HorizontalPanel h4=new HorizontalPanel();
		positionXRange = new HTML5InputRange(-50,50,0);
		parent.add(HTML5Builder.createRangeLabel("X-Position:", positionXRange));
		parent.add(h4);
		h4.add(positionXRange);
		Button reset4=new Button("Reset");
		reset4.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionXRange.setValue(0);
			}
		});
		h4.add(reset4);
		
		HorizontalPanel h5=new HorizontalPanel();
		positionYRange = new HTML5InputRange(-50,50,0);
		parent.add(HTML5Builder.createRangeLabel("Y-Position:", positionYRange));
		parent.add(h5);
		h5.add(positionYRange);
		Button reset5=new Button("Reset");
		reset5.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionYRange.setValue(0);
			}
		});
		h5.add(reset5);
		
		HorizontalPanel h6=new HorizontalPanel();
		positionZRange = new HTML5InputRange(-50,50,0);
		parent.add(HTML5Builder.createRangeLabel("Z-Position:", positionZRange));
		parent.add(h6);
		h6.add(positionZRange);
		Button reset6=new Button("Reset");
		reset6.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionZRange.setValue(0);
			}
		});
		h6.add(reset6);
		
		positionYRange.setValue(-13);
		positionXRange.setValue(20);
		
		Button bt=new Button("Pause/Play");
		parent.add(bt);
		bt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				paused=!paused;
			}
		});
		
		//editor
		
		
		indexWeightEditor = new IndexAndWeightEditor();
		parent.add(indexWeightEditor);
		
		Button update=new Button("Update");
		update.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int index=indexWeightEditor.getArrayIndex();
				if(index==-1){
					return;
				}
				Vector4 in=bodyIndices.get(index);
				Vector4 we=bodyWeight.get(index);
				in.setX(indexWeightEditor.getIndex1());
				in.setY(indexWeightEditor.getIndex2());
				we.setX(indexWeightEditor.getWeight1());
				we.setY(indexWeightEditor.getWeight2());
				
				log("new-ind-weight:"+in.getX()+","+in.getY()+","+we.getX()+","+we.getY());
				createSkinnedMesh();
			}
		});
		parent.add(update);
		
		showControl();
	}
	
	
	
	private boolean paused;
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
	
	private JsArray<Vector4> bodyIndices;
	private JsArray<Vector4> bodyWeight;
	
	
	private BVH bvh;
	private Animation animation;
	
	private Geometry bodyGeometry;
	private Object3D boneAndVertex;
	private JsArray<AnimationBone> bones;
	private SkinnedMesh skinnedMesh;
	
	private void parseBVH(String bvhText){
		final BVHParser parser=new BVHParser();
		
		parser.parseAsync(bvhText, new ParserListener() {
			
			

			

			

			@Override
			public void onSuccess(BVH bv) {
				
				
				bvh=bv;
				//bvh.setSkips(10);
				//bvh.setSkips(skipFrames);
				AnimationBoneConverter converter=new AnimationBoneConverter();
				bones = converter.convertJsonBone(bvh);
				
				indexWeightEditor.setBones(bones);
				GWT.log("parsed");
				/*
				
				
				for(int i=0;i<bones.length();i++){
					Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5), THREE.MeshLambertMaterial().color(0x0000ff).build());
					scene.add(mesh);
					JsArrayNumber pos=bones.get(i).getPos();
					mesh.setPosition(pos.get(0),pos.get(1),pos.get(2));
				}
				*/
				
				
				AnimationDataConverter dataConverter=new AnimationDataConverter();
				final AnimationData data=dataConverter.convertJsonAnimation(bvh);
				animationName=data.getName();
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
				
				/*
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
				
				
				List<Vector3> parentPos=new ArrayList<Vector3>();
				parentPos.add(THREE.Vector3());
				
				for(int j=1;j<bones.length();j++){
					Geometry cbone=THREE.CubeGeometry(1, 1, 1);
					AnimationBone ab=bones.get(j);
					Vector3 pos=AnimationBone.jsArrayToVector3(ab.getPos());
					pos.addSelf(parentPos.get(ab.getParent()));
					parentPos.add(pos);
					
					Matrix4 m4=THREE.Matrix4();
					m4.setPosition(pos);
					cbone.applyMatrix(m4);
					GeometryUtils.merge(cube, cbone);
					for(int i=0;i<cbone.vertices().length();i++){
						Vector4 v4=THREE.Vector4();
						v4.set(j, j, 0, 0);
					//	v4.set(0, 0, 0, 0);
						indices.push(v4);
						
						Vector4 v4w=THREE.Vector4();
						v4w.set(1, 0, 0, 0);
						weight.push(v4w);
					}
				}
				
				
				log("cube");
				log(cube);
				
				cube.setSkinIndices(indices);
				cube.setSkinWeight(weight);
				
				cube.setBones(bones);
				*/
				 
				 
				final List<Vector3> bonePositions=new ArrayList<Vector3>();
				for(int j=0;j<bones.length();j++){
					AnimationBone bone=bones.get(j);
					Vector3 pos=AnimationBone.jsArrayToVector3(bone.getPos());
					if(bone.getParent()!=-1){
						pos.addSelf(bonePositions.get(bone.getParent()));
					}
					bonePositions.add(pos);
				}
				
				AnimationHandler.add(data);
				log(data);
				log(bones);
				
				JSONArray array=new JSONArray(bones);
				//log(array.toString());
				
				JSONObject test=new JSONObject(data);
				//log(test.toString());
				
				root=THREE.Object3D();
				scene.add(root);
				
				boneAndVertex = THREE.Object3D();
				scene.add(boneAndVertex);
				Object3D bo=boneToCube(bones);
				//bo.setPosition(-30, 0, 0);
				boneAndVertex.add(bo);
				
				
				log("before create");
				//Mesh mesh=THREE.Mesh(cube, THREE.MeshLambertMaterial().skinning(false).color(0xff0000).build());
				log("create-mesh");
				//scene.add(mesh);
				
				
				
				
				JSONLoader loader=THREE.JSONLoader();
				loader.load("men3.js", new  LoadHandler() {
					


					

					@Override
					public void loaded(Geometry geometry) {
						loadedGeometry=geometry;
						bodyGeometry=GeometryUtils.clone(geometry);
						bodyIndices = (JsArray<Vector4>) JsArray.createArray();
						bodyWeight = (JsArray<Vector4>) JsArray.createArray();
						for(int i=0;i<geometry.vertices().length();i++){
							/*
							int index=findNear(bonePositions,geometry.vertices().get(i).getPosition());
							Vector4 v4=THREE.Vector4();
							v4.set(index, index, 0, 0);
							indices.push(v4);
							
							Vector4 v4w=THREE.Vector4();
							v4w.set(1, 0, 0, 0);
							weight.push(v4w);
							*/
							//Vector4 ret=findNearDouble(bonePositions,geometry.vertices().get(i).getPosition(),1.2);
							//Vector4 ret=findNearParent(bonePositions,geometry.vertices().get(i).getPosition(),bones);
							Vector4 ret=findNearParentAndChildren(bonePositions,geometry.vertices().get(i).getPosition(),bones);
							
							Vector4 v4=THREE.Vector4();
							v4.set(ret.getX(), ret.getY(), 0, 0);
							bodyIndices.push(v4);
							
							Vector4 v4w=THREE.Vector4();
							v4w.set(ret.getZ(), ret.getW(), 0, 0);
							bodyWeight.push(v4w);
						}
						
						
						
						createSkinnedMesh();
						
						
						Mesh wireBody=THREE.Mesh(bodyGeometry, THREE.MeshBasicMaterial().wireFrame(true).color(0xffffff).build());
						boneAndVertex.add(wireBody);
						
						selectVertex=THREE.Object3D();
						vertexs.clear();
						boneAndVertex.add(selectVertex);
						Geometry cube=THREE.CubeGeometry(.3, .3, .3);
						Material mt=THREE.MeshBasicMaterial().color(0xffff00).build();
						for(int i=0;i<bodyGeometry.vertices().length();i++){
							Vector3 vx=bodyGeometry.vertices().get(i).getPosition();
							Mesh point=THREE.Mesh(cube, mt);
							point.setName("point:"+i);
							point.setPosition(vx);
							selectVertex.add(point);
							vertexs.add(point);
						}
					}
				});
			}
			
			@Override
			public void onFaild(String message) {
				log(message);
			}
		});
	}
	private String animationName;
	private Geometry loadedGeometry;
	private void createSkinnedMesh(){
		Geometry newgeo=GeometryUtils.clone(loadedGeometry);
		newgeo.setSkinIndices(bodyIndices);
		newgeo.setSkinWeight(bodyWeight);
		newgeo.setBones(bones);
		if(skinnedMesh!=null){
			root.remove(skinnedMesh);
		}
		skinnedMesh = THREE.SkinnedMesh(newgeo, THREE.MeshBasicMaterial().skinning(true).color(0xffffff).map(ImageUtils.loadTexture("men3_anime_texture.png")).build());
		root.add(skinnedMesh);
		
		if(animation!=null){
			AnimationHandler.removeFromUpdate(animation);
		}
		animation = THREE.Animation( skinnedMesh, animationName );
		animation.play();
	}
	
	private List<Mesh> vertexs=new ArrayList<Mesh>();

	private IndexAndWeightEditor indexWeightEditor;
	
	//simple way
	private int findNear(List<Vector3> bonePositions,Vector3 pos){
		Vector3 pt=THREE.Vector3();
		Vector3 near=pt.sub(bonePositions.get(0),pos);
		int index=0;
		double length=pt.length();
		for(int i=1;i<bonePositions.size();i++){
			Vector3 npt=THREE.Vector3();
			near=npt.sub(bonePositions.get(i),pos);
			double l=near.length();
			if(l<length){
				index=i;
				length=l;
			}
		}
		
		return index;
	}

	
	private Map<Integer,Integer> createChildMap(JsArray<AnimationBone> bones){
		Map<Integer,Integer> childMap=new HashMap<Integer,Integer>();
		for(int i=0;i<bones.length();i++){
			
			List<Integer> children=new ArrayList<Integer>();
			for(int j=0;j<bones.length();j++){
				AnimationBone child=bones.get(j);
				if(child.getParent()==i){
					children.add(j);
				}
			}
			if(children.size()==1){
				childMap.put(i, children.get(0));
			}
		}
		return childMap;
	}
	private Vector4 findNearParentAndChildren(List<Vector3> bonePositions, Vector3 pos,JsArray<AnimationBone> bones){
		
		Map<Integer,Integer> cm=createChildMap(bones);
		
		Vector3 pt=THREE.Vector3();
		Vector3 near=pt.sub(bonePositions.get(0),pos);
		int index=0;
		double length=pt.length();
		for(int i=1;i<bonePositions.size();i++){
			Vector3 npt=THREE.Vector3();
			near=npt.sub(bonePositions.get(i),pos);
			double l=near.length();
			if(l<length){
				index=i;
				length=l;
			}
			
		}
		
		if(index!=0){
			Vector3 parentPt=THREE.Vector3();
			int parentIndex=bones.get(index).getParent();
			parentPt=parentPt.sub(bonePositions.get(parentIndex),pos);
			double plength=parentPt.length();
			
			Integer child=cm.get(index);
			if(child!=null){
				Vector3 childPt=THREE.Vector3();
				childPt=childPt.sub(bonePositions.get(child),pos);
				double clength=childPt.length();
				if(clength<plength){
					double total=length+clength;
					return THREE.Vector4(index,child,(total-length)/total,(total-clength)/total);
				}else{
					double total=length+plength;
					return THREE.Vector4(index,parentIndex,(total-length)/total,(total-plength)/total);
				}
				
				
			}else{
			double total=length+plength;
			return THREE.Vector4(index,parentIndex,(total-length)/total,(total-plength)/total);
			}
		}else{
			return THREE.Vector4(index,index,1,0);//root
		}
		
		
	}
	
	private Vector4 findNearParent(List<Vector3> bonePositions, Vector3 pos,JsArray<AnimationBone> bones){
		Vector3 pt=THREE.Vector3();
		Vector3 near=pt.sub(bonePositions.get(0),pos);
		int index=0;
		double length=pt.length();
		for(int i=1;i<bonePositions.size();i++){
			Vector3 npt=THREE.Vector3();
			near=npt.sub(bonePositions.get(i),pos);
			double l=near.length();
			if(l<length){
				index=i;
				length=l;
			}
			
		}
		
		if(index!=0){
			Vector3 parentPt=THREE.Vector3();
			int parentIndex=bones.get(index).getParent();
			parentPt=parentPt.sub(bonePositions.get(parentIndex),pos);
			double plength=parentPt.length();
			
			double total=length+plength;
			return THREE.Vector4(index,parentIndex,(total-length)/total,(total-plength)/total);
		}else{
			return THREE.Vector4(index,index,1,0);//root
		}
		
		
	}
	
	//second choice
	private Vector4 findNearDouble(List<Vector3> bonePositions, Vector3 pos,double value){
		Vector3 pt=THREE.Vector3();
		Vector3 near=pt.sub(bonePositions.get(0),pos);
		int index1=0;
		double near1=pt.length();
		int index2=0;
		double near2=pt.length();
		
		
		for(int i=1;i<bonePositions.size();i++){
			Vector3 npt=THREE.Vector3();
			near=npt.sub(bonePositions.get(i),pos);
			double l=near.length();
			if(l<near1){
				int tmp=index1;
				double tmpL=near1;
				index1=i;
				near1=l;
				if(tmpL<near2){
					index2=tmp;
					near2=tmpL;
				}
			}
		}
		if(index1==index2){
			return THREE.Vector4(index1,index1,1,0);
		}else if(near2>near1*value){
			//too fa near2
			return THREE.Vector4(index1,index1,1,0);
		}else{
			double total=near1+near2;
			return THREE.Vector4(index1,index2,near1/total,near2/total);
		}
	}
	
}
