package edu.nju.ics.pgs.ide.action;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.nju.ics.pgs.ide.part.ResultPresenter;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.BaseAction;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;

@Singleton
public class CloseAction extends BaseAction {
  //    private final NotificationManager notificationManager;
  //    private WorkspaceAgent workspaceAgent;
  //    private ResultPresenter resultPresenter;

  @Inject
  public CloseAction(WorkspaceAgent workspaceAgent, ResultPresenter resultPresenter) {
    super("Close", "Close the plugin");
    //        ValuePool.workspaceAgent = workspaceAgent;
    //        ValuePool.resultPresenter = resultPresenter;
    //        this.notificationManager = notificationManager;
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    ValuePool.workspaceAgent.hidePart(ValuePool.resultPresenter);
    ValuePool.workspaceAgent.removePart(ValuePool.resultPresenter);
    ValuePool.workspaceAgent.hidePart(ValuePool.comparePresenter);
    ValuePool.workspaceAgent.removePart(ValuePool.comparePresenter);
  }

  // unenabled when there is no project
  @Override
  public void update(ActionEvent actionEvent) {
    actionEvent
        .getPresentation()
        .setEnabledAndVisible(ValuePool.appContext.getRootProject() != null);
  }
}
