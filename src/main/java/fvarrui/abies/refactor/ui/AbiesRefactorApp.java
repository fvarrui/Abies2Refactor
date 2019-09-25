package fvarrui.abies.refactor.ui;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import fvarrui.abies.refactor.db.Database;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AbiesRefactorApp extends Application {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(AbiesRefactorApp.class.getPackage().getName() + ".version");
	private static final Logger LOG = Logger.getLogger(AbiesRefactorApp.class.getName());

	public static AbiesRefactorApp app;

	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		app = this;
		
		this.primaryStage = primaryStage;

//		if (chooseDatabase()) {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PrincipalView.fxml"));

			this.primaryStage.setTitle("Abies2 Refactor v" + BUNDLE.getString("version") + " por Fran Vargas");
			this.primaryStage.setMaximized(true);
			this.primaryStage.getIcons().add(new Image(getClass().getResource("/images/app-icon-16x16.png").toExternalForm()));
			this.primaryStage.getIcons().add(new Image(getClass().getResource("/images/app-icon-32x32.png").toExternalForm()));
			this.primaryStage.getIcons().add(new Image(getClass().getResource("/images/app-icon-64x64.png").toExternalForm()));
			this.primaryStage.setScene(new Scene(loader.load()));
			this.primaryStage.show();

//		} else {
//			Platform.exit();
//		}

	}

	@Override
	public void stop() throws Exception {
		super.stop();
		Database.close();
	}
	
	public void runInBackground(Task<Void> task) {
//		principalController.getProgresoController().run(task);
	}

	private boolean chooseDatabase() throws IOException {
		boolean ok = false;
		FileChooser dbChooser = new FileChooser();
		dbChooser.setTitle("Seleccionar base de datos Abies 2");
		dbChooser.setInitialDirectory(new File("."));
		dbChooser.getExtensionFilters().add(new ExtensionFilter("MDB", "*.mdb"));
		File dbFile = dbChooser.showOpenDialog(this.primaryStage);
		if (dbFile != null) {
			String dbUrl = "jdbc:ucanaccess://" + dbFile.getAbsolutePath();
			Connection conn = Database.connect(dbUrl, "", "nohay2sin3");
			if (conn != null) {
				Database.setConnection(conn);
				mdbBackup(dbFile);
				ok = true;
			} else {
				error("Abies 2 Refactor", "Error al abrir la conexión", "No fue posible abrir la conexión con la base de datos:\n" + dbFile);
			}
		}
		return ok;
	}

	private void mdbBackup(File original) throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String path = original.getAbsolutePath();
		path = path.substring(0, path.lastIndexOf(".mdb"));
		LOG.info("Copiando fichero MDB con la base de datos ABIES 2 en " + path);
		File target = new File(path + "-" + formatter.format(new Date()) + ".mdb");
		FileUtils.copyFile(original, target);
	}

	public ButtonType alert(AlertType type, String title, String headerText, String contentText, ButtonType ... buttons) {
		Alert alertDialog = new Alert(type);
		alertDialog.initOwner(primaryStage);
		alertDialog.setTitle(title);
		alertDialog.setHeaderText(headerText);
		alertDialog.setContentText(contentText);
		alertDialog.getButtonTypes().setAll(buttons);
		return alertDialog.showAndWait().get();
	}
	
	public void error(String title, String headerText, String contentText) {
		alert(AlertType.ERROR, title, headerText, contentText);
	}
	
	public void warning(String title, String headerText, String contentText) {
		alert(AlertType.ERROR, title, headerText, contentText);
	}

	public boolean confirm(String title, String headerText, String contentText) {
		return alert(AlertType.ERROR, title, headerText, contentText, ButtonType.YES, ButtonType.NO).equals(ButtonType.YES);
	}

	public static void main(String[] args) throws IOException {
		launch(args);
	}

}
