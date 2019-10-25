package edu.nju.ics.pgs.ide.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CompareRowItem extends Composite {

  interface ItemBinder extends UiBinder<Widget, CompareRowItem> {}

  interface CompareRowStyle extends CssResource {
    String add();

    String normal();

    String addLineNumber();

    String normalLineNumber();
  }

  private static final ItemBinder UI_BINDER = GWT.create(ItemBinder.class);

  @UiField CompareRowStyle style;

  @UiField FlowPanel compareRowPanel;

  @UiField FlowPanel linenumber;

  @Inject
  public CompareRowItem(String status, String content, String line) {
    initWidget(UI_BINDER.createAndBindUi(this));

    if (status.equals("add")) {
      linenumber.getElement().addClassName(style.addLineNumber());
      linenumber.getElement().setInnerHTML(line);
      compareRowPanel.getElement().addClassName(style.add());
    } else if (status.equals("normal")) {
      linenumber.getElement().addClassName(style.normalLineNumber());
      linenumber.getElement().setInnerHTML(line);
      compareRowPanel.getElement().addClassName(style.normal());
    }
    if (content.equals("")) {
      content = "\n";
    }
    content = content.replace("&", "&amp;");
    content = content.replace(" ", "&nbsp;");
    content = content.replace("\"", "&quot;");
    content = content.replace("\'", "&apos;");
    content = content.replace("<", "&lt;");
    content = content.replace(">", "&gt;");
    content = content.replace("\n", "<br/>");
    content = content.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    this.compareRowPanel.getElement().setInnerHTML("<span>" + content + "</span>");
  }
}
