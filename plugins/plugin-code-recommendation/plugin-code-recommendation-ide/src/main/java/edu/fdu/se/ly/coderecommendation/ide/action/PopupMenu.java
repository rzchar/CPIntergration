/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.fdu.se.ly.coderecommendation.ide.action;

import static elemental.css.CSSStyleDeclaration.Unit.PX;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;
import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.html.SpanElement;
import elemental.html.Window;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.editor.orion.client.OrionEditorWidget;
import org.eclipse.che.ide.editor.orion.client.jso.OrionEditorOverlay;
import org.eclipse.che.ide.editor.orion.client.jso.OrionKeyModeOverlay;
import org.eclipse.che.ide.editor.orion.client.jso.OrionPixelPositionOverlay;
import org.eclipse.che.ide.editor.orion.client.jso.OrionTextViewOverlay;
import org.eclipse.che.ide.ui.popup.PopupResources;
import org.eclipse.che.ide.util.dom.Elements;

public class PopupMenu implements EventListener {

  private final PopupResources popupResources;
  private final Element popupElement;
  private final Element docPopupElement;
  private final Element docpopupBodyElement;
  private final Element popupBodyElement;
  /** The list (ul) element for the popup. */
  private final Element listElement;

  private final NotificationManager notificationManager;
  private static final String CUSTOM_EVT_TYPE_VALIDATE = "itemvalidate";
  private static final String DOCUMENTATION = "documentation";

  private final OrionEditorWidget textEditor;
  private final TextEditor textEditor2;
  private final EventListener popupListener;
  private OrionKeyModeOverlay assistMode;
  // private final ModuleHolder moduleHolder;
  private OrionEditorOverlay editorOverlay;

  private boolean visible = false;
  private boolean focused = false;

  public PopupMenu(
      final PopupResources popupResources,
      final OrionEditorWidget textEditor,
      final NotificationManager notificationManager,
      final TextEditor textEditor2) {
    this.popupResources = popupResources;
    this.textEditor = textEditor;
    this.notificationManager = notificationManager;
    this.textEditor2 = textEditor2;

    popupElement = Elements.createDivElement(popupResources.popupStyle().popup());
    Element headerElement = Elements.createDivElement(popupResources.popupStyle().header());
    headerElement.setInnerText("Top-10 API recommendations:");
    popupElement.appendChild(headerElement);

    docpopupBodyElement = Elements.createDivElement(popupResources.popupStyle().body());

    popupBodyElement = Elements.createDivElement(popupResources.popupStyle().body());
    docPopupElement = Elements.createDivElement(popupResources.popupStyle().popup());
    popupElement.appendChild(popupBodyElement);
    docPopupElement.appendChild(docpopupBodyElement);
    listElement = Elements.createUListElement();
    popupBodyElement.appendChild(listElement);

    popupListener =
        evt -> {
          if (!(evt instanceof MouseEvent)) {
            return;
          }
          final MouseEvent mouseEvent = (MouseEvent) evt;
          final EventTarget target = mouseEvent.getTarget();
          if (target instanceof Element) {
            final Element elementTarget = (Element) target;

            if (!PopupMenu.this.popupElement.contains(elementTarget)) {
              hide();
              evt.preventDefault();
            }
          }
        };
  }

  protected Widget createTitleWidget(String text) {
    Panel titlePanel = new FlowPanel();
    Label textLabel = new Label(text);
    titlePanel.add(textLabel);
    return titlePanel;
  }

  public String makelinefeed(String s) {
    // 用空格作为分隔符，将单词存到字符数组里面
    String[] str = s.split(" ");
    // 利用StringBuffer对字符串进行修改
    StringBuffer buffer = new StringBuffer();
    // 判断单词长度，计算
    int len = 0;
    for (int i = 0; i < str.length; i++) {
      len += str[i].length();
      if (len > 50) {
        buffer.append("<br />" + str[i] + " "); // 利用StringBuffer对字符串进行修改
        len = str[i].length() + 1; // +1为换行后读出空格一位
      } else {
        buffer.append(str[i] + " ");
        len++;
      }
    }
    return buffer.toString();
  }

  public Element createElement(
      int index, String api, String importString, String tip, int importLine, String existImport) {
    final String tip2 = makelinefeed(tip);
    final Element element = Elements.createLiElement(popupResources.popupStyle().item());
    element.setId(Integer.toString(index));

    final Element icon = Elements.createDivElement(popupResources.popupStyle().icon());
    element.appendChild(icon);

    final SpanElement label = Elements.createSpanElement(popupResources.popupStyle().label());
    label.setInnerHTML(api);
    element.appendChild(label);

    final EventListener validateListener =
        evt -> applyAPI(api, importString, importLine, existImport);
    element.setTabIndex(1);
    element.addEventListener(Event.DBLCLICK, validateListener, false);
    element.addEventListener(CUSTOM_EVT_TYPE_VALIDATE, validateListener, false);

    element.addEventListener(
        Event.MOUSEDOWN,
        new EventListener() {
          @Override
          public void handleEvent(Event event) {
            if (tip != null) {
              docPopupElement.getStyle().setOpacity(1);
              docpopupBodyElement.setInnerHTML(tip2);
              //      notificationManager.notify(
              // "Tips:",
              // importString + "//" + importLine + "//" + existImport,
              // StatusNotification.Status.SUCCESS,
              // StatusNotification.DisplayMode.FLOAT_MODE);
            }
          }
        },
        false);

    return element;
  }

  public void applyAPI(String api, String importString, int importLine, String existImport) {
    Document document = textEditor2.getDocument();
    int importOffset = 0;
    if (importLine == 0) {
      importLine = 1;
    }
    importOffset = document.getLineStart(importLine);
    int offset = textEditor2.getCursorOffset();
    document.replace(offset, 0, api);
    textEditor2.getCursorModel().setCursorPosition(offset + api.length());
    if (importString.length() > 1) {
      String[] sourceImport = importString.split(",");
      for (int i = 0; i < sourceImport.length; i++) {
        sourceImport[i] = sourceImport[i].trim();
        if (!existImport.contains(sourceImport[i])) {
          String newImport = "import " + sourceImport[i] + ";\r\n";
          document.replace(importOffset, 0, newImport);
        }
      }
    }
    hide();
  }

  public void show(
      String[] apis, String[] imports, String[] tips, int importLine, String existImport) {
    if (existImport == null || existImport.equals("0")) {
      existImport = "no import";
    }
    OrionTextViewOverlay textView = textEditor.getTextView();
    OrionPixelPositionOverlay caretLocation =
        textView.getLocationAtOffset(textView.getCaretOffset());
    caretLocation.setY(caretLocation.getY() + textView.getLineHeight());
    caretLocation = textView.convert(caretLocation, "document", "page");

    /** The fastest way to remove element children. Clear and add items. */
    listElement.setInnerHTML("");

    /* Reset popup dimensions and show. */
    popupElement.getStyle().setLeft(caretLocation.getX(), PX);
    popupElement.getStyle().setTop(caretLocation.getY(), PX);
    popupElement.getStyle().setWidth("400px");
    popupElement.getStyle().setHeight("200px");
    popupElement.getStyle().setOpacity(1);
    docPopupElement.getStyle().setLeft(caretLocation.getX() + 403, PX);
    docPopupElement.getStyle().setTop(caretLocation.getY(), PX);
    docPopupElement.getStyle().setWidth("370px");
    docPopupElement.getStyle().setHeight("100px");
    docpopupBodyElement.getStyle().setWidth("370px");
    docpopupBodyElement.getStyle().setHeight("90px");
    docpopupBodyElement.getStyle().setDisplay("block");
    Elements.getDocument().getBody().appendChild(this.popupElement);
    Elements.getDocument().getBody().appendChild(this.docPopupElement);

    /* Add the popup items. */
    for (int i = 0; i < apis.length; i++) {
      listElement.appendChild(
          createElement(i, apis[i], imports[i], tips[i], importLine, existImport));
    }

    /* Correct popup position (wants to be refactored) */
    final Window window = Elements.getWindow();
    final int viewportWidth = window.getInnerWidth();
    final int viewportHeight = window.getInnerHeight();

    int spaceBelow = viewportHeight - caretLocation.getY();
    if (this.popupElement.getOffsetHeight() > spaceBelow) {
      // Check if div is too large to fit above
      int spaceAbove = caretLocation.getY() - textView.getLineHeight();
      if (this.popupElement.getOffsetHeight() > spaceAbove) {
        // Squeeze the div into the larger area
        if (spaceBelow > spaceAbove) {
          this.popupElement.getStyle().setProperty("maxHeight", spaceBelow + "px");
        } else {
          this.popupElement.getStyle().setProperty("maxHeight", spaceAbove + "px");
          this.popupElement.getStyle().setTop("0");
        }
      } else {
        // Put the div above the line
        this.popupElement
            .getStyle()
            .setTop(
                (caretLocation.getY()
                        - this.popupElement.getOffsetHeight()
                        - textView.getLineHeight())
                    + "px");
        this.popupElement.getStyle().setProperty("maxHeight", spaceAbove + "px");
      }
    } else {
      this.popupElement.getStyle().setProperty("maxHeight", spaceBelow + "px");
    }

    if (caretLocation.getX() + this.popupElement.getOffsetWidth() > viewportWidth) {
      int leftSide = viewportWidth - this.popupElement.getOffsetWidth();
      if (leftSide < 0) {
        leftSide = 0;
      }
      this.popupElement.getStyle().setLeft(leftSide + "px");
      this.popupElement.getStyle().setProperty("maxWidth", (viewportWidth - leftSide) + "px");
    } else {
      this.popupElement
          .getStyle()
          .setProperty("maxWidth", viewportWidth + caretLocation.getX() + "px");
    }

    /* Don't attach handlers twice. Visible popup must already have their attached. */
    if (!visible) {
      addPopupEventListeners();
    }

    /* Indicates the codeassist is visible. */
    visible = true;
    focused = false;
  }

  /** Hides the popup and displaying javadoc. */
  public void hide() {
    textEditor.setFocus();
    popupElement.getStyle().setOpacity(0);
    docPopupElement.getStyle().setOpacity(0);
    new Timer() {
      @Override
      public void run() {
        // detach assist popup
        if (popupElement.getParentNode() != null) {
          popupElement.getParentNode().removeChild(popupElement);
        }
        if (docPopupElement.getParentNode() != null) {
          docPopupElement.getParentNode().removeChild(docPopupElement);
        }
        docPopupElement.setInnerHTML("");
        // remove all items from popup element
        listElement.setInnerHTML("");
      }
    }.schedule(250);

    visible = false;
    // selectedElement = null;
    // showDocTimer.cancel();

    removePopupEventListeners();
  }

  @Override
  public void handleEvent(Event evt) {
    if (Event.KEYDOWN.equalsIgnoreCase(evt.getType())) {
      final KeyboardEvent keyEvent = (KeyboardEvent) evt;
      switch (keyEvent.getKeyCode()) {
        case KeyCodes.KEY_ESCAPE:
          Scheduler.get().scheduleDeferred(this::hide);
          break;

        case KeyCodes.KEY_DOWN:
          // selectNext();
          // evt.preventDefault();
          break;

        case KeyCodes.KEY_UP:
          // selectPrevious();
          // evt.preventDefault();
          break;
        case KeyCodes.KEY_ENTER:
          // evt.preventDefault();
          // evt.stopImmediatePropagation();
          // validateItem(true);
          break;
      }
    } else if (Event.SCROLL.equalsIgnoreCase(evt.getType())) {
      // updateIfNecessary();
    } else if (Event.FOCUS.equalsIgnoreCase(evt.getType())) {
      focused = true;
    }
  }

  private void addPopupEventListeners() {
    Elements.getDocument().addEventListener(Event.MOUSEDOWN, this.popupListener, false);
    listElement.addEventListener(Event.KEYDOWN, this, false);
    popupBodyElement.addEventListener(Event.SCROLL, this, false);
  }

  private void removePopupEventListeners() {
    /* Remove popup listeners. */
    // remove the keyboard listener
    listElement.removeEventListener(Event.KEYDOWN, this, false);

    // remove the scroll listener
    popupBodyElement.removeEventListener(Event.SCROLL, this, false);

    // remove the mouse listener
    Elements.getDocument().removeEventListener(Event.MOUSEDOWN, this.popupListener);
  }
}
