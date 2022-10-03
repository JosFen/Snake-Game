import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_W = 600;
    static final int SCREEN_H = 600;
    static final int GRID_SIZE = 30;
    static final int GAME_UNITS = SCREEN_W * SCREEN_H / GRID_SIZE;
    static final int DELAY = 150;
    static final int INITIAL_SNAKE_LENGTH = 2;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int snakeBodyLength = INITIAL_SNAKE_LENGTH;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean isRunning = false;
    Timer timer;
    Random random;
    JButton restartBtn;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
        this.setBackground(new Color(141, 197, 134));
        this.setFocusable(true);
        this.addKeyListener(new changeDirection());

        // restart button
        restartBtn = new JButton("Restart");
        restartBtn.setBounds(540, 2, 50, 20);
        restartBtn.setFont(new Font("Courier", Font.BOLD, 10));
        this.setLayout(null);
        this.add(restartBtn);
        restartBtn.setLocation(540, 2);
        restartBtn.addActionListener(this);
        restartBtn.addKeyListener(new changeDirection());

        start();
    }

    // start game
    public void start() {
        createApple();
        isRunning = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    public void drawGame(Graphics g) {
        // draw game panel
        if (isRunning) {
//        for (int i = 0; i < SCREEN_H / GRID_SIZE; i++) {
//            g.drawLine(i * GRID_SIZE, 0, i * GRID_SIZE, SCREEN_H);
//            g.drawLine(0, i * GRID_SIZE, SCREEN_W, i * GRID_SIZE);
//        }
            // draw apple
            g.setColor(new Color(247, 81, 81));
            g.fillOval(appleX, appleY, GRID_SIZE, GRID_SIZE);

            //draw mosaic-color snake
            for (int i = 0; i < snakeBodyLength; i++) {
                if (i == 0) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                }
                g.fillRect(x[i], y[i], GRID_SIZE, GRID_SIZE);
            }

            //draw score
            drawScore(g);
        } else {
            gameOver(g);
        }
    }

    // draw score earned
    public void drawScore(Graphics g) {
        //g.setColor(new Color(0xB94949));
        g.setColor(Color.darkGray);
        g.setFont(new Font("Courier", Font.BOLD, 15));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_W - metrics.stringWidth("GAME OVER!")) / 2, g.getFont().getSize());

    }

    public void createApple() {
        appleX = random.nextInt(SCREEN_W / GRID_SIZE) * GRID_SIZE;
        appleY = random.nextInt(SCREEN_H / GRID_SIZE) * GRID_SIZE;
    }

    public void move() {
        for (int i = snakeBodyLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - GRID_SIZE;
                break;
            case 'D':
                y[0] = y[0] + GRID_SIZE;
                break;
            case 'R':
                x[0] = x[0] + GRID_SIZE;
                break;
            case 'L':
                x[0] = x[0] - GRID_SIZE;
                break;
        }
    }

    public void checkEaten() {
        if (x[0] == appleX && y[0] == appleY) {
            snakeBodyLength++;
            applesEaten++;
            createApple();
        }
    }

    public void checkCollision() {
        // head collides with body:
        for (int i = snakeBodyLength; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                isRunning = false;
                break;
            }
        }

        // collides with boundaries
        if ((x[0] < 0) || (x[0] > SCREEN_W) || (y[0] < 0) || (y[0] > SCREEN_H)) {
            isRunning = false;
        }

        // stop if not running
        if (!isRunning) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        drawScore(g);
        // draw message
        g.setColor(new Color(0xB94949));
        g.setFont(new Font("Courier", Font.BOLD, 60));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("GAME OVER!", (SCREEN_W - metrics.stringWidth("GAME OVER!")) / 2, SCREEN_H / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            move();
            checkEaten();
            checkCollision();
        }
        repaint();

        if (e.getSource() == restartBtn) {
            reset();
            isRunning = true;
            start();
        }

    }

    // reset to initial setting
    public void reset() {
        isRunning = true;
        x[0] = 0;
        y[0] = 0;
        snakeBodyLength = INITIAL_SNAKE_LENGTH;
        applesEaten = 0;
        direction = 'R';
    }

    public class changeDirection extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
