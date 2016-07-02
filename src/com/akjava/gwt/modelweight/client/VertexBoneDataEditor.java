package com.akjava.gwt.modelweight.client;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.java.ui.experiments.Vector4Editor;
import com.akjava.gwt.three.client.js.THREE;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VertexBoneDataEditor extends Composite implements Editor<VertexBoneData>,ValueAwareEditor<VertexBoneData>{
		private VertexBoneData value;
		private Label vertexIndexLabel;
		private Vector4Editor vector4Editor;
		
		
		private List<ListBox> indexEditors=Lists.newArrayList();
		
		public VertexBoneData getValue() {
			return value;
		}
		
		
		public void setBone(JsArray<AnimationBone> bones){
			for(ListBox listBox:indexEditors){
				listBox.clear();
				for(int i=0;i<bones.length();i++){
					listBox.addItem(bones.get(i).getName());
				}
			}
		}
		
		public VertexBoneDataEditor(){
			VerticalPanel root=new VerticalPanel();
			root.setSpacing(2);
			HorizontalPanel h1=new HorizontalPanel();
			h1.add(new Label("vertexIndex:"));
			vertexIndexLabel=new Label();
			h1.add(vertexIndexLabel);
			root.add(h1);
			
			root.add(new Label("Skinning-Indices"));
			for(int i=0;i<4;i++){
			HorizontalPanel indexPanel=new HorizontalPanel();
			indexPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			indexPanel.add(new Label(String.valueOf(i)));
			ListBox listBox=new ListBox();
			indexPanel.add(listBox);
			root.add(indexPanel);
			indexEditors.add(listBox);
			
			}
			
			root.add(new Label("Skinning-Weights"));
			vector4Editor = new Vector4Editor("", 0, 1, 0.001, 0);
			vector4Editor.setLabelX("0");
			vector4Editor.setLabelY("1");
			vector4Editor.setLabelZ("2");
			vector4Editor.setLabelW("3");
			
			vector4Editor.getResetAllButton().setVisible(false);
			
			vector4Editor.getRangeList().get(0).addtRangeListener(new ValueChangeHandler<Number>() {
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					// TODO Auto-generated method stub
					autoBalance(vector4Editor.getRangeList().get(0),vector4Editor.getRangeList().get(1),vector4Editor.getRangeList().get(2),vector4Editor.getRangeList().get(3));
				}
			});
			vector4Editor.getRangeList().get(1).addtRangeListener(new ValueChangeHandler<Number>() {
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					// TODO Auto-generated method stub
					autoBalance(vector4Editor.getRangeList().get(1),vector4Editor.getRangeList().get(0),vector4Editor.getRangeList().get(2),vector4Editor.getRangeList().get(3));
				}
			});
			vector4Editor.getRangeList().get(2).addtRangeListener(new ValueChangeHandler<Number>() {
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					// TODO Auto-generated method stub
					autoBalance(vector4Editor.getRangeList().get(2),vector4Editor.getRangeList().get(1),vector4Editor.getRangeList().get(0),vector4Editor.getRangeList().get(3));
				}
			});
			vector4Editor.getRangeList().get(3).addtRangeListener(new ValueChangeHandler<Number>() {
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					// TODO Auto-generated method stub
					autoBalance(vector4Editor.getRangeList().get(3),vector4Editor.getRangeList().get(1),vector4Editor.getRangeList().get(2),vector4Editor.getRangeList().get(0));
				}
			});
			root.add(vector4Editor);
			initWidget(root);
			
		}
			protected void autoBalance(LabeledInputRangeWidget2 target, LabeledInputRangeWidget2 remain1,
				LabeledInputRangeWidget2 remain2, LabeledInputRangeWidget2 remain3) {
				double remain=1.0-target.getValue();
				double total=remain1.getValue()+remain2.getValue()+remain3.getValue();
				double ratio1=0;
				double ratio2=0;
				double ratio3=0;
				if(total==0){
					ratio1=1.0/3;
					ratio2=1.0/3;
					ratio3=1.0/3;
				}else{
					ratio1=remain1.getValue()/total;
					
					
					ratio2=remain2.getValue()/total;
					
					
					ratio3=remain3.getValue()/total;
					
				}
				remain1.setValue(remain*ratio1);
				remain2.setValue(remain*ratio2);
				remain3.setValue(remain*ratio3);
				
				//flush();
				for(int i=0;i<4;i++){
					//LogUtils.log("autobalanced:"+i+"="+vector4Editor.getRangeList().get(i).getValue());
					vector4Editor.getValue().gwtSet(i,vector4Editor.getRangeList().get(i).getValue());
				}
		}

			@Override
			public void setDelegate(EditorDelegate<VertexBoneData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				for(int i=0;i<4;i++){
				value.getIndices().gwtSet(i, indexEditors.get(i).getSelectedIndex());
				}
				value.getWeights().copy(vector4Editor.getValue());
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(@Nullable VertexBoneData value) {
				this.value=value;
				if(value==null){
					vertexIndexLabel.setText("");
					
					vector4Editor.setEnabled(false);
					for(int i=0;i<4;i++){
						indexEditors.get(i).setEnabled(false);
					}
					return;
				}else{
					//set enable
					vector4Editor.setEnabled(true);
					vector4Editor.setVisible(true);
					for(int i=0;i<4;i++){
						indexEditors.get(i).setEnabled(true);
					}
				}
				
				for(int i=0;i<4;i++){
					indexEditors.get(i).setSelectedIndex(value.getIndices().gwtGet(i));
				}
				//not set value direct,TODO one time?
				vector4Editor.setValue(THREE.Vector4().copy(value.getWeights()));
				
				vertexIndexLabel.setText(String.valueOf(value.getVertexIndex()));
			}


			public void copyValue(VertexBoneData copyFrom) {
				if(value==null){
					LogUtils.log("selection is null");
					return;
				}
				if(value.getVertexIndex()!=copyFrom.getVertexIndex()){
					LogUtils.log("vertexindex not same");
					return;
				}
				
				value.getIndices().copy(copyFrom.getIndices());
				value.getWeights().copy(copyFrom.getWeights());
				setValue(value);
				
			}
	}