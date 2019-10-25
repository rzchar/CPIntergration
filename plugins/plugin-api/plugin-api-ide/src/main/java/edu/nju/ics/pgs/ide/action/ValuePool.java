package edu.nju.ics.pgs.ide.action;

import edu.nju.ics.pgs.ide.MyServiceClient;
import edu.nju.ics.pgs.ide.part.ResultPresenter;
import edu.nju.ics.pgs.ide.view.ComparePresenter;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;

public class ValuePool {
  public static WorkspaceAgent workspaceAgent;
  public static ResultPresenter resultPresenter;
  public static ComparePresenter comparePresenter;
  public static AppContext appContext;
  public static NotificationManager notificationManager;
  public static EditorAgent editorAgent;
  public static MyServiceClient serviceClient;
  public static String fileType;
  public static String[] oldCode;
}
