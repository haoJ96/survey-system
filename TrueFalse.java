import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Represents a true/false question.  Only a single response is allowed.
 */
public class TrueFalse extends Question {
    private static final long serialVersionUID = 1L;

    public TrueFalse(String prompt) {
        super(prompt);
        // true/false always permits only one response
        this.numResponsesAllowed = 1;
    }

    @Override
    public void display() {
        System.out.println(getPrompt());
        System.out.println("(T/F)");
    }

    @Override
    public List<String> getUserAnswer(Scanner scanner) {
        List<String> answers = new ArrayList<>();
        while (true) {
            System.out.print("Enter T for True or F for False: ");
            String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
            if (input.equals("t") || input.equals("true")) {
                answers.add("True");
                break;
            } else if (input.equals("f") || input.equals("false")) {
                answers.add("False");
                break;
            } else {
                System.out.println("Invalid input. Please enter 'T' or 'F'.");
            }
        }
        return answers;
    }

    @Override
    public void modify(Scanner scanner) {
        System.out.println("Current prompt: " + getPrompt());
        System.out.print("Do you wish to modify the prompt? (Y/N): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("y") || response.equals("yes")) {
            System.out.print("Enter a new prompt: ");
            String newPrompt = scanner.nextLine();
            setPrompt(newPrompt);
        }
        // Nothing else to modify for true/false questions
    }
}