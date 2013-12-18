package com.akjava.gwt.modelweight.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.gwt.bvh.client.threejs.AnimationBoneConverter;
import com.akjava.gwt.bvh.client.threejs.AnimationDataConverter;
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
import com.akjava.gwt.lib.client.JsonValueUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.modelweight.client.weight.GWTWeightData;
import com.akjava.gwt.modelweight.client.weight.WeighDataParser;
import com.akjava.gwt.three.client.gwt.Clock;
import com.akjava.gwt.three.client.gwt.GWTGeometryUtils;
import com.akjava.gwt.three.client.gwt.GWTThreeUtils;
import com.akjava.gwt.three.client.gwt.ThreeLog;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.akjava.gwt.three.client.gwt.animation.AnimationData;
import com.akjava.gwt.three.client.gwt.animation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.gwt.animation.WeightBuilder;
import com.akjava.gwt.three.client.gwt.collada.ColladaData;
import com.akjava.gwt.three.client.gwt.model.JSONModelFile;
import com.akjava.gwt.three.client.gwt.ui.SimpleTabDemoEntryPoint;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.core.Intersect;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.core.Projector;
import com.akjava.gwt.three.client.js.extras.GeometryUtils;
import com.akjava.gwt.three.client.js.extras.ImageUtils;
import com.akjava.gwt.three.client.js.extras.animation.Animation;
import com.akjava.gwt.three.client.js.extras.animation.AnimationHandler;
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
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
public class GWTModelWeight extends SimpleTabDemoEntryPoint{
	public static final String version="0.3(for r63)";//for three.js r63
	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		
		
		if(root!=null){
			
			boneAndVertex.getRotation().set(Math.toRadians(rotX),Math.toRadians(rotY),0,Euler.XYZ);
			boneAndVertex.getPosition().set(posX,posY,0);
			root.setPosition(positionXRange.getValue(), positionYRange.getValue(), positionZRange.getValue());
			
			root.getRotation().set(Math.toRadians(rotationRange.getValue()),Math.toRadians(rotationYRange.getValue()),Math.toRadians(rotationZRange.getValue()),Euler.XYZ);
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
	private Clock clock=new Clock();

	private Mesh mouseClickCatcher;
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		
		
	
		
		//Window.open("text/plain:test.txt:"+url, "test", null);
		
		storageControler = new StorageControler();
		canvas.setClearColorHex(0x333333);//canvas has margin?
		
		
		//scene.add(THREE.AmbientLight(0xffffff));
		
		Light pointLight = THREE.DirectionalLight(0xffffff,1);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		
		Light pointLight2 = THREE.DirectionalLight(0xffffff,1);//for fix back side dark problem
		pointLight2.setPosition(0, 10, -300);
		scene.add(pointLight2);
		
		projector=THREE.Projector();
		
		//TODO is this really need?
		mouseClickCatcher=THREE.Mesh(THREE.PlaneGeometry(100, 100, 10, 10), THREE.MeshBasicMaterial().color(0xffff00).wireFrame().build());
		mouseClickCatcher.setVisible(false);
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
		
		loadBVH(bvhUrl);
		//loadBVH("pose.bvh");//no motion
	}
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
					dialog2.setVisible(true);
					resized(screenWidth,screenHeight);//for some blackout;
				}else{
				stats.setVisible(false);
				//bottomPanel.setVisible(false);
				hideControl();
				dialog2.setVisible(false);
				}
				
			}
		});
		
		tabPanel.add(new CopyToolPanel(),"Copy");
		tabPanel.add(new MergeToolPanel(),"Merge");
		tabPanel.add(new ConvertToolPanel(),"Convert");
	}
	
	
	
	
	Object3D root;
	
	//for selection
	List<Mesh> boneJointMeshs=new ArrayList<Mesh>();
	List<Mesh> tmp=new ArrayList<Mesh>();
	private Object3D boneToSkelton(BVH bvh){
		boneJointMeshs.clear();
		AnimationBoneConverter converter=new AnimationBoneConverter();
		JsArray<AnimationBone> bones = converter.convertJsonBone(bvh);//has no endsite
		
		List<List<Vector3>> endSites=converter.convertJsonBoneEndSites(bvh);
		tmp.clear();
		Object3D group=THREE.Object3D();
		for(int i=0;i<bones.length();i++){
			AnimationBone bone=bones.get(i);
			Geometry cube=THREE.CubeGeometry(.3, .3, .3);
			int color=0x00aa00;
			if(i==0){
				//color=0x00ff00;
			}
			Mesh mesh=THREE.Mesh(cube, THREE.MeshLambertMaterial().color(color).build());
			group.add(mesh);
			Vector3 pos=GWTThreeUtils.jsArrayToVector3(bone.getPos());
			
			if(bone.getParent()!=-1){
				
				Vector3 half=pos.clone().multiplyScalar(.5);
				
				
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				pos.add(ppos);
				
				half.add(ppos);
				
				double length=ppos.clone().sub(pos).length();
				
				//half
				Mesh halfMesh=THREE.Mesh(THREE.CubeGeometry(.2, .2, length), THREE.MeshLambertMaterial().color(0xaaaaaa).build());
				group.add(halfMesh);
				halfMesh.setPosition(half);
				halfMesh.lookAt(pos);
				halfMesh.setName(bones.get(bone.getParent()).getName());
				
				boneJointMeshs.add(halfMesh);
			}
			mesh.setPosition(pos);
			mesh.setName(bone.getName());
			
			List<Vector3> sites=endSites.get(i);
			for(Vector3 end:sites){
				Mesh endMesh=THREE.Mesh(THREE.CubeGeometry(.2, .2, .2), THREE.MeshLambertMaterial().color(0x00a00aa).build());
				if(end.getX()==0 && end.getY()==0 && end.getZ()==0){
					continue;//ignore 0 
				}else{
					//LogUtils.log(bone.getName()+":"+ThreeLog.get(end));
				}
				Vector3 epos=end.clone().add(pos);
				endMesh.setPosition(epos);
				group.add(GWTGeometryUtils.createLineMesh(pos, epos, 0x888888));
				group.add(endMesh);
			}
			
			
			if(bone.getParent()!=-1){
				//AnimationBone parent=bones.get(bone.getParent());
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				Object3D line=THREE.Line(GWTGeometryUtils.createLineGeometry(pos, ppos), THREE.LineBasicMaterial().color(0x888888).build());
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
	
	
	private Projector projector;
	Label debugLabel;
	List<Integer> selections=new ArrayList<Integer>();

	private Object3D selectedObject;
	private Vector3 offset=THREE.Vector3();
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		super.onMouseUp(event);
		selectedObject=null;
	}
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
		//LogUtils.log("screen:"+screenWidth+"x"+screenHeight);
		JsArray<Intersect> intersects=projector.gwtPickIntersects(event.getX(), event.getY(), screenWidth, screenHeight, camera,objects);
		//LogUtils.log("intersects:"+intersects.length());
		for(int i=0;i<intersects.length();i++){
			Intersect sect=intersects.get(i);
			
			Object3D target=sect.getObject();
			LogUtils.log(target);
			if(!target.getName().isEmpty()){
				if(target.getName().startsWith("point:")){
					if(!target.getVisible()){
						//not selected bone
						continue;
					}
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
						clearSelections();
						
						
						selectVertex(at);
					}
					
					
					return;
				}else{//must be bone
					if(event.isShiftKeyDown()){
						continue;
					}
					
					selectedObject=target;
					Ray ray=projector.gwtCreateRay(x, y, screenWidth, screenHeight, camera);
					
					
					
					
					mouseClickCatcher.getPosition().copy( GWTThreeUtils.toPositionVec(target.getMatrixWorld()) );
					//mouseClickCatcher.getPosition().copy( target.getPosition() );
					mouseClickCatcher.updateMatrixWorld(true);//very important
					
					
					//mouseClickCatcher.lookAt(camera.getPosition());
					
					JsArray<Intersect> pintersects=ray.intersectObject(mouseClickCatcher);
					//LogUtils.log("plain:"+ThreeLog.get(pintersects.get(0).getPoint()));
					offset.copy(pintersects.get(0).getPoint()).sub(mouseClickCatcher.getPosition());
					
					
					
				clearSelections();
				selectBone(target);
				break;
				}
				
			}
			
		}
		//selectionMesh.setVisible(false);
	}
	private void clearSelections(){
		for(int index:selections){
			vertexMeshs.get(index).getMaterial().gwtGetColor().setHex(getVertexColor(index));
		}
		selections.clear();
	}
	
	private int getVertexColor(int index){
		double index1=bodyIndices.get(index).getX();
		boolean isIndex1=false;
		if(index1==selectionBoneIndex){
			isIndex1=true;
		}
		
		double v=0;
		if(isIndex1){
			v=bodyWeight.get(index).getX();
		}else{
			v=bodyWeight.get(index).getY();
		}
		
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
	}
	

	private Mesh boneSelectionMesh;
	private int selectionBoneIndex;
	private Object3D selectVertex;
	private void selectBone(Object3D target) {
		lastSelection=-1;
		if(boneSelectionMesh==null){
			boneSelectionMesh=THREE.Mesh(THREE.CubeGeometry(1, 1, 1), THREE.MeshLambertMaterial().color(0x00ff00).build());
			boneAndVertex.add(boneSelectionMesh);
		}
		boneSelectionMesh.setPosition(target.getPosition());
		selectionBoneIndex=findBoneIndex(target.getName());
		
		boneListBox.setSelectedIndex(selectionBoneIndex);
		selectVertexsByBone(selectionBoneIndex);
		
		int selectionIndex=indexWeightEditor.getArrayIndex();
		if(selectionIndex!=-1 && !vertexMeshs.get(selectionIndex).getVisible()){
			indexWeightEditor.setAvailable(false);
			updateWeightButton.setEnabled(false);
			selectionPointIndicateMesh.setVisible(false);
		}
	}
	private void selectVertexsByBone(int selectedBoneIndex) {
		LogUtils.log("selectVertexsByBone");
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
	double posY;
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		
		
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
			posX+=(double)diffX/2;
			posY-=(double)diffY/2;
			}else{
				rotX=(rotX+diffY);
				rotY=(rotY+diffX);
			}
		}
	}
	
	private InputRangeWidget positionXRange;
	private InputRangeWidget positionYRange;
	private InputRangeWidget positionZRange;
	
	private InputRangeWidget rotationRange;
	private InputRangeWidget rotationYRange;
	private InputRangeWidget rotationZRange;
	@Override
	public void createControl(DropVerticalPanelBase parent) {
		debugLabel=new Label();
		parent.add(debugLabel);
		
		stackPanel = new StackLayoutPanel(Unit.PX);
		stackPanel.setSize("220px","400px");
		parent.add(stackPanel);
		
		VerticalPanel modelPositionAndRotation=new VerticalPanel();
		modelPositionAndRotation.setWidth("100%");
		
		stackPanel.add(modelPositionAndRotation,"Model Postion&Rotation",30);
		
HorizontalPanel h1=new HorizontalPanel();
		
		rotationRange = InputRangeWidget.createInputRange(-180,180,0);
		modelPositionAndRotation.add(HTML5Builder.createRangeLabel("X-Rotate:", rotationRange));
		modelPositionAndRotation.add(h1);
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
		positionXRange.setValue(-13);
		
		parent.add(new Label("Play Control"));
		Button bt=new Button("Pause/Play SkinnedMesh");
		parent.add(bt);
		bt.addClickHandler(new ClickHandler() {
			
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
		
		
		//editor
		
		VerticalPanel boneAndWeight=new VerticalPanel();
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
		
		updateWeightButton = new Button("Update Weight");
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
		
		boneAndWeight.add(new Label("Auto-Weight"));
		final ListBox autoWeightBox=new ListBox();
		autoWeightBox.addItem("From Geometry", "4");
		autoWeightBox.addItem("test", "6");
		autoWeightBox.addItem("ParentAndChildren Agressive", "5");
		autoWeightBox.addItem("ParentAndChildren", "3");
		autoWeightBox.addItem("NearAgressive", "2");
		autoWeightBox.addItem("NearSingleBon", "0");
		autoWeightBox.addItem("NearSpecial", "1");
		
		autoWeightBox.setSelectedIndex(1);
		boneAndWeight.add(autoWeightBox);
		autoWeightBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateAutoWeight(autoWeightBox.getValue(autoWeightBox.getSelectedIndex()));
			}
		});
		
		
		//DONT need?
		useBasicMaterial = new CheckBox();
		useBasicMaterial.setText("Use Basic Material");
		//parent.add(useBasicMaterial);
		
		VerticalPanel loadAndExport=new VerticalPanel();
		stackPanel.add(loadAndExport,"Load Datas",30);
		
		loadAndExport.add(new Label("Bone(BVH Motion file)"));
		final Label bvhSelection=new Label("selection:"+bvhUrl);
		bvhSelection.setStylePrimaryName("gray");
		loadAndExport.add(bvhSelection);
		
		final FileUploadForm bvhUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				parseBVH(value);
				bvhSelection.setText("selection:"+file.getFileName());
			}
		}, true);
		bvhUpload.setShowDragOverBorder(true);
		
		loadAndExport.add(bvhUpload);
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
		
		loadAndExport.add(new Label("Character(Three.js model file)"));
		modelSelection = new Label("selection:"+modelUrl);
		modelSelection.setStylePrimaryName("gray");
		loadAndExport.add(modelSelection);
		
		final FileUploadForm meshUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(final File file, String value) {
				JSONObject object=parseJsonObject(value);
				lastJsonObject=object;
				loadJsonModel(object,new JSONLoadHandler() {
					
					@Override
					public void loaded(Geometry geometry,JsArray<Material> materials) {
						
						onModelLoaded(file.getFileName(),geometry);
						createSkinnedMesh();
					}
				});
			}
		}, true);
		meshUpload.setShowDragOverBorder(true);
		loadAndExport.add(meshUpload);
	
		
		
		loadAndExport.add(new Label("Texture(PNG or JPEG)"));
		final Label textureSelection=new Label("selection:"+textureUrl);
		textureSelection.setStylePrimaryName("gray");
		final FileUploadForm textureUpload=FileUtils.createSingleFileUploadForm(new DataURLListener() {	
			@Override
			public void uploaded(File file, String value) {
				textureUrl=value;
				generateTexture();
				//createSkinnedMesh();
				textureSelection.setText("selection:"+file.getFileName());
			}
		}, true);
		textureUpload.setShowDragOverBorder(true);
		
		loadAndExport.add(textureSelection);
		loadAndExport.add(textureUpload);
		
		
		loadAndExport.add(new Label("Weights and Idecis(json)"));
		weightSelection = new Label("selection:");
		weightSelection.setStylePrimaryName("gray");
		final FileUploadForm weightUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(final File file, String value) {
				JSONObject object=parseJsonObject(value);
				
				//dont need json object,just use weights and indecis info
				loadJsonModel(object,new JSONLoadHandler() {
					
					@Override
					public void loaded(Geometry geometry,JsArray<Material> materials) {
						onWeightLoaded(file.getFileName(), geometry, materials);
					}

				});
			}
		}, true);
		weightUpload.setShowDragOverBorder(true);
		weightUpload.setTitle("upload json model but only load weight&indecis");
		
		
		loadAndExport.add(weightSelection);
		loadAndExport.add(weightUpload);
		
		VerticalPanel export=new VerticalPanel();
		stackPanel.add(export,"Export Datas",30);
		
		export.add(new Label("you can load form pose editor preference"));
		Button webstorage=new Button("Export in WebStorage");
		webstorage.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				exportWebStorage();
			}
		});
		export.add(webstorage);
		
		
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
		
		createTabs();
		//loadAndExport.add(new Label("Dont export large BVH.large(10M?) text data crash browser"));
		showControl();
	}
	
	private boolean needFlipY=true;//default
	private void onModelLoaded(String fileName,Geometry geometry){
		LogUtils.log("onModuleLoaded");
		if(lastJsonObject!=null){//TODO temporaly
		JSONModelFile modelFile=(JSONModelFile) lastJsonObject.getJavaScriptObject();
		//old version 3.0 need flip-Y
		//see https://github.com/mrdoob/three.js/wiki/Migration#r49--r50
		if(modelFile.getMetaData().getFormatVersion()==3){
			needFlipY=false;
		}
		}
		
		initializeObject();
		
		loadedGeometry=geometry;
		
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
	}

	private void setWeigthFromGeometry(Geometry geometry) {
		//loadedGeometry
		//LogUtils.log(geometry);
		//LogUtils.log(geometry.getSkinIndices());
		//LogUtils.log(""+geometry.getSkinIndices().length());
		if(geometry.getSkinIndices().length()==0){
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
	
	protected void updateAutoWeight(String value) {
		int selectedValue=Integer.parseInt(value);
		if(selectedValue!=-1){
			bodyIndices = (JsArray<Vector4>) JsArray.createArray();
			bodyWeight = (JsArray<Vector4>) JsArray.createArray();
			if(loadedGeometry.getSkinIndices().length()!=0 && loadedGeometry.getSkinWeight().length()!=0){
				WeightBuilder.autoWeight(loadedGeometry, bones, endSites,selectedValue, bodyIndices, bodyWeight);
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
			if(vertexMeshs.get(i).getVisible()){
				selectVertex(i);
				return;
			}
		}
		for(int i=vertexMeshs.size()-1;i>currentSelection;i--){
			if(vertexMeshs.get(i).getVisible()){
				selectVertex(i);
				return;
			}
		}
	}
	protected void selectNextVertex() {
		int currentSelection=indexWeightEditor.getArrayIndex();
		for(int i=currentSelection+1;i<vertexMeshs.size();i++){
			if(vertexMeshs.get(i).getVisible()){
				selectVertex(i);
				return;
			}
		}
		for(int i=0;i<currentSelection;i++){
			if(vertexMeshs.get(i).getVisible()){
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
		
		//LogUtils.log("select:"+index+","+we.getX()+","+we.getY());
		
		//change selected point to selection color,multiple selection is avaiable and to detect change point color.
		vertexMeshs.get(index).getMaterial().gwtGetColor().setHex(selectColor);
		
		indexWeightEditor.setAvailable(true);
		updateWeightButton.setEnabled(true);
		
		indexWeightEditor.setValue(index, in, we);
		
		//show which point selection,with wireframe weight-color
		selectionPointIndicateMesh.setVisible(true);
		selectionPointIndicateMesh.setPosition(vertexMeshs.get(index).getPosition());
		selectionPointIndicateMesh.getMaterial().gwtGetColor().setHex(getVertexColor(index));
		
		stackPanel.showWidget(1); //bone & weight panel
		lastSelection=index;
	}

	/*
	 * add bones and skinIndices & skinWeights to last selected model
	 * animation is option.
	 * so model format version keep(not touch uv)
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
		
		return lastJsonObject.toString();
	}
	public static String getGeneratedBy(){
		return "GWTModel-Weight ver"+GWTModelWeight.version;
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
	
	
	public native final void exportTextChrome(String text,String wname)/*-{
	win = $wnd.open("", wname)
	win.document.body.innerText =""+text+"";
	}-*/;
	
	private boolean paused=true;
	private void loadBVH(String path){
	
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(path));

			try {
				builder.sendRequest(null, new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						
						String bvhText=response.getText();
						//LogUtils.log("loaded:"+Benchmark.end("load"));
						//useless spend allmost time with request and spliting.
						parseBVH(bvhText);

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
	
	
	
	private void parseBVH(String bvhText){
		final BVHParser parser=new BVHParser();
		
		parser.parseAsync(bvhText, new ParserListener() {
			
			

			

			
			
			
			private AnimationData animationData;

			@Override
			public void onSuccess(BVH bv) {
				
				
				bvh=bv;
				
				
				//bvh.setSkips(10);
				//bvh.setSkips(skipFrames);
				AnimationBoneConverter converter=new AnimationBoneConverter();
				bones = converter.convertJsonBone(bvh);
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
					frameRange.setMax(bvh.getFrames()-1);
				}
				
				animationData = dataConverter.convertJsonAnimation(bones,bvh);
				//for(int i=0;i<animationData.getHierarchy().length();i++){
					AnimationHierarchyItem item=animationData.getHierarchy().get(0);
					for(int j=0;j<item.getKeys().length();j++){
						item.getKeys().get(j).setPos(0, 0, 0);//dont move;
					}
				//}
				rawAnimationData=dataConverter.convertJsonAnimation(bones,bvh);//for json
				animationName=animationData.getName();
				 JsArray<AnimationHierarchyItem> hitem=animationData.getHierarchy();
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
				AnimationHandler.add(animationData);
				//LogUtils.log(data);
				//LogUtils.log(bones);
				
				JSONArray array=new JSONArray(bones);
				//LogUtils.log(array.toString());
				
				JSONObject test=new JSONObject(animationData);
				//LogUtils.log(test.toString());
				
				
				texture=ImageUtils.loadTexture(textureUrl);
				texture.setFlipY(false);//for temporaly old model
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
			
			@Override
			public void onFaild(String message) {
				LogUtils.log(message);
			}
		});
	}
	private String animationName;
	private Geometry loadedGeometry;

	private Mesh selectionPointIndicateMesh;
	
	//used for mouse selection ,added mesh's eash point and bone-joint.
	JsArray<Object3D> objects;
	
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
		boneAndVertex.setPosition(-15, 0, 0);
		scene.add(boneAndVertex);
		Object3D bo=boneToSkelton(bvh);
		//bo.setPosition(-30, 0, 0);
		boneAndVertex.add(bo);
		
		selectionPointIndicateMesh=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5), THREE.MeshBasicMaterial().color(selectColor).transparent(true).wireFrame(true).build());
		boneAndVertex.add(selectionPointIndicateMesh);
		selectionPointIndicateMesh.setVisible(false);	
		
	}
	private int selectColor=0xb362ff;
	
	//for selection
	private List<Mesh> wireBodyPoints=new ArrayList<Mesh>();
	//private int defaultColor=0xffff00;
	private void createWireBody(){
		wireBodyPoints.clear();
		bodyGeometry=GeometryUtils.clone(loadedGeometry);
		Mesh wireBody=THREE.Mesh(bodyGeometry, THREE.MeshBasicMaterial().wireFrame(true).color(0xffffff).build());
		boneAndVertex.add(wireBody);
		
		selectVertex=THREE.Object3D();
		vertexMeshs.clear();
		
		boneAndVertex.add(selectVertex);
		Geometry cube=THREE.CubeGeometry(.2, .2, .2);
		
		for(int i=0;i<bodyGeometry.vertices().length();i++){
			Material mt=THREE.MeshBasicMaterial().color(getVertexColor(i)).build();
			Vector3 vx=bodyGeometry.vertices().get(i);
			Mesh point=THREE.Mesh(cube, mt);
			point.setVisible(false);
			point.setName("point:"+i);
			point.setPosition(vx);
			selectVertex.add(point);
			vertexMeshs.add(point);
			wireBodyPoints.add(point);
		}
	}
	private void autoWeight(){
		bodyIndices = (JsArray<Vector4>) JsArray.createArray();
		bodyWeight = (JsArray<Vector4>) JsArray.createArray();
		if(loadedGeometry.getSkinIndices().length()!=0 && loadedGeometry.getSkinWeight().length()!=0){
			WeightBuilder.autoWeight(loadedGeometry, bones, endSites,WeightBuilder.MODE_FROM_GEOMETRY, bodyIndices, bodyWeight);
		}else{
			LogUtils.log("empty indices&weight auto-weight from geometry:");
			WeightBuilder.autoWeight(loadedGeometry, bones, endSites,WeightBuilder.MODE_MODE_Start_And_Half_ParentAndChildrenAgressive, bodyIndices, bodyWeight);
			}
	}
	
	private void loadJsonModel(JSONObject object,JSONLoadHandler handler){
		JSONLoader loader=THREE.JSONLoader();
		JavaScriptObject jsobject=loader.parse(object.getJavaScriptObject(), null);
		JSONObject newobject=new JSONObject(jsobject);
		
		
		handler.loaded((Geometry) newobject.get("geometry").isObject().getJavaScriptObject(),null);
		//LogUtils.log(object.getJavaScriptObject());
		//loader.createModel(object.getJavaScriptObject(), handler, "");
		//loader.onLoadComplete();
		
		//return object;
		
	}
	//TODO more validate model format
	private JSONObject parseJsonObject(String jsonText){
		JSONValue lastJsonValue = JSONParser.parseLenient(jsonText);
		JSONObject object=lastJsonValue.isObject();
		if(object==null){
			LogUtils.log("invalid-json object");
		}
		return object;
	}
	
	private void loadModel(String path,final JSONLoadHandler handler){
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(path));
			try {
				builder.sendRequest(null, new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						JSONObject object=parseJsonObject(response.getText());
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
		
		
	private String textureUrl="female001_texture1.jpg";
	private String bvhUrl="walking.bvh";
	private String modelUrl="female001.json";
	
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
				texture=THREE.Texture(canvas.getCanvasElement());
				texture.setNeedsUpdate(true);
				//Window.open(canvas.toDataUrl(), "test", null);
				img.removeFromParent();
				updateMaterial();
			}
		});
		
		
		
	}
	
	protected void updateMaterial() {
		
		Material material=null;
		
		texture.setFlipY(needFlipY);//for temporary release //TODO as option for 3.1 format models
		
		if(!needFlipY){
			LogUtils.log("texture flipY:false for old 3.0 format");
		}
		
		if(useBasicMaterial.getValue()){
			material=THREE.MeshBasicMaterial().skinning(true).color(0xffffff).map(texture).build();
		}else{
			material=THREE.MeshLambertMaterial().skinning(true).color(0xffffff).map(texture).build();
		}
		
		
		if(skinnedMesh!=null){
			skinnedMesh.setMaterial(material);
		}
	}
	private void createSkinnedMesh(){
		LogUtils.log("createSkinnedMesh");
		//LogUtils.log(bones);
		JsArray<AnimationBone> clonedBone=cloneBones(bones);
		//this is not work fine.just remove root moving to decrese flicking
		AnimationBoneConverter.setBoneAngles(clonedBone, rawAnimationData, 0);
		//LogUtils.log(clonedBone);
		Geometry newgeo=GeometryUtils.clone(loadedGeometry);
		newgeo.setSkinIndices(bodyIndices);
		newgeo.setSkinWeight(bodyWeight);
		newgeo.setBones(clonedBone);
		if(skinnedMesh!=null){
			root.remove(skinnedMesh);
		}
		Material material=null;
		if(useBasicMaterial.getValue()){
			material=THREE.MeshBasicMaterial().skinning(true).color(0xffffff).map(texture).build();
			
		}else{
			material=THREE.MeshLambertMaterial().skinning(true).color(0xffffff).map(texture).build();
		}
		skinnedMesh = THREE.SkinnedMesh(newgeo, material);
		root.add(skinnedMesh);
		
		if(animation!=null){
			AnimationHandler.removeFromUpdate(animation);
		}
		animation = THREE.Animation( skinnedMesh, animationName );
		animation.play(true,currentTime);
	}
	
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
	
}
