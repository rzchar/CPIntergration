package edu.nju.ics.pgs.ide.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class StackOverflowItem extends Composite {

  interface ItemBinder extends UiBinder<Widget, StackOverflowItem> {}

  private static final StackOverflowItem.ItemBinder UI_BINDER = GWT.create(ItemBinder.class);

  @UiField Anchor titlePanel;

  @UiField FlowPanel excerptPanel;

  @Inject
  public StackOverflowItem(String title, String url, String excerpt) {
    initWidget(UI_BINDER.createAndBindUi(this));
    title = removeQuotation(title);
    url = removeQuotation(url);
    excerpt = removeQuotation(excerpt);
    title = convertStr(title);
    excerpt = convertStr(excerpt);
    titlePanel.getElement().setInnerHTML(title);
    titlePanel.getElement().setAttribute("href", url);
    titlePanel.getElement().setAttribute("target", "view_window");

    excerptPanel.getElement().setInnerHTML(excerpt);
  }

  private String convertStr(String input) {
    input = input.replace("\\n", "");
    input = input.replace("\\\"", "\"");
    return input;
  }

  private String removeQuotation(String input) {
    input = input.substring(1, input.length() - 1);
    return input;
  }
}
