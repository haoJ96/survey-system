import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a date question.  Responses must be valid dates in
 * YYYY-MM-DD format.  Only a single response is allowed.
 */
public class DateQuestion extends Question {
    private static final long serialVersionUID = 1L;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public DateQuestion(String prompt) {
        super(prompt);
        this.numResponsesAllowed = 1;
    }

    @Override
    public void display() {
        System.out.println(getPrompt());
        System.out.println("(Please enter a date in YYYY-MM-DD format)");
    }

    @Override
    public List<String> getUserAnswer(Scanner scanner) {
        List<String> answers = new ArrayList<>();
        while (true) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                LocalDate.parse(input, formatter);
                answers.add(input);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
        return answers;
    }

    @Override
    public void modify(Scanner scanner) {
        System.out.println("Current prompt: " + getPrompt());
        System.out.print("Do you wish to modify the prompt? (Y/N): ");
        String resp = scanner.nextLine().trim().toLowerCase();
        if (resp.equals("y") || resp.equals("yes")) {
            System.out.print("Enter a new prompt: ");
            setPrompt(scanner.nextLine());
        }
        // Date questions always allow only one response
    }
}