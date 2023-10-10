package com.TReSA.main;

import org.apache.lucene.document.Document;

import com.TReSA.lucene.LuceneConstants;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class ViewArticle {
	
	private Stage viewStage;
	private Document doc;
	
	private TextField placesField, peopleField, titleField;
	private TextArea bodyArea;
	private String places, people, title, body;
	
	public ViewArticle(Stage view, Document doc) {
		
		this.doc = doc;
		getDocumentContext();
		
		Scene scene = new Scene(setLayout());
		scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
		this.viewStage = view;
		view.setTitle("View Article");
		view.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		view.requestFocus();
		view.setMinWidth(400);
		view.setMinHeight(700);
		view.setResizable(false);
		view.setScene(scene);
		view.show();
	}
	
	private VBox setLayout() {
		
		Label placesLabel = new Label("Places");
		placesLabel.setFont(new Font("arial", 20));
		placesLabel.setTextFill(Color.BLACK);
		placesLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		placesLabel.setAlignment(Pos.TOP_CENTER);
		placesLabel.setTextAlignment(TextAlignment.LEFT);
		
		placesField = new TextField();
		placesField.setEditable(false);
		placesField.setFont(new Font("arial", 14));
		placesField.setText(places);
		
		Label peopleLabel = new Label("People");
		peopleLabel.setFont(new Font("arial", 20));
		peopleLabel.setTextFill(Color.BLACK);
		peopleLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		peopleLabel.setAlignment(Pos.TOP_CENTER);
		peopleLabel.setTextAlignment(TextAlignment.LEFT);

		peopleField = new TextField();
		peopleField.setEditable(false);
		peopleField.setFont(new Font("arial", 14));
		peopleField.setText(people);
		
		Label titleLabel = new Label("Title");
		titleLabel.setFont(new Font("arial", 20));
		titleLabel.setTextFill(Color.BLACK);
		titleLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		titleLabel.setAlignment(Pos.TOP_CENTER);
		titleLabel.setTextAlignment(TextAlignment.LEFT);
		
		titleField = new TextField();
		titleField.setEditable(false);
		titleField.setFont(new Font("arial", 14));
		titleField.setText(title);
		
		Label bodyLabel = new Label("Body");
		bodyLabel.setFont(new Font("arial", 20));
		bodyLabel.setTextFill(Color.BLACK);
		bodyLabel.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		bodyLabel.setAlignment(Pos.TOP_CENTER);
		bodyLabel.setTextAlignment(TextAlignment.LEFT);
		
		bodyArea = new TextArea();
		bodyArea.setEditable(false);
		bodyArea.setFont(new Font("arial", 14));
		bodyArea.setMinHeight(330);
		bodyArea.setText(body);
		
		VBox context = new VBox();
		context.setSpacing(5.0);
		context.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
		context.getChildren().addAll(placesLabel, placesField, peopleLabel, peopleField, titleLabel, titleField, bodyLabel, bodyArea);
		
		Button closeBtn = new Button("Close");
		closeBtn.setFont(new Font("arial", 20));
		closeBtn.setTextFill(Color.BLACK);
		closeBtn.setMinWidth(170);
		closeBtn.setOnMouseClicked(e -> {
			viewStage.close();
		});
		
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.TOP_CENTER);
		buttons.setMinWidth(200);
		buttons.setPadding(new Insets(15.0, 5.0, 5.0, 5.0));
		buttons.setSpacing(70.0);
		buttons.getChildren().addAll(closeBtn);
		
		VBox root = new VBox();
		root.setBackground(Background.EMPTY);
		root.setSpacing(10.0);
		root.getChildren().addAll(context, buttons);
		return root;
	}
	
	private void getDocumentContext() {
		String context = doc.getField(LuceneConstants.CONTENT).stringValue();
		String[] lines = context.split("\n");
		this.places = lines[0];
		this.people = lines[1];
		this.title = lines[2];
		this.body = lines[3];
		for (int i = 4; i < lines.length - 1; i++) {
			this.body += "\n" + lines[i];
		}
		this.body += "\n" + lines[lines.length - 1];
	}
}
