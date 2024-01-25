import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.*;
import javafx.concurrent.*;
import javafx.beans.*;
import javafx.beans.value.*;
import java.nio.ByteBuffer;
import javafx.scene.Group;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.scene.canvas.GraphicsContext;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import java.io.*;
import javax.imageio.*;

//dodane z filechoosera
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

//dodane z frames
import java.awt.image.*;
import java.awt.image.BufferedImage;

import cam.Frames;


public class JavaFXApp extends Application {
  private static final int FRAME_WIDTH  = 640;
  private static final int FRAME_HEIGHT = 480;

  GraphicsContext gc;
  Canvas canvas;
  byte buffer[];
  PixelWriter pixelWriter;
  PixelFormat<ByteBuffer> pixelFormat;

  int RGB_pixels[];
  public BufferedImage bi;


  Frames frames;

  Stage stage;

  public static void main(String[] args) {
            launch(args);
	        }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Microscope view");

    int result;
    Timeline timeline;

    frames = new Frames();

    result = frames.open_shm("/frames");

    canvas     = new Canvas(FRAME_WIDTH + 100, FRAME_HEIGHT + 100);
    gc         = canvas.getGraphicsContext2D();

    timeline = new Timeline(new KeyFrame(Duration.millis(130), e->disp_frame()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  
    RGB_pixels = new int[FRAME_WIDTH*FRAME_HEIGHT];


    stage = primaryStage;

    Menu menu1 = new Menu("File");
    
    MenuItem menuItem1 = new MenuItem("Save Image");
    menuItem1.setOnAction(e -> {
                              save_image(menuItem1);
                             });

    MenuItem menuItem2 = new MenuItem("Credits");
    menuItem2.setOnAction(e -> {
                              credits();
                             });

    MenuItem menuItem3 = new MenuItem("Exit");
    menuItem3.setOnAction(e -> {
                              exit_dialog();
                             });
  
    menu1.getItems().add(menuItem1);
    menu1.getItems().add(menuItem2);
    menu1.getItems().add(menuItem3);

	
    MenuBar menuBar = new MenuBar();
  
    menuBar.getMenus().add(menu1);
  					
    VBox vBox = new VBox(menuBar);

    vBox.getChildren().add(canvas);
    Scene scene = new Scene(vBox, FRAME_WIDTH+100, FRAME_HEIGHT+100);
							
    primaryStage.setScene(scene);
  
    primaryStage.setOnCloseRequest(e -> {
                                       e.consume();
                                       exit_dialog();
                                      });

    primaryStage.show();
  }

 

 public void save_image(MenuItem item)
  {
   FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Save");
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
      //Adding action on the menu item
      item.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent event) {
            //Opening a dialog box
            File selectedfile = fileChooser.showSaveDialog(stage);
            String filePath = selectedfile.getPath();
            //String fileName = selectedfile.getName();
            System.out.println(filePath);
            //System.out.println(fileName);

     System.out.println("Save image");

     //zapis do pliku filePath
     try {

        int i, j;

        j = 0;
        for(i = 0; i < RGB_pixels.length; i++)
        {
        RGB_pixels[i] = (int) (buffer[j] << 16) + (buffer[j+1]<< 8) + buffer[j+2];
        j+=3;
        }

        bi = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_RGB);

        bi.setRGB(0, 0, FRAME_WIDTH, FRAME_HEIGHT, RGB_pixels, 0, FRAME_WIDTH);


        ImageIO.write(bi, "png", selectedfile);
     } catch (IOException e) {

     }


         }
      });




  }
 public void credits()
  {
   System.out.println("credits");
   Alert alertCredits = new Alert(AlertType.INFORMATION,
                                            "Credits: Tomasz Dabrowa, Filip Halys, Mateusz Mitan",
                                             ButtonType.OK);
    alertCredits.setResizable(true);
   alertCredits.onShownProperty().addListener(e -> {
                                             Platform.runLater(() -> alertCredits.setResizable(false));
                                            });

   Optional<ButtonType> result = alertCredits.showAndWait();
  if (result.get() == ButtonType.OK)
   {
        System.out.println("OK kliknieto");
   }
  else
   {

   }
  }
 
 public void exit_dialog()
  {
   System.out.println("exit dialog");
   

   Alert alert = new Alert(AlertType.CONFIRMATION,
                           "Do you really want to exit the program?.",
 			    ButtonType.YES, ButtonType.NO);

   alert.setResizable(true);
   alert.onShownProperty().addListener(e -> { 
                                             Platform.runLater(() -> alert.setResizable(false)); 
                                            });

  Optional<ButtonType> result = alert.showAndWait();
  if (result.get() == ButtonType.YES)
   {
    Platform.exit();
   } 
  else 
   {
   }

  }

  private void disp_frame() {
      pixelWriter = gc.getPixelWriter();
      pixelFormat = PixelFormat.getByteRgbInstance();


      buffer = frames.get_frame();
      pixelWriter.setPixels(50, 50, FRAME_WIDTH, FRAME_HEIGHT, pixelFormat, buffer, 0, FRAME_WIDTH*3);







     }


}
