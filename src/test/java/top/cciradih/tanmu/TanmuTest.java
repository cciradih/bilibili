package top.cciradih.tanmu;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;

class TanmuTest {
    @Test
    void test() {
        try (
                FileReader fileReader = new FileReader("test")
        ) {
            JSONReader jsonReader = new JSONReader(fileReader);
            JSONObject jsonObject = jsonReader.readObject(JSONObject.class);
            System.out.println(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
