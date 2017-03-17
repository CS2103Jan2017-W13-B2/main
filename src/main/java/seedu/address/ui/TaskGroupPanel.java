package seedu.address.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.ShowTaskGroupEvent;
import seedu.address.model.task.ReadOnlyTask;

/**
 * Panel containing a group of tasks.
 */
public class TaskGroupPanel extends UiPart<Region> {

    private final Logger logger = LogsCenter.getLogger(TaskGroupPanel.class);
    private static final String FXML = "TaskGroupPanel.fxml";

    @FXML
    private TitledPane titledPane;

    @FXML
    private VBox taskGroupView;

    private String title;

    private int indexOffset;

    public TaskGroupPanel(String title, ObservableList<ReadOnlyTask> taskList, int indexOffset) {
        super(FXML);
        setIndexOffset(indexOffset);
        setConnections(taskList);
        setTitle(title, taskList.size());
        setExpandingListener();
        createTaskGroupView(taskList);
        closeTitlePane();
        registerAsAnEventHandler(this);
    }

    public void closeTitlePane() {
        titledPane.setExpanded(false);
    }

    public void openTitlePane() {
        titledPane.setExpanded(true);
    }

    public void setIndexOffset(int indexOffset) {
        this.indexOffset = indexOffset;
    }

    public void setTitle(String title, int taskCount) {
        this.title = title;
        titledPane.setText(title + " (" + taskCount + ")");
    }

    public String getTitle() {
        return this.title;
    }

    private void setConnections(ObservableList<ReadOnlyTask> taskList) {
        // When taskList changes, update everything
        taskList.addListener(new ListChangeListener<ReadOnlyTask>() {
            public void onChanged(Change<? extends ReadOnlyTask> change) {
                setTitle(title, taskList.size());
                createTaskGroupView(taskList);
            }
        });
    }

    /**
     * Instantiate TaskCard objects and add them to taskListView.
     */
    private void createTaskGroupView(ObservableList<ReadOnlyTask> taskList) {
        taskGroupView.getChildren().clear();
        int index = 0;
        for (ReadOnlyTask task : taskList) {
            TaskCard taskCard = new TaskCard(task, index + 1 + indexOffset);
            taskGroupView.getChildren().add(taskCard.getRoot());
            index++;
        }
    }

    private void setExpandingListener() {
        titledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue && newValue != oldValue) {
                EventsCenter.getInstance().post(new ShowTaskGroupEvent(getTitle()));
            }
        });
    }

    @Subscribe
    private void handleShowTaskGroupEvent(ShowTaskGroupEvent event) {
        if (!getTitle().equals(event.title) && titledPane.isExpanded()) {
            closeTitlePane();
        }
    }

}