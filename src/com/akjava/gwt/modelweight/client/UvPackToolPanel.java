package com.akjava.gwt.modelweight.client;


import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.HtmlColumn;
import com.akjava.gwt.lib.client.widget.cell.ListEditorGenerator;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.modelweight.client.uvpack.UVPackData;
import com.akjava.gwt.modelweight.client.uvpack.UVPackDataEditor;
import com.akjava.gwt.modelweight.client.uvpack.UVPackDataEditor.UVPackDataEditorDriver;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UvPackToolPanel extends VerticalPanel{

	private Geometry file1Object;

	private Geometry file2Object;
	private Button copyButton;
	private Label file2Label;

	private TextBox saveFileBox;
	
	private UVPackDataEditorDriver driver=GWT.create(UVPackDataEditorDriver.class);
	private EasyCellTableObjects<UVPackData> easyCellTableObjects;
	
	private Canvas canvas;
	public UvPackToolPanel(){
		canvas=CanvasUtils.createCanvas(512, 512);
		SimpleCellTable<UVPackData> uvPackTable=new SimpleCellTable<UVPackData>(999) {
			@Override
			public void addColumns(CellTable<UVPackData> table) {
				TextColumn<UVPackData> name=new TextColumn<UVPackData>() {
					@Override
					public String getValue(UVPackData object) {

						return object.getFileName();
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
			public UVPackData createNewData() {
				// TODO Auto-generated method stub
				return new UVPackData();
			}};
		
		//TODO make simple method
		VerticalPanel panel=generator.generatePanel(uvPackTable, null, editor, driver,null, null);
		
		easyCellTableObjects=generator.getEasyCells();
		
		add(panel);
		
		add(editor);
		
		
		HorizontalPanel fileNames=new HorizontalPanel();
		add(fileNames);
		fileNames.add(new Label("SaveName:"));
		saveFileBox = new TextBox();
		saveFileBox.setText("uvpacked.js");
		fileNames.add(saveFileBox);
		
		
		
		copyButton = new Button("Pack Uv");
		//copyButton.setEnabled(false);
		add(copyButton);
		final HorizontalPanel linkContainer=new HorizontalPanel();
		add(linkContainer);
		copyButton.addClickHandler(new ClickHandler() {
			
			private Anchor anchror;

			@Override
			public void onClick(ClickEvent event) {
				copyButton.setEnabled(false);
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
						anchror = new HTML5Download().generateTextDownloadLink(modelFile.getJsonText(), saveFileBox.getText(), "Download object");
						anchror.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								anchror.removeFromParent();
							}
						});
						linkContainer.add(anchror);
						copyButton.setEnabled(true);
						
						//create images
						String url=createPackedImage();
						final Anchor imageAnchor = new HTML5Download().generateBase64DownloadLink(url, "image/"+getImageMime(), "paccked."+getImageExtension(), "Download image", true);
						imageAnchor.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								imageAnchor.removeFromParent();
							}
						});
						linkContainer.add(anchror);
						linkContainer.add(imageAnchor);
						copyButton.setEnabled(true);
						Image img=new Image(url);
						linkContainer.add(img);
					}

					
				};
				timer.schedule(5);
				
			
				
			}
		});
	}
	
	private void packUVImage(ImageElement element,int split,int x,int y){
		if(element==null){
			return;
		}
		int canvasSize=canvas.getCoordinateSpaceWidth();
		int unitSize=canvasSize/split;
		canvas.getContext2d().drawImage(element, unitSize*x, unitSize*y,unitSize,unitSize);
	}
	
	private String getImageMime(){
		return "jpeg";
	}
	private String getImageExtension(){
		return "jpg";
	}
	private String createPackedImage() {
		//resize canvas 
		CanvasUtils.clear(canvas);
		
		for(int i=0;i<easyCellTableObjects.getDatas().size();i++){
			UVPackData data=easyCellTableObjects.getDatas().get(i);
			packUVImage(data.getTexture(),data.getSplit(),data.getX(),data.getY());
		}
		
		return canvas.toDataUrl("image/"+getImageMime());
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
		//keep original clean for re-try
		Geometry geometry=GWTGeometryUtils.clonePlusWeights(easyCellTableObjects.getDatas().get(0).getGeometry());
		
		
		LogUtils.log("model-1");
		LogUtils.log(geometry);
		LogUtils.log("vertices:"+geometry.getVertices().length()+" uvs:"+geometry.getFaceVertexUvs().get(0).length()+" weight:"+geometry.getSkinIndices().length());
		UVPackData data=easyCellTableObjects.getDatas().get(0);
		packUV(geometry,data.getSplit(),data.getX(),data.getY());
		
		for(int i=1;i<easyCellTableObjects.getDatas().size();i++){
			data=easyCellTableObjects.getDatas().get(i);
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
