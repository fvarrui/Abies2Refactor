package fvarrui.abies.refactor.ui.editoriales;

import fvarrui.abies.refactor.ui.model.EditorialModel;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class EditorialesView extends BorderPane {

	private Button combinarButton;
	private Button normalizarButton;
	private Button eliminarButton;
	private Button duplicadosButton;

	private TableView<EditorialModel> editorialesTable;
	private TableColumn<EditorialModel, Long> idColumn;
	private TableColumn<EditorialModel, String> nombreColumn;
	private TableColumn<EditorialModel, Long> ejemplaresColumn;

	public EditorialesView() {
		init();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		setId("background");
		
		editorialesTable = new TableView<>();
		editorialesTable.selectionModelProperty().get().setSelectionMode(SelectionMode.MULTIPLE);
		editorialesTable.setEditable(true);

		idColumn = new TableColumn<>("Identificador");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		idColumn.prefWidthProperty().bind(editorialesTable.widthProperty().subtract(2).multiply(0.2));

		nombreColumn = new TableColumn<>("Nombre");
		nombreColumn.setEditable(true);
		nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		nombreColumn.prefWidthProperty().bind(editorialesTable.widthProperty().subtract(2).multiply(0.6));

		ejemplaresColumn = new TableColumn<>("Ejemplares");
		ejemplaresColumn.setCellValueFactory(new PropertyValueFactory<>("ejemplares"));
		ejemplaresColumn.prefWidthProperty().bind(editorialesTable.widthProperty().subtract(2).multiply(0.2));

		editorialesTable.getColumns().addAll(idColumn, nombreColumn, ejemplaresColumn);

		combinarButton = new Button("Combinar editoriales");
		normalizarButton = new Button("Normalizar editoriales");
		duplicadosButton = new Button("Combinar duplicadas");
		eliminarButton = new Button("Eliminar editoriales sin ejemplares");

		eliminarButton.getStyleClass().addAll("first");
		combinarButton.getStyleClass().addAll("last");

		HBox botonesPane = new HBox();
		botonesPane.getStyleClass().setAll("segmented-button-bar");
		botonesPane.getChildren().addAll(eliminarButton, normalizarButton, duplicadosButton, combinarButton);

		ToolBar toolBar = new ToolBar();
		toolBar.getItems().addAll(botonesPane);

		BorderPane centroPane = new BorderPane();
		centroPane.setCenter(editorialesTable);
		centroPane.setTop(toolBar);

		setCenter(centroPane);
		
	}

	public TableView<EditorialModel> getEditorialesTable() {
		return editorialesTable;
	}

	public Button getCombinarButton() {
		return combinarButton;
	}

	public Button getNormalizarButton() {
		return normalizarButton;
	}

	public Button getEliminarButton() {
		return eliminarButton;
	}

	public Button getDuplicadosButton() {
		return duplicadosButton;
	}

	public TableColumn<EditorialModel, String> getNombreColumn() {
		return nombreColumn;
	}

}
