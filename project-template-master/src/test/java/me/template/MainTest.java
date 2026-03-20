package me.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

    Main main;

    @BeforeEach
    void setup() {
        main = new Main();
    }

    @Test
    void intsTest() {
        assertThat(main.ints()).containsExactly(1, 2, 3, 4, 5);
    }
}
