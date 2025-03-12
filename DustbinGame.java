
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class DustbinGame extends JFrame {
    // Game constants
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int DUSTBIN_WIDTH = 100;
    private static final int DUSTBIN_HEIGHT = 120;
    private static final int ITEM_SIZE = 50;
    private static final int DUSTBIN_SPEED = 12;
    private static final int MAX_LIVES = 3;
    
    // Game variables
    private int score = 0;
    private int lives = MAX_LIVES;
    private boolean gameOver = false;
    private Timer gameTimer;
    private Random random = new Random();
    
    // Game objects
    private int dustbinX;
    private ArrayList<Item> items = new ArrayList<>();
    
    // Arrays of image filenames
    private String[] goodItemImages = {
        "/images/apple.png",
        "/images/banana.png",
        "/images/carrot.png",
        "/images/tomato.png",
        "/images/broccoli.png"
    };
    
    private String[] badItemImages = {
        "/images/trash.png",
        "/images/plastic.png",
        "/images/cigarette.png",
        "/images/battery.png"
    };
    
    // Inner class for items
    private class Item {
        int x, y;
        boolean isGood;
        int speed;
        Image image;
        
        public Item(int x, int y, boolean isGood, int speed, Image image) {
            this.x = x;
            this.y = y;
            this.isGood = isGood;
            this.speed = speed;
            this.image = image;
        }
    }
    
    // Game panel
    private class GamePanel extends JPanel {
        private Image dustbinImage;
        private Image backgroundImage;
        private ArrayList<Image> goodImages = new ArrayList<>();
        private ArrayList<Image> badImages = new ArrayList<>();
        
        public GamePanel() {
            setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            setFocusable(true);
            
            // Load images
            try {
                // Load dustbin image
                dustbinImage = new ImageIcon(getClass().getResource("/images/dustbin.png")).getImage();
                
                // Load background
                backgroundImage = new ImageIcon(getClass().getResource("/images/background.png")).getImage();
                
                // Load good item images
                for (String filename : goodItemImages) {
                    Image img = new ImageIcon(getClass().getResource(filename)).getImage();
                    goodImages.add(img);
                }
                
                // Load bad item images
                for (String filename : badItemImages) {
                    Image img = new ImageIcon(getClass().getResource(filename)).getImage();
                    badImages.add(img);
                }
            } catch (Exception e) {
                // If images aren't available, we'll use colored shapes instead
                System.out.println("Image resources not found, using shapes instead. Error: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Key listener for dustbin movement
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (gameOver) {
                        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                            resetGame();
                        }
                        return;
                    }
                    
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        dustbinX = Math.max(0, dustbinX - DUSTBIN_SPEED);
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        dustbinX = Math.min(FRAME_WIDTH - DUSTBIN_WIDTH, dustbinX + DUSTBIN_SPEED);
                    }
                }
            });
        }
        
        // Get a random good image
        public Image getRandomGoodImage() {
            if (goodImages.isEmpty()) {
                return null;
            }
            return goodImages.get(random.nextInt(goodImages.size()));
        }
        
        // Get a random bad image
        public Image getRandomBadImage() {
            if (badImages.isEmpty()) {
                return null;
            }
            return badImages.get(random.nextInt(badImages.size()));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw background
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, null);
            } else {
                g2d.setColor(new Color(230, 230, 255));
                g2d.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
            }
            
            // Draw items
            for (Item item : items) {
                if (item.image != null) {
                    g2d.drawImage(item.image, item.x, item.y, ITEM_SIZE, ITEM_SIZE, null);
                } else {
                    // Fallback if image is missing
                    if (item.isGood) {
                        g2d.setColor(Color.GREEN);
                    } else {
                        g2d.setColor(Color.RED);
                    }
                    g2d.fillOval(item.x, item.y, ITEM_SIZE, ITEM_SIZE);
                }
            }
            
            // Draw dustbin
            if (dustbinImage != null) {
                g2d.drawImage(dustbinImage, dustbinX, FRAME_HEIGHT - DUSTBIN_HEIGHT, DUSTBIN_WIDTH, DUSTBIN_HEIGHT, null);
            } else {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(dustbinX, FRAME_HEIGHT - DUSTBIN_HEIGHT, DUSTBIN_WIDTH, DUSTBIN_HEIGHT);
            }
            
            // Draw score and lives
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Score: " + score, 20, 30);
            
            // Draw hearts for lives
            for (int i = 0; i < lives; i++) {
                g2d.setColor(Color.RED);
                g2d.fillOval(FRAME_WIDTH - 40 * (i + 1), 15, 25, 25);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(FRAME_WIDTH - 40 * (i + 1), 15, 25, 25);
            }
            
            // Draw game over message
            if (gameOver) {
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 50));
                g2d.drawString("GAME OVER", FRAME_WIDTH / 2 - 150, FRAME_HEIGHT / 2 - 20);
                g2d.setFont(new Font("Arial", Font.BOLD, 35));
                g2d.drawString("Final Score: " + score, FRAME_WIDTH / 2 - 120, FRAME_HEIGHT / 2 + 30);
                g2d.setFont(new Font("Arial", Font.PLAIN, 25));
                g2d.drawString("Press SPACE to Play Again", FRAME_WIDTH / 2 - 150, FRAME_HEIGHT / 2 + 80);
            }
        }
    }
    
    public DustbinGame() {
        setTitle("Dustbin Catcher Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        
        pack();
        setLocationRelativeTo(null);
        
        initGame(gamePanel);
    }
    
    private void initGame(GamePanel gamePanel) {
        // Initialize dustbin position
        dustbinX = FRAME_WIDTH / 2 - DUSTBIN_WIDTH / 2;
        
        // Start game timer
        gameTimer = new Timer(20, new ActionListener() {
            private int itemCounter = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) {
                    return;
                }
                
                // Create new items
                itemCounter++;
                if (itemCounter >= 50) {  // Create item every 50 ticks (approximately 1 second)
                    itemCounter = 0;
                    
                    int x = random.nextInt(FRAME_WIDTH - ITEM_SIZE);
                    boolean isGood = random.nextBoolean();
                    int speed = 2 + random.nextInt(4);  // Random speed between 2-5
                    
                    Image itemImage;
                    if (isGood) {
                        itemImage = gamePanel.getRandomGoodImage();
                    } else {
                        itemImage = gamePanel.getRandomBadImage();
                    }
                    
                    items.add(new Item(x, 0, isGood, speed, itemImage));
                }
                
                // Move items
                ArrayList<Item> itemsToRemove = new ArrayList<>();
                for (Item item : items) {
                    item.y += item.speed;
                    
                    // Check if item reached bottom
                    if (item.y > FRAME_HEIGHT) {
                        itemsToRemove.add(item);
                        continue;
                    }
                    
                    // Check collision with dustbin
                    if (item.y + ITEM_SIZE >= FRAME_HEIGHT - DUSTBIN_HEIGHT && 
                        item.x + ITEM_SIZE >= dustbinX && 
                        item.x <= dustbinX + DUSTBIN_WIDTH) {
                        
                        itemsToRemove.add(item);
                        
                        if (item.isGood) {
                            score += 10;
                            playSound("collect.wav"); // Play collection sound
                        } else {
                            lives--;
                            playSound("wrong.wav"); // Play wrong item sound
                            if (lives <= 0) {
                                gameOver = true;
                                playSound("gameover.wav"); // Play game over sound
                            }
                        }
                    }
                }
                
                // Remove processed items
                items.removeAll(itemsToRemove);
                
                // Redraw
                repaint();
            }
        });
        
        gameTimer.start();
    }
    
    // Method to play sounds
    private void playSound(String soundFile) {
        try {
            // This is a simple sound player that you could implement
            // For brevity, I'm just leaving this as a stub
            // System.out.println("Playing sound: " + soundFile);
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }
    
    private void resetGame() {
        score = 0;
        lives = MAX_LIVES;
        gameOver = false;
        items.clear();
        dustbinX = FRAME_WIDTH / 2 - DUSTBIN_WIDTH / 2;
        gameTimer.start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DustbinGame().setVisible(true);
        });
    }
}
