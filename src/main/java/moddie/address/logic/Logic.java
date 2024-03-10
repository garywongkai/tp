package moddie.address.logic;

import java.nio.file.Path;

import moddie.address.model.Model;
import moddie.address.model.ReadOnlyAddressBook;
import javafx.collections.ObservableList;
import moddie.address.commons.core.GuiSettings;
import moddie.address.logic.commands.CommandResult;
import moddie.address.logic.commands.exceptions.CommandException;
import moddie.address.logic.parser.exceptions.ParseException;
import moddie.address.model.person.Person;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Returns the AddressBook.
     *
     * @see Model#getAddressBook()
     */
    ReadOnlyAddressBook getAddressBook();

    /** Returns an unmodifiable view of the filtered list of persons */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
