package com.akjava.gwt.modelweight.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropVerticalPanelBase;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.java.bone.CloseVertexAutoWeight;
import com.akjava.gwt.three.client.java.ui.SimpleTabDemoEntryPoint;
import com.akjava.gwt.three.client.java.ui.experiments.Vector4Editor;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.extras.helpers.VertexNormalsHelper;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTModelWeight extends SimpleTabDemoEntryPoint{
	public static final String version="6.00(for r74)";//for three.js r74
	private StorageControler storageControler;
	private Mesh mouseClickCatcher;
	
	private OrbitControls trackballControls;

	//private TrackballControls trackballControls;

	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		
		if(mixer!=null){
			mixer.update(1.0/60);
		}
		
		if(trackballControls!=null){
			trackballControls.update();
			//LogUtils.log(camera.getUuid());
		}
		
		if(baseSkinnedModelMesh!=null){
		baseSkinnedModelMesh.getPosition().set(basePosEditor.getX(), basePosEditor.getY(), basePosEditor.getZ());
		}
		
		if(baseSkinnedModelWireMesh!=null){
			baseSkinnedModelWireMesh.getPosition().set(wirePosEditor.getX(), wirePosEditor.getY(), wirePosEditor.getZ());
			
		}
		
		if(editingGeometryNormalsHelper!=null){
			editingGeometryNormalsHelper.update();//need?
		}
	}

	private String textureUrl="underware-nonipple.png";
	private String modelUrl="merged3.json";//testing 4-point
	
	
	private int selectedTabIndex;
	final int WEIGHT_TAB_INDEX=3;
	public void updateSelectedTabIndex(){
		if(selectedTabIndex==WEIGHT_TAB_INDEX){
			editingGeometryVertexSelector.setVisible(true);
			editingGeometryNormalsHelper.setVisible(true);
		}else{
			editingGeometryVertexSelector.setVisible(false);
			editingGeometryNormalsHelper.setVisible(false);
		}
	}
	
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		
		
		//Window.open("text/plain:test.txt:"+url, "test", null);
		
		storageControler = new StorageControler();
		canvas.setClearColor(0xaaaaaa);//canvas has margin?
		
		
		
		scene.add(THREE.AmbientLight(0x444444));
		Light pointLight = THREE.DirectionalLight(0xaaaaaa,1);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		
		Light pointLight2 = THREE.DirectionalLight(0xaaaaaa,1);//for fix back side dark problem
		pointLight2.setPosition(0, 10, -300);
		scene.add(pointLight2);
		
		mouseClickCatcher=THREE.Mesh(THREE.PlaneGeometry(100, 100, 10, 10),
				
				THREE.MeshBasicMaterial(GWTParamUtils.MeshBasicMaterial().color(0xffff00).wireframe(true)
						.visible(false)));//hide and catch mouse
		mouseClickCatcher.setVisible(true); //now Ray only check visible object and to hide use material's visible
		
		scene.add(mouseClickCatcher);
		
		nearCamera=0.01;
		updateCamera(scene,screenWidth , screenHeight);
		
		cameraZ=4;
		
		
		
		
		
		autoUpdateCameraPosition=false;
		
		//showControl();
	
		loadBaseSkinnedModel(modelUrl);
		
		mouseSelector = new Object3DMouseSelecter(renderer, camera);
		
	}


	private JSONObject baseSkinnedModelJson;
	private Geometry baseSkinnedModelGeometry;
	private SkinnedMesh baseSkinnedModelMesh;
	private Vector4Editor basePosEditor;
	private Mesh baseSkinnedModelWireMesh;
	private BoneVertexColorTools baseVertexColorTools;
	private BoneVertexColorTools editingVertexColorTools;
	private ValueListBox<AnimationBone> boneListBox;
	private List<AnimationBone> baseSkinnedMeshBones;
	private Object3DMouseSelecter mouseSelector;
	private Group boneMeshGroup;
	private BoneMeshMouseSelector boneMouseSelector;
	private Label editingGeometryName;
	private Vector4Editor wirePosEditor;
	private void loadBaseSkinnedModel(String modelUrl) {
		THREE.XHRLoader().load(modelUrl, new XHRLoadHandler() {
			@Override
			public void onLoad(String text) {
				JSONValue jsonValue=JSONParser.parseStrict(text);
				JSONObject object=jsonValue.isObject();
				if(object==null){
					Window.alert("invalid model");
					return;
				}
				baseSkinnedModelJson=object;
				baseSkinnedModelGeometry=THREE.JSONLoader().parse(baseSkinnedModelJson.getJavaScriptObject()).getGeometry();
				
				createBaseSkinnedMesh();
				createBaseWireMesh();
				createBoneMeshs();
				updateBoneListBox();
				
				vertexBoneDataEditor.setBone(baseSkinnedModelGeometry.getBones());
				
				mixer=THREE.AnimationMixer(baseSkinnedModelMesh);
				
				//test
				final String url="tshirt.json";
				THREE.XHRLoader().load(url, new XHRLoadHandler() {
					@Override
					public void onLoad(String text) {
						loadEditingGeometry(url, text);
					}
				});
			}
		});
	}

	protected void createBoneMeshs() {
		BoneMeshTools bmt=new BoneMeshTools();
		boneMeshGroup = bmt.createBoneMeshs(baseSkinnedModelGeometry.getBones());
		scene.add(boneMeshGroup);
		
		boneMouseSelector = new BoneMeshMouseSelector(boneMeshGroup, baseSkinnedModelGeometry.getBones(), renderer, camera);
	}

	protected void updateBoneListBox() {
		baseSkinnedMeshBones = JavaScriptUtils.toList(baseSkinnedModelGeometry.getBones());
		boneListBox.setValue(baseSkinnedMeshBones.get(0));
		boneListBox.setAcceptableValues(baseSkinnedMeshBones);
	}

	protected void createBaseWireMesh(){
		
		if(baseSkinnedModelWireMesh!=null){
			scene.remove(baseSkinnedModelWireMesh);
		}
		
		Geometry bodyGeometry=baseSkinnedModelGeometry.clone();
		bodyGeometry.setSkinIndices(baseSkinnedModelGeometry.getSkinIndices());
		bodyGeometry.setSkinWeights(baseSkinnedModelGeometry.getSkinWeights());
		bodyGeometry.setBones(baseSkinnedModelGeometry.getBones());
		
baseSkinnedModelWireMesh = THREE.Mesh(bodyGeometry, 
				
				THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial()
						.wireframe(true)
						.color(0xffffff)
						.vertexColors(THREE.VertexColors)
						)
				);

//watch out once set color can't replace color.use setHex()

		
		
		scene.add(baseSkinnedModelWireMesh);
		
		baseVertexColorTools = new BoneVertexColorTools(bodyGeometry);
	
		//make a class handle vertex color
	}
protected void createEditingWireMesh(){
		
		if(editingGeometryWireMesh!=null){
			scene.remove(editingGeometryWireMesh);
		}
		
		
		editingGeometryWireMesh = THREE.Mesh(editingGeometry, 
				
				THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial()
						.wireframe(true)
						.color(0xffffff)
						.vertexColors(THREE.VertexColors)
						)
				);

		scene.add(editingGeometryWireMesh);
		editingVertexColorTools = new BoneVertexColorTools(editingGeometry);
	}
	
	protected void createBaseSkinnedMesh() {
		Texture texture=THREE.TextureLoader().load(textureUrl);
		MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial().skinning(true).map(texture));
		
		if(baseSkinnedModelMesh!=null){
			scene.remove(baseSkinnedModelMesh);
		}
		baseSkinnedModelMesh = THREE.SkinnedMesh(baseSkinnedModelGeometry, material);
		scene.add(baseSkinnedModelMesh);
		
		baseSkinnedModelGeometry.computeBoundingBox();
		ThreeLog.log("base",baseSkinnedModelGeometry.getBoundingBox());
		
		cameraY=baseSkinnedModelGeometry.getBoundingBox().getMax().getY()/2;
		
		
		//this position used 
		camera.getPosition().set(cameraX, cameraY, cameraZ);
		
		if(trackballControls!=null){
			trackballControls.dispose();
		}
		
		//trackball initialize here
		
		//i feeel orbit is much stable on y-axis
		trackballControls=THREEExp.OrbitControls(camera,canvas.getElement());
		
	//	trackballControls=THREEExp.TrackballControls(camera,canvas.getElement());
		//trackballControls.setRotateSpeed(10);
		
		trackballControls.setTarget(THREE.Vector3(0,cameraY,0));
		
	}
	
	protected void createEditingGeometrySkinnedMesh() {
		MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial().skinning(true).color(0x880000));
		
		if(editingGeometryMesh!=null){
			//not scene ,
			baseSkinnedModelMesh.remove(editingGeometryMesh);
		}
		
		//can't use editingGeometry,maybe Indices & weights registered somewhere.need new geometry to update indicis&weight
		Geometry geometry=editingGeometry.clone();
		//lightCopy
		geometry.setSkinIndices(editingGeometry.getSkinIndices());
		geometry.setSkinWeights(editingGeometry.getSkinWeights());
		geometry.setBones(editingGeometry.getBones());
		
		editingGeometryMesh = THREE.SkinnedMesh(geometry, material);
		
		editingGeometryMesh.setSkeleton(baseSkinnedModelMesh.getSkeleton());//share bone to work same mixer
		baseSkinnedModelMesh.add(editingGeometryMesh);//share same position,rotation
		
		LogUtils.log("createEditingGeometrySkinnedMesh");
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseClick(ClickEvent event) {
		if(selectedTabIndex==WEIGHT_TAB_INDEX){
			if(editingGeometryVertexSelector!=null){
				int vertex=editingGeometryVertexSelector.pickVertex(event);
				//LogUtils.log("picked "+vertex);
				if(vertex!=-1){
					vertexBoneDataEditor.setValue(VertexBoneData.createFromdMesh(editingGeometry, vertex));
				}else{
					vertexBoneDataEditor.setValue(null);
				}
			}
			
			return;
		}
		//when other tab selected
		
		if(boneMouseSelector!=null){
			int index=boneMouseSelector.pickBone(event);
			
			//boneListBox would update wireframe weights color
			if(index==-1){
				//no need unselect,
				//boneListBox.setValue(null,true);
			}else{
				boneListBox.setValue(baseSkinnedMeshBones.get(index),true);
			}
			
		}
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void createControl(DropVerticalPanelBase parent) {
		TabPanel tab=new TabPanel();
		parent.add(tab);
		
		VerticalPanel posPanel=new VerticalPanel();
		tab.add(posPanel,"Pos&Rot");
		tab.selectTab(0);
		
		posPanel.add(new HTML("<h4>Base Model</h4>"));
		
		basePosEditor = new Vector4Editor("Position",-2, 2, 0.001, 0);
		posPanel.add(basePosEditor);
		basePosEditor.setX(-1.5,true);
		
		wirePosEditor = new Vector4Editor("Position",-2, 2, 0.001, 0);
		posPanel.add(wirePosEditor);
		
		posPanel.add(new HTML("<h4>Camera</h4>"));
		Button resetCamera=new Button("Reset",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				trackballControls.reset();
				trackballControls.setTarget(THREE.Vector3(0,cameraY,0));
				
			}
		});
		posPanel.add(resetCamera);
		
		
		VerticalPanel bonePanel=new VerticalPanel();
		tab.add(bonePanel,"Bone");
		
		boneListBox = new ValueListBox<AnimationBone>(new Renderer<AnimationBone>() {

			@Override
			public String render(AnimationBone object) {
				if(object==null){
					return "";
				}
				return object.getName();
			}

			@Override
			public void render(AnimationBone object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		bonePanel.add(boneListBox);
		boneListBox.addValueChangeHandler(new ValueChangeHandler<AnimationBone>() {
			@Override
			public void onValueChange(ValueChangeEvent<AnimationBone> event) {
				onBoneSelectionChanged(event.getValue());
			}
		});
		
		
		//create load/export
		
		
		tab.add(createLoadExportPanel(),"Load/Export");
		
		
		//create weight
		
		
		tab.add(createWeightPanel(),"Weight");
		
		
		
		
		
		
		tab.add(createAnimationPanel(),"Animation");
		
		tab.selectTab(4);
		
		
		tab.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				selectedTabIndex=event.getSelectedItem();
				updateSelectedTabIndex();
			}
		});
		showControl();
	}
	
	private Button makeAnimationButton(String name,final String url){
		return new Button(name,new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				THREE.XHRLoader().load(url, new XHRLoadHandler() {
					
					@Override
					public void onLoad(String text) {
						loadAnimation(text);
					}

					
				});
			}
		});
	}
	private Panel createAnimationPanel(){
		VerticalPanel animationPanel=new VerticalPanel();
		HorizontalPanel controls=new HorizontalPanel();
		animationPanel.add(controls);
		controls.add(new Button("Stop",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopAnimation();
			}
		}));
		controls.add(new Button("Pause",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mixer.setTimeScale(0);
			}
		}));
		controls.add(new Button("UnPause",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mixer.setTimeScale(1);
			}
		}));
		controls.add(new Button("Step",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mixer.setTimeScale(1);
				mixer.update(1.0/60);
				mixer.setTimeScale(0);
			}
		}));
		HorizontalPanel animations=new HorizontalPanel();
		animationPanel.add(animations);
		animations.add(makeAnimationButton("animation1", "animation/animation0.json"));
		animations.add(makeAnimationButton("animation2", "animation/animation2.json"));
		animations.add(makeAnimationButton("animation3", "animation/animation4.json"));
		return animationPanel;
	}

	public void stopAnimation() {
		if(mixer==null){
			return;
		}
		mixer.stopAllAction();
		
		//characterMesh.getGeometry().getBones().get(60).setRotq(q)
	}
	
	private AnimationMixer mixer;
	private void loadAnimation(String text) {
		JSONValue object=JSONParser.parseStrict(text);
		JavaScriptObject js=object.isObject().getJavaScriptObject();
		AnimationClip animationClip = AnimationClip.parse(js);
		
		playAnimation(animationClip);
	}
	
	public void playAnimation(AnimationClip clip) {
		mixer.setTimeScale(1);//if paused
		mixer.stopAllAction();
		mixer.uncacheClip(clip);//reset can cache
		mixer.clipAction(clip).play();
		
	}
	
	private Panel createLoadExportPanel(){
		VerticalPanel loadExportPanel=new VerticalPanel();
		HorizontalPanel f1=new HorizontalPanel();
		loadExportPanel.add(f1);
		f1.add(new Label("Name:"));
		editingGeometryName = new Label();
		f1.add(editingGeometryName);
		FileUploadForm geometryUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				loadEditingGeometry(file.getFileName(),text);
			}
		}, true).setAccept(FileUploadForm.ACCEPT_JAVASCRIPT);
		
		loadExportPanel.add(geometryUpload);
		
		final HorizontalPanel downloadPanel=new HorizontalPanel();
		loadExportPanel.add(downloadPanel);
		
		Button exportButton=new Button("Export",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JSONObject object=editingGeometry.gwtJSONWithBone();
				downloadPanel.clear();
				
				Anchor a=HTML5Download.get().generateTextDownloadLink(object.toString(), "geometry-weight-modified.json", "geometry to download",true);
				downloadPanel.add(a);
			}
		});
		loadExportPanel.add(exportButton);
		
		return loadExportPanel;
		
	}
	
	private Panel createWeightPanel(){
		VerticalPanel weightPanel=new VerticalPanel();
	
		Button testClearAll=new Button("Test clear all",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(int i=0;i<editingGeometry.getSkinIndices().length();i++){
					
					for(int j=0;j<4;j++){
						editingGeometry.getSkinIndices().get(i).gwtSet(j, 0);
						editingGeometry.getSkinWeights().get(i).gwtSet(j, 0);
					}
				}
				createEditingGeometrySkinnedMesh();//weight updated
				LogUtils.log(editingGeometryMesh);
				//editingGeometryMesh.setVisible(false);
					AnimationBone bone=boneListBox.getValue();
					if(bone!=null){
						onBoneSelectionChanged(bone);
					}
				
			}
		});
		weightPanel.add(testClearAll);
		
		Button removeInfluenceButton=new Button("Remove selected bone influence",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				 boolean neddUpdate=false;
				AnimationBone bone=boneListBox.getValue();
				if(bone!=null){
					int boneIndex=boneMouseSelector.getBoneIndex(bone);
					
					JsArray<Vector4> indices=editingGeometry.getSkinIndices();
					JsArray<Vector4> weights=editingGeometry.getSkinWeights();
					for(int i=0;i<indices.length();i++){
						boolean modified=false;
						for(int j=0;j<4;j++){
							int index=indices.get(i).gwtGet(j);
							if(index==boneIndex){
								weights.get(i).gwtSet(j, 0);
								modified=true;
								neddUpdate=true;
							}
						}
						if(modified){
							autobalanceWeight(weights.get(i));
						}
					}
					
				}
				if(neddUpdate){
					createEditingGeometrySkinnedMesh();//weight updated
				}
				onBoneSelectionChanged(bone);
			}
		});
		weightPanel.add(removeInfluenceButton);
		
		weightPanel.add(new HTML("<h4>Skinning Indices & Weight</h4>"));
		vertexBoneDataEditor = new VertexBoneDataEditor();
		weightPanel.add(vertexBoneDataEditor);
		//for test
		
		//need set bone
		HorizontalPanel buttons=new HorizontalPanel();
		weightPanel.add(buttons);
		Button applyBt=new Button("Apply",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(vertexBoneDataEditor.getValue()==null){
					return;
				}
				int index=vertexBoneDataEditor.getValue().getVertexIndex();
				vertexBoneDataEditor.flush();
				createEditingGeometrySkinnedMesh();
				ThreeLog.log("vertex-index:"+index);
				ThreeLog.log("indices:",editingGeometryMesh.getGeometry().getSkinIndices().get(index));
				ThreeLog.log("weight:",editingGeometryMesh.getGeometry().getSkinWeights().get(index));
			}
		});
		buttons.add(applyBt);
		applyBt.setWidth("120px");
		Button cancelBt=new Button("Cancel Edit",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(vertexBoneDataEditor.getValue()==null){
					return;
				}
				int index=vertexBoneDataEditor.getValue().getVertexIndex();
				vertexBoneDataEditor.setValue(VertexBoneData.createFromdMesh(editingGeometry, index));
				//no effect when flushd
				//vertexBoneDataEditor.flush();
				//createEditingGeometrySkinnedMesh();
			}
		});
		buttons.add(cancelBt);
		
		Button resetBt=new Button("Rest Origin",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(vertexBoneDataEditor.getValue()==null){
					return;
				}
				int index=vertexBoneDataEditor.getValue().getVertexIndex();
				vertexBoneDataEditor.setValue(VertexBoneData.createFromdMesh(editingGeometryOrigin, index));
				vertexBoneDataEditor.flush();
				createEditingGeometrySkinnedMesh();
			}
		});
		buttons.add(resetBt);
		
		return weightPanel;
	}
	
	/** @deprecated
	 *  i don know how to update,without recreate skinnedmesh maybe attribute?
	 * @param index
	 */
	public void copyIndicesAndWeightToSkinningMesh(int index){
		editingGeometryMesh.getGeometry().getSkinIndices().get(index).copy(editingGeometry.getSkinIndices().get(index));
		editingGeometryMesh.getGeometry().getSkinWeights().get(index).copy(editingGeometry.getSkinWeights().get(index));
	}
	
	protected void autobalanceWeight(Vector4 vector4) {
		double total=0;
		for(int i=0;i<4;i++){
			total+=vector4.gwtGet(i);
		}
		for(int i=0;i<4;i++){
			if(total!=0){
			vector4.gwtSet(i, vector4.gwtGet(i)/total);
			}else{
			vector4.gwtSet(i,0);//need?
			}
		}
	}

	protected void onBoneSelectionChanged(AnimationBone value) {
		if(value==null){
		baseVertexColorTools.clearVertexsColor();	
		}else{
		 baseVertexColorTools.updateVertexsColorByBone(baseSkinnedMeshBones.indexOf(value));
		}
		
		if(editingVertexColorTools!=null){
			if(value==null){
				editingVertexColorTools.clearVertexsColor();	
				}else{
					editingVertexColorTools.updateVertexsColorByBone(baseSkinnedMeshBones.indexOf(value));
				}
		}
	}

	private Geometry editingGeometryOrigin;
	private Geometry editingGeometry;
	private SkinnedMesh editingGeometryMesh;
	private Mesh editingGeometryWireMesh;
	private VertexNormalsHelper editingGeometryNormalsHelper;
	private MeshVertexSelector editingGeometryVertexSelector;
	private VertexBoneDataEditor vertexBoneDataEditor;
	protected void loadEditingGeometry(String fileName, String text) {
		editingGeometryName.setText(fileName);
		
		JSONValue json=JSONParser.parseStrict(text);
		
		//TODO check version & type
		
		JSONObject jsonObject=json.isObject();
		
		boolean hasData=jsonObject.get("data")!=null;//4.* format has this
		
		if(hasData){
			jsonObject=jsonObject.get("data").isObject();
		}
		
		
		
		editingGeometryOrigin=THREE.JSONLoader().parse(jsonObject.getJavaScriptObject()).getGeometry();
		
		//TODO bone check
		
		//TODO make gwtCloneWithBones()
		
		
		editingGeometry=THREE.JSONLoader().parse(jsonObject.getJavaScriptObject()).getGeometry();
	
		double influencesPerVertex=jsonObject.get("influencesPerVertex")!=null?jsonObject.get("influencesPerVertex").isNumber().doubleValue():2;
		editingGeometry.gwtSetInfluencesPerVertex((int)influencesPerVertex);//used when export
		
		LogUtils.log(editingGeometry);
		if(editingGeometry.getSkinIndices()==null || editingGeometry.getSkinIndices().length()==0){
			//Window.alert("has no skin indices");
			LogUtils.log("No skin indices.it would auto weight.");
			new CloseVertexAutoWeight().autoWeight(editingGeometry, baseSkinnedModelGeometry).insertToGeometry(editingGeometry);
			editingGeometry.gwtSetInfluencesPerVertex(baseSkinnedModelGeometry.gwtGetInfluencesPerVertex());
		}
		
		createEditingGeometrySkinnedMesh();
		createEditingWireMesh();
		createVertexSelections();
	}

	private void createVertexSelections() {
		if(editingGeometryNormalsHelper!=null){
			scene.remove(editingGeometryNormalsHelper);
		}
		double lineWidth=0.1;
		double size=0.01;
		editingGeometryNormalsHelper = THREE.VertexNormalsHelper(editingGeometryWireMesh, size, 0xffff00, lineWidth);
		scene.add(editingGeometryNormalsHelper);
		
		if(editingGeometryVertexSelector!=null){
			editingGeometryVertexSelector.dispose();
		}
		editingGeometryVertexSelector = new MeshVertexSelector(editingGeometryWireMesh, renderer, camera, scene);
		
		updateSelectedTabIndex();
	}

	@Override
	public String getTabTitle() {
		return "Weight Tool";
	}
	/**
	 * for json meta
	 * @return
	 */
	public static String getGeneratedBy(){
		return "GWTModel-Weight ver"+GWTModelWeight.version;
	}
	
}
