/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.nju.seg.mlb.ide;

import com.google.inject.Inject;
import edu.nju.seg.mlb.ide.action.MyAction;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.IdeActions;
import org.eclipse.che.ide.api.extension.Extension;

/**
 * Server service extension that registers action which calls a service.
 *
 * @author Edgar Mueller
 */
@Extension(title = "MLB Server Service Extension", version = "0.0.1")
public class MLBServiceExtension {

  /**
   * Constructor.
   *
   * @param actionManager the {@link ActionManager} that is used to register our actions
   * @param myAction the action that calls the example server service
   */
  @Inject
  public MLBServiceExtension(ActionManager actionManager, MyAction myAction) {

    actionManager.registerAction("myAction", myAction);

    DefaultActionGroup mainContextMenuGroup =
        (DefaultActionGroup) actionManager.getAction(IdeActions.GROUP_MAIN_CONTEXT_MENU);
    mainContextMenuGroup.add(myAction);
  }
}
