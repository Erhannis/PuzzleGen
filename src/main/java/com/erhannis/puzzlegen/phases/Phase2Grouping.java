/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.mathnstuff.FactoryHashMap;
import com.erhannis.mathnstuff.utils.BagMap;
import com.erhannis.mathnstuff.utils.ListMap;
import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Face;
import com.erhannis.puzzlegen.structure.Group;
import com.erhannis.puzzlegen.structure.Vertex;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author erhannis
 */
public class Phase2Grouping {

  public static enum P2ResolutionMode {

    NONE, ASSIGN_TO_LEAST_CONTACT, ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER, MAKE_OWN_GROUP, MAKE_OWN_GROUP_WITH_MIN_SIZE_20_ELSE_ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER
  }

  private static final int MIN_RESOLUTION_GROUP_SIZE_FOR_MODE = 20;

  public static Set<Group> groupCellsDefault(Cell root, Collection<Face> walls, int groupCount, int ignoreSourceNeighborRadius, P2ResolutionMode resolutionMode) {
    final int GROUP_COUNT = groupCount;
    SecureRandom rand = new SecureRandom();

    Set<Group> groups = new HashSet<>();
    HashMap<Cell, Group> c2g = new HashMap<>();

    HashSet<Cell> allCells = new HashSet<>();
    Utils.forEachCell(root, c -> allCells.add(c));

    ArrayList<Cell> growing = new ArrayList<>();
    HashSet<Cell> done = new HashSet<>();

    ArrayList<Cell> potentialSeeds = new ArrayList<>(allCells);
    for (int i = 0; i < GROUP_COUNT; i++) {
      Cell c = potentialSeeds.remove(rand.nextInt(potentialSeeds.size()));
      growing.add(c);
      Group g = new Group();
      groups.add(g);
      g.cells.add(c);
      c2g.put(c, g);
    }
    HashSet<Cell> unassignedCells = new HashSet<Cell>(potentialSeeds);

    //TODO This is complicated, and should be tested.
    Function<Cell, Set<Cell>> getNeighborsAvailableForExpansion = c -> {
      Set<Cell> adjNs = c.getAdjacentNeighbors();
      Set<Cell> result = adjNs;
      result = result.stream().filter(c2 -> !c2g.containsKey(c2)).collect(Collectors.toSet()); // Remove grouped
      Set<Cell> ignore0;
      if (ignoreSourceNeighborRadius <= 0) {
        ignore0 = new HashSet<Cell>();
      } else {
        ignore0 = adjNs.stream().filter(c2 -> c2g.get(c2) == c2g.get(c)).collect(Collectors.toSet());
        int i = ignoreSourceNeighborRadius - 1;
        while (i > 0) {
          ignore0 = ignore0.stream().flatMap(c2 -> c2.getAdjacentNeighborsAndSelf().stream()).filter(c2 -> c2g.get(c2) == c2g.get(c)).collect(Collectors.toSet());
          i--;
        }
      }
      Set<Cell> ignore = ignore0;
      result = result.stream().filter(c2 -> !(c2.getAllNeighbors().stream().filter(c2n -> !ignore.contains(c2n) && !c2n.equals(c)).anyMatch(c2n -> c2g.get(c2n) == c2g.get(c)))).collect(Collectors.toSet()); // Remove if too close to own group, ignoring adj from c
      // Remove results only accessible through walls
      if (walls != null) {
        result = result.stream().filter(c2 -> {
          return !Cell.getSharedFaces(c, c2).stream().allMatch(f -> walls.contains(f));
        }).collect(Collectors.toSet());
      }
      return result;
    };

    while (!growing.isEmpty()) {
      int i = rand.nextInt(growing.size());
      Cell source = growing.get(i);
      ArrayList<Cell> neighbors = new ArrayList<>(getNeighborsAvailableForExpansion.apply(source));
      if (neighbors.isEmpty()) {
        //TODO Perhaps this is slow?
        growing.remove(i);
        done.add(source);
        continue;
      }
      Cell target = neighbors.get(rand.nextInt(neighbors.size()));
      Group g = c2g.get(source);
      g.cells.add(target);
      c2g.put(target, g);
      growing.add(target);
      unassignedCells.remove(target);
    }

    Function<Cell, Group> fillUnassigned = cell -> {
      if (c2g.get(cell) != null) {
        throw new IllegalArgumentException("Cell already has a group!");
      }
      Group g = new Group();
      g.cells.add(cell);

      HashSet<Cell> toDo0 = new HashSet<>();
      HashSet<Cell> done0 = new HashSet<>();
      toDo0.add(cell);
      while (!toDo0.isEmpty()) {
        Iterator<Cell> i = toDo0.iterator();
        Cell c = i.next();
        i.remove();

        g.cells.add(c);

        HashSet<Cell> lToDo = new HashSet<Cell>();
        lToDo.addAll(c.getAdjacentNeighbors().stream().filter(c2 -> c2g.get(c2) == null).collect(Collectors.toSet()));
        lToDo.removeAll(done0); // Hopefully this is smart about it
        toDo0.addAll(lToDo);

        done0.add(c);
      }

      return g;
    };

    Group border = new Group(); //TODO ???

    BiConsumer<Group, Boolean> assignToLeastContact = (group, skipBorder) -> {
      Set<Face> faces = group.cells.stream().flatMap(c2 -> c2.faces.stream()).collect(Collectors.toSet());
      FactoryHashMap<Group, Integer> scores = new FactoryHashMap<Group, Integer>(g2 -> 0);
      boolean hasBorderFace = false;
      for (Face f : faces) {
        if (f.cells.size() < 2) {
          hasBorderFace = true;
        }
        for (Cell c2 : f.cells) {
          Group g2 = c2g.get(c2);
          if (g2 != null) {
            scores.put(g2, scores.get(g2) + 1);
          }
        }
      }
      Group winner;
      if (hasBorderFace && skipBorder) {
        winner = border;
      } else {
        winner = scores.entrySet().stream().min((a, b) -> Integer.compare(a.getValue(), b.getValue())).get().getKey();
      }

      winner.cells.addAll(group.cells);
      for (Cell c2 : group.cells) {
        c2g.put(c2, winner);
        unassignedCells.remove(c2);
        done.add(c2);
      }
    };

    switch (resolutionMode) {
      case ASSIGN_TO_LEAST_CONTACT:
      case ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER:
        while (!unassignedCells.isEmpty()) {
          Iterator<Cell> i = unassignedCells.iterator();
          Cell c = i.next();
          i.remove();
          Group gTemp = fillUnassigned.apply(c);
          assignToLeastContact.accept(gTemp, resolutionMode == P2ResolutionMode.ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER);
        }
        break;
      case MAKE_OWN_GROUP:
        while (!unassignedCells.isEmpty()) {
          Iterator<Cell> i = unassignedCells.iterator();
          Cell c = i.next();
          i.remove();
          Group gTemp = fillUnassigned.apply(c);

          groups.add(gTemp);
          for (Cell c2 : gTemp.cells) {
            c2g.put(c2, gTemp);
            unassignedCells.remove(c2);
            done.add(c2);
          }
        }
        break;
      case MAKE_OWN_GROUP_WITH_MIN_SIZE_20_ELSE_ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER:
        while (!unassignedCells.isEmpty()) {
          Iterator<Cell> i = unassignedCells.iterator();
          Cell c = i.next();
          i.remove();
          Group gTemp = fillUnassigned.apply(c);

          if (gTemp.cells.size() >= MIN_RESOLUTION_GROUP_SIZE_FOR_MODE) {
            groups.add(gTemp);
            for (Cell c2 : gTemp.cells) {
              c2g.put(c2, gTemp);
              unassignedCells.remove(c2);
              done.add(c2);
            }
          } else {
            assignToLeastContact.accept(gTemp, true);
          }
        }
        break;
      case NONE:
      default:
        // Nothing
        break;
    }

    if (done.size() != allCells.size()) {
      //throw new IllegalStateException("Darn, didn't get all the cells!");
      System.err.println("Only got " + done.size() + " / " + allCells.size() + " cells");
    }

    return groups;
  }

  /**
   * Splatter paint on grid, expand it outward until it hits other paint, at
   * which point create an edge.
   * @param faces
   * @param groupCount
   * @param forbidSelfAdjacency
   * @return 
   */
//  public static Collection<Cell> splitCellsByPaint(Collection<Face> faces, int groupCount, boolean forbidSelfAdjacency) {
//    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
//      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
//    }));
//    BagMap<Vertex, Face> v2f = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
//      return new Face(input.toArray(new Vertex[0]));
//    }));
//    //TODO OPT This is redoing work already done
//    for (Face f : faces) {
//      v2f.put(f, f.vertices.toArray(new Vertex[0]));
//      for (Vertex v : f.vertices) {
//        vertices.put(v, ArrayUtils.toObject(v.coords));
//      }
//    }
//
//    SecureRandom rand = new SecureRandom();
//
//    
//    
//  }
}
