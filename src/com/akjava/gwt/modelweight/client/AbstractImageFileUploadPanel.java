package com.akjava.gwt.modelweight.client;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.FileUtils.ImageFileListener;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AbstractImageFileUploadPanel extends VerticalPanel{

	private Label fileNameLabel;
	public Label getFileNameLabel() {
		return fileNameLabel;
	}

	public void setFileNameLabel(Label fileNameLabel) {
		this.fileNameLabel = fileNameLabel;
	}

	public FileUploadForm getUploadForm() {
		return uploadForm;
	}

	public void setUploadForm(FileUploadForm uploadForm) {
		this.uploadForm = uploadForm;
	}


	private FileUploadForm uploadForm;

	private ImageElement lastUploadImage;


	public ImageElement getLastUploadImage() {
		return lastUploadImage;
	}
	private Label titleLabel;
	public AbstractImageFileUploadPanel(String labelName,boolean hasReset){
		this.setSpacing(1);
		this.setWidth("100%");
		HorizontalPanel filePanel=new HorizontalPanel();
		filePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		filePanel.setWidth("100%");
		
		this.add(filePanel);
		titleLabel = new Label(labelName);
		titleLabel.setWidth("80px");
		
		filePanel.add(titleLabel);
		fileNameLabel = new Label();
		fileNameLabel.setWidth("100%");
		filePanel.add(fileNameLabel);
		
		HorizontalPanel h1=new HorizontalPanel();
		h1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		this.add(h1);
		uploadForm = FileUtils.createImageFileUploadForm(new ImageFileListener() {
			
			@Override
			public void uploaded(File file, ImageElement imageElement) {
				
				uploadImage(file.getFileName(),imageElement);
			}
		}, true,true);
		
		//hide annyoing "unchoosed"
		uploadForm.getFileUpload().getElement().getStyle().setBackgroundColor("#e8e8e8");
		uploadForm.getFileUpload().getElement().getStyle().setColor("#e8e8e8");
		uploadForm.setWidth("100%");
		h1.add(uploadForm);
		if(hasReset){
		h1.add(new Button("Reset",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				uploadImage("",null);
			}
		}));
		}
		
		
	}
	public void reload(){
		onImageFileUpload(lastUploadImage);
	}

	public void uploadImage(String fileName,ImageElement imageElement){
		lastUploadImage=imageElement;
		fileNameLabel.setText(fileName);
		onImageFileUpload(imageElement);
	}
	
	
	abstract protected void onImageFileUpload(@Nullable ImageElement imageElement);

	public boolean isUploaded() {
		return lastUploadImage!=null;
	}
	}
	
	

