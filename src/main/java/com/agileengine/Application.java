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

class Application {

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

        final Optional<Element> elementByStyle = tryToFindByOriginStyle(otherFile, origin);
        if (elementByStyle.isPresent()) {
            return elementToPath(elementByStyle.get());
        }

        return "";
    }

    private Optional<Element> tryToFindByVisibleText(File otherFile, Element origin) {
        final String text = origin.text();
        final Tag originTag = origin.tag();
        Elements elements = findElementsByText(otherFile, text).orElseThrow();

        return elements
                .stream()
                .filter(elm -> Objects.equals(elm.tag().getName(), originTag.getName()))
                .findAny();
    }

    @SneakyThrows
    private File pathFoFile(String pathToFile) {
        return Paths.get(getClass().getClassLoader().getResource(pathToFile).toURI()).toFile();
    }

    private Optional<Element> tryToFindByOriginStyle(File htmlFile, Element origin) {
        final String searchQuery = getSearchQuery(origin);

        final Document doc = parseDocument(htmlFile);
        final Elements elements = doc.select(searchQuery);
        final int size = elements.size();
        if (size != 1) {
            throw new IllegalStateException("Expected one success button, found " + size);
        }
        final Element element = elements.get(0);
        return Optional.ofNullable(element);
    }

    private String getSearchQuery(Element origin) {
        final String tagName = origin.tagName();
        final String[] classes = origin.attributes().get("class").split(" ");
        final String style = String.join(".", classes);
        return tagName + '.' + style;
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

        final Element elementById = doc.getElementById(targetElementId);

        return Optional.of(elementById);
    }

    @SneakyThrows
    private Optional<Elements> findElementsByText(File htmlFile, String elementText) {
        Objects.requireNonNull(htmlFile);
        Objects.requireNonNull(elementText);

        Document doc = parseDocument(htmlFile);

        final Elements visibleElement = doc.getElementsContainingOwnText(elementText).not("*[style*=display:none]");

        return Optional.of(visibleElement);
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
