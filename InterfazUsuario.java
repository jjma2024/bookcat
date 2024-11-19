package com.catalogolibros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;

public class InterfazUsuario {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BaseDeDatos.crearTabla();  // Crear la tabla de la base de datos

        System.out.println("Bienvenido al Catálogo de Libros");
        boolean salir = false;

        while (!salir) {
            System.out.println("\n1. Buscar libros por título");
            System.out.println("2. Buscar libros por autor");
            System.out.println("3. Buscar libros por idioma");
            System.out.println("4. Ver todos los libros");
            System.out.println("5. Salir");
            System.out.print("Selecciona una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    System.out.print("Introduce el título del libro: ");
                    String titulo = scanner.nextLine();
                    buscarLibros(titulo);
                    break;
                case 2:
                    System.out.print("Introduce el autor del libro: ");
                    String autor = scanner.nextLine();
                    buscarLibros(autor);
                    break;
                case 3:
                    System.out.print("Introduce el idioma: ");
                    String idioma = scanner.nextLine();
                    buscarLibros(idioma);
                    break;
                case 4:
                    mostrarTodosLosLibros();
                    break;
                case 5:
                    salir = true;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void buscarLibros(String query) {
        try {
            String response = ApiGutendex.obtenerLibros(query);
            JsonNode books = ApiGutendex.parsearRespuesta(response).path("results");

            if (books.size() == 0) {
                System.out.println("No se encontraron libros.");
            } else {
                for (JsonNode book : books) {
                    String titulo = book.path("title").asText();
                    String autor = book.path("authors").get(0).path("name").asText();
                    String idiomas = book.path("languages").asText();
                    System.out.println("Título: " + titulo);
                    System.out.println("Autor: " + autor);
                    System.out.println("Idiomas: " + idiomas);
                    BaseDeDatos.insertarLibro(titulo, autor, idiomas);  // Insertar en la base de datos
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar libros: " + e.getMessage());
        }
    }

    private static void mostrarTodosLosLibros() {
        try {
            String sql = "SELECT * FROM libros";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catalogo_libros.db");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Título: " + rs.getString("titulo"));
                    System.out.println("Autor: " + rs.getString("autor"));
                    System.out.println("Idiomas: " + rs.getString("idiomas"));
                    System.out.println("----");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al mostrar los libros: " + e.getMessage());
        }
    }
}
