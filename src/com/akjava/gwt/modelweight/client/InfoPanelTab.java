package com.akjava.gwt.modelweight.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.bvh.client.BVHNode;
import com.akjava.bvh.client.Channels;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.google.common.base.Joiner;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoPanelTab extends VerticalPanel {
private EasyCellTableObjects<BVHNode> bvhObjects;
private EasyCellTableObjects<AnimationBone> bvhAnimationObjects;
private EasyCellTableObjects<AnimationBone> geometryAnimationObjects;

public EasyCellTableObjects<AnimationBone> getGeometryAnimationObjects() {
	return geometryAnimationObjects;
}


public EasyCellTableObjects<BVHNode> getBvhObjects() {
	return bvhObjects;
}


private ScrollPanel createBVHAnimationPanel(){

	ScrollPanel scroll=new ScrollPanel();
	
	VerticalPanel bvhBone=new VerticalPanel();
	scroll.add(bvhBone);
	SimpleCellTable<AnimationBone> bvhNodeTable =  createAnimationBoneTable();
	bvhBone.add(bvhNodeTable);
	bvhAnimationObjects = new EasyCellTableObjects<AnimationBone>(bvhNodeTable) {
		@Override
		public void onSelect(AnimationBone selection) {
			
		}
	};
		
	return scroll;
}

private ScrollPanel createGeometryAnimationPanel(){

	ScrollPanel scroll=new ScrollPanel();
	
	VerticalPanel bvhBone=new VerticalPanel();
	scroll.add(bvhBone);
	SimpleCellTable<AnimationBone> bvhNodeTable =  createAnimationBoneTable();
	bvhBone.add(bvhNodeTable);
	geometryAnimationObjects = new EasyCellTableObjects<AnimationBone>(bvhNodeTable) {
		@Override
		public void onSelect(AnimationBone selection) {
			
		}
	};
		
	return scroll;
}

private SimpleCellTable<AnimationBone> createAnimationBoneTable(){
	SimpleCellTable<AnimationBone> bvhNodeTable=new SimpleCellTable<AnimationBone>(999) {

		@Override
		public void addColumns(CellTable<AnimationBone> table) {
			TextColumn<AnimationBone> parent=new TextColumn<AnimationBone>() {
				@Override
				public String getValue(AnimationBone object) {
					if(object.getParent()==-1){
						return "";
					}
					return bvhAnimationObjects.getDatas().get(object.getParent()).getName();
					
				}
			};
			table.addColumn(parent,"Parent");
			
			TextColumn<AnimationBone> name=new TextColumn<AnimationBone>() {
				@Override
				public String getValue(AnimationBone object) {
					return object.getName();
				}
			};
			table.addColumn(name,"Name");
			
			  Column<AnimationBone, Number> offsetX = new Column<AnimationBone, Number>(new NumberCell()) {
		            @Override
		            public Double getValue(AnimationBone object) {
		                //return object.
		            	return object.getPos().get(0);
		            }
		        };
		    table.addColumn(offsetX,"posX");
		    
		    Column<AnimationBone, Number> offsetY = new Column<AnimationBone, Number>(new NumberCell()) {
	            @Override
	            public Double getValue(AnimationBone object) {
	                //return object.
	            	return object.getPos().get(1);
	            }
	        };
	    table.addColumn(offsetY,"posY");
	    
	    
	    Column<AnimationBone, Number> offsetZ = new Column<AnimationBone, Number>(new NumberCell()) {
            @Override
            public Double getValue(AnimationBone object) {
                //return object.
            	return object.getPos().get(2);
            }
        };
    table.addColumn(offsetZ,"posZ");
        
    TextColumn<AnimationBone> rotq=new TextColumn<AnimationBone>() {
		@Override
		public String getValue(AnimationBone object) {
			JsArrayNumber rotq=object.getRotq();
			return rotq.join(" ");
		}
	};
	table.addColumn(rotq,"rotQ");
		        
			
			
		}
		
	};
	
	return bvhNodeTable;
}

private ScrollPanel createBVHBonePanel(){

	ScrollPanel scroll=new ScrollPanel();
	
	
	VerticalPanel bvhBone=new VerticalPanel();
	scroll.add(bvhBone);
	SimpleCellTable<BVHNode> bvhNodeTable=new SimpleCellTable<BVHNode>(999) {

		@Override
		public void addColumns(CellTable<BVHNode> table) {
			TextColumn<BVHNode> parent=new TextColumn<BVHNode>() {
				@Override
				public String getValue(BVHNode object) {
					if(object.getParentName()==null){
						return "";
					}else{
						return object.getParentName();
					}
					
				}
			};
			table.addColumn(parent,"Parent");
			
			TextColumn<BVHNode> name=new TextColumn<BVHNode>() {
				@Override
				public String getValue(BVHNode object) {
					return object.getName();
				}
			};
			table.addColumn(name,"Name");
			
			  Column<BVHNode, Number> offsetX = new Column<BVHNode, Number>(new NumberCell()) {
		            @Override
		            public Double getValue(BVHNode object) {
		                //return object.
		            	return object.getOffset().getX();
		            }
		        };
		    table.addColumn(offsetX,"offX");
		    
		    Column<BVHNode, Number> offsetY = new Column<BVHNode, Number>(new NumberCell()) {
	            @Override
	            public Double getValue(BVHNode object) {
	                //return object.
	            	return object.getOffset().getY();
	            }
	        };
	        	table.addColumn(offsetY,"offY");
	    
	    
	    Column<BVHNode, Number> offsetZ = new Column<BVHNode, Number>(new NumberCell()) {
            @Override
            public Double getValue(BVHNode object) {
                //return object.
            	return object.getOffset().getZ();
            }
        };
        table.addColumn(offsetZ,"offZ");
        
        TextColumn<BVHNode> channel=new TextColumn<BVHNode>() {
			@Override
			public String getValue(BVHNode object) {
				List<String> ret=new ArrayList<String>();
				Channels ch=object.getChannels();
				if(ch.isXposition()){
					ret.add("posX");
				}
				if(ch.isYposition()){
					ret.add("posY");
				}
				if(ch.isZposition()){
					ret.add("posZ");
				}
				if(ch.isXrotation()){
					ret.add("rotX");
				}
				if(ch.isYrotation()){
					ret.add("rotY");
				}
				if(ch.isZrotation()){
					ret.add("rotZ");
				}
				return Joiner.on(" ").join(ret);
			}
		};
		table.addColumn(channel,"channel");
		        
			
			
		}
		
	};
	
	bvhBone.add(bvhNodeTable);
	bvhObjects = new EasyCellTableObjects<BVHNode>(bvhNodeTable) {
		@Override
		public void onSelect(BVHNode selection) {
			
		}
	};
	return scroll;
}
public InfoPanelTab(){
	TabLayoutPanel tab=new TabLayoutPanel(30, Unit.PX);
	add(tab);
	tab.setWidth("100%");
	tab.setHeight("100%");
	
	tab.add(createBVHBonePanel(),"BVH(Bone)");
	tab.add(createBVHAnimationPanel(),"BVH(A-Bone)");
	tab.add(createGeometryAnimationPanel(),"Geometry(A-Bone)");
}


public EasyCellTableObjects<AnimationBone> getBvhAnimationObjects() {
	return bvhAnimationObjects;
}


}
