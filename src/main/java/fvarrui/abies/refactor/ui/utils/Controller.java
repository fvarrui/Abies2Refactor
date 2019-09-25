package fvarrui.abies.refactor.ui.utils;

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.Node;

public abstract class Controller<A extends Application, N extends Node> implements Initializable {

	private A app = null;
	private N rootNode;
	
	public Controller(A app, Class<N> clazz) {
		super();
		try {
			rootNode = clazz.newInstance();
			setApp(app);
			bind();
			initialize(null, null);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public A getApp() {
		return app;
	}

	public void setApp(A app) {
		this.app = app;
	}

	public N getRootNode() {
		return rootNode;
	}

	protected abstract void bind();

}
