package com.akjava.gwt.modelweight.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHNode;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.bvh.client.Vec3;
import com.akjava.gwt.bvh.client.threejs.AnimationBoneConverter;
import com.akjava.gwt.bvh.client.threejs.AnimationDataConverter;
import com.akjava.gwt.bvh.client.threejs.BVHConverter;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.extra.HTML5Builder;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropVerticalPanelBase;
import com.akjava.gwt.lib.client.IStorageControler;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.modelweight.client.morphmerge.MorphMergeToolPanel;
import com.akjava.gwt.modelweight.client.uvpack.UvPackToolPanel;
import com.akjava.gwt.modelweight.client.weight.GWTWeightData;
import com.akjava.gwt.modelweight.client.weight.WeighDataParser;
import com.akjava.gwt.three.client.examples.animation.Animation;
import com.akjava.gwt.three.client.examples.animation.AnimationHandler;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.TrackballControls;
import com.akjava.gwt.three.client.examples.utils.GeometryUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationData;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.gwt.collada.ColladaData;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.gwt.materials.LineBasicMaterialParameter;
import com.akjava.gwt.three.client.gwt.materials.MeshLambertMaterialParameter;
import com.akjava.gwt.three.client.java.JClock;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.java.animation.WeightBuilder;
import com.akjava.gwt.three.client.java.ui.SimpleTabDemoEntryPoint;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.Camera;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.core.Raycaster;
import com.akjava.gwt.three.client.js.extras.ImageUtils;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.loaders.JSONLoader;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.math.Euler;
import com.akjava.gwt.three.client.js.math.Ray;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CopyOfGWTModelWeight extends SimpleTabDemoEntryPoint{
	public static final String version="6(for r74)";//for three.js r74
	
	
	double baseScale=0.5;//it's better if i can modify this.//TODO move outside ?
	private double posScale=0.1*baseScale;
	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		
		if(trackballControls!=null){
			trackballControls.update();
			//LogUtils.log(camera.getUuid());
		}
		
		if(root!=null){
			
		//	boneAndVertex.getRotation().set(Math.toRadians(rotX),Math.toRadians(rotY),0,Euler.XYZ);
		//	boneAndVertex.getPosition().set(posX,posY,0);
			
			
			root.setPosition(posScale*positionXRange.getValue(), posScale*positionYRange.getValue(), posScale*positionZRange.getValue());
			root.getRotation().set(Math.toRadians(rotationXRange.getValue()),Math.toRadians(rotationYRange.getValue()),Math.toRadians(rotationZRange.getValue()),Euler.XYZ);
			}
		
		long delta=clock.delta();
		//LogUtils.log(""+animation.getCurrentTime());
		double v=(double)delta/1000;
		if(!paused && animation!=null){
			AnimationHandler.update(v);
			currentTime=animation.getCurrentTime();
			//LogUtils.log("animation:"+currentTime+","+delta);
			String check=""+currentTime;
			if(check.equals("NaN")){
				currentTime=0;
			}
			if(currentTime<0.25){
				//animation.setCurrentTime(0.25);
				//AnimationHandler.update(v);
				//skinnedMesh.setVisible(false);
			}else{
				skinnedMesh.setVisible(true);
			}
		}else{
			if(animation!=null){
			currentTime=animation.getCurrentTime();
			
			}
		}
		//
	}

	double currentTime;
	
	private IStorageControler storageControler;
	private JClock clock=new JClock();

	private Mesh mouseClickCatcher;
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		cameraZ=3;
		cameraY=0;
		
		//Window.open("text/plain:test.txt:"+url, "test", null);
		
		storageControler = new StorageControler();
		canvas.setClearColor(0x333333);//canvas has margin?
		
		
		//scene.add(THREE.AmbientLight(0xffffff));
		
		Light pointLight = THREE.DirectionalLight(0xffffff,1);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		
		Light pointLight2 = THREE.DirectionalLight(0xffffff,1);//for fix back side dark problem
		pointLight2.setPosition(0, 10, -300);
		scene.add(pointLight2);
		
		//projector=THREEExp.Projector();
		
		//TODO is this really need?
		mouseClickCatcher=THREE.Mesh(THREE.PlaneGeometry(100, 100, 10, 10),
				
				THREE.MeshBasicMaterial(GWTParamUtils.MeshBasicMaterial().color(0xffff00).wireframe(true).visible(false)));
		mouseClickCatcher.setVisible(true); //now Ray only check visible object and to hide use material's visible
		
		scene.add(mouseClickCatcher);
		/*
		//write test
		Geometry g=THREE.CubeGeometry(1, 1, 1);
		LogUtils.log(g);
		
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
		LogUtils.log(js.toString());
		
		scene.add(THREE.AmbientLight(0x888888));
		Light pointLight = THREE.PointLight(0xffffff);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		*/
		
		//loadBVH("14_08.bvh");
		/*
		JSONLoader loader=THREE.JSONLoader();
		loader.load("buffalo.js", new LoadHandler() {
			
			@Override
			public void loaded(Geometry geometry) {
				AnimationHandler.add(geometry.getAnimation());
				LogUtils.log(geometry.getBones());
				LogUtils.log(geometry.getAnimation());
				//JSONObject test=new JSONObject(geometry.getAnimation());
				//LogUtils.log(test.toString());
				
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
				
				//Animation animation = THREE.Animation( mesh, "take_001" );
				//LogUtils.log(animation);
				//animation.play(); //buffalo
				
				
			}
		});*/
		
		//stop initial load for test
		if(debugTab){
		LogUtils.log("debug tab mode:main widget not work because of lack of base bvh");	
		
		}else{//this action generate skinned mesh and this freeze on debug-mode
			//loadBVH(bvhUrl);
			loadModel(modelUrl,
			new  JSONLoadHandler() {
				//loader.load("men3smart.js", new  LoadHandler() {
					@Override
					public void loaded(Geometry geometry,JsArray<Material> materials) {
						loadedGeometry=geometry;
						onModelLoaded(modelUrl,geometry);
						//createSkinnedMesh();
					}
				
				});
			
		}
		//loadBVH("pose.bvh");//no motion
		
		nearCamera=0.01;
		updateCamera(scene,screenWidth , screenHeight);
		
		
		camera.getPosition().set(cameraX, cameraY, cameraZ);
		
		trackballControls=THREEExp.TrackballControls(camera);
		trackballControls.setNoZoom(false);//controls.noZoom = false;
		trackballControls.setNoPan(false);//controls.noPan = false;
		trackballControls.setRotateSpeed(10);
		trackballControls.setTarget(THREE.Vector3());
		autoUpdateCameraPosition=false;
	}
	
	private boolean debugTab=false;
	
	//private PopupPanel bottomPanel;//TODO future
	private void createTabs(){
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int selection=event.getSelectedItem();
				if(selection==0){
					stats.setVisible(true);
					showControl();
					//bottomPanel.setVisible(true);
					popupPanel.setVisible(true);
					resized(screenWidth,screenHeight);//for some blackout;
				}else{
				stats.setVisible(false);
				//bottomPanel.setVisible(false);
				hideControl();
				if(popupPanel!=null){//debug mode call this
					popupPanel.setVisible(false);
					}
				}
				
			}
		});
		
		infoPanel = new InfoPanelTab();
		infoPanel.setSize("100%", "100%");
		tabPanel.add(infoPanel,"Info");
		
		tabPanel.add(new CopyToolPanel(),"Copy");
		tabPanel.add(new MergeToolPanel(),"Merge");
		tabPanel.add(new ConvertToolPanel(),"Convert");
		
		tabPanel.add(new UvPackToolPanel(),"UvPack");
		tabPanel.add(new MorphMergeToolPanel(),"MorphMerge");
		//for special debug
		if(debugTab){
			
			}
		tabPanel.selectTab(0);
	}
	
	
	
	
	Object3D root;
	
	//for selection
	private final int boneJointColor=0x888888;
	private final int boneCoreColor=0x008800;
	
	private final int selectBoneJointColor=0xeeddee;
	private final int selectBoneCoreColor=0xee88ee;
	List<Mesh> boneJointMeshs=new ArrayList<Mesh>();
	List<Mesh> boneCoreMeshs=new ArrayList<Mesh>();
	private List<Mesh> endPointMeshs=new ArrayList<Mesh>();
	
	List<Mesh> tmp=new ArrayList<Mesh>();
	
	private String attachGeometryUrl="";
	private Object3D createBoneObjects(BVH bvh){
		boneJointMeshs.clear();
		
		endPointMeshs.clear();
		AnimationBoneConverter converter=new AnimationBoneConverter();
		JsArray<AnimationBone> bones = converter.convertJsonBone(bvh);//has no endsite
		
		List<List<Vector3>> endSites=converter.convertJsonBoneEndSites(bvh);
		tmp.clear();
		Object3D group=THREE.Object3D();
		
		//Vec3 rootBoneOffset=bvh.getHiearchy().getOffset();
		//group.getPosition().set(rootBoneOffset.getX(),rootBoneOffset.getY(), rootBoneOffset.getZ());
		
		double boneCoreSize=0.03*baseScale;
		double halfBonecore=0.02*baseScale;
		double sitesBonecore=0.02*baseScale;
		
		for(int i=0;i<bones.length();i++){
			AnimationBone bone=bones.get(i);
			Geometry cube=THREE.BoxGeometry(boneCoreSize, boneCoreSize, boneCoreSize);
			
			
			Mesh mesh=THREE.Mesh(cube, THREE.MeshLambertMaterial(MeshLambertMaterialParameter.create().color(boneCoreColor)));
			group.add(mesh);
			boneCoreMeshs.add(mesh);
			Vector3 pos=GWTThreeUtils.jsArrayToVector3(bone.getPos());
			
			if(bone.getParent()!=-1){
				
				Vector3 half=pos.clone().multiplyScalar(.5);
				
				
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				pos.add(ppos);
				
				half.add(ppos);
				
				double length=ppos.clone().sub(pos).length();
				
				//half
				Mesh halfMesh=THREE.Mesh(THREE.BoxGeometry(halfBonecore, halfBonecore, length), THREE.MeshLambertMaterial(MeshLambertMaterialParameter.create().color(boneJointColor)));
				group.add(halfMesh);
				halfMesh.setPosition(half);
				halfMesh.lookAt(pos);
				halfMesh.setName(bones.get(bone.getParent()).getName());
				
				boneJointMeshs.add(halfMesh);
			}
			mesh.setPosition(pos);
			mesh.setName(bone.getName());
			
			//this mesh is for helping auto-weight
			List<Vector3> sites=endSites.get(i);
			for(Vector3 end:sites){
				Mesh endMesh=THREE.Mesh(THREE.BoxGeometry(sitesBonecore, sitesBonecore, sitesBonecore), THREE.MeshLambertMaterial(MeshLambertMaterialParameter.create().color(0x00a00aa)));
				if(end.getX()==0 && end.getY()==0 && end.getZ()==0){
					continue;//ignore 0 
				}else{
					//LogUtils.log(bone.getName()+":"+ThreeLog.get(end));
				}
				Vector3 epos=end.clone().add(pos);
				endMesh.setPosition(epos);
				group.add(GWTGeometryUtils.createLineMesh(pos, epos, 0x888888));
				group.add(endMesh);
				endPointMeshs.add(endMesh);	
			}
			
			
			if(bone.getParent()!=-1){
				//AnimationBone parent=bones.get(bone.getParent());
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				Object3D line=THREE.Line(GWTGeometryUtils.createLineGeometry(pos, ppos), THREE.LineBasicMaterial(LineBasicMaterialParameter.create().color(0x888888)));
				group.add(line);
			}
			tmp.add(mesh);
		}
		return group;
	}
	
	/*
	private List<NameAndPosition> boneToNameAndWeight(JsArray<AnimationBone> bones){
		return boneToNameAndWeight(bones,null);
	}
	private List<NameAndPosition> boneToNameAndWeight(JsArray<AnimationBone> bones,List<List<Vector3>> endSites){
		List<NameAndPosition> lists=new ArrayList<NameAndPosition>();
		List<Vector3> absolutePos=new ArrayList<Vector3>();
		for(int i=0;i<bones.length();i++){
			AnimationBone bone=bones.get(i);
			
			Vector3 pos=GWTThreeUtils.jsArrayToVector3(bone.getPos());
			String parentName=null;
			//add start
			//add center
			Vector3 parentPos=null;
			Vector3 endPos=null;
			int parentIndex=0;
			if(bone.getParent()!=-1){
				parentIndex=bone.getParent();
				parentName=bones.get(parentIndex).getName();
				
				parentPos=absolutePos.get(parentIndex);
				
				if(pos.getX()!=0 || pos.getY()!=0 || pos.getZ()!=0){
				endPos=pos.clone().multiplyScalar(.9).addSelf(parentPos);
				Vector3 half=pos.clone().multiplyScalar(.5).addSelf(parentPos);
				
				lists.add(new NameAndPosition(parentName,endPos,parentIndex));//start pos
				lists.add(new NameAndPosition(parentName,half,parentIndex));//half pos
				
				Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.2, .2, .2), THREE.MeshLambertMaterial().color(0x00ff00).build());
				mesh.setName(parentName);
				mesh.setPosition(endPos);
				//boneAndVertex.add(mesh);
				
				Mesh mesh2=THREE.Mesh(THREE.CubeGeometry(.3, .3, .2), THREE.MeshLambertMaterial().color(0x00ff00).build());
				mesh2.setName(parentName);
				mesh2.setPosition(half);
				//boneAndVertex.add(mesh2);
				}else{
					
				}
			}
			//add end
			
			if(parentPos!=null){
				pos.addSelf(parentPos);
			}
			absolutePos.add(pos);
			
			Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5), THREE.MeshLambertMaterial().color(0x00ff00).build());
			mesh.setName(bone.getName());
			mesh.setPosition(pos);
			
			if(endSites!=null){
				List<Vector3> ends=endSites.get(i);
				for(Vector3 endSite:ends){
					lists.add(new NameAndPosition(bone.getName(),pos.clone().addSelf(endSite),i));//
				}
			}
			//boneAndVertex.add(mesh);
			
			if(parentPos!=null){
				Mesh line=GWTGeometryUtils.createLineMesh(parentPos, pos, 0xff0000);
				//boneAndVertex.add(line);
			}
			
			
			lists.add(new NameAndPosition(bone.getName(),pos,i));//end pos
		}
		return lists;
	}*/
	
	
	//private Projector projector;
	Label debugLabel;
	List<Integer> selections=new ArrayList<Integer>();

	private Object3D selectedObject;
	private Vector3 offset=THREE.Vector3();
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		super.onMouseUp(event);
		selectedObject=null;
	}
	
	//TODO method and move
	private JsArray<Intersect> pickIntersects(double mx,double my,double sw,double sh,Camera camera,JsArray<? extends Object3D> objects){
		Vector3 screenPosition=THREE.Vector3(( mx / sw ) * 2 - 1, - ( my / sh ) * 2 + 1,1 );//no idea why 0.5
		screenPosition.unproject(camera);
		Raycaster ray=THREE.Raycaster(camera.getPosition(), screenPosition.sub( camera.getPosition() ).normalize());
	
		JsArray<Intersect> intersects=ray.intersectObjects(objects);
		return intersects;
	}
	
	public final native Ray gwtCreateRay(int mx,int my,int sw,int sh,Camera camera)/*-{

	var vector = new $wnd.THREE.Vector3( ( mx / sw ) * 2 - 1, - ( my / sh ) * 2 + 1, 0.5 );
				vector.unproject(camera );

				var ray = new $wnd.THREE.Raycaster( camera.position, vector.sub( camera.position ).normalize() );

				return  ray;

	}-*/;
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		super.onMouseDown(event);
		int x=event.getX();
		int y=event.getY();
		/*
		if(inEdge(x,y)){
			screenMove(x,y);
			return;
		}*/
		JsArray<Intersect> intersects=pickIntersects(event.getX(), event.getY(), screenWidth, screenHeight, camera,objects);
		
		for(int i=0;i<intersects.length();i++){
			Intersect sect=intersects.get(i);
			
			Object3D target=sect.getObject();
			//LogUtils.log(target);
			if(!target.getName().isEmpty()){//only point: and bone name
				if(target.getName().startsWith("point:")){//never happen no far
					if(!target.isVisible()){
						//not selected bone
						continue;
					}
					
					//changeBoneSelectionColor(null);
					
					String[] pv=target.getName().split(":");
					int at=Integer.parseInt(pv[1]);
					
					if(event.isShiftKeyDown()){
						if(selections.contains(at)){
							selections.remove(new Integer(at));
							//TODO cast material
							vertexMeshs.get(at).getMaterial().gwtGetColor().setHex(getVertexColor(at));
							//??
							if(lastSelection==at){
								if(selections.size()>0){
									//select last
									selectVertex(selections.get(selections.size()-1));
								}else{
									indexWeightEditor.setAvailable(false);
									updateWeightButton.setEnabled(false);
								}
							}
						}else{
							//plus
							if(lastSelection!=-1){
								selections.add(lastSelection);
								}
							selectVertex(at);
						}
						for(int index:selections){
							vertexMeshs.get(index).getMaterial().gwtGetColor().setHex(selectColor);
						}
					}else{
						//clear cache
						clearBodyPointSelections();
						
						
						selectVertex(at);
					}
					
					
					return;
				}else{//must be bone
					if(event.isShiftKeyDown()){
						continue;
					}
					
					selectedObject=target;
					Ray ray=gwtCreateRay(x, y, screenWidth, screenHeight, camera);
					
					
					
					
					mouseClickCatcher.getPosition().copy( GWTThreeUtils.toPositionVec(target.getMatrixWorld()) );
					//mouseClickCatcher.getPosition().copy( target.getPosition() );
					mouseClickCatcher.updateMatrixWorld(true);//very important
					
					
					//mouseClickCatcher.lookAt(camera.getPosition());
					
					JsArray<Intersect> pintersects=ray.intersectObject(mouseClickCatcher);
					if(pintersects.length()==0){
						LogUtils.log("some how empty result:ray.intersectObject(mouseClickCatcher)");
						return;
					}
					//LogUtils.log("plain:"+ThreeLog.get(pintersects.get(0).getPoint()));
					offset.copy(pintersects.get(0).getPoint()).sub(mouseClickCatcher.getPosition());
					
					
					
				clearBodyPointSelections();
				
				selectBone(target);
				break;
				}
				
			}
			
		}
		//selectionMesh.setVisible(false);
		//TODO clear selection
	}
	private void clearBodyPointSelections(){
		for(int index:selections){
			vertexMeshs.get(index).getMaterial().gwtGetColor().setHex(getVertexColor(index));
		}
		selections.clear();
	}
	
	private int getVertexColor(int vertexIndex){
		return getVertexColor(vertexIndex,selectionBoneIndex);
	}
	//4 point supported
	private int getVertexColor(int vertexIndex,int boneIndex){
		/*
		double index1=bodyIndices.get(vertexIndex).getX();//first one
		boolean isIndex1=false;
		
		if(index1==boneIndex){
			isIndex1=true;
		}
		*/
		
		double v=0;
		
		Vector4 indices=bodyIndices.get(vertexIndex);
		for(int i=0;i<4;i++){
			if(indices.gwtGet(i)==boneIndex){
				v+=bodyWeight.get(vertexIndex).gwtGet(i);
			}
		}
		
		/*
		if(isIndex1){
			v=bodyWeight.get(vertexIndex).getX();
		}else{
			v=bodyWeight.get(vertexIndex).getY();
		}
		
		//if both index is same value is 1
		if(bodyIndices.get(vertexIndex).getX()==bodyIndices.get(vertexIndex).getY()){
			v=1;
		}
		*/
		
		
		if(v==1){
			return 0xffffff;
		}else if(v==0){
			return 0;
		}
		
		int[] cv=toColorByDouble(v);
		
		int color=ColorUtils.toColor(cv);
		return color;
		/*
		if(index==318){
			LogUtils.log("isIndex1:"+isIndex1+",index="+index1+",selectBone="+selectionBoneIndex+",v="+v);
		}
		if(v==1){
			return 0xfffefe;
		}else if(v>0.949){
			return 0xff0000;
		}else if(v>0.849){
			return 0xff977f;
		}else if(v>0.749){
			return 0xffb87f;
		}else if(v>0.649){
			return 0xffd67f;
		}
		else if(v>0.549){
			return 0xfff17f;
		}
		else if(v>0.449){
			return 0xffff00;
		}else if(v>0.349){
			return 0xafff7f;
		}else if(v>0.249){
			return 0x82ff7f;
		}else if(v>0.149){
			return 0x7fffbb;
		}else if(v>0.05){
			return 0x7fffee;
		}else if(v==0){
			return 0;
		}else{
			return 0x7fcaff;
		}
		*/
	}
	

	private Mesh boneSelectionMesh;
	private int selectionBoneIndex;
	private Object3D selectVertex;
	
	/**
	 * change bone colors to detect which one selected.
	 * when point selected ,all clear bone selection
	 * @param boneName
	 */
	private void changeBoneSelectionColor(String boneName){
		for(Mesh mesh:boneCoreMeshs){
			if(boneName==null || !boneName.equals(mesh.getName())){
				GWTThreeUtils.changeMeshMaterialColor(mesh, boneCoreColor);
			}else{
				GWTThreeUtils.changeMeshMaterialColor(mesh, selectBoneCoreColor);
			}
		}
		for(Mesh mesh:boneJointMeshs){
			if(boneName==null || !boneName.equals(mesh.getName())){
				GWTThreeUtils.changeMeshMaterialColor(mesh, boneJointColor);
			}else{
				GWTThreeUtils.changeMeshMaterialColor(mesh, selectBoneJointColor);
			}
		}
	}
	
	private void selectBone(Object3D target) {
		
		lastSelection=-1;
		//right now off boneSelectionMesh
		double boneSelectionSize=0.1*baseScale;
		if(boneSelectionMesh==null){
			boneSelectionMesh=THREE.Mesh(THREE.BoxGeometry(boneSelectionSize, boneSelectionSize, boneSelectionSize), THREE.MeshLambertMaterial(
					GWTParamUtils.MeshLambertMaterial().color(0x00ff00)
					));
			boneAndVertex.add(boneSelectionMesh);
		}else{
			
		}
		
		boneSelectionMesh.setVisible(false);//temporaly
		boneSelectionMesh.setPosition(target.getPosition());
		
		changeBoneSelectionColor(target.getName());
		
		
		selectionBoneIndex=findBoneIndex(target.getName());
		
		boneListBox.setSelectedIndex(selectionBoneIndex);
		selectVertexsByBone(selectionBoneIndex);
		
		int selectionIndex=indexWeightEditor.getArrayIndex();
		
		
		if(vertexMeshs.size()<=selectionIndex){
			LogUtils.log("some how vertexMesh index problem:"+vertexMeshs.size());
			return;
		}
		
		if(selectionIndex!=-1 && !vertexMeshs.get(selectionIndex).isVisible()){
			indexWeightEditor.setAvailable(false);
			updateWeightButton.setEnabled(false);
			boneSelectionIndicateBoxMesh.setVisible(false);
		}
		
	}
	private void selectVertexsByBone(int selectedBoneIndex) {
		
		for(int i=0;i<wireBody.getGeometry().getFaces().length();i++){
			Face3 face=wireBody.getGeometry().getFaces().get(i);
			//LogUtils.log("color-size:"+face.getVertexColors().length());
			for(int j=0;j<3;j++){
				int vertexIndex=face.gwtGet(j);
				Vector4 index=bodyIndices.get(vertexIndex);
				int color=getVertexColor(vertexIndex);
				if(index.getX()==selectedBoneIndex || index.getY()==selectedBoneIndex){//TODO convert 4point
					//LogUtils.log("indexed:"+color);
				}
				
				//LogUtils.log("set color:"+i+","+j);
				
				//http://stackoverflow.com/questions/37358419/how-to-update-colors-after-vertex-colors-are-changed/37368746#37368746
				//can't replace color object
				//face.getVertexColors().set(j, THREE.Color(color));//
				face.getVertexColors().get(j).setHex(color);
			}
		}
		wireBody.getGeometry().setColorsNeedUpdate(true);
		
		
		/*
		//LogUtils.log("selectVertexsByBone");
		for(int i=0;i<bodyGeometry.vertices().length();i++){
			Vector4 index=bodyIndices.get(i);
			Mesh mesh=vertexMeshs.get(i);
			if(index.getX()==selectedBoneIndex || index.getY()==selectedBoneIndex){
				mesh.setVisible(true);
				mesh.getMaterial().gwtGetColor().setHex(getVertexColor(i));
				//mesh.getMaterial().getColor().setHex(getVertexColor(i));
				//LogUtils.log(weight.getX()+","+weight.getY());
			}else{
				mesh.setVisible(false);
				mesh.getMaterial().gwtGetColor().setHex(getVertexColor(i));
			}
		}
		*/
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
	double posX;
	double posY=0;//TODO check bounds and center
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(useTrackball){
			return;
		}
		
		/*
		if(selectedObject!=null && event.getNativeButton()==NativeEvent.BUTTON_MIDDLE){
			
			Ray ray=projector.gwtCreateRay(event.getX(), event.getY(), screenWidth, screenHeight, camera);
			JsArray<Intersect> intersects = ray.intersectObject( mouseClickCatcher );
			
			
			Vector3 newPos=intersects.get(0).getPoint().subSelf( offset );
			
			Matrix4 rotM=THREE.Matrix4();
			rotM.getInverse(selectedObject.getMatrixRotationWorld());
			rotM.multiplyVector3(newPos);		
			
			selectedObject.getPosition().copy( newPos);
			return;
		}*/
		
		
		
		
		if(mouseDown){
			
			int diffX=event.getX()-mouseDownX;
			int diffY=event.getY()-mouseDownY;
			mouseDownX=event.getX();
			mouseDownY=event.getY();
			
			
			
			
			if(event.getNativeButton()==NativeEvent.BUTTON_MIDDLE){
				int newX=rotationXRange.getValue()+diffY;
				if(newX<-180){
					newX=360+newX;
				}
				if(newX>180){
					newX=360-newX;
				}
				rotationXRange.setValue(newX);
				
				int newY=rotationYRange.getValue()+diffX;
				if(newY<-180){
					newY=360+newY;
				}
				if(newY>180){
					newY=360-newY;
				}
				rotationYRange.setValue(newY);
				return;
			}
			
			
			
			if(event.isControlKeyDown()){//TODO future function
				/*
			int index=indexWeightEditor.getArrayIndex();	
			if(index!=-1){
				loadedGeometry.vertices().get(index).getPosition().incrementX(diffX);
				loadedGeometry.vertices().get(index).getPosition().incrementX(diffY);
				createSkinnedMesh();
				createWireBody();
				
			}*/
			}else if(event.isAltKeyDown()){
			posX+=(double)diffX/16*posScale;
			posY-=(double)diffY/16*posScale;
			}else{
				rotX=(rotX+diffY);
				rotY=(rotY+diffX);
			}
		}
	}
	
	private InputRangeWidget positionXRange;
	private InputRangeWidget positionYRange;
	private InputRangeWidget positionZRange;
	
	private InputRangeWidget rotationXRange;
	private InputRangeWidget rotationYRange;
	private InputRangeWidget rotationZRange;

	private CheckBox useBone;

	private Button updateIndexOnlyButton;

	private Button updateWeightOnlyButton;
	@Override
	public void createControl(DropVerticalPanelBase parent) {
		nearCamera=0.001;
		debugLabel=new Label();
		parent.add(debugLabel);
		
		stackPanel = new StackLayoutPanel(Unit.PX);
		stackPanel.setSize("220px","450px");
		parent.add(stackPanel);
		
		VerticalPanel modelPositionAndRotation=new VerticalPanel();
		modelPositionAndRotation.setWidth("100%");
		
		stackPanel.add(modelPositionAndRotation,"Model Postion&Rotation",30);
		
		
		controlBone = new CheckBox("controlBone");
		
		HorizontalPanel h1=new HorizontalPanel();
		
		rotationXRange = InputRangeWidget.createInputRange(-180,180,0);
		modelPositionAndRotation.add(HTML5Builder.createRangeLabel("X-Rotate:", rotationXRange));
		modelPositionAndRotation.add(h1);
		h1.add(rotationXRange);
		Button reset=new Button("Reset");
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationXRange.setValue(0);
			}
		});
		h1.add(reset);
		
		HorizontalPanel h2=new HorizontalPanel();
		
		rotationYRange = InputRangeWidget.createInputRange(-180,180,0);
		modelPositionAndRotation.add(HTML5Builder.createRangeLabel("Y-Rotate:", rotationYRange));
		modelPositionAndRotation.add(h2);
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
		rotationZRange = InputRangeWidget.createInputRange(-180,180,0);
		modelPositionAndRotation.add(HTML5Builder.createRangeLabel("Z-Rotate:", rotationZRange));
		modelPositionAndRotation.add(h3);
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
		positionXRange = InputRangeWidget.createInputRange(-50,50,0);
		modelPositionAndRotation.add(HTML5Builder.createRangeLabel("X-Position:", positionXRange));
		modelPositionAndRotation.add(h4);
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
		positionYRange = InputRangeWidget.createInputRange(-50,50,0);
		modelPositionAndRotation.add(HTML5Builder.createRangeLabel("Y-Position:", positionYRange));
		modelPositionAndRotation.add(h5);
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
		positionZRange = InputRangeWidget.createInputRange(-50,50,0);
		modelPositionAndRotation.add(HTML5Builder.createRangeLabel("Z-Position:", positionZRange));
		modelPositionAndRotation.add(h6);
		h6.add(positionZRange);
		Button reset6=new Button("Reset");
		reset6.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionZRange.setValue(0);
			}
		});
		h6.add(reset6);
		
		
		//move left-side
		//positionYRange.setValue(-13);
		positionXRange.setValue(-30);
		
		//really need?
		modelPositionAndRotation.add(new Label("ALT+Mouse move wire-bone"));
		modelPositionAndRotation.add(new Label("Center+Mouse rotate charactor"));
		
		pauseBt = new Button("Pause/Play SkinnedMesh");
		parent.add(pauseBt);
		pauseBt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				paused=!paused;
			}
		});
		
		frameRange = InputRangeWidget.createInputRange(0, 0, 0);
		frameRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				updateFrameRange();
			}
		});
		
		parent.add(frameRange);
		/*
		CheckBox do1small=new CheckBox("x 0.1");
		do1small.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				onTotalSizeChanged(event.getValue());
			}
		});
		parent.add(do1small);
		*/
		
		//editor
		
		VerticalPanel boneAndWeight=new VerticalPanel();
		boneAndWeight.setWidth("100%");
		stackPanel.add(boneAndWeight,"Bone & Weight",30);
		
		boneAndWeight.add(new Label("Bone Selection"));
		boneListBox = new ListBox();
		boneAndWeight.add(boneListBox);
		boneListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectVertexsByBone(boneListBox.getSelectedIndex());
			}
		});
		
		HorizontalPanel prevAndNext=new HorizontalPanel();
		boneAndWeight.add(prevAndNext);
		Button prev=new Button("Prev");
		prev.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				selectPrevVertex();
			}
		});
		prevAndNext.add(prev);
		
		Button next=new Button("Next");
		next.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				selectNextVertex();
			}
		});
		prevAndNext.add(next);
		
		indexWeightEditor = new IndexAndWeightEditor();
		indexWeightEditor.setWidth("200px");
		boneAndWeight.add(indexWeightEditor);
		
		
		updateWeightButton = new Button("Update Both");
		updateWeightButton.setWidth("100%");
		updateWeightButton.addClickHandler(new ClickHandler() {
			
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
				
				if(selections.size()>0){
					for(int selectedIndex:selections){
					in=bodyIndices.get(selectedIndex);
					we=bodyWeight.get(selectedIndex);
					in.setX(indexWeightEditor.getIndex1());
					in.setY(indexWeightEditor.getIndex2());
					we.setX(indexWeightEditor.getWeight1());
					we.setY(indexWeightEditor.getWeight2());
					}
				}
				
				//LogUtils.log("new-ind-weight:"+in.getX()+","+in.getY()+","+we.getX()+","+we.getY());
				createSkinnedMesh();
				selectVertexsByBone(selectionBoneIndex);
			}
		});
		boneAndWeight.add(updateWeightButton);
		
		HorizontalPanel update2=new HorizontalPanel();
		update2.setWidth("100%");
		boneAndWeight.add(update2);
		updateIndexOnlyButton = new Button("Update Index");
		//updateIndexOnlyButton.setWidth("50%");
		updateIndexOnlyButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int index=indexWeightEditor.getArrayIndex();
				if(index==-1){
					return;
				}
				
				Vector4 in=bodyIndices.get(index);
				in.setX(indexWeightEditor.getIndex1());
				in.setY(indexWeightEditor.getIndex2());
				
				
				if(selections.size()>0){
					for(int selectedIndex:selections){
					in=bodyIndices.get(selectedIndex);
					in.setX(indexWeightEditor.getIndex1());
					in.setY(indexWeightEditor.getIndex2());
					
					}
				}
				
				//LogUtils.log("new-ind-weight:"+in.getX()+","+in.getY()+","+we.getX()+","+we.getY());
				createSkinnedMesh();
				selectVertexsByBone(selectionBoneIndex);
			}
		});
		update2.add(updateIndexOnlyButton);
		
		updateWeightOnlyButton = new Button("Update Weight");
		//updateWeightOnlyButton.setWidth("50%");
		updateWeightOnlyButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int index=indexWeightEditor.getArrayIndex();
				if(index==-1){
					return;
				}
				
				
				Vector4 we=bodyWeight.get(index);
				
				we.setX(indexWeightEditor.getWeight1());
				we.setY(indexWeightEditor.getWeight2());
				
				if(selections.size()>0){
					for(int selectedIndex:selections){
					
					we=bodyWeight.get(selectedIndex);
					
					we.setX(indexWeightEditor.getWeight1());
					we.setY(indexWeightEditor.getWeight2());
					}
				}
				
				createSkinnedMesh();
				selectVertexsByBone(selectionBoneIndex);
			}
		});
		update2.add(updateWeightOnlyButton);
		
		
		
		boneAndWeight.add(new Label("Re Auto-Weight"));
		
		autoWeightListBox = new ListBox();
		autoWeightListBox.addItem("From Geometry", "4");
		autoWeightListBox.addItem("Half ParentAndChildrenAg", "6");
		autoWeightListBox.addItem("ParentAndChildren Agressive", "5");
		autoWeightListBox.addItem("ParentAndChildren", "3");
		autoWeightListBox.addItem("NearAgressive", "2");
		autoWeightListBox.addItem("NearSingleBon", "0");
		autoWeightListBox.addItem("NearSpecial", "1");
		autoWeightListBox.addItem("Root All", "7");
		autoWeightListBox.addItem("Makehuman SecondLife-19", ""+MakehumanWeightBuilder.MODE_MAKEHUMAN_SECOND_LIFE19);
		
		autoWeightListBox.setSelectedIndex(1);
		boneAndWeight.add(autoWeightListBox);
		
		Button weightExec=new Button("Exec Weight",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//LogUtils.log("Exec weight");
				updateAutoWeight(autoWeightListBox.getValue(autoWeightListBox.getSelectedIndex()));
				createSkinnedMesh();
			}
		});
		boneAndWeight.add(weightExec);
		
		
		//DONT need?
		useBasicMaterial = new CheckBox();
		useBasicMaterial.setText("Use Basic Material");
		//parent.add(useBasicMaterial);
		
		VerticalPanel loadAndExport=new VerticalPanel();
		stackPanel.add(loadAndExport,"Load Datas",30);
		
		
		/*
		bvhUpload.getFileUpload().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JsArray<File> files = FileUtils.toFile(event.getNativeEvent());
				
				final FileReader reader=FileReader.createFileReader();
				final File file=files.get(0);
				reader.setOnLoad(new FileHandler() {
					@Override
					public void onLoad() {
						//LogUtils.log("load:"+Benchmark.end("load"));
						//GWT.log(reader.getResultAsString());
						parseBVH(reader.getResultAsString());
						bvhSelection.setText("selection:"+file.getFileName());
					}
				});
				reader.readAsText(file,"utf-8");
				bvhUpload.reset();
			}
		});
		*/
		
		Label jsonTitleLabel=new Label("Character(Three.js model file)");
		jsonTitleLabel.setStylePrimaryName("subtitle");
		
		loadAndExport.add(jsonTitleLabel);
		modelSelection = new Label("selection:"+modelUrl);
		modelSelection.setStylePrimaryName("gray");
		loadAndExport.add(modelSelection);
		
		final FileUploadForm meshUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(final File file, String value) {
				onModelFileUploaded(file, value);
				
			}
		}, true);
		meshUpload.setShowDragOverBorder(true);
		//meshUpload.getFileUpload().setStylePrimaryName("darkgray");
		loadAndExport.add(meshUpload);
	
		//makehuman export bvh somehow large,and try to fix it ,but this make problem
		//force10x = new CheckBox("force multiple x10");
		//loadAndExport.add(force10x);
		
		useBone = new CheckBox("use bone inside model when load");
		useBone.setValue(true);
		loadAndExport.add(useBone);
		
		
		loadAndExport.add(new HTML("&nbsp;"));
		
		Label bvhTitleLabel=new Label("attach Geometry model");
		loadAndExport.add(bvhTitleLabel);
		bvhTitleLabel.setStylePrimaryName("subtitle");
		geometrySelection = new Label("selection:"+attachGeometryUrl);
		geometrySelection.setStylePrimaryName("gray");
		loadAndExport.add(geometrySelection);
		
		final FileUploadForm bvhUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				onGeometryFileUploaded(file, value);
			}
		}, true);
		bvhUpload.setShowDragOverBorder(true);
		
		
		loadAndExport.add(bvhUpload);
		
		loadAndExport.add(new HTML("&nbsp;"));
		Label textureLabel=new Label("Texture(PNG or JPEG)");
		textureLabel.setStylePrimaryName("subtitle");
		loadAndExport.add(textureLabel);
		textureSelection = new Label("selection:"+textureUrl);
		textureSelection.setStylePrimaryName("gray");
		final FileUploadForm textureUpload=FileUtils.createSingleFileUploadForm(new DataURLListener() {	
			@Override
			public void uploaded(File file, String value) {
				LogUtils.log("texture upload");
				onTextureFileUploaded(file, value);
			}
		}, true);
		textureUpload.setShowDragOverBorder(true);
		
		loadAndExport.add(textureSelection);
		loadAndExport.add(textureUpload);
		loadAndExport.add(new HTML("&nbsp;"));
		Label label2=new Label("Weights and Indices(js/json)");
		label2.setStylePrimaryName("subtitle");
		loadAndExport.add(label2);
		weightSelection = new Label("selection:");
		weightSelection.setStylePrimaryName("gray");
		final FileUploadForm weightUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(final File file, String value) {
				onWeightFileUploaded(file, value);
			}
		}, true);
		weightUpload.setShowDragOverBorder(true);
		weightUpload.setTitle("upload json model but only load weight&indecis");
		//weightUpload.getFileUpload().setStylePrimaryName("darkgray");
		
		loadAndExport.add(weightSelection);
		loadAndExport.add(weightUpload);
		
		VerticalPanel export=new VerticalPanel();
		stackPanel.add(export,"Export Datas",30);
		
		//export.add(new Label("you can load form pose editor preference"));
		Button webstorage=new Button("Export in WebStorage");
		webstorage.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				exportWebStorage();
			}
		});
		//export.add(webstorage);
		
		
		export.add(new Label("three.js model format"));
		HorizontalPanel fileNames=new HorizontalPanel();
		export.add(fileNames);
		fileNames.add(new Label("Name:"));
		saveFileBox = new TextBox();
		saveFileBox.setText("untitled.js");
		fileNames.add(saveFileBox);
		
		HorizontalPanel exports=new HorizontalPanel();
		loadAndExport.add(exports);
		Button json=new Button("Export as Json");
		json.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				exportAsJson();
			}
		});
		exports.add(json);
		withAnimation = new CheckBox("+Animation");
		withAnimation.setValue(false);	//dont work collectly <--what mean is this?
		exports.add(withAnimation);
		
		
		export.add(exports);
		
		
		exportLinks = new VerticalPanel();
		export.add(exportLinks);
		
		
		
		
		
		stackPanel.showWidget(2);//load
		
		
		//other controls
		CheckBox showBoneCheck=new CheckBox("showWireBody");
		showBoneCheck.setValue(true);
		parent.add(showBoneCheck);
		showBoneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(wireBody!=null){
					wireBody.setVisible(event.getValue());
				}
			}
		});
		
		/*
		Button makeDot=new Button("makedot",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		parent.add(makeDot);
		*/
		
		createTabs();
		//loadAndExport.add(new Label("Dont export large BVH.large(10M?) text data crash browser"));
		showControl();
	}
	
	
	protected void onTotalSizeChanged(Boolean value) {
		
		
		List<Mesh> meshs=Lists.newArrayList();
		meshs.addAll(vertexMeshs);
		meshs.addAll(boneJointMeshs);
		meshs.addAll(boneCoreMeshs);
		meshs.addAll(endPointMeshs);
		
		
		if(boneSelectionIndicateBoxMesh!=null){
		meshs.add(boneSelectionIndicateBoxMesh);
		}
		if(boneSelectionMesh!=null){
			meshs.add(boneSelectionMesh);
			}
		
		//redo-bone and vertex
		/*
		for(Mesh mesh:meshs){
			double scale=(1.0/upscale);
			mesh.getScale().set(scale,scale,scale);
		}
		*/
	}

	private boolean needFlipY=true;//default
	
	
	private void onModelLoaded(String fileName,Geometry geometry){
		LogUtils.log("onModelLoaded");
		//LogUtils.log(geometry);
		
		
		if(lastJsonObject!=null){
		JSONModelFile modelFile=(JSONModelFile) lastJsonObject.getJavaScriptObject();
		//old version 3.0 need flip-Y
		//see https://github.com/mrdoob/three.js/wiki/Migration#r49--r50
		if(modelFile.getMetaData().getFormatVersion()==3){
			needFlipY=false;
		}else{
			needFlipY=true;
		}
		}
		
		
		//set geo bone info if exists.
		List<AnimationBone> abList=new ArrayList<AnimationBone>();
		JsArray<AnimationBone> geoBones=geometry.getBones();
		if(geoBones!=null){
			for(int i=0;i<geoBones.length();i++){
				abList.add(geoBones.get(i));
			}
			
			
		}
		
		loadedGeometry=geometry;
		
		infoPanel.getGeometryAnimationObjects().setDatas(abList);
		infoPanel.getGeometryAnimationObjects().update(false);//
		
		//LogUtils.log("geometry-bone-info updated");
		
		//now always use
		if(useBone.getValue()){
			//LogUtils.log(geoBones);
			/*
			BVHConverter converter=new BVHConverter();
			LogUtils.log("geo-bone:"+geoBones);
			LogUtils.log("getBones:"+geoBones.get(0).getPos());
			BVHNode rootNode=converter.convertBVHNode(geoBones);
			LogUtils.log("rootNode:"+rootNode.getOffset());
			converter.setChannels(rootNode,0,"XYZ");	//TODO support other order
			BVH inBvh=new BVH();
			inBvh.setHiearchy(rootNode);
			LogUtils.log("1");
			
			BVHMotion motion=new BVHMotion();
			motion.setFrameTime(.25);
			*/
			
			//zero
			/*
			for(PoseFrameData pose:Lists.newArrayList(new PoseFrameData())){
				double[] values=converter.angleAndMatrixsToMotion(pose.getAngleAndMatrixs(),BVHConverter.ROOT_POSITION_ROTATE_ONLY,"XYZ");
				motion.add(values);
			}
			*/
			
			/*
			motion.add(new double[geoBones.length()*3+3]);//only root has pos,TODO find better way
			
			motion.setFrames(motion.getMotions().size());//
			LogUtils.log("2");
			inBvh.setMotion(motion);
			
			BVHWriter writer=new BVHWriter();
			
			String bvhText=writer.writeToString(inBvh);
			*/
			
			//because this time not channel setted,TODO
			setBvh(new BVHConverter().createBVH(geoBones));
			/*
			try {
				setBvh(new BVHParser().parse(bvhText));
			} catch (InvalidLineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
		
		initializeObject();
		
		
		
		//each time model loaded,check model format and set flipY
		if(texture!=null){
			if(!needFlipY){
				LogUtils.log("texture flipY:false for old 3.0 format");
			}
			texture.setFlipY(needFlipY);
			texture.setNeedsUpdate(true);
		}
		
		autoWeight();		
		createWireBody();
		
		modelSelection.setText("selection:"+fileName);
		
		if(fileName.endsWith(".js")){
		//	fileName=FileNames.getRemovedExtensionName(fileName)+".js";//replace .js to .json i prefer
		}
		saveFileBox.setText(fileName);
		
		updateSelectableObjects();
	}
/**
 * on load model for weight&indecis 
 * @param fileName
 * @param geometry
 * @param materials
 */
	private void onWeightLoaded(String fileName,Geometry geometry,JsArray<Material> materials){
		setWeigthFromGeometry(geometry);
		weightSelection.setText("selection:"+fileName);
	}
	//called when bone changed or mesh changed
	@SuppressWarnings("unchecked")
	private void updateSelectableObjects(){
		objects=THREE.createJsArray();
		//body each point
		for(Mesh point:wireBodyPoints){
			objects.push(point);
		}
		//bone
		for(Mesh mesh:boneJointMeshs){
			objects.push(mesh);
		}
		
		for(Mesh mesh:boneCoreMeshs){
			objects.push(mesh);
		}
	}

	private void setWeigthFromGeometry(Geometry geometry) {
		//loadedGeometry
		//LogUtils.log(geometry);
		//LogUtils.log(geometry.getSkinIndices());
		//LogUtils.log(""+geometry.getSkinIndices().length());
		if(geometry.getSkinIndices().length()==0){
			LogUtils.log("loaded geometry has no indices:do nothing");
			return;
		}
		
		for(int i=0;i<geometry.vertices().length();i++){
			int loadedIndex=findSameIndex(loadedGeometry.vertices(), geometry.vertices().get(i));
		//	LogUtils.log(i+" find:"+loadedIndex);
			if(loadedIndex!=-1){
				
				bodyIndices.get(loadedIndex).setX(geometry.getSkinIndices().get(i).getX());
				bodyIndices.get(loadedIndex).setY(geometry.getSkinIndices().get(i).getY());
				
				bodyWeight.get(loadedIndex).setX(geometry.getSkinWeight().get(i).getX());
				bodyWeight.get(loadedIndex).setY(geometry.getSkinWeight().get(i).getY());
				
				
			}
		}
		//LogUtils.log("updated weight:"+updated);
		updateVertexColor();
	}
	private int findSameIndex(JsArray<Vector3> vertexList,Vector3 checkVertex){
		int result=-1;
		for(int i=0;i<vertexList.length();i++){
			boolean same=true;
			if(vertexList.get(i).getX()!=checkVertex.getX()){
				same=false;
			}else if(vertexList.get(i).getY()!=checkVertex.getY()){
				same=false;
			}else if(vertexList.get(i).getZ()!=checkVertex.getZ()){
				same=false;
			}
			if(same){
				result=i;
				break;
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected void updateAutoWeight(String value) {
		int selectedValue=Integer.parseInt(value);
		if(selectedValue!=-1){
			//clear Indices & Weight
			bodyIndices = (JsArray<Vector4>) JsArray.createArray();
			bodyWeight = (JsArray<Vector4>) JsArray.createArray();
			
			if(loadedGeometry.getSkinIndices().length()!=0 && loadedGeometry.getSkinWeight().length()!=0){
				if(selectedValue<100){
					WeightBuilder.autoWeight(loadedGeometry, bones, endSites,selectedValue, bodyIndices, bodyWeight);
				}else{
					MakehumanWeightBuilder.autoWeight(loadedGeometry, bones, endSites,selectedValue, bodyIndices, bodyWeight);
				}
			}else{
				//cant select geometry
				if(selectedValue==WeightBuilder.MODE_FROM_GEOMETRY){
					Window.alert("This geometry has no weight.choose another way");
				}else{
				WeightBuilder.autoWeight(loadedGeometry, bones, endSites,selectedValue, bodyIndices, bodyWeight);
				}
			}
			
			updateVertexColor();
		}
	}
	
	private void updateVertexColor(){
		for(int i=0;i<bodyGeometry.vertices().length();i++){
			Mesh mesh=vertexMeshs.get(i);
			mesh.getMaterial().gwtGetColor().setHex(getVertexColor(i));
		}
	}
	/**
	 right now totally broken.
	 it seems don't touch setCurrentTime ,it's make storm of "THREE.Animation.update: Warning! Scale out of bounds:" error
	 
	 some how bvh.getframetime totall differenct in animation.why?
	 i should fix it first.and set delta to update?
	 
	 handling delta seems fine so far.
	 but i should test when change animation.what happend?
	 */
	protected void updateFrameRange() {
		paused=true;
		double time=frameRange.getValue()*bvh.getFrameTime();
		//LogUtils.log("bvh-length="+bvh.getFrames()+",ftime="+bvh.getFrameTime());
		//LogUtils.log("set-current:"+time+",rangeValue="+frameRange.getValue()+",bvhTime="+bvh.getFrameTime());
		//LogUtils.log("animation,length="+animation.getData().getLength()+",fps="+animation.getData().getFps());
		
		double totaltime=animation.getData().getLength();
		double animationCurrentTime=animation.getCurrentTime();
		
		//animation.setCurrentTime(time);
		double delta=0;
		if(time>animationCurrentTime){
			delta=animationCurrentTime=time;
		}else{
			double last=totaltime-animationCurrentTime;
			delta=last+time;
		}
		
		AnimationHandler.update(delta);
		//animation.stop();
		//animation.play(true, currentTime);
		//currentTime=0;//to reset?
		//paused=false;
		//AnimationHandler.update(0);
		//animation.stop();
		//animation.pause();
		//AnimationHandler.update(0);
	}
	protected void exportWebStorage() {
		StorageDataList modelControler=new StorageDataList(storageControler, "MODEL");
		String title=Window.prompt("FileName", "Skinning-Exported");
		String text=toJsonText();
		modelControler.setDataValue(title, text);
		int id=modelControler.incrementId();
	}

	protected void selectPrevVertex() {
		int currentSelection=indexWeightEditor.getArrayIndex();
		for(int i=currentSelection-1;i>=0;i--){
			if(vertexMeshs.get(i).isVisible()){
				selectVertex(i);
				return;
			}
		}
		for(int i=vertexMeshs.size()-1;i>currentSelection;i--){
			if(vertexMeshs.get(i).isVisible()){
				selectVertex(i);
				return;
			}
		}
	}
	protected void selectNextVertex() {
		int currentSelection=indexWeightEditor.getArrayIndex();
		for(int i=currentSelection+1;i<vertexMeshs.size();i++){
			if(vertexMeshs.get(i).isVisible()){
				selectVertex(i);
				return;
			}
		}
		for(int i=0;i<currentSelection;i++){
			if(vertexMeshs.get(i).isVisible()){
				selectVertex(i);
				return;
			}
		}
	}
	private int lastSelection=-1;
	private void selectVertex(int index){
		if(lastSelection!=-1){
			vertexMeshs.get(lastSelection).getMaterial().gwtGetColor().setHex(getVertexColor(lastSelection));
		}
		Vector4 in=bodyIndices.get(index);
		Vector4 we=bodyWeight.get(index);
		
		LogUtils.log("select:"+index+","+we.getX()+","+we.getY());
		
		//change selected point to selection color,multiple selection is avaiable and to detect change point color.
		vertexMeshs.get(index).getMaterial().gwtGetColor().setHex(selectColor);
		
		indexWeightEditor.setAvailable(true);
		updateWeightButton.setEnabled(true);
		
		indexWeightEditor.setValue(index, in, we);
		
		//show which point selection,with wireframe weight-color
		boneSelectionIndicateBoxMesh.setVisible(true);
		boneSelectionIndicateBoxMesh.setPosition(vertexMeshs.get(index).getPosition());
		boneSelectionIndicateBoxMesh.getMaterial().gwtGetColor().setHex(getVertexColor(index));
		
		stackPanel.showWidget(1); //bone & weight panel
		lastSelection=index;
	}

	public static String get(Vector4 vec){
		if(vec==null){
			return "Null";
		}
		String ret="x:"+vec.getX();
		ret+=",y:"+vec.getY();
		ret+=",z:"+vec.getZ();
		ret+=",w:"+vec.getW();
		return ret;
	}
	
	/*
	 * add bones and skinIndices & skinWeights to last selected model
	 * animation is option.
	 * so model format version keep(not touch uv)
	 */
	
	/**
	 * i can't trust this maybe 2 influence fixed
	 * @return
	 */
	public String toJsonText(){
		JsArray<AnimationBone> clonedBone=cloneBones(bones);
		JSONArray arrays=new JSONArray(clonedBone);
		lastJsonObject.put("bones", arrays);
		//AnimationBoneConverter.setBoneAngles(clonedBone, rawAnimationData, 0); //TODO
		if(withAnimation.getValue()){
			lastJsonObject.put("animation", new JSONObject(rawAnimationData));
		}
		//private JsArray<Vector4> bodyIndices;
		//private JsArray<Vector4> bodyWeight;
		/*
		List<String> lines=new ArrayList<String>();
		for(int i=0;i<bodyWeight.length();i++){
			lines.add(i+get(bodyWeight.get(i)));
		}
		LogUtils.log("after");
		LogUtils.log(Joiner.on("\n").join(lines));f
		*/
		
		//LogUtils.log(bodyIndices);
		
		//LogUtils.log(bodyWeight);
		JsArrayNumber indices=(JsArrayNumber) JsArrayNumber.createArray();
		for(int i=0;i<bodyIndices.length();i++){
			indices.push(bodyIndices.get(i).getX());
			indices.push(bodyIndices.get(i).getY());
		}
		lastJsonObject.put("skinIndices", new JSONArray(indices));
		
		JsArrayNumber weights=(JsArrayNumber) JsArrayNumber.createArray();
		for(int i=0;i<bodyWeight.length();i++){
			weights.push(bodyWeight.get(i).getX());
			weights.push(bodyWeight.get(i).getY());
		}
		lastJsonObject.put("skinWeights", new JSONArray(weights));
		
		//rewrite gen
		
		JSONModelFile file=(JSONModelFile) lastJsonObject.getJavaScriptObject();
		file.getMetaData().setGeneratedBy(getGeneratedBy());
		
		return stringify(lastJsonObject.getJavaScriptObject());
		//return lastJsonObject.toString();
	}
	public static String getGeneratedBy(){
		return "GWTModel-Weight ver"+CopyOfGWTModelWeight.version;
	}
	private void exportAsJson(){
		//set bone
		String json=toJsonText();
		String name=saveFileBox.getText();
		if(name.isEmpty()){
			name="untitled.js";
		}
		if(anchor!=null){
			anchor.removeFromParent();
			anchor=null;
		}
		anchor = new HTML5Download().generateTextDownloadLink(json, name, "Download File");
		exportLinks.add(anchor);
		anchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				anchor.removeFromParent();
			}
		});
		//exportTextAsDownloadDataUrl(toJsonText(),"UTF-8","WeightTool"+exportIndex);
		//exportTextChrome(toJsonText(),"WeightTool"+exportIndex);
		//exportIndex++;
	}
	
	int exportIndex;
	
	
	
	
	public static final void exportTextAsDataUrl(String text,String encode,String wname){
		
		String url="data:text/plain;charset="+encode+","+text;
		Window.open(url, wname, null);
	}
	//export json pretty way
	public native final String stringify(JavaScriptObject json)/*-{
	return $wnd.JSON.stringify(json,null,2);
	}-*/;
	
	
	public native final void exportTextChrome(String text,String wname)/*-{
	win = $wnd.open("", wname)
	win.document.body.innerText =""+text+"";
	}-*/;
	
	private boolean paused=true;

	
	private JsArray<Vector4> bodyIndices;
	private JsArray<Vector4> bodyWeight;
	
	
	private BVH bvh;
	private Animation animation;
	
	private Geometry bodyGeometry;
	private Object3D boneAndVertex;
	private JsArray<AnimationBone> bones;
	private List<List<Vector3>> endSites;
	
	private SkinnedMesh skinnedMesh;
	
	private ColladaData rawCollada;
	private AnimationData rawAnimationData;
	
	private void updateBoneListBox(){
		boneListBox.clear();
		for(int i=0;i<bones.length();i++){
			boneListBox.addItem(bones.get(i).getName());
		}
	}
	

	
	public JsArray<AnimationBone>  cloneBones(JsArray<AnimationBone> bones){
		JsArray<AnimationBone> array=(JsArray<AnimationBone>) JsArray.createArray();
		for(int i=0;i<bones.length();i++){
			AnimationBone bone=bones.get(i);
			
			AnimationBone cloned=(AnimationBone) AnimationBone.createObject();
			cloned.setName(bone.getName());
			cloned.setParent(bone.getParent());
			if(bone.getPos()!=null){
			cloned.setPos(GWTThreeUtils.clone(bone.getPos()));
			}
			if(bone.getRotq()!=null){
				cloned.setRotq(GWTThreeUtils.clone(bone.getRotq()));
				}
			if(bone.getRot()!=null){
				cloned.setRot(GWTThreeUtils.clone(bone.getRot()));
				}
			if(bone.getScl()!=null){
				cloned.setScl(GWTThreeUtils.clone(bone.getScl()));
				}
			array.push(cloned);
		}
		return array;
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
	
	
	//TODO fix bvh not support anymore
	private void setBvh(BVH bv){

		LogUtils.log("setBvh:root-pos:"+bv.getHiearchy().getOffset());
		
		bvh=bv;
		
		//
		
		/* not work
		bvhVec=bvh.getHiearchy().getOffset();
		bvh.getHiearchy().setOffset(new Vec3(0,0,0));//i believe no need.
		*/
		
		List<BVHNode> bvhNodes=new ArrayList<BVHNode>();
		addNode(bvh.getHiearchy(),bvhNodes);
		
		infoPanel.getBvhObjects().setDatas(bvhNodes);
		infoPanel.getBvhObjects().update(true);
		
		for(int i=0;i<bvhNodes.size();i++){
			LogUtils.log(i+","+bvhNodes.get(i).getName()+","+bvhNodes.get(i).getOffset().toString()+","+bvhNodes.get(i).getOffset().getX());
		}
		
		//bvh.setSkips(10);
		//bvh.setSkips(skipFrames);
		
		AnimationBoneConverter converter=new AnimationBoneConverter();
		bones = converter.convertJsonBone(bvh);
		
		
		List<AnimationBone> abList=new ArrayList<AnimationBone>();
		for(int i=0;i<bones.length();i++){
			abList.add(bones.get(i));
		}
		
		infoPanel.getBvhAnimationObjects().setDatas(abList);
		infoPanel.getBvhAnimationObjects().update(true);
		
		
		LogUtils.log(bones);
		endSites=converter.convertJsonBoneEndSites(bvh);
		
		updateBoneListBox();
		indexWeightEditor.setBones(bones);
		
		/*
		for(int i=0;i<bones.length();i++){
		//	LogUtils.log(i+":"+bones.get(i).getName());
		}
		GWT.LogUtils.log("parsed");
		*/
		
		/*
		
		
		for(int i=0;i<bones.length();i++){
			Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5), THREE.MeshLambertMaterial().color(0x0000ff).build());
			scene.add(mesh);
			JsArrayNumber pos=bones.get(i).getPos();
			mesh.setPosition(pos.get(0),pos.get(1),pos.get(2));
		}
		*/
		
		
		AnimationDataConverter dataConverter=new AnimationDataConverter();
		if(bvh.getFrames()==1){
			dataConverter.setSkipFirst(false);
			frameRange.setMax(bvh.getFrames());
		}else{
			dataConverter.setSkipFirst(false);//need skip first option
			//frameRange.setMax(bvh.getFrames()-1);
			frameRange.setMax(bvh.getFrames());
		}
		
		animationData = dataConverter.convertJsonAnimation(bones,bvh);
		//for(int i=0;i<animationData.getHierarchy().length();i++){
			AnimationHierarchyItem item=animationData.getHierarchy().get(0);
			for(int j=0;j<item.getKeys().length();j++){
				item.getKeys().get(j).setPos(0, 0, 0);//dont move;
			}
		//}
		rawAnimationData=dataConverter.convertJsonAnimation(bones,bvh);//for json
		
		if(bvh.getFrames()==1){
		animationName=null;//i guess 1 frame bvh usually maked geometry bone which has no animation erase mesh.
		pauseBt.setEnabled(false);
		frameRange.setEnabled(false);
		}else{
			pauseBt.setEnabled(true);
			frameRange.setEnabled(true);	
		animationName=animationData.getName();
		}
		
		//JsArray<AnimationHierarchyItem> hitem=animationData.getHierarchy();
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
		
		
		LogUtils.log("cube");
		LogUtils.log(cube);
		
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
		
		//overwrite same name
		//AnimationHandler.add(animationData);
		//LogUtils.log(data);
		//LogUtils.log(bones);
		
		JSONArray array=new JSONArray(bones);
		//LogUtils.log(array.toString());
		
		JSONObject test=new JSONObject(animationData);
		//LogUtils.log(test.toString());
		
		
		if(texture==null){
			//initial texture load,no need to care flipY,it's care when model loaded.
			texture=ImageUtils.loadTexture(textureUrl);
		}
		
		
		initializeObject();
		
		if(loadedGeometry==null){//initial load
		loadModel(modelUrl,
		new  JSONLoadHandler() {
			//loader.load("men3smart.js", new  LoadHandler() {
				@Override
				public void loaded(Geometry geometry,JsArray<Material> materials) {
					onModelLoaded(modelUrl,geometry);
					createSkinnedMesh();
				}
			
			});
		}else{
			//is this order is important?
			autoWeight();
			createSkinnedMesh();
			createWireBody();
			updateSelectableObjects();
			
		}
		//LogUtils.log("before create");
		//Mesh mesh=THREE.Mesh(cube, THREE.MeshLambertMaterial().skinning(false).color(0xff0000).build());
		//LogUtils.log("create-mesh");
		//scene.add(mesh);
		
		
		
		/*
		ColladaLoader loader=THREE.ColladaLoader();
		loader.load("men3smart_hair.dae#1", new  ColladaLoadHandler() {
			
			
			
			public void colladaReady(ColladaData collada) {
				rawCollada=collada;
				LogUtils.log(collada);
				boneNameMaps=new HashMap<String,Integer>();
				for(int i=0;i<bones.length();i++){
					boneNameMaps.put(bones.get(i).getName(), i);
					LogUtils.log(i+":"+bones.get(i).getName());
				}
				
				Geometry geometry=collada.getGeometry();
				colladaJoints=collada.getJoints();
				
				Vector3 xup=THREE.Vector3(Math.toRadians(-90),0,0);
				Matrix4 mx=THREE.Matrix4();
				mx.setRotationFromEuler(xup, "XYZ");
				geometry.applyMatrix(mx);
				*/
	}
	
	private void addNode(BVHNode node,List<BVHNode> container){
		container.add(node);
		for(BVHNode child:node.getJoints()){
			child.setParentName(node.getName());//somehow name is empty
			addNode(child,container);
		}
	}
	
	
	
	private AnimationData animationData;

	private Vec3 bvhVec;
	

	private String animationName;
	private Geometry loadedGeometry;

	private Mesh boneSelectionIndicateBoxMesh;
	
	//used for mouse selection ,added mesh's eash point and bone-joint.
	JsArray<Object3D> objects;


	private TrackballControls trackballControls;
	
	@SuppressWarnings("unchecked")
	private void initializeObject(){

		currentTime=0;
		//for skinned mesh
		if(root!=null){
			scene.remove(root);
		}
		
		root=THREE.Object3D();
		scene.add(root);
		
			
		
		
		if(boneAndVertex!=null){
			scene.remove(boneAndVertex);
			if(boneSelectionMesh!=null){
				boneSelectionMesh=null;
			}
		}
		
		boneAndVertex = THREE.Object3D();
		//boneAndVertex.setPosition(-15, 0, 0);
		scene.add(boneAndVertex);
		Object3D bo=createBoneObjects(bvh);
		//bo.setPosition(-30, 0, 0);
		boneAndVertex.add(bo);
		
		double selectionSize=0.02*baseScale;
		
		//what is this?
		boneSelectionIndicateBoxMesh=THREE.Mesh(THREE.BoxGeometry(selectionSize, selectionSize, selectionSize), THREE.MeshBasicMaterial(GWTParamUtils.MeshBasicMaterial().color(selectColor).transparent(true).wireframe(true)));
		boneAndVertex.add(boneSelectionIndicateBoxMesh);
		boneSelectionIndicateBoxMesh.setVisible(false);	
		
		camera.lookAt(root.getPosition());
		
	}
	private int selectColor=0xb362ff;
	
	//for selection
	private List<Mesh> wireBodyPoints=new ArrayList<Mesh>();
	//private int defaultColor=0xffff00;
	private void createWireBody(){
		wireBodyPoints.clear();
		bodyGeometry=GeometryUtils.clone(loadedGeometry);
		
		//clone because of set color
		
		
		//test set random color
				
				
				
		
		wireBody = THREE.Mesh(bodyGeometry, 
				
				THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial()
						.wireframe(true)
						.color(0xffffff)
						.vertexColors(THREE.VertexColors)
						)
				);
		//wireBody.getGeometry().computeBoundingBox();
		
		//ThreeLog.log("wire",wireBody.getGeometry().getBoundingBox());
		//ThreeLog.log("pos",wireBody.getPosition());
		//ThreeLog.log("scale",wireBody.getScale());
		
		//watch out once set color can't replace color.use setHex()
		for(int i=0;i<bodyGeometry.getFaces().length();i++){
			Face3 face=bodyGeometry.getFaces().get(i);
			for(int j=0;j<3;j++){
				face.getVertexColors().set(j, THREE.Color(0));
			}
		}
	
		wireBody.getGeometry().setColorsNeedUpdate(true);
		
		
		
		//
		boneAndVertex.add(wireBody);
		
		selectVertex=THREE.Object3D();
		vertexMeshs.clear();
		
		boneAndVertex.add(selectVertex);
		double bmeshSize=0.01*baseScale;
		
		Geometry cube=THREE.BoxGeometry(bmeshSize, bmeshSize, bmeshSize);
		
		for(int i=0;i<bodyGeometry.vertices().length();i++){
			/*
			Material mt=THREE.MeshBasicMaterial().color(getVertexColor(i)).build();
			Vector3 vx=bodyGeometry.vertices().get(i);
			Mesh point=THREE.Mesh(cube, mt);
			point.setVisible(false);
			point.setName("point:"+i);
			point.setPosition(vx);
			selectVertex.add(point);
			vertexMeshs.add(point);
			wireBodyPoints.add(point);
			*/
		}
	}
	/* call change listener ,this is not good
	private void changeAutoWeightListBox(int value){
		//callAutoWeight=false;
		autoWeightListBox.setSelectedIndex(value);
		//callAutoWeight=true;
	}
	*/
	private void autoWeight(){
		clearBodyIndicesAndWeightArray();
		LogUtils.log(loadedGeometry);
		
		//have valid skinIndices and weight use from that.
		if(GWTGeometryUtils.isValidSkinIndicesAndWeight(loadedGeometry)){
			WeightBuilder.autoWeight(loadedGeometry, bones, endSites,WeightBuilder.MODE_FROM_GEOMETRY, bodyIndices, bodyWeight);
		
			//changeAutoWeightListBox(0);
			
			//wait must be 1.0
			for(int i=0;i<bodyWeight.length();i++){
				Vector4 vec4=bodyWeight.get(i);
				double total=vec4.getX()+vec4.getY();
				double remain=(1.0-total)/2;
				//vec4.setX(vec4.getX()+remain);// i guess this is problem
				//vec4.setY(vec4.getY()+remain);
			}
			/*
			List<String> lines=new ArrayList<String>();
			for(int i=0;i<bodyWeight.length();i++){
				lines.add(i+get(bodyWeight.get(i)));
			}
			LogUtils.log("before:");
			LogUtils.log(Joiner.on("\n").join(lines));
			*/
			
		}else{
			LogUtils.log("empty indices&weight auto-weight from geometry:this action take a time");
			//can't auto weight algo change? TODO it
			WeightBuilder.autoWeight(loadedGeometry, bones, endSites,WeightBuilder.MODE_MODE_Start_And_Half_ParentAndChildrenAgressive, bodyIndices, bodyWeight);
			//changeAutoWeightListBox(1);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void clearBodyIndicesAndWeightArray(){
		bodyIndices = (JsArray<Vector4>) JsArray.createArray();
		bodyWeight = (JsArray<Vector4>) JsArray.createArray();
	}
	
	private void loadJsonModel(JSONObject object,JSONLoadHandler handler){
		JSONLoader loader=THREE.JSONLoader();
		JavaScriptObject jsobject=loader.parse(object.getJavaScriptObject(), null);
		JSONObject newobject=new JSONObject(jsobject);
		
		
		
		handler.loaded((Geometry) newobject.get("geometry").isObject().getJavaScriptObject(),null);
		//LogUtils.log("json");
		//LogUtils.log(object.getJavaScriptObject());
		//loader.createModel(object.getJavaScriptObject(), handler, "");
		//loader.onLoadComplete();
		
		//return object;
		
	}
	//TODO replace to GWTThreeUtils.parseJsonObject
	private JSONObject parseJsonObject(String jsonText){
		JSONValue lastJsonValue = JSONParser.parseStrict(jsonText);
		JSONObject object=lastJsonValue.isObject();
		if(object==null){
			LogUtils.log("invalid-json object");
		}
		return object;
	}
	
	//this is original loaded text,dont edit it
	private String lastLoadedText;
	
	/*
	 * need json-text and json-model for export
	 */
	private void loadModel(String path,final JSONLoadHandler handler){
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(path));
			try {
				builder.sendRequest(null, new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						lastLoadedText=response.getText();
						JSONObject object=parseJsonObject(lastLoadedText);
						lastJsonObject=object;
						loadJsonModel(object,handler);
					}
					
					
					

@Override
public void onError(Request request, Throwable exception) {
				Window.alert("load faild:");
}
				});
			} catch (RequestException e) {
				LogUtils.log(e.getMessage());
				e.printStackTrace();
			}
		
		
	}
		
		
	private String textureUrl="underware-nonipple.png";
	
	
	//private String modelUrl="model001_female2661_bone19.js";
	private String modelUrl="merged3.json";//testing 4-point
	
	private Texture texture;
	private void generateTexture(){
		final Image img=new Image(textureUrl);
		img.setVisible(false);
		RootPanel.get().add(img);
		img.addLoadHandler(new com.google.gwt.event.dom.client.LoadHandler() {
			
			@Override
			public void onLoad(LoadEvent event) {
				Canvas canvas=Canvas.createIfSupported();
				canvas.setCoordinateSpaceWidth(img.getWidth());
				canvas.setCoordinateSpaceHeight(img.getHeight());
				canvas.getContext2d().drawImage(ImageElement.as(img.getElement()),0,0);
				texture=THREE.CanvasTexture(canvas.getCanvasElement());
				texture.setNeedsUpdate(true);
				//Window.open(canvas.toDataUrl(), "test", null);
				img.removeFromParent();
				updateMaterial();
			}
		});
		
		
		
	}
	boolean useTrackball=true;
	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if(useTrackball){
			return;
		}
		double tzoom=0.05*baseScale;
		//TODO make class
		long t=System.currentTimeMillis();
		if(mouseLast+100>t){
			czoom*=2;
		}else{
			czoom=tzoom;
		}
		//GWT.log("wheel:"+event.getDeltaY());
		double tmp=cameraZ+event.getDeltaY()*czoom;
		tmp=Math.max(0.2, tmp);
		tmp=Math.min(4000, tmp);
		cameraZ=(double)tmp;
		mouseLast=t;
	}
	double czoom;
	
	protected void updateMaterial() {
		
		Material material=null;
		
		texture.setFlipY(needFlipY);//for temporary release //TODO as option for 3.1 format models
		
		if(!needFlipY){
			LogUtils.log("texture flipY:false for old 3.0 format");
		}
		
		if(useBasicMaterial.getValue()){
			material=THREE.MeshBasicMaterial(GWTParamUtils.MeshBasicMaterial()
					.skinning(true).color(0xffffff).map(texture));
		}else{
			material=THREE.MeshLambertMaterial(GWTParamUtils.MeshBasicMaterial().skinning(true).color(0xffffff).map(texture));
		}
		
		
		if(skinnedMesh!=null){
			skinnedMesh.setMaterial(material);
		}
	}
	private void createSkinnedMesh(){
		LogUtils.log("start createSkinnedMesh");
		String json=toJsonText();
		JSONObject object=parseJsonObject(json);
		//JSONObject object=parseJsonObject(lastLoadedText);
		
		//try load original
		loadJsonModel(object,new JSONLoadHandler() {
			
			@Override
			public void loaded(Geometry geometry, JsArray<Material> materials) {
				if(skinnedMesh!=null){
					root.remove(skinnedMesh);
				}
				Material material=null;
				if(useBasicMaterial.getValue()){
					material=THREE.MeshBasicMaterial(GWTParamUtils.MeshBasicMaterial().skinning(true).color(0xffffff).map(texture));
					
				}else{
					material=THREE.MeshLambertMaterial(GWTParamUtils.MeshLambertMaterial().skinning(true).color(0xffffff).map(texture));
				}
				
				//try to better ,but seems faild
				/*
				LogUtils.log("skinmesh root bone pos changed to 0");
				AnimationBone bone=geometry.getBones().get(0);
				Vector3 rootOffset=THREE.Vector3(bone.getPos().get(0), bone.getPos().get(1),bone.getPos().get(2));
				
				for(int i=0;i<geometry.getVertices().length();i++){
					geometry.getVertices().get(i).sub(rootOffset);
				}
				
				bone.setPos(0, 0, 0);//root is 0 is better?
				*/
				
				
				//trying vertex color
				
				/*
				material=THREE.MeshBasicMaterial(vertexColors());
				
				List<Integer> result=new ArrayList<Integer>();
				
				groupdFaces(result,geometry.getFaces(),0);
				Color red=THREE.Color(0xff0000);
				LogUtils.log("size:"+result.size());
				for(int index:result){
					geometry.getFaces().get(index).setColor(red);
				}
				*/
				
				
				skinnedMesh = THREE.SkinnedMesh(geometry, material);
				
				//skinnedMesh.getGeometry().computeBoundingBox();
				//ThreeLog.log("skinned",skinnedMesh.getGeometry().getBoundingBox());
				//ThreeLog.log("pos",skinnedMesh.getPosition());
				//ThreeLog.log("scale",skinnedMesh.getScale());
				
				root.add(skinnedMesh);
				LogUtils.log("skinnedMesh created");
				
				camera.lookAt(skinnedMesh.getPosition());
				
				if(animation!=null){
				//	AnimationHandler.removeFromUpdate(animation);
				}
				
				//i guess this crash,if animation length is 1 
				if(animationName!=null){//animation setted when set bvh
					LogUtils.log("unsupport animatin now");
					//animation = THREE.Animation( skinnedMesh, animationName );
					//LogUtils.log(animation);
					//animation.play(true,0);
					//right now animation already ignore position in animation
				}else{
					//what is this hell?
					//skinnedMesh.getPosition().sub(skinnedMesh.getSkeleton().getBones().get(0).getPosition());
				}
				
					
				
				
				//debug for geometry inside animation
				/*
				if(geometry.getAnimations()!=null){
					if(geometry.getAnimations().length()>0){
						animationName=geometry.getAnimations().get(0).getName();
						AnimationHandler.add(geometry.getAnimations().get(0));
						LogUtils.log(geometry.getAnimations().get(0));
						
						
						LogUtils.log("start new animation:"+animationName);
						LogUtils.log(skinnedMesh);
						animation = THREE.Animation( skinnedMesh, animationName );
						LogUtils.log(animation);
						animation.play(true,0);
					}else{
						animation=null;
						LogUtils.log("this bone has no animation");
					}
					
				
					
				}
				*/
				
			}
		});
	}
	/*
	public  List<Integer> groupdFaces(List<Integer> result,JsArray<Face3> faces,int targetIndex){
		result.add(targetIndex);
		Face3 face=faces.get(targetIndex);
		
		
		int a=(int) face.getA();
		for(int i=0;i<faces.length();i++){
			if(result.contains(i)){
				continue;
			}else{
				if(hasIndex(faces.get(i), a)){
					groupdFaces(result,faces,i);
				}
			}	
		}
		
		int b=(int) face.getB();
		for(int i=0;i<faces.length();i++){
			if(result.contains(i)){
				continue;
			}else{
				if(hasIndex(faces.get(i), b)){
					groupdFaces(result,faces,i);
				}
			}	
		}
		
		int c=(int) face.getC();
		for(int i=0;i<faces.length();i++){
			if(result.contains(i)){
				continue;
			}else{
				if(hasIndex(faces.get(i), c)){
					groupdFaces(result,faces,i);
				}
			}	
		}
		
		if(face.isFace4()){
			int d=(int) face.getD();
			for(int i=0;i<faces.length();i++){
				if(result.contains(i)){
					continue;
				}else{
					if(hasIndex(faces.get(i), d)){
						groupdFaces(result,faces,i);
					}
				}	
			}
		}
		
		
		return result;
		
	}
	
	private boolean hasIndex(Face3 face,int index){
		if(face.getA()==index){
			return true;
		}
		if(face.getB()==index){
			return true;
		}
		if(face.getC()==index){
			return true;
		}
		
		if(face.isFace4() && face.getD()==index){
			return true;
		}
		return false;
	}
	
	*/
	
	//for vertex color material
	public static  native final JavaScriptObject vertexColors()/*-{
	return {vertexColors: $wnd.THREE.VertexColors };
	}-*/;
	
	
	private List<Mesh> vertexMeshs=new ArrayList<Mesh>();

	private IndexAndWeightEditor indexWeightEditor;
	
	//simple simple near positions only use end point,this is totall not good
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
	
	List<List<GWTWeightData>> wdatas;
	
	
	/*
	 * some point totally wrong?or only work on multiple weight points
	 * 
	 */
	JsArrayString colladaJoints;
	Map<String,Integer> boneNameMaps;

	private CheckBox useBasicMaterial;

	//this is used for export so,not original loaded
	private JSONObject lastJsonObject;

	private CheckBox withAnimation;

	private StackLayoutPanel stackPanel;

	private ListBox boneListBox;

	private Button updateWeightButton;

	private InputRangeWidget frameRange;

	private TextBox saveFileBox;

	private VerticalPanel exportLinks;

	private Anchor anchor;

	private Label weightSelection;

	private Label modelSelection;

	private ListBox autoWeightListBox;

	private Label geometrySelection;

	private Label textureSelection;
	//private boolean callAutoWeight=true;
	private Vector4 convertWeight(int index,ColladaData collada){
		if(wdatas==null){
			//wdatas=new WeighDataParser().parse(Bundles.INSTANCE.weight().getText());
			wdatas=new WeighDataParser().convert(collada.getWeights());
			for(List<GWTWeightData> wd:wdatas){
				String log="";
				for(GWTWeightData w:wd){
					log+=w.getBoneIndex()+":"+w.getWeight()+",";
				}
				//LogUtils.log(log);
			}
		}
		List<GWTWeightData> wd=wdatas.get(index);
		if(wd.size()<2){
			int id=wd.get(0).getBoneIndex();
			String fname=colladaJoints.get(id);
			id=boneNameMaps.get(fname);
			
			return THREE.Vector4(id,id,1,0);
		}else{
			int fid=wd.get(0).getBoneIndex();
			int sid=wd.get(1).getBoneIndex();
			String fname=colladaJoints.get(fid);
			fid=boneNameMaps.get(fname);
			
			String sname=colladaJoints.get(sid);
			sid=boneNameMaps.get(sname);
			
			
			double fw=wd.get(0).getWeight();
			double sw=wd.get(1).getWeight();
			//LogUtils.log(fid+":"+fw+","+sid+":"+sw);
			
			//fw=1;
			
			return THREE.Vector4(fid,sid,fw,sw);
		}
	}
	
private Vector4 findNearSpecial(List<NameAndPosition> nameAndPositions,Vector3 pos,JsArray<AnimationBone> bones,int vindex){
	
	Vector3 pt=nameAndPositions.get(0).getPosition().clone();
	Vector3 near=pt.subSelf(pos);
	int index1=nameAndPositions.get(0).getIndex();
	double near1=near.length();
	int index2=index1;
	double near2=near1;
	
	int nameIndex1=0;
	int nameIndex2=0;
	
	for(int i=1;i<nameAndPositions.size();i++){
		Vector3 npt=nameAndPositions.get(i).getPosition().clone();
		Vector3 subPos=npt.subSelf(pos);
		double l=subPos.length();
		//if(vindex==250)LogUtils.log(nameAndPositions.get(i).getName()+","+l);
		
		if(l<near1){
			int tmp=index1;
			double tmpL=near1;
			int tmpName=nameIndex1;
			
			index1=nameAndPositions.get(i).getIndex();
			near1=l;
			nameIndex1=i;
			if(tmpL<near2){
				index2=tmp;
				near2=tmpL;
				nameIndex2=tmpName;
			}
		}else if(l<near2){
			index2=nameAndPositions.get(i).getIndex();
			near2=l;
			nameIndex2=i;
		}
	}
	
	
	
		
		Map<Integer,Double> totalLength=new HashMap<Integer,Double>();
		Map<Integer,Integer> totalIndex=new HashMap<Integer,Integer>();
		
		//zero is largest
		Vector3 rootNear=nameAndPositions.get(0).getPosition().clone();
		rootNear.subSelf(pos);
		
		
		
		for(int i=0;i<nameAndPositions.size();i++){
			int index=nameAndPositions.get(i).getIndex();
			Vector3 nearPos=nameAndPositions.get(i).getPosition().clone();
			nearPos.subSelf(pos);
			double l=nearPos.length();
			
			Double target=totalLength.get(index);
			double cvalue=0;
			if(target!=null){
				cvalue=target.doubleValue();
			}
			cvalue+=l;
			
			totalLength.put(index,cvalue);
			
			
			
			Integer count=totalIndex.get(index);
			int countV=0;
			if(count!=null){
				countV=count;
			}
			countV++;
			totalIndex.put(index, countV);
		}
		
		
		//do average for end like head
		Integer[] keys=totalLength.keySet().toArray(new Integer[0]);
		for(int i=0;i<keys.length;i++){
			int index=keys[i];
			int count=totalIndex.get(index);
			totalLength.put(index, totalLength.get(index)/count);
		}
		
		if(index1==index2){
			//LogUtils.log(""+vindex+","+nameIndex1+":"+nameIndex2);
			if(vindex==250){
			//	LogUtils.log(nameAndPositions.get(nameIndex1).getPosition());
			//	LogUtils.log(nameAndPositions.get(nameIndex2).getPosition());
			}
			return THREE.Vector4(index1,index1,1,0);
		}else{
			double near1Length=totalLength.get(index1);
			double near2Length=totalLength.get(index2);
			
			double total=near1Length+near2Length;
			return THREE.Vector4(index1,index2,(total-near1Length)/total,(total-near2Length)/total);
		}
		
	}

	
	private Vector4 findNearThreeBone(List<NameAndPosition> nameAndPositions,Vector3 pos,JsArray<AnimationBone> bones,int vindex){
		
		
		Map<Integer,Double> totalLength=new HashMap<Integer,Double>();
		Map<Integer,Integer> totalIndex=new HashMap<Integer,Integer>();
		
		
		//LogUtils.log("find-near:"+vindex);
		for(int i=0;i<nameAndPositions.size();i++){
			int index=nameAndPositions.get(i).getIndex();
			Vector3 near=nameAndPositions.get(i).getPosition().clone();
			near.subSelf(pos);
			double l=near.length();
			
			Double target=totalLength.get(index);
			double cvalue=0;
			if(target!=null){
				cvalue=target.doubleValue();
			}
			cvalue+=l;
			totalLength.put(index,cvalue);
			
			Integer count=totalIndex.get(index);
			int countV=0;
			if(count!=null){
				countV=count;
			}
			countV++;
			totalIndex.put(index, countV);
		}
		Integer[] keys=totalLength.keySet().toArray(new Integer[0]);
		
		for(int i=0;i<keys.length;i++){
			int index=keys[i];
			int count=totalIndex.get(index);
			totalLength.put(index, totalLength.get(index)/count);
		}
		
		
		
		
		
		
		
		
		int index1=0;
		double near1=totalLength.get(keys[0]);
		int index2=0;
		double near2=totalLength.get(keys[0]);
		
		for(int i=1;i<keys.length;i++){
			double l=totalLength.get(keys[i]);
			if(l<near1){
				int tmp=index1;
				double tmpL=near1;
				index1=keys[i];
				near1=l;
				if(tmpL<near2){
					index2=tmp;
					near2=tmpL;
				}
			}else if(l<near2){
				index2=keys[i];
				near2=l;
			}
		}
		
		if(index1==index2){
			return THREE.Vector4(index1,index1,1,0);
		}else{
			double total=near1+near2;
			//LogUtils.log("xx:"+index1+","+index2);
			return THREE.Vector4(index1,index2,(total-near1)/total,(total-near2)/total);
		}
	}
	
	
	//use start,center and end position,choose near 2point
	private Vector4 findNearBoneAggresive(List<NameAndPosition> nameAndPositions,Vector3 pos,JsArray<AnimationBone> bones){
		Vector3 pt=nameAndPositions.get(0).getPosition().clone();
		Vector3 near=pt.subSelf(pos);
		int index1=nameAndPositions.get(0).getIndex();
		double near1=pt.length();
		int index2=nameAndPositions.get(0).getIndex();
		double near2=pt.length();
		
		
		for(int i=1;i<nameAndPositions.size();i++){
			Vector3 npt=nameAndPositions.get(i).getPosition().clone();
			near=npt.subSelf(pos);
			double l=near.length();
			if(l<near1){
				int tmp=index1;
				double tmpL=near1;
				index1=nameAndPositions.get(i).getIndex();
				near1=l;
				if(tmpL<near2){
					index2=tmp;
					near2=tmpL;
				}
			}else if(l<near2){
				index2=nameAndPositions.get(i).getIndex();
				near2=l;
			}
		}
		near1*=near1*near1*near1;
		near2*=near2*near2*near2;
		if(index1==index2){
			return THREE.Vector4(index1,index1,1,0);
		}else{
			
			double total=near1+near2;
			return THREE.Vector4(index1,index2,(total-near1)/total,(total-near2)/total);
		}
	}
	

	/*
	 * find near ,but from three point
	 */
	private Vector4 findNearSingleBone(List<NameAndPosition> nameAndPositions,Vector3 pos,JsArray<AnimationBone> bones){
		Vector3 pt=nameAndPositions.get(0).getPosition().clone();
		Vector3 near=pt.subSelf(pos);
		int index1=nameAndPositions.get(0).getIndex();
		double near1=pt.length();

		for(int i=1;i<nameAndPositions.size();i++){
			Vector3 npt=nameAndPositions.get(i).getPosition().clone();
			near=npt.subSelf(pos);
			double l=near.length();
			if(l<near1){
				index1=nameAndPositions.get(i).getIndex();
				near1=l;
			}
		}
		return THREE.Vector4(index1,index1,1,0);
	}
	
	//find neard point,choose second from parent or children which near than
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
	
	/*
	 * find near and choose parent
	 */
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
	
	//choose two point but only near about first * value
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
			}else if(l<near2){
				index2=i;
				near2=l;
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
	@Override
	public String getTabTitle() {
		return "Weight Tool";
	}
	@Override
	public String getHtml(){
	String html="Weight Tool ver."+version+" "+super.getHtml();

	return html;	
	}
	@Override
	public void onMouseClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private void onTextureFileUploaded(final File file,String value){
		textureUrl=value;
		generateTexture();
		//createSkinnedMesh();
		textureSelection.setText("selection:"+file.getFileName());
	}
	private void onGeometryFileUploaded(final File file,String value){
		//parseBVH(value);
		geometrySelection.setText("selection:"+file.getFileName());
		//TODO parse
	}
	private void onWeightFileUploaded(final File file,String value){
		JSONObject object=parseJsonObject(value);
		
		//dont need json object,just use weights and indecis info
		loadJsonModel(object,new JSONLoadHandler() {
			
			@Override
			public void loaded(Geometry geometry,JsArray<Material> materials) {
				onWeightLoaded(file.getFileName(), geometry, materials);
			}

		});
	}
	

	private void onModelFileUploaded(final File file,String value){
		lastLoadedText=value;
		JSONObject object=parseJsonObject(value);
		lastJsonObject=object;//lastJson object used in exportAsJson
		LogUtils.log(lastJsonObject.getJavaScriptObject());
		
		/* old code for force scale up
		if(force10x.getValue()){
		JSONNumber jscale=lastJsonObject.get("scale").isNumber();
		if(jscale!=null ){
			double scale=(int) jscale.doubleValue();
			JSONArray jvertices=lastJsonObject.get("vertices").isArray();
			LogUtils.log("scale:"+scale);
			if(jvertices!=null){
				LogUtils.log("scale attribute found in json file,and scale it only vertices");
				JsArrayNumber numbers=(JsArrayNumber) jvertices.getJavaScriptObject();
				for(int i=0;i<numbers.length();i++){
					if(scale==1){
					numbers.set(i, numbers.get(i)*scale*10);
					}else{
						numbers.set(i, numbers.get(i)*scale);
					}
				}
			}
			
		}
		}
		*/
		
		loadJsonModel(object,new JSONLoadHandler() {
			
			@Override
			public void loaded(Geometry geometry,JsArray<Material> materials) {
				
				/*
				LogUtils.log("force scale up x10,");
				for(int i=0;i<geometry.getVertices().length();i++){
				Vector3 vec=geometry.getVertices().get(i);
				vec.multiply(THREE.Vector3(10, 10, 10));
				}
				geometry.setVerticesNeedUpdate(true);
				*/
				
				
				onModelLoaded(file.getFileName(),geometry);
				createSkinnedMesh();
			}
		});
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		unselectAll();
	}
	private void unselectAll() {
		clearBodyPointSelections();//clear only selection
		selectVertexsByBone(-1);//not bone selected
		changeBoneSelectionColor(null);
		//TODO disable bone&weight panel
	}
	
	private static final int TYPE_UNKNOWN=0;
	private static final int TYPE_IMAGE=1;
	private static final int TYPE_JSON=2;
	private static final int TYPE_BVH=3;

	//private CheckBox force10x;

	private InfoPanelTab infoPanel;

	private CheckBox controlBone;


	private Button pauseBt;

	private Mesh wireBody;
	
	protected void onDrop(DropEvent event){
		LogUtils.log("root-drop");
		event.preventDefault();//TODO
		String[] images={"png","jpg","jpeg"};
		String[] jsons={"js","json"};
		String[] bvhs={"bvh"};
		
		
		final JsArray<File> files = FileUtils.transferToFile(event
				.getNativeEvent());

			
			final String fileName=files.get(0).getFileName();
			int type=0;
			for(String name:images){
				if(fileName.toLowerCase().endsWith("."+name)){
					type=TYPE_IMAGE;
					break;
				}
			}
			if(type==TYPE_UNKNOWN){
				for(String name:jsons){
					if(fileName.toLowerCase().endsWith("."+name)){
						type=TYPE_JSON;
						break;
					}
				}
			}
			if(type==TYPE_UNKNOWN){
				for(String name:bvhs){
					if(fileName.toLowerCase().endsWith("."+name)){
						type=TYPE_BVH;
						break;
					}
				}
			}
			final int final_type=type;
			final FileReader reader = FileReader.createFileReader();
			if (files.length() > 0) {
				reader.setOnLoad(new FileHandler() {
					@Override
					public void onLoad() {
						switch(final_type){
						case TYPE_BVH:
							onGeometryFileUploaded(files.get(0), reader.getResultAsString());
							break;
						case TYPE_IMAGE:
							onTextureFileUploaded(files.get(0), reader.getResultAsString());
							break;
						case TYPE_JSON:
							onModelFileUploaded(files.get(0), reader.getResultAsString());
							
							break;	
						
						case TYPE_UNKNOWN://never happen
						}
					}
				});
			
			if(type==TYPE_UNKNOWN){
				Window.alert("not supported file-type:"+fileName);
			}else{
				if(type==TYPE_IMAGE){
					reader.readAsDataURL(files.get(0));
				}else{
					reader.readAsText(files.get(0),"UTF-8");
				}
			}
			//TODO support multiple files
		}
	}
}
