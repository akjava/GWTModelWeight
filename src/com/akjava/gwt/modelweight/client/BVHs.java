package com.akjava.gwt.modelweight.client;

import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/*
 * deprecated bvh functions for future comeback
 */
public class BVHs {
	private String bvhUrl="standing2.bvh";
	private void loadBVH(String path){
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(path));

			try {
				builder.sendRequest(null, new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						
						String bvhText=response.getText();
						//LogUtils.log("loaded:"+Benchmark.end("load"));
						//useless spend allmost time with request and spliting.
						parseBVH(bvhText);

					}
					
					
					

@Override
public void onError(Request request, Throwable exception) {
				Window.alert("load faild:");
}
				});
			} catch (RequestException e) {
				LogUtils.log(e.getMessage());
				e.printStackTrace();
			}
	}
	
	private void parseBVH(String bvhText){
		final BVHParser parser=new BVHParser();
		
		parser.parseAsync(bvhText, new ParserListener() {
			
			

			

	
			
			
			
			@Override
			public void onSuccess(BVH bv) {
				LogUtils.log("BVH Loaded:nameAndChannel="+bv.getNameAndChannels().size()+",frame-size="+bv.getFrames());
				
				setBvh(bv);
			}
			
			@Override
			public void onFaild(String message) {
				LogUtils.log(message);
			}
		});
	}

	protected void setBvh(BVH bv) {
		// TODO Auto-generated method stub
		
	}
}
