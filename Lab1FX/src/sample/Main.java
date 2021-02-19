package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String str = "VirtualBox Host-Only Network:���� IP 㧫�: [192.168.56.1] ��� ������: []    " +
                "���� �� ������.    ��⥢�� ������祭�� Bluetooth:���� IP 㧫�: [0.0.0.0] ��� " +
                "������: []    ���� �� ������.    ���\u0BA2����� ���:���� IP 㧫�: " +
                "[192.168.1.144] ��� ������: []           ������ NetBIOS-���� 㤠������ " +
                "�������\u0BA2       ���                ���          ����ﭨ�    -" +
                "---------------------------------------------------    WORKGROUP      \n";

        Matcher matcher = Pattern.compile("([\\da-fA-F]{2}-){5}[\\da-fA-F]{2}").matcher(str);
        if(matcher.find()) {
            System.out.println("Found");
        }
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
