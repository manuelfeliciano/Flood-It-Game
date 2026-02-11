import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;



//interface to make the cell size available to all classes
interface IConstant {
  int cellSize = 20;
}

//represents the cell class that implelemts the interface
class Cell implements IConstant {

  // coordinates of the cell
  int x;
  int y;
  //color of cell
  Color color;

  //flooded or not
  boolean flooded;

  // the four cells adjacent to this one
  //used for linkage/neighboring cells around
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // start of the game constructor
  Cell(int x, int y, Color color) {
    this(x, y, color, false, null, null, null, null);
  }

  // constructor that has all
  Cell(int x, int y, Color color, boolean flooded,
      Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }



  // updates scene
  // EFFECT: draws this cell onto the given WorldScene

  public void createCell(WorldScene scene) {
    RectangleImage cell = new RectangleImage(cellSize, cellSize, "solid", this.color);
    scene.placeImageXY(cell, this.x + 50, this.y + 50);
  }

  //EFFECT: updates links of this cell
  void neighboringCells(Cell top, Cell bottom, Cell left, Cell right) {
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }


  // Checks if this cell is flooded
  boolean isFlooded() {
    return this.flooded;
  }

  boolean contains(int x, int y) {
    return x > this.x - (cellSize / 2) + 50 && x <= this.x + (cellSize / 2) + 50
        && y > this.y - (cellSize / 2) + 50 && y <= this.y + (cellSize / 2) + 50;
  }




  // EFFECT: updates the flooded value of this cell if
  // this cell's color matches the given and one of its neighbors is flooded
  void updateFlooded(Color colorClicked) {
    boolean neighborFlooded = false; //neighbor is false, will change based on cell color matching
    if (!this.flooded) {
      if ((right != null && right.flooded) 
          || (left != null && left.flooded) 
          || (bottom != null && bottom.flooded) 
          || ((top != null && top.flooded))) {
        neighborFlooded = true;
      }
      if (neighborFlooded && this.color == colorClicked) {
        this.flooded = true; //updating flooded value
      }
    }
  }




  // adds this cell to queue, as well as it's right and bottom neighbors
  //EFFECT: modifies the workList with this cell and its neighbors
  void push(ArrayList<Cell> list) {
    boolean rightNull = (this.right == null);
    boolean bottomNull = (this.bottom == null);
    boolean leftNull = (this.left == null);
    boolean topNull = (this.top == null);

    this.addTo(list);

    //bottom
    if (!bottomNull) {
      this.bottom.addTo(list);
    }

    //right
    if (!rightNull) {
      this.right.addTo(list);
    }

    //top
    if (!topNull) {
      this.top.addTo(list);
    }

    //left
    if (!leftNull) {
      this.left.addTo(list);
    }

  }

  //EFFECT: adds this cell to workList if it is considered as flooded
  void addTo(ArrayList<Cell> list) {
    if (!list.contains(this) && this.flooded) {
      list.add(this);
    }
  }
}

// flood it game
class FloodItWorld extends World implements IConstant {
  // size of the board

  ArrayList<Cell> board;      // all cells in board
  ArrayList<Color> colors;    // all possible colors

  ArrayList<Cell> workList; //keeps track of the floods


  int boardSize;
  int amountOfColors;

  //used to help store the color of the cell the user clicks
  Color colorClicked;


  Random rand;
  int moves = 0; // The number of moves made by the player.
  int timer = 0; // The number of ticks that have passed.


  //max amount of clicks the user can do based on the amount of colors and its board size
  int maxMoves;
  private boolean gameWon;

  //USED FOR TESTING
  FloodItWorld(int amountOfColors, int boardSize, Random rand) {
    this.amountOfColors = amountOfColors;
    this.boardSize = boardSize;
    this.rand = rand;


    //creates the worList
    this.workList = new ArrayList<Cell>();



    this.boardSize = boardSize;

    this.board = new ArrayList<Cell>();

    //arraylist that stores the possible colors
    this.colors = new ArrayList<Color>();
    colors.add(Color.blue);
    colors.add(Color.red);
    colors.add(Color.green);
    colors.add(Color.orange);
    colors.add(Color.yellow);
    colors.add(Color.cyan);
    colors.add(Color.pink);






    //will generate left to right, up to down
    //of the amount of colors given exceeds the a
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        //too many colors chosen
        if (amountOfColors > colors.size()) {
          throw new RuntimeException("Pick less colors");
        }
        else {
          //randomly chooses a color to assign to the cell
          Color color = colors.get(rand.nextInt(amountOfColors));
          //creates a cell and then adds it to the board
          Cell tempCell = new Cell(j * Cell.cellSize ,
              i * Cell.cellSize, color, false, null, null, null, null);
          board.add(tempCell);
        }
      }
    }


    //this.colorClicked = this.board.get(0).color;
    //colorClicked becomes the color of the top leftmost cell
    this.colorClicked = this.board.get(0).color;
    this.neighboringCells();

    this.board.get(0).flooded = true;   // always starts with first cell being flooded
    this.updateFlooded();
    this.workList.add(this.board.get(0));
    //maximum amount of moves possible to win, given amount of colors
    maxMoves = (this.boardSize * (this.boardSize - 1)) / (2 * (this.amountOfColors));
    this.moves = 0; //tracks current amount of moves
  }

  // constructor for game and amount of colors you would like to represent (1-8)
  //can call for the randomness of seeds
  FloodItWorld(int amountOfColors, int boardSize) {
    this(amountOfColors,  boardSize, new Random());
  }


  //helps reset the game if R is clicked
  //EFFECT: modifies the board and resets the entire board back into 
  //initial position so that player can play again
  public void resetGame() {


    //rest is repeat of the constructors


    //creates the workList
    this.workList = new ArrayList<Cell>();

    //game board
    this.board = new ArrayList<Cell>();

    //arraylist that stores the possible colors
    this.colors = new ArrayList<Color>();
    colors.add(Color.blue);
    colors.add(Color.red);
    colors.add(Color.green);
    colors.add(Color.orange);
    colors.add(Color.yellow);
    colors.add(Color.cyan);
    colors.add(Color.pink);
    Collections.shuffle(colors); //randomizes the colors in the ArrayList





    //will generate left to right, up to down
    //of the amount of colors given exceeds the a
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        //too many colors chosen
        if (amountOfColors > colors.size()) {
          throw new RuntimeException("Pick less colors");
        }
        else {
          //generates a random color
          Color color = colors.get(rand.nextInt(amountOfColors));
          //creates a cell with the random color and then adds it to the board
          Cell tempCell = new Cell(j * Cell.cellSize ,
              i * Cell.cellSize, color, false, null, null, null, null);
          board.add(tempCell);
        }
      }
    }


    //this.colorClicked = this.board.get(0).color;
    //top left most cell color becomes colorClicked
    this.colorClicked = this.board.get(0).color;
    this.neighboringCells();

    this.board.get(0).flooded = true;   // always starts with first cell being flooded
    this.updateFlooded();
    this.workList.add(this.board.get(0));


  }


  public void resetGame(Random rand) {

    this.rand = rand;


    //rest is repeat of the constructors


    //creates the workList
    this.workList = new ArrayList<Cell>();

    //game board
    this.board = new ArrayList<Cell>();

    //arraylist that stores the possible colors
    this.colors = new ArrayList<Color>();
    colors.add(Color.blue);
    colors.add(Color.red);
    colors.add(Color.green);
    colors.add(Color.orange);
    colors.add(Color.yellow);
    colors.add(Color.cyan);
    colors.add(Color.pink);






    //will generate left to right, up to down
    //of the amount of colors given exceeds the a
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        //too many colors chosen
        if (amountOfColors > colors.size()) {
          throw new RuntimeException("Pick less colors");
        }
        else {
          //generates a random color
          Color color = colors.get(rand.nextInt(amountOfColors));
          //creates a cell with the random color and then adds it to the board
          Cell tempCell = new Cell(j * Cell.cellSize ,
              i * Cell.cellSize, color, false, null, null, null, null);
          board.add(tempCell);
        }
      }
    }


    //this.colorClicked = this.board.get(0).color;
    //top left most cell color becomes colorClicked
    this.colorClicked = this.board.get(0).color;
    this.neighboringCells();

    this.board.get(0).flooded = true;   // always starts with first cell being flooded
    this.updateFlooded();
    this.workList.add(this.board.get(0));


  }




  // EFFECT: modifies the top, bottom, left, and right of this links the cell
  public void neighboringCells() {
    Cell left;
    Cell right;
    Cell top;
    Cell bottom;
    for (int i = 0; i < board.size(); i++) {
      // check bottom is not null
      //incase there is no bottom cell
      if (i + boardSize >= board.size()) {
        bottom = null;
      }
      else {
        bottom = board.get(i + boardSize);
      }
      // check top is not null
      //incase there is no top cell
      if (i - boardSize < 0) {
        top = null;
      }
      else {
        top = board.get(i - boardSize);
      }

      // check left is not null
      //incase there is no left cell
      if (i % boardSize != 0) {
        left = board.get(i - 1);
      }
      else {
        left = null;
      }
      // check right is not null 
      //incase there is no right cell
      if (i % boardSize != boardSize - 1) {
        right = board.get(i + 1);
      }
      else {
        right = null;
      }

      board.get(i).neighboringCells(top, bottom, left, right);
    }
  }

  // draws scene and creates
  public WorldScene makeScene() {

    //creates blank world screen
    WorldImage blankScreen = new RectangleImage(1000,
        1000, OutlineMode.SOLID, Color.WHITE);

    //lose text
    WorldImage lose = new TextImage("You Lost", 30, FontStyle.BOLD, Color.RED);

    //retry text
    WorldImage retryAgainText = new TextImage("Press 'r' to Play Again", 20, 
        FontStyle.BOLD, Color.RED);

    //win text
    WorldImage win = new TextImage("You Win", 30, FontStyle.BOLD, Color.GREEN);

    //generates the actual board on the blank world screen
    WorldScene scene = new WorldScene((boardSize * Cell.cellSize) + 100,
        (boardSize * Cell.cellSize) + 100);

    //generates the board in a list
    for (int i = 0; i < board.size(); i++) {
      //places each new Cell into the list
      board.get(i).createCell(scene);
    }

    //starts gameWon as true
    boolean gameWon = true;

    //for each loop traverses through the board and if a single 
    //cell is not the color of colorClicked, the gameWon is not true
    //and the player has not won
    for (Cell c : this.board) {
      if (c.color != this.colorClicked) {
        gameWon = false;
      }
    }

    //when the game has not been won and the moves taken is greater than maximum moves
    if (!gameWon && this.moves >= maxMoves) {
      //places a blank screen over the game board
      scene.placeImageXY(blankScreen, ((boardSize ) / 2) * 4 , ((boardSize ) / 2 ) * 4);
      //places lose text onto blank screen
      scene.placeImageXY(lose, (boardSize * Cell.cellSize) / 2, (boardSize * Cell.cellSize) / 2);
      //places retry text onto blank screen as well
      scene.placeImageXY(retryAgainText, (boardSize * Cell.cellSize) / 2 + 40, 
          (boardSize * Cell.cellSize) / 2 + 40);



      // scene.placeImageXY(new TextImage("Press 'r' to Play Again", 10, FontStyle.BOLD, 
      // Color.BLACK), cellSize * 9, (30));
    }

    //when the game has been won
    if (gameWon) {
      //places a blank screen over the game board
      scene.placeImageXY(blankScreen, ((boardSize ) / 2) * 4 , ((boardSize ) / 2 ) * 4);
      //places win text onto blank screen
      scene.placeImageXY(win, (boardSize * Cell.cellSize) / 2, (boardSize * Cell.cellSize) / 2);

      // scene.placeImageXY(new TextImage("You Win!", 10, FontStyle.BOLD, Color.BLACK), cellSize * 
      // 9 + cellSize, (10));

    }
    //places flood it text onto the scene
    scene.placeImageXY(new TextImage("Flood It", 10, FontStyle.BOLD, Color.BLACK), cellSize * 5,
        (10));
    //  time text
    scene.placeImageXY(new TextImage("Time: " + this.timer + " seconds", 10, FontStyle.BOLD, 
        Color.BLACK), cellSize * 5, (cellSize * boardSize + (cellSize * 3)));
    //current moves and max moves 
    scene.placeImageXY(new TextImage("Moves: " + this.moves + "/" + this.maxMoves, 10, 
        FontStyle.BOLD, Color.BLACK), cellSize * 5, ((cellSize * boardSize + (cellSize * 4))));
    //    
    //    if(this.moves > this.maxMoves) {
    //      
    //    
    //      scene.placeImageXY(new TextImage("YOU LOSE", 100, FontStyle.BOLD, Color.BLACK), 175,
    //          (10));
    //    }

    return scene;




  }



  //resets the game and resets all mutated values
  // EFFECT: handles the reset of the game by changing data values and reseting values
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.resetGame();
      this.moves = 0;
      this.timer = 0;
      //turns = 0;
      // time = 0;

    }

  }


  //

  //add 100 to each x and y
  //use the cell size (20)


  //handles mouse clicking actions in the game
  // EFFECT: Modifies the game by updating the board with the clicked Cell
  public void onMouseClicked(Posn pos) {

    this.moves = this.moves + 1;
    for (Cell cell : this.board) {
      if (cell.contains(pos.x , pos.y )) {
        colorClicked = cell.color;
      }
    }
    //updating
    this.updateFlooded();
    this.updateWorkList();

    System.out.print("clicked! "); //prints to express the registered clicks

  }

  //EFFECT: update that tries to create the flooding effect
  public void update() {
    this.drawFlood();

  }

  //onTick method updates world as time passes
  //EFFECT: mutates data whenever a tick or unit of time has passed; is constantly 
  //updating with time
  public void onTick() {
    this.update();
    // this.timer++;
    this.drawFlood();
    this.updateFlooded();
    this.updateWorkList();

    //keeps timer changing 
    if (!((!gameWon && this.moves >= maxMoves)) || (gameWon && this.moves < maxMoves) ) {

      this.timer++;
    }

    if (gameWon) {
      return;
    }
  }






  //changes the color of flooded cells to the color clicked
  //EFFECT: modifies the workList with every cell in the column and row
  public void updateWorkList() {
    ArrayList<Cell> copy = new ArrayList<Cell>();
    ArrayList<Cell> neighbors = new ArrayList<Cell>();
    board.get(0).push(copy);

    //pushes the current cell
    for (int i = 0; i < board.size(); i++) {
      Cell b = board.get(i);
      b.push(copy);

      //pushing neigbors
      for (int j = 0; j < copy.size(); j++) {
        Cell c = copy.get(j);
        c.push(neighbors);
      }
    }

    for (int k = 0; k < neighbors.size(); k++) {
      Cell d = neighbors.get(k);
      workList.add(d);
    }
  }


  //EFFECT: updates if the cell is flooded based on if it's color
  // matches the given color
  public void updateFlooded() {
    for (int i = 0; i < board.size(); i++) {
      Cell cell = board.get(i);
      cell.updateFlooded(colorClicked);
      if (cell.flooded) {
        cell.color = colorClicked; //updates the color of the cell to flooding color
      }
    }
  }

  //removes the first element in the WorkList
  //EFFECT: modifies the workList by removing the first element and drawing the updated scene
  public void drawFlood() {
    if (workList.size() > 0) {
      workList.remove(0).createCell(makeScene());
    }
  }
}

// examples
class ExampleFloodItWorld {
  //example cells
  Cell cellExample1;
  Cell cellExample2;
  Cell cellExample3;
  Cell cellExample4;
  Cell cellExample5;
  Cell cellExample6;

  //example arraylists of cells and colors
  ArrayList<Cell> cells;
  ArrayList<Color> colors;


  WorldScene scene;
  Random rand;

  //creates a random example for testing
  FloodItWorld example = new FloodItWorld(2,  4, new Random(2));


  // initializes test cases
  void initCond() {
    cellExample1 = new Cell(0, 20, Color.RED);
    cellExample2 = new Cell(20, 40, Color.BLUE);
    cellExample3 = new Cell(40, 20, Color.BLUE);
    cellExample4 = new Cell(40, 40, Color.PINK);
    cellExample5 = new Cell(20, 0, Color.PINK);
    cellExample6 = new Cell(40, 0, Color.RED);
    // cellExample1 = new Cell(1, 2, Color.CYAN);

    cells = new ArrayList<Cell>();
    colors = new ArrayList<Color>();

    cells.add(cellExample1);
    cells.add(cellExample2);
    cells.add(cellExample3);
    cells.add(cellExample4);
    cells.add(cellExample5);
    cells.add(cellExample6);

    colors.add(Color.BLUE);
    colors.add(Color.YELLOW);
    colors.add(Color.RED);
    colors.add(Color.BLACK);
    colors.add(Color.PINK);

    example = new FloodItWorld(2,  4, new Random(2));

    scene = new WorldScene(1000, 1000);
    rand = new Random();



  }


  //HERE CREATE THE GAME BY TOGGLING THE NUMBERS
  //big bang
  void testBigBang(Tester t) {
    FloodItWorld game = new FloodItWorld(3, 20);//CAN CHANGE THE # OF COLORS HERE
    int worldWidth = game.boardSize * Cell.cellSize;
    int worldHeight = game.boardSize * Cell.cellSize;
    double tickRate = 1.0;

    game.bigBang(worldWidth + 100, worldHeight + 100, tickRate);




  }


  //tests createCell
  void testCreateCell(Tester t) {
    initCond();

    this.cellExample1.createCell(scene);
    this.cellExample2.createCell(scene);
    this.cellExample3.createCell(scene);
    this.cellExample4.createCell(scene);
    this.cellExample5.createCell(scene);
    this.cellExample6.createCell(scene);
    t.checkExpect(this.cellExample1, new Cell(0, 20, Color.RED));
    t.checkExpect(this.cellExample2, new Cell(20, 40, Color.BLUE));
    t.checkExpect(this.cellExample3, new Cell(40, 20, Color.BLUE));
    t.checkExpect(this.cellExample4, new Cell(40, 40, Color.PINK));
    t.checkExpect(this.cellExample5, new Cell(20, 0, Color.PINK));
    t.checkExpect(this.cellExample6, new Cell(40, 0, Color.RED));

  }


  //tests updating the neighboring cells for board
  void testNeighboringCells(Tester t) {
    this.initCond();


    //table so its easier to read the tests
    //tested ALL Cases (corners, middle, and middle sides)

    //     ---------------------
    //     | 00 | 01 | 02 | 03 |
    //     ---------------------
    //     | 04 | 05 | 06 | 07 |
    //     ---------------------
    //     | 08 | 09 | 10 | 11 |
    //     ---------------------
    //     | 12 | 13 | 14 | 15 |
    //     ---------------------


    // updateLinks called in the constructor for example
    // top left corner
    t.checkExpect(example.board.get(0).bottom, example.board.get(4));
    t.checkExpect(example.board.get(0).top, null);
    t.checkExpect(example.board.get(0).left, null);
    t.checkExpect(example.board.get(0).right, example.board.get(1));
    t.checkExpect(example.board.get(0).x, 0);
    t.checkExpect(example.board.get(0).y, 0);

    // next cell
    t.checkExpect(example.board.get(1).bottom, example.board.get(5));
    t.checkExpect(example.board.get(1).top, null);
    t.checkExpect(example.board.get(1).left, example.board.get(0));
    t.checkExpect(example.board.get(1).right, example.board.get(2));
    t.checkExpect(example.board.get(1).x, 20);
    t.checkExpect(example.board.get(1).y, 0);

    // bottom left corner
    t.checkExpect(example.board.get(12).bottom, null);
    t.checkExpect(example.board.get(12).top, example.board.get(8));
    t.checkExpect(example.board.get(12).left, null);
    t.checkExpect(example.board.get(12).right, example.board.get(13));
    t.checkExpect(example.board.get(12).x, 0);
    t.checkExpect(example.board.get(12).y, 60);

    // middle left 
    t.checkExpect(example.board.get(8).bottom, example.board.get(12));
    t.checkExpect(example.board.get(8).top, example.board.get(4));
    t.checkExpect(example.board.get(8).left, null);
    t.checkExpect(example.board.get(8).right, example.board.get(9));
    t.checkExpect(example.board.get(8).x, 0);
    t.checkExpect(example.board.get(8).y, 40);

    // middle right 
    t.checkExpect(example.board.get(11).bottom, example.board.get(15));
    t.checkExpect(example.board.get(11).top, example.board.get(7));
    t.checkExpect(example.board.get(11).left, example.board.get(10));
    t.checkExpect(example.board.get(11).right, null);
    t.checkExpect(example.board.get(11).x, 60);
    t.checkExpect(example.board.get(11).y, 40);

    // middle top 
    t.checkExpect(example.board.get(2).bottom, example.board.get(6));
    t.checkExpect(example.board.get(2).top, null);
    t.checkExpect(example.board.get(2).left, example.board.get(1));
    t.checkExpect(example.board.get(2).right, example.board.get(3));
    t.checkExpect(example.board.get(2).x, 40);
    t.checkExpect(example.board.get(2).y, 0);

    // middle bottom 
    t.checkExpect(example.board.get(13).bottom, null);
    t.checkExpect(example.board.get(13).top, example.board.get(9));
    t.checkExpect(example.board.get(13).left, example.board.get(12));
    t.checkExpect(example.board.get(13).right, example.board.get(14));
    t.checkExpect(example.board.get(13).x, 20);
    t.checkExpect(example.board.get(13).y, 60);

    // bottom right
    t.checkExpect(example.board.get(15).bottom, null);
    t.checkExpect(example.board.get(15).top, example.board.get(11));
    t.checkExpect(example.board.get(15).left, example.board.get(14));
    t.checkExpect(example.board.get(15).right, null);
    t.checkExpect(example.board.get(15).x, 60);
    t.checkExpect(example.board.get(15).y, 60);

    // middle
    t.checkExpect(example.board.get(9).bottom, example.board.get(13));
    t.checkExpect(example.board.get(9).top, example.board.get(5));
    t.checkExpect(example.board.get(9).left, example.board.get(8));
    t.checkExpect(example.board.get(9).right, example.board.get(10));
    t.checkExpect(example.board.get(9).x, 20);
    t.checkExpect(example.board.get(9).y, 40);


  }

  //tests updateneighbors() for cells
  void testUpdateNeighboringCells(Tester t) {
    initCond();
    cellExample1.neighboringCells(cellExample4, cellExample1, cellExample3, cellExample2);
    t.checkExpect(cellExample1.left, cellExample3);
    t.checkExpect(cellExample1.top, cellExample4);
    t.checkExpect(cellExample2.right, null);
    t.checkExpect(cellExample4.bottom, null);

    cellExample2.neighboringCells(cellExample1, cellExample2, cellExample4, cellExample3);
    t.checkExpect(cellExample2.left, cellExample4);
    t.checkExpect(cellExample2.top, cellExample1);
    t.checkExpect(cellExample2.right, cellExample3);
    t.checkExpect(cellExample2.bottom, cellExample2);
  }

  //tests makeScene()
  void testmakeScene(Tester t) {
    initCond();

    FloodItWorld game1 = new FloodItWorld(2, 3, new Random(2));//, USED FOR TESTING

    WorldScene expectedScene = new WorldScene(160, 160);

    //places all the images as a result of placing the cells
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize ,
        Cell.cellSize ,
        "solid",
        Color.RED), 50, 50);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize ,
        Cell.cellSize ,
        "solid",
        Color.BLUE), 70, 50);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize ,
        Cell.cellSize ,
        "solid",
        Color.RED), 90, 50);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize ,
        Cell.cellSize ,
        "solid",
        Color.BLUE), 50, 70);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize,
        Cell.cellSize,
        "solid",
        Color.BLUE), 70, 70);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize,
        Cell.cellSize,
        "solid",
        Color.RED), 90, 70);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize,
        Cell.cellSize,
        "solid",
        Color.RED), 50, 90);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize,
        Cell.cellSize,
        "solid",
        Color.BLUE), 70, 90);
    expectedScene.placeImageXY(new RectangleImage(Cell.cellSize,
        Cell.cellSize,
        "solid",
        Color.RED), 90, 90);


    expectedScene.placeImageXY(new TextImage("Flood It", 10, FontStyle.BOLD, Color.BLACK), 100,
        (10));

    expectedScene.placeImageXY(new TextImage("Time: 0 seconds", 10, FontStyle.BOLD, Color.BLACK),
        100, (120));

    expectedScene.placeImageXY(new TextImage("Moves: 0/1", 10, FontStyle.BOLD, Color.BLACK), 100,
        (140));

    //      expectedScene.placeImageXY(new TextImage(Cell.cellSize,
    //          Cell.cellSize,
    //          "solid",
    //          Color.RED), 100, 10);


    t.checkExpect(game1.makeScene(), expectedScene);




  }




  //tests the exceptions we created if user exceeds the color size
  void testExceptions(Tester t) {
    initCond();
    t.checkConstructorException(new RuntimeException("Pick less colors"), 
        "FloodItWorld", 
        8, 
        10);
    t.checkConstructorException(new RuntimeException("Pick less colors"), 
        "FloodItWorld", 
        9, 
        10);
    t.checkConstructorException(new RuntimeException("Pick less colors"), 
        "FloodItWorld", 
        10, 
        10);

  }

  //tests push
  void testPush(Tester t) {
    this.initCond();
    t.checkExpect(this.example.workList.size(), 1);
    this.cellExample1.push(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);

    this.cellExample2.push(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);

    this.cellExample4.push(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);

    this.cellExample3.push(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);

  }

  //tests addTo
  void testAddTo(Tester t) {
    this.initCond();
    t.checkExpect(this.example.workList.size(), 1);
    this.cellExample1.addTo(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);

    this.cellExample2.addTo(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);

    this.cellExample3.addTo(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);

    this.cellExample4.addTo(this.example.workList);
    t.checkExpect(this.example.workList.size(), 1);
  }

  //tests updateworkList
  void testUpdateWorkList(Tester t) {
    this.initCond();

    t.checkExpect(this.example.workList.size(), 1);
    this.example.updateWorkList();
    t.checkExpect(this.example.workList.size(), 2);

    // not change because nothing is flooded yet
    t.checkExpect(this.example.workList.size(), 2);
    this.example.updateWorkList();
    t.checkExpect(this.example.workList.size(), 3);
    this.example.updateWorkList();
    t.checkExpect(this.example.workList.size(), 4);

  }

  //tests drawFlood
  void testDrawFlood(Tester t) {
    this.initCond();

    t.checkExpect(this.example.workList.size(), 1);
    this.example.drawFlood();
    t.checkExpect(this.example.workList.size(), 0);


  }



  //tests resetGame
  void testResetGame(Tester t) {
    this.initCond();
    Random rand1 = new Random(2);
    //this is only constructed with 4 colors and 4 tiles
    t.checkExpect(this.example.board.size(), 16);
    t.checkExpect(this.example.colors.size(), 7);
    //when it is reset it is a full board with 484 tiles and 6 colors
    this.example.resetGame(rand1);
    t.checkExpect(this.example.board.size(), 16);
    t.checkExpect(this.example.colors.size(), 7);

    // check colors different
    t.checkExpect(example.board.get(0).color, Color.RED);
    t.checkExpect(example.board.get(1).color, Color.BLUE);
    t.checkExpect(example.board.get(2).color, Color.RED);
    t.checkExpect(example.board.get(3).color, Color.BLUE);
    this.example.resetGame(new Random(5));
    t.checkExpect(example.board.get(0).color, Color.RED);
    t.checkExpect(example.board.get(1).color, Color.BLUE);
    t.checkExpect(example.board.get(2).color, Color.BLUE);
    t.checkExpect(example.board.get(3).color, Color.RED);

    //this is the game board so it starts with 484 tiles and 6 possible colors
    t.checkExpect(this.example.board.size(), 16);
    t.checkExpect(this.example.colors.size(), 7);
    //when it is reset it is a full board with 484 tiles and 6 colors
    this.example.resetGame();
    t.checkExpect(this.example.board.size(), 16);
    t.checkExpect(this.example.colors.size(), 7);
  }

  // tests contains()
  void testContains(Tester t) {
    initCond();
    t.checkExpect(cellExample1.contains(20,  20), false); // middle
    t.checkExpect(cellExample1.contains(10, 20), false); // x left bound exclude
    t.checkExpect(cellExample1.contains(11, 20), false); // x left bound include
    t.checkExpect(cellExample1.contains(30, 20), false); // x right bound
    t.checkExpect(cellExample1.contains(20, 10), false); // y top bound include
    t.checkExpect(cellExample1.contains(20, 11), false); // y left bound exclude
    t.checkExpect(cellExample1.contains(20, 30), false); // y bottom bound
    t.checkExpect(example.board.get(0).contains(20, 0),false);
    t.checkExpect(example.board.get(0).contains(20, 20),false);
  }


  // tests onMouseClick
  void testOnMouseClick(Tester t) {
    initCond();

    example.onMouseClicked(new Posn(20, 20)); // clicked middle of C1
    t.checkExpect(example.colorClicked, Color.RED);
    t.checkExpect(cellExample1.flooded, false);
    t.checkExpect(cellExample1.color, Color.RED);
    t.checkExpect(!cellExample1.flooded && !cellExample3.flooded && !cellExample4.flooded, true);

  }

  // tests onKeyEvent
  void testOnKeyEvent(Tester t) {
    this.initCond();


    this.example.onKeyEvent("d"); //not a valid key
    //  t.checkExpect(this.example.makeScene(), false);

    this.example.onKeyEvent("r"); //resets
    //  t.checkExpect(this.example.makeScene(), true);

    this.example.onKeyEvent("enter");
    // t.checkExpect(this.example.makeScene(), false);


  }
}
