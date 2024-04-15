package seedu.address.model.schedule;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a Schedule in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidSchedName(String)},
 * timings are valid as declared in {@link #isValidTiming(LocalDateTime, LocalDateTime)}
 */
public class Schedule {

    public static final String MESSAGE_CONSTRAINTS = "Schedule names should be alphanumeric";
    public static final String MESSAGE_CONSTRAINTS_STARTAFTEREND = "Start datetime should not be after end datetime";
    public static final String VALIDATION_REGEX = "\\s?\\p{Alnum}+[\\s?\\p{Alnum}*]*";
    public static final String DATETIME_STRING = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter CUSTOM_DATETIME = DateTimeFormatter.ofPattern(DATETIME_STRING);
    public static final DateTimeFormatter DATETIME_SHOW = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mma");
    private final String schedName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private ArrayList<String> personList;

    /**
     * Constructs a {@code Schedule}.
     *
     * @param schedName A valid schedule name
     * @param startTime A valid start time
     * @param endTime A valid end time
     */

    public Schedule(String schedName, LocalDateTime startTime,
                    LocalDateTime endTime) {
        requireNonNull(schedName);
        checkArgument(isValidSchedName(schedName), MESSAGE_CONSTRAINTS);
        checkArgument(isValidTiming(startTime, endTime), MESSAGE_CONSTRAINTS_STARTAFTEREND);
        this.schedName = schedName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.personList = new ArrayList<String>();
    }

    /**
     * Constructs a {@code Schedule}.
     *
     * @param schedName A valid schedule name
     * @param startTime A valid start time
     * @param endTime A valid end time
     * @param personList A valid list of participants
     */
    public Schedule(String schedName, LocalDateTime startTime,
                    LocalDateTime endTime, ArrayList<String> personList) {
        requireNonNull(schedName);
        checkArgument(isValidSchedName(schedName), MESSAGE_CONSTRAINTS);
        checkArgument(isValidTiming(startTime, endTime));
        this.schedName = schedName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.personList = personList;
    }

    public String getSchedName() {
        return schedName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setPersonList(ArrayList<String> newPersonList) {
        personList = newPersonList;
    }

    public ArrayList<String> getPersonList() {
        return personList;
    }

    public String getParticipantsName() {
        StringBuilder participants = new StringBuilder();

        for (int i = 0; i < personList.size(); i++) {
            participants.append("(").append(i + 1).append(") ");
            participants.append(personList.get(i));
            participants.append(", ");

        }
        String res = participants.toString();
        if (!res.isEmpty()) {
            return res.substring(0, res.length() - 2);
        }
        return res;
    }

    /**
     * Add new person(s) into personList if they are not added yet
     *
     * @param newParticipants
     */
    public void addParticipants(ArrayList<String> newParticipants) {
        for (String p: newParticipants) {
            if (!personList.contains(p)) {
                personList.add(p);
            }
        }
    }

    public void removePerson(String name) {
        personList.remove(name);
    }

    /**
     * Returns true if a given string is a valid schedule name.
     */
    public static boolean isValidSchedName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if startTime before endTime
     */
    public static boolean isValidTiming(LocalDateTime startTime, LocalDateTime endTime) {
        return !startTime.isAfter(endTime);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSameSchedule(Schedule otherSchedule) {
        if (otherSchedule == this) {
            return true;
        }

        return otherSchedule != null
                && otherSchedule.getSchedName().equals(getSchedName());
    }

    /**
     * Returns true if both schedules have the same identity and data fields.
     * This defines a stronger notion of equality between two schedules.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Schedule)) {
            return false;
        }

        Schedule otherSched = (Schedule) other;
        return schedName.equals(otherSched.schedName)
                && startTime.equals(otherSched.startTime)
                && endTime.equals(otherSched.endTime)
                && personList.equals(otherSched.personList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schedName, startTime, endTime, personList);
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return schedName + "\n"
                + "\tstart: " + startTime.format(DATETIME_SHOW) + "\n"
                + "\tend: " + endTime.format(DATETIME_SHOW) + "\n"
                + "\tParticipants: " + getParticipantsName() + "\n";
    }

    /**
     * Format state as text for viewing with participants list.
     */
    public String toStringWithoutParticipants() {
        return schedName + "\n"
                + "\tstart: " + startTime.format(DATETIME_SHOW) + "\n"
                + "\tend: " + endTime.format(DATETIME_SHOW) + "\n";
    }

    /**
     * Returns custom date time format used
     */
    public static DateTimeFormatter getScheduleDateTimeFormatter() {
        return CUSTOM_DATETIME;
    }

    /**
     * Returns custom date time format used in string
     */
    public static String getDateTimeStringFormat() {
        return DATETIME_STRING;
    }
}
