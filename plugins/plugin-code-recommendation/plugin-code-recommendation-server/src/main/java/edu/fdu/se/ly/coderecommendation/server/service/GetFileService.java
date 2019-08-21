package edu.fdu.se.ly.coderecommendation.server.service;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import edu.fdu.se.ly.coderecommendation.server.astbuilder.AstTreeAnalyzer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.fs.server.FsManager;
import org.eclipse.che.api.fs.server.WsPathUtils;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("getFileLines/{ws-id}/")
public class GetFileService {
  private FsManager fsManager;

  @Inject
  public GetFileService(FsManager fsManager) {
    this.fsManager = fsManager;
  }

  private int countLines(String wsPath)
      throws ServerException, NotFoundException, ConflictException {
    String content = fsManager.readAsString(wsPath);
    String[] lines = content.split("\r\n|\r|\n");
    return lines.length;
  }

  private void getFileLinesRecursivly(String path, Map<String, String> linesPerFile)
      throws ServerException, NotFoundException, ConflictException {
    Set<String> fileWsPaths = fsManager.getFileWsPaths(path);
    for (String fileWsPath : fileWsPaths) {
      linesPerFile.put(fileWsPath, Integer.toString(countLines(fileWsPath)));
    }
    Set<String> dirWsPaths = fsManager.getDirWsPaths(path);
    for (String dirWsPath : dirWsPaths) {
      getFileLinesRecursivly(dirWsPath, linesPerFile);
    }
  }

  @GET
  @Path("{projectPath}")
  public Map<String, String> countLinesPerFile(@PathParam("projectPath") String projectPath)
      throws ServerException, NotFoundException, ConflictException {
    String projectWsPath = WsPathUtils.absolutize(projectPath);
    Map<String, String> linesPerFile = new LinkedHashMap<>();
    getFileLinesRecursivly(projectWsPath, linesPerFile);
    return linesPerFile;
  }

  @GET
  @Path("ast/{project}")
  @Produces(APPLICATION_JSON)
  public Map<String, String> getAstForProject(@PathParam("project") String projectPath) {
    Map<String, String> result = new HashMap<>();
    try {
      String projectWsPath = WsPathUtils.absolutize(projectPath);
      long serverStartTime = (new Date()).getTime();
      Map<String, JSONObject> astPerFile = new HashMap<>();
      this.getFileASTRecursivly(projectWsPath, astPerFile);
      JSONArray resultArray = new JSONArray();
      long serverEndTime = (new Date()).getTime();
      long totalExpression = 0;
      long totalMethod = 0;
      long fileNum = 0;
      for (String fileName : astPerFile.keySet()) {
        fileNum += 1;
        JSONObject jo = astPerFile.get(fileName);
        jo.put("fileName", fileName);
        totalMethod += jo.optInt("methodNum", 0);
        totalExpression += jo.optInt("expressionNum", 0);
        resultArray.put(jo);
      }
      fsManager.createFile(
          projectWsPath + "/astRecord/" + "rec" + serverStartTime + ".json",
          resultArray.toString());
      result.put("fileAmount", String.valueOf(fileNum));
      result.put("methodAmount", String.valueOf(totalMethod));
      result.put("expressionAmount", String.valueOf(totalExpression));
      result.put("serverAnalyzerCost", String.valueOf(serverEndTime - serverStartTime));
    } catch (Exception e) {
      StringBuilder errBuilder = new StringBuilder();
      errBuilder.append("\n");
      for (StackTraceElement s : e.getStackTrace()) {
        errBuilder.append(s.toString() + "\n");
      }
      result.put("errorMessage", e.getMessage());
      result.put("stacktrace", errBuilder.toString());
    }

    return result;
  }

  private boolean isFileWanted(String filePath) {
    if (filePath.endsWith(".java")) {
      return true;
    }
    return false;
  }

  private void getFileASTRecursivly(String path, Map<String, JSONObject> astPerFile)
      throws ConflictException, NotFoundException, ServerException {
    Set<String> fileWsPaths = fsManager.getFileWsPaths(path);
    for (String fileWsPath : fileWsPaths) {
      if (isFileWanted(fileWsPath)) {
        astPerFile.put(fileWsPath, getASTResult(fileWsPath));
      }
    }
    Set<String> dirWsPaths = fsManager.getDirWsPaths(path);
    for (String dirWsPath : dirWsPaths) {
      getFileASTRecursivly(dirWsPath, astPerFile);
    }
  }

  private JSONObject getASTResult(String fileWsPath)
      throws ConflictException, NotFoundException, ServerException {
    String fileContent = fsManager.readAsString(fileWsPath);
    AstTreeAnalyzer astTreeAnalyzer = new AstTreeAnalyzer(fileContent);
    astTreeAnalyzer.reslove();
    return astTreeAnalyzer.getRecord();
  }

  @GET
  @Path("server/{code}")
  @Produces(APPLICATION_JSON)
  public Map<String, String> sendPost(@PathParam("code") String code) {
    String url = "http://bigcode.fudan.edu.cn/CodeRecommendationAPI/CheServlet";
    int timeout = 5000;

    String result = "";

    HttpURLConnection conn = null;
    OutputStreamWriter out = null;
    BufferedReader in = null;
    try {
      // String param = str2HexStr(projectPath);
      String param = code;
      // System.out.println(param);
      URL realUrl = new URL(url);
      conn = (HttpURLConnection) realUrl.openConnection();
      conn.setConnectTimeout(timeout);
      conn.setReadTimeout(timeout);
      //            conn.setRequestProperty("accept", "*/*");
      //            conn.setRequestProperty("connection", "Keep-Alive");
      //            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0;
      // Windows NT 5.1;SV1)");
      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.connect();
      // 获取URLConnection对象对应的输出流
      out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
      // 发送请求参数
      out.write(param);
      // flush输出流的缓冲
      out.flush();
      InputStream inputStream = conn.getInputStream();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      String line;
      String lineSeparator = hexStr2Str(System.getProperty("line.separator"));
      while ((line = in.readLine()) != null) {
        result += line + lineSeparator;
      }
      result = hexStr2Str(result);
      if (!result.startsWith("[[{\"importLineNumber")) {
        result =
            "[[{\"importLineNumber\":\""
                + "1"
                + "\",\"existImport\":\""
                + "0"
                + "\",\"index\":0,\"statement\":\"Sorry, CodeWisdom-aiAssistant cannot connect to the server.\",\"import\":\"[]\",\"tip\":\"No API Description\"}]]";
      }
      if (out != null) {
        out.close();
      }
      if (in != null) {
        in.close();
      }
      if (conn != null) {
        conn.disconnect();
      }
    } catch (Exception e) {
      if (conn != null) {
        conn.disconnect();
      }
      result =
          "[[{\"importLineNumber\":\""
              + "1"
              + "\",\"existImport\":\""
              + "0"
              + "\",\"index\":0,\"statement\":\"Sorry, CodeWisdom-aiAssistant cannot connect to the server.\",\"import\":\"[]\",\"tip\":\"No API Description\"}]]";
    } catch (Error e) {
      if (conn != null) {
        conn.disconnect();
      }

      result =
          "[[{\"importLineNumber\":\""
              + "1"
              + "\",\"existImport\":\""
              + "0"
              + "\",\"index\":0,\"statement\":\"Sorry, CodeWisdom-aiAssistant cannot connect to the server.\",\"import\":\"[]\",\"tip\":\"No API Description\"}]]";
    } finally {
      try {
        if (in != null) in.close();
        if (out != null) {
          out.close();
        }
      } catch (Exception e) {

      } catch (Error e) {
      }
    }
    Map<String, String> map = new HashMap<>();
    JSONArray codeResult = new JSONArray(result);
    codeResult = codeResult.getJSONArray(0);
    String resultString = "";
    String importString = "";
    String tipString = "";
    String importLineString = "";
    String existImportString = "";
    final int size = codeResult.length();
    for (int i = 0; i < codeResult.length(); i++) {
      if (i > 0) {
        resultString += "chenchifengefu";
        importString += "chenchifengefu";
        tipString += "chenchifengefu";
        importLineString += "chenchifengefu";
        existImportString += "chenchifengefu";
      }
      JSONObject object = (JSONObject) codeResult.get(i);
      resultString += object.getString("statement");
      if (object.getString("import") != null) {
        String im = object.getString("import");
        im = im.substring(1, im.length() - 1);
        importString += im;
      } else {
        importString += "null";
      }
      String line = object.getString("importLineNumber");
      importLineString += line;
      if (object.getString("existImport") != null) {
        String existImport = object.getString("existImport");
        existImportString += existImport;
      } else {
        existImportString += "no import";
      }
      String tip = object.getString("tip");
      if (tip.equals("No API Description")) {
        tip = "No API Description.";
      }
      tipString += tip;
    }
    map.put("api", resultString);
    map.put("import", importString);
    map.put("tip", tipString);
    map.put("line", importLineString);
    map.put("existImport", existImportString);
    return map;
  }

  public String hexStr2Str(String hexStr) {
    String str = "0123456789ABCDEF";
    char[] hexs = hexStr.toCharArray();
    byte[] bytes = new byte[hexStr.length() / 2];
    int n;
    for (int i = 0; i < bytes.length; i++) {
      n = str.indexOf(hexs[2 * i]) * 16;
      n += str.indexOf(hexs[2 * i + 1]);
      bytes[i] = (byte) (n & 0xff);
    }
    return new String(bytes);
  }
}
