package com.akjava.gwt.modelweight.client.uvpack;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.loaders.JSONLoader;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UVPackDataEditor extends VerticalPanel implements Editor<UVPackData> {
	public interface UVPackDataEditorDriver extends SimpleBeanEditorDriver<UVPackData, UVPackDataEditor> {}
	SimpleEditor<Geometry> geometryEditor;
	SimpleEditor<JSONModelFile> modelFileEditor; 
	Label fileNameEditor;
	SimpleEditor<Integer> faceTypeEditor;//can't use because until export ,no way to know
	SimpleEditor<ImageElement> textureEditor;
	
	ValueListBox<Integer> xEditor;
	ValueListBox<Integer> yEditor;
	ValueListBox<Integer> splitEditor;
	//ValueListBox<Integer> 
	
	@Ignore
	private Label imageSizeLabel;
	public UVPackDataEditor(){
		
		add(new Label("dont drop file here.first tab handle all drop event"));
		
		modelFileEditor=SimpleEditor.of();
		
		fileNameEditor=new Label();
		add(fileNameEditor);
		
		imageSizeLabel=new Label();
		add(imageSizeLabel);
		
		faceTypeEditor=SimpleEditor.of();
		textureEditor=SimpleEditor.of();
		geometryEditor=SimpleEditor.of();
		
		List<Integer> xylist=Lists.newArrayList(0,1,2,3,4,5,6,7);
		
		splitEditor=createToStringListBox(Lists.newArrayList(1,2,4,8),2);
		add(new Label("split"));
		add(splitEditor);
		
		xEditor=createToStringListBox(xylist,0);
		add(new Label("x"));
		add(xEditor);
		yEditor=createToStringListBox(xylist,0);
		add(new Label("y"));
		add(yEditor);
		
		
		HorizontalPanel fPanel=new HorizontalPanel();
		
		
	
		
		
		
		
		
		FileUploadForm uploadForm=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				fileNameEditor.setText(file.getFileName());
				
			
				
				JSONLoadHandler handler=new JSONLoadHandler() {
					
					@Override
					public void loaded(Geometry geometry,JsArray<Material> materials) {
						LogUtils.log(geometry);
						geometryEditor.setValue(geometry);
					}
				};

				
				JSONLoader loader=THREE.JSONLoader();
				JSONValue lastJsonValue = JSONParser.parseStrict(value);
				JSONObject object=lastJsonValue.isObject();
				if(object==null){
					LogUtils.log("null loaded:");
					handler.loaded(null, null);
					return;
				}
				
				JSONModelFile modelFile=(JSONModelFile)object.getJavaScriptObject();
				modelFileEditor.setValue(modelFile);
				LogUtils.log(modelFile);
				
				JavaScriptObject jsObject=loader.parse(object.getJavaScriptObject(), null);
				if(jsObject==null){
					LogUtils.log("some how parse faild:");
					LogUtils.log(object.getJavaScriptObject());
				}else{
					JSONObject newobject=new JSONObject(jsObject);
					JSONObject geoObject=newobject.get("geometry").isObject();
					if(geoObject!=null){
						handler.loaded((Geometry) geoObject.getJavaScriptObject(), null);
					}else{
						LogUtils.log("geometry not contain:");
						LogUtils.log(jsObject);
					}
				}
				
				
				//
			}
		}, true);
		uploadForm.setAccept(Lists.newArrayList(".js",".json"));//file name filter
		
		
		add(fPanel);
		fPanel.add(createLabel("Model"));
		fPanel.add(uploadForm);
		
		
		
		
		HorizontalPanel imagePanel=new HorizontalPanel();
		imagePanel=new HorizontalPanel();
		add(imagePanel);
		fPanel.add(createLabel("Texture"));
		
		FileUploadForm imageForm=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				LogUtils.log(value);
				ImageElement element=ImageElementUtils.create(value);
				
				//imageSizeLabel.setText(element.getWidth()+"x"+element.getHeight());
				
				element.setWidth(64);
				textureEditor.setValue(element);
			}
		}, true);
		imageForm.setAccept(Lists.newArrayList(".jpg",".png",".webp"));//file name filter
		fPanel.add(imageForm);
		
		
	}
	
	private Widget createLabel(String string) {
		Label label=new Label(string);
		label.setWidth("100px");
		return label;
	}

	//TODO move lib
	@Ignore
	public <T> ValueListBox<T> createToStringListBox(){
		return createToStringListBox(null,null);
	}
	@Ignore
	public <T> ValueListBox<T> createToStringListBox(List<T> accepts,T value){
		ValueListBox<T> listBox=new ValueListBox<T>(new Renderer<T>() {

			@Override
			public String render(T object) {
				// TODO Auto-generated method stub
				if(object==null){
					return null;
				}
				return String.valueOf(object);
			}

			@Override
			public void render(T object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
			
		});
		if(value!=null){
			listBox.setValue(value);
		}
		if(accepts!=null){
			listBox.setAcceptableValues(accepts);
		}
		return listBox;
	}
}
