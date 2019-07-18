package top.cciradih.tanmu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Controller {
    private static final Http HTTP = new Http();
    private static final Cookie COOKIE = new Cookie();
    private static final ObservableList<String> DANMU_LIST = FXCollections.observableArrayList();

    @FXML
    private ImageView qrCode;
    @FXML
    private ListView<String> danmuList;
    @FXML
    private TextField danmuInput;

    void setQrCodeImage() {
        try {
            qrCode.setImage(new Image(new FileInputStream("qrCode")));
            qrCode.setVisible(true);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void setQrCodeVisible(boolean value) {
        qrCode.setVisible(value);
    }

    void setDanmuListVisible(boolean value) {
        danmuList.setVisible(value);
    }

    void setDanmuInputVisible(boolean value) {
        danmuInput.setVisible(value);
    }

    void setDanmuInputText(String text) {
        danmuInput.setText(text);
    }

    @FXML
    private void onEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            String cookie = COOKIE.readFromFile();
            String message = danmuInput.getText();
            HTTP.sendDanmu(cookie, message);
            setDanmuInputText("");
        }
    }

    void setDanmuListItems() {
        danmuList.setItems(DANMU_LIST);
    }

    void addDanmuListItem(String danmu){
        DANMU_LIST.add(danmu);
    }
}
