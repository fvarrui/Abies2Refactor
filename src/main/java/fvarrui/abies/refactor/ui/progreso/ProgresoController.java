package fvarrui.abies.refactor.ui.progreso;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

public class ProgresoController implements Initializable {

	private Task<Void> backgroundTask;
	private Thread backgroundThread;
	
	@FXML 
	private BorderPane view;

	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Button terminateButton;
	
	@FXML
	private Label progressLabel;
	
	public void initialize(URL location, ResourceBundle resources) {
		view.managedProperty().bind(view.visibleProperty());
	}
	
	public void run(Task<Void> task) {
		this.backgroundTask = task;
		terminateButton.disableProperty().bind(this.backgroundTask.stateProperty().isNotEqualTo(State.RUNNING));
		progressLabel.textProperty().bind(backgroundTask.messageProperty());
		progressBar.progressProperty().bind(backgroundTask.progressProperty());
		this.backgroundTask.stateProperty().addListener( (observable, oldValue, newValue) -> {
			if (newValue == State.SUCCEEDED || newValue == State.FAILED || newValue == State.CANCELLED) {
				progressLabel.textProperty().unbind();
				progressBar.progressProperty().unbind();
				terminateButton.disableProperty().unbind();
				backgroundTask = null;
			}
		});		
		backgroundThread = new Thread(this.backgroundTask);
		backgroundThread.start();
	}
	
	public Task<Void> getBackgroundTask() {
		return backgroundTask;
	}
	
	public BorderPane getView() {
		return view;
	}

	// ===================================
	// EVENTOS

	@SuppressWarnings("deprecation")
	@FXML
	private void onTerminateButtonAction(ActionEvent e) {
		if (this.backgroundThread != null && this.backgroundThread.isAlive()) {
			this.backgroundThread.stop(); // FIXIT hay que mejorar esto
		}
	}
	
}
