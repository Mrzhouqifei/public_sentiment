/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.toolkit.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JFrame;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.*;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.toolkit.demos.plugins.preview.MouseListenerTemplate;
import org.gephi.toolkit.demos.plugins.preview.PreviewSketch;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class PreviewJFrame extends JApplet{
 Workspace workspace;
    public void init() {
    	
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
        final PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        //Import file
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        try {
            File file = new File("/Users/mr_zhou/Desktop/舆情发展/gml/data_before.gml");
            container = importController.importFile(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //Preview configuration
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.ARROW_SIZE, 20);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLUE);

      
        
        //New Processing target, get the PApplet
        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);
        final PreviewSketch previewSketch = new PreviewSketch(target);
        final MouseListenerTemplate mlt = new MouseListenerTemplate();
        previewController.refreshPreview();
        
        
        setLayout(new BorderLayout());
        add(previewSketch, BorderLayout.CENTER);
        setSize(512, 384);
        //Add the applet to a JFrame and display
//        JFrame frame = new JFrame("Test Preview");
//        frame.setLayout(new BorderLayout());
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(previewSketch, BorderLayout.CENTER);
//
////        frame.setSize(1024, 768);
//        frame.setSize(512, 384);
        
        previewSketch.addMouseListener(new MouseAdapter() {
        		@Override
        		public void mouseClicked(MouseEvent e) {
//        			previewSketch.mouseClicked(e);
        			System.out.println("click point :"+e.getX()+","+ e.getY());
                                mlt.mouseClicked(new PreviewMouseEvent(e.getX(), e.getY(), PreviewMouseEvent.Type.CLICKED, PreviewMouseEvent.Button.LEFT, null), model.getProperties(), workspace);
//                                System.err.println(e.getX());
                        }
        		
		});
getAllNodePoint();
	
//       resize(512, 512);
//        frame.setVisible(true);
    }
        	public ArrayList<Point> getAllNodePoint(){
		ArrayList<Point> points = new ArrayList<Point>();
		for (Node node : Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace).getGraph().getNodes()) {
			Point point = new Point();
			point.setLocation(node.x(), node.y());
                        System.out.println(node.getId()+ ":"+node.x()+" "+node.y());
			points.add(point);
		}
		return points;
	}
 
//        	 public static void main(String[] args) {
//        	        PreviewJFrame previewJFrame = new PreviewJFrame();
//        	        previewJFrame.init();
//        	    }
}



