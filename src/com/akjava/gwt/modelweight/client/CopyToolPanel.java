package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
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
import com.google.gwt.user.client.ui.VerticalPanel;

public class CopyToolPanel extends VerticalPanel{

	private JSONObject file1Object;
	private int file1Vertex;
	private int file2Vertex;
	private boolean hasIndices;
	private boolean hasWeights;
	private JSONObject file2Object;
	private Button copyButton;
	private Label file2Label;
	public CopyToolPanel(){
		
		add(new Label("Copy src file's indices & weight data to dest one"));
		add(new Label("Src File"));
		final Label file1Label=new Label("not selected");
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
						JSONValue lastJsonValue = JSONParser.parseStrict(text);
						
						//TODO more validate
						JSONObject object=lastJsonValue.isObject();
						
						GWTGeometryUtils.loadJsonModel(text, new JSONLoadHandler() {
							
							@Override
							public void loaded(Geometry geometry,JsArray<Material> materials) {
								file1Vertex=geometry.vertices().length();
								file1InfoLabel.setText("Vertex:"+geometry.vertices().length()+" Indices:"+geometry.getSkinIndices().length()+",Weigths:"+geometry.getSkinWeight().length());	
							
								hasWeights=geometry.getSkinWeight().length()>0;
								hasIndices=geometry.getSkinIndices().length()>0;
							}
						});
						
						
						file1Object=object;
						file1Label.setText(file.getFileName());
						updateButton();
					}
				});
				reader.readAsText(file,"utf-8");
			}
		});
		
		

		add(new Label("Dest File"));
		file2Label = new Label("not selected");
		add(file2Label);
		
		final Label file2InfoLabel=new Label(".");
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
						JSONValue lastJsonValue = JSONParser.parseStrict(text);
						//TODO more validate
						JSONObject object=lastJsonValue.isObject();
						
						GWTGeometryUtils.loadJsonModel(text, new JSONLoadHandler() {
							
							@Override
							public void loaded(Geometry geometry,JsArray<Material> materials) {
								file2Vertex=geometry.vertices().length();
								file2InfoLabel.setText("Vertex:"+geometry.vertices().length()+" Indices:"+geometry.getSkinIndices().length()+",Weigths:"+geometry.getSkinWeight().length());	
							
								
							}
						});
						
						file2Object=object;
						file2Label.setText(file.getFileName());
						updateButton();
					}
				});
				reader.readAsText(file,"utf-8");
			}
		});
		copyButton = new Button("copy skinIndices & skinWeights");
		copyButton.setEnabled(false);
		add(copyButton);
		final HorizontalPanel linkContainer=new HorizontalPanel();
		add(linkContainer);
		copyButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				file2Object.put("skinIndices", file1Object.get("skinIndices"));
				file2Object.put("skinWeights", file1Object.get("skinWeights"));
				
				final Anchor anchror=new HTML5Download().generateTextDownloadLink(file2Object.toString(), file2Label.getText(), "Download");
				anchror.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						anchror.removeFromParent();
					}
				});
				linkContainer.add(anchror);
			}
		});
	}
	private void updateButton(){
		boolean status=false;
		if(file1Object!=null && file2Object!=null &&hasIndices && hasWeights && file1Vertex==file2Vertex){
			status=true;
		}
		copyButton.setEnabled(status);
	}
	//copy
	
	//link container
	
}
