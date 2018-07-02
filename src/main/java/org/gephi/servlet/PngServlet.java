/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gephi.service.SvgService;

/**
 *
 * @author Qifei_Zhou
 */
@WebServlet(name = "PngServlet", urlPatterns = {"/PngServlet"})
public class PngServlet extends HttpServlet {

    public PngServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

   /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//                SvgService svgService = (SvgService)request.getSession().getAttribute("svgService");
//        ArrayList<Integer> actived = (ArrayList<Integer>)request.getSession().getAttribute("actived");
//        BufferedImage image = svgService.Png(actived);
//	response.setContentType("image/png");
//	ImageIO.write(image, "png", request.getOutputStream());     
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        SvgService svgService = (SvgService)req.getSession().getAttribute("svgService");
        
        ArrayList<Integer> actived = (ArrayList<Integer>)req.getSession().getAttribute("actived");       
        BufferedImage image = svgService.Png(actived);
        if(image == null)
            System.err.println("********************");
	res.setContentType("image/png");
	ImageIO.write(image, "png", res.getOutputStream());           
    }
  
}
