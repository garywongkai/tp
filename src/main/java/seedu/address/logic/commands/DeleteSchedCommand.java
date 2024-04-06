package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SPECIFIC_SCHEDULE_INDEX;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.List;

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
public class DeleteSchedCommand extends Command {

    public static final String COMMAND_WORD = "deleteSched";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a schedule in address book. "
            + "Parameters: "
            + "Person INDEX(S) (must be positive integer) "
            + PREFIX_SPECIFIC_SCHEDULE_INDEX + "Schedule INDEX(S) (must be positive integer) \n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_SPECIFIC_SCHEDULE_INDEX + " 1";

    public static final String MESSAGE_SUCCESS = "The schedule deleted: %1$s";

    private final Index deletePersonIndex;

    private final Index deleteScheduleIndex;



    /**
     * Creates an DeleteCommand to delete the specified {@code Schedule}
     */
    public DeleteSchedCommand(Index deletePersonIndex, Index deleteScheduleIndex) {
        this.deletePersonIndex = deletePersonIndex;
        this.deleteScheduleIndex = deleteScheduleIndex;
    }

    /**
     * Deletes a {@code Schedule}
     *
     * @param model model used
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> personList = model.getFilteredPersonList();

        if (deletePersonIndex.getZeroBased() >= personList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person personToDelete = personList.get(deletePersonIndex.getZeroBased());

        ArrayList<Schedule> personScheduleList = personToDelete.getSchedules();

        if (deleteScheduleIndex.getZeroBased() >= personScheduleList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_SCHEDULE_DISPLAYED_INDEX);
        }
        Schedule scheduleToDelete = personToDelete.getSchedules().get(deleteScheduleIndex.getZeroBased());

        deleteSchedForSpecificPerson(model, scheduleToDelete, personToDelete);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(scheduleToDelete)));
    }


    /**
     * Deletes a {@code Schedule} for specific participants
     *
     * @param model model used
     * @param scheduleToDelete the schedule to delete
     * @param personToDelete specified participant to delete schedule from
     */
    private void deleteSchedForSpecificPerson(Model model, Schedule scheduleToDelete,
                                               Person personToDelete) {
        //scheduleToDelete.removePerson(personToDelete.getName().toString());
        model.deleteSchedule(personToDelete, scheduleToDelete);
        if (!scheduleToDelete.getPersonList().isEmpty()) {
            for (Person p: model.getFilteredPersonList()) {
                if (!p.equals(personToDelete)) {
                    if (p.getSchedules().contains(scheduleToDelete)) {
                        p.getSchedules().forEach(schedule -> {
                            if (schedule.isSameSchedule(scheduleToDelete)) {
                                schedule.removePerson(personToDelete.getName().toString());
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteSchedCommand)) {
            return false;
        }

        DeleteSchedCommand dsc = (DeleteSchedCommand) other;
        return deletePersonIndex.equals(dsc.deletePersonIndex) || deleteScheduleIndex.equals(dsc.deleteScheduleIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("Person index", deletePersonIndex)
                .add("Schedule index", deleteScheduleIndex)
                .toString();
    }
}


