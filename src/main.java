import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class main extends Application implements EventHandler {

    ArrayList<String> arrayList = new ArrayList<>();


    JFXListView<String> listView;
    JFXButton refresh;
    JFXButton send;
    JFXTextField textField;
    VBox vBox;
    static Server server;

    static {
        try {
            server = new Server();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startRunning();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

      /*  FileInputStream fis = new FileInputStream("C:\\Users\\Mohamad Nsearaty\\Desktop\\One Piece wallpapers\\[Al3asq] One Piece - 876 [h264 1080p 10bit].mkv_snapshot_21.31_[2019.03.23_16.05.42].jpg");
        biyte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        BufferedImage bufferedImage = ImageIO.read(bis);
        WritableImage writableImage = null;
        if(bufferedImage != null)
        {
            writableImage = new WritableImage(bufferedImage.getWidth(),bufferedImage.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for(int x = 0 ; x < bufferedImage.getWidth();x++)
                for(int y = 0 ;y <bufferedImage.getHeight();y++)
                {
                    pixelWriter.setArgb(x,y,bufferedImage.getRGB(x,y));
                }
        }
        final ImageView imageView = new ImageView(writableImage);

        imageView.setX(30);
        imageView.setY(30);

        imageView.setFitHeight(450);
        imageView.setFitWidth(500);
        imageView.setPreserveRatio(true);
*/
        listView = new JFXListView<>();
        ArrayList<String> strings = new ArrayList<>(server.onlineUsers.keySet());
        listView.setCellFactory(lv->new MainCell());
        listView.setItems(getMessages());
        refresh = new JFXButton("Refresh");
        send = new JFXButton("Send Message");
        textField = new JFXTextField("");
       textField.setPromptText("TYPE A MESSAGE");
       send.setOnAction(e ->
       {
           String message = textField.getText();
           ObservableList<String> list = listView.getSelectionModel().getSelectedItems();
           for(String mail : list)
           {
               Server.HandleThread handleThread = server.onlineUsers.get(mail);
               if(handleThread != null) {
                   Thread thread = new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               ArrayList<Object> arrayList = new ArrayList<>();
                               arrayList.add(100);
                               arrayList.add(message);
                               handleThread.getOutput().writeObject(arrayList);
                               handleThread.getOutput().flush();
                           } catch (IOException ex) {
                               ex.printStackTrace();
                           }
                       }
                   });
                   thread.start();
               }
           }
       });
        refresh.setOnAction(e ->
        {
            listView = new JFXListView<>();
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listView.setCellFactory(lv ->new MainCell());
            listView.setItems(getMessages());
            vBox = new VBox(listView , new HBox(refresh,send) ,textField);

            vBox.setPadding(new Insets(20,20,20,20));
            Scene scene = new Scene(vBox,300,300);

            primaryStage.setScene(scene);
            primaryStage.show();
        });
        vBox = new VBox(listView , new HBox(refresh,send) ,textField);
        vBox.resize(listView.getWidth() , listView.getHeight());
        vBox.setPadding(new Insets(20,20,20,20));
        Scene scene = new Scene(vBox,300,300);
        primaryStage.setTitle("Server");


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<String> getMessages() {
        ArrayList<String> arrayList = new ArrayList(server.onlineUsers.keySet());
        return FXCollections.observableList(arrayList);
    }

    @Override
    public void handle(Event event) {

        //vBox = new VBox(listView , refresh);
    }


    public class MainCell extends ListCell<String> {
        private Label email;
        private VBox box;

        public MainCell() {
            setPrefWidth(100);
            email = new Label();
            box = new VBox(email);
        }
        @Override
        protected void updateItem(String message, boolean empty) {
            super.updateItem(message, empty);

            if (message == "" || empty) {
                setGraphic(null);
            } else {
                email.setText(message);
                setGraphic(box);
            }
        }
    }
}