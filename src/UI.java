import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JPanel implements ActionListener {

    private WeaponBall ballA; // Unarmed
    private WeaponBall ballB; // Sword
    private Timer gameTimer;
    private JButton resetButton;

    private boolean isGameOver = false;
    private String winner = "";

    private int p1Wins = 0;
    private int p2Wins = 0;

    private final int ARENA_WIDTH = 440;
    private final int ARENA_HEIGHT = 440;

    public UI() {
        setDoubleBuffered(true); // Asked AI to help with optimization (it was super laggy)
        initializeUI();
    }
    // initializes ui
    public void initializeUI() {
        setLayout(null);
        setBackground(new Color(253, 246, 231));

        resetButton = new JButton("Play Again");
        resetButton.setBounds(170, 400, 140, 40);
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setFocusable(false);

        resetButton.addActionListener(this);

        add(resetButton);
        resetButton.setVisible(false);

        gameTimer = new Timer(16, this);
        resetGame();
    }


    public void resetGame() {
        isGameOver = false;
        winner = "";
        resetButton.setVisible(false);

        int minPos = 60;
        int maxPos = 360;

        double randomAx = (Math.random() * (maxPos - minPos + 1)) + minPos;
        double randomAy = (Math.random() * (maxPos - minPos + 1)) + minPos;
        double randomBx = (Math.random() * (maxPos - minPos + 1)) + minPos;
        double randomBy = (Math.random() * (maxPos - minPos + 1)) + minPos;

       // instantiates new weaponball objects everytime game resets
        ballA = new WeaponBall(randomAx, randomAy, 5.0, 2.0, 50, new Color(209, 209, 209), false);
        ballB = new WeaponBall(randomBx, randomBy, -2.5, -4.0, 50, new Color(255, 105, 106), true);

        gameTimer.start();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draws Arena
        g.setColor(Color.WHITE);
        g.fillRect(20, 150, ARENA_WIDTH, ARENA_HEIGHT);
        g.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++) {
            g.drawRect(20 - i, 150 - i, ARENA_WIDTH + (i * 2), ARENA_HEIGHT + (i * 2));
        }

        // Translation
        g.translate(20, 150);
        ballA.draw(g);
        ballB.draw(g);
        g.translate(-20, -150);

        // Draw Score and HUD data
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.BLACK);

        g.drawString("P1 (Unarmed) Health: " + ballA.getHealth() + "  (Slam Damage: " + ballA.getCurrentDamage() + ")", 15, 140);
        g.drawString("P2 (Sword) Health: " + ballB.getHealth() + "  (Slash Damage: " + ballB.getCurrentDamage() + ")", 180, 610);
        g.setFont(new Font("Arial",Font.BOLD,30));

        g.drawString("WEAPON BALL TOURNAMENT",19,50);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(40, 40, 40));

        g.drawString("TOURNAMENT WINS", 20, 85);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Player 1: " + p1Wins + " wins  |  Player 2: " + p2Wins + " wins", 20, 105);

        // Game Over Screen
        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(20, 150, ARENA_WIDTH, ARENA_HEIGHT);

            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.setColor(Color.YELLOW);
            g.drawString(winner, 110, 320);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Tournament Concluded!", 145, 360);
        }

        Toolkit.getDefaultToolkit().sync(); // more stuff I asked AI for optimization
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            ballA.move(ARENA_WIDTH, ARENA_HEIGHT);
            ballB.move(ARENA_WIDTH, ARENA_HEIGHT);

            ballA.spin();
            ballB.spin();

            ballA.updateImmunity();
            ballB.updateImmunity();

            checkCollisions();
            checkWinCondition();
            repaint();
        }
        else if (e.getSource() == resetButton) {
            resetGame();
        }
    }


    public void checkWinCondition() {
        if (ballA.getHealth() <= 0) {
            isGameOver = true;
            winner = "PLAYER 2 WINS!";
            p2Wins++;
            endMatch();
        } else if (ballB.getHealth() <= 0) {
            isGameOver = true;
            winner = "PLAYER 1 WINS!";
            p1Wins++;
            endMatch();
        }
    }

    public void endMatch() {
        gameTimer.stop();
        resetButton.setVisible(true);
    }

    public void checkCollisions() {
        // body hits body (unarmed deals damage)
        double ballsDistance = Math.sqrt(Math.pow(ballA.getX() - ballB.getX(), 2) + Math.pow(ballA.getY() - ballB.getY(), 2)); // distance formula
        if (ballsDistance < (ballA.getRadius() + ballB.getRadius())) {

            // Only deal damage if Player 2 is not in iframes
            if (!ballB.isImmune()) {
                ballB.takeDamage(ballA.getCurrentDamage());
                ballA.incrementDamage();
                // cooldown
                ballB.startImmunity();
            }

            // Standard momentum physics bounce transfer (law of conservation of momentum or something like that)
            double vAx = ballA.getVx();
            double vAy = ballA.getVy();
            double vBx = ballB.getVx();
            double vBy = ballB.getVy();

            ballA.setVelocity(vBx, vBy);
            ballB.setVelocity(vAx, vAy);

            ballA.move(ARENA_WIDTH, ARENA_HEIGHT);
            ballB.move(ARENA_WIDTH, ARENA_HEIGHT);
        }

        // Sword hits body
        if (ballB.swordIntersectsBody(ballA)) {
            ballA.takeDamage(ballB.getCurrentDamage());
            ballB.incrementDamage();
            // cooldown
            ballA.startImmunity();

            // physics impact or something used ai to get something semi-realistic
            ballA.setVelocity(ballA.getVx() + ballB.getVx() * 1.5, ballA.getVy() + ballB.getVy() * 1.5);
            ballB.reverseSpin();
            ballB.setVelocity(-ballB.getVx() * 0.8, -ballB.getVy() * 0.8);
        }
    }
}