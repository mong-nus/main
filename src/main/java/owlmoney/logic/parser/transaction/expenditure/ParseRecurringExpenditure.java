package owlmoney.logic.parser.transaction.expenditure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import owlmoney.logic.command.Command;
import owlmoney.logic.parser.ParseRawData;
import owlmoney.logic.parser.exception.ParserException;
import owlmoney.logic.regex.RegexUtil;

/**
 * Abstracts common RecurringExpenditure methods and functions where the child parsers will inherit from.
 */
public abstract class ParseRecurringExpenditure {
    HashMap<String, String> expendituresParameters = new HashMap<String, String>();
    private ParseRawData parseRawData = new ParseRawData();
    private String rawData;
    String type;
    private static final String[] EXPENDITURE_KEYWORD = new String[] {
        "/amount", "/desc", "/category", "/from", "/transno"
    };
    private static final List<String> EXPENDITURE_KEYWORD_LISTS = Arrays.asList(EXPENDITURE_KEYWORD);
    static final String AMOUNT = "/amount";
    static final String DATE = "/date";
    static final String DESCRIPTION = "/desc";
    static final String CATEGORY = "/category";
    static final String FROM = "/from";
    static final String TRANSNO = "/transno";

    /**
     * Creates an instance of any ParseRecurringExpenditure type object.
     *
     * @param data Raw user input data.
     * @param type Represents type of recurring expenditure to be added.
     */
    ParseRecurringExpenditure(String data, String type) {
        this.rawData = data;
        this.type = type;
    }

    /**
     * Checks the user input for any redundant parameters.
     *
     * @param parameter Redundant parameter to check for,
     * @param command   Command the user performed.
     * @throws ParserException If a redundant parameter is detected.
     */
    void checkRedundantParameter(String parameter, String command) throws ParserException {
        if (rawData.contains(parameter)) {
            throw new ParserException(command + " /expenditure should not contain " + parameter);
        }
    }

    /**
     * Checks if the first parameter is a valid parameter.
     *
     * @throws ParserException If the first parameter is invalid.
     */
    void checkFirstParameter() throws ParserException {
        String[] rawDateSplit = rawData.split(" ", 2);
        if (!EXPENDITURE_KEYWORD_LISTS.contains(rawDateSplit[0])) {
            throw new ParserException("Incorrect parameter " + rawDateSplit[0]);
        }
    }

    /**
     * Fills a hash table mapping each user input to each parameter.
     *
     * @throws ParserException If duplicate parameters are detected.
     */
    public void fillHashTable() throws ParserException {
        expendituresParameters.put(AMOUNT,
                parseRawData.extractParameter(rawData, AMOUNT, EXPENDITURE_KEYWORD));
        expendituresParameters.put(DESCRIPTION,
                parseRawData.extractParameter(rawData, DESCRIPTION, EXPENDITURE_KEYWORD));
        expendituresParameters.put(CATEGORY,
                parseRawData.extractParameter(rawData, CATEGORY, EXPENDITURE_KEYWORD));
        expendituresParameters.put(FROM,
                parseRawData.extractParameter(rawData, FROM, EXPENDITURE_KEYWORD));
        expendituresParameters.put(TRANSNO,
                parseRawData.extractParameter(rawData, TRANSNO, EXPENDITURE_KEYWORD));
    }

    /**
     * Checks if the amount entered by the user is a double and only contains numbers.
     *
     * @param valueString String to be converted to double as the user's amount.
     * @throws ParserException If the string is not a double value.
     */
    void checkAmount(String valueString) throws ParserException {
        if (!RegexUtil.regexCheckMoney(valueString)) {
            throw new ParserException("/amount can only be positive numbers"
                    + " with at most 9 digits and 2 decimal places");
        }
    }

    /**
     * Checks if the transaction number or display number entered by the user is an integer.
     *
     * @param valueString String to be converted to integer.
     * @throws ParserException If the string is not an integer.
     */
    void checkInt(String variable, String valueString) throws ParserException {
        if (!RegexUtil.regexCheckListNumber(valueString)) {
            throw new ParserException(variable + " can only be a positive number with at most 9 digits");
        }
    }

    /**
     * Checks if the description entered by the user does not have special characters and is not too long.
     *
     * @param descString Deposit description.
     * @throws ParserException If the string has special characters or is too long.
     */
    void checkDescription(String descString, String keyword) throws ParserException {
        if (!RegexUtil.regexCheckDescription(descString)) {
            throw new ParserException(keyword + " can only contain numbers and letters and at most 50 characters");
        }
    }

    /**
     * Checks if the bank name entered by the user does not contain special character and not too long.
     *
     * @param nameString Name of bank
     * @throws ParserException If the name is too long or contain special characters.
     */
    void checkName(String nameString) throws ParserException {
        if (!RegexUtil.regexCheckName(nameString)) {
            throw new ParserException("/from can only contain letters and at most 30 characters");
        }
    }

    /**
     * Abstract method where each saving parser performs different checks on the parameters.
     *
     * @throws ParserException If any parameters fail the check.
     */
    public abstract void checkParameter() throws ParserException;

    /**
     * Abstract method where each saving parser creates different commands.
     *
     * @return Command to be executed.
     */
    public abstract Command getCommand();
}