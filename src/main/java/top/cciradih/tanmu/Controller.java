package top.cciradih.tanmu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Controller {
    private static final ObservableList<String> DANMU_LIST = FXCollections.observableArrayList();

    @FXML
    private ImageView qrCode;
    @FXML
    private ListView<String> danmuList;
    @FXML
    private TextField danmuInput;
    @FXML
    private Label popularity;
    @FXML
    private Label roomId;

    void setQrCodeImage() {
        try {
            qrCode.setImage(new Image(new FileInputStream("qrCode")));
            qrCode.setVisible(true);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    void setQrCodeVisible(boolean value) {
        qrCode.setVisible(value);
    }

    void setPopularityVisible(boolean value) {
        popularity.setVisible(value);
    }

    void setRoomIdVisible(boolean value) {
        roomId.setVisible(value);
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

    void setPopularityText(String text) {
        popularity.setText(text);
    }

    void setRoomIdText(String text) {
        roomId.setText(text);
    }

    @FXML
    private void onEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            String cookie = Cookie.getInstance().readFromFile();
            String message = danmuInput.getText();
            Http.getInstance().sendDanmu(cookie, message, Live.ROOM_ID.getId());
            setDanmuInputText("");
        }
    }

    void setDanmuListItems() {
        danmuList.setItems(DANMU_LIST);
    }

    void addDanmuListItem(String danmu) {
        if (DANMU_LIST.size() > 19) {
            DANMU_LIST.remove(0);
        }
        DANMU_LIST.add(danmu);
    }
}
