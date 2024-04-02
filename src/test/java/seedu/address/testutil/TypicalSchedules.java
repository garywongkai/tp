package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_END_5PM;
import static seedu.address.logic.commands.CommandTestUtil.VALID_END_8PM;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SCHEDULE_DISCUSSION;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SCHEDULE_MEETING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_START_10AM;
import static seedu.address.logic.commands.CommandTestUtil.VALID_START_3PM;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import seedu.address.model.AddressBook;
import seedu.address.model.schedule.Schedule;

/**
 * A utility class containing a list of {@code Schedule} objects to be used in tests.
 */
public class TypicalSchedules {
    public static final LocalDateTime START_10AM = LocalDateTime.parse(VALID_START_10AM,
            Schedule.CUSTOM_DATETIME);
    public static final LocalDateTime START_3PM = LocalDateTime.parse(VALID_START_3PM,
            Schedule.CUSTOM_DATETIME);
    public static final LocalDateTime END_5PM = LocalDateTime.parse(VALID_END_5PM,
            Schedule.CUSTOM_DATETIME);
    public static final LocalDateTime END_8PM = LocalDateTime.parse(VALID_END_8PM,
            Schedule.CUSTOM_DATETIME);
    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final Schedule MEETING = new ScheduleBuilder().withSchedName(VALID_SCHEDULE_MEETING)
            .withStartTime(START_10AM).withEndTime(END_5PM).build();
    public static final Schedule DISCUSSION = new ScheduleBuilder().withSchedName(VALID_SCHEDULE_DISCUSSION)
            .withStartTime(START_3PM).withEndTime(END_8PM).build();

    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Schedule schedule : getTypicalSchedules()) {
            ab.addSchedule(schedule, getTypicalPersons());
        }
        return ab;
    }

    public static ArrayList<Schedule> getTypicalSchedules() {
        return new ArrayList<>(Arrays.asList(MEETING, DISCUSSION));
    }
}
