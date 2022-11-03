package com.cscie97;

/**
 * Custom exception for error conditions in CommandProcessor class.
 * Captures the command that was failed and the reason for failure.
 *
 * @author Burak Ufuktepe
 */
public class CommandProcessorException extends Exception {

	/**
	 * Command that was failed.
	 */
	private String command;

	/**
	 * Attempted action.
	 */
	private String action;

	/**
	 * Reason for failure.
	 */
	private String reason;

	/**
	 * Line number of the failed command in the input file.
	 */
	private int lineNum;

	/**
	 * Constructor.
	 *
	 * @param aCommand  Command that was failed
	 * @param anAction  Attempted action
	 * @param aReason  Reason for failure
	 * @param aLineNumber  Line number of the failed command in the input file
	 */
	public CommandProcessorException(String aCommand, String anAction, String aReason, int aLineNumber) {
		this.command = aCommand;
		this.action = anAction;
		this.reason = aReason;
		this.lineNum = aLineNumber;
	}

	/**
	 * Sets the line number of the failed command in the input file.
	 *
	 * @param aLineNumber  Line number of the failed command in the input file
	 */
	public void setLineNum(int aLineNumber) {
		this.lineNum = aLineNumber;
	}

	/**
	 * Sets the command that was attempted.
	 *
	 * @param command  Attempted command
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Returns a string that includes the command, reason, and the line number of the failed command in the input file.
	 *
	 * @return  String representation of the exception
	 */
	@Override
	public String getMessage(){
		return String.format("%-20s : %d%n%-20s : %s%n%-20s : %s%n%-20s : %s%n", "ERROR AT LINE NUMBER", this.lineNum, "FAILED "
						+ "COMMAND", this.command, "ACTION", this.action, "REASON", this.reason);
	}
}

