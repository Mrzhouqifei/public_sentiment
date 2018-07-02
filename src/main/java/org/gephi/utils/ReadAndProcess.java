package org.gephi.utils;

import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.entity.Edge;
import org.gephi.entity.Node;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.SVGExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ReadAndProcess {

	public Node[] allNodes;
	public Edge[] allEdges;
	public Node[] allNodesAfter;
	public Edge[] allEdgesAfter;
	public int MAX_TIME = 0;
	int MAX_TIME_AFTER = 0;

	public void read(String readPath, boolean isFirst) {
		try {
			JsonParser parser = new JsonParser(); // 创建JSON解析器
			File file = new File(readPath);
			JsonObject object = (JsonObject) parser.parse(new FileReader(file)); // 创建JsonObject对象
			JsonArray nodes = object.get("nodes").getAsJsonArray(); // 得到为json的数组
			JsonArray edges = object.get("edges").getAsJsonArray();
			allNodes = new Node[nodes.size()];
			for (int i = 0; i < nodes.size(); i++) {
				JsonObject subObject = nodes.get(i).getAsJsonObject();
				allNodes[i] = new Node();
				allNodes[i].setIsolated(true);
				allNodes[i].setNid(subObject.get("nid").getAsInt());
				allNodes[i].setNodeName(subObject.get("nodeName").getAsString());
				if (isFirst) {
					allNodes[i].setActived(false);
				} else {
					Boolean tag = subObject.get("actived").getAsBoolean();
					allNodes[i].setActived(tag);
				}
			}
			allEdges = new Edge[edges.size()];
			for (int i = 0; i < edges.size(); i++) {
				JsonObject subObject = edges.get(i).getAsJsonObject();
				allEdges[i] = new Edge();
				float probability = subObject.get("probability").getAsFloat();
				allEdges[i].setEid(subObject.get("eid").getAsInt());
				// allEdges[i].setConsumeTime(subObject.get("consumeTime").getAsInt());
				allEdges[i].setConsumeTime((int)(Math.random()*10));
				allEdges[i].setEdgeName(subObject.get("edgeName").getAsString());
				allEdges[i].setProbability(probability);
				int startPoint = subObject.get("startPoint").getAsInt();
				int endPoint = subObject.get("endPoint").getAsInt();
				int rd = Math.random() <= probability ? 1 : 0;
				if (rd == 1) {
					allEdges[i].setState(true);
				} else {
					allEdges[i].setState(false);
				}
				int after_id = Math.random() <= probability/2 ? 1 : 0;
				if (after_id == 1) {
					allEdges[i].setAfterState(true);
				} else {
					allEdges[i].setAfterState(false);
				}
				int count1 = 0;
				int count2 = 0;
				for (int j = 0; j < nodes.size(); j++) {
					if (count1 == 1 && count2 == 1) {
						break;
					}
					if (count1 == 0 && startPoint == allNodes[j].getNid()) {
						allNodes[j].setIsolated(false);
						count1 = 1;
						allEdges[i].setStartPoint(allNodes[j]);
					}
					if (count2 == 0 && endPoint == allNodes[j].getNid()) {
						allNodes[j].setIsolated(false);
						count2 = 1;
						allEdges[i].setEndPoint(allNodes[j]);
					}
				}
			}
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 处理一次过程所有节点激活时间
	 */
	public void handleData() {
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isActived()) {
				allNodes[i].setActiveTime(0);
			} else {
				allNodes[i].setActiveTime(Integer.MAX_VALUE);
			}
		}
		boolean update = true;
		while (update) {
			update = false;
			for (int i = 0; i < allEdges.length; i++) {
				if (allEdges[i].isState()) {
					if (allEdges[i].getStartPoint().getActiveTime() == Integer.MAX_VALUE) {
						continue;
					}
					int oldTime = allEdges[i].getEndPoint().getActiveTime();
					int newTime = allEdges[i].getStartPoint().getActiveTime() + allEdges[i].getConsumeTime();
					allEdges[i].setState(false);
					if (newTime < oldTime) {
//						System.err.println(newTime);
						allEdges[i].getEndPoint().setActiveTime(newTime);
						update = true;
					}
				}
			}
		}
	}

	public void handleDataAfter() {
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isActived()) {
				allNodes[i].setActiveTimeAfter(0);
			} else {
				allNodes[i].setActiveTimeAfter(Integer.MAX_VALUE);
			}
		}
		boolean update = true;
		while (update) {
			update = false;
			for (int i = 0; i < allEdges.length; i++) {
				if (allEdges[i].isAfterState()) {
					if (allEdges[i].getStartPoint().getActiveTimeAfter() == Integer.MAX_VALUE) {
						continue;
					}
					int oldTime = allEdges[i].getEndPoint().getActiveTimeAfter();
					int newTime = allEdges[i].getStartPoint().getActiveTimeAfter() + allEdges[i].getConsumeTime();
					allEdges[i].setAfterState(false);
					if (newTime < oldTime) {
//						System.err.println(newTime);
						allEdges[i].getEndPoint().setActiveTimeAfter(newTime);
						update = true;
					}
				}
			}
		}
	}
	
	public void formMp4(String saveMp4name, String imagesPath) throws Exception {
		System.out.println("-----------formMp4Before----------------");
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(saveMp4name, 1024, 1024);

		recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 28
		// recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // 28
		// recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
		recorder.setFormat("mp4");
		// recorder.setFormat("mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
		recorder.setFrameRate(1);
		recorder.setPixelFormat(0); // yuv420p
		recorder.start();
		//
		OpenCVFrameConverter.ToIplImage conveter = new OpenCVFrameConverter.ToIplImage();
		// 列出目录中所有的图片，都是jpg的，以1.jpg,2.jpg的方式，方便操作
		File file = new File(imagesPath);
		File[] flist = file.listFiles();
		// 循环所有图片
		int m = 1;
		if(flist.length == 1) {
			m=0;
		}
		for (int i = m; i <= flist.length; i++) {
			String fname = imagesPath + "/before" + i + ".png";
			IplImage image = cvLoadImage(fname); // 非常吃内存！！
			recorder.record(conveter.convert(image));
			// 释放内存？ cvLoadImage(fname); // 非常吃内存！！
			// opencv_core.cvReleaseImage(image);
		}
		recorder.stop();
		recorder.release();
	}

	public void formMp4After(String saveMp4name, String imagesPath) throws Exception {
		System.out.println("-----------formMp4After----------------");
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(saveMp4name, 1024, 1024);
		recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 28
		// recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // 28
		// recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
		recorder.setFormat("mp4");
		// recorder.setFormat("mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
		recorder.setFrameRate(1);
		recorder.setPixelFormat(0); // yuv420p
		recorder.start();
		//
		OpenCVFrameConverter.ToIplImage conveter = new OpenCVFrameConverter.ToIplImage();
		// 列出目录中所有的图片，都是jpg的，以1.jpg,2.jpg的方式，方便操作
		File file = new File(imagesPath);
		File[] flist = file.listFiles();
		// 循环所有图片
		int m = 1;
		if(flist.length == 1) {
			m=0;
		}
		for (int i = m; i <= flist.length; i++) {
			String fname = imagesPath + "/after" + i + ".png";
			IplImage image = cvLoadImage(fname); // 非常吃内存！！
			recorder.record(conveter.convert(image));
			// 释放内存？ cvLoadImage(fname); // 非常吃内存！！
			// opencv_core.cvReleaseImage(image);
		}
		recorder.stop();
		recorder.release();
	}

	/**
	 * 根据Applet 信息更新active
	 *
	 * @param readPath
	 */
	public void updateActive(String readPath) {
		File filename = new File(readPath); // 要读取以上路径的input。txt文件
		InputStreamReader reader;
		BufferedReader br;
		try {
			reader = new InputStreamReader(new FileInputStream(filename));
			br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = br.readLine(); // 一次读入一行数据
				int id = Integer.valueOf(line);
				for (int i = 0; i < allNodes.length; i++) {
					if (allNodes[i].getNid() == id) {
						allNodes[i].setActived(true);
						break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 建立一个输入流对象reader

	}


	public int writeGmlNoAfter(String writePath) {
		handleData();
		handleDataAfter();
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].getActiveTime() != Integer.MAX_VALUE) {
				MAX_TIME = Math.max(MAX_TIME, allNodes[i].getActiveTime());
			}
		}
		int TIME = MAX_TIME;
		String s = "graph [\ndirected 1";
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isIsolated()) {
				continue;
			}
			int time = allNodes[i].getActiveTime();
			int timeAfter = allNodes[i].getActiveTimeAfter();
			s += "\nnode [\n";
			s += "id ";
			s += allNodes[i].getNid();
			s += "\nlabel ";
			s += "\"";
			s += allNodes[i].getNodeName();
			s += "\"";
			s += "\nactive0 ";
			s += "\"";
			s += allNodes[i].isActived();
			s += "\"";
			for (int j = 1; j <= TIME; j++) {
				s = s + "\nactive" + j + " ";
				if (j < time) {
					s += "\"false\"";
				} else {
					s += "\"true\"";
				}
			}
			
			//after
			s += "\nactive_after0 ";
			s += "\"";
			s += allNodes[i].isActived();
			s += "\"";
			for (int j = 1; j <= TIME; j++) {
				s = s + "\nactive_after" + j + " ";
				if (j < timeAfter) {
					s += "\"false\"";
				} else {
					s += "\"true\"";
				}
			}
		
			s += "\n]";
		}
		for (int i = 0; i < allEdges.length; i++) {
			s += "\nedge [\n";
			s += "source ";
			s += allEdges[i].getStartPoint().getNid();
			s += "\ntarget ";
			s += allEdges[i].getEndPoint().getNid();
			s += "\nlabel ";
			s += "\"";
			s += allEdges[i].getEdgeName();
			s += "\"";
			s += "\n]";
		}
		s += "\n]";
		try {
			File file = new File(writePath);
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(s);
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return TIME;
	}

	
}
