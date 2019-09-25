package fvarrui.abies.refactor.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import fvarrui.abies.refactor.db.Database;
import fvarrui.abies.refactor.services.items.AutorItem;
import fvarrui.abies.refactor.services.items.EditorialItem;
import fvarrui.abies.refactor.services.items.ProgressListener;

public class AbiesService {
	
	private static final Logger LOG = Logger.getLogger(AbiesService.class.getName());

	public static List<AutorItem> listarAutores() {
		LOG.info("Listando todos los autores");
		List<AutorItem> autores = new ArrayList<AutorItem>();
		try {
			ResultSet rs  = 
					Database.executeQuery(
							"SELECT AUTORES_LIBROS.IdAutor, AUTORES_LIBROS.Autor, AUTORES_LIBROS.Resto, sum(AUTORES_LIBROS.Ejemplares) As Ejemplares FROM " +
							"((SELECT Autores.IdAutor, Autores.a AS Autor, Autores.b AS Resto, Count(Fondos_Autores.IdFondo) As Ejemplares " +
							"FROM Autores LEFT JOIN Fondos_Autores ON Autores.IdAutor = Fondos_Autores.IdAutor " +
							"GROUP BY Autores.IdAutor, Autores.a, Autores.b) " +
							"UNION " +
							"(SELECT Autores.IdAutor, Autores.a AS Autor, Autores.b AS Resto, Count(Fondos.IdFondo) As Ejemplares " +
							"FROM Autores LEFT JOIN Fondos ON Autores.IdAutor = Fondos.IdAutor " +
							"GROUP BY Autores.IdAutor, Autores.a, Autores.b)) AS AUTORES_LIBROS " +
							"GROUP BY AUTORES_LIBROS.IdAutor, AUTORES_LIBROS.Autor, AUTORES_LIBROS.Resto " +
							"ORDER BY Autor"
							);
			
			while (rs.next()) {
				AutorItem autor = new AutorItem(rs.getLong("IdAutor"), rs.getString("Autor"), rs.getString("Resto"), rs.getLong("Ejemplares"));
				autores.add(autor);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return autores;
	}
	
	public static AutorItem obtenerAutor(Long id) {
		LOG.info("Obteniendo autor con ID " + id);
		AutorItem autor = new AutorItem();
		try {
			ResultSet rs  = 
					Database.executeQuery(
							"SELECT AUTORES_LIBROS.IdAutor, AUTORES_LIBROS.Autor, AUTORES_LIBROS.Resto, sum(AUTORES_LIBROS.Ejemplares) As Ejemplares FROM " +
							"((SELECT Autores.IdAutor, Autores.a AS Autor, Autores.b AS Resto, Count(Fondos_Autores.IdFondo) As Ejemplares " +
							"FROM Autores LEFT JOIN Fondos_Autores ON Autores.IdAutor = Fondos_Autores.IdAutor " +
							"GROUP BY Autores.IdAutor, Autores.a, Autores.b) " +
							"UNION " +
							"(SELECT Autores.IdAutor, Autores.a AS Autor, Autores.b AS Resto, Count(Fondos.IdFondo) As Ejemplares " +
							"FROM Autores LEFT JOIN Fondos ON Autores.IdAutor = Fondos.IdAutor " +
							"GROUP BY Autores.IdAutor, Autores.a, Autores.b)) AS AUTORES_LIBROS " +
							"WHERE AUTORES_LIBROS.IdAutor=? " + 
							"GROUP BY AUTORES_LIBROS.IdAutor, AUTORES_LIBROS.Autor, AUTORES_LIBROS.Resto " +
							"ORDER BY Autor",
							id
							);
			
			if (rs.next()) {
				autor = new AutorItem(rs.getLong("IdAutor"), rs.getString("Autor"), rs.getString("Resto"), rs.getLong("Ejemplares"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return autor;
	}

	
	public static List<EditorialItem> listarEditoriales() {
		LOG.info("Listando todas las editoriales");
		List<EditorialItem> editoriales = new ArrayList<EditorialItem>();
		try {
			ResultSet rs  = 
					Database.executeQuery(
							"SELECT Editoriales.IdEditorial, Editoriales.Editorial, Count(Fondos.IdFondo) As Ejemplares " + 
							"FROM Editoriales LEFT JOIN Fondos ON Editoriales.IdEditorial=Fondos.IdEditorial " + 
							"GROUP BY Editoriales.IdEditorial, Editoriales.Editorial " + 
							"ORDER BY Editoriales.Editorial"
							);
			
			while (rs.next()) {
				EditorialItem editorial = new EditorialItem(rs.getLong("IdEditorial"), rs.getString("Editorial"), rs.getLong("Ejemplares"));
				editoriales.add(editorial);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return editoriales;
	}
	
	public static void combinarAutores(AutorItem principal, List<AutorItem> autores, ProgressListener listener) {
		autores.remove(principal);
		LOG.info("Combinando autores: principal=" + principal + " / todos=" + autores);
		for (int count = 0; count < autores.size(); count++) {
			AutorItem autor = autores.get(count);
			reemplazarAutorEnEjemplares(autor, principal);
			eliminarAutor(autor);
			listener.update(count, autores.size());
		}
	}
	
	public static void combinarEditoriales(EditorialItem principal, List<EditorialItem> editoriales, ProgressListener listener) {
		editoriales.remove(principal);		
		LOG.info("Combinando editoriales: principal=" + principal + " / todos=" + editoriales);
		for (int count = 0; count < editoriales.size(); count++) {
			EditorialItem editorial = editoriales.get(count);
			reemplazarEditorialEnEjemplares(editorial, principal);
			eliminarEditorial(editorial);
			listener.update(count, editoriales.size());
		}
	}
	
	public static void combinarAutoresDuplicados(ProgressListener listener) {
		LOG.info("Combinando autores duplicados");
		List<AutorItem> autores = listarAutores();
		int count = 0;
		while (!autores.isEmpty() && !listener.isCancelled()) {
			
			// saco el primer autor de la lista
			AutorItem principal = autores.remove(0);
			
			// buscar los que tienen el mismo nombre y sacarlos de la lista 
			List<AutorItem> duplicados = new ArrayList<>();
			for (AutorItem autor : autores) {
				String nombrePrincipal = principal.getNombre().toUpperCase().replaceAll("\\s*[,.\\s]\\s*", " ").trim();
				String nombreDuplicado = autor.getNombre().toUpperCase().replaceAll("\\s*[,.\\s]\\s*", " ").trim();
				if (nombrePrincipal.equalsIgnoreCase(nombreDuplicado)) {
					duplicados.add(autor);
				}
			}
			autores.removeAll(duplicados);
			
			// combino los autores
			if (!duplicados.isEmpty()) {
				combinarAutores(principal, duplicados, new ProgressListener() {
						public void update(long workDone, long max) {}
					});
			}
			
			listener.update(++count, autores.size());
			
		}
	}
	
	public static void combinarEditorialesDuplicadas(ProgressListener listener) {
		LOG.info("Combinando editoriales duplicadas");
		List<EditorialItem> editoriales = listarEditoriales();
		int count = 0;
		while (!editoriales.isEmpty() && !listener.isCancelled()) {

			// saco la primera editorial de la lista
			EditorialItem principal = editoriales.remove(0);
			
			// buscar los que tienen el mismo nombre y sacarlos de la lista 
			List<EditorialItem> duplicados = new ArrayList<>();
			for (EditorialItem editorial : editoriales) {
				String nombrePrincipal = principal.getNombre().toUpperCase().replaceAll("\\s*[,.\\s:\\-]\\s*", " ").trim();
				String nombreDuplicado = editorial.getNombre().toUpperCase().replaceAll("\\s*[,.\\s:\\-]\\s*", " ").trim();
				if (nombrePrincipal.equalsIgnoreCase(nombreDuplicado)) {
					duplicados.add(editorial);
				}
			}
			editoriales.removeAll(duplicados);
			
			// combino los autores
			if (!duplicados.isEmpty()) {
				combinarEditoriales(principal, duplicados, new ProgressListener() {
						public void update(long workDone, long max) {}
					});
			}

			listener.update(++count, editoriales.size());

		}
	}
	
	public static void reemplazarAutorEnEjemplares(AutorItem anteriorAutor, AutorItem nuevoAutor) {
		LOG.info("Remplazando autor en ejemplares: anterior=" + anteriorAutor + "/nuevo=" + nuevoAutor);
		try {
			Database.executeUpdate("update Fondos set IdAutor=? where IdAutor=?", nuevoAutor.getId(), anteriorAutor.getId());
			Database.executeUpdate("update Fondos_Autores set IdAutor=? where IdAutor=?", nuevoAutor.getId(), anteriorAutor.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void reemplazarEditorialEnEjemplares(EditorialItem anteriorEditorial, EditorialItem nuevaEditorial) {
		LOG.info("Remplazando editorial en ejemplares: anterior=" + anteriorEditorial + "/nueva=" + nuevaEditorial);
		try {
			Database.executeUpdate("update Fondos set IdEditorial=? where IdEditorial=?", nuevaEditorial.getId(), anteriorEditorial.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void eliminarAutor(AutorItem autor) {
		LOG.info("Eliminando autor: " + autor);
		try {
			Database.executeUpdate("delete from Autores where IdAutor = ?", autor.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public static void eliminarEditorial(EditorialItem editorial) {
		LOG.info("Eliminando editorial: " + editorial);
		try {
			Database.executeUpdate("delete from Editoriales where IdEditorial=?", editorial.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public static void actualizarAutor(AutorItem autor) {
		LOG.info("Actualizando autor: " + autor);
		try {
			Database.executeUpdate("update Autores set a=?, b=? where IdAutor=?", autor.getNombre(), autor.getResto(), autor.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public static void actualizarEditorial(EditorialItem editorial) {
		LOG.info("Actualizando editorial: " + editorial);
		try {
			Database.executeUpdate("update Editoriales set Editorial=? where IdEditorial=?", editorial.getNombre(), editorial.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public static void eliminarAutoresSinFondos(ProgressListener listener) {
		LOG.info("Eliminando autores sin fondos");
		List<AutorItem> autores = listarAutores();
		for (int count = 0; count < autores.size(); count++) {
			AutorItem autor = autores.get(count);
			if (autor.getEjemplares() <= 0) {
				eliminarAutor(autor);
			}
			listener.update(count, autores.size());
		}
	}
	
	public static void eliminarEditorialesSinFondos(ProgressListener listener) {
		LOG.info("Eliminando editoriales sin fondos");
		List<EditorialItem> editoriales = listarEditoriales();
		for (int count = 0; count < editoriales.size(); count++) {
			EditorialItem editorial = editoriales.get(count);
			if (editorial.getEjemplares() <= 0) {
				eliminarEditorial(editorial);
			}
			listener.update(count, editoriales.size());
		}
	}

	public static void invertirNombresAutores(List<AutorItem> autores, ProgressListener listener) {
		LOG.info("Invirtiendo nombres de autores");
		for (int count = 0; count < autores.size(); count++) {
			AutorItem autor = autores.get(count);
			String completo = autor.getNombre();
			int pos = completo.indexOf(" ");
			String nombre = completo.substring(0, pos).trim();
			String apellidos = completo.substring(pos).trim();
			autor.setNombre(apellidos + ", " + nombre);
			actualizarAutor(autor);
			listener.update(count, autores.size());
		}
	}
	
	public static void normalizarAutores(List<AutorItem> autores, boolean mayusculas, boolean capitales, boolean tildes, ProgressListener listener) {
		LOG.info("Normalizando nombres de los autores: cantidad=" + autores.size() + "/mayusculas=" + mayusculas + "/capitales=" + capitales + "/tildes=" + tildes);
		for (int count = 0; count < autores.size(); count++) {
			AutorItem autor = autores.get(count); 
			
			String nombre = autor.getNombre().trim();
			String resto = (autor.getResto() != null) ? autor.getResto().trim() : "";
			if (!resto.isEmpty()) {
				nombre = resto + " " + nombre;
			}
			if (mayusculas) {
				nombre = nombre.toUpperCase();
			} else if (capitales) {
				nombre = WordUtils.capitalizeFully(nombre);				
			} else {
				nombre = nombre.toLowerCase();
			}
			if (!tildes) {
				nombre = StringUtils.replaceChars(nombre, "ÁÉÍÓÚáéíóúÄËÏÖÜäëïöü", "AEIOUaeiouAEIOUaeiou");
			}
			nombre = nombre.replaceAll("\\s+", " ").replaceAll("\\s*,+\\s*", ", ");
			
			autor.setNombre(nombre);
			autor.setResto("");

			actualizarAutor(autor);
			
			listener.update(count, autores.size());
		}
	}

	public static void normalizarEditoriales(List<EditorialItem> editoriales, boolean mayusculas, boolean capitales, boolean tildes, ProgressListener listener) {
		LOG.info("Normalizando nombres de las editoriales: cantidad=" + editoriales.size() + "/mayusculas=" + mayusculas + "/capitales=" + capitales + "/tildes=" + tildes);
		for (int count = 0 ; count < editoriales.size() && !listener.isCancelled(); count++) {
			EditorialItem editorial = editoriales.get(count);

			String nombre = editorial.getNombre().trim();
			nombre = nombre.replaceFirst(":", "").trim();
			if (mayusculas) {
				nombre = nombre.toUpperCase();
			} else if (capitales) {
				nombre = WordUtils.capitalizeFully(nombre);				
			} else {
				nombre = nombre.toLowerCase();
			}
			if (!tildes) {
				nombre = StringUtils.replaceChars(nombre, "ÁÉÍÓÚáéíóúÄËÏÖÜäëïöü", "AEIOUaeiouAEIOUaeiou");
			}
			nombre = nombre.replaceAll("\\s+", " ").replaceAll("\\s*,+\\s*", ", ");
			editorial.setNombre(nombre);

			actualizarEditorial(editorial);
			
			listener.update(count, editoriales.size());

		}

	}

}
