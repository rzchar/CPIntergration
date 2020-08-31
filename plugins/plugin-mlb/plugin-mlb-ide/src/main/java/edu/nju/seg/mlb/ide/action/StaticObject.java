package edu.nju.seg.mlb.ide.action;

import edu.nju.seg.mlb.ide.MyServiceClient;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;

public class StaticObject {
  public static NotificationManager notificationManager;
  public static MyServiceClient serviceClient;
  public static EditorAgent editorAgent;
  public static AppContext appContext;
  public static ProjectExplorerPresenter projectExplorerPresenter;
}
