/**
 * Created by wail babou on 2016-12-19.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class Main extends Application {
    boolean[][] tabStop = new boolean[10][10];

    static ArrayList<Robot> netList = new ArrayList<>();
    ObservableList<Robot> list = FXCollections.observableArrayList();

    TableView<Robot> table1;
    Button start, end, resume;
    static ImageView sense;
    static Random rand = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DAC PROJET ");
        GridPane gridPane = new GridPane();
        gridPane.setPrefSize(50, 50);
        ImageView[][] btn = Monde.btn;

        for (int i = 0; i < btn.length; i++) {
            for (int j = 0; j < btn.length; j++) {
                btn[i][j] = new ImageView();
                btn[i][j].setFitWidth(50);
                btn[i][j].setFitHeight(50);
                btn[i][j].setImage(new Image("img\\nrml.png"));
                gridPane.add(btn[i][j], i, j);
                Monde.sema[i][j] = new Semaphore(1, true);
            }
        }
        start = new Button("Start");
        start.setPrefSize(100, 50);
        end = new Button("Stop");
        end.setPrefSize(100, 50);
        resume = new Button("Resume");
        resume.setPrefSize(100, 50);
        sense = new ImageView(new Image("/img/icon2.png",50,50,false,false));

        HBox change=new HBox(10);
        change.getChildren().addAll(sense);

        HBox top = new HBox(10);
        top.setPadding(new Insets(5, 5, 5, 5));
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(start, end, resume);

        VBox topV=new VBox();
        topV.setAlignment(Pos.CENTER);
        topV.getChildren().addAll(top,change);

        table1 = new TableView<>();
        // robot name
        TableColumn<Robot, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMaxWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("namme"));
        // robot position
        TableColumn<Robot, Position> positionColumn = new TableColumn<>("Position");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        //robot energie
        TableColumn<Robot, Integer> energieColumn = new TableColumn<>("Energie");
        energieColumn.setCellValueFactory(new PropertyValueFactory<>("energie"));

        table1.setItems(getRobotPoll());
        table1.getColumns().addAll(nameColumn, positionColumn,energieColumn);


        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topV);
        borderPane.setRight(table1);
        borderPane.setCenter(gridPane);


        //Adding GridPane to the scene
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);

        primaryStage.show();
        primaryStage.setOnCloseRequest(e->  Platform.exit());


        updating();
        start.setOnAction(event -> {
                    try {
                        CreatRobots();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new ChangeSens(netList).start();
                    start.setVisible(false);
        }
        );
        end.setOnAction(event -> new Thread() {
            @Override
            public void run() {
                pause();
            }
        }.start());

        resume.setOnAction(event -> new Thread() {
            @Override
            public void run() {
                resumer();
            }
        }.start());

    }

    public void pause() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                boolean st = Monde.sema[i][j].tryAcquire();
                if (st) {
                    tabStop[i][j] = true;
                }
            }
        }
    }
    public void resumer(){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (tabStop[i][j]) {
                    Monde.sema[i][j].release();
                    tabStop[i][j] = false;
                }
            }
        }
    }

    public static void CreatRobots() throws InterruptedException {

        if (Monde.sens == Dir.H) {
            try {
                Monde.feu.acquire();
                changeLight();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (Monde.light) {
                        int x = rand.nextInt(10);
                        ajouterRobot(x,0,i,Dir.H);
                        i++;
                    }
                }
            }.start();

        } else {
            try {
                Monde.feu.acquire();
                changeLight();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            netList.clear();

            new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (!Monde.light) {
                        int y = rand.nextInt(10);
                        ajouterRobot(0,y,i,Dir.V);
                        i++;
                    }
                }
            }.start();
        }
    }

    private void updating() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        list.clear();
                        table1.setItems(getRobotPoll());
                        sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public ObservableList<Robot> getRobotPoll() {
        list = FXCollections.observableArrayList();
        for (Robot r : netList) {
            list.add(r);
        }
        return list;
    }

    public static void changeLight() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(Monde.light){
                    Monde.light = !Monde.light;
                    sense.setImage(new Image("./img/icon1.png",50,50,false,false));
                }else {
                    Monde.light = !Monde.light;
                    sense.setImage(new Image("./img/icon2.png",50,50,false,false));

                }

            }
        }, 15000);

    }

    public static void ajouterRobot(int x,int y,int i,Dir dir){

        RobotPollueur r1 = new RobotPollueur(new Position(x, y), "robotP" + i, dir);
        netList.add(r1);
        r1.start();

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RobotNettoyeur r2 = new RobotNettoyeur(new Position(x, y), "robotN" + i, dir);
        netList.add(r2);
        r2.start();
        Monde.count = Monde.count + 2;
        try {
            Thread.currentThread().sleep(rand.nextInt(3000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
