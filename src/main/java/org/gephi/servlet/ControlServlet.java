package org.gephi.servlet;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gephi.service.PngService;
import org.gephi.service.SvgService;
import org.gephi.utils.ReadAndProcess;

/**
 * Servlet implementation class ControlServlet
 */
@WebServlet(name = "ControlServlet", urlPatterns = { "/ControlServlet" })
public class ControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControlServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String gmlReadPath = this.getServletContext().getRealPath("/WEB-INF/gml/data_before.gml");
		String writePath = this.getServletContext().getRealPath("/WEB-INF/gml");
		String pngWritePath = this.getServletContext().getRealPath("/WEB-INF/png");
		File file1 = new File(pngWritePath);
		// 判断上传文件的保存目录是否存在
		if (!file1.exists() && !file1.isDirectory()) {
			// 创建目录
			file1.mkdir();
		}
		String mp4WritePath = this.getServletContext().getRealPath("/mp4");
		File file2 = new File(mp4WritePath);
		// 判断上传文件的保存目录是否存在
		if (!file2.exists() && !file2.isDirectory()) {
			// 创建目录
			file2.mkdir();
		}
		File[] file2s = file2.listFiles();// 声明目录下所有的文件 files[];
		for (int i = 0; i < file2s.length; i++) {// 遍历目录下所有的文件
			file2s[i].delete();
		}
		String beforemp4WritePath = mp4WritePath + "/before.mp4";
		String aftermp4WritePath = mp4WritePath +"/after.mp4";
		
		ReadAndProcess readAndProcess = (ReadAndProcess) request.getSession().getAttribute("readAndProcess");
//		PngService pngService = new PngService();
		PngService pngService = (PngService) request.getSession().getAttribute("pngService");
		int time = readAndProcess.MAX_TIME;
		
//		pngService.script(gmlReadPath, pngWritePath);
		pngService.scriptTimeNoAfter(gmlReadPath, time, pngWritePath);
		pngService.scriptTimeAfter(gmlReadPath, time, pngWritePath);
		try {
			readAndProcess.formMp4(beforemp4WritePath, pngWritePath);
			readAndProcess.formMp4After(aftermp4WritePath, pngWritePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		// 删除之前生成的照片
		File[] files = file1.listFiles();// 声明目录下所有的文件 files[];
		for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
			files[i].delete();
		}
		SvgService svgService = (SvgService) request.getSession().getAttribute("svgService");
		svgService.script(gmlReadPath);
		response.sendRedirect("mp4.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
