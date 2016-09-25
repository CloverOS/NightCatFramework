package cn.NightCat.Servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cn.NightCat.Config.NCConfig;
import cn.NightCat.Exception.NCException;
import cn.NightCat.Util.LogUtil;

public class CodeMakerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	private static Map<Long, String> CodeMap = new LinkedHashMap<Long,String>();
	private String[] img = new String[]{"0","1","2","3","4","5","5","7","8","9"
			,"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t",
			"u","v","w","x","y","z"
			,"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T",
			"U","V","W","X","Y","Z"};
    Random random = new Random();
    private Font[] codeFont =
    {
        new Font("Times New Roman", Font.PLAIN,18), 
        new Font("Times New Roman", Font.PLAIN, 18),
        new Font("Times New Roman", Font.PLAIN,18),
        new Font("Times New Roman", Font.PLAIN, 18)
    };
    private Color[] color =
    {
       Color.BLACK, Color.RED, Color.DARK_GRAY, Color.BLUE
    };
    int width = 60, height = 20;
 
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
                      throws ServletException, IOException
    {
        doPost(request, response);
    }
 
    public String drawCode(Graphics graphics, int i)
    {
        String number = img[random.nextInt(img.length)];
        graphics.setFont(codeFont[i]);
        graphics.setColor(color[i]);
        //绘制验证码到图片X、Y
        graphics.drawString(number, 6 + i * 13,16);
        return number;
    }
    
    public void drawNoise(Graphics graphics, int lineNumber)
    {
        graphics.setColor(getRandColor(160,200));
        for (int i = 0; i < lineNumber; i++)
        {
            int pointX1 = 1 + (int)(Math.random() * width);
            int pointY1 = 1 + (int)(Math.random() * height);
            int pointX2 = 1 + (int)(Math.random() * width);
            int pointY2 = 1 + (int)(Math.random() * height);
            graphics.drawLine(pointX1, pointY1, pointX2, pointY2);
        }
    }
    public Color getRandColor(int fc,int bc){
        Random random = new Random();
        if(fc>255) fc=255;
        if(bc>255) bc=255;
        int r=fc+random.nextInt(bc-fc);
        int g=fc+random.nextInt(bc-fc);
        int b=fc+random.nextInt(bc-fc);
        return new Color(r,g,b);
    }

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
    	try {
    		String str_time = request.getParameter("Timestamp");
    		
    		if(null == str_time){
    			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Verify.png not found!");
    			return;
    		}
    		if(!str_time.matches("[0-9]{17}")){
    			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Verify.png not found!");
    			return;
    		}
    		BigDecimal timestamp = new BigDecimal(str_time);
            response.reset();
            response.setContentType("image/png");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(getRandColor(200,250));
            g.fillRect(0, 0, width, height);
            String code = "";
            for (int i = 0; i < 4; i++)
            {
                code += drawCode(g, i);
            }
            LogUtil.getInstance().debug("生成验证码["+timestamp+"] 验证码:["+code+"]");
            CodeMap.put(timestamp.longValue(), code.trim().toUpperCase());
            drawNoise(g, 5);
            ServletOutputStream sos = response.getOutputStream();
            ImageIO.write(image, "PNG", sos);
            sos.close();
            clearCode();
		} catch (Exception e) {
			// TODO: handle exception
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Verify.png not found!");
			NCException.printStackTrace(e,false);
		}
    }
    /***
     * 清除超过5分钟的验证码
     */
    private void clearCode()
    {
		Iterator<Map.Entry<Long, String>> iter = CodeMap.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry<Long, String> entry = iter.next();
			if((System.currentTimeMillis() - entry.getKey()) < NCConfig.ImageOverTime)
				break;
			iter.remove();
		}
    }
    /**
     * 判断验证码是否正确
     * @param key 时间戳
     * @param value 验证码值
     * @return true 说明验证码正确, false 验证码错误
     */
    public final static boolean VerifyCode(long key,String value)
    {
    	if(null != value)
    		value = value.replace(" ", "");
    	if(!CodeMap.containsKey(key))
    		return false;
    	if(value.trim().toUpperCase().equals(CodeMap.get(key)))
    		return true;
    	else
    		return false;
	}
    /**
     * 删除验证码
     * @param key 删除指定验证码
     */
    public final static void DeleteVerifyCode(long key){
    	CodeMap.remove(key);
    }
    /**
     * 获取验证码值 仅用于测试
     * @param key 时间戳
     * @return
     */
    public final static String getVerfiCode(long key){
    	if(!CodeMap.containsKey(key))
    		return null;
    	return CodeMap.get(key);
    }
    

}
