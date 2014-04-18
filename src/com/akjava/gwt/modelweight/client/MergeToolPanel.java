package com.akjava.gwt.modelweight.client;

import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.loaders.JSONLoader;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.google.common.base.Joiner;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MergeToolPanel extends VerticalPanel{

	private Geometry file1Object;

	private Geometry file2Object;
	private Button copyButton;
	private Label file2Label;

	private TextBox saveFileBox;
	JavaScriptObject loadedObject;
	public MergeToolPanel(){
		
		add(new Label("src"));
		final Label file1Label=new Label("choose three.js model file");
		add(file1Label);
		
		final Label file1InfoLabel=new Label(".");
		add(file1InfoLabel);
		
		final FileUploadForm srcFile=new FileUploadForm();
		add(srcFile);
		
		
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
		
		

		add(new Label("dest"));
		file2Label = new Label();
		add(file2Label);
		
		final Label file2InfoLabel=new Label();
		add(file2InfoLabel);
		
		final FileUploadForm destFile=new FileUploadForm();
		add(destFile);
		
		
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
						
							GWTGeometryUtils.loadJsonModel(text, new JSONLoadHandler() {
							
							@Override
							public void loaded(Geometry geometry,JsArray<Material> materials) {
								file2Object=geometry;
								
							
								file2InfoLabel.setText("Indices:"+file2Object.getSkinIndices().length()+",Weigths"+file2Object.getSkinWeight().length());	
								saveFileBox.setText(file.getFileName());
							}
						});

						file2Label.setText(file.getFileName());
						updateButton();
					}
				});
				reader.readAsText(file,"utf-8");
			}
		});
		
		HorizontalPanel fileNames=new HorizontalPanel();
		add(fileNames);
		fileNames.add(new Label("Name:"));
		saveFileBox = new TextBox();
		fileNames.add(saveFileBox);
		
		
		
		copyButton = new Button("Merge two models");
		copyButton.setEnabled(false);
		add(copyButton);
		final HorizontalPanel linkContainer=new HorizontalPanel();
		add(linkContainer);
		copyButton.addClickHandler(new ClickHandler() {
			
			private Anchor anchror;

			@Override
			public void onClick(ClickEvent event) {
				/*
				if(true){
				//what was this?
					throw new RuntimeException("now not support need fix setVertices");
				}
				*/
				Geometry exportGeo=GWTGeometryUtils.mergeGeometryPlusWeights(GWTGeometryUtils.clonePlusWeights(file1Object), file2Object);
				
				
				JSONModelFile modelFile=JSONModelFile.create();
				
				
				
				modelFile.setGeometryUvs(exportGeo.getFaceVertexUvs());
				
				modelFile.setVertices(exportGeo.vertices());
				
				//set normal first
				modelFile.setNormals(exportGeo.faces());
				
				modelFile.setFaces(exportGeo.faces());
				
				
				LogUtils.log("set-normal");
				
				//TODO
				
				//support normals
				//support bones
				
				
				if(exportGeo.getSkinIndices().length()>0){
				modelFile.setSkinIndicesAndWeights(exportGeo.getSkinIndices(),exportGeo.getSkinWeight());
				}
				
				//LogUtils.log(modelFile);
				
				String fileName=saveFileBox.getText();
				if(fileName.isEmpty()){
					fileName="untitled.js";
				}
				LogUtils.log(modelFile.getJsonText());
				if(anchror!=null){
					anchror.removeFromParent();
					anchror=null;
				}
				anchror = new HTML5Download().generateTextDownloadLink(modelFile.getJsonText(), fileName, "Download");
				anchror.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						anchror.removeFromParent();
					}
				});
				linkContainer.add(anchror);
			}
		});
		
		Button testCompare=new Button("test compare",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				
				
				JSONModelFile modelFile=JSONModelFile.create();
				
				modelFile.setGeometryUvs(file1Object.getFaceVertexUvs());
				
				modelFile.setVertices(file1Object.vertices());
				
				//set normal first
				modelFile.setNormals(file1Object.faces());

				modelFile.setFaces(file1Object.faces());
				
				ModelCompare compare=new ModelCompare();
				
				LogUtils.log(loadedObject);
				LogUtils.log(modelFile);
				
				List<String> result=compare.compareCore((JSONModelFile)loadedObject, modelFile);
				
				//copy others
							
				
				
				
				if(result.size()==0){
					LogUtils.log("fine:same model");
				}else{
					LogUtils.log(Joiner.on("\n").join(result));
				}
				
			}
		});
		//stop test compare
		//add(testCompare);
	}
	private void updateButton(){
		boolean status=false;
		if(file1Object!=null && file2Object!=null){
			//check same weight
			status=true;
		}
		copyButton.setEnabled(status);
	}
	//copy
	
	//link container
	
}
