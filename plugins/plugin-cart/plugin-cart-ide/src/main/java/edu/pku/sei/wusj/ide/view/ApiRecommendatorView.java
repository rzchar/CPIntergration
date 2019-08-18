package edu.pku.sei.wusj.ide.view;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;

public interface ApiRecommendatorView extends View<ApiRecommendatorView.ActionDelegate> {

  interface ActionDelegate extends BaseActionDelegate {}

  void sayHello(String content);

  void setVisible(boolean visible);
}
