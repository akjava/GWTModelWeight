package com.akjava.gwt.modelweight.client.uvpack;

import java.io.IOException;
import java.util.List;

import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class ToStringValueListBox<T> extends ValueListBox<T> {

	public ToStringValueListBox() {
		this(null,null);
	}
	public ToStringValueListBox(List<T> accepts,T value) {
		super(new Renderer<T>() {

			@Override
			public String render(T object) {
				// TODO Auto-generated method stub
				if(object==null){
					return null;
				}
				return String.valueOf(object);
			}

			@Override
			public void render(T object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		this.setValue(value);
		if(accepts!=null){
		this.setAcceptableValues(accepts);
		}
		
	}

}
