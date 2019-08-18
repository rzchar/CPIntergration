package edu.pku.sei.wusj.ide.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import edu.pku.sei.wusj.ide.view.client.jso.ApiRecommendatorViewOverlay;
import org.eclipse.che.ide.api.parts.base.BaseView;

public class ApiRecommendatorViewImpl extends BaseView<ApiRecommendatorView.ActionDelegate>
    implements ApiRecommendatorView {

  interface ApiRecommendatorViewImplUiBinder extends UiBinder<Widget, ApiRecommendatorViewImpl> {}

  private static final ApiRecommendatorViewImplUiBinder UI_BINDER =
      GWT.create(ApiRecommendatorViewImplUiBinder.class);

  @UiField FlowPanel apiRecommendatorPanel;

  @Inject
  public ApiRecommendatorViewImpl() {
    setContentWidget(UI_BINDER.createAndBindUi(this));
  }

  @Override
  public void sayHello(String content) {
    ApiRecommendatorViewOverlay.sayHello(apiRecommendatorPanel.getElement(), content);
    apiRecommendatorPanel.setVisible(true);
  }
}
