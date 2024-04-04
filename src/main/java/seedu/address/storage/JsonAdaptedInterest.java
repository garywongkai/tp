package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.tag.Interest;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Interest}.
 */
public class JsonAdaptedInterest extends JsonAdaptedTag {

    /**
     * Constructs a {@code JsonAdaptedInterest} with the given tag name.
     * @param tagName The name of the interest.
     */
    @JsonCreator
    public JsonAdaptedInterest(String tagName) {
        super(tagName);
    }

    /**
     * Constructs a {@code JsonAdaptedInterest} with the given {@code Interest}
     * object.
     * @param source The source interest object.
     */
    public JsonAdaptedInterest(Interest source) {
        super(source);
    }

    /**
     * Converts this Jackson-friendly adapted tag object into the model's
     * {@code Interest} object.
     * @return The converted {@code Interest} object.
     * @throws IllegalValueException If there were any data constraints violated in
     *                               the adapted tag.
     */
    @Override
    public Interest toModelType() throws IllegalValueException {
        if (!Tag.isValidTagName(super.getTagName())) {
            throw new IllegalValueException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Interest(super.getTagName());
    }

}
