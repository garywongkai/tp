# Moddie User Guide

Moddie is a **desktop app for managing contacts, optimized for use via a Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, AB3 can get your contact management tasks done faster than traditional GUI apps.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `11` or above installed in your Computer.

1. Download the latest `Moddie.jar` from [here](https://github.com/AY2324S2-CS2103-F15-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your Moddie.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar Moddie.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help : `help`

Shows a message explaning how to access the help page.

Format: `help`

Expected success outcome:
![help message](images/helpMessage.png)

Expected failure outcome:
```
Help not available. Please try again.
```

### Adding a person: `add`

Adds a person to the address book with their information.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG] [i/INTEREST]…​`

* Phone number **must be a valid Singapore number** (i.e. 8 digits, starts with either 6, 8 or 9)
* Email **must include @ character**
* Address **must include and be ordered in street name, block number, and unit number (note: include # symbol)**,
separated with comma
* If multiple `tag` are added, separate with comma
* if multiple `interest` are added, separate with comma

**Tip:** A person can have any number of tags or interests (including 0)

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal i/hunting`

Expected success outcome:
```
New contact added!
```

Expected failure outcome:
```
Values not accepted.
```

Potential Errors:
* Phone number format is wrong (i.e. not a Singapore number)
* Email format is wrong (i.e. no @)
* Address format is wrong
* An existing contact with same name and phone number is found in address book


### Listing all persons : `list`

Shows a list of all persons in the address book.

Format: `list`

Expected success outcome:
```
List of contacts:
...
```

Expected failure outcome:
```
No contacts added yet.
```


### Editing a person : `edit`

Edits an existing person's information in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG] [i/INTEREST]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* When editing interests, the existing interests of the person will be removed 
  i.e adding of interests is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.
* * You can remove all the person’s tags by typing `i/` without
    specifying any interests after it.
* Adding a person's format for **phone number, email, and address** applies here as well.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com i/Bird Watching` Edits the phone number, email address and interest of the 
* 1st person to be `91234567`, `johndoe@example.com` and `Bird Watching` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

Expected success outcome:
```
Contact is updated!
```

Expected failure outcome:
```
Values not accepted.
```
OR
```
Contact not found in address book
```

Potential Errors:
* [if applicable] Phone number format is wrong (i.e. not a Singapore number)
* [if applicable] Email format is wrong (i.e. no @)
* [if applicable] Address format is wrong
* An existing contact with same name and phone number is found in address book


### Locating persons by name : `find`

Finds persons whose names, phone number, email, address or tag contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g `hans` will match `Hans`
* The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`
* Only full words will be matched e.g. `Han` will not match `Hans`
* Persons matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
* `find 9123` returns the phone numbers `91236547` and `23912376`
* `find gmail` returns the emails `alexY@gmail.com` and `davidL@gmail.com`
* `find road` returns the address `Upper Thomson Road` and `Lower Kent Ridge Road`
* `find friend` returns anyone with the tag `friend`
  ![result for 'find alex david'](images/findAlexDavidResult.png)

Expected success outcome:
```
xx persons listed!
...
```

Expected failure outcome:
```
0 persons listed!
```


### Deleting a person : `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

Expected success outcome:
```
Contact is updated!
```

Expected failure outcome:
```
Values not accepted.
```
OR
```
Contact not found in address book
```


### Clearing all entries : `clear`

Clears all entries from the address book.

Format: `clear`

Expected success outcome:
```
History cleared
```

Expected failure outcome:
```
History not cleared
```

### Adding persons to schedule : `addSched`

Adds an event with contact from specified date with time

Format: `addSched PERSON_INDEX [MORE_PERSON_INDEX] s/SCHEDULE_NAME start/START_DATETIME end/END_DATETIME`

* The PERSON_INDEX **must be a positive integer** 1, 2, 3, …​ and must be in range of the
    number of people in the address book
* The SCHEDULE_NAME **must not have any special characters** e.g. !, @, #, $, …​
* The START_DATETIME must be in the format of yyyy-MM-dd HH:mm in 24-hour time
* The END_DATETIME must be in the format of yyyy-MM-dd HH:mm in 24-hour time
* `find Betsy` followed by `addSched 4 s/Exam start/2024-03-05 16:00 end/2024-03-05 18:00` adds the 1st person in
   the results of the `find` command to the event stated.

Examples:
* `addSched 4 s/Exam start/2024-03-05 16:00 end/2024-03-05 18:00` will add the 4th person in the address list to the `Exam` event which
would take place on 5th March 2024 from 4pm - 6pm
* `addSched 1, 2, 3 s/CSMeeting start/2024-03-18 13:00 end/2024-03-18 19:00` will add the 1st, 2nd and 3rd persons in the address list
to the `CSMeeting` event which would take place on 18th March 2024 from 3pm - 7pm

Expected success outcome:
```
Added schedule with ...
```

Expected failure outcome:
```
Schedule failed to be added.
```

Potential Errors:
* Time format is wrong!
* Date format is wrong
* Contact not found in address book

### Deleting a schedule: `deleteSched`

Deletes a schedule that associated with a person

Format: `deleteSched PERSON_INDEX schedule/SCHEDULE_INDEX`

* The PERSON_INDEX **must be a positive integer** 1, 2, 3, …​ and must be in range of the 
   number of people in the address book.
* The SCHEDULE_INDEX **must be a positive integer** 1, 2, 3 …​ and must be in range of the number of schedules in 
   the schedule list for the person from PERSON_INDEX.
* `find Betsy` followed by `deleteSched 1 schedule/2` deletes the 2nd schedule from the 1st person in
   the results of the `find` command.

Examples:
* `deleteSched 1 schedule/2` will delete the 2nd schedule from the 1st person in the address list

Expected success outcome:
```
Delete schedule from ...
```

Expected failure outcome:
```
Schedule failed to be deleted.
```

Potential Errors:
* Contact not found in address book
* Schedule not found in schedule list of person

### Editing a schedule: `editSched`

Edit a schedule that associated with a person with new information

Format: `editSched PERSON_INDEX schedule/SCHEDULE_INDEX [s/SCHEDULE_NAME] [start/START_DATETIME] [end/END_DATETIME]`

* The PERSON_INDEX **must be a positive integer** 1, 2, 3, …​ and must be in range of the
  number of people in the address book.
* The SCHEDULE_INDEX **must be a positive integer** 1, 2, 3 …​ and must be in range of the number of schedules in
  the schedule list for the person from PERSON_INDEX.
* The SCHEDULE_NAME **must not have any special characters** e.g. !, @, #, $, …​
* The START_DATETIME must be in the format of yyyy-MM-dd HH:mm in 24-hour time
* The END_DATETIME must be in the format of yyyy-MM-dd HH:mm in 24-hour time
* There must be at least 1 input for SCHEDULE_NAME, START_DATETIME or END_DATETIME, 
   or the command would not be accepted.
* `find Betsy` followed by `editSched 1 schedule/2 s/CCA meeting` edits the 2nd schedule from the 1st person in
  the results of the `find` command with the new schedule name `CCA meeting`.

Examples:
* `editSched 1 schedule/2 s/CS1101S meeting start/ 2024-02-03 12:00 end/ 2024-02-03 15:00` will 
   edit the 2nd schedule from the 1st person in the address list with the new name `CS1101S meeting` on the 
   new timing from 3rd February 2024 12pm to 3rd February 2024 3pm.
* `editSched 1 schedule/2 s/CS2040S class` will edit the 2nd schedule from the 1st person 
   in the address list with the new name `CS2040S class`.
* `editSched 1 schedule/2 start/ 2024-03-05 11:00 ` will edit the 2nd schedule from the 1st person 
   in the address list with the new starting date time of 5th March 2024 11am.
* `editSched 1 schedule/2 end/ 2024-06-12 20:00` will edit the 2nd schedule from the 1st person 
   in the address list with the new ending date time of 12th June 2024 8pm.

Expected success outcome:
```
Edit schedule from ...
```

Expected failure outcome:
```
Schedule failed to be edited.
```

Potential Errors:
* [if applicable] Time format is wrong!
* [if applicable] Date format is wrong!
* Contact not found in address book
* Schedule not found in schedule list of person
* There is no input for the SCHEDULE_NAME, START_DATETIME and END_DATETIME

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

AddressBook data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, AddressBook will discard all data and start with an empty data file at the next run.  Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Help**   | `help`
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**List**   | `list`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Clear**  | `clear`
**Add Schedule**   | `addSched INDEX [MORE_INDEX] s/SCHEDULE_NAME start/START_DATETIME end/END_DATETIME` <br> e.g. `addSched 1, 2, 3 s/CSMeeting start/2024-02-24 09:00 end/2024-02-24 17:00`
**Delete Schedule**   | `deleteSched PERSON_INDEX schedule/SCHEDULE_INDEX` <br> e.g. `deleteSched 1 schedule/2`
**Edit Schedule**   | `editSched PERSON_INDEX schedule/SCHEDULE_INDEX [s/SCHEDULE_NAME] [start/START_DATETIME] [end/END_DATETIME]` <br> e.g. `editSched 1 schedule/1 s/CS1101S meeting start/ 2024-02-03 12:00 end/ 2024-02-03 15:00`
**Exit**   | `exit`
