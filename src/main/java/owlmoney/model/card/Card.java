package owlmoney.model.card;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import owlmoney.model.card.exception.CardException;
import owlmoney.model.transaction.Transaction;
import owlmoney.model.transaction.TransactionList;
import owlmoney.model.transaction.exception.TransactionException;
import owlmoney.ui.Ui;

/**
 * Card class for initialisation of credit card object.
 */
public class Card {
    private String name;
    private double limit;
    private double rebate;
    private TransactionList paid;
    private TransactionList unpaid;
    private static final int OBJ_DOES_NOT_EXIST = -1;
    private static final int ONE_ARRAY_INDEX = 1;

    /**
     * Creates a Card with details of name, limit and rebate.
     *
     * @param name   A name for the credit card.
     * @param limit  Credit card monthly spending limit.
     * @param rebate Credit card monthly cash back rebate.
     */
    public Card(String name, double limit, double rebate) {
        this.name = name;
        this.limit = limit;
        this.rebate = rebate;
        this.paid = new TransactionList();
        this.unpaid = new TransactionList();
    }

    /**
     * Gets the card name of the credit card.
     *
     * @return name of the credit card.
     */
    String getName() {
        return this.name;
    }

    /**
     * Sets the card name for the credit card.
     *
     * @param name A name for the credit card.
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the card limit of the credit card.
     *
     * @return card limit of the credit card.
     */
    double getLimit() {
        return this.limit;
    }


    /**
     * Sets the card limit for the credit card.
     *
     * @param limit A name for the credit card.
     */
    void setLimit(double limit) {
        this.limit = limit;
    }

    /**
     * Gets the rebate of the credit card.
     *
     * @return rebate of the credit card.
     */
    double getRebate() {
        return this.rebate;
    }

    /**
     * Sets the rebate for the credit card.
     *
     * @param rebate Rebate for the credit card.
     */
    void setRebate(double rebate) {
        this.rebate = rebate;
    }

    /**
     * Checks if expenditure exceeds remaining card limit.
     *
     * @param exp Expenditure to be added.
     * @throws CardException If expenditure exceeds remaining card limit.
     */
    private void checkExpExceedRemainingLimit(Transaction exp) throws CardException {
        LocalDate date = exp.getLocalDate();
        double monthAmountSpent = unpaid.getMonthAmountSpent(date.getMonthValue(), date.getYear());
        double remainingMonthAmount = limit - monthAmountSpent;
        if (exp.getAmount() > remainingMonthAmount) {
            throw new CardException("Expenditure to be added cannot exceed remaining limit of $"
                    + remainingMonthAmount);
        }
    }

    /**
     * Adds expenditure to the credit card unpaid transaction list.
     *
     * @param exp  Expenditure to be added.
     * @param ui   Ui of OwlMoney.
     * @param type Type of account to add expenditure into
     * @throws CardException If expenditure exceeds card limit.
     */
    void addInExpenditure(Transaction exp, Ui ui, String type) throws CardException {
        this.checkExpExceedRemainingLimit(exp);
        unpaid.addExpenditureToList(exp, ui, type);
    }

    /**
     * Adds expenditure to the credit card paid transaction list.
     *
     * @param exp  Expenditure to be added.
     * @param ui   Ui of OwlMoney.
     * @param type Type of account to add expenditure into
     * @throws CardException If expenditure exceeds card limit.
     */
    void addInPaidExpenditure(Transaction exp, Ui ui, String type) throws CardException {
        this.checkExpExceedRemainingLimit(exp);
        paid.addExpenditureToList(exp, ui, type);
    }

    /**
     * Lists all the unpaid expenditures in the current credit card.
     *
     * @param ui         Ui of OwlMoney.
     * @param displayNum Number of expenditure to list.
     * @throws TransactionException If no expenditure is found or no expenditure is in the list.
     */
    void listAllExpenditure(Ui ui, int displayNum) throws TransactionException {
        try {
            unpaid.listExpenditure(ui, displayNum);
        } catch (TransactionException e) {
            throw new TransactionException("There are no expenditures in this card.");
        }

    }

    /**
     * Lists all the paid expenditures in the current credit card.
     *
     * @param ui         Ui of OwlMoney.
     * @param displayNum Number of expenditure to list.
     * @throws TransactionException If no expenditure is found or no expenditure is in the list.
     */
    void listAllPaidExpenditure(Ui ui, int displayNum) throws TransactionException {
        try {
            paid.listExpenditure(ui, displayNum);
        } catch (TransactionException e) {
            throw new TransactionException("There are no expenditures in this card.");
        }
    }

    /**
     * Deletes an expenditure in the current credit card.
     *
     * @param exId Transaction number of the transaction.
     * @param ui   Ui of OwlMoney.
     * @throws TransactionException If invalid transaction.
     */
    void deleteExpenditure(int exId, Ui ui) throws TransactionException {
        unpaid.deleteExpenditureFromList(exId, ui);
    }

    /**
     * Edits the expenditure details from the current card account.
     *
     * @param expNum   Transaction number.
     * @param desc     New description.
     * @param amount   New amount.
     * @param date     New date.
     * @param category New category.
     * @param ui       Ui of OwlMoney.
     * @throws TransactionException If incorrect date format.
     * @throws CardException        If amount is negative after editing expenditure.
     */
    void editExpenditureDetails(int expNum, String desc, String amount, String date, String category, Ui ui)
            throws TransactionException, CardException {
        double remainingLimit = 0;
        if (date.isBlank() || date.isEmpty()) {
            int expMonth = unpaid.getTransactionMonthByIndex(expNum);
            int expYear = unpaid.getTransactionYearByIndex(expNum);
            remainingLimit = limit - unpaid.getMonthAmountSpent(expMonth, expYear);
        } else {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate expDate = LocalDate.parse(date, dateFormat);
            int expMonth = expDate.getMonthValue();
            int expYear = expDate.getYear();
            remainingLimit = limit - unpaid.getMonthAmountSpent(expMonth, expYear);
        }

        double existingExpAmount = unpaid.getExpenditureAmount(expNum);
        double limitLeftExcludeExistingExp = remainingLimit + existingExpAmount;
        if (!(amount.isEmpty() || amount.isBlank())
                && limitLeftExcludeExistingExp < Double.parseDouble(amount)) {
            throw new CardException("Edited expenditure cannot exceed $" + limitLeftExcludeExistingExp);
        }
        unpaid.editExpenditure(expNum, desc, amount, date, category, ui);
    }

    /** Returns remaining limit of this current month.
     *
     * @return      Remaining limit of this current month.
     */
    public double getRemainingLimitNow() {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();
        return limit - unpaid.getMonthAmountSpent(month, year);
    }

    /**
     * Returns true if unpaid expenditure list is empty.
     *
     * @return True if unpaid expenditure list is empty.
     */
    public boolean isEmpty() {
        return unpaid.expListIsEmpty();
    }

    /**
     * Finds the transactions from the card object that matches with the keywords specified by the user.
     *
     * @param fromDate The date to search from.
     * @param toDate The date to search until.
     * @param description The description keyword to match against.
     * @param category The category keyword to match against.
     * @param ui The object required for printing.
     * @throws TransactionException If incorrect date format.
     */
    void findTransaction(String fromDate, String toDate, String description, String category, Ui ui)
            throws TransactionException {
        unpaid.findMatchingTransaction(fromDate, toDate, description, category, ui);
    }

    /**
     * Returns the total amount of all unpaid card expenditures of specified date.
     *
     * @param date  The YearMonth date of unpaid card expenditures to get the total amount.
     * @return      The total amount of all unpaid card expenditures of specified date.
     */
    public double getUnpaidBillAmount(YearMonth date) {
        return unpaid.getMonthAmountSpent(date.getMonthValue(), date.getYear());
    }

    /**
     * Returns the total amount of all paid card expenditures of specified date.
     *
     * @param date  The YearMonth date of paid card expenditures to get the total amount.
     * @return      The total amount of all paid card expenditures of specified date.
     */
    public double getPaidBillAmount(YearMonth date) {
        return paid.getMonthAmountSpent(date.getMonthValue(), date.getYear());
    }

    /**
     * Transfers expenditures from unpaid list to paid list.
     *
     * @param cardDate      The YearMonth date of expenditures to transfer.
     * @param type          Type of expenditure (card or bank).
     * @throws TransactionException If invalid transaction when deleting.
     */
    void transferExpUnpaidToPaid(YearMonth cardDate, String type) throws TransactionException {
        try {
            for (int i = 0; i < unpaid.getSize(); i++) {
                int id = unpaid.getExpenditureIdByYearMonth(cardDate);
                if (id != OBJ_DOES_NOT_EXIST) {
                    Transaction exp = unpaid.getExpenditureObjectByYearMonth(id);
                    paid.addExpenditureToList(exp, type);
                    unpaid.deleteExpenditureFromList(id + ONE_ARRAY_INDEX);
                    i -= ONE_ARRAY_INDEX;
                }
            }
        } catch (TransactionException e) {
            throw new TransactionException("There are no expenditures in this card.");
        }
    }

    /**
     * Transfers expenditures from paid list to unpaid list.
     *
     * @param cardDate      The YearMonth date of expenditures to transfer.
     * @param type          Type of expenditure (card or bank).
     * @throws TransactionException If invalid transaction when deleting.
     */
    void transferExpPaidToUnpaid(YearMonth cardDate, String type) throws TransactionException {
        try {
            for (int i = 0; i < paid.getSize(); i++) {
                int id = paid.getExpenditureIdByYearMonth(cardDate);
                if (id != OBJ_DOES_NOT_EXIST) {
                    Transaction exp = paid.getExpenditureObjectByYearMonth(id);
                    unpaid.addExpenditureToList(exp, type);
                    paid.deleteExpenditureFromList(id + ONE_ARRAY_INDEX);
                    i -= ONE_ARRAY_INDEX;
                }
            }
        } catch (TransactionException e) {
            throw new TransactionException("There are no expenditures in this card.");
        }
    }
}
