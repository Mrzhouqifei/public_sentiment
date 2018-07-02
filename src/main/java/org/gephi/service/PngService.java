package org.gephi.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.apache.bcel.generic.NEW;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PNGExporter;
import org.gephi.io.exporter.preview.SVGExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.quadtree.QuadTree;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;

public class PngService {

	private final ExecutorService executor = Executors.newCachedThreadPool();// 启用多线程

	ProjectController pc;
	Workspace workspace;
	ImportController importController;
	GraphModel graphModel;
	AppearanceController appearanceController;
	AppearanceModel appearanceModel;
	DirectedGraph graph;
	PreviewModel model;
	Color[] colors = new Color[] { Color.RED, Color.green, new Color(0, 0, 255, 0), Color.black}; //new Color(0xFEF0D9)

	/**
	 * 初始的svg图片
	 *
	 * @param readPath
	 * @return xml代码
	 */
	public void script(String readPath, String writePath) {
		// Init a project - and therefore a workspace
		pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		workspace = pc.getCurrentWorkspace();

		// Get controllers and models
		importController = Lookup.getDefault().lookup(ImportController.class);
		graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
		appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
		appearanceModel = appearanceController.getModel();
		model = Lookup.getDefault().lookup(PreviewController.class).getModel();

		// Import file
		Container container;
		try {
			File file = new File(readPath);
			container = importController.importFile(file);
			container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED); // Force DIRECTED
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		// Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		// See if graph is well imported
		graph = graphModel.getDirectedGraph();

		// Run YifanHuLayout for 100 passes - The layout always takes the current
		// visible view
		YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		layout.setGraphModel(graphModel);
		layout.resetPropertiesValues();

		layout.initAlgo();
		for (int i = 0; i < 100 && layout.canAlgo(); i++) {
			layout.goAlgo();
		}
		layout.endAlgo();

		// Get Centrality
		GraphDistance distance = new GraphDistance();
		distance.setDirected(true);
		distance.execute(graphModel);

		// Rank size by centrality
		Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
		Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn,
				RankingNodeSizeTransformer.class);
		RankingNodeSizeTransformer centralityTransformer = (RankingNodeSizeTransformer) centralityRanking
				.getTransformer();
		centralityTransformer.setMinSize(3);
		centralityTransformer.setMaxSize(7);
		appearanceController.transform(centralityRanking);
	}

	public void scriptTimeNoAfter(String readPath, int time, String writePath) throws IOException {
		int m = 1;
		if(time == 1) {
			m=0;
		}
		for (int i = m; i <= time; i++) {
			String active = "active" + i;
			Column column = graphModel.getNodeTable().getColumn(active);
			Function func = appearanceModel.getNodeFunction(graph, column, PartitionElementColorTransformer.class);
			Partition partition = ((PartitionFunction) func).getPartition();
			// partition.setColors(colors);
			partition.setColor("true", colors[0]);
			partition.setColor("false", colors[1]);
			appearanceController.transform(func);

//			model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));
			model.getProperties().putValue(PreviewProperty.SHOW_EDGES, Boolean.TRUE);
			model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
			model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, colors[3]);
			ExportController ec = Lookup.getDefault().lookup(ExportController.class);
			String path = writePath + "/before" + i + ".png";
			 try {
			 ec.exportFile(new File(path));
			 } catch (IOException ex) {
			 ex.printStackTrace();
			 }
			System.out.println("formpngBefore" + i);
		}
	}
	
	public void scriptTimeAfter(String readPath, int time, String writePath) throws IOException {
		int m = 1;
		if(time == 1) {
			m=0;
		}
		for (int i = m; i <= time; i++) {
			String active = "active_after" + i;
			Column column = graphModel.getNodeTable().getColumn(active);
			Function func = appearanceModel.getNodeFunction(graph, column, PartitionElementColorTransformer.class);
			Partition partition = ((PartitionFunction) func).getPartition();
			partition.setColor("true", colors[0]);
			partition.setColor("false", colors[1]);
			appearanceController.transform(func);

//			model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));
			model.getProperties().putValue(PreviewProperty.SHOW_EDGES, Boolean.TRUE);
			model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
			model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, colors[3]);
			ExportController ec = Lookup.getDefault().lookup(ExportController.class);
			String path = writePath + "/after" + i + ".png";
			 try {
			 ec.exportFile(new File(path));
			 } catch (IOException ex) {
			 ex.printStackTrace();
			 }
			System.out.println("formpngAfter" + i);
		}
	}
	 public void scriptFour(String readPath, int time, String writePath) throws IOException {
		 System.out.println(time);
	 		if (time==0){
	 			return;
	 		}
	 		{
	 			//潜伏期
	 			int i = 1;
	 			if (i<1) {
	 				i = 1;
	 			}
	 			String active = "active" + i;
	 			Column column = graphModel.getNodeTable().getColumn(active);
	 			Function func = appearanceModel.getNodeFunction(graph, column, PartitionElementColorTransformer.class);
	 			Partition partition = ((PartitionFunction) func).getPartition();
	 			// partition.setColors(colors);
	 			
	 			partition.setColor("false", colors[1]);
	 			partition.setColor("true", colors[0]);
	 			appearanceController.transform(func);
	 			
	 	        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.FALSE);
	 	        model.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.GRAY));
	 			
	 			model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
	 			model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, colors[2]);
	 			ExportController ec = Lookup.getDefault().lookup(ExportController.class);
	 			String path = writePath +"/p"+ 1 + ".png";
	 			 try {
	 			 ec.exportFile(new File(path));
	 			 } catch (IOException ex) {
	 			 ex.printStackTrace();
	 			 }
	 			System.out.println("formpngBefore" + i);
	 		}
	 		
	 		{
	 			//突发期
	 			int i = time / 2;
	 			if (i<1) {
	 				i = 1;
	 			}
	 			String active = "active" + i;
	 			Column column = graphModel.getNodeTable().getColumn(active);
	 			Function func = appearanceModel.getNodeFunction(graph, column, PartitionElementColorTransformer.class);
	 			Partition partition = ((PartitionFunction) func).getPartition();
	 			// partition.setColors(colors);
	 			
	 			partition.setColor("false", colors[1]);
	 			partition.setColor("true", colors[0]);
	 			appearanceController.transform(func);

	 			model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
	 			model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, colors[2]);
	 			ExportController ec = Lookup.getDefault().lookup(ExportController.class);
	 			String path = writePath+"/p" + 2 + ".png";
	 			 try {
	 			 ec.exportFile(new File(path));
	 			 } catch (IOException ex) {
	 			 ex.printStackTrace();
	 			 }
	 			System.out.println("formpngBefore" + i);
	 		}
	 		
	 		{
	 			//蔓延期
	 			int i = time;
	 			String active = "active" + i;
	 			Column column = graphModel.getNodeTable().getColumn(active);
	 			
	 			
	 			Function func = appearanceModel.getNodeFunction(graph, column, PartitionElementColorTransformer.class);
	 			Partition partition = ((PartitionFunction) func).getPartition();
	 			// partition.setColors(colors);
	 			
	 			partition.setColor("false", colors[1]);
	 			partition.setColor("true", colors[0]);
	 			
	 			appearanceController.transform(func);
	 			

//	 			model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.WHITE));
	 			model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
	 			model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, colors[2]);
	 			ExportController ec = Lookup.getDefault().lookup(ExportController.class);
	 			String path = writePath +"/p"+ 3 + ".png";
	 			 try {
	 			 ec.exportFile(new File(path));
	 			 } catch (IOException ex) {
	 			 ex.printStackTrace();
	 			 }
	 			System.out.println("formpngBefore" + i);
	 		}
	 		
			

	 	}
}
