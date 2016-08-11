package com.akjava.gwt.modelweight.client.animation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class GeometryBoneUtils {

	public static class GeometryBoneData {
		private String name;
		public GeometryBoneData(String name, int index) {
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
	
	
	public static List<GeometryBoneData> bonesToBoneDataList(JsArray<AnimationBone> bones){
		checkNotNull(bones,"skeltonToBoneData:need skeleton");
		checkArgument(bones.length()>0,"skeltonToBoneData:need atleast one bone");
		
		
		List<GeometryBoneData> boneDatas = Lists.newArrayList();
		for(int i=0;i<bones.length();i++){
			boneDatas.add(new GeometryBoneData(bones.get(i).getName(), i));
		}
		return boneDatas;
	}
	

	public static ValueListBox<GeometryBoneData> createBoneListBox(){
		ValueListBox<GeometryBoneData> boneIndexBox = new ValueListBox<GeometryBoneData>(new Renderer<GeometryBoneData>() {

			@Override
			public String render(GeometryBoneData object) {
				if(object==null){
					return null;
				}
				// TODO Auto-generated method stub
				return object.getName();
			}

			@Override
			public void render(GeometryBoneData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		return boneIndexBox;
	}


	public static boolean isHasBone(Geometry baseGeometry) {
		return baseGeometry.getBones()!=null && baseGeometry.getBones().length()>0;
	}
	
	public static List<Vector3> convertAbsolutePosition(JsArray<AnimationBone> bones){
		List<Vector3> bonePositions=Lists.newArrayList();
		for(int j=0;j<bones.length();j++){//TODO make a function
			AnimationBone ab=bones.get(j);
			Vector3 bonePos=THREE.Vector3().fromArray(ab.getPos());
			bonePositions.add(bonePos);
		}
		return convertAbsolutePosition(bonePositions, boneToPath(bones));
	}

	//no care about rotate matrix
	private static List<Vector3> convertAbsolutePosition(List<Vector3> positions,List<List<Integer>> paths){
		List<Vector3> absolutePositions=Lists.newArrayList();
		checkArgument(positions.size()==paths.size(),"sumPosition:must be same");
		
		for(int i=0;i<paths.size();i++){
			Vector3 pos=THREE.Vector3();
			List<Integer> path=paths.get(i);
			for(int index:path){
				pos.add(positions.get(index));
			}
			absolutePositions.add(pos);
		}
		
		
		return absolutePositions;
	}
	
	public static  List<List<Integer>> boneToPath(JsArray<AnimationBone> bones){
		List<List<Integer>> data=new ArrayList<List<Integer>>();
		for(int i=0;i<bones.length();i++){
			List<Integer> path=new ArrayList<Integer>();
			AnimationBone bone=bones.get(i);
			path.add(i);
			data.add(path);
			while(bone.getParent()!=-1){
				//path.add(bone.getParent());
				path.add(0,bone.getParent());
				bone=bones.get(bone.getParent());
			}
		}
		return data;
	}
	
}
