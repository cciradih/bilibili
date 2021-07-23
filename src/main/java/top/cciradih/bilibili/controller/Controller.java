package top.cciradih.bilibili.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {
    @FXML
    private ImageView qrCode;
    @FXML
    private Label popularity;
    @FXML
    private Label roomId;
    @FXML
    private ListView<String> danmuList;
    @FXML
    private TextField danmuInput;

    public void onEnterPressed() {
    }

    public void setQrCodeImage(Image image) {
        this.qrCode.setImage(image);
    }
}
