/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.ext.git.client.cldiff.selectablechangespanel;

import org.eclipse.che.ide.resource.Path;

/**
 * Delegates select action into {@link SelectableChangesPanelPresenter}.
 *
 * @author Igor Vinokur
 */
public interface SelectionCallBack {

  /**
   * Is called when item check-box changed selection state.
   *
   * @param path path of the selected item
   * @param isChecked checkbox selection state
   */
  void onSelectionChanged(Path path, boolean isChecked);
}
