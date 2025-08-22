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
import java.util.List;
import java.util.Scanner;

/**
 * Represents a survey consisting of a sequence of questions.  Provides
 * operations to add questions, display them, modify them, take the survey
 * (collect responses) and save/load the survey from disk via
 * serialization.
 */
public class Survey implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private final List<Question> questions;

    public Survey(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public int size() {
        return questions.size();
    }

    public void display() {
        System.out.println("Survey: " + name);
        for (int i = 0; i < questions.size(); i++) {
            System.out.print((i + 1) + ") ");
            questions.get(i).display();
            System.out.println();
        }
    }

    /**
     * Saves this survey to the specified file.  Uses Java object
     * serialization.  The parent directory is created if it does not
     * already exist.
     *
     * @param filePath relative or absolute path of the file
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
     * Loads a survey from the given file.
     *
     * @param filePath path to the survey file
     * @return the deserialized Survey
     * @throws IOException if reading fails
     * @throws ClassNotFoundException if class resolution fails
     */
    public static Survey loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = in.readObject();
            if (obj instanceof Survey) {
                return (Survey) obj;
            } else {
                throw new IOException("File does not contain a Survey object.");
            }
        }
    }

    /**
     * Conduct the survey by prompting the respondent for answers to each
     * question.  Collected responses are stored in a ResponseSet and
     * returned.  The ResponseSet may subsequently be serialized to a
     * responses directory.
     *
     * @param scanner scanner from main program
     * @return ResponseSet containing the collected responses
     */
    public ResponseSet takeSurvey(Scanner scanner) {
        System.out.println("Beginning survey: " + name);
        List<List<String>> responses = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println();
            System.out.print((i + 1) + ") ");
            q.display();
            List<String> answer = q.getUserAnswer(scanner);
            responses.add(answer);
        }
        return new ResponseSet(name, responses);
    }

    /**
     * Allows the user to modify a question in the survey.  The user is
     * asked to select the question by its number.  If the selection is
     * valid, the question's modify() method is invoked.
     *
     * @param scanner scanner for user input
     */
    public void modifySurvey(Scanner scanner) {
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
        Question q = questions.get(index - 1);
        q.modify(scanner);
    }

    /**
     * Returns a copy of the questions list.  Used for tabulation when
     * summarizing survey responses.
     *
     * @return list of questions in this survey
     */
    public List<Question> getQuestions() {
        return new ArrayList<>(questions);
    }
}