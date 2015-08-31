package de.jetwick.snacktory;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageExtractor {

    public static Element extractImages(Element el, List<ImageResult> images) {
        int maxWeight = 0;
        Element maxNode = null;
        Elements imgs = getImages(el);

        double score = 1;
        for (Element img : imgs) {
            ImageResult image = analyzeImage(img, score);
            if (image == null){
                continue;
            }

            if (image.weight > maxWeight) {
                maxWeight = image.weight;
                maxNode = img;
                score = score / 2;
            }

            images.add(image);
        }

        Collections.sort(images, new ImageComparator());
        return maxNode;
    }

    public static Elements getImages(Element el){
        Elements els = el.select("img");
        if (els.isEmpty())
            els = el.parent().select("img");
        return els;
    }

    private static boolean isAdImage(String imageUrl) {
        return SHelper.count(imageUrl, "ad") >= 2;
    }

    public static ImageResult analyzeImage(Element img){
        return analyzeImage(img, null);
    }

    public static ImageResult analyzeImage(Element img, Double score){
        String sourceUrl = img.attr("src");
        if (sourceUrl.isEmpty() || isAdImage(sourceUrl))
            return null;

        int weight = 0;
        int height = 0;
        try {
            height = Integer.parseInt(img.attr("height"));
            if (height >= 50)
                weight += 20;
            else
                weight -= 20;
        } catch (Exception ex) {
        }

        int width = 0;
        try {
            width = Integer.parseInt(img.attr("width"));
            if (width >= 50)
                weight += 20;
            else
                weight -= 20;
        } catch (Exception ex) {
        }
        String alt = img.attr("alt");
        if (alt.length() > 35)
            weight += 20;

        String title = img.attr("title");
        if (title.length() > 35)
            weight += 20;

        String rel = null;
        boolean noFollow = false;
        if (img.parent() != null) {
            rel = img.parent().attr("rel");
            if (rel != null && rel.contains("nofollow")) {
                noFollow = rel.contains("nofollow");
                weight -= 40;
            }
        }
        if (score != null){
            weight = (int) (weight * score);
        }
        return new ImageResult(sourceUrl, weight, title, height, width, alt, noFollow);
    }


    /**
     * Comparator for Image by weight
     *
     * @author Chris Alexander, chris@chris-alexander.co.uk
     *
     */
    public static class ImageComparator implements Comparator<ImageResult> {

        @Override
        public int compare(ImageResult o1, ImageResult o2) {
            // Returns the highest weight first
            return o2.weight.compareTo(o1.weight);
        }
    }
}
