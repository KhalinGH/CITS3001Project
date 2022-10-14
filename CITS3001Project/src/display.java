import java.awt.*;
import javax.swing.*;

public class display extends Canvas{
        public display(){
        JFrame newWindow = new JFrame("Node Display");
        Canvas canvas = new display();
        canvas.setSize(400, 400);
        newWindow.add(canvas);
        newWindow.pack();
        newWindow.setVisible(true);
    }
    public static void main(String[] args){
        new display();

    }
    public void setBackground(Canvas canvas){
        canvas.setBackground(Color.white);
    }
    public void draw(Graphics g){

        g.fillOval(100, 100, 200, 200);
        g.setColor(Color.green);
    }
}
