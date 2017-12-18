import java.util.ArrayList;


/**
 * Created by wail babou on 2017-01-06.
 */
public class ChangeSens extends Thread {
    ArrayList<Robot> list;

    public ChangeSens(ArrayList<Robot> list) {
        this.list = list;
    }

    @Override
    public void run() {

        while (true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(Monde.count==0)
                changer();
        }
    }

    public void changer() {

            if (Monde.sens == Dir.H) {
                Monde.sens = Dir.V;
            } else {
                Monde.sens = Dir.H;
            }
        try {
            Main.CreatRobots();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
