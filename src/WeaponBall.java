import java.awt.*;

public class WeaponBall {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private int radius;
    private int health;
    private double angle;
    private int spinDirection;
    private Color color;
    private boolean hasSword;
    private int swordLength;

    private int currentDamage;
    private boolean isImmune;
    private int immunityFramesLeft;

    private final int IMMUNITY_DURATION = 30;

    public WeaponBall(double startX, double startY, double startVx, double startVy, int radius, Color color, boolean hasSword) {
        this.x = startX;
        this.y = startY;
        this.vx = startVx;
        this.vy = startVy;
        this.radius = radius;
        this.color = color;
        this.hasSword = hasSword;
        this.health = 100;
        this.angle = 0.0;
        this.spinDirection = 1;
        this.isImmune = false;
        this.immunityFramesLeft = 0;

        if (hasSword) {
            this.currentDamage = 5; // Sword starts at 5 damage
            this.swordLength = 115;
        } else {
            this.currentDamage = 1; // Unarmed  starts at 1 damage
            this.swordLength = 0;
        }
    }

    public void move(int arenaWidth, int arenaHeight) {
        x += vx;
        y += vy;

        if (x - radius < 0) {
            x = radius;
            vx = -vx;
        } else if (x + radius > arenaWidth) {
            x = arenaWidth - radius;
            vx = -vx;
        }

        if (y - radius < 0) {
            y = radius;
            vy = -vy;
        } else if (y + radius > arenaHeight) {
            y = arenaHeight - radius;
            vy = -vy;
        }
    }

    public void spin() {
        if (hasSword) {
            angle += 0.1 * spinDirection;
        }
    }

    public void reverseSpin() {
        this.spinDirection *= -1;
    }

    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void startImmunity() {
        this.isImmune = true;
        this.immunityFramesLeft = IMMUNITY_DURATION;
    }

    public void updateImmunity() {
        if (isImmune) {
            immunityFramesLeft--;
            if (immunityFramesLeft <= 0) {
                isImmune = false;
            }
        }
    }


    public boolean swordIntersectsBody(WeaponBall opponent) {

        if (!hasSword || opponent.isImmune()) {
            return false;
        }

        int steps = 10;
        for (int i = 1; i <= steps; i++) {
            double percent = (double) i / steps;
            double currentLength = this.swordLength * percent;

            double testX = this.x + currentLength * Math.cos(this.angle);
            double testY = this.y + currentLength * Math.sin(this.angle);

            double distance = Math.sqrt(Math.pow(testX - opponent.getX(), 2) +
                    Math.pow(testY - opponent.getY(), 2));

            if (distance < opponent.getRadius()) {
                return true;
            }
        }
        return false;
    }

    // Getters + Setters

    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public int getRadius() { return radius; }
    public int getHealth() { return health; }
    public double getAngle() { return angle; }
    public int getSwordLength() { return swordLength; }
    public int getCurrentDamage() { return currentDamage; }
    public boolean isImmune() { return isImmune; }
    public boolean hasSword() { return hasSword; }

    public void setVelocity(double newVx, double newVy) {
        this.vx = newVx;
        this.vy = newVy;
    }

    public void incrementDamage() {
        this.currentDamage++;
    }

    public double getSwordTipX() {
        return x + swordLength * Math.cos(angle);
    }

    public double getSwordTipY() {
        return y + swordLength * Math.sin(angle);
    }

    public void draw(Graphics g) {

        // Draws body
        g.setColor(color);
        g.fillOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);

        g.setColor(Color.BLACK);
        g.drawOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);

        // Draws sword only if has swrod
        if (hasSword) {
            int tipX = (int) getSwordTipX();
            int tipY = (int) getSwordTipY();

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));

            g2.setColor(Color.DARK_GRAY);
            g2.drawLine((int) x, (int) y, tipX, tipY);

            g2.setStroke(new BasicStroke(1));

            g2.setColor(Color.RED);
            g2.fillOval(tipX - 5, tipY - 5, 10, 10);
        }
    }
}