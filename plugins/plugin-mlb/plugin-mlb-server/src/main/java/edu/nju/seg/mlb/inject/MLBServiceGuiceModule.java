/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.nju.seg.mlb.inject;

import com.google.inject.AbstractModule;
import edu.nju.seg.mlb.MyService;
import org.eclipse.che.inject.DynaModule;

/** Server service example Guice module for setting up a simple service. */
@DynaModule
public class MLBServiceGuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(MyService.class);
  }
}
