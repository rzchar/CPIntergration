package edu.pku.sei.wusj.ide.action;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pku.sei.wusj.ide.view.ApiRecommendatorViewPresenter;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.BaseAction;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;

/** Action for opening a part which embeds javascript code. */
@Singleton
public class ApiRecommendatorAction extends BaseAction {

  public static final String ACTION_ID = "APIRecommendationFromJSAction";

  private WorkspaceAgent workspaceAgent;
  private ApiRecommendatorViewPresenter apiRecommendatorViewPresenter;

  @Inject
  public ApiRecommendatorAction(
      WorkspaceAgent workspaceAgent, ApiRecommendatorViewPresenter apiRecommendatorViewPresenter) {
    super("Show Api Recommendation");
    this.workspaceAgent = workspaceAgent;
    this.apiRecommendatorViewPresenter = apiRecommendatorViewPresenter;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    workspaceAgent.openPart(apiRecommendatorViewPresenter, PartStackType.INFORMATION);
    workspaceAgent.setActivePart(apiRecommendatorViewPresenter);
    apiRecommendatorViewPresenter.sayHello("Loading...");
    apiRecommendatorViewPresenter.show();
  }
}
