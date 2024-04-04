package seedu.address.ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
    private Pane timetableOverlap;
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
        timetableGrid.setMinWidth(1200);
        timetableGrid.setHgap(10);
    }

    private void initializeTimetable() {
        for (int j = 0; j < 27; j++) {
            LocalTime time = LocalTime.from(startTime.plusMinutes(j * 30));
            Label timeLabel = new Label(time.toString());
            timeLabel.setStyle("-fx-padding: 0 10 0 10;");
            timeLabel.setTextAlignment(TextAlignment.CENTER);
            timeLabel.setId("timeLabel");
            timetableGrid.add(timeLabel, j + 1, 0);
        }

        // Add day labels to the first row
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < numDays; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            if (i == 0) {
                // Set the preferred width of the first day label
                dayLabel.setMinWidth(80); // Adjust the width as needed
            }
            dayLabel.setTextAlignment(TextAlignment.CENTER);
            dayLabel.setId("dayLabel");
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
        ArrayList<Schedule> toRemove = new ArrayList<>();
        for (Schedule schedule : nonOverlappingSchedules) {
            if (hasOverlap(schedule, overlappingSchedules)) {
                toRemove.add(schedule); //Cross-check overlaps
                overlappingSchedules.add(schedule);
            }
        }
        nonOverlappingSchedules.removeAll(toRemove);
        populateCellsForSchedule(overlappingSchedules, nonOverlappingSchedules);
    }

    private boolean hasOverlap(Schedule schedule, ArrayList<Schedule> schedules) {
        for (Schedule existingSchedule : schedules) {
            if (schedule.getStartTime().getDayOfWeek() == existingSchedule.getStartTime().getDayOfWeek()) {
                if (schedule.getStartTime().isBefore(existingSchedule.getStartTime())
                        && (schedule.getEndTime().isBefore(existingSchedule.getEndTime())
                        || schedule.getEndTime().isAfter(existingSchedule.getEndTime()))) {
                    return true;
                }
                if (schedule.getStartTime().isBefore(existingSchedule.getEndTime())
                        && schedule.getEndTime().isAfter(existingSchedule.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Function to create cells for a Schedule into the Timetable
     * @param overlappingSchedules Schedules that overlaps
     */

    private void populateCellsForSchedule(ArrayList<Schedule> overlappingSchedules,
                                          ArrayList<Schedule> nonOverlappingSchedules) {
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
        while (!overlappingSchedules.isEmpty()) {
            Schedule overlap = overlappingSchedules.remove(0);
            ArrayList<Schedule> currentOverlaps = new ArrayList<>();
            currentOverlaps.add(overlap); // Add the earliest overlapped schedule to the list
            Iterator<Schedule> iterator = overlappingSchedules.iterator();
            while (iterator.hasNext()) {
                Schedule schedule = iterator.next();
                if (overlap.getStartTime().isBefore(schedule.getEndTime())
                        && overlap.getEndTime().isAfter(schedule.getStartTime())) {
                    iterator.remove(); // Remove the overlapping schedule from the list
                    currentOverlaps.add(schedule);
                }
            }
            LocalDateTime earliestStartTime = overlap.getStartTime();
            LocalDateTime latestEndTime = overlap.getEndTime();
            DayOfWeek dayOfWeek = earliestStartTime.getDayOfWeek();
            int columnIndex = dayOfWeek.getValue();
            for (Schedule schedule : currentOverlaps) {
                earliestStartTime = earliestStartTime.isBefore(schedule.getStartTime())
                        ? earliestStartTime : schedule.getStartTime();
                latestEndTime = latestEndTime.isAfter(schedule.getEndTime())
                        ? latestEndTime : schedule.getEndTime();
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
        // Create a tooltip to show the overlapped schedule names
        List<Schedule> distinctSchedules = overlappingSchedules.stream().distinct().collect(Collectors.toList());
        StringBuilder tooltipText = (distinctSchedules.size() == 1) ? new StringBuilder("Participants")
                : new StringBuilder("Overlapping Schedules:\n");
        if (distinctSchedules.size() == 1) {
            Label label = new Label("Group Schedules");
            cellPane.setStyle("-fx-border-color: blue; -fx-border-radius: 18; -fx-background-radius: 18;");
            // Create a label to display the overlapped schedule information
            label.setWrapText(true);
            label.setId("GroupScheduleLabel");
            label.setAlignment(Pos.CENTER);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setStyle("-fx-border-color: black;");
            tooltipText.append(distinctSchedules.get(0).getParticipantsName());
            Tooltip tooltip = new Tooltip(tooltipText.toString());
            label.setTooltip(tooltip);

            //Add the label to the cell pane
            cellPane.getChildren().add(label);
            //Set the alignment of the label within the cell pane
            StackPane.setAlignment(label, Pos.CENTER);
        } else {
            // Create a label to display the overlapped schedule information
            Pane timetableOverlap = new Pane();
            int earliestStartTime = Integer.MAX_VALUE;
            int latestEndTime = Integer.MIN_VALUE;
            int scheduleDone = 0;
            for (Schedule overlappingSchedule : distinctSchedules) {
                Label label1 = new Label(overlappingSchedule.getSchedName());
                // Calculate the row index for the start time of the schedule
                LocalDateTime startTime = overlappingSchedule.getStartTime();
                int startRowIndex = calculateRowIndex(startTime);
                earliestStartTime = Math.min(earliestStartTime, startRowIndex);

                // Calculate the row index for the end time of the schedule
                LocalDateTime endTime = overlappingSchedule.getEndTime();
                int endRowIndex = calculateRowIndex(endTime);
                latestEndTime = Math.max(latestEndTime, endRowIndex);
                timetableOverlap.setPrefWidth((latestEndTime - earliestStartTime) * 65);
                timetableOverlap.setPrefHeight(50 * distinctSchedules.size());
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                label1.setId("ScheduleLabel1");
                label1.setPrefWidth((endRowIndex - startRowIndex + 1) * 65);
                label1.setPrefHeight(50);
                int leftPad = ((startRowIndex - earliestStartTime) * 65);
                label1.relocate(leftPad, (scheduleDone * 50));
                scheduleDone++;
                timetableOverlap.getChildren().add(label1);
                tooltipText.append(overlappingSchedule.getSchedName()).append("\n");
            }
            cellPane.getChildren().add(timetableOverlap);
        }

        // Set the preferred size of the cell
        cellPane.setPrefWidth(Region.USE_PREF_SIZE);
        cellPane.setPrefHeight(distinctSchedules.size() * 50);
        //Region.USE_PREF_SIZE

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
        //cellPane.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
        // Create a label to display the schedule information
        Label label = new Label(schedule.getSchedName());
        label.setId("ScheduleLabel");
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
