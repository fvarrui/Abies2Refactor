package fvarrui.abies.refactor.services.items;

public class EditorialItem {
	private Long id;
	private String nombre;
	private Long ejemplares;

	public EditorialItem() {
		super();
	}

	public EditorialItem(Long id, String nombre) {
		this(id, nombre, null);
	}

	public EditorialItem(Long id, String nombre, Long ejemplares) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.ejemplares = ejemplares;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getEjemplares() {
		return ejemplares;
	}

	public void setEjemplares(Long ejemplares) {
		this.ejemplares = ejemplares;
	}
	
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof EditorialItem) {
			EditorialItem editorial = (EditorialItem) obj;
			if (id.equals(editorial.getId())) return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return nombre + " (" + ejemplares + ")";
	}

}
