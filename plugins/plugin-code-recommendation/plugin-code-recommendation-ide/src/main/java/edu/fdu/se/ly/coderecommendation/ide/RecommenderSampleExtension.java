/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.fdu.se.ly.coderecommendation.ide;

import com.google.inject.Inject;
import edu.fdu.se.ly.coderecommendation.ide.action.SampleAction1GetFileLinesOnRightClick;
import edu.tongji.sse.intelligentmanagementcenter.ide.manager.IntelligentPluginManager;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.IdeActions;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;

/**
 * Server service extension that registers action which calls a service.
 *
 * @author Edgar Mueller
 */
@Extension(title = "Server Service Sample Extension", version = "0.0.1")
public class RecommenderSampleExtension {

  private IntelligentPluginManager intelligentPluginManager;

  private WorkspaceAgent workspaceAgent;

  private ActionManager actionManager;

  @Inject
  public RecommenderSampleExtension(
      ActionManager actionManager,
      WorkspaceAgent workspaceAgent,
      IntelligentPluginManager intelligentPluginManager,
      SampleAction1GetFileLinesOnRightClick sampleAction1GetFileLinesOnRightClick) {

    this.intelligentPluginManager = intelligentPluginManager;
    this.actionManager = actionManager;
    this.workspaceAgent = workspaceAgent;

    this.prepareSampleAction1(sampleAction1GetFileLinesOnRightClick);
    intelligentPluginManager.registerPlugin(
        "Record Productivity", sampleAction1GetFileLinesOnRightClick);

    // mouseRightClickGroup.add(sampleAction2, Constraints.LAST);
  }

  private void prepareSampleAction1(
      SampleAction1GetFileLinesOnRightClick sampleAction1GetFileLinesOnRightClick) {
    actionManager.registerAction(
        "SampleAction1GetFileLinesOnRightClick", sampleAction1GetFileLinesOnRightClick);
    DefaultActionGroup mouseRightClickGroup =
        (DefaultActionGroup) actionManager.getAction(IdeActions.GROUP_EDITOR_CONTEXT_MENU);
    mouseRightClickGroup.add(sampleAction1GetFileLinesOnRightClick, Constraints.LAST);

    //    workspaceAgent.openPart(
    //        sampleAction1GetFileLinesOnRightClick.getBasePresenter(), PartStackType.INFORMATION);
  }
}
