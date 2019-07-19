package top.cciradih.tanmu;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class TanmuTest {
    @Test
    void test() {
        int[][] ints = {{0, 1, 2}, {1}, {2}, {3}};
        for (int i = 0; i < ints.length; i++) {
            System.out.println(Arrays.toString(ints[i]));
        }
    }
}
