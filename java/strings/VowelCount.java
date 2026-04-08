package strings;

class VowelCounter {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public int countVowels() {
        int count = 0;
        String vowels = "aeiouAEIOU";
        for (int i = 0; i < text.length(); i++) {
            if (vowels.indexOf(text.charAt(i)) != -1) {
                count++;
            }
        }
        return count;
    }

    public void showResult() {
        System.out.println("Text: " + text);
        System.out.println("Number of vowels: " + countVowels());
    }
}

public class VowelCount {
    public static void main(String[] args) {
        VowelCounter vc = new VowelCounter();
        vc.setText("NucleusTeq");
        vc.showResult();

        vc.setText("Aradhya");
        vc.showResult();
    }
}
