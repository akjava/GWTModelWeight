package com.akjava.gwt.modelweight.client.animation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.google.common.collect.Lists;

public class SkeletonUtils {

	public static class BoneData {
		private String name;
		public BoneData(String name, int index) {
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
	
	
	public static List<BoneData> skeltonToBoneData(Skeleton skeleton){
		checkNotNull(skeleton,"skeltonToBoneData:need skeleton");
		checkArgument(skeleton.getBones().length()>0,"skeltonToBoneData:need atleast one bone");
		
		
		List<BoneData> boneDatas = Lists.newArrayList();
		for(int i=0;i<skeleton.getBones().length();i++){
			boneDatas.add(new BoneData(skeleton.getBones().get(i).getName(), i));
		}
		return boneDatas;
	}
	
	
}
