package com.akjava.gwt.modelweight.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.file.ui.DropVerticalPanelBase;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.TrackballControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.java.ui.SimpleTabDemoEntryPoint;
import com.akjava.gwt.three.client.java.ui.experiments.Vector4Editor;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.objects.Bone;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
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
	private TrackballControls trackballControls;

	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		if(trackballControls!=null){
			trackballControls.update();
			//LogUtils.log(camera.getUuid());
		}
		
		if(baseSkinnedModelMesh!=null){
		baseSkinnedModelMesh.getPosition().set(basePosEditor.getX(), basePosEditor.getY(), basePosEditor.getZ());
		}
	}

	private String textureUrl="underware-nonipple.png";
	private String modelUrl="merged3.json";//testing 4-point
	
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		
		
		//Window.open("text/plain:test.txt:"+url, "test", null);
		
		storageControler = new StorageControler();
		canvas.setClearColor(0x333333);//canvas has margin?
		
		
		
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
	}


	private JSONObject baseSkinnedModelJson;
	private Geometry baseSkinnedModelGeometry;
	private SkinnedMesh baseSkinnedModelMesh;
	private Vector4Editor basePosEditor;
	private Mesh wireBody;
	private BoneVertexColorTools vertexColorTools;
	private ValueListBox<AnimationBone> boneListBox;
	private List<AnimationBone> baseSkinnedMeshBones;
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
				
				updateBoneListBox();
			}
		});
	}

	protected void updateBoneListBox() {
		baseSkinnedMeshBones = JavaScriptUtils.toList(baseSkinnedModelGeometry.getBones());
		boneListBox.setValue(baseSkinnedMeshBones.get(0));
		boneListBox.setAcceptableValues(baseSkinnedMeshBones);
	}

	protected void createBaseWireMesh(){
		
		if(wireBody!=null){
			scene.remove(wireBody);
		}
		
		Geometry bodyGeometry=baseSkinnedModelGeometry.clone();
		bodyGeometry.setSkinIndices(baseSkinnedModelGeometry.getSkinIndices());
		bodyGeometry.setSkinWeights(baseSkinnedModelGeometry.getSkinWeights());
		bodyGeometry.setBones(baseSkinnedModelGeometry.getBones());
		
wireBody = THREE.Mesh(bodyGeometry, 
				
				THREE.MeshPhongMaterial(GWTParamUtils.MeshBasicMaterial()
						.wireframe(true)
						.color(0xffffff)
						.vertexColors(THREE.VertexColors)
						)
				);

//watch out once set color can't replace color.use setHex()

		
		
		scene.add(wireBody);
		
		vertexColorTools = new BoneVertexColorTools(bodyGeometry);
	
		//make a class handle vertex color
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
		
		trackballControls=THREEExp.TrackballControls(camera,canvas.getElement());
		trackballControls.setNoZoom(false);//controls.noZoom = false;
		trackballControls.setNoPan(false);//controls.noPan = false;
		trackballControls.setRotateSpeed(10);
		
		trackballControls.setTarget(THREE.Vector3(0,cameraY,0));
		
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		// TODO Auto-generated method stub
		
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
	public void createControl(DropVerticalPanelBase parent) {
		TabPanel tab=new TabPanel();
		parent.add(tab);
		
		VerticalPanel posPanel=new VerticalPanel();
		tab.add(posPanel,"Pos&Rot");
		tab.selectTab(0);
		
		posPanel.add(new HTML("<h4>Base Model</h4>"));
		
		basePosEditor = new Vector4Editor("Position",-2, 2, 0.001, 0);
		posPanel.add(basePosEditor);
		
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
				 vertexColorTools.updateVertexsColorByBone(baseSkinnedMeshBones.indexOf(event.getValue()));
			}
		});
		
		
		showControl();
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
