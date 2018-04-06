/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen;

import com.erhannis.puzzlegen.phases.Phase1CellGeneration;
import com.erhannis.puzzlegen.phases.Phase2Grouping;
import com.erhannis.puzzlegen.phases.Phase4GenSvg;
import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Group;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author erhannis
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException {
    Runnable onError = () -> {
      System.err.println("Usage: java [-Xms4G -Xmx4G] -jar PuzzleGen.jar [<cellShape (tri|sqr|hex)> <width> <height> <squareBoard (t|f)> <groups> <neighborStrictness> (<resolutionMode> (<groupColorMode> (<skipUnassignedBorderFaces>))) ]\n"
              + "\n"
              + "    cellShape - Shape of cells.  One of tri, sqr, hex.\n"
              + "    width, height - Width and height of board in cells.  Note that cells may be wider than they are tall, etc.\n"
              + "    squareBoard - Should the board be squared.  t or f.  (Not yet implemented for hex cells.)\n"
              + "    groups - How many pieces the board should try to generate.  Will probably end up with more, from the leftovers.  I recommend that groups be about (width*height)/80.  Integer.\n"
              + "    neighborStrictness - The lower this is, the more the algorithm tries to prevent groups connecting with itself, basically.  I strongly recommend the following: \n"
              + "        hex - 0 (maybe 1)\n"
              + "        sqr - 1\n"
              + "        tri - 2 or 3\n"
              + "    resolutionMode - How unassigned cells are resolved.  Sorry, but this is exactly one of the following (and that longest one tends to work best):\n"
              + "        NONE\n"
              + "        ASSIGN_TO_LEAST_CONTACT\n"
              + "        ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER\n"
              + "        MAKE_OWN_GROUP\n"
              + "        MAKE_OWN_GROUP_WITH_MIN_SIZE_20_ELSE_ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER\n"
              + "    groupColorMode - How to color the colored group svg.  One of:\n"
              + "        NONE\n"
              + "        RANDOM\n"
              + "        RANDOM_WITH_ALPHA\n"
              + "        WHITE\n"
              + "    skipUnassignedBorderFaces - Should the svg rendering attempt to blend border cells into the border?  t or f.");
    };
    if (args.length > 0) { // Hotwiring to show usage, for now
      try {
        Collection<Cell> cells;
        int i = 0;
        String cellType = args[i++];
        int w = Integer.parseInt(args[i++]);
        int h = Integer.parseInt(args[i++]);
        boolean squared = "t".equalsIgnoreCase(args[i++]);
        switch (cellType) {
          case "tri":
            cells = Phase1CellGeneration.generateTriangleBoard(w, h, squared);
            break;
          case "sqr":
            cells = Phase1CellGeneration.generateSquareBoard(w, h);
            break;
          case "hex":
            cells = Phase1CellGeneration.generateHexBoard(w, h, squared);
            break;
          default:
            onError.run();
            return;
        }
        int groupCount = Integer.parseInt(args[i++]);
        int neighborStrictness = Integer.parseInt(args[i++]);
        
        Phase2Grouping.P2ResolutionMode resolutionMode = Phase2Grouping.P2ResolutionMode.MAKE_OWN_GROUP_WITH_MIN_SIZE_20_ELSE_ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER;
        if (args.length > i) {
          resolutionMode = Phase2Grouping.P2ResolutionMode.valueOf(args[i++]);
        }
        Phase4GenSvg.ColorMode groupColorMode = Phase4GenSvg.ColorMode.RANDOM;
        if (args.length > i) {
          groupColorMode = Phase4GenSvg.ColorMode.valueOf(args[i++]);
        }
        boolean skipUnassignedBorderFaces = true;
        if (args.length > i) {
          skipUnassignedBorderFaces = "t".equalsIgnoreCase(args[i++]);
        }
        
        long t = System.currentTimeMillis();
        System.out.println("Phase 1 " + (System.currentTimeMillis() - t));

        t = System.currentTimeMillis();
        Set<Group> groups = Phase2Grouping.groupCellsDefault(cells.stream().findAny().get(), groupCount, neighborStrictness, resolutionMode);
        System.out.println("Phase 2 " + (System.currentTimeMillis() - t));

        t = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        Phase4GenSvg.writeGridToSvg(cells.stream().findAny().get(), new File("gen/" + time + "_grid.svg"));
        Phase4GenSvg.writeGroupsToSvg(groups, new File("gen/" + time + "_groups.svg"), Phase4GenSvg.ColorMode.NONE, skipUnassignedBorderFaces);
        Phase4GenSvg.writeGroupsToSvg(groups, new File("gen/" + time + "_groups_colored.svg"), groupColorMode, skipUnassignedBorderFaces);
        System.out.println("Phase 4 " + (System.currentTimeMillis() - t));
      } catch (Exception e) {
        e.printStackTrace();
        onError.run();
        return;
      }
    } else {
      long t = System.currentTimeMillis();
      //Collection<Cell> cells = Phase1CellGeneration.generateSquareBoard(100, 100);
      //Collection<Cell> cells = Phase1CellGeneration.generateTriangleBoard(160, 80, true);
      //Collection<Cell> cells = Phase1CellGeneration.generateHexBoard(120, 80, true);
      //Collection<Cell> cells = Phase1CellGeneration.generateSierpinski(7, true);
      Collection<Cell> cells = Phase1CellGeneration.generateLSystem(4);
      System.out.println("Got " + cells.size() + " cells");
      System.out.println("Phase 1 " + (System.currentTimeMillis() - t));

      t = System.currentTimeMillis();
      int bestCount = 0;
      Set<Group> groups = null;
      for (int i = 0; i < 1; i++) {
        System.err.print(i + " ");
        Set<Group> lGroups = Phase2Grouping.groupCellsDefault(cells.stream().findAny().get(), 1, 1, Phase2Grouping.P2ResolutionMode.MAKE_OWN_GROUP_WITH_MIN_SIZE_20_ELSE_ASSIGN_TO_LEAST_CONTACT_SAVE_BORDER);
        System.err.println("");
        HashSet<Cell> lCells = new HashSet<Cell>();
        for (Group g : lGroups) {
          lCells.addAll(g.cells);
        }
        if (lCells.size() > bestCount) {
          groups = lGroups;
          bestCount = lGroups.size();
        }
        if (lCells.size() == cells.size()) {
          System.out.println("Found ideal candidate, breaking early");
          break;
        }
      }
      System.out.println("Got " + groups.size() + " groups");
      System.out.println("Phase 2 " + (System.currentTimeMillis() - t));

      t = System.currentTimeMillis();
      long time = System.currentTimeMillis();
      Phase4GenSvg.writeGridToSvg(cells.stream().findAny().get(), new File("gen/" + time + "_grid.svg"));
      Phase4GenSvg.writeGroupsToSvg(groups, new File("gen/" + time + "_groups.svg"), Phase4GenSvg.ColorMode.NONE, true);
      Phase4GenSvg.writeGroupsToSvg(groups, new File("gen/" + time + "_groups_colored.svg"), Phase4GenSvg.ColorMode.RANDOM_WITH_ALPHA, true);
      System.out.println("Phase 4 " + (System.currentTimeMillis() - t));
    }
  }
}
