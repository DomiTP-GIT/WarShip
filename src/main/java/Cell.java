/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * @author joange
 */
public class Cell {
  // Contantes para controlar el contenido de la celda
  public static final int CELL_WATER = 0;
  public static final int CELL_BOAT = 1;
  public static final int CELL_TOUCH = 2;
  public static final int CELL_SUNKEN = 3;
  public static final int CELL_FIRED = 4;
  public static final int CELL_NOT_INITIALIZED = -1;

  // Constantes para controlar lo que ser verá por pantalla

  public static final String CELL_WATER_CHAR = "_";
  public static final String CELL_BOAT_CHAR = "X";
  public static final String CELL_TOUCH_CHAR = "T";
  public static final String CELL_SUNKEN_CHAR = "H";
  public static final String CELL_SUNKEN_FIRED = "o";

  // Inicializació de variabl privadas
  private int fila = 0;     // fila en la que se encuentra la celda
  private int columna = 0;  // Columna en la que se eunentra la celda
  private int contains = CELL_NOT_INITIALIZED; //Contenido de la celda
  private Boat boat; // Si la celda contiene parte de un bote ..

  public Cell(int fila, int columna) {
    // Al crear la celda inicializamos los valores....
    this.fila = fila;
    this.columna = columna;
    this.contains = CELL_WATER;
    boat = null;
  }

  // Se utilizará cuando caiga una bomba sobre la celda y esta contnega un bote
  public void setTouch() {
    this.contains = CELL_TOUCH;
  }

  // se utilizará cuando todas las celdas ocupadas por un barco estén tocadas
  public void setSunken() {
    this.contains = CELL_SUNKEN;
  }

  // se utilizará cuando todas las celdas ocupadas por un barco estén tocadas
  public void setFired() {
    this.contains = CELL_FIRED;
  }

  // Métodos para establecer/consultas las propiedades
  public Boat getBoat() {
    return boat;
  }

  public void setBoat(Boat boat) {
    // Si ponemos un bote en la celda cambiamos los valores de las propiedades
    this.boat = boat; // Puntero al objeto bote que contien la celda
    this.contains = CELL_BOAT;
  }

  public int getRow() {
    return this.fila;
  }

  public int getColumn() {
    return this.columna;
  }

  public int getContains() {
    return this.contains;
  }

  //Método para controlar el texto que aparecerá en pantalla
  public String getContainsString() {
    if (this.contains == CELL_WATER) return CELL_WATER_CHAR;
    if (this.contains == CELL_BOAT) return CELL_BOAT_CHAR;
    if (this.contains == CELL_TOUCH) return CELL_TOUCH_CHAR;
    if (this.contains == CELL_SUNKEN) return CELL_SUNKEN_CHAR;
    if (this.contains == CELL_FIRED) return CELL_SUNKEN_FIRED;
    return "E"; // Si devuelve E 'Error' no está inicializado
  }

  //Sobreescribe el método tostring de Object.
  @Override
  public String toString() {
    return "(" + this.fila + ", " + this.columna + ")  --> " + this.getContainsString();
  }

}
