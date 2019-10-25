package edu.nju.ics.pgs.ide.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import edu.nju.ics.pgs.ide.action.ValuePool;

public class ApiList extends Composite {

  interface ItemBinder extends UiBinder<Widget, ApiList> {}

  private static final ApiList.ItemBinder UI_BINDER = GWT.create(ApiList.ItemBinder.class);

  @UiField ListBox listBox;

  @UiField FlowPanel descriptionPanel;

  @Inject
  public ApiList(String description) {
    initWidget(UI_BINDER.createAndBindUi(this));
    setDescription(description);
    listBox.getElement().setAttribute("style", "width:100%;display:block");
    listBox.addChangeHandler(
        new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent changeEvent) {
            int index = listBox.getSelectedIndex();
            ValuePool.resultPresenter.setIndex(index);
            ValuePool.resultPresenter.updateCompare();
            ValuePool.comparePresenter.updateStackOverflow();
          }
        });
  }

  private void setDescription(String description) {
    descriptionPanel.getElement().setInnerHTML(description);
  }

  public void addItem(String item) {
    listBox.addItem(item);
  }

  public int getSelectedIndex() {
    return listBox.getSelectedIndex();
  }

  public String getItemText(int index) {
    return listBox.getItemText(index);
  }

  public int getItemCount() {
    return listBox.getItemCount();
  }

  public ListBox getListBox() {
    return listBox;
  }
}
