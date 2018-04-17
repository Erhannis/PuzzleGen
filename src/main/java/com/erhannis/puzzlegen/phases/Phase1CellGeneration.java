/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.mathnstuff.FactoryHashMap;
import com.erhannis.mathnstuff.Holder;
import com.erhannis.mathnstuff.MeMath;
import com.erhannis.mathnstuff.utils.BagMap;
import com.erhannis.mathnstuff.utils.ListMap;
import com.erhannis.puzzlegen.datagroups.LSystemResult;
import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Face;
import com.erhannis.puzzlegen.structure.Vertex;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Contains a variety of methods for generating cells.
 *
 * @author erhannis
 */
public class Phase1CellGeneration {

  public static final int FACTOR = 10;

  public static Collection<Cell> generateSquareBoard(int w, int h) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));
    BagMap<Vertex, Cell> v2c = new BagMap<>();

    for (double y = 0; y < (h * FACTOR); y += FACTOR) {
      for (double x = 0; x < (w * FACTOR); x += FACTOR) {
        Face[] lFaces = new Face[4];
        double o = FACTOR;
        lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + o, y));
        lFaces[1] = faces.get(vertices.get(x + o, y), vertices.get(x + o, y + o));
        lFaces[2] = faces.get(vertices.get(x + o, y + o), vertices.get(x, y + o));
        lFaces[3] = faces.get(vertices.get(x, y + o), vertices.get(x, y));
        Cell c = new Cell(lFaces);
        v2c.put(c, c.vertices.toArray(new Vertex[0]));
      }
    }
    return v2c.map.values();
  }

  public static Collection<Cell> generateTriangleBoard(int w, int h, boolean square) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));
    HashSet<Cell> cells = new HashSet<>();

    //double phi = Math.sin(Math.PI / 3); // About 0.866
    double phi = 0.85546875; // Near-ish base 2 representation
    for (int row = 0; row < h; row++) {
      double y = row * FACTOR * phi;
      int colOffset = 0;
      if (!square) {
        w = (row * 2) + 1;
        colOffset = -row;
      }
      for (int col = colOffset; col < w + colOffset; col++) {
        double x = col * (FACTOR / 2);

        Face[] lFaces = new Face[3];
        double u = FACTOR / 2;
        double u2 = FACTOR;
        double v = phi * FACTOR;

        if (row % 2 == 0) {
          // Leftmost trigangle faces up, from y+1 to y
          if (col % 2 == 0) {
            // Same as leftmost: facing up
            lFaces[0] = faces.get(vertices.get(x, y + v), vertices.get(x + u, y));
            lFaces[1] = faces.get(vertices.get(x + u, y), vertices.get(x + u2, y + v));
            lFaces[2] = faces.get(vertices.get(x + u2, y + v), vertices.get(x, y + v));
          } else {
            // Opposite from leftmost: facing down
            lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + u, y + v));
            lFaces[1] = faces.get(vertices.get(x + u, y + v), vertices.get(x + u2, y));
            lFaces[2] = faces.get(vertices.get(x + u2, y), vertices.get(x, y));
          }
        } else {
          // Leftmost triangle faces down, from y to y+1
          if (col % 2 == 0) {
            // Same as leftmost: facing down
            lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + u, y + v));
            lFaces[1] = faces.get(vertices.get(x + u, y + v), vertices.get(x + u2, y));
            lFaces[2] = faces.get(vertices.get(x + u2, y), vertices.get(x, y));
          } else {
            // Opposite from leftmost: facing up
            lFaces[0] = faces.get(vertices.get(x, y + v), vertices.get(x + u, y));
            lFaces[1] = faces.get(vertices.get(x + u, y), vertices.get(x + u2, y + v));
            lFaces[2] = faces.get(vertices.get(x + u2, y + v), vertices.get(x, y + v));
          }
        }

        Cell c = new Cell(lFaces);
        cells.add(c);
      }
    }
    return cells;
  }

  public static Collection<Cell> generateHexBoard(int w, int h, boolean square) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));
    HashSet<Cell> cells = new HashSet<>();

    //double px = Math.cos(Math.PI / 3); // 1/2
    //double py = Math.sin(Math.PI / 3);
    double phi = 0.85546875; // Near-ish base 2 representation
    for (int row = 0; row < h; row++) {
      double y0 = row * FACTOR * 2 * phi;
      int colOffset = 0;
      if (!square) {
//       w = (row * 2) + 1;
//       colOffset = -row;
        //TODO Deal with this
        throw new RuntimeException("Non-square hex board not yet implemented");
      }
      for (int col = colOffset; col < w + colOffset; col++) {
        double x = (col * 1.5) * FACTOR;

        Face[] lFaces = new Face[6];
        double u = FACTOR / 2;
        double u2 = FACTOR;
        double u3 = (3 * FACTOR) / 2;
        double v = phi * FACTOR;
        double v2 = (phi * 2) * FACTOR;

        double y;
        if (col % 2 == 0) {
          // Up
          y = y0;
        } else {
          // Down
          y = y0 + v;
        }
        lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + u2, y));
        lFaces[1] = faces.get(vertices.get(x + u2, y), vertices.get(x + u3, y + v));
        lFaces[2] = faces.get(vertices.get(x + u3, y + v), vertices.get(x + u2, y + v2));
        lFaces[3] = faces.get(vertices.get(x + u2, y + v2), vertices.get(x, y + v2));
        lFaces[4] = faces.get(vertices.get(x, y + v2), vertices.get(x - u, y + v));
        lFaces[5] = faces.get(vertices.get(x - u, y + v), vertices.get(x, y));

        Cell c = new Cell(lFaces);
        cells.add(c);
      }
    }
    return cells;
  }

  public static Collection<Cell> generateSierpinski(int level, boolean dbl) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));

    //TODO Allow customization
    HashMap<Character, String> rules = new HashMap<>();
    rules.put('F', "F-G+F+G-F");
    rules.put('G', "GG");
    rules.put('+', "+");
    rules.put('-', "-");
    String init = "F-G-G";
    String code = doLSystem(init, rules, level);

    double xf = FACTOR;
    double yf = xf;

    Holder<Double> x = new Holder<>(0.0);
    Holder<Double> y = new Holder<>(0.0);
    Holder<Integer> dir = new Holder<>(0); //n ne e s sw w
    Holder<Vertex> lastVertex = new Holder<>(null);

    HashMap<Character, Runnable> actions = new HashMap<>();
    actions.put('F', () -> {
      //TODO Note that n/s may be backwards, technically
      switch (dir.value) {
        case 0: //n
          y.value += yf;
          break;
        case 1: //ne
          x.value += xf;
          y.value += yf;
          break;
        case 2: //e
          x.value += xf;
          break;
        case 3: //s
          y.value -= yf;
          break;
        case 4: //sw
          x.value -= xf;
          y.value -= yf;
          break;
        case 5: //w
          x.value -= xf;
          break;
      }
    });
    actions.put('G', actions.get('F'));
    actions.put('+', () -> {
      dir.value = MeMath.mod(dir.value - 2, 6);
    });
    actions.put('-', () -> {
      dir.value = MeMath.mod(dir.value + 2, 6);
    });

    // Make faces
    lastVertex.value = vertices.get(x.value, y.value);
    code.chars().forEachOrdered(c -> {
      actions.get((Character) (char) c).run();
      Vertex thisVertex = vertices.get(x.value, y.value);
      if (!thisVertex.equals(lastVertex.value)) {
        faces.get(lastVertex.value, thisVertex);
        lastVertex.value = thisVertex;
      }
    });

    if (dbl) {
      Collection<Face> halfFaces = new HashSet<Face>(faces.map.values());
      for (Face f : halfFaces) {
        Face newFace = faces.get(f.vertices.stream().map(v -> vertices.get(v.coords[1], v.coords[0])).collect(Collectors.toList()).toArray(new Vertex[0]));
        faces.put(newFace, newFace.vertices.toArray(new Vertex[0]));
      }
    }

    return autoCellFaces(faces.map.values(), true);
  }

  protected static Collection<Cell> autoCellFaces(Collection<Face> faces, boolean removeOutsideCell) {
    BagMap<Vertex, Face> v2f = new BagMap<>();
    for (Face f : faces) {
      v2f.put(f, f.vertices.toArray(new Vertex[0]));
    }

    Set<Cell> cells = new HashSet<>();

    Function<Set<Vertex>, Vertex[]> orderPair = (vs) -> {
      if (vs.size() != 2) {
        throw new RuntimeException("Trying to order invalid number of vertices: " + vs.size());
      }
      Iterator<Vertex> i = vs.iterator();
      Vertex v1 = i.next();
      Vertex v2 = i.next();
      if (v1.coords[0] < v2.coords[0]) {
        return new Vertex[]{v1, v2};
      } else if (v1.coords[0] > v2.coords[0]) {
        return new Vertex[]{v2, v1};
      } else {
        if (v1.coords[1] < v2.coords[1]) {
          return new Vertex[]{v1, v2};
        } else if (v1.coords[1] > v2.coords[1]) {
          return new Vertex[]{v2, v1};
        } else {
          throw new RuntimeException("Trying to order two identical vertices!");
        }
      }
    };

    //TODO I suspect that this is pretty inefficient, calc cells up to 2*cell.faces.length times.
    //TODO For each face, run cw until we get back to the same face.  Make that a cell.
    for (Face f : faces) {
      Consumer<Boolean> addCell = (reverseDir) -> {
        HashSet<Face> newFaces = new HashSet<>();
        Vertex[] pair = orderPair.apply(f.vertices); //TODO Maybe just comparator?
        Face curFace = f;
        do {
          newFaces.add(curFace);
          List<Vertex> neighbors = Utils.orderVertices(pair[1].getNeighbors(), pair[1]);
          if (reverseDir) {
            Collections.reverse(neighbors);
          }
          int i = 0;
          boolean isNext = false;
          Vertex next = null;
          for (Vertex n : neighbors) {
            if (isNext) {
              next = n;
              break;
            }
            if (n.equals(pair[0])) {
              isNext = true;
            }
          }
          if (next == null) {
            if (isNext) {
              next = neighbors.get(0);
            } else {
              throw new RuntimeException("Didn't hit other neighbor?");
            }
          }
          pair[0] = pair[1];
          pair[1] = next;
          curFace = v2f.get(pair[0], pair[1]);
        } while (!curFace.equals(f));
        cells.add(new Cell(newFaces.toArray(new Face[0])));
      };
      addCell.accept(false);
      addCell.accept(true);
    }

    if (removeOutsideCell) {
      // Note that this yields empty set if there's only one cell, and this is expected.
      //TODO ...Note, also, that this fails to remove the outside cell.
      return cells.stream().filter(c -> c.faces.stream().anyMatch(f -> f.cells.size() > 1)).collect(Collectors.toSet());
    } else {
      return cells;
    }
  }

  public static LSystemResult generatePeanoCurve(int level) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));

    //TODO Allow customization
    HashMap<Character, String> rules = new HashMap<>();
    rules.put('n', "nfbfn-f-bfnfb+f+nfbfn");
    rules.put('b', "bfnfb+f+nfbfn-f-bfnfb");
    rules.put('f', "f");
    rules.put('+', "+");
    rules.put('-', "-");
    String init = "n";
    String code = doLSystem(init, rules, level);

    double xf = FACTOR;
    double yf = xf;

    Holder<Double> x = new Holder<>(0.0);
    Holder<Double> y = new Holder<>(0.0);
    Holder<Integer> dir = new Holder<>(0); //n e s w
    Holder<Vertex> lastVertex = new Holder<>(null);

    HashMap<Character, Runnable> actions = new HashMap<>();
    actions.put('n', () -> {
    });
    actions.put('b', () -> {
    });
    actions.put('f', () -> {
      //TODO Note that n/s may be backwards, technically
      switch (dir.value) {
        case 0: //n
          y.value += yf;
          break;
        case 1: //e
          x.value += xf;
          break;
        case 2: //s
          y.value -= yf;
          break;
        case 3: //w
          x.value -= xf;
          break;
      }
    });
    actions.put('+', () -> {
      dir.value = MeMath.mod(dir.value - 1, 4);
    });
    actions.put('-', () -> {
      dir.value = MeMath.mod(dir.value + 1, 4);
    });

    // Make faces
    lastVertex.value = vertices.get(x.value, y.value);
    code.chars().forEachOrdered(c -> {
      actions.get((Character) (char) c).run();
      Vertex thisVertex = vertices.get(x.value, y.value);
      if (!thisVertex.equals(lastVertex.value)) {
        faces.get(lastVertex.value, thisVertex);
        lastVertex.value = thisVertex;
      }
    });

    LSystemResult result = new LSystemResult();
    result.walls = new HashSet<Face>(faces.map.values());

    // Generate corresponding grid
    Holder<Double> minX = new Holder<>(0.0);
    Holder<Double> minY = new Holder<>(0.0);
    Holder<Double> maxX = new Holder<>(0.0);
    Holder<Double> maxY = new Holder<>(0.0);
    faces.map.values().stream().flatMap(f -> f.vertices.stream()).distinct().forEach(v -> {
      synchronized (minX) {
        minX.value = Math.min(minX.value, v.coords[0]);
        minY.value = Math.min(minY.value, v.coords[1]);
        maxX.value = Math.max(maxX.value, v.coords[0]);
        maxY.value = Math.max(maxY.value, v.coords[1]);
      }
    });
    // I was gonna use the turtle system to draw the grid...but that's kinda stupid inefficient in two ways
    HashSet<Cell> gridCells = new HashSet<>();
    for (double curX = minX.value - xf; curX < maxX.value + xf; curX += xf) {
      for (double curY = minY.value - yf; curY < maxY.value + yf; curY += yf) {
        Face[] lFaces = new Face[4];
        lFaces[0] = faces.get(vertices.get(curX, curY), vertices.get(curX + xf, curY));
        lFaces[1] = faces.get(vertices.get(curX + xf, curY), vertices.get(curX + xf, curY + yf));
        lFaces[2] = faces.get(vertices.get(curX + xf, curY + yf), vertices.get(curX, curY + yf));
        lFaces[3] = faces.get(vertices.get(curX, curY + yf), vertices.get(curX, curY));
        Cell newCell = new Cell(lFaces);
        gridCells.add(newCell);
      }
    }

    result.gridCells = gridCells;
    return result;
  }

  public static LSystemResult generateHilbertCurve(int level) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));

    //TODO Allow customization
    HashMap<Character, String> rules = new HashMap<>();
    rules.put('a', "-bf+afa+fb-");
    rules.put('b', "+af-bfb-fa+");
    rules.put('f', "f");
    rules.put('+', "+");
    rules.put('-', "-");
    String init = "a";
    String code = doLSystem(init, rules, level);

    double xf = FACTOR;
    double yf = xf;

    Holder<Double> x = new Holder<>(0.0);
    Holder<Double> y = new Holder<>(0.0);
    Holder<Integer> dir = new Holder<>(0); //n e s w
    Holder<Vertex> lastVertex = new Holder<>(null);

    HashMap<Character, Runnable> actions = new HashMap<>();
    actions.put('a', () -> {
    });
    actions.put('b', () -> {
    });
    actions.put('f', () -> {
      //TODO Note that n/s may be backwards, technically
      switch (dir.value) {
        case 0: //n
          y.value += yf;
          break;
        case 1: //e
          x.value += xf;
          break;
        case 2: //s
          y.value -= yf;
          break;
        case 3: //w
          x.value -= xf;
          break;
      }
    });
    actions.put('+', () -> {
      dir.value = MeMath.mod(dir.value - 1, 4);
    });
    actions.put('-', () -> {
      dir.value = MeMath.mod(dir.value + 1, 4);
    });

    // Make faces
    lastVertex.value = vertices.get(x.value, y.value);
    code.chars().forEachOrdered(c -> {
      actions.get((Character) (char) c).run();
      Vertex thisVertex = vertices.get(x.value, y.value);
      if (!thisVertex.equals(lastVertex.value)) {
        faces.get(lastVertex.value, thisVertex);
        lastVertex.value = thisVertex;
      }
    });

    LSystemResult result = new LSystemResult();
    result.walls = new HashSet<Face>(faces.map.values());

    // Generate corresponding grid
    Holder<Double> minX = new Holder<>(0.0);
    Holder<Double> minY = new Holder<>(0.0);
    Holder<Double> maxX = new Holder<>(0.0);
    Holder<Double> maxY = new Holder<>(0.0);
    faces.map.values().stream().flatMap(f -> f.vertices.stream()).distinct().forEach(v -> {
      synchronized (minX) {
        minX.value = Math.min(minX.value, v.coords[0]);
        minY.value = Math.min(minY.value, v.coords[1]);
        maxX.value = Math.max(maxX.value, v.coords[0]);
        maxY.value = Math.max(maxY.value, v.coords[1]);
      }
    });
    // I was gonna use the turtle system to draw the grid...but that's kinda stupid inefficient in two ways
    HashSet<Cell> gridCells = new HashSet<>();
    for (double curX = minX.value - xf; curX < maxX.value + xf; curX += xf) {
      for (double curY = minY.value - yf; curY < maxY.value + yf; curY += yf) {
        Face[] lFaces = new Face[4];
        lFaces[0] = faces.get(vertices.get(curX, curY), vertices.get(curX + xf, curY));
        lFaces[1] = faces.get(vertices.get(curX + xf, curY), vertices.get(curX + xf, curY + yf));
        lFaces[2] = faces.get(vertices.get(curX + xf, curY + yf), vertices.get(curX, curY + yf));
        lFaces[3] = faces.get(vertices.get(curX, curY + yf), vertices.get(curX, curY));
        Cell newCell = new Cell(lFaces);
        gridCells.add(newCell);
      }
    }

    result.gridCells = gridCells;
    return result;
  }
  
  public static LSystemResult generateGosperCurve(int level) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));

    //TODO Allow customization
    HashMap<Character, String> rules = new HashMap<>();
    rules.put('a', "a-b--b+a++aa+b-");
    rules.put('b', "+a-bb--b-a++a+b");
    rules.put('+', "+");
    rules.put('-', "-");
    String init = "a";
    String code = doLSystem(init, rules, level);

    double xf = FACTOR;
    double yf = xf;

    Holder<Double> x = new Holder<>(0.0);
    Holder<Double> y = new Holder<>(0.0);
    Holder<Integer> dir = new Holder<>(0); //n ne e s sw w
    Holder<Vertex> lastVertex = new Holder<>(null);

    HashMap<Character, Runnable> actions = new HashMap<>();
    actions.put('a', () -> {
      //TODO Note that n/s may be backwards, technically
      switch (dir.value) {
        case 0: //n
          y.value += yf;
          break;
        case 1: //ne
          x.value += xf;
          y.value += yf;
          break;
        case 2: //e
          x.value += xf;
          break;
        case 3: //s
          y.value -= yf;
          break;
        case 4: //sw
          x.value -= xf;
          y.value -= yf;
          break;
        case 5: //w
          x.value -= xf;
          break;
      }
    });
    actions.put('b', actions.get('a'));
    actions.put('+', () -> {
      dir.value = MeMath.mod(dir.value - 1, 6);
    });
    actions.put('-', () -> {
      dir.value = MeMath.mod(dir.value + 1, 6);
    });

    // Make faces
    lastVertex.value = vertices.get(x.value, y.value);
    code.chars().forEachOrdered(c -> {
      actions.get((Character) (char) c).run();
      Vertex thisVertex = vertices.get(x.value, y.value);
      if (!thisVertex.equals(lastVertex.value)) {
        faces.get(lastVertex.value, thisVertex);
        lastVertex.value = thisVertex;
      }
    });

    LSystemResult result = new LSystemResult();
    result.walls = new HashSet<Face>(faces.map.values());

    // Generate corresponding grid
    for (Vertex v : new HashSet<Vertex>(vertices.map.values())) {
      for (int d = 0; d < 6; d++) {
        switch (d) {
          case 0: //n
            faces.get(v, vertices.get(v.coords[0], v.coords[1] + yf));
            break;
          case 1: //ne
            faces.get(v, vertices.get(v.coords[0] + xf, v.coords[1] + yf));
            break;
          case 2: //e
            faces.get(v, vertices.get(v.coords[0] + xf, v.coords[1]));
            break;
          case 3: //s
            faces.get(v, vertices.get(v.coords[0], v.coords[1] - yf));
            break;
          case 4: //sw
            faces.get(v, vertices.get(v.coords[0] - xf, v.coords[1] - yf));
            break;
          case 5: //w
            faces.get(v, vertices.get(v.coords[0] - xf, v.coords[1]));
            break;
        }
      }
    }

    Set<Face> nonStick = new HashSet<>(faces.map.values());
//    boolean changed;
//    int i = 0;
//    do {
//      System.out.println("Anti-sticking loop " + (i++));
//      changed = false;
//      int size = nonStick.size();
//      nonStick = nonStick.stream().filter(f -> !f.vertices.stream().anyMatch(v -> v.faces.size() == 1)).collect(Collectors.toSet());
//      if (size != nonStick.size()) {
//        changed = true;
//      }
//    } while (changed);

    result.gridCells = autoCellFaces(nonStick, true);
    return result;
  }

  public static String doLSystem(String init, Map<Character, String> rules, int level) {
    StringBuilder s = new StringBuilder(init);
    for (int i = 0; i < level; i++) {
      StringBuilder r = new StringBuilder();
      s.chars().forEachOrdered(c -> r.append(rules.get((Character) (char) c)));
      s = r;
    }
    return s.toString();
  }
}
