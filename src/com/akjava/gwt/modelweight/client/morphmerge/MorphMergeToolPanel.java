package com.akjava.gwt.modelweight.client.morphmerge;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.modelweight.client.ModelCompare;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.gwt.core.MorphTarget;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.java.utils.Mbl3dLoader;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @deprecated
 * i find a way to direct
 * @author aki
 *
 */
public class MorphMergeToolPanel extends VerticalPanel{

	
	private Geometry file2Object;
	private Button copyButton;
	private Label file2Label;

	private TextBox saveFileBox;
	JavaScriptObject loadedObject;
	
	HorizontalPanel downloadPanel;
	

	private void doTest2(Geometry morphGeometry) {
			LogUtils.log(morphGeometry);
		
			//Big ArrayList crash easily
			//JsArrayNumber targetIndexs=JsArrayNumber.createArray().cast();//only array
			//Uint32ArrayNative targetIndexs=Uint32ArrayNative.create(geometry.getVertices().length());
			JsArrayNumber morphTargetIndexs=JsArrayNumber.createArray().cast();
			//init -1
			for(int i=0;i<baseGeometry.getVertices().length();i++){
				morphTargetIndexs.set(i, -1);
			}
			
			boolean validate=true;
			for(int i=0;i<morphGeometry.getVertices().length();i++){
				Vector3 gv=morphGeometry.getVertices().get(i);
				int same=0;
				
				for(int j=0;j<baseGeometry.getVertices().length();j++){
					Vector3 tv=baseGeometry.getVertices().get(j);
					if(gv.equals(tv)){
						morphTargetIndexs.set(j,i);
						same++;
					}
					
				}
				if(same==0){
					LogUtils.log("not exist-at:"+i);
					validate=false;
					break;
				}else if(same>1){
					LogUtils.log("too much same:"+i+" same="+same);
					validate=false;
					break;
				}
			}
			LogUtils.log("validate:"+validate);
			LogUtils.log("morphTargetIndexs-length:"+morphTargetIndexs.length());
		
			
			JsArray<JavaScriptObject> morphTargetsJson=JsArray.createArray().cast();
			for(int i=0;i<morphGeometry.getMorphTargets().length();i++){
			LogUtils.log("merging:"+i);
			MorphTarget morphTarget=morphGeometry.getMorphTargets().get(i);
			
			JsArray<Vector3> vertexs=JsArray.createArray(baseGeometry.getVertices().length()).cast();
			for(int vertexIndex=0;vertexIndex<baseGeometry.getVertices().length();vertexIndex++){
				//LogUtils.log(vertexIndex);
				
				int at=(int)morphTargetIndexs.get(vertexIndex);
				if(at!=-1){
					//LogUtils.log("set:"+vertexIndex+" at "+at);
					vertexs.set(vertexIndex, morphTarget.getVertices().get(at));
				}else{
					vertexs.set(vertexIndex, baseGeometry.getVertices().get(vertexIndex));
				}
			}
			LogUtils.log("newMorphTarget:"+i);
			MorphTarget newMorphTarget=MorphTarget.create();
			newMorphTarget.setName(morphTarget.getName());
			newMorphTarget.setVertices(vertexs);
			LogUtils.log("toJSON:"+i);
			//make morph jsons
			LogUtils.log(newMorphTarget);
			JavaScriptObject morphJson=newMorphTarget.toJSON();
			morphTargetsJson.set(i,morphJson);
			}
			
			/*
			 * format 4 not support bone
			JavaScriptObject coreJson=morphGeometry.toJSON();
			JSONObject object=new JSONObject(coreJson);
			object.put("morphTargets", new JSONArray(morphTargetsJson));
			*/
			
			baseModelFile.setMorphTargets(morphTargetsJson);
			
			
			//make morph targets by hands
			//copy same as morphs size
			downloadPanel.clear();
			Anchor a=HTML5Download.get().generateTextDownloadLink(baseModelFile.getJsonText(), "merged.json", "download", true);
			downloadPanel.add(a);
	}
	

	
	Geometry baseGeometry;

	private boolean applyAxisAngle=true;
	public MorphMergeToolPanel(){
		
		
		CheckBox applyCheck=new CheckBox("applyAxisAngle(for r74 mbl3d)");
		applyCheck.setValue(true);
		applyCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				applyAxisAngle=event.getValue();
			}
		});
		add(applyCheck);
		
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
		
		add(new Label("base-noMorph(broken morph)"));
		final Label file1Label=new Label("choose three.js model file");
		add(file1Label);
		
		final Label file1InfoLabel=new Label(".");
		add(file1InfoLabel);
		
		final FileUploadForm srcFile=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String asStringText) {

				onLoadBaseGeometry(file.getFileName(),asStringText);
				

				
				//I'M afraid why i'M using json-parser

				/*
				JSONLoader loader=THREE.JSONLoader();
				JSONValue lastJsonValue = JSONParser.parseStrict(asStringText);
				JSONObject object=lastJsonValue.isObject();
				if(object==null){
					LogUtils.log("null loaded:");
					handler.loaded(null, null);
				}
				
				loadedObject=object.getJavaScriptObject();
				
				JavaScriptObject jsObject=loader.parse(object.getJavaScriptObject(), null);
				JSONObject newobject=new JSONObject(jsObject);
				
				handler.loaded((Geometry) newobject.get("geometry").isObject().getJavaScriptObject(), null);
				
				*/
				
				
				file1Label.setText(file.getFileName());
				//updateButton();
				
			}
		}, true);
		add(srcFile);
		srcFile.setAccept(".json");
		
		/*
		srcFile.getFileUpload().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JsArray<File> files = FileUtils.toFile(event.getNativeEvent());
				
				final FileReader reader=FileReader.createFileReader();
				final File file=files.get(0);
				reader.setOnLoad(new FileHandler() {
					@Override
					public void onLoad() {
						srcFile.reset();
						
						String text=reader.getResultAsString();
						
						JSONLoadHandler handler=new JSONLoadHandler() {
							
							@Override
							public void loaded(Geometry geometry,JsArray<Material> materials) {
								file1Object=geometry;
								file1InfoLabel.setText("Indices:"+file1Object.getSkinIndices().length()+",Weigths"+file1Object.getSkinWeight().length());	
							}
						};

						
						JSONLoader loader=THREE.JSONLoader();
						JSONValue lastJsonValue = JSONParser.parseStrict(text);
						JSONObject object=lastJsonValue.isObject();
						if(object==null){
							LogUtils.log("null loaded:");
							handler.loaded(null, null);
						}
						
						loadedObject=object.getJavaScriptObject();
						
						JavaScriptObject jsObject=loader.parse(object.getJavaScriptObject(), null);
						JSONObject newobject=new JSONObject(jsObject);
						
						handler.loaded((Geometry) newobject.get("geometry").isObject().getJavaScriptObject(), null);
						
						
						
						
						file1Label.setText(file.getFileName());
						updateButton();
					}
				});
				reader.readAsText(file,"utf-8");
			}
		});
		*/
		

		add(new Label("morph-object"));
		file2Label = new Label();
		add(file2Label);
		
		final Label file2InfoLabel=new Label();
		add(file2InfoLabel);
		
		final FileUploadForm destFile=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(final File file, String asStringText) {
				new Mbl3dLoader().parse(asStringText, new JSONLoadHandler() {
					
					@Override
					public void loaded(Geometry geometry,JsArray<Material> materials) {
						
						file2Object=geometry;
						//file1InfoLabel.setText("Indices:"+file1Object.getSkinIndices().length()+",Weigths"+file1Object.getSkinWeight().length());	
					
						
						doTest2(file2Object);
						
	
					}
				});

				file2Label.setText(file.getFileName());
				//updateButton();
				
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
		fileNames.add(new Label("Name:"));
		saveFileBox = new TextBox();
		fileNames.add(saveFileBox);
		
		
		
		copyButton = new Button("Merge two models(or fix morph)");
		copyButton.setEnabled(true);
		add(copyButton);
		final HorizontalPanel linkContainer=new HorizontalPanel();
		add(linkContainer);
		copyButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(file2Object==null){
				JsArray<JavaScriptObject> morphTargetsJson=JsArray.createArray().cast();
				for(int i=0;i<baseGeometry.getMorphTargets().length();i++){
					JavaScriptObject morphJson=baseGeometry.getMorphTargets().get(i).toJSON();
					morphTargetsJson.set(i,morphJson);
				}
				
				baseModelFile.setMorphTargets(morphTargetsJson);
				
				
				downloadPanel.clear();
				Anchor a=HTML5Download.get().generateTextDownloadLink(baseModelFile.getJsonText(), "merged.json", "download", true);
				downloadPanel.add(a);
				}else{
					Window.alert("not yet;maybe dotest2()");
				}
			}
		});
		
		
		downloadPanel=new HorizontalPanel();
		this.add(downloadPanel);
	}
	
	JSONModelFile baseModelFile;
	protected void onLoadBaseGeometry(String fileName, String text) {
		JSONValue value=new Mbl3dLoader().applyAxisAngle(applyAxisAngle).parse(text, new JSONLoadHandler() {
			@Override
			public void loaded(Geometry geometry, JsArray<Material> materials) {
				baseGeometry=geometry;
			}
		});
		baseModelFile=JSONModelFile.create(value);
	}
	
	/*
	private void updateButton(){
		boolean status=false;
		if(baseGeometry!=null && file2Object!=null){
			//check same weight
			status=true;
		}
		copyButton.setEnabled(status);
	}
	*/
	//copy
	
	//link container
	
}
