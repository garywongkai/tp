package seedu.address.ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import seedu.address.model.schedule.Schedule;


/**
 * WeeklyScheduleView class to assist with population of Calender
 */
public class WeeklyScheduleView extends UiPart<Region> {

    private static final String FXML = "WeeklyScheduleView.fxml";
    private final LocalDateTime startTime = LocalDateTime.of(LocalDate.parse("2024-04-01"), LocalTime.of(8, 0));
    private final int numDays = 7; // Assuming a typical workweek from Monday to Sunday
    private final int timeInterval = 30;

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
        VBox.setVgrow(timetableGrid, Priority.ALWAYS);
        timetableGrid.setMaxWidth(Double.MAX_VALUE);
        timetableGrid.setHgap(10);
    }

    private void initializeTimetable() {
        for (int j = 0; j < 27; j++) {
            LocalTime time = LocalTime.from(startTime.plusMinutes(j * 30));
            Label timeLabel = new Label(time.toString());
            timeLabel.setStyle("-fx-padding: 0 10 0 10;");
            timeLabel.setTextAlignment(TextAlignment.CENTER);
            timetableGrid.add(timeLabel, j + 1, 0);
        }

        // Add day labels to the first row
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < numDays; i++) {
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

    /**
     * Function to insert all Schedules into the Timetable
     * @param schedules ArrayList of Schedules
     */

    public void populateTimetable(ArrayList<Schedule> schedules) {
        // Clear the timetable before populating it
        clear();
        schedules.sort(Comparator.comparing(Schedule::getStartTime));
        ArrayList<Schedule> overlappingSchedules = new ArrayList<>();
        ArrayList<Schedule> nonOverlappingSchedules = new ArrayList<>();

        for (Schedule schedule : schedules) {
            if (hasOverlap(schedule, nonOverlappingSchedules)) {
                overlappingSchedules.add(schedule);
            } else {
                nonOverlappingSchedules.add(schedule);
            }
        }

        for (Schedule schedule : nonOverlappingSchedules) {
            if (hasOverlap(schedule, overlappingSchedules)) {
                nonOverlappingSchedules.remove(schedule); //Cross-check overlaps
                overlappingSchedules.add(schedule);
            } else {
                System.out.println("Non-Overlapped:" + schedule);
            }
        }

        for (Schedule schedule : overlappingSchedules) {
            System.out.println("Overlapped:" + schedule);
        }
        System.out.println("I managed to reach here");
        populateCellsForSchedule(overlappingSchedules, nonOverlappingSchedules);
    }

    private boolean hasOverlap(Schedule schedule, ArrayList<Schedule> schedules) {
        for (Schedule existingSchedule : schedules) {
            if (schedule.getStartTime().isBefore(existingSchedule.getEndTime()) &&
                    schedule.getEndTime().isAfter(existingSchedule.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function to create cells for a Schedule into the Timetable
     * @param overlappingSchedules Schedules that overlaps
     */

    private void populateCellsForSchedule(ArrayList<Schedule> overlappingSchedules, ArrayList<Schedule> nonOverlappingSchedules) {
        for (Schedule nonOverlap : nonOverlappingSchedules) {
            // Calculate the row index for the start time of the schedule
            LocalDateTime startTime = nonOverlap.getStartTime();
            int startRowIndex = calculateRowIndex(startTime);

            // Calculate the row index for the end time of the schedule
            LocalDateTime endTime = nonOverlap.getEndTime();
            int endRowIndex = calculateRowIndex(endTime);

            // Determine the rowSpan for the schedule based on its duration
            int rowSpan = endRowIndex - startRowIndex + 1;

            // Calculate the column index for the day of the schedule
            DayOfWeek dayOfWeek = startTime.getDayOfWeek();
            int columnIndex = dayOfWeek.getValue(); // Adjust for 0-based indexing
            Node scheduleNode = createScheduleCell(nonOverlap);
            if (rowSpan > 2) {
                GridPane.setColumnSpan(scheduleNode, rowSpan);
            }
            timetableGrid.add(scheduleNode, startRowIndex + 1, columnIndex);
        }
        overlappingSchedules.sort(Comparator.comparing(Schedule::getStartTime));
        Iterator<Schedule> iterator = overlappingSchedules.iterator();
        while (iterator.hasNext()) {
            Schedule overlap = iterator.next();
            ArrayList<Schedule> currentOverlaps = new ArrayList<>();
            currentOverlaps.add(overlap); // Add the current overlap to the list
            ArrayList<Schedule> schedulesToRemove = new ArrayList<>(); // New list to collect schedules to remove
            schedulesToRemove.add(overlap);
            iterator.remove(); // Remove the current overlap from the list
            iterator = overlappingSchedules.iterator();
            while (iterator.hasNext()) {
                Schedule schedule = iterator.next();
                if (overlap.getStartTime().isBefore(schedule.getEndTime()) &&
                        overlap.getEndTime().isAfter(schedule.getStartTime())) {
                    schedulesToRemove.add(schedule); // Add the overlapping schedule to the list
                    currentOverlaps.add(schedule);
                }
            }
            // Remove the schedules after the loop completes
            overlappingSchedules.removeAll(schedulesToRemove);
            LocalDateTime earliestStartTime = overlap.getStartTime();
            LocalDateTime latestEndTime = overlap.getEndTime();
            DayOfWeek dayOfWeek = earliestStartTime.getDayOfWeek();
            int columnIndex = dayOfWeek.getValue();
            for (Schedule schedule : currentOverlaps) {
                earliestStartTime = earliestStartTime.isBefore(schedule.getStartTime()) ?
                        earliestStartTime : schedule.getStartTime();
                latestEndTime = latestEndTime.isAfter(schedule.getEndTime()) ?
                        latestEndTime : schedule.getEndTime();
            }
            Node scheduleNode = createOverlapScheduleCell(currentOverlaps);
            int rowSpan = calculateRowIndex(latestEndTime) - calculateRowIndex(earliestStartTime) + 1;
            if (rowSpan > 2) {
                GridPane.setColumnSpan(scheduleNode, rowSpan);
            }
            timetableGrid.add(scheduleNode, calculateRowIndex(earliestStartTime) + 1, columnIndex);
        }
    }

    private Node createOverlapScheduleCell(ArrayList<Schedule> overlappingSchedules) {
        StackPane cellPane = new StackPane();
        cellPane.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");

        // Create a label to display the overlapped schedule information
        Label label = new Label("Overlapping Schedules");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);

        // Create a tooltip to show the overlapped schedule names
        StringBuilder tooltipText = new StringBuilder("Overlapping Schedules:\n");
        for (Schedule overlappingSchedule : overlappingSchedules) {
            tooltipText.append(overlappingSchedule.getSchedName()).append("\n");
        }
        Tooltip tooltip = new Tooltip(tooltipText.toString());
        label.setTooltip(tooltip);

        // Add the label to the cell pane
        cellPane.getChildren().add(label);

        // Set the alignment of the label within the cell pane
        StackPane.setAlignment(label, Pos.CENTER);

        // Set the preferred size of the cell
        cellPane.setPrefWidth(Region.USE_PREF_SIZE);
        cellPane.setPrefHeight(Region.USE_PREF_SIZE);

        return cellPane;
    }

    private int calculateRowIndex(LocalDateTime time) {
        LocalDateTime startTimeOfDay = time.toLocalDate().atStartOfDay().plusHours(8);
        int minutesFromStart = (int) startTimeOfDay.until(time, ChronoUnit.MINUTES);
        return minutesFromStart / timeInterval;
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

        // Set the preferred size of the cell
        cellPane.setPrefWidth(Region.USE_PREF_SIZE);
        cellPane.setPrefHeight(Region.USE_PREF_SIZE);
        // Return the cell pane
        return cellPane;
    }

    /**
     * Function to clear all Schedule from timetable
     */
    public void clear() {
        timetableGrid.getChildren().clear();
        initializeTimetable();
    }
}
