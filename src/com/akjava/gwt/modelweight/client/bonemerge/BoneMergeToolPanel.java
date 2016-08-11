package com.akjava.gwt.modelweight.client.bonemerge;

import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.modelweight.client.animation.GeometryBoneUtils;
import com.akjava.gwt.modelweight.client.animation.GeometryBoneUtils.GeometryBoneData;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.core.MorphTarget;
import com.akjava.gwt.three.client.gwt.loader.JSONLoaderObject;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.java.utils.Mbl3dLoader;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * i find a way to direct?
 * @author aki
 *
 */
public class BoneMergeToolPanel extends VerticalPanel{

	
	private Geometry file2Object;
	private Button mergeButton;
	private Label file2Label;

	private TextBox suffixNameBox;
	JavaScriptObject loadedObject;
	
	HorizontalPanel downloadPanel;
	

	

	
	Geometry baseGeometry;

	private boolean ignoreRootBone=false;

	public BoneMergeToolPanel(){
		
		
		
		
		//for easy debug
		/*
		final String fileName="model11-moved_eyelid-extendhair4.json";
		THREE.XHRLoader().load(fileName, new XHRLoadHandler() {
			@Override
			public void onLoad(String text) {
				onLoadBaseGeometry(fileName, text);
			}
		});
		*/
		boneIndexBox=GeometryBoneUtils.createBoneListBox();
		add(boneIndexBox);
		add(new Label("base-geometry"));
		file1Label = new Label("choose three.js model file");
		add(file1Label);
		
		final Label file1InfoLabel=new Label(".");
		add(file1InfoLabel);
		
		final FileUploadForm srcFile=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String asStringText) {
				onLoadBaseGeometry(file.getFileName(),asStringText);
				file1Label.setText(file.getFileName());
			}
		}, true);
		add(srcFile);
		srcFile.setAccept(".json");
		
		add(new Label("adding-bone"));
		file2Label = new Label();
		add(file2Label);
		
		CheckBox ignoreRoot=new CheckBox("ignore root-bone");
		ignoreRoot.setValue(false);
		ignoreRoot.setVisible(false);
		ignoreRoot.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				ignoreRootBone=event.getValue();
			}
		});
		add(ignoreRoot);
		
		
		final Label file2InfoLabel=new Label();
		add(file2InfoLabel);
		
		final FileUploadForm destFile=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(final File file, String asStringText) {
				
				JSONObject jsonObj=GWTThreeUtils.parseJSONGeometryObject(asStringText);
				JSONLoaderObject loaderObject=THREE.JSONLoader().parse(jsonObj.getJavaScriptObject());
				
				//bone check first.
				
				if(!GeometryBoneUtils.isHasBone(loaderObject.getGeometry())){
					Window.alert("no bone");
					return;
				}
				
				//TODO check duplicate
				
				file2Object=loaderObject.getGeometry();
				file2Label.setText(file.getFileName());
				updateButton();
				
				LogUtils.log(file2Object.getBones());
			}
		}, true);
		add(destFile);
		destFile.setAccept(".json");
		/*
		destFile.getFileUpload().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JsArray<File> files = FileUtils.toFile(event.getNativeEvent());
				
				final FileReader reader=FileReader.createFileReader();
				final File file=files.get(0);
				reader.setOnLoad(new FileHandler() {
					

					@Override
					public void onLoad() {
						destFile.reset();
						
						String text=reader.getResultAsString();
						
						
					}
				});
				reader.readAsText(file,"utf-8");
			}
		});
		*/
		
		HorizontalPanel fileNames=new HorizontalPanel();
		add(fileNames);
		fileNames.add(new Label("Suffix-Name:"));
		suffixNameBox = new TextBox();
		fileNames.add(suffixNameBox);
		suffixNameBox.setText("additional-");//
		
		
		
		mergeButton = new Button("Merge two bones");
		mergeButton.setEnabled(false);
		add(mergeButton);
		final HorizontalPanel linkContainer=new HorizontalPanel();
		add(linkContainer);
		mergeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				List<Vector3> basePositions=GeometryBoneUtils.convertAbsolutePosition(baseGeometry.getBones());
				List<Vector3> addingPositions=GeometryBoneUtils.convertAbsolutePosition(file2Object.getBones());
				
				String suffix=suffixNameBox.getValue();
				
				int offset=baseGeometry.getBones().length();
				//ignoreRootBone
				int selectionBoneIndex=boneIndexBox.getValue().getIndex();
				if(ignoreRootBone){
					offset--;
				}
				for(int i=0;i<file2Object.getBones().length();i++){
					AnimationBone bone=file2Object.getBones().get(i);
					if(ignoreRootBone){
						if(bone.getParent()==-1){
							continue;
						}else if(bone.getParent()==0){//root
							bone.setParent(selectionBoneIndex);
						}else{
							int newIndex=bone.getParent()+offset;
							bone.setParent(newIndex);
						}
					}else{
						if(bone.getParent()==-1){//root
							ThreeLog.log("add",addingPositions.get(0));
							ThreeLog.log("base",basePositions.get(selectionBoneIndex));
							Vector3 diff=addingPositions.get(0).clone().sub(basePositions.get(selectionBoneIndex));
							bone.setPos(diff);
							bone.setParent(selectionBoneIndex);
							
						}else{
							int newIndex=bone.getParent()+offset;
							bone.setParent(newIndex);
						}
					}
					
					bone.setName(suffix+bone.getName());
					
					baseGeometry.getBones().push(bone);
				}
				
				
				baseModelFile.setBones(baseGeometry.getBones());
				
				
				//copy back to model-file
				
				
				downloadPanel.clear();
				Anchor a=HTML5Download.get().generateTextDownloadLink(baseModelFile.getJsonText(), "bone-merged.json", "Download", true);
				downloadPanel.add(a);
				
			}
		});
		
		
		downloadPanel=new HorizontalPanel();
		this.add(downloadPanel);
	}
	
	ValueListBox<GeometryBoneData> boneIndexBox;
	JSONModelFile baseModelFile;
	private Label file1Label;
	protected void onLoadBaseGeometry(String fileName, String text) {
		
		JSONObject jsonObj=GWTThreeUtils.parseJSONGeometryObject(text);
		JSONLoaderObject loaderObject=THREE.JSONLoader().parse(jsonObj.getJavaScriptObject());
		baseGeometry=loaderObject.getGeometry();
		
		baseModelFile=JSONModelFile.create(jsonObj);
		
		//must exist bone.
		if(!GeometryBoneUtils.isHasBone(baseGeometry)){
			Window.alert("has no bone");
			baseModelFile=null;
			file1Label.setText("");
			return;
		}
		//make bone list
		
		List<GeometryBoneData> bones=GeometryBoneUtils.bonesToBoneDataList(baseGeometry.getBones());
		boneIndexBox.setValue(bones.get(0));
		boneIndexBox.setAcceptableValues(bones);
		mergeButton.setEnabled(true);
		updateButton();
	}
	
	
	private void updateButton(){
		boolean status=false;
		if(baseGeometry!=null && file2Object!=null){
			//check same weight
			status=true;
		}
		mergeButton.setEnabled(status);
	}
	
	//copy
	
	//link container
	
}
