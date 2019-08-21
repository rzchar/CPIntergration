/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.fdu.se.ly.coderecommendation.ide.action;

import com.google.inject.Inject;
import edu.tongji.sse.intelligentmanagementcenter.ide.action.AbstractIntelligentPluginAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.*;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.editor.orion.client.OrionEditorWidget;
import org.eclipse.che.ide.editor.quickfix.QuickAssistWidgetFactory;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.StringMapUnmarshaller;
import org.eclipse.che.ide.ui.popup.PopupResources;
import org.eclipse.che.ide.ui.window.Window;

public class SampleAction1GetFileLinesOnRightClick extends AbstractIntelligentPluginAction {

  private final NotificationManager notificationManager;
  private final StringMapUnmarshaller unmarshaller;
  private final AsyncRequestFactory asyncRequestFactory;
  private final EditorAgent editorAgent;
  private final AppContext appContext;
  private final PopupResources popupResources;
  @Inject private QuickAssistWidgetFactory widgetFactory;

  @Inject
  public SampleAction1GetFileLinesOnRightClick(
      final NotificationManager notificationManager,
      final EditorAgent editorAgent,
      final AppContext appContext,
      final AsyncRequestFactory asyncRequestFactory,
      final PopupResources popupResources) {
    // super("Sample Action to recommend code", "Code Recommendation");
    super("CodeWisdom-aiAssistant", "Code Recommendation");
    this.notificationManager = notificationManager;
    this.editorAgent = editorAgent;
    this.appContext = appContext;
    this.unmarshaller = new StringMapUnmarshaller();
    this.asyncRequestFactory = asyncRequestFactory;
    this.popupResources = popupResources;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    EditorPartPresenter editor = editorAgent.getActiveEditor();
    TextEditor textEditor2 = (TextEditor) editor;
    OrionEditorWidget orionEditorWidget = (OrionEditorWidget) textEditor2.getEditorWidget();
    Document document = textEditor2.getDocument();
    String code = document.getContents();
    int offset = textEditor2.getCursorOffset();
    StringBuffer codeBuffer = new StringBuffer(code);
    codeBuffer.insert(offset, "$hole$");
    code = codeBuffer.toString();

    String url =
        appContext.getWsAgentServerApiEndpoint()
            + "/getFileLines/"
            + appContext.getWorkspaceId()
            + "/server/"
            + str2HexStr(code);
    asyncRequestFactory
        .createGetRequest(url)
        .send(this.unmarshaller)
        .then(
            resultMap -> {
              PopupMenu popupMenu =
                  new PopupMenu(
                      popupResources, orionEditorWidget, notificationManager, textEditor2);

              String apiString = resultMap.get("api");
              String importString = resultMap.get("import");
              String tipString = resultMap.get("tip");
              String lineString = resultMap.get("line");
              String existImportString = resultMap.get("existImport");
              String[] apiArray = apiString.split("chenchifengefu");
              String[] importArray = importString.split("chenchifengefu");
              String[] tipArray = tipString.split("chenchifengefu");
              String[] lineArray = lineString.split("chenchifengefu");
              String[] existImport = existImportString.split("chenchifengefu");
              String apis = "";
              for (int i = 0; i < apiArray.length; i++) {
                apis += apiArray[i] + "\r\n";
              }
              popupMenu.show(
                  apiArray, importArray, tipArray, Integer.parseInt(lineArray[0]), existImport[0]);
              // notificationManager.notify(
              //     "success",
              //     apis,
              //     StatusNotification.Status.SUCCESS,
              //     StatusNotification.DisplayMode.FLOAT_MODE);
            })
        .catchError(
            error -> {
              // notificationManager.notify(
              //     "err",
              //     error.getMessage(),
              //     StatusNotification.Status.SUCCESS,
              //     StatusNotification.DisplayMode.FLOAT_MODE);
            });
  }

  @Override
  public boolean isEnable() {
    return false;
  }

  @Override
  public void setEnable(boolean enable) {}

  @Override
  public PartPresenter getResultPresenter() {
    return null;
  }

  @Override
  public Window getConfigWindow() {
    return null;
  }

  public String str2HexStr(String str) {
    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder("");
    byte[] bs = str.getBytes();
    int bit;
    for (int i = 0; i < bs.length; i++) {
      bit = (bs[i] & 0x0f0) >> 4;
      sb.append(chars[bit]);
      bit = bs[i] & 0x0f;
      sb.append(chars[bit]);
    }
    return sb.toString();
  }
}
