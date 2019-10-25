package edu.nju.ics.pgs.ide.view;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.nju.ics.pgs.ide.MyResources;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.vectomatic.dom.svg.ui.SVGResource;

@Singleton
public class ComparePresenter extends BasePresenter implements CompareView.ActionDelegate {
  private final CompareView compareView;

  @Inject
  public ComparePresenter(final CompareView compareView) {
    this.compareView = compareView;
  }

  @Override
  public String getTitle() {
    return "Compare";
  }

  @Override
  public SVGResource getTitleImage() {
    return (MyResources.INSTANCE.icon());
  }

  @Override
  public View getView() {
    return compareView;
  }

  @Override
  public String getTitleToolTip() {
    return "Compare Tool Tip";
  }

  @Override
  public void go(AcceptsOneWidget container) {
    container.setWidget(compareView);
  }

  public void addItem(String status, String code, String line) {
    this.compareView.addItem(status, code, line);
  }

  public void clearItem() {
    this.compareView.clearItem();
  }

  public void setInfo(String api, int start) {
    this.compareView.setInfo(api, start);
  }

  public void setScrollPosition(int pos) {
    this.compareView.setScrollPosition(pos);
  }

  public void setHeight(int height) {
    this.compareView.setHeight(height);
  }

  public void updateStackOverflow() {
    this.compareView.updateStackOverflow();
  }

  public void clearStackOverflow() {
    this.compareView.clearStackOverflow();
  }
}
