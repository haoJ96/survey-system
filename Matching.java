import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a matching question.  There are two lists of equal length;
 * during the survey the respondent matches each lettered left item with
 * a numbered right item.  Each matching pair is stored as a string of
 * the form "A-2" (letter followed by dash and number).
 */
public class Matching extends Question {
    private static final long serialVersionUID = 1L;
    private List<String> leftItems;
    private List<String> rightItems;

    public Matching(String prompt, List<String> leftItems, List<String> rightItems) {
        super(prompt);
        if (leftItems.size() != rightItems.size()) {
            throw new IllegalArgumentException("Left and right items must be the same size");
        }
        this.leftItems = new ArrayList<>(leftItems);
        this.rightItems = new ArrayList<>(rightItems);
        // each left item will be matched with one right item
        this.numResponsesAllowed = leftItems.size();
    }

    /**
     * Returns a copy of the left items list.  Used by tests to
     * display the original matching columns during tabulation.
     *
     * @return list of left side items
     */
    public List<String> getLeftItems() {
        return new ArrayList<>(leftItems);
    }

    /**
     * Returns a copy of the right items list.  Used by tests to
     * display the original matching columns during tabulation.
     *
     * @return list of right side items
     */
    public List<String> getRightItems() {
        return new ArrayList<>(rightItems);
    }

    @Override
    public void display() {
        System.out.println(getPrompt());
        System.out.println("Match the following items:");
        // Determine longest left item for formatting
        int maxLeftLength = 0;
        for (String s : leftItems) {
            if (s.length() > maxLeftLength) maxLeftLength = s.length();
        }
        // Print left column and right column
        for (int i = 0; i < leftItems.size(); i++) {
            char letter = (char) ('A' + i);
            int number = i + 1;
            String left = leftItems.get(i);
            String right = rightItems.get(i);
            System.out.printf("%c) %-" + maxLeftLength + "s   %d) %s%n", letter, left, number, right);
        }
    }

    @Override
    public List<String> getUserAnswer(Scanner scanner) {
        List<String> answers = new ArrayList<>();
        int n = leftItems.size();
        char maxLetter = (char) ('A' + n - 1);
        System.out.println("Please enter your matches. For each letter on the left, enter the number of the matching item on the right.");
        for (int i = 0; i < n; i++) {
            char letter = (char) ('A' + i);
            while (true) {
                System.out.print(letter + " -> ");
                String input = scanner.nextLine().trim();
                try {
                    int number = Integer.parseInt(input);
                    if (number < 1 || number > n) {
                        System.out.println("Please enter a number between 1 and " + n + ".");
                        continue;
                    }
                    // ensure no duplicate numbers selected
                    boolean duplicate = false;
                    for (String a : answers) {
                        String[] parts = a.split("-");
                        if (parts.length == 2) {
                            int used = Integer.parseInt(parts[1]);
                            if (used == number) {
                                duplicate = true;
                                break;
                            }
                        }
                    }
                    if (duplicate) {
                        System.out.println("That number has already been used. Please choose a different number.");
                        continue;
                    }
                    answers.add(letter + "-" + number);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
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
        // Modify left or right items
        System.out.print("Do you wish to modify the left and/or right items? (Y/N): ");
        String modifyItems = scanner.nextLine().trim().toLowerCase();
        if (modifyItems.equals("y") || modifyItems.equals("yes")) {
            while (true) {
                // Display items
                System.out.println("Current items:");
                for (int i = 0; i < leftItems.size(); i++) {
                    char letter = (char) ('A' + i);
                    int number = i + 1;
                    System.out.println(letter + ") " + leftItems.get(i) + "   " + number + ") " + rightItems.get(i));
                }
                System.out.print("Enter the letter/number of the item to modify (e.g. A or 1), or press Enter to finish: ");
                String input = scanner.nextLine().trim().toUpperCase();
                if (input.isEmpty()) {
                    break;
                }
                // Determine whether letter or number
                boolean isLetter = input.length() == 1 && Character.isLetter(input.charAt(0));
                boolean isNumber = input.matches("\\d+");
                if (isLetter) {
                    char letter = input.charAt(0);
                    int index = letter - 'A';
                    if (index < 0 || index >= leftItems.size()) {
                        System.out.println("Invalid letter.");
                        continue;
                    }
                    System.out.print("Enter new value for left item " + letter + ": ");
                    leftItems.set(index, scanner.nextLine());
                } else if (isNumber) {
                    int numIdx;
                    try {
                        numIdx = Integer.parseInt(input) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                        continue;
                    }
                    if (numIdx < 0 || numIdx >= rightItems.size()) {
                        System.out.println("Invalid number.");
                        continue;
                    }
                    System.out.print("Enter new value for right item " + (numIdx + 1) + ": ");
                    rightItems.set(numIdx, scanner.nextLine());
                } else {
                    System.out.println("Please enter a valid letter or number.");
                }
            }
        }
        // After modifications, reset number of responses allowed to size
        setNumResponsesAllowed(leftItems.size());
    }
}