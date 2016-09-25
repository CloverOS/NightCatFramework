package cn.NightCat.Util;


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;
import java.util.zip.*;
import net.sf.json.JSONObject;


public class HttpUtil {
	/** 平台编号 */
	public static final String Head = "";
	/** 平台密钥 */
	public static final String Key = "";

	private static final SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	private static String send(String url, String method, byte[] param, Map<String, String> headers) throws Exception {
        String result = null;
        HttpURLConnection conn = getConnection(url, method, param, headers);
        String charset = conn.getHeaderField("Content-Type");

        int buffsize = conn.getHeaderFieldInt("Content-Length", 8092);
        charset = detectCharset(charset);
        InputStream input = getInputStream(conn);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int count;
        byte[] buffer = new byte[buffsize];
        while ((count = input.read(buffer, 0, buffer.length)) > 0) {
            output.write(buffer, 0, count);
        }
        input.close();
        // 若已通过请求头得到charset，则不需要去html里面继续查找
        if (charset == null || charset.equals("")) {
            charset = detectCharset(output.toString());
            // 若在html里面还是未找到charset，则设置默认编码为gbk
            if (charset == null || charset.equals("")) {
                charset = "utf-8";
            }
        }
        result = output.toString(charset);
        output.close();
        return result;
    }

    private static String detectCharset(String input) {
        Pattern pattern = Pattern.compile("charset=\"?([\\w\\d-]+)\"?;?", Pattern.CASE_INSENSITIVE);
        if (input != null && !input.equals("")) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private static InputStream getInputStream(HttpURLConnection conn) throws Exception {
        String ContentEncoding = conn.getHeaderField("Content-Encoding");
        if (ContentEncoding != null) {
            ContentEncoding = ContentEncoding.toLowerCase();
            if (ContentEncoding.indexOf("gzip") != 1)
                return new GZIPInputStream(conn.getInputStream());
            else if (ContentEncoding.indexOf("deflate") != 1)
                return new DeflaterInputStream(conn.getInputStream());
        }

        return conn.getInputStream();
    }

    static HttpURLConnection getConnection(String url, String method, byte[] param, Map<String, String> header) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
        conn.setRequestMethod(method);
        // 设置通用的请求属性
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
        String ContentEncoding = null;
        if (header != null) {
            for (Entry<String, String> entry : header.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("Content-Encoding"))
                    ContentEncoding = entry.getValue();
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (method == "POST") {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (param != null && param.length != 0) {
                OutputStream output = conn.getOutputStream();
                if (ContentEncoding != null) {
                    if (ContentEncoding.indexOf("gzip") > 0) {
                        output=new GZIPOutputStream(output);
                    }
                    else if(ContentEncoding.indexOf("deflate") > 0) {
                        output=new DeflaterOutputStream(output);
                    }
                }
                output.write(param);
            }
        }
        // 建立实际的连接
        conn.connect();
        return conn;
    }

    /***
     * 上传文件
     * @param url 上传地址
     * @param file 文件
     * @param filename 文件地址例( /201508280126/1.jpg)
     * @param other 其他提交附加信息 如果无请填 null
     * @return 返回上传结果（上传成功返回 ok 失败返回失败消息）
     * @throws Exception
     */
    public static String sendFile(String url, File file, String session, Map<String, String> other) throws Exception
    {
    	JSONObject  jsonObject = new JSONObject();
    	JSONObject body = new JSONObject();
    	String timestamp = sm.format(new Date());
    	jsonObject.put("Head", HttpUtil.Head);
    	if(other!=null)
    	{
    		Iterator<Map.Entry<String, String>> iter = other.entrySet().iterator();
        	while (iter.hasNext()) {
        		Map.Entry<String, String> entry = iter.next();
        		body.put(entry.getKey(), entry.getValue());
    		}
    	}
    	jsonObject.put("Body", body);
    	jsonObject.put("Session", session);
    	jsonObject.put("Timestamp", timestamp);
    	jsonObject.put("Sign", Sign.getSign(body.toString(), Sign.getKey(HttpUtil.Key, timestamp)));
    	
        FileInputStream fi = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write((jsonObject.toString().replace("\n", "") + "\n").getBytes("utf-8"));
        int buff;
        while (-1 != (buff = fi.read()))
            bos.write(buff);
        fi.close();
        
        return send(url, "POST", bos.toByteArray(), null);
    }

    /***
     * HTTP get 方式请求
     * @param url 请求地址
     * @return 返回内容
     * @throws Exception
     */
    public static String sendGet(String url) throws Exception {
        return send(url, "GET", null, null);
    }

    /***
     * HTTP post 方式请求
     * @param url url
     * @param param 参数
     * @param charset 字符编码
     * @return
     * @throws Exception
     */
    public static String sendPost(String url, String param,String charset) throws Exception {
        Map<String, String> head = new HashMap<String,String>();
        head.put("Content-Type", "application/json;charset=" + charset);
        return send(url, "POST", param.getBytes(charset), head);
    }
    
    /***
     * HTTP post 方式请求
     * @param url url
     * @param param 参数
     * @return
     * @throws Exception
     */
    public static String sendPost(String url, String param) throws Exception {
        Map<String, String> head = new HashMap<String,String>();
        head.put("Content-Type", "application/json;charset=UTF-8");
        return send(url, "POST", param.getBytes("UTF-8"), head);
    }
}