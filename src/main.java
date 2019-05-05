import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class main extends Application implements EventHandler {

    ArrayList<String> arrayList = new ArrayList<>();

    Button button = new Button();
    TextField textField = new TextField();


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
        primaryStage.setTitle("Server");

        button.setText("Send");
        button.setOnAction(this::handle);
        HBox layout = new HBox();

        VBox vBox = new VBox();
       // vBox.getChildren().add(imageView);
       // listView.getItems().addAll(server.getList());



        layout.getChildren().addAll(textField,button);
        Scene scene = new Scene(layout,300,50);
        layout.setAlignment(Pos.BOTTOM_CENTER);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void handle(Event event) {


        String message = textField.getText();
        server.SendMessage(message);
    }
}
