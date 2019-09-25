package fvarrui.abies.refactor.ui.model;

import fvarrui.abies.refactor.services.items.AutorItem;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class AutorModel {
	private SimpleLongProperty id = new SimpleLongProperty();
	private SimpleStringProperty nombre = new SimpleStringProperty();
	private SimpleStringProperty resto = new SimpleStringProperty();
	private SimpleLongProperty ejemplares = new SimpleLongProperty();

	public AutorModel() {
		super();
	}
	
	public AutorModel(Long id, String nombre) {
		this(id, nombre, null, null);
	}

	public AutorModel(Long id, String nombre, String resto, Long ejemplares) {
		super();
		this.id.set(id);
		this.nombre.set(nombre);
		this.resto.set(resto);
		this.ejemplares.set(ejemplares);
	}

	public SimpleLongProperty idProperty() {
		return id;
	}
	
	public SimpleStringProperty nombreProperty() {
		return nombre;
	}

	public SimpleStringProperty restoProperty() {
		return resto;
	}

	public SimpleLongProperty ejemplaresProperty() {
		return ejemplares;
	}

	@Override
	public String toString() {
		return nombre.get() + " (" + ejemplares.get() + ")";
	}
	
	public static AutorModel fromItem(AutorItem autor) {
		return new AutorModel(autor.getId(), autor.getNombre(), autor.getResto(), autor.getEjemplares());
	}
	
	public AutorItem toItem() {
		return new AutorItem(idProperty().get(), nombreProperty().get(), restoProperty().get(), ejemplaresProperty().get());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof AutorModel) {
			AutorModel autor = (AutorModel) obj;
			if (idProperty().get() == autor.idProperty().get()) return true;
		}
		return false;
	}

}
