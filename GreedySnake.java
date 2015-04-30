import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class GreedySnake {
    public static void main(String[] args) {
        SnakeFrame frame = new SnakeFrame();
    }
}

class Node {
    int x;
    int y;
    
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Snake {
    ArrayList<Node> body;
    Node food;
    int direction;
    int score;
    int status;
    
    public Snake() {
        score = 0;
        status = 1;
        direction = 3;
        body = new ArrayList<Node>();
        body.add(new Node(20, 20));
        body.add(new Node(30, 20));
        body.add(new Node(40, 20));
        makeFood();
    }
    
    private boolean eat() {
        Node head = body.get(0);
        if (direction == 1 && (head.x - 10) == food.x && head.y == food.y) return true;
        else if (direction == 2 && head.x == food.x && (head.y - 10) == food.y) return true;
        else if (direction == 3 && (head.x + 10) == food.x && head.y == food.y) return true;
        else if (direction == 4 && head.x == food.x && (head.y + 10) == food.y) return true;
        else return false;
    }
    
    private boolean collsion() {
        Node node = body.get(0);
        if (direction == 1 && node.x == 0) return true;
        else if (direction == 2 && node.y == 0) return true;
        else if (direction == 3 && node.x == 200) return true;
        else if (direction == 4 && node.y == 200) return true;
        Node temp = null;
        int i = 0;
        for (i = 3; i < body.size(); i++) {
            temp = body.get(i);
            if (temp.x == node.x && temp.y == node.y) break;
        }
        if (i < body.size()) return true;
        else return false;
    }
    
    private void makeFood() {
        Node node = new Node(0, 0);
        boolean inBody = true;
        int x = 0, y = 0, i = 0;
        while (inBody) {
            x = (int) (Math.random() * 20);
            y = (int) (Math.random() * 20);
            for (i = 0; i < body.size(); i++) {
                if (body.get(i).x == 10*x && body.get(i).y == 10*y) break;
            }
            if (i < body.size()) inBody = true;
            else inBody = false;
        }
        food = new Node(10*x, 10*y);
    }
    
    public void changeDer(int newDer) {
        if (direction % 2 != newDer % 2) direction = newDer;
    }
    
    public void move() {
        if (eat()) {
            body.add(0, food);
            score += 10;
            makeFood();
        }
        else if (collsion()) {
            status = 3;
        }
        else if (status == 1) {
            Node node = body.get(0);
            int tempX = node.x;
            int tempY = node.y;
            switch (direction) {
            case 1:
                tempX -= 10;
                break;
            case 2:
                tempY -= 10;
                break;
            case 3:
                tempX += 10;
                break;
            case 4:
                tempY += 10;
                break;
            }
            body.add(0, new Node(tempX, tempY));
            body.remove(body.size() - 1);
        }
    }
}

class SnakeRunnable implements Runnable {
    private Snake snake;
    private Component component;
    private JLabel scoreLabel;
    private JLabel statusLabel;
    private JLabel overLabel;
    public SnakeRunnable(Snake snake, Component component, JLabel scoreLabel, JLabel statusLabel, JLabel overLabel) {
        this.snake = snake;
        this.component = component;
        this.scoreLabel = scoreLabel;
        this.statusLabel = statusLabel;
        this.overLabel = overLabel;
    }
    
    public void run() {
        while (true) {
            scoreLabel.setText("Score: "+snake.score);
            if (snake.status == 1) statusLabel.setText("Press Space to pause.");
            else if (snake.status == 2) statusLabel.setText("Press Space to continue.");
            else if (snake.status == 3) {
                statusLabel.setText("Press Space to restart.");
                overLabel.setText("Game Over!");
            }
            try {
                snake.move();
                component.repaint();
                Thread.sleep(300);
            } catch (Exception e) {
            }
        }
    }
}

class SnakePanel extends JPanel {
    private Snake snake;
    
    public SnakePanel(Snake snake) {
        this.snake = snake;
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Node node = null;
        for (int i = 0; i < snake.body.size(); i++) {
            g.setColor(Color.blue);
            node = snake.body.get(i);
            g.fillRect(node.x, node.y, 10, 10);
        }
        node = snake.food;
        g.setColor(Color.red);
        g.fillRect(node.x, node.y, 10, 10);
    }
}

class SnakeFrame extends JFrame {
    private Snake snake;
    private JPanel snakePanel;
    private JLabel scoreLabel;
    private JLabel statusLabel;
    private JLabel overLabel;
    
    public SnakeFrame() {
        setTitle("Greedy Snake");
        setSize(230, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        snake = new Snake();
        snakePanel = new SnakePanel(snake);
        snakePanel.setBackground(Color.white);
        snakePanel.setBounds(10, 10, 210, 210);
        snakePanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        add(snakePanel);
        scoreLabel = new JLabel();
        scoreLabel.setBounds(90, 230, 100, 20);
        add(scoreLabel);
        statusLabel = new JLabel();
        statusLabel.setBounds(55, 250, 170, 20);
        add(statusLabel);
        overLabel = new JLabel();
        overLabel.setBounds(85, 270, 80, 20);
        add(overLabel);
        Runnable r1 = new SnakeRunnable(snake, snakePanel, scoreLabel, statusLabel, overLabel);
        Thread t1 = new Thread(r1);
        t1.start();
        snake.status=1;
        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    snake.changeDer(1);
                    break;
                case KeyEvent.VK_UP:
                    snake.changeDer(2);
                    break;
                case KeyEvent.VK_RIGHT:
                    snake.changeDer(3);
                    break;
                case KeyEvent.VK_DOWN:
                    snake.changeDer(4);
                    break;
                case KeyEvent.VK_SPACE:
                    if (snake.status == 1) {
                        snake.status = 2;
                        break;
                    }
                    else if (snake.status == 2) {
                        snake.status = 1;
                        break;
                    }
                    else if (snake.status == 3) {
                        SnakeFrame frame = new SnakeFrame();
                        break;
                    }
                }
            }
            public void keyReleased(KeyEvent k) {
            }
            public void keyTyped(KeyEvent k) {
            }
        });
    }
}