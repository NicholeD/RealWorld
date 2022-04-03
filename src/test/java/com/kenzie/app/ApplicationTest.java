package com.kenzie.app;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.NoSuchElementException;


import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTest {
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    // Set up streams to test console input and output
    @BeforeAll
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @BeforeEach
    public void setTestInput() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testReadCSVFromFile() throws IOException {
        String filename = "austin_weather_test.csv";
        String fileContents = Application.readCSVLines(filename);
        String expectedFileContents;
        if (System.getProperty("os.name").contains("Windows")) {
            expectedFileContents = "Date,DayOfWeek,TempHighF,TempAvgF,TempLowF\r\n"
                    + "2013-12-22,Sunday,56,48,39\r\n"
                    + "2013-12-23,Monday,58,45,32\r\n"
                    + "2013-12-24,Tuesday,61,46,31\r\n"
                    + "2013-12-25,Wednesday,58,50,41\r\n"
                    + "2013-12-26,Thursday,57,48,39\r\n"
                    + "2013-12-27,Friday,60,53,45\r\n"
                    + "2013-12-28,Saturday,62,51,40";
        } else {
            expectedFileContents = "Date,DayOfWeek,TempHighF,TempAvgF,TempLowF\n"
                    + "2013-12-22,Sunday,56,48,39\n"
                    + "2013-12-23,Monday,58,45,32\n"
                    + "2013-12-24,Tuesday,61,46,31\n"
                    + "2013-12-25,Wednesday,58,50,41\n"
                    + "2013-12-26,Thursday,57,48,39\n"
                    + "2013-12-27,Friday,60,53,45\n"
                    + "2013-12-28,Saturday,62,51,40";
        }
        assertEquals(expectedFileContents, fileContents);
    }

    @Test
    public void testCreateMultidimensionalArray() {
        int numRows = 1;
        int numColumns = 6;
        String fileContents;

        if (System.getProperty("os.name").contains("Windows")) {
            fileContents = "2013-12-22,Sunday,56,48,39\r\n"
                    + "2013-12-23,Monday,58,45,32\r\n"
                    + "2013-12-24,Tuesday,61,46,31\r\n"
                    + "2013-12-25,Wednesday,58,50,41\r\n"
                    + "2013-12-26,Thursday,57,48,39\r\n"
                    + "2013-12-27,Friday,60,53,45\r\n"
                    + "2013-12-28,Saturday,62,51,40";
        } else {
            fileContents = "2013-12-22,Sunday,56,48,39\n" +
                    "2013-12-23,Monday,58,45,32\n" +
                    "2013-12-24,Tuesday,61,46,31\n" +
                    "2013-12-25,Wednesday,58,50,41\n" +
                    "2013-12-26,Thursday,57,48,39\n" +
                    "2013-12-27,Friday,60,53,45\n" +
                    "2013-12-28,Saturday,62,51,40";
        }
        String[][] contentArray = Application.saveToMultidimensionalArray(fileContents, numRows, numColumns);
        String[] expectedArray = Application.removeCSVHeader(fileContents.split("\n"));
        for (int i = 0; i < 6; i++) {
            assertEquals(expectedArray[i], contentArray[0][i]);
        }
    }

    @Test
    public void testRunMainBadWeekInput_Word(){
        runMainWithInput("Oranges\n14\n");
        assertThat(outContent.toString(), containsString("You have to enter an integer for the week!"));
        assertThat(outContent.toString(), containsString("You need to enter a number between 1 and 10 for the week!"));
    }

    @Test
    public void testRunMainBadWeekInput_Number(){
        runMainWithInput("14\n");
        assertThat(outContent.toString(), containsString("You need to enter a number between 1 and 10 for the week!"));
    }

    @Test
    public void testRunMainBadWeekInput_Word_ThenGoodInput(){
        runMainWithInput("Oranges\n10\n7\n");
        assertThat(outContent.toString(), containsString("You have to enter an integer for the week!"));
        assertThat(outContent.toString(), containsString("Average temperature (degrees F): "));
    }

    @Test
    public void testRunMainBadDayInput_Word(){
        runMainWithInput("10\napples\n7\n");
        assertThat(outContent.toString(), containsString("You have to enter an integer for the day!"));
        assertThat(outContent.toString(), containsString("Average temperature (degrees F): "));
    }

    @Test
    public void testRunMainBadDayInput_Number(){
        runMainWithInput("10\n8\n");
        assertThat(outContent.toString(), containsString("You need to enter a number between 1 and 7 for the day!"));

    }

    private static void runMainWithInput(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        try {
            Application.main(new String[]{});
        }
        catch(IOException e){

        }
    }
}
