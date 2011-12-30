package com.akjava.gwt.modelweight.client.weight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.akjava.gwt.lib.client.LineSplitter;
import com.akjava.gwt.three.client.gwt.collada.WeightData;
import com.google.gwt.core.client.JsArray;

public class WeighDataParser {

	public List<List<GWTWeightData>> convert(JsArray<JsArray<WeightData>> wdata){
		List<List<GWTWeightData>> result=new ArrayList<List<GWTWeightData>>();
		for(int i=0;i<wdata.length();i++){
			JsArray<WeightData> jdata=wdata.get(i);
			List<GWTWeightData> data=new ArrayList<GWTWeightData>();
			
			for(int j=0;j<jdata.length();j++){
				WeightData gw=jdata.get(j);
				if(gw.getJoint()!=-1){
					data.add(new GWTWeightData(gw));
				}
			}
			result.add(data);
		}
		return result;
	}
	public List<List<GWTWeightData>> parse(String text){
		List<List<GWTWeightData>> result=new ArrayList<List<GWTWeightData>>();
		List<String> lines=LineSplitter.splitLines(text);
		
		for(String line:lines){
			if(line.isEmpty()){
				continue;
			}
			
			List<GWTWeightData> data=new ArrayList<GWTWeightData>();
			String[] csv=line.split(",");
			for(int i=0;i<csv.length;i++){
				String id_w[]=csv[i].split(":");
				if(id_w.length==2){
					GWTWeightData wd=new GWTWeightData();
					wd.setBoneIndex(Integer.parseInt(id_w[0]));
					wd.setWeight(Double.parseDouble(id_w[1]));
					if(wd.getBoneIndex()!=-1){
						data.add(wd);
					}
				}
			}
			
			if(data.size()>0){
				Collections.sort(data);
				result.add(data);
			}
			
		}
		
		
		return result;
	}
}
