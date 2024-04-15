package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.Schedule;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;

    private final FilteredList<Schedule> filteredSchedules;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredSchedules = new FilteredList<>(this.addressBook.getScheduleList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public boolean hasSchedule(Schedule schedule) {
        requireNonNull(schedule);
        return addressBook.hasSchedule(schedule);
    }

    @Override
    public void deletePerson(Person target) {
        for (int i = 0; i < target.getSchedules().size(); i++) {
            //System.out.println(target.getSchedules().get(i));
            addressBook.removeSchedule(target, target.getSchedules().get(i));
            updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
        }
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        System.out.println("Address book = " + addressBook.getPersonList().toString());
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void deleteSchedule(Schedule toDeleteSchedule) {
        addressBook.removeSchedule(toDeleteSchedule);
        updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
    }

    @Override
    public void deleteSchedule(Person toDeleteParticipant, Schedule toDeleteSchedule) {
        addressBook.removeSchedule(toDeleteParticipant, toDeleteSchedule);
        updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
    }

    @Override
    public void addSchedulePeople(Schedule schedule, ArrayList<String> personName) {
        if (addressBook.hasSchedule(schedule)) {
            Schedule toEdit = addressBook.getSameSchedule(schedule);
            for (String names : personName) {
                if (!toEdit.getPersonList().contains(names)) {
                    toEdit.getPersonList().add(names);
                }
            }
            //toEdit.getPersonList().addAll(personName);
        } else {
            addressBook.addSchedule(schedule);
            updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
        }
    }

    @Override
    public void addSchedule(Schedule schedule) {
        addressBook.addSchedule(schedule);
        updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
    }

    @Override
    public void addSchedule(Schedule schedule, ArrayList<Person> participantsList) {
        addressBook.addSchedule(schedule, participantsList);
        updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        for (int i = 0; i < target.getSchedules().size(); i++) {
            Schedule sched = target.getSchedules().get(0);
            Schedule currentSched = new Schedule(sched.getSchedName(), sched.getStartTime(), sched.getEndTime(),
                    sched.getPersonList());
            ArrayList<String> curPersonList = (ArrayList<String>) currentSched.getPersonList().clone();
            ArrayList<String> editPersonList = (ArrayList<String>) currentSched.getPersonList().clone();
            editPersonList.remove(target.getName().toString());
            editPersonList.add(editedPerson.getName().toString());
            Schedule editSched = new Schedule(currentSched.getSchedName(), currentSched.getStartTime(),
                    currentSched.getEndTime(), editPersonList);
            System.out.println("person list: " + addressBook.getPersonList());
            System.out.println("current schedule: " + currentSched);
            System.out.println("editSched: " + editSched);
            for (int personIndex = 0; personIndex < addressBook.getPersonList().size(); personIndex++) {
                Person currentPerson = addressBook.getPersonList().get(personIndex);
                if (currentPerson.equals(target)) {
                    editedPerson.deleteSchedule(currentSched);
                    editedPerson.addSchedule(editSched);
                    currentPerson.deleteSchedule(currentSched);
                    currentPerson.addSchedule(editSched);
                    addressBook.setPerson(addressBook.getPersonList().get(personIndex), currentPerson);
                } else if (curPersonList.contains(currentPerson.getName().toString())) {
                    currentPerson.deleteSchedule(currentSched);
                    currentPerson.addSchedule(editSched);
                    addressBook.setPerson(addressBook.getPersonList().get(personIndex), currentPerson);
                }
            }
            addressBook.setSchedule(currentSched, editSched);
            updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
        }
        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public void setSchedule(Schedule target, Schedule editedSchedule) {
        requireAllNonNull(target, editedSchedule);

        addressBook.setSchedule(target, editedSchedule);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public ObservableList<Schedule> getFilteredScheduleList() {
        return filteredSchedules;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updateFilteredScheduleList(Predicate<Schedule> predicate) {
        requireNonNull(predicate);
        filteredSchedules.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
