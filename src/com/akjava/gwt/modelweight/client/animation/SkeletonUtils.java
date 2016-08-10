package com.akjava.gwt.modelweight.client.animation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.google.common.collect.Lists;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class SkeletonUtils {

	public static class SkeltonBoneData {
		private String name;
		public SkeltonBoneData(String name, int index) {
			super();
			this.name = name;
			this.index = index;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		private int index;
	}
	
	
	public static List<SkeltonBoneData> skeltonToBoneData(Skeleton skeleton){
		checkNotNull(skeleton,"skeltonToBoneData:need skeleton");
		checkArgument(skeleton.getBones().length()>0,"skeltonToBoneData:need atleast one bone");
		
		
		List<SkeltonBoneData> boneDatas = Lists.newArrayList();
		for(int i=0;i<skeleton.getBones().length();i++){
			boneDatas.add(new SkeltonBoneData(skeleton.getBones().get(i).getName(), i));
		}
		return boneDatas;
	}
	

	public static ValueListBox<SkeltonBoneData> createBoneListBox(){
		ValueListBox<SkeltonBoneData> boneIndexBox = new ValueListBox<SkeltonBoneData>(new Renderer<SkeltonBoneData>() {

			@Override
			public String render(SkeltonBoneData object) {
				if(object==null){
					return null;
				}
				// TODO Auto-generated method stub
				return object.getName();
			}

			@Override
			public void render(SkeltonBoneData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		return boneIndexBox;
	}


}
