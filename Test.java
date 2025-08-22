import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

// Import question types for instanceof checks

/**
 * Represents a test consisting of a list of TestQuestion objects.  A
 * Test behaves similarly to a Survey but each question includes one or
 * more correct answers used for grading.  Tests can be saved to and
 * loaded from disk via serialization.
 */
public class Test implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private final List<TestQuestion> questions;

    public Test(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addQuestion(TestQuestion tq) {
        questions.add(tq);
    }

    public int size() {
        return questions.size();
    }

    public List<TestQuestion> getQuestions() {
        return new ArrayList<>(questions);
    }

    /**
     * Display the test without correct answers.  Each question is shown
     * using its underlying Question display() method.
     */
    public void display() {
        System.out.println("Test: " + name);
        for (int i = 0; i < questions.size(); i++) {
            System.out.print((i + 1) + ") ");
            questions.get(i).display();
            System.out.println();
        }
    }

    /**
     * Display the test along with correct answers.  Useful when
     * reviewing or grading the test.
     */
    public void displayWithAnswers() {
        System.out.println("Test: " + name);
        for (int i = 0; i < questions.size(); i++) {
            System.out.print((i + 1) + ") ");
            TestQuestion tq = questions.get(i);
            tq.displayWithAnswer();
            System.out.println();
        }
    }

    /**
     * Saves this test to the specified file.  Uses Java object
     * serialization.  The parent directory is created if it does not
     * already exist.
     *
     * @param filePath path of the file to save
     * @throws IOException if an I/O error occurs
     */
    public void saveToFile(String filePath) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        }
    }

    /**
     * Loads a Test object from the given file.
     *
     * @param filePath path to the serialized Test
     * @return the deserialized Test
     * @throws IOException if reading fails
     * @throws ClassNotFoundException if class resolution fails
     */
    public static Test loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = in.readObject();
            if (obj instanceof Test) {
                return (Test) obj;
            } else {
                throw new IOException("File does not contain a Test object.");
            }
        }
    }

    /**
     * Conduct the test by prompting the respondent for answers to each
     * question.  Collected responses are stored in a ResponseSet and
     * returned.  The ResponseSet may subsequently be serialized to a
     * responses directory.
     *
     * @param scanner scanner from main program
     * @return ResponseSet containing the collected responses
     */
    public ResponseSet takeTest(Scanner scanner) {
        System.out.println("Beginning test: " + name);
        List<List<String>> responses = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            TestQuestion tq = questions.get(i);
            Question q = tq.getQuestion();
            System.out.println();
            System.out.print((i + 1) + ") ");
            q.display();
            List<String> answer = q.getUserAnswer(scanner);
            responses.add(answer);
        }
        return new ResponseSet(name, responses);
    }

    /**
     * Allows the user to modify a question within the test, including its
     * prompt, choices and correct answer(s).  The user selects the
     * question by its number.  If the selection is valid, both the
     * underlying Question and the associated correct answers are
     * modified.
     *
     * @param scanner scanner for user input
     */
    public void modifyTest(Scanner scanner) {
        if (questions.isEmpty()) {
            System.out.println("There are no questions to modify.");
            return;
        }
        display();
        int index = -1;
        while (true) {
            System.out.print("Enter the number of the question you wish to modify: ");
            String input = scanner.nextLine().trim();
            try {
                index = Integer.parseInt(input);
                if (index < 1 || index > questions.size()) {
                    System.out.println("Please enter a number between 1 and " + questions.size() + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        TestQuestion tq = questions.get(index - 1);
        // modify underlying question prompt/choices/allowed responses
        tq.getQuestion().modify(scanner);
        // update correct answers if not essay
        tq.modifyCorrectAnswers(scanner);
    }

    /**
     * Computes the number of auto‑gradable questions answered correctly for
     * the given response set.  Essay questions are skipped.  The order
     * of responses is assumed to correspond to the order of questions in
     * this test.
     *
     * @param responseSet the user's responses
     * @return the number of correct answers to auto‑gradable questions
     */
    public int countCorrect(ResponseSet responseSet) {
        List<List<String>> answers = responseSet.getResponses();
        int correct = 0;
        for (int i = 0; i < questions.size() && i < answers.size(); i++) {
            TestQuestion tq = questions.get(i);
            if (!tq.isEssay()) {
                if (tq.isCorrect(answers.get(i))) {
                    correct++;
                }
            }
        }
        return correct;
    }

    /**
     * Tabulate and display the results for this test using the provided
     * list of response sets.  For each question, the responses are
     * aggregated and summarized according to the question type.  Essay
     * questions simply list the responses.
     *
     * @param responseSets list of ResponseSet objects for this test
     */
    public void tabulate(List<ResponseSet> responseSets) {
        if (responseSets == null || responseSets.isEmpty()) {
            System.out.println("No responses to tabulate for test '" + name + "'.");
            return;
        }
        System.out.println("Tabulation of test: " + name);
        for (int i = 0; i < questions.size(); i++) {
            TestQuestion tq = questions.get(i);
            Question q = tq.getQuestion();
            System.out.println();
            System.out.print((i + 1) + ") ");
            q.display();
            // Collect all answers for this question
            List<List<String>> ansForThis = new ArrayList<>();
            for (ResponseSet rs : responseSets) {
                List<List<String>> ansList = rs.getResponses();
                if (i < ansList.size()) {
                    ansForThis.add(ansList.get(i));
                }
            }
            // Tabulate by type
            if (q instanceof TrueFalse || q instanceof MultipleChoice) {
                Map<String, Integer> counts = new HashMap<>();
                // Determine possible options for display order
                List<String> options = new ArrayList<>();
                if (q instanceof TrueFalse) {
                    options.add("True");
                    options.add("False");
                } else if (q instanceof MultipleChoice) {
                    // possible letter options
                    MultipleChoice mc = (MultipleChoice) q;
                    int n = mc.getChoices().size();
                    for (int j = 0; j < n; j++) {
                        char letter = (char) ('A' + j);
                        options.add(String.valueOf(letter));
                    }
                }
                // Initialize counts
                for (String opt : options) {
                    counts.put(opt, 0);
                }
                // Count responses
                for (List<String> resp : ansForThis) {
                    for (String ans : resp) {
                        String normalized;
                        if (q instanceof TrueFalse) {
                            String lower = ans.trim().toLowerCase(Locale.ROOT);
                            if (lower.startsWith("t")) {
                                normalized = "True";
                            } else if (lower.startsWith("f")) {
                                normalized = "False";
                            } else {
                                continue;
                            }
                        } else {
                            normalized = ans.trim().toUpperCase(Locale.ROOT);
                        }
                        counts.put(normalized, counts.getOrDefault(normalized, 0) + 1);
                    }
                }
                // Display counts
                if (q instanceof TrueFalse) {
                    System.out.println("True: " + counts.get("True"));
                    System.out.println("False: " + counts.get("False"));
                } else {
                    for (String opt : options) {
                        System.out.println(opt + ": " + counts.get(opt));
                    }
                }
            } else if (q instanceof ShortAnswer || q instanceof DateQuestion) {
                Map<String, Integer> counts = new HashMap<>();
                for (List<String> resp : ansForThis) {
                    for (String ans : resp) {
                        String key = ans.trim();
                        counts.put(key, counts.getOrDefault(key, 0) + 1);
                    }
                }
                // Display each unique answer and its count
                for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            } else if (q instanceof Essay) {
                // List all essay responses verbatim
                for (List<String> resp : ansForThis) {
                    for (String ans : resp) {
                        System.out.println(ans);
                    }
                }
            } else if (q instanceof Matching) {
                Map<String, Integer> comboCounts = new HashMap<>();
                for (List<String> resp : ansForThis) {
                    // Build a canonical representation: join pairs with '|'
                    List<String> pairs = new ArrayList<>();
                    for (String ans : resp) {
                        pairs.add(ans.trim().toUpperCase(Locale.ROOT));
                    }
                    // Sort pairs alphabetically by letter for consistency
                    pairs.sort((a, b) -> {
                        // compare by letter before '-'
                        String la = a.split("-")[0];
                        String lb = b.split("-")[0];
                        return la.compareTo(lb);
                    });
                    String key = String.join("|", pairs);
                    comboCounts.put(key, comboCounts.getOrDefault(key, 0) + 1);
                }
                // Display results.  Show original matching choices for clarity
                Matching m = (Matching) q;
                // Show left and right items
                System.out.println();
                List<String> left = m.getLeftItems();
                List<String> right = m.getRightItems();
                for (int j = 0; j < left.size(); j++) {
                    char letter = (char) ('A' + j);
                    int num = j + 1;
                    System.out.printf("%c) %s %d) %s%n", letter, left.get(j), num, right.get(j));
                }
                // Print each unique permutation with count
                for (Map.Entry<String, Integer> entry : comboCounts.entrySet()) {
                    int count = entry.getValue();
                    String key = entry.getKey();
                    System.out.println(count);
                    String[] pairs = key.split("\\|");
                    // Display each mapping on its own line
                    for (String p : pairs) {
                        String[] parts = p.split("-");
                        if (parts.length == 2) {
                            System.out.println(parts[0] + " " + parts[1]);
                        } else {
                            System.out.println(p);
                        }
                    }
                }
            }
        }
    }
}