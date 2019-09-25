package fvarrui.abies.refactor.services.items;

public class AutorItem {
	private Long id;
	private String nombre;
	private String resto;
	private Long ejemplares;

	public AutorItem() {
		super();
	}

	public AutorItem(Long id, String nombre) {
		this(id, nombre, null, null);
	}

	public AutorItem(Long id, String nombre, String resto, Long ejemplares) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.resto = resto;
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

	public String getResto() {
		return resto;
	}

	public void setResto(String resto) {
		this.resto = resto;
	}

	public Long getEjemplares() {
		return ejemplares;
	}

	public void setEjemplares(Long ejemplares) {
		this.ejemplares = ejemplares;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof AutorItem) {
			AutorItem autor = (AutorItem) obj;
			if (id.equals(autor.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "[" + id + "] " + nombre + " (" + ejemplares + ")";
	}

}
