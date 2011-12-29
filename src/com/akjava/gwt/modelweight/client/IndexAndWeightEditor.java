package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.html5.client.HTML5InputRange;
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
		
		index1 = new ListBox();
		
		add(index1);
		index2 = new ListBox();
		
		add(index2);
		balance = new HTML5InputRange(0,100,50);
		add(balance);
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
	
	public void setValue(int ind,Vector4 index,Vector4 weight){
		nameLabel.setText(""+ind);
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
}
