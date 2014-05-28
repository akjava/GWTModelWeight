package com.akjava.gwt.modelweight.client;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.akjava.gwt.three.client.gwt.animation.NameAndVector3;
import com.akjava.gwt.three.client.java.animation.WeightBuilder;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;

public class MakehumanWeightBuilder extends WeightBuilder {

	public static final int MODE_MAKEHUMAN_SECOND_LIFE19=100;	//special for makehuman model
	
	public static void autoWeight(Geometry geometry,JsArray<AnimationBone> bones,List<List<Vector3>> endSites,int mode,JsArray<Vector4> bodyIndices,JsArray<Vector4> bodyWeight){
		LogUtils.log("makehuman-auto-weight:");
		List<NameAndVector3> nameAndPositions=boneToNameAndPosition(bones,endSites);
		for(int i=0;i<geometry.vertices().length();i++){
			Vector4 ret=null;
			if(mode==MODE_MAKEHUMAN_SECOND_LIFE19){
				ret=makehuman(nameAndPositions, bones, geometry, i);
			}else{
				Window.alert("null mode");
			}
			
			//now support only 2 bones
			Vector4 v4=THREE.Vector4();
			v4.set(ret.getX(), ret.getY(), 0, 0);
			bodyIndices.push(v4);
			
			Vector4 v4w=THREE.Vector4();
			v4w.set(ret.getZ(), ret.getW(), 0, 0);
			bodyWeight.push(v4w);
			}
		}
	

/**
//sample layout 
0,hip,OFFSET 0 12.5051 0.138,0
1,lThigh,OFFSET 1.3715302093343145 -0.22580068856896668 -0.1385497230997023,1.3715302093343145
2,lShin,OFFSET -0.2208247879140235 -5.811115694208872 0.10780347726606987,-0.2208247879140235
3,lFoot,OFFSET -0.08076486482253914 -5.581016269535659 -0.4351708072400138,-0.08076486482253914
4,rThigh,OFFSET -1.3715302092785857 -0.22580068987037877 -0.13854972153040235,-1.3715302092785857
5,rShin,OFFSET 0.27114512957005793 -5.801512949115939 0.31379532200961524,0.27114512957005793
6,rFoot,OFFSET 0.12846022108752797 -5.592060893158316 -0.2367992334759048,0.12846022108752797
7,abdomen,OFFSET -9.313230841302399e-10 1.5998028305167598 -0.30120205912442544,-9.313230841302399e-10
8,chest,OFFSET 1.8626476191980043e-9 0.6278005070008543 -0.12785091230442436,1.8626476191980043e-9
9,rCollar,OFFSET -0.29992517036703975 3.850099827871939 1.0205467004843214,-0.29992517036703975
10,rShldr,OFFSET -2.211221057691872 0.030659981394585423 -0.6015124130731888,-2.211221057691872
11,rForeArm,OFFSET -3.20795281179909 -0.34386782131067717 -0.5581125351286402,-3.20795281179909
12,rHand,OFFSET -3.433209935558393 -0.012178579191600702 -0.22147211132800648,-3.433209935558393
13,lCollar,OFFSET 0.29992516912446904 3.8500998279992564 1.020546700369181,0.29992516912446904
14,lShldr,OFFSET 2.211221057447937 0.030659982333242505 -0.6015124139220721,2.211221057447937
15,lForeArm,OFFSET 3.2079604900196124 -0.343866998582144 -0.558076564505493,3.2079604900196124
16,lHand,OFFSET 3.4332152200114097 -0.01212442457304852 -0.22144427609355943,3.4332152200114097
17,neck,OFFSET -9.313469227184514e-10 4.827601841811972 0.4860947898657715,-9.313469227184514e-10
18,head,OFFSET -8.592426132893153e-15 1.3965022325531948 -0.08505226400119725,-8.592426132893153e-15
 
 * @param nameAndPositions
 * @param bones
 * @param geometry
 * @param index
 * @return
 */
  public static Vector4 makehuman(List<NameAndVector3> nameAndPositions,JsArray<AnimationBone> bones,Geometry geometry,int index){
	  Vector4 v4=THREE.Vector4();
		
		v4.setX(geometry.getSkinIndices().get(index).getX());
		v4.setY(geometry.getSkinIndices().get(index).getY());
		
		v4.setZ(geometry.getSkinWeight().get(index).getX());
		v4.setW(geometry.getSkinWeight().get(index).getY());
		
		
		boolean firstIsEmpty=false;
	    boolean isEmpty=false;
	    
	   
		
		if(v4.getX()==0 && v4.getY()==0){//both is root
			
			if(v4.getW()==1 || v4.getZ()==1){//totally root;
				return v4;
			}
			if(v4.getZ()==0){
				firstIsEmpty=true;
				isEmpty=true;
			}else if(v4.getW()==0){
				isEmpty=true;
			}
			
			
			if(isEmpty){
				//rThigh or lThigh;
				double centerX=bones.get(0).getPos().get(0);
				
				
				
				double centerY=bones.get(0).getPos().get(1);//+bones.get(7).getPos().get(1)/2;//half-y
				double atX=geometry.getVertices().get(index).getX();
				double atY=geometry.getVertices().get(index).getY();
				
				if(atY<centerY){
				if(atX<centerX){
					
					if(firstIsEmpty){
						v4.setX(4);//lThigh
						v4.setZ(1-v4.getW());
					}else{
						v4.setY(4);//lThigh
						v4.setW(1-v4.getZ());
					}
					
				}else if(atX>centerX){
					
					if(firstIsEmpty){
						v4.setX(1);//rThigh
						v4.setZ(1-v4.getW());
					}else{
						v4.setY(1);//rThigh
						v4.setW(1-v4.getZ());
					}
				}
				}else{
					double v;
					if(firstIsEmpty){
						v=(1-v4.getW());
					}else{
						v=(1-v4.getZ());
					}
					
					if(v<0.2){//small is usually legs,TODO check by position
						if(atX<centerX){
							
							if(firstIsEmpty){
								v4.setX(4);//lThigh
								v4.setZ(1-v4.getW());
							}else{
								v4.setY(4);//lThigh
								v4.setW(1-v4.getZ());
							}
							
						}else if(atX>centerX){
							
							if(firstIsEmpty){
								v4.setX(1);//rThigh
								v4.setZ(1-v4.getW());
							}else{
								v4.setY(1);//rThigh
								v4.setW(1-v4.getZ());
							}
						}
					}else{
						if(firstIsEmpty){
							v4.setX(7);
						}else{
							v4.setY(7);
						}
					}
					
					
					
					if(firstIsEmpty){
						v4.setZ(1-v4.getW());
					}else{
						v4.setW(1-v4.getZ());
					}
					
				}
			}
			
			
		}else{
			
			
			if(v4.getX()==0 && v4.getZ()==0 && v4.getW()!=1){
				firstIsEmpty=true;
				isEmpty=true;
			}
			else if(v4.getY()==0 && v4.getW()==0 && v4.getZ()!=1){
				isEmpty=true;
			}
			
			if(isEmpty){//fix arms
				int target;
				if(firstIsEmpty){
					target=(int) v4.getY();
				}else{
					target=(int) v4.getX();
				}
				
				int second=findClosedBone(nameAndPositions, geometry.getVertices().get(index), target);
				//LogUtils.log(target+",second="+second);
				
				
				
				if(firstIsEmpty){
					v4.setX(second);
					v4.setZ(1-v4.getW());
				}else{
					v4.setY(second);
					v4.setW(1-v4.getZ());
				}
				
				//horrible rThigh & lThigh case
				if(target==1 && second==4){
					if(firstIsEmpty){
						v4.setX(0);
					}else{
						v4.setY(0);
					}
				}else if(target==4 && second==1){
					if(firstIsEmpty){
						v4.setX(0);
					}else{
						v4.setY(0);
					}
				}
				
			}
			
			//not allow chest-sholder
			if(v4.getX()==8 && v4.getY()==10){//chest-rsholder to rcolor-rsholder
				v4.setX(9);
			}
			else if(v4.getX()==8 && v4.getY()==14){//chest-lsholder to lcolor-lsholder
				v4.setX(13);
			}else if(v4.getY()==8 && v4.getX()==10){//chest-rsholder to rcolor-rsholder
				v4.setY(9);
			}
			else if(v4.getY()==8 && v4.getX()==14){//chest-lsholder to lcolor-lsholder
				v4.setY(13);
			}
			
			//now allow chest-neck
			//not good at check to color
			/*
			if(v4.getX()==8 && v4.getY()==17){//chest-rsholder to rcolor-rsholder
				double centerX=bones.get(0).getPos().get(0);
				double atX=geometry.getVertices().get(index).getX();
				
				if(atX<centerX){
					v4.setX(9);
					
				}else if(atX>centerX){
					v4.setX(13);
					
				}
				
				
			}else if(v4.getY()==8 && v4.getX()==17){//neck
				double centerX=bones.get(0).getPos().get(0);
				double atX=geometry.getVertices().get(index).getX();
				
				if(atX<centerX){
					v4.setY(9);
					
				}else if(atX>centerX){
					v4.setY(13);
					
				}
			}
			*/
			
		}
		
		if(!isConnected(v4, bones)){
			String debug=bones.get((int)v4.getX()).getName()+" ="+v4.getZ()+","+bones.get((int)v4.getY()).getName()+" ="+v4.getW();
			//LogUtils.log(index+","+debug);
			
			if(isHasIndex(v4, 7)){//abdeon
				int another=getAnother(v4, 7);
				replaceIndex(v4, another, 0);//hip
			}if(isHasIndex(v4, 18)){//head
				int another=getAnother(v4, 18);
				replaceIndex(v4, another, 17);//neck
			}
			
		}
		
		return v4;
  }
}
