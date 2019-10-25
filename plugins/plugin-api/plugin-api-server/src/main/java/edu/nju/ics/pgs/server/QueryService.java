package edu.nju.ics.pgs.server;

import edu.nju.ics.pgs.server.http.HttpRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("stackOverflow")
public class QueryService {

  @POST
  public String getStackOverflowResult(String api) {

    return HttpRequest.sendPost(Value.serverUrl + "/stackoverflow", api);
  }
}
