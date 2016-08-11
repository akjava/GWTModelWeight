package com.akjava.gwt.modelweight.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.materials.LineBasicMaterialParameter;
import com.akjava.gwt.three.client.gwt.materials.MeshLambertMaterialParameter;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Line;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JsArray;

public class BoneMeshTools {

	private  int boneJointColor=0x888888;
	private  int boneCoreColor=0x008800;
	
	private double boneCoreSize=0.015;
	private double halfBonecore=0.01;//connect-line
	private double sitesBonecore=0.02;//TODO
	
	
	private Map<String,Integer> nameMap=Maps.newHashMap();
/**
 * 
 * @param bones 	geometry.getBones()
 * @return
 */
	public Group createBoneMeshs(JsArray<AnimationBone> bones){
		Group group=THREE.Group();
		
		
		
		
		//Vec3 rootBoneOffset=bvh.getHiearchy().getOffset();
		//group.getPosition().set(rootBoneOffset.getX(),rootBoneOffset.getY(), rootBoneOffset.getZ());
		
		List<Mesh> tmp=new ArrayList<Mesh>();
		
		Geometry cube=THREE.BoxGeometry(boneCoreSize, boneCoreSize, boneCoreSize);
		
		for(int i=0;i<bones.length();i++){
			AnimationBone bone=bones.get(i);
			
			
			
			Mesh mesh=THREE.Mesh(cube, THREE.MeshLambertMaterial(MeshLambertMaterialParameter.create().color(boneCoreColor)));
			mesh.setName("core:"+bone.getName());
			group.add(mesh);
			
			Vector3 pos=GWTThreeUtils.jsArrayToVector3(bone.getPos());
			
			if(bone.getParent()!=-1){
				
				Vector3 half=pos.clone().multiplyScalar(.5);
				
				
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				pos.add(ppos);
				
				half.add(ppos);
				
				double length=ppos.clone().sub(pos).length();
				
				//half
				Mesh halfMesh=THREE.Mesh(THREE.BoxGeometry(halfBonecore, halfBonecore, length), THREE.MeshLambertMaterial(MeshLambertMaterialParameter.create().color(boneJointColor)));
				group.add(halfMesh);
				halfMesh.setPosition(half);
				halfMesh.lookAt(pos);
				halfMesh.setName("joint:"+bones.get(bone.getParent()).getName());
				
			}
			mesh.setPosition(pos);
			//mesh.setName(bone.getName());
			
			//this mesh is for helping auto-weight
			//TODO support end sites
			/*
			List<Vector3> sites=endSites.get(i);
			for(Vector3 end:sites){
				Mesh endMesh=THREE.Mesh(THREE.BoxGeometry(sitesBonecore, sitesBonecore, sitesBonecore), THREE.MeshLambertMaterial(MeshLambertMaterialParameter.create().color(0x00a00aa)));
				if(end.getX()==0 && end.getY()==0 && end.getZ()==0){
					continue;//ignore 0 
				}else{
					//LogUtils.log(bone.getName()+":"+ThreeLog.get(end));
				}
				Vector3 epos=end.clone().add(pos);
				endMesh.setPosition(epos);
				group.add(GWTGeometryUtils.createLineMesh(pos, epos, 0x888888));
				group.add(endMesh);
				endPointMeshs.add(endMesh);	
			}
			*/
			
			if(bone.getParent()!=-1){
				//AnimationBone parent=bones.get(bone.getParent());
				Vector3 ppos=tmp.get(bone.getParent()).getPosition();
				Line line=THREE.Line(GWTGeometryUtils.createLineGeometry(pos, ppos), THREE.LineBasicMaterial(LineBasicMaterialParameter.create().color(0x888888)));
				line.setName("line:"+bone.getName());
				//group.add(line);
			}
			tmp.add(mesh);
		}
		return group;
	}
}
