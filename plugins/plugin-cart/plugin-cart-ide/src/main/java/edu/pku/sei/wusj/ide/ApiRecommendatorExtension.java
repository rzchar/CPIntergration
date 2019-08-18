package edu.pku.sei.wusj.ide;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pku.sei.wusj.ide.action.ApiRecommendatorAction;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.IdeActions;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;

@Extension(title = "show api recommendation")
@Singleton
public class ApiRecommendatorExtension {

  @Inject
  private void configureActions(
      final ActionManager actionManager, final ApiRecommendatorAction apiRecommendatorAction) {

    DefaultActionGroup editorContextMenuGroup =
        (DefaultActionGroup) actionManager.getAction(IdeActions.GROUP_EDITOR_CONTEXT_MENU);

    actionManager.registerAction(apiRecommendatorAction.ACTION_ID, apiRecommendatorAction);
    editorContextMenuGroup.addAction(apiRecommendatorAction, Constraints.LAST);
  }
}
