import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a multiple choice question.  The creator provides a list of
 * possible choices and may optionally specify how many selections are
 * allowed.  When taking the survey the respondent selects letters
 * corresponding to the choices.
 */
public class MultipleChoice extends Question {
    private static final long serialVersionUID = 1L;
    private final List<String> choices;

    /**
     * Constructs a multiple choice question with the given prompt and list
     * of choices.  The number of responses allowed defaults to 1 and can
     * be changed via setNumResponsesAllowed().
     *
     * @param prompt the question text
     * @param choices a list of choice strings
     */
    public MultipleChoice(String prompt, List<String> choices) {
        super(prompt);
        this.choices = new ArrayList<>(choices);
    }

    public List<String> getChoices() {
        return new ArrayList<>(choices);
    }

    @Override
    public void display() {
        System.out.println(getPrompt());
        char letter = 'A';
        for (String choice : choices) {
            System.out.printf("%c) %s%n", letter, choice);
            letter++;
        }
    }

    @Override
    public List<String> getUserAnswer(Scanner scanner) {
        List<String> answers = new ArrayList<>();
        // Determine the valid letter range
        int nChoices = choices.size();
        char maxLetter = (char) ('A' + nChoices - 1);
        if (getNumResponsesAllowed() > 1) {
            System.out.printf("Please select %d distinct choices (e.g. A B C):%n", getNumResponsesAllowed());
        } else {
            System.out.print("Please select one choice (e.g. A):\n");
        }
        while (answers.size() < getNumResponsesAllowed()) {
            System.out.print("Choice " + (answers.size() + 1) + ": ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.length() != 1) {
                System.out.println("Please enter a single letter corresponding to a choice.");
                continue;
            }
            char letter = input.charAt(0);
            if (letter < 'A' || letter > maxLetter) {
                System.out.println("Invalid choice. Please enter a letter between A and " + maxLetter + ".");
                continue;
            }
            // Ensure distinct answers
            if (answers.contains(String.valueOf(letter))) {
                System.out.println("You already selected that letter. Please choose a different option.");
                continue;
            }
            answers.add(String.valueOf(letter));
        }
        return answers;
    }

    @Override
    public void modify(Scanner scanner) {
        // modify prompt
        System.out.println("Current prompt: " + getPrompt());
        System.out.print("Do you wish to modify the prompt? (Y/N): ");
        String resp = scanner.nextLine().trim().toLowerCase();
        if (resp.equals("y") || resp.equals("yes")) {
            System.out.print("Enter a new prompt: ");
            setPrompt(scanner.nextLine());
        }
        // modify choices
        System.out.print("Do you wish to modify the choices? (Y/N): ");
        String respChoice = scanner.nextLine().trim().toLowerCase();
        if (respChoice.equals("y") || respChoice.equals("yes")) {
            // display choices
            char letter = 'A';
            for (String choice : choices) {
                System.out.printf("%c) %s%n", letter, choice);
                letter++;
            }
            while (true) {
                System.out.print("Enter the letter of the choice to modify (or press Enter to finish): ");
                String choiceInput = scanner.nextLine().trim().toUpperCase();
                if (choiceInput.isEmpty()) {
                    break;
                }
                if (choiceInput.length() != 1) {
                    System.out.println("Please enter a single letter.");
                    continue;
                }
                char ch = choiceInput.charAt(0);
                int index = ch - 'A';
                if (index < 0 || index >= choices.size()) {
                    System.out.println("Invalid selection. Please choose a valid letter.");
                    continue;
                }
                System.out.print("Enter new value for choice " + ch + ": ");
                String newVal = scanner.nextLine();
                choices.set(index, newVal);
            }
        }
        // modify number of responses
        System.out.println("Current number of allowed responses: " + getNumResponsesAllowed());
        System.out.print("Do you wish to change the number of responses allowed? (Y/N): ");
        String respNum = scanner.nextLine().trim().toLowerCase();
        if (respNum.equals("y") || respNum.equals("yes")) {
            while (true) {
                System.out.print("Enter the new number of allowed responses (1-" + choices.size() + "): ");
                String input = scanner.nextLine().trim();
                try {
                    int n = Integer.parseInt(input);
                    if (n < 1 || n > choices.size()) {
                        System.out.println("Please enter a valid number between 1 and " + choices.size() + ".");
                        continue;
                    }
                    setNumResponsesAllowed(n);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a numeric value.");
                }
            }
        }
    }
}