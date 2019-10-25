/**
 * ***************************************************************************** Copyright (c)
 * 2012-2017 All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * *****************************************************************************
 */
package edu.nju.ics.pgs.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import edu.nju.ics.pgs.ide.MyResources;
import edu.nju.ics.pgs.ide.part.ResultView;
import edu.nju.ics.pgs.ide.part.ResultViewImpl;
import edu.nju.ics.pgs.ide.view.CompareView;
import edu.nju.ics.pgs.ide.view.CompareViewImpl;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.filetypes.FileType;

/** @author Vitalii Parfonov */
@ExtensionGinModule
public class SampleMenuGinModule extends AbstractGinModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    bind(ResultView.class).to(ResultViewImpl.class);
    bind(CompareView.class).to(CompareViewImpl.class);
  }

  @Provides
  @Singleton
  @Named("MyFileType")
  // will be registered as MyFileType
  protected FileType provideMyFile() {
    return new FileType(MyResources.INSTANCE.icon(), "my");
  }
}
