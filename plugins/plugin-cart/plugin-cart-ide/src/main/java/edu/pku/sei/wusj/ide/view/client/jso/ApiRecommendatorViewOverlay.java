package edu.pku.sei.wusj.ide.view.client.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/** JavaScript overlay to demonstrate a global js function call */
public class ApiRecommendatorViewOverlay extends JavaScriptObject {

  protected ApiRecommendatorViewOverlay() {}

  public static final native void sayHello(final Element element, String message) /*-{
        new $wnd.ApiRecommendator(element, message);
    }-*/;
}
