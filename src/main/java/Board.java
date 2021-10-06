import Utils.ConsoleColors;

import java.io.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * @author joange
 */
public class Board {

  // Constantes del tablero
  public static int BOARD_DIM = 10;
  public static int BOARD_BOATS_COUNT = 5;
  private static int jugada = 0;
  private static boolean inicio = false;

  // Propiedades de la clase
  private final Cell[][] cells;
  private final int[] dimensiones = {5, 4, 3, 2, 2};
  private Boat[] boats;
  private boolean end_game;

  public Board() {
    // Crea la matriz de celdas del tablero
    cells = new Cell[BOARD_DIM][BOARD_DIM];
    // inicializa la matriz a agua
    for (int i = 0; i < BOARD_DIM; i++) {
      for (int j = 0; j < BOARD_DIM; j++) {
        cells[i][j] = new Cell(i, j);
      }
    }

    end_game = false;
  }

  /**
   * Guarda las jugadas
   *
   * @param numMov  Número de movimiento
   * @param fila    Fila
   * @param columna Columna
   * @param estado  Estado de la celda después de ser disparada
   */
  public static void guardarJugadas(int numMov, int fila, int columna, int estado) {

    int res = 0;

    // Calcular el resultado de la jugada
    switch (estado) {
      case 0:
        res = 0;
        break;
      case 3:
        res = 2;
        break;
      case 2:
        res = 1;
        break;
      case -1:
        res = 3;
        break;
    }

    jugada++; // Aumenta la jugada

    // Escribir los movimientos
    FileWriter fw = null;

    try {
      File f = new File("moviments_out.txt");
      fw = new FileWriter(f, inicio); // La primera vez inicio es False por lo que sobreescribirá el archivo para que no esté la partida anterior, luego sigue añadiendo.
      inicio = true;
      BufferedWriter bw = new BufferedWriter(fw);

      bw.write(numMov + ";" + fila + ";" + columna + ";" + res + "\n");
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // comprueba si ha acabado el juego
  private void testEnd() {
    for (Boat boat : boats) {
      if (boat.getBoatState() != Boat.BOAT_SUNKEN)
        return;
    }
    end_game = true;
  }

  public boolean getEnd_Game() {
    return end_game;
  }

  // Crea los barcos y los posiciona
  public void initBoats(boolean cargar) {
    boats = new Boat[BOARD_BOATS_COUNT];

    if (cargar) { // Carga los barcos o los genera
      FileReader fr = null;
      try {
        File f = new File("boat_in.txt");
        if (!f.exists()) {
          System.out.println(ConsoleColors.RED + "El archivo de barcos no existe.");
          System.out.println(ConsoleColors.YELLOW + "Generando barcos automáticamente.");
          initBoats(false); // Si ha seleccionado que se carguen los barcos y no existe el archivo los genera aleatoriamente.
        } else {
          fr = new FileReader(f);
          BufferedReader br = new BufferedReader(fr);

          while (br.ready()) {
            String linea = br.readLine();
            String[] campos = linea.split(";");
            boats[Integer.parseInt(campos[0])] = new Boat();
            boats[Integer.parseInt(campos[0])].setBoat(Integer.parseInt(campos[1]), this, Integer.parseInt(campos[0]), Integer.parseInt(campos[3]), Integer.parseInt(campos[4]), Integer.parseInt(campos[2]));
            boats[Integer.parseInt(campos[0])].viewCells();
          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      for (int i = 0; i < BOARD_BOATS_COUNT; i++) {
        boats[i] = new Boat();
        boats[i].setBoat(dimensiones[i], this, i); // Le paso "i" como ID.
        boats[i].viewCells(); //Muestra por pantalla la posición que ocupan
      }
    }
  }

  //Devuelve el objeto Cell que ocupa una fila y coulmna
  public Cell getCell(int fila, int columna) {
    return cells[fila][columna];
  }

  //Devuelve un valor válido dentro del tablero
  public int fitValueToBoard(int value) {
    if (value <= 0) return 0;
    if (value > BOARD_DIM - 1) return BOARD_DIM - 1;
    return value;
  }

  //El jugador lanza una bomba sobre el tablero
  public int shot(int fila, int columna) {
    System.out.print(ConsoleColors.PURPLE + "---- ");

    //Sacamos el objeto Boat que hay en la celda bombardeada
    if (fila > BOARD_DIM || columna > BOARD_DIM) {
      guardarJugadas(jugada, fila, columna, Cell.CELL_NOT_INITIALIZED);
      return Cell.CELL_NOT_INITIALIZED;
    } else {
      Boat boat = cells[fila][columna].getBoat();
      if (boat != null) {
        //Si en la celda hay un barco, llamamos a su método touch (tocado)
        boat.touchBoat(fila, columna);
        testEnd();

      } else { // marco la casilla como disparada
        cells[fila][columna].setFired();
      }
      System.out.print(ConsoleColors.GREEN + " [" + fila + "], [" + columna + "] --> " +
          cells[fila][columna].getContainsString());
      System.out.println(ConsoleColors.PURPLE + " ----" + ConsoleColors.RESET);

      // Guarda cada jugada
      guardarJugadas(jugada, fila, columna, cells[fila][columna].getContains());

      return cells[fila][columna].getContains();
    }
  }

  // indica si una cel·la ha estat o no disparada, per no repetir la jugada
  public boolean fired(int fila, int columna) {
    // Comprobar que no se encuentre fuera de los límites

    /**
     * Si le das a false, se repite la jugada
     * Si se repite la jugada te saca otro número aleatorio
     * y se va a repetir hasta que salga uno correcto**/
    if (fila > BOARD_DIM || columna > BOARD_DIM) {
      return false;
    }
    return cells[fila][columna].getContains() != Cell.CELL_WATER &&
        cells[fila][columna].getContains() != Cell.CELL_BOAT;

  }

  // Para mostrar el tablero por pantalla
  public void paint() {
    // Cabecera ...
    System.out.print("      ");
    for (int k = 0; k < Board.BOARD_DIM; k++) {
      System.out.print(ConsoleColors.BLUE + k + " ");
    }
    System.out.println();
    char c = 'A';
    for (int i = 0; i < Board.BOARD_DIM; i++) {
      System.out.print((ConsoleColors.BLUE + c++) + " <-- " + ConsoleColors.RESET);
      for (int j = 0; j < Board.BOARD_DIM; j++) {
        System.out.print(cells[i][j].getContainsString() + " ");
      }
      System.out.println(ConsoleColors.BLUE + " -->");
    }

    System.out.print("      ");
    for (int k = 0; k < Board.BOARD_DIM; k++) {
      System.out.print(ConsoleColors.BLUE + k + " ");
    }
    System.out.println(ConsoleColors.RESET);
  }

  // Para mostrar el tablero por pantalla durante el juego
  // (sin mostrar los barcos)
  public void paintGame() {
    // Cabecera ...
    System.out.print("  <-- ");
    for (int k = 0; k < Board.BOARD_DIM; k++) {
      System.out.print(k + " ");
    }
    System.out.println(" -->");
    char c = 'A';
    for (int i = 0; i < Board.BOARD_DIM; i++) {
      System.out.print((c++) + " <-- ");
      for (int j = 0; j < Board.BOARD_DIM; j++) {
        if (cells[i][j].getContainsString() == Cell.CELL_BOAT_CHAR)
          System.out.print("_ ");
        else
          System.out.print(cells[i][j].getContainsString() + " ");
      }
      System.out.println(" -->");
    }
  }
}
