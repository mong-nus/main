package owlmoney.logic.parser.goals;

import owlmoney.logic.command.Command;
import owlmoney.logic.command.goals.EditGoalsCommand;
import owlmoney.logic.parser.exception.ParserException;

import java.util.Date;
import java.util.Iterator;

/**
 * Represents the parsing of inputs for editing a goal.
 */
public class ParseEditGoals extends ParseGoals {

    private Date by;

    /**
     * Creates an instance of ParseEditGoals class.
     *
     * @param data raw data of the input.
     * @throws ParserException If valid parameters.
     */
    public ParseEditGoals(String data) throws ParserException {
        super(data);
        checkFirstParameter();
    }

    /**
     * Checks each user input for each parameter.
     *
     * @throws ParserException If there are any invalid user input.
     */
    @Override
    public void checkParameter() throws ParserException {
        Iterator<String> goalIterator = goalsParameters.keySet().iterator();
        int changeCounter = 0;
        while (goalIterator.hasNext()) {
            String key = goalIterator.next();
            String value = goalsParameters.get(key);
            if (NAME.equals(key) && (value.isEmpty() || value.isBlank())) {
                throw new ParserException("/name cannot be empty.");
            } else if (NAME.equals(key)) {
                checkName(NAME, value);
            }
            if (AMOUNT.equals(key) && !(value.isEmpty() || value.isBlank())) {
                checkAmount(value);
                changeCounter++;
            }
            if (NEW_NAME.equals(key) && !(value.isEmpty() || value.isBlank())) {
                checkName(NEW_NAME, value);
                changeCounter++;
            }
            if (BY.equals(key) && !(value.isEmpty() || value.isBlank())) {
                by = checkDate(value);
                changeCounter++;
            }
            if (FROM.equals(key) && !(value.isEmpty() || value.isBlank())) {
                checkName(FROM, value);
                changeCounter++;
            }
            if (IN.equals(key) && !(value.isEmpty() || value.isBlank())) {
                checkDay(IN, value);
                by = convertDaysToDate(Integer.parseInt(value));
                changeCounter++;
            }
        }
        if (changeCounter == 0) {
            throw new ParserException("Edit should have at least 1 differing parameter to change.");
        }
    }

    /**
     * Returns command to execute editing of goals.
     *
     * @return EditGoalsCommand to be executed.
     */
    @Override
    public Command getCommand() {
        EditGoalsCommand newEditGoalsCommand = new EditGoalsCommand(goalsParameters.get(NAME),
                goalsParameters.get(AMOUNT),
                by, goalsParameters.get(NEW_NAME), goalsParameters.get(FROM));
        return newEditGoalsCommand;
    }
}
