package com.akjava.gwt.modelweight.client;

import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.js.cameras.Camera;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;

public class BoneMeshMouseSelector extends Object3DMouseSelecter{
	private Map<String,Integer> boneMap=Maps.newHashMap();
	public BoneMeshMouseSelector(Group group,JsArray<AnimationBone> bones,WebGLRenderer renderer, Camera camera) {
		super(renderer, camera);
		this.group=group;
		for(int i=0;i<bones.length();i++){
			boneMap.put(bones.get(i).getName(), i);
		}
	}

	
	public int getBoneIndex(AnimationBone bone){
		Integer result=boneMap.get(bone.getName());
		if(result==null){
			return -1;
		}else{
			return result;
		}
	}

	private Group group;
	
	public int pickBone(ClickEvent event){
		return pickBone(event.getX(),event.getY());
	}
	public int pickBone(int mx,int my){
		int result=-1;
		
		JsArray<Intersect> intersects=pickIntersects(mx,my,group.getChildren());
		if(intersects!=null && intersects.length()>0){
			Object3D object=intersects.get(0).getObject();
			String[] name=object.getName().split(":");
			if(name.length>1){
				String boneName=name[1];
				Integer boneIndex=boneMap.get(boneName);
				if(boneIndex==null){
					LogUtils.log("invalid bone not exist:"+boneName);
				}else{
					
					updateSelectionColor(boneName);
					
					return boneIndex;
				}
			}else{
				LogUtils.log("invalid selection bone name:"+object.getName());
			}
		}
		
		//TODO update selected bone-mesh color
		updateSelectionColor(null);
		
		return result;
	}

	private int coreSelectedColor=0xee88ee;
	private int coreUnSelectedColor=0x008800;
	private int jointSelectedColor=0xeeddee;
	private int jointUnSelectedColor=0x888888;
	
	private void updateSelectionColor(@Nullable String boneName) {
		for(int i=0;i<group.getChildren().length();i++){
			Object3D object=group.getChildren().get(i);
			if(!object.getType().equals("Mesh")){
				LogUtils.log("updateSelectionColor:only check mesh:"+object.getType());
				continue;
			}
			Mesh mesh=object.cast();
			String name=object.getName();
			if(name!=null){
				String[] type_name=object.getName().split(":");
				if(type_name.length!=2){
					LogUtils.log("updateSelectionColor:something wrong name contain:"+name);
					continue;
				}
				if(type_name[0].equals("core")){
					if(Objects.equal(boneName, type_name[1])){
						mesh.getMaterial().gwtCastMeshPhongMaterial().getColor().setHex(coreSelectedColor);
					}else{
						mesh.getMaterial().gwtCastMeshPhongMaterial().getColor().setHex(coreUnSelectedColor);
					}
				}else{//joint
					if(Objects.equal(boneName, type_name[1])){
						mesh.getMaterial().gwtCastMeshPhongMaterial().getColor().setHex(jointSelectedColor);
					}else{
						mesh.getMaterial().gwtCastMeshPhongMaterial().getColor().setHex(jointUnSelectedColor);
					}
				}
			}
		}
	}
	
	
}
