package fvarrui.abies.refactor.ui.autores;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import fvarrui.abies.refactor.services.AbiesService;
import fvarrui.abies.refactor.services.items.ProgressListener;
import fvarrui.abies.refactor.ui.AbiesRefactorApp;
import fvarrui.abies.refactor.ui.model.AutorModel;
import fvarrui.abies.refactor.ui.utils.ModelUtils;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class AutoresController implements Initializable {
	
	private ListProperty<AutorModel> autores = new SimpleListProperty<>(this, "autores", FXCollections.observableArrayList());
	
	@FXML
	private Button combinarButton;
	
	@FXML
	private Button normalizarButton;
	
	@FXML
	private Button eliminarButton;
	
	@FXML
	private Button duplicadosButton;
	
	@FXML
	private Button invertirButton;

	@FXML
	private TableView<AutorModel> autoresTable;

	@FXML
	private TableColumn<AutorModel, Long> idColumn;
	
	@FXML
	private TableColumn<AutorModel, String> nombreColumn;

	@FXML
	private TableColumn<AutorModel, Long> ejemplaresColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		autoresTable.selectionModelProperty().get().setSelectionMode(SelectionMode.MULTIPLE);
		autoresTable.setEditable(true);

		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		idColumn.prefWidthProperty().bind(autoresTable.widthProperty().subtract(2).multiply(0.2));

		nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		nombreColumn.prefWidthProperty().bind(autoresTable.widthProperty().subtract(2).multiply(0.6));

		ejemplaresColumn.setCellValueFactory(new PropertyValueFactory<>("ejemplares"));
		ejemplaresColumn.prefWidthProperty().bind(autoresTable.widthProperty().subtract(2).multiply(0.2));
		
		autoresTable.itemsProperty().bind(autores);
		
		autores.setAll(ModelUtils.fromAutorItemToModelList(AbiesService.listarAutores()));

	}
	
	// ===================================
	// EVENTOS
	
	@FXML
	private void onCombinarButtonAction(ActionEvent e) {
		ObservableList<AutorModel> autores = autoresTable.selectionModelProperty().get().getSelectedItems();
		if (autores.size() < 2) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Combinando autores");
		    alert.setContentText("Debe seleccionar al menos 2 autores para combinarlos");
			alert.showAndWait();
		} else {
			ChoiceDialog<AutorModel> dialog = new ChoiceDialog<>();
			dialog.setHeaderText("Seleccione el autor principal");
			dialog.setTitle("Combinando autores");
			dialog.setSelectedItem(autores.get(0));
			dialog.getItems().addAll(autores);
			Optional<AutorModel> choice = dialog.showAndWait();
			if (choice.isPresent()) {
				AutorModel principal = choice.get();
				Task<Void> backgroundTask = new Task<Void>() {
					protected Void call() throws Exception {
						updateMessage("Combinando autores...");
						AbiesService.combinarAutores(principal.toItem(), ModelUtils.fromAutorModelToItemList(autores), new ProgressListener() {
								public void update(long workDone, long max) {
									updateProgress(workDone, max);
								}
							});
						return null;
					}
					protected void done() {
						super.done();
						if (!isCancelled()) {
							updateMessage("");
							updateProgress(0, 100); 
						}
					}
				}; 
				backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) -> {
						if (newValue == State.SUCCEEDED || newValue == State.CANCELLED || newValue == State.FAILED) {
							refrescarAutores();						
						} 
					}
				);
				AbiesRefactorApp.app.runInBackground(backgroundTask);
			}
		}
		
	}

	@FXML
	private void onNormalizarButtonAction(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Normalizando autores");
	    alert.setContentText("¿Seguro que desea normalizar los nombres de los autores seleccionados?");
		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get().equals(ButtonType.OK)) {
			final List<AutorModel> seleccionados = autoresTable.getSelectionModel().getSelectedItems();
			
			Task<Void> backgroundTask = new Task<Void>() {
				protected Void call() throws Exception {
					updateMessage("Normalizando autores...");
					AbiesService.normalizarAutores(ModelUtils.fromAutorModelToItemList(seleccionados), true, false, false, new ProgressListener() {
							public void update(long workDone, long max) {
								updateProgress(workDone, max);
							}
						});
					return null;
				}
				protected void done() {
					super.done();
					if (!isCancelled()) {
						updateMessage("");
						updateProgress(0, 100); 
					}
				}
			}; 
			backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) -> {
					if (newValue == State.SUCCEEDED || newValue == State.CANCELLED || newValue == State.FAILED) {
						refrescarAutores();
					} 
				}
			);
			AbiesRefactorApp.app.runInBackground(backgroundTask);			
		}
	}
	
	@FXML
	private void onInvertirButtonAction(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Invertir nombres");
	    alert.setContentText("¿Seguro que desea invertir los nombres de los autores seleccionados?");
		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get().equals(ButtonType.OK)) {
			final List<AutorModel> seleccionados = autoresTable.getSelectionModel().getSelectedItems();
			Task<Void> backgroundTask = new Task<Void>() {
				protected Void call() throws Exception {
					updateMessage("Invertiendo nombres...");
					AbiesService.invertirNombresAutores(ModelUtils.fromAutorModelToItemList(seleccionados), new ProgressListener() {
							public void update(long workDone, long max) {
								updateProgress(workDone, max);
							}
						});
					return null;
				}
				protected void done() {
					super.done();
					if (!isCancelled()) {
						updateMessage("");
						updateProgress(0, 100); 
					}
				}
			}; 
			backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) -> {
					if (newValue == State.SUCCEEDED || newValue == State.CANCELLED || newValue == State.FAILED) {
						refrescarAutores();
					} 
				}
			);
			AbiesRefactorApp.app.runInBackground(backgroundTask);
		}
	}

	@FXML
	private void onEliminarButtonAction(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Eliminar autores");
	    alert.setContentText("¿Seguro que desea eliminar los autores sin fondos?");
		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get().equals(ButtonType.OK)) {
			Task<Void> backgroundTask = new Task<Void>() {
				protected Void call() throws Exception {
					updateMessage("Eliminando autores...");
					AbiesService.eliminarAutoresSinFondos(new ProgressListener() {
							public void update(long workDone, long max) {
								updateProgress(workDone, max);
							}
						});
					return null;
				}
				protected void done() {
					super.done();
					if (!isCancelled()) {
						updateMessage("");
						updateProgress(0, 100); 
					}
				}
			}; 
			backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) -> {
					if (newValue == State.SUCCEEDED || newValue == State.CANCELLED || newValue == State.FAILED) {
						refrescarAutores();
					} 
				}
			);
			AbiesRefactorApp.app.runInBackground(backgroundTask);
		}
	}


	@FXML
	private void onDuplicadosButtonAction(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Combinar duplicados");
	    alert.setContentText("¿Seguro que desea combinar todos los autores duplicados?");
		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get().equals(ButtonType.OK)) {
			Task<Void> backgroundTask = new Task<Void>() {
				protected Void call() throws Exception {
					updateMessage("Combinando autores...");
					AbiesService.combinarAutoresDuplicados(new ProgressListener() {
							public void update(long workDone, long max) {
								updateProgress(workDone, max);
							}
						});
					return null;
				}
				protected void done() {
					super.done();
					if (!isCancelled()) {
						updateMessage("");
						updateProgress(0, 100); 
					}
				}
			}; 
			backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) -> {
					if (newValue == State.SUCCEEDED || newValue == State.CANCELLED || newValue == State.FAILED) {
						refrescarAutores();
					} 
				}
			);
			AbiesRefactorApp.app.runInBackground(backgroundTask);
		}
	}

	@FXML
	private void onNombreColumnEditCommit(CellEditEvent<AutorModel, String> e) {
		 AutorModel autorModel = (AutorModel)e.getTableView().getItems().get(e.getTablePosition().getRow());
		 autorModel.nombreProperty().set(e.getNewValue());
		 AbiesService.actualizarAutor(autorModel.toItem());
	}


	private void refrescarAutores() {
		autoresTable.itemsProperty().get().setAll(ModelUtils.fromAutorItemToModelList(AbiesService.listarAutores()));
		autoresTable.selectionModelProperty().get().clearSelection();
//		autoresTable.selectionModelProperty().get().select(principal);
	}
	
}
