package edu.nju.ics.pgs.ide.part;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;

public interface ResultView extends View<ResultView.ActionDelegate> {

  interface ActionDelegate extends BaseActionDelegate {}

  void setStart(int start);

  void addItem(int index, int start, double prob);

  void setInfo(String filename);

  void setApi(String api);

  void clearItem();

  void initialize(String response, String fileName);

  String genNewCode();

  void setIndex(int index);

  void updateCompare();
}
