package com.akjava.gwt.modelweight.client;

import java.io.Flushable;
import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;

/**
 * watch out !
 * 3.1 model loader(or exporter) contain quad.but inside no more use quad ,these converted to face3.
 * so once merged.it's totally differece data.can't compare original jsmodel
 * @author aki
 *
 */
public class ModelCompare {

	//modofied when merge
	public List<String> compareCore(JSONModelFile modelA,JSONModelFile modelB){
		List<String> difference=new ArrayList<String>();
		
		//geomtetryuvs
		difference.add(compare("uvs",modelA.getUvs(),modelB.getUvs()));
		
		//vertices
		difference.add(compare("vertices",modelA.getVertices(),modelB.getVertices()));
		//normal
		difference.add(compare("normal",modelA.getNormals(),modelB.getNormals()));
		//faces
		difference.add(compare("faces",modelA.getFaces(),modelB.getFaces()));
		
		difference.add(compare("skinIndices",modelA.getSkinIndices(),modelB.getSkinIndices()));
		
		difference.add(compare("skinWeight",modelA.getSkinWeights(),modelB.getSkinWeights()));
		
		//TODO support color
		
		return FluentIterable.from(difference).filter(Predicates.notNull()).toList();
	}
	
	public String compare(String label,JsArray<JsArrayNumber> objectA,JsArray<JsArrayNumber> objectB){
		String result="";
		if(objectA==null){
			if(objectB==null){
				result="";//no need check
			}else{
				result= "A is null,B's length is "+objectB.length();
			}
		}
		else if(objectB==null){
			result= "A is "+objectA.length()+",but B is Empty";
		}else{
			if(objectA.length()!=objectB.length()){
				return "A's length "+objectA.length()+",B's length is "+objectB.length();
			}
			
			
			for(int i=0;i<objectA.length();i++){
				JsArrayNumber numberA=objectA.get(i);
				JsArrayNumber numberB=objectB.get(i);
				String numberResult=compareJsArrayNumber(numberA,numberB);	
				if(!numberResult.isEmpty()){
					result="difference array at:"+i;
					break;
				}
			}
			
		}
		
	
		if(!result.isEmpty()){
			return label+":"+result;
		}
		return null;//return null for predicate
	}
	
	
	public String compare(String label,JsArrayNumber objectA,JsArrayNumber objectB){
		String result=compareJsArrayNumber(objectA,objectB);
		if(!result.isEmpty()){
			return label+":"+result;
		}
		return null;
	}
	public String compareJsArrayNumber(JsArrayNumber objectA,JsArrayNumber objectB){
		if(objectA==null){
			if(objectB==null){
				return "";//no need check
			}else{
				return "A is null,B's length is "+objectB.length();
			}
		}
		if(objectB==null){
			return "A is"+objectA.length()+",but B is Empty";
		}
		
		if(objectA.length()!=objectB.length()){
			return "A's length"+objectA.length()+",B's length is "+objectB.length();
		}
		
		for(int i=0;i<objectA.length();i++){
			if(objectA.get(i)!=objectB.get(i)){
				return "difference value at:"+i;
			}
		}
		
		return "";
		}
}
