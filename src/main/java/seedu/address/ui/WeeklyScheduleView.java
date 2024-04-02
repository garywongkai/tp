package seedu.address.ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import seedu.address.model.schedule.Schedule;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * WeeklyScheduleView class to assist with population of Calender
 */
public class WeeklyScheduleView extends UiPart<Region> {

    private static final String FXML = "WeeklyScheduleView.fxml";
    private final LocalDateTime START_TIME = LocalDateTime.of(LocalDate.parse("2024-04-01"), LocalTime.of(8, 0));
    private final LocalDateTime END_TIME = LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0));
    private final int NUM_DAYS = 7; // Assuming a typical workweek from Monday to Sunday
    private final int TIME_INTERVAL_MINUTES = 30;

    @FXML
    private VBox timeTableBox;
    @FXML
    private GridPane timetableGrid;

    /**
     * Constructor method to initialize class
     */
    public WeeklyScheduleView() {
        super(FXML);
        initializeTimetable();
    }

    /**
     * initialize call to set up CellFactory to populate Schedule objects
     */

    public void initialize() {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);

        // Apply the column constraint to the gridpane
        timetableGrid.getColumnConstraints().add(columnConstraints);
        VBox.setVgrow(timetableGrid, javafx.scene.layout.Priority.ALWAYS);
        timetableGrid.setMaxWidth(Double.MAX_VALUE);
        timetableGrid.setHgap(10);
        System.out.println("I've been set-up!");
    }

    private void initializeTimetable() {
        for (int j = 0; j < 27; j++) {
            LocalTime time = LocalTime.from(START_TIME.plusMinutes(j * 30));
            Label timeLabel = new Label(time.toString());
            timeLabel.setStyle("-fx-padding: 0 10 0 10;");
            timeLabel.setTextAlignment(TextAlignment.CENTER);
            timetableGrid.add(timeLabel, j + 1, 0);
        }

        // Add day labels to the first row
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < NUM_DAYS; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            if (i == 0) {
                // Set the preferred width of the first day label
                dayLabel.setMinWidth(60); // Adjust the width as needed
            }
            dayLabel.setTextAlignment(TextAlignment.CENTER);
            timetableGrid.add(dayLabel, 0, i + 1);
        }
        timetableGrid.setStyle("-fx-padding: 0 10 0 10;");
    }

    public void populateTimetable(ArrayList<Schedule> schedules) {
        // Clear the timetable before populating it
        clear();

        // Populate the timetable grid
        for (Schedule schedule : schedules) {
            populateCellsForSchedule(schedule);
        }
    }

    private void populateCellsForSchedule(Schedule schedule) {
        // Calculate the row index for the start time of the schedule
        LocalDateTime startTime = schedule.getStartTime();
        int startRowIndex = calculateRowIndex(startTime);

        // Calculate the row index for the end time of the schedule
        LocalDateTime endTime = schedule.getEndTime();
        int endRowIndex = calculateRowIndex(endTime);

        // Determine the rowSpan for the schedule based on its duration
        int rowSpan = endRowIndex - startRowIndex + 1;

        // Calculate the column index for the day of the schedule
        DayOfWeek dayOfWeek = startTime.getDayOfWeek();
        int columnIndex = dayOfWeek.getValue(); // Adjust for 0-based indexing

        Node scheduleNode = createScheduleCell(schedule);
        if (rowSpan > 2) {
            GridPane.setColumnSpan(scheduleNode, rowSpan);
        }
        timetableGrid.add(scheduleNode, startRowIndex + 1, columnIndex);
    }

    public void removeSchedule(Schedule schedule) {
        ArrayList<Node> nodesToRemove = new ArrayList<>();
        for (Node node : timetableGrid.getChildren()) {
            if (node instanceof StackPane) {
                StackPane cell = (StackPane) node;

                // Check if the cell contains a label with the schedule description
                for (Node cellContent : cell.getChildren()) {
                    if (cellContent instanceof Label) {
                        Label label = (Label) cellContent;

                        // Compare the label text with the schedule description
                        if (label.getText().equals(schedule.getSchedName())) {
                            // Add the cell to the list of nodes to be removed
                            nodesToRemove.add(cell);
                        }
                    }
                }
            }
        }

        // Remove all the cells corresponding to the schedule from the grid
        timetableGrid.getChildren().removeAll(nodesToRemove);
    }

    private int calculateRowIndex(LocalDateTime time) {
        LocalDateTime startTimeOfDay = time.toLocalDate().atStartOfDay().plusHours(8);
        int minutesFromStart = (int) startTimeOfDay.until(time, java.time.temporal.ChronoUnit.MINUTES);
        System.out.println(minutesFromStart / TIME_INTERVAL_MINUTES);
        return minutesFromStart / TIME_INTERVAL_MINUTES;
    }

    private Node createScheduleCell(Schedule schedule) {
        // Create a StackPane to hold the content of the schedule cell
        StackPane cellPane = new StackPane();

        // Set the style of the cell
        cellPane.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
        // Create a label to display the schedule information
        Label label = new Label(schedule.getSchedName());
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        // Add the label to the cell pane
        cellPane.getChildren().add(label);

        // Set the alignment of the label within the cell pane
        StackPane.setAlignment(label, Pos.CENTER);
//        StackPane.setMargin(label, new Insets(5)); // Add margin to the label

        // Set the preferred size of the cell
        cellPane.setPrefWidth(Region.USE_PREF_SIZE);
        cellPane.setPrefHeight(Region.USE_PREF_SIZE);
        // Return the cell pane
        return cellPane;
    }

    public void clear() {
        timetableGrid.getChildren().clear();
        initializeTimetable();
    }
}
