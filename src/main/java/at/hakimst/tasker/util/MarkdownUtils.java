package at.hakimst.tasker.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
public class MarkdownUtils {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownUtils() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    public String renderMarkdown(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        try {
            Node document = parser.parse(markdown);
            return renderer.render(document);
        } catch (Exception e) {
            return ""; // Return empty string on any parsing error
        }
    }
}