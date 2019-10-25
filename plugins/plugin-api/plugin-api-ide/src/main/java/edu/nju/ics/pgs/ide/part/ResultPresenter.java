package edu.nju.ics.pgs.ide.part;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.nju.ics.pgs.ide.MyResources;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.vectomatic.dom.svg.ui.SVGResource;

@Singleton
public class ResultPresenter extends BasePresenter implements ResultView.ActionDelegate {
  private final ResultView resultView;

  @Inject
  public ResultPresenter(final ResultView resultView) {
    this.resultView = resultView;
  }

  @Override
  public String getTitle() {
    return "Result";
  }

  @Override
  public SVGResource getTitleImage() {
    return (MyResources.INSTANCE.icon());
  }

  @Override
  public View getView() {
    return resultView;
  }

  @Override
  public String getTitleToolTip() {
    return "Result Tool Tip";
  }

  @Override
  public void go(AcceptsOneWidget container) {
    container.setWidget(resultView);
  }

  public void addItem(int index, int start, double prob) {
    this.resultView.addItem(index, start, prob);
  }

  public void setInfo(String filename) {
    this.resultView.setInfo(filename);
  }

  public void setApi(String api) {
    this.resultView.setApi(api);
  }

  public void clearItem() {
    this.resultView.clearItem();
  }

  public void initialize(String response, String fileName) {
    this.resultView.initialize(response, fileName);
  }

  public String genNewCode() {
    return this.resultView.genNewCode();
  }

  public void setIndex(int index) {
    this.resultView.setIndex(index);
  }

  public void updateCompare() {
    this.resultView.updateCompare();
  }

  public void setStart(int start) {
    this.resultView.setStart(start);
  }
}
