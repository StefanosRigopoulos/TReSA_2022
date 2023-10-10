package com.TReSA.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import com.TReSA.lucene.LuceneConstants;
import com.TReSA.lucene.Lucene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Window {
	
	private String dataDir = "C:\\Users\\stefa\\eclipse-workspace\\TReSA\\data\\";
	private	String fileSearchDir = "C:\\Users\\stefa\\eclipse-workspace\\TReSA\\Reuters_articles";
	
	private Stage primaryStage;
	public static Lucene lucene = new Lucene();
	
	public static ListView<String> listView;
	private SelectionModel<String> selectionModel;
	public static ArrayList<Document> docs = new ArrayList<>();
	public static ObservableList<Document> observableList = FXCollections.observableArrayList(docs);
	
	private ComboBox<String> searchBar;
	private ChoiceBox<Integer> numberChoice;
	public static Label searchInfo;
	private CheckBox placesBox, peopleBox, titleBox, bodyBox, saveHistoryBox;
	public static boolean saved = false;

	public Window(Stage primaryStage) {
		
		// Creating layout.
		VBox root = new VBox();
		root.setBackground(Background.EMPTY);
		root.getChildren().addAll(SetSearch(), SetBody());
		
		// Reading history.
		ReadHistory();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
		this.primaryStage = primaryStage;
		primaryStage.setTitle("The Reuters Search Assistant");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("The Reuters Search Assistant");
			alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you close the application?");

	        Optional<ButtonType> result = alert.showAndWait();
	        if (result.isPresent()) {
	        	if (result.get() == ButtonType.OK) {
	        		if (saveHistoryBox.isSelected()) {
	        			SaveHistory();
	        		} else {
	        			DeleteHistory();
	        		}
	        	}
	        	else if (result.get() == ButtonType.CANCEL) {
	        		e.consume();
	        	}
	        }
		});
		primaryStage.requestFocus();
		// Setting the aspect ratio to 16:10.
		primaryStage.setMinWidth(1280);
		primaryStage.setMinHeight(800);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Indexing already files into the data dir.
		try {
			lucene.createIndex();
			searchInfo.setText("Indexed " + lucene.getResults() + " files in " + lucene.getTime() + "ms.");
			lucene.clearValues();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private VBox SetSearch() {
		// Search bar layout.
		Label searchLabel = new Label("Search");
		searchLabel.setFont(new Font("arial", 30));
		searchLabel.setTextFill(Color.BLACK);
		searchLabel.setPadding(new Insets(0, 5.0, 5.0, 5.0));
		searchLabel.setAlignment(Pos.CENTER_LEFT);
		searchLabel.setTextAlignment(TextAlignment.LEFT);
		
		searchBar = new ComboBox<>();
		searchBar.setEditable(true);
		searchBar.setMinWidth(1100);
		searchBar.setPromptText("type here");
		searchBar.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				SearchAction(searchBar.getValue());
			}
		});
		
		// Search Filters
		placesBox = new CheckBox();
		placesBox.setText("Places");
		placesBox.setTextFill(Color.BLACK);
		placesBox.setFont(new Font("arial", 15));
		placesBox.setPadding(new Insets(0, 5.0, 0, 5.0));
		placesBox.setAlignment(Pos.CENTER_LEFT);
		placesBox.setTextAlignment(TextAlignment.LEFT);
		
		peopleBox = new CheckBox();
		peopleBox.setText("People");
		peopleBox.setTextFill(Color.BLACK);
		peopleBox.setFont(new Font("arial", 15));
		peopleBox.setPadding(new Insets(0, 5.0, 0, 5.0));
		peopleBox.setAlignment(Pos.CENTER_LEFT);
		peopleBox.setTextAlignment(TextAlignment.LEFT);

		titleBox = new CheckBox();
		titleBox.setText("Title");
		titleBox.setTextFill(Color.BLACK);
		titleBox.setFont(new Font("arial", 15));
		titleBox.setPadding(new Insets(0, 5.0, 0, 5.0));
		titleBox.setAlignment(Pos.CENTER_LEFT);
		titleBox.setTextAlignment(TextAlignment.LEFT);

		bodyBox = new CheckBox();
		bodyBox.setText("Body");
		bodyBox.setTextFill(Color.BLACK);
		bodyBox.setFont(new Font("arial", 15));
		bodyBox.setPadding(new Insets(0, 5.0, 0, 5.0));
		bodyBox.setAlignment(Pos.CENTER_LEFT);
		bodyBox.setTextAlignment(TextAlignment.LEFT);

		HBox searchFilter = new HBox();
		searchFilter.setAlignment(Pos.TOP_LEFT);
		searchFilter.setPadding(new Insets(5.0, 5.0, 0, 5.0));
		searchFilter.setSpacing(5.0);
		searchFilter.getChildren().addAll(placesBox, peopleBox, titleBox, bodyBox);
		
		VBox search = new VBox();
		search.setAlignment(Pos.TOP_LEFT);
		search.setPadding(new Insets(5.0, 5.0, 0 ,0));
		search.setSpacing(5.0);
		search.fillWidthProperty();
		search.getChildren().addAll(searchLabel, searchBar, searchFilter);
		
		// Number of articles layout.
		Label numberLabel = new Label("Number");
		numberLabel.setFont(new Font("arial", 30));
		numberLabel.setTextFill(Color.BLACK);
		numberLabel.setPadding(new Insets(0, 0, 5.0, 5.0));
		numberLabel.setAlignment(Pos.CENTER_LEFT);
		numberLabel.setTextAlignment(TextAlignment.LEFT);
		
		numberChoice = new ChoiceBox<Integer>();
		numberChoice.setValue(10);
		numberChoice.setMinWidth(120);
		numberChoice.getItems().addAll(3, 5, 10, 20, 50, 100, 200, 500, 1000, 5000);
		
		VBox number = new VBox();
		number.setAlignment(Pos.TOP_LEFT);
		number.setPadding(new Insets(5.0, 0, 0 ,5.0));
		number.setSpacing(5.0);
		number.fillWidthProperty();
		number.getChildren().addAll(numberLabel, numberChoice);
		
		// Input layout.
		HBox input = new HBox();
		input.setAlignment(Pos.TOP_LEFT);
		input.setPadding(new Insets(5.0, 5.0, 5.0 ,5.0));
		input.setSpacing(5.0);
		input.getChildren().addAll(search, number);
		
		Separator separator = new Separator();
		separator.setBackground(Background.EMPTY);
		separator.setOrientation(Orientation.HORIZONTAL);
		
		VBox searchPanel = new VBox();
		searchPanel.setAlignment(Pos.TOP_LEFT);
		searchPanel.setPadding(new Insets(5.0, 5.0, 0 ,5.0));
		searchPanel.setSpacing(7.0);
		searchPanel.fillWidthProperty();
		searchPanel.getChildren().addAll(input, separator);
		return searchPanel;
	}
	
	private HBox SetBody() {
		// Search File-based Button.
		Button searchFileBtn = new Button("Article Search");
		searchFileBtn.setFont(new Font("arial", 20));
		searchFileBtn.setTextFill(Color.BLACK);
		searchFileBtn.setMinWidth(170);
		searchFileBtn.setOnMouseClicked(e -> {
			ArticleSearchAction();
		});

		Separator btnSeparator = new Separator();
		btnSeparator.setBackground(Background.EMPTY);
		btnSeparator.setOrientation(Orientation.HORIZONTAL);
		
		// Add article Button.
		Button addBtn = new Button("Add Article");
		addBtn.setFont(new Font("arial", 20));
		addBtn.setTextFill(Color.BLACK);
		addBtn.setMinWidth(170);
		addBtn.setOnMouseClicked(e -> {
			AddArticleAction();
		});

		// Edit article Button.
		Button editBtn = new Button("Edit Article");
		editBtn.setFont(new Font("arial", 20));
		editBtn.setTextFill(Color.BLACK);
		editBtn.setMinWidth(170);
		editBtn.setOnMouseClicked(e -> {
			EditArticleAction();
		});

		// Delete article Button.
		Button delBtn = new Button("Delete Article");
		delBtn.setFont(new Font("arial", 20));
		delBtn.setTextFill(Color.BLACK);
		delBtn.setMinWidth(170);
		delBtn.setOnMouseClicked(e -> {
			DelArticleAction();
		});
		
		Label saveTransparentSeparator = new Label();
		saveTransparentSeparator.setOpacity(0);
		
		saveHistoryBox = new CheckBox();
		saveHistoryBox.setText("Save History");
		saveHistoryBox.setFont(new Font("arial", 14));
		saveHistoryBox.setTextFill(Color.BLACK);
		saveHistoryBox.setPadding(new Insets(3.0, 0, 3.0, 0));
		saveHistoryBox.setTextAlignment(TextAlignment.LEFT);
		
		HBox saveHistory = new HBox();
		saveHistory.setAlignment(Pos.BOTTOM_CENTER);
		saveHistory.setPadding(new Insets(322.0, 0, 2.0, 0));
		saveHistory.getChildren().addAll(saveTransparentSeparator, saveHistoryBox);
		
		VBox buttonsBox = new VBox();
		buttonsBox.setAlignment(Pos.TOP_CENTER);
		buttonsBox.setMinWidth(200);
		buttonsBox.setPadding(new Insets(15.0, 0, 0, 5.0));
		buttonsBox.setSpacing(20.0);
		buttonsBox.getChildren().addAll(searchFileBtn, btnSeparator, addBtn, editBtn, delBtn, saveHistory);
		
		Separator separator = new Separator();
		separator.setBackground(Background.EMPTY);
		separator.setOrientation(Orientation.VERTICAL);
		
		searchInfo = new Label();
		searchInfo.setFont(new Font("arial", 14));
		searchInfo.setId("searchInfo");
		searchInfo.setTextFill(Color.BLACK);
		searchInfo.setPadding(new Insets(2.0, 2.0, 2.0, 4.0));
		searchInfo.setMinWidth(1045);
		searchInfo.setAlignment(Pos.CENTER_LEFT);
		searchInfo.setTextAlignment(TextAlignment.CENTER);
		
		listView = new ListView<String>();
		listView.setCellFactory(cell -> {
			return new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setFont(new Font("arial", 14));
                    } else {
                    	setText(null);
                    }
                }
			};
		});
		listView.setBackground(Background.EMPTY);
		listView.setStyle("-fx-border-color: #444444; -fx-border-width: 2px; -fx-border-style: solid; -fx-background-color: #4f8e90;");
		listView.setMinWidth(1045);
		listView.setMinHeight(590);
		Label placeholder = new Label("No search result to display here");
		placeholder.setTextFill(Color.BLACK);
		listView.setPlaceholder(placeholder);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            if(mouseEvent.getClickCount() == 2){
		            	OpenArticleAction();
		            }
		        }
		    }
		});
		selectionModel = listView.getSelectionModel();
		
		VBox listViewBox = new VBox();
		listViewBox.setPadding(new Insets(5.0, 0, 0, 5.0));
		listViewBox.setSpacing(4.0);
		listViewBox.getChildren().addAll(searchInfo, listView);
		
		HBox bodyPanel = new HBox();
		bodyPanel.getChildren().addAll(buttonsBox, separator, listViewBox);
		return bodyPanel;
	}
	
	private void SearchAction(String searchQuery) {
		// Cleaning docs and listView from the previous search.
		listView.getItems().clear();
		observableList.clear();
		docs.clear();
        
        if (!placesBox.isSelected() && !peopleBox.isSelected() && !titleBox.isSelected() && !bodyBox.isSelected()) {
			searchInfo.setText("");
           	Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("The Reuters Search Assistant");
    		alert.setHeaderText("Warning");
            alert.setContentText("You have to select at least 1 field to search for.");
            alert.showAndWait();
        } else {
           	// Checking if the searchQuery is valid.
        	// / : ( ) [ ] { } ? " - + ! ^ *
            if (searchQuery.equals("")) {
    			searchInfo.setText("");
               	Alert alert = new Alert(AlertType.WARNING);
        		alert.setTitle("The Reuters Search Assistant");
        		alert.setHeaderText("Warning");
                alert.setContentText("You have to enter a string of characters before hitting search.");
                alert.showAndWait();
            } else {
            	// Search the given word.
    	        try {
    		        if (placesBox.isSelected()) {
    					lucene.searchPlaces(searchQuery);
    		        }
    		        if (peopleBox.isSelected()) {
    					lucene.searchPeople(searchQuery);
    		        }
    		        if (titleBox.isSelected()) {
    					lucene.searchTitle(searchQuery);
    		        }
    		        if (bodyBox.isSelected()) {
    					lucene.searchBody(searchQuery);
    		        }
    			} catch (IOException e) {
    				// Do Nothing
    			} catch (ParseException e) {
    				// Do Nothing
    			}

    	        // Getting the search into the history.
    	        if (searchBar.getItems().contains(searchQuery)) {
    	        	searchBar.getItems().remove(searchQuery);
        	        searchBar.getItems().add(0, searchQuery);
    	        } else {
        	        searchBar.getItems().add(0, searchQuery);
    	        }
        		
    			// Getting the results into the ArrayList.
    			docs.addAll(lucene.getDocs());

    			// Showing the results and time to infoTextField.
    			searchInfo.setText("Searching for '" + searchQuery + "'. " + lucene.getResults() + " hits found in " + lucene.getTime() + "ms.");

        		// Checking to not exceed the number of article we identified.
        		int topk = 0;
        		if (numberChoice.getValue() > docs.size()) {
        			topk = docs.size();
        		} else {
        			topk = numberChoice.getValue();
        		}
        		// Returning the articles based on the number the user wants to see.
        		for (int i = 0; i < topk; i++) {
            		// Getting the results into the ObservableList.
        			observableList.add(docs.get(i));
        			
        			// Printing only a part of every article.
        			String content = docs.get(i).getField(LuceneConstants.CONTENT).stringValue();
        			String[] lines = content.split("\n");
        			int numOfLines = 0;
        			if (lines.length > 8) {
        				numOfLines = 8;
        			} else {
        				numOfLines = lines.length;
        			}
        			String stringValue = "";
        			for (int j = 2; j < numOfLines; j++) {
        				if (j == numOfLines - 1) {
            				stringValue += lines[j];
        				} else if (j == 2) {
            				stringValue += lines[j] + "\n" + "\n";
        				} else {
            				stringValue += lines[j] + "\n";
        				}
        			}
        			stringValue += "... \n [Doucle click to view entire article]\n";
        			
            		// Getting the results into the ListView.
        			listView.getItems().add(stringValue);
        		}
        		
        		// Clearing docs.
        		lucene.clearValues();
        		searchBar.setValue("");
        		placesBox.setSelected(false);
        		peopleBox.setSelected(false);
        		titleBox.setSelected(false);
        		bodyBox.setSelected(false);
        	}
        }
	}
	
	@SuppressWarnings("unused")
	private void ArticleSearchAction() {
		// Open File Explorer and select only one txt for search.
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(fileSearchDir));
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TEXT Files", "*.txt"));
		
		File file = fc.showOpenDialog(primaryStage);
		
		if (file != null) {
			try {
				// Extract the title.
				boolean formatError = false;
				BufferedReader br = new BufferedReader(new FileReader(file));

				// First line is for the places.
				String linePlaces = br.readLine().toString();
				if (!linePlaces.contains("</PLACES>")) {
					linePlaces += br.readLine().toString();
					// Checking for the format of the file.
					if (!linePlaces.contains("<PLACES>") || !linePlaces.contains("</PLACES>")) {
						formatError = true;
					}
					linePlaces = linePlaces.replace("<PLACES>", "");
					linePlaces = linePlaces.replace("</PLACES>", "");
				} else {
					linePlaces = linePlaces.replace("<PLACES>", "");
					linePlaces = linePlaces.replace("</PLACES>", "");
				}
				
				// Second line is for the people.
				String linePeople = br.readLine().toString();
				if (!linePeople.contains("</PEOPLE>")) {
					linePeople += br.readLine().toString();
					// Checking for the format of the file.
					if (!linePeople.contains("<PEOPLE>") || !linePeople.contains("</PEOPLE>")) {
						formatError = true;
					}
					linePeople = linePeople.replace("<PEOPLE>", "");
					linePeople = linePeople.replace("</PEOPLE>", "");
				} else {
					linePeople = linePeople.replace("<PEOPLE>", "");
					linePeople = linePeople.replace("</PEOPLE>", "");
				}
				
				// Third line is for the title.
				String lineTitle = br.readLine().toString();
				if (!lineTitle.contains("</TITLE>")) {
					lineTitle += br.readLine().toString();
					// Checking for the format of the file.
					if (!lineTitle.contains("<TITLE>") || !lineTitle.contains("</TITLE>")) {
						formatError = true;
					}
					lineTitle = lineTitle.replace("<TITLE>", "");
					lineTitle = lineTitle.replace("</TITLE>", "");
				} else {
					lineTitle = lineTitle.replace("<TITLE>", "");
					lineTitle = lineTitle.replace("</TITLE>", "");
				}
				
				// Fourth and beyond is for body.
				String tmp, lineBody = "";
				while ((tmp = br.readLine()) != null) {
					if (tmp.contains("<BODY>")) {
						// Checking for the format of the file.
						if (!tmp.contains("<BODY>")) {
							formatError = true;
						}
						tmp = tmp.replace("<BODY>", "");
					}
					if (tmp.contains("</BODY>")) {
						// Checking for the format of the file.
						if (!tmp.contains("</BODY>")) {
							formatError = true;
						}
						tmp = tmp.replace("</BODY>", "");
					}
					lineBody += tmp + "\n";
				}
				
				br.close();
				
				if (formatError) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("The Reuters Search Assistant");
					alert.setHeaderText("File Format ERROR!");
		            alert.setContentText("The file you imported for relevant search has format errors.\n"
		            				   + "Please choose the correct file and check the format to meet\n"
		            				   + "the required format as shown below:\n"
		            				   + "<PLACES>example_place</PLACES>\n"
		            				   + "<PEOPLE>example_people</PEOPLE>\n"
		            				   + "<TITLE>example_title</TITLE>\n"
		            				   + "<BODY>example_body</BODY>");
		            alert.showAndWait();
				} else {
					// Do a searchTitle operation and print the relevant docs.
					lucene.searchTitle(lineTitle);

	    			// Getting the results into the ArrayList.
	    			docs.addAll(lucene.getDocs());
	    			
	    			// Showing the results and time to infoTextField.
	    			searchInfo.setText("Searching for '" + file.getName() + "'. " + lucene.getResults() + " hits found in " + lucene.getTime() + "ms.");
	    			
	        		// Checking to not exceed the number of article we identified.
	        		int topk = 0;
	        		if (numberChoice.getValue() > docs.size()) {
	        			topk = docs.size();
	        		} else {
	        			topk = numberChoice.getValue();
	        		}
	        		
	        		// Returning the articles based on the number the user wants to see.
	        		for (int i = 0; i < topk; i++) {
	            		// Getting the results into the ObservableList.
	        			observableList.add(docs.get(i));
	        			
	        			// Printing only a part of every article.
	        			String content = docs.get(i).getField(LuceneConstants.CONTENT).stringValue();
	        			String[] lines = content.split("\n");
	        			int numOfLines = 0;
	        			if (lines.length > 8) {
	        				numOfLines = 8;
	        			} else {
	        				numOfLines = lines.length;
	        			}
	        			String stringValue = "";
	        			for (int j = 2; j < numOfLines; j++) {
	        				if (j == numOfLines - 1) {
	            				stringValue += lines[j];
	        				} else if (j == 2) {
	            				stringValue += lines[j] + "\n" + "\n";
	        				} else {
	            				stringValue += lines[j] + "\n";
	        				}
	        			}
	        			stringValue += "... \n [Doucle click to view entire article]\n";
	        			
	            		// Getting the results into the ListView.
	        			listView.getItems().add(stringValue);
	        		}
	        		
	        		// Clearing docs.
	        		lucene.clearValues();
				}
			} catch (FileNotFoundException e) {
				// Do Nothing
			} catch (IOException e) {
				// Do Nothing
			} catch (ParseException e) {
				// Do Nothing
			}
		}
	}
	
	private void AddArticleAction() {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(fileSearchDir));
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TEXT Files", "*.txt"));
		
		List<File> files = fc.showOpenMultipleDialog(primaryStage);
		
		if (files != null) {
			// Copy the files to the data dir.
			File dst = new File(dataDir);
			for (File src: files) {
				try {
					FileUtils.copyFileToDirectory(src, dst, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Indexing the files from data dir.
			try {
				lucene.createIndex();
				searchInfo.setText("Indexed " + lucene.getResults() + " files in " + lucene.getTime() + "ms.");
				lucene.clearValues();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void EditArticleAction() {
		if (!selectionModel.isEmpty()) {
            // Gets the article selected.
            // Opens the context to a new window.
            // Lets the user edit its values and then recreate it and insert it back into the folder.
            Stage edit = new Stage();
            Document doc = observableList.get(selectionModel.getSelectedIndex());
            new EditWindow(edit, doc, selectionModel.getSelectedIndex());
            
            selectionModel.clearSelection();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("The Reuters Search Assistant");
			alert.setHeaderText("Error");
            alert.setContentText("You have NOT selected any articles to edit.");
            alert.showAndWait();
		}
	}
	
	private void DelArticleAction() {
		if (!selectionModel.isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("The Reuters Search Assistant");
			alert.setHeaderText("Confirmation");
            alert.setContentText("Proceed with removal of the registry?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()){
                if (result.get() == ButtonType.OK){
                    // Remove from the directory.
                	Document tempDocument = observableList.get(selectionModel.getSelectedIndex());
                	File file = new File(tempDocument.getField(LuceneConstants.FILE_PATH).stringValue());
                	file.delete();
                	
                	// Remove from the two lists.
                	observableList.remove(selectionModel.getSelectedIndex());
                	listView.getItems().remove(selectionModel.getSelectedIndex());
                    selectionModel.clearSelection();
                    
        			// Indexing the files from data dir.
        			try {
        				lucene.createIndex();
        				searchInfo.setText("Indexed " + lucene.getResults() + " files in " + lucene.getTime() + "ms.");
        				lucene.clearValues();
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
                } else{
                    selectionModel.clearSelection();
                    alert.close();
                }
            }
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("The Reuters Search Assistant");
			alert.setHeaderText("Error");
            alert.setContentText("You have NOT selected any articles for removal.");
            alert.showAndWait();
		}
	}
	
	private void OpenArticleAction() {
		if (!selectionModel.isEmpty()) {
            // Gets the article selected.
            // Opens the context to a new window.
            // Lets the user view the entire article.
            Stage view = new Stage();
            Document doc = observableList.get(selectionModel.getSelectedIndex());
            new ViewArticle(view, doc);
			
            selectionModel.clearSelection();
		}
	}
	
	private void SaveHistory() {
		File history = new File("history.txt");
		if (history != null) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(history));
				bw.write("on\n");
				ObservableList<String> searches = searchBar.getItems();
				for (String search: searches) {
					bw.write(search + "\n");
				}
				bw.close();
			} catch (IOException e) {
				// Do Nothing
			}
		}
	}
	
	private void ReadHistory() {
		File history = new File("history.txt");
		if (history != null) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(history));
				if (br.readLine().equals("on")) {
					saveHistoryBox.setSelected(true);
				}
				String search = "";
				while ((search = br.readLine()) != null) {
					searchBar.getItems().add(search);
				}
				br.close();
			} catch (IOException e) {
				// Do Nothing
			}
		}
	}
	
	private void DeleteHistory() {
		File history = new File("history.txt");
		if (history != null) {
			history.delete();
		}
	}
}