package org.gephi.servlet;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.gephi.service.PngService;
import org.gephi.service.SvgService;
import org.gephi.toolkit.demos.PartitionGraph;
import org.gephi.utils.ReadAndProcess;

import com.itextpdf.awt.geom.Point;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class SvgServlet
 */
@WebServlet(name = "SvgServlet", urlPatterns = {"/SvgServlet.svg"})
public class SvgServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SvgServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
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
//        String readPath = this.getServletContext().getRealPath("/WEB-INF/gml/data_before.gml");
    	String gmlReadPath = this.getServletContext().getRealPath("/WEB-INF/gml/data_before.gml");
//        SvgService svgService = (SvgService)req.getSession().getAttribute("svgService");
        SvgService svgService = new SvgService();
        svgService.script(gmlReadPath);
//        ArrayList<Integer> actived = (ArrayList<Integer>)req.getSession().getAttribute("actived");
        String s = svgService.scriptTime(null);
        res.setContentType("image/svg+xml");
        ServletOutputStream out = res.getOutputStream();
        out.println(s);
        out.flush();
        out.close();
    }

}
