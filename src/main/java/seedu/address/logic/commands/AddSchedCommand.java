package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCHEDULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_SCHEDULES;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.Schedule;

/**
 * Adds a schedule to the address book.
 */
public class AddSchedCommand extends Command {

    public static final String COMMAND_WORD = "addSched";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a schedule to person(s) in address book. "
            + "Parameters: "
            + "PERSON INDEX(S) (must be positive integer) "
            + PREFIX_SCHEDULE + "SCHEDULE "
            + PREFIX_START + "START_DATETIME (yyyy-MM-dd HH:mm) "
            + PREFIX_END + "END_DATETIME (yyyy-MM-dd HH:mm) "
            + "(START_DATETIME and END_DATETIME must be in the same day and between 08:00 and 21:00)\n"
            + "Example: " + COMMAND_WORD + " "
            + "1, 2 "
            + PREFIX_SCHEDULE + "CS2103 weekly meeting "
            + PREFIX_START + "2024-02-24 09:00 "
            + PREFIX_END + "2024-02-24 17:00";

    public static final String MESSAGE_SUCCESS = "New schedule added: %1$s";
    private static final String MESSAGE_DUPLICATE = "Schedule has already been added: %1$s";

    private final ArrayList<Index> targetIndexes;

    private final Schedule schedule;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddSchedCommand(ArrayList<Index> targetIndexes, Schedule schedule) {
        requireNonNull(schedule);
        this.targetIndexes = targetIndexes;
        this.schedule = schedule;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        ArrayList<Person> participants = new ArrayList<Person>();
        ArrayList<String> participantsNames = new ArrayList<String>();
        for (Index index : targetIndexes) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            participants.add(lastShownList.get(index.getZeroBased()));
            participantsNames.add(lastShownList.get(index.getZeroBased()).getName().toString());
        }
        schedule.addParticipants(participantsNames);
        if (model.hasSchedule(schedule)) {
            model.addSchedulePeople(schedule, participantsNames);
            ObservableList<Schedule> arraySched = model.getAddressBook().getScheduleList();
            arraySched.forEach(schedule1 -> {
                if (schedule1.isSameSchedule(schedule)) {
                    schedule1.setPersonList(schedule.getPersonList());
                }
            });
            System.out.println("i was called cause duplicate");
            for (String name : participantsNames) {
                model.getFilteredPersonList().forEach(person -> {
                    if (Objects.equals(person.getName().toString(), name)) {
                        if (!person.getSchedules().contains(schedule)) {
                            // add schedule to the people involved
                            person.addSchedule(schedule);
                        } else {
                            int index = person.getSchedules().indexOf(schedule);
                            person.getSchedules().get(index).addParticipants(participantsNames);
                        }
                    }
                });
            }
        } else {
            for (Person p : participants) {
                p.addSchedule(schedule);
            }
            model.addSchedule(schedule);
        }
        model.updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
        return new CommandResult(generateSuccessMessage());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddSchedCommand)) {
            return false;
        }

        AddSchedCommand asc = (AddSchedCommand) other;
        return targetIndexes.equals(asc.targetIndexes)
                && schedule.equals(asc.schedule);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("person indexes", targetIndexes)
                .add("Schedule", schedule)
                .toString();
    }

    /**
     * Generates a command execution success message based on whether
     * the remark is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage() {
        return String.format(MESSAGE_SUCCESS, schedule);
    }

    private String generateDuplicateMessage() {
        return String.format(MESSAGE_DUPLICATE, schedule);
    }
}

