package edu.nju.ics.pgs.ide.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import edu.nju.ics.pgs.ide.action.ValuePool;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.eclipse.che.ide.api.editor.text.TextRange;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;

public class CompareViewImpl extends BaseView<CompareView.ActionDelegate> implements CompareView {

  private String newCode;
  private int start;
  private String api;
  private int selectedLen;

  interface CompareViewImplUiBinder extends UiBinder<Widget, CompareViewImpl> {}

  @UiField FlowPanel mainPanel;

  @UiField ScrollPanel scrollPanel;

  @UiField Button confirmButton;

  @UiField FlowPanel fileInfo;

  @UiField FlowPanel so_mainPanel;

  @UiField FlowPanel so_Info;

  @Inject
  public CompareViewImpl(PartStackUIResources resources, CompareViewImplUiBinder uiBinder) {
    //        super(resources);
    setContentWidget(uiBinder.createAndBindUi(this));
    confirmButton.getElement().setInnerHTML("Confirm");
    this.newCode = "";
    selectedLen = 0;
  }

  @UiHandler("confirmButton")
  protected void onClicked(ClickEvent event) {

    EditorPartPresenter editor = ValuePool.editorAgent.getActiveEditor();
    //        editor.doSave();
    Document document = ((TextEditor) editor).getDocument();

    String contentURL = editor.getEditorInput().getFile().getContentUrl();

    ValuePool.serviceClient
        .getFileContent(ValuePool.editorAgent)
        .then(
            new Operation<String>() {
              @Override
              public void apply(String s) throws OperationException {
                Integer fileLen = s.length();

                document.replace(0, fileLen, newCode);
              }
            })
        .catchError(
            new Operation<PromiseError>() {
              @Override
              public void apply(PromiseError promiseError) throws OperationException {
                ValuePool.notificationManager.notify(
                    "Confirm failed",
                    StatusNotification.Status.FAIL,
                    StatusNotification.DisplayMode.EMERGE_MODE);
              }
            });

    //        document.setCursorPosition(new TextPosition(start+1, 1));
    document.setSelectedRange(
        new TextRange(new TextPosition(start, 0), new TextPosition(start, selectedLen)), true);
  }

  @Override
  public void addItem(String status, String code, String line) {
    CompareRowItem item = new CompareRowItem(status, code, line);
    if (status.equals("add")) {
      selectedLen = code.length();
    }
    mainPanel.add(item);
    if (newCode.equals("")) {
      newCode += code;
    } else {
      newCode += "\n" + code;
    }
  }

  @Override
  public void clearItem() {
    mainPanel.clear();
    this.fileInfo.getElement().setInnerHTML("");
    newCode = "";
  }

  @Override
  public void clearStackOverflow() {
    so_mainPanel.clear();
  }

  @Override
  public void setInfo(String api, int start) {
    this.start = start;
    this.api = api.split("\\(")[0];
    this.fileInfo
        .getElement()
        .setInnerHTML(this.api + " has been inserted into line " + (start + 1));
  }

  @Override
  public void setScrollPosition(int pos) {
    scrollPanel.setVerticalScrollPosition(pos);
  }

  @Override
  public void setHeight(int height) {
    mainPanel.getElement().setAttribute("style", "height:" + new Integer(height).toString() + "px");
  }

  @Override
  public void updateStackOverflow() {
    so_mainPanel.clear();
    so_Info
        .getElement()
        .setInnerHTML(
            "Results from <img style=\"height: 25px;\" src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Stack_Overflow_logo.svg/250px-Stack_Overflow_logo.svg.png\">");

    ValuePool.serviceClient
        .getStackOverFlowResult(this.api)
        .then(
            new Operation<String>() {
              @Override
              public void apply(String response) throws OperationException {

                //                ValuePool.notificationManager.notify("hhhh" + response,
                // StatusNotification.Status.SUCCESS, StatusNotification.DisplayMode.EMERGE_MODE);
                JSONObject responseJson = JSONParser.parseStrict(response).isObject();
                JSONArray result_arr = responseJson.get("result").isArray();
                so_mainPanel.clear();
                if (result_arr.size() == 0) {
                  FlowPanel no_result_panel = new FlowPanel();
                  no_result_panel
                      .getElement()
                      .setAttribute("style", "width: 96%;margin: 1%; padding: 1%;");
                  no_result_panel.getElement().setInnerHTML("Result not found");
                  so_mainPanel.add(no_result_panel);
                } else {
                  for (int i = 0; i < result_arr.size(); i++) {
                    JSONObject result = result_arr.get(i).isObject();
                    String title = result.get("title").isString().toString();
                    String url = result.get("url").isString().toString();
                    String excerpt = result.get("excerpt").isString().toString();
                    StackOverflowItem soi = new StackOverflowItem(title, url, excerpt);
                    so_mainPanel.add(soi);
                  }
                }
              }
            })
        .catchError(
            new Operation<PromiseError>() {
              @Override
              public void apply(PromiseError error) throws OperationException {
                ValuePool.notificationManager.notify(
                    "Get results from Stack Overflow failed",
                    StatusNotification.Status.FAIL,
                    StatusNotification.DisplayMode.FLOAT_MODE);
              }
            });
  }
}
