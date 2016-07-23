package com.akjava.gwt.modelweight.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropVerticalPanelBase;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.modelweight.client.animation.QuickBoneAnimationWidget;
import com.akjava.gwt.modelweight.client.morphmerge.MorphMergeToolPanel;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.java.SkinningVertexCalculator;
import com.akjava.gwt.three.client.java.bone.BoneNameUtils;
import com.akjava.gwt.three.client.java.bone.CloseVertexAutoWeight;
import com.akjava.gwt.three.client.java.bone.SimpleAutoWeight;
import com.akjava.gwt.three.client.java.bone.WeightResult;
import com.akjava.gwt.three.client.java.ui.SimpleTabDemoEntryPoint;
import com.akjava.gwt.three.client.java.ui.experiments.Vector3Editor;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.animation.AnimationMixerAction;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.extras.helpers.VertexNormalsHelper;
import com.akjava.gwt.three.client.js.extras.helpers.WireframeHelper;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.akjava.gwt.three.client.js.objects.Bone;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTModelWeight extends SimpleTabDemoEntryPoint{
	public static final String version="6.01(for r74)";//for three.js r74
	private StorageControler storageControler;
	private Mesh mouseClickCatcher;
	
	private OrbitControls trackballControls;

	//private TrackballControls trackballControls;

	
	private boolean gpuSkinning;
	
	public boolean isGpuSkinning() {
		return gpuSkinning;
	}
	
	@Override
	public String getHtml(){
	String html="";

	return html;	
	}

	//re-creating character
	private boolean disableMixer;
	public void setGpuSkinning(boolean gpuSkinning) {
		disableMixer=true;
		//i have no idea somehow problem
		//trackballControls.reset();
		//trackballControls.setTarget(THREE.Vector3(0,cameraY,0));
		double time=currentAnimationAction!=null?currentAnimationAction.getTime():0;
		double timeScale=mixer!=null?mixer.getTimeScale():1;
		
		
		this.gpuSkinning = gpuSkinning;
		createBaseCharacterModelSkin();//re-create mixer here
		
		if(hasEditingGeometry()){
		createEditingClothSkin();
		}
		
		
		
		playAnimation(lastAnimationClip);//pauseButton label update here
		//sync old value to recreated-mixer and action 
		if(currentAnimationAction!=null){
			currentAnimationAction.setTime(time);
		}
		mixer.setTimeScale(timeScale);
		updatePauseButtonLabel();
		disableMixer=false;
	}

	private void updateTimeLabel(){
		if(currentAnimationAction==null){
			return;
		}
		double t=currentAnimationAction.getTime();
		if(t==0){
			timeLabel.setText("time:0");
			return;
		}
		int m=(int) (t/60);
		String head="time:";
		if(m>0){
			head=m+"m ";
		}
		double r=JavaScriptUtils.fixNumber(4,t/60);
		
		timeLabel.setText(head+r);
	}
	
	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		
		if(mixer!=null && !disableMixer){
			mixer.update(1.0/60);
			updateTimeLabel();
		}
		
		
		
		if(trackballControls!=null){
			trackballControls.update();
			//LogUtils.log(camera.getUuid());
		}
		
		if(baseCharacterModelSkinnedMesh!=null){
		baseCharacterModelSkinnedMesh.getPosition().set(baseCharacterModelPositionEditor.getX(), baseCharacterModelPositionEditor.getY(), baseCharacterModelPositionEditor.getZ());
		
		if(!gpuSkinning){
			skinningbyHand(baseCharacterModelSkinnedMesh,baseCharacterModelGeometry);
			baseCharacterModelSkinnedMesh.getGeometry().computeBoundingSphere();
			}
		}
		
		if(baseCharacterModelWireframeMesh!=null){
			baseCharacterModelWireframeMesh.getPosition().set(baseCharacterWireframePositionEditor.getX(), baseCharacterWireframePositionEditor.getY(), baseCharacterWireframePositionEditor.getZ());
			
		}
		
		if(editingClothWireframeNormalsHelper!=null){
			//editingClothWireframeNormalsHelper.update();
			
		}
		
		
		
		
		if(editingClothModelSkinnedMesh!=null){
			if(!gpuSkinning){
			skinningbyHand(editingClothModelSkinnedMesh,editingGeometry);
			editingClothModelSkinnedMesh.getGeometry().computeBoundingSphere();
			
			if(editingClothSkinVertexSelector.getSelectecVertex()!=-1){
				editingClothSkinVertexSelector.update();
			}
			
			}
		}
		
		if(editingClothSkinNormalsHelper!=null){
			editingClothSkinNormalsHelper.update();
			if(!gpuSkinning){
				editingClothSkinNormalsHelper.getGeometry().computeBoundingSphere();//still gone easily
			}
		}
	}


	
	private int selectedTabIndex;
	final int WEIGHT_TAB_INDEX=3;
	final int BONE_TAB_INDEX=1;
	public void updateSelectedTabIndex(){
		if(selectedTabIndex!=BONE_TAB_INDEX){
			if(editingClothWireframeVertexSelector!=null){
			editingClothWireframeVertexSelector.setVisible(true);
			}
			if(editingClothWireframeNormalsHelper!=null){
			editingClothWireframeNormalsHelper.setVisible(true);
			}
			
			if(editingClothSkinNormalsHelper!=null){
				if(!gpuSkinning){
					editingClothSkinNormalsHelper.setVisible(true);
				}
			}
			
		}else{
			if(editingClothWireframeVertexSelector!=null){
			editingClothWireframeVertexSelector.setVisible(false);
			}
			if(editingClothWireframeNormalsHelper!=null){
			editingClothWireframeNormalsHelper.setVisible(false);
			}
			
			if(editingClothSkinNormalsHelper!=null){
				editingClothSkinNormalsHelper.setVisible(false);
			}
		}
	}
	
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		LogUtils.log("Model Weight:"+version);
		
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
		
		nearCamera=0.001;
		updateCamera(scene,screenWidth , screenHeight);
		
		cameraZ=4;
		
		
		
		
		
		autoUpdateCameraPosition=false;
		
		//showControl();
	
		
		
		mouseSelector = new Object3DMouseSelecter(renderer, camera);
		
		
		//warning not loaded
		basicCharacterModelDisplacementTexture=GWTHTMLUtils.parameterImage("displacement");
		basicCharacterModelMapTexture=GWTHTMLUtils.parameterImage("texture");
	}
	
	ImageElement basicCharacterModelDisplacementTexture;
	ImageElement basicCharacterModelMapTexture;

	private JSONObject baseCharacterModelJson;
	private Geometry baseCharacterModelGeometry;
	private SkinnedMesh baseCharacterModelSkinnedMesh;
	private Vector3Editor baseCharacterModelPositionEditor;
	private Mesh baseCharacterModelWireframeMesh;
	private BoneVertexColorTools baseCharacterVertexColorTools;
	private BoneVertexColorTools editingClothVertexColorTools;
	private ValueListBox<AnimationBone> boneListBox;
	private List<AnimationBone> baseCharacterModelBones;
	private Object3DMouseSelecter mouseSelector;
	private Group boneMeshGroup;
	private BoneMeshMouseSelector boneMouseSelector;
	private Vector3Editor baseCharacterWireframePositionEditor;
	private Mesh editingClothSkinWireframeHelperMesh;
	private AbstractImageFileUploadPanel baseCharacterModelDisplacementUpload;
	private void initialLoadBaseCharacterModel(final String modelUrl) {
		THREE.XHRLoader().load(modelUrl, new XHRLoadHandler() {
			@Override
			public void onLoad(String text) {
				baseCharacterModelUpload.uploadText(FileNames.getFileNameAsSlashFileSeparator(modelUrl), text);
			}
		});
	}
	
	private void createOrbitControler(){
baseCharacterModelGeometry.computeBoundingBox();
		
		
		cameraY=baseCharacterModelGeometry.getBoundingBox().getMax().getY()/2;
		
		
		//this position used 
		camera.getPosition().set(cameraX, cameraY, cameraZ);
		
		
		if(trackballControls!=null){
			trackballControls.dispose();
		}
		
		//trackball initialize here
		
		//i feeel orbit is much stable on y-axis
		trackballControls=THREEExp.OrbitControls(camera,canvas.getElement());
		//trackballControls.setNoZoom(true);
		trackballControls.getMouseButtons().set("ORBIT", THREE.MOUSE.MIDDLE);
		trackballControls.getMouseButtons().set("ZOOM", 3);//3 is not exist,for ignore left button
		
	//	trackballControls=THREEExp.TrackballControls(camera,canvas.getElement());
		//trackballControls.setRotateSpeed(10);
		
		trackballControls.setTarget(THREE.Vector3(0,cameraY,0));
	}
	

	protected void createBaseCharacterBones() {
		if(boneMeshGroup!=null){
			scene.remove(boneMeshGroup);
		}
		BoneMeshTools bmt=new BoneMeshTools();
		boneMeshGroup = bmt.createBoneMeshs(baseCharacterModelGeometry.getBones());
		scene.add(boneMeshGroup);
		
		boneMouseSelector = new BoneMeshMouseSelector(boneMeshGroup, baseCharacterModelGeometry.getBones(), renderer, camera);
	}

	protected void updateBoneListBox() {
		baseCharacterModelBones = JavaScriptUtils.toList(baseCharacterModelGeometry.getBones());
		boneListBox.setValue(baseCharacterModelBones.get(0));
		boneListBox.setAcceptableValues(baseCharacterModelBones);
	}

	protected void createBaseCharacterWireframe(){
		
		if(baseCharacterModelWireframeMesh!=null){
			scene.remove(baseCharacterModelWireframeMesh);
		}
		
		Geometry bodyGeometry=baseCharacterModelGeometry.clone();
		bodyGeometry.setSkinIndices(baseCharacterModelGeometry.getSkinIndices());
		bodyGeometry.setSkinWeights(baseCharacterModelGeometry.getSkinWeights());
		bodyGeometry.setBones(baseCharacterModelGeometry.getBones());
		
baseCharacterModelWireframeMesh = THREE.Mesh(bodyGeometry, 
				
				THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial()
						.wireframe(true)
						.color(0xffffff)
						.vertexColors(THREE.VertexColors)
						)
				);

//watch out once set color can't replace color.use setHex()

		
		
		scene.add(baseCharacterModelWireframeMesh);
		
		baseCharacterVertexColorTools = new BoneVertexColorTools(bodyGeometry);
	
		//make a class handle vertex color
	}
protected void createEditingClothWireframe(){
		
		if(editingGeometryWireMesh!=null){
			scene.remove(editingGeometryWireMesh);
		}
		
		
		editingGeometryWireMesh = THREE.Mesh(editingGeometry, 
				
				THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial()
						//.wireframe(true)
						.shading(THREE.FlatShading)
						.color(0xffffff)
						.vertexColors(THREE.VertexColors)
						.transparent(true)
						.opacity(0.7)
						)
				);
		
		

		scene.add(editingGeometryWireMesh);
		editingClothVertexColorTools = new BoneVertexColorTools(editingGeometry);
		
		
		if(editingGeometryWireMeshHelper!=null){
			scene.remove(editingGeometryWireMeshHelper);
		}
		
		editingGeometryWireMeshHelper = THREE.WireframeHelper(editingGeometryWireMesh,0x888888);
		scene.add(editingGeometryWireMeshHelper);
	}

	

	protected void createBaseCharacterModelSkin() {
		Texture texture=baseCharacterModelTextureUpload.createTextureFromUpload(basicCharacterModelMapTexture);
		Texture displacement=baseCharacterModelDisplacementUpload.createTextureFromUpload(basicCharacterModelDisplacementTexture);
		
		
		MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
				.skinning(gpuSkinning)
				.map(texture)
				.transparent(true)
				.alphaTest(0.1)
				.side(textureSide)
				.displacementMap(displacement)
				.displacementScale(GWTHTMLUtils.parameterDouble("displacementScale", 0.1))
				);
		
		if(baseCharacterModelSkinnedMesh!=null){
			scene.remove(baseCharacterModelSkinnedMesh);
		}
		Geometry geometry=baseCharacterModelGeometry.clone();
		baseCharacterModelGeometry.gwtSoftCopyToWeightsAndIndicesAndBone(geometry);
		
		baseCharacterModelSkinnedMesh = THREE.SkinnedMesh(geometry, material);
		scene.add(baseCharacterModelSkinnedMesh);
		
		
		
		mixer=THREE.AnimationMixer(baseCharacterModelSkinnedMesh);
	}
	
	public boolean hasEditingGeometry(){
		return editingGeometry!=null;
	}
	
	protected MeshPhongMaterial createEditingClothSkinMaterial(){
		MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
				.skinning(gpuSkinning)
				.color(editingClothModelSkinnedMeshColor)
				.shading(THREE.FlatShading)
				.transparent(true)
				.alphaTest(0.1)
				.side(textureSide)
				.displacementScale(GWTHTMLUtils.parameterDouble("displacementScale", 0.1))
				);
		
		if(editingClothModelTextureUpload.isUploaded()){
			Texture texture=THREE.Texture(editingClothModelTextureUpload.getLastUploadImage());
			texture.setNeedsUpdate(true);
			material.setMap(texture);
			material.getColor().setHex(0xffffff);
		}
		material.setDisplacementMap(editingClothModelDisplacementUpload.createTextureFromUpload(null));
		return material;
	}
	
	protected void createEditingClothSkin() {
		
		Material material=createEditingClothSkinMaterial();
		
		if(editingClothModelSkinnedMesh!=null){
			//not scene ,
			baseCharacterModelSkinnedMesh.remove(editingClothModelSkinnedMesh);
		}
		
		
		
		//can't use editingGeometry,maybe Indices & weights registered somewhere with geometry.need new geometry to update indicis&weight
		Geometry geometry=editingGeometry.clone();
		//lightCopy
		geometry.setSkinIndices(editingGeometry.getSkinIndices());
		geometry.setSkinWeights(editingGeometry.getSkinWeights());
		//warning possible no bones,maybe no need here?
		geometry.setBones(editingGeometry.getBones());
		
		editingClothModelSkinnedMesh = THREE.SkinnedMesh(geometry, material);
		
		editingClothModelSkinnedMesh.setSkeleton(baseCharacterModelSkinnedMesh.getSkeleton());//share bone to work same mixer
		baseCharacterModelSkinnedMesh.add(editingClothModelSkinnedMesh);//share same position,rotation
		
		
		
		//create normal helper
		if(editingClothSkinNormalsHelper!=null){
			scene.remove(editingClothSkinNormalsHelper);
		}
		
		double lineWidth=0.1;
		double size=0.01;
		editingClothSkinNormalsHelper = THREE.VertexNormalsHelper(editingClothModelSkinnedMesh, size, 0xffff00, lineWidth);
		scene.add(editingClothSkinNormalsHelper);
		
		//create wireframe helper wireframehelper not good at geometry update with shareing geometry
		if(editingClothSkinWireframeHelperMesh!=null){
			baseCharacterModelSkinnedMesh.remove(editingClothSkinWireframeHelperMesh);
		}
		editingClothSkinWireframeHelperMesh = THREE.Mesh(geometry,THREE.MeshBasicMaterial(GWTParamUtils.MeshBasicMaterial().color(0x888888).shading(THREE.FlatShading).wireframe(true)));
		baseCharacterModelSkinnedMesh.add(editingClothSkinWireframeHelperMesh);
		
		
		//for animation-mesh 
		if(editingClothSkinVertexSelector!=null){
			editingClothSkinVertexSelector.dispose();
			}
		editingClothSkinVertexSelector = new MeshVertexSelector(editingClothModelSkinnedMesh, renderer, camera, scene);
		
		if(editingClothWireframeVertexSelector!=null){
			//on initialize no need to sync,but when skinning-data update,need to sync selection
			editingClothSkinVertexSelector.setSelectionVertex(editingClothWireframeVertexSelector.getSelectecVertex());//sync again
		}
		
		//gpu based
		if(gpuSkinning){
			editingClothModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().setShading(THREE.SmoothShading);
		}else{
			editingClothModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().setShading(THREE.FlatShading);
			
		}
		editingClothSkinNormalsHelper.setVisible(!gpuSkinning);
		editingClothSkinVertexSelector.setVisible(!gpuSkinning);
		editingClothSkinWireframeHelperMesh.setVisible(!gpuSkinning);
	}
	
	public void skinningbyHand(SkinnedMesh mesh,Geometry origonalGeometry){
		mesh.getGeometry().setDynamic(true);
		for(int i=0;i<mesh.getGeometry().getVertices().length();i++){
			Vector3 transformed=SkinningVertexCalculator.transformSkinningVertex(mesh,i,origonalGeometry.getVertices().get(i));
			mesh.getGeometry().getVertices().get(i).copy(transformed);
		}
		
		//for lighting
		mesh.getGeometry().computeFaceNormals ();
		mesh.getGeometry().computeVertexNormals ();
		
		mesh.getGeometry().setVerticesNeedUpdate(true);
		mesh.getGeometry().setNormalsNeedUpdate(true);
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseClick(ClickEvent event) {
		if(event.getNativeButton()!=NativeEvent.BUTTON_LEFT){
			return;
		}
		
		//LogUtils.log("mouse-click");
		if(selectedTabIndex!=BONE_TAB_INDEX){
			if(editingClothWireframeVertexSelector!=null){
				int vertex=editingClothWireframeVertexSelector.pickVertex(event);
				
				if(vertex==-1){
					vertex=editingClothSkinVertexSelector.pickVertex(event);
					
					editingClothWireframeVertexSelector.setSelectionVertex(vertex);
				}else{
					editingClothSkinVertexSelector.setSelectionVertex(vertex);//just syn now
				}
				
				
				
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
				boneListBox.setValue(baseCharacterModelBones.get(index),true);
			}
			
		}
		
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		
	}

	
	public void onEditingClothJsonLoaded(String text){

		JSONObject jsonObject=parseJSONGeometry(text);
		
		
		
		editingGeometryOrigin=THREE.JSONLoader().parse(jsonObject.getJavaScriptObject()).getGeometry();
		
		//TODO bone check
		
		//TODO make gwtCloneWithBones()
		
		
		editingGeometry=THREE.JSONLoader().parse(jsonObject.getJavaScriptObject()).getGeometry();
	
		setInfluencePerVertexFromJSON(editingGeometry,jsonObject);
		
		boolean needAutoSkinning=editingGeometry.getSkinIndices()==null || editingGeometry.getSkinIndices().length()==0;
		//todo more check
		
		if(editingGeometry.getBones()==null || editingGeometry.getBones().length()==0){
			needAutoSkinning=true;
		}else{
			if(editingGeometry.getBones().length()!=baseCharacterModelGeometry.getBones().length()){
				LogUtils.log("editingGeometry has bone,but size different:base-bone-size="+baseCharacterModelGeometry.getBones().length()+",editing-size="+editingGeometry.getBones().length());
				needAutoSkinning=true;
			}	
		}
		
		if(needAutoSkinning){
			//Window.alert("has no skin indices");
			LogUtils.log("No skin indices.it would auto skinning.");
			Stopwatch watch=LogUtils.stopwatch();
			editingGeometry.computeBoundingBox();
			double maxDistance=editingGeometry.getBoundingBox().getMax().distanceTo(editingGeometry.getBoundingBox().getMin());
			new CloseVertexAutoWeight().autoWeight(editingGeometry, baseCharacterModelGeometry,maxDistance).insertToGeometry(editingGeometry);
			//LogUtils.millisecond("auto-weight vertex="+editingGeometry.getVertices().length(), watch);
			editingGeometry.gwtSetInfluencesPerVertex(baseCharacterModelGeometry.gwtGetInfluencesPerVertex());
			
			
			editingGeometryOrigin.gwtSetInfluencesPerVertex(baseCharacterModelGeometry.gwtGetInfluencesPerVertex());
			editingGeometry.gwtHardCopyToWeightsAndIndices(editingGeometryOrigin);
		}
		
		
		createEditingClothSkin();
		createEditingClothWireframe();
		createVertexSelections();
	}
	public void onBaseCharacterModelJsonLoaded(String text){
		JSONObject object=parseJSONGeometry(text);
		if(object==null){
			Window.alert("invalid model");
			return;
		}
		Geometry geometry=THREE.JSONLoader().parse(object.getJavaScriptObject()).getGeometry();
		if(!geometry.gwtHasBone() || !geometry.gwtHasSkinIndicesAndWeights()){
			Window.alert("Model has no bone,base model need bone.\nexport with Bones and Skinning checked");
			return;
		}
		
		//TODO check weights
		
		//now safe
		baseCharacterModelJson=object;
		
		baseCharacterModelGeometry=geometry;
		
		
		setInfluencePerVertexFromJSON(baseCharacterModelGeometry,object);
		
		createBaseCharacterModelSkin();
		createBaseCharacterWireframe();
		createBaseCharacterBones();
		updateBoneListBox();
		
		vertexBoneDataEditor.setBone(baseCharacterModelGeometry.getBones());
		
		
		createOrbitControler();//based skinned mesh height
		
		//editing has a possible based on baseSkinnedMesh,safe to reload
		if(editingMeshUpload.isUploaded()){
			editingMeshUpload.reload();
		}
		quickBoneAnimationWidget.setSkelton(baseCharacterModelSkinnedMesh.getSkeleton());
	}
	
	private Panel createBasicControl(){
		VerticalPanel basicPanel=new VerticalPanel();
		basicPanel.setSpacing(2);

		basicPanel.add(new HTML("<h4>Base Skinnd Character Model</h4>"));
		
		baseCharacterModelUpload = new AbstractTextFileUploadPanel("Base Json",false) {
			
			@Override
			protected void onTextFileUpload(String text) {
				onBaseCharacterModelJsonLoaded(text);
			}
		};
		baseCharacterModelUpload.getUploadForm().setAccept(FileUploadForm.ACCEPT_JSON);
		
		basicPanel.add(baseCharacterModelUpload);
		
		baseCharacterModelTextureUpload=new AbstractImageFileUploadPanel("Base Texture",true){

			@Override
			protected void onImageFileUpload(ImageElement imageElement) {
				onBaseCharacterModelTextureLoaded(imageElement);
			}
			
		};
		
		baseCharacterModelDisplacementUpload=new AbstractImageFileUploadPanel("Base Displacement",true){

			@Override
			protected void onImageFileUpload(ImageElement imageElement) {
				onBaseCharacterModelDisplacementLoaded(imageElement);
			}
			
		};
		baseCharacterModelTextureUpload.getUploadForm().setAccept(FileUploadForm.ACCEPT_IMAGE);
		basicPanel.add(baseCharacterModelTextureUpload);
		
		baseCharacterModelPositionEditor = new Vector3Editor("Skin Position",-2, 2, 0.001, 0);
		basicPanel.add(baseCharacterModelPositionEditor);
		baseCharacterModelPositionEditor.setX(-1.5,true);
		
		
		baseCharacterWireframePositionEditor = new Vector3Editor("Wire Position",-2, 2, 0.001, 0);
		basicPanel.add(baseCharacterWireframePositionEditor);
		
		basicPanel.add(new HTML("<h4>Camera</h4>"));
		Button resetCamera=new Button("Reset",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				trackballControls.reset();
				trackballControls.setTarget(THREE.Vector3(0,cameraY,0));
				
			}
		});
		basicPanel.add(resetCamera);
		
		basicPanel.add(new HTML("<h4>Texture</h4>"));
		CheckBox doubleSideCheck=new CheckBox("Double Side");
		basicPanel.add(doubleSideCheck);
		doubleSideCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				textureSide=event.getValue()?THREE.DoubleSide:THREE.FrontSide;
				
				editingClothModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().setSide(textureSide);
				baseCharacterModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().setSide(textureSide);
			}
		});
		return basicPanel;
	}
	private int textureSide=THREE.FrontSide;
	
	private Panel createBonePanel(){
		VerticalPanel bonePanel=new VerticalPanel();

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
		
		Button unselectBone=new Button("unselect",new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boneListBox.setValue(null,true);
				
			}
		});
		bonePanel.add(unselectBone);
		
		
		CheckBox gpuSkinning=new CheckBox("GPU Skin Animation");
		gpuSkinning.setTitle("Using GPU is first.but can't pick vertex");
		bonePanel.add(gpuSkinning);
		gpuSkinning.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setGpuSkinning(event.getValue());
			}
			
		});
		
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
					createEditingClothSkin();//weight updated
				}
				onBoneSelectionChanged(bone);
			}
		});
bonePanel.add(removeInfluenceButton);

Button weightAllButton=new Button("Set  all selected bone influence ",new ClickHandler() {
	
	@Override
	public void onClick(ClickEvent event) {
		
		AnimationBone bone=boneListBox.getValue();
		if(bone!=null){
			int boneIndex=boneMouseSelector.getBoneIndex(bone);
			
			JsArray<Vector4> indices=editingGeometry.getSkinIndices();
			JsArray<Vector4> weights=editingGeometry.getSkinWeights();
			for(int i=0;i<indices.length();i++){
				
				for(int j=0;j<4;j++){
					if(j==0){
						indices.get(i).gwtSet(j, boneIndex);
						weights.get(i).gwtSet(j, 1);
					}else{
						indices.get(i).gwtSet(j, 0);
						weights.get(i).gwtSet(j, 0);
					}
				}
			}
			
		}
		
		createEditingClothSkin();//weight updated
		onBoneSelectionChanged(bone);
	}
});
bonePanel.add(weightAllButton);
Button autoWeightButton=new Button("Exec auto close vertex skinning",new ClickHandler() {
	
	@Override
	public void onClick(ClickEvent event) {
		
	int influence=baseCharacterModelGeometry.gwtGetInfluencesPerVertex();
		
	WeightResult result= new CloseVertexAutoWeight().autoWeight(editingGeometry, baseCharacterModelGeometry);

	JsArray<Vector4> indices=editingGeometry.getSkinIndices();
	JsArray<Vector4> weights=editingGeometry.getSkinWeights();
	for(int i=0;i<indices.length();i++){
		indices.get(i).copy(result.getSkinIndices().get(i));
		weights.get(i).copy(result.getSkinWeights().get(i));
	}
	editingGeometry.gwtSetInfluencesPerVertex(influence);
	
	createEditingClothSkin();//weight updated
		
		//redraw wireframe
		AnimationBone bone=boneListBox.getValue();
		onBoneSelectionChanged(bone);
	}
});
bonePanel.add(autoWeightButton);

HorizontalPanel boneSkinningPanel=new HorizontalPanel();
boneSkinningPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
bonePanel.add(boneSkinningPanel);

boneSkinningPanel.add(new Label("Influence"));

final ListBox influenceBox=new ListBox();
influenceBox.addItem("1");
influenceBox.addItem("2");
influenceBox.addItem("3");
influenceBox.addItem("4");
influenceBox.setSelectedIndex(2);//for auto grouping
boneSkinningPanel.add(influenceBox);
Button autoBoneWeightButton=new Button("Exec auto close bone skinning",new ClickHandler() {
	
	@Override
	public void onClick(ClickEvent event) {
	int influence=influenceBox.getSelectedIndex()+1;

	WeightResult result= new SimpleAutoWeight(influence).autoWeight(editingGeometry, baseCharacterModelGeometry.getBones());

	JsArray<Vector4> indices=editingGeometry.getSkinIndices();
	JsArray<Vector4> weights=editingGeometry.getSkinWeights();
	for(int i=0;i<indices.length();i++){
		indices.get(i).copy(result.getSkinIndices().get(i));
		weights.get(i).copy(result.getSkinWeights().get(i));
	}
	editingGeometry.gwtSetInfluencesPerVertex(influence);
		createEditingClothSkin();//weight updated
		
		//redraw wireframe
		AnimationBone bone=boneListBox.getValue();
		onBoneSelectionChanged(bone);
	}
});
boneSkinningPanel.add(autoBoneWeightButton);


Button resetWeightButton=new Button("Reset to default indices & weights",new ClickHandler() {
	
	@Override
	public void onClick(ClickEvent event) {
		

	JsArray<Vector4> indices=editingGeometry.getSkinIndices();
	JsArray<Vector4> weights=editingGeometry.getSkinWeights();
	for(int i=0;i<indices.length();i++){
		indices.get(i).copy(editingGeometryOrigin.getSkinIndices().get(i));
		weights.get(i).copy(editingGeometryOrigin.getSkinWeights().get(i));
	}
		
		createEditingClothSkin();//weight updated
		
		//redraw wireframe
		AnimationBone bone=boneListBox.getValue();
		onBoneSelectionChanged(bone);
	}
});
bonePanel.add(resetWeightButton);


return bonePanel;
	}
	
	@Override
	public void createControl(DropVerticalPanelBase parent) {
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
		
		tabPanel.add(new CopyToolPanel(),"Copy Indices/Weights");
		tabPanel.add(new MorphMergeToolPanel(),"Fix morphtargets");
		
		
		TabPanel tab=new TabPanel();
		parent.add(tab);
		
		
		tab.add(createBasicControl(),"Basic");
		
		tab.add(createBonePanel(),"Bone");
		
		tab.add(createLoadExportPanel(),"Load/Export");
		
		tab.add(createSkinningPanel(),"Skinning");
		
		tab.add(createAnimationPanel(),"Animation");
		
		tab.selectTab(2);
		
		
		tab.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				selectedTabIndex=event.getSelectedItem();
				updateSelectedTabIndex();
			}
		});
		showControl();
		
		
		initialLoadBaseCharacterModel(GWTHTMLUtils.parameterFile("baseModel"));
	}
	
	protected void onBaseCharacterModelDisplacementLoaded(@Nullable ImageElement imageElement) {
		
		baseCharacterModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().setDisplacementMap(baseCharacterModelDisplacementUpload.createTextureFromUpload(basicCharacterModelDisplacementTexture));
		baseCharacterModelSkinnedMesh.getMaterial().setNeedsUpdate(true);
		
		
	}
	
	protected void onBaseCharacterModelTextureLoaded(@Nullable ImageElement imageElement) {
		ImageElement newImage=imageElement;
		if(imageElement==null){//reset
			newImage=basicCharacterModelMapTexture;
		}
		baseCharacterModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().getMap().setImage(newImage);
		baseCharacterModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().getMap().setNeedsUpdate(true);
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
		controls.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		animationPanel.add(controls);
		
		controls.add(new Button("Stop",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopAnimation();
				
				//Quaternion q=THREE.Quaternion();
				for(int i=0;i<baseCharacterModelSkinnedMesh.getSkeleton().getBones().length();i++){
					Bone bone=baseCharacterModelSkinnedMesh.getSkeleton().getBones().get(i);
					bone.getQuaternion().copy(baseCharacterModelBones.get(i).gwtGetRotationQuaternion());
					bone.getPosition().copy(baseCharacterModelBones.get(i).gwtGetPosition());
					bone.updateMatrixWorld(true);
				}
				
				//something strange on position
				//baseSkinnedModelMesh.pose();
			}
		}));
		 pauseButton = new Button("Pause",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(mixer.getTimeScale()==1){
					
					mixer.setTimeScale(0);
					
				}else{
					pauseButton.setText("Pause");
					mixer.setTimeScale(1);
				}
				updatePauseButtonLabel();
			}
		});
		 pauseButton.setEnabled(false);
		 pauseButton.setWidth("100px");
		 controls.add(pauseButton);
		 
		controls.add(new Button("Step",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mixer.setTimeScale(1);
				mixer.update(1.0/60);
				mixer.setTimeScale(0);
				updatePauseButtonLabel();
			}
		}));
		
		
		timeLabel = new Label("time:0");
		controls.add(timeLabel);
		
		
		HorizontalPanel animations=new HorizontalPanel();
		animationPanel.add(animations);
		animations.add(makeAnimationButton("animation1", GWTHTMLUtils.parameterFile("animation1")));
		animations.add(makeAnimationButton("animation2", GWTHTMLUtils.parameterFile("animation2")));
		animations.add(makeAnimationButton("animation3", GWTHTMLUtils.parameterFile("animation3")));
		animations.add(makeAnimationButton("animation4", GWTHTMLUtils.parameterFile("animation4")));
		
		HorizontalPanel filePanel=new HorizontalPanel();
		filePanel.setWidth("100%");
		filePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		animationPanel.add(filePanel);
		
		
		filePanel.add(new Button("Play",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(uploadAnimationText==null){
					return;
				}
				loadAnimation(uploadAnimationText);
			}
		}));
		
		
		final Label fileNameLabel=new Label();
		fileNameLabel.setWidth("100%");
		filePanel.add(fileNameLabel);
		FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				fileNameLabel.setText(file.getFileName());
				uploadAnimationText=text;
				loadAnimation(text);
			}
		}, true);
		animationPanel.add(upload);
		
		quickBoneAnimationWidget = new QuickBoneAnimationWidget("bone-animation");
		quickBoneAnimationWidget.addValueChangeHandler(new ValueChangeHandler<AnimationClip>() {

			@Override
			public void onValueChange(ValueChangeEvent<AnimationClip> event) {
				stopAnimation();
				baseCharacterModelSkinnedMesh.getPosition().set(0, 0, 0);
				baseCharacterModelSkinnedMesh.updateMatrixWorld(true);
				baseCharacterModelSkinnedMesh.getSkeleton().pose();//before reset,better to fix position
				if(event.getValue()!=null){
					playAnimation(event.getValue());
				}
			}
		});
		animationPanel.add(quickBoneAnimationWidget);
		return animationPanel;
	}
	protected void updatePauseButtonLabel() {
		if(mixer.getTimeScale()==0){
			pauseButton.setText("UnPause");
		}else{
			pauseButton.setText("Pause");
		}
	}

	private String uploadAnimationText;

	public void stopAnimation() {
		if(mixer==null){
			return;
		}
		mixer.stopAllAction();
		lastAnimationClip=null;
		//characterMesh.getGeometry().getBones().get(60).setRotq(q)
		pauseButton.setEnabled(false);
	}
	
	private AnimationMixer mixer;
	private void loadAnimation(String text) {
		JSONValue object=JSONParser.parseStrict(text);
		JavaScriptObject js=object.isObject().getJavaScriptObject();
		AnimationClip animationClip = AnimationClip.parse(js);
		
		playAnimation(animationClip);
	}
	
	private AnimationClip lastAnimationClip;
	private AbstractImageFileUploadPanel editingClothModelTextureUpload;
	private AbstractImageFileUploadPanel editingClothModelDisplacementUpload;
	
	private AnimationMixerAction currentAnimationAction;
	public void playAnimation(@Nullable AnimationClip clip) {
		if(clip==null){
			return;
		}
		mixer.setTimeScale(1);//if paused
		mixer.stopAllAction();
		mixer.uncacheClip(clip);//reset can cache
		currentAnimationAction=mixer.clipAction(clip).play();
		lastAnimationClip=clip;
		pauseButton.setEnabled(true);
		updatePauseButtonLabel();
	}
	
	private Panel createLoadExportPanel(){
		VerticalPanel loadExportPanel=new VerticalPanel();
		loadExportPanel.add(new HTML("<h4>EditingCloth</h4>"));
		editingMeshUpload=new AbstractTextFileUploadPanel("Cloth Model",false) {
			@Override
			protected void onTextFileUpload(String text) {
				onEditingClothJsonLoaded(text);
			}
		};
		editingMeshUpload.getUploadForm().setAccept(FileUploadForm.ACCEPT_JSON);

		loadExportPanel.add(editingMeshUpload);
		
		
		editingClothModelTextureUpload=new AbstractImageFileUploadPanel("Cloth Texture",true){

			@Override
			protected void onImageFileUpload(ImageElement imageElement) {
				onEditingClothModelTextureLoaded(imageElement);
			}
			
		};
		editingClothModelTextureUpload.getUploadForm().setAccept(FileUploadForm.ACCEPT_IMAGE);
		loadExportPanel.add(editingClothModelTextureUpload);
		
		editingClothModelDisplacementUpload=new AbstractImageFileUploadPanel("Cloth Displacement",true){

			@Override
			protected void onImageFileUpload(ImageElement imageElement) {
				onEditingClothModelDisplacementLoaded(imageElement);
			}
			
		};
		editingClothModelDisplacementUpload.getUploadForm().setAccept(FileUploadForm.ACCEPT_IMAGE);
		loadExportPanel.add(editingClothModelDisplacementUpload);
		
		
		final HorizontalPanel downloadPanel=new HorizontalPanel();
		
		
		loadExportPanel.add(new HTML("Export geometry as Version4 json Format"));
		loadExportPanel.add(new Label("contain bones,indices and weights"));
		ExecuteButton exportButton=new ExecuteButton("Exec Export"){

			@Override
			public void executeOnClick() {
				JSONObject object=editingGeometry.gwtJSONWithBone();
				downloadPanel.clear();
				
				Anchor a=HTML5Download.get().generateTextDownloadLink(object.toString(), "geometry-weight-modified.json", "geometry to download",true);
				downloadPanel.add(a);
			}
			
		};
		loadExportPanel.add(exportButton);
		
		loadExportPanel.add(downloadPanel);
		
		return loadExportPanel;
		
	}
	
	private int editingClothModelSkinnedMeshColor=0x880000;
	protected void onEditingClothModelTextureLoaded(ImageElement imageElement) {
		if(editingClothModelSkinnedMesh==null){
			return;
		}
		
		Texture texture=editingClothModelTextureUpload.createTextureFromUpload(null);
		editingClothModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().setMap(texture);
		editingClothModelSkinnedMesh.getMaterial().setNeedsUpdate(true);
		
		if(editingClothModelTextureUpload.isUploaded()){
			editingClothModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().getColor().setHex(0xffffff);
		}else{
			editingClothModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().getColor().setHex(editingClothModelSkinnedMeshColor);
		}
	}
	
	protected void onEditingClothModelDisplacementLoaded(ImageElement imageElement) {
		if(editingClothModelSkinnedMesh==null){
			return;
		}
		
		Texture texture=editingClothModelDisplacementUpload.createTextureFromUpload(null);
		editingClothModelSkinnedMesh.getMaterial().gwtCastMeshPhongMaterial().setDisplacementMap(texture);
		editingClothModelSkinnedMesh.getMaterial().setNeedsUpdate(true);
		
	}

	private Panel createSkinningPanel(){
		VerticalPanel weightPanel=new VerticalPanel();
	
		//this is test.
		Button testClearAll=new Button("Test clear all",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(int i=0;i<editingGeometry.getSkinIndices().length();i++){
					
					for(int j=0;j<4;j++){
						editingGeometry.getSkinIndices().get(i).gwtSet(j, 0);
						editingGeometry.getSkinWeights().get(i).gwtSet(j, 0);
					}
				}
				createEditingClothSkin();//weight updated
				LogUtils.log(editingClothModelSkinnedMesh);
				//editingGeometryMesh.setVisible(false);
					AnimationBone bone=boneListBox.getValue();
					if(bone!=null){
						onBoneSelectionChanged(bone);
					}
				
			}
		});
		//weightPanel.add(testClearAll);
		
		
		
		weightPanel.add(new HTML("<h4>Skinning Indices & Weight</h4>"));
		vertexBoneDataEditor = new VertexBoneDataEditor();
		vertexBoneDataEditor.setValue(null);
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
				//int index=vertexBoneDataEditor.getValue().getVertexIndex();
				vertexBoneDataEditor.flush();
				createEditingClothSkin();
				
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
				
				
				vertexBoneDataEditor.copyValue(VertexBoneData.createFromdMesh(editingGeometryOrigin, index));
				vertexBoneDataEditor.flush();
				
				createEditingClothSkin();
			}
		});
		buttons.add(resetBt);
		
		
		Button testVertexGroup=new Button("Vertical vertex group selection",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(vertexBoneDataEditor.getValue()==null){
					return;
				}
				//warning possible editingGeometry has no bone.
				int index=vertexBoneDataEditor.getValue().getVertexIndex();
				
				execVerticalVertexGroup(index,false);//TODO balancing
				
				createEditingClothSkin();
			}
		});
		weightPanel.add(testVertexGroup);
		
		ExecuteButton testVertexGroupAll=new ExecuteButton("Vertical vertex group all"){

			@Override
			public void executeOnClick() {
				boolean balancing=true;
				Stopwatch watch=LogUtils.stopwatch();
				JSParameter param=JSParameter.createParameter();
				for(int i=0;i<editingGeometry.getVertices().length();i++){
					if(param.exists(String.valueOf(i))){
						continue;
					}
					List<Integer> result=execVerticalVertexGroup(i,balancing);
					if(result==null){
						return;//cancel
					}
					for(int v:result){
						param.set(String.valueOf(v), true);
					}
				}
				
				if(balancing){
					editingGeometry.gwtSetInfluencesPerVertex(editingGeometry.gwtGetInfluencesPerVertex()+1);
				}
				
				LogUtils.millisecond("convert-all", watch);
				
				
				createEditingClothSkin();
			}
			
		};
		weightPanel.add(testVertexGroupAll);
		
		return weightPanel;
	}
	
	private List<Integer> execVerticalVertexGroup(int index,boolean balancing){
		
		int influence=editingGeometry.gwtGetInfluencesPerVertex();
		if(balancing && influence==4){
			LogUtils.log("not supported influence4 and balancing");
			return null;
		}
		
		List<Integer> result=Lists.newArrayList();
		
		findVertexGroup(editingGeometry,index,result);
		//LogUtils.log("size:"+result.size());
		//LogUtils.millisecond("find", watch);
		List<List<String>> beforeBoneNames=Lists.newArrayList();
		for(int i=0;i<result.size();i++){
			Vector4 indices=editingGeometry.getSkinIndices().get(result.get(i));
			List<String> boneNames=Lists.newArrayList();
			for(int j=0;j<4;j++){
				int boneIndex=indices.gwtGet(j);
				
				if(boneIndex!=0){//ignore root;
					boneNames.add(baseCharacterModelGeometry.getBones().get(boneIndex).getName());
					}
			}
			beforeBoneNames.add(boneNames);
		}
		
		
		Map<Integer,Integer> countMap=Maps.newHashMap();
		for(List<String> vs:beforeBoneNames){
			for(String v:vs){
				if(!v.contains(",")){
					continue;// no plain style
				}
				
				Vector2 pos=BoneNameUtils.parsePlainBoneName(v);
				
				Integer count=countMap.get((int)pos.getX());
				if(count==null){
					countMap.put((int)pos.getX(), 1);
				}else{
					countMap.put((int)pos.getX(), count+1);
					//LogUtils.log("?"+((int)pos.getX())+( count+1));
				}
			}
		}
		
		if(countMap.size()==0){
			LogUtils.log("maybe no plain-bone style");
			return null;
		}
		
		
		int maxX=0;
		int maxValue=0;
		int total=0;
		for(int key:countMap.keySet()){
			int count=countMap.get(key);
			total+=count;
			if(count>maxValue){
				maxX=key;
				maxValue=count;
			}
		}
		
		//apply changes
		for(int i=0;i<result.size();i++){
			Vector4 indices=editingGeometry.getSkinIndices().get(result.get(i));
			
			for(int j=0;j<4;j++){
				int boneIndex=indices.gwtGet(j);
				if(boneIndex!=0){//ignore root;
					String boneName=baseCharacterModelGeometry.getBones().get(boneIndex).getName();
					Vector2 boneVec=BoneNameUtils.parsePlainBoneName(boneName);
						if(boneVec.getX()!=maxX){
						String newName=BoneNameUtils.makePlainBoneName(maxX,(int)boneVec.getY());
						
						int newIndex=BoneNameUtils.findBoneByName(baseCharacterModelGeometry.getBones(),newName);
						if(newIndex!=-1){
							indices.gwtSet(j, newIndex);
							//LogUtils.log("update indices at "+j+" of "+result.get(i)+" name="+newName);
						}else{
							LogUtils.log("can't find newBoneName:"+newName);
						}
						}
					}
			}
			
		}
		
		double mainPercent=(double)maxValue/total;
		//LogUtils.log("main-percent:"+mainPercent);
		double rootValue=1.0-mainPercent;
		for(int i=0;i<result.size();i++){
			Vector4 indices=editingGeometry.getSkinIndices().get(result.get(i));
			Vector4 weights=editingGeometry.getSkinWeights().get(result.get(i));
			for(int j=0;j<influence;j++){
				double changed=weights.gwtGet(j)*mainPercent;
				weights.gwtSet(j, changed);
			}
			indices.gwtSet(influence+1, 0);//must be root
			weights.gwtSet(influence+1, rootValue);
			
		}
		
		//DO increment influence finished all
		
		return result;
	}
	
	
	private void findVertexGroup(Geometry geometry,int vertexIndex,List<Integer> vertexs){
		vertexs.add(vertexIndex);
		List<Integer> result= findContainFace(geometry,vertexIndex);
		for(int faceIndex:result){
			Face3 face=geometry.getFaces().get(faceIndex);
			for(int i=0;i<3;i++){
				int v=face.gwtGet(i);
				if(!vertexs.contains(v)){
					findVertexGroup(geometry,v,vertexs);
				}
			}
		}
	}
	
	private List<Integer> findContainFace(Geometry geometry,int vertexIndex){
		List<Integer> faces=Lists.newArrayList();
		for(int i=0;i<geometry.getFaces().length();i++){
			Face3 face=geometry.getFaces().get(i);
			for(int j=0;j<3;j++){
				if(face.gwtGet(j)==vertexIndex){
					faces.add(i);
				}
			}
		}
		return faces;
	}
	
	/** @deprecated
	 *  i don know how to update,without recreate skinnedmesh maybe attribute?
	 * @param index
	 */
	public void copyIndicesAndWeightToSkinningMesh(int index){
		editingClothModelSkinnedMesh.getGeometry().getSkinIndices().get(index).copy(editingGeometry.getSkinIndices().get(index));
		editingClothModelSkinnedMesh.getGeometry().getSkinWeights().get(index).copy(editingGeometry.getSkinWeights().get(index));
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

	protected void onBoneSelectionChanged(@Nullable AnimationBone value) {
		if(value==null){
		baseCharacterVertexColorTools.clearVertexsColor();	
		
		boneMouseSelector.setBoneSelection(null);
		}else{
		 baseCharacterVertexColorTools.updateVertexsColorByBone(baseCharacterModelBones.indexOf(value));
		
		 boneMouseSelector.setBoneSelection(value.getName());
		}
		
		if(editingClothVertexColorTools!=null){
			if(value==null){
				editingClothVertexColorTools.clearVertexsColor();	
				}else{
					editingClothVertexColorTools.updateVertexsColorByBone(baseCharacterModelBones.indexOf(value));
				}
		}
	}

	private Geometry editingGeometryOrigin;
	private Geometry editingGeometry;
	private SkinnedMesh editingClothModelSkinnedMesh;
	private Mesh editingGeometryWireMesh;
	private VertexNormalsHelper editingClothWireframeNormalsHelper;
	private MeshVertexSelector editingClothWireframeVertexSelector;
	private VertexBoneDataEditor vertexBoneDataEditor;
	private WireframeHelper editingGeometryWireMeshHelper;
	private VertexNormalsHelper editingClothSkinNormalsHelper;
	private MeshVertexSelector editingClothSkinVertexSelector;
	private AbstractTextFileUploadPanel baseCharacterModelUpload;
	private AbstractTextFileUploadPanel editingMeshUpload;
	
	private AbstractImageFileUploadPanel baseCharacterModelTextureUpload;
	private Button pauseButton;
	private Label timeLabel;
	private QuickBoneAnimationWidget quickBoneAnimationWidget;
	
	private JSONObject parseJSONGeometry(String text){
		JSONValue json=JSONParser.parseStrict(text);
		
		JSONObject jsonObject=json.isObject();
		
		boolean hasData=jsonObject.get("data")!=null;//4.* format has this
		
		if(hasData){
			jsonObject=jsonObject.get("data").isObject();
		}
		return jsonObject;
	}
	private void setInfluencePerVertexFromJSON(Geometry geometry,JSONObject jsonObject){
		double influencesPerVertex=jsonObject.get("influencesPerVertex")!=null?jsonObject.get("influencesPerVertex").isNumber().doubleValue():2;
		geometry.gwtSetInfluencesPerVertex((int)influencesPerVertex);//used when export
	}
	
	

	private void createVertexSelections() {
		if(editingClothWireframeNormalsHelper!=null){
			scene.remove(editingClothWireframeNormalsHelper);
		}
		double lineWidth=0.1;
		double size=0.01;
		editingClothWireframeNormalsHelper = THREE.VertexNormalsHelper(editingGeometryWireMesh, size, 0xffff00, lineWidth);
		scene.add(editingClothWireframeNormalsHelper);
		
		//for editing-mesh
		if(editingClothWireframeVertexSelector!=null){
			editingClothWireframeVertexSelector.dispose();
		}
		editingClothWireframeVertexSelector = new MeshVertexSelector(editingGeometryWireMesh, renderer, camera, scene);
		
		updateSelectedTabIndex();
	}

	@Override
	public String getTabTitle() {
		return "Skinning Tool";
	}
	/**
	 * for json meta
	 * @return
	 */
	public static String getGeneratedBy(){
		return "GWTModel-Weight ver"+GWTModelWeight.version;
	}
	
}
