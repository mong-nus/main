package owlmoney.logic.parser.goals;

import owlmoney.logic.command.Command;
import owlmoney.logic.parser.ParseRawData;
import owlmoney.logic.parser.exception.ParserException;
import owlmoney.logic.regex.RegexUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Abstracts common Goals methods and functions where the child parsers will inherit from.
 */
public abstract class ParseGoals {
    HashMap<String, String> goalsParameters = new HashMap<>();
    private ParseRawData parseRawData = new ParseRawData();
    private String rawData;

    private static final String[] GOALS_KEYWORD = new String[]{"/name", "/amount", "/by",
        "/newname", "/from", "/num", "/in"};
    private static final List<String> GOALS_KEYWORD_LISTS = Arrays.asList(GOALS_KEYWORD);
    static final String NAME = "/name";
    static final String AMOUNT = "/amount";
    static final String BY = "/by";
    static final String NEW_NAME = "/newname";
    static final String FROM = "/from";
    static final String IN = "/in";

    /**
     * Creates an instance of any ParseGoals type object.
     *
     * @param data Raw user input data.
     */
    ParseGoals(String data) {
        this.rawData = data;
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
            throw new ParserException(command + " /goals should not contain " + parameter);
        }
    }

    /**
     * Checks if the first parameter is a valid parameter.
     *
     * @throws ParserException If the first parameter is invalid.
     */
    void checkFirstParameter() throws ParserException {
        String[] rawDataSplit = rawData.split(" ", 2);
        if (!GOALS_KEYWORD_LISTS.contains(rawDataSplit[0])) {
            throw new ParserException("Incorrect parameter: " + rawDataSplit[0]);
        }
    }

    /**
     * Fills a hash table mapping each user input to each parameter.
     *
     * @throws ParserException If duplicate parameters are detected.
     */
    public void fillHashTable() throws ParserException {
        goalsParameters.put(NAME,
                parseRawData.extractParameter(rawData, NAME, GOALS_KEYWORD));
        goalsParameters.put(AMOUNT,
                parseRawData.extractParameter(rawData, AMOUNT, GOALS_KEYWORD));
        goalsParameters.put(BY,
                parseRawData.extractParameter(rawData, BY, GOALS_KEYWORD));
        goalsParameters.put(NEW_NAME,
                parseRawData.extractParameter(rawData, NEW_NAME, GOALS_KEYWORD));
        goalsParameters.put(IN,
                parseRawData.extractParameter(rawData, IN, GOALS_KEYWORD));
        goalsParameters.put(FROM,
                parseRawData.extractParameter(rawData, FROM, GOALS_KEYWORD));
    }

    /**
     * Checks if the amount entered by the user is a double and only contains numbers.
     *
     * @param valueString String to be converted to double as the user's amount.
     * @throws ParserException If the string is not a double value.
     */
    void checkAmount(String valueString) throws ParserException {
        if (!RegexUtil.regexCheckBankAmount(valueString)) {
            throw new ParserException("/amount can only be numbers with at most 9 digits, 2 decimal places"
                    + " and a value of at least 0");
        }
    }

    /**
     * Checks if the goal name entered by the user does not contain special character and not too long.
     *
     * @param key  /name or /newname
     * @param name Name of goal
     * @throws ParserException If the name is too long or contain special characters.
     */
    void checkName(String key, String name) throws ParserException {
        if (!RegexUtil.regexCheckName(name)) {
            throw new ParserException(key + " can only be alphanumeric and at most 30 characters");
        }
    }

    /**
     * Checks if the date set for the goal is of valid format and not before now.
     *
     * @param dateString Date to be checked.
     * @return Date if checks pass.
     * @throws ParserException If date format is invalid.
     */
    Date checkDate(String dateString) throws ParserException {
        if (RegexUtil.regexCheckDateFormat(dateString)) {
            DateFormat temp = new SimpleDateFormat("dd/MM/yyyy");
            temp.setLenient(false);
            Date date;
            try {
                date = temp.parse(dateString);
                if (((Date) date).compareTo(new Date()) < 0) {
                    throw new ParserException("/date has already passed");
                }
                return date;
            } catch (ParseException e) {
                throw new ParserException("Incorrect date format."
                        + " Date format is dd/mm/yyyy in year range of 1900-2099");
            }
        }
        throw new ParserException("Incorrect date format." + " Date format is dd/mm/yyyy in year range of 1900-2099");
    }

    /**
     * Checks if days set for goal is valid format.
     *
     * @param variable    number of days.
     * @param valueString value of number of days.
     * @throws ParserException If days is set too long after or contain invalid parameters.
     */
    void checkDay(String variable, String valueString) throws ParserException {
        if (!RegexUtil.regexCheckDay(valueString)) {
            throw new ParserException(variable + " can only be a positive number up to only 365 days!");
        }
    }

    /**
     * Converts days to Date.
     *
     * @param day number of days in which user expects to achieve goal.
     * @return Date of goal deadline.
     */
    Date convertDaysToDate(int day) {
        //count from now to number of days left
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * Checks the parameters entered by the user.
     *
     * @throws ParserException If any parameters fail the check.
     */
    public abstract void checkParameter() throws ParserException;

    /**
     * Gets the relevant command to be executed.
     *
     * @return Command to be executed.
     */
    public abstract Command getCommand();
}
