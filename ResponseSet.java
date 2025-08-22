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

/**
 * Represents a set of responses to a particular survey.  Stores the name
 * of the survey and the list of answers provided for each question.  The
 * response set can be serialized to disk to preserve the answers.
 */
public class ResponseSet implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String surveyName;
    private final List<List<String>> responses;
    private final Date timestamp;

    public ResponseSet(String surveyName, List<List<String>> responses) {
        this.surveyName = surveyName;
        this.responses = new ArrayList<>();
        for (List<String> ans : responses) {
            this.responses.add(new ArrayList<>(ans));
        }
        this.timestamp = new Date();
    }

    public String getSurveyName() {
        return surveyName;
    }

    public List<List<String>> getResponses() {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> ans : responses) {
            copy.add(new ArrayList<>(ans));
        }
        return copy;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Serialize this response set to the given file path.  The parent
     * directory is created if necessary.
     *
     * @param filePath path to the output file
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
     * Generate a filename based on the survey name and timestamp.
     *
     * @return filename string
     */
    public String generateFileName() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String ts = fmt.format(timestamp);
        // sanitize survey name: replace spaces with underscores, remove illegal chars
        String safeName = surveyName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return safeName + "_" + ts + ".resp";
    }

    /**
     * Loads a ResponseSet object from the given file.
     *
     * @param filePath path to the serialized ResponseSet
     * @return the deserialized ResponseSet
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be resolved
     */
    public static ResponseSet loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = in.readObject();
            if (obj instanceof ResponseSet) {
                return (ResponseSet) obj;
            } else {
                throw new IOException("File does not contain a ResponseSet object.");
            }
        }
    }
}