package fvarrui.abies.refactor.ui.model;

import fvarrui.abies.refactor.services.items.EditorialItem;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class EditorialModel {
	private SimpleLongProperty id = new SimpleLongProperty();
	private SimpleStringProperty nombre = new SimpleStringProperty();
	private SimpleLongProperty ejemplares = new SimpleLongProperty();

	public EditorialModel() {
		super();
	}
	
	public EditorialModel(Long id, String nombre) {
		this(id, nombre, null);
	}

	public EditorialModel(Long id, String nombre, Long ejemplares) {
		super();
		this.id.set(id);
		this.nombre.set(nombre);
		this.ejemplares.set(ejemplares);
	}

	public SimpleLongProperty idProperty() {
		return id;
	}
	
	public SimpleStringProperty nombreProperty() {
		return nombre;
	}

	public SimpleLongProperty ejemplaresProperty() {
		return ejemplares;
	}

	@Override
	public String toString() {
		return nombre.get() + " (" + ejemplares.get() + ")";
	}
	
	public static EditorialModel fromItem(EditorialItem editorial) {
		return new EditorialModel(editorial.getId(), editorial.getNombre(), editorial.getEjemplares());
	}
	
	public EditorialItem toItem() {
		return new EditorialItem(idProperty().get(), nombreProperty().get(), ejemplaresProperty().get());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof EditorialModel) {
			EditorialModel editorial = (EditorialModel) obj;
			if (idProperty().get() == editorial.idProperty().get()) return true;
		}
		return false;
	}

}
