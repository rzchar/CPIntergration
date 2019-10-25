package edu.nju.ics.pgs.server.http;

// import org.apache.http.NameValuePair;
// import org.apache.http.client.entity.UrlEncodedFormEntity;
// import org.apache.http.client.methods.CloseableHttpResponse;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.entity.StringEntity;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.message.BasicNameValuePair;
// import org.apache.http.util.EntityUtils;

import static org.apache.commons.compress.utils.CharsetNames.UTF_8;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class HttpRequest {
  /**
   * 向指定URL发送GET方法的请求
   *
   * @param url 发送请求的URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return URL 所代表远程资源的响应结果
   */
  public static String sendGet(String url, String param) {
    String result = "";
    BufferedReader in = null;
    try {
      String urlNameString = url + "?" + param;
      URL realUrl = new URL(urlNameString);
      // 打开和URL之间的连接
      URLConnection connection = realUrl.openConnection();
      // 设置通用的请求属性
      connection.setRequestProperty("accept", "*/*");
      connection.setRequestProperty("connection", "Keep-Alive");
      connection.setRequestProperty(
          "user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 建立实际的连接
      connection.connect();
      // 获取所有响应头字段
      Map<String, List<String>> map = connection.getHeaderFields();
      // 遍历所有的响应头字段
      for (String key : map.keySet()) {
        System.out.println(key + "--->" + map.get(key));
      }
      // 定义 BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      System.out.println("发送GET请求出现异常！" + e);
      e.printStackTrace();
    }
    // 使用finally块来关闭输入流
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    return result;
  }

  /**
   * 向指定 URL 发送POST方法的请求
   *
   * @param url 发送请求的 URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return 所代表远程资源的响应结果
   */
  public static String sendPost(String url, String param) {
    DataOutputStream outputStream = null;
    BufferedReader in = null;
    String result = "";
    try {
      String realParam = "input=" + URLEncoder.encode(param, "utf-8");

      URL realUrl = new URL(url);
      // 打开和URL之间的连接
      HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
      // 设置通用的请求属性
      //            conn.setRequestProperty("accept", "application/x-www-urlencoded");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("connection", "Keep-Alive");
      //            conn.setRequestProperty("user-agent",
      //                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setUseCaches(false);
      conn.setInstanceFollowRedirects(true);
      conn.setRequestMethod("POST");
      conn.connect();
      // 获取URLConnection对象对应的输出流
      //            out = new PrintWriter(conn.getOutputStream());
      // 发送请求参数

      //            out.print(realParam);
      outputStream = new DataOutputStream(conn.getOutputStream());
      outputStream.writeBytes(realParam);
      // flush输出流的缓冲
      outputStream.flush();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF_8));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      System.out.println("发送 POST 请求出现异常！" + e);
      e.printStackTrace();
    }
    // 使用finally块来关闭输出流、输入流
    finally {
      try {
        if (outputStream != null) {
          outputStream.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return result;
  }

  //    public static String sendClintPost(String url, String param){
  //
  //        CloseableHttpClient httpClient = null;
  //        CloseableHttpResponse response = null;
  //        String result = "default string";
  //        try{
  //            httpClient = HttpClients.createDefault();
  //            //创建一个post对象
  //            HttpPost post =new HttpPost(url);
  //            //创建一个Entity。模拟一个表单
  //            List<NameValuePair>kvList = new ArrayList<>();
  //            kvList.add(new BasicNameValuePair("input", param));
  //            //包装成一个Entity对象
  //            StringEntity entity = new UrlEncodedFormEntity(kvList,"utf-8");
  //            //设置请求的内容
  //            post.setEntity(entity);
  //            //执行post请求
  //            response =httpClient.execute(post);
  //            result = EntityUtils.toString(response.getEntity());
  //
  //        }catch (Exception e){
  //            System.out.print(e.getMessage());
  //        }finally {
  //            try{
  //                response.close();
  //                httpClient.close();
  //            }catch (Exception e){
  //                System.out.print(e.getMessage());
  //            }
  //        }
  //        return result;
  //    }
}
