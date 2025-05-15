package ee.projekt.blackjackgui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CssUtil {
    /*
    See klass võtab teatud faili asjad syles.css failist.
    Ma ei taha apply tervele projektile seda css faili kuna asju mis seal ei ole
    saavad vahest overwritten mittemillegagi.
    See annab teatud klassile tema css ja seda saab kasutada läbi .setStyle()
    */
    public static String getCss(String  className){
        try {
            String content = Files.readString(Paths.get("src/main/resources/styles.css"));
            Pattern pattern = Pattern.compile("\\." + Pattern.quote(className) + "\\s*\\{([^}]+)\\}");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim().replaceAll("\\s+", " ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
