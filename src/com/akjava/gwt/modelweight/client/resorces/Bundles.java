package com.akjava.gwt.modelweight.client.resorces;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Bundles extends ClientBundle {
public static Bundles INSTANCE=GWT.create(Bundles.class);
	TextResource weight();
	TextResource vertex();
}