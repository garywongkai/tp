package moddie.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import moddie.address.commons.exceptions.DataLoadingException;
import moddie.address.model.ReadOnlyAddressBook;
import moddie.address.model.ReadOnlyUserPrefs;
import moddie.address.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage extends AddressBookStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getAddressBookFilePath();

    @Override
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException;

    @Override
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

}
