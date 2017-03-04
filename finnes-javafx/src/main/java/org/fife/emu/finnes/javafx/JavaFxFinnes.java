package org.fife.emu.finnes.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Application entry point for the JavaFX implementation of the  Finnes emulator.
 */
public class JavaFxFinnes extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(event -> System.out.println("Hello World!"));

		StackPane root = new StackPane();
		root.getChildren().add(btn);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}

	/**
	 * Application entry point.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}