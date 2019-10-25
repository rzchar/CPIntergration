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

public class SelectItem extends Composite {

  interface ItemBinder extends UiBinder<Widget, SelectItem> {}

  private static final SelectItem.ItemBinder UI_BINDER = GWT.create(SelectItem.ItemBinder.class);

  @UiField ListBox listBox;

  @UiField FlowPanel descriptionPanel;

  @Inject
  public SelectItem(String description) {
    initWidget(UI_BINDER.createAndBindUi(this));
    setDescription(description);
    listBox.getElement().setAttribute("style", "width:100%;display:block");
    listBox.addChangeHandler(
        new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent changeEvent) {
            ValuePool.resultPresenter.updateCompare();
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

  public void setSelectedIndex(int index) {
    listBox.setSelectedIndex(index);
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
