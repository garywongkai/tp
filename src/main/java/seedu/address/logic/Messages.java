package seedu.address.logic;

import static seedu.address.model.schedule.Schedule.getDateTimeStringFormat;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.Schedule;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_INVALID_SCHEDULE_DISPLAYED_INDEX = "The schedule index provided is invalid.";
    public static final String MESSAGE_INVALID_GROUP_DISPLAYED_INDEX = "The group indicator provided is invalid."
            + "Please choose either 'y' or 'n'.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_GROUP_SCHEDULE =
            "The schedule you are trying to edit is a group schedule. "
                    + "Would you like to edit all of the schedules for the participants? (editYes/editNo)";
    public static final String MESSAGE_INVALID_DATETIME_FORMAT = "The date format provided is invalid. "
            + "Format: " + getDateTimeStringFormat();
    public static final String MESSAGE_OUT_SCOPE_DATETIME = "The schedule time should be between 8:00 to 21:00. "
            + "Format: " + getDateTimeStringFormat();
    public static final String MESSAGE_DIFFERENT_DATE =
            "The schedule start time and end time should be in the same day. "
            + "Format: " + getDateTimeStringFormat();

    public static final String MESSAGE_START_LATE_THAN_END =
            "The schedule start time should be earlier than end time. "
                    + "Format: " + getDateTimeStringFormat();
    public static final String MESSAGE_SCHEDULE_NAME_SPACE =
            "The schedule name should not be empty.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code schedule} for display to the user.
     */
    public static String format(Schedule schedule) {
        final StringBuilder builder = new StringBuilder();
        builder.append(schedule.getSchedName())
                .append("; StartTime: ")
                .append(schedule.getStartTime())
                .append("; EndTime: ")
                .append(schedule.getEndTime())
                .append("; Participants: ")
                .append(schedule.getParticipantsName());
        return builder.toString();
    }

}
