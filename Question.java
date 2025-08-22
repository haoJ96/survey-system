import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Base abstract class for all survey question types.  Each question has
 * a prompt and optionally supports multiple responses.  Subclasses
 * implement methods to display themselves, solicit answers from the user
 * and modify their configuration.
 */
public abstract class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String prompt;
    /**
     * The number of responses allowed for this question.  A value of 1
     * indicates a single response.  For questions that permit multiple
     * selections (e.g. multi‑answer multiple choice), this field is
     * greater than 1.
     */
    protected int numResponsesAllowed = 1;

    public Question(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Returns the number of responses allowed for this question.
     */
    public int getNumResponsesAllowed() {
        return numResponsesAllowed;
    }

    public void setNumResponsesAllowed(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Number of responses must be positive");
        }
        this.numResponsesAllowed = n;
    }

    /**
     * Displays the question prompt and any additional information
     * necessary for the respondent.
     */
    public abstract void display();

    /**
     * Solicit one or more responses from the user via the provided scanner.
     * The returned list contains each individual response in order.  For
     * questions with a single response only the first element of the list
     * will be used.
     *
     * @param scanner A shared scanner used to read from System.in
     * @return A list of response strings
     */
    public abstract List<String> getUserAnswer(Scanner scanner);

    /**
     * Allows modification of this question.  Concrete subclasses should
     * prompt the user to change the prompt and any other configurable
     * properties.  The provided scanner should be used for reading
     * user input.
     *
     * @param scanner A scanner for user input
     */
    public abstract void modify(Scanner scanner);
}