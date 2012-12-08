package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.three.client.core.Vector4;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class IndexAndWeightEditor extends VerticalPanel{


	private ListBox index1;
	private ListBox index2;
	private HTML5InputRange balance;
	private Label nameLabel;
	private int ind=-1;
	public IndexAndWeightEditor(){
		super();
		nameLabel = new Label();
		add(nameLabel);
		
		add(new Label("Index1"));
		index1 = new ListBox();
		add(index1);
		
		add(new Label("Index2"));
		index2 = new ListBox();
		add(index2);
		
		balance = new HTML5InputRange();
		balance.setMin(0);
		balance.setMax(100);
		balance.setValue(50);
		
		add(balance);
		
		add(createRangeLabel("",balance));
	}
	public void setBones(JsArray<AnimationBone> bones){
		index1.clear();
		for(int i=0;i<bones.length();i++){
			index1.addItem(bones.get(i).getName());
		}
		index2.clear();
		for(int i=0;i<bones.length();i++){
			index2.addItem(bones.get(i).getName());
		}
	}
	public void setAvailable(boolean bool){
		index1.setEnabled(bool);
		index2.setEnabled(bool);
		balance.setEnabled(bool);
	}
	public void setValue(int ind,Vector4 index,Vector4 weight){
		nameLabel.setText("vertex-Number:"+ind);
		this.ind=ind;
		index1.setSelectedIndex((int) index.getX());
		index2.setSelectedIndex((int) index.getY());
		int v=(int) (100-weight.getX()*100);
		balance.setValue(v);
	}
	
	public int getArrayIndex(){
		return ind;
	}
	public int getIndex1(){
		return index1.getSelectedIndex();
	}
	public int getIndex2(){
		return index2.getSelectedIndex();
	}
	public double getWeight1(){
		double v=(100-balance.getValue());
		return v/100;
	}
	public double getWeight2(){
		return 1.0-getWeight1();
	}
	
	public static Label createRangeLabel(final String text,final HTML5InputRange range){
		final Label label=new Label();
		label.setText("");
		label.setStylePrimaryName("title");
		range.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				double index1=(100.0-newValue)/100;
				double index2=1.0-index1;
				
				String text1=(""+index1).substring(0,4);
				String text2=(""+index2).substring(0,4);
				label.setText(""+text1+":"+text2);
			}
		});
		
		return label;
	}
}
