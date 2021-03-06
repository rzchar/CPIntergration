/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.nju.ics.pgs.ide;

import javax.inject.Inject;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.StringUnmarshaller;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;

/**
 * Client for consuming the server server service.
 *
 * @author Edgar Mueller
 */
public class MyServiceClient {

  private AppContext appContext;
  private AsyncRequestFactory asyncRequestFactory;
  private LoaderFactory loaderFactory;

  /**
   * Constructor.
   *
   * @param appContext the {@link AppContext}
   * @param asyncRequestFactory the {@link AsyncRequestFactory} that is used to create requests
   * @param loaderFactory the {@link LoaderFactory} for displaying a message while waiting for a
   *     response
   */
  @Inject
  public MyServiceClient(
      final AppContext appContext,
      final AsyncRequestFactory asyncRequestFactory,
      final LoaderFactory loaderFactory) {

    this.appContext = appContext;
    this.asyncRequestFactory = asyncRequestFactory;
    this.loaderFactory = loaderFactory;
  }

  /**
   * Invoke the server server service.
   *
   * @param name a parameter
   * @return a Promise containing the server response
   */
  /**
   * Invoke the server server service.
   *
   * @param content a parameter
   * @return a Promise containing the server response
   */
  public Promise<String> getResult(String content) {
    //    ValuePool.notificationManager.notify(appContext.getWsAgentServerApiEndpoint() +
    // "/analyze/", StatusNotification.Status.SUCCESS, StatusNotification.DisplayMode.EMERGE_MODE);
    return asyncRequestFactory
        .createPostRequest(appContext.getWsAgentServerApiEndpoint() + "/analyze/", content)
        //                .loader(loaderFactory.newLoader("Analyzing..."))
        .send(new StringUnmarshaller());
  }

  //  public Promise<String> countLine(String name) {
  //    return asyncRequestFactory.createGetRequest(appContext.getMasterApiEndpoint() + "/count/" +
  // name)
  //            .loader(loaderFactory.newLoader("Waiting for hello..."))
  //            .send(new StringUnmarshaller());
  //
  //  }

  public Promise<String> getFileContent(EditorAgent editorAgent) {
    return asyncRequestFactory
        .createGetRequest(editorAgent.getActiveEditor().getEditorInput().getFile().getContentUrl())
        .loader(loaderFactory.newLoader("Waiting for content..."))
        .send(new StringUnmarshaller());
    //        return editorAgent.getActiveEditor().getEditorInput().getFile().getContent();
  }

  public Promise<String> getStackOverFlowResult(String request) {
    //        ValuePool.notificationManager.notify(request, StatusNotification.Status.SUCCESS,
    // StatusNotification.DisplayMode.EMERGE_MODE);
    return asyncRequestFactory
        .createPostRequest(appContext.getWsAgentServerApiEndpoint() + "/stackOverflow/", request)
        //                .loader(loaderFactory.newLoader("Waiting for Stack Overflow Results..."))
        .send(new StringUnmarshaller());
  }
}
