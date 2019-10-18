package com.agileengine;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;

public class Application {

    @SneakyThrows
    String getPathToButton(String pathToFile, String pathToOtherFile) {
        Objects.requireNonNull(pathToFile);
        Objects.requireNonNull(pathToOtherFile);

        final File originFile = pathFoFile(pathToFile);
        final File otherFile = pathFoFile(pathToOtherFile);

        Element origin = findElementById(originFile, "make-everything-ok-button").orElseThrow();
        final Optional<Element> elementByText = tryToFindByVisibleText(otherFile, origin);

        if (elementByText.isPresent()) {
            return elementToPath(elementByText.get());
        }

        return "";
    }

    private Optional<Element> tryToFindByVisibleText(File otherFile, Element origin) {
        final String text = origin.text();
        final Tag originTag = origin.tag();
        Elements elements = findElementsByText(otherFile, text).orElseThrow();

        return elements
                .stream()
                .filter(elm -> {
                    return Objects.equals(elm.tag().getName(), originTag.getName());
                })
                .findAny();
    }

    @SneakyThrows
    private File pathFoFile(String pathToFile) {
        return Paths.get(getClass().getClassLoader().getResource(pathToFile).toURI()).toFile();
    }

    private String elementToPath(Element elem) {
        final Deque<String> pathItems = new ArrayDeque<>();
        Element element = elem;
        do {
            pathItems.push(element.tagName());
            element = element.parent();
        } while (element.hasParent());

        return joinPathItems(pathItems);
    }

    private String joinPathItems(Deque<String> deque) {
        final StringJoiner joiner = new StringJoiner(" > ");
        while (!deque.isEmpty()) {
            joiner.add(deque.pop());
        }
        return joiner.toString();
    }

    @SneakyThrows
    private Optional<Element> findElementById(File htmlFile, String targetElementId) {
        Objects.requireNonNull(htmlFile);
        Objects.requireNonNull(targetElementId);

        Document doc = parseDocument(htmlFile);

        return Optional.of(doc.getElementById(targetElementId));
    }

    @SneakyThrows
    private Optional<Elements> findElementsByText(File htmlFile, String elementText) {
        Objects.requireNonNull(htmlFile);
        Objects.requireNonNull(elementText);

        Document doc = parseDocument(htmlFile);

        return Optional.of(doc.getElementsContainingOwnText(elementText).not("*[style*=display:none]"));
    }

    @SneakyThrows
    private Document parseDocument(File htmlFile) {
        Objects.requireNonNull(htmlFile);
        return Jsoup.parse(
                htmlFile,
                Charset.defaultCharset().name(),
                htmlFile.getAbsolutePath());
    }
}
