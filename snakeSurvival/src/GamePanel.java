import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 80;

    final int heroX[] = new int[GAME_UNITS];
    final int heroY[] = new int[GAME_UNITS];

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int count = 0;
    final static int HERO_BODY = 4;

    int snakeBoost = 0;
    int bodyParts = 6;
    int health = 50;
    int score = 0;
    int appleX;
    int appleY;
    char heroDirection = 'D';
    char snakeDirection = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    Random randSwitch = new Random();


    public GamePanel() {

        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        heroX[0] = SCREEN_WIDTH / 2;
        heroY[0] = SCREEN_HEIGHT / 4;
        startGame();
    }

    public void startGame() {

        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {

                g.setColor(Color.orange);
                g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            for (int i = 0; i < HERO_BODY; i++) {

                g.setColor(Color.green);
                g.fillOval(heroX[i], heroY[i], UNIT_SIZE, UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.setFont(new Font("Serif", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Health: " + health, (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

        } else {
            gameOver(g);
        }
    }

    public void newApple() {

        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;


    }

    public void moveHero() {

        for (int i = HERO_BODY; i > 0; i--) {
            heroX[i] = heroX[i - 1];
            heroY[i] = heroY[i - 1];
        }

        switch (heroDirection) {
            case 'U':
                heroY[0] = heroY[0] - UNIT_SIZE;
                break;
            case 'D':
                heroY[0] = heroY[0] + UNIT_SIZE;
                break;
            case 'L':
                heroX[0] = heroX[0] - UNIT_SIZE;
                break;
            case 'R':
                heroX[0] = heroX[0] + UNIT_SIZE;
                break;
        }

        if (count < 10) {
            count++;
        }


    }

    public void moveSnake() {

        int rand = randSwitch.nextInt(12);

        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (snakeDirection) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

        if (rand == 6) {

            turnLeft();
        }

    }

    public void checkApple() {

        if (heroX[0] == appleX && heroY[0] == appleY) {
            health += 10;
            score += 10;
            newApple();
        }

    }

    public void checkHeroCollisions() {


        if (heroX[0] > SCREEN_WIDTH) {
            heroDirection = 'L';
            heroX[0] = SCREEN_WIDTH - UNIT_SIZE;

        } else if (heroX[0] < 0) {
            heroDirection = 'R';
            heroX[0] = UNIT_SIZE;

        } else if (heroY[0] > SCREEN_HEIGHT) {
            heroDirection = 'U';
            heroY[0] = SCREEN_HEIGHT - UNIT_SIZE;

        } else if (heroY[0] < 0) {
            heroDirection = 'D';
            heroY[0] = UNIT_SIZE;
        }

        for (int i = bodyParts; i > 0; i--) {
            if (heroX[0] == x[i] && heroY[0] == y[i]) {
                if (count > 5) {
                    //running = false;
                    health -= 17;
                }
            }
        }

        if (health <= 0) {
            running = false;
        }


    }

    public void checkSnakeCollisions() {

        for (int i = bodyParts; i > 0; i--) {

            for (int k = HERO_BODY; k > 0; k--) {
                if ((heroX[k] == x[i]) && (heroY[k] == y[i])) {
                    if (count > 5) {
                        health -= 3;
                    }
                    System.out.println("HIT!");
                }
            }
        }

        if (x[0] >= SCREEN_WIDTH) {
            snakeDirection = 'L';
            moveSnake();
            turnRight();
        } else if (x[0] <= 0) {
            snakeDirection = 'R';
            moveSnake();
            turnRight();
        } else if (y[0] >= SCREEN_HEIGHT) {
            snakeDirection = 'U';
            moveSnake();
            turnRight();

        } else if (y[0] <= 0) {
            snakeDirection = 'D';
            moveSnake();
            turnRight();
        }

        if (health <= 0) {
            running = false;
        }

    }

    public void gameOver(Graphics g) {
        //text for end of game

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (running) {

            moveHero();
            moveSnake();
            checkApple();
            checkHeroCollisions();
            checkSnakeCollisions();
            snakeBoost++;

            if (snakeBoost >= 25) {
                bodyParts++;
                snakeBoost = 0;
            }

        }

        repaint();

    }

    public void turnRight() {
        switch (snakeDirection) {
            case 'R':
                snakeDirection = 'D';
                break;
            case 'D':
                snakeDirection = 'L';
                break;
            case 'L':
                snakeDirection = 'U';
                break;
            case 'U':
                snakeDirection = 'R';
                break;
        }
    }

    public void turnLeft() {

        switch (snakeDirection) {
            case 'R':
                snakeDirection = 'U';
                break;
            case 'D':
                snakeDirection = 'R';
                break;
            case 'L':
                snakeDirection = 'D';
                break;
            case 'U':
                snakeDirection = 'L';
                break;
        }


    }


    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (heroDirection != 'R') {
                        heroDirection = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (heroDirection != 'L') {
                        heroDirection = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (heroDirection != 'D') {
                        heroDirection = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (heroDirection != 'U') {
                        heroDirection = 'D';
                    }
                    break;
            }

        }
    }
}
