package org.gephi.servlet;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gephi.service.SvgService;

import org.gephi.utils.ReadAndProcess;

/**
 * Servlet implementation class WelcomeServlet
 */
@WebServlet(name = "WelcomeServlet", urlPatterns = {"/WelcomeServlet"})
public class WelcomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WelcomeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String readPath = this.getServletContext().getRealPath("/WEB-INF/upload/data_before.json");
		String gmlReadPath = this.getServletContext().getRealPath("/WEB-INF/gml/data_before.gml");
		String readPath1 = this.getServletContext().getRealPath("/WEB-INF/upload/data_after.json");		
		String writePath = this.getServletContext().getRealPath("/WEB-INF/gml");
		
		File file = new File(writePath);
		// 判断上传文件的保存目录是否存在
		if (!file.exists() && !file.isDirectory()) {
			// 创建目录
			file.mkdir();
		}
		String svgWritePath = this.getServletContext().getRealPath("/WEB-INF/svg");
		File file1 = new File(svgWritePath);
		// 判断上传文件的保存目录是否存在
		if (!file1.exists() && !file1.isDirectory()) {
			// 创建目录
			file1.mkdir();
		}
		ReadAndProcess readAndProcess = new ReadAndProcess();
		readAndProcess.read(readPath, true);
		int png_time = readAndProcess.writeGmlNoAfter(writePath + "/data_before.gml");		
//		request.getSession().setAttribute("nodes", readAndProcess.allNodes);
                request.getSession().setAttribute("readAndProcess", readAndProcess);
                SvgService svgService = new SvgService();
                svgService.script(gmlReadPath);
//                svgService.scriptTimeNoAfter(gmlReadPath, png_time, pngWritePath4);
                request.getSession().setAttribute("svgService", svgService);
                
                request.getSession().setAttribute("actived", null);
		response.sendRedirect("image.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
