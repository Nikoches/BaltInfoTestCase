import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class MainClass {
    public static void main(String[] args)  {
       findLineGroups(args[0],args[1]);
    }

    private static List<List<String>> findLineGroups(String source, String target) {
        List<String> lines = null;
        try{
           lines =Files.readAllLines(Path.of(source));
        }catch (IOException ex){
            ex.getMessage();
        }
        class NewLineElement {
            private String lineElement;
            private int columnNum;

            private NewLineElement(String lineElement, int columnNum) {
                this.lineElement = lineElement;
                this.columnNum = columnNum;
            }
        }
        List<List<String>> linesGroups = new ArrayList<>();
        assert lines != null;
        if (lines.size() < 2) {
            linesGroups.add(lines);
            return linesGroups;
        }

        List<Map<String, Integer>> columns = new ArrayList<>();
        Map<Integer, Integer> unitedGroups = new HashMap<>();
        for (String line : lines) {
            String[] lineElements = line.split(";");
            TreeSet<Integer> groupsWithSameElems = new TreeSet<>();
            List<NewLineElement> newElements = new ArrayList<>();

            for (int elmIndex = 0; elmIndex < lineElements.length; elmIndex++) {
                String currLnElem = lineElements[elmIndex];
                if (columns.size() == elmIndex)
                    columns.add(new HashMap<>());
                if ("".equals(currLnElem.replaceAll("\"", "").trim()))
                    continue;

                Map<String, Integer> currCol = columns.get(elmIndex);
                Integer elemGrNum = currCol.get(currLnElem);
                if (elemGrNum != null) {
                    while (unitedGroups.containsKey(elemGrNum))
                        elemGrNum = unitedGroups.get(elemGrNum);
                    groupsWithSameElems.add(elemGrNum);
                } else {
                    newElements.add(new NewLineElement(currLnElem, elmIndex));
                }
            }
            int groupNumber;
            if (groupsWithSameElems.isEmpty()) {
                linesGroups.add(new ArrayList<>());
                groupNumber = linesGroups.size() - 1;
            } else {
                groupNumber = groupsWithSameElems.first();
            }
            for (NewLineElement newLineElement : newElements) {
                columns.get(newLineElement.columnNum).put(newLineElement.lineElement, groupNumber);
            }
            for (int matchedGrNum : groupsWithSameElems) {
                if (matchedGrNum != groupNumber) {
                    unitedGroups.put(matchedGrNum, groupNumber);
                    linesGroups.get(groupNumber).addAll(linesGroups.get(matchedGrNum));
                    linesGroups.set(matchedGrNum, null);
                }
            }
            linesGroups.get(groupNumber).add(line);
        }
        linesGroups.removeAll(Collections.singleton(null));
        try {
            Files.write(Paths.get(target), linesGroups.toString().getBytes());
        } catch (IOException ex) {
            ex.getMessage();
        }
        return linesGroups;
    }
}
