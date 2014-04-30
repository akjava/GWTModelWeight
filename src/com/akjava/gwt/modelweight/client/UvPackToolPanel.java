package com.akjava.gwt.modelweight.client;


import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.CanvasResizer;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.HtmlColumn;
import com.akjava.gwt.lib.client.widget.cell.ListEditorGenerator;
import com.akjava.gwt.lib.client.widget.cell.ListEditorGenerator.ValueControler;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.modelweight.client.resorces.Bundles;
import com.akjava.gwt.modelweight.client.uvpack.ToStringValueListBox;
import com.akjava.gwt.modelweight.client.uvpack.UVPackData;
import com.akjava.gwt.modelweight.client.uvpack.UVPackDataConverter;
import com.akjava.gwt.modelweight.client.uvpack.UVPackDataEditor;
import com.akjava.gwt.modelweight.client.uvpack.UVPackDataEditor.UVPackDataEditorDriver;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.gwt.three.client.js.renderers.CanvasRenderer;
import com.akjava.lib.common.io.FileType;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UvPackToolPanel extends DeckLayoutPanel{

	private Geometry file1Object;

	private Geometry file2Object;
	private Button packButton;
	private Label file2Label;

	//private TextBox saveFileBox;
	
	private UVPackDataEditorDriver driver=GWT.create(UVPackDataEditorDriver.class);
	private EasyCellTableObjects<UVPackData> easyCellTableObjects;
	
	private Canvas textureCanvas;
	private int canvasSize=512;
	private StorageControler storageControler=new StorageControler();
	private final String KEY_UV_SET="key_model_weight_uv_set";
	public UvPackToolPanel(){
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setSize("100%", "100%");
		add(scroll);
		this.showWidget(0);
		
		VerticalPanel root=new VerticalPanel();
		root.setSpacing(8);
		scroll.add(root);
		root.setSize("100%", "100%");
		
		HorizontalPanel h1=new HorizontalPanel();
		root.add(h1);
		h1.add(new Label("uv-line:size"));
		
		
		
		
		uvLineWidthBox = new ValueListBox<Double>(new Renderer<Double>() {
			@Override
			public String render(Double object) {
				if(object==null){
					return "";
				}
				return String.valueOf(object);
			}

			@Override
			public void render(Double object, Appendable appendable) throws IOException {
			}
		});
		uvLineWidthBox.setValue(0.1);
		
		uvLineWidthBox.setAcceptableValues(Lists.newArrayList(0.1,0.01,1.0,2.0,3.0,4.0,5.0));
		h1.add(uvLineWidthBox);
		
		uvLineColorBox = new ColorBox();
		uvLineColorBox.setValue("#000000");
		h1.add(uvLineColorBox);
		
		h1.add(new Label("image:size"));
		imageSizeValueListBox = new ToStringValueListBox<Integer>(Lists.newArrayList(512,1024,2048),canvasSize);
		imageSizeValueListBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				canvasSize=event.getValue();
				textureCanvas=CanvasUtils.createCanvas(textureCanvas,canvasSize, canvasSize);//TODO ondeman?
				
				if(uvCanvas!=null){//null canvas create on demand
					CanvasUtils.createCanvas(uvCanvas,canvasSize, canvasSize);
				}
			}
			
		});
		h1.add(imageSizeValueListBox);
		imageTypeValueListBox = new ToStringValueListBox<FileType>(Lists.newArrayList(FileType.PNG,FileType.JPEG,FileType.WEBP),FileType.PNG);
		h1.add(new Label("type"));
		h1.add(imageTypeValueListBox);
		
		
			
		h1.add(new Label("preset"));
		final ListBox presetListBox=new ListBox();
		presetListBox.addItem("");
		presetListBox.addItem("8-all");
		h1.add(presetListBox);
		
		Button loadBt=new Button("Load",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String text="";
				if(presetListBox.getSelectedIndex()==1){
					text=Bundles.INSTANCE.eightbase().getText();
				}
				List<UVPackData> datas=new UVPackDataConverter().reverse().convert(text);
				easyCellTableObjects.setDatas(datas);
				easyCellTableObjects.update(false);
				updateButton();
			}
		});
		h1.add(loadBt);
		
		
		
		textureCanvas=CanvasUtils.createCanvas(canvasSize, canvasSize);
		SimpleCellTable<UVPackData> uvPackTable=new SimpleCellTable<UVPackData>(999) {
			@Override
			public void addColumns(CellTable<UVPackData> table) {
				TextColumn<UVPackData> name=new TextColumn<UVPackData>() {
					@Override
					public String getValue(UVPackData object) {

						return object.getModelFileName();
					}
				};
				table.addColumn(name,"name");
				
				 Column<UVPackData, Number> vertices = new Column<UVPackData, Number>(new NumberCell()) {
					 @Override
			            public Integer getValue(UVPackData object) {
			                if(object.getGeometry()==null){
			                	return -1;
			                }else{
			                	return object.getGeometry().vertices().length();
			                }
			            }
			        };
			        table.addColumn(vertices,"vertices");
			        
			        //TODO support image
			        
			        Column<UVPackData, Number> split = new Column<UVPackData, Number>(new NumberCell()) {
						 @Override
				            public Integer getValue(UVPackData object) {
				                return object.getSplit();
				            }
				        };
				        table.addColumn(split,"split");
				        
				        
				        Column<UVPackData, Number> x = new Column<UVPackData, Number>(new NumberCell()) {
							 @Override
					            public Integer getValue(UVPackData object) {
					                return object.getX();
					            }
					        };
					        table.addColumn(x,"x");
					        
					        
					        Column<UVPackData, Number> y = new Column<UVPackData, Number>(new NumberCell()) {
								 @Override
						            public Integer getValue(UVPackData object) {
						                return object.getY();
						            }
						        };
						        table.addColumn(y,"y");
						        
						        
						        TextColumn<UVPackData> scale=new TextColumn<UVPackData>() {
									@Override
									public String getValue(UVPackData object) {
										if(object.getModelFile()!=null){
										return ""+object.getModelFile().getScale();
										}else{
											return "";
										}
									}
								};
								table.addColumn(scale,"scale");//TODO add scale for check
						        
								  TextColumn<UVPackData> comment=new TextColumn<UVPackData>() {
										@Override
										public String getValue(UVPackData object) {

											return object.getComment();
										}
									};
									table.addColumn(comment,"comment");//TODO add scale for check
									
									
									 
						        HtmlColumn<UVPackData> img=new HtmlColumn<UVPackData>() {
									@Override
									public String toHtml(UVPackData object) {
										if(object.getTexture()==null){
											return "";
										}
										return "<img width='64' src='"+object.getTexture().getSrc()+"'>";
									}
								};
								  table.addColumn(img);
								  
								  
								 
			
			}
		};
		UVPackDataEditor editor=new UVPackDataEditor(); 
		ListEditorGenerator<UVPackData> generator=new ListEditorGenerator<UVPackData>(){
			@Override
			public void onUpdateData(){
				updateButton();
			}
			
			
			@Override
			public UVPackData createNewData() {
				// TODO Auto-generated method stub
				return new UVPackData();
			}};
		
		//TODO make simple method
		VerticalPanel panel=generator.generatePanel(uvPackTable, new UVPackDataConverter(), editor, driver,null, new ValueControler() {

			@Override
			public void setValue(String value) {
				
				try {
					storageControler.setValue(KEY_UV_SET, value);
				} catch (StorageException e) {
					Window.alert(e.getMessage());
				}
			}

			@Override
			public String getValue() {
				
				return storageControler.getValue(KEY_UV_SET,"");
			}
		});
		
		easyCellTableObjects=generator.getEasyCells();
		
		uvPackTable.getCellTable().insertColumn(0,easyCellTableObjects.generateUpDownActionColumn("Up", "Down"));
		
		root.add(panel);
		
		root.add(editor);
		
		
	
		
		
		
		packButton = new Button("Pack Uv");
		packButton.setEnabled(false);
		
		root.add(packButton);
		
		
		/*
		HorizontalPanel fileNames=new HorizontalPanel();
		root.add(fileNames);
		fileNames.add(new Label("SaveName:"));
		saveFileBox = new TextBox();
		saveFileBox.setText("uvpacked.js");
		fileNames.add(saveFileBox);
		*/
		
		final HorizontalPanel linkContainer=new HorizontalPanel();
		root.add(linkContainer);
		packButton.addClickHandler(new ClickHandler() {
			
			private Anchor anchror;

			@Override
			public void onClick(ClickEvent event) {
				packButton.setEnabled(false);
				Timer timer=new Timer(){
					@Override
					public void run() {
						JSONModelFile modelFile=packUvJsonModels();
						
						
						//LogUtils.log(modelFile.getJsonText());
						linkContainer.clear();
						
						
						if(anchror!=null){
							anchror.removeFromParent();
							anchror=null;
						}
						if(modelFile!=null){
						anchror = new HTML5Download().generateTextDownloadLink(modelFile.getJsonText(), "uvpacked.js", "Download js-model");
						anchror.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								anchror.removeFromParent();
							}
						});
						linkContainer.add(anchror);
						
						}
						
						
						//create images
						 
						String url=createPackedImage();
						final Anchor imageAnchor = new HTML5Download().generateBase64DownloadLink(url, "image/"+getImageMime(), "paccked."+getImageExtension(), "Download image", true);
						imageAnchor.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								imageAnchor.removeFromParent();
							}
						});
						
						linkContainer.add(imageAnchor);
				
						Image img=new Image(url);
						linkContainer.add(img);
						
						
						//create uv pack image
						if(modelFile!=null){
							
						String uvImageUrl=createUvImage(modelFile);
						final Anchor uvImageAnchor = new HTML5Download().generateBase64DownloadLink(uvImageUrl, "image/"+"png", "pacckedUv."+"png", "Download uv image", true);
						uvImageAnchor.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								uvImageAnchor.removeFromParent();
							}
						});
						
						linkContainer.add(uvImageAnchor);
						Image uvImg=new Image(uvImageUrl);
						linkContainer.add(uvImg);
						}
						
						packButton.setEnabled(true);
					
					}

					
				};
				timer.schedule(5);
				
			
				
			}
		});
		
		updateButton();
	}
	
	private Canvas uvCanvas;

	private ValueListBox<Double> uvLineWidthBox;

	private ColorBox uvLineColorBox;

	private ToStringValueListBox<Integer> imageSizeValueListBox;

	private ToStringValueListBox<FileType> imageTypeValueListBox;
	private String createUvImage(JSONModelFile modelFile){
		
	
		
		if(uvCanvas==null){
			uvCanvas=CanvasUtils.createCanvas(canvasSize, canvasSize);
		}else{
			CanvasUtils.clear(uvCanvas);
		}
		
		uvCanvas.getContext2d().save();
		//need flip image,TODO method
		uvCanvas.getContext2d().translate(0, uvCanvas.getCoordinateSpaceHeight()); 
		uvCanvas.getContext2d().scale(1, -1);
		
		uvCanvas.getContext2d().setStrokeStyle(uvLineColorBox.getValue());
		uvCanvas.getContext2d().setLineWidth(uvLineWidthBox.getValue());
		uvCanvas.getContext2d().setLineCap(LineCap.ROUND);
		/*
		for(UVPackData data:easyCellTableObjects.getDatas()){
			createUvImage(uvCanvas,data.getModelFile(),data.getGeometry(),data.getSplit(),data.getX(),data.getY());
		}
		*/
		createUvImage(uvCanvas,modelFile,null,0,0,0);
		
		uvCanvas.getContext2d().restore();
		
		return uvCanvas.toDataUrl("image/png");
	}
	private static void createUvImage(Canvas canvas,JSONModelFile model,Geometry geometry,int split,int x,int y){
		double csize=canvas.getCoordinateSpaceWidth();
		
	
		//parssing jsonmodelfile face list
		JsArrayNumber faces=model.getFaces();
		
		//parsing 3.1
		//https://github.com/mrdoob/three.js/wiki/JSON-Model-format-3
			
		for(int i=0;i<faces.length();){
			int fv=i++;	//bitmask has uv & normal
			int format=(int)faces.get(fv);
			boolean quad=(format &1)==1;
			
			int f1=i++;
			int f2=i++;
			int f3=i++;
			
			if(quad){
				int f4=i++;
				LogUtils.log("quad:"+format);
			}
			
			int uv1Index=i++;
			int uv2Index=i++;
			int uv3Index=i++;
			
			int uv1=(int) faces.get(uv1Index);
			int uv2=(int) faces.get(uv2Index);
			int uv3=(int) faces.get(uv3Index);
			
			
			double x1=model.getUvs().get(0).get(uv1*2)*csize;
			double y1=model.getUvs().get(0).get(uv1*2+1)*csize;
			double x2=model.getUvs().get(0).get(uv2*2)*csize;
			double y2=model.getUvs().get(0).get(uv2*2+1)*csize;
			double x3=model.getUvs().get(0).get(uv3*2)*csize;
			double y3=model.getUvs().get(0).get(uv3*2+1)*csize;
			
			if(quad){//bug not work
				int uv4Index=i++;
				int uv4=(int) faces.get(uv4Index);
				double x4=model.getUvs().get(0).get(uv4*2)*csize;
				double y4=model.getUvs().get(0).get(uv4*2+1)*csize;
				

				List<Double> points=Lists.newArrayList(x1,y1,x2,y2,x3,y3,x4,y4,x1,y1);
				drawCanvas(canvas,points,false);
			}else{

				List<Double> points=Lists.newArrayList(x1,y1,x2,y2,x3,y3,x1,y1);
				drawCanvas(canvas,points,false);
			}
			
			
			int n1=i++;
			int n2=i++;
			int n3=i++;
			if(quad){
				int n4=i++;
			}
		}
		
	}
	
	private static void drawCanvas(Canvas canvas,List<Double> points,boolean fill){
		canvas.getContext2d().beginPath();
		for(int i=0;i<points.size();i+=2){
			double x=points.get(i);
			double y=points.get(i+1);
			if(i==0){
				canvas.getContext2d().moveTo(x, y);
			}else{
				canvas.getContext2d().lineTo(x, y);
			}
		}
		canvas.getContext2d().closePath();
		
		if(fill){
			canvas.getContext2d().fill();
		}else{
			canvas.getContext2d().stroke();
		}
	}
	
	private Canvas resizeCanvas;
	private void packUVImage(ImageElement element,int split,int x,int y){
		
		if(element==null){
			return;
		}
		int canvasSize=textureCanvas.getCoordinateSpaceWidth();
		int unitSize=canvasSize/split;
		
		if(resizeCanvas==null){
			resizeCanvas=Canvas.createIfSupported();
		}
		Canvas resized=CanvasResizer.on(resizeCanvas).image(element).width(unitSize).downscale(false).toCanvas();//downscale transparent problem
		
		textureCanvas.getContext2d().drawImage(resized.getCanvasElement(), unitSize*x, unitSize*y);
	}
	
	private String getImageMime(){
		return imageTypeValueListBox.getValue().getSubtype();
	}
	private String getImageExtension(){
		return imageTypeValueListBox.getValue().getExtension();
	}
	private String createPackedImage() {
		
		//resize canvas 
		CanvasUtils.clear(textureCanvas);
		
		for(int i=0;i<easyCellTableObjects.getDatas().size();i++){
			UVPackData data=easyCellTableObjects.getDatas().get(i);
			
			packUVImage(data.getTexture(),data.getSplit(),data.getX(),data.getY());
			
		}
		
		return textureCanvas.toDataUrl("image/"+getImageMime());
	}
	
	private void packUV(Geometry geometry,int split,int x,int y){
		y=split-1-y;//flip
		if(split<=1){//0 is invalid ,1 no need
			return;
		}
		double offX=1.0/split*x;
		double offY=1.0/split*y;
		for(int i=0;i<geometry.getFaceVertexUvs().length();i++){
			JsArray<JsArray<Vector2>> arrays=geometry.getFaceVertexUvs().get(i);
			
			for(int k=0;k<arrays.length();k++){//usually j is 3 for each face
				JsArray<Vector2> array=arrays.get(k);
			for(int j=0;j<array.length();j++){
				Vector2 vec2=array.get(j);
				double nx=vec2.getX()/split+offX;
				double ny=vec2.getY()/split+offY;
				vec2.set(nx, ny);
			}
			}
		}
	}
	
	
	private JSONModelFile packUvJsonModels(){
		if(easyCellTableObjects.getDatas().size()==0){
			return null;
		}
		JSONModelFile baseModelFile=easyCellTableObjects.getDatas().get(0).getModelFile();
		if(baseModelFile==null){
			//maybe preset
			LogUtils.log("model-1 is null");
			return null;
		}
		//keep original clean for re-try
		LogUtils.log(baseModelFile);
		Geometry geometry=GWTGeometryUtils.clonePlusWeights(easyCellTableObjects.getDatas().get(0).getGeometry());
		
		
		LogUtils.log("model-1");
		LogUtils.log(geometry);
		LogUtils.log("vertices:"+geometry.getVertices().length()+" uvs:"+geometry.getFaceVertexUvs().get(0).length()+" weight:"+geometry.getSkinIndices().length());
		UVPackData data=easyCellTableObjects.getDatas().get(0);
		packUV(geometry,data.getSplit(),data.getX(),data.getY());
		
		for(int i=1;i<easyCellTableObjects.getDatas().size();i++){
			data=easyCellTableObjects.getDatas().get(i);
			if(data.getGeometry()==null){
				continue;
			}
			LogUtils.log("model-"+(i+1));
			
			
			Geometry second=GWTGeometryUtils.clonePlusWeights(data.getGeometry());
			LogUtils.log(second);
			LogUtils.log("vertices:"+second.getVertices().length()+" uvs:"+second.getFaceVertexUvs().get(0).length()+" weight:"+second.getSkinIndices().length());
			
			
			packUV(second,data.getSplit(),data.getX(),data.getY());
			
			geometry=GWTGeometryUtils.mergeGeometryPlusWeights(geometry, second);
			
			LogUtils.log("merged-"+(i+1));
			LogUtils.log(geometry);
			LogUtils.log("vertices:"+geometry.getVertices().length()+" uvs:"+geometry.getFaceVertexUvs().get(0).length()+" weight:"+geometry.getSkinIndices().length());
			
		}
		
		//SkinnedMesh mesh=THREE.SkinnedMesh(geometry, THREE.MeshBasicMaterial(null)); not solve normal map
		
		LogUtils.log("merged-vertices:"+geometry.getVertices().length());

		int faceCount=geometry.faces().length();
		
		
		baseModelFile.setGeometryUvs(geometry.getFaceVertexUvs());
		
		int predictUvs=faceCount*3*2;
		if(predictUvs!=baseModelFile.getUvs().get(0).length()){
			LogUtils.log("invalid uvs-size:must be "+predictUvs+", but value is "+baseModelFile.getUvs().get(0).length());
		}
		
		baseModelFile.setVertices(geometry.vertices());
		
		int predictVertices=geometry.getVertices().length()*3;
		if(predictVertices!=baseModelFile.getVertices().length()){
			LogUtils.log("invalid vertices-size:must be "+predictVertices+", but value is "+baseModelFile.getVertices().length());
		}
		
		//set normal first
		baseModelFile.setNormals(geometry.faces());
		int predictNormals=faceCount*3*3;//each vertex *xyz
		if(predictNormals!=baseModelFile.getNormals().length()){
			LogUtils.log("invalid normal-size:must be "+predictNormals+", but value is "+baseModelFile.getNormals().length()+",faces="+geometry.faces().length());
		}
		
		baseModelFile.setFaces(geometry.faces());
		int predictFaces=faceCount*10;//first is define,face-index,uv-index,normal-index
		if(predictFaces!=baseModelFile.getFaces().length()){
			LogUtils.log("invalid face-size:must be "+predictFaces+", but value is "+baseModelFile.getFaces().length());
		}
		
		if(baseModelFile.getSkinIndices().length()>0){
			baseModelFile.setSkinIndicesAndWeights(geometry.getSkinIndices(),geometry.getSkinWeight());
		}
		
		return baseModelFile;
	}
	
	//these are totally miss algolitm even,and extreamly slow
	//need compare vertices which face indicate.but these action freese 
	protected boolean isSameFace(Face3 f1, Face3 f2) {
		
		
		return f1.getA() == f2.getA() && f1.getB() == f2.getB() && f1.getC() == f2.getC();
	}
	
	public  final native boolean isNativeSameFace(Face3 f1, Face3 f2)/*-{
	return f1.a == f2.a && f1.b == f2.b && f1.c == f2.c;
	}-*/;
	
	private void updateButton(){
		boolean status=easyCellTableObjects.getDatas().size()>0;
		packButton.setEnabled(status);
	}
	//copy
	
	//link container
	
}
