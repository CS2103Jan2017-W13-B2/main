//@@author A0143504R
package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.task.Name;
import seedu.address.testutil.TestTask;
import seedu.address.testutil.TestUtil;

public class RedoCommandTest extends TaskManagerGuiTest {

    private TestTask[] expectedList = td.getTypicalTasks();

    /**
     * Tries to redo an add command
     */
    @Test
    public void redo_addCommand() {
        TestTask taskToAdd = td.alice;
        commandBox.runCommand(taskToAdd.getAddCommand());
        commandBox.runCommand(UndoCommand.COMMAND_WORD);

        expectedList = TestUtil.addTasksToList(expectedList, taskToAdd);
        assertRedoSuccess(expectedList);
    }

    /**
     * Tries to redo a delete command
     */
    @Test
    public void redo_deleteCommand() {
        int targetIndex = 1;
        commandBox.runCommand(DeleteCommand.COMMAND_WORD + " " + targetIndex);
        commandBox.runCommand(UndoCommand.COMMAND_WORD);

        expectedList = TestUtil.removeTaskFromList(expectedList, targetIndex);
        assertRedoSuccess(expectedList);
    }

    /**
     * Tries to redo a clear command
     */
    @Test
    public void redo_clearCommand() {
        commandBox.runCommand("mark 1");
        commandBox.runCommand("mark 2");
        commandBox.runCommand(ClearCommand.COMMAND_WORD + " done");
        commandBox.runCommand(UndoCommand.COMMAND_WORD);

        expectedList = TestUtil.removeTaskFromList(expectedList, 1);
        expectedList = TestUtil.removeTaskFromList(expectedList, 1);
        assertRedoSuccess(expectedList);
    }

    /**
     * Tries to redo a mark command
     */
    @Test
    public void redo_markCommand() {
        int targetIndex = 1;
        commandBox.runCommand(MarkCommand.COMMAND_WORD + " " + 1);
        commandBox.runCommand(UndoCommand.COMMAND_WORD);

        expectedList[targetIndex - 1].setStatus("Done");
        assertRedoSuccess(expectedList);
    }

    /**
     * Tries to redo an edit command
     * @throws IllegalValueException
     */
    @Test
    public void redo_editCommand() throws IllegalValueException {
        int targetIndex = 1;
        String detailsToEdit = "Bobby";
        commandBox.runCommand(EditCommand.COMMAND_WORD + " " + targetIndex + " " + detailsToEdit);
        commandBox.runCommand(UndoCommand.COMMAND_WORD);

        expectedList[targetIndex - 1].setName(new Name("Bobby"));
        assertRedoSuccess(expectedList);
    }

    /**
     * Tries to redo a command with an invalid command word
     */
    @Test
    public void redo_invalidCommand_fail() {
        commandBox.runCommand("rredo");
        assertResultMessage(String.format(Messages.MESSAGE_UNKNOWN_COMMAND));
    }

    /**
     * Tries to redo a command without a previous undo
     */
    @Test
    public void redo_noChange_fail() {
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        assertResultMessage(String.format(RedoCommand.MESSAGE_NO_CHANGE));
    }

    /**
     * Runs redo command and checks whether the current list matches the expected list
     * @param expectedList list after redo command is carried out
     */
    private void assertRedoSuccess(TestTask[] expectedList) {
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        assertTrue(taskListPanel.isListMatching(expectedList));
        assertResultMessage(String.format(RedoCommand.MESSAGE_SUCCESS));
    }
}
