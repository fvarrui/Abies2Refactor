package fvarrui.abies.refactor.ui.principal;

import java.net.URL;
import java.util.ResourceBundle;

import fvarrui.abies.refactor.ui.autores.AutoresController;
import fvarrui.abies.refactor.ui.progreso.ProgresoController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class PrincipalController implements Initializable {
	
	@FXML
	private AutoresController autoresController;

	@FXML
	private ProgresoController progresoController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		progresoController.getView().setVisible(false);				
	}

}
