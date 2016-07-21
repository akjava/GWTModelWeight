package com.akjava.gwt.modelweight.client.animation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.modelweight.client.animation.SkeletonUtils.BoneData;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.KeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.QuaternionKeyframeTrack;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QuickBoneAnimationWidget extends Composite implements HasValueChangeHandlers<AnimationClip>{

	private ValueListBox<BoneData> boneIndexBox;
	private int boneIndex;
	private String name;
	private CheckBox bothSideCheck;
	private LabeledInputRangeWidget2 xRange;
	private LabeledInputRangeWidget2 yRange;
	private LabeledInputRangeWidget2 zRange;
	public int getBoneIndex() {
		return boneIndex;
	}




	public QuickBoneAnimationWidget(String name){
		this.name=name;
		VerticalPanel root=new VerticalPanel();
		
		HorizontalPanel animationPanel=new HorizontalPanel();
		animationPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		root.add(animationPanel);
		animationPanel.add(new Label("Quick Animation-"));
		
		boneIndexBox = new ValueListBox<BoneData>(new Renderer<BoneData>() {

			@Override
			public String render(BoneData object) {
				if(object==null){
					return null;
				}
				// TODO Auto-generated method stub
				return object.getName();
			}

			@Override
			public void render(BoneData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		boneIndexBox.addValueChangeHandler(new ValueChangeHandler<BoneData>() {


			@Override
			public void onValueChange(ValueChangeEvent<BoneData> event) {
				boneIndex=event.getValue().getIndex();
				updateAnimation();
			}
		});
		animationPanel.add(new Label("TargetBone:"));
		animationPanel.add(boneIndexBox);
		
		HorizontalPanel h=new HorizontalPanel();
		h.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		bothSideCheck = new CheckBox("both side");
		h.add(bothSideCheck);
		h.add(new Button("Reset",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				resetAnimation();
			}
		}));
		
		xRange = new LabeledInputRangeWidget2("X", -180, 180, 1);
		xRange.setButtonVisible(true);
		root.add(xRange);
		RangeButtons xButtons=new RangeButtons(xRange, Lists.newArrayList(0.0,45.0,90.0,135.0,180.0));
		root.add(xButtons);
		xRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				updateAnimation();
			}
		});
		
		yRange = new LabeledInputRangeWidget2("Y", -180, 180, 1);
		yRange.setButtonVisible(true);
		root.add(yRange);
		RangeButtons yButtons=new RangeButtons(yRange, Lists.newArrayList(0.0,45.0,90.0,135.0,180.0));
		root.add(yButtons);
		yRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				updateAnimation();
			}
		});
		
		zRange = new LabeledInputRangeWidget2("Z", -180, 180, 1);
		zRange.setButtonVisible(true);
		root.add(zRange);
		RangeButtons zButtons=new RangeButtons(zRange, Lists.newArrayList(0.0,45.0,90.0,135.0,180.0));
		root.add(zButtons);
		zRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				updateAnimation();
			}
		});
		
		initWidget(root);
	}
	

	protected void resetAnimation() {
		xRange.setValue(0);
		yRange.setValue(0);
		zRange.setValue(0);
		fire(null);
		//updateAnimation();
	}




	protected void updateAnimation() {
		AnimationClip clip=createAnimation(boneIndex, Math.toRadians(xRange.getValue()), Math.toRadians(yRange.getValue()), Math.toRadians(zRange.getValue()), bothSideCheck.getValue());
		fire(clip);
	}


	public static class RangeButtons extends HorizontalPanel{
		private LabeledInputRangeWidget2 range;
		private boolean increment;
		public RangeButtons(final LabeledInputRangeWidget2 range,List<Double> values) {
			super();
			this.setWidth("100%");
			this.setHorizontalAlignment(ALIGN_CENTER);
			this.range = range;
			
			
			for(int i=values.size()-1;i>=0;i--){
				final Double v=values.get(i);
				if(v==0){
					continue;
				}
				Button bt=new Button("-"+String.valueOf(v),new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						double value=range.getValue();
						if(increment){
						value-=value;	
						}else{
						value=-v;
						}
						if(value<range.getRange().getMin()){
							value=range.getRange().getMin();
						}
						range.setValue(value,true);
					}
				});
				this.add(bt);
			}
			for(final Double v:values){
				Button bt=new Button(String.valueOf(v),new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						double value=range.getValue();
						if(increment){
							value+=value;	
							}else{
							value=v;
							}
						if(value>range.getRange().getMax()){
							value=range.getRange().getMax();
						}
						range.setValue(value,true);
					}
				});
				this.add(bt);
			}
			
		}
		
	}

	public void setSkelton(Skeleton skeleton){
		checkNotNull(skeleton,"need skelton");
		List<BoneData> boneDatas=SkeletonUtils.skeltonToBoneData(skeleton);
		if(boneDatas==null){
			LogUtils.log("somehow skelton convert faild");
			return;
		}
		boneIndexBox.setValue(boneDatas.get(0));
		boneIndexBox.setAcceptableValues(boneDatas);
		resetAnimation();
	}

	/*
	 * must reset pose before play,this animation only contain target angle
	 */
	public AnimationClip createAnimation(int boneIndex,double x,double y,double z,boolean both){
		
		Quaternion q=THREE.Quaternion();
		
		Quaternion xq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(1, 0, 0), x);
		q.multiply(xq);
		
		Quaternion yq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 1, 0), y);
		q.multiply(yq);
		
		Quaternion zq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 0, 1), z);
		q.multiply(zq);
		
		Quaternion q2=THREE.Quaternion();
		
		Quaternion xq2=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(1, 0, 0), x*-1);
		q2.multiply(xq2);
		
		Quaternion yq2=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 1, 0), y*-1);
		q2.multiply(yq2);
		
		Quaternion zq2=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 0, 1), z*-1);
		q2.multiply(zq2);
		
		double duration=1.0;
		
		JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
		
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		times.push(0);
		times.push(duration);
		times.push(duration*2);
		if(both){
		times.push(duration*3);
		times.push(duration*4);
		}
		JsArrayNumber values=JsArray.createArray().cast();
		
		
		
		
		
		JavaScriptUtils.concat(values,THREE.Quaternion().toArray());
		JavaScriptUtils.concat(values,q.toArray());
		JavaScriptUtils.concat(values,THREE.Quaternion().toArray());
		if(both){
			JavaScriptUtils.concat(values,q2.toArray());
			JavaScriptUtils.concat(values,THREE.Quaternion().toArray());
		}
		
		//LogUtils.log(values);
		
		//value is not valid number
		
		//head fixed
		//quaternion is alias for rot
		QuaternionKeyframeTrack track=THREE.QuaternionKeyframeTrack(".bones["+boneIndex+"].quaternion", times, values);
		
		tracks.push(track);
		
		AnimationClip clip=THREE.AnimationClip(name, -1, tracks);
		//LogUtils.log(track.validate());
		
		return clip;
	}


	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AnimationClip> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	private void fire(AnimationClip clip){
		ValueChangeEvent.fire(this, clip);
	}
}
