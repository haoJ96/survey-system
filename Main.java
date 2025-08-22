import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

// Import survey and test classes

/**
 * Main class providing a menu driven interface for the Survey Generator.
 * Allows users to create, load, save, display, modify and take surveys.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    // directories where surveys and responses are stored
    private static final String SURVEY_DIR = "../surveys";
    private static final String SURVEY_RESPONSE_DIR = "../responses";
    private static final String TEST_DIR = "../tests";
    private static final String TEST_RESPONSE_DIR = "../test_responses";
    private static Survey currentSurvey = null;
    private static Test currentTest = null;

    public static void main(String[] args) {
        // ensure directories exist
        new File(SURVEY_DIR).mkdirs();
        new File(SURVEY_RESPONSE_DIR).mkdirs();
        new File(TEST_DIR).mkdirs();
        new File(TEST_RESPONSE_DIR).mkdirs();
        while (true) {
            System.out.println();
            System.out.println("Main Menu");
            System.out.println("1) Survey");
            System.out.println("2) Test");
            System.out.println("3) Exit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    surveyMenu();
                    break;
                case "2":
                    testMenu();
                    break;
                case "3":
                    System.out.println("Exiting. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please select a number from the menu.");
            }
        }
    }

    /**
     * Prompts the user for a survey name and then enters a loop allowing
     * the addition of questions.  When finished the newly created survey
     * becomes the current survey.
     */
    private static void createNewSurvey() {
        System.out.print("Enter a name for your survey: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Survey name cannot be empty. Returning to main menu.");
            return;
        }
        Survey survey = new Survey(name);
        while (true) {
            System.out.println();
            System.out.println("Add Questions Menu");
            System.out.println("1) Add a new T/F question");
            System.out.println("2) Add a new multiple-choice question");
            System.out.println("3) Add a new short answer question");
            System.out.println("4) Add a new essay question");
            System.out.println("5) Add a new date question");
            System.out.println("6) Add a new matching question");
            System.out.println("7) Finish adding questions and return to main menu");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    survey.addQuestion(createTrueFalse());
                    break;
                case "2":
                    survey.addQuestion(createMultipleChoice());
                    break;
                case "3":
                    survey.addQuestion(createShortAnswer());
                    break;
                case "4":
                    survey.addQuestion(createEssay());
                    break;
                case "5":
                    survey.addQuestion(createDateQuestion());
                    break;
                case "6":
                    survey.addQuestion(createMatching());
                    break;
                case "7":
                    currentSurvey = survey;
                    System.out.println("Survey creation complete. Returning to previous menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose an option from the menu.");
            }
        }
    }

    private static TrueFalse createTrueFalse() {
        System.out.print("Enter the prompt for your True/False question: ");
        String prompt = scanner.nextLine();
        return new TrueFalse(prompt);
    }

    private static MultipleChoice createMultipleChoice() {
        System.out.print("Enter the prompt for your multiple-choice question: ");
        String prompt = scanner.nextLine();
        int numChoices = 0;
        while (true) {
            System.out.print("Enter the number of choices for your multiple-choice question: ");
            String input = scanner.nextLine().trim();
            try {
                numChoices = Integer.parseInt(input);
                if (numChoices < 2) {
                    System.out.println("A multiple-choice question requires at least two choices.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        List<String> choices = new ArrayList<>();
        for (int i = 0; i < numChoices; i++) {
            System.out.print("Enter choice #" + (i + 1) + ": ");
            String choice = scanner.nextLine();
            choices.add(choice);
        }
        MultipleChoice mc = new MultipleChoice(prompt, choices);
        // ask about number of responses allowed
        while (true) {
            System.out.print("Enter the number of selections allowed (1 for single answer): ");
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n < 1 || n > numChoices) {
                    System.out.println("Please enter a number between 1 and " + numChoices + ".");
                    continue;
                }
                mc.setNumResponsesAllowed(n);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        return mc;
    }

    private static ShortAnswer createShortAnswer() {
        System.out.print("Enter the prompt for your short answer question: ");
        String prompt = scanner.nextLine();
        ShortAnswer sa = new ShortAnswer(prompt);
        // ask about number of responses allowed
        while (true) {
            System.out.print("Enter the number of responses allowed (>=1): ");
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n < 1) {
                    System.out.println("Number must be at least 1.");
                    continue;
                }
                sa.setNumResponsesAllowed(n);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        return sa;
    }

    private static Essay createEssay() {
        System.out.print("Enter the prompt for your essay question: ");
        String prompt = scanner.nextLine();
        Essay essay = new Essay(prompt);
        // ask number of responses allowed
        while (true) {
            System.out.print("Enter the number of essay responses allowed (>=1): ");
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n < 1) {
                    System.out.println("Number must be at least 1.");
                    continue;
                }
                essay.setNumResponsesAllowed(n);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        return essay;
    }

    private static DateQuestion createDateQuestion() {
        System.out.print("Enter the prompt for your date question: ");
        String prompt = scanner.nextLine();
        return new DateQuestion(prompt);
    }

    private static Matching createMatching() {
        System.out.print("Enter the prompt for your matching question: ");
        String prompt = scanner.nextLine();
        int numPairs = 0;
        while (true) {
            System.out.print("Enter the number of items on each side (>=2): ");
            String input = scanner.nextLine().trim();
            try {
                numPairs = Integer.parseInt(input);
                if (numPairs < 2) {
                    System.out.println("There must be at least two pairs for a matching question.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        List<String> leftItems = new ArrayList<>();
        List<String> rightItems = new ArrayList<>();
        for (int i = 0; i < numPairs; i++) {
            char letter = (char) ('A' + i);
            System.out.print("Enter left item " + letter + ": ");
            leftItems.add(scanner.nextLine());
        }
        for (int i = 0; i < numPairs; i++) {
            int number = i + 1;
            System.out.print("Enter right item " + number + ": ");
            rightItems.add(scanner.nextLine());
        }
        return new Matching(prompt, leftItems, rightItems);
    }

    private static void displaySurvey() {
        if (currentSurvey == null) {
            System.out.println("You must have a survey loaded in order to display it.");
            return;
        }
        currentSurvey.display();
    }

    private static void loadSurvey() {
        File dir = new File(SURVEY_DIR);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No survey files found in " + SURVEY_DIR + ".");
            return;
        }
        // filter only files (not directories) and sort by name
        List<File> surveyFiles = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                surveyFiles.add(f);
            }
        }
        if (surveyFiles.isEmpty()) {
            System.out.println("No survey files found in " + SURVEY_DIR + ".");
            return;
        }
        System.out.println("Please select a file to load:");
        for (int i = 0; i < surveyFiles.size(); i++) {
            System.out.println((i + 1) + ") " + surveyFiles.get(i).getName());
        }
        int selection = -1;
        while (true) {
            System.out.print("Enter the number of the file to load (or 0 to cancel): ");
            String input = scanner.nextLine().trim();
            try {
                selection = Integer.parseInt(input);
                if (selection == 0) {
                    System.out.println("Load cancelled.");
                    return;
                }
                if (selection < 1 || selection > surveyFiles.size()) {
                    System.out.println("Please select a valid number between 1 and " + surveyFiles.size() + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        File chosen = surveyFiles.get(selection - 1);
        try {
            Survey loaded = Survey.loadFromFile(chosen.getPath());
            currentSurvey = loaded;
            System.out.println("Survey '" + loaded.getName() + "' loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load survey: " + e.getMessage());
        }
    }

    private static void saveSurvey() {
        if (currentSurvey == null) {
            System.out.println("You must have a survey loaded in order to save it.");
            return;
        }
        // Suggest a filename based on survey name
        String defaultName = currentSurvey.getName().replaceAll("[^a-zA-Z0-9_-]", "_") + ".ser";
        System.out.print("Enter the filename to save the survey (default: " + defaultName + "): ");
        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) {
            fileName = defaultName;
        }
        File file = new File(SURVEY_DIR, fileName);
        try {
            currentSurvey.saveToFile(file.getPath());
            System.out.println("Survey saved to " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error saving survey: " + e.getMessage());
        }
    }

    private static void takeSurvey() {
        if (currentSurvey == null) {
            System.out.println("You must have a survey loaded in order to take it.");
            return;
        }
        ResponseSet responses = currentSurvey.takeSurvey(scanner);
        String respFileName = responses.generateFileName();
        File file = new File(SURVEY_RESPONSE_DIR, respFileName);
        try {
            responses.saveToFile(file.getPath());
            System.out.println("Responses saved to " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error saving responses: " + e.getMessage());
        }
    }

    private static void modifySurvey() {
        if (currentSurvey == null) {
            System.out.println("You must have a survey loaded in order to modify it.");
            return;
        }
        currentSurvey.modifySurvey(scanner);
    }

    /**
     * Creates a new test by prompting the user for a name and then
     * allowing them to add questions of various types.  Correct
     * answers are solicited for each question as appropriate.
     */
    private static void createNewTest() {
        System.out.print("Enter a name for your test: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Test name cannot be empty. Returning to previous menu.");
            return;
        }
        Test test = new Test(name);
        while (true) {
            System.out.println();
            System.out.println("Add Questions Menu");
            System.out.println("1) Add a new T/F question");
            System.out.println("2) Add a new multiple-choice question");
            System.out.println("3) Add a new short answer question");
            System.out.println("4) Add a new essay question");
            System.out.println("5) Add a new date question");
            System.out.println("6) Add a new matching question");
            System.out.println("7) Finish adding questions and return to previous menu");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    test.addQuestion(createTrueFalseTest());
                    break;
                case "2":
                    test.addQuestion(createMultipleChoiceTest());
                    break;
                case "3":
                    test.addQuestion(createShortAnswerTest());
                    break;
                case "4":
                    test.addQuestion(createEssayTest());
                    break;
                case "5":
                    test.addQuestion(createDateTest());
                    break;
                case "6":
                    test.addQuestion(createMatchingTest());
                    break;
                case "7":
                    currentTest = test;
                    System.out.println("Test creation complete. Returning to previous menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose an option from the menu.");
            }
        }
    }

    /**
     * Constructs a True/False test question, prompting for the prompt and
     * correct answer.
     */
    private static TestQuestion createTrueFalseTest() {
        System.out.print("Enter the prompt for your True/False question: ");
        String prompt = scanner.nextLine();
        TrueFalse q = new TrueFalse(prompt);
        // solicit correct answer
        List<String> correct = new ArrayList<>();
        while (true) {
            System.out.print("Enter the correct answer (T/F): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("t") || input.equals("true")) {
                correct.add("True");
                break;
            } else if (input.equals("f") || input.equals("false")) {
                correct.add("False");
                break;
            } else {
                System.out.println("Invalid input. Please enter T or F.");
            }
        }
        return new TestQuestion(q, correct);
    }

    /**
     * Constructs a multiple choice test question, prompting for the prompt,
     * number of choices, choices themselves, number of selections allowed
     * and the correct answer(s).
     */
    private static TestQuestion createMultipleChoiceTest() {
        System.out.print("Enter the prompt for your multiple-choice question: ");
        String prompt = scanner.nextLine();
        int numChoices = 0;
        while (true) {
            System.out.print("Enter the number of choices for your multiple-choice question: ");
            String input = scanner.nextLine().trim();
            try {
                numChoices = Integer.parseInt(input);
                if (numChoices < 2) {
                    System.out.println("A multiple-choice question requires at least two choices.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        List<String> choices = new ArrayList<>();
        for (int i = 0; i < numChoices; i++) {
            System.out.print("Enter choice #" + (i + 1) + ": ");
            String choice = scanner.nextLine();
            choices.add(choice);
        }
        MultipleChoice mc = new MultipleChoice(prompt, choices);
        // ask about number of responses allowed
        while (true) {
            System.out.print("Enter the number of selections allowed (1 for single answer): ");
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n < 1 || n > numChoices) {
                    System.out.println("Please enter a number between 1 and " + numChoices + ".");
                    continue;
                }
                mc.setNumResponsesAllowed(n);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        // solicit correct answers
        List<String> correct = new ArrayList<>();
        for (int i = 0; i < mc.getNumResponsesAllowed(); i++) {
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
                    correct.add(String.valueOf(letter));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }
        }
        return new TestQuestion(mc, correct);
    }

    /**
     * Constructs a short answer test question.
     */
    private static TestQuestion createShortAnswerTest() {
        System.out.print("Enter the prompt for your short answer question: ");
        String prompt = scanner.nextLine();
        ShortAnswer sa = new ShortAnswer(prompt);
        // ask about number of responses allowed
        while (true) {
            System.out.print("Enter the number of responses allowed (>=1): ");
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n < 1) {
                    System.out.println("Number must be at least 1.");
                    continue;
                }
                sa.setNumResponsesAllowed(n);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        // solicit correct answers
        List<String> correct = new ArrayList<>();
        for (int i = 0; i < sa.getNumResponsesAllowed(); i++) {
            System.out.print("Enter correct answer #" + (i + 1) + ": ");
            String ans = scanner.nextLine();
            correct.add(ans);
        }
        return new TestQuestion(sa, correct);
    }

    /**
     * Constructs an essay test question.  Essay questions do not have
     * automatically graded answers.
     */
    private static TestQuestion createEssayTest() {
        System.out.print("Enter the prompt for your essay question: ");
        String prompt = scanner.nextLine();
        Essay essay = new Essay(prompt);
        // ask number of responses allowed
        while (true) {
            System.out.print("Enter the number of essay responses allowed (>=1): ");
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n < 1) {
                    System.out.println("Number must be at least 1.");
                    continue;
                }
                essay.setNumResponsesAllowed(n);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        return new TestQuestion(essay, null);
    }

    /**
     * Constructs a date test question.
     */
    private static TestQuestion createDateTest() {
        System.out.print("Enter the prompt for your date question: ");
        String prompt = scanner.nextLine();
        DateQuestion dq = new DateQuestion(prompt);
        // solicit correct date
        List<String> correct = new ArrayList<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
        while (true) {
            System.out.print("Enter the correct date (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                java.time.LocalDate.parse(input, formatter);
                correct.add(input);
                break;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
        return new TestQuestion(dq, correct);
    }

    /**
     * Constructs a matching test question.
     */
    private static TestQuestion createMatchingTest() {
        System.out.print("Enter the prompt for your matching question: ");
        String prompt = scanner.nextLine();
        int numPairs = 0;
        while (true) {
            System.out.print("Enter the number of items on each side (>=2): ");
            String input = scanner.nextLine().trim();
            try {
                numPairs = Integer.parseInt(input);
                if (numPairs < 2) {
                    System.out.println("There must be at least two pairs for a matching question.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        List<String> leftItems = new ArrayList<>();
        List<String> rightItems = new ArrayList<>();
        for (int i = 0; i < numPairs; i++) {
            char letter = (char) ('A' + i);
            System.out.print("Enter left item " + letter + ": ");
            leftItems.add(scanner.nextLine());
        }
        for (int i = 0; i < numPairs; i++) {
            int number = i + 1;
            System.out.print("Enter right item " + number + ": ");
            rightItems.add(scanner.nextLine());
        }
        Matching m = new Matching(prompt, leftItems, rightItems);
        // solicit correct matches
        List<String> correct = new ArrayList<>();
        for (int i = 0; i < numPairs; i++) {
            char letter = (char) ('A' + i);
            while (true) {
                System.out.print("Enter correct match for item " + letter + " (1-" + numPairs + "): ");
                String input = scanner.nextLine().trim();
                try {
                    int idx = Integer.parseInt(input);
                    if (idx < 1 || idx > numPairs) {
                        System.out.println("Please enter a number between 1 and " + numPairs + ".");
                        continue;
                    }
                    correct.add(letter + "-" + idx);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }
        }
        return new TestQuestion(m, correct);
    }

    /**
     * Displays the current test without showing correct answers.
     */
    private static void displayTestWithoutAnswers() {
        if (currentTest == null) {
            System.out.println("You must have a test loaded in order to display it.");
            return;
        }
        currentTest.display();
    }

    /**
     * Displays the current test with correct answers.
     */
    private static void displayTestWithAnswers() {
        if (currentTest == null) {
            System.out.println("You must have a test loaded in order to display it.");
            return;
        }
        currentTest.displayWithAnswers();
    }

    /**
     * Loads a test from the tests directory.  Shows the user a list of
     * available files and prompts for selection.
     */
    private static void loadTest() {
        File dir = new File(TEST_DIR);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No test files found in " + TEST_DIR + ".");
            return;
        }
        List<File> testFiles = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                testFiles.add(f);
            }
        }
        if (testFiles.isEmpty()) {
            System.out.println("No test files found in " + TEST_DIR + ".");
            return;
        }
        System.out.println("Please select a file to load:");
        for (int i = 0; i < testFiles.size(); i++) {
            System.out.println((i + 1) + ") " + testFiles.get(i).getName());
        }
        int selection = -1;
        while (true) {
            System.out.print("Enter the number of the file to load (or 0 to cancel): ");
            String input = scanner.nextLine().trim();
            try {
                selection = Integer.parseInt(input);
                if (selection == 0) {
                    System.out.println("Load cancelled.");
                    return;
                }
                if (selection < 1 || selection > testFiles.size()) {
                    System.out.println("Please select a valid number between 1 and " + testFiles.size() + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        File chosen = testFiles.get(selection - 1);
        try {
            Test loaded = Test.loadFromFile(chosen.getPath());
            currentTest = loaded;
            System.out.println("Test '" + loaded.getName() + "' loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load test: " + e.getMessage());
        }
    }

    /**
     * Saves the current test to a file in the tests directory.
     */
    private static void saveTest() {
        if (currentTest == null) {
            System.out.println("You must have a test loaded in order to save it.");
            return;
        }
        // Suggest a filename based on test name
        String defaultName = currentTest.getName().replaceAll("[^a-zA-Z0-9_-]", "_") + ".tst";
        System.out.print("Enter the filename to save the test (default: " + defaultName + "): ");
        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) {
            fileName = defaultName;
        }
        File file = new File(TEST_DIR, fileName);
        try {
            currentTest.saveToFile(file.getPath());
            System.out.println("Test saved to " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error saving test: " + e.getMessage());
        }
    }

    /**
     * Takes the current test by prompting the user for answers and saving
     * the response set.
     */
    private static void takeTest() {
        if (currentTest == null) {
            System.out.println("You must have a test loaded in order to take it.");
            return;
        }
        ResponseSet responses = currentTest.takeTest(scanner);
        // Save responses to test responses directory
        String respFileName = responses.generateFileName();
        File file = new File(TEST_RESPONSE_DIR, respFileName);
        try {
            responses.saveToFile(file.getPath());
            System.out.println("Responses saved to " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error saving responses: " + e.getMessage());
        }
    }

    /**
     * Modifies the current test.  Prompts the user to select a question
     * and then modifies the question and correct answers.
     */
    private static void modifyTest() {
        if (currentTest == null) {
            System.out.println("You must have a test loaded in order to modify it.");
            return;
        }
        currentTest.modifyTest(scanner);
    }

    /**
     * Tabulates the responses for the current survey.  Prompts the user
     * to select which survey to tabulate, loads all response sets for
     * that survey and prints a summary of answers by question.
     */
    private static void tabulateSurvey() {
        // Ensure there is at least one survey loaded to know what to tabulate
        if (currentSurvey == null) {
            System.out.println("You must have a survey loaded in order to tabulate it.");
            return;
        }
        // Gather all response sets for this survey
        List<ResponseSet> sets = loadResponseSets(currentSurvey.getName(), SURVEY_RESPONSE_DIR);
        if (sets.isEmpty()) {
            System.out.println("No responses found for survey '" + currentSurvey.getName() + "'.");
            return;
        }
        // Tabulate each question
        System.out.println("Tabulation of survey: " + currentSurvey.getName());
        List<Question> qs = currentSurvey.getQuestions();
        for (int i = 0; i < qs.size(); i++) {
            Question q = qs.get(i);
            System.out.println();
            System.out.print((i + 1) + ") ");
            q.display();
            // Collect responses for this question
            List<List<String>> ansForThis = new ArrayList<>();
            for (ResponseSet rs : sets) {
                List<List<String>> ansList = rs.getResponses();
                if (i < ansList.size()) {
                    ansForThis.add(ansList.get(i));
                }
            }
            // Summarize
            if (q instanceof TrueFalse || q instanceof MultipleChoice) {
                Map<String, Integer> counts = new HashMap<>();
                List<String> options = new ArrayList<>();
                if (q instanceof TrueFalse) {
                    options.add("True");
                    options.add("False");
                } else {
                    MultipleChoice mc = (MultipleChoice) q;
                    int n = mc.getChoices().size();
                    for (int j = 0; j < n; j++) {
                        char letter = (char) ('A' + j);
                        options.add(String.valueOf(letter));
                    }
                }
                for (String opt : options) {
                    counts.put(opt, 0);
                }
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
                for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            } else if (q instanceof Essay) {
                for (List<String> resp : ansForThis) {
                    for (String ans : resp) {
                        System.out.println(ans);
                    }
                }
            } else if (q instanceof Matching) {
                Map<String, Integer> comboCounts = new HashMap<>();
                for (List<String> resp : ansForThis) {
                    List<String> pairs = new ArrayList<>();
                    for (String ans : resp) {
                        pairs.add(ans.trim().toUpperCase(Locale.ROOT));
                    }
                    pairs.sort((a, b) -> {
                        String la = a.split("-")[0];
                        String lb = b.split("-")[0];
                        return la.compareTo(lb);
                    });
                    String key = String.join("|", pairs);
                    comboCounts.put(key, comboCounts.getOrDefault(key, 0) + 1);
                }
                Matching m = (Matching) q;
                // display items
                List<String> left = m.getLeftItems();
                List<String> right = m.getRightItems();
                System.out.println();
                for (int j = 0; j < left.size(); j++) {
                    char letter = (char) ('A' + j);
                    int num = j + 1;
                    System.out.printf("%c) %s %d) %s%n", letter, left.get(j), num, right.get(j));
                }
                for (Map.Entry<String, Integer> entry : comboCounts.entrySet()) {
                    int count = entry.getValue();
                    System.out.println(count);
                    String[] pairs = entry.getKey().split("\\|");
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

    /**
     * Tabulates responses for the current test.  Loads all response sets
     * associated with the current test and summarises responses by
     * question.
     */
    private static void tabulateTest() {
        if (currentTest == null) {
            System.out.println("You must have a test loaded in order to tabulate it.");
            return;
        }
        List<ResponseSet> sets = loadResponseSets(currentTest.getName(), TEST_RESPONSE_DIR);
        if (sets.isEmpty()) {
            System.out.println("No responses found for test '" + currentTest.getName() + "'.");
            return;
        }
        currentTest.tabulate(sets);
    }

    /**
     * Grades a test by allowing the user to select a saved response set
     * and computing the score.  Essay questions are excluded from
     * automatic grading.
     */
    private static void gradeTest() {
        // list available test files first
        File dir = new File(TEST_DIR);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No tests exist to grade.");
            return;
        }
        List<File> testFiles = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                testFiles.add(f);
            }
        }
        if (testFiles.isEmpty()) {
            System.out.println("No tests exist to grade.");
            return;
        }
        System.out.println("Select an existing test to grade:");
        for (int i = 0; i < testFiles.size(); i++) {
            System.out.println((i + 1) + ") " + testFiles.get(i).getName());
        }
        int testSelection = -1;
        while (true) {
            System.out.print("Enter the number of the test (or 0 to cancel): ");
            String input = scanner.nextLine().trim();
            try {
                testSelection = Integer.parseInt(input);
                if (testSelection == 0) {
                    System.out.println("Operation cancelled.");
                    return;
                }
                if (testSelection < 1 || testSelection > testFiles.size()) {
                    System.out.println("Please enter a valid number between 1 and " + testFiles.size() + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        File testFile = testFiles.get(testSelection - 1);
        Test test;
        try {
            test = Test.loadFromFile(testFile.getPath());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load test: " + e.getMessage());
            return;
        }
        // Find all response sets for this test
        List<ResponseSet> responses = loadResponseSets(test.getName(), TEST_RESPONSE_DIR);
        if (responses.isEmpty()) {
            System.out.println("No responses found for test '" + test.getName() + "'.");
            return;
        }
        // Display list of responses
        System.out.println("Select an existing response set:");
        for (int i = 0; i < responses.size(); i++) {
            ResponseSet rs = responses.get(i);
            // Format timestamp for readability
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String ts = fmt.format(rs.getTimestamp());
            System.out.println((i + 1) + ") " + test.getName() + " - Response " + (i + 1) + " (" + ts + ")");
        }
        int respSelection = -1;
        while (true) {
            System.out.print("Enter the number of the response set (or 0 to cancel): ");
            String input = scanner.nextLine().trim();
            try {
                respSelection = Integer.parseInt(input);
                if (respSelection == 0) {
                    System.out.println("Operation cancelled.");
                    return;
                }
                if (respSelection < 1 || respSelection > responses.size()) {
                    System.out.println("Please enter a valid number between 1 and " + responses.size() + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
        ResponseSet chosenRs = responses.get(respSelection - 1);
        int totalQuestions = test.size();
        int essayCount = 0;
        for (TestQuestion tq : test.getQuestions()) {
            if (tq.isEssay()) {
                essayCount++;
            }
        }
        int correct = test.countCorrect(chosenRs);
        // Compute grade: each question worth equal weight
        double pointsPerQuestion = 100.0 / totalQuestions;
        double grade = correct * pointsPerQuestion;
        int autoQuestions = totalQuestions - essayCount;
        double autoPoints = autoQuestions * pointsPerQuestion;
        // Round grade and autoPoints to nearest whole number for display
        int gradeRounded = (int) Math.round(grade);
        int autoPointsRounded = (int) Math.round(autoPoints);
        System.out.print("You received a " + gradeRounded + " on the test. The test was worth 100 points, but only " + autoPointsRounded + " of those points could be auto graded because there ");
        if (essayCount == 1) {
            System.out.println("was 1 essay question.");
        } else {
            System.out.println("were " + essayCount + " essay questions.");
        }
    }

    /**
     * Loads all response sets from the given directory whose filenames
     * correspond to the provided survey or test name.  The name is
     * sanitised in the same way filenames are generated.  If parsing
     * fails for a file, it is skipped.
     *
     * @param surveyOrTestName the name of the survey or test
     * @param directory path to the responses directory
     * @return list of ResponseSet objects
     */
    private static List<ResponseSet> loadResponseSets(String surveyOrTestName, String directory) {
        List<ResponseSet> sets = new ArrayList<>();
        File dir = new File(directory);
        File[] files = dir.listFiles();
        if (files == null) {
            return sets;
        }
        // compute safe name prefix
        String safeName = surveyOrTestName.replaceAll("[^a-zA-Z0-9_-]", "_");
        for (File f : files) {
            if (!f.isFile()) {
                continue;
            }
            String fname = f.getName();
            if (!fname.startsWith(safeName + "_")) {
                continue;
            }
            try {
                ResponseSet rs = ResponseSet.loadFromFile(f.getPath());
                sets.add(rs);
            } catch (IOException | ClassNotFoundException e) {
                // Skip files that cannot be parsed
            }
        }
        return sets;
    }

    /**
     * Presents the survey submenu (Menu 2) allowing the user to create,
     * display, load, save, take, modify or tabulate surveys.  The user
     * can return to the main menu.
     */
    private static void surveyMenu() {
        while (true) {
            System.out.println();
            System.out.println("Survey Menu");
            System.out.println("1) Create a new Survey");
            System.out.println("2) Display an existing Survey");
            System.out.println("3) Load an existing Survey");
            System.out.println("4) Save the current Survey");
            System.out.println("5) Take the current Survey");
            System.out.println("6) Modify the current Survey");
            System.out.println("7) Tabulate a survey");
            System.out.println("8) Return to previous menu");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    createNewSurvey();
                    break;
                case "2":
                    displaySurvey();
                    break;
                case "3":
                    loadSurvey();
                    break;
                case "4":
                    saveSurvey();
                    break;
                case "5":
                    takeSurvey();
                    break;
                case "6":
                    modifySurvey();
                    break;
                case "7":
                    tabulateSurvey();
                    break;
                case "8":
                    return;
                default:
                    System.out.println("Invalid choice. Please select a number from the menu.");
            }
        }
    }

    /**
     * Presents the test submenu (Menu 2) allowing the user to create,
     * display, load, save, take, modify, tabulate or grade tests.  The
     * user can return to the main menu.
     */
    private static void testMenu() {
        while (true) {
            System.out.println();
            System.out.println("Test Menu");
            System.out.println("1) Create a new Test");
            System.out.println("2) Display an existing Test without correct answers");
            System.out.println("3) Display an existing Test with correct answers");
            System.out.println("4) Load an existing Test");
            System.out.println("5) Save the current Test");
            System.out.println("6) Take the current Test");
            System.out.println("7) Modify the current Test");
            System.out.println("8) Tabulate a Test");
            System.out.println("9) Grade a Test");
            System.out.println("10) Return to the previous menu");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    createNewTest();
                    break;
                case "2":
                    displayTestWithoutAnswers();
                    break;
                case "3":
                    displayTestWithAnswers();
                    break;
                case "4":
                    loadTest();
                    break;
                case "5":
                    saveTest();
                    break;
                case "6":
                    takeTest();
                    break;
                case "7":
                    modifyTest();
                    break;
                case "8":
                    tabulateTest();
                    break;
                case "9":
                    gradeTest();
                    break;
                case "10":
                    return;
                default:
                    System.out.println("Invalid choice. Please select a number from the menu.");
            }
        }
    }

    // Helper functions for test operations will be defined below.
}