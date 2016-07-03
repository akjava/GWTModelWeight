package com.akjava.gwt.modelweight.client;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AbstractTextFileUploadPanel extends VerticalPanel{

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

	private Label titleLabel;

	private HorizontalPanel firstRow;

	public HorizontalPanel getFirstRow() {
		return firstRow;
	}

	public HorizontalPanel getSecondRow() {
		return secondRow;
	}


	private HorizontalPanel secondRow;
	public String getLastUploadText() {
		return lastUploadText;
	}

	public AbstractTextFileUploadPanel(){
		this("Name:",false);
	}
	public AbstractTextFileUploadPanel(String labelName,boolean hasReset){
		this.setWidth("100%");
		this.setSpacing(1);
		firstRow = new HorizontalPanel();
		firstRow.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		firstRow.setWidth("100%");
		
		
		this.add(firstRow);
		titleLabel = new Label(labelName);
		titleLabel.setWidth("80px");
		
		firstRow.add(titleLabel);
		fileNameLabel = new Label();
		fileNameLabel.setWidth("100%");
		firstRow.add(fileNameLabel);
		
		secondRow = new HorizontalPanel();
		secondRow.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		this.add(secondRow);
		uploadForm = FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				lastUploadText=text;
				uploadText(file.getFileName(),text);
			}
		}, true);
		
		//hide annyoing
		uploadForm.getFileUpload().getElement().getStyle().setBackgroundColor("#e8e8e8");
		uploadForm.getFileUpload().getElement().getStyle().setColor("#e8e8e8");
		
		secondRow.add(uploadForm);
		if(hasReset){
		secondRow.add(new Button("Reset",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				uploadText("", null);
			}
		}));
		}
		
		
	}
	public void reload(){
		onTextFileUpload(lastUploadText);
	}

	public void uploadText(String fileName,String text){
		fileNameLabel.setText(fileName);
		onTextFileUpload(text);
	}
	
	
	abstract protected void onTextFileUpload(@Nullable String text);

	public boolean isUploaded() {
		return lastUploadText!=null;
	}
	}
	
	

