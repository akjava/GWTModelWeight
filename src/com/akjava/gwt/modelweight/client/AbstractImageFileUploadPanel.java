package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.ImageFileListener;
import com.google.gwt.dom.client.ImageElement;
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

	public AbstractImageFileUploadPanel(){
		HorizontalPanel filePanel=new HorizontalPanel();
		filePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		
		this.add(filePanel);
		fileNameLabel = new Label();
		fileNameLabel.setWidth("140px");
		filePanel.add(fileNameLabel);
		uploadForm = FileUtils.createImageFileUploadForm(new ImageFileListener() {
			
			@Override
			public void uploaded(File file, ImageElement imageElement) {
				lastUploadImage=imageElement;
				uploadImage(file.getFileName(),imageElement);
			}
		}, true, true);
		
		
		filePanel.add(uploadForm);
	}
	public void reload(){
		onImageFileUpload(lastUploadImage);
	}

	public void uploadImage(String fileName,ImageElement imageElement){
		fileNameLabel.setText(fileName);
		onImageFileUpload(imageElement);
	}
	
	
	abstract protected void onImageFileUpload(ImageElement imageElement);

	public boolean isUploaded() {
		return lastUploadImage!=null;
	}
	}
	
	

