package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCHEDULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_SCHEDULES;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.Schedule;

/**
 * Adds a schedule to the address book.
 */
public class EditSchedCommand extends Command {

    public static final String COMMAND_WORD = "editSched";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edit a schedule in address book. "
            + "Parameters: "
            + "PERSON INDEX(S) (must be positive integer) "
            + PREFIX_TASK + "TASK INDEX(S) (must be positive integer) "
            + "[" + PREFIX_SCHEDULE + " SCHEDULE NAME] "
            + "[" + PREFIX_START + " START DATETIME (yyyy-MM-dd HH:mm)] "
            + "[" + PREFIX_END + " END DATETIME (yyyy-MM-dd HH:mm)] "
            + "Example: " + COMMAND_WORD + " " + "1 "
            + PREFIX_TASK + " 1, 2 "
            + "[" + PREFIX_SCHEDULE + " CS2103 weekly meeting] "
            + "[" + PREFIX_START + " 2024-02-24 15:00] "
            + "[" + PREFIX_END + " 2024-02-24 17:00] ";

    public static final String MESSAGE_EDIT_SCHEDULE_SUCCESS = "Edited Schedule: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_TASK_NOT_SPECIFIED = "No task index has been specified.";
    public static final String MESSAGE_DUPLICATE_SCHEDULE =
            "This schedule already exists in the address book.";

    private final Index personIndex;
    private final Index scheduleIndex;


    private final EditSchedCommand.EditScheduleDescriptor editScheduleDescriptor;


    /**
     * Creates EditSchedCommand object
     *
     * @param personIndex index of person to edit
     * @param scheduleIndex index of schedule to edit
     * @param editScheduleDescriptor to create edited schedule
     */
    public EditSchedCommand(Index personIndex, Index scheduleIndex,
                            EditSchedCommand.EditScheduleDescriptor editScheduleDescriptor) {
        assert personIndex != null;
        assert schedulIndex != null;
        assert editScheduleDescriptor != null;
        requireNonNull(personIndex);
        requireNonNull(scheduleIndex);
        requireNonNull(editScheduleDescriptor);

        this.personIndex = personIndex;
        this.scheduleIndex = scheduleIndex;
        this.editScheduleDescriptor =
                new EditSchedCommand.EditScheduleDescriptor(editScheduleDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        assert model != null;
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person personToChange = model.getFilteredPersonList().get(personIndex.getZeroBased());

        ArrayList<Schedule> personScheduleList = personToChange.getSchedules();
        if (scheduleIndex.getZeroBased() >= personScheduleList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Schedule scheduleToEdit = personScheduleList.get(scheduleIndex.getZeroBased());

        deleteSchedForSpecificPerson(model, scheduleToEdit, personToChange);

        Schedule editedSchedule = createEditedSchedule(scheduleToEdit, editScheduleDescriptor);

        Person personChanged = personToChange;
        personChanged.addSchedule(editedSchedule);

        model.updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
        return new CommandResult(String.format(MESSAGE_EDIT_SCHEDULE_SUCCESS,
                Messages.format(editedSchedule)));
    }

    private void deleteSchedForSpecificPerson(Model model, Schedule scheduleToDelete,
                                              Person personToDelete) {
        model.deleteSchedule(scheduleToDelete);
        Schedule scheduleToAdd = scheduleToDelete;
        scheduleToAdd.removePerson(personToDelete.getName().toString());
        if (!scheduleToDelete.getPersonList().isEmpty()) {
            model.addSchedule(scheduleToAdd);
        }
        for (Person p: model.getFilteredPersonList()) {
            if (!p.getSchedules().contains(scheduleToDelete)) {
                continue;
            }
            Person personChanged = p;
            personChanged.deleteSchedule(scheduleToDelete);
            if (!p.equals(personToDelete)) {
                personChanged.addSchedule(scheduleToAdd);
            }
            model.setPerson(p, personChanged);
        }
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Schedule createEditedSchedule(Schedule scheduleToEdit,
                                                 EditSchedCommand.EditScheduleDescriptor editScheduleDescriptor) throws CommandException {
        assert scheduleToEdit != null;

        String updatedSchedName = editScheduleDescriptor.getSchedName().orElse(scheduleToEdit.getSchedName());
        LocalDateTime updatedStartTime = editScheduleDescriptor.getStartTime().orElse(scheduleToEdit.getStartTime());
        LocalDateTime updatedEndTime = editScheduleDescriptor.getEndTime().orElse(scheduleToEdit.getEndTime());

        return new Schedule(updatedSchedName, updatedStartTime, updatedEndTime);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditSchedCommand)) {
            return false;
        }

        EditSchedCommand otherEditSchedCommand = (EditSchedCommand) other;
        return personIndex.equals(otherEditSchedCommand.personIndex)
                && scheduleIndex.equals(otherEditSchedCommand.scheduleIndex)
                && editScheduleDescriptor.equals(otherEditSchedCommand.editScheduleDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", personIndex)
                .add("scheduleIndex", scheduleIndex)
                .add("editScheduleDescriptor", editScheduleDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditScheduleDescriptor {
        private Index schedtaskIndex;
        private String schedName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public EditScheduleDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditScheduleDescriptor(EditSchedCommand.EditScheduleDescriptor toCopy) {
            setSchedTask(toCopy.schedtaskIndex);
            setSchedName(toCopy.schedName);
            setStartTime(toCopy.startTime);
            setEndTime(toCopy.endTime);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(schedName, startTime, endTime);
        }

        public void setSchedTask(Index schedtaskIndex) {
            this.schedtaskIndex = schedtaskIndex;
        }
        public Optional<Index> getSchedTask() {
            return Optional.ofNullable(schedtaskIndex);
        }
        public void setSchedName(String schedName) {
            this.schedName = schedName;
        }

        public Optional<String> getSchedName() {
            return Optional.ofNullable(schedName);
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public Optional<LocalDateTime> getStartTime() {
            return Optional.ofNullable(startTime);
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public Optional<LocalDateTime> getEndTime() {
            return Optional.ofNullable(endTime);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditSchedCommand.EditScheduleDescriptor)) {
                return false;
            }

            EditSchedCommand.EditScheduleDescriptor otherEditPersonDescriptor =
                    (EditSchedCommand.EditScheduleDescriptor) other;
            return Objects.equals(schedName, otherEditPersonDescriptor.schedName)
                    && Objects.equals(startTime, otherEditPersonDescriptor.startTime)
                    && Objects.equals(endTime, otherEditPersonDescriptor.endTime);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", schedName)
                    .add("phone", startTime)
                    .add("email", endTime)
                    .toString();
        }
    }
}
