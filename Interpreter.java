import java.util.*;

public class Interpreter {
    private static final Set<String> keywords = Set.of("+", "-", "*", "=", ";");
    private static final Map<String, Integer> variables = new HashMap<>();
    private static final List<String> tokens = new ArrayList<>();
    private static int curIndex = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your program (end with an empty line):");
        StringBuilder input = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty())
                break;
            input.append(line).append(" ");
        }

        try {
            tokenize(input.toString());
            parseAndEvaluate();
            printVariables();
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            scanner.close();
        }
    }

    // Tokenizer: Converts the input into tokens
    private static void tokenize(String input) {
        StringBuilder token = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else if (keywords.contains(String.valueOf(c)) || c == '(' || c == ')') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                token.append(c);
            }
        }
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
    }

    // Parser and evaluator
    private static void parseAndEvaluate() {
        while (curIndex < tokens.size()) {
            assignment();
        }
    }

    private static void assignment() {
        String identifier = currentToken();
        if (!isIdentifier(identifier))
            throw new RuntimeException("Invalid identifier");
        nextToken();
        match("=");
        int value = exp();
        match(";");
        variables.put(identifier, value);
    }

    private static int exp() {
        int value = term();
        while (isCurrentToken("+") || isCurrentToken("-")) {
            String operator = currentToken();
            nextToken();
            int nextValue = term();
            value = operator.equals("+") ? value + nextValue : value - nextValue;
        }
        return value;
    }

    private static int term() {
        int value = factor();
        while (isCurrentToken("*")) {
            nextToken();
            value *= factor();
        }
        return value;
    }

    private static int factor() {
        if (isCurrentToken("(")) {
            nextToken();
            int value = exp();
            match(")");
            return value;
        } else if (isCurrentToken("-")) {
            nextToken();
            return -factor();
        } else if (isCurrentToken("+")) {
            nextToken();
            return factor();
        } else if (isLiteral(currentToken())) {
            int value = Integer.parseInt(currentToken());
            nextToken();
            return value;
        } else if (isIdentifier(currentToken())) {
            String identifier = currentToken();
            if (!variables.containsKey(identifier))
                throw new RuntimeException("Uninitialized variable");
            nextToken();
            return variables.get(identifier);
        } else {
            throw new RuntimeException("Syntax error in factor");
        }
    }

    // Utility methods for parsing
    private static String currentToken() {
        if (curIndex >= tokens.size())
            return null;
        return tokens.get(curIndex);
    }

    private static void nextToken() {
        curIndex++;
    }

    private static void match(String expected) {
        if (!expected.equals(currentToken()))
            throw new RuntimeException("Syntax error: expected " + expected);
        nextToken();
    }

    private static boolean isCurrentToken(String token) {
        return token.equals(currentToken());
    }

    private static boolean isIdentifier(String token) {
        return token != null && token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    private static boolean isLiteral(String token) {
        return token != null && token.matches("0|[1-9][0-9]*");
    }

    // Print variables
    private static void printVariables() {
        variables.forEach((key, value) -> System.out.println(key + " = " + value));
    }
}
