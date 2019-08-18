package edu.pku.sei.wusj.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import edu.pku.sei.wusj.ide.view.ApiRecommendatorView;
import edu.pku.sei.wusj.ide.view.ApiRecommendatorViewImpl;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;

@ExtensionGinModule
public class ApiRecommendatorViewExampleGinModule extends AbstractGinModule {

  @Override
  protected void configure() {
    bind(ApiRecommendatorView.class).to(ApiRecommendatorViewImpl.class);
  }
}
