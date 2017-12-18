import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wail babou on 2016-12-19.
 */
public class Monde {
    public  static  ImageView [][] btn = new ImageView[10][10];
    public static Semaphore[][] sema =new Semaphore[10][10];
    //public static CountDownLatch countDownLatch;
    public static Dir sens=Dir.H;
    public static boolean light =true;

    public static int count=0;
    public static Semaphore feu=new Semaphore(1,true);
    //.........



}
