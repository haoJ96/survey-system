import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

// Import question types used for instanceof checks

/**
 * Encapsulates a survey Question along with its correct answer(s) for use
 * in tests.  The underlying Question is reused from the survey classes
 * and provides the display and input logic.  A TestQuestion holds one
 * or more correct responses depending on the type of question and the
 * number of responses allowed.  Essay questions are considered non‑auto
 * gradable and therefore have an empty list of correct answers.
 */
public class TestQuestion implements Serializable {
    private static final long serialVersionUID = 1L;
    private Question question;
    private List<String> correctAnswers;

    public TestQuestion(Question question, List<String> correctAnswers) {
        this.question = question;
        if (correctAnswers == null) {
            this.correctAnswers = null;
        } else {
            this.correctAnswers = new ArrayList<>(correctAnswers);
        }
    }

    public Question getQuestion() {
        return question;
    }

    public List<String> getCorrectAnswers() {
        if (correctAnswers == null) return null;
        return new ArrayList<>(correctAnswers);
    }

    public void setCorrectAnswers(List<String> answers) {
        if (answers == null) {
            this.correctAnswers = null;
        } else {
            this.correctAnswers = new ArrayList<>(answers);
        }
    }

    /**
     * Returns true if this question is an essay question, which is not
     * automatically gradable.
     */
    public boolean isEssay() {
        return question instanceof Essay;
    }

    /**
     * Determines whether the provided user answers are correct.  If the
     * question is an essay or no correct answers were supplied, this
     * method always returns false because essays cannot be graded
     * automatically.  Otherwise the answers are compared according to
     * the type of the question.
     *
     * @param userAnswers the list of answers submitted by the user
     * @return true if the user’s answers match the stored correct answers
     */
    public boolean isCorrect(List<String> userAnswers) {
        if (isEssay() || correctAnswers == null) {
            return false;
        }
        if (userAnswers == null) {
            return false;
        }
        // Compare answers based on question type
        if (question instanceof TrueFalse) {
            if (userAnswers.size() != 1 || correctAnswers.size() != 1) {
                return false;
            }
            String user = userAnswers.get(0).trim().toLowerCase(Locale.ROOT);
            String correct = correctAnswers.get(0).trim().toLowerCase(Locale.ROOT);
            return (user.equals("true") || user.equals("t")) && (correct.equals("true") || correct.equals("t"))
                    || (user.equals("false") || user.equals("f")) && (correct.equals("false") || correct.equals("f"));
        } else if (question instanceof MultipleChoice) {
            // Compare sets of chosen letters (case insensitive)
            Set<String> userSet = new HashSet<>();
            for (String ans : userAnswers) {
                userSet.add(ans.trim().toUpperCase(Locale.ROOT));
            }
            Set<String> correctSet = new HashSet<>();
            for (String ans : correctAnswers) {
                correctSet.add(ans.trim().toUpperCase(Locale.ROOT));
            }
            return userSet.equals(correctSet);
        } else if (question instanceof ShortAnswer) {
            // Compare sets of responses case‑insensitively
            Set<String> userSet = new HashSet<>();
            for (String ans : userAnswers) {
                userSet.add(ans.trim().toLowerCase(Locale.ROOT));
            }
            Set<String> correctSet = new HashSet<>();
            for (String ans : correctAnswers) {
                correctSet.add(ans.trim().toLowerCase(Locale.ROOT));
            }
            return userSet.equals(correctSet);
        } else if (question instanceof DateQuestion) {
            // Date questions always allow a single response
            if (userAnswers.size() != correctAnswers.size()) {
                return false;
            }
            // Compare strings exactly (format validated when captured)
            Set<String> userSet = new HashSet<>();
            for (String ans : userAnswers) {
                userSet.add(ans.trim());
            }
            Set<String> correctSet = new HashSet<>();
            for (String ans : correctAnswers) {
                correctSet.add(ans.trim());
            }
            return userSet.equals(correctSet);
        } else if (question instanceof Matching) {
            // Each answer is of the form "A-2".  Compare sets of these strings.
            Set<String> userSet = new HashSet<>();
            for (String ans : userAnswers) {
                userSet.add(ans.trim().toUpperCase(Locale.ROOT));
            }
            Set<String> correctSet = new HashSet<>();
            for (String ans : correctAnswers) {
                correctSet.add(ans.trim().toUpperCase(Locale.ROOT));
            }
            return userSet.equals(correctSet);
        }
        // Unknown type
        return false;
    }

    /**
     * Displays this question including its prompt and any associated
     * choices.  Delegates to the underlying Question display().
     */
    public void display() {
        question.display();
    }

    /**
     * Displays this question along with its correct answer(s).  The format
     * depends on the type of question.  Essay questions do not display
     * a correct answer.
     */
    public void displayWithAnswer() {
        question.display();
        if (isEssay() || correctAnswers == null) {
            System.out.println("(No automatic grading for this question)");
            return;
        }
        if (question instanceof TrueFalse) {
            String ans = correctAnswers.get(0).trim().toLowerCase(Locale.ROOT);
            String letter = ans.startsWith("t") ? "T" : "F";
            System.out.println("The correct answer is " + letter);
        } else if (question instanceof MultipleChoice) {
            MultipleChoice mc = (MultipleChoice) question;
            List<String> choices = mc.getChoices();
            // Build descriptive answers like A) Choice1
            List<String> parts = new ArrayList<>();
            for (String ans : correctAnswers) {
                ans = ans.trim().toUpperCase(Locale.ROOT);
                if (ans.length() == 1) {
                    char letter = ans.charAt(0);
                    int idx = letter - 'A';
                    if (idx >= 0 && idx < choices.size()) {
                        parts.add(letter + ") " + choices.get(idx));
                    } else {
                        parts.add(letter + ")");
                    }
                } else {
                    parts.add(ans);
                }
            }
            if (parts.size() == 1) {
                System.out.println("The correct choice is " + parts.get(0));
            } else {
                System.out.print("The correct choices are ");
                for (int i = 0; i < parts.size(); i++) {
                    System.out.print(parts.get(i));
                    if (i < parts.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        } else if (question instanceof ShortAnswer) {
            if (correctAnswers.size() == 1) {
                System.out.println("The correct answer is " + correctAnswers.get(0));
            } else {
                System.out.print("The correct answers are ");
                for (int i = 0; i < correctAnswers.size(); i++) {
                    System.out.print(correctAnswers.get(i));
                    if (i < correctAnswers.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        } else if (question instanceof DateQuestion) {
            System.out.println("The correct answer is " + correctAnswers.get(0));
        } else if (question instanceof Matching) {
            // For matching, display each mapping nicely
            System.out.println("The correct matches are:");
            for (String ans : correctAnswers) {
                String[] parts = ans.split("-");
                if (parts.length == 2) {
                    System.out.println(parts[0].toUpperCase(Locale.ROOT) + " -> " + parts[1]);
                } else {
                    System.out.println(ans);
                }
            }
        }
    }

    /**
     * Prompts the user to modify the correct answers associated with this
     * question.  The underlying question’s modify() method should be
     * invoked prior to calling this method if question text or choices
     * need to be changed.  If this is an essay question the user will
     * not be prompted to supply a correct answer.
     *
     * @param scanner scanner for user input
     */
    public void modifyCorrectAnswers(Scanner scanner) {
        if (isEssay()) {
            // Essay questions are not auto-graded; nothing to modify
            return;
        }
        System.out.print("Do you wish to modify the correct answer(s)? (Y/N): ");
        String resp = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        if (!(resp.equals("y") || resp.equals("yes"))) {
            return;
        }
        // Determine type and ask accordingly
        if (question instanceof TrueFalse) {
            while (true) {
                System.out.print("Enter the correct answer (T/F): ");
                String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
                if (input.equals("t") || input.equals("true")) {
                    List<String> ans = new ArrayList<>();
                    ans.add("True");
                    setCorrectAnswers(ans);
                    break;
                } else if (input.equals("f") || input.equals("false")) {
                    List<String> ans = new ArrayList<>();
                    ans.add("False");
                    setCorrectAnswers(ans);
                    break;
                } else {
                    System.out.println("Invalid input. Please enter T or F.");
                }
            }
        } else if (question instanceof MultipleChoice) {
            MultipleChoice mc = (MultipleChoice) question;
            List<String> choices = mc.getChoices();
            int numAllowed = mc.getNumResponsesAllowed();
            List<String> newCorrect = new ArrayList<>();
            for (int i = 0; i < numAllowed; i++) {
                while (true) {
                    System.out.print("Enter correct choice #" + (i + 1) + " (1-" + choices.size() + "): ");
                    String input = scanner.nextLine().trim();
                    try {
                        int idx = Integer.parseInt(input);
                        if (idx < 1 || idx > choices.size()) {
                            System.out.println("Please enter a number between 1 and " + choices.size() + ".");
                            continue;
                        }
                        char letter = (char) ('A' + idx - 1);
                        newCorrect.add(String.valueOf(letter));
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer.");
                    }
                }
            }
            setCorrectAnswers(newCorrect);
        } else if (question instanceof ShortAnswer) {
            int numAllowed = question.getNumResponsesAllowed();
            List<String> newCorrect = new ArrayList<>();
            for (int i = 0; i < numAllowed; i++) {
                System.out.print("Enter correct answer #" + (i + 1) + ": ");
                String ans = scanner.nextLine();
                newCorrect.add(ans);
            }
            setCorrectAnswers(newCorrect);
        } else if (question instanceof DateQuestion) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            while (true) {
                System.out.print("Enter the correct date (YYYY-MM-DD): ");
                String input = scanner.nextLine().trim();
                try {
                    LocalDate.parse(input, formatter);
                    List<String> ans = new ArrayList<>();
                    ans.add(input);
                    setCorrectAnswers(ans);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please try again.");
                }
            }
        } else if (question instanceof Matching) {
            Matching match = (Matching) question;
            // Determine number of pairs
            int n = match.getNumResponsesAllowed();
            List<String> newCorrect = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                char letter = (char) ('A' + i);
                while (true) {
                    System.out.print("Enter correct match for item " + letter + " (1-" + n + "): ");
                    String input = scanner.nextLine().trim();
                    try {
                        int num = Integer.parseInt(input);
                        if (num < 1 || num > n) {
                            System.out.println("Please enter a number between 1 and " + n + ".");
                            continue;
                        }
                        newCorrect.add(letter + "-" + num);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer.");
                    }
                }
            }
            setCorrectAnswers(newCorrect);
        }
    }
}