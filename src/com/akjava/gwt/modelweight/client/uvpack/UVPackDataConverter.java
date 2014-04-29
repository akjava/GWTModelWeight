package com.akjava.gwt.modelweight.client.uvpack;

import java.util.ArrayList;
import java.util.List;

import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class UVPackDataConverter extends Converter<List<UVPackData>,String> {

	@Override
	protected String doForward(List<UVPackData> a) {
		Iterable<String> values=Iterables.transform(a, Functions.toStringFunction());
		return Joiner.on("\n").join(values);
	}

	@Override
	protected List<UVPackData> doBackward(String b) {
		List<UVPackData> datas=new ArrayList<UVPackData>();
		String[] lines=b.split("\n");
		for(String line:lines){
			if(line.isEmpty()){
				continue;
			}
			UVPackData data=new UVPackData();
			String[] csv=line.split(",");
			if(csv.length>0){
				data.setSplit(ValuesUtils.toInt(csv[0], 2));
			}
			if(csv.length>1){
				data.setX(ValuesUtils.toInt(csv[1], 0));
			}
			if(csv.length>2){
				data.setY(ValuesUtils.toInt(csv[2], 0));
			}
			if(csv.length>3){
				data.setComment(csv[3]);
			}
			datas.add(data);
		}
		return datas;
	}

}
