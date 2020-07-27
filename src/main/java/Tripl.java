import java.util.*;

public class Tripl {
    private final String first;
    private final String second;
    private final String third;

    public Tripl(String first, String second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return first + ";" + second + ";" + third;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        Tripl group = (Tripl) obj;
        return (group.first.equals(this.first) && group.second.equals(this.second) && group.third.equals(this.third));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        result = prime * result + ((third == null) ? 0 : third.hashCode());
        return result;
    }
    private static class NewWord
    {
        public String value;
        public int position;

        public NewWord(String value, int position)
        {
            this.value = value;
            this.position = position;
        }
    }

    public static List<List<String>> findGroups(List<String> lines)
    {
        List<Map<String, Integer>> wordsToGroupsNumbers = new ArrayList<>(); //[позиция_слова:{слово:номер_группы}]
        List<List<String>> linesGroups = new ArrayList<>(); //[номер_группы:[строки_группы]]
        Map<Integer, Integer> mergedGroupNumberToFinalGroupNumber = new HashMap<>(); //{номер_слитой_группы:номер_группы_в_которую_слили}
        for (String line : lines)
        {
            String[] words = line.split(";");
            TreeSet<Integer> foundInGroups = new TreeSet<>();
            List<NewWord> newWords = new ArrayList<>();
            for (int i = 0; i < words.length; i++)
            {
                String word = words[i];

                if (wordsToGroupsNumbers.size() == i)
                    wordsToGroupsNumbers.add(new HashMap<>());

                if (word.equals(""))
                    continue;

                Map<String, Integer> wordToGroupNumber = wordsToGroupsNumbers.get(i);
                Integer wordGroupNumber = wordToGroupNumber.get(word);
                if (wordGroupNumber != null)
                {
                    while (mergedGroupNumberToFinalGroupNumber.containsKey(wordGroupNumber))
                        wordGroupNumber = mergedGroupNumberToFinalGroupNumber.get(wordGroupNumber);
                    foundInGroups.add(wordGroupNumber);
                }
                else
                {
                    newWords.add(new NewWord(word, i));
                }
            }
            int groupNumber;
            if (foundInGroups.isEmpty())
            {
                groupNumber = linesGroups.size();
                linesGroups.add(new ArrayList<>());
            }
            else
            {
                groupNumber = foundInGroups.first();
            }
            for (NewWord newWord : newWords)
            {
                wordsToGroupsNumbers.get(newWord.position).put(newWord.value, groupNumber);
            }
            for (int mergeGroupNumber : foundInGroups)
            {
                if (mergeGroupNumber != groupNumber)
                {
                    mergedGroupNumberToFinalGroupNumber.put(mergeGroupNumber, groupNumber);
                    linesGroups.get(groupNumber).addAll(linesGroups.get(mergeGroupNumber));
                    linesGroups.set(mergeGroupNumber, null);
                }
            }
            linesGroups.get(groupNumber).add(line);
        }
        linesGroups.removeAll(Collections.singleton(null));
        return linesGroups;
    }
}
