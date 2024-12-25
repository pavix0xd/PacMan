import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener { 

    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';  // U = up, D = down, L = left, R = right
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection (char direction){
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls){
                if (collision(this, wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L'){
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R'){
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;

    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameloop;

    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    int score = 0;  //score
    int lives = 3;  //pacman has default 3 lives
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load imgs
        wallImage = new ImageIcon(getClass().getResource("./assets/wall.png")).getImage();

        blueGhostImage = new ImageIcon(getClass().getResource("./assets/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./assets/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./assets/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./assets/redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./assets/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./assets/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./assets/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./assets/pacmanRight.png")).getImage();

        loadMap();

        for (Block ghost : ghosts){  //moving ghosts randomly
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        //how long it takes the timer to start, milliseconds between frames
        gameloop = new Timer(50, this); //20 fps
        gameloop.start();
    }

    public void loadMap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++){
            for (int c = 0; c < columnCount; c++){
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X'){ // wall block
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b'){ //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o'){ //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p'){ //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r'){ //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P'){
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' '){
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent (Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw (Graphics g){
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        //score
        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        if (gameOver){
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collisions
        for (Block wall : walls){
            if (collision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collisions
        for (Block ghost : ghosts){
            if (collision(ghost, pacman)){
                lives -= 1;
                if (lives == 0){
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls){
                if (collision(ghost, wall)){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
            
            // teleport if the ghost go off boundaries
            if (ghost.x < 0){
                ghost.x = boardWidth - ghost.width;
            }
            else if (ghost.x + ghost.width > boardWidth){
                ghost.x = 0;
            }
            if (ghost.y < 0){
                ghost.y = boardHeight - ghost.height;
            }
            else if (ghost.y + ghost.height > boardHeight){
                ghost.y = 0;
            }
        }

        //check food collision
        Block foodEaten = null;
        for (Block food : foods){
            if (collision(pacman, food)){
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()){
            loadMap();
            resetPositions();
        }
    }

    public boolean collision (Block a, Block b){ //collision detection 
        return  a.x < b.x + b.width &&
                a.x + b.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions(){
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            gameloop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver){
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameloop.start();
        }
        //System.out.println("key event " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U'){
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D'){
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L'){
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R'){
            pacman.image = pacmanRightImage;
        }
    }

}