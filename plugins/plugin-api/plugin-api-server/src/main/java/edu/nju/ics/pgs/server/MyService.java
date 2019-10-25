/**
 * ***************************************************************************** Copyright (c)
 * 2012-2017 All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * *****************************************************************************
 */
package edu.nju.ics.pgs.server;

import edu.nju.ics.pgs.server.http.HttpRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Example server service that greets the user.
 *
 * @author Edgar Mueller
 */
@Path("analyze")
public class MyService {

  /**
   * Returns a greeting message.
   *
   * @param content the parameter
   * @return a greeting message
   */
  @POST
  public String getResult(String content) {
    return HttpRequest.sendPost(Value.serverUrl + "/analyze", content);
    //        return "Hello " + name + "!";
  }

  @GET
  @Path("{name}")
  public String sayHello(@PathParam("name") String name) {
    return "Hello " + name + "!";
  }
}
