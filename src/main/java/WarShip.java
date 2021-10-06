import Utils.ConsoleColors;
import Utils.Leer;

import java.io.*;
import java.util.Properties;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author joange
 */
public class WarShip {

  /**
   * @param args the command line arguments
   */
  static int MAX_JUGADAS = 100;
  static boolean barcos = false;
  static boolean movimientos = false;

  private final Random r;
  private Board board;
  private WarShip ws;

  /**
   * @param barcos true Carga los barcos del archivo // false genera los barcos
   */
  public WarShip(boolean barcos) {
    cargarConfig(board);
    r = new Random(System.currentTimeMillis());
    board = new Board();
    board.initBoats(barcos);
  }

  public static void main(String[] args) {

    // Avisos Iniciales
    System.out.println(ConsoleColors.RED + "⚠️ SE RECOMIENDA EJECUTAR EL JAR EN UN TERMINAL DE UBUNTU PARA PODER VISUALIZAR CORRECTAMENTE TODO EL CONTENIDO ⚠️ \n\n" + ConsoleColors.RESET);
    System.out.println(ConsoleColors.YELLOW + "Puedes cargar los barcos y la última partida que hayas jugado.\nSi la opción está en verde, la carga está activada, si está rojo, no está activado y los generará aleatoriamente.\nSi no quieres cargar ningún perfil, selecciona la opción de " + ConsoleColors.YELLOW_BOLD + "CONTINUAR");
    System.out.println(ConsoleColors.RED + "⚠️ Para que la partida funcione correctamente hay que cargar los barcos de esa misma partida. ⚠️" + ConsoleColors.RESET);

    // Carga los archivos para luego comprobar si existen
    File bar = new File("boat_in.txt");
    File mov = new File("moviments_in.txt");

    int opc = 0;
    boolean menu = true;

    // Menu de seleccion
    do {
      do {
        System.out.println((barcos ? ConsoleColors.GREEN : ConsoleColors.RED) + "1. Cargar Barcos........");
        System.out.println((movimientos ? ConsoleColors.GREEN : ConsoleColors.RED) + "2. Cargar Movimientos...");
        System.out.println(ConsoleColors.YELLOW_BOLD + "3. Continuar............");
        opc = Leer.leerEntero(ConsoleColors.CYAN + "Selecciona una opción: " + ConsoleColors.RESET);
        ConsoleColors.CLEARCONSOLE();
      } while (opc < 1 || opc > 3);
      switch (opc) {
        case 1:  // Comprueba si existe el fichero de los barcos y en caso de que exista permite la carga // descarga de la importacion de los barcos
          if (bar.exists()) {
            barcos = !barcos;
          } else {
            System.out.println(ConsoleColors.YELLOW_BOLD + "\n\n==================================================================");
            System.out.println(ConsoleColors.RED_BOLD + "No se ha encontrado el archivo de los barcos, no se puede cargar.");
            System.out.println(ConsoleColors.YELLOW_BOLD + "==================================================================\n\n");
          }
          break;

        case 2: // Comprueba si existe el fichero de los movimientos y en caso de que exista permite la carga // descarga de la importacion de los movimientos
          if (mov.exists()) {
            movimientos = !movimientos;
          } else {
            System.out.println(ConsoleColors.YELLOW_BOLD + "\n\n=======================================================================");
            System.out.println(ConsoleColors.RED_BOLD + "No se ha encontrado el archivo de los movimientos, no se puede cargar.");
            System.out.println(ConsoleColors.YELLOW_BOLD + "======================================================================\n\n");
          }
          break;
        case 3: // Continua al menu de juego
          menu = false;
          break;
      }
    } while (menu);

    WarShip ws = new WarShip(barcos); // Crea un nuevo juego y le pasa -barcos- que indica si tiene que generar nuevos barcos o importarlos

    int opcio = 0;
    do {
      System.out.println(ConsoleColors.GREEN + "--    Escollir   --");
      System.out.println(ConsoleColors.GREEN + "1. Joc automàtic..." + (movimientos ? ConsoleColors.YELLOW + "  ⚡ Esta opción jugará la partida guardada" : ConsoleColors.YELLOW + "  \uD83D\uDD00 Esta opción jugará una partida aleatoria") + ConsoleColors.RESET); // Dependiendo de si ha activado los movimientos o no, muestra un mensaje
      System.out.println(ConsoleColors.GREEN + "2. Joc manual......");
      opcio = Leer.leerEntero(ConsoleColors.CYAN + "Indica el tipus de joc que vols: " + ConsoleColors.RESET);
      ConsoleColors.CLEARCONSOLE();
    } while (opcio < 1 || opcio > 2);

    switch (opcio) {
      case 1:
        ws.autoPlay(movimientos); // Si se cargan los movimientos, juega la partida con los movimientos cargados, si no estan cargados, juega aleatoriamente.
        break;
      case 2:
        ws.play(); // Juega normal
        break;
    }

    guardarConfig(ws.board); // Guarda la configuracion de la partida

  }

  /**
   * Guarda la configuracion de la partida
   *
   * @param board tablero
   */
  public static void guardarConfig(Board board) {
    Properties config = new Properties();
    config.setProperty("board_tam", String.valueOf(Board.BOARD_DIM));
    config.setProperty("num_boats", String.valueOf(Board.BOARD_BOATS_COUNT));
    config.setProperty("max_jugadas", String.valueOf(MAX_JUGADAS));
    try {
      config.store(new FileOutputStream("warship.properties"), "Warship config");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(ConsoleColors.GREEN + "Configuración guardada correctamente." + ConsoleColors.RESET);
  }

  /**
   * Carga la configuracion del tablero
   *
   * @param board tablero
   */
  public static void cargarConfig(Board board) {
    File f = new File("warship.properties");
    if (f.exists()) {
      Properties config = new Properties();
      try {
        config.load(new FileInputStream(f));
        Board.BOARD_DIM = Integer.parseInt(config.getProperty("board_tam"));
        Board.BOARD_BOATS_COUNT = Integer.parseInt(config.getProperty("num_boats"));
        MAX_JUGADAS = Integer.parseInt(config.getProperty("max_jugadas"));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println(ConsoleColors.RED + "El fichero " + ConsoleColors.YELLOW + "\"warship.properties\"" + ConsoleColors.RED + " no se ha encontrado.");
      System.out.println(ConsoleColors.GREEN + "Utilizando parámetros por defecto." + ConsoleColors.RESET);
    }
  }

  /**
   * Jugar automáticamente una partida
   *
   * @param cargarMovimientos Le dice al método si tiene que cargar los movimientos
   */
  private void autoPlay(boolean cargarMovimientos) {

    board.paint();

    if (cargarMovimientos) {
      FileReader fr = null;
      try {
        File f = new File("moviments_in.txt");
        if (!f.exists()) {
          System.out.println(ConsoleColors.RED + "El archivo de movimientos no existe.");
          System.out.println(ConsoleColors.YELLOW + "Generando partida automáticamente.");
          autoPlay(false); // Si ha seleccionado que se carguen los barcos y no existe el archivo los genera aleatoriamente.
        } else {
          fr = new FileReader(f);
          BufferedReader br = new BufferedReader(fr);

          while (br.ready()) { // Lee las jugadas
            String linea = br.readLine();
            String[] campos = linea.split(";");
            int jugada = Integer.parseInt(campos[0]);

            System.out.println(ConsoleColors.GREEN_BRIGHT + "JUGADA: " + (jugada + 1));

            int fila = Integer.parseInt(campos[1]);
            int columna = Integer.parseInt(campos[2]);

            if (board.shot(fila, columna) != Cell.CELL_WATER) {
              board.paint();
            } else {
              System.out.println("(" + fila + "," + columna + ") --> AGUA");
            }
          }
          System.out.printf("Fin de la partida cargada\n");
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          fr.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else { // Jugadas aleatorias
      for (int i = 1; i <= MAX_JUGADAS; i++) {
        System.out.println(ConsoleColors.GREEN_BRIGHT + "JUGADA: " + i);

        int fila, columna;
        do {
          fila = r.nextInt(Board.BOARD_DIM);
          columna = r.nextInt(Board.BOARD_DIM);
        } while (board.fired(fila, columna));

        if (board.shot(fila, columna) != Cell.CELL_WATER) {
          board.paint();
        } else {
          System.out.println("(" + fila + "," + columna + ") --> AGUA");
        }

        if (board.getEnd_Game()) {
          System.out.printf("Joc acabat amb %2d jugades\n", i);
          break;
        }
      }
    }
  }

  /**
   * Jugar una partida normal
   */
  private void play() {
    int num_jugadas = 0;
    boolean rendit = false;

    String jugada;
    int fila = -1, columna = -1;
    do {
      do {
        jugada = Leer.leerTexto("Dime la jugada en dos letras A3, B5... de A0 a J9: ").toUpperCase();
        if (jugada.equalsIgnoreCase("00")) {
          System.out.println("Jugador rendit");
          rendit = true;
          break;
        }
        if (jugada.length() == 0 || jugada.length() > 2) {
          System.out.println("Format incorrecte.");
          continue;
        }

        fila = jugada.charAt(0) - 'A';
        columna = jugada.charAt(1) - '0';

      } while (board.fired(fila, columna));

      // acaba el joc
      if (rendit) {
        break;
      }

      num_jugadas++;

      int boardShot = board.shot(fila, columna);

      if (boardShot == Cell.CELL_NOT_INITIALIZED) {
        System.out.println("(" + fila + "," + columna + ") --> FUERA DEL TABLERO");
      } else if (boardShot != Cell.CELL_WATER) {
        board.paintGame();
      } else {
        System.out.println("(" + fila + "," + columna + ") --> AGUA");
      }

      if (board.getEnd_Game()) {
        System.out.printf("Joc acabat amb %2d jugades\n", num_jugadas);
        break;
      }

    } while (num_jugadas < MAX_JUGADAS);

  }

}
