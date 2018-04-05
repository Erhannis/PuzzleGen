/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Face;
import com.erhannis.puzzlegen.structure.Group;
import com.erhannis.puzzlegen.structure.Vertex;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This is phase 4, the generation of SVGs.
 * @author erhannis
 */
public class Phase4GenSvg {
  public static enum ColorMode {
    NONE, RANDOM, RANDOM_WITH_ALPHA, WHITE
  }
  
  public static void writeGridToSvg(Cell root, File target) throws IOException {
    SVGGraphics2D g = getSvgGraphics2D();
    
    // Draw stuff
    
    // Get faces
    //TODO Extract this in some way
    HashSet<Face> faces = new HashSet<>();
    Utils.forEachCell(root, c -> {
      faces.addAll(c.faces);
    });
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
  
  public static void writeGroupsToSvg(Set<Group> groups, File target, ColorMode colorMode, boolean skipUnassignedBorderFaces) throws IOException {
    SVGGraphics2D g = getSvgGraphics2D();
    
    // Draw stuff
    
    SecureRandom rand = new SecureRandom();
    // Get faces
    //TODO Extract this in some way
    HashMap<Cell, Group> c2g = new HashMap<>();
    for (Group group : groups) {
      Color color;
      switch (colorMode) {
        case RANDOM:
          color = new Color(rand.nextInt());
          break;
        case RANDOM_WITH_ALPHA:
          color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.5f);
          break;
        case WHITE:
          color = new Color(0xFFFFFF);
          break;
        case NONE:
        default:
          color = null;
          break;
      }
      for (Cell c : group.cells) {
        c2g.put(c, group);
        if (color != null) {
          g.setColor(color);
          float[] xPoints = new float[c.vertices.size()];
          float[] yPoints = new float[c.vertices.size()];
          int i = 0;
          for (Vertex v : Utils.orderVertices(c.vertices)) {
            xPoints[i] = (float)v.coords[0];
            yPoints[i] = (float)v.coords[1];
            i++;
          }
          Polygon2D p = new Polygon2D(xPoints, yPoints, xPoints.length);
          g.fill(p);
        }
      }
    }
    if (colorMode != ColorMode.NONE) {
      g.setColor(Color.BLACK);
    }
    HashSet<Face> faces = new HashSet<>();
    Utils.forEachCell(groups.iterator().next().cells.iterator().next(), c -> {
      faces.addAll(c.faces);
    });
    HashSet<Face> facesToDraw = new HashSet<>();
    final int EXPECTED_CELLS = 2; // This is probably true across all dimensions, really
    final int BORDER_EXPECTED_CELLS = 1; // Likewise
    for (Face f : faces) {
      boolean markedGroup = false;
      Group group = null;
      if (f.cells.size() != EXPECTED_CELLS) {
        if (skipUnassignedBorderFaces && f.cells.size() == BORDER_EXPECTED_CELLS && c2g.get(f.cells.iterator().next()) == null) {
          // Skip
        } else {
          facesToDraw.add(f);
        }
      } else {
        for (Cell c : f.cells) {
          if (!markedGroup) {
            group = c2g.get(c);
            markedGroup = true;
          }
          if (!Objects.equals(group, c2g.get(c))) {
            facesToDraw.add(f);
          }
        }
      }
    }
    // Draw faces
    for (Face f : facesToDraw) {
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
    out.getParentFile().mkdirs();
    boolean useCSS = true;
    BufferedWriter bw = new BufferedWriter(new FileWriter(out));
    g.stream(bw, useCSS);
    bw.flush();
    bw.close();
  }
}
