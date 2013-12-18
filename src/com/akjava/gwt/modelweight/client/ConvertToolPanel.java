package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.model.JSONModelFile;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.lib.common.utils.FileNames;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ConvertToolPanel extends VerticalPanel{

	private Geometry file1Object;

	private Geometry file2Object;
	private Button copyButton;
	private Label file2Label;

	private TextBox saveFileBox;

	private Label convertLabel;

	private CheckBox convert31;

	private Label infoLabel;

	private Button convertButton;
	private JSONModelFile jsonmodel;
	private String fileName;

	private HorizontalPanel linkContainer;
	public ConvertToolPanel(){
		convert31 = new CheckBox("");
		convert31.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(convert31.getValue()){
					convertLabel.setText("3.0 to new 3.1 format");
				}else{
					convertLabel.setText("3.1 to old 3.0 format");
				}
				updateButtons();
			}
		});
		convert31.setValue(true);
		HorizontalPanel hp=new HorizontalPanel();
		add(hp);
		hp.add(convert31);
		convertLabel = new Label("3.0 to new 3.1 format");
		hp.add(convertLabel);
		
		add(new Label("json model file"));
		
		final FileUploadForm srcFile=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			

			@Override
			public void uploaded(File file, String value) {
				fileName=file.getFileName();
				JSONValue jsonValue = JSONParser.parseLenient(value);
				JSONObject object=jsonValue.isObject();
				if(object!=null){
					jsonmodel = (JSONModelFile) object.getJavaScriptObject();
					LogUtils.log(jsonmodel);
					updateButtons();
				}
			}
		}, true);
		srcFile.setShowDragOverBorder(true);
		add(srcFile);
		
		add(new Label("just converting UV's V value to flip on JSON-level.And modify metadata."));
		convertButton = new Button("Convert",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JSONModelFile newModel=jsonmodel.clone();
				
				//right now just flipV of Uv
				flipV(newModel);
				
				newModel.getMetaData().setGeneratedBy(GWTModelWeight.getGeneratedBy());
				if(convert31.getValue()){
					newModel.getMetaData().setFormatVersion(3.1);
				}else{
					newModel.getMetaData().setFormatVersion(3.0);
				}
				linkContainer.clear();
				final Anchor anchror=new HTML5Download().generateTextDownloadLink(newModel.getJsonText(), createFileName(), "Download");
				anchror.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						//anchror.removeFromParent();
					}
				});
				linkContainer.add(anchror);
			}
		});
		add(convertButton);
		convertButton.setEnabled(false);
		infoLabel = new Label();
		infoLabel.setStylePrimaryName("gray");
		add(infoLabel);
		linkContainer = new HorizontalPanel();
		add(linkContainer);
	}
	
	protected String createFileName() {
		String extension=FileNames.getExtension(fileName);
		String name=FileNames.getRemovedExtensionName(fileName);
		String ver=convert31.getValue()?"_v31":"_v30";
		return name+ver+"."+extension;
	}

	protected void updateButtons() {
		if(jsonmodel==null){
			return;
		}
		if(convert31.getValue()){
			if(jsonmodel.getMetaData().getFormatVersion()==3.1){
				convertButton.setEnabled(false);
				infoLabel.setText("model format is already 3.1");
			}else if(jsonmodel.getMetaData().getFormatVersion()!=3){
				convertButton.setEnabled(false);
				infoLabel.setText("model format is "+jsonmodel.getMetaData().getFormatVersion()+".only support 3 to 3.1");
			}else{
				infoLabel.setText("");
				convertButton.setEnabled(true);
			}
		}else{
			if(jsonmodel.getMetaData().getFormatVersion()==3){
				convertButton.setEnabled(false);
				infoLabel.setText("model format is already 3");
			}else if(jsonmodel.getMetaData().getFormatVersion()!=3.1){
				convertButton.setEnabled(false);
				infoLabel.setText("model format is "+jsonmodel.getMetaData().getFormatVersion()+".only support 3.1 to 3.0");
			}else{
				infoLabel.setText("");
				convertButton.setEnabled(true);
			}
		}
		
	}

	public void flipV(JSONModelFile model){
		
		JsArray<JsArrayNumber> uvs= model.getUvs();
		if(uvs==null){
			return;
		}
		for(int i=0;i<uvs.length();i++){
			JsArrayNumber array=uvs.get(i);
			for(int j=0;j<array.length();j++){
				if(j%2==1){//v only
					double old=array.get(j);
					array.set(j, 1-old);
				}
			}
		}
	}
	
	
}
