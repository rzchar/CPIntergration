package edu.nju.ics.pgs.ide.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import edu.nju.ics.pgs.ide.action.ValuePool;

public class ResultItem extends Composite {

  interface ItemBinder extends UiBinder<Widget, ResultItem> {}

  private static final ItemBinder UI_BINDER = GWT.create(ItemBinder.class);

  private int start;
  private double prob;
  private String api;
  private int index;
  private String fileName;

  @UiField FocusPanel resultItemPanel;

  @UiHandler("resultItemPanel")
  protected void onclicked(ClickEvent event) {

    ValuePool.resultPresenter.setStart(start);
    ValuePool.resultPresenter.updateCompare();
    //        ValuePool.comparePresenter.setHeight((addPos+10)*18);
  }

  @Inject
  public ResultItem(int index, int start, double prob, String fileName) {
    initWidget(UI_BINDER.createAndBindUi(this));
    this.index = index;
    this.start = start;
    this.prob = prob;
    this.fileName = fileName;
    if (index > 1) {
      this.setContentText("Line: " + start);
    } else {
      this.setContentText("Line: " + start + " (recommended)");
    }

    String green = new Integer((new Double(prob * 100 + 50)).intValue()).toString();
    String rgb = "rgb(50," + green + ",50)";
    resultItemPanel.getElement().setAttribute("style", "background-color:" + rgb);
  }

  private void setContentText(String content) {

    content = content.replace("&", "&amp;");
    content = content.replace(" ", "&nbsp;");
    content = content.replace("\"", "&quot;");
    content = content.replace("\'", "&apos;");
    content = content.replace("<", "&lt;");
    content = content.replace(">", "&gt;");
    content = content.replace("\n", "<br/>");
    content = content.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    this.resultItemPanel.getElement().setInnerHTML("<span>" + content + "</span>");
  }

  public String getContentText() {
    return this.resultItemPanel.getElement().getInnerText();
  }
}
