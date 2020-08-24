/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.nju.seg.mlb.ide.action;

import static edu.nju.seg.mlb.ide.action.StaticObject.notificationManager;

import com.google.inject.Inject;
import edu.nju.seg.mlb.ide.MyServiceClient;
import java.util.List;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.BaseAction;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.resources.tree.ContainerNode;

/**
 * Actions that triggers the sample server service call.
 *
 * @author Taeyang
 */
public class MyAction extends BaseAction {

  /**
   * Constructor.
   *
   * @param notificationManager the notification manager
   * @param serviceClient the client that is used to create requests
   */
  @Inject
  public MyAction(
      final NotificationManager notificationManager,
      final MyServiceClient serviceClient,
      final EditorAgent editorAgent,
      final AppContext appContext,
      final ProjectExplorerPresenter projectExplorerPresenter) {
    super("MLB Test Analysis", "Generate test cases and coverage using MLB");
    StaticObject.notificationManager = notificationManager;
    StaticObject.serviceClient = serviceClient;
    StaticObject.editorAgent = editorAgent;
    StaticObject.appContext = appContext;
    StaticObject.projectExplorerPresenter = projectExplorerPresenter;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // This calls the service in the workspace.
    // This method is in our org.eclipse.che.plugin.mlb.ide.MyServiceClient class
    // This is a Promise, so the .then() method is invoked after the response is made

    List<?> selection = StaticObject.projectExplorerPresenter.getSelection().getAllElements();
    StringBuilder dirUrl = new StringBuilder();

    if (selection.size() == 1 && selection.get(0) instanceof ContainerNode) {
      for (String seg : ((ContainerNode) selection.get(0)).getData().getLocation().segments())
        dirUrl.append(seg).append("——");
    } else {
      notificationManager.notify(
          "请在项目目录上右键", StatusNotification.Status.FAIL, StatusNotification.DisplayMode.FLOAT_MODE);
      return;
    }

    StaticObject.serviceClient
        .getHello(dirUrl.toString())
        .then(
            message -> {
              if (message.startsWith("Error:"))
                notificationManager.notify(
                    message.split(":")[1],
                    StatusNotification.Status.FAIL,
                    StatusNotification.DisplayMode.FLOAT_MODE);
              else
                notificationManager.notify(
                    message,
                    StatusNotification.Status.SUCCESS,
                    StatusNotification.DisplayMode.FLOAT_MODE);
            })
        .catchError(
            error -> {
              notificationManager.notify(
                  error.getCause().getMessage(),
                  StatusNotification.Status.FAIL,
                  StatusNotification.DisplayMode.FLOAT_MODE);
            });
  }
}
