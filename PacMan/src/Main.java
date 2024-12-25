import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        // Creating the game window
        JFrame frame = new JFrame("PacMan");
        //frame.setVisible(true); // Make the window visible
        frame.setSize(boardWidth, boardHeight);  //set the window size
        frame.setLocationRelativeTo(null);  // Center the window
        frame.setResizable(false);  // User can't resize the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Terminate the game when closing

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack(); // Adjusts the frame size to fit its content
        pacmanGame.requestFocus();
        frame.setVisible(true);
       
    }
}
