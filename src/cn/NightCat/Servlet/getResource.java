package cn.NightCat.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.NightCat.Config.NCConfig;

/**
 * Servlet implementation class pay
 */
@WebServlet({"*.js","*.css","*.png","/API/index.html","/API/api.html","/API/Update.txt"})
public class getResource extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			response.setCharacterEncoding("utf-8");
			String urlPath = request.getRequestURI();
			String[] url = urlPath.split("\\.");
			if(url.length < 2){
				urlPath += "index.html";
				url = urlPath.split("\\.");
			}
			String Path =getServletContext().getRealPath("/");
			InputStream is = null;
			switch (url[url.length - 1]) {
				case "js":
					response.setContentType("application/x-javascript");
					break;
				case "png":
					response.setContentType("image/png");
					break;
				case "css":
					response.setContentType("text/css");
					break;
				case "html":
					response.setContentType("text/html");
					break;
				default:
					response.setContentType("text/plain");
					break;
			}
			if(NCConfig.OpenWiki)
				is = this.getClass().getResourceAsStream(urlPath);
			if(is == null)
				is = new FileInputStream(Path + urlPath.replaceFirst("/", ""));
			if(null == is){
				response.sendError(HttpServletResponse.SC_NOT_FOUND,urlPath + " is not found!");
				return;
			} 
			ServletOutputStream sos = response.getOutputStream();
			int buff = -1;
			while((buff = is.read()) != -1)
				sos.write(buff);
			is.close();
			sos.close();
		} catch (Exception e) {
			// TODO: handle exception
			response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI() + " is not found!");
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
