/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.nju.ics.pgs.ide.action;

import static edu.nju.ics.pgs.ide.action.ValuePool.notificationManager;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.inject.Inject;
import edu.nju.ics.pgs.ide.MyServiceClient;
import edu.nju.ics.pgs.ide.part.ResultPresenter;
import edu.nju.ics.pgs.ide.view.ComparePresenter;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.BaseAction;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;

/**
 * Actions that triggers the server server service call.
 *
 * @author Edgar Mueller
 */
public class MyAction extends BaseAction {

  private String fileContent;

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
      final AppContext appContext,
      final EditorAgent editorAgent,
      final ResultPresenter resultPresenter,
      final ComparePresenter comparePresenter,
      final WorkspaceAgent workspaceAgent) {
    super("Analyze", "My Action Description");
    ValuePool.serviceClient = serviceClient;
    ValuePool.editorAgent = editorAgent;
    ValuePool.workspaceAgent = workspaceAgent;
    ValuePool.resultPresenter = resultPresenter;
    ValuePool.comparePresenter = comparePresenter;
    ValuePool.appContext = appContext;
    ValuePool.notificationManager = notificationManager;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ValuePool.comparePresenter.clearStackOverflow();
    ValuePool.comparePresenter.clearItem();
    ValuePool.resultPresenter.clearItem();
    ValuePool.workspaceAgent.openPart(ValuePool.resultPresenter, PartStackType.TOOLING);
    ValuePool.workspaceAgent.openPart(ValuePool.comparePresenter, PartStackType.INFORMATION);
    ValuePool.workspaceAgent.setActivePart(ValuePool.resultPresenter);
    ValuePool.workspaceAgent.setActivePart(ValuePool.comparePresenter);

    String contentURL =
        ValuePool.editorAgent.getActiveEditor().getEditorInput().getFile().getContentUrl();
    //        ValuePool.editorAgent.getActiveEditor().doSave();
    //        notificationManager.notify("content url: " + contentURL,
    // StatusNotification.Status.SUCCESS, StatusNotification.DisplayMode.EMERGE_MODE);
    String[] filenameArr = contentURL.split("/");
    String fileName = filenameArr[filenameArr.length - 1];
    if (fileName.endsWith(".java")) {
      ValuePool.fileType = "Java";
    } else if (fileName.endsWith(".py")) {
      ValuePool.fileType = "Python";
    } else {
      notificationManager.notify(
          "File type not supported",
          StatusNotification.Status.FAIL,
          StatusNotification.DisplayMode.EMERGE_MODE);
      return;
    }

    ValuePool.serviceClient
        .getFileContent(ValuePool.editorAgent)
        .then(
            new Operation<String>() {
              @Override
              public void apply(String response) throws OperationException {
                // This passes the response String to the notification manager.
                //                notificationManager.notify("response: " + response,
                // StatusNotification.Status.SUCCESS, StatusNotification.DisplayMode.FLOAT_MODE);
                fileContent = response;
                ValuePool.oldCode = fileContent.split("\n");
                //                ValuePool.currentCode = fileContent.split("\n");

                JSONObject requestJson = new JSONObject();
                requestJson.put("fileName", new JSONString(fileName));
                requestJson.put("fileContent", new JSONString(fileContent));

                ValuePool.serviceClient
                    .getResult(requestJson.toString())
                    .then(
                        new Operation<String>() {
                          @Override
                          public void apply(String response) throws OperationException {
                            //                      notificationManager.notify("response: " +
                            // response, StatusNotification.Status.SUCCESS,
                            // StatusNotification.DisplayMode.FLOAT_MODE);
                            // This passes the response String to the notification manager.
                            ValuePool.resultPresenter.clearItem();
                            ValuePool.resultPresenter.initialize(response, fileName);
                          }
                        })
                    .catchError(
                        new Operation<PromiseError>() {
                          @Override
                          public void apply(PromiseError error) throws OperationException {
                            notificationManager.notify(
                                "Get result failed",
                                StatusNotification.Status.FAIL,
                                StatusNotification.DisplayMode.FLOAT_MODE);
                          }
                        });
              }
            })
        .catchError(
            new Operation<PromiseError>() {
              @Override
              public void apply(PromiseError error) throws OperationException {
                notificationManager.notify(
                    "Get file content failed",
                    StatusNotification.Status.FAIL,
                    StatusNotification.DisplayMode.FLOAT_MODE);
              }
            });
  }
}
