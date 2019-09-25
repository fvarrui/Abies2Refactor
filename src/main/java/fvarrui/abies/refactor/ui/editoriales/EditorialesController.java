package fvarrui.abies.refactor.ui.editoriales;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import fvarrui.abies.refactor.services.AbiesService;
import fvarrui.abies.refactor.services.items.ProgressListener;
import fvarrui.abies.refactor.ui.AbiesRefactorApp;
import fvarrui.abies.refactor.ui.model.EditorialModel;
import fvarrui.abies.refactor.ui.utils.Controller;
import fvarrui.abies.refactor.ui.utils.ModelUtils;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;

public class EditorialesController extends Controller<AbiesRefactorApp, EditorialesView> {
	
	private Button combinarButton;
	private Button normalizarButton;
	private Button eliminarButton;
	private Button duplicadosButton;

	private TableView<EditorialModel> editorialesTable;
	private TableColumn<EditorialModel, String> nombreColumn;
	
	public EditorialesController(AbiesRefactorApp app) {
		super(app, EditorialesView.class);
	}
	
	protected void bind() {
		combinarButton = getRootNode().getCombinarButton();
		normalizarButton = getRootNode().getNormalizarButton();
		eliminarButton = getRootNode().getEliminarButton();
		duplicadosButton = getRootNode().getDuplicadosButton();
		editorialesTable = getRootNode().getEditorialesTable();
		nombreColumn = getRootNode().getNombreColumn();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		combinarButton.setOnAction(e -> onCombinarButtonAction(e));
		normalizarButton.setOnAction(e -> onNormalizarButtonAction(e));
		eliminarButton.setOnAction(e -> onEliminarButtonAction(e));
		duplicadosButton.setOnAction(e -> onDuplicadosButtonAction(e));
		nombreColumn.setOnEditCommit(e -> onNombreColumnEditCommit(e));
//		editorialesTable.itemsProperty().set(getApp().getEditoriales());
	}
	
	// ===================================
	// EVENTOS
	
	private void onCombinarButtonAction(ActionEvent e) {
		ObservableList<EditorialModel> editoriales = editorialesTable.selectionModelProperty().get().getSelectedItems();
		if (editoriales.size() < 2) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Combinando editoriales");
		    alert.setContentText("Debe seleccionar al menos 2 editoriales para combinarlas");
			alert.showAndWait();
		} else {
			ChoiceDialog<EditorialModel> dialog = new ChoiceDialog<>();
			dialog.setHeaderText("Seleccione la editorial principal");
			dialog.setTitle("Combinando editoriales");
			dialog.setSelectedItem(editoriales.get(0));
			dialog.getItems().addAll(editoriales);
			Optional<EditorialModel> choice = dialog.showAndWait();
			if (choice.isPresent()) {
				EditorialModel principal = choice.get();
				Task<Void> backgroundTask = new Task<Void>() {
					protected Void call() throws Exception {
						updateMessage("Normalizando editoriales...");
						AbiesService.combinarEditoriales(principal.toItem(), ModelUtils.fromEditorialModelToItemList(editoriales), new ProgressListener() {
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
							refrescarEditoriales();					
						} 
					}
				);
				getApp().runInBackground(backgroundTask);
			}
		}
		
	}

	private void refrescarEditoriales() {
		editorialesTable.itemsProperty().get().setAll(ModelUtils.fromEditorialItemToModelList(AbiesService.listarEditoriales()));
		editorialesTable.selectionModelProperty().get().clearSelection();
	}

	private void onNormalizarButtonAction(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Normalizando editoriales");
	    alert.setContentText("¿Seguro que desea normalizar los nombres de las editoriales seleccionadas?");
		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get().equals(ButtonType.OK)) {
			final List<EditorialModel> seleccionados = editorialesTable.getSelectionModel().getSelectedItems();
			Task<Void> backgroundTask = new Task<Void>() {
				protected Void call() throws Exception {
					updateMessage("Normalizando editoriales...");
					AbiesService.normalizarEditoriales(ModelUtils.fromEditorialModelToItemList(seleccionados), true, false, false, new ProgressListener() {
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
						refrescarEditoriales();					
					} 
				}
			);
			getApp().runInBackground(backgroundTask);
		}
	}
	
	private void onEliminarButtonAction(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Eliminar editoriales");
	    alert.setContentText("¿Seguro que desea eliminar las editoriales sin fondos?");
		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get().equals(ButtonType.OK)) {
			Task<Void> backgroundTask = new Task<Void>() {
				protected Void call() throws Exception {
					updateMessage("Eliminando editoriales...");
					AbiesService.eliminarEditorialesSinFondos(new ProgressListener() {
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
			backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) ->  {
				if (newValue == State.SUCCEEDED || newValue == State.CANCELLED || newValue == State.FAILED) {
					refrescarEditoriales();					
				}
			});
			getApp().runInBackground(backgroundTask);
		}
	}


	private void onDuplicadosButtonAction(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Combinar duplicadas");
	    alert.setContentText("¿Seguro que desea combinar todas las editoriales duplicadas?");
		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get().equals(ButtonType.OK)) {
			Task<Void> backgroundTask = new Task<Void>() {
				protected Void call() throws Exception {
					updateMessage("Combinando editoriales...");
					AbiesService.combinarEditorialesDuplicadas(new ProgressListener() {
							public void update(long workDone, long max) {
								updateProgress(workDone, max);
							}
						});
					return null;
				}
				protected void done() {
					super.done();
					updateMessage("");
					updateProgress(0, 100); 
				}
			};
			backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) ->  {
				if (newValue == State.SUCCEEDED || newValue == State.CANCELLED || newValue == State.FAILED) {
					refrescarEditoriales();					
				}
			});
			getApp().runInBackground(backgroundTask);
		}
	}

	private void onNombreColumnEditCommit(CellEditEvent<EditorialModel, String> e) {
		 EditorialModel editorialModel = e.getTableView().getItems().get(e.getTablePosition().getRow());
		 editorialModel.nombreProperty().set(e.getNewValue());
		 AbiesService.actualizarEditorial(editorialModel.toItem());
	}
	
}
