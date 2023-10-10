package com.TReSA.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.document.Document;

import com.TReSA.lucene.LuceneConstants;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class EditWindow {
	
	private Stage editStage;
	private Document doc;
	
	private TextField placesField, peopleField, titleField;
	private TextArea bodyArea;
	private String places, people, title, body;
	private int index = 0;
	
	public EditWindow(Stage edit, Document doc, int index) {
		
		this.index = index;
		this.doc = doc;
		getDocumentContext();
		
		Scene scene = new Scene(setLayout());
		scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
		this.editStage = edit;
		edit.setTitle("Edit Article");
		edit.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		edit.requestFocus();
		edit.setMinWidth(400);
		edit.setMinHeight(700);
		edit.setResizable(false);
		edit.setScene(scene);
		edit.show();
	}
	
	private VBox setLayout() {
		
		Label placesLabel = new Label("Places");
		placesLabel.setFont(new Font("arial", 20));
		placesLabel.setTextFill(Color.BLACK);
		placesLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		placesLabel.setAlignment(Pos.TOP_CENTER);
		placesLabel.setTextAlignment(TextAlignment.LEFT);
		
		placesField = new TextField();
		placesField.setFont(new Font("arial", 14));
		placesField.setText(places);
		
		Label peopleLabel = new Label("People");
		peopleLabel.setFont(new Font("arial", 20));
		peopleLabel.setTextFill(Color.BLACK);
		peopleLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		peopleLabel.setAlignment(Pos.TOP_CENTER);
		peopleLabel.setTextAlignment(TextAlignment.LEFT);

		peopleField = new TextField();
		peopleField.setFont(new Font("arial", 14));
		peopleField.setText(people);
		
		Label titleLabel = new Label("Title");
		titleLabel.setFont(new Font("arial", 20));
		titleLabel.setTextFill(Color.BLACK);
		titleLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		titleLabel.setAlignment(Pos.TOP_CENTER);
		titleLabel.setTextAlignment(TextAlignment.LEFT);
		
		titleField = new TextField();
		titleField.setFont(new Font("arial", 14));
		titleField.setText(title);
		
		Label bodyLabel = new Label("Body");
		bodyLabel.setFont(new Font("arial", 20));
		bodyLabel.setTextFill(Color.BLACK);
		bodyLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		bodyLabel.setAlignment(Pos.TOP_CENTER);
		bodyLabel.setTextAlignment(TextAlignment.LEFT);
		
		bodyArea = new TextArea();
		bodyArea.setFont(new Font("arial", 14));
		bodyArea.setMinHeight(330);
		bodyArea.setText(body);
		
		VBox context = new VBox();
		context.setSpacing(5.0);
		context.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
		context.getChildren().addAll(placesLabel, placesField, peopleLabel, peopleField, titleLabel, titleField, bodyLabel, bodyArea);
		
		Button cancelBtn = new Button("Cancel");
		cancelBtn.setFont(new Font("arial", 20));
		cancelBtn.setTextFill(Color.BLACK);
		cancelBtn.setMinWidth(170);
		cancelBtn.setOnMouseClicked(e -> {
			cancelAction();
		});
		
		Button saveBtn = new Button("Save");
		saveBtn.setFont(new Font("arial", 20));
		saveBtn.setTextFill(Color.BLACK);
		saveBtn.setMinWidth(170);
		saveBtn.setOnMouseClicked(e -> {
			try {
				saveAction();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.TOP_CENTER);
		buttons.setMinWidth(200);
		buttons.setPadding(new Insets(15.0, 5.0, 5.0, 5.0));
		buttons.setSpacing(70.0);
		buttons.getChildren().addAll(cancelBtn, saveBtn);
		
		VBox root = new VBox();
		root.setBackground(Background.EMPTY);
		root.setSpacing(10.0);
		root.getChildren().addAll(context, buttons);
		return root;
	}
	
	private void getDocumentContext() {
		String context = doc.getField(LuceneConstants.CONTENT).stringValue();
		String[] lines = context.split("\n");
		this.places = "<PLACES>" + lines[0] + "</PLACES>";
		this.people = "<PEOPLE>" + lines[1] + "</PEOPLE>";
		this.title = "<TITLE>" + lines[2] + "</TITLE>";
		this.body = "<BODY>" + lines[3];
		for (int i = 4; i < lines.length - 1; i++) {
			this.body += "\n" + lines[i];
		}
		this.body += "\n" + lines[lines.length - 1] + "</BODY>";
	}
	
	private void cancelAction() {
		// Discard changes.
		editStage.close();
	}
	
	private void saveAction() throws IOException {
		String placesFinal = placesField.getText();
		String peopleFinal = peopleField.getText();
		String titleFinal = titleField.getText();
		String bodyFinal = bodyArea.getText();

    	// Checking data to see if there are the required defining words.
    	if (placesFinal.startsWith("<PLACES>") && placesFinal.endsWith("</PLACES>") && peopleFinal.startsWith("<PEOPLE>") && peopleFinal.endsWith("</PEOPLE>")
    																				&& titleFinal.startsWith("<TITLE>") && titleFinal.endsWith("</TITLE>")
    																				&& bodyFinal.startsWith("<BODY>") && bodyFinal.endsWith("</BODY>")) {
    		
    		// Remove the old file.
    		File removalFile = new File(doc.getField(LuceneConstants.FILE_PATH).stringValue());
    		removalFile.delete();

    		// Creating the new file with the same name.
        	File savedFile = new File(doc.getField(LuceneConstants.FILE_PATH).stringValue());
        	BufferedWriter bw = new BufferedWriter(new FileWriter(savedFile));
        	
    		bw.write(placesFinal + "\n");
    		bw.write(peopleFinal + "\n");
    		bw.write(titleFinal + "\n");
    		bw.write(bodyFinal + "\n");
    		bw.close();
    		
    		// Indexing the files from data dir.
    		try {
    			Window.lucene.createIndex();
    			Window.searchInfo.setText("Indexed " + Window.lucene.getResults() + " files in " + Window.lucene.getTime() + "ms.");
    			Window.lucene.clearValues();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		// Remove from the two lists.
    		Window.observableList.remove(index);
    		Window.listView.getItems().remove(index);
    		
    		editStage.close();
    	} else {
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("The Reuters Search Assistant");
    		alert.setHeaderText("Field Format ERROR.");
            alert.setContentText("Fields must contain all the required definings as shown below:\n<PLACES>example_place</PLACES>\n<PEOPLE>example_people</PEOPLE>\n<TITLE>example_title</TITLE>\n<BODY>example_body</BODY>");
            alert.showAndWait();
    	}
	}
}
