package edu.pku.sei.wusj.ide;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.vectomatic.dom.svg.ui.SVGResource;

public interface ApiRecommendatorResources extends ClientBundle {

  ApiRecommendatorResources INSTANCE = GWT.create(ApiRecommendatorResources.class);

  @Source("svg/icon.svg")
  SVGResource icon();
}
