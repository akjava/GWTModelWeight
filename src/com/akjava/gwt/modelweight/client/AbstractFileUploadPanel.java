package com.akjava.gwt.modelweight.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AbstractFileUploadPanel extends VerticalPanel{

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

	private String lastUploadText;
	public String getLastUploadText() {
		return lastUploadText;
	}

	public AbstractFileUploadPanel(){
		HorizontalPanel filePanel=new HorizontalPanel();
		filePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		
		this.add(filePanel);
		fileNameLabel = new Label();
		fileNameLabel.setWidth("140px");
		filePanel.add(fileNameLabel);
		uploadForm = FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				lastUploadText=text;
				uploadText(file.getFileName(),text);
			}
		}, true);
		filePanel.add(uploadForm);
	}
	public void reload(){
		onTextFileUpload(lastUploadText);
	}

	public void uploadText(String fileName,String text){
		fileNameLabel.setText(fileName);
		onTextFileUpload(text);
	}
	
	
	abstract protected void onTextFileUpload(String text);

	public boolean isUploaded() {
		return lastUploadText!=null;
	}
	}
	
	

