import javafx.scene.image.Image;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wail babou on 2016-12-20.
 */
public class RobotNettoyeur extends Thread implements Robot {
    Boolean isLive = true;
    private Position position;
    private String namme;
    private Dir direction;
    private int energie;
    Boolean enough=true;

    public RobotNettoyeur(Position position, String name, Dir d) {
        this.position = position;
        this.namme = name;
        this.direction = d;
        energie=6;
    }

    public int getEnergie() {
        return energie;
    }

    public void setEnergie(int energie) {
        this.energie = energie;
    }

    public String getNamme() {
        return namme;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Dir getDirection() {
        return direction;
    }

    public void setDirection(Dir direction) {
        this.direction = direction;
    }

    @Override
    public void run() {
        Random rand = new Random();

        try {

            ajouter();
            while (isLive) {
                if(enough) {

                    if (direction == Dir.H) {
                        traversee_H(1);
                    } else {
                        traversee_V(1);
                    }
                }
                sleep(rand.nextInt(5000) + 3000);
            }
            interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ajouter() throws InterruptedException {
        Monde.sema[position.getX()][position.getY()].acquire();
        Monde.btn[position.getX()][position.getY()].setImage(new Image("img\\robot.png"));
    }


    public void traversee_H(int step) throws InterruptedException {
        if(energie-step>=0){
            if (position.getY() + step < 10) {
                Monde.sema[position.getX()][position.getY() + step].acquire();
                Monde.btn[position.getX()][position.getY()].setImage(new Image("img\\nrml.png"));
                Monde.sema[position.getX()][position.getY()].release();

                position.setY(position.getY() + step);
                Monde.btn[position.getX()][position.getY()].setImage(new Image("img\\robot.png"));
            } else {
                horsMonde();
            }
            energie=energie-step;

        }else {
            // energie ma tekfich
            chargerEnergie(6,position.getX(),position.getY());
        }

    }

    public void traversee_V(int step) throws InterruptedException {
        if(energie-step>=0){
            if (position.getX() + step < 10) {
                Monde.sema[position.getX() + step][position.getY()].acquire();

                Monde.btn[position.getX()][position.getY()].setImage(new Image("img\\nrml.png"));
                Monde.sema[position.getX()][position.getY()].release();

                position.setX(position.getX() + step);
                Monde.btn[position.getX()][position.getY()].setImage(new Image("img\\robot.png"));
            } else {
                horsMonde();
            }
            energie=energie-step;
        }else {
            // energie ma tekfich
            chargerEnergie(6,position.getX(),position.getY());
        }
    }
    public void horsMonde(){
        Monde.btn[position.getX()][position.getY()].setImage(new Image("img\\nrml.png"));
        Monde.sema[position.getX()][position.getY()].release();
        isLive = false;

        Monde.count=Monde.count-2;
        if(Monde.count==0){
            Monde.feu.release();
        }
    }
    public void chargerEnergie(int plus,int x,int y){
        enough=false;
        Monde.btn[x][y].setImage(new Image("img\\robot2.png"));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Monde.btn[x][y].setImage(new Image("img\\robot2.png"));
                energie=energie+plus;
                enough=true;
            }
        }, 3000);
    }
}
