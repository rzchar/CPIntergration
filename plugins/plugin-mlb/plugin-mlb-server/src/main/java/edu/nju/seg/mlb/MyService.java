/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package edu.nju.seg.mlb;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.fs.server.FsManager;
import sun.nio.ch.ChannelInputStream;

/**
 * Example server service that greets the user.
 *
 * @author Edgar Mueller
 */
@Path("hello")
public class MyService {
  private FsManager fsManager;
  private final String resultName = "MLBResult";

  @Inject
  public MyService(FsManager fsManager) {
    this.fsManager = fsManager;
  }

  private String Exception2String(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    try {
      e.printStackTrace(pw);
    } finally {
      pw.close();
    }
    return sw.toString();
  }

  private String sendInputStream(URL url, InputStream inputStream, String projectPath)
      throws IOException, ConflictException, NotFoundException, ServerException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    int timeout = 30000; // 1,000 ms = 1 s

    // 设置
    conn.setDoOutput(true);
    conn.setDoInput(true);
    conn.setConnectTimeout(timeout);
    conn.setReadTimeout(timeout);
    conn.setUseCaches(false);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Charsert", "UTF-8");
    conn.setRequestProperty("Cache-Control", "no-cache");

    conn.connect();

    OutputStream out = conn.getOutputStream();
    DataInputStream in = new DataInputStream(inputStream);
    String result = null;
    int bytes = 0;
    byte[] bufferOut = new byte[2048];
    while ((bytes = in.read(bufferOut)) != -1) {
      out.write(bufferOut, 0, bytes);
    }
    in.close();
    out.flush();
    out.close();

    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
      // 接收传来的 ZipInputStream 并保存到本地 MLBResult.zip
      InputStream responseStream = conn.getInputStream();
      fsManager.unzip(projectPath + resultName, responseStream, false);
      result = String.format("MLB 生成的文件和覆盖率情况在 %s 文件夹中", resultName);
    } else {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(conn.getErrorStream())); // 这里要用 getErrorStream()
      StringBuilder sb = new StringBuilder();
      sb.append("Error: code ").append(conn.getResponseCode()).append(" ====> ");
      String line = null;
      while ((line = br.readLine()) != null) sb.append(line);
      result = sb.toString();
    }
    conn.disconnect();
    return result;
  }

  /**
   * Returns a greeting message.
   *
   * @param name the parameter
   * @return a greeting message
   */
  @GET
  @Path("{name}")
  public String sayHello(@PathParam("name") String name) {
    if (fsManager == null) {
      return "fsManager is null";
    }

    try {
      String path = name.replaceAll("——", "/"); // 用 —— 替换 / 只是暂时的解决方法
      if (fsManager.exists(path)) {
        if (fsManager.isDir(path)) {
          if (fsManager.existsAsDir(path + resultName)) fsManager.delete(path + resultName, true);
          InputStream inputStream = fsManager.zip(path);
          if (inputStream instanceof ChannelInputStream) {
            // 阿里云服务器地址: 139.196.146.199
            String ip = "139.196.146.199";
            URL url = new URL(String.format("http://%s:8080/MLBserver/fileupload", ip));
            try {
              return sendInputStream(url, inputStream, path);
            } catch (IOException e) {
              return Exception2String(e);
            }
          } else return "Error: InputStream is not ChannelInputStream";
        } else return "Error: Not exist such dir";
      } else return "Error: Not exist such path";
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return e.toString();
    }
  }
}
