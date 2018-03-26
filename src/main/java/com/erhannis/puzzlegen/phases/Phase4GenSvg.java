/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Face;
import com.erhannis.puzzlegen.structure.Vertex;
import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This is phase 4, the generation of SVGs.
 * @author erhannis
 */
public class Phase4GenSvg {
  public static void writeGridToSvg(Cell root, File target) throws IOException {
    SVGGraphics2D g = getSvgGraphics2D();
    
    // Draw stuff
    
    // Get faces
    //TODO Extract this in some way
    HashSet<Cell> toDo = new HashSet<>();
    HashSet<Cell> done = new HashSet<>();
    HashSet<Face> faces = new HashSet<>();
    toDo.add(root);
    while (!toDo.isEmpty()) {
      Iterator<Cell> i = toDo.iterator();
      Cell c = i.next();
      i.remove();
      
      faces.addAll(c.faces);
      HashSet<Cell> lToDo = new HashSet<Cell>();
      lToDo.addAll(c.getAllNeighbors());
      lToDo.removeAll(done); // Hopefully this is smart about it
      toDo.addAll(lToDo);
      
      done.add(c);
    }
    // Draw faces
    for (Face f : faces) {
      if (f.vertices.size() != 2) {
        throw new IllegalStateException("This face has " + f.vertices.size() + " vertices; can't render to SVG");
      }
      Iterator<Vertex> i = f.vertices.iterator();
      Vertex v1 = i.next();
      Vertex v2 = i.next();
      if (v1.coords.length != 2) {
        throw new IllegalStateException("This vertex has " + v1.coords.length + " coords; can't render to SVG");
      }
      if (v2.coords.length != 2) {
        throw new IllegalStateException("This vertex has " + v2.coords.length + " coords; can't render to SVG");
      }
      g.draw(new Line2D.Double(v1.coords[0], v1.coords[1], v2.coords[0], v2.coords[1]));
    }
    
    writeSvg(g, target);
  }
  
  protected static SVGGraphics2D getSvgGraphics2D() {
    DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
    String svgNS = "http://www.w3.org/2000/svg";
    Document document = domImpl.createDocument(svgNS, "svg", null);
    return new SVGGraphics2D(document);
  }
  
  protected static void writeSvg(SVGGraphics2D g, File out) throws IOException {
    boolean useCSS = true;
    BufferedWriter bw = new BufferedWriter(new FileWriter(out));
    g.stream(bw, useCSS);
    bw.flush();
    bw.close();
  }
}
