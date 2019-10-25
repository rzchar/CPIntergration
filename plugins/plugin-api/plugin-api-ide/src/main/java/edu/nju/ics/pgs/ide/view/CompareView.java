package edu.nju.ics.pgs.ide.view;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;

public interface CompareView extends View<CompareView.ActionDelegate> {

  interface ActionDelegate extends BaseActionDelegate {}

  void addItem(String status, String code, String line);

  void clearItem();

  void setInfo(String api, int start);

  void setScrollPosition(int pos);

  void setHeight(int height);

  void updateStackOverflow();

  void clearStackOverflow();
}
