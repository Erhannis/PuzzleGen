/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Group;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author erhannis
 */
public class Phase2Grouping {
  public static Set<Group> groupCellsDefault(Cell root, int groupCount, int ignoreSourceNeighborRadius) {
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
    }
    
    if (done.size() != allCells.size()) {
      //throw new IllegalStateException("Darn, didn't get all the cells!");
      System.err.println("Only got " + done.size() + " / " + allCells.size() + " cells");
    }
    
    return groups;
  }
}
