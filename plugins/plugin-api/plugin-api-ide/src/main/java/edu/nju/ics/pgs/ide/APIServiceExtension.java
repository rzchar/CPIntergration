/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.nju.ics.pgs.ide;

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_HELP;
import static org.eclipse.che.ide.api.constraints.Anchor.AFTER;

import com.google.inject.Inject;
import edu.nju.ics.pgs.ide.action.CloseAction;
import edu.nju.ics.pgs.ide.action.MyAction;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.IdeActions;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;

/**
 * Server service extension that registers action which calls a service.
 *
 * @author Edgar Mueller
 */
@Extension(title = "Server Service Sample Extension", version = "0.0.1")
public class APIServiceExtension {

  /**
   * Constructor.
   *
   * @param actionManager the {@link ActionManager} that is used to register our actions
   * @param myAction the action that calls the example server service
   */
  private static final String SAMPLE_GROUP_MAIN_MENU = "Repair";

  @Inject
  public APIServiceExtension(
      ActionManager actionManager, MyAction myAction, CloseAction closeAction) {

    DefaultActionGroup mainMenuGroup =
        (DefaultActionGroup) actionManager.getAction(IdeActions.GROUP_MAIN_MENU);
    // create the server group
    DefaultActionGroup sampleGroup =
        new DefaultActionGroup(SAMPLE_GROUP_MAIN_MENU, true, actionManager);
    // register the server group
    actionManager.registerAction("sampleGroup", sampleGroup);
    // add to the position after the 'help'
    mainMenuGroup.add(sampleGroup, new Constraints(AFTER, GROUP_HELP));

    actionManager.registerAction("myAction", myAction);
    actionManager.registerAction("closeAction", closeAction);

    sampleGroup.add(myAction, Constraints.FIRST);
    sampleGroup.add(closeAction, new Constraints(AFTER, "myAction"));
  }
}
