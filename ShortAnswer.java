import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a short answer question.  The creator may specify how many
 * responses are allowed; each response is a single line of text.
 */
public class ShortAnswer extends Question {
    private static final long serialVersionUID = 1L;

    public ShortAnswer(String prompt) {
        super(prompt);
        // default number of responses is 1
    }

    @Override
    public void display() {
        System.out.println(getPrompt());
    }

    @Override
    public List<String> getUserAnswer(Scanner scanner) {
        List<String> answers = new ArrayList<>();
        for (int i = 0; i < getNumResponsesAllowed(); i++) {
            System.out.print("Answer " + (i + 1) + ": ");
            String response = scanner.nextLine();
            answers.add(response);
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
        System.out.println("Current number of allowed responses: " + getNumResponsesAllowed());
        System.out.print("Do you wish to change the number of responses allowed? (Y/N): ");
        String respNum = scanner.nextLine().trim().toLowerCase();
        if (respNum.equals("y") || respNum.equals("yes")) {
            while (true) {
                System.out.print("Enter new number of responses (>=1): ");
                String input = scanner.nextLine().trim();
                try {
                    int n = Integer.parseInt(input);
                    if (n < 1) {
                        System.out.println("The number must be at least 1.");
                        continue;
                    }
                    setNumResponsesAllowed(n);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }
        }
    }
}