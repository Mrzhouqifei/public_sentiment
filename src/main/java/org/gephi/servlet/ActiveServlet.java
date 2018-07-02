/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gephi.entity.Node;
import org.gephi.service.PngService;
import org.gephi.service.SvgService;
import org.gephi.utils.ReadAndProcess;

/**
 *
 * @author Qifei_Zhou
 */
@WebServlet(name = "ActiveServlet", urlPatterns = {"/ActiveServlet"})
public class ActiveServlet extends HttpServlet {

   

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    		String pngWritePath4 = this.getServletContext().getRealPath("/png4"); //存储四个时期概览图片
    		File file2 = new File(pngWritePath4);
    		// 判断上传文件的保存目录是否存在
    		if (!file2.exists() && !file2.isDirectory()) {
    			// 创建目录
    			file2.mkdir();
    		}
    		File[] file2s = file2.listFiles();// 声明目录下所有的文件 files[];
    		for (int i = 0; i < file2s.length; i++) {// 遍历目录下所有的文件
    			file2s[i].delete();
    		}
    		String gmlReadPath = this.getServletContext().getRealPath("/WEB-INF/gml/data_before.gml");
        ArrayList<Integer> actived = new ArrayList<Integer>();
        String writePath = this.getServletContext().getRealPath("/WEB-INF/gml");
        String[] s = (String[]) request.getParameterValues("search_to");
        ReadAndProcess readAndProcess = (ReadAndProcess) request.getSession().getAttribute("readAndProcess");
        for (Node node : readAndProcess.allNodes) {
            node.setActived(false);
        }
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                int id = Integer.valueOf(s[i]);
                actived.add(id);
//                System.err.println(id);
                for (Node node : readAndProcess.allNodes) {                  
                    if (id == node.getNid()) {
                        node.setActived(true);
                        break;
                    }
                }
            }
        }else{
            System.err.println("nullllll");
        }
        //四个时期
        PngService pngService = new PngService();
		int time = readAndProcess.writeGmlNoAfter(writePath + "/data_before.gml");
		
		pngService.script(gmlReadPath, pngWritePath4);
		pngService.scriptFour(gmlReadPath, time, pngWritePath4);
		request.getSession().setAttribute("pngService", pngService);
        request.getSession().setAttribute("actived", actived);
        response.sendRedirect("image.jsp");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
