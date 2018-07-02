package org.gephi.service;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.bcel.generic.NEW;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.entity.Edge;
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
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewMouseEvent;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.plugin.builders.EdgeLabelBuilder;
import org.gephi.preview.spi.Renderer;
import org.gephi.preview.types.DependantColor;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.toolkit.demos.plugins.preview.MouseListenerTemplate;
import org.gephi.toolkit.demos.plugins.preview.PreviewSketch;
import org.openide.util.Lookup;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.gephi.graph.api.Node;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.spi.PreviewMouseListener;
import org.openide.util.lookup.ServiceProvider;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.pdf.qrcode.Version.ECB;
import org.gephi.utils.ReadAndProcess;

public class SvgService {

    ProjectController pc;
    Workspace workspace;
    ImportController importController;
    GraphModel graphModel;
    AppearanceController appearanceController;
    AppearanceModel appearanceModel;
    DirectedGraph graph;
    Color[] colors = new Color[]{Color.RED, Color.green, new Color(0, 0, 255, 0)}; //new Color(0xFEF0D9)  new Color(0x272727)
    PreviewModel model;

    /**
     * 初始的svg图片
     *
     * @param readPath
     * @return xml代码
     */
    public String script(String readPath) {
        // Init a project - and therefore a workspace
        pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();

        // Get controllers and models
        importController = Lookup.getDefault().lookup(ImportController.class);
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        appearanceModel = appearanceController.getModel();
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        model = previewController.getModel();

        // Import file
        Container container;
        try {
            File file = new File(readPath);
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED); // Force DIRECTED
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
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

        //Get Centrality
        GraphDistance distance = new GraphDistance();
        distance.setDirected(true);
        distance.execute(graphModel);

        //Rank size by centrality
        Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn, RankingNodeSizeTransformer.class);
        if (centralityRanking != null) {
            RankingNodeSizeTransformer centralityTransformer = (RankingNodeSizeTransformer) centralityRanking.getTransformer();
            centralityTransformer.setMinSize(3);
            centralityTransformer.setMaxSize(7);
            appearanceController.transform(centralityRanking);
        }

        // 粮食
        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        model.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.BLACK));
//        model.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.FALSE);
        
//        model.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
//        model.getProperties().putValue(PreviewProperty.ARROW_SIZE, 6);
//        model.getProperties().putValue(PreviewProperty.EDGE_LABEL_OUTLINE_SIZE, 5);
        model.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_SIZE, 5);
//        model.getProperties().putValue(PreviewProperty.EDGE_LABEL_COLOR, new DependantOriginalColor(Color.GRAY));
//        model.getProperties().putValue(PreviewProperty.EDGE_LABEL_MAX_CHAR, 2);
        model.getProperties().putValue(PreviewProperty.NODE_LABEL_MAX_CHAR, 2);
        model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(colors[1]));
        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.5f));
        model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, colors[2]);
        return null;
    }

    /**
     *
     * @return 所有点的坐标集合
     */
    public ArrayList<Point> getAllNodePoint() {
        ArrayList<Point> points = new ArrayList<Point>();
        for (Node node : Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace).getGraph().getNodes()) {
            Point point = new Point();
            point.setLocation(node.x(), node.y());
            points.add(point);
            System.out.println(node.getLabel() + ":" + node.x() + "," + (node.y()));
        }
        return points;
    }

    /**
     * 目前假设time分度值为1 读入active，重新输出svg代码
     *
     * @param readPath
     * @param time
     * @return
     */
    public String scriptTime(ArrayList<Integer> actived) {

        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        SVGExporter svge = (SVGExporter) ec.getExporter("svg");
        svge.setWorkspace(workspace);
        StringWriter writer = new StringWriter();
        ec.exportWriter(writer, svge);
        String s = writer.getBuffer().toString();
        return s;
    }
    
     public BufferedImage Png(ArrayList<Integer> actived) throws IOException {
//         System.err.println("###################");
//        if(actived!=null)
//        System.err.println(actived.size());
        for (Node node : Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace).getGraph().getNodes()) {
            boolean flag = false;
            if (actived != null) {
                for (int i : actived) {
                    if ((int) Float.parseFloat(String.valueOf(node.getId())) == i) {
                        node.setColor(colors[0]);
//                        System.err.println("###################");
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                node.setColor(colors[1]);
            }

        }
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        PNGExporter svge = (PNGExporter) ec.getExporter("png");
        svge.setWorkspace(workspace);      
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();   
        ec.exportStream(baos, svge);
        byte[] bs = baos.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bs);
        BufferedImage image = ImageIO.read(in);            
        return image;
    }
     
    
}
